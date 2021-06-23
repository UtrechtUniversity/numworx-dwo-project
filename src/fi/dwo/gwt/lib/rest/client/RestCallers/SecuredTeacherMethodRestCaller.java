package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.entities.RestMethod;

public interface SecuredTeacherMethodRestCaller extends MethodRestCaller, RestService {

	@Override
    @PUT
    @Path("/sec:{id}/teacher/method/get")
	void getMethod(@PathParam("id") String id, RestMethod rest, MethodCallback<DomMethod> callback);

}
