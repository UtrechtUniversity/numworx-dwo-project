package fi.dwo.gwt.lib.rest.client.RestCallers;

//import fi.dwo.gwt.lib.rest.CallManagers.Callback; NOTA BENE Wim: Gebruik MethodCallback niet Callback bij een RestService
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import java.util.List;
import javax.ws.rs.GET;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassFull;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.entities.RestSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestTeacher;

public interface SecuredTeacherSchoolClassRestCaller extends RestService {

    @GET
    @Path("/secure/teacher/schoolclass/getList")
    public void getTeachersSchoolClasses(MethodCallback<List<DomSchoolClass>> callback);

    @GET
    @Path("/secure/teacher/schoolclass/getTeachersInSchoolList")
    public void getTeachersInSchool(MethodCallback<List<DomTeacher>> callback);
    

    @GET
    @Path("/secure/teacher/schoolclass/getStudentsInSchoolList")
    public void getStudentsInSchool(MethodCallback<List<DomStudent>> callback);

    @PUT
    @Path("/secure/teacher/schoolclass/getTeacherList")
    public void getTeachersInSchoolClass(RestSchoolClass restData, MethodCallback<List<DomTeacher>> callback);
    
    @PUT
    @Path("/secure/teacher/schoolclass/getStudentList")
    public void getStudentsInSchoolClass(RestSchoolClass restData, MethodCallback<List<DomStudent>> callback);

    @PUT
    @Path("/secure/teacher/schoolclass/submit")
    public void submitSchoolClass(RestSchoolClassFull restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/secure/teacher/schoolclass/remove")
    public void removeSchoolClass(RestSchoolClass restSchoolClass, MethodCallback<Boolean> callback);
    

    @GET
    @Path("/secure/teacher/schoolclass/getSchoolsList")
    public void getSchoolsClasses(MethodCallback<List<DomSchoolClass>> callback);

//    @GET
//    @Path("/secure/teacher/schoolclass/getActive")
//    public void getActiveSchoolClass(Callback<DomSchoolClass> callback);

    @PUT
    @Path("/secure/teacher/schoolclass/submitTeacher")
    public void submitTeacherToSchoolClass(RestSubmitTeacherToSchoolClass restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/secure/teacher/schoolclass/removeTeacher")
    public void removeTeacherFromSchoolClass(RestTeacher teacher, MethodCallback<Boolean> callback);

    @PUT
    @Path("/secure/teacher/schoolclass/submitStudent")
    public void submitStudentToSchoolClass(RestSubmitStudentToSchoolClass restData, MethodCallback<Boolean> callback);

    @PUT
    @Path("/secure/teacher/schoolclass/removeStudent")
    public void removeStudentFromSchoolClass(RestRemoveStudentFromSchoolClass restData, MethodCallback<Boolean> callback);
    

    @PUT
    @Path("/secure/teacher/schoolclass/update")
    public void updateSchoolClass(RestSchoolClassFull schoolClass, MethodCallback<Boolean> callBack);

    @PUT
    @Path("/secure/teacher/schoolclass/getFull")
    public void getFullSchoolClass(RestSchoolClass schoolClass, MethodCallback<DomSchoolClassFull> callBack);

    @GET
    @Path("/secure/teacher/schoolclass/getSingleSchoolStudentsInSchoolList")
    public void getSingleSchoolStudentsInSchool(MethodCallback<List<DomStudent>> callback);
    
    @PUT
    @Path("/secure/teacher/schoolclass/submitSingleSchoolStudent")
    public void submitSingleSchoolStudent(RestNewSingleSchoolStudent schoolClass, MethodCallback<Boolean> callBack);
    
    @PUT
    @Path("/secure/teacher/schoolclass/getSingleSchoolStudent")
    public void getSingleSchoolStudent(RestGetSingleSchoolStudent singleSchoolStudent,MethodCallback<DomSingleSchoolStudent> callback);
    
    @PUT
    @Path("/secure/teacher/schoolclass/updateSingleSchoolStudent")
    public void updateSingleSchoolStudent(RestSingleSchoolStudent submit, MethodCallback<Boolean> callback);
    
}
