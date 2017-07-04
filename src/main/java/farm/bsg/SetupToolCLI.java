package farm.bsg;

import java.util.regex.Pattern;

import com.amazonaws.services.s3.model.ListObjectsRequest;

import farm.bsg.models.Person;

public class SetupToolCLI {
    private static void create(final String domain, final String adminLogin, final String adminPassword, final ServerOptions options) {
        if (options.production) {
            final Person person = new Person();
            person.generateAndSetId();
            person.set("login", adminLogin);
            person.setPassword(adminPassword);
            person.set("permissions_and_roles", "god");
            person.set("name", "ADMIN");
            final String key = "customers/" + domain + "/" + person.getStorageKey();
            options.s3().putObject(options.bucket, key, person.toJson());
        } else {
            System.out.println("not available without production credentials");
        }
    }

    public static void main(final String[] args) {
        System.out.println("-=[ Setup Tool ]=-");
        for (final String arg : args) {
            System.out.println("arg:" + arg);
        }
        final ServerOptions options = new ServerOptions(args);

        System.out.print("command:");
        final String command = System.console().readLine();
        if ("new".equals(command)) {
            System.out.print("domain:");
            final String domain = System.console().readLine().trim().toLowerCase();
            System.out.print("admin login:");
            final String login = System.console().readLine().trim().toLowerCase();
            System.out.print("admin password:");
            final String adminPassword1 = new String(System.console().readPassword());
            System.out.print("admin password (confirm):");
            final String adminPassword2 = new String(System.console().readPassword());
            if (adminPassword1.length() < 8) {
                System.err.println("Admin password not long enough, aborting...");
                return;
            }
            if (!adminPassword1.equals(adminPassword2)) {
                System.err.println("Failed to confirm passwords, aborting...");
                return;
            }
            create(domain, login, adminPassword1, options);
        }
        if ("list".equals(command)) {
            if (options.production) {
                final ListObjectsRequest lor = new ListObjectsRequest().withBucketName(options.bucket).withDelimiter("/").withPrefix("customers/");
                for (final String prefix : options.s3().listObjects(lor).getCommonPrefixes()) {
                    // Parse out scope, then look up properties to get the domain.
                    final String domain = prefix.split(Pattern.quote("/"))[1];
                    System.out.println(prefix + "-->" + domain);
                }
            } else {
                System.out.println("not available without production credentials");
            }
        }

    }
}
