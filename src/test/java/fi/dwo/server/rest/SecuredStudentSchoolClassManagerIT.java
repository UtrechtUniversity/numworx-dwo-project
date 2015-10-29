/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceClassType;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.rest.entities.RestSchoolClass;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
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

    static DatabaseManager dbInstance = null;

    public SecuredStudentSchoolClassManagerIT() {
    }

    @BeforeClass
    public static void setUpClass() {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        dbInstance = new DatabaseManager();
    }

    @AfterClass
    public static void tearDownClass() {
        dbInstance = new DatabaseManager();
        DwoEmfFactory.setDefaultEntityManagerFactory();
    }

    @Before
    public void setUp() {
        dbInstance.IntializeTestDatabase();
    }

    @After
    public void tearDown() {
        dbInstance.ClearDatabase();
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
        restSchoolClass.setId(MySQLPersistenceId.createPersistenceId(2, PersistenceClassType.PersistentSchoolClass));
        restSchoolClass.setSchoolClassName("SchoolClass02");
        SecuredStudentSchoolClassManager instance = new SecuredStudentSchoolClassManager();
        Boolean result = instance.setActiveSchoolClass(sc, restSchoolClass);
        assertEquals(true, result);
        PersistentHasRole hr = null;
        try {
            hr = HasRoleUtilManager.getCurrentHasRole("user02", RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredStudentSchoolClassManagerIT.class.getName()).log(Level.SEVERE, null, ex);
            fail("Setting the active school threw an error.");
        }
        if (hr == null || (long) hr.getPersistentHasRolePK().getUserID() != (long) UserManager.findByUserName("user02").getUserID()
                || hr.getSchoolGroup().getSchoolGroupID() != 2L || hr.getClassID() != 2L) {
            fail("Failed setting active login.");
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
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setId(MySQLPersistenceId.createPersistenceId(1, PersistenceClassType.PersistentSchoolClass));
        restSchoolClass.setSchoolClassName("SchoolClass02");
        SecuredStudentSchoolClassManager instance = new SecuredStudentSchoolClassManager();
        Boolean result = instance.removeStudentFromSchoolClass(sc, restSchoolClass);
        assertEquals("Removing the student from the schoolclass failed.", true, result);
        PersistentStudentOfClassPK socKey =  new PersistentStudentOfClassPK();
        socKey.setClassID(1L);
        socKey.setSchoolGroupID(2L);
        socKey.setUserID(9L);
        try{
            PersistentStudentOfClass soc = StudentOfClassManager.findEntity(socKey);
            if(soc==null) return;
        }catch(Exception ex){
            return;
        }
        fail("StudentOfClass was not removed!");
    }

    /**
     * Test of registerStudentForSchoolClass method, of class
     * SecuredStudentSchoolClassManager.
     */
    @Test
    public void testRegisterStudentForSchoolClass() {
        System.out.println("registerStudentForSchoolClass");
        SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setId(MySQLPersistenceId.createPersistenceId(1, PersistenceClassType.PersistentSchoolClass));
        restSchoolClass.setSchoolClassName("SchoolClass04");
        SecuredStudentSchoolClassManager instance = new SecuredStudentSchoolClassManager();
        Boolean result = instance.registerStudentForSchoolClass(sc, restSchoolClass);
        assertEquals(true, result);
        PersistentStudentOfClassPK socKey =  new PersistentStudentOfClassPK();
        socKey.setClassID(1L);
        socKey.setSchoolGroupID(2L);
        socKey.setUserID(9L);
        try{
            PersistentStudentOfClass soc = StudentOfClassManager.findEntity(socKey);
            if(soc==null) fail("StudentOfClass not registered!");
        }catch(Exception ex){
            fail("StudentOfClass not registered!, exception:"+ex.getMessage());
        }
    }

}
