/**
 * Copyrighted Sep 18, 2015
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.server.mysql.DatabaseManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import static fi.dwo.server.rest.PublicUserManagerIT.instance;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.SqlResultSetMapping;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
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
     * allows for query accelleration.
     */
    @Test
    public void testSqlResultSetMapping() {
        System.out.println("testResultMap");
        EntityManager em = DwoEmfFactory.getEntityManager();
//        List<PersistentCourseInClass> results = em.createNativeQuery("SELECT a.* ,b.* FROM tblclasscourse a join tblcourse b using (courseid)", "CourseInClassMapping").getResultList();
        List<Object[]> results = em.createNativeQuery("SELECT a.ClassCourseID, a.CourseID, a.ClassID, a.type, a.viewState, "
                + "a.notBefore, a.notAfter, a.CourseID, b.schoolID, b.name, b.description, b.image, b.dwoProfileID, b.imageData, b.export, "
                + "b.withChildren, b.parentID FROM tblclasscourse a join tblcourse b using (courseid)", "CourseInClassMapping").getResultList();
        if(!(results.get(0)[0] instanceof PersistentCourse)){
        fail("wrong class type");
    }
        if(!(results.get(0)[1] instanceof PersistentClassCourse)){
        fail("wrong class type");
    }


    }
}
