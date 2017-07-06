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
    public static SimpleURI SITE = new SimpleURI("/admin/edit-site-properties");

    public static void link(final CounterCodeGen c) {
        c.section("Page: Site");
    }

    public static void link(final RoutingTable routing) {
        routing.navbar(SITE, "Site", Permission.WebMaster);
        routing.get_or_post(SITE, (session) -> new Site(session).show());
    }

    public Site(final SessionRequest session) {
        super(session, SITE);
    }

    public SiteProperties pullSite() {
        final SiteProperties prop = query().siteproperties_get();
        prop.importValuesFromReqeust(this.session, "");
        return prop;
    }

    public String show() {
        person().mustHave(Permission.WebMaster);
        final SiteProperties properties = pullSite();
        if ("yes".equals(this.session.getParam("commit"))) {
            query().put(properties);
        }

        final Block formInner = Html.block();
        formInner.add(Html.input("id").pull(properties));
        formInner.add(Html.input("commit").value("yes"));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("domain", "Domain")) //
                .wrap(Html.input("domain").id_from_name().pull(properties).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("product_name", "Site Name")) //
                .wrap(Html.input("product_name").id_from_name().pull(properties).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("description", "Description of business")) //
                .wrap(Html.input("description").id_from_name().pull(properties).textarea(4, 60)));

        formInner.add(Html.W().h3().wrap("Business Hours"));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("business_hours", "Business Hours (Super Complex)")) //
                .wrap(Html.input("business_hours").id_from_name().pull(properties).textarea(8, 50)));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("business_timezone", "Timezone")) //
                .wrap(Html.input("business_timezone").id_from_name().pull(properties).text()));

        formInner.add(Html.W().h3().wrap("Public Phone"));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("business_phone", "Phone")) //
                .wrap(Html.input("business_phone").id_from_name().pull(properties).text()));

        formInner.add(Html.W().h3().wrap("Fulfilment"));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("fulfilment_strategy", "Fulfilment Strategy")) //
                .wrap(Html.input("fulfilment_strategy").id_from_name().pull(properties).select("none", "pickup", "delivery", "both")));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("delivery_radius", "Delivery Radius")) //
                .wrap(Html.input("delivery_radius").id_from_name().pull(properties).text()));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("pickup_rule", "Pick Rule (Super Complex)")) //
                .wrap(Html.input("pickup_rule").id_from_name().pull(properties).text()));

        formInner.add(Html.W().h3().wrap("Business Address"));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("business_address1", "Address.1")) //
                .wrap(Html.input("business_address1").id_from_name().pull(properties).text()));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("business_address2", "Address.2")) //
                .wrap(Html.input("business_address2").id_from_name().pull(properties).text()));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("business_city", "City")) //
                .wrap(Html.input("business_city").id_from_name().pull(properties).text()));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("business_state", "State")) //
                .wrap(Html.input("business_state").id_from_name().pull(properties).text()));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("business_postal", "Postal")) //
                .wrap(Html.input("business_postal").id_from_name().pull(properties).text()));

        formInner.add(Html.W().h3().wrap("Notification Connectivity"));
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

        formInner.add(Html.W().h3().wrap("Product Imaging"));
        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("product_imaging_thumbprint_size", "Product Imaging Thumbprint Size")) //
                .wrap(Html.input("product_imaging_thumbprint_size").id_from_name().pull(properties).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.label("product_imaging_normal_size", "Product Imaging Normal Size")) //
                .wrap(Html.input("product_imaging_normal_size").id_from_name().pull(properties).text()));

        formInner.add(Html.wrapped().form_group() //
                .wrap(Html.input("submit").id_from_name().value("Update").submit()));

        final Block page = Html.block();
        page.add(Html.form("post", SITE.href()).inner(formInner));
        return finish_pump(page);
    }
}
