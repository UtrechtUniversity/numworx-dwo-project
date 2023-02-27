package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Map;
import java.util.Set;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class DomStudentModelContext4Student extends DomStudentModelContextId {

	public DomStudentModelContext4Student(PersistenceId persistenceId) {
		super(persistenceId);
	}

	public DomStudentModelContext4Student() {
	}
    private DomStudentModelStructure modelStructure;
    private DomSchoolClassId schoolClass;
    private Map<String,Map<String,Set<Integer>>> filter;
	public DomStudentModelStructure getModelStructure() {
		return modelStructure;
	}

	public void setModelStructure(DomStudentModelStructure modelStructure) {
		this.modelStructure = modelStructure;
	}

	public DomSchoolClassId getSchoolClass() {
		return schoolClass;
	}

	public void setSchoolClass(DomSchoolClassId schoolClass) {
		this.schoolClass = schoolClass;
	}

	public Map<String, Map<String, Set<Integer>>> getFilter() {
		return filter;
	}

	public void setFilter(Map<String, Map<String, Set<Integer>>> filter) {
		this.filter = filter;
	}

}
