/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.persistence;

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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
@Entity
@Table(name = "tblteacherof", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"classID", "userID"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblteacherof.findAll", query = "SELECT t FROM Tblteacherof t"),
    @NamedQuery(name = "Tblteacherof.findByUserID", query = "SELECT t FROM Tblteacherof t WHERE t.tblteacherofPK.userID = :userID"),
    @NamedQuery(name = "Tblteacherof.findByClassID", query = "SELECT t FROM Tblteacherof t WHERE t.tblteacherofPK.classID = :classID"),
    @NamedQuery(name = "Tblteacherof.findByRegisterDate", query = "SELECT t FROM Tblteacherof t WHERE t.registerDate = :registerDate")})
public class Tblteacherof implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TblteacherofPK tblteacherofPK;
    @Basic(optional = false)
    @Column(name = "registerDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date registerDate;

    public Tblteacherof() {
    }

    public Tblteacherof(TblteacherofPK tblteacherofPK) {
        this.tblteacherofPK = tblteacherofPK;
    }

    public Tblteacherof(TblteacherofPK tblteacherofPK, Date registerDate) {
        this.tblteacherofPK = tblteacherofPK;
        this.registerDate = registerDate;
    }

    public Tblteacherof(int userID, int classID) {
        this.tblteacherofPK = new TblteacherofPK(userID, classID);
    }

    public TblteacherofPK getTblteacherofPK() {
        return tblteacherofPK;
    }

    public void setTblteacherofPK(TblteacherofPK tblteacherofPK) {
        this.tblteacherofPK = tblteacherofPK;
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
        hash += (tblteacherofPK != null ? tblteacherofPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblteacherof)) {
            return false;
        }
        Tblteacherof other = (Tblteacherof) object;
        if ((this.tblteacherofPK == null && other.tblteacherofPK != null) || (this.tblteacherofPK != null && !this.tblteacherofPK.equals(other.tblteacherofPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.Tblteacherof[ tblteacherofPK=" + tblteacherofPK + " ]";
    }
    
}
