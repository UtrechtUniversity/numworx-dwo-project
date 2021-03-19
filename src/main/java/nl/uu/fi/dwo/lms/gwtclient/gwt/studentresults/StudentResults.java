package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.List;

import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;

interface StudentResults {

	void clear();
	Promise<List<DomStudentModelContext4Student>> getModels();
	Promise<DomStudentModelDataScore> getScore(DomStudentModelContextId id);
	Promise<DomStudentModelContext4Student> getModel(DomStudentModelContextId id);
	
	Promise<String> getDescription(DomStudentModelContextId id, DomStudentModelContextInfo info);
}
