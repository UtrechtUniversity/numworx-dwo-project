package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

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

    @GET
    @Path("/sec:{id}/user/account/loginsV2/getList")
    public void getSchoolLogins(@PathParam("id") String id, MethodCallback<DomSchoolsRolesAndClassesV2> callback);

    @PUT
    @Path("/sec:{id}/user/account/loginsV2/select")
    public void switchToSchoolLogin(@PathParam("id") String id, RestSchoolRoleAndClassV2 rsrc, MethodCallback<DomSchoolRoleAndClassV2> callback);

    @PUT
    @Path("/sec:{id}/user/account/loginsV2/remove")
    public void removeASchoolLogin(@PathParam("id") String id, RestSchoolRoleAndClassV2 rsrc, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/user/account/loginsV2/submit")
    public void addASchoolLogin(@PathParam("id") String id, RestNewSchoolLogin rnl, MethodCallback<Boolean> callback);
}
