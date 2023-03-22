/** Copyrighted Jun 7, 2018 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.SchoolAdminTeacherDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer;
import static fi.dwo.server.PersistentDataManagers.actions.MySQLCourseActionsTest.dbInstance;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.List;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author plas0006
 */
public class MySQLTeacherActionsTest {
    
    public MySQLTeacherActionsTest() {
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
//
//    /**
//     * Test of addStudentModel method, of class MySQLTeacherActions.
//     */
//    @Test
//    public void testAddStudentModel() throws Exception {
//        System.out.println("addStudentModel");
//        TeacherDomainAuthorizer.Context context = null;
//        PersistentStudentModelContext model = null;
//        MySQLTeacherActions instance = new MySQLTeacherActions();
//        PersistentStudentModelContext expResult = null;
//        PersistentStudentModelContext result = instance.addStudentModel(context, model);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getStudentModels method, of class MySQLTeacherActions.
//     */
//    @Test
//    public void testGetStudentModels() throws Exception {
//        System.out.println("getStudentModels");
//        TeacherDomainAuthorizer.Context context = null;
//        MySQLTeacherActions instance = new MySQLTeacherActions();
//        List<PersistentStudentModelContext> expResult = null;
//        List<PersistentStudentModelContext> result = instance.getStudentModels(context);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSchoolClasses method, of class MySQLTeacherActions.
//     */
//    @Test
//    public void testGetSchoolClasses() throws Exception {
//        System.out.println("getSchoolClasses");
//        TeacherDomainAuthorizer.Context context = null;
//        MySQLTeacherActions instance = new MySQLTeacherActions();
//        List<PersistentSchoolClass> expResult = null;
//        List<PersistentSchoolClass> result = instance.getSchoolClasses(context);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getTeachersStudents method, of class MySQLTeacherActions.
//     */
//    @Test
//    public void testGetTeachersStudents() throws Exception {
//        System.out.println("getTeachersStudents");
//        TeacherDomainAuthorizer.Context context = null;
//        MySQLTeacherActions instance = new MySQLTeacherActions();
//        List<PersistentUser> expResult = null;
//        List<PersistentUser> result = instance.getTeachersStudents(context);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addStudent method, of class MySQLTeacherActions.
//     */
//    @Test
//    public void testAddStudent() throws Exception {
//        System.out.println("addStudent");
//        TeacherDomainAuthorizer.Context context = null;
//        PersistentSchoolClass sc = null;
//        PersistentHasRole shr = null;
//        MySQLTeacherActions instance = new MySQLTeacherActions();
//        instance.addStudent(context, sc, shr);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of getSharedTeacherClasses method, of class MySQLTeacherActions.
     */
    @Test
    public void testGetSharedTeacherClasses() throws Exception {
        System.out.println("getSharedTeacherClasses");
        
        PersistentUser otherTeacher =  UserManager.findEntity(14L);
        UserDomainAuthorizer.Context userCtx = new UserDomainAuthorizer.Context(new AnonDomainAuthorizer.Context());
        PersistentUser teacher = UserManager.findEntity(10L);
        userCtx.getUserCtx().user = teacher;
        userCtx.getUserCtx().schoolGroup = SchoolGroupManager.findEntity(3L);
        TeacherDomainAuthorizer.Context context = new TeacherDomainAuthorizer.Context(new SchoolAdminTeacherDomainAuthorizer.Context(userCtx));
        
        MySQLTeacherActions instance = new MySQLTeacherActions();
        List<PersistenceId> result = instance.getSharedTeacherClasses(context, otherTeacher);
         assertEquals("Should find one class.", result.size(), 1);
         assertEquals("Should find class with id=2.", result.get(0).getIdString(), PersistentSchoolClass.buildPersistenceId(Long.valueOf(2)).getIdString());
    }

    /**
     * Test of getTeachersClassesOfStudent method, of class MySQLTeacherActions.
     */
    @Test
    public void testGetTeachersStudentClasses() throws Exception {
        System.out.println("getTeachersStudentClasses");
        PersistentSchoolGroup studentSg = SchoolGroupManager.findEntity(2L);
        PersistentUser student = UserManager.findEntity(9L);
        UserDomainAuthorizer.Context userCtx = new UserDomainAuthorizer.Context(new AnonDomainAuthorizer.Context());
        PersistentUser teacher = UserManager.findEntity(10L);
        userCtx.getUserCtx().user = teacher;
        userCtx.getUserCtx().schoolGroup = SchoolGroupManager.findEntity(3L);
        TeacherDomainAuthorizer.Context context = new TeacherDomainAuthorizer.Context(new SchoolAdminTeacherDomainAuthorizer.Context(userCtx));
        
        MySQLTeacherActions instance = new MySQLTeacherActions();
        List<PersistenceId> result = instance.getTeachersClassesOfStudent(context, studentSg, student);
         assertEquals("Should find one class.", result.size(), 1);
         assertEquals("Should find class with id=2.", result.get(0).getIdString(), PersistentSchoolClass.buildPersistenceId(Long.valueOf(2)).getIdString());
    }
    
}
