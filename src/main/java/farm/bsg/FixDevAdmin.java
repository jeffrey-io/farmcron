package farm.bsg;

import farm.bsg.models.Person;

public class FixDevAdmin {
    public static void main(final String[] args) throws Exception {
        final ProductEngine engine = Server.devEngine();
        for (final Person p : engine.select_person().done()) {
            if ("admin".equals(p.get("login"))) {
                p.setPassword("password");
                engine.put(p);
            }
        }
    }
}
