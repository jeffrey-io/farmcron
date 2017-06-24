package farm.bsg.ops;

public class RequestMetrics {

    private final Counter hits;

    public class InflightRequest {
        private final long timestamp;

        public InflightRequest() {
            this.timestamp = System.currentTimeMillis();
        }

        public void complete(boolean success) {

        }
    }

    public RequestMetrics(CounterSource source, String method, String uri) {
        source.setSection("traffic method:" + method + " uri:" + uri);
        this.hits = source.counter("hits_" + method + "_" + uri, "");
    }

    public InflightRequest begin() {
        hits.bump();
        return new InflightRequest();
    }

}
