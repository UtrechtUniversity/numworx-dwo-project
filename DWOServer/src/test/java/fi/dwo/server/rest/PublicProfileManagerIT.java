package fi.dwo.server.rest;

import static org.junit.Assert.*;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;

public class PublicProfileManagerIT {

	private static DatabaseManager instance;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
        DwoEmfFactory.setDefaultEntityManagerFactory();
        instance = null;
	}

	private PublicProfileManager manager;

	@Before
	public void setUp() throws Exception {
        instance.IntializeTestDatabase();
		manager = new PublicProfileManager();
	}

	@After
	public void tearDown() throws Exception {
        instance.ClearDatabase();
	}

	@Test
	public void testGet() {
		DomDwoProfileFull p = manager.get("1");
		DomDwoProfileFull q = manager.get("testprofile01");
		assertEquals(p.getDwoProfileName(), q.getDwoProfileName());
		p = manager.get("unknown");
		assertNull(p);
		p = manager.get("122132312313");
		assertNull(p);
	}

}
