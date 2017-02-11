package farm.bsg.pages.common;

import farm.bsg.ProductEngine;
import farm.bsg.QueryEngine;
import farm.bsg.Security.Permission;
import farm.bsg.html.HtmlPump;
import farm.bsg.models.Person;
import farm.bsg.route.SessionRequest;

public class SessionPage {
    protected final SessionRequest session;
    protected final ProductEngine  engine;
    protected final String       href;
    protected String             currentTitle;

    public SessionPage(SessionRequest session, String href) {
        this.session = session;
        this.engine = session.engine;
        this.href = href;
        this.currentTitle = engine.navbar.title(href);
    }

    public String formalize_html(String body) {
        String navbar = engine.navbar.html(href, session);
        return engine.template.html(currentTitle, navbar, body);
    }
    
    public String finish_pump(HtmlPump pump) {
        StringBuilder html = new StringBuilder();
        pump.pump(html);
        return formalize_html(html.toString());
    }

    public QueryEngine query() {
        return engine;
    }

    public Person person() {
        return session.getPerson();
    }

    public void redirect(String uri) {
        session.redirect(uri);
    }

    public boolean has(Permission permission) {
        return session.has(permission);
    }

    public void mustHave(Permission permission) {

    }

}
