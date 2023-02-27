package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * Future use to be used as a list element for a teacher querying an overview.
 * Work in progress.
 * 
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelDataStudentScore extends DomStudentModelDataScore implements Cloneable {
    private PersistenceId studentId;

	public PersistenceId getStudentId() {
		return studentId;
	}

	public void setStudentId(PersistenceId studentId) {
		this.studentId = studentId;
	}

     
}
