package fi.dwo.gwt.lib.rest.client.RestCallers;

import fi.dwo.gwt.lib.rest.CallManagers.Callback;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.entities.RestNewSchoolLogin;
import nl.uu.fi.dwo.rest.entities.RestSchoolRoleAndClassV2;

public interface SecuredUserSchoolLoginRestCallerV2 extends RestService {
    @GET
    @Path("/secure/user/account/loginsV2/getList")
    public void getSchoolLogins(MethodCallback<DomSchoolsRolesAndClassesV2> callback);

    @PUT
    @Path("/rest/secure/user/account/loginsV2/select")
    public void switchToSchoolLogin(RestSchoolRoleAndClassV2 rsrc, Callback<DomSchoolRoleAndClassV2> callback);

    @PUT
    @Path("/rest/secure/user/account/loginsV2/remove")
    public void removeASchoolLogin(RestSchoolRoleAndClassV2 rsrc, Callback<Boolean> callback);

    @PUT
    @Path("/rest/secure/user/account/lologinsV2gins/submit")
    public void addASchoolLogin(RestNewSchoolLogin rnl, Callback<Boolean> callback);
}
