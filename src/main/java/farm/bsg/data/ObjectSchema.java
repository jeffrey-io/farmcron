package farm.bsg.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import farm.bsg.data.contracts.ReadOnlyType;

public class ObjectSchema {
    public static class Properties {
        public final String  prefix;
        public final boolean singleton;
        public final boolean ephemeral;

        public Properties(final String prefix, final boolean singleton, final boolean ephemeral) {
            this.prefix = prefix;
            this.singleton = singleton;
            this.ephemeral = ephemeral;
        }
    }

    public static ObjectSchema ephemeral(final String prefix, final Type... types) {
        return new ObjectSchema(new Properties(prefix, false, true), types);
    }

    /**
     * Generate a schema that will be persisted
     *
     * @param prefix
     * @param types
     * @return
     */
    public static ObjectSchema persisted(final String prefix, final Type... types) {
        return new ObjectSchema(new Properties(prefix, false, false), types);
    }

    public static ObjectSchema singleton(final String key, final Type... types) {
        return new ObjectSchema(new Properties(key, true, false), types);
    }

    private final String                prefix;
    private final List<Type>            typesInOrder;

    private final HashMap<String, Type> schema;

    private final List<String>          dirtyBitIndicesJavaTypes;

    public final boolean                singleton;

    public final boolean                ephemeral;

    private ObjectSchema(final Properties properties, final Type... types) {
        this.prefix = properties.prefix;
        this.singleton = properties.singleton;
        this.ephemeral = properties.ephemeral;

        this.schema = new HashMap<String, Type>();
        final ArrayList<Type> orderedTypes = new ArrayList<>();
        final Type atomicRev = Field.STRING("__token");

        final Type id = Field.UUID("id");
        orderedTypes.add(id);
        this.schema.put("id", id);

        orderedTypes.add(atomicRev);
        this.schema.put("_rev", atomicRev);

        for (final Type t : types) {
            if (t.name().startsWith("_")) {
                throw new RuntimeException("Not allowed");
            }
            orderedTypes.add(t);
            this.schema.put(t.name(), t);
        }
        this.typesInOrder = Collections.unmodifiableList(orderedTypes);
        this.dirtyBitIndicesJavaTypes = new ArrayList<>();
    }

    public ObjectSchema dirty(final String javaClass) {
        this.dirtyBitIndicesJavaTypes.add(javaClass);
        return this;
    }

    public ReadOnlyType get(final String name) {
        return this.schema.get(name);
    }

    public List<String> getDirtyBitIndicesJavaTypes() {
        return Collections.unmodifiableList(this.dirtyBitIndicesJavaTypes);
    }

    public String getPrefix() {
        return this.prefix;
    }

    public synchronized List<Type> getTypes() {
        return this.typesInOrder;
    }

    public boolean isEphemeral() {
        return this.ephemeral;
    }

    public boolean isSingleton() {
        return this.singleton;
    }

}
