/**
 * Copyrighted Sep 18, 2015
 */
package fi.dwo.server.testutil;

import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.fail;

/**
 * Tests bi-implication of roles in RoleType and the database.
 * 
 * 
 * @author Gert van der Plas
 */
public class SqlResultSetMappingIT {

    private static final Logger LOG = Logger.getLogger(SqlResultSetMappingIT.class.getName());
    
    private static DatabaseManager instance = null;

    public SqlResultSetMappingIT() {
      //  Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionJavaTranslator());
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

    /**
     * Test of getRoles method, of class PublicRoleManager. Tests for one-to-one
     * of RoleTypes mapping between persistent store and enum class. SqlResultSetMapping
     * allows for query acceleration.
     */
    @Test
    public void testSqlResultSetMapping() {
        System.out.println("testResultMap");
        EntityManager em = DwoEmfFactory.getEntityManager();
//      requires class with @sqlResultSetMapping        
//        List<PersistentCourseInClass> results = em.createNativeQuery("SELECT a.* ,b.* FROM tblclasscourse a join tblcourse b using (courseid)", "CourseInClassMapping").getResultList();
//        Allows multiple resultmappings and picking which goes where
//        List<Object[]> results = em.createNativeQuery("SELECT a.ClassCourseID, a.CourseID, a.ClassID, a.type, a.viewState, "
//                + "a.notBefore, a.notAfter, a.CourseID, b.schoolID, b.name, b.description, b.image, b.dwoProfileID, b.imageData, b.export, "
//                + "b.withChildren, b.parentID FROM tblclasscourse a join tblcourse b using (courseid)", "CourseInClassMapping").getResultList();
//      which courseId is assigned to what entitity is unknown. Any may be picked.
        List<Object[]> results = em.createQuery("SELECT a, b FROM PersistentClassCourse a, PersistentCourse b where a.courseID = b.courseID").getResultList();
        if(!(results.get(0)[1] instanceof PersistentCourse)){
        fail("wrong class type");
    }
        if(!(results.get(0)[0] instanceof PersistentClassCourse)){
        fail("wrong class type");
    }

        //create list of students classes in course
        List<Long> studentResults;
         Query q = em.createQuery("SELECT s.persistentStudentOfClassPK.classID FROM PersistentStudentOfClass s where s.persistentStudentOfClassPK.userID=:studentId and s.persistentStudentOfClassPK.schoolGroupID=:studentSgId and s.persistentStudentOfClassPK.classID in  (select toc.persistentTeacherOfClassPK.classID from PersistentTeacherOfClass toc where toc.persistentTeacherOfClassPK.userID=:teacherId and toc.persistentTeacherOfClassPK.schoolGroupID =:teacherSgId)");
         q.setParameter("teacherId", 10);
         q.setParameter("teacherSgId", 3);
         q.setParameter("studentSgId", 2);
         q.setParameter("studentId", 9);
         studentResults = q.getResultList();
         assertEquals("Should find one class.", studentResults.size(), 1);
         assertEquals("Should find class with id=2.", studentResults.get(0), Long.valueOf(2));
         

        //create list of students classes in course
        List<Long> teacherResults;
         Query q2 = em.createQuery("SELECT c.persistentTeacherOfClassPK.classID FROM PersistentTeacherOfClass c where c.persistentTeacherOfClassPK.userID=:coTeacherId and c.persistentTeacherOfClassPK.schoolGroupID=:teacherSgId and c.persistentTeacherOfClassPK.classID in  (select toc.persistentTeacherOfClassPK.classID from PersistentTeacherOfClass toc where toc.persistentTeacherOfClassPK.userID=:teacherId and toc.persistentTeacherOfClassPK.schoolGroupID =:teacherSgId)");
         q2.setParameter("teacherId", 10);
         q2.setParameter("teacherSgId", 3);
         q2.setParameter("coTeacherId", 14);
         teacherResults = q2.getResultList();
         assertEquals("Should find one class.", teacherResults.size(), 1);
         assertEquals("Should find class with id=2.", teacherResults.get(0), Long.valueOf(2));
        
    }
}
