package farm.bsg.data.contracts;

public interface ReadOnlyType {
    public String defaultValue();

    public String name();

    public String normalize(String value);

    public boolean validate(String value);
}
