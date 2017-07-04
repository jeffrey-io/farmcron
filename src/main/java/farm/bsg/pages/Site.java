package farm.bsg.pages;

import farm.bsg.Security.Permission;
import farm.bsg.html.Block;
import farm.bsg.html.Html;
import farm.bsg.models.SiteProperties;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;

public class Site extends SessionPage {
    public Site(SessionRequest session) {
        super(session, SITE);
    }

    public SiteProperties pullSite() {
        SiteProperties prop = query().siteproperties_get();
        prop.importValuesFromReqeust(session, "");
        return prop;
    }

    public String show() {
        person().mustHave(Permission.WebMaster);
        SiteProperties properties = pullSite();
        if ("yes".equals(session.getParam("commit"))) {
            query().put(properties);
        }

        Block formInner = Html.block();
        formInner.add(Html.input("id").pull(properties));
        formInner.add(Html.input("commit").value("yes"));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("domain", "Domain")) //
                .wrap(Html.input("domain").id_from_name().pull(properties).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("product_name", "Site Name")) //
                .wrap(Html.input("product_name").id_from_name().pull(properties).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("description", "Description")) //
                .wrap(Html.input("description").id_from_name().pull(properties).textarea(4, 60)));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("fb_page_token", "Facebook Page Token")) //
                .wrap(Html.input("fb_page_token").id_from_name().pull(properties).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("twilio_phone_number", "Twilio Phone Number")) //
                .wrap(Html.input("twilio_phone_number").id_from_name().pull(properties).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("twilio_username", "Twilio Username (or SID)")) //
                .wrap(Html.input("twilio_username").id_from_name().pull(properties).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("twilio_password", "Twilio Password (or Token)")) //
                .wrap(Html.input("twilio_password").id_from_name().pull(properties).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("admin_phone", "Admin Phone Number (for System Events)")) //
                .wrap(Html.input("admin_phone").id_from_name().pull(properties).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("product_imaging_thumbprint_size", "Product Imaging Thumbprint Size")) //
                .wrap(Html.input("product_imaging_thumbprint_size").id_from_name().pull(properties).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("product_imaging_normal_size", "Product Imaging Normal Size")) //
                .wrap(Html.input("product_imaging_normal_size").id_from_name().pull(properties).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.input("submit").id_from_name().value("Update").submit()));

        Block page = Html.block();
        page.add(Html.form("post", SITE.href()).inner(formInner));
        return finish_pump(page);
    }

    public static void link(RoutingTable routing) {
        routing.navbar(SITE, "Site", Permission.WebMaster);
        routing.get_or_post(SITE, (session) -> new Site(session).show());
    }

    public static SimpleURI SITE = new SimpleURI("/admin/edit-site-properties");

    public static void link(CounterCodeGen c) {
        c.section("Page: Site");
    }
}
