package farm.bsg.route;

/**
 * Defines the requirements for wrapping up the HTTP behavior. This allows us to do both unit test and allows us independence from Spark when we migrate to netty.
 * 
 * @author jeffrey
 */
public interface RequestResponseWrapper {

    /**
     * @return the full uri of the request
     */
    public String getURI();

    /**
     * return a binary file
     * 
     * @param key
     * @return a binary file
     */
    public BinaryFile getFile(String key);

    /**
     * @return a single query parameter
     */
    public String getParam(String key);

    /**
     * @return multiple query parameters for the same name
     */
    public String[] getParamList(String key);

    /**
     * @param value
     *            set a cookie on the response
     */
    public void setCookie(String key, String value);

    /**
     * @return the cookie associated to the given keey
     */
    public String getCookie(String key);

    /**
     * 
     * @return whether or not a query param is set (TODO: deprecate)
     */
    public boolean hasNonNullQueryParam(String key);

    /**
     * forward the request to a different URI
     * 
     * @param uri
     *            the uri to redirect the request to
     */
    public void redirect(FinishedHref href);
}
