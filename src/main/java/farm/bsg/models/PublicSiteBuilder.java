package farm.bsg.models;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;

import com.google.common.base.Charsets;

import farm.bsg.BsgCounters;
import farm.bsg.QueryEngine;
import farm.bsg.data.AsyncTaskTarget;
import farm.bsg.data.DirtyBitIndexer;
import farm.bsg.data.UriBlobCache;
import farm.bsg.wake.sources.Source;
import farm.bsg.wake.stages.QueryEngineStage;
import farm.bsg.wake.stages.Stage;

public class PublicSiteBuilder extends DirtyBitIndexer {
    private final QueryEngine engine;

    public PublicSiteBuilder(final QueryEngine engine) {
        this.engine = engine;
    }

    @Override
    public void onDirty(final AsyncTaskTarget target) {
        this.engine.scheduler.schedule(() -> {
            AsyncTaskTarget.execute(this.engine.executor, target, () -> {
                BsgCounters.I.compile_wake.bump();
                run();
                return true;
            });
        } , 1000, TimeUnit.MILLISECONDS);
    }

    public void run() {
        try {
            final String buildId = UUID.randomUUID().toString().replaceAll("-", "") + "_" + System.currentTimeMillis();
            final Stage stage = new QueryEngineStage(this.engine, buildId).compile();
            for (final Source source : stage.sources()) {
                final String url = source.get("url");
                final String body = source.get("body");
                final String contentType = "text/html"; // TODO: fix this
                this.engine.publicBlobCache.write("/" + url, new UriBlobCache.UriBlob(contentType, body.getBytes(Charsets.UTF_8)));
                if ("index.html".equals(url)) {
                    this.engine.publicBlobCache.write("/", new UriBlobCache.UriBlob(contentType, body.getBytes(Charsets.UTF_8)));
                }
            }
            final StringBuilder js = new StringBuilder();
            final StringBuilder css = new StringBuilder();

            for (final WakeInputFile file : this.engine.select_wakeinputfile().done()) {
                final String contentType = file.get("content_type");
                final String contentsRaw = file.get("contents");
                if (contentsRaw != null) {
                    final String content = new String(Base64.decodeBase64(contentsRaw.getBytes()));
                    if ("text/javascript".equals(contentType)) {
                        js.append(content);
                    }
                    if ("text/css".equals(contentType)) {
                        css.append(content);
                    }
                }
            }

            // bring in YUI compressor here
            this.engine.publicBlobCache.write("/" + buildId + ".js", new UriBlobCache.UriBlob("text/javascript", js.toString().getBytes(Charsets.UTF_8)));
            this.engine.publicBlobCache.write("/" + buildId + ".css", new UriBlobCache.UriBlob("text/css", css.toString().getBytes(Charsets.UTF_8)));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
