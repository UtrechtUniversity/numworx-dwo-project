package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.List;

import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;

interface StudentResults {

	void clear();
	Promise<List<DomStudentModelContext>> getModels();
	Promise<DomStudentModelDataScore> getScore(DomStudentModelContextId id);
	Promise<DomStudentModelContext> getModel(DomStudentModelContextId id);
}
