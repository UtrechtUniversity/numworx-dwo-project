package fi.dwo.gwt.lib.rest.client;

import fi.dwo.gwt.lib.rest.CallManagers.Callback;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClass;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClasses;
import nl.uu.fi.dwo.rest.entities.RestNewSchoolLogin;
import nl.uu.fi.dwo.rest.entities.RestSchoolRoleAndClass;

public interface SecuredUserSchoolLoginRestCaller extends RestService {
    @GET
    @Path("/secure/user/account/logins/getList")
    public void getSchoolLogins(MethodCallback<DomSchoolsRolesAndClasses> callback);

    @PUT
    @Path("/rest/secure/user/account/logins/select")
    public void switchToSchoolLogin(RestSchoolRoleAndClass rsrc, Callback<DomSchoolRoleAndClass> callback);

    @PUT
    @Path("/rest/secure/user/account/logins/remove")
    public void removeASchoolLogin(RestSchoolRoleAndClass rsrc, Callback<Boolean> callback);

    @PUT
    @Path("/rest/secure/user/account/logins/submit")
    public void addASchoolLogin(RestNewSchoolLogin rnl, Callback<Boolean> callback);
}
