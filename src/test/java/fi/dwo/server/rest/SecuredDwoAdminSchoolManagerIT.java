/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceClassType;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.rest.entities.RestSchool4Admin;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import java.util.List;
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
public class SecuredDwoAdminSchoolManagerIT {

    private static final Logger LOG = Logger.getLogger(SecuredDwoAdminSchoolManagerIT.class.getName());

    static DatabaseManager instance = null;

    public SecuredDwoAdminSchoolManagerIT() {
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
    public void setUp() {
        instance.IntializeTestDatabase();
    }

    @After
    public void tearDown() {
        instance.ClearDatabase();
    }

    /**
     * Test of submitSchool method, of class SecuredDwoAdminSchoolManager.
     */
    @Test
    public void testSubmitSchool() {
        System.out.println("submitSchool");
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
        PersistentSchool school = new PersistentSchool();
        school.setExport(Boolean.TRUE);
        school.setSchoolLogin("dummyLogin");
        school.setSchoolName("dummyName");
        school.setExpire(null);
        school.setSchoolRights("_");
        school.setImage(null);

        SecuredDwoAdminSchoolManager instance = new SecuredDwoAdminSchoolManager();
        PersistentSchool result = instance.submitSchool(sc, school);
        if (!school.similar(result)) {
            fail("Method returned an unsimilar school result.");
        }

        result = SchoolManager.findBySchoolLogin("dummyLogin");
        if (!school.similar(result)) {
            fail("School not found in persistent store.");
        }

    }

    /**
     * Test of getSchool method, of class SecuredDwoAdminSchoolManager.
     */
    @Test
    public void testGetSchool() {
        System.out.println("getSchool");
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);

        SecuredDwoAdminSchoolManager instance = new SecuredDwoAdminSchoolManager();
        PersistentSchool expResult = null;
        PersistentSchool result = instance.getSchool(sc, MySQLPersistenceId.createPersistenceId(3, PersistenceClassType.PersistentSchool));
        expResult = SchoolManager.findEntity(3);
        assertEquals(expResult, result);
        if (!result.similar(expResult)) {
            fail("School fetched not similar with data directly from persistent store.");
        }
    }

    /**
     * Test of getSchools method, of class SecuredDwoAdminSchoolManager.
     */
    @Test
    public void testGetSchools() {
        System.out.println("getSchools");
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
        SecuredDwoAdminSchoolManager instance = new SecuredDwoAdminSchoolManager();
        List<PersistentSchool> expResult = SchoolManager.findEntities();
        List<RestSchool4Admin> result = instance.getSchools(sc);
        assertEquals("The number of schools found did not match.", expResult.size(), result.size());
    }

    /**
     * Test of updateSchool method, of class SecuredDwoAdminSchoolManager.
     */
    @Test
    public void testUpdateSchool() {
        System.out.println("updateSchool");
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
        PersistentSchool school = SchoolManager.findBySchoolLogin("school01");
        school.setExport(Boolean.TRUE);
        school.setSchoolName("dummyName");
        school.setExpire(null);
        school.setSchoolRights("_");
        school.setImage(null);

        SecuredDwoAdminSchoolManager instance = new SecuredDwoAdminSchoolManager();
        PersistentSchool result = instance.updateSchool(sc, school);
        if (!school.similar(result)) {
            fail("Method returned an unsimilar school result.");
        }

        result = SchoolManager.findBySchoolLogin("school01");
        if (!school.similar(result)) {
            fail("School not updated in persistent store.");
        }

        //try illegal action updating index and/or schoollogin
        school = SchoolManager.findBySchoolLogin("school01");
        school.setSchoolID(1);
        try {
            result = instance.updateSchool(sc, school);
            if (result.getSchoolID() == school.getSchoolID()) {
                fail("School id updated in persistent store to value:" + result.getSchoolID() + ".");
            }
        }
        catch (Dwo2RestException e) {
            //all is well
        }
    }

    /**
     * Test of removeSchool method, of class SecuredDwoAdminSchoolManager.
     */
    @Test
    public void testRemoveSchool() {
        System.out.println("removeSchool");
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);

        SecuredDwoAdminSchoolManager instance = new SecuredDwoAdminSchoolManager();
        PersistentSchool expResult = null;
        expResult = SchoolManager.findEntity(3);
        RestSchool4Admin restSchool = new RestSchool4Admin(expResult);
        restSchool.setId(MySQLPersistenceId.createPersistenceId(expResult.getSchoolID(), PersistenceClassType.PersistentSchool));
        restSchool.setSchoolLogin("school01");
        restSchool.setSchoolName("Trivial");
        try {
            Boolean b = instance.removeSchool(sc, restSchool);
            assertEquals("School failed to delete.", b, true);
        }
        catch (Dwo2RestException e) {
            fail("School failed to delete.");
        }
        PersistentSchool result = SchoolManager.findEntity(3);
        if (result != null) {
            fail("School failed to delete.");
        }
    }

}
