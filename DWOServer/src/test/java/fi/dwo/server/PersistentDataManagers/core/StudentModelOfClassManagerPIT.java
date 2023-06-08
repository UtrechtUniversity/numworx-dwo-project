package fi.dwo.server.PersistentDataManagers.core;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.PersistenceException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentModelOfClassPK;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class StudentModelOfClassManagerPIT {

    static DatabaseManager instance = null;

    {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
    }

    @BeforeClass
    public static void setUpClass() {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
    }

    @AfterClass
    public static void tearDownClass() {
        instance = null;
    }

    @Before
    public void setUp() {
        instance.IntializeTestDatabase();
    }

    @After
    public void tearDown() {
        instance.ClearDatabase();
    }

	@Test
	public void create() {
		PersistentStudentModelContext ctx = StudentModelContextManager.findEntity(1L);
		PersistentSchoolClass sc = SchoolClassManager.findEntity(1L);
		
		PersistentStudentModelOfClassPK key = new PersistentStudentModelOfClassPK();
		key.setClassID(sc.getClassID());
		key.setSchoolID(sc.getSchoolID());
		key.setModelID(ctx.getModelID());
		PersistentStudentModelOfClass smOf = new PersistentStudentModelOfClass();
		smOf.setId(key);
		
		StudentModelOfClassManager.create(smOf);
		
	}
	
	@Test
	public void findandedit() throws PersistenceException, Exception {
		List<PersistentStudentModelOfClass> list = StudentModelOfClassManager.findEntities();
		assertTrue(list.size()> 0);
		PersistentStudentModelOfClass item = StudentModelOfClassManager.findEntity(list.get(0).getId());
		assertEquals(list.get(0), item);
		item.setValue("[]");
		StudentModelOfClassManager.edit(item);
		
		PersistentStudentModelOfClass result = StudentModelOfClassManager.findEntity(list.get(0).getId());
		assertEquals("[]", result.getValue());
		
	}

	@Test public void destroy() {
		List<PersistentStudentModelOfClass> list = StudentModelOfClassManager.findEntities();
		assertEquals(list.size(),1);
		PersistentStudentModelOfClass item = StudentModelOfClassManager.findEntity(list.get(0).getId());
		StudentModelOfClassManager.destroy(item.getId());
		list = StudentModelOfClassManager.findEntities();
		assertTrue(list.isEmpty());
	}
	
	@Test public void findbyclass() throws Exception {
		List<PersistentStudentModelOfClass> list = StudentModelOfClassManager.findEntities();
		PersistentStudentModelOfClass sm = list.get(0);
		PersistentSchoolClass sc = new PersistentSchoolClass(sm.getId().getClassID(), sm.getId().getSchoolID(), "testklas");
		list = StudentModelOfClassManager.findEntities(sc);
		assertEquals(sm, list.get(0));
	}

	@Test public void findbymodel() throws Exception {
		List<PersistentStudentModelOfClass> list = StudentModelOfClassManager.findEntities();
		PersistentStudentModelOfClass sm = list.get(0);
		PersistentStudentModelContext sc = new PersistentStudentModelContext();
		sc.setModelID(sm.getId().getModelID());
		sc.setSchoolID(sm.getId().getSchoolID());
		
		list = StudentModelOfClassManager.findEntities(sc);
		assertEquals(sm, list.get(0));
		
	}
	
}
