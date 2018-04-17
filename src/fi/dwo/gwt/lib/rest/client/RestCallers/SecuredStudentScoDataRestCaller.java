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

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;

import fi.dwo.gwt.lib.rest.util.PathId;

public class SecuredStudentScoDataRestCaller implements ScoDataRestCaller {

	interface Helper extends RestService {
	
		@PUT
		@Path("/sec:{id}/user/scoData/getValues")
		public void getValues(@PathParam("id") String id, RestScormValues restScormValues, MethodCallback<DomScormValues> callback);
		
		@PUT
		@Path("/sec:{id}/user/scoData/setValues")
		public void setValues(@PathParam("id") String id, RestScormValues restScormValues, MethodCallback<Boolean> callback);
		
		@PUT
		@Path("/sec:{id}/user/scoData/setValues")
		public void setValuesETag(@PathParam("id") String id, @HeaderParam("if-match") String eTag, RestScormValues restScormValues,
				MethodCallback<Boolean> callback);
		
		@PUT
		@Path("/sec:{id}/user/scoData/patchValues")
		public void patchValues(@PathParam("id") String id, @HeaderParam("if-match") String eTag, RestScormValues restScormValues,
				MethodCallback<Boolean> callback);
		
		@PUT
		@Path("sec:{id}/user/scoData/getJSONLaunchDataBytes")
		public void getJSONLaunchDataBytes(@PathParam("id") String id, RestScoContext rest, MethodCallback<JSONValue> callback);
	
	}

	Helper service = GWT.create(Helper.class);
	@Override
	public void getValues(RestScormValues restScormValues, MethodCallback<DomScormValues> callback) {
		service.getValues(PathId.getId(restScormValues.getRestContext()), restScormValues, callback);
	}

	@Override
	public void setValues(RestScormValues restScormValues, MethodCallback<Boolean> callback) {
		service.setValues(PathId.getId(restScormValues.getRestContext()), restScormValues, callback);
	}

	@Override
	public void setValuesETag(String eTag, RestScormValues restScormValues, MethodCallback<Boolean> callback) {
		service.setValuesETag(PathId.getId(restScormValues.getRestContext()), eTag, restScormValues, callback);
	}

	@Override
	public void patchValues(String eTag, RestScormValues restScormValues, MethodCallback<Boolean> callback) {
		service.patchValues(PathId.getId(restScormValues.getRestContext()), eTag, restScormValues, callback);
	}

	@Override
	public void getJSONLaunchDataBytes(RestScoContext rest, MethodCallback<JSONValue> callback) {
		service.getJSONLaunchDataBytes(PathId.getId(rest.getRestContext()), rest, callback);
	}
}
