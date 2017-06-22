package farm.bsg.models;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
            Stage stage = new QueryEngineStage(engine).compile();
            for (final Source source : stage.sources()) {
                final String url = source.get("url");
                final String body = source.get("body");
                engine.publicBlobCache.write("/" + url, new UriBlobCache.UriBlob("text/html" /* todo */, body.getBytes()));
            }
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
        }, 500, TimeUnit.MILLISECONDS);
    }
}
