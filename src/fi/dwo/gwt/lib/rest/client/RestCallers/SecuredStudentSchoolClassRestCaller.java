package fi.dwo.gwt.lib.rest.client.RestCallers;

import fi.dwo.gwt.lib.rest.CallManagers.Callback;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.entities.RestNewSchoolClass4Student;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import java.util.List;
import javax.ws.rs.GET;

public interface SecuredStudentSchoolClassRestCaller extends RestService {

    @PUT
    @Path("/sec:{id}/student/schoolclass/select")
    public void setActiveSchoolClass(@PathParam("id") String id, RestSchoolClass restSchoolClass, Callback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/student/schoolclass/remove")
    public void removeSchoolClass(@PathParam("id") String id, RestSchoolClass restSchoolClass, Callback<Boolean> callback);

    @GET
    @Path("/sec:{id}/student/schoolclass/getList")
    public void getStudentsSchoolClasses(@PathParam("id") String id, Callback<List<DomSchoolClass>> callback);
    
    @PUT
    @Path("/sec:{id}/student/schoolclass/submit")
    public void registerStudentForSchoolClass(@PathParam("id") String id, RestNewSchoolClass4Student restData, Callback<Boolean> callback);

    @GET
    @Path("/sec:{id}/student/schoolclass/getSchoolsList")
    public void getSchoolsClasses(@PathParam("id") String id, Callback<List<DomSchoolClass>> callback);

    @GET
    @Path("/sec:{id}/student/schoolclass/getActive")
    public void getActiveSchoolClass(@PathParam("id") String id, Callback<DomSchoolClass> callback);
}
