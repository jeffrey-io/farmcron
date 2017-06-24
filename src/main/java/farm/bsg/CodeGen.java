package farm.bsg;

import java.io.File;
import java.nio.file.Files;

import farm.bsg.data.Authenticator;
import farm.bsg.data.SchemaCodeGenerator;
import farm.bsg.models.Cart;
import farm.bsg.models.CartItem;
import farm.bsg.models.Check;
import farm.bsg.models.Customer;
import farm.bsg.models.Habit;
import farm.bsg.models.PayrollEntry;
import farm.bsg.models.Person;
import farm.bsg.models.Product;
import farm.bsg.models.SiteProperties;
import farm.bsg.models.Subscriber;
import farm.bsg.models.Subscription;
import farm.bsg.models.Task;
import farm.bsg.models.TaskFactory;
import farm.bsg.models.WakeInputFile;
import farm.bsg.ops.CounterCodeGen;
import farm.bsg.pages.Checks;
import farm.bsg.pages.Dashboard;
import farm.bsg.pages.Habits;
import farm.bsg.pages.Payroll;
import farm.bsg.pages.People;
import farm.bsg.pages.Products;
import farm.bsg.pages.PublicSite;
import farm.bsg.pages.Shop;
import farm.bsg.pages.SignIn;
import farm.bsg.pages.Site;
import farm.bsg.pages.Subscriptions;
import farm.bsg.pages.TaskFactoryManagement;
import farm.bsg.pages.Tasks;
import farm.bsg.pages.You;
import farm.bsg.pages.YourCart;

public class CodeGen {
    private final String path;

    public CodeGen(String path) {
        this.path = path;
    }

    private void buildQueryEngine() throws Exception {
        SchemaCodeGenerator codegen = new SchemaCodeGenerator("farm.bsg", "QueryEngine");

        codegen.addSample(new Cart());
        codegen.addSample(new CartItem());
        codegen.addSample(new Check());
        codegen.addSample(new Habit());
        codegen.addSample(new PayrollEntry());
        codegen.addSample(new Person());
        codegen.addSample(new Product());
        codegen.addSample(new SiteProperties());
        codegen.addSample(new Subscriber());
        codegen.addSample(new Subscription());
        codegen.addSample(new Task());
        codegen.addSample(new TaskFactory());
        codegen.addSample(new WakeInputFile());
        String java = codegen.java();

        System.out.println(java);
        File f = new File(path + "QueryEngine.java");
        Files.write(f.toPath(), java.getBytes());
    }

    private void FacebookMessenger(CounterCodeGen c) {
        c.section("Facebook Messenger");
        c.counter("fb_has_invalid_host", "Facebook is setting a host header that is wrong");
        c.counter("fb_subscribe_begin", "Facebook is attempting to subscribe to the page");
        c.counter("fb_token_given_correct", "Facebook gave us the right token");
        c.counter("fb_token_given_wrong", "Facebook gave us the wrong token");
        c.counter("fb_message", "Facebook is sending us a message");
        c.counter("fb_message_valid", "We were able to parse the message");
        c.counter("fb_message_invalid", "We failed to parse the message");
        c.counter("fb_attempt_response", "The engine gave a response back from a given message");
        c.counter("fb_send_failed_no_fb_token", "We are unable to send messages since the site lacks a token");
        c.counter("fb_send_out_on_wire", "We attempted to actuall send the message");
        c.counter("fb_send_ok", "We sent a message well enough");
        c.counter("fb_send_failed_send", "We were unable to send the message via HTTP");
    }

    private void Data(CounterCodeGen c) {
        AlexaCommands.link(c);
        Authenticator.link(c);
        Cart.link(c);
        CartItem.link(c);
        Check.link(c);
        Customer.link(c);
        Habit.link(c);
        PayrollEntry.link(c);
        Person.link(c);
        Product.link(c);
        SiteProperties.link(c);
        Subscriber.link(c);
        Subscription.link(c);
        Task.link(c);
        WakeInputFile.link(c);
    }

    private void Pages(CounterCodeGen c) {
        Checks.link(c);
        Dashboard.link(c);
        Habits.link(c);
        Payroll.link(c);
        People.link(c);
        Products.link(c);
        PublicSite.link(c);
        Shop.link(c);
        SignIn.link(c);
        Site.link(c);
        Subscriptions.link(c);
        TaskFactoryManagement.link(c);
        Tasks.link(c);
        You.link(c);
        YourCart.link(c);
    }

    private void buildCounters() throws Exception {
        CounterCodeGen c = new CounterCodeGen();
        FacebookMessenger(c);
        Pages(c);
        Data(c);

        File f = new File(path + "BsgCounters.java");
        Files.write(f.toPath(), c.java("bsg.farm", "BsgCounters").getBytes());
    }

    public static void main(String[] args) throws Exception {
        CodeGen codegen = new CodeGen("/home/jeffrey/projects/farmcron/src/main/java/farm/bsg/");
        codegen.buildQueryEngine();
        codegen.buildCounters();
    }
}
