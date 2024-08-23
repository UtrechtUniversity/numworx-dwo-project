package fi.dwo.server.PersistentDataManagers.util;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;

public class SchoolClassUtilManagerPIT {
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
	public void testRemoveStudentFromSchoolClass() {
		ViewState viewstate = ViewState.studentsAndTeachers; // classcourse 5 met 1 ssc
		PersistentSchoolClass schoolClass = new PersistentSchoolClass(2L);
		PersistentHasRolePK pk = new PersistentHasRolePK(9L, 2L);
		PersistentHasRole phr = new PersistentHasRole(pk);
		SchoolClassUtilManager.removeStudentFromSchoolClass(phr, schoolClass);
		Long schoolclassid = schoolClass.getClassID();
		Map<Long, Long> result = SchoolClassUtilManager.countStudentScoForClassCourse(schoolclassid, viewstate);
		assertEquals(1, result.size());
		assertEquals(Collections.singleton(5L), result.keySet());
		assertArrayEquals(Collections.singleton(0L).toArray(), result.values().toArray());
		pk.setUserID(10L);
		SchoolClassUtilManager.removeStudentFromSchoolClass(phr, schoolClass);
		pk.setUserID(11L);
		SchoolClassUtilManager.removeStudentFromSchoolClass(phr, schoolClass);

		result = SchoolClassUtilManager.countStudentScoForClassCourse(schoolclassid, viewstate);
		assertEquals(1, result.size());
		assertEquals(Collections.singleton(5L), result.keySet());
		assertArrayEquals(Collections.singleton(0L).toArray(), result.values().toArray());
		
	}
/*
 SELECT cc.ClassCourseID, count(ssc.studentsco) FROM tblclasscourse cc
join tblcourse c using(courseid)
left join tblscocontext sc on (cc.courseid = sc.courseid)
left join tblstudentof so on (cc.classid = so.classid)
left join tblstudentscocontext ssc on (so.userid = ssc.userid and sc.scoid = ssc.scoid and ssc.schoolGroupId = so.schoolgroupid)
where cc.classid = 2  and withchildren = 0 group by cc.classcourseid, cc.courseid
 */
	@Test public void testCountStudentScoForClassCourse() throws Exception {
		Long schoolclassid = 2L;
		ViewState viewstate = ViewState.studentsAndTeachers; // classcourse 5 met 1 ssc

		Map<Long, Long> result = SchoolClassUtilManager.countStudentScoForClassCourse(schoolclassid, viewstate);
		assertEquals(1, result.size());
		assertEquals(Collections.singleton(5L), result.keySet());
		assertArrayEquals(Collections.singleton(1L).toArray(), result.values().toArray());
				
		viewstate = ViewState.invisible; // classcourse 6 met 1 ssc
		result = SchoolClassUtilManager.countStudentScoForClassCourse(schoolclassid, viewstate);
		assertEquals(1, result.size());
		assertEquals(Collections.singleton(6L), result.keySet());
		assertArrayEquals(Collections.singleton(1L).toArray(), result.values().toArray());
		
	}
	
}
