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

public interface SecuredStudentStudentModelRestCaller extends RestService{
	
		@PUT
		@Path("/sec:{id}/student/studentmodel/getList")
		void getStudentModels(@PathParam("id") String id, RestContext context, MethodCallback<List<DomStudentModelContext>> callback);

		@PUT
		@Path("/sec:{id}/student/studentmodel/getScore")
		void getStudentModelDataScore(@PathParam("id") String id, RestStudentModelContextId restModelId, MethodCallback<DomStudentModelDataScore> callback);

}
