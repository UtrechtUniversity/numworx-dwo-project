package nl.uu.fi.dwo.lms.gwtclient.gwt.results;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionService;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;

public class SMDescriptionService implements DescriptionService {

	@Inject SMDescriptionService() {
	}

	@Override
	public Promise<String> getDescription(DomStudentModelContextId id, DomStudentModelContextInfo info) {
		return Promises.failed(new IllegalArgumentException());
	}

	@Override
	public Promise<String> getCSS(DomStudentModelContextId current, DomStudentModelContextInfo info) {
		return Promises.resolved("");
	}

}
