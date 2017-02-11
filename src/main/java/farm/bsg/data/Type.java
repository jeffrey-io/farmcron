package farm.bsg.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import farm.bsg.data.contracts.ReadOnlyType;

public abstract class Type implements ReadOnlyType {
    private final String name;
    private Set<String>  annotations;
    private boolean      isIndex  = false;
    private boolean      isUnique = false;
    private boolean      isScoped = false;

    private Set<String>  projections;

    public Type(String name) {
        this.name = name;
        this.annotations = null;
        this.projections = null;
    }

    public Type addProjection(String projection) {
        if (this.projections == null) {
            this.projections = new HashSet<>();
            this.projections.add("admin");
        }
        this.projections.add(projection);
        return this;
    }

    public Set<String> getProjections() {
        if (projections == null) {
            return Collections.singleton("admin");
        } else {
            return projections;
        }
    }

    public Type markAsScope() {
        isScoped = true;
        return this;
    }

    public boolean isScoped() {
        return this.isScoped;
    }

    public Type makeIndex(boolean isUnique) {
        this.isIndex = true;
        this.isUnique = isUnique;
        return this;
    }

    public boolean isIndexed() {
        return this.isIndex;
    }

    public boolean isIndexedUniquely() {
        return this.isUnique;
    }

    public boolean has(String flag) {
        if (this.annotations == null) {
            return false;
        }
        return this.annotations.add(flag);
    }

    public Type annotate(String flag) {
        if (this.annotations == null) {
            this.annotations = new HashSet<>();
        }
        this.annotations.add(flag);
        return this;
    }

    @Override
    public String name() {
        return this.name;
    }

    public abstract String type();
}