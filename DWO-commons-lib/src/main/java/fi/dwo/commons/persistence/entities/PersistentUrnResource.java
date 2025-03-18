package fi.dwo.commons.persistence.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;

import nl.uu.fi.dwo.rest.dom.entities.util.DelState;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * @since 1.5
 * @author velth101
 *
 */
@Entity
@Table(name = "tblurnresource", schema = "")
public class PersistentUrnResource {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "urnID", nullable = false)
	private Long urnID;

	@Column(name = "optlock")
	@Version
	int optlock;
	@Column(name = "lastChangeTimeStamp")
	long lastChangeTimeStamp;
	@Column(name = "del")
	private DelState delState = DelState.not;

	@PrePersist
	@PreUpdate
	void changeTimestamp() {
		lastChangeTimeStamp = System.currentTimeMillis();
	}
	// content

	@Column(name = "extUUID")
	private String extUUID;

	@Column(name = "mimetype")
	private String mimeType;

	@Column(name = "chksum")
	private String checksum;

	@Column(name = "refCount")
	private Integer refCount;

	@Column(name = "byteSize")
	private Long byteSize;

	public Long getUrnID() {
		return urnID;
	}

	public void setUrnID(Long urnID) {
		this.urnID = urnID;
	}

	public int getOptlock() {
		return optlock;
	}

	public void setOptlock(int optlock) {
		this.optlock = optlock;
	}

	public DelState getDelState() {
		return delState;
	}

	public void setDelState(DelState delState) {
		this.delState = delState;
	}

	public String getExtUUID() {
		return extUUID;
	}

	public void setExtUUID(String extUUID) {
		this.extUUID = extUUID;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public Integer getRefCount() {
		return refCount;
	}

	public void setRefCount(Integer refCount) {
		this.refCount = refCount;
	}

	public Long getByteSize() {
		return byteSize;
	}

	public void setByteSize(Long byteSize) {
		this.byteSize = byteSize;
	}

	public long getLastChangeTimeStamp() {
		return lastChangeTimeStamp;
	}

	/**
	 * Builds a persistenceId from the parameters given.
	 *
	 * @param urnId
	 * @return
	 */
	public static PersistenceId buildPersistenceId(Long urnId) {
		PersistenceId id = new PersistenceId();
		id.setIdString(String.format("MYSQL;%s;%020d", PersistenceClassType.PersistentUrnResource.name(), urnId));
		return id;
	}

	/**
	 * Builds a PersistenceId using this object's data.
	 *
	 * @return
	 */
	public PersistenceId buildPersistenceId() {
		return buildPersistenceId(urnID);
	}

}
