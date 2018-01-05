package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class DomAppletConfigDataId extends DomId {

	public DomAppletConfigDataId(PersistenceId id) {
		super(id);
	}

	public DomAppletConfigDataId() {
	}

	public DomAppletConfigDataId duplicate() {
		return new DomAppletConfigDataId(getId());
	}

}
