/** Copyrighted Mar 15, 2018 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentMethod;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.core.MethodManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import java.util.List;
import javax.ws.rs.core.SecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestMethod;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author plas0006
 */
public class SecuredStudentMethodManagerIT {

    private static final String ID = "LOCAL;none;testmethod";
    private static final String PROXY = "PROXY;" + PersistenceClassType.PersistentMethod + ";testmethod";
	static DatabaseManager dbInstance = null;

    public SecuredStudentMethodManagerIT() {
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
        
        PersistentMethod m = new PersistentMethod();
        m.setMethodID(ID);
        m.setMethod("{}");
        m.setSchoolID(3L);
        m.setDwoProfileID(1L);
        MethodManager.create(m);
    }

    @After
    public void tearDown() {
        dbInstance.ClearDatabase();
    }

    /**
     * Test of getStudentModels method, of class
     * SecuredTeacherStudentModelManager.
     */
    @Test
    public void testGet() throws Exception {
        SecuredStudentMethodManager instance = new SecuredStudentMethodManager();
            StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser("user02")
                    .setDefaultHasRole()//
                    .buildStudent();
            SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);
            RestMethod restContext = new RestMethod();
            DomHasRole hr = state.getContext().getUserCtx().getHasRole().buildDomHasRole();
            DomContext context = new DomContext();
            context.setDomHasRole(hr);
            restContext.setRestContext(context);
            restContext.setDomDwoProfile(new DomDwoProfileId());
            restContext.getDomDwoProfile().setId(PersistentDwoProfile.buildPersistenceId(1L));
            restContext.setDomMethod(new DomMethod());
            restContext.getDomMethod().setId(new PersistenceId(ID));
            DomMethod result = instance.get(sc, restContext);
            assertNotNull(result);
    }

    /**
     * Test of getStudentModelDataScore method, of class
     * SecuredStudentStudentModelManager.
     */
    /**
     * Test of getStudentModels method, of class
     * SecuredTeacherStudentModelManager.
     */
    @Test
    public void testGetProxy() throws Exception {
        SecuredStudentMethodManager instance = new SecuredStudentMethodManager();
            StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser("user02")
                    .setDefaultHasRole()//
                    .buildStudent();
            SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);
            RestMethod restContext = new RestMethod();
            DomHasRole hr = state.getContext().getUserCtx().getHasRole().buildDomHasRole();
            DomContext context = new DomContext();
            context.setDomHasRole(hr);
            restContext.setRestContext(context);
            restContext.setDomDwoProfile(new DomDwoProfileId());
            restContext.getDomDwoProfile().setId(PersistentDwoProfile.buildPersistenceId(1L));
            restContext.setDomMethod(new DomMethod());
            restContext.getDomMethod().setId(new PersistenceId(PROXY));
             DomMethod result = instance.get(sc, restContext);
            assertNotNull(result);
    }

}
