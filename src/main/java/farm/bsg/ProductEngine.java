package farm.bsg;

import farm.bsg.data.Authenticator;
import farm.bsg.data.PutResult;
import farm.bsg.data.RawObject;
import farm.bsg.data.Value;
import farm.bsg.data.contracts.PersistenceLogger;
import farm.bsg.html.shit.GenericTemplate;
import farm.bsg.models.SiteProperties;
import farm.bsg.pages.common.NavBar;

public class ProductEngine extends QueryEngine {
    public final Authenticator   auth;
    public final NavBar          navbar;
    public final GenericTemplate template;

    public final AlexaCommands   alexa;

    public ProductEngine(PersistenceLogger persistence, String pageTemplate) throws Exception {
        super(persistence);

        this.auth = new Authenticator(this);
        this.navbar = new NavBar(this);
        this.alexa = new AlexaCommands(this);

        this.template = new GenericTemplate(pageTemplate);
    }

    public SiteProperties properties() {
        SiteProperties prop = siteproperties_by_id("properties", true);
        prop.set("id", "properties");
        return prop;
    }

    public boolean save(RawObject o) {
        String key = o.getStorageKey();
        PutResult result = storage.put(key, new Value(o.toJson()));
        return result.success();
    }
}
