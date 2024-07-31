package fi.dwo.gwt.lib.rest.client.RestCallers;

//import fi.dwo.gwt.lib.rest.CallManagers.Callback; NOTA BENE Wim: Gebruik MethodCallback niet Callback bij een RestService
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import java.util.List;
//import javax.ws.rs.GET;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacher;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass4Teacherv2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestMoveStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseAndProfile;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseAndProfileNew;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewAccessKey;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewFrom;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewTo;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassCourseProfilewType;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassFull;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.entities.RestSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestTeacher;

public interface SecuredTeacherSchoolClassRestCaller extends RestService {

//    @GET
//    @Path("/sec:{id}/teacher/schoolclass/getList")
//    @Deprecated
//    public void getTeachersSchoolClasses(@PathParam("id") String id, MethodCallback<List<DomSchoolClass>> callback);

//    @GET
//    @Path("/sec:{id}/teacher/schoolclass/getTeachersInSchoolList")
//    @Deprecated
//    public void getTeachersInSchool(@PathParam("id") String id, MethodCallback<List<DomTeacher>> callback);

//    @GET
//    @Path("/sec:{id}/teacher/schoolclass/getStudentsInSchoolList")
//    @Deprecated
//    public void getStudentsInSchool(@PathParam("id") String id, MethodCallback<List<DomStudent>> callback);

    
    @PUT
    @Path("/sec:{id}/teacher/schoolclass/getList")
    public void getTeachersSchoolClasses(@PathParam("id") String id, RestContext context, MethodCallback<List<DomSchoolClass>> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/getTeachersInSchoolList")
    public void getTeachersInSchool(@PathParam("id") String id, RestContext context, MethodCallback<List<DomTeacher>> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/getStudentsInSchoolList")
    public void getStudentsInSchool(@PathParam("id") String id, RestContext context, MethodCallback<List<DomStudent>> callback);

    
    @PUT
    @Path("/sec:{id}/teacher/schoolclass/getTeacherList")
    public void getTeachersInSchoolClass(@PathParam("id") String id, RestSchoolClass restData, MethodCallback<List<DomTeacher>> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/getStudentList")
    public void getStudentsInSchoolClass(@PathParam("id") String id, RestSchoolClass restData, MethodCallback<List<DomStudent>> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/submit")
    public void submitSchoolClass(@PathParam("id") String id, RestSchoolClassFull restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/remove")
    public void removeSchoolClass(@PathParam("id") String id, RestSchoolClass restSchoolClass, MethodCallback<Boolean> callback);

//    @GET
//    @Path("/sec:{id}/teacher/schoolclass/getSchoolsList")
//    @Deprecated
//    public void getSchoolsClasses(@PathParam("id") String id, MethodCallback<List<DomSchoolClass>> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/getSchoolsList")
    public void getSchoolsClasses(@PathParam("id") String id, RestContext context, MethodCallback<List<DomSchoolClass>> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/submitTeacher")
    public void submitTeacherToSchoolClass(@PathParam("id") String id, RestSubmitTeacherToSchoolClass restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/removeTeacher")
    public void removeTeacherFromSchoolClass(@PathParam("id") String id, RestRemoveTeacherFromSchoolClass restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/submitStudent")
    public void submitStudentToSchoolClass(@PathParam("id") String id, RestSubmitStudentToSchoolClass restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/moveStudent")
    public void moveStudentToSchoolClass(@PathParam("id") String id, RestMoveStudentToSchoolClass restData, MethodCallback<Boolean> callback);    
    
    @PUT
    @Path("/sec:{id}/teacher/schoolclass/removeStudent")
    public void removeStudentFromSchoolClass(@PathParam("id") String id, RestRemoveStudentFromSchoolClass restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/update")
    public void updateSchoolClass(@PathParam("id") String id, RestSchoolClassFull schoolClass, MethodCallback<Boolean> callBack);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/getFull")
    public void getFullSchoolClass(@PathParam("id") String id, RestSchoolClass schoolClass, MethodCallback<DomSchoolClassFull> callBack);

//    @GET
//    @Path("/sec:{id}/teacher/schoolclass/getSingleSchoolStudentsInSchoolList")
//    @Deprecated
//    public void getSingleSchoolStudentsInSchool(@PathParam("id") String id, MethodCallback<List<DomStudent>> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/getSingleSchoolStudentsInSchoolList")
    public void getSingleSchoolStudentsInSchool(@PathParam("id") String id, RestContext context, MethodCallback<List<DomStudent>> callback);
    
    @PUT
    @Path("/sec:{id}/teacher/schoolclass/submitSingleSchoolStudent")
    public void submitSingleSchoolStudent(@PathParam("id") String id, RestNewSingleSchoolStudent schoolClass, MethodCallback<Boolean> callBack);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/getSingleSchoolStudent")
    public void getSingleSchoolStudent(@PathParam("id") String id, RestGetSingleSchoolStudent singleSchoolStudent, MethodCallback<DomSingleSchoolStudent> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/updateSingleSchoolStudent")
    public void updateSingleSchoolStudent(@PathParam("id") String id, RestSingleSchoolStudent submit, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/getModules")
    public void getModules(@PathParam("id") String id, RestSchoolClassAndProfile submit, MethodCallback<DomCoursesOfSchoolClass4Teacher> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/getModulesv2")
    public void getModulesv2(@PathParam("id") String id, RestSchoolClassAndProfile submit, MethodCallback<DomCoursesOfSchoolClass4Teacherv2> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/addCourseToClass")
    public void addCourseToClass(@PathParam("id") String id, RestSchoolClassCourseAndProfileNew submit, MethodCallback<Boolean> callback);
    
    @PUT
    @Path("/sec:{id}/teacher/schoolclass/attachCourseToClass")
    public void attachCourseToClass(@PathParam("id") String id, RestSchoolClassCourseAndProfile submit, MethodCallback<Boolean> callback);
    
    @PUT
    @Path("/sec:{id}/teacher/schoolclass/detachCourseFromClass")
    public void detachCourseFromClass(@PathParam("id") String id, RestSchoolClassCourseAndProfile submit, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/setFromDateClassCourse")
    public void setFromDateClassCourse(@PathParam("id") String id, RestSchoolClassCourseProfilewFrom submit, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/setToDateClassCourse")
    public void setToDateClassCourse(@PathParam("id") String id, RestSchoolClassCourseProfilewTo submit, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/setClassCourseType")
    public void setClassCourseType(@PathParam("id") String id, RestSchoolClassCourseProfilewType submit, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/setAccessKeyClassCourse")
	public void setAccessKeyClassCourse(@PathParam("id") String id, RestSchoolClassCourseProfilewAccessKey rest,
			MethodCallback<Boolean> promiseCallback);

//    @GET
//    @Path("/sec:{id}/teacher/schoolclass/getTeachersStudents")
//    @Deprecated
//    public void getTeachersStudents(@PathParam("id") String id, MethodCallback<List<DomStudent>> callback);
 
    @PUT
    @Path("/sec:{id}/teacher/schoolclass/getTeachersStudents")
    public void getTeachersStudents(@PathParam("id") String id, RestContext rest, MethodCallback<List<DomStudent>> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/getTeachersClassesOfStudent")
    public void getTeachersClassesOfStudent(@PathParam("id") String id, RestStudent submit, MethodCallback<List<DomSchoolClassId>> callback);

    @PUT
    @Path("/sec:{id}/teacher/schoolclass/getSharedTeacherClasses")
    public void getSharedTeacherClasses(@PathParam("id") String id, RestTeacher submit, MethodCallback<List<DomSchoolClassId>> callback);
}
