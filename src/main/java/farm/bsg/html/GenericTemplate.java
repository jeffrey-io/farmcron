package farm.bsg.html;

import java.util.ArrayList;

public class GenericTemplate {
    private final ArrayList<Fragment> fragments;

    private enum FragmentType {
        STRING(false), NAVBAR(true), BODY(true), TITLE(true);

        public final boolean isVariable;
        public final String  splitOn;

        private FragmentType(boolean isVariable) {
            this.isVariable = isVariable;
            this.splitOn = "$" + this.toString().toUpperCase() + "$";
        }
    }

    private class Fragment {
        private FragmentType type;
        private final String data;

        private Fragment(FragmentType type, String data) {
            this.type = type;
            this.data = data;
        }

        public String eval(String title, String navbar, String body) {
            switch (type) {
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

    public GenericTemplate(String template) {
        this.fragments = new ArrayList<>();
        splitOn(template, this.fragments);

    }

    private void splitOn(String data, ArrayList<Fragment> output) {
        if (data.length() == 0) {
            return;
        }
        for (FragmentType type : FragmentType.values()) {
            if (type.isVariable) {
                int index = data.indexOf(type.splitOn);
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

    public String html(String title, String navbar, String body) {
        StringBuilder result = new StringBuilder();
        for (Fragment fragment : fragments) {
            result.append(fragment.eval(title, navbar, body));
        }
        return result.toString();
    }
}
