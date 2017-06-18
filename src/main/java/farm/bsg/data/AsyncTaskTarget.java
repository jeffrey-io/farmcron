package farm.bsg.data;

import java.util.concurrent.ExecutorService;

public interface AsyncTaskTarget {

    void begin();
    
    void complete(boolean success);
    
    public static void execute(ExecutorService executor, AsyncTaskTarget task, Runnable body) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                task.begin();
                boolean success = false;
                try {
                    body.run();
                    success = true;
                } finally {
                    task.complete(success);
                }
            }
        });
    }
}
