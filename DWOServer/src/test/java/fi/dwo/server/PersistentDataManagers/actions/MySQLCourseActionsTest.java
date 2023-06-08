package fi.dwo.server.PersistentDataManagers.actions;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;

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

  @Test @Ignore
  public void testUpdate() {
    fail("Not yet implemented");
  }

  @Test @Ignore
  public void testAdd() {
    fail("Not yet implemented");
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
