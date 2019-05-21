package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.List;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.google.gwt.core.client.GWT;

import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextId;
import nl.uu.fi.dwo.rest.util.PathId;

public interface SecuredStudentStudentModelRestCaller extends RestService{
	
		@PUT
		@Path("/sec:{id}/student/studentmodel/getList")
		void getStudentModels(@PathParam("id") String id, RestContext context, MethodCallback<List<DomStudentModelContext>> callback);

		@PUT
		@Path("/sec:{id}/student/studentmodel/getScore")
		void getStudentModelDataScore(@PathParam("id") String id, RestStudentModelContextId restModelId, MethodCallback<DomStudentModelDataScore> callback);

		@PUT
		@Path("/sec:{id}/student/studentmodel/getLRS")
		void getLRS(@PathParam("id") String id, RestContext rest, MethodCallback<DomLRS> callback);
}
