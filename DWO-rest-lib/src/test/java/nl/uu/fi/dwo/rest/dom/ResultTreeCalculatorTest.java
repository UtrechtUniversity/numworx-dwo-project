package nl.uu.fi.dwo.rest.dom;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomResultCourseInClass;
import nl.uu.fi.dwo.rest.dom.entities.DomResultSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentOfClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class ResultTreeCalculatorTest {
		
	private DomResultTree tree;
	private DomMappedResultsPerTeacher mapped;
	private DomResultCourseInClass resultCourse;
	private DomResultSchoolClass<DomResultCourseInClass> resultClass;

	private static final String user01 = "LOCAL;" + PersistenceClassType.PersistentUser + ";01";
	private static final String user02 = "LOCAL;" + PersistenceClassType.PersistentUser + ";02";
	private static final String user03 = "LOCAL;" + PersistenceClassType.PersistentUser + ";03";
	
	private static final String class01 = "LOCAL;" + PersistenceClassType.PersistentSchoolClass + ";01";

	private static final String course01 = "LOCAL;" + PersistenceClassType.PersistentCourse + ";01";
	private static final String sco01 = "LOCAL;" + PersistenceClassType.PersistentScoContext  + ";01";
    private static final String sco02 = "LOCAL;" + PersistenceClassType.PersistentScoContext  + ";02";
	private static final String cc01 = "LOCAL;" + PersistenceClassType.PersistentCourseInClass + ";01;01";
	private static final String ss01 = "LOCAL;" + PersistenceClassType.PersistentStudentScoContext + ";01";
    private static final String ss02 = "LOCAL;" + PersistenceClassType.PersistentStudentScoContext + ";02";
    private static final String ss03 = "LOCAL;" + PersistenceClassType.PersistentStudentScoContext + ";03";
	private static final String studentOfClass01 = "LOCAL;" + PersistenceClassType.PersistentStudentOfClass  + ";01";
    private static final String studentOfClass02 = "LOCAL;" + PersistenceClassType.PersistentStudentOfClass  + ";02";
	
	@Before
	public void setUp() throws Exception {
		DomTeacher aTeacher = new DomTeacher();
		aTeacher.setId(new PersistenceId(user01));
		aTeacher.setFamilyName("user");
		aTeacher.setGivenName("01");
		mapped = new DomMappedResultsPerTeacher(aTeacher);
		DomCourse dc01 = new DomCourse();
		dc01.setId(new PersistenceId(course01));
		dc01.setName("course 01");
		dc01.setWithChildren(Boolean.FALSE);
		Map<PersistenceId, DomCourse> courses = Collections.singletonMap(dc01.getId(), dc01);
		mapped.setCourses(courses);
		
		DomSchoolClass dsc01 = new DomSchoolClass();
		dsc01.setId(new PersistenceId(class01));
		dsc01.setSchoolClassName("class 01");
		Map<PersistenceId, DomSchoolClass> schoolClasses = Collections.singletonMap(dsc01.getId(), dsc01);
		mapped.setSchoolClasses(schoolClasses);
		
		DomClassCourse4Teacher dcc4t01 = new DomClassCourse4Teacher();
		dcc4t01.setId(new PersistenceId(cc01));
		dcc4t01.setClassId(dsc01.getId());
		dcc4t01.setCourseId(dc01.getId());
		dcc4t01.setCourseType(CourseType.normal);
		dcc4t01.setViewState(ViewState.studentsAndTeachers);
		Map<PersistenceId, DomClassCourse4Teacher> classCourses = Collections.singletonMap(dcc4t01.getId(), dcc4t01); 
		mapped.setClassCourses(classCourses);
		
		DomStudent ds02 = new DomStudent();
		ds02.setId(new PersistenceId(user02));
		ds02.setFamilyName("Student");
		ds02.setGivenName("02");
		Map<PersistenceId, DomStudent> students = Collections.singletonMap(ds02.getId(), ds02);
		mapped.setStudents(students);
		
		DomStudentOfClass dsoc01 = new DomStudentOfClass();
		dsoc01.setClassId(dsc01.getId());
		dsoc01.setStudentId(ds02.getId());
		dsoc01.setId(new PersistenceId(studentOfClass01));
		Map<PersistenceId, DomStudentOfClass> studentsOfClasses = Collections.singletonMap(dsoc01.getId(), dsoc01);
		mapped.setStudentsOfClasses(studentsOfClasses);
		
		
		DomScoContext dsco01 = new DomScoContext();
		dsco01.setId(new PersistenceId(sco01));
		dsco01.setScoName("Sco 01");
		dsco01.setSequencenr(1L);
		dsco01.setCourseId(dc01.getId());
		
		Map<PersistenceId, DomScoContext> scoContexts = Collections.singletonMap(dsco01.getId(), dsco01);
		mapped.setScoContexts(scoContexts);
		
		DomStudentScoContext dssc01 = new DomStudentScoContext();
		dssc01.setId(new PersistenceId(ss01));
		dssc01.setUserID(ds02.getId());
		dssc01.setScoID(dsco01.getId());
		dssc01.setScore(50);
		dssc01.setTotalTime("forthnight");
		
		Map<PersistenceId, DomStudentScoContext> studentScoContexts = Collections.singletonMap(dssc01.getId(), dssc01);
		mapped.setStudentScoContexts(studentScoContexts);
		
		initTree();
	}


  private void initTree() {
    tree = new DomResultTree(mapped);
		resultClass = tree.getResultTree().getChildren().values().stream().findFirst().get();
		resultCourse = resultClass.getChildren().values().stream().findFirst().get();
  }

	
	private void addStudent03() {
      DomStudent ds03 = new DomStudent();
      ds03.setId(new PersistenceId(user03));
      ds03.setFamilyName("Student");
      ds03.setGivenName("03");
      Map<PersistenceId, DomStudent> students = Collections.singletonMap(ds03.getId(), ds03);      
      students = new HashMap<>(students); students.putAll(mapped.getStudents()); 
      mapped.setStudents(students);
      
      DomStudentOfClass dsoc01 = new DomStudentOfClass();
      DomSchoolClass dsc01 = resultClass.getSchoolClass();
      dsoc01.setClassId(dsc01.getId());
      dsoc01.setStudentId(ds03.getId());
      dsoc01.setId(new PersistenceId(studentOfClass02));
      Map<PersistenceId, DomStudentOfClass> studentsOfClasses = Collections.singletonMap(dsoc01.getId(), dsoc01);

      studentsOfClasses = new HashMap<>(studentsOfClasses); studentsOfClasses.putAll(mapped.getStudentsOfClasses());
      mapped.setStudentsOfClasses(studentsOfClasses);

      DomStudentScoContext dssc02 = new DomStudentScoContext();
      dssc02.setId(new PersistenceId(ss02));
      dssc02.setUserID(ds03.getId());
      dssc02.setScoID(mapped.getScoContexts().keySet().stream().findFirst().get());
      dssc02.setScore(10);
      
      Map<PersistenceId, DomStudentScoContext> studentScoContexts = Collections.singletonMap(dssc02.getId(), dssc02);
      studentScoContexts = new HashMap<>(studentScoContexts); studentScoContexts.putAll(mapped.getStudentScoContexts());
      mapped.setStudentScoContexts(studentScoContexts);
  
      initTree();
	}
	
	private void addSco02() {
      DomScoContext dsco2 = new DomScoContext();
      dsco2.setId(new PersistenceId(sco02));
      dsco2.setScoName("Sco 02");
      dsco2.setSequencenr(2L);
      PersistenceId dc01 = mapped.getCourses().keySet().stream().findFirst().get();
      dsco2.setCourseId(dc01);
      
      Map<PersistenceId, DomScoContext> scoContexts = Collections.singletonMap(dsco2.getId(), dsco2);
      scoContexts = new HashMap<>(scoContexts); scoContexts.putAll(mapped.getScoContexts());
      mapped.setScoContexts(scoContexts);
      
      DomStudentScoContext dssc01 = new DomStudentScoContext();
      dssc01.setId(new PersistenceId(ss03));
      PersistenceId ds02 = mapped.getStudents().keySet().stream().findFirst().get();
      dssc01.setUserID(ds02);
      dssc01.setScoID(dsco2.getId());
      dssc01.setScore(40);
      
      Map<PersistenceId, DomStudentScoContext> studentScoContexts = Collections.singletonMap(dssc01.getId(), dssc01);
      studentScoContexts = new HashMap<>(studentScoContexts); studentScoContexts.putAll(mapped.getStudentScoContexts());
      mapped.setStudentScoContexts(studentScoContexts);
	  
      initTree();
	}
	
	
	@Test
	public void testGetScoreOfTeacherClassesByLeafCourses() {
      DomResultPlotMatrix result = ResultTreeCalculator.GetScoreOfTeacherClassesByLeafCourses(tree);
      MatrixPlotter m = new MatrixPlotter(result);
      System.out.println(m);
      assertEquals("score", 50.0, m.getMark(0, 0).getScore().doubleValue(), 0.00001);
	}
    @Test
    public void testGetScoreOfTeacherClassesByLeafCourses2() {
      addStudent03();
      DomResultPlotMatrix result = ResultTreeCalculator.GetScoreOfTeacherClassesByLeafCourses(tree);
      MatrixPlotter m = new MatrixPlotter(result);
      System.out.println(m);
      assertEquals("score", 30.0, m.getMark(0, 0).getScore().doubleValue(), 0.00001);
    }
    @Test
    public void testGetScoreOfTeacherClassesByLeafCourses3() {
      addSco02();
      DomResultPlotMatrix result = ResultTreeCalculator.GetScoreOfTeacherClassesByLeafCourses(tree);
      MatrixPlotter m = new MatrixPlotter(result);
      System.out.println(m);
      assertEquals("score", 45.0, m.getMark(0, 0).getScore().doubleValue(), 0.00001);
    }
    @Test
    public void testGetScoreOfTeacherClassesByLeafCourses4() {
      addSco02();addStudent03();
      DomResultPlotMatrix result = ResultTreeCalculator.GetScoreOfTeacherClassesByLeafCourses(tree);
      MatrixPlotter m = new MatrixPlotter(result);
      System.out.println(m);
      assertEquals("score", 25.0, m.getMark(0, 0).getScore().doubleValue(), 0.00001);
    }

	@Test
	public void testGetScoreOfLeafCoursesByStudentsInClass() {
      DomResultPlotMatrix result = ResultTreeCalculator.GetScoreOfLeafCoursesByStudentsInClass(tree, resultClass);
      MatrixPlotter m = new MatrixPlotter(result);
      System.out.println(m);
      assertEquals("score", 50.0, m.getMark(0, 0).getScore().doubleValue(), 0.00001);
	}
	@Test
    public void testGetScoreOfLeafCoursesByStudentsInClass2() {
      addStudent03();
      DomResultPlotMatrix result = ResultTreeCalculator.GetScoreOfLeafCoursesByStudentsInClass(tree, resultClass);
      MatrixPlotter m = new MatrixPlotter(result);
      System.out.println(m);
      assertEquals("score", 50.0, m.getMark(0, 0).getScore().doubleValue(), 0.00001);
    }
    @Test
    public void testGetScoreOfLeafCoursesByStudentsInClass3() {
      addSco02();
      DomResultPlotMatrix result = ResultTreeCalculator.GetScoreOfLeafCoursesByStudentsInClass(tree, resultClass);
      MatrixPlotter m = new MatrixPlotter(result);
      System.out.println(m);
      assertEquals("score", 45.0, m.getMark(0, 0).getScore().doubleValue(), 0.00001);
    }
    @Test
    public void testGetScoreOfLeafCoursesByStudentsInClass4() {
      addSco02();addStudent03();
      DomResultPlotMatrix result = ResultTreeCalculator.GetScoreOfLeafCoursesByStudentsInClass(tree, resultClass);
      MatrixPlotter m = new MatrixPlotter(result);
      System.out.println(m);
      assertEquals("score", 45.0, m.getMark(0, 0).getScore().doubleValue(), 0.00001);
    }

	@Test
	public void testGetScoreOfTeacherClassesByActivitiesOfCourse() {
      DomResultPlotMatrix result = ResultTreeCalculator.GetScoreOfTeacherClassesByActivitiesOfCourse(tree, resultCourse);
      MatrixPlotter m = new MatrixPlotter(result);
      System.out.println(m);
      assertEquals("score", 50.0, m.getMark(0, 0).getScore().doubleValue(), 0.00001);
	}
    @Test
    public void testGetScoreOfTeacherClassesByActivitiesOfCourse2() {
      addStudent03();
      DomResultPlotMatrix result = ResultTreeCalculator.GetScoreOfTeacherClassesByActivitiesOfCourse(tree, resultCourse);
      MatrixPlotter m = new MatrixPlotter(result);
      System.out.println(m);
      assertEquals("score", 30.0, m.getMark(0, 0).getScore().doubleValue(), 0.00001);
    }
    @Test
    public void testGetScoreOfTeacherClassesByActivitiesOfCourse3() {
      addSco02();
      DomResultPlotMatrix result = ResultTreeCalculator.GetScoreOfTeacherClassesByActivitiesOfCourse(tree, resultCourse);
      MatrixPlotter m = new MatrixPlotter(result);
      System.out.println(m);
      assertEquals("score", 50.0, m.getMark(0, 0).getScore().doubleValue(), 0.00001);
    }
    @Test
    public void testGetScoreOfTeacherClassesByActivitiesOfCourse4() {
      addSco02();addStudent03();
      DomResultPlotMatrix result = ResultTreeCalculator.GetScoreOfTeacherClassesByActivitiesOfCourse(tree, resultCourse);
      MatrixPlotter m = new MatrixPlotter(result);
      System.out.println(m);
      assertEquals("score", 30.0, m.getMark(0, 0).getScore().doubleValue(), 0.00001);
    }

	@Test
	public void testGetScoreOfActivitiesOfCourseByStudentsInClass() {
		DomResultPlotMatrix result = ResultTreeCalculator.GetScoreOfActivitiesOfCourseByStudentsInClass(tree, resultCourse, resultClass);
		MatrixPlotter m = new MatrixPlotter(result);
		System.out.println(m);
		assertEquals("score", 50.0, m.getMark(0, 0).getScore().doubleValue(), 0.00001);
		assertEquals("totalTime", "forthnight", m.getMark(0, 0).getTotalTime());
	}
    @Test
    public void testGetScoreOfActivitiesOfCourseByStudentsInClass2() {
        addStudent03();
        DomResultPlotMatrix result = ResultTreeCalculator.GetScoreOfActivitiesOfCourseByStudentsInClass(tree, resultCourse, resultClass);
        MatrixPlotter m = new MatrixPlotter(result);
        System.out.println(m);
        assertEquals("score", 50.0, m.getMark(0, 0).getScore().doubleValue(), 0.00001);
        assertEquals("totalTime", "forthnight", m.getMark(0, 0).getTotalTime());
    }
    @Test
    public void testGetScoreOfActivitiesOfCourseByStudentsInClass3() {
        addSco02();
        DomResultPlotMatrix result = ResultTreeCalculator.GetScoreOfActivitiesOfCourseByStudentsInClass(tree, resultCourse, resultClass);
        MatrixPlotter m = new MatrixPlotter(result);
        System.out.println(m);
        assertEquals("score", 50.0, m.getMark(0, 0).getScore().doubleValue(), 0.00001);
        assertEquals("totalTime", "forthnight", m.getMark(0, 0).getTotalTime());
   }
    @Test
    public void testGetScoreOfActivitiesOfCourseByStudentsInClass4() {
        addSco02();addStudent03();
        DomResultPlotMatrix result = ResultTreeCalculator.GetScoreOfActivitiesOfCourseByStudentsInClass(tree, resultCourse, resultClass);
        MatrixPlotter m = new MatrixPlotter(result);
        System.out.println(m);
        assertEquals("score", 50.0, m.getMark(0, 0).getScore().doubleValue(), 0.00001);
        assertEquals("totalTime", "forthnight", m.getMark(0, 0).getTotalTime());
    }

//	@Test @Ignore
//	public void testGetScoreOfActivitiesByStudentsInSco() {
//		fail("Not yet implemented");
//	}

}
