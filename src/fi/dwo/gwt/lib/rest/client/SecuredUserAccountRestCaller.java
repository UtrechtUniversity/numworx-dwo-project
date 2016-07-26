package fi.dwo.gwt.lib.rest.client;

import fi.dwo.gwt.lib.rest.CallManagers.Callback;
import fi.dwo.rest.dom.entities.DomLoginContext;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import fi.dwo.rest.entities.RestLoginCheck;
import fi.dwo.rest.entities.RestSamlUser;
import fi.dwo.rest.entities.RestUserFull;
import fi.dwo.rest.exceptions.Dwo2Exception;

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
    public void logout(DomLoginContext loginContext, MethodCallback<Dwo2Exception> callback);

}
