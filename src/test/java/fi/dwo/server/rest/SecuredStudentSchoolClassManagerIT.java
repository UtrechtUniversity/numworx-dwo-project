/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceClassType;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.rest.entities.RestSchoolClass;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class SecuredStudentSchoolClassManagerIT {

    private static final Logger LOG = Logger.getLogger(SecuredStudentSchoolClassManagerIT.class.getName());

    static DatabaseManager instance = null;

    public SecuredStudentSchoolClassManagerIT() {
    }

    @BeforeClass
    public static void setUpClass() {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
    }

    @AfterClass
    public static void tearDownClass() {
        instance = new DatabaseManager();
        DwoEmfFactory.setDefaultEntityManagerFactory();
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
     * Test of setActiveSchoolClass method, of class
     * SecuredStudentSchoolClassManager.
     */
    @Test
    public void testSetActiveSchoolClass() {
        System.out.println("setActiveSchoolClass");
        SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        //put in schoolgroup 5 , class 3 for user 9 (user02)
        restSchoolClass.setId(MySQLPersistenceId.createPersistenceId(3, PersistenceClassType.PersistentSchoolClass));
        restSchoolClass.setSchoolClassName("SchoolClass03");
        SecuredStudentSchoolClassManager instance = new SecuredStudentSchoolClassManager();
        Boolean result = instance.setActiveSchoolClass(sc, restSchoolClass);
        assertEquals(true, result);
        PersistentHasRole hr=null;
        try {
            hr = HasRoleUtilManager.getCurrentHasRole("user02", RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredStudentSchoolClassManagerIT.class.getName()).log(Level.SEVERE, null, ex);
            fail("Setting the active school threw an error.");
        }
        if (hr==null || hr.getPersistentHasRolePK().getUserID() != 3
                || hr.getSchoolGroup().getSchoolGroupID() != 5
                || UserManager.findByUserName("user02").getUserID() != 9) {
            fail("The test case is a prototype.");
        }
    }

    /**
     * Test of removeStudentFromSchoolClass method, of class
     * SecuredStudentSchoolClassManager.
     */
    @Test
    public void testRemoveStudentFromSchoolClass() {
        System.out.println("removeStudentFromSchoolClass");
        SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);
        RestSchoolClass restSchoolClass = null;
        SecuredStudentSchoolClassManager instance = new SecuredStudentSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.removeStudentFromSchoolClass(sc, restSchoolClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of registerStudentForSchoolClass method, of class
     * SecuredStudentSchoolClassManager.
     */
    @Test
    public void testRegisterStudentForSchoolClass() {
        System.out.println("registerStudentForSchoolClass");
        SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);
        RestSchoolClass restSchoolClass = null;
        SecuredStudentSchoolClassManager instance = new SecuredStudentSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.registerStudentForSchoolClass(sc, restSchoolClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
