/**
 * Copyrighted Sep 18, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import nl.uu.fi.dwo.rest.dom.entities.DomRole;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentRole;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import static fi.dwo.server.rest.PublicUserManagerIT.instance;
import java.util.List;
import javax.ws.rs.core.SecurityContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests bi-implication of roles in RoleType and the database.
 * 
 * 
 * @author Gert van der Plas
 */
public class PublicRoleManagerIT {

    public PublicRoleManagerIT() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
    }

    @BeforeClass
    public static void setUpClass() {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
    }

    @AfterClass
    public static void tearDownClass() {
        DwoEmfFactory.setDefaultEntityManagerFactory();
        instance = null;
    }

    @Before
    public void setUp() {
        instance.IntializeTestDatabase();
    }

    @After
    public void tearDown() {
        instance.ClearDatabase();
    }

    /**
     * Test of getRoles method, of class PublicRoleManager. Tests for one-to-one
     * of RoleTypes mapping between persistent store and enum class.
     */
    @Test
    public void testGetRoles() {
        System.out.println("getRoles");
        SecurityContext sc = null;
        PublicRoleManager instance = new PublicRoleManager();
        List<DomRole> result = instance.getRoles(sc);
        RoleType[] types = RoleType.values();
//        // Database roles => RoleTypes
//        for (DomRole r : result) {
//            assertEquals(r.getRoleName(), RoleType.valueOf(r.getRoleName()).name());
//        }
        // RoleTypes => Database roles
        for (DomRole r : result) {
            Boolean fail = true;
            for(RoleType t : RoleType.values()){
                if(t.name().equals(r.getRoleName())){
                    fail = false;
                    break;
                }
            }
            if(fail){
                fail("RoleType roles are missing in the Database.");
            }
        }
    }
}
