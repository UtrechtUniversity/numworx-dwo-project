package fi.dwo.server.PersistentDataManagers.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.Dwo2ExceptionJavaTranslator;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoPage;
import fi.dwo.commons.persistence.entities.PersistentScoPagePK;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import nl.uu.fi.dwo.rest.dom.entities.util.ACL;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class ScoPageManagerPIT {
    static DatabaseManager instance = null;
    
    public ScoPageManagerPIT() {
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
    }
    
    @BeforeClass
    public static void setUpClass() {
        DwoEmfFactory.setEntityManagerFactory("DWO_TestDB");
        instance = new DatabaseManager();
    }

    @AfterClass
    public static void tearDownClass() {
        DwoEmfFactory.setDefaultEntityManagerFactory();
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
    public void testcreateForSco() throws Exception {
		PersistentCourse course = CourseManager.findEntity(13333L);
		List<PersistentScoContext> scos = ScoContextManager.findEntities(course);
		PersistentScoContext sco = scos.get(0);
		List<PersistentScoPage> list = ScoPageManager.find(sco);
		assertTrue(list.isEmpty());
		PersistentScoPage page = new PersistentScoPage();
		PersistentScoPagePK pk = new PersistentScoPagePK(sco.getScoID(), 0L, null);
		page.setId(pk);
		//page.setCourseID(sco.getCourseID());
		page.setCheckDocent(Boolean.TRUE);
		page.setMaxScore(100);
		page.setOptlock(sco.getOptlock());
		try {
			ScoPageManager.create(page);
		} catch (ConstraintViolationException e) {
			System.err.println(e.getConstraintViolations());
			throw e;
		}
		list = ScoPageManager.find(sco);
		assertEquals(1, list.size());
		assertEquals(pk, list.get(0).getId());
    }

    @Test public void testcreateForStudentSco() throws Exception {
    	PersistentStudentScoContext ssc = StudentScoContextManager.findEntities().get(0);
		PersistentHasRolePK role = ssc.getPersistentHasRolePK();
		PersistentScoContext sco = ScoContextManager.findEntity(ssc.getScoID());
		PersistentScoPagePK pk = new PersistentScoPagePK(sco.getScoID(), 0L, role);
		PersistentScoPage page = new PersistentScoPage();
		page.setId(pk);
		page.setCheckDocent(Boolean.FALSE);
		page.setScore(12);
		page.setCorrectie(3);
		page.setMaxScore(100);
		page.setCourseID(sco.getCourseID());
		page.setOptlock(ssc.getOptlock());
		ScoPageManager.create(page);
		pk = new PersistentScoPagePK(sco.getScoID(), 0L, null);
		page.setId(pk);
		page.setScore(null);
		page.setCorrectie(null);
		page.setCourseID(null); // is optional
		ScoPageManager.create(page);
		List<?> list = ScoPageManager.find(ssc);
		assertEquals(1, list.size());
	

    }
}
