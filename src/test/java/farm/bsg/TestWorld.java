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
        public void pump(final KeyValueStoragePut storage) throws Exception {
            for (final Entry<String, Value> entry : this.valuesWrittenByKey.entrySet()) {
                storage.put(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public boolean put(final String key, final Value newValue) {
            this.keysWrittenInOrder.add(key);
            this.valuesWrittenByKey.put(key, newValue);
            return true;
        }
    }

    public static class TestWorldBuilder {
        private final TestLogger logger;

        public TestWorldBuilder() {
            this.logger = new TestLogger();
        }

        public TestWorld done() throws Exception {
            return new TestWorld(this.logger);
        }

        public TestWorldBuilder withSampleData() {
            TestWorldBuilder chain = this;
            chain = chain.withTestPerson("admin", "password", "god");
            return chain;
        }

        public TestWorldBuilder withTestPerson(final String login, final String password, final String grants) {
            final Person person = new Person();
            person.generateAndSetId();
            person.set("login", login);
            person.setPassword(password);
            person.set("notification_token", "token_" + login);
            person.set("super_cookie", "SUPER_COOKIE_" + login.toUpperCase());
            person.set("permissions_and_roles", grants);
            return write(person.getStorageKey(), new Value(person.toJson()));
        }

        public TestWorldBuilder write(final String key, final Value value) {
            this.logger.put(key, value);
            return this;
        }
    }

    public static class ValueBuilder {
        private final HashMap<String, String> data;

        public ValueBuilder() {
            this.data = new HashMap<>();
        }

        public Value done() {
            return new Value(Jackson.toJsonString(this.data));
        }

        public ValueBuilder with(final String key, final String value) {
            this.data.put(key, value);
            return this;
        }
    }

    public static TestLogger IN_MEMORY_PERSISTENCE_LOGGER() {
        return new TestLogger();
    }

    public static TestWorldBuilder start() {
        return new TestWorldBuilder();
    }

    public static ValueBuilder value_start() {
        return new ValueBuilder();
    }

    public final ProductEngine engine;

    private TestWorld(final TestLogger logger) throws Exception {
        final JobManager jobManager = new JobManager();
        this.engine = new ProductEngine(jobManager, logger, "$BODY$");
    }
}
