package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;
/**
 * 
 * @author Wim van Velthoven
 *
 */
abstract class DomId {
	PersistenceId id;

	DomId(PersistenceId id) {
		super();
		this.id = id;
	}

	DomId() {
		super();
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
	
}
