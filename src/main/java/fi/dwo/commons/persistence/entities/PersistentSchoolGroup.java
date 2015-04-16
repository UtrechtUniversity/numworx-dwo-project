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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblschoolgroup", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentSchoolGroup.findAll", query = "SELECT p FROM PersistentSchoolGroup p"),
    @NamedQuery(name = "PersistentSchoolGroup.findBySchoolGroupID", query = "SELECT p FROM PersistentSchoolGroup p WHERE p.schoolGroupID = :schoolGroupID"),
    @NamedQuery(name = "PersistentSchoolGroup.findByGroupID", query = "SELECT p FROM PersistentSchoolGroup p WHERE p.groupID = :groupID"),
    @NamedQuery(name = "PersistentSchoolGroup.findBySchoolID", query = "SELECT p FROM PersistentSchoolGroup p WHERE p.schoolID = :schoolID"),
    @NamedQuery(name = "PersistentSchoolGroup.findByPasswd", query = "SELECT p FROM PersistentSchoolGroup p WHERE p.passwd = :passwd")})
public class PersistentSchoolGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "schoolGroupID", nullable = false)
    private Integer schoolGroupID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "groupID", nullable = false)
    private int groupID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "schoolID", nullable = false)
    private int schoolID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "passwd", nullable = false, length = 128)
    private String passwd;

    public PersistentSchoolGroup() {
    }

    public PersistentSchoolGroup(Integer schoolGroupID) {
        this.schoolGroupID = schoolGroupID;
    }

    public PersistentSchoolGroup(Integer schoolGroupID, int groupID, int schoolID, String passwd) {
        this.schoolGroupID = schoolGroupID;
        this.groupID = groupID;
        this.schoolID = schoolID;
        this.passwd = passwd;
    }

    public Integer getSchoolGroupID() {
        return schoolGroupID;
    }

    public void setSchoolGroupID(Integer schoolGroupID) {
        this.schoolGroupID = schoolGroupID;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public int getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(int schoolID) {
        this.schoolID = schoolID;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (schoolGroupID != null ? schoolGroupID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentSchoolGroup)) {
            return false;
        }
        PersistentSchoolGroup other = (PersistentSchoolGroup) object;
        if ((this.schoolGroupID == null && other.schoolGroupID != null) || (this.schoolGroupID != null && !this.schoolGroupID.equals(other.schoolGroupID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentSchoolGroup[ schoolGroupID=" + schoolGroupID + " ]";
    }
    
}
