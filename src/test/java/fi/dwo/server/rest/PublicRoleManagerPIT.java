/**
 * Copyrighted Sep 18, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentRole;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.List;
import javax.ws.rs.core.SecurityContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gert van der Plas
 */
public class PublicRoleManagerPIT {

    public PublicRoleManagerPIT() {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getRoles method, of class PublicRoleManager.
     */
    @Test
    public void testGetRoles() {
        System.out.println("getRoles");
        SecurityContext sc = null;
        PublicRoleManager instance = new PublicRoleManager();
        List<PersistentRole> result = instance.getRoles(sc);
        RoleType[] types = RoleType.values();
        for (PersistentRole r : result) {
            assertEquals(r.getGroupname(), RoleType.valueOf(r.getGroupname()).name());
        }
    }
}
