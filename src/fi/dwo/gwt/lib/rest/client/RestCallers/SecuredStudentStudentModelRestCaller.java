package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.List;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextId;

public interface SecuredStudentStudentModelRestCaller extends RestService {

	@PUT
	@Path("/secure/student/studentmodel/getList")
	void getStudentModels(RestContext context, MethodCallback<List<DomStudentModelContext>> callback);

	@PUT
	@Path("/secure/student/studentmodel/getScore")
	void getStudentModelDataScore(RestStudentModelContextId restModelId, MethodCallback<DomStudentModelDataScore> callback);

}
