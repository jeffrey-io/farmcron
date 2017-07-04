package farm.bsg.html;

import java.util.ArrayList;

public class Table extends HtmlPump {

    private final String[]            headings;
    private final ArrayList<Object[]> rows;
    private final String[]            colTypes;
    private String                    tableClass;
    private final boolean             hasHeadings;

    public Table(String... headings) {
        this.headings = headings;
        this.rows = new ArrayList<>();
        this.colTypes = new String[headings.length];
        boolean hasHeadings_ = false;
        for (int k = 0; k < headings.length; k++) {
            this.colTypes[k] = "td";
            if (this.headings[k] != null) {
                hasHeadings_ = true;
            } else {
                this.headings[k] = "";
            }
        }
        this.hasHeadings = hasHeadings_;
        this.tableClass = "table table-striped";
    }

    public static Table start(String... headings) {
        return new Table(headings);
    }

    public Table tableClass(String tableClass) {
        this.tableClass = tableClass;
        return this;
    }

    public Table columnType(int index, String type) {
        this.colTypes[index] = type;
        return this;
    }

    public Table row(Object... row) {
        if (row.length != headings.length) {
            throw new RuntimeException("does not have right length");
        }
        this.rows.add(row);
        return this;
    }

    public Table footer(Object... footer) {
        if (footer.length != headings.length) {
            throw new RuntimeException("does not have right length");
        }
        // TODO annotate as footer
        this.rows.add(footer);
        return this;
    }

    @Override
    public void pump(StringBuilder html) {
        html.append("<table class=\"" + tableClass + "\">");
        if (hasHeadings) {
            html.append("<thead><tr>");
            for (String heading : headings) {
                html.append("<th>" + heading + "</th>");
            }
            html.append("</tr></thead>");
        }

        html.append("<tbody>");
        for (Object[] row : rows) {
            html.append("<tr>");
            int at = 0;
            for (Object value : row) {
                html.append("<").append(colTypes[at]).append(">");
                if (value != null) {
                    if (value instanceof HtmlPump) {
                        ((HtmlPump) value).pump(html);
                    } else {
                        html.append(value.toString());
                    }
                }
                html.append("</").append(colTypes[at]).append(">");
                at++;
            }
            html.append("</tr>");
        }
        html.append("</tbody></table>");
    }

}
