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

    public SessionPage(final SessionRequest session, final SimpleURI uri) {
        super(session.engine, uri);
        this.session = session;
        this.currentTitle = this.engine.navbar.title(uri.toRoutingPattern());
    }

    public String finish_pump(final HtmlPump pump) {
        final StringBuilder html = new StringBuilder();
        pump.pump(html);
        final String navbar = this.engine.navbar.html(this.href, this.session);
        return this.engine.template.html(this.currentTitle, navbar, html.toString());
    }

    public boolean has(final Permission permission) {
        return this.session.has(permission);
    }

    public void mustHave(final Permission permission) {

    }

    public Person person() {
        return this.session.getPerson();
    }

    public void redirect(final FinishedHref href) {
        this.session.redirect(href);
    }

}
