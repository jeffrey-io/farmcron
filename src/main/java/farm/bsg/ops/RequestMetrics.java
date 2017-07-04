package farm.bsg.ops;

public class RequestMetrics {

    public class InflightRequest {
        private final long timestamp;

        public InflightRequest() {
            this.timestamp = System.currentTimeMillis();
        }

        public void complete(final boolean success) {
            final long computedLatency = System.currentTimeMillis() - this.timestamp;
            RequestMetrics.this.latency.add(computedLatency);
        }
    }

    private final Counter   hits;

    private final Histogram latency;

    public RequestMetrics(final CounterSource source, final String method, final String uri) {
        source.setSection("traffic method:" + method + " uri:" + uri);
        this.hits = source.counter("hits_" + method + "_" + uri, "");
        this.latency = source.histogram("latency_" + method + "_" + uri, "");
    }

    public InflightRequest begin() {
        this.hits.bump();
        return new InflightRequest();
    }

}
