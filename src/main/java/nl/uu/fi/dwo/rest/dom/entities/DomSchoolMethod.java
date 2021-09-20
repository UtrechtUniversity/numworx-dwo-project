package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class DomSchoolMethod extends DomId {
	
    private PersistenceId activeMethod;

	public DomSchoolMethod(PersistenceId id) {
		super(id);
	}

	public DomSchoolMethod() {
	}

	/**
	 * @return the activeMethod
	 */
	public PersistenceId getActiveMethod() {
		return activeMethod;
	}

	/**
	 * @param activeMethod the activeMethod to set
	 */
	public void setActiveMethod(PersistenceId activeMethod) {
		this.activeMethod = activeMethod;
	}

}
