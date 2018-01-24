package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import nl.uu.fi.dwo.rest.dom.entities.DomScormValues;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScormValues;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.google.gwt.json.client.JSONValue;

public interface SecuredStudentExamScoDataRestCaller extends RestService, ScoDataRestCaller {
	@PUT
    @Path("/secure/student/exam/scoData/getValues")
    public void getValues(RestScormValues restScormValues, MethodCallback<DomScormValues> callback);

	@PUT
	@Path("/secure/student/exam/scoData/setValues")
	public void setValues(RestScormValues restScormValues, MethodCallback<Boolean> callback);

	@PUT
	@Path("secure/student/exam/scoData/getJSONLaunchDataBytes")
	public void getJSONLaunchDataBytes(RestScoContext rest, MethodCallback<JSONValue> callback);

	@PUT
	@Path("/secure/student/exam/scoData/patchValues")
	public void patchValues(@HeaderParam("if-match") String eTag, RestScormValues restScormValues,
			MethodCallback<Boolean> callback);

}
