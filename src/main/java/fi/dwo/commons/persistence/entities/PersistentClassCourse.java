/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.persistence.entities;

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

/**
 *
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
    private Integer classCourseID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ClassID", nullable = false)
    private int classID;
    @Column(name = "type")
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
    private int courseID;

    public PersistentClassCourse() {
    }

    public PersistentClassCourse(Integer classCourseID) {
        this.classCourseID = classCourseID;
    }

    public PersistentClassCourse(Integer classCourseID, int classID, int courseID) {
        this.classCourseID = classCourseID;
        this.classID = classID;
        this.courseID = courseID;
    }

    public Integer getClassCourseID() {
        return classCourseID;
    }

    public void setClassCourseID(Integer classCourseID) {
        this.classCourseID = classCourseID;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
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

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (classCourseID != null ? classCourseID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentClassCourse)) {
            return false;
        }
        PersistentClassCourse other = (PersistentClassCourse) object;
        if ((this.classCourseID == null && other.classCourseID != null) || (this.classCourseID != null && !this.classCourseID.equals(other.classCourseID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentClassCourse[ classCourseID=" + classCourseID + " ]";
    }
    
}
