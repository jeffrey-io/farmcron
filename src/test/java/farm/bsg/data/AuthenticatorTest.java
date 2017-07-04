package farm.bsg.data;

import org.junit.Assert;
import org.junit.Test;

import farm.bsg.TestWorld;
import farm.bsg.data.Authenticator.AuthResult;

public class AuthenticatorTest {

    @Test
    public void ByUsernameAndPasswordFlow() throws Exception {
        final TestWorld world = TestWorld.start().withSampleData().done();
        final AuthResult authByUsername = world.engine.auth.authenticateByUsernameAndPassword("admin", "password");
        Assert.assertTrue(authByUsername.allowed);
        Assert.assertNotNull(authByUsername.cookie);
        Assert.assertEquals("admin", authByUsername.person.get("login"));

        final AuthResult authByCookie = world.engine.auth.authenticateAdminByCookies(authByUsername.cookie);
        Assert.assertTrue(authByCookie.allowed);
        Assert.assertNotNull(authByCookie.cookie);
        Assert.assertEquals("admin", authByCookie.person.get("login"));
    }

    @Test
    public void Impersonation() throws Exception {
        final TestWorld world = TestWorld.start().withSampleData().withTestPerson("demo", "demo", "employee").done();
        final AuthResult authByUsername = world.engine.auth.authenticateByUsernameAndPassword("admin:demo", "password");
        Assert.assertTrue(authByUsername.allowed);
        Assert.assertNotNull(authByUsername.cookie);
        Assert.assertEquals("demo", authByUsername.person.get("login"));

        final AuthResult authByCookie = world.engine.auth.authenticateAdminByCookies(authByUsername.cookie);
        Assert.assertTrue(authByCookie.allowed);
        Assert.assertNotNull(authByCookie.cookie);
        Assert.assertEquals("demo", authByCookie.person.get("login"));
    }
}
