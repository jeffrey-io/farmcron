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

    public void addFieldFailure(final String field, final String errorCode) {
        this.successful = false;
        if (this.errors == null) {
            this.errors = new HashMap<>();
        }
        ArrayList<String> current = this.errors.get(field);
        if (current == null) {
            current = new ArrayList<>();
            this.errors.put(field, current);
        }
        current.add(errorCode);
    }

    public List<String> getErrors(final String field) {
        if (this.errors == null) {
            return null;
        }
        return this.errors.get(field);
    }

    public void setFailedStorage() {
        this.successful = false;
        this.storageFailure = true;
    }

    public void setTooMuchData() {
        this.successful = false;
        this.tooMuchData = true;
    }

    public boolean success() {
        return this.successful;
    }

    @Override
    public String toString() {
        if (this.successful) {
            return "success";
        }
        final StringBuilder failure = new StringBuilder();
        failure.append("failed;");
        return failure.toString();
    }

    public boolean wasStorageFailure() {
        return this.storageFailure;
    }

    public boolean wasTooMuchData() {
        return this.tooMuchData;
    }
}
