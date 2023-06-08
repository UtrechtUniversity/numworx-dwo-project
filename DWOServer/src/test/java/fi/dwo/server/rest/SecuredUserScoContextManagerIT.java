package fi.dwo.server.rest;

import static org.junit.Assert.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.SecurityContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestScoContextId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class SecuredUserScoContextManagerIT {
  private static DatabaseManager instance = null;
  private SecuredUserScoContextManager manager;

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

  @Before
  public void setUp() throws Exception {
    instance.IntializeTestDatabase();
    manager = new SecuredUserScoContextManager();    
  }

  @After
  public void tearDown() throws Exception {
    instance.ClearDatabase();
  }

  @Test @Ignore
  public void testGetScos() {
    fail("Not yet implemented");
  }

  @Test @Ignore
  public void testGet() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetDataStudent() throws Dwo2Exception {
    SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);//school01
    RestScoContextId rest = new RestScoContextId();
    DomContext restContext = new DomContext();
    DomHasRole domHasRole = null;
    PersistentUser pUser = UserManager.findByUserName("user02");
    PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3

    try {
        PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.STUDENT);
        domHasRole = pHasRole.buildDomHasRole();
    } catch (Dwo2Exception ex) {
        Logger.getLogger(SecuredTeacherResultsManagerIT.class.getName()).log(Level.SEVERE, null, ex);
        fail("Could not find student hasRole");
    }
    restContext.setDomHasRole(domHasRole);
    rest.setRestContext(restContext);
    rest.setDomScoContext(new DomScoContextId());
    rest.getDomScoContext().setId(PersistentScoContext.buildPersistenceId(1L));
    rest.setDomDwoProfile(new DomDwoProfileId(PersistentDwoProfile.buildPersistenceId(1L)));
    rest.setSchoolClassID(new DomSchoolClassId(PersistentSchoolClass.buildPersistenceId(1L)));
    
    DomScoData data;
	try {
		data = manager.getData(sc, rest);
		fail("should fail");
	} catch (Dwo2Exception e) {
		assertEquals("illegal action", Dwo2ExceptionCode.User_IllegalAction, e.getDwo2Code());
	}

  }

  @Test 
  public void testGetDataTeacher() throws Dwo2Exception {
    SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
    RestScoContextId rest = new RestScoContextId();
    DomContext restContext = new DomContext();
    DomHasRole domHasRole = null;
    PersistentUser pUser = UserManager.findByUserName("user07");
    PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");//id =3

    try {
        PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);
        domHasRole = pHasRole.buildDomHasRole();
    } catch (Dwo2Exception ex) {
        Logger.getLogger(SecuredTeacherResultsManagerIT.class.getName()).log(Level.SEVERE, null, ex);
        fail("Could not find student hasRole");
    }
    restContext.setDomHasRole(domHasRole);
    rest.setRestContext(restContext);
    rest.setDomScoContext(new DomScoContextId());
    rest.getDomScoContext().setId(PersistentScoContext.buildPersistenceId(1L));
    rest.setDomDwoProfile(new DomDwoProfileId(PersistentDwoProfile.buildPersistenceId(1L)));
    rest.setSchoolClassID(new DomSchoolClassId(PersistentSchoolClass.buildPersistenceId(1L)));
    
    DomScoData data = manager.getData(sc, rest);

    assertNotNull(data);
  }

}
