package fi.dwo.server.PersistentDataManagers.core;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentMethod;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolMethod;
import fi.dwo.commons.persistence.entities.PersistentSchoolMethodPK;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class SchoolMethodManagerPIT {

    private static DatabaseManager instance;

	@BeforeClass
    public static void setUpClass() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
    }

    @AfterClass
    public static void tearDownClass() {
        DwoEmfFactory.setDefaultEntityManagerFactory();
        instance = null;
    }

    PersistentSchool school;
    
	@Before
	public void setUp() throws Exception {
	      instance.IntializeTestDatabase();
	      school = SchoolManager.findEntity(3L);
	}

	@After
	public void tearDown() throws Exception {
        instance.ClearDatabase();
	}

	@Test
	public void testCreate() {
		PersistentSchoolMethod method = create();
		assertEquals(1, method.getOptlock().intValue());
		assertNotNull(method.getMethodID());
	}

	private PersistentSchoolMethod create() {
		PersistentMethod method = createMethod();
		PersistentSchoolMethod sm = new PersistentSchoolMethod(new PersistentSchoolMethodPK(school.getSchoolID(), 1L));
		sm.setMethodID(method.buildPersistenceId());
		SchoolMethodManager.create(sm);
		return sm;
	}

	private PersistentMethod createMethod() {
		PersistentMethod method = new PersistentMethod();
		PersistentDwoProfile profile = new PersistentDwoProfile(1L);
		InputStream in = getClass().getResourceAsStream("/fi/dwo/server/mysql/Getal&Ruimte.json");
		DomMethod dm = MethodManager.genson.deserialize(in, DomMethod.class);
		method = MethodManager.toValue(dm, school, profile);
		MethodManager.create(method);
		return method;
	}

	@Test
	public void testEdit() {
		PersistentSchoolMethod sm = create();
		sm.setMethodID(null);
		sm = SchoolMethodManager.edit(sm);
		List<PersistentSchoolMethod> list = SchoolMethodManager.findEntities();
		assertNull(list.get(0).getMethodID());
	}

	@Test
	public void testDestroy() {
		SchoolMethodManager.destroy(create().getId());
	}

	@Test
	public void testFindEntities() {
		List<PersistentSchoolMethod> list = SchoolMethodManager.findEntities();
		assertEquals(0, list.size());
		PersistentSchoolMethod m = create();
		list = SchoolMethodManager.findEntities();
		assertEquals(m.getMethodID(), list.get(0).getMethodID());
	}

	@Test
	public void testFindEntity() {
		List<PersistentSchoolMethod> list = SchoolMethodManager.findEntities(school);
		assertEquals(0, list.size());
		PersistentSchoolMethod m = create();
		PersistentSchoolMethod m2 = SchoolMethodManager.findEntity(m.getId());
		assertEquals(m.getMethodID(), m2.getMethodID());
	}

	@Test
	public void testGetEntityCount() {
		assertEquals(0, SchoolMethodManager.getEntityCount());
		PersistentSchoolMethod m = create();
		assertEquals(1, SchoolMethodManager.getEntityCount());
		SchoolMethodManager.destroy(m.getId());
		assertEquals(0, SchoolMethodManager.getEntityCount());
	}

}
