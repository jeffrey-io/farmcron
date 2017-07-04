package farm.bsg.pages;

import java.util.List;
import java.util.UUID;

import farm.bsg.EventBus.Event;
import farm.bsg.EventBus.EventPayload;
import farm.bsg.Security.Permission;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.html.HtmlPump;
import farm.bsg.html.Link;
import farm.bsg.html.Table;
import farm.bsg.models.Task;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;

public class Tasks extends SessionPage {

    public static SimpleURI TASKS            = new SimpleURI("/admin/tasks");

    public static SimpleURI TASKS_CREATE     = new SimpleURI("/admin/tasks;create");

    public static SimpleURI TASKS_UPDATE     = new SimpleURI("/admin/tasks;update");

    public static SimpleURI TASKS_COMMIT     = new SimpleURI("/admin/tasks;commit");

    public static SimpleURI TASKS_TRANSITION = new SimpleURI("/admin/tasks;transition");

    public static void link(final CounterCodeGen c) {
        c.section("Page: Chores");
    }

    public static void link(final RoutingTable routing) {
        routing.navbar(TASKS, "Tasks", Permission.SeeTasksTab);
        routing.get(TASKS, (sr) -> new Tasks(sr).list());
        routing.get(TASKS_CREATE, (sr) -> new Tasks(sr).create());
        routing.get(TASKS_UPDATE, (sr) -> new Tasks(sr).update());
        routing.post(TASKS_COMMIT, (sr) -> new Tasks(sr).commit());
        routing.get_or_post(TASKS_TRANSITION, (sr) -> new Tasks(sr).transition());
    }

    public static HtmlPump priorityRender(final int status) {
        if (status == 0) {
            return Html.tag().danger().pill().content("urgent");
        }
        if (status == 1) {
            return Html.tag().warning().pill().content("high");
        }
        return Html.tag().info().pill().content("normal");
    }

    public Tasks(final SessionRequest session) {
        super(session, TASKS);
    }

    public String commit() {
        person().mustHave(Permission.EditTasks);
        final Task task = query().task_by_id(this.session.getParam("id"), true);
        if (task.get("state") == null) {
            task.setState("created");
        }
        task.importValuesFromReqeust(this.session, "");
        if (task.get("name") == null) {
            query().del(task);
        } else {
            query().put(task);
            if ("true".equals(this.session.getParam("notify"))) {
                final EventPayload payload = new EventPayload("'" + task.get("name") + "' was created.");
                this.engine.eventBus.trigger(Event.TaskCreation, payload);
            }
        }
        redirect(TASKS.href());
        return null;
    }

    public String create() {
        person().mustHave(Permission.EditTasks);
        return createUpdateForm("Create Task", UUID.randomUUID().toString(), "create", TASKS_CREATE, true);
    }

    private String createUpdateForm(final String title, final String id, final String commitLabel, final SimpleURI caller, final boolean notify) {
        final Task task = query().task_by_id(id, true);
        final Block formInner = Html.block();
        formInner.add(Html.input("id").pull(task));

        if (notify) {
            formInner.add(Html.input("notify").value("true"));
        }

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("name", "Name")) //
                .wrap(Html.input("name").id_from_name().pull(task).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("priority", "Priority")) //
                .wrap(Html.input("priority").id_from_name().pull(task).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("description", "Description")) //
                .wrap(Html.input("description").id_from_name().pull(task).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.input("submit").id_from_name().value(commitLabel).submit()));

        final Block page = Html.block();
        page.add(tabs(caller));
        page.add(Html.wrapped().h4().wrap(title));
        page.add(Html.form("post", TASKS_COMMIT.href()).inner(formInner));
        return finish_pump(page);
    }

    public String list() {
        person().mustHave(Permission.SeeTasksTab);
        final Block block = Html.block();
        block.add(tabs(TASKS));

        final boolean ableToStart = person().has(Permission.StartTask);
        final boolean ableToClose = person().has(Permission.CloseTask);

        final Table table = new Table("Name", "State", "Due", "Actions");
        final List<Task> tasks = query().select_task().where_state_eq("created", "started").to_list().done();
        for (final Task task : tasks) {
            final Block actions = Html.block() //
                    .add(Html.link(TASKS_UPDATE.href("id", task.getId()), "{update}").btn_primary()) //
                    .add_if(ableToStart && task.canStart(), Html.link(TASKS_TRANSITION.href("id", task.getId(), "state", "started"), "{start}").btn_primary())//
                    .add_if(ableToClose && task.canClose(), Html.link(TASKS_TRANSITION.href("id", task.getId(), "state", "closed"), "{close}").btn_primary());
            final HtmlPump name = Html.block().add(task.get("name")).add(priorityRender((int) task.getAsDouble("priority")));
            table.row(name, task.get("state"), task.get("due_date"), actions);
        }

        block.add(table);
        return finish_pump(block);
    }

    public HtmlPump tabs(final SimpleURI current) {
        final Link tabList = Html.link(TASKS.href(), "Tasks").nav_link().active_if_href_is(current.href());
        final Link tabCreate = Html.link(TASKS_CREATE.href(), "Create").nav_link().active_if_href_is(current.href());
        return Html.nav().pills().with(tabList).with_if(person().has(Permission.EditTasks), tabCreate);
    }

    public String transition() {
        final Task task = query().task_by_id(this.session.getParam("id"), false);
        if (task != null) {
            final String newState = this.session.getParam("state");
            if ("started".equals(newState)) {
                person().mustHave(Permission.StartTask);
            }
            if ("closed".equals(newState)) {
                person().mustHave(Permission.CloseTask);
            }

            if (newState != null) {
                task.setState(newState);
                query().put(task);
            }
        }
        redirect(TASKS.href());
        return null;
    }

    public String update() {
        person().mustHave(Permission.EditTasks);
        return createUpdateForm("Update Task", this.session.getParam("id"), "update", TASKS_UPDATE, false);
    }
}
