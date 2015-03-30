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
@Table(name = "tblstudentof", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"classID", "userID"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblstudentof.findAll", query = "SELECT t FROM Tblstudentof t"),
    @NamedQuery(name = "Tblstudentof.findByUserID", query = "SELECT t FROM Tblstudentof t WHERE t.tblstudentofPK.userID = :userID"),
    @NamedQuery(name = "Tblstudentof.findByClassID", query = "SELECT t FROM Tblstudentof t WHERE t.tblstudentofPK.classID = :classID"),
    @NamedQuery(name = "Tblstudentof.findByRegisterDate", query = "SELECT t FROM Tblstudentof t WHERE t.registerDate = :registerDate")})
public class Tblstudentof implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TblstudentofPK tblstudentofPK;
    @Basic(optional = false)
    @Column(name = "registerDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date registerDate;

    public Tblstudentof() {
    }

    public Tblstudentof(TblstudentofPK tblstudentofPK) {
        this.tblstudentofPK = tblstudentofPK;
    }

    public Tblstudentof(TblstudentofPK tblstudentofPK, Date registerDate) {
        this.tblstudentofPK = tblstudentofPK;
        this.registerDate = registerDate;
    }

    public Tblstudentof(int userID, int classID) {
        this.tblstudentofPK = new TblstudentofPK(userID, classID);
    }

    public TblstudentofPK getTblstudentofPK() {
        return tblstudentofPK;
    }

    public void setTblstudentofPK(TblstudentofPK tblstudentofPK) {
        this.tblstudentofPK = tblstudentofPK;
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
        hash += (tblstudentofPK != null ? tblstudentofPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblstudentof)) {
            return false;
        }
        Tblstudentof other = (Tblstudentof) object;
        if ((this.tblstudentofPK == null && other.tblstudentofPK != null) || (this.tblstudentofPK != null && !this.tblstudentofPK.equals(other.tblstudentofPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.Tblstudentof[ tblstudentofPK=" + tblstudentofPK + " ]";
    }
    
}
