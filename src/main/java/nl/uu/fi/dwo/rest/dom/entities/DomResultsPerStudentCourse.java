package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Map;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class DomResultsPerStudentCourse {

	public DomResultsPerStudentCourse() {
	}

	// TODO more to come...
	private Map<PersistenceId,DomStudentScoContext> studentScoContexts;

    
    
    
    public Map<PersistenceId, DomStudentScoContext> getStudentScoContexts() {
		return studentScoContexts;
	}
	public void setStudentScoContexts(Map<PersistenceId, DomStudentScoContext> studentScoContexts) {
		this.studentScoContexts = studentScoContexts;
	}    

}
