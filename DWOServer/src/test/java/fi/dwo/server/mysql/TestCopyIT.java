package fi.dwo.server.mysql;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.server.persistence.DwoEmfFactory;

public class TestCopyIT {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateTable() {
		try {
			DwoEmfFactory.setEntityManagerFactory("DWO_CopyDB");
			EntityManager em = DwoEmfFactory.getEntityManager();
			PersistentCourse course = new PersistentCourse(2L);
			course.setName("course 1234");
			course.changeTimestamp();
			course.version = 1L;
			course.setDwoProfileID(1L);
			course.setParentID(0L);
			course.setSequencenr(2L);
			em.getTransaction().begin();
			
			PersistentCourse source = em.find(PersistentCourse.class, course.getCourseID());
			if (source != null) {
				course.version = source.version;
				course = em.merge(course);
			} else {
				em.persist(course);
			}
			
			em.getTransaction().commit();
			em.close();
			
		} catch (ConstraintViolationException e) {
			System.err.println(e.getConstraintViolations());
			e.printStackTrace();
		}
	}

}
