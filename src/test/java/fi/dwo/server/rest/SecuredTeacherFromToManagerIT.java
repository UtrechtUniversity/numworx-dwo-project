package fi.dwo.server.rest;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.SecurityContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFrom;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFromTo;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestSchoolAndProfile;
import nl.uu.fi.dwo.rest.entities.RestSchoolFromTo;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class SecuredTeacherFromToManagerIT {
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

  SecuredTeacherFromToManager manager;
  @Before
  public void setUp() throws Exception {
      instance.IntializeTestDatabase();
      manager = new SecuredTeacherFromToManager();
  }

  @After
  public void tearDown() throws Exception {
      instance.ClearDatabase();
  }
  @Test
  public void testGet() throws Dwo2Exception {
    SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
    RestContext rest = new RestContext();
    DomContext context = new DomContext();
    DomHasRole domHasRole = null;
    PersistentUser pUser = UserManager.findByUserName("user07");
    PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3

    try {
        PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);
        domHasRole = pHasRole.buildDomHasRole();
    } catch (Dwo2Exception ex) {
        Logger.getLogger(SecuredTeacherResultsManagerIT.class.getName()).log(Level.SEVERE, null, ex);
        fail("Could not find teacher's hasRole");
    }
    context.setDomHasRole(domHasRole);
    rest.setRestContext(context);
    DomSchoolFromTo result = manager.get(sc, rest);
    assertNotNull(result);
    assertFalse(result.getAll());
    assertFalse(result.getSchools().isEmpty());
  }

  @Test
  public void testSet() throws Dwo2Exception {
    SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
    DomSchoolFromTo dom = new DomSchoolFromTo();
    RestSchoolFromTo rest = new RestSchoolFromTo();
    DomContext context = new DomContext();
    DomHasRole domHasRole = null;
    PersistentUser pUser = UserManager.findByUserName("user07");
    PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3

    try {
        PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);
        domHasRole = pHasRole.buildDomHasRole();
    } catch (Dwo2Exception ex) {
        Logger.getLogger(SecuredTeacherResultsManagerIT.class.getName()).log(Level.SEVERE, null, ex);
        fail("Could not find teacher's hasRole");
    }
    rest.setRestContext(context);
    context.setDomHasRole(domHasRole);
    rest.setSchoolFromTo(dom);
    dom.setAll(Boolean.TRUE);
    dom.setSchools(Collections.emptyList());
    
    assertTrue( manager.set(sc, rest) ) ;
    RestContext r = new RestContext();
    r.setRestContext(context);
    dom = manager.get(sc, r);
    assertTrue( dom.getAll()) ;
    assertTrue( dom.getSchools().isEmpty());
    
  }

  @Test
  public void testGetExports() throws Dwo2Exception {
    SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
    RestContext rest = new RestContext();
    DomContext context = new DomContext();
    DomHasRole domHasRole = null;
    PersistentUser pUser = UserManager.findByUserName("user07");
    PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3

    try {
        PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);
        domHasRole = pHasRole.buildDomHasRole();
    } catch (Dwo2Exception ex) {
        Logger.getLogger(SecuredTeacherResultsManagerIT.class.getName()).log(Level.SEVERE, null, ex);
        fail("Could not find teacher's hasRole");
    }
    context.setDomHasRole(domHasRole);
    rest.setRestContext(context);
    List<DomSchoolFrom> result = manager.getExports(sc, rest);
    
    assertNotNull(result);
    assertFalse(result.isEmpty());
    
  }

  @Test public void getCourses() throws Dwo2Exception {
    SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
    RestSchoolAndProfile rest = new RestSchoolAndProfile();
    DomContext context = new DomContext();
    DomHasRole domHasRole = null;
    PersistentUser pUser = UserManager.findByUserName("user07");
    PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3
    PersistentDwoProfile profile = DwoProfileManager.findEntity(1L);
    try {
      PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);
      domHasRole = pHasRole.buildDomHasRole();
  } catch (Dwo2Exception ex) {
      Logger.getLogger(SecuredTeacherResultsManagerIT.class.getName()).log(Level.SEVERE, null, ex);
      fail("Could not find teacher's hasRole");
  }
  context.setDomHasRole(domHasRole);
  rest.setRestContext(context);
  rest.setDomSchoolAndProfile(new DomSchoolAndProfile());
  rest.getDomSchoolAndProfile().setDomDwoProfile(profile.buildDomDwoProfile());
  rest.getDomSchoolAndProfile().setDomSchool(pSchool.buildDomSchool());
  List<DomCourse> result = manager.getCourses(sc, rest);
  
  assertNotNull(result);
  assertFalse(result.isEmpty());
  
  
  }
}
