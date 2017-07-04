package farm.bsg.data;

import java.util.concurrent.ExecutorService;
import java.util.function.BooleanSupplier;

public interface AsyncTaskTarget {

    public static void execute(final ExecutorService executor, final AsyncTaskTarget task, final BooleanSupplier body) {
        executor.execute(() -> {
            task.begin();
            boolean success = false;
            try {
                success = body.getAsBoolean();
            } finally {
                task.complete(success);
            }
        });
    }

    void begin();

    void complete(boolean success);
}
