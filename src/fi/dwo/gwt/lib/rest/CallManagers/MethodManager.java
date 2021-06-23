package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.MethodRestCaller;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentMethodRestCalller;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredTeacherMethodRestCaller;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.entities.RestMethod;
import nl.uu.fi.dwo.rest.util.PathId;

import static fi.dwo.gwt.lib.rest.GwtRestVars.F;

public class MethodManager {

	private final MethodRestCaller service;
	
	private MethodManager(MethodRestCaller service) {
		this.service = service;
	}
	
	public static MethodManager teacher() {
		return new MethodManager(GWT.create(SecuredTeacherMethodRestCaller.class));
	}
	public static MethodManager student() {
		return new MethodManager(GWT.create(SecuredStudentMethodRestCalller.class));
	}


	public Promise<DomMethod> getMethod(DomContext context, DomMethod id) {
		RestMethod rest = new RestMethod();
		rest.setDomMethod(id);
		rest.setRestContext(context);
		return F(service::getMethod, PathId.getId(context), rest);
	}
	
}
