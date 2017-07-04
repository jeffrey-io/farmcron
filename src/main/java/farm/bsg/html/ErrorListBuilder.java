package farm.bsg.html;

public class ErrorListBuilder {
    private final StringBuilder list;
    private boolean             errored;

    public ErrorListBuilder() {
        this.list = new StringBuilder();
        this.errored = false;
    }

    public void accept(boolean trueForError, String errorMessageIfTrue) {
        if (trueForError) {
            errored = true;
            list.append("<li>").append(errorMessageIfTrue).append("</li>");
        }
    }

    public boolean hasErrored() {
        return errored;
    }

    public String getErrors() {
        return list.toString();
    }

    @Override
    public String toString() {
        return getErrors();
    }
}
