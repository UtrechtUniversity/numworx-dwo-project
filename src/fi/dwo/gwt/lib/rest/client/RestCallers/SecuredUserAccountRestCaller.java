package fi.dwo.gwt.lib.rest.client.RestCallers;

import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import com.google.gwt.json.client.JSONValue;

import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.entities.RestAuthToken;
import nl.uu.fi.dwo.rest.entities.RestContext;
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
    
    @PUT
    @Path("/sec:{id}/user/account/get")
    public void getAccount(@PathParam("id") String id, RestContext rest, MethodCallback<DomUserFull> callback);

    @PUT
    @Path("/sec:{id}/user/account/update")
    public void updateAccountData(@PathParam("id") String id, RestUserFull updateUser, MethodCallback<DomUserFull> callBack);

    @PUT
    @Path("/public/user/submitSaml")
    public void getSamlUser(RestSamlUser samlRestUser, MethodCallback<DomUserFullwLoginContext> restcallback);

    @PUT
    @Path("/sec:{id}/user/account/basicAuthLogout")
    public void logout(@PathParam("id") String id, RestLoginContext loginContext, MethodCallback<Dwo2Exception> callback);

    @GET
    @Path("/secure/user/account/getLoginContext")
    public void getLoginContext(MethodCallback<DomLoginContext> callback);

    @PUT
    @Path("/public/user/getUserFromAuthToken")
	public void getUserFromAuthToken(RestAuthToken restToken,
			MethodCallback<DomUserFullwLoginContext> restcallback);
    
    @POST
    @Path("/secure/user/account/loginUser")
    public void loginUserWithPOST( @FormParam("user") String user, MethodCallback<DomUserFullwLoginContext> restcallback);

    @GET
    @Path("/sec:{id}/user/account/verifyTOTPv2")
    public void verifyTOTP(@PathParam("id") String id, MethodCallback<JSONValue> callback);
    
    @GET
    @Path("/sec:{id}/user/account/getBearerToken")
    public void getBearerToken(@PathParam("id") String id, MethodCallback<String> callback);
    
    @PUT
    @Path("/sec:{id}/user/account/linkSaml")
    public void linkSaml(@PathParam("id") String id, RestSamlUser rest, MethodCallback<Boolean> callback);

    @GET
    @Path("/sec:{id}/user/account/remove")
    public void removeCurrentUser(@PathParam("id") String id, MethodCallback<Boolean> callback);
}