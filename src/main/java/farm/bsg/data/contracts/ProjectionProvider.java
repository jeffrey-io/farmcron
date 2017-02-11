package farm.bsg.data.contracts;

public interface ProjectionProvider {
    public String[] multiple(String key);

    public String first(String key);
}
