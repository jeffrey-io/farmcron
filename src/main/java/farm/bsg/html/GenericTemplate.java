package farm.bsg.html;

import java.util.ArrayList;

public class GenericTemplate {
    private class Fragment {
        private final FragmentType type;
        private final String       data;

        private Fragment(final FragmentType type, final String data) {
            this.type = type;
            this.data = data;
        }

        public String eval(final String title, final String navbar, final String body) {
            switch (this.type) {
                case BODY:
                    return body;
                case NAVBAR:
                    return navbar;
                case TITLE:
                    return title;
                default:
                    return this.data;
            }
        }
    }

    private enum FragmentType {
        STRING(false), NAVBAR(true), BODY(true), TITLE(true);

        public final boolean isVariable;
        public final String  splitOn;

        private FragmentType(final boolean isVariable) {
            this.isVariable = isVariable;
            this.splitOn = "$" + this.toString().toUpperCase() + "$";
        }
    }

    private final ArrayList<Fragment> fragments;

    public GenericTemplate(final String template) {
        this.fragments = new ArrayList<>();
        splitOn(template, this.fragments);

    }

    public String html(final String title, final String navbar, final String body) {
        final StringBuilder result = new StringBuilder();
        for (final Fragment fragment : this.fragments) {
            result.append(fragment.eval(title, navbar, body));
        }
        return result.toString();
    }

    private void splitOn(final String data, final ArrayList<Fragment> output) {
        if (data.length() == 0) {
            return;
        }
        for (final FragmentType type : FragmentType.values()) {
            if (type.isVariable) {
                final int index = data.indexOf(type.splitOn);
                if (index >= 0) {
                    splitOn(data.substring(0, index), output);
                    output.add(new Fragment(type, null));
                    splitOn(data.substring(index + type.splitOn.length()), output);
                    return;
                }
            }
        }
        output.add(new Fragment(FragmentType.STRING, data));
    }
}
