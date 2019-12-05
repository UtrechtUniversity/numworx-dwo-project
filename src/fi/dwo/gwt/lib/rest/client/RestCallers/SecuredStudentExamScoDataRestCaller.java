package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import nl.uu.fi.dwo.rest.dom.entities.DomScormValues;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScormValues;
import nl.uu.fi.dwo.rest.util.PathId;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;

public class SecuredStudentExamScoDataRestCaller implements ScoDataRestCaller {

  interface Helper extends RestService {
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

  @Override
  public void getValues(RestScormValues restScormValues, MethodCallback<DomScormValues> callback) {
    helper.getValues(PathId.getId(restScormValues.getRestContext()), restScormValues, callback);
    
  }

  private final Helper helper = GWT.create(Helper.class);
  @Override
  public void setValues(RestScormValues rest, MethodCallback<Boolean> callback) {
    helper.setValues(PathId.getId(rest.getRestContext()), rest, callback);    
  }

  @Override
  public void setValuesETag(String eTag, RestScormValues rest,
      MethodCallback<Boolean> callback) {
   helper.setValuesETag(PathId.getId(rest.getRestContext()), eTag, rest, callback);
  }

  @Override
  public void patchValues(String eTag, RestScormValues rest,
      MethodCallback<Boolean> callback) {
    helper.patchValues(PathId.getId(rest.getRestContext()), eTag, rest, callback);    
  }

  @Override
  public void getJSONLaunchDataBytes(RestScoContext rest, MethodCallback<JSONValue> callback) {
    helper.getJSONLaunchDataBytes(PathId.getId(rest.getRestContext()), rest, callback);
  }
  
}
