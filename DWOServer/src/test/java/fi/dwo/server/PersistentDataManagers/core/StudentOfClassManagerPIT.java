package fi.dwo.server.PersistentDataManagers.core;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.PersistenceException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import nl.uu.fi.dwo.rest.dom.entities.util.DelState;

public class StudentOfClassManagerPIT {
    static DatabaseManager instance = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
        DwoEmfFactory.setDefaultEntityManagerFactory();
        instance = null;
	}

	@Before
	public void setUp() throws Exception {
        instance.IntializeTestDatabase();
	}

	@After
	public void tearDown() throws Exception {
        instance.ClearDatabase();
	}

	@Test
	public void testFindEntitiesPersistentSchoolClass() throws PersistenceException, Exception {
		Long classID = 2L;
		PersistentSchoolClass sc = SchoolClassManager.findEntity(classID);
		List<PersistentStudentOfClass> all = StudentOfClassManager.findEntities(sc);
		assertFalse(all.isEmpty());
		for (PersistentStudentOfClass item: all) {
			item.setDelState(DelState.marked);
			StudentOfClassManager.edit(item);		
		}
		all = StudentOfClassManager.findEntities(sc);
		assertTrue(all.isEmpty());
	}

	@Test
	public void testFindAllEntities() {
		List<PersistentStudentOfClass> all = StudentOfClassManager.findEntities();
		assertFalse(all.isEmpty());
	}
	
}
