package fi.dwo.gwt.lib.rest.client;

import fi.dwo.gwt.lib.rest.CallManagers.Callback;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.RestService;

import fi.dwo.rest.entities.RestNewSchoolClass4Student;
import fi.dwo.rest.entities.RestSchoolClass;
import java.util.List;
import javax.ws.rs.GET;

public interface SecuredStudentSchoolClassRestCaller extends RestService {

    @PUT
    @Path("/rest/secure/student/schoolclass/select")
    public void setActiveSchoolClass(RestSchoolClass restSchoolClass, Callback<Boolean> callback);

    @PUT
    @Path("/rest/secure/student/schoolclass/remove")
    public void removeSchoolClass(RestSchoolClass restSchoolClass, Callback<Boolean> callback);

    @GET
    @Path("/rest/secure/student/schoolclass/getList")
    public void getStudentsSchoolClasses(Callback<List<DomSchoolClass>> callback);
    
    @PUT
    @Path("/rest/secure/student/schoolclass/submit")
    public void registerStudentForSchoolClass(RestNewSchoolClass4Student restData, Callback<Boolean> callback);

    @GET
    @Path("/rest/secure/student/schoolclass/getSchoolsList")
    public void getSchoolsClasses(Callback<List<DomSchoolClass>> callback);

    @GET
    @Path("/rest/secure/student/schoolclass/getActive")
    public void getActiveSchoolClass(Callback<DomSchoolClass> callback);
}
