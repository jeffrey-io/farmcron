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
    private QueryEngine engine;

    public PublicSiteBuilder(QueryEngine engine) {
        this.engine = engine;
    }

    public void run() {
        try {
            String buildId = UUID.randomUUID().toString().replaceAll("-", "") + "_" + System.currentTimeMillis();
            Stage stage = new QueryEngineStage(engine, buildId).compile();
            for (final Source source : stage.sources()) {
                final String url = source.get("url");
                final String body = source.get("body");
                String contentType = "text/html"; // TODO: fix this
                engine.publicBlobCache.write("/" + url, new UriBlobCache.UriBlob(contentType, body.getBytes(Charsets.UTF_8)));
                if ("index.html".equals(url)) {
                    engine.publicBlobCache.write("/", new UriBlobCache.UriBlob(contentType, body.getBytes(Charsets.UTF_8)));
                }
            }
            StringBuilder js = new StringBuilder();
            StringBuilder css = new StringBuilder();
            
            for (WakeInputFile file : engine.select_wakeinputfile().done()) {
                String contentType = file.get("content_type");
                String content = new String(Base64.decodeBase64(file.get("contents").getBytes()));
                if ("text/javascript".equals(contentType)) {
                    js.append(content);
                }
                if ("text/css".equals(contentType)) {
                    css.append(content);
                }
            }
            
            // bring in YUI compressor here
            engine.publicBlobCache.write("/" + buildId + ".js", new UriBlobCache.UriBlob("text/javascript", js.toString().getBytes(Charsets.UTF_8)));
            engine.publicBlobCache.write("/" + buildId + ".css", new UriBlobCache.UriBlob("text/css", css.toString().getBytes(Charsets.UTF_8)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirty(AsyncTaskTarget target) {
        engine.scheduler.schedule(() -> {
            AsyncTaskTarget.execute(engine.executor, target, () -> {
                BsgCounters.I.compile_wake.bump();
                run();
                return true;
            });
        } , 1000, TimeUnit.MILLISECONDS);
    }
}
