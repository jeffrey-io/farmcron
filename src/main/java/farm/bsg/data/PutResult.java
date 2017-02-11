package farm.bsg.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PutResult {
    private HashMap<String, ArrayList<String>> errors;
    private boolean                            successful;
    private boolean                            storageFailure;
    private boolean                            tooMuchData;

    public PutResult() {
        this.successful = true;
        this.storageFailure = false;
        this.errors = null;
    }

    public void addFieldFailure(String field, String errorCode) {
        this.successful = false;
        if (errors == null) {
            errors = new HashMap<>();
        }
        ArrayList<String> current = errors.get(field);
        if (current == null) {
            current = new ArrayList<>();
            errors.put(field, current);
        }
        current.add(errorCode);
    }

    public void setFailedStorage() {
        this.successful = false;
        this.storageFailure = true;
    }

    public void setTooMuchData() {
        this.successful = false;
        this.tooMuchData = true;
    }

    public List<String> getErrors(String field) {
        if (errors == null) {
            return null;
        }
        return errors.get(field);
    }

    public boolean wasStorageFailure() {
        return storageFailure;
    }

    public boolean wasTooMuchData() {
        return tooMuchData;
    }

    public boolean success() {
        return successful;
    }
}
