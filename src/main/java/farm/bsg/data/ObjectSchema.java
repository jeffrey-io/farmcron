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
    public final boolean singleton;
    
    public static class Properties {
        public final String prefix;
        public final boolean singleton;
        public final boolean ephemeral;
        
        public Properties(String prefix, boolean singleton, boolean ephemeral) {
            this.prefix = prefix;
            this.singleton = singleton;
            this.ephemeral = ephemeral;
        }
    }

    /**
     * Generate a schema that will be persisted
     * @param prefix
     * @param types
     * @return
     */
    public static ObjectSchema persisted(String prefix, Type... types) {
        return new ObjectSchema(new Properties(prefix, false, false), types);
    }

    public static ObjectSchema singleton(String key, Type... types) {
        return new ObjectSchema(new Properties(key, true, false), types);
    }

    public static ObjectSchema ephemeral(String prefix, Type... types) {
        return new ObjectSchema(new Properties(prefix, false, true), types);
    }

    private ObjectSchema(Properties properties, Type... types) {
        this.prefix = properties.prefix;
        this.singleton = properties.singleton;

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
