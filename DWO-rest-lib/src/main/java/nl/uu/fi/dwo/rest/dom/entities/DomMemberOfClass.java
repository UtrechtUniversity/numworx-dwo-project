package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@XmlRootElement
public class DomMemberOfClass {

	private PersistenceId id;
	private PersistenceId classId;
	protected PersistenceId userId;

	/**
	 * @return the userId
	 */
	public PersistenceId getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(PersistenceId userId) {
		this.userId = userId;
	}

	/**
	 * @return the id
	 */
	public PersistenceId getId() {
	    return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(PersistenceId id) {
	    this.id = id;
	}

	/**
	 * @return the classId
	 */
	public PersistenceId getClassId() {
	    return classId;
	}

	/**
	 * @param classId the classId to set
	 */
	public void setClassId(PersistenceId classId) {
	    this.classId = classId;
	}

}
