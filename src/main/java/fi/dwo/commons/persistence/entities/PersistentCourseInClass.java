/**
 * Copyrighted Oct 5, 2017
 */
package fi.dwo.commons.persistence.entities;

import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;

/**
 * @author Gert van der Plas
 */
 //         EntityManager em = DwoEmfFactory.getEntityManager();
 //         List<PersistentCourseInClass> results = em.createNativeQuery("SELECT a.ClassCourseID, a.CourseID, a.ClassID, b.name FROM tblclasscourse a join tblcourse b using (courseid)", "CourseInClassMapping").getResultList();
 //         System.out.println(results.size());
@SqlResultSetMapping(
        name = "CourseInClassMapping",
        entities = @EntityResult(
                entityClass = PersistentCourseInClass.class,
                fields = {
                    @FieldResult(name = "id", column = "ClassCourseID"),
                    @FieldResult(name = "classId", column = "ClassID"),
                    @FieldResult(name = "courseId", column = "CourseID"),
                    @FieldResult(name = "courseName", column = "name")}))
public class PersistentCourseInClass {
    @Id private Long id;
    @Id private Long classId;
    @Id private Long courseId;
    private String name;

    /**
     * @return the courseId
     */
    public Long getCourseId() {
        return courseId;
    }

    /**
     * @param courseId the courseId to set
     */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the classId
     */
    public Long getClassId() {
        return classId;
    }

    /**
     * @param classId the classId to set
     */
    public void setClassId(Long classId) {
        this.classId = classId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}