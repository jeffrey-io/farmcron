package farm.bsg.ops;

public class RequestMetrics {

    private final Counter hits;
    private final Histogram latency;

    public class InflightRequest {
        private final long timestamp;

        public InflightRequest() {
            this.timestamp = System.currentTimeMillis();
        }

        public void complete(boolean success) {
            long computedLatency = System.currentTimeMillis() - timestamp;
            latency.add(computedLatency);
        }
    }

    public RequestMetrics(CounterSource source, String method, String uri) {
        source.setSection("traffic method:" + method + " uri:" + uri);
        this.hits = source.counter("hits_" + method + "_" + uri, "");
        this.latency = source.histogram("latency_" + method + "_" + uri, "");
    }

    public InflightRequest begin() {
        hits.bump();
        return new InflightRequest();
    }

}
