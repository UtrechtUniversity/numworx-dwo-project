/**
 * Copyrighted Sep 22, 2017
 */
package fi.dwo.server.PersistentDataManagers.core;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import nl.uu.fi.dwo.rest.util.DwoDateUtilities;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.Date;
import javax.persistence.EntityNotFoundException;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Gert van der Plas
 */
public class ClassCourseManagerPIT {

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

    public ClassCourseManagerPIT() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
    }

    /**
     * Test of create method, of class CourseInClassManager.
     */
    @Test
    public void testCreate() {

        //recreate with double class/course id
        try {
            PersistentClassCourse cc = PersistentClassCourse.buildEmptyPersistentClassCourse();
            cc.setClassID(2);
            cc.setCourseID(6);
            cc.setDwoProfileID(1L);
            ClassCourseManager.create(cc);
            fail("Creating double copy should not work."); //unless nosql
        } catch (Exception e) {
            //success
        }
        //create
            PersistentClassCourse cc = PersistentClassCourse.buildEmptyPersistentClassCourse();
            cc.setClassID(1);
            cc.setCourseID(7);
            cc.setDwoProfileID(1L);
            ClassCourseManager.create(cc);
    }

    /**
     * Test of findEntity method, of class CourseInClassManager.
     */
    @Test
    public void testFindEntity() {
        System.out.println("findEntity");
        Long id = 1L;
        PersistentClassCourse result = ClassCourseManager.findEntity(id);
        assertEquals(result.getClassCourseID(), id.longValue());
        assertEquals(result.getClassID(), 1);
        assertEquals(result.getCourseID().longValue(), 1);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of edit method, of class CourseInClassManager.
     */
    @Test
    public void testEdit() {
        System.out.println("edit");
        Long id = 1L;
        PersistentClassCourse cc = ClassCourseManager.findEntity(id);
        final Date first = DwoDateUtilities.getStartOfDay();
        final Date last = DwoDateUtilities.getEndOfDay();
        cc.setNotBefore(first);
        cc.setNotAfter(last);
        cc.setType(1);
        cc.setViewState(ViewState.studentsAndTeachers);
        ClassCourseManager.edit(cc);
        PersistentClassCourse result = ClassCourseManager.findEntity(id);
        assertEquals(result.getType(), Integer.valueOf(1));
        assertEquals(result.getViewState(), ViewState.studentsAndTeachers);
        assertEquals(result.getNotBefore().toString(), cc.getNotBefore().toString());
        assertEquals(result.getNotAfter().toString(), last.toString());
    }

    /**
     * Test of edit method, of class CourseInClassManager.
     */
    @Test
    public void testEditViewState() {
        System.out.println("edit");
        Long id = 1L;
        PersistentClassCourse cc = ClassCourseManager.findEntity(id);
        final Date first = DwoDateUtilities.getStartOfDay();
        final Date last = DwoDateUtilities.getEndOfDay();
        cc.setViewState(ViewState.studentsAndTeachers);
        ClassCourseManager.edit(cc);
        PersistentClassCourse result = ClassCourseManager.findEntity(id);
        assertEquals(result.getType(), cc.getType());
        assertEquals(result.getViewState(), ViewState.studentsAndTeachers);
        assertEquals(result.getNotBefore().toString(), cc.getNotBefore().toString());
        assertEquals(result.getNotAfter().toString(), cc.getNotAfter().toString());
    }

    /**
     * Test of edit method, of class CourseInClassManager.
     */
    @Test
    public void testEditAccessKey() {
        System.out.println("edit");
        Long id = 1L;
        PersistentClassCourse cc = ClassCourseManager.findEntity(id);
        cc.setAccessKey("secret");
        
        ClassCourseManager.edit(cc);
        PersistentClassCourse result = ClassCourseManager.findEntity(id);
        assertEquals(result.getType(), cc.getType());
        assertEquals(result.getViewState(), ViewState.studentsAndTeachers);
        assertEquals(result.getNotBefore().toString(), cc.getNotBefore().toString());
        assertEquals(result.getNotAfter().toString(), cc.getNotAfter().toString());
        assertEquals(result.getAccessKey(), cc.getAccessKey());
    }
    
    
    
    /**
     * Test of edit method, of class CourseInClassManager.
     */
    @Test
    public void testEditType() {
        System.out.println("edit");
        Long id = 1L;
        PersistentClassCourse cc = ClassCourseManager.findEntity(id);
        final Date first = DwoDateUtilities.getStartOfDay();
        final Date last = DwoDateUtilities.getEndOfDay();
        cc.setType(1);
        ClassCourseManager.edit(cc);
        PersistentClassCourse result = ClassCourseManager.findEntity(id);
        assertEquals(result.getType().longValue(), 1);
        assertEquals(result.getViewState(), cc.getViewState());
        assertEquals(result.getNotBefore().toString(), cc.getNotBefore().toString());
        assertEquals(result.getNotAfter().toString(), cc.getNotAfter().toString());
    }

    /**
     * Test of edit method, of class CourseInClassManager.
     */
    @Test
    public void testEditTo() {
        System.out.println("edit");
        Long id = 1L;
        final PersistentClassCourse cc = ClassCourseManager.findEntity(id);
        final Date last = DwoDateUtilities.getEndOfDay();
        cc.setNotAfter(last);
        ClassCourseManager.edit(cc);
        PersistentClassCourse result = ClassCourseManager.findEntity(id);
        assertEquals(result.getType(), cc.getType());
        assertEquals(result.getViewState(), cc.getViewState());
        assertEquals(result.getNotBefore().toString(), cc.getNotBefore().toString());
        assertEquals(result.getNotAfter().toString(), cc.getNotAfter().toString());
    }

    /**
     * Test of edit method, of class CourseInClassManager.
     */
    @Test
    public void testEditFrom() {
        System.out.println("edit");
        Long id = 1L;
        final PersistentClassCourse cc = ClassCourseManager.findEntity(id);
        final Date first = DwoDateUtilities.getStartOfDay();
        cc.setNotBefore(first);
        ClassCourseManager.edit(cc);
        PersistentClassCourse result = ClassCourseManager.findEntity(id);
        assertEquals(result.getType(), cc.getType());
        assertEquals(result.getViewState(), cc.getViewState());
        assertEquals(result.getNotAfter().toString(), cc.getNotAfter().toString());
        assertEquals(result.getNotBefore().toString(), cc.getNotBefore().toString());
    }

    /**
     * Test of destroy method, of class CourseInClassManager.
     */
    @Test
    public void testDestroy() {
        System.out.println("destroy");
        Long id = 1L;
        try {
            ClassCourseManager.destroy(id);
            final PersistentClassCourse cc = ClassCourseManager.findEntity(id);
            assertEquals("Object exists where as it should be destroyed.", cc, null);
        } catch (Exception e) {
            if(!(e instanceof EntityNotFoundException)) fail("Classcourse destroy has curious exception.");
        }
    }

    @Test
    public void testUncacheResultsFromCourse() {
    	PersistentClassCourse cc = ClassCourseManager.findEntity(1L);
    	cc.setResults(Boolean.TRUE);
    	cc = ClassCourseManager.edit(cc);
    	PersistentCourse course = CourseManager.findEntity(cc.getCourseID());
   
    	// uncache
    	ClassCourseManager.uncacheResults(course);
    	
    	PersistentClassCourse ccc = ClassCourseManager.findEntity(1L);
    	assertNull(ccc.hasResults());
    }
    
    @Test
    public void testUncacheResultsFromClass() {
    	PersistentClassCourse cc = ClassCourseManager.findEntity(1L);
    	cc.setResults(Boolean.TRUE);
    	cc = ClassCourseManager.edit(cc);
    	PersistentSchoolClass sc = SchoolClassManager.findEntity(cc.getClassID());
   
    	// uncache
    	ClassCourseManager.uncacheResults(sc);
    	
    	PersistentClassCourse ccc = ClassCourseManager.findEntity(1L);
    	assertNull(ccc.hasResults());
    	// what about version?
    }
  
}
