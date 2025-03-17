/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolDataFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomStatistics;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.DelState;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolData;
import fi.dwo.commons.persistence.entities.PersistentUser;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchool;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestNewSchool;
import nl.uu.fi.dwo.rest.entities.RestSchool;
import nl.uu.fi.dwo.rest.entities.RestSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.entities.RestSchoolDataFull;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.ws.rs.core.SecurityContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gert van der Plas
 */
public class SecuredDwoAdminSchoolDataManagerIT {

    private static final Logger LOG = Logger.getLogger(SecuredDwoAdminSchoolDataManagerIT.class.getName());

    private static DatabaseManager instance = null;

    private DomContext context;

    public SecuredDwoAdminSchoolDataManagerIT() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
    }

    @BeforeClass
    public static void setUpClass() {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
    }

    @AfterClass
    public static void tearDownClass() {
        instance = new DatabaseManager();
        DwoEmfFactory.setDefaultEntityManagerFactory();
    }

    @Before
    public void setUp() throws Dwo2Exception {
        instance.IntializeTestDatabase();
        PersistentUser pUser = UserManager.findByUserName("dwoadmin");
        PersistentSchool pSchool = SchoolManager.findEntity(0L);
        PersistentHasRole pHasRole = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(pUser, pSchool, RoleType.ADMIN);
        context = new DomContext();
        context.setDomHasRole(pHasRole.buildDomHasRole());
    }

    @After
    public void tearDown() {
        context = null;
        instance.ClearDatabase();
    }

    /**
     * Test of submitSchool method, of class SecuredDwoAdminSchoolManager.
     *
     * Tests whether a new school can be submitted.
     * @throws Dwo2Exception 
     */
    @Test
    public void testSubmitSchoolData() throws Dwo2Exception {
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
        PersistentSchoolData school = new PersistentSchoolData(3L);
        school.setSchoolData("{ \"int\": 0 }");
        SecuredDwoAdminSchoolDataManager instance = new SecuredDwoAdminSchoolDataManager();
        DomSchoolDataFull data = school.buildDomSchoolDataFull();
        RestSchoolDataFull rest = new RestSchoolDataFull();
        rest.setRestContext(context);
        rest.setData(data);
        DomSchoolDataFull result = instance.update(sc, rest);
        assertEquals(2, result.getOptLock().intValue());
     }

    /**
     * Test of getSchool method, of class SecuredDwoAdminSchoolManager. Tests if
     * a known school can be retrieved.
     * @throws Exception 
     */
    @Test
    public void testGetSchool() throws Exception {
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);

        SecuredDwoAdminSchoolDataManager instance = new SecuredDwoAdminSchoolDataManager();
        PersistentSchool expResult = null;
        expResult = SchoolManager.findEntity(3L);
        RestSchool restSchool = new RestSchool();
        restSchool.setRestContext(context);
        DomSchoolFull dSchoolResult = expResult.buildDomSchoolFull();
        restSchool.setDomSchool(dSchoolResult);
        DomSchoolDataFull data = instance.get(sc, restSchool);
        assertEquals("{ }", data.getSchoolData()); // formatted 
    }


    /**
     * Test of updateSchool method, of class SecuredDwoAdminSchoolManager. Tests
     * if the properties of a known school are changed. It tests also if illegal
     * changes of index and school login can occur.
     */
    @Test
    public void testUpdateSchool() {
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
    }

    /**
     * Test of removeSchool method, of class SecuredDwoAdminSchoolManager. Tests
     * if a known school can be removed.
     */
    @Test
    public void testRemoveSchool() throws Exception {
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);

    }
    
}
