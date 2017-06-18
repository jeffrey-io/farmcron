package farm.bsg.route.spark;

import farm.bsg.route.RequestResponseWrapper;
import spark.Request;
import spark.Response;

public class SparkBox implements RequestResponseWrapper {
    private final Request  request;
    private final Response response;
    private final boolean  secure;

    public SparkBox(Request request, Response response, boolean secure) {
        this.request = request;
        this.response = response;
        this.secure = secure;
    }
    
    @Override
    public String getURI() {
        return request.uri();
    }

    @Override
    public String getParam(String key) {
        return request.queryParams(key);
    }

    @Override
    public boolean hasNonNullQueryParam(String key) {
        return request.queryParams().contains(key);
    }

    @Override
    public void setCookie(String key, String value) {
        response.cookie(key, value);
        ;
    }

    @Override
    public void redirect(String uri) {
        StringBuilder sb = new StringBuilder();
        if (secure) {
            sb.append("https://");
        } else {
            sb.append("http://");
        }
        sb.append(request.headers("Host"));
        sb.append(uri);
        response.redirect(sb.toString());

        response.redirect(uri);
    }

    @Override
    public String getCookie(String key) {
        return request.cookie(key);
    }

    @Override
    public String[] getParamList(String key) {
        return request.queryParamsValues(key);
    }
}
