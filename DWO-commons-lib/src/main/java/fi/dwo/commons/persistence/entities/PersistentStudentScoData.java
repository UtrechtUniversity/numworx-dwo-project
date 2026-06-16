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
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.util.DelState;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblstudentscodata", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentStudentScoData.findAll", query = "SELECT p FROM PersistentStudentScoData p")
    ,
    @NamedQuery(name = "PersistentStudentScoData.findByStudentSco", query = "SELECT p FROM PersistentStudentScoData p WHERE p.studentSco = :studentSco")
    ,
    @NamedQuery(name = "PersistentStudentScoData.removeByScoIDandHasRolePK", query = "DELETE FROM PersistentStudentScoData p WHERE p.studentSco=:studentSco")})

public class PersistentStudentScoData implements Serializable, PersistentEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "studentSco", nullable = false)
    private Long studentSco;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 0, max = 16777215)
    @Column(name = "suspendData", nullable = false, length = 16777215)
    private String suspendData;
    @Lob
    @Size(max = 16777215)
    @Column(name = "cocd", length = 16777215)
    private String cocd;
    @Column(name = "optlock")
    @Version
    private Long optlock;
    @Column(name = "lastChangeTimeStamp")
    private long lastChangeTimeStamp;
    @NotNull
    @Column(name = "del", nullable = false)
    private DelState delState = DelState.not;

	public
    void changeTimestamp() {
        lastChangeTimeStamp = System.currentTimeMillis();
    }

    public PersistentStudentScoData() {
    }

    public PersistentStudentScoData(Long studentSco) {
        this.studentSco = studentSco;
    }

    public PersistentStudentScoData(Long studentSco, String suspendData) {
        this.studentSco = studentSco;
        this.suspendData = suspendData;
    }

    public Long getStudentSco() {
        return studentSco;
    }

    public void setStudentSco(Long studentSco) {
        this.studentSco = studentSco;
    }

    public String getSuspendData() {
        return suspendData;
    }

    public void setSuspendData(String suspendData) {
        this.suspendData = suspendData;
    }

    public String getCocd() {
        return cocd;
    }

    public void setCocd(String cocd) {
        this.cocd = cocd;
    }

    public Long getOptlock() {
        return optlock;
    }

    public void setOptlock(Long optlock) {
        this.optlock = optlock;
    }

    public long getLastChangeTimeStamp() {
        return lastChangeTimeStamp;
    }

    public void setLastChangeTimeStamp(long lastChangeTimeStamp) {
        this.lastChangeTimeStamp = lastChangeTimeStamp;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studentSco != null ? studentSco.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentStudentScoData)) {
            return false;
        }
        PersistentStudentScoData other = (PersistentStudentScoData) object;
        if ((this.studentSco == null && other.studentSco != null) || (this.studentSco != null && !this.studentSco.equals(other.studentSco))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentStudentScoData[ studentSco=" + studentSco + " ]";
    }

    /**
     * Builds a PersistenceId using this object's data.
     *
     * @return
     */
    public PersistenceId buildPersistenceId() {
        return buildPersistenceId(studentSco);
    }

    /**
     * Builds a persistenceId from the parameters given.
     *
     * @param aStudentScoId
     * @return
     */
    public static PersistenceId buildPersistenceId(Long aStudentScoId) {
        PersistenceId id = new PersistenceId();
        id.setIdString(String.format("MYSQL;%s;%020d",
                PersistenceClassType.PersistentStudentScoData.name(), aStudentScoId));
        return id;
    }
}
