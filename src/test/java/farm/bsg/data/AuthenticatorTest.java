package farm.bsg.data;

import org.junit.Assert;
import org.junit.Test;

import farm.bsg.TestWorld;
import farm.bsg.data.Authenticator.AuthResult;

public class AuthenticatorTest {

    @Test
    public void ByUsernameAndPasswordFlow() throws Exception {
        TestWorld world = TestWorld.start().withSampleData().done();
        AuthResult authByUsername = world.engine.auth.authenticateByUsernameAndPassword("admin", "password");
        Assert.assertTrue(authByUsername.allowed);
        Assert.assertNotNull(authByUsername.cookie);
        Assert.assertEquals("admin", authByUsername.person.get("login"));

        AuthResult authByCookie = world.engine.auth.authenticateByCookies(authByUsername.cookie, null);
        Assert.assertTrue(authByCookie.allowed);
        Assert.assertNotNull(authByCookie.cookie);
        Assert.assertEquals("admin", authByCookie.person.get("login"));
    }

    @Test
    public void Impersonation() throws Exception {
        TestWorld world = TestWorld.start().withSampleData().withTestPerson("demo", "demo", "employee").done();
        AuthResult authByUsername = world.engine.auth.authenticateByUsernameAndPassword("admin:demo", "password");
        Assert.assertTrue(authByUsername.allowed);
        Assert.assertNotNull(authByUsername.cookie);
        Assert.assertEquals("demo", authByUsername.person.get("login"));

        AuthResult authByCookie = world.engine.auth.authenticateByCookies(authByUsername.cookie, null);
        Assert.assertTrue(authByCookie.allowed);
        Assert.assertNotNull(authByCookie.cookie);
        Assert.assertEquals("demo", authByCookie.person.get("login"));
    }

    @Test
    public void BySuperCookie() throws Exception {
        TestWorld world = TestWorld.start().withSampleData().done();
        AuthResult authBySuperCookie = world.engine.auth.authenticateByCookies(null, "SUPER_COOKIE_ADMIN");
        Assert.assertTrue(authBySuperCookie.allowed);
        Assert.assertNotNull(authBySuperCookie.cookie);
        Assert.assertEquals("admin", authBySuperCookie.person.get("login"));

        AuthResult authByCookie = world.engine.auth.authenticateByCookies(authBySuperCookie.cookie, null);
        Assert.assertTrue(authByCookie.allowed);
        Assert.assertNotNull(authByCookie.cookie);
        Assert.assertEquals("admin", authByCookie.person.get("login"));
    }

    @Test
    public void DefaultAuthCreaesOnEmptyStorage() throws Exception {
        TestWorld world = TestWorld.start().done();
        AuthResult authByUsername = world.engine.auth.authenticateByUsernameAndPassword("admin", "default_password_42");
        Assert.assertTrue(authByUsername.allowed);
        Assert.assertNotNull(authByUsername.cookie);
        Assert.assertEquals("admin", authByUsername.person.get("login"));
        Assert.assertEquals(1, world.engine.select_person().to_list().done().size());
    }

    @Test
    public void DefaultAuthFailsWithAdminGiven() throws Exception {
        TestWorld world = TestWorld.start().withSampleData().done();
        AuthResult authByUsername = world.engine.auth.authenticateByUsernameAndPassword("admin", "default_password_42");
        Assert.assertFalse(authByUsername.allowed);
    }
}
