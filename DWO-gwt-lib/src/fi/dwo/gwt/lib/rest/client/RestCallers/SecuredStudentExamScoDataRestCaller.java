package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import nl.uu.fi.dwo.rest.dom.entities.DomScormValues;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScormValues;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.google.gwt.json.client.JSONValue;

public interface SecuredStudentExamScoDataRestCaller extends RestService,  ScoDataRestCaller {

  @PUT
    @Path("/sec:{id}/student/exam/scoData/getValues")
    public void getValues(@PathParam("id") String id, RestScormValues restScormValues, MethodCallback<DomScormValues> callback);

	@PUT
	@Path("/sec:{id}/student/exam/scoData/setValues")
	public void setValues(@PathParam("id") String id, RestScormValues restScormValues, MethodCallback<Boolean> callback);

	@PUT
	@Path("/sec:{id}/student/exam/scoData/setValues")
	public void setValuesETag(@PathParam("id") String id, @HeaderParam("if-match") String eTag, RestScormValues restScormValues,
			MethodCallback<Boolean> callback);
	
	@PUT
	@Path("/sec:{id}/student/exam/scoData/getJSONLaunchDataBytes")
	public void getJSONLaunchDataBytes(@PathParam("id") String id, RestScoContext rest, MethodCallback<JSONValue> callback);

	@PUT
	@Path("/sec:{id}/student/exam/scoData/patchValues")
	public void patchValues(@PathParam("id") String id, @HeaderParam("if-match") String eTag, RestScormValues restScormValues,
			MethodCallback<Boolean> callback);
}
