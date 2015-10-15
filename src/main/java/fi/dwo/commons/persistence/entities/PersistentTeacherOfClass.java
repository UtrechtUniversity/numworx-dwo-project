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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Table(name = "tblteacherof", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"classID", "userID"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentTeacherOfClass.findAll", query = "SELECT p FROM PersistentTeacherOfClass p"),
    @NamedQuery(name = "PersistentTeacherOfClass.findByUserID", query = "SELECT p FROM PersistentTeacherOfClass p WHERE p.persistentTeacherOfClassPK.userID = :userID"),
    @NamedQuery(name = "PersistentTeacherOfClass.findByClassID", query = "SELECT p FROM PersistentTeacherOfClass p WHERE p.persistentTeacherOfClassPK.classID = :classID"),
    @NamedQuery(name = "PersistentTeacherOfClass.findByHasRolePK", query = "SELECT p FROM PersistentTeacherOfClass p WHERE p.persistentTeacherOfClassPK.userID = :userID and p.persistentTeacherOfClassPK.schoolGroupID= :schoolGroupID"),
    @NamedQuery(name = "PersistentTeacherOfClass.findByRegisterDate", query = "SELECT p FROM PersistentTeacherOfClass p WHERE p.registerDate = :registerDate")})
public class PersistentTeacherOfClass implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PersistentTeacherOfClassPK persistentTeacherOfClassPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "registerDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date registerDate;

    public PersistentTeacherOfClass() {
    }

    public PersistentTeacherOfClass(PersistentTeacherOfClassPK persistentTeacherOfClassPK) {
        this.persistentTeacherOfClassPK = persistentTeacherOfClassPK;
    }

    public PersistentTeacherOfClass(PersistentTeacherOfClassPK persistentTeacherOfClassPK, Date registerDate) {
        this.persistentTeacherOfClassPK = persistentTeacherOfClassPK;
        this.registerDate = registerDate;
    }

    public PersistentTeacherOfClass(int userID, int classID, int schoolGroupID) {
        this.persistentTeacherOfClassPK = new PersistentTeacherOfClassPK(userID, classID, schoolGroupID);
    }

    public PersistentTeacherOfClassPK getPersistentTeacherOfClassPK() {
        return persistentTeacherOfClassPK;
    }

    public void setPersistentTeacherOfClassPK(PersistentTeacherOfClassPK persistentTeacherOfClassPK) {
        this.persistentTeacherOfClassPK = persistentTeacherOfClassPK;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (persistentTeacherOfClassPK != null ? persistentTeacherOfClassPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentTeacherOfClass)) {
            return false;
        }
        PersistentTeacherOfClass other = (PersistentTeacherOfClass) object;
        if ((this.persistentTeacherOfClassPK == null && other.persistentTeacherOfClassPK != null) || (this.persistentTeacherOfClassPK != null && !this.persistentTeacherOfClassPK.equals(other.persistentTeacherOfClassPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentTeacherOfClass[ persistentTeacherOfClassPK=" + persistentTeacherOfClassPK + " ]";
    }
    
}
