package fi.dwo.commons.persistence.entities;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContextFull;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * StudentScoContext manager. Known issues are that is does not provide a way to
 * access data directly on a HasRolePK. Only via the component indices of the
 * HasRolePK.
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblstudentscocontext", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentStudentScoContext.findAll", query = "SELECT p FROM PersistentStudentScoContext p"),
    @NamedQuery(name = "PersistentStudentScoContext.findByTotalTime", query = "SELECT p FROM PersistentStudentScoContext p WHERE p.totalTime = :totalTime"),
    @NamedQuery(name = "PersistentStudentScoContext.findBySessionTime", query = "SELECT p FROM PersistentStudentScoContext p WHERE p.sessionTime = :sessionTime"),
    @NamedQuery(name = "PersistentStudentScoContext.findByStudentSco", query = "SELECT p FROM PersistentStudentScoContext p WHERE p.studentSco = :studentSco"),
    @NamedQuery(name = "PersistentStudentScoContext.findByScoID", query = "SELECT p FROM PersistentStudentScoContext p WHERE p.scoID = :scoID"),
    @NamedQuery(name = "PersistentStudentScoContext.findByHasRolePK", query = "SELECT p FROM PersistentStudentScoContext p WHERE p.persistentHasRolePK = :keyID"),
    @NamedQuery(name = "PersistentStudentScoContext.findByScoIDandHasRolePK", query = "SELECT p FROM PersistentStudentScoContext p WHERE p.scoID = :scoID and p.persistentHasRolePK = :keyID"),    
    @NamedQuery(name = "PersistentStudentScoContext.findByCreateDate", query = "SELECT p FROM PersistentStudentScoContext p WHERE p.createDate = :createDate"),
    @NamedQuery(name = "PersistentStudentScoContext.findByScore", query = "SELECT p FROM PersistentStudentScoContext p WHERE p.score = :score")})
public class PersistentStudentScoContext implements Serializable {

    private static final long serialVersionUID = 1L;
    @Size(max = 100)
    @Column(name = "total_time", length = 100)
    private String totalTime;
    @Size(max = 100)
    @Column(name = "session_time", length = 100)
    private String sessionTime;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "studentSco", nullable = false)
    private Long studentSco;
    @Basic(optional = false)
    @NotNull
    @Column(name = "scoID", nullable = false)
    private Long scoID;
    protected PersistentHasRolePK persistentHasRolePK;
//   @Basic(optional = false)
//    @NotNull
//    @Column(name = "userID", nullable = false)
//    private Long userID;
//    @Basic(optional = false)
//    @NotNull
//    @Column(name = "schoolGroupID", nullable = false)
//    private Long schoolGroupID;
//    @Basic(optional = false)
    @NotNull
    @Column(name = "createDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date createDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "score", nullable = false)
    private float score;
    @Column(name = "createTime")
    private java.sql.Time createTime;
    @Column(name = "completionStatus")
    private String completionStatus;
    @Column(name = "location")
    private String location;

    public PersistentStudentScoContext() {
    }

    public PersistentStudentScoContext(Long studentSco) {
        this.studentSco = studentSco;
    }

//    incomplete    
//    public PersistentStudentScoContext(Long studentSco, Long scoID, PersistentHasRolePK hasRoleKey, Date createDate, float score) {
//        this.studentSco = studentSco;
//        this.scoID = scoID;
//        this.persistentHasRolePK = hasRoleKey;
//        this.createDate = createDate;
//        this.score = score;
//    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(String sessionTime) {
        this.sessionTime = sessionTime;
    }

    public Long getStudentSco() {
        return studentSco;
    }

    public void setStudentSco(Long studentSco) {
        this.studentSco = studentSco;
    }

    public Long getScoID() {
        return scoID;
    }

    public void setScoID(Long scoID) {
        this.scoID = scoID;
    }

//    public Long getUserID() {
//        return userID;
//    }
//
//    public void setUserID(Long userID) {
//        this.userID = userID;
//    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public Time getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Time createTime) {
        this.createTime = createTime;
    }

    public String getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(String completionStatus) {
        this.completionStatus = completionStatus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public PersistentHasRolePK getPersistentHasRolePK() {
		return persistentHasRolePK;
	}

	public void setPersistentHasRolePK(PersistentHasRolePK persistentHasRolePK) {
		this.persistentHasRolePK = persistentHasRolePK;
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
        if (!(object instanceof PersistentStudentScoContext)) {
            return false;
        }
        PersistentStudentScoContext other = (PersistentStudentScoContext) object;
        if ((this.studentSco == null && other.studentSco != null) || (this.studentSco != null && !this.studentSco.equals(other.studentSco))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentStudentScoContext[ studentSco=" + studentSco + " ]";
    }

//    /**
//     * @return the schoolGroupID
//     */
//    public Long getSchoolGroupID() {
//        return schoolGroupID;
//    }
//
//    /**
//     * @param schoolGroupID the schoolGroupID to set
//     */
//    public void setSchoolGroupID(Long schoolGroupID) {
//        this.schoolGroupID = schoolGroupID;
//    }

    public DomStudentScoContext buildDomStudentScoContext() {
        DomStudentScoContext studentSco = new DomStudentScoContext();
        fillDomStudentScoContext(studentSco);
        return studentSco;
    }

    public DomStudentScoContextFull buildDomStudentScoContextFull() {
        DomStudentScoContextFull studentSco = new DomStudentScoContextFull();
        fillDomStudentScoContextFull(studentSco);
        return studentSco;
    }

    public void fillDomStudentScoContext(DomStudentScoContext studentSco) {
        if (this.studentSco != null) {
            studentSco.setId(buildPersistenceId());
        } else {
            this.studentSco = null;
        }
        studentSco.setSchoolGroupID(PersistentSchoolGroup.buildPersistenceId(persistentHasRolePK.getSchoolGroupID()));
        studentSco.setUserID(PersistentUser.buildPersistenceId(this.persistentHasRolePK.getUserID()));
        studentSco.setScore(score);
        studentSco.setScoID(PersistentScoContext.buildPersistenceId(this.scoID));
    }

    public void fillDomStudentScoContextFull(DomStudentScoContextFull studentSco) {
        fillDomStudentScoContext(studentSco);
        studentSco.setCompletionStatus(completionStatus);
        studentSco.setCreateDate(createDate);
        studentSco.setCreateTime(createTime);
        studentSco.setLocation(location);
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
                PersistenceClassType.PersistentStudentScoContext.name(), aStudentScoId));
        return id;
    }    
}
