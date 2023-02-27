package fi.dwo.gwt.lib.rest.client.RestCallers;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestNewSchoolClass4Student;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import java.util.List;
import javax.ws.rs.GET;

public interface SecuredStudentSchoolClassRestCaller extends RestService {

    @PUT
    @Path("/sec:{id}/student/schoolclass/select")
    public void setActiveSchoolClass(@PathParam("id") String id, RestSchoolClass restSchoolClass, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/student/schoolclass/remove")
    public void removeSchoolClass(@PathParam("id") String id, RestSchoolClass restSchoolClass, MethodCallback<Boolean> callback);

//    @GET
//    @Path("/sec:{id}/student/schoolclass/getList")
//    @Deprecated
//    public void getStudentsSchoolClasses(@PathParam("id") String id, MethodCallback<List<DomSchoolClass>> callback);
    @PUT
    @Path("/sec:{id}/student/schoolclass/getList")
    public void getStudentsSchoolClasses(@PathParam("id") String id, RestContext rest, MethodCallback<List<DomSchoolClass>> callback);
    
    @PUT
    @Path("/sec:{id}/student/schoolclass/submit")
    public void registerStudentForSchoolClass(@PathParam("id") String id, RestNewSchoolClass4Student restData, MethodCallback<Boolean> callback);

//    @GET
//    @Path("/sec:{id}/student/schoolclass/getSchoolsList")
//    @Deprecated
//    public void getSchoolsClasses(@PathParam("id") String id, MethodCallback<List<DomSchoolClass>> callback);

    @PUT
    @Path("/sec:{id}/student/schoolclass/getSchoolsList")
    public void getSchoolsClasses(@PathParam("id") String id, RestContext rest, MethodCallback<List<DomSchoolClass>> callback);

//    @GET
//    @Path("/sec:{id}/student/schoolclass/getActive")
//    @Deprecated
//    public void getActiveSchoolClass(@PathParam("id") String id, MethodCallback<DomSchoolClass> callback);

    @PUT
    @Path("/sec:{id}/student/schoolclass/getActive")
    public void getActiveSchoolClass(@PathParam("id") String id, RestContext rest, MethodCallback<DomSchoolClass> callback);

    @PUT
    @Path("/sec:{id}/student/schoolclass/getTeacherList")
    public void getTeachersInSchoolClass(@PathParam("id") String id, RestSchoolClass restData, MethodCallback<List<DomTeacher>> callback);
    @PUT
    @Path("/sec:{id}/student/schoolclass/getStudentList")
    public void getStudentsInSchoolClass(@PathParam("id") String id, RestSchoolClass restData, MethodCallback<List<DomStudent>> callback);

    
    
    
}
