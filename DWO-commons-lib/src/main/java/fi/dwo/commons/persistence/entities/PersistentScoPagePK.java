package fi.dwo.commons.persistence.entities;

import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class PersistentScoPagePK {
    @Basic(optional = false)
    @Column(name = "scoID", nullable = false)
    private long scoID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "sequencenr", nullable = false)
    private long sequencenr;
    /**
     * hasrole only set iff part of suspend_data.
     */
   // private PersistentHasRolePK hasRolePK;
    @Column(name = "userID", nullable = true)
    private long userID;
    @Basic
    @Column(name = "schoolGroupID", nullable = true)
    private long schoolGroupID;

    /**
	 * @return the scoID
	 */
	public Long getScoID() {
		return scoID;
	}
	/**
	 * @param scoID the scoID to set
	 */
	public void setScoID(Long scoID) {
		this.scoID = scoID;
	}
	/**
	 * @return the sequencenr
	 */
	public Long getSequencenr() {
		return sequencenr;
	}
	/**
	 * @param sequencenr the sequencenr to set
	 */
	public void setSequencenr(Long sequencenr) {
		this.sequencenr = sequencenr;
	}
	
	/**
	 * @return the hasRolePK
	 */
	public PersistentHasRolePK getHasRolePK() {
		if (userID != 0 || schoolGroupID != 0) {
			return new PersistentHasRolePK(userID, schoolGroupID);
		}
		return null;
	}
	/**
	 * @param hasRolePK the hasRolePK to set
	 */
	public void setHasRolePK(PersistentHasRolePK hasRolePK) {
		if (hasRolePK != null) {
			this.userID = hasRolePK.getUserID();
			this.schoolGroupID = hasRolePK.getSchoolGroupID();
		} else {
			this.userID = 0L;
			this.schoolGroupID = 0L;
		}
	}
	public PersistentScoPagePK() {
	}

	public PersistentScoPagePK(Long scoID, Long sequencenr, PersistentHasRolePK hasRolePK) {
		this.scoID = scoID;
		this.sequencenr = sequencenr;
		setHasRolePK( hasRolePK );
	}

	@Override
	public int hashCode() {
		return Objects.hash(schoolGroupID, scoID, sequencenr, userID);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersistentScoPagePK other = (PersistentScoPagePK) obj;
		return schoolGroupID == other.schoolGroupID && scoID == other.scoID && sequencenr == other.sequencenr
				&& userID == other.userID;
	}
	
}
