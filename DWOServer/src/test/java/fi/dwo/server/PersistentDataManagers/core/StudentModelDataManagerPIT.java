package fi.dwo.server.PersistentDataManagers.core;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import static fi.dwo.server.PersistentDataManagers.core.StudentModelDataManager.*;
public class StudentModelDataManagerPIT {
    static DatabaseManager instance = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
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

	@Test @Ignore
	public void testCreate() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testEdit() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testDestroy() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testFindEntities() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testFindEntitiesIntInt() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testFindEntityLong() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testGetEntityCount() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testInsertOrUpdate() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testFindEntityPersistentScoContextPersistentHasRole() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testFindEntitiesPersistentStudentModelContextPersistentHasRole() {
		fail("Not yet implemented");
	}

	@Test @Ignore
	public void testFindEntityPersistentScoContext() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindEntitiesPersistentHasRole() {
		PersistentHasRole hr;
		PersistentHasRolePK id = new PersistentHasRolePK(9L, 2L);
		hr = HasRoleManager.findEntity(id);
		List<PersistentStudentModelData> list = findEntities(hr);
		assertNotNull(list);
	}

}
