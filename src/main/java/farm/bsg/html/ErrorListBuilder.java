package farm.bsg.html;

public class ErrorListBuilder {
    private final StringBuilder list;
    private boolean             errored;

    public ErrorListBuilder() {
        this.list = new StringBuilder();
        this.errored = false;
    }

    public void accept(final boolean trueForError, final String errorMessageIfTrue) {
        if (trueForError) {
            this.errored = true;
            this.list.append("<li>").append(errorMessageIfTrue).append("</li>");
        }
    }

    public String getErrors() {
        return this.list.toString();
    }

    public boolean hasErrored() {
        return this.errored;
    }

    @Override
    public String toString() {
        return getErrors();
    }
}
