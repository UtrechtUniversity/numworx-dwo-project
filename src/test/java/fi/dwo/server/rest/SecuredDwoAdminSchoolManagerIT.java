/**
 * Copyrighted Oct 16, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchool;
import nl.uu.fi.dwo.rest.entities.RestNewSchool;
import nl.uu.fi.dwo.rest.entities.RestSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
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
public class SecuredDwoAdminSchoolManagerIT {

    private static final Logger LOG = Logger.getLogger(SecuredDwoAdminSchoolManagerIT.class.getName());

    static DatabaseManager instance = null;

    public SecuredDwoAdminSchoolManagerIT() {
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
    public void setUp() {
        instance.IntializeTestDatabase();
    }

    @After
    public void tearDown() {
        instance.ClearDatabase();
    }

    /**
     * Test of submitSchool method, of class SecuredDwoAdminSchoolManager.
     *
     * Tests whether a new school can be submitted.
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
        
        RestNewSchool restSchool = new RestNewSchool();
        restSchool.setRestContext(new DomContext());
        DomNewSchool newSchool = new DomNewSchool();
        newSchool.setDomSchoolFull(school.buildDomSchoolFull());
        Set<RoleType> keySet = newSchool.getRoleTypePasswords().keySet();
        for (RoleType roleType : keySet) {
            newSchool.getRoleTypePasswords().put(roleType, "passw" + roleType.name());
        }
        restSchool.setDomNewSchool(newSchool);
        
        SecuredDwoAdminSchoolManager lclInstance = new SecuredDwoAdminSchoolManager();
        lclInstance.submitSchool(sc, restSchool);
        PersistentSchool pSchool = SchoolManager.findBySchoolLogin("dummyLogin");
        if (!pSchool.similar(school)) {
            fail("Method returned an unsimilar school result.");
        }

        pSchool = SchoolManager.findBySchoolLogin("dummyLogin");
        if (!school.similar(pSchool)) {
            fail("School not found in persistent store.");
        }

    }

    /**
     * Test of getSchool method, of class SecuredDwoAdminSchoolManager. Tests if
     * a known school can be retrieved.
     */
    @Test
    public void testGetSchool() {
        System.out.println("getSchool");
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);

        SecuredDwoAdminSchoolManager instance = new SecuredDwoAdminSchoolManager();
        PersistentSchool expResult = null;
        expResult = SchoolManager.findEntity(3L);
        RestSchool4DwoAdmin restSchool = new RestSchool4DwoAdmin();
        restSchool.setRestContext(new DomContext());
        DomSchool4DwoAdmin dSchoolIn = expResult.buildDomSchool4DwoAdmin();
        DomSchoolFull dSchoolResult = expResult.buildDomSchoolFull();
        restSchool.setDomSchool4DwoAdmin(dSchoolIn);
        DomSchoolFull dSchoolOut = instance.getSchool(sc, restSchool);
        assertEquals(dSchoolOut.getExpire(), dSchoolResult.getExpire());
        assertEquals(dSchoolOut.getExport(), dSchoolResult.getExport());
        assertEquals(dSchoolOut.getImage(), dSchoolResult.getImage());
        assertEquals(dSchoolOut.getSchoolLogin(), dSchoolResult.getSchoolLogin());
        assertEquals(dSchoolOut.getSchoolName(), dSchoolResult.getSchoolName());
        assertEquals(dSchoolOut.getSchoolRights(), dSchoolResult.getSchoolRights());
        if (!dSchoolOut.getId().equals(dSchoolResult.getId())) {
            fail("School fetched data with different persistent id from persistent store.");
        }
    }

    /**
     * Test of getSchools method, of class SecuredDwoAdminSchoolManager. Tests
     * if the number of schools fetched is as expected.
     */
    @Test
    public void testGetSchools() {
        System.out.println("getSchools");
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);
        SecuredDwoAdminSchoolManager instance = new SecuredDwoAdminSchoolManager();
        List<PersistentSchool> expResult = SchoolManager.findEntities();
        List<DomSchool4DwoAdmin> result = instance.getSchools(sc);
        assertEquals("The number of schools found did not match.", expResult.size(), result.size());
    }

    /**
     * Test of updateSchool method, of class SecuredDwoAdminSchoolManager. Tests
     * if the properties of a known school are changed. It tests also if illegal
     * changes of index and school login can occur.
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
        RestSchoolFull restSchool = new RestSchoolFull();
        restSchool.setRestContext(new DomContext());
        restSchool.setDomSchoolFull(school.buildDomSchoolFull());

        SecuredDwoAdminSchoolManager instance = new SecuredDwoAdminSchoolManager();
        Boolean result = instance.updateSchool(sc, restSchool);
        if (!result) {
            fail("Method return false on update of school.");
        }

        PersistentSchool pSchool = SchoolManager.findBySchoolLogin("school01");
        if (!school.similar(pSchool)) {
            fail("School not updated in persistent store.");
        }

        //try illegal action updating index and/or schoollogin
        school = SchoolManager.findBySchoolLogin("school01");
        school.setSchoolID(1L);
        restSchool.setDomSchoolFull(school.buildDomSchoolFull());
        try {
            result = instance.updateSchool(sc, restSchool);
            pSchool = SchoolManager.findBySchoolLogin("school01");
            if (school.getSchoolID().equals(pSchool.getSchoolID())) {
                fail("School id updated in persistent store to value:" + pSchool.getSchoolID() + ".");
            }
        } catch (Dwo2RestException e) {
            //all is well
        }
    }

    /**
     * Test of removeSchool method, of class SecuredDwoAdminSchoolManager. Tests
     * if a known school can be removed.
     */
    @Test
    public void testRemoveSchool() {
        System.out.println("removeSchool");
        SecurityContext sc = new TestSecurityContext("dwoadmin", RoleType.ADMIN);

        SecuredDwoAdminSchoolManager instance = new SecuredDwoAdminSchoolManager();
        PersistentSchool expResult = null;
        expResult = SchoolManager.findEntity(3L);
        RestSchool4DwoAdmin restSchool = new RestSchool4DwoAdmin();
        DomSchool4DwoAdmin domSchool = expResult.buildDomSchool4DwoAdmin();
        restSchool.setDomSchool4DwoAdmin(domSchool);
        domSchool.setId(MySQLPersistenceId.createPersistenceId(expResult.getSchoolID(), PersistenceClassType.PersistentSchool));
        domSchool.setSchoolLogin("school01");
        domSchool.setSchoolName("Trivial");
        try {
            Boolean b = instance.removeSchool(sc, restSchool);
            assertEquals("School failed to delete.", b, true);
        } catch (Dwo2RestException e) {
            fail("School failed to delete.");
        }
        PersistentSchool result = SchoolManager.findEntity(3L);
        if (result != null) {
            fail("School failed to delete.");
        }
    }

}
