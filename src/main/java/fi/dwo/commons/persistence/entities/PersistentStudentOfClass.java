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
@Table(name = "tblstudentof", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"classID", "userID"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentStudentOfClass.findAll", query = "SELECT p FROM PersistentStudentOfClass p"),
    @NamedQuery(name = "PersistentStudentOfClass.findByUserID", query = "SELECT p FROM PersistentStudentOfClass p WHERE p.persistentStudentOfClassPK.userID = :userID"),
    @NamedQuery(name = "PersistentStudentOfClass.findByClassID", query = "SELECT p FROM PersistentStudentOfClass p WHERE p.persistentStudentOfClassPK.classID = :classID"),
    @NamedQuery(name = "PersistentStudentOfClass.findByHasRolePK", query = "SELECT p FROM PersistentStudentOfClass p WHERE p.userID = :userID and p.schoolGroupID= :schoolGroupID"),
    @NamedQuery(name = "PersistentStudentOfClass.findByRegisterDate", query = "SELECT p FROM PersistentStudentOfClass p WHERE p.registerDate = :registerDate")})
public class PersistentStudentOfClass implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PersistentStudentOfClassPK persistentStudentOfClassPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "registerDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date registerDate;

    public PersistentStudentOfClass() {
    }

    public PersistentStudentOfClass(PersistentStudentOfClassPK persistentStudentOfClassPK) {
        this.persistentStudentOfClassPK = persistentStudentOfClassPK;
    }

    public PersistentStudentOfClass(PersistentStudentOfClassPK persistentStudentOfClassPK, Date registerDate) {
        this.persistentStudentOfClassPK = persistentStudentOfClassPK;
        this.registerDate = registerDate;
    }

    public PersistentStudentOfClass(int userID, int classID, int schoolGroupID) {
        this.persistentStudentOfClassPK = new PersistentStudentOfClassPK(userID, classID, schoolGroupID);
    }

    public PersistentStudentOfClassPK getPersistentStudentOfClassPK() {
        return persistentStudentOfClassPK;
    }

    public void setPersistentStudentOfClassPK(PersistentStudentOfClassPK persistentStudentOfClassPK) {
        this.persistentStudentOfClassPK = persistentStudentOfClassPK;
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
        hash += (persistentStudentOfClassPK != null ? persistentStudentOfClassPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentStudentOfClass)) {
            return false;
        }
        PersistentStudentOfClass other = (PersistentStudentOfClass) object;
        if ((this.persistentStudentOfClassPK == null && other.persistentStudentOfClassPK != null) || (this.persistentStudentOfClassPK != null && !this.persistentStudentOfClassPK.equals(other.persistentStudentOfClassPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentStudentOfClass[ persistentStudentOfClassPK=" + persistentStudentOfClassPK + " ]";
    }
    
}
