package fi.dwo.gwt.lib.rest.client.RestCallers;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.entities.RestNewSchoolLogin;
import nl.uu.fi.dwo.rest.entities.RestSchoolRoleAndClassV2;

public interface SecuredUserSchoolLoginRestCaller extends RestService {
    @GET
    @Path("/secure/user/account/loginsV2/getList")
    public void getSchoolLogins(MethodCallback<DomSchoolsRolesAndClassesV2> callback);

    @PUT
    @Path("/secure/user/account/loginsV2/select")
    public void switchToSchoolLogin(RestSchoolRoleAndClassV2 rsrc, MethodCallback<DomSchoolRoleAndClassV2> callback);

    @PUT
    @Path("/secure/user/account/loginsV2/remove")
    public void removeASchoolLogin(RestSchoolRoleAndClassV2 rsrc, MethodCallback<Boolean> callback);

    @PUT
    @Path("/secure/user/account/loginsV2/submit")
    public void addASchoolLogin(RestNewSchoolLogin rnl, MethodCallback<Boolean> callback);
}
