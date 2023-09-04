package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;

public interface PublicStudentModelRestCaller extends RestService {

	  @PUT
	  @Path("/public/studentmodel/getLRS")
	  void getLRS(RestDwoProfile restContext, MethodCallback<DomLRS> callback);

}
