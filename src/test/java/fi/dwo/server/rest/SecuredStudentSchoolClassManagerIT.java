/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolClass4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import nl.uu.fi.dwo.rest.entities.RestNewSchoolClass4Student;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import java.util.List;
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
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
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
     * Test of getTeachersSchoolClasses method, of class
     * SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testGetStudentsSchoolClasses() {
        System.out.println("getStudentsSchoolClasses");
        SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);//school01
        SecuredStudentSchoolClassManager instance = new SecuredStudentSchoolClassManager();
        List<DomSchoolClass> result = instance.getStudentsSchoolClasses(sc);
        assertEquals(2, result.size());
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
        DomSchoolClass domSchoolClass = new DomSchoolClass();
        restSchoolClass.setDomSchoolClass(domSchoolClass);
        //put in schoolgroup 5 , class 3 for user 9 (user02)
        domSchoolClass.setId(PersistentSchoolClass.buildPersistenceId(2L));
        domSchoolClass.setSchoolClassName("SchoolClass02");
        SecuredStudentSchoolClassManager instance = new SecuredStudentSchoolClassManager();
        Boolean result = instance.setActiveSchoolClass(sc, restSchoolClass);
        assertEquals(true, result);
        PersistentHasRole hr = null;
        try {
            hr = HasRoleUtilManager.getCurrentHasRole("user02", RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredStudentSchoolClassManagerIT.class.getName()).log(Level.SEVERE, "", ex);
            fail("Setting the active school threw an error.");
        }
        if (hr == null || (long) hr.getPersistentHasRolePK().getUserID() != (long) UserManager.findByUserName("user02").getId()
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
        SecurityContext sc = new TestSecurityContext("user05", RoleType.STUDENT);
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        DomSchoolClass domSchoolClass;
        PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school02");
        PersistentSchoolClass pSchoolClass = SchoolClassManager.findEntity("SchoolClass03", pSchool);
        domSchoolClass = pSchoolClass.buildDomSchoolClass();
        restSchoolClass.setDomSchoolClass(domSchoolClass);

        //test school with student rights to register a schoolclass    
        SecuredStudentSchoolClassManager instance = new SecuredStudentSchoolClassManager();
        Boolean result = instance.removeStudentFromSchoolClass(sc, restSchoolClass);
        assertEquals("Removing the student from the schoolclass failed.", true, result);
        PersistentStudentOfClassPK socKey = new PersistentStudentOfClassPK();
        socKey.setClassID(3L);
        socKey.setSchoolGroupID(5L);
        socKey.setUserID(12L);
        try {
            PersistentStudentOfClass soc = StudentOfClassManager.findEntity(socKey);
            if (soc == null) {
                return;
            }
            fail("StudentOfClass was not removed!");
        }
        catch (Exception ex) {
            // success
        }

        //test school with no student rights to register a schoolclass
        sc = new TestSecurityContext("user02", RoleType.STUDENT);
        pSchool = SchoolManager.findBySchoolLogin("school01");
        pSchoolClass = SchoolClassManager.findEntity("SchoolClass02", pSchool);
        domSchoolClass = pSchoolClass.buildDomSchoolClass();
        restSchoolClass.setDomSchoolClass(domSchoolClass);

        result = instance.removeStudentFromSchoolClass(sc, restSchoolClass);
        assertEquals("Removing the student from the schoolclass failed.", true, result);
        socKey = new PersistentStudentOfClassPK();
        socKey.setClassID(1L);
        socKey.setSchoolGroupID(2L);
        socKey.setUserID(9L);
        try {
            PersistentStudentOfClass soc = StudentOfClassManager.findEntity(socKey);
            if (soc == null) {
                return;
            }
            fail("StudentOfClass was not removed!");
        }
        catch (Exception ex) {
            // success
        }
    }

    /**
     * Test of registerStudentForSchoolClass method, of class
     * SecuredStudentSchoolClassManager.
     */
    @Test
    public void testRegisterStudentForSchoolClass() {
        System.out.println("registerStudentForSchoolClass");
        SecurityContext sc = new TestSecurityContext("user05", RoleType.STUDENT);
        RestNewSchoolClass4Student restSchoolClass = new RestNewSchoolClass4Student();
        DomNewSchoolClass4Student domSchoolClass = new DomNewSchoolClass4Student();
        domSchoolClass.setId(PersistentSchoolClass.buildPersistenceId(4L));
        domSchoolClass.setSchoolClassName("SchoolClass04");
        domSchoolClass.setRegistrationKey("key");
        restSchoolClass.setDomNewSchoolClass4Student(domSchoolClass);
        
        //test school with student rights to register a schoolclass
        SecuredStudentSchoolClassManager instance = new SecuredStudentSchoolClassManager();
        try {
            Boolean result = instance.registerStudentForSchoolClass(sc, restSchoolClass);
            assertEquals(true, result);
        }
        catch (Dwo2RestException e) {
            fail("StudentOfClass not registered!, exception:" + e.getMessage());
        }
        PersistentStudentOfClassPK socKey = new PersistentStudentOfClassPK();
        socKey.setClassID(4L);
        socKey.setSchoolGroupID(5L);
        socKey.setUserID(12L);
        try {
            PersistentStudentOfClass soc = StudentOfClassManager.findEntity(socKey);
            if (soc == null) {
                fail("StudentOfClass not registered!");
            }
        }
        catch (Exception ex) {
            fail("StudentOfClass not registered!, exception:" + ex.getMessage());
        }
        //test school with no student rights to register a schoolclas
        sc = new TestSecurityContext("user02", RoleType.STUDENT);
        domSchoolClass.setId(PersistentSchoolClass.buildPersistenceId(2L));
        domSchoolClass.setSchoolClassName("SchoolClass02");
        domSchoolClass.setRegistrationKey("key");
        restSchoolClass.setDomNewSchoolClass4Student(domSchoolClass);
        try {
            Boolean result = instance.registerStudentForSchoolClass(sc, restSchoolClass);
            assertEquals("StudentOfClass registered, should fail!",false, result);
        }
        catch (Dwo2RestException e) {
            //success
        }
        socKey.setClassID(2L);
        socKey.setSchoolGroupID(5L);
        socKey.setUserID(9L);
        try {
            PersistentStudentOfClass soc = StudentOfClassManager.findEntity(socKey);
            if (soc!= null) {
                // success
                fail("StudentOfClass registered, should fail!");
            }
        }
        catch (Exception ex) {
            //success
        }
        
    }

    /**
     * Test of getSchoolsClasses method, of class
     * SecuredStudentSchoolClassManager.
     */
    @Test
    public void testGetSchoolsClasses() {
        System.out.println("getSchoolsClasses");
        System.out.println("getStudentsSchoolClasses");
        SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);//school01
        SecuredStudentSchoolClassManager instance = new SecuredStudentSchoolClassManager();
        List<DomSchoolClass> result = instance.getSchoolsClasses(sc);
        assertEquals(2, result.size());
    }
}
