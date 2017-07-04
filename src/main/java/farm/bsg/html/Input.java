package farm.bsg.html;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Function;

import farm.bsg.data.RawObject;
import farm.bsg.route.RequestResponseWrapper;

public class Input extends HtmlPump {

    public static HtmlPump reset(final String name) {
        return new Input("__reset_" + name).value("");
    }

    private final String             name;
    private String                   id               = null;
    private String                   type             = "hidden";
    private String                   value            = null;
    private String                   placeholder      = null;
    private String                   clazz            = null;
    private boolean                  required         = false;
    private boolean                  autofocus        = false;
    private int                      rows             = 3;
    private int                      cols             = 30;
    private Function<String, String> wrapper          = null;
    private String[]                 options          = null;

    private String[]                 labels           = null;
    private boolean                  labelThenControl = true;
    private boolean                  multiple         = false;
    private HashSet<String>          optionsSelected  = null;

    private int                      size             = 1;

    public Input(final String name) {
        this.name = name;
        this.id = name;
    }

    public Input autofocus() {
        this.autofocus = true;
        return this;
    }

    public Input autofocus(final boolean autofocus) {
        this.autofocus = autofocus;
        return this;
    }

    private String builtInControls(String givenValue) {
        final StringBuilder sb = new StringBuilder();

        boolean isTextArea = false;
        boolean isSelect = false;
        if (this.type.equals("textarea")) {
            sb.append("<textarea");
            isTextArea = true;
        } else if (this.type.equals("select")) {
            sb.append("<select");
            isSelect = true;
        } else {
            sb.append("<input type=\"").append(this.type).append("\"");
        }

        if (this.id != null) {
            sb.append(" id=\"").append(this.id).append("\"");
        }
        if (this.name != null) {
            sb.append(" name=\"").append(this.name).append("\"");
        }
        if (this.clazz != null) {
            sb.append(" class=\"").append(this.clazz).append("\"");
        }
        if (isTextArea) {
            sb.append(" cols=\"" + this.cols + "\"");
            sb.append(" rows=\"" + this.rows + "\"");
        }

        if ("radio".equals(this.type)) {
            if (givenValue != null && this.value != null) {
                if (givenValue.equals(this.value)) {
                    sb.append(" checked");
                }
            }
        }

        if ("checkbox".equals(this.type)) {
            if (givenValue != null && givenValue.trim().length() > 0) {
                sb.append(" checked");
            }
            givenValue = "true";
        }

        if (givenValue != null && !isTextArea && !isSelect) {
            sb.append(" value=\"").append(givenValue).append("\"");
        }
        if (this.placeholder != null && !isSelect) {
            sb.append(" placeholder=\"").append(this.placeholder).append("\"");
        }
        if (this.required) {
            sb.append(" required");
        }
        if (this.autofocus) {
            sb.append(" autofocus");
        }
        if (this.multiple && isSelect) {
            sb.append(" multiple");
        }
        if (isSelect && this.size > 1) {
            sb.append(" size=\"" + this.size + "\"");
        }
        if (isTextArea) {
            sb.append(" >");
            if (givenValue != null) {
                sb.append(givenValue);
            }
            sb.append("</textarea>");
        } else if (isSelect) {
            sb.append(" >");
            if (this.options != null) {
                final int n = this.options.length;
                for (int k = 0; k < n; k++) {
                    String label = null;
                    if (this.labels != null && k < this.labels.length) {
                        label = this.labels[k];
                    } else {
                        label = this.options[k];
                    }

                    sb.append("<option value=\"" + this.options[k] + "\"");
                    boolean selected = this.options[k].equals(givenValue);
                    if (this.optionsSelected != null) {
                        if (this.optionsSelected.contains(this.options[k])) {
                            selected = true;
                        }
                    }
                    if (selected) { // what about multiple
                        sb.append(" selected");
                    }
                    sb.append(">").append(label).append("</option>");
                }
            }
            sb.append("</select>");
        } else {
            sb.append(" />");
        }
        return sb.toString();
    }

    public Input checkbox() {
        this.type = "checkbox";
        return this;
    }

    public Input clazz(final String clazz) {
        this.clazz = clazz;
        return this;
    }

    public Input file() {
        this.type = "file";
        return this;
    }

    public Input id(final String id) {
        this.id = id;
        return this;
    }

    public Input id_from_name() {
        this.id = this.name;
        return this;
    }

    public Input label_then_control(final boolean labelThenControl) {
        this.labelThenControl = labelThenControl;
        return this;
    }

    public Input labels(final String... labels) {
        this.labels = labels;
        return this;
    }

    public Input multiple() {
        this.multiple = true;
        return this;
    }

    public Input multiple(final boolean multiple) {
        this.multiple = multiple;
        return this;
    }

    public Input options(final Map<String, String> valueToLabelsMap) {
        this.options = new String[valueToLabelsMap.size()];
        this.labels = new String[valueToLabelsMap.size()];
        int at = 0;
        for (final Entry<String, String> entry : valueToLabelsMap.entrySet()) {
            this.options[at] = entry.getKey();
            this.labels[at] = entry.getValue();
            at++;
        }
        return this;
    }

    public Input options(final String... options) {
        if (options != null && options.length > 0) {
            this.options = options;
            if (this.labels == null) {
                this.labels = options;
            }
        }
        return this;
    }

    public Input password() {
        this.type = "password";
        return this;
    }

    public Input placeholder(final String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public Input pull(final RawObject o) {
        return pull(o, this.name);
    }

    public Input pull(final RawObject o, final String key) {
        final String value = o.get(key);
        this.value = value;
        if ("select".equals(this.type) && this.multiple) {
            // OK, not sure about this
        }
        return this;
    }

    public Input pull(final RequestResponseWrapper req) {
        if (this.type.equals("password")) {
            // never pull passwords out of request
            return this;
        }

        final String value = req.getParam(this.name);
        if (value != null) {
            this.value = value;
        }

        if ("select".equals(this.type)) {
            this.optionsSelected = new HashSet<>();
            final String[] optionsAvailable = req.getParamList(this.name);
            if (optionsAvailable != null) {
                for (final String option : optionsAvailable) {
                    this.optionsSelected.add(option);
                }
            }
        }

        return this;
    }

    @Override
    public void pump(final StringBuilder sb) {
        if (this.type.equals("radio")) {
            if (this.options != null) {
                final int n = this.options.length;
                final String newIdPrefix = this.id + "_id_" + new Random().nextInt();

                for (int k = 0; k < n; k++) {
                    final String restoreId = this.id;
                    String label = null;
                    if (this.labels != null && k < this.labels.length) {
                        label = this.labels[k];
                    } else {
                        label = this.options[k];
                    }
                    this.id = newIdPrefix + "_" + k;

                    String prefix = "";
                    final String suffix = builtInControls(this.options[k]);
                    ;
                    if (label != null) {
                        prefix = Html.label(this.id, label).toHtml();
                    }
                    if (this.labelThenControl) {
                        sb.append(wrap(prefix + suffix));
                    } else {
                        sb.append(wrap(suffix + prefix));

                    }
                    this.id = restoreId;
                }
                return;
            }
        }
        sb.append(wrap(builtInControls(this.value)));
    }

    public Input radio(final Map<String, String> valueToLabelsMap) {
        this.type = "radio";
        return this.options(valueToLabelsMap);
    }

    public Input radio(final String... options) {
        this.type = "radio";
        return this.options(options);
    }

    public Input required() {
        this.required = true;
        return this;
    }

    public Input select(final Map<String, String> valueToLabelsMap) {
        this.type = "select";
        return this.options(valueToLabelsMap);
    }

    public Input select(final String... options) {
        this.type = "select";
        return this.options(options);
    }

    public Input select_hour() {
        final TreeMap<String, String> times = new TreeMap<>();
        times.put("00", "12:00 AM (midnight)");
        times.put("12", "12:00 PM (noon)");
        for (int k = 1; k <= 11; k++) {
            String padded = "0" + k;
            if (padded.length() == 3) {
                padded = padded.substring(1);
            }
            times.put(padded, k + ":00 AM");
            times.put("" + (12 + k), k + ":00 PM");
        }
        return select(times);
    }

    public Input size(final int size) {
        this.size = size;
        return this;
    }

    public Input submit() {
        this.type = "submit";
        return this;
    }

    public Input text() {
        this.type = "text";
        return this;
    }

    public Input textarea(final int rows, final int cols) {
        this.type = "textarea";
        this.rows = rows;
        this.cols = cols;
        return this;
    }

    @Override
    public String toString() {
        return toHtml();
    }

    public Input value(final String value) {
        this.value = value;
        return this;
    }

    public Input wrap(final Function<String, String> wrapper) {
        this.wrapper = wrapper;
        return this;
    }

    private String wrap(final String x) {
        if (this.wrapper == null) {
            return x;
        }
        return this.wrapper.apply(x);
    }
}
