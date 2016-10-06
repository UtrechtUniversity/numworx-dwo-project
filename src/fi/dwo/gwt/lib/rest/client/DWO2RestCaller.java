package fi.dwo.gwt.lib.rest.client;

import fi.dwo.gwt.lib.rest.CallManagers.Callback;
import nl.uu.fi.dwo.rest.dom.entities.DomRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClass;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClasses;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.entities.RestLoginCheck;
import nl.uu.fi.dwo.rest.entities.RestNewSchoolLogin;
import nl.uu.fi.dwo.rest.entities.RestSchoolRoleAndClass;
import nl.uu.fi.dwo.rest.entities.RestUserFull;
import java.util.List;

public interface DWO2RestCaller extends RestService {

//    @PUT
//    @Path("/public/user/loginCheck")
//    public void loginCheck(RestLoginCheck arg, MethodCallback<Boolean> callback);
//
//    @GET
//    @Path("/secure/user/account/get")
//    public void getAccountData(MethodCallback<DomUserFull> callback);
//
//    @GET
//    @Path("/secure/user/account/logins/getList")
//    public void getSchoolLogins(MethodCallback<DomSchoolsRolesAndClasses> callback);
//
//    @GET
//    @Path("/public/roles/getList")
//    public void getRoles(Callback<List<DomRole>> callback);
//
//    @PUT
//    @Path("/secure/user/account/update")
//    public void updateAccountData(RestUserFull updateUser, Callback<DomUserFull> callback);
//
//    @GET
//    @Path("/rest/secure/user/account/logins/getList")
//    public void getSchoolLogins(Callback<DomUserFull> callback);
//
//    @PUT
//    @Path("/rest/secure/user/account/logins/select")
//    public void switchToSchoolLogin(RestSchoolRoleAndClass rsrc, Callback<DomSchoolRoleAndClass> callback);
//
//    @PUT
//    @Path("/rest/secure/user/account/logins/remove")
//    public void removeASchoolLogin(RestSchoolRoleAndClass rsrc, Callback<Boolean> callback);
//
//    @PUT
//    @Path("/rest/secure/user/account/logins/submit")
//    public void addASchoolLogin(RestNewSchoolLogin rnl, Callback<Boolean> callback);
}
