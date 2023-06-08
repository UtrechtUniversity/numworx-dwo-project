package fi.dwo.server.PersistentDataManagers.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;

public class UserUtilManagerPIT {

  private static DatabaseManager instance;

  @Test
  public void test() {
    Long id = 1L;
    PersistentUser result = UserManager.findEntity(id);
    assertNotNull(result);
    UserUtilManager.deleteUser(result);
    
    assertNull(UserManager.findEntity(id));

  }
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
  @After
  public void tearDown() {
      instance.ClearDatabase();
  }
  @Before
  public void setUp() {
    instance.IntializeTestDatabase();
  }
}
