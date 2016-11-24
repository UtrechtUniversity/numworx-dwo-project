/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.SecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Gert van der Plas
 */
public class SecuredTeacherResultsManagerIT {

    private static final Logger LOG = Logger.getLogger(SecuredTeacherResultsManagerIT.class.getName());

    static DatabaseManager dbInstance = null;

    public SecuredTeacherResultsManagerIT() {
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
    public void testGetTeachersResults() {
        System.out.println("testGetTeachersResults");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        SecuredTeacherResultsManager instance = new SecuredTeacherResultsManager();
        RestDwoProfile restProfile = new RestDwoProfile();
        DomDwoProfile domProfile;
        
        PersistentDwoProfile pProfile = new PersistentDwoProfile();
        pProfile.setDwoProfileID(1L);
        pProfile.setDwoProfileName("testprofile01");
        pProfile.setDwoProfileRights("_");
        pProfile.setDwoProfileDescription("Test dwoProfileDescription02iption");
        pProfile.setDwoProfileText("Test dwoProfileText01");
        domProfile = pProfile.buildDomDwoProfileFull();
        
        DomContext restContext = new DomContext();
        restProfile.setRestContext(restContext);
        restProfile.setDomDwoProfile(domProfile);
        
        DomHasRole domHasRole = null;
        PersistentUser pUser = UserManager.findByUserName("user07");
        PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");

        try {
            PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);
            domHasRole = pHasRole.buildDomHasRole();
        } catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredTeacherResultsManagerIT.class.getName()).log(Level.SEVERE, null, ex);
            fail("Could not find teacher's hasRole");
        }
        
        restContext.setDomHasRole(domHasRole);
        
        DomResultsPerTeacher result = instance.getTeachersResults(sc,restProfile);
        
        assertEquals(result.getTeacher().getId().getIdString(),"MYSQL;PersistentUser;00000000000000000014");
        //requires  students with id 9,10 and 11 in class with id
        assertEquals(3, result.getStudentsOfClasses().size());
        assertEquals(true, result.getStudentsOfClasses().containsKey((PersistenceId) new MySQLPersistenceId("MYSQL;PersistentStudentOfClass;00000000000000000009;00000000000000000002;00000000000000000002")));
        assertEquals(true, result.getStudentsOfClasses().containsKey(new MySQLPersistenceId("MYSQL;PersistentStudentOfClass;00000000000000000010;00000000000000000002;00000000000000000002")));
        assertEquals(true, result.getStudentsOfClasses().containsKey(new MySQLPersistenceId("MYSQL;PersistentStudentOfClass;00000000000000000011;00000000000000000002;00000000000000000002")));
        assertEquals(3, result.getStudents().size());
        assertEquals(true, result.getStudents().containsKey(new MySQLPersistenceId("MYSQL;PersistentUser;00000000000000000009")));
        assertEquals(true, result.getStudents().containsKey(new MySQLPersistenceId("MYSQL;PersistentUser;00000000000000000010")));
        assertEquals(true, result.getStudents().containsKey(new MySQLPersistenceId("MYSQL;PersistentUser;00000000000000000011")));
        //requires schoolclass with id 2
        assertEquals(1, result.getSchoolClasses().size());
        assertEquals(true, result.getSchoolClasses().containsKey(new MySQLPersistenceId("MYSQL;PersistentSchoolClass;00000000000000000002")));
        //requires courses with id 2,5,6
        assertEquals(3, result.getCourses().size());
        assertEquals(true, result.getCourses().containsKey(new MySQLPersistenceId("MYSQL;PersistentCourse;00000000000000000005")));
        assertEquals(true, result.getCourses().containsKey(new MySQLPersistenceId("MYSQL;PersistentCourse;00000000000000000006")));
        assertEquals(true, result.getCourses().containsKey(new MySQLPersistenceId("MYSQL;PersistentCourse;00000000000000000002 ")));
        //requires classCourses with id 2.4
        assertEquals(2, result.getClassCourses().size());
        assertEquals(true, result.getClassCourses().containsKey(new MySQLPersistenceId("MYSQL;PersistentClassCourse;00000000000000000002 ")));
        assertEquals(true, result.getClassCourses().containsKey(new MySQLPersistenceId("MYSQL;PersistentClassCourse;00000000000000000004")));
        //requires scoContexts with id 2,1
        assertEquals(2, result.getScoContexts().size());
        assertEquals(true, result.getScoContexts().containsKey(new MySQLPersistenceId("MYSQL;PersistentScoContext;00000000000000000002")));
        assertEquals(true, result.getScoContexts().containsKey(new MySQLPersistenceId("MYSQL;PersistentScoContext;00000000000000000001 ")));
        //requires studentScoContexts with id 1,2
        assertEquals(2, result.getStudentScoContexts().size());
        assertEquals(true, result.getStudentScoContexts().containsKey(new MySQLPersistenceId("MYSQL;PersistentStudentScoContext;00000000000000000001 ")));
        assertEquals(true, result.getStudentScoContexts().containsKey(new MySQLPersistenceId("MYSQL;PersistentStudentScoContext;00000000000000000002")));
    }
}
