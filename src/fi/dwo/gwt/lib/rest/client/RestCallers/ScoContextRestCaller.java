package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.List;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestScoContext;

import org.fusesource.restygwt.client.MethodCallback;

public interface ScoContextRestCaller {

	public void get(RestScoContext restScoContext,
			MethodCallback<DomScoContext> callback);

	public void getScos(RestCourse restCourse,
			MethodCallback<List<DomScoContext>> callback);

}