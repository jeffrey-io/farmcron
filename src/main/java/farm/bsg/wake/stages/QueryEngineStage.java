package farm.bsg.wake.stages;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.codec.binary.Base64;

import farm.bsg.QueryEngine;
import farm.bsg.models.WakeInputFile;
import farm.bsg.wake.sources.BangedSource;
import farm.bsg.wake.sources.EngineJoinSource;
import farm.bsg.wake.sources.InjectBuildId;
import farm.bsg.wake.sources.MarkdownFilteredSource;
import farm.bsg.wake.sources.Source;
import farm.bsg.wake.sources.TagsFilteredSource;

public class QueryEngineStage extends Stage {

    private final Collection<Source> sources;

    private static Source loadIfPossible(WakeInputFile input, QueryEngine engine) throws IOException {
        final String name = input.get("filename");
        if (!(name.endsWith(".html") || name.endsWith(".markdown"))) {
            return null;
        }
        String content = new String(Base64.decodeBase64(input.get("contents").getBytes()));
        try {
            Source source = new TagsFilteredSource(new EngineJoinSource(new BangedSource(name, new StringReader(content)), engine));
            if (name.endsWith(".markdown")) {
                source = new MarkdownFilteredSource(source, "body");
            }
            final boolean isSnippet = "snippet".equalsIgnoreCase(source.get("type"));
            if (isSnippet) {
                return source;
            }
            final boolean isTemplate = source.get("template-name") != null;
            if (isTemplate) {
                return source;
            }
            return source;
        } catch (final Exception err) {
            System.err.println("Skipping:" + name);
            err.printStackTrace();
            return null;
        }
    }

    public QueryEngineStage(QueryEngine engine, String buildId) throws IOException {
        final ArrayList<Source> _sources = new ArrayList<>();
        for (WakeInputFile input : engine.select_wakeinputfile().done()) {
            Source src = loadIfPossible(input, engine);
            if (src != null) {
                _sources.add(new InjectBuildId(src, buildId));
            }
        }
        this.sources = Collections.unmodifiableCollection(_sources);
    }

    @Override
    public Collection<Source> sources() {
        return sources;
    }
    
    public Stage compile() {
        // sort them for giggles
        final SortByOrderStage sorted = new SortByOrderStage(this);
        // inject a global index
        final CrossBuildIndexStage indexed = new CrossBuildIndexStage(sorted);
        // inject the topology (connect the pages together according to the one true tree)
        final InjectTopologyStage withTopology = new InjectTopologyStage(indexed);
        // inject snippets
        final SnippetInjectorStage withSnippets = new SnippetInjectorStage(withTopology);
        // put templates into place
        final TemplateCrossStage withTemplates = new TemplateCrossStage(withSnippets);
        // enable linkage between pages
        final LinkageStage linked = new LinkageStage(withTemplates);
        // clean up the HTML
        return new CompressHTMLStage(linked);        
    }
}
