package farm.bsg.data;

import java.util.ArrayList;
import java.util.HashMap;

public class StringGroupBy<T> {
    public final HashMap<String, ArrayList<T>> index;

    public StringGroupBy() {
        this.index = new HashMap<>();
    }

    public void add(String key, T value) {
        ArrayList<T> list = index.get(key);
        if (list == null) {
            list = new ArrayList<>();
            index.put(key, list);
        }
        list.add(value);
    }
}
