package farm.bsg.pages;

import farm.bsg.Security.Permission;
import farm.bsg.html.shit.ObjectModelForm;
import farm.bsg.models.SiteProperties;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;

public class Site extends SessionPage {
    public Site(SessionRequest session) {
        super(session, "/site");
    }

    public SiteProperties pullSite() {
        SiteProperties prop = query().siteproperties_get();
        prop.importValuesFromReqeust(session, "");
        return prop;
    }

    public String show() {
        SiteProperties properties = pullSite();
        StringBuilder sb = new StringBuilder();
        if (session.hasNonNullQueryParam("commit")) {
            if (has(Permission.EditSiteProperties)) {
                session.engine.save(properties);
            } else {
                sb.append("Unable to save");
                // handle error
            }
        }
        sb.append("<form method=\"post\" action=\"/site\">");
        sb.append(ObjectModelForm.htmlOf(properties));
        sb.append("<hr /><input class=\"btn btn-primary\" type=\"submit\" name=\"commit\" value=\"commit\">");
        return formalize_html(sb.toString());
    }

    public static void link(RoutingTable routing) {
        routing.navbar("/site", "Site", Permission.SeeSiteProperties);
        routing.get_or_post("/site", (session) -> new Site(session).show());
    }
    
    public static void link(CounterCodeGen c) {
        c.section("Page: Site");
    }
}
