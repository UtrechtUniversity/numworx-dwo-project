package fi.dwo.server.PersistentDataManagers.core;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

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

	@Test
	public void testCreate() throws Exception {
		Long classID = 2L;
		PersistentSchoolClass sc = SchoolClassManager.findEntity(classID);
		List<PersistentStudentOfClass> all = StudentOfClassManager.findEntities(sc);
		PersistentStudentOfClass toDelete = all.get(0);
		toDelete.setDelState(DelState.marked);
		toDelete = StudentOfClassManager.edit(toDelete);
		toDelete.setDelState(DelState.deleted);
		toDelete = StudentOfClassManager.edit(toDelete);
		
		PersistentStudentOfClass newSoC = new PersistentStudentOfClass(toDelete.getPersistentStudentOfClassPK());
		newSoC.setRegisterDate(new Date(System.currentTimeMillis()));
		try {
			StudentOfClassManager.create(newSoC); // dit vult optlock in met 1.
			
			fail("Should fail!");
		} catch (RollbackException e) {
// fix optlock
			PersistentStudentOfClass mock = StudentOfClassManager.findEntity(newSoC.getPersistentStudentOfClassPK());
			newSoC.setOptlock(mock.getOptlock());
		}
		try {
			newSoC = StudentOfClassManager.edit(newSoC); // zo dan, alleen als optlock is ingevuld, werkt het. maar is niet de goede waarde
		} catch (PersistenceException e) {
			throw e; // should not happen?
		}
		
		toDelete = StudentOfClassManager.findEntity(toDelete.getPersistentStudentOfClassPK());
		
		assertEquals("geen idee of dit zo is", toDelete, newSoC);
		assertEquals("register", toDelete.getRegisterDate().getDay(), newSoC.getRegisterDate().getDay());
		assertEquals("register", toDelete.getRegisterDate().getYear(), newSoC.getRegisterDate().getYear());
		assertEquals("register", toDelete.getRegisterDate().getMonth(), newSoC.getRegisterDate().getMonth());
		
		
	}
}
