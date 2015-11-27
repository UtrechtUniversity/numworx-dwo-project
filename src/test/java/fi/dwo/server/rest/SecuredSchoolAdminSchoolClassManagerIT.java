/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSingleSchoolStudent;
import fi.dwo.commons.dom.entities.DomTeacher;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceClassType;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.rest.entities.RestRemoveTeacherFromSchoolClass;
import fi.dwo.commons.rest.entities.RestSchoolClass;
import fi.dwo.commons.rest.entities.RestSingleSchoolStudent;
import fi.dwo.commons.rest.entities.RestSubmitTeacherToSchoolClass;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
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
     *
     * Tests for the number of SchoolClasses in a school.
     */
    @Test
    public void testGetSchoolClasses() {
        System.out.println("getSchoolClasses");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        List<PersistentSchoolClass> expResult;
        List<DomSchoolClass> result = instance.getSchoolClasses(sc);
        //fetch classes
        expResult = SchoolClassManager.findEntities(SchoolManager.findEntity(3L));
        assertEquals(expResult.size(), result.size());
    }

    /**
     * Test of getTeachersInSchool method, of class
     * SecuredSchoolAdminSchoolClassManager. Tests for the number of teachers in a
     * known school.
     */
    @Test
    public void testGetTeachersInSchool() {
        System.out.println("getTeachersInSchool");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        List<DomTeacher> result = instance.getTeachersInSchool(sc);
        assertEquals("Number of teachers don't match.", 2, result.size());
    }

    /**
     * Test of GetTeachersInSchoolClass method, of class
     * SecuredSchoolAdminSchoolClassManager.
     *
     * Checks the number of teachers of a known class in a school.
     */
    @Test
    public void testGetTeachersInSchoolClass() {
        System.out.println("GetTeachersInSchoolClass");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        DomSchoolClass domSchoolClass = new DomSchoolClass();
        restSchoolClass.setDomSchoolClass(domSchoolClass);
        domSchoolClass.setId(MySQLPersistenceId.createPersistenceId(2, PersistenceClassType.PersistentSchoolClass));
        domSchoolClass.setSchoolClassName("SchoolClass02");
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        List<DomTeacher> result = instance.GetTeachersInSchoolClass(sc, restSchoolClass);
        assertEquals(2, result.size());
    }

    /**
     * Test of SubmitTeacherToSchoolClass method, of class
     * SecuredSchoolAdminSchoolClassManager.
     * 
     * Tests if a known teacher can be submitted to a known class of a proper school.
     */
    @Test
    public void testSubmitTeacherToSchoolClass() {
        System.out.println("SubmitTeacherToSchoolClass");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        DomSchoolClass domSchoolClass = new DomSchoolClass();
        domSchoolClass.setId(MySQLPersistenceId.createPersistenceId(1, PersistenceClassType.PersistentSchoolClass));
        domSchoolClass.setSchoolClassName("SchoolClass01");
        DomTeacher domTeacher = new DomTeacher();
        domTeacher.setId(MySQLPersistenceId.createPersistenceId(10L, PersistenceClassType.PersistentUser));
        domTeacher.setUsername("user03");
        domTeacher.setGivenName("User");
        domTeacher.setFamilyName("Lastname 03");
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        Boolean expResult = true;
        RestSubmitTeacherToSchoolClass msg = new RestSubmitTeacherToSchoolClass();
        msg.setSchoolClass(domSchoolClass);
        msg.setTeacher(domTeacher);
        Boolean result = instance.SubmitTeacherToSchoolClass(sc, msg);
        assertEquals(expResult, result);
        PersistentTeacherOfClassPK key = new PersistentTeacherOfClassPK();
        key.setUserID(10L);
        key.setClassID(01L);
        key.setSchoolGroupID(03L);
        PersistentTeacherOfClass newTeacher = TeacherOfClassManager.findEntity(key);
        assertNotEquals("New teacher could not be found.", newTeacher, null);
    }

    /**
     * Test of removeTeacherFromSchoolClass method, of class
     * SecuredSchoolAdminSchoolClassManager.
     * 
     * Tests if a known teacher can be removed from a school class.Tests for both
     * legal and illegal teacher removal.
     */
    @Test
    public void testRemoveTeacherFromSchoolClass() {
        System.out.println("removeTeacherFromSchoolClass");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01
        DomSchoolClass domSchoolClass = new DomSchoolClass();
        domSchoolClass.setId(MySQLPersistenceId.createPersistenceId(2, PersistenceClassType.PersistentSchoolClass));
        domSchoolClass.setSchoolClassName("SchoolClass02");
        DomTeacher domTeacher = new DomTeacher();
        domTeacher.setId(MySQLPersistenceId.createPersistenceId(10L, PersistenceClassType.PersistentUser));
        domTeacher.setUsername("user03");
        domTeacher.setGivenName("User");
        domTeacher.setFamilyName("Lastname 03");
        RestRemoveTeacherFromSchoolClass msg = new RestRemoveTeacherFromSchoolClass();
        msg.setSchoolClass(domSchoolClass);
        msg.setTeacher(domTeacher);
        
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        Boolean expResult = true;
        Boolean result = instance.removeTeacherFromSchoolClass(sc, msg);
        assertEquals(expResult, result);
        PersistentTeacherOfClassPK key = new PersistentTeacherOfClassPK();
        key.setUserID(10L);
        key.setClassID(02L);
        key.setSchoolGroupID(03L);
        PersistentTeacherOfClass newTeacher = TeacherOfClassManager.findEntity(key);
        assertEquals("New teacher was not deleted.", newTeacher, null);

        //fail next
        domSchoolClass.setId(MySQLPersistenceId.createPersistenceId(2, PersistenceClassType.PersistentSchoolClass));
        domSchoolClass.setSchoolClassName("SchoolClass03");
        domTeacher.setId(MySQLPersistenceId.createPersistenceId(10L, PersistenceClassType.PersistentUser));
        domTeacher.setUsername("user03");
        domTeacher.setGivenName("User");
        domTeacher.setFamilyName("Lastname 03");
        
        try {
            result = instance.removeTeacherFromSchoolClass(sc,msg);
            assertEquals("Teacher removed while this should not occur.", false, result);
        }
        catch (Dwo2RestException e) {
            //success
        }
    }

    /**
     * Test of SubmitSingleSchoolStudent method, of class
     * SecuredSchoolAdminSchoolClassManager. Tests if a single student student
     * can be added. Tests only for a proper request. 
     */
    @Test
    public void testSubmitSingleSchoolStudent() {
        System.out.println("SubmitSingleSchoolStudent");
        SecurityContext sc = new TestSecurityContext("user06", RoleType.SCHOOLADMIN);//school01

        RestSingleSchoolStudent rss = new RestSingleSchoolStudent();
        DomSingleSchoolStudent dss = new DomSingleSchoolStudent();
        rss.setDomSingleSchoolStudent(dss);
        dss.setUsername("singleschooluser");
        dss.setGivenName("a");
        dss.setInsertion("b");
        dss.setFamilyName("c");
        dss.setEmail("a@b.c");
        dss.setPassword("pwd");
        SecuredSchoolAdminSchoolClassManager instance = new SecuredSchoolAdminSchoolClassManager();
        Boolean result = instance.SubmitSingleSchoolStudent(sc, rss);
        assertEquals("Operation failed to be true.", true, result);

        //fetch user and hasrole and class if given?
        PersistentUser user = UserManager.findByUserName(dss.getUsername());
        assertEquals("Given name not as expected.", dss.getGivenName(), user.getFirstname());
        assertEquals("Insertion not as expected.", dss.getInsertion(), user.getMiddlename());
        assertEquals("Familyname not as expected.", dss.getFamilyName(), user.getLastname());
        assertEquals("Email not as expected.", dss.getEmail(), user.getEmail());
        assertEquals("Password not as expected.", dss.getPassword(), user.getPasswd());
        assertEquals("Did not creat a single schoolstudent.", user.isSingleSchoolAccount(), true);
        try {
            //check for hasRole
            PersistentHasRole hr = HasRoleUtilManager.getHasRoleInSchool(user, (PersistentSchool) SchoolManager.findEntity(3L), RoleType.STUDENT);
        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(PublicUserManagerIT.class.getName()).log(Level.SEVERE, null, ex);
            fail("Could not find created user's hasRole");
        }
    }
}
