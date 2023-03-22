package fi.dwo.server.rest;

import static org.junit.Assert.assertNotNull;

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
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentMethod;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestMethod;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class SecuredTeacherMethodManagerIT {
    private static final Logger LOG = Logger.getLogger(SecuredTeacherMethodManagerIT.class.getName());

    static DatabaseManager dbInstance = null;

    public SecuredTeacherMethodManagerIT() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
    }

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
 
    @Ignore
    @Test public void testGetMethod() throws Exception {
    	SecuredTeacherMethodManager manager = new SecuredTeacherMethodManager();
    	
    	SecurityContext sc = new TestSecurityContext("user03", RoleType.TEACHER);;
		RestMethod rest = new RestMethod();
		
        DomHasRole hr = new DomHasRole();
        //MYSQL;PersistentHasRole;00000000000000000010;00000000000000000003 TEACHER School01
        PersistentHasRolePK key = new PersistentHasRolePK(10L, 3L);
        PersistenceId id = PersistentHasRole.buildPersistenceId(key);
        hr.setId(id);
        hr.setUserId(PersistentUser.buildPersistenceId(10L));
        hr.setSchoolGroupId(PersistentSchoolGroup.buildPersistenceId(10L));
        DomContext context = new DomContext();
        context.setDomHasRole(hr);
        rest.setRestContext(context);
        DomDwoProfileId domDwoProfile = new DomDwoProfileId(PersistentDwoProfile.buildPersistenceId(1L));
		rest.setDomDwoProfile(domDwoProfile);
		DomMethod method = new DomMethod(new PersistenceId("LOCAL;none;Test"));
		rest.setDomMethod(method);
		DomMethod result = manager.get(sc, rest);
		assertNotNull(result);
    }
    
}
