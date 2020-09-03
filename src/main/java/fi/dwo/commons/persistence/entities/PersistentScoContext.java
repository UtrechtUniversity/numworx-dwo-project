/* Copyrighted 2015.  */
package fi.dwo.commons.persistence.entities;

import fi.dwo.commons.persistence.MySQLPersistenceId;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.util.DelState;
import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;
import nl.uu.fi.dwo.rest.dom.entities.util.ScoType;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblscocontext", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"sconame", "courseID"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentScoContext.findAll", query = "SELECT p FROM PersistentScoContext p"),
    @NamedQuery(name = "PersistentScoContext.findByScoID", query = "SELECT p FROM PersistentScoContext p WHERE p.scoID = :scoID"),
    @NamedQuery(name = "PersistentScoContext.findByCourseID", query = "SELECT p FROM PersistentScoContext p WHERE p.courseID = :courseID"),
    @NamedQuery(name = "PersistentScoContext.findByAppletID", query = "SELECT p FROM PersistentScoContext p WHERE p.appletID = :appletID"),
    @NamedQuery(name = "PersistentScoContext.findBySconame", query = "SELECT p FROM PersistentScoContext p WHERE p.sconame = :sconame"),
    @NamedQuery(name = "PersistentScoContext.findByShowscore", query = "SELECT p FROM PersistentScoContext p WHERE p.showscore = :showscore"),
    @NamedQuery(name = "PersistentScoContext.findBySequencenr", query = "SELECT p FROM PersistentScoContext p WHERE p.sequencenr = :sequencenr")})
public class PersistentScoContext implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "scoID", nullable = false)
    private Long scoID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "courseID", nullable = false)
    private Long courseID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "appletID", nullable = false)
    private Long appletID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "sconame", nullable = false, length = 255)
    private String sconame;
    @Column(name = "showscore")
    private Boolean showscore;
    @Basic(optional = false)
    @NotNull
    @Column(name = "sequencenr", nullable = false)
    private Long sequencenr;

    /**
     * @since 1.5
     */
    @Column(name = "optlock")
    @Version int optlock;
    @Column(name = "lastChangeTimeStamp")
    long lastChangeTimeStamp;
    @NotNull
    @Column(name="del",nullable = false)
    private DelState delState = DelState.not;
    @NotNull
    @Column(name="publishState", nullable = false)
    private PublishState publishState = PublishState.published;
    /** 
     * @since 1.5.0 (was al in 1.5 besteld);
     */
    @Column(name="urnID")
    private Long urnID;
    @Column(name="scoType", nullable = false)
    private ScoType scoType = ScoType.OEFENEN;
    /**
     * @since 1.5.2
     */
    @Column(name="schoolID")
    private Long schoolID;
    @Column(name="dwoProfileID")
    private Long dwoProfileID;
    @Column(name="modelID")
    private Long modelID;
    
////not supported.
//    /**
//     * Last author. for ACL and publishState.
//     */
//    @Column(name="userID")
//    private Long userID;
//
//    /**
//     * if set, this is a private copy for a schoolclass.
//     */
//    @Column(name="classID")
//    private Long classID; 
     
    @PrePersist
    @PreUpdate
    void changeTimestamp() {
    	lastChangeTimeStamp = System.currentTimeMillis();
    }
   
    public PersistentScoContext() {
    }

    public PersistentScoContext(Long scoID) {
        this.scoID = scoID;
    }

    public PersistentScoContext(Long scoID, Long courseID, Long appletID, String sconame, Long sequencenr) {
        this.scoID = scoID;
        this.courseID = courseID;
        this.appletID = appletID;
        this.sconame = sconame;
        this.sequencenr = sequencenr;
    }

    public Long getScoID() {
        return scoID;
    }

    public void setScoID(Long scoID) {
        this.scoID = scoID;
    }

    public Long getCourseID() {
        return courseID;
    }

    public void setCourseID(Long courseID) {
        this.courseID = courseID;
    }

    public Long getAppletID() {
        return appletID;
    }

    public void setAppletID(Long appletID) {
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

    public Long getSequencenr() {
        return sequencenr;
    }

    public void setSequencenr(Long sequencenr) {
        this.sequencenr = sequencenr;
    }

    public Long getSchoolID() {
      return schoolID;
    }

    public void setSchoolID(Long schoolID) {
      this.schoolID = schoolID;
    }

    public Long getDwoProfileID() {
      return dwoProfileID;
    }

    public void setDwoProfileID(Long dwoProfileID) {
      this.dwoProfileID = dwoProfileID;
    }

    public Long getModelID() {
      return modelID;
    }

    public void setModelID(Long modelID) {
      this.modelID = modelID;
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
        if (!(object instanceof PersistentScoContext)) {
            return false;
        }
        PersistentScoContext other = (PersistentScoContext) object;
        if ((this.scoID == null && other.scoID != null) || (this.scoID != null && !this.scoID.equals(other.scoID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PerisistentScoContext[ scoID=" + scoID + " ]";
    }

    public DomScoContext buildDomScoContext() {
        DomScoContext scoContext = new DomScoContext();
        fillDomScoContext(scoContext);
        return scoContext;
    }

    private void fillDomScoContext(DomScoContext scoContext) {
        scoContext.setId(buildPersistenceId());
        scoContext.setAppletId(PersistentApplet.buildPersistenceId(this.appletID));
        scoContext.setCourseId(PersistentCourse.buildPersistenceId(this.courseID));
        if (this.schoolID == null) 
          scoContext.setSchoolId(null); // like PersistentCourse, schoolID may be null
        else
          scoContext.setSchoolId(PersistentSchool.buildPersistenceId(this.schoolID));
        scoContext.setStudentModelContext(PersistentStudentModelContext.buildPersistenceId(modelID));
        
        scoContext.setScoName(sconame);
        scoContext.setSequencenr(sequencenr);
        scoContext.setShowScore(showscore);
        scoContext.setScoType(scoType);
    }

    public void fillDomScoContextFull(DomScoContextFull scoContext) {
    	fillDomScoContext(scoContext);
    }
       /**
     * Builds a PersistenceId using this object's data.
     *
     * @return
     */
    public PersistenceId buildPersistenceId() {
        return buildPersistenceId(scoID);
    }

    /**
     * Builds a persistenceId from the parameters given.
     *
     * @param aScoId
     * @return
     */
    public static PersistenceId buildPersistenceId(Long aScoId) {
        PersistenceId id = new PersistenceId();
        id.setIdString(String.format("MYSQL;%s;%020d",
                PersistenceClassType.PersistentScoContext.name(), aScoId));
        return id;
    }

	public Long getUrnID() {
		return urnID;
	}

	public void setUrnID(Long urnID) {
		this.urnID = urnID;
	}

	public ScoType getScoType() {
		return scoType;
	}

	public void setScoType(ScoType scoType) {
		this.scoType = scoType;
	}
}
