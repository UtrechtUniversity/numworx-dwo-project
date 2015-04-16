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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblscocontext", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"sconame", "courseID"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PerisistentScoContext.findAll", query = "SELECT p FROM PerisistentScoContext p"),
    @NamedQuery(name = "PerisistentScoContext.findByScoID", query = "SELECT p FROM PerisistentScoContext p WHERE p.scoID = :scoID"),
    @NamedQuery(name = "PerisistentScoContext.findByCourseID", query = "SELECT p FROM PerisistentScoContext p WHERE p.courseID = :courseID"),
    @NamedQuery(name = "PerisistentScoContext.findByAppletID", query = "SELECT p FROM PerisistentScoContext p WHERE p.appletID = :appletID"),
    @NamedQuery(name = "PerisistentScoContext.findBySconame", query = "SELECT p FROM PerisistentScoContext p WHERE p.sconame = :sconame"),
    @NamedQuery(name = "PerisistentScoContext.findByShowscore", query = "SELECT p FROM PerisistentScoContext p WHERE p.showscore = :showscore"),
    @NamedQuery(name = "PerisistentScoContext.findBySequencenr", query = "SELECT p FROM PerisistentScoContext p WHERE p.sequencenr = :sequencenr")})
public class PerisistentScoContext implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "scoID", nullable = false)
    private Integer scoID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "courseID", nullable = false)
    private int courseID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "appletID", nullable = false)
    private int appletID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "sconame", nullable = false, length = 40)
    private String sconame;
    @Column(name = "showscore")
    private Boolean showscore;
    @Basic(optional = false)
    @NotNull
    @Column(name = "sequencenr", nullable = false)
    private int sequencenr;

    public PerisistentScoContext() {
    }

    public PerisistentScoContext(Integer scoID) {
        this.scoID = scoID;
    }

    public PerisistentScoContext(Integer scoID, int courseID, int appletID, String sconame, int sequencenr) {
        this.scoID = scoID;
        this.courseID = courseID;
        this.appletID = appletID;
        this.sconame = sconame;
        this.sequencenr = sequencenr;
    }

    public Integer getScoID() {
        return scoID;
    }

    public void setScoID(Integer scoID) {
        this.scoID = scoID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getAppletID() {
        return appletID;
    }

    public void setAppletID(int appletID) {
        this.appletID = appletID;
    }

    public String getSconame() {
        return sconame;
    }

    public void setSconame(String sconame) {
        this.sconame = sconame;
    }

    public Boolean getShowscore() {
        return showscore;
    }

    public void setShowscore(Boolean showscore) {
        this.showscore = showscore;
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
        hash += (scoID != null ? scoID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PerisistentScoContext)) {
            return false;
        }
        PerisistentScoContext other = (PerisistentScoContext) object;
        if ((this.scoID == null && other.scoID != null) || (this.scoID != null && !this.scoID.equals(other.scoID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PerisistentScoContext[ scoID=" + scoID + " ]";
    }
    
}
