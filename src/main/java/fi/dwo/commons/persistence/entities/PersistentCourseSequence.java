/*Copyrighted 2015. */
package fi.dwo.commons.persistence.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblcoursesequence", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentCourseSequence.findAll", query = "SELECT p FROM PersistentCourseSequence p"),
    @NamedQuery(name = "PersistentCourseSequence.findByCoursesequenceID", query = "SELECT p FROM PersistentCourseSequence p WHERE p.coursesequenceID = :coursesequenceID"),
    @NamedQuery(name = "PersistentCourseSequence.findByCourseID", query = "SELECT p FROM PersistentCourseSequence p WHERE p.courseID = :courseID"),
    @NamedQuery(name = "PersistentCourseSequence.findBySchoolID", query = "SELECT p FROM PersistentCourseSequence p WHERE p.schoolID = :schoolID"),
    @NamedQuery(name = "PersistentCourseSequence.findByClassID", query = "SELECT p FROM PersistentCourseSequence p WHERE p.classID = :classID"),
    @NamedQuery(name = "PersistentCourseSequence.findByParent", query = "SELECT p FROM PersistentCourseSequence p WHERE p.parent = :parent"),
    @NamedQuery(name = "PersistentCourseSequence.findByProfileID", query = "SELECT p FROM PersistentCourseSequence p WHERE p.profileID = :profileID"),
    @NamedQuery(name = "PersistentCourseSequence.findBySequencenr", query = "SELECT p FROM PersistentCourseSequence p WHERE p.sequencenr = :sequencenr")})
public class PersistentCourseSequence implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "coursesequenceID", nullable = false)
    private Long coursesequenceID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "courseID", nullable = false)
    private Long courseID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "schoolID", nullable = false)
    private Long schoolID;
    @Column(name = "classID")
    private Long classID;
    @Column(name = "parent")
    private Long parent;
    @Basic(optional = false)
    @NotNull
    @Column(name = "profileID", nullable = false)
    private Long profileID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "sequencenr", nullable = false)
    private Long sequencenr;

    public PersistentCourseSequence() {
    }

    public PersistentCourseSequence(Long coursesequenceID) {
        this.coursesequenceID = coursesequenceID;
    }

    public PersistentCourseSequence(Long coursesequenceID, Long courseID, Long schoolID, Long profileID, Long sequencenr) {
        this.coursesequenceID = coursesequenceID;
        this.courseID = courseID;
        this.schoolID = schoolID;
        this.profileID = profileID;
        this.sequencenr = sequencenr;
    }

    public Long getCoursesequenceID() {
        return coursesequenceID;
    }

    public void setCoursesequenceID(Long coursesequenceID) {
        this.coursesequenceID = coursesequenceID;
    }

    public Long getCourseID() {
        return courseID;
    }

    public void setCourseID(Long courseID) {
        this.courseID = courseID;
    }

    public Long getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(Long schoolID) {
        this.schoolID = schoolID;
    }

    public Long getClassID() {
        return classID;
    }

    public void setClassID(Long classID) {
        this.classID = classID;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Long getProfileID() {
        return profileID;
    }

    public void setProfileID(Long profileID) {
        this.profileID = profileID;
    }

    public Long getSequencenr() {
        return sequencenr;
    }

    public void setSequencenr(Long sequencenr) {
        this.sequencenr = sequencenr;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (coursesequenceID != null ? coursesequenceID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentCourseSequence)) {
            return false;
        }
        PersistentCourseSequence other = (PersistentCourseSequence) object;
        if ((this.coursesequenceID == null && other.coursesequenceID != null) || (this.coursesequenceID != null && !this.coursesequenceID.equals(other.coursesequenceID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentCourseSequence[ coursesequenceID=" + coursesequenceID + " ]";
    }
    
}
