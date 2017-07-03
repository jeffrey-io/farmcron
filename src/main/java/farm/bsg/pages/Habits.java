package farm.bsg.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;

import farm.bsg.Security.Permission;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.html.HtmlPump;
import farm.bsg.html.Input;
import farm.bsg.html.Link;
import farm.bsg.html.Table;
import farm.bsg.models.Habit;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.FinishedHref;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;

public class Habits extends SessionPage {

    public Habits(SessionRequest session) {
        super(session, HABITS);
    }

    public HtmlPump habits_as_cards() {
        person().mustHave(Permission.HabitsUnlocked);
        Block block = Html.block();
        ArrayList<Habit> habits = query().select_habit() //
                .scope(person().getId()).to_list() //
                .inline_filter(h -> !h.cache(person()).able) //
                .inline_order_lexographically_asc_by("unlock_time", "name").done();

        int remaining = query().select_habit() //
                .scope(person().getId()).to_list() //
                .inline_filter(h -> h.cache(person()).done) //
                .count();

        if (remaining > 0) {
            block.add(Html.wrapped().card() //
                    .wrap(Html.wrapped().card_title().wrap("Remaining")) //
                    .wrap(Html.wrapped().card_text().wrap("You have " + remaining + " habits remaining for the day")));
        } else {
            block.add(Html.wrapped().h1().wrap("CONGRATS, you did it buddy!"));
        }

        for (Habit habit : habits) {
            HtmlPump tag = Html.tag().danger().content("Warning");
            HtmlPump text = Html.wrapped().card_text().wrap(habit.get("name")).wrap_if(habit.cache(person()).warn, tag);
            Link perform = Html.link(HABITS_PERFORM.href("id", habit.getId()), "Perform").btn_success();
            block.add(Html.wrapped().card().wrap(text).wrap(perform));
        }
        return block;
    }

    public String list_available_as_cards() {
        person().mustHave(Permission.HabitsUnlocked);
        Block page = Html.block();
        page.add(tabs(HABITS.href()));
        page.add(habits_as_cards());
        return finish_pump(page);
    }

    public HtmlPump tabs(FinishedHref current) {
        Link tab1 = Html.link(HABITS.href(), "Unlocked Habits").nav_link().active_if_href_is(current);
        Link tab2 = Html.link(HABITS_ALL.href(), "All Habits A-Z").nav_link().active_if_href_is(current);
        Link tab3 = Html.link(HABITS_TIMELINE.href(), "Edit Timeline").nav_link().active_if_href_is(current);
        Link tab4 = Html.link(HABITS_NEW.href(), "Add New Habit").nav_link().active_if_href_is(current);
        return Html.nav().pills().with(tab1).with(tab2).with(tab3).with(tab4);
    }

    public String all() {
        person().mustHave(Permission.HabitsUnlocked);
        Block block = Html.block();
        block.add(tabs(HABITS_ALL.href()));
        Table table = new Table("Name", "Last Done", "Last Arg", "Actions");
        List<Habit> habits = query().select_habit().scope(person().getId()).to_list().inline_order_lexographically_by(true, false, "name").done();
        for (Habit habit : habits) {
            Block actions = Html.block() //
                    .add(Html.link(HABITS_EDIT.href("id", habit.get("id")), "Edit").btn_info()) //
                    .add(" ") //
                    .add(Html.link(HABITS_HISTORY.href("id", habit.get("id")), "History").btn_info()) //
                    ;
            Block name = Html.block().add(habit.get("name"));
            table.row(//
                    name, //
                    habit.get("last_done"), //
                    habit.get("last_arg_given"), //
                    actions);
        }
        block.add(table);
        return finish_pump(block);
    }

    public String bulk_commit() {
        person().mustHave(Permission.HabitsUnlocked);
        int k = 0;
        while (true) {
            String id = session.getParam("id_" + k);
            if (id == null) {
                break;
            }
            Habit habit = query().habit_by_id(id, false);
            habit.importValuesFromReqeust(session, k + "_");
            engine.put(habit);
            k++;
        }
        session.redirect(HABITS_TIMELINE.href());
        return null;
    }

    public String timeline() {
        person().mustHave(Permission.HabitsUnlocked);
        Table table = new Table("Name", "Unlock", "Warning");
        List<Habit> habits = query().select_habit().scope(person().getId()).to_list().inline_order_lexographically_by(true, false, "unlock_time", "warn_time", "name").done();
        int k = 0;
        for (Habit habit : habits) {
            table.row(//
                    Html.wrapped() //
                            .wrap(habit.get("name")) //
                            .wrap(Html.input("id_" + k).value(habit.getId())), //
                    Html.input(k + "_unlock_time").id_from_name().pull(habit, "unlock_time").select_hour(), //
                    Html.input(k + "_warn_time").id_from_name().pull(habit, "warn_time").select_hour());
            k++;
        }
        Block innerForm = Html.block() //
                .add(tabs(HABITS_TIMELINE.href())).add(table) //
                .add(Html.input("submit").submit().value("Bulk Edit Times"));
        return finish_pump(Html.form("post", HABITS_BULK_COMMIT.href()).inner(innerForm));
    }

    public Habit pullHabit() {
        Habit habit = query().habit_by_id(session.getParam("id"), true);
        habit.importValuesFromReqeust(session, "");
        habit.set("who", person().getId());
        return habit;
    }

    public String history() {
        person().mustHave(Permission.HabitsUnlocked);
        Habit habit = pullHabit();
        Map<String, String> history = habit.getHistory();
        Table table = Html.table("Date", "value");
        for (Entry<String, String> entry : history.entrySet()) {
            table.row(entry.getKey(), entry.getValue());
        }
        Block page = Html.block();
        page.add(tabs(HABITS_ALL.href()));
        page.add(table);
        return finish_pump(page);
    }

    public String edit_old() {
        person().mustHave(Permission.HabitsUnlocked);
        Habit habit = pullHabit();
        return edit(habit, HABITS_ALL.href(), "Apply", "Edit Habit");
    }

    public String make_new() {
        person().mustHave(Permission.HabitsUnlocked);
        Habit habit = new Habit();
        habit.generateAndSetId();
        String sessionId = session.getPerson().getId();
        habit.set("id", sessionId + "/" + UUID.randomUUID().toString());
        habit.set("who", person().getId());
        return edit(habit, HABITS_NEW.href(), "Make", "New Habit");
    }

    private String edit(Habit habit, FinishedHref current, String commitLabel, String title) {
        person().mustHave(Permission.HabitsUnlocked);
        Block formInner = Html.block();
        formInner.add(Html.input("id").pull(habit));
        formInner.add(Html.input("who").pull(habit));
        formInner.add(Input.reset("has_arg"));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("name", "Name")) //
                .wrap(Html.input("name").id_from_name().pull(habit).text()));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("has_arg", "Has Argument")) //
                .wrap(Html.input("has_arg").id_from_name().pull(habit).checkbox()) //
                .wrap(Html.wrapped().small().muted_form_text().wrap("Upon completing, the system will ask for a value")));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("unlock_time", "Unlock Time")) //
                .wrap(Html.input("unlock_time").id_from_name().pull(habit).select_hour()) //
                .wrap(Html.wrapped().small().muted_form_text().wrap("When the habit will be shown to you for completion")));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("warn_time", "Warn Time")) //
                .wrap(Html.input("warn_time").id_from_name().pull(habit).select_hour()) //
                .wrap(Html.wrapped().small().muted_form_text().wrap("When a habit will be considered late")));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.input("submit").id_from_name().value(commitLabel).submit()));

        Block page = Html.block();
        page.add(tabs(current));
        page.add(Html.wrapped().h4().wrap(title));
        page.add(Html.form("post", HABITS_COMMIT_EDIT.href()).inner(formInner));
        return finish_pump(page);
    }

    private String provide_data(Habit habit) {
        person().mustHave(Permission.HabitsUnlocked);
        Block formInner = Html.block();
        formInner.add(Html.input("id").pull(habit));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("arg", "Argument Value")) //
                .wrap(Html.input("arg").id_from_name().pull(habit).text()));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.input("submit").id_from_name().value(currentTitle).submit()));

        Block page = Html.block();
        page.add(tabs(HABITS.href()));
        page.add(Html.wrapped().h4().wrap("Provide Arg:" + habit.get("name")));
        page.add(Html.form("post", HABITS_PERFORM.href()).inner(formInner));
        return finish_pump(page);
    }

    public String commit() {
        person().mustHave(Permission.HabitsUnlocked);
        Habit habit = pullHabit();
        if  (habit.isNullOrEmpty("name")) {
            query().del(habit);
        } else {
            query().put(habit);
        }
        
        redirect(HABITS_ALL.href());
        return null;
    }

    public String ask_or_perform() {
        person().mustHave(Permission.HabitsUnlocked);
        Habit habit = pullHabit();
        String arg = session.getParam("arg");
        boolean argApplied = arg != null;
        if (habit.getAsBoolean("has_arg") && !argApplied) {
            return provide_data(habit);
        } else {
            String day = person().getCurrentDay();
            habit.set("last_done", day);

            ArrayList<String> history = new ArrayList<>();
            String historyFlat = habit.get("history");
            if (historyFlat != null) {
                try {
                    JsonNode list = Jackson.jsonNodeOf(historyFlat);
                    for (int k = 0; k < list.size(); k++) {
                        history.add(list.get(k).asText());
                    }
                } catch (Exception err) {
                }
            }
            history.add(day + (argApplied ? ("=" + arg) : ""));
            if (argApplied) {
                habit.set("last_arg_given", arg);
            }
            habit.set("history", Jackson.toJsonString(history));
            query().put(habit);
            redirect(HABITS.href());
            return null;
        }
    }

    public static void link(RoutingTable routing) {
        routing.navbar(HABITS, "Habits", Permission.HabitsUnlocked);
        routing.get(HABITS, (session) -> new Habits(session).list_available_as_cards());
        routing.get(HABITS_ALL, (session) -> new Habits(session).all());
        routing.get(HABITS_TIMELINE, (session) -> new Habits(session).timeline());
        routing.get(HABITS_HISTORY, (session) -> new Habits(session).history());
        routing.get_or_post(HABITS_EDIT, (session) -> new Habits(session).edit_old());
        routing.get_or_post(HABITS_NEW, (session) -> new Habits(session).make_new());
        routing.post(HABITS_COMMIT_EDIT, (session) -> new Habits(session).commit());
        routing.get_or_post(HABITS_BULK_COMMIT, (session) -> new Habits(session).bulk_commit());
        routing.get_or_post(HABITS_PERFORM, (session) -> new Habits(session).ask_or_perform());
    }
    
    public static SimpleURI HABITS = new SimpleURI("/you;habits");
    public static SimpleURI HABITS_ALL = new SimpleURI("/you;habits;all");
    public static SimpleURI HABITS_TIMELINE = new SimpleURI("/you;habits;timeline");
    public static SimpleURI HABITS_HISTORY = new SimpleURI("/you;habits;history");
    public static SimpleURI HABITS_EDIT = new SimpleURI("/you;habit;edit");
    public static SimpleURI HABITS_NEW = new SimpleURI("/you;create;habit");
    public static SimpleURI HABITS_COMMIT_EDIT = new SimpleURI("/you;habit;edit;commit");
    public static SimpleURI HABITS_BULK_COMMIT = new SimpleURI("/you;habits;bulk;commit");
    public static SimpleURI HABITS_PERFORM = new SimpleURI("/you;habit;perform");


    public static void link(CounterCodeGen c) {
        c.section("Page: Habits");
    }

}
