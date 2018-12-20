package fi.dwo.server.rest;

import static org.junit.Assert.*;

import java.util.List;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.entities.RestSchool;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class SystemManagerIT {
  private static final Logger LOG = Logger.getLogger(PublicCourseManagerIT.class.getName());

  private static DatabaseManager instance = null;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
      Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
      DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
      instance = new DatabaseManager();
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
      DwoEmfFactory.setDefaultEntityManagerFactory();
      instance = null;
  }

  SystemManager manager;
  @Before
  public void setUp() throws Exception {
      instance.IntializeTestDatabase();
      manager = new SystemManager();
  }

  @After
  public void tearDown() throws Exception {
      instance.ClearDatabase();
  }


  @Test
  public void testGetSchool() throws Dwo2Exception {
    RestSchool rest = new RestSchool();
    rest.setDomSchool(new DomSchool());
    rest.getDomSchool().setSchoolName("dwo");
    DomSchoolFull school = manager.getSchool(rest);
    assertEquals("school 0", (Long)0L, MySQLPersistenceId.getNativeId(school));
  }

  @Test 
  public void testGetListSchoolClass() throws Dwo2Exception {
    RestSchool rest = new RestSchool();
    rest.setDomSchool(new DomSchool());
    rest.getDomSchool().setId(PersistentSchool.buildPersistenceId(2L));
    List<DomSchoolClass> list = manager.getListSchoolClass(rest);
    assertEquals(0, list.size());
    rest.getDomSchool().setId(PersistentSchool.buildPersistenceId(3L));
    list = manager.getListSchoolClass(rest);
    assertEquals(2, list.size());
  }

  @Test @Ignore
  public void testRequestSamlToken() {
    fail("Not yet implemented");
  }

  @Test
  public void testSuggestion() {
    String input = "user02";
    String suggestion = manager.suggestion(input);
    assertEquals("suggest", input + 1, suggestion);
    
    input = "hoplada";
    suggestion = manager.suggestion(input);
    assertEquals("notfound", input, suggestion);
  
  }

}
