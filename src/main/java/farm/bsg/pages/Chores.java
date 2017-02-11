package farm.bsg.pages;

import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

import farm.bsg.Security.Permission;
import farm.bsg.data.PutResult;
import farm.bsg.data.RawObject;
import farm.bsg.data.Value;
import farm.bsg.data.types.TypeDayFilter;
import farm.bsg.data.types.TypeMonthFilter;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.html.HtmlPump;
import farm.bsg.html.Table;
import farm.bsg.html.shit.ObjectModelForm;
import farm.bsg.models.Chore;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;

public class Chores extends SessionPage {

    public Chores(SessionRequest session) {
        super(session, "/chores");
    }

    private HtmlPump actions(Chore chore, boolean canPerform) {
        Block actions = Html.block();

        if (has(Permission.ViewChore)) {
            actions.add(Html.link("/chore-view?id=" + chore.get("id"), "view").btn_primary());
        }
        if (has(Permission.EditChore)) {
            actions.add(Html.link("/chore-edit?id=" + chore.get("id"), "edit").btn_secondary());
        }
        if (has(Permission.PerformChore) && canPerform) {
            actions.add(Html.link("/chore-perform?id=" + chore.get("id"), "perform").btn_secondary());
        }
        return actions;
    }

    public String list() {
        Block page = Html.block();
        Table readyChores = new Table("Name", "First Day", "Due", "Days Available", "FT", "Actions");
        Table notReadyChores = new Table("Name", "First Day", "Due", "Days Available", "FT", "Actions");
        List<Chore> chores = query().select_chore().to_list().inline_order_by(new Chore.Ranking()).done();

        double hours = 0.0;
        boolean showHours = true;

        double[] hoursByDay = new double[7];

        String[] labels = new String[] { "M", "TU", "W", "TH", "F", "SA", "SU" };

        for (Chore chore : chores) {

            boolean ableToPerform = chore.canBeDoneBy(person());
            Table table = chore.ready() ? readyChores : notReadyChores;

            if (!chore.complete()) {
                showHours = false;
            }
            double timePerDay = chore.getAsDouble("time_to_perform_hours");
            double futureTime = chore.future() * timePerDay;
            hours += futureTime;
            StringBuilder timing = new StringBuilder();
            timing.append("total=").append(Math.round(futureTime * 10) / 10.0);

            for (int k = 0; k < hoursByDay.length; k++) {
                int instances = chore.futureByDay()[k];
                if (instances > 0) {
                    double delta = timePerDay * instances;
                    hoursByDay[k] += delta;
                    timing.append(", ").append(labels[k]).append("=").append(Math.round(delta * 10) / 10.0);

                }
            }
            Block name = Html.block() //
                    .add(chore.get("name")) //
                    .add_if(chore.late(), Html.tag().pill().warning().content("late")) //
                    .add_if(!chore.complete(), Html.tag().pill().warning().content("incomplete"));
            table.row(//
                    name, //
                    chore.firstAvailableDay(), //
                    chore.dayDue(), //
                    chore.daysAvailable(), //
                    timing.toString(), //
                    actions(chore, ableToPerform));
        }
        page.add(Html.wrapped().h5().wrap("Ready Chores"));
        page.add(readyChores);
        page.add(Html.wrapped().h5().wrap("Not Ready Chores"));
        page.add(notReadyChores);
        DateTime now = new DateTime();
        int weeksLeft = -1;
        int year = now.getYear();
        while (year == now.getYear()) {
            weeksLeft ++;
            now = now.plusDays(7);
        }
        if (weeksLeft == 0) {
            weeksLeft = 1;
        }
        if (showHours) {
            page.add(Html.wrapped().h5().wrap("Work Left: " + weeksLeft));
            page.add(Html.wrapped().p().wrap("Hours: ").wrap("" + hours));
            page.add(Html.wrapped().p().wrap("8 Hr Days: ").wrap("" + (hours / 8.0)));
            for (int k = 0; k < hoursByDay.length; k++) {
                double weeklyHeat = Math.round(10 * hoursByDay[k] / weeksLeft) / 10.0;
                page.add(Html.wrapped().p() //
                        .wrap("On " + labels[k] + ": ").wrap("" + hoursByDay[k]) //
                        .wrap(" (").wrap("" + weeklyHeat).wrap(" per wk)") //
                        .wrap_if(weeklyHeat > 4.0, Html.tag().pill().danger().content("HOT")));
            }
        } else {
            page.add(Html.wrapped().h5().wrap("Fill in the time available to get an estimate for the work left"));
        }
        page.add(Html.link("/new-chore", "Create New Chore").btn_primary());
        return finish_pump(page);
    }

    public String view() {
        Chore chore = query().chore_by_id(session.getParam("id"), true);
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>view</h1>");
        sb.append(ObjectModelForm.htmlOf(chore));
        sb.append("<h1>extended actions</h1>");
        return formalize_html(sb.toString());
    }

    private String edit_page(Chore chore, String current, String commitLabel, String title, PutResult putResult) {
        Block formInner = Html.block();
        formInner.add(Html.input("id").pull(chore));

        // name
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("name", "Name")) //
                .wrap(Html.input("name").id_from_name().pull(chore).text()));

        // frequency
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("frequency", "Frequency")) //
                .wrap(Html.input("frequency").id_from_name().pull(chore).text()) //
                .wrap(Html.wrapped().small().muted_form_text().wrap("How many days should be this done (i.e. 7 = 1 week).")));

        // slack
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("slack", "Slack")) //
                .wrap(Html.input("slack").id_from_name().pull(chore).text()) //
                .wrap(Html.wrapped().small().muted_form_text().wrap("TODO: define slack or remove.")));

        // manual
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("manual", "Manual / Notes")) //
                .wrap(Html.input("manual").id_from_name().pull(chore).textarea(4, 50)).wrap(Html.wrapped().small().muted_form_text().wrap("Notes about how to do this task")));

        // time_to_perform_hours
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("time_to_perform_hours", "Time to Perform (hours)")) //
                .wrap(Html.input("time_to_perform_hours").id_from_name().pull(chore).text()) //
                .wrap(Html.wrapped().small().muted_form_text().wrap("How many hours does this chore take")));

        // day_filter
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.bitmask("day_filter", TypeDayFilter.PROVIDER).pull(chore)) //
                .wrap(Html.wrapped().small().muted_form_text().wrap("which days may this take place")));

        // month_filter
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.bitmask("month_filter", TypeMonthFilter.PROVIDER).pull(chore)) //
                .wrap(Html.wrapped().small().muted_form_text().wrap("which months may this take place")));

        // hour_filter
        // weather_requirements
        // equipment_skills_required

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.input("submit").id_from_name().value(commitLabel).submit()));

        Block page = Html.block();
        page.add(Html.wrapped().h4().wrap(title));
        page.add(Html.form("post", "/chore-edit").inner(formInner));
        return finish_pump(page);
    }

    public String edit() {
        Chore chore = query().chore_by_id(session.getParam("id"), true);
        if (session.hasNonNullQueryParam("submit")) {
            PutResult put = query().projection_chore_edit_of(session).apply(chore);
            if (put.success()) {
                if (chore.getAsInt("frequency") == 0) { // TOOD; this is a terrible UI
                    session.engine.storage.put(chore.getStorageKey(), null);
                } else {
                    put = engine.storage.put(chore.getStorageKey(), new Value(chore.toJson()));
                }
            }
            if (!put.success()) {
                return edit_page(chore, "", "Save", "Edit Chore", put);
            } else {
                redirect("/chores");
            }
        }
        return edit_page(chore, "", "Save", "Edit Chore", new PutResult());
    }

    public String perform() {
        Chore chore = query().chore_by_id(session.getParam("id"), true);
        chore.set("last_performed", RawObject.isoTimestamp());
        chore.set("last_performed_by", person().getId());
        session.engine.storage.put(chore.getStorageKey(), new Value(chore.toJson()));
        redirect("/chores");
        return null;
    }

    public static void link(RoutingTable routing) {
        routing.navbar("/chores", "Chores", Permission.SeeChoresTab);

        routing.get("/chores", (session) -> new Chores(session).list());
        routing.get("/new-chore", (session) -> {
            session.redirect("/chore-edit?id=" + UUID.randomUUID().toString());
            return null;
        });

        routing.get_or_post("/chore-edit", (session) -> new Chores(session).edit());

        routing.get_or_post("/chore-view", (session) -> new Chores(session).view());
        routing.get_or_post("/chore-perform", (session) -> new Chores(session).perform());
    }

    public static void link(CounterCodeGen c) {
        c.section("Page: Chores");
    }
}
