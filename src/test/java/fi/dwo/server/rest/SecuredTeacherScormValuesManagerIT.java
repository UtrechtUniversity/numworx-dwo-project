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
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherScormValues;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestTeacherScormValues;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class SecuredTeacherScormValuesManagerIT {

    static DatabaseManager instance = null;
    SecuredTeacherScormValuesManager manager;
    
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
		manager = new SecuredTeacherScormValuesManager();
	}

	@After
	public void tearDown() throws Exception {
        instance.ClearDatabase();
	}

	@Test
	public void testGet() throws Dwo2Exception {
		String key = "cmi.score.raw";
		Long sscID = 1L;

		
		
		DomContext restContext = new DomContext();
		
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
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
        restContext.setDomHasRole(domHasRole);
        DomMapEntry<String,String> entry = new DomMapEntry<String,String>();
        entry.setKey(key);
        entry.setValue("");
		List<DomMapEntry<String,String>> input = Collections.singletonList(entry);
        RestTeacherScormValues rest = new RestTeacherScormValues();
        rest.setRestContext(restContext);
        DomTeacherScormValues domTeacherScormValues = new DomTeacherScormValues();
        rest.setDomTeacherScormValues(domTeacherScormValues);
        domTeacherScormValues.setValues(input);
        PersistentStudentScoContext pssd = StudentScoContextManager.findEntity(sscID);
        DomStudentScoContext studentScoContext = pssd.buildDomStudentScoContext();
		domTeacherScormValues.setStudentScoContext(studentScoContext);
		rest.setDomTeacherScormValues(domTeacherScormValues);
        
		DomTeacherScormValues result = manager.get(sc, rest);
        assertNotNull(result);
        List<DomMapEntry<String,String>> list = result.getValues();
        entry = list.get(0);
        assertEquals(key, entry.getKey());
        assertEquals("0.0", entry.getValue());
	}

	@Test
	public void testSet() throws Dwo2Exception {
		String key = "cmi.completion_status";
		String value = "complete";
		Long sscID = 1L;

		
		
		DomContext restContext = new DomContext();
		
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
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
        restContext.setDomHasRole(domHasRole);
        DomMapEntry<String,String> entry = new DomMapEntry<String,String>();
        entry.setKey(key);
        entry.setValue(value);
		List<DomMapEntry<String,String>> input = Collections.singletonList(entry);
        RestTeacherScormValues rest = new RestTeacherScormValues();
        rest.setRestContext(restContext);
        DomTeacherScormValues domTeacherScormValues = new DomTeacherScormValues();
        rest.setDomTeacherScormValues(domTeacherScormValues);
        domTeacherScormValues.setValues(input);
        PersistentStudentScoContext pssd = StudentScoContextManager.findEntity(sscID);
        DomStudentScoContext studentScoContext = pssd.buildDomStudentScoContext();
		domTeacherScormValues.setStudentScoContext(studentScoContext);
		rest.setDomTeacherScormValues(domTeacherScormValues);
        DomStudentScoContext result = manager.set(sc, rest);
        assertEquals(studentScoContext.getId(), result.getId());
        pssd = StudentScoContextManager.findEntity(sscID);
        assertEquals(value, pssd.getCompletionStatus());
	}

}
