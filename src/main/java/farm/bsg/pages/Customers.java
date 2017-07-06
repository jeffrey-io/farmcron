package farm.bsg.pages;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import farm.bsg.Security.Permission;
import farm.bsg.data.Authenticator.AuthResultCustomer;
import farm.bsg.data.UriBlobCache.UriBlob;
import farm.bsg.html.Block;
import farm.bsg.html.ErrorListBuilder;
import farm.bsg.html.Html;
import farm.bsg.html.Table;
import farm.bsg.models.Cart;
import farm.bsg.models.Customer;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.common.CustomerPage;
import farm.bsg.pages.common.SessionPage;
import farm.bsg.route.CustomerRequest;
import farm.bsg.route.FinishedHref;
import farm.bsg.route.RoutingTable;
import farm.bsg.route.SessionRequest;
import farm.bsg.route.SimpleURI;

public class Customers {

    public static class ForAdmin extends SessionPage {

        public ForAdmin(final SessionRequest session) {
            super(session, CUSTOMERS);
        }

        public String list() {
            person().mustHave(Permission.CustomerManager);
            final Table table = Html.table("Email");
            for (final Customer customer : query().select_customer().done()) {
                table.row(customer.get("email"));
            }
            final Block page = Html.block();
            page.add(table);
            return finish_pump(page);
        }
    }

    public static class ForCustomer extends CustomerPage {

        public ForCustomer(final CustomerRequest request) {
            super(request, CUSTOMER_JOIN);
        }

        public String info() {
            if (!this.request.hasCustomer) {
                this.request.redirect(CUSTOMER_JOIN.href("success_url", CUSTOMER_INFO.href().value));
                return null;
            }
            final Table table = Html.table("Order Id", "State", "Task");
            for (final Cart cart : query().select_cart().where_customer_eq(this.request.customer.getId()).done()) {
                String state = cart.get("state");
                if ("wait".equals(state)) {
                    // waiting for task to get completed
                } else if ("processing".equals(state)) {

                } else if ("done".equals(state)) {

                } else {
                    state = "Your current cart.";
                }
                table.row(cart.getId(), state, cart.get("task"));
            }
            return table.toHtml();
        }

        public UriBlob join() {
            final HashMap<String, Object> root = new HashMap<>();
            String success_url = null;
            if (this.request.hasNonNullQueryParam("success_url")) {
                success_url = this.request.getParam("success_url");
                root.put("success_url", success_url);
            }
            final UriBlob blob = query().publicBlobCache.get("/*join.html");
            if (blob == null) {
                return null;
            }
            final String email = this.request.getParam("email");
            String error = null;
            String phase = "normal";
            if ("login".equals(this.request.getParam("command"))) {
                final String password = this.request.getParam("password");
                final AuthResultCustomer authResult = this.engine.auth.authenticateCustomer(email, password);
                if (authResult.allowed) {
                    this.request.setCookie(CustomerRequest.AUTH_COOKIE_NAME, authResult.cookie);
                    if (success_url != null) {
                        this.request.redirect(new FinishedHref(success_url));
                    } else {
                        this.request.redirect(CUSTOMER_INFO.href());
                    }
                    return null;
                } else {
                    error = "Authorization failed, please check your email and password.";
                }
            }
            if ("create".equals(this.request.getParam("command"))) {
                final String password1 = this.request.getParam("password_1");
                final String password2 = this.request.getParam("password_2");
                final String name = this.request.getParam("name");
                final String policy = this.request.getParam("policy");
                if (name != null) {
                    root.put("name", name);
                }
                final String phone = this.request.getParam("phone");
                if (name != null) {
                    root.put("phone", phone);
                }

                final ErrorListBuilder errorBuilder = new ErrorListBuilder();
                errorBuilder.accept(email == null || "".equals(email), "Please provide an email address.");
                errorBuilder.accept(name == null || "".equals(name), "Please provide a name; we use your name to help staff identify orders.");
                errorBuilder.accept(phone == null || "".equals(phone), "Please provide your phone number; we will use this to contact you about orders.");
                errorBuilder.accept(password1 == null || "".equals(password1), "Please provide your password; we use this to protect your order history.");
                errorBuilder.accept(password2 == null || "".equals(password2), "Please confirm your password.");
                if (password1 != null && password2 != null) {
                    errorBuilder.accept(!password1.equals(password2), "Your passwords do not match.");
                }
                if (email != null) {
                    final boolean exists = this.engine.select_customer().where_email_eq(email).to_list().count() > 0;
                    errorBuilder.accept(exists, "That email already exists in our system. Please try to sign in or recover your password.");
                }
                errorBuilder.accept(!("agree".equals(policy)), "Please accept our Privacy and Terms of Use agreements.");

                if (errorBuilder.hasErrored()) {
                    error = errorBuilder.getErrors();
                } else {
                    // it was a success, let's create it
                    final Customer customer = new Customer();
                    customer.generateAndSetId();
                    customer.set("email", email);
                    customer.setPassword(password1);
                    customer.set("name", name);
                    customer.set("phone", phone);
                    query().put(customer);
                }
            }
            if ("recover".equals(this.request.getParam("command"))) {
                phase = "recover";
            }
            if ("recover-execute".equals(this.request.getParam("command")) || this.request.hasNonNullQueryParam("token")) {
                phase = "token";
            }
            if (email != null) {
                root.put("email", email);
            }
            root.put("has_error", error != null);
            root.put("error", error);
            root.put("token", this.request.getParam("token"));
            root.put("is_" + phase, true);
            root.put("recovery_begin_url", CUSTOMER_JOIN.href("command", "recover", "email", email).value);

            return blob.transform((s) -> {
                try {
                    final String template = s.replaceAll(Pattern.quote("%["), "{{").replaceAll(Pattern.quote("]%"), "}}");
                    return compiler.compileInline(template).apply(root);
                } catch (final IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            });
        }
    }

    public static final SimpleURI CUSTOMERS     = new SimpleURI("/admin/customers");
    public static final SimpleURI CUSTOMER_JOIN = new SimpleURI("/join");
    public static final SimpleURI CUSTOMER_INFO = new SimpleURI("/membership-portal");

    public static void link(final CounterCodeGen c) {
        c.section("Page: Checks");
    }

    public static void link(final RoutingTable routing) {
        routing.navbar(CUSTOMERS, "Customers", Permission.CustomerManager);
        routing.get(CUSTOMERS, (session) -> new ForAdmin(session).list());
        routing.customer_get_or_post(CUSTOMER_JOIN, (cr) -> new ForCustomer(cr).join());
        routing.customer_get_or_post(CUSTOMER_INFO, (cr) -> new ForCustomer(cr).info());
    }
}
