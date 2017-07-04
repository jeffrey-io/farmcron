package farm.bsg.data;

import java.util.concurrent.ExecutorService;
import java.util.function.BooleanSupplier;

public interface AsyncTaskTarget {

    void begin();

    void complete(boolean success);

    public static void execute(ExecutorService executor, AsyncTaskTarget task, BooleanSupplier body) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                task.begin();
                boolean success = false;
                try {
                    success = body.getAsBoolean();
                } finally {
                    task.complete(success);
                }
            }
        });
    }
}
