package farm.bsg.pages.common;

import farm.bsg.Security.Permission;
import farm.bsg.html.HtmlPump;
import farm.bsg.models.Person;
import farm.bsg.route.FinishedHref;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;

public class SessionPage extends GenericPage {
    protected final SessionRequest session;
    protected String               currentTitle;

    public SessionPage(SessionRequest session, SimpleURI uri) {
        super(session.engine, uri);
        this.session = session;
        this.currentTitle = engine.navbar.title(uri.toRoutingPattern());
    }

    public String finish_pump(HtmlPump pump) {
        StringBuilder html = new StringBuilder();
        pump.pump(html);
        String navbar = engine.navbar.html(href, session);
        return engine.template.html(currentTitle, navbar, html.toString());
    }

    public Person person() {
        return session.getPerson();
    }

    public void redirect(FinishedHref href) {
        session.redirect(href);
    }

    public boolean has(Permission permission) {
        return session.has(permission);
    }

    public void mustHave(Permission permission) {

    }

}
