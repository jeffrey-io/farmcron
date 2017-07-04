package farm.bsg.wake.sources;

import java.util.Set;
import java.util.function.BiConsumer;

public class InjectBuildId extends Source {

    private final Source source;
    private final String buildId;

    public InjectBuildId(final Source source, final String buildId) {
        this.source = source;
        this.buildId = buildId;
    }

    @Override
    public String get(final String key) {
        if ("build_id".equals(key)) {
            return this.buildId;
        }
        return this.source.get(key);
    }

    @Override
    public void populateDomain(final Set<String> domain) {
        this.source.populateDomain(domain);
        domain.add("build_id");
    }

    @Override
    public void walkComplex(final BiConsumer<String, Object> injectComplex) {
        this.source.walkComplex(injectComplex);
    }
}
