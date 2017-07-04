package farm.bsg;

import farm.bsg.cron.JobManager;
import farm.bsg.data.Authenticator;
import farm.bsg.data.contracts.PersistenceLogger;
import farm.bsg.html.GenericTemplate;
import farm.bsg.pages.TaskFactoryManagement;
import farm.bsg.pages.common.NavBar;

public class ProductEngine extends QueryEngine {
    public final Authenticator   auth;
    public final NavBar          navbar;
    public final GenericTemplate template;
    public final AlexaCommands   alexa;
    public final EventBus eventBus;

    public ProductEngine(JobManager manager, PersistenceLogger persistence, String pageTemplate) throws Exception {
        super(persistence);

        this.auth = new Authenticator(this);
        this.navbar = new NavBar(this);
        this.alexa = new AlexaCommands(this);

        this.template = new GenericTemplate(pageTemplate);
        this.eventBus = new EventBus(this);
        manager.add(new TaskFactoryManagement.TaskFactoryMonitor(this));
    }
}
