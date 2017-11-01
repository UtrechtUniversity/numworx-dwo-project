/* Copyrighted 2015. */
package fi.dwo.commons.persistence.entities;

public class PersistentCourseInClass {
    private PersistentCourse course;
    private PersistentClassCourse classCourse;

    /**
     * @return the course
     */
    public PersistentCourse getCourse() {
        return course;
    }

    /**
     * @param course the course to set
     */
    public void setCourse(PersistentCourse course) {
        this.course = course;
    }

    /**
     * @return the classCourse
     */
    public PersistentClassCourse getClassCourse() {
        return classCourse;
    }

    /**
     * @param classCourse the classCourse to set
     */
    public void setClassCourse(PersistentClassCourse classCourse) {
        this.classCourse = classCourse;
    }
    
    public static PersistentCourseInClass build(PersistentClassCourse cc,PersistentCourse c){
        PersistentCourseInClass result = new PersistentCourseInClass();
        result.setClassCourse(cc);
        result.setCourse(c);
        return result;        
    }
}
