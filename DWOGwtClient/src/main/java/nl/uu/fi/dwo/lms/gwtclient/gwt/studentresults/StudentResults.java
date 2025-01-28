package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.List;

import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

public interface StudentResults extends DescriptionService {

	void clear();
	Promise<List<DomStudentModelContext4Student>> getModels();
	Promise<DomStudentModelDataScore> getScore(DomStudentModelContextId id);
	Promise<DomStudentModelContext4Student> getModel(DomStudentModelContextId id);
	Promise<DomMethod> getActiveMethod(DomStudentModelStructure structure);
	default void setContext(DomStudentModelContext4Student context) {}
	default void updateScore(DomStudentModelContextId id) { clear(); }
	
}
