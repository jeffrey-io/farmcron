package farm.bsg.html;

import farm.bsg.data.contracts.SingleCharacterBitmaskProvider;

public class Html {

    public static Input input(String name) {
        return new Input(name);
    }

    public static Table table(String... headings) {
        return Table.start(headings);
    }

    public static Label label(String id, String label) {
        return new Label(id, text(label));
    }

    public static Text text(String s) {
        return new Text(s);
    }

    public static Tag tag() {
        return new Tag();
    }

    public static BreadCrumbs breadcrumbs() {
        return new BreadCrumbs();
    }

    public static Link link(String href, String label) {
        return new Link(href, text(label));
    }

    public static Link link(String href, HtmlPump label) {
        return new Link(href, label);
    }

    public static Block block() {
        return new Block();
    }

    public static WrappedBlock W() {
        return new WrappedBlock();
    }

    public static WrappedBlock wrapped() {
        return new WrappedBlock();
    }

    public static Nav nav() {
        return new Nav();
    }

    public static Form form(String method, String href) {
        return new Form(method, href);
    }
    
    public static BitMaskInput bitmask(String name, SingleCharacterBitmaskProvider provider) {
        return new BitMaskInput(name, provider);
    }
}
