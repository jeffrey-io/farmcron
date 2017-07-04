package farm.bsg.html;

import farm.bsg.data.contracts.SingleCharacterBitmaskProvider;
import farm.bsg.route.FinishedHref;

public class Html {

    public static BitMaskInput bitmask(final String name, final SingleCharacterBitmaskProvider provider) {
        return new BitMaskInput(name, provider);
    }

    public static Block block() {
        return new Block();
    }

    public static BreadCrumbs breadcrumbs() {
        return new BreadCrumbs();
    }

    public static Form form(final String method, final FinishedHref href) {
        return new Form(method, href.value);
    }

    public static Img img() {
        return new Img();
    }

    public static Input input(final String name) {
        return new Input(name);
    }

    public static Label label(final String id, final String label) {
        return new Label(id, text(label));
    }

    public static Link link(final FinishedHref href, final HtmlPump label) {
        return new Link(href.value, label);
    }

    public static Link link(final FinishedHref href, final String label) {
        return new Link(href.value, text(label));
    }

    @Deprecated
    public static Link link(final String href, final HtmlPump label) {
        return new Link(href, label);
    }

    public static Link link_direct(final String href, final String label) {
        return new Link(href, text(label));
    }

    public static Nav nav() {
        return new Nav();
    }

    public static Table table(final String... headings) {
        return Table.start(headings);
    }

    public static Tag tag() {
        return new Tag();
    }

    public static Text text(final String s) {
        return new Text(s);
    }

    public static WrappedBlock W() {
        return new WrappedBlock();
    }

    public static WrappedBlock wrapped() {
        return new WrappedBlock();
    }
}
