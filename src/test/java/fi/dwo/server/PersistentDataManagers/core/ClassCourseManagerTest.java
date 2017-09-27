/**
 * Copyrighted Sep 22, 2017
 */
package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.Date;
import java.util.List;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
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
public class ClassCourseManagerTest {
    
    PersistentClassCourse cicA = new PersistentClassCourse();
    PersistentClassCourse cicB = new PersistentClassCourse();

    static DatabaseManager instance = null;

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
    public ClassCourseManagerTest() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
    }
    
// 
//    /**
//     * Test of create method, of class CourseInClassManager.
//     */
//    @Test
//    public void testCreate() {
//        System.out.println("create");
//        PersistentClassCourse classCourse = null;
//        PersistentClassCourse expResult = null;
//        PersistentClassCourse result = ClassCourseManager.create(classCourse);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of findEntity method, of class CourseInClassManager.
//     */
//    @Test
//    public void testFindEntity() {
//        System.out.println("findEntity");
//        Long id = 1L;
//        PersistentClassCourse expResult = null;
//        PersistentClassCourse result = ClassCourseManager.findEntity(id);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    /**
//     * Test of edit method, of class CourseInClassManager.
//     */
//    @Test
//    public void testEdit() {
//        System.out.println("edit");
//        PersistentClassCourse classCourse = null;
//        ClassCourseManager.edit(classCourse);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of destroy method, of class CourseInClassManager.
//     */
//    @Test
//    public void testDestroy() {
//        System.out.println("destroy");
//        Long id = null;
//        ClassCourseManager.destroy(id);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    
}
