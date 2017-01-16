package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Date;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * The class defines which DomSchoolClass has which DomCourse.
 * 
 * @author G.A.J. van der Plas  email: G.A.J.vanderPlas@uu.nl
 */
@XmlRootElement
public class DomClassCourse {

    private static final Logger LOG = Logger.getLogger(DomClassCourse.class.getName());
    
    private PersistenceId id;
    private PersistenceId classId;
    private Integer type;
    private Date notBefore;
    private Date notAfter;
    private PersistenceId courseId;

    /**
     * @return the id
     */
    public PersistenceId getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(PersistenceId id) {
        this.id = id;
    }

    /**
     * @return the classId
     */
    public PersistenceId getClassId() {
        return classId;
    }

    /**
     * @param classId the classId to set
     */
    public void setClassId(PersistenceId classId) {
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
    public PersistenceId getCourseId() {
        return courseId;
    }

    /**
     * @param courseId the courseId to set
     */
    public void setCourseId(PersistenceId courseId) {
        this.courseId = courseId;
    }
}
