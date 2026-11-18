package fi.dwo.server.PersistentDataManagers.util;

import static org.junit.Assert.*;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoPage;
import fi.dwo.commons.persistence.entities.PersistentScoPagePK;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.ScoPageManager;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;

public class ScoPageUtilManagerPIT {

	  private static DatabaseManager instance;
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
	public void setUp() throws Exception {
      instance.IntializeTestDatabase();
	}

	@After
	public void tearDown() throws Exception {
	      instance.ClearDatabase();
	}

	@Test
	public void testTransferPages() {
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
			ScoPageManager.create(page);
			PersistentHasRolePK user = new PersistentHasRolePK(1L, 1L);
			PersistentScoPagePK npk = new PersistentScoPagePK(sco.getScoID(), 0L, user);
			page.setId(npk);
			page.setMaxScore(0);
			ScoPageManager.create(page);
		list = ScoPageManager.find(sco);
		assertEquals(1, list.size());
		assertEquals(pk, list.get(0).getId());
		ScoPageUtilManager.transferPages(sco);
		list = ScoPageManager.findAll(sco);
		assertEquals(2, list.size());
		assertEquals(list.get(0).getMaxScore(), list.get(1).getMaxScore());
		
		
		
	}

}
