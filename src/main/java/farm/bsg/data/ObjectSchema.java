package farm.bsg.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import farm.bsg.data.contracts.ReadOnlyType;

public class ObjectSchema {
    private final String                prefix;
    private final List<Type>            typesInOrder;
    private final HashMap<String, Type> schema;
    private final List<String> dirtyBitIndicesJavaTypes;

    public ObjectSchema(String prefix, Type... types) {
        this.prefix = prefix;
        this.schema = new HashMap<String, Type>();
        ArrayList<Type> orderedTypes = new ArrayList<>();
        Type atomicRev = Field.STRING("__token");

        Type id = Field.UUID("id");
        orderedTypes.add(id);
        schema.put("id", id);

        orderedTypes.add(atomicRev);
        schema.put("_rev", atomicRev);

        for (Type t : types) {
            if (t.name().startsWith("_")) {
                throw new RuntimeException("Not allowed");
            }
            orderedTypes.add(t);
            schema.put(t.name(), t);
        }
        this.typesInOrder = Collections.unmodifiableList(orderedTypes);
        this.dirtyBitIndicesJavaTypes = new ArrayList<>();
    }
    
    public ObjectSchema dirty(String javaClass) {
        this.dirtyBitIndicesJavaTypes.add(javaClass);
        return this;
    }

    public String getPrefix() {
        return prefix;
    }
    
    public List<String> getDirtyBitIndicesJavaTypes() {
        return Collections.unmodifiableList(dirtyBitIndicesJavaTypes);
    }

    public synchronized List<Type> getTypes() {
        return typesInOrder;
    }

    public ReadOnlyType get(String name) {
        return schema.get(name);
    }

}
