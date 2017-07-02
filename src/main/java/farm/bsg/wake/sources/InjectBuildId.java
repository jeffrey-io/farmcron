package farm.bsg.wake.sources;

import java.util.Set;
import java.util.function.BiConsumer;

public class InjectBuildId extends Source {

    private Source source;
    private String buildId;

    public InjectBuildId(Source source, String buildId) {
        this.source = source;
        this.buildId = buildId;
    }

    @Override
    public String get(String key) {
        if ("build_id".equals(key)) {
            return buildId;
        }
        return source.get(key);
    }

    @Override
    public void populateDomain(Set<String> domain) {
        source.populateDomain(domain);
        domain.add("build_id");
    }

    @Override
    public void walkComplex(BiConsumer<String, Object> injectComplex) {
        source.walkComplex(injectComplex);
    }
}
