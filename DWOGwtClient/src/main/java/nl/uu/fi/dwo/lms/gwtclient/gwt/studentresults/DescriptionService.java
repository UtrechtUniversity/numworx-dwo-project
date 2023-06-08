package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;

public interface DescriptionService {
	Promise<String> getDescription(DomStudentModelContextId id, DomStudentModelContextInfo info);

}
