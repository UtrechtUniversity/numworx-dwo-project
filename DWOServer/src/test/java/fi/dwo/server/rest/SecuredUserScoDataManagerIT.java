package fi.dwo.server.rest;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonPatchBuilder;
import javax.json.spi.JsonProvider;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.json.JsonProviderImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.PersistenceIdFactory;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScormValues;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScormValues;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class SecuredUserScoDataManagerIT {
    private static DatabaseManager instance = null;
    SecuredUserScoDataManager manager;
    
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
		manager = new SecuredUserScoDataManager();
	}

	@After
	public void tearDown() throws Exception {
        instance.ClearDatabase();
	}

	@Test
	public void testGetJSONLaunchDataBytes() throws Exception {
        SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);//school01
        RestScoContext rest = new RestScoContext();
        DomContext context = new DomContext();
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
        context.setDomHasRole(domHasRole);
        rest.setRestContext(context);
		rest.setDomDwoProfile(new DomDwoProfile());
		rest.setDomScoContext(new DomScoContext());
		PersistenceId id = PersistentDwoProfile.buildPersistenceId(Long.valueOf(1));
		rest.getDomDwoProfile().setId(id);
		id = PersistentScoContext.buildPersistenceId(1L);
		rest.getDomScoContext().setId(id);
		String result = manager.getJSONLaunchDataBytes(sc, rest);
		assertTrue(result.length() > 2);

	}

	@Test
	public void testGetValues() throws Dwo2Exception {
		String key = "cmi.score.raw";
		Long scoID = 1L;
        SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);//school01
        RestScormValues rest = new RestScormValues();
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
        DomScormValues values = new DomScormValues();
        rest.setDomScormValues(values);
        rest.setRestContext(restContext);
        
        PersistentScoContext scoContext = ScoContextManager.findEntity(scoID);
        DomScoContext sco = scoContext.buildDomScoContext();
        DomMapEntry<String,String> entry = new DomMapEntry<>();
        entry.setKey(key);
        entry.setValue("");
        values.setValues(Collections.singletonList(entry));
        values.setScoContext(sco);
        
        DomScormValues result = (DomScormValues) manager.getValues(sc, rest).getEntity();
        assertEquals(rest.getDomScormValues().getValues().size(), result.getValues().size());
        assertEquals(key, result.getValues().get(0).getKey());
        assertEquals("0.0", result.getValues().get(0).getValue());
	}

	@Test
	public void testSetValues() throws Dwo2Exception {
		String key = "cmi.score.raw";
		Long scoID = 1L;
        SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);//school01
        RestScormValues rest = new RestScormValues();
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
        DomScormValues values = new DomScormValues();
        rest.setDomScormValues(values);
        rest.setRestContext(restContext);
        
        PersistentScoContext scoContext = ScoContextManager.findEntity(scoID);
        DomScoContext sco = scoContext.buildDomScoContext();
        DomMapEntry<String,String> entry = new DomMapEntry<>();
        entry.setKey(key);
        entry.setValue("100");
 
        values.setValues(Collections.singletonList(entry));
        values.setScoContext(sco);
        
        Object result = manager.setValues(sc, rest, null).getEntity();
        assertEquals(Boolean.TRUE, result);
        
        values = (DomScormValues) manager.getValues(sc, rest).getEntity();
        assertEquals(rest.getDomScormValues().getValues().size(), values.getValues().size());
        assertEquals(key, values.getValues().get(0).getKey());
        assertEquals("100.0", values.getValues().get(0).getValue());
       
        
	}
	@Test
	public void testPatchValues() throws Dwo2Exception {
		String key = "cmi.suspend_data";
		Long scoID = 1L;
        SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);//school01
        RestScormValues rest = new RestScormValues();
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
        DomScormValues values = new DomScormValues();
        rest.setDomScormValues(values);
        rest.setRestContext(restContext);
        
        PersistentScoContext scoContext = ScoContextManager.findEntity(scoID);
        DomScoContext sco = scoContext.buildDomScoContext();
        DomMapEntry<String,String> entry = new DomMapEntry<>();
        entry.setKey(key);
        entry.setValue("{\"a\": 12345, \"onsState\": {} }");
        values.setValues(Collections.singletonList(entry));
        values.setScoContext(sco);      
        Response response = manager.setValues(sc, rest,null);
		Object result = response.getEntity();
		String etag   = response.getEntityTag().toString();
        assertEquals(Boolean.TRUE, result);
        assertNotNull(etag);
        
        JsonPatchBuilder builder;
        JsonProvider provider = new JsonProviderImpl();
        JsonProvider provider1 = JsonProvider.provider();
        assertSame(provider.getClass(),provider1.getClass());
        builder = provider.createPatchBuilder();
        builder.add("/b", true);
        builder.replace("/a", 44321);
        StringWriter patch = new StringWriter();
        provider.createWriter(patch).write(builder.build().toJsonArray());
        entry.setValue(patch.toString());
        response = manager.patchValues(sc, rest, etag);
        assertEquals(200, response.getStatus());
        values = (DomScormValues) manager.getValues(sc, rest).getEntity();
        assertEquals(rest.getDomScormValues().getValues().size(), values.getValues().size());
        assertEquals(key, values.getValues().get(0).getKey());
        String value = values.getValues().get(0).getValue();
		assertTrue("44321", value.contains("44321"));
        assertTrue("true", values.getValues().get(0).getValue().contains("true"));
       
        
	}

}
