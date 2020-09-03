/**
 * Copyrighted Sep 22, 2017
 */
package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import nl.uu.fi.dwo.rest.util.DwoDateUtilities;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Gert van der Plas
 */
public class ClassManagerPIT {

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

    public ClassManagerPIT() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
    }

    /**
     * Test of create method, of class CourseInClassManager.
     */
    @Test
    public void testExport() {
          PersistentSchool school = SchoolManager.findEntity(3L);
          PersistentDwoProfile profile = DwoProfileManager.findEntity(1L);
          List<PersistentCourse> cc = CourseManager.findExportsOf(school, profile);
          assertEquals("size", 1, cc.size());
          assertTrue("export", cc.get(0).getExport().booleanValue());
    }

    /**
     * Test of findEntity method, of class CourseInClassManager.
     */
    @Test
    public void testFindEntity() {
        System.out.println("findEntity");
        Long id = 1L;
        PersistentCourse result = CourseManager.findEntity(id);
        assertEquals(result.getCourseID(), id);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of edit method, of class CourseInClassManager.
     */
    @Test
    public void testEdit() {
        System.out.println("edit");
        Long id = 1L;
        PersistentCourse cc = CourseManager.findEntity(id);
        CourseManager.edit(cc);
        PersistentClassCourse result = ClassCourseManager.findEntity(id);
     }


    /**
     * Test of destroy method, of class CourseInClassManager.
     */
    @Test
    public void testDestroy() throws Exception {
        System.out.println("destroy");
        Long id = 1L;
        ClassCourseManager.destroy(id);
        final PersistentClassCourse cc = ClassCourseManager.findEntity(id);
        assertNull("Object exists where as it should be destroyed.", cc);
    }

    
    /**
     * Test validation of PersistentSchoolClass
     */
    @Test
    public void testValidator() throws Exception {
    	PersistentSchoolClass sc = new PersistentSchoolClass(null, 1L, "");
    	try {
			SchoolClassManager.create(sc);
			fail("should fail");
		} catch (Exception e) {
			//e.printStackTrace();
		}
    }
    @Test
    public void testUpdateValidation() throws Exception {
    	PersistentSchoolClass sc = SchoolClassManager.findEntity(1L);
    	sc.setClass1("");
    	try {
    	   	SchoolClassManager.edit(sc);
			fail("should fail");
		} catch (Exception e) {
			//e.printStackTrace();
		}
    }
}
