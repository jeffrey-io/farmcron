package farm.bsg.data.contracts;

public interface ProjectionProvider {
    public String first(String key);

    public String[] multiple(String key);
}
