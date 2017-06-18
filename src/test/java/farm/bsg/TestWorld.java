package farm.bsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.amazonaws.util.json.Jackson;

import farm.bsg.cron.JobManager;
import farm.bsg.data.Value;
import farm.bsg.data.contracts.KeyValueStoragePut;
import farm.bsg.data.contracts.PersistenceLogger;
import farm.bsg.models.Person;

public class TestWorld {

    public static class TestLogger implements PersistenceLogger {

        private final HashMap<String, Value> valuesWrittenByKey;
        private final ArrayList<String>      keysWrittenInOrder;

        public TestLogger() {
            this.valuesWrittenByKey = new HashMap<>();
            this.keysWrittenInOrder = new ArrayList<>();
        }

        @Override
        public boolean put(String key, Value newValue) {
            keysWrittenInOrder.add(key);
            valuesWrittenByKey.put(key, newValue);
            return true;
        }

        @Override
        public void pump(KeyValueStoragePut storage) throws Exception {
            for (Entry<String, Value> entry : valuesWrittenByKey.entrySet()) {
                storage.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public static TestLogger IN_MEMORY_PERSISTENCE_LOGGER() {
        return new TestLogger();
    }

    public final ProductEngine engine;

    private TestWorld(TestLogger logger) throws Exception {
        JobManager jobManager = new JobManager();
        this.engine = new ProductEngine(jobManager, logger, "$BODY$");
    }

    public static TestWorldBuilder start() {
        return new TestWorldBuilder();
    }

    public static class TestWorldBuilder {
        private final TestLogger logger;

        public TestWorldBuilder() {
            this.logger = new TestLogger();
        }

        public TestWorldBuilder write(String key, Value value) {
            this.logger.put(key, value);
            return this;
        }

        public TestWorldBuilder withSampleData() {
            TestWorldBuilder chain = this;
            chain = chain.withTestPerson("admin", "password", "god");
            return chain;
        }

        public TestWorldBuilder withTestPerson(String login, String password, String grants) {
            Person person = new Person();
            person.generateAndSetId();
            person.set("login", login);
            person.setPassword(password);
            person.set("notification_token", "token_" + login);
            person.set("super_cookie", "SUPER_COOKIE_" + login.toUpperCase());
            person.set("permissions_and_roles", grants);
            return write(person.getStorageKey(), new Value(person.toJson()));
        }

        public TestWorld done() throws Exception {
            return new TestWorld(this.logger);
        }
    }

    public static class ValueBuilder {
        private HashMap<String, String> data;

        public ValueBuilder() {
            this.data = new HashMap<>();
        }

        public ValueBuilder with(String key, String value) {
            this.data.put(key, value);
            return this;
        }

        public Value done() {
            return new Value(Jackson.toJsonString(data));
        }
    }

    public static ValueBuilder value_start() {
        return new ValueBuilder();
    }
}
