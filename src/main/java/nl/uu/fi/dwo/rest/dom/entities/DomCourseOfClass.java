package nl.uu.fi.dwo.rest.dom.entities;

import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DomCourse. 
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomCourseOfClass {
    //not extended from DomCourse because of some issues wil occur with initalization.
    private static Logger LOG = Logger.getLogger(DomCourseOfClass.class.getName());
    private DomClassCourse classCourse;
    private DomCourse course;

    public DomCourseOfClass(){
        
    }
    
    public DomCourseOfClass(DomCourse aCourse){
        course = aCourse;
    }
    
    public DomCourseOfClass(DomCourse aCourse, DomClassCourse aClassCourse){
        course = aCourse;
        classCourse = aClassCourse;
    }

    /**
     * @return the classCourse
     */
    public DomClassCourse getClassCourse() {
        return classCourse;
    }

    /**
     * @param classCourse the classCourse to set
     */
    public void setClassCourse(DomClassCourse classCourse) {
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
