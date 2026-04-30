package fi.dwo.server.rest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.core.SecurityContext;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.AboType;
import nl.uu.fi.dwo.rest.entities.RestSchool;
import nl.uu.fi.dwo.rest.entities.RestSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import nl.uu.fi.dwo.rest.entities.RestUser;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class GarbageManagerIT {
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

  SecuredDwoAdminGarbageManager manager;
  @Before
  public void setUp() throws Exception {
      instance.IntializeTestDatabase();
      manager = new SecuredDwoAdminGarbageManager();
  }

  @After
  public void tearDown() throws Exception {
      instance.ClearDatabase();
  }


  @Test
  public void testGetUsers() throws Dwo2Exception {
    SecurityContext sc = getSecurityContext();
    Integer limit = 1;
    Long before = System.currentTimeMillis();
    List<DomUserFullwLoginContext> result = manager.getUsers(sc, before, limit, Boolean.FALSE );
    assertEquals(1, result.size());
    result = manager.getUsers(sc, before, limit, Boolean.TRUE );
    assertEquals(1, result.size());
 }

  @Test
  public void testGetUsersAll() throws Dwo2Exception {
    SecurityContext sc = getSecurityContext();
    Integer limit = null;
    Long before = System.currentTimeMillis();
    List<DomUserFullwLoginContext> result = manager.getUsers(sc, before, limit, Boolean.FALSE );
    assertEquals(5, result.size());
    result = manager.getUsers(sc, before, limit, Boolean.TRUE );
    assertEquals(8, result.size());
 }

  
  
  
  @Test
  public void testDeleteUser() throws Exception {
    SecurityContext sc = getSecurityContext();
    RestUser rest = new RestUser();
    DomUser user = new DomUser();
    user.setId(PersistentUser.buildPersistenceId(1L));
    rest.setDomUser(user);
    DomContext context = getContext();
    rest.setRestContext(context); 
    manager.removeUser(sc, rest);
  }
  
  
  @Test
  public void testDeleteSchool() throws Exception {
    SecurityContext sc = getSecurityContext();
    List<DomSchool4DwoAdmin> schools = manager.getSchools(sc, 1);
    assertTrue(schools.isEmpty());
    SecuredDwoAdminSchoolManager sm = new SecuredDwoAdminSchoolManager();
    schools = sm.getSchools(sc);
    
    RestSchool4DwoAdmin restSchool = new RestSchool4DwoAdmin();
    restSchool.setRestContext(getContext());
    int size = schools.size()-1;
    restSchool.setDomSchool4DwoAdmin(schools.get(size));
    sm.removeSchool(sc, restSchool);
    schools = sm.getSchools(sc);
    assertEquals(size, schools.size());
    schools = manager.getSchools(sc, null);
    assertEquals(1, schools.size());
    sm.removeSchool(sc, restSchool);
    schools = manager.getSchools(sc, 1);
    assertTrue(schools.isEmpty());
  }

  SecurityContext getSecurityContext() {
    SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
    return sc;
  }

  DomContext getContext() throws Dwo2Exception {
    PersistentUser pUser = UserManager.findByUserName("dwoadmin");
    PersistentSchool pSchool = SchoolManager.findEntity(0L);
    PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.ADMIN);
    DomContext context = new DomContext();
    context.setDomHasRole(pHasRole.buildDomHasRole());
    return context;
  }

}
