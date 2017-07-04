package farm.bsg.html;

import java.util.ArrayList;

public class Table extends HtmlPump {

    public static Table start(final String... headings) {
        return new Table(headings);
    }

    private final String[]            headings;
    private final ArrayList<Object[]> rows;
    private final String[]            colTypes;
    private String                    tableClass;

    private final boolean             hasHeadings;

    public Table(final String... headings) {
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

    public Table columnType(final int index, final String type) {
        this.colTypes[index] = type;
        return this;
    }

    public Table footer(final Object... footer) {
        if (footer.length != this.headings.length) {
            throw new RuntimeException("does not have right length");
        }
        // TODO annotate as footer
        this.rows.add(footer);
        return this;
    }

    @Override
    public void pump(final StringBuilder html) {
        html.append("<table class=\"" + this.tableClass + "\">");
        if (this.hasHeadings) {
            html.append("<thead><tr>");
            for (final String heading : this.headings) {
                html.append("<th>" + heading + "</th>");
            }
            html.append("</tr></thead>");
        }

        html.append("<tbody>");
        for (final Object[] row : this.rows) {
            html.append("<tr>");
            int at = 0;
            for (final Object value : row) {
                html.append("<").append(this.colTypes[at]).append(">");
                if (value != null) {
                    if (value instanceof HtmlPump) {
                        ((HtmlPump) value).pump(html);
                    } else {
                        html.append(value.toString());
                    }
                }
                html.append("</").append(this.colTypes[at]).append(">");
                at++;
            }
            html.append("</tr>");
        }
        html.append("</tbody></table>");
    }

    public Table row(final Object... row) {
        if (row.length != this.headings.length) {
            throw new RuntimeException("does not have right length");
        }
        this.rows.add(row);
        return this;
    }

    public Table tableClass(final String tableClass) {
        this.tableClass = tableClass;
        return this;
    }

}
