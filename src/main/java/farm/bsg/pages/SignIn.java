package farm.bsg.pages;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.regex.Pattern;

import farm.bsg.ProductEngine;
import farm.bsg.Server;
import farm.bsg.data.Authenticator.AuthResult;
import farm.bsg.html.Input;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.route.MultiTenantRouter;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;

public class SignIn {

    private static final String ERROR_BANNER   = Pattern.quote("$ERROR_BANNER$");
    private static final String PRODUCT_NAME   = Pattern.quote("$PRODUCT_NAME$");
    private static final String USERNAME_INPUT = Pattern.quote("$USERNAME_INPUT$");
    private static final String PASSWORD_INPUT = Pattern.quote("$PASSWORD_INPUT$");

    public static SimpleURI     SIGNIN         = new SimpleURI("/admin/sign-in");

    public static SimpleURI     SIGNIN_EXECUTE = new SimpleURI("/admin/sign-in;execute");

    public static void link(final CounterCodeGen c) {
        c.section("Page: Sign-In");
    }

    public static void link(final RoutingTable routing, final MultiTenantRouter router) throws Exception {
        final String template = Server.getTextFile("sign-in.html");
        get(SIGNIN.href().value, (req, res) -> {

            try {
                final String host = req.headers("Host");
                final ProductEngine engine = router.findByDomain(req.headers("Host"));
                if (engine == null) {
                    return "Host '" + host + "' not found";
                }

                final String username = req.queryParams("username");
                boolean invalid = false;
                final String invalidRaw = req.queryParams("invalid");
                if (invalidRaw != null && invalidRaw.length() > 0) {
                    invalid = true;
                }
                return new SignIn(template).signin(engine.siteproperties_get().get("product_name"), username, invalid);
            } catch (final Exception err) {
                err.printStackTrace();
                throw err;
            }
        });
        post(SIGNIN_EXECUTE.href().value, (req, res) -> {
            try {
                final String host = req.headers("Host");
                final ProductEngine engine = router.findByDomain(req.headers("Host"));
                if (engine == null) {
                    return "Host '" + host + "' not found";
                }
                final String username = req.queryParams("username");
                final String password = req.queryParams("password");
                final AuthResult authResult = engine.auth.authenticateByUsernameAndPassword(username, password);
                final String prefix = (router.isSecure() ? "https://" : "http://") + host;
                if (authResult.allowed) {
                    res.cookie(SessionRequest.AUTH_COOKIE_NAME, authResult.cookie);
                    res.redirect(prefix + Dashboard.DASHBOARD.href().value);
                } else {
                    res.redirect(prefix + SIGNIN.href("username", username, "invalid", "T").value);
                }
            } catch (final Exception err) {
                err.printStackTrace();
            }
            return null;
        });
    }

    private final String template;

    public SignIn(final String template) {
        this.template = template;
    }

    public String signin(String product_name, final String username, final boolean invalid) {
        String result = this.template;
        String error = "";
        if (invalid) {
            error = "<div class=\"alert alert-danger\" role=\"alert\">Your attempt to sign into the system has <strong>failed</strong>. This is either due to your password being incorrect, or you do not exist. We sure hope you do exist, so please try again.</div>";
        }

        if (product_name == null) {
            product_name = "";
        }
        result = result.replaceAll(ERROR_BANNER, error);
        result = result.replaceAll(PRODUCT_NAME, product_name);
        result = result.replaceAll(USERNAME_INPUT, new Input("username").text().id("username").clazz("form-control").placeholder("login...").required().autofocus(username == null).value(username).toHtml());
        result = result.replaceAll(PASSWORD_INPUT, new Input("password").password().id("password").clazz("form-control").placeholder("password...").required().autofocus(username != null).toHtml());
        return result;
    }
}
