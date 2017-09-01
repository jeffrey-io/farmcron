package farm.bsg.pages;

import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

import farm.bsg.BsgCounters;
import farm.bsg.EventBus.Event;
import farm.bsg.EventBus.EventPayload;
import farm.bsg.ProductEngine;
import farm.bsg.Security.Permission;
import farm.bsg.cron.PeriodicJob;
import farm.bsg.data.RawObject;
import farm.bsg.data.types.TypeDayFilter;
import farm.bsg.data.types.TypeMonthFilter;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.html.HtmlPump;
import farm.bsg.html.Link;
import farm.bsg.html.Table;
import farm.bsg.models.Task;
import farm.bsg.models.TaskFactory;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;

public class TaskFactoryManagement extends SessionPage {
    public static class TaskFactoryMonitor implements PeriodicJob {

        private final ProductEngine engine;

        public TaskFactoryMonitor(final ProductEngine engine) {
            this.engine = engine;
        }

        @Override
        public void run(final long now) {
            BsgCounters.I.task_factory_monitor_run.bump();
            advance(this.engine, now);
        }
    }

    public static SimpleURI TASKS_FACTORY        = new SimpleURI("/admin/tasks-factory");

    public static SimpleURI TASKS_FACTORY_CREATE = new SimpleURI("/admin/tasks-factory;create");

    public static SimpleURI TASKS_FACTORY_EDIT   = new SimpleURI("/admin/tasks-factory;edit");

    public static SimpleURI TASKS_FACTORY_COMMIT = new SimpleURI("/admin/tasks-factory;commit");

    public static void advance(final ProductEngine engine, final long now) {
        final List<TaskFactory> factories = engine.select_taskfactory().to_list().done();
        for (final TaskFactory factory : factories) {
            Task currentTask = null;
            final String currentTaskId = factory.get("current_task");
            if (currentTaskId != null) {
                currentTask = engine.task_by_id(currentTaskId, false);
            }
            final int daysAfter = factory.getAsInt("frequency");
            if (factory.ready(now)) {
                if (Task.isClosedAndReadyForTransition(currentTask, now, daysAfter)) {
                    final Task task = new Task();
                    task.generateAndSetId();
                    task.copyFrom(factory, "name", "description", "priority");
                    task.created();
                    task.setDue(now, factory.getAsInt("slack"));
                    factory.set("current_task", task.getId());
                    engine.put(factory);
                    engine.put(task);
                    final EventPayload payload = new EventPayload("'" + task.get("name") + "' has been scheduled automatically.");
                    engine.eventBus.trigger(Event.TaskCreation, payload);
                }
            }
        }
    }

    public static HtmlPump getProgress(final TaskFactory factory, final Task task, final long now) {
        if (task != null) {
            final String state = task.get("state");
            if ("closed".equals(state)) {
                final int daysAfter = factory.getAsInt("frequency");
                for (int k = 0; k <= daysAfter; k++) {
                    final long futureNow = new DateTime(now).plusDays(k).getMillis();
                    if (factory.ready(futureNow) && Task.isClosedAndReadyForTransition(task, futureNow, daysAfter)) {
                        return Html.block().add("ready in " + k + " days:" + RawObject.isoTimestamp(futureNow));
                    }
                }
                return Html.block().add(">" + daysAfter + " days");
            } else {
                return Html.block().add("blocked");
            }
        } else {
            return Html.block().add("about to create");
        }
    }

    public static void link(final CounterCodeGen c) {
        c.section("Page: Task Factory");

        c.counter("task_factory_monitor_run", "How many runs of the task factory have there been");
    }

    public static void link(final RoutingTable routing) {
        routing.navbar(TASKS_FACTORY, "Task Factory", Permission.SeeTaskFactoryTab);

        routing.get(TASKS_FACTORY, (sr) -> new TaskFactoryManagement(sr).list());
        routing.get(TASKS_FACTORY_CREATE, (sr) -> new TaskFactoryManagement(sr).create());
        routing.get(TASKS_FACTORY_EDIT, (sr) -> new TaskFactoryManagement(sr).update());
        routing.post(TASKS_FACTORY_COMMIT, (sr) -> new TaskFactoryManagement(sr).commit());
    }

    public TaskFactoryManagement(final SessionRequest session) {
        super(session, TASKS_FACTORY);
    }

    public String commit() {
        person().mustHave(Permission.EditTaskFactory);
        final TaskFactory factory = query().taskfactory_by_id(this.session.getParam("id"), true);
        if (this.engine.projection_taskfactory_edit_of(this.session).apply(factory).success()) {
            if (factory.get("name") == null) {
                query().del(factory);
            } else {
                query().put(factory);
            }
        }
        redirect(TASKS_FACTORY.href());
        return null;
    }

    public String create() {
        person().mustHave(Permission.EditTaskFactory);
        return createUpdateForm("Create Factory", UUID.randomUUID().toString(), "create", TASKS_FACTORY_CREATE);
    }

    private String createUpdateForm(final String title, final String id, final String commitLabel, final SimpleURI caller) {
        final TaskFactory factory = query().taskfactory_by_id(id, true);
        final Block formInner = Html.block();
        formInner.add(Html.input("id").pull(factory));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("name", "Name")) //
                .wrap(Html.input("name").id_from_name().pull(factory).text()).wrap(Html.wrapped().small().muted_form_text().wrap("The future name of the task.")));

        // description of the future task
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("description", "Description")) //
                .wrap(Html.input("description").id_from_name().pull(factory).textarea(4, 50)).wrap(Html.wrapped().small().muted_form_text().wrap("The future description of the task.")));

        // priority
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("priority", "Priority")) //
                .wrap(Html.input("priority").id_from_name().pull(factory).text()) //
                .wrap(Html.wrapped().small().muted_form_text().wrap("What is the priority of the future task (1 = urgent, 5 = not super urgent).")));

        // frequency
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("frequency", "Frequency")) //
                .wrap(Html.input("frequency").id_from_name().pull(factory).text()) //
                .wrap(Html.wrapped().small().muted_form_text().wrap("How many days should be this done (i.e. 7 = 1 week).")));

        // snooze_time
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("snooze_time", "Snooze Time")) //
                .wrap(Html.input("snooze_time").id_from_name().pull(factory).text()) //
                .wrap(Html.wrapped().small().muted_form_text().wrap("How many minutes can this item be snoozed for.")));

        // slack
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("slack", "Slack")) //
                .wrap(Html.input("slack").id_from_name().pull(factory).text()) //
                .wrap(Html.wrapped().small().muted_form_text().wrap("How relaxed is the timing; this controls the due date of the future task.")));

        // day_filter
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.bitmask("day_filter", TypeDayFilter.PROVIDER).pull(factory)) //
                .wrap(Html.wrapped().small().muted_form_text().wrap("which days may this take place")));

        // month_filter
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.bitmask("month_filter", TypeMonthFilter.PROVIDER).pull(factory)) //
                .wrap(Html.wrapped().small().muted_form_text().wrap("which months may this take place")));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.input("submit").id_from_name().value(commitLabel).submit()));

        final Block page = Html.block();
        page.add(tabs(caller));
        page.add(Html.wrapped().h4().wrap(title));
        page.add(Html.form("post", TASKS_FACTORY_COMMIT.href()).inner(formInner));
        return finish_pump(page);
    }

    public String list() {
        person().mustHave(Permission.SeeTaskFactoryTab);
        final Block block = Html.block();
        block.add(tabs(TASKS_FACTORY));

        final Table table = new Table("Name", "Progress", "Actions");
        final List<TaskFactory> factories = query().select_taskfactory().to_list().done();
        for (final TaskFactory factory : factories) {
            Task currentTask = null;
            final String currentTaskId = factory.get("current_task");
            if (currentTaskId != null) {
                currentTask = query().task_by_id(currentTaskId, false);
            }
            final Block actions = Html.block().add_if(person().has(Permission.EditTaskFactory), Html.link(TASKS_FACTORY_EDIT.href("id", factory.getId()), "{update}").btn_primary());
            final HtmlPump progress = getProgress(factory, currentTask, System.currentTimeMillis());
            final HtmlPump name = Html.block().add(factory.get("name")).add(" ").add(Tasks.priorityRender((int) factory.getAsDouble("priority")));
            table.row(name, progress, actions);
        }
        block.add(table);
        return finish_pump(block);
    }

    public HtmlPump tabs(final SimpleURI current) {
        final Link tabList = Html.link(TASKS_FACTORY.href(), "Factories").nav_link().active_if_href_is(current.href());
        final Link tabCreate = Html.link(TASKS_FACTORY_CREATE.href(), "Create").nav_link().active_if_href_is(current.href());
        return Html.nav().pills().with(tabList).with_if(person().has(Permission.EditTaskFactory), tabCreate);
    }

    public String update() {
        person().mustHave(Permission.EditTaskFactory);
        return createUpdateForm("Update Factory", this.session.getParam("id"), "update", TASKS_FACTORY_EDIT);
    }
}
