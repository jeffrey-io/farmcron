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
        SeeChecksTab("see_checks_tab", Roles.GOD, Roles.OWNER), //
        SeeOutstandingChecksForEveryone("see_all_checks", Roles.GOD, Roles.OWNER), //
        CheckMake("check_make", Roles.GOD, Roles.OWNER), //
        
        // CHORE RELATED ACTIONS
        SeeChoresTab("see_chores_tab", Roles.GOD, Roles.OWNER, Roles.ALL_CHORES), //
        ViewChore("view_chore", Roles.GOD, Roles.OWNER, Roles.ALL_CHORES), //
        EditChore("edit_chore", Roles.GOD, Roles.OWNER, Roles.ALL_CHORES), //
        PerformChore("perform_chore", Roles.GOD, Roles.OWNER, Roles.ALL_CHORES), //

        // CHECK RELATED ACTIONS
        SeeEventsTab("see_events_tab", Roles.GOD, Roles.OWNER, Roles.ALL_EVENTS), //

        SeeSubscripionsTab("see_subscriptions_tab", Roles.GOD, Roles.OWNER), //

        SeeHabitsTab("see_habits_tab", Roles.GOD, Roles.OWNER), //

        
        // 
        SeePayrollTab("see_payroll_tab", Roles.GOD, Roles.OWNER, Roles.PAID_EMPLOYEE),

        
        SeePeopleTab("see_people_tab", Roles.GOD, Roles.OWNER, Roles.MANAGER), //
        CreatePeople("create_people", Roles.GOD, Roles.OWNER), //
        

        SeeSiteProperties("see_site_properties", Roles.GOD, Roles.OWNER), //
        EditSiteProperties("edit_site_properties", Roles.GOD, Roles.OWNER), //

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
