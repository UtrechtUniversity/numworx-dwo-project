/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.SecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Gert van der Plas
 */
public class SecuredTeacherResultsManagerIT {

    private static final Logger LOG = Logger.getLogger(SecuredTeacherResultsManagerIT.class.getName());

    static DatabaseManager dbInstance = null;

    public SecuredTeacherResultsManagerIT() {
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
     * Test of getTeachersSchoolClasses method, of class
     * SecuredTeacherSchoolClassManager.
     */
    @Test
    public void testGetTeachersResults() {
        System.out.println("testGetTeachersResults");
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
        SecuredTeacherResultsManager instance = new SecuredTeacherResultsManager();
        RestContext restContext = new RestContext();
        DomContext domContext = new DomContext();
        DomHasRole domHasRole = null;
        PersistentUser pUser = UserManager.findByUserName("user07");
        PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");

        try {
            PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.TEACHER);
            domHasRole = pHasRole.buildDomHasRole();
        } catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredTeacherResultsManagerIT.class.getName()).log(Level.SEVERE, null, ex);
            fail("Could not find teacher's hasRole");
        }
        
        domContext.setDomHasRole(domHasRole);
        restContext.setRestContext(domContext);
        
        DomResultsPerTeacher result = instance.getTeachersResults(sc,restContext);
        
        assertEquals(2, result.getClassCourses().size());

    }
}
