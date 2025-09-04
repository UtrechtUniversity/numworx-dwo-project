package fi.dwo.server.rest;

import static org.junit.Assert.*;

import javax.ws.rs.core.SecurityContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import fi.dwo.server.testutil.TestSecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolOrganisation;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.entities.RestSchoolOrganisation;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class SecuredTeacherSchoolManagerIT {

    static DatabaseManager dbInstance = null;

    {
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

	@Test
	public void testGetStudentsInSchool() throws Dwo2Exception {
		SecuredTeacherSchoolManager manager = new SecuredTeacherSchoolManager();
        SecurityContext sc = new TestSecurityContext("user07", RoleType.TEACHER);//school01
		RestSchoolOrganisation rest = new RestSchoolOrganisation(new DomContext(), new DomSchoolOrganisation());
        DomHasRole domHasRole;
        domHasRole = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.TEACHER).buildDomHasRole();
		rest.getRestContext().setDomHasRole(domHasRole);
		
		DomSchoolOrganisation result = manager.getStudentsInSchool(sc, rest);
		
		assertNotNull(result);
		assertEquals(3, result.getUsers().size());
		
		result.getUsers().remove(0);
		result.getUsers().remove(0);
		result.setSkip(0L);
		rest.setDomSchoolOrganisation(result);
		
		result = manager.getStudentsInSchool(sc, rest);
	}

}
