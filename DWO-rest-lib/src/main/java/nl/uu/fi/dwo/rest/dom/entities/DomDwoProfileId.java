package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class DomDwoProfileId extends DomId {

	public DomDwoProfileId(PersistenceId id) {
		super(id);
	}

	public DomDwoProfileId() {
	}

	public DomDwoProfileId duplicate() {
		return new DomDwoProfileId(getId());
	}

}
