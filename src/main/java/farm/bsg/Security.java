package farm.bsg;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Security {

    private static final HashMap<String, Set<Permission>> TOKEN_TO_PERMISSION_TABLE = buildTable();

    private static HashMap<String, Set<Permission>> buildTable() {
        HashMap<String, Set<Permission>> table = new HashMap<String, Set<Permission>>();

        for (Permission p : Permission.values()) {
            table.put(p.token, Collections.singleton(p));
        }
        for (Roles r : Roles.values()) {
            HashSet<Permission> set = new HashSet<>();

            for (Permission p : Permission.values()) {
                for (Roles belongs : p.roles) {
                    if (belongs == r) {
                        set.add(p);
                        break;
                    }
                }
            }

            table.put(r.token, set);
        }

        return table;
    }

    public static Set<Permission> parse(String tokens) {
        if (tokens == null) {
            return Collections.emptySet();
        }
        HashSet<Permission> allowed = new HashSet<>();
        HashSet<Permission> deny = new HashSet<>();
        String[] parts = tokens.split(",");
        for (String part : parts) {
            Set<Permission> target = allowed;
            part = part.toLowerCase().trim();
            if (part.startsWith("-")) {
                part = part.substring(1).trim();
                target = deny;
            }
            Set<Permission> resolved = TOKEN_TO_PERMISSION_TABLE.get(part);
            if (resolved != null) {
                for (Permission p : resolved) {
                    target.add(p);
                }
            }
        }

        for (Permission toRemove : deny) {
            if (allowed.contains(toRemove)) {
                allowed.remove(toRemove);
            }
        }

        return allowed;
    }

    public enum Permission {
        Public("public", Roles.GOD, Roles.OWNER, Roles.UNPAID_EMPLOYEE, Roles.PAID_EMPLOYEE), // everyone can see or do this

        // CHECK RELATED ACTIONS
        CheckWriter("check_writer", Roles.GOD, Roles.OWNER), //

        // TASK RELATED PERMISSIONS
        SeeTaskFactoryTab("see_task_factories_tab", Roles.GOD, Roles.OWNER, Roles.MANAGER), //
        EditTaskFactory("edit_task_factories", Roles.GOD, Roles.OWNER, Roles.MANAGER), //
        SeeTasksTab("see_tasks_tab", Roles.GOD, Roles.OWNER, Roles.MANAGER), //
        EditTasks("edit_tasks", Roles.GOD, Roles.OWNER, Roles.MANAGER), //
        StartTask("start_task", Roles.GOD, Roles.OWNER, Roles.MANAGER, Roles.PAID_EMPLOYEE), //
        CloseTask("close_task", Roles.GOD, Roles.OWNER, Roles.MANAGER, Roles.PAID_EMPLOYEE), //

        // SUBSCRIPTION RELATED activities
        SubscriptionView("subscriptions_view", Roles.GOD, Roles.OWNER), //
        SubscriptionWrite("subscriptions_write", Roles.GOD, Roles.OWNER), //
        SubscriptionPublish("subscriptions_publish", Roles.GOD, Roles.OWNER), //

        // HABITS
        HabitsUnlocked("habits_unlocked", Roles.GOD, Roles.OWNER), //

        // for employees to report wages
        SeePayrollTab("see_payroll_tab", Roles.GOD, Roles.OWNER, Roles.PAID_EMPLOYEE),

        // Product Management
        SeeProductsTab("see_products_tab", Roles.GOD, Roles.OWNER, Roles.MANAGER), //
        CreateProduct("create_product", Roles.GOD, Roles.OWNER, Roles.MANAGER), //

        // For managing peoples
        PeopleManagement("people_manager", Roles.GOD, Roles.OWNER, Roles.MANAGER), //

        // For managing the site properties and the public site
        WebMaster("webmaster", Roles.GOD, Roles.OWNER), //

        ;

        private final String  token;
        private final Roles[] roles;

        private Permission(String token, Roles... roles) {
            this.token = token;
            this.roles = roles;
        }
    }

    public enum Roles {
        GOD("god"), // all permissions
        OWNER("owner"), // owner of the business
        MANAGER("manager"), // manager of people

        ALL_CHORES("chores"), // chore
        ALL_EVENTS("events"), // chore

        UNPAID_EMPLOYEE("unpaid_employee"), // i.e. child
        PAID_EMPLOYEE("employee"); //

        private final String token;

        private Roles(String token) {
            this.token = token;
        }
    }
}
