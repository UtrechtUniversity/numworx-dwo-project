package fi.dwo.server.PersistentDataManagers.core;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentCourseData;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class CourseDataManagerPIT {
    static DatabaseManager instance = null;

    @BeforeClass
    public static void setUpClass() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
    }

    @AfterClass
    public static void tearDownClass() {
        DwoEmfFactory.setDefaultEntityManagerFactory();
        instance = null;
    }

    PersistentSchool school;
    
	@Before
	public void setUp() throws Exception {
	      instance.IntializeTestDatabase();
	      school = SchoolManager.findEntity(3L);
	}

	@After
	public void tearDown() throws Exception {
        instance.ClearDatabase();
	}

	@Test
	public void testFindEntityIDs() {
		List<Long> list = CourseManager.findEntityIDs(school);
		assertEquals(1, list.size());
		Long id = list.get(0);
		assertEquals(13333L, id.longValue());
		PersistentCourseData data = CourseDataManager.findEntity(id);
		assertNotNull(data);
	}

	@Test
	public void testOmzettenCourse() throws Exception {
		List<PersistentCourse> courses = CourseManager.findEntities();
		CourseDataManager.destroy(13333L);
		for( PersistentCourse course : courses) {
			if (course.getDescription() != null) { // omzet criterium
				Long id = course.getCourseID();
				String description = course.getDescription();
				PersistentCourseData data = new PersistentCourseData();
				data.setCourseID(id);
				data.setDescription(description);
				data.setImageData(course.getImageData());
				CourseDataManager.create(data);
				course.setDescription(null);
				course.setImageData(null);
				if (course.getSequencenr() == null) {
					course.setSequencenr(Long.valueOf(Integer.MAX_VALUE));
				}
				course = CourseManager.edit(course);
			}
		}
		List<PersistentCourseData> datalist = CourseDataManager.findEntities();
		assertEquals(courses.size(), datalist.size());
	}
	
}
