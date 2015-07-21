/**
 * Copyrighted Jul 20, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.entities.PersistentRole;
import java.util.List;
import javax.annotation.Resource;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.SecurityContext;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests whether PublicRoleManager works. First code example.
 *
 * @author G.A.J. van der Plas
 */
public class PublicRoleManagerIT extends JerseyTest {

    public PublicRoleManagerIT() {
    }

    @Override
    protected Application configure() {
//        enable(TestProperties.LOG_TRAFFIC);
 //       enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(Resource.class);
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    @Override
    public void setUp() {
    }

    @After
    @Override
    public void tearDown() {
    }

    /**
     * Test of getRoles method, of class PublicRoleManager.
     */
    @Path("/get/json")
 //   @Test
    public void testGetRoles() {
        System.out.println("getRoles");
        SecurityContext sc = null;
        PublicRoleManager instance = new PublicRoleManager();
        List<PersistentRole> expResult = null;
        List<PersistentRole> result = instance.getRoles(sc);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
