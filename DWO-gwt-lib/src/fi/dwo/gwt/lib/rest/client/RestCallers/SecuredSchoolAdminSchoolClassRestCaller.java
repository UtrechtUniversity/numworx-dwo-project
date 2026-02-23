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
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudentv2;
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
    @Path("/sec:{id}/schooladmin/schoolclass/getTeacherList")
    public void getTeachersInSchoolClass(@PathParam("id") String id, RestSchoolClass restData, MethodCallback<List<DomTeacher>> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/getStudentList")
    public void getStudentsInSchoolClass(@PathParam("id") String id, RestSchoolClass restData, MethodCallback<List<DomStudent>> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/submit")
    public void submitSchoolClass(@PathParam("id") String id, RestSchoolClassFull restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/remove")
    public void removeSchoolClass(@PathParam("id") String id, RestSchoolClass restSchoolClass, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/submitTeacher")
    public void submitTeacherToSchoolClass(@PathParam("id") String id, RestSubmitTeacherToSchoolClass restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/removeTeacher")
    public void removeTeacherFromSchoolClass(@PathParam("id") String id, RestRemoveTeacherFromSchoolClass restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/submitStudent")
    public void submitStudentToSchoolClass(@PathParam("id") String id, RestSubmitStudentToSchoolClass restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/moveStudent")
    public void moveStudentToSchoolClass(@PathParam("id") String id, RestMoveStudentToSchoolClass restData, MethodCallback<Boolean> callback);    
    
    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/removeStudent")
    public void removeStudentFromSchoolClass(@PathParam("id") String id, RestRemoveStudentFromSchoolClass restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/update")
    public void updateSchoolClass(@PathParam("id") String id, RestSchoolClassFull schoolClass, MethodCallback<Boolean> callBack);

    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/getFull")
    public void getFullSchoolClass(@PathParam("id") String id, RestSchoolClass schoolClass, MethodCallback<DomSchoolClassFull> callBack);

    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/getSingleSchoolStudentsInSchoolList")
    public void getSingleSchoolStudentsInSchool(@PathParam("id") String id, RestContext rest, MethodCallback<List<DomStudent>> callback);
    
    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/submitSingleSchoolStudent")
    public void submitSingleSchoolStudent(@PathParam("id") String id, RestNewSingleSchoolStudent schoolClass, MethodCallback<Boolean> callBack);

    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/submitSingleSchoolStudentv2")
    public void submitSingleSchoolStudentv2(@PathParam("id") String id, RestNewSingleSchoolStudentv2 schoolClass, MethodCallback<Boolean> callBack);

}
