/** Copyrighted Mar 15, 2018 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import java.util.List;
import javax.ws.rs.core.SecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
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
public class SecuredStudentStudentModelManagerTest {

    static DatabaseManager dbInstance = null;

    public SecuredStudentStudentModelManagerTest() {
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

    /**
     * Test of getStudentModels method, of class
     * SecuredTeacherStudentModelManager.
     */
    @Test
    public void testGetStudentModels() throws Exception {
        SecuredStudentStudentModelManager instance = new SecuredStudentStudentModelManager();
            StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser("user02")
                    .setDefaultHasRole()//
                    .buildStudent();
            SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);
            RestContext restContext = new RestContext();
            DomHasRole hr = state.getContext().getUserCtx().getHasRole().buildDomHasRole();
            DomContext context = new DomContext();
            context.setDomHasRole(hr);
            restContext.setRestContext(context);

            List<DomStudentModelContext> result = instance.getMergedStudentModels(sc, restContext);
            assertEquals(2, result.size());
    }

    /**
     * Test of getStudentModelDataScore method, of class
     * SecuredStudentStudentModelManager.
     */
    @Test
    public void testGetStudentModelDataScore() throws Exception {
        System.out.println("getStudentModelDataScore");
        SecuredStudentStudentModelManager instance = new SecuredStudentStudentModelManager();
        
        try {
            StudentDomainAuthorizer.StudentState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser("user02")
                    .setDefaultHasRole()//
                    .buildStudent();
            SecurityContext sc = new TestSecurityContext("user02", RoleType.STUDENT);
            RestContext restContext = new RestContext();
            DomHasRole hr = state.getContext().getUserCtx().getHasRole().buildDomHasRole();
            DomContext context = new DomContext();
            context.setDomHasRole(hr);
            restContext.setRestContext(context);
            
            RestStudentModelContextId restModelId = new RestStudentModelContextId();
            DomStudentModelContextId domModelId = new DomStudentModelContextId();
            domModelId.setId(PersistentStudentModelContext.buildPersistenceId(1L));
            restModelId.setDomStudentModelContext(domModelId);
            restModelId.setRestContext(context);

            DomStudentModelDataScore result = instance.getStudentModelDataScore(sc, restModelId);
            long count = result.getDomStudentModelStructureScore().getCount();
            double score = result.getDomStudentModelStructureScore().getScore();
            result.getDomStudentModelStructureScore().recalculateAncestors();
            assertEquals(count, result.getDomStudentModelStructureScore().getCount());
            assertEquals(true,Math.abs(score) < 2 * Double.MIN_VALUE);
            assertEquals(true,Math.abs(result.getDomStudentModelStructureScore().getScore()) < 2 * Double.MIN_VALUE);
        } catch (Dwo2Exception e) {
            fail("could not fetch models.");
        }
    }
}
