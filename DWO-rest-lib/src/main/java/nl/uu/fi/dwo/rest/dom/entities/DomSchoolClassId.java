package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@XmlRootElement
public class DomSchoolClassId extends DomId {

	public DomSchoolClassId() {
		super();
	}

	public DomSchoolClassId(PersistenceId id) {
		super(id);
	}
}
