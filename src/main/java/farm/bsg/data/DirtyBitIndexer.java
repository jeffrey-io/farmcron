package farm.bsg.data;

import farm.bsg.data.contracts.KeyValuePairLogger;

/**
 * a dirty bit indexer allows for asynchonous invalidating of caches or things that can be computed. It allows you to say "Ok, everything under this prefix changed, so we need to compute something.";
 *
 * It is asynchronous in the sense that if multiple things changes, then the implementor can shift the compute temporarily and wait until things are settled down and minimize compute.
 *
 * @author jeffrey
 */
public abstract class DirtyBitIndexer implements KeyValuePairLogger, AsyncTaskTarget {

    public static enum State {
        DIRTY, TASK_IN_PROGRESS, TASK_IN_PROGRESS_NEEDS_DIRTY, CLEAN
    }

    private State state;

    public DirtyBitIndexer() {
        this.state = State.CLEAN;
    }

    @Override
    public synchronized void begin() {
        this.state = State.TASK_IN_PROGRESS;
    }

    @Override
    public void complete(final boolean success) {
        if (completeUnderLock(success)) {
            onDirty(this);
        }
    }

    private synchronized boolean completeUnderLock(final boolean success) {
        switch (this.state) {
            case TASK_IN_PROGRESS_NEEDS_DIRTY:
                this.state = State.TASK_IN_PROGRESS;
                return true;
            default:
                if (success) {
                    this.state = State.CLEAN;
                    return false;
                } else {
                    this.state = State.DIRTY;
                    return true;
                }
        }
    }

    private synchronized boolean markDirtyUnderLock() {
        switch (this.state) {
            case CLEAN:
                this.state = State.DIRTY;
                return true;
            case TASK_IN_PROGRESS:
                this.state = State.TASK_IN_PROGRESS_NEEDS_DIRTY;
            default:
                return false;

        }
    }

    public abstract void onDirty(AsyncTaskTarget target);

    @Override
    public void put(final String key, final Value oldValue, final Value newValue) {
        if (markDirtyUnderLock()) {
            onDirty(this);
        }
    }

    @Override
    public void validate(final String key, final Value oldValue, final Value newValue, final PutResult result) {
    }
}