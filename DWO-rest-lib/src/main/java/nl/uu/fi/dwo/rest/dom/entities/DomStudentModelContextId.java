package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 *
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelContextId extends DomId {

	public DomStudentModelContextId(PersistenceId persistenceId) {
		super(persistenceId);
	}

	public DomStudentModelContextId() {
		super();
	}

}
