package farm.bsg.html;

import java.util.Map.Entry;
import java.util.Set;

import farm.bsg.data.RawObject;
import farm.bsg.data.contracts.SingleCharacterBitmaskProvider;

public class BitMaskInput extends HtmlPump {

    private final String                         name;
    private final SingleCharacterBitmaskProvider provider;
    private String                               value = null;

    public BitMaskInput(final String name, final SingleCharacterBitmaskProvider provider) {
        this.name = name;
        this.provider = provider;
    }

    public BitMaskInput pull(final RawObject o) {
        return pull(o, this.name);
    }

    public BitMaskInput pull(final RawObject o, final String key) {
        final String value = o.get(key);
        this.value = value;
        return this;
    }

    @Override
    public void pump(final StringBuilder html) {
        final Table table = Html.table("Label", "Is Selected");

        final Set<String> valuesPresent = this.provider.valuesOf(this.value);

        for (final Entry<String, String> entry : this.provider.asMap().entrySet()) {
            final String id = this.name + "_" + entry.getValue();
            String checked = null;
            if (valuesPresent.contains(entry.getValue())) {
                checked = "true";
            }
            table.row( //
                    new Label(id, new Text(entry.getKey())), //
                    new Input(id).id_from_name().checkbox().value(checked));
        }
        table.pump(html);
    }

    public BitMaskInput value(final String value) {
        this.value = value;
        return this;
    }
}
