package fi.dwo.gwt.lib.rest.client;

import fi.dwo.gwt.lib.rest.CallManagers.Callback;
import fi.dwo.rest.dom.entities.DomRole;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import fi.dwo.rest.dom.entities.DomSchoolsRolesAndClasses;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.entities.RestLoginCheck;
import fi.dwo.rest.entities.RestUserFull;
import java.util.List;

public interface DWO2RestCaller extends RestService {
	@PUT
	@Path("/public/user/loginCheck")
	public void loginCheck(RestLoginCheck arg, MethodCallback<Boolean> callback);
	
	@GET
	@Path("/secure/user/account/get")
	public void getAccountData(MethodCallback<DomUserFull> callback);

	@GET
	@Path("/secure/user/account/logins/getList")
	public void getSchoolLogins(MethodCallback<DomSchoolsRolesAndClasses> callback);

	@GET
	@Path("/public/roles/getList")
	public void getRoles(Callback<List<DomRole>> callback);

        @PUT
        @Path("/secure/user/account/update")
        public void updateAccountData(RestUserFull updateUser, Callback<DomUserFull> callback);
	
}
