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
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class CourseManagerPIT {
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
		assertEquals(13333L, list.get(0).longValue());
		
	}
	
	@Test
	public void testFindAllProfile() throws Exception {
	  Long profileID = 1L;
	  Long schoolID = null;
      List<PersistentCourse> list = CourseManager.findEntities(profileID, schoolID );
      assertEquals(12, list.size());
      PersistentCourse c = list.get(0);
      c.setNotVisible(true);
      CourseManager.edit(c);
      List<PersistentCourse> list2 = CourseManager.findVisibleEntities(profileID);
      assertEquals(9, list2.size());
	}

}
