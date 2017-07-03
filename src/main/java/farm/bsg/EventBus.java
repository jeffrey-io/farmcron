package farm.bsg;

import farm.bsg.models.Subscription;

public class EventBus {

    public static enum Event {
        TaskCreation("tc", "Task Creation", "Fires when a new task is created"), // A new task was created
        
        TaskSummary("ts", "Task Summary", "Fires at the given subscription time"), // a task summary of all tasks sent during morning (if there are any)
        
        DirectPublish("dp", "Direct Publish", "Fires when someone publishes");
        
        public final String code;
        public final String fullName;
        public final String description;

        private Event(String code, String fullName, String description) {
            this.code = code;
            this.fullName = fullName;
            this.description = description;
        }
    }
    
    public class EventPayload {
        public final String shortText;
        
        public EventPayload(String shortText) {
            this.shortText = shortText;
        }
    }
    
    private final QueryEngine query;

    public EventBus(QueryEngine query) {
        this.query = query;
    }
    
    public void triger(Event event, EventPayload payload) {
        for (Subscription sub : query.select_subscription().done()) {
        }
    }
}
