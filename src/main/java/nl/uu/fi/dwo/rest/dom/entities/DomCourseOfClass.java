package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DomCourse. 
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomCourseOfClass {
    private DomClassCourse4Teacher classCourse;
    private DomCourse course;

    public DomCourseOfClass(){
        
    }
    
    public DomCourseOfClass(DomCourse aCourse){
        course = aCourse;
    }
    
    public DomCourseOfClass(DomCourse aCourse, DomClassCourse4Teacher aClassCourse){
        course = aCourse;
        classCourse = aClassCourse;
    }

    /**
     * @return the classCourse
     */
    public DomClassCourse4Teacher getClassCourse() {
        return classCourse;
    }

    /**
     * @param classCourse the classCourse to set
     */
    public void setClassCourse(DomClassCourse4Teacher classCourse) {
        this.classCourse = classCourse;
    }

    /**
     * @return the course
     */
    public DomCourse getCourse() {
        return course;
    }

    /**
     * @param course the course to set
     */
    public void setCourse(DomCourse course) {
        this.course = course;
    }

}
