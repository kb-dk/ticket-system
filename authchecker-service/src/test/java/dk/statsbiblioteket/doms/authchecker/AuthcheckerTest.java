package dk.statsbiblioteket.doms.authchecker;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.statsbiblioteket.doms.authchecker.exceptions.FedoraException;
import dk.statsbiblioteket.doms.authchecker.exceptions.InvalidCredentialsException;
import dk.statsbiblioteket.doms.authchecker.exceptions.MissingArgumentException;
import dk.statsbiblioteket.doms.authchecker.exceptions.ResourceNotFoundException;
import dk.statsbiblioteket.doms.authchecker.user.Roles;
import dk.statsbiblioteket.doms.authchecker.user.User;
import dk.statsbiblioteket.doms.webservices.configuration.ConfigCollection;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

/** Test of authchecker class. */
public class AuthcheckerTest extends TestCase {
    /** Remembers old fedora location property. */
    private String oldFedoraLocationProperty;

    /**
     * Start a mockup fedora server, and overwrite property referring to fedora location with the mockup.
     *
     * @throws Exception On errors in the test setup.
     */
    @Before
    public void setUp() throws Exception {
        // Start the mockup Fedora REST server.
        HttpServerFactory.create("http://localhost:12345/fedora", new ClassNamesResourceConfig(MockupFedora.class)).start();
        // Remember old fedora server property
        oldFedoraLocationProperty = ConfigCollection.getProperties().getProperty("dk.statsbiblioteket.doms.authchecker.fedoralocation");
        // Configure authchecker to use mockup fedora server.
        ConfigCollection.getProperties().setProperty("dk.statsbiblioteket.doms.authchecker.fedoralocation", "http://localhost:12345/fedora");
    }

    /**
     * Restore the old property referring to fedora location.
     *
     * @throws Exception On errors in the test setup.
     */
    @After
    public void tearDown() throws Exception {
        // Reset fedora server property
        if (oldFedoraLocationProperty != null) {
            ConfigCollection.getProperties()
                    .setProperty("dk.statsbiblioteket.doms.authchecker.fedoralocation", oldFedoraLocationProperty);
        } else {
            ConfigCollection.getProperties().remove("dk.statsbiblioteket.doms.authchecker.fedoralocation");
        }
    }

    /**
     * Test the functionality for checking access to a pid.
     *
     * @throws Exception On test errors.
     */
    @Test
    public void testIsPidAllowedForThisUserWithTheseRoles() throws Exception {
        Authchecker authchecker = new Authchecker();
        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.putSingle("hello", "world");
        map.add("foo", "bar");
        map.add("foo", "baz");

        // Good case: Allowed
        User user = authchecker.isPidAllowedForThisUserWithTheseRoles("x", "demo:1", map);
        assertEquals("x", user.getUsername());
        List<Roles> roles = user.getAttributes();
        assertEquals(2, roles.size());
        //Test result. Note: this assumes order is preserved, although this is not required. If test fails, please fix
        //to ignore order.
        assertEquals("hello", roles.get(0).getAssociation());
        assertEquals(1, roles.get(0).getRoles().size());
        assertEquals("world", roles.get(0).getRoles().get(0));
        assertEquals("foo", roles.get(1).getAssociation());
        assertEquals(2, roles.get(1).getRoles().size());
        assertEquals("bar", roles.get(1).getRoles().get(0));
        assertEquals("baz", roles.get(1).getRoles().get(1));

        // Good case: Allowed with null values
        user = authchecker.isPidAllowedForThisUserWithTheseRoles(null, "demo:1", (MultivaluedMap<String, String>) null);
        assertNotNull(user.getUsername());
        roles = user.getAttributes();
        assertNotNull(roles);
        assertEquals(0, roles.size());

        // Bad case: Null pid
        try {
            authchecker.isPidAllowedForThisUserWithTheseRoles("x", null, map);
            fail("Should have thrown MissingArgumentException");
        } catch (MissingArgumentException e) {
            //Expected
        }

        // Bad case: Not found
        try {
            authchecker.isPidAllowedForThisUserWithTheseRoles("x", "demo:2", map);
            fail("Should have thrown ResourceNotFoundException");
        } catch (ResourceNotFoundException e) {
            //Expected
        }

        // Bad case: Not allowed
        try {
            authchecker.isPidAllowedForThisUserWithTheseRoles("x", "demo:3", map);
            fail("Should have thrown InvalidCredentialsException");
        } catch (InvalidCredentialsException e) {
            //Expected
        }

        // Bad case: Fedora communication error
        try {
            authchecker.isPidAllowedForThisUserWithTheseRoles("x", "demo:4", map);
            fail("Should have thrown FedoraException");
        } catch (FedoraException e) {
            //Expected
        }
    }
}
