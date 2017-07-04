package farm.bsg.data;

import java.util.ArrayList;
import java.util.HashMap;

public class StringGroupBy<T> {
    public final HashMap<String, ArrayList<T>> index;

    public StringGroupBy() {
        this.index = new HashMap<>();
    }

    public void add(final String key, final T value) {
        ArrayList<T> list = this.index.get(key);
        if (list == null) {
            list = new ArrayList<>();
            this.index.put(key, list);
        }
        list.add(value);
    }
}
