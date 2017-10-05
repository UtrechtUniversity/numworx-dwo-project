package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

abstract class DomId {
	PersistenceId id;

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
