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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblschool", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"schoollogin"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentSchool.findAll", query = "SELECT p FROM PersistentSchool p"),
    @NamedQuery(name = "PersistentSchool.findBySchoolID", query = "SELECT p FROM PersistentSchool p WHERE p.schoolID = :schoolID"),
    @NamedQuery(name = "PersistentSchool.findBySchoolName", query = "SELECT p FROM PersistentSchool p WHERE p.schoolName = :schoolName"),
    @NamedQuery(name = "PersistentSchool.findBySchoollogin", query = "SELECT p FROM PersistentSchool p WHERE p.schoollogin = :schoollogin"),
    @NamedQuery(name = "PersistentSchool.findByPasswordSchool", query = "SELECT p FROM PersistentSchool p WHERE p.passwordSchool = :passwordSchool"),
    @NamedQuery(name = "PersistentSchool.findByExport", query = "SELECT p FROM PersistentSchool p WHERE p.export = :export"),
    @NamedQuery(name = "PersistentSchool.findBySchoolRights", query = "SELECT p FROM PersistentSchool p WHERE p.schoolRights = :schoolRights"),
    @NamedQuery(name = "PersistentSchool.findByImage", query = "SELECT p FROM PersistentSchool p WHERE p.image = :image"),
    @NamedQuery(name = "PersistentSchool.findByExpire", query = "SELECT p FROM PersistentSchool p WHERE p.expire = :expire")})
public class PersistentSchool implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "schoolID", nullable = false)
    private Integer schoolID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "schoolName", nullable = false, length = 128)
    private String schoolName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "schoollogin", nullable = false, length = 128)
    private String schoollogin;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "passwordSchool", nullable = false, length = 128)
    private String passwordSchool;
    @Column(name = "export")
    private Boolean export;
    @Size(max = 100)
    @Column(name = "schoolRights", length = 100)
    private String schoolRights;
    @Size(max = 128)
    @Column(name = "image", length = 128)
    private String image;
    @Column(name = "expire")
    @Temporal(TemporalType.DATE)
    private Date expire;

    public PersistentSchool() {
    }

    public PersistentSchool(Integer schoolID) {
        this.schoolID = schoolID;
    }

    public PersistentSchool(Integer schoolID, String schoolName, String schoollogin, String passwordSchool) {
        this.schoolID = schoolID;
        this.schoolName = schoolName;
        this.schoollogin = schoollogin;
        this.passwordSchool = passwordSchool;
    }

    public Integer getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(Integer schoolID) {
        this.schoolID = schoolID;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoollogin() {
        return schoollogin;
    }

    public void setSchoollogin(String schoollogin) {
        this.schoollogin = schoollogin;
    }

    public String getPasswordSchool() {
        return passwordSchool;
    }

    public void setPasswordSchool(String passwordSchool) {
        this.passwordSchool = passwordSchool;
    }

    public Boolean getExport() {
        return export;
    }

    public void setExport(Boolean export) {
        this.export = export;
    }

    public String getSchoolRights() {
        return schoolRights;
    }

    public void setSchoolRights(String schoolRights) {
        this.schoolRights = schoolRights;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (schoolID != null ? schoolID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentSchool)) {
            return false;
        }
        PersistentSchool other = (PersistentSchool) object;
        if ((this.schoolID == null && other.schoolID != null) || (this.schoolID != null && !this.schoolID.equals(other.schoolID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentSchool[ schoolID=" + schoolID + " ]";
    }
    
}
