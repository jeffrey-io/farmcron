package farm.bsg.data;

import farm.bsg.data.contracts.KeyValuePairLogger;

public abstract class DirtyBitIndexer implements KeyValuePairLogger, AsyncTaskTarget {

    public static enum State {
        DIRTY, TASK_IN_PROGRESS, TASK_IN_PROGRESS_NEEDS_DIRTY, CLEAN
    }
    
    private State state;
    
    public DirtyBitIndexer() {
        this.state = State.CLEAN;
    }
    
    @Override
    public void validate(String key, Value oldValue, Value newValue, PutResult result) {
    }

    @Override
    public void put(String key, Value oldValue, Value newValue) {
        if (markDirtyUnderLock()) {
            onDirty(this);
        }
    }
    
    private synchronized boolean markDirtyUnderLock() {
        switch (state) {
            case CLEAN:
                state = State.DIRTY;
                return true;
            case TASK_IN_PROGRESS:
                state = State.TASK_IN_PROGRESS_NEEDS_DIRTY;
            default:
                return false;
             
        }
    }
    
    public abstract void onDirty(AsyncTaskTarget target);
    
    @Override
    public synchronized void begin() {
        state = State.TASK_IN_PROGRESS;
    }

    public synchronized boolean completeUnderLock(boolean success) {
        switch (state) {
            case TASK_IN_PROGRESS_NEEDS_DIRTY:
                state = State.TASK_IN_PROGRESS;
                return true;
            default:
                if (success) {
                  state = State.CLEAN;
                  return false;
                } else {
                  state = State.DIRTY;
                  return true;
                }
        }
    }
    
    @Override
    public void complete(boolean success) {
        if (completeUnderLock(success)) {
            onDirty(this);
        }
    }
}