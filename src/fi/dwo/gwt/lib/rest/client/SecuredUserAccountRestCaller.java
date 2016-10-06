package fi.dwo.gwt.lib.rest.client;

import fi.dwo.gwt.lib.rest.CallManagers.Callback;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.entities.RestAuthToken;
import nl.uu.fi.dwo.rest.entities.RestLoginCheck;
import nl.uu.fi.dwo.rest.entities.RestLoginContext;
import nl.uu.fi.dwo.rest.entities.RestSamlUser;
import nl.uu.fi.dwo.rest.entities.RestUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public interface SecuredUserAccountRestCaller extends RestService {

    @PUT
    @Path("/public/user/loginCheck")
    public void loginCheck(RestLoginCheck arg, MethodCallback<Boolean> callback);

    @GET
    @Path("/secure/user/account/login")
    public void login(MethodCallback<DomUserFullwLoginContext> callback);

    @GET
    @Path("/secure/user/account/loginUser/{user}")
    public void loginUser(@PathParam("user") String user, MethodCallback<DomUserFullwLoginContext> callback);

    @GET
    @Path("/secure/user/account/get")
    public void getAccountData(MethodCallback<DomUserFull> callback);

    @PUT
    @Path("/secure/user/account/update")
    public void updateAccountData(RestUserFull updateUser, Callback<DomUserFull> callback);

    @PUT
    @Path("/public/user/submitSaml")
    public void getSamlUser(RestSamlUser samlRestUser, MethodCallback<DomUserFullwLoginContext> restcallback);

    @PUT
    @Path("/secure/user/account/basicAuthLogout")
    public void logout(RestLoginContext loginContext, MethodCallback<Dwo2Exception> callback);

    @GET
    @Path("/secure/user/account/getLoginContext")
    public void getLoginContext(MethodCallback<DomLoginContext> callback);

    @PUT
    @Path("/public/user/getUserFromAuthToken")
	public void getUserFromAuthToken(RestAuthToken restToken,
			MethodCallback<DomUserFullwLoginContext> restcallback);
    
}
