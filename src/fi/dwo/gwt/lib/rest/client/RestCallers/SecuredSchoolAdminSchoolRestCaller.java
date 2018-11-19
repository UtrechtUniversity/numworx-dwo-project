package fi.dwo.gwt.lib.rest.client.RestCallers;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;

import java.util.List;
import javax.ws.rs.GET;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestSchoolAdmin;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.entities.RestTeacher;
import nl.uu.fi.dwo.rest.entities.RestUserFull;

public interface SecuredSchoolAdminSchoolRestCaller extends RestService {

    @GET
    @Path("/secure/schooladmin/school/getSchoolAdminList")
    public void getSchoolAdminsInSchool(MethodCallback<List<DomSchoolAdmin>> callback);

    @GET
    @Path("/secure/schooladmin/school/getTeachersInSchoolList")
    public void getTeachersInSchool(MethodCallback<List<DomTeacher>> callback);

    @GET
    @Path("/secure/schooladmin/school/getStudentsInSchoolList")
    public void getStudentsInSchool(MethodCallback<List<DomStudent>> callback);

    @PUT
    @Path("/secure/schooladmin/school/getTeacherList")
    public void getTeachersInSchoolClass(RestSchoolClass restData, MethodCallback<List<DomTeacher>> callback);

    @GET
    @Path("/secure/schooladmin/school/getSchoolClasssList")
    public void getSchoolsClasses(MethodCallback<List<DomSchoolClass>> callback);

    @GET
    @Path("/secure/schooladmin/schoolclass/getSingleSchoolStudentsInSchoolList")
    public void getSingleSchoolStudentsInSchool(MethodCallback<List<DomStudent>> callback);
    
    @PUT
    @Path("/secure/schooladmin/school/submitSingleSchoolStudent")
    public void submitSingleSchoolStudent(RestNewSingleSchoolStudent schoolClass, MethodCallback<Boolean> callBack);

    @PUT
    @Path("/secure/schooladmin/school/getSingleSchoolStudent")
    public void getSingleSchoolStudent(RestGetSingleSchoolStudent singleSchoolStudent, MethodCallback<DomSingleSchoolStudent> callback);

    @PUT
    @Path("/secure/schooladmin/school/updateSingleSchoolStudent")
    public void updateSingleSchoolStudent(RestSingleSchoolStudent submit, MethodCallback<Boolean> callback);

    @PUT
    @Path("/secure/schooladmin/school/getTeachersSchoolClassList")
    public void getTeachersSchoolClasses(RestTeacher submit, MethodCallback<List<DomSchoolClassId>> callback);

    @PUT
    @Path("/secure/schooladmin/school/submitTeacher")
    public void submitTeacher( RestUserFull teacher, MethodCallback<Boolean> callback);
 
    @PUT
    @Path("/secure/schooladmin/school/getStudentsSchoolClassList")
    public void getStudentsSchoolClasses(RestStudent restStudent, MethodCallback<List<DomSchoolClass>> callback);

    @PUT
    @Path("/secure/schooladmin/school/update")
    public void updateSchool(RestSchoolFull rest, MethodCallback<Boolean> callback);
    
    @PUT
    @Path("/secure/schooladmin/school/removeSchoolAdmin")
    public void removeSchoolAdminFromSchool(RestSchoolAdmin restSchoolAdmin, MethodCallback<Boolean> callback);
    @PUT
    @Path("/secure/schooladmin/school/removeSingleSchoolStudentFromSchool")
    public void removeSingleSchoolStudentFromSchool(RestStudent restStudent, MethodCallback<Boolean> callback);
    @PUT
    @Path("/secure/schooladmin/school/removeStudent")
    public void removeStudentFromSchool(RestStudent restStudent, MethodCallback<Boolean> callback);
    @PUT
    @Path("/secure/schooladmin/school/removeTeacher")
    public void removeTeacherFromSchool(RestTeacher restTeacher, MethodCallback<Boolean> callback);

}
