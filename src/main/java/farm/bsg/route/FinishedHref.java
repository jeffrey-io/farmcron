package farm.bsg.route;

public class FinishedHref {

    public final String value;

    public FinishedHref(final String href) {
        this.value = href;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
