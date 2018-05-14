package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.List;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.google.gwt.core.client.GWT;

import fi.dwo.gwt.lib.rest.util.PathId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextId;

public class SecuredStudentStudentModelRestCaller {
	
	interface Helper extends RestService {
		@PUT
		@Path("/sec:{id}/student/studentmodel/getList")
		void getStudentModels(@PathParam("id") String id, RestContext context, MethodCallback<List<DomStudentModelContext>> callback);

		@PUT
		@Path("/sec:{id}/student/studentmodel/getScore")
		void getStudentModelDataScore(@PathParam("id") String id, RestStudentModelContextId restModelId, MethodCallback<DomStudentModelDataScore> callback);
		
	}

	private Helper service = GWT.create(Helper.class);
	
	public void getStudentModels(RestContext context, MethodCallback<List<DomStudentModelContext>> callback) {
		service.getStudentModels(PathId.getId(context.getRestContext()), context, callback);
		
	}

	public void getStudentModelDataScore(RestStudentModelContextId restModelId, MethodCallback<DomStudentModelDataScore> callback) {
		service.getStudentModelDataScore(PathId.getId(restModelId.getRestContext()), restModelId, callback);
	}

}
