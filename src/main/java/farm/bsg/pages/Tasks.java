package farm.bsg.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.amazonaws.util.json.Jackson;

import farm.bsg.BsgCounters;
import farm.bsg.EventBus.Event;
import farm.bsg.EventBus.EventPayload;
import farm.bsg.ProductEngine;
import farm.bsg.Security.Permission;
import farm.bsg.cron.PeriodicJob;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.html.HtmlPump;
import farm.bsg.html.Link;
import farm.bsg.html.Table;
import farm.bsg.models.Cart;
import farm.bsg.models.Task;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.ApiAction;
import farm.bsg.route.ApiRequest;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;

public class Tasks extends SessionPage {
    public static class TasksMonitor implements PeriodicJob {

        private final ProductEngine engine;

        public TasksMonitor(final ProductEngine engine) {
            this.engine = engine;
        }

        @Override
        public void run(final long now) {
            BsgCounters.I.task_factory_monitor_run.bump();
            advance(this.engine, now);
        }
    }

    public static SimpleURI TASKS            = new SimpleURI("/admin/tasks");

    public static SimpleURI TASKS_CREATE     = new SimpleURI("/admin/tasks;create");

    public static SimpleURI TASKS_UPDATE     = new SimpleURI("/admin/tasks;update");

    public static SimpleURI TASKS_COMMIT     = new SimpleURI("/admin/tasks;commit");

    public static SimpleURI TASKS_TRANSITION = new SimpleURI("/admin/tasks;transition");

    public static SimpleURI GOSSIP           = new SimpleURI("/api/gossip;tasks");

    public static void advance(final ProductEngine engine, final long now) {
        final ArrayList<Task> tasks = engine.select_task().where_state_eq("snoozed").done();
        for (final Task task : tasks) {
            if (task.ready()) {
                task.wake();
                engine.put(task);
            }
        }
    }

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
        
        routing.api_post(GOSSIP, new ApiAction() {
            
            @Override
            public Object handle(ApiRequest request) {
                // apply the changes
                
                HashMap<String, Object> root = new HashMap<>();
                for (Task task : request.engine.select_task().where_state_eq("created").done()) {
                    HashMap<String, String> item = new HashMap<>();
                    item.put("name", task.get("name"));
                    root.put(task.getId(), item);
                }
                return Jackson.toJsonString(root);
            }
        });
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

    private Table active() {
        final boolean ableToClose = person().has(Permission.CloseTask);
        final Table table = new Table("Name", "Due", "Actions");
        final List<Task> tasks = query().select_task().where_state_eq("created", "started").to_list().done();
        for (final Task task : tasks) {
            final boolean canExecute = ableToClose && task.canTransition();
            final Block actions = Html.block() //
                    .add(Html.link(TASKS_UPDATE.href("id", task.getId()), "{update}").btn_primary()) //
                    .add_if(canExecute, Html.link(TASKS_TRANSITION.href("id", task.getId(), "state", "snoozed"), "Snooze").btn_secondary()) //
                    .add_if(canExecute, Html.link(TASKS_TRANSITION.href("id", task.getId(), "state", "closed"), "Close").btn_primary()) //
                    ;
            final HtmlPump name = Html.block().add(task.get("name")).add(" ").add(priorityRender((int) task.getAsDouble("priority")));
            table.row(name, task.get("due_date"), actions);
        }
        return table;
    }

    public String commit() {
        person().mustHave(Permission.EditTasks);
        final Task task = query().task_by_id(this.session.getParam("id"), true);
        if (task.get("state") == null) {
            task.created();
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
                .wrap(Html.label("snooze_time", "Snooze Time")) //
                .wrap(Html.input("snooze_time").id_from_name().pull(task).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("description", "Description")) //
                .wrap(Html.input("description").id_from_name().pull(task).textarea(3, 40)));

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
        block.add(active());
        block.add(snoozed());
        return finish_pump(block);
    }

    private Table snoozed() {
        final boolean ableToClose = person().has(Permission.CloseTask);
        final Table table = new Table("Name", "Ready At", "Actions");
        final List<Task> tasks = query().select_task().where_state_eq("snoozed").to_list().done();
        for (final Task task : tasks) {
            final boolean canExecute = ableToClose && task.canTransition();
            final Block actions = Html.block() //
                    .add(Html.link(TASKS_UPDATE.href("id", task.getId()), "{update}").btn_primary()) //
                    .add_if(canExecute, Html.link(TASKS_TRANSITION.href("id", task.getId(), "state", "closed"), "Close").btn_primary()) //
                    ;
            final HtmlPump name = Html.block().add(task.get("name")).add(" ").add(priorityRender((int) task.getAsDouble("priority")));
            table.row(name, task.readyIsoTime(), actions);
        }
        return table;
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
            final String cartId = task.get("cart_id");
            Cart cart = null;
            boolean saveCart = false;
            if (cartId != null) {
                cart = query().cart_by_id(cartId, false);
            }
            boolean write = false;

            if ("snoozed".equals(newState)) {
                person().mustHave(Permission.CloseTask);
                write = task.snooze();
            }
            if ("closed".equals(newState)) {
                person().mustHave(Permission.CloseTask);
                if (cart != null) {
                    cart.set("state", "done");
                    saveCart = true;
                }
                task.close();
            }

            if (write) {
                query().put(task);
                if (saveCart) {
                    query().put(cart);
                }
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
