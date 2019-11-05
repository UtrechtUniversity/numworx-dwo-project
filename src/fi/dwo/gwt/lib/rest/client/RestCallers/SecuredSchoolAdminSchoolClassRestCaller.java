package fi.dwo.gwt.lib.rest.client.RestCallers;

import java.util.List;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestMoveStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassFull;
import nl.uu.fi.dwo.rest.entities.RestSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSubmitTeacherToSchoolClass;

public interface SecuredSchoolAdminSchoolClassRestCaller extends RestService {

    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/getList")
    public void getSchoolClasses(@PathParam("id") String id, RestContext rest, MethodCallback<List<DomSchoolClass>> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/getTeachersInSchoolList")
    public void getTeachersInSchool(@PathParam("id") String id, RestContext rest, MethodCallback<List<DomTeacher>> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/getStudentsInSchoolList")
    public void getStudentsInSchool(@PathParam("id") String id, RestContext rest, MethodCallback<List<DomStudent>> callback);
 
    @PUT
    @Path("/secure/schooladmin/schoolclass/getTeacherList")
    public void getTeachersInSchoolClass(RestSchoolClass restData, MethodCallback<List<DomTeacher>> callback);

    @PUT
    @Path("/secure/schooladmin/schoolclass/getStudentList")
    public void getStudentsInSchoolClass(RestSchoolClass restData, MethodCallback<List<DomStudent>> callback);

    @PUT
    @Path("/secure/schooladmin/schoolclass/submit")
    public void submitSchoolClass(RestSchoolClassFull restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/secure/schooladmin/schoolclass/remove")
    public void removeSchoolClass(RestSchoolClass restSchoolClass, MethodCallback<Boolean> callback);

//    @GET
//    @Path("/secure/schooladmin/schoolclass/getSchoolsList")
//    public void getSchoolsClasses(MethodCallback<List<DomSchoolClass>> callback);

    @PUT
    @Path("/secure/schooladmin/schoolclass/submitTeacher")
    public void submitTeacherToSchoolClass(RestSubmitTeacherToSchoolClass restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/secure/schooladmin/schoolclass/removeTeacher")
    public void removeTeacherFromSchoolClass(RestRemoveTeacherFromSchoolClass restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/secure/schooladmin/schoolclass/submitStudent")
    public void submitStudentToSchoolClass(RestSubmitStudentToSchoolClass restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/secure/schooladmin/schoolclass/moveStudent")
    public void moveStudentToSchoolClass(RestMoveStudentToSchoolClass restData, MethodCallback<Boolean> callback);    
    
    @PUT
    @Path("/secure/schooladmin/schoolclass/removeStudent")
    public void removeStudentFromSchoolClass(RestRemoveStudentFromSchoolClass restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/secure/schooladmin/schoolclass/update")
    public void updateSchoolClass(RestSchoolClassFull schoolClass, MethodCallback<Boolean> callBack);

    @PUT
    @Path("/secure/schooladmin/schoolclass/getFull")
    public void getFullSchoolClass(RestSchoolClass schoolClass, MethodCallback<DomSchoolClassFull> callBack);

    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/getSingleSchoolStudentsInSchoolList")
    public void getSingleSchoolStudentsInSchool(@PathParam("id") String id, RestContext rest, MethodCallback<List<DomStudent>> callback);
    
    @PUT
    @Path("/secure/schooladmin/schoolclass/submitSingleSchoolStudent")
    public void submitSingleSchoolStudent(RestNewSingleSchoolStudent schoolClass, MethodCallback<Boolean> callBack);

}
