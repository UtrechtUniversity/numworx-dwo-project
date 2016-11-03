/* Copyrighted 2015. */
package fi.dwo.commons.persistence.entities;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;

/**
 * JPA/EclipseLink entity for the ClassCourses.
 * <p>
 * ClassCourses. Each classCourse links a class to a tree or sub tree in the 
 * Courses data set.
 * <p>
 * &lt; courseId, classId, type, notBefore, notAfter &gt;
 * <p>
 * <b>classCourseId</b> : Unique, non-negative and not null<br>
 * <b>courseId</b> : Unique, non-negative and not null.<br>
 * <b>classId</b> : Unique, non-negative and not null.<br>
 * <b>type</b> : Declares under which conditions a school may access it.<br>
 * <b>notBefore</b> : The referenced course module tree is not visible before notBefore. <br>
 * <b>notAfter</b> : The referenced course module tree is not visible after notAfter.<br>
 * <p>
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblclasscourse", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ClassID", "CourseID"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentClassCourse.findAll", query = "SELECT p FROM PersistentClassCourse p"),
    @NamedQuery(name = "PersistentClassCourse.findByClassCourseID", query = "SELECT p FROM PersistentClassCourse p WHERE p.classCourseID = :classCourseID"),
    @NamedQuery(name = "PersistentClassCourse.findByClassID", query = "SELECT p FROM PersistentClassCourse p WHERE p.classID = :classID"),
    @NamedQuery(name = "PersistentClassCourse.findByType", query = "SELECT p FROM PersistentClassCourse p WHERE p.type = :type"),
    @NamedQuery(name = "PersistentClassCourse.findByNotBefore", query = "SELECT p FROM PersistentClassCourse p WHERE p.notBefore = :notBefore"),
    @NamedQuery(name = "PersistentClassCourse.findByNotAfter", query = "SELECT p FROM PersistentClassCourse p WHERE p.notAfter = :notAfter"),
    @NamedQuery(name = "PersistentClassCourse.findByCourseID", query = "SELECT p FROM PersistentClassCourse p WHERE p.courseID = :courseID")})
public class PersistentClassCourse implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ClassCourseID", nullable = false)
    private long classCourseID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ClassID", nullable = false)
    private long classID;
    @Column(name = "type") //enum afschermd, normaal, todo beveiligd (safeexamebrowser), chrome
    private Integer type;
    @Column(name = "notBefore")
    @Temporal(TemporalType.TIMESTAMP)
    private Date notBefore;
    @Column(name = "notAfter")
    @Temporal(TemporalType.TIMESTAMP)
    private Date notAfter;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CourseID", nullable = false)
    private long courseID;

    public PersistentClassCourse() {
    }

    public PersistentClassCourse(long classCourseID) {
        this.classCourseID = classCourseID;
    }

    public PersistentClassCourse(long classCourseID, long classID, long courseID) {
        this.classCourseID = classCourseID;
        this.classID = classID;
        this.courseID = courseID;
    }

    public long getClassCourseID() {
        return classCourseID;
    }

    public void setClassCourseID(long classCourseID) {
        this.classCourseID = classCourseID;
    }

    public long getClassID() {
        return classID;
    }

    public void setClassID(long classID) {
        this.classID = classID;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
    }

    public Date getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(Date notAfter) {
        this.notAfter = notAfter;
    }

    public Long getCourseID() {
        return courseID;
    }

    public void setCourseID(long courseID) {
        this.courseID = courseID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += Long.hashCode(classCourseID);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentClassCourse)) {
            return false;
        }
        PersistentClassCourse other = (PersistentClassCourse) object;
        if ((this.classCourseID == other.classCourseID)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentClassCourse[ classCourseID=" + classCourseID + " ]";
    }

    public DomClassCourse buildDomClassCourse() {
        DomClassCourse classCourse = new DomClassCourse();
        PersistentClassCourse.this.fillDomClassCourse(classCourse);
        return classCourse;
    }

    private void fillDomClassCourse(DomClassCourse classCourse) {
        classCourse.setId(MySQLPersistenceId.createPersistentId(this));
        classCourse.setClassId(MySQLPersistenceId.createPersistenceId(this.classID, PersistenceClassType.PersistentSchoolClass));
        classCourse.setCourseId(MySQLPersistenceId.createPersistenceId(this.courseID, PersistenceClassType.PersistentCourse));
        classCourse.setNotAfter(this.notAfter);
        classCourse.setNotBefore(this.notBefore);
        classCourse.setType(this.type);
    }

}
