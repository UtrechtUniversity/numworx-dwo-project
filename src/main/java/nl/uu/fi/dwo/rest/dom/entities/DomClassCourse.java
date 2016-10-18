package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Date;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The class defines which DomSchoolClass has which DomCourse.
 * 
 * @author G.A.J. van der Plas <G.A.J.vanderPlas@uu.nl>
 */
@XmlRootElement
public class DomClassCourse {

    private static final Logger LOG = Logger.getLogger(DomClassCourse.class.getName());
    
    private Long id;
    private int classId;
    private Integer type;
    private Date notBefore;
    private Date notAfter;
    private int courseId;

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
    public int getClassId() {
        return classId;
    }

    /**
     * @param classId the classId to set
     */
    public void setClassId(int classId) {
        this.classId = classId;
    }

    /**
     * @return the type
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @return the notBefore
     */
    public Date getNotBefore() {
        return notBefore;
    }

    /**
     * @param notBefore the notBefore to set
     */
    public void setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
    }

    /**
     * @return the notAfter
     */
    public Date getNotAfter() {
        return notAfter;
    }

    /**
     * @param notAfter the notAfter to set
     */
    public void setNotAfter(Date notAfter) {
        this.notAfter = notAfter;
    }

    /**
     * @return the courseId
     */
    public int getCourseId() {
        return courseId;
    }

    /**
     * @param courseId the courseId to set
     */
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
    
}
