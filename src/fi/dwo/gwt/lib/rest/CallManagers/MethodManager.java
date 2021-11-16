package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.MethodRestCaller;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentMethodRestCalller;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredTeacherMethodRestCaller;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestMethod;
import nl.uu.fi.dwo.rest.util.PathId;

import static fi.dwo.gwt.lib.rest.GwtRestVars.F;

import java.util.List;

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


	public Promise<DomMethod> getMethod(DomContext context, DomMethod id, DomDwoProfileId profile) {
		RestMethod rest = new RestMethod();
		rest.setDomMethod(id);
		rest.setDomDwoProfile(profile);
		rest.setRestContext(context);
		return F(service::getMethod, PathId.getId(context), rest);
	}
	
	public Promise<List<DomMethod>> getList(DomContext context, DomDwoProfile profile) {
		RestDwoProfile rest = new RestDwoProfile();
		rest.setRestContext(context);
		rest.setDomDwoProfile(profile);
		return F(service::getList, PathId.getId(context), rest); 
	}
}
