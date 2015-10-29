/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.rest.entities.RestSchoolClass;
import fi.dwo.commons.rest.entities.RestSingleSchoolStudent;
import fi.dwo.commons.rest.entities.RestTeacher;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import static fi.dwo.server.rest.SecuredStudentSchoolClassManagerIT.dbInstance;
import fi.dwo.server.testutil.TestSecurityContext;
import java.util.List;
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
public class SecuredSchoolAdminSchoolClassManagerIT {

    private static final Logger LOG = Logger.getLogger(SecuredSchoolAdminSchoolClassManagerIT.class.getName());

    static DatabaseManager dbInstance = null;
    public SecuredSchoolAdminSchoolClassManagerIT() {
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
     * Test of getSchoolClasses method, of class
     * SecuredSchoolAdminSchoolClassManager.
     */
    @Test
    public void testGetSchoolClasses() {
        System.out.println("getSchoolClasses");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        List<PersistentSchoolClass> expResult;
        List<RestSchoolClass> result = instance.getSchoolClasses(sc);
        //fetch classes
        expResult = SchoolClassManager.findEntities(SchoolManager.findEntity(3L));
        assertEquals(expResult.size(), result.size());
    }

/**
 * Test of getTeachersInSchool method, of class
 * SecuredSchoolAdminSchoolClassManager.
 */
@Test
        public void testGetTeachersInSchool() {
        System.out.println("getTeachersInSchool");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        List<RestTeacher> result = instance.getTeachersInSchool(sc);
        assertNotEquals(0, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of GetTeachersInSchoolClass method, of class SecuredSchoolAdminSchoolClassManager.
     */
    @Test
        public void testGetTeachersInSchoolClass() {
        System.out.println("GetTeachersInSchoolClass");
        SecurityContext sc = null;
        RestSchoolClass restSchoolClass = null;
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        List<RestTeacher> expResult = null;
        List<RestTeacher> result = instance.GetTeachersInSchoolClass(sc, restSchoolClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of SubmitTeacherToSchoolClass method, of class SecuredSchoolAdminSchoolClassManager.
     */
    @Test
        public void testSubmitTeacherToSchoolClass() {
        System.out.println("SubmitTeacherToSchoolClass");
        SecurityContext sc = null;
        RestTeacher restTeacher = null;
        RestSchoolClass restSchoolClass = null;
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.SubmitTeacherToSchoolClass(sc, restTeacher, restSchoolClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeTeacherFromSchoolClass method, of class SecuredSchoolAdminSchoolClassManager.
     */
    @Test
        public void testRemoveTeacherFromSchoolClass() {
        System.out.println("removeTeacherFromSchoolClass");
        SecurityContext sc = null;
        RestSchoolClass restSchoolClass = null;
        RestTeacher restTeacher = null;
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.removeTeacherFromSchoolClass(sc, restSchoolClass, restTeacher);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of SubmitSingleSchoolStudent method, of class SecuredSchoolAdminSchoolClassManager.
     */
    @Test
        public void testSubmitSingleSchoolStudent() {
        System.out.println("SubmitSingleSchoolStudent");
        SecurityContext sc = null;
        RestSingleSchoolStudent nssStudent = null;
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        Boolean expResult = null;
        Boolean result = instance.SubmitSingleSchoolStudent(sc, nssStudent);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
