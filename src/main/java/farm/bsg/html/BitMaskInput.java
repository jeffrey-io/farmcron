package farm.bsg.html;

import java.util.Map.Entry;
import java.util.Set;

import farm.bsg.data.RawObject;
import farm.bsg.data.contracts.SingleCharacterBitmaskProvider;

public class BitMaskInput extends HtmlPump {

    private final String                         name;
    private final SingleCharacterBitmaskProvider provider;
    private String                               value = null;

    public BitMaskInput(String name, SingleCharacterBitmaskProvider provider) {
        this.name = name;
        this.provider = provider;
    }

    public BitMaskInput value(String value) {
        this.value = value;
        return this;
    }

    public BitMaskInput pull(RawObject o) {
        return pull(o, name);
    }
    
    public BitMaskInput pull(RawObject o, String key) {
        String value = o.get(key);
        this.value = value;
        return this;
    }
    
    @Override
    public void pump(StringBuilder html) {
        Table table = Html.table("Label", "Is Selected");

        Set<String> valuesPresent = provider.valuesOf(value);

        for (Entry<String, String> entry : provider.asMap().entrySet()) {
            String id = name + "_" + entry.getValue();
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
}
