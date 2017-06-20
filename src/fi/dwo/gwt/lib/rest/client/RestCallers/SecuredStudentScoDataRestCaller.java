package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import nl.uu.fi.dwo.rest.dom.entities.DomScormValues;
import nl.uu.fi.dwo.rest.entities.RestScormValues;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

public interface SecuredStudentScoDataRestCaller extends RestService {
	@PUT
    @Path("/secure/user/scoData/getValues")
    public void getValues(RestScormValues restScormValues, MethodCallback<DomScormValues> callback);

	@PUT
	@Path("/secure/user/scoData/setValues")
	public void setValues(RestScormValues restScormValues, MethodCallback<Boolean> callback);

}
