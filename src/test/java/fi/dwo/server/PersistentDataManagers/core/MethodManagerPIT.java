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
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class MethodManagerPIT {

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
		PersistentMethod method = create();
		assertNotNull(method.getMethodID());
	}

	private PersistentMethod create() {
		PersistentMethod method = new PersistentMethod();
		InputStream in = getClass().getResourceAsStream("/fi/dwo/server/mysql/Getal&Ruimte.json");
		DomMethod dm = MethodManager.genson.deserialize(in, DomMethod.class);
		PersistentDwoProfile profile = new PersistentDwoProfile(Long.valueOf(1));
    method = MethodManager.toValue(dm, school, profile);
		MethodManager.create(method);
		return method;
	}

	@Test
	public void testEdit() {
		PersistentMethod m = create();
		m.setMethod("{ }");
		Long optlock = m.getOptlock();
		m = MethodManager.edit(m);
		assertNotEquals(optlock, m.getOptlock());
	}

	@Test
	public void testDestroy() {
		MethodManager.destroy(create().getMethodID());
	}

	@Test
	public void testFindEntities() {
		List<PersistentMethod> list = MethodManager.findEntities();
		assertEquals(0, list.size());
		PersistentMethod m = create();
		list = MethodManager.findEntities();
		assertEquals(m.getMethodID(), list.get(0).getMethodID());
	}

	@Test
	public void testFindEntity() {
		PersistentMethod m = create();
		PersistentMethod m2 = MethodManager.findEntity(m.getMethodID());
		assertNotNull(m2);
	}

	@Test
	public void testGetEntityCount() {
		assertEquals(0, MethodManager.getEntityCount());
		PersistentMethod m = create();
		assertEquals(1, MethodManager.getEntityCount());
		MethodManager.destroy(m.getMethodID());
		assertEquals(0, MethodManager.getEntityCount());
	}

	@Test
	public void testtoDom() {
		PersistentMethod p = create();
		DomMethod m = MethodManager.toDom(p);
		assertEquals(p.getMethodID(), m.getId().getIdString());
	}

	
	@Test
	public void testFindEntitiesSchool() {
	  PersistentMethod p = create();
	  List<PersistentMethod> list;
	  list = MethodManager.findEntities(school, null);
	  assertEquals(1, list.size());
	  PersistentDwoProfile profile = new PersistentDwoProfile(1L);
	  list = MethodManager.findEntities(school, profile);
      assertEquals(1, list.size());
       profile = new PersistentDwoProfile(2L);
      list = MethodManager.findEntities(school, profile);
      assertTrue(list.isEmpty());
	  
	}
}
