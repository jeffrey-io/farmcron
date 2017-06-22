package farm.bsg.pages;

import java.util.List;
import java.util.UUID;

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

    public Tasks(SessionRequest session) {
        super(session, TASKS);
    }

    public HtmlPump tabs(SimpleURI current) {
        Link tabList = Html.link(TASKS.href(), "Tasks").nav_link().active_if_href_is(current.href());
        Link tabCreate = Html.link(TASKS_CREATE.href(), "Create").nav_link().active_if_href_is(current.href());
        return Html.nav().pills().with(tabList).with_if(person().has(Permission.EditTasks), tabCreate);
    }

    public String list() {
        person().mustHave(Permission.SeeTasksTab);
        Block block = Html.block();
        block.add(tabs(TASKS));

        boolean ableToStart = person().has(Permission.StartTask);
        boolean ableToClose = person().has(Permission.CloseTask);

        Table table = new Table("Name", "State", "Due", "Actions");
        List<Task> tasks = query().select_task().where_state_eq("created", "started").to_list().done();
        for (Task task : tasks) {
            Block actions = Html.block() //
                    .add(Html.link(TASKS_UPDATE.href("id", task.getId()), "{update}").btn_primary()) //
                    .add_if(ableToStart && task.canStart(), Html.link(TASKS_TRANSITION.href("id", task.getId(), "state", "started"), "{start}").btn_primary())//
                    .add_if(ableToClose && task.canClose(), Html.link(TASKS_TRANSITION.href("id", task.getId(), "state", "closed"), "{close}").btn_primary());
            table.row(task.get("name"), task.get("state"), task.get("due_date"), actions);
        }
        
        block.add(table);
        return finish_pump(block);
    }

    public String create() {
        person().mustHave(Permission.EditTasks);
        return createUpdateForm("Create Task", UUID.randomUUID().toString(), "create", TASKS_CREATE);
    }

    public String update() {
        person().mustHave(Permission.EditTasks);
        return createUpdateForm("Update Task", session.getParam("id"), "update", TASKS_UPDATE);
    }

    private String createUpdateForm(String title, String id, String commitLabel, SimpleURI caller) {
        Task task = query().task_by_id(id, true);
        Block formInner = Html.block();
        formInner.add(Html.input("id").pull(task));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("name", "Name")) //
                .wrap(Html.input("name").id_from_name().pull(task).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("description", "Description")) //
                .wrap(Html.input("description").id_from_name().pull(task).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.input("submit").id_from_name().value(commitLabel).submit()));

        Block page = Html.block();
        page.add(tabs(caller));
        page.add(Html.wrapped().h4().wrap(title));
        page.add(Html.form("post", TASKS_COMMIT.href()).inner(formInner));
        return finish_pump(page);
    }

    public String commit() {
        person().mustHave(Permission.EditTasks);
        Task task = query().task_by_id(session.getParam("id"), true);
        if (task.get("state") == null) {
            task.setState("created");
        }
        task.importValuesFromReqeust(session, "");
        if (task.get("name") == null) {
            query().del(task);
        } else {
            query().put(task);
        }
        redirect(TASKS.href().value);
        return null;
    }

    public String transition() {
        Task task = query().task_by_id(session.getParam("id"), false);
        if (task != null) {
            String newState = session.getParam("state");
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
        redirect(TASKS.href().value);
        return null;
    }

    public static void link(RoutingTable routing) {
        routing.navbar(TASKS, "Tasks", Permission.SeeTasksTab);
        routing.get(TASKS, (sr) -> new Tasks(sr).list());
        routing.get(TASKS_CREATE, (sr) -> new Tasks(sr).create());
        routing.get(TASKS_UPDATE, (sr) -> new Tasks(sr).update());
        routing.post(TASKS_COMMIT, (sr) -> new Tasks(sr).commit());
        routing.get_or_post(TASKS_TRANSITION, (sr) -> new Tasks(sr).transition());
    }

    public static SimpleURI TASKS            = new SimpleURI("/admin/tasks");
    public static SimpleURI TASKS_CREATE     = new SimpleURI("/admin/tasks;create");
    public static SimpleURI TASKS_UPDATE     = new SimpleURI("/admin/tasks;update");
    public static SimpleURI TASKS_COMMIT     = new SimpleURI("/admin/tasks;commit");
    public static SimpleURI TASKS_TRANSITION = new SimpleURI("/admin/tasks;transition");

    public static void link(CounterCodeGen c) {
        c.section("Page: Chores");
    }
}
