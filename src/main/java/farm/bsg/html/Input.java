package farm.bsg.html;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Function;

import farm.bsg.data.RawObject;
import farm.bsg.route.RequestResponseWrapper;

public class Input extends HtmlPump {

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

    public Input(String name) {
        this.name = name;
        this.id = name;
    }
    
    public static HtmlPump reset(String name) {
        return new Input("__reset_" + name).value("");
    }

    public Input wrap(Function<String, String> wrapper) {
        this.wrapper = wrapper;
        return this;
    }

    public Input options(String... options) {
        if (options != null && options.length > 0) {
            this.options = options;
            if (this.labels == null) {
                this.labels = options;
            }
        }
        return this;
    }

    public Input checkbox() {
        this.type = "checkbox";
        return this;
    }

    public Input radio(String... options) {
        this.type = "radio";
        return this.options(options);
    }

    public Input options(Map<String, String> valueToLabelsMap) {
        this.options = new String[valueToLabelsMap.size()];
        this.labels = new String[valueToLabelsMap.size()];
        int at = 0;
        for (Entry<String, String> entry : valueToLabelsMap.entrySet()) {
            options[at] = entry.getKey();
            labels[at] = entry.getValue();
            at++;
        }
        return this;
    }

    public Input pull(RequestResponseWrapper req) {
        if (type.equals("password")) {
            // never pull passwords out of request
            return this;
        }

        String value = req.getParam(name);
        if (value != null) {
            this.value = value;
        }

        if ("select".equals(type)) {
            optionsSelected = new HashSet<>();
            String[] optionsAvailable = req.getParamList(name);
            if (optionsAvailable != null) {
                for (String option : optionsAvailable) {
                    optionsSelected.add(option);
                }
            }
        }

        return this;
    }

    public Input pull(RawObject o) {
        return pull(o, name);
    }
    
    public Input pull(RawObject o, String key) {
        String value = o.get(key);
        this.value = value;
        if ("select".equals(type) && multiple) {
            // OK, not sure about this
        }
        return this;
    }

    public Input radio(Map<String, String> valueToLabelsMap) {
        this.type = "radio";
        return this.options(valueToLabelsMap);
    }
    
    public Input select_hour() {
        TreeMap<String, String> times = new TreeMap<>();
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

    public Input select(Map<String, String> valueToLabelsMap) {
        this.type = "select";
        return this.options(valueToLabelsMap);
    }

    public Input select(String... options) {
        this.type = "select";
        return this.options(options);
    }

    public Input label_then_control(boolean labelThenControl) {
        this.labelThenControl = labelThenControl;
        return this;
    }

    public Input labels(String... labels) {
        this.labels = labels;
        return this;
    }

    public Input submit() {
        this.type = "submit";
        return this;
    }

    public Input id(String id) {
        this.id = id;
        return this;
    }

    public Input id_from_name() {
        this.id = this.name;
        return this;
    }

    public Input clazz(String clazz) {
        this.clazz = clazz;
        return this;
    }

    public Input placeholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public Input text() {
        this.type = "text";
        return this;
    }

    public Input textarea(int rows, int cols) {
        this.type = "textarea";
        this.rows = rows;
        this.cols = cols;
        return this;
    }

    public Input password() {
        this.type = "password";
        return this;
    }

    public Input required() {
        this.required = true;
        return this;
    }

    public Input autofocus() {
        this.autofocus = true;
        return this;
    }

    public Input autofocus(boolean autofocus) {
        this.autofocus = autofocus;
        return this;
    }

    public Input multiple(boolean multiple) {
        this.multiple = multiple;
        return this;
    }

    public Input multiple() {
        this.multiple = true;
        return this;
    }

    public Input size(int size) {
        this.size = size;
        return this;
    }

    public Input value(String value) {
        this.value = value;
        return this;
    }

    @Override
    public void pump(StringBuilder sb) {
        if (type.equals("radio")) {
            if (options != null) {
                int n = options.length;
                String newIdPrefix = this.id + "_id_" + new Random().nextInt();

                for (int k = 0; k < n; k++) {
                    String restoreId = this.id;
                    String label = null;
                    if (labels != null && k < labels.length) {
                        label = labels[k];
                    } else {
                        label = options[k];
                    }
                    this.id = newIdPrefix + "_" + k;

                    String prefix = "";
                    String suffix = builtInControls(options[k]);
                    ;
                    if (label != null) {
                        prefix = Html.label(this.id, label).toHtml();
                    }
                    if (labelThenControl) {
                        sb.append(wrap(prefix + suffix));
                    } else {
                        sb.append(wrap(suffix + prefix));

                    }
                    this.id = restoreId;
                }
                return;
            }
        }
        sb.append(wrap(builtInControls(value)));
    }

    private String builtInControls(String givenValue) {
        StringBuilder sb = new StringBuilder();

        boolean isTextArea = false;
        boolean isSelect = false;
        if (type.equals("textarea")) {
            sb.append("<textarea");
            isTextArea = true;
        } else if (type.equals("select")) {
            sb.append("<select");
            isSelect = true;
        } else {
            sb.append("<input type=\"").append(type).append("\"");
        }

        if (id != null) {
            sb.append(" id=\"").append(id).append("\"");
        }
        if (name != null) {
            sb.append(" name=\"").append(name).append("\"");
        }
        if (clazz != null) {
            sb.append(" class=\"").append(clazz).append("\"");
        }
        if (isTextArea) {
            sb.append(" cols=\"" + cols + "\"");
            sb.append(" rows=\"" + rows + "\"");
        }

        if ("radio".equals(type)) {
            if (givenValue != null && value != null) {
                if (givenValue.equals(value)) {
                    sb.append(" checked");
                }
            }
        }

        if ("checkbox".equals(type)) {
            if (givenValue != null && givenValue.trim().length() > 0) {
                sb.append(" checked");
            }
            givenValue = "true";
        }

        if (givenValue != null && !isTextArea && !isSelect) {
            sb.append(" value=\"").append(givenValue).append("\"");
        }
        if (placeholder != null && !isSelect) {
            sb.append(" placeholder=\"").append(placeholder).append("\"");
        }
        if (required) {
            sb.append(" required");
        }
        if (autofocus) {
            sb.append(" autofocus");
        }
        if (multiple && isSelect) {
            sb.append(" multiple");
        }
        if (isSelect && size > 1) {
            sb.append(" size=\"" + size + "\"");
        }
        if (isTextArea) {
            sb.append(" >");
            if (givenValue != null) {
                sb.append(givenValue);
            }
            sb.append("</textarea>");
        } else if (isSelect) {
            sb.append(" >");
            if (options != null) {
                int n = options.length;
                for (int k = 0; k < n; k++) {
                    String label = null;
                    if (labels != null && k < labels.length) {
                        label = labels[k];
                    } else {
                        label = options[k];
                    }

                    sb.append("<option value=\"" + options[k] + "\"");
                    boolean selected = options[k].equals(givenValue);
                    if (optionsSelected != null) {
                        if (optionsSelected.contains(options[k])) {
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

    private String wrap(String x) {
        if (wrapper == null) {
            return x;
        }
        return wrapper.apply(x);
    }

    @Override
    public String toString() {
        return toHtml();
    }
}
