package fi.restrpcgwt.client;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import fi.restrpcgwt.shared.entities.DomSchoolsRolesAndClasses;
import fi.restrpcgwt.shared.entities.DomUserFull;
import fi.restrpcgwt.shared.entities.RestLoginCheck;

public interface DWO2Server extends RestService {
	@PUT
	@Path("/public/user/loginCheck")
	public void loginCheck(RestLoginCheck arg, MethodCallback<Boolean> callback);
	
	@GET
	@Path("/secure/user/account/get")
	public void getCurrentUser(MethodCallback<DomUserFull> callback);

	@GET
	@Path("/secure/user/account/logins/getList")
	public void getSchoolLogins(MethodCallback<DomSchoolsRolesAndClasses> callback);
	
}
