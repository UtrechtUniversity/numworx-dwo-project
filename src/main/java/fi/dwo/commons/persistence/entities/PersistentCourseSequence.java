/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    private Integer coursesequenceID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "courseID", nullable = false)
    private int courseID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "schoolID", nullable = false)
    private int schoolID;
    @Column(name = "classID")
    private Integer classID;
    @Column(name = "parent")
    private Integer parent;
    @Basic(optional = false)
    @NotNull
    @Column(name = "profileID", nullable = false)
    private int profileID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "sequencenr", nullable = false)
    private int sequencenr;

    public PersistentCourseSequence() {
    }

    public PersistentCourseSequence(Integer coursesequenceID) {
        this.coursesequenceID = coursesequenceID;
    }

    public PersistentCourseSequence(Integer coursesequenceID, int courseID, int schoolID, int profileID, int sequencenr) {
        this.coursesequenceID = coursesequenceID;
        this.courseID = courseID;
        this.schoolID = schoolID;
        this.profileID = profileID;
        this.sequencenr = sequencenr;
    }

    public Integer getCoursesequenceID() {
        return coursesequenceID;
    }

    public void setCoursesequenceID(Integer coursesequenceID) {
        this.coursesequenceID = coursesequenceID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(int schoolID) {
        this.schoolID = schoolID;
    }

    public Integer getClassID() {
        return classID;
    }

    public void setClassID(Integer classID) {
        this.classID = classID;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public int getProfileID() {
        return profileID;
    }

    public void setProfileID(int profileID) {
        this.profileID = profileID;
    }

    public int getSequencenr() {
        return sequencenr;
    }

    public void setSequencenr(int sequencenr) {
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
