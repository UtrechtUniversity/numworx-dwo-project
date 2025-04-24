package fi.dwo.server.PersistentDataManagers.actions;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;

public class MySQLCourseActionsTest {

  static DatabaseManager dbInstance = null;

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

  @Test
  public void testUpdate() {
	    Long courseid = Long.valueOf(3);
	    PersistentCourse pc = CourseManager.findEntity(courseid);
	    DomCourseFull full = pc.buildDomCourseFull();
	    MySQLCourseActions.update(pc, full);
	    PersistentCourse pc2 = CourseManager.findEntity(courseid);
	    assertEquals(full.getName(), pc2.getName());	    
  }

  @Test
  public void testAdd() {
    DomCourseFull full = new DomCourseFull();
    full.setName("name of full");
    full.setDwoProfileId(PersistentDwoProfile.buildPersistenceId(1L));
    full.setDescription("no description");
    full.setNotVisible(false);
    
    DomCourseFull result = MySQLCourseActions.add(full);
    assertEquals(full.getName(), result.getName());
  }

  @Test
  public void testRemove3() {
    Long courseid = Long.valueOf(3);
    PersistentCourse pc = CourseManager.findEntity(courseid);
    MySQLCourseActions.remove(pc);
    pc = CourseManager.findEntity(courseid);
    assertNull(pc);
  }

  @Test
  public void testRemove1() {
    Long courseid = Long.valueOf(1);
    PersistentCourse pc = CourseManager.findEntity(courseid);
    MySQLCourseActions.remove(pc);
    pc = CourseManager.findEntity(courseid);
    assertNull(pc);
  }

}
