package fi.dwo.gwt.lib.rest.client.RestCallers;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdminAndHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import nl.uu.fi.dwo.rest.entities.RestSchoolOrganisation;

import java.util.List;
//import javax.ws.rs.GET;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolOrganisation;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherAndHasRole;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestSchoolAdmin;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.entities.RestTeacher;
import nl.uu.fi.dwo.rest.entities.RestUserFull;

public interface SecuredSchoolAdminSchoolRestCaller extends RestService {

    @PUT
    @Path("/sec:{id}/schooladmin/school/getSchoolAdminList")
    public void getSchoolAdminsInSchool(@PathParam("id") String id, RestContext rest, MethodCallback<List<DomSchoolAdmin>> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/school/getTeachersInSchoolList")
    public void getTeachersInSchool(@PathParam("id") String id, RestContext rest, MethodCallback<List<DomTeacher>> callback);

    @PUT
    @Path("sec:{id}/schooladmin/school/getTeachersAndHasRoleInSchool")
    public void getTeachersAndHasRoleInSchool(@PathParam("id") String id, RestContext rest, MethodCallback<List<DomTeacherAndHasRole>> callback);
    
    @PUT
    @Path("sec:{id}/schooladmin/school/getSchoolAdminsAndHasRoleInSchool")
    public void getSchoolAdminsAndHasRoleInSchool(@PathParam("id") String id, RestContext rest, MethodCallback<List<DomSchoolAdminAndHasRole>> callback);
    
    @PUT
    @Path("/sec:{id}/schooladmin/school/getStudentsInSchoolList")
    public void getStudentsInSchool(@PathParam("id") String id, RestContext rest, MethodCallback<List<DomStudent>> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/school/getTeacherList")
    public void getTeachersInSchoolClass(@PathParam("id") String id, RestSchoolClass restData, MethodCallback<List<DomTeacher>> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/school/getSchoolClasssList")
    public void getSchoolsClasses(@PathParam("id") String id, RestContext rest, MethodCallback<List<DomSchoolClass>> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/schoolclass/getSingleSchoolStudentsInSchoolList")
    public void getSingleSchoolStudentsInSchool(@PathParam("id") String id, RestContext rest, MethodCallback<List<DomStudent>> callback);
   
    @PUT
    @Path("/sec:{id}/schooladmin/school/submitSingleSchoolStudent")
    public void submitSingleSchoolStudent(@PathParam("id") String id, RestNewSingleSchoolStudent schoolClass, MethodCallback<Boolean> callBack);

    @PUT
    @Path("/sec:{id}/schooladmin/school/getSingleSchoolStudent")
    public void getSingleSchoolStudent(@PathParam("id") String id, RestGetSingleSchoolStudent singleSchoolStudent, MethodCallback<DomSingleSchoolStudent> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/school/updateSingleSchoolStudent")
    public void updateSingleSchoolStudent(@PathParam("id") String id, RestSingleSchoolStudent submit, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/school/getTeachersSchoolClassList")
    public void getTeachersSchoolClasses(@PathParam("id") String id, RestTeacher submit, MethodCallback<List<DomSchoolClassId>> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/school/submitTeacher")
    public void submitTeacher(@PathParam("id") String id,  RestUserFull teacher, MethodCallback<Boolean> callback);
 
    @PUT
    @Path("/sec:{id}/schooladmin/school/getStudentsSchoolClassList")
    public void getStudentsSchoolClasses(@PathParam("id") String id, RestStudent restStudent, MethodCallback<List<DomSchoolClass>> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/school/update")
    public void updateSchool(@PathParam("id") String id, RestSchoolFull rest, MethodCallback<Boolean> callback);
    
    @PUT
    @Path("/sec:{id}/schooladmin/school/removeSchoolAdmin")
    public void removeSchoolAdminFromSchool(@PathParam("id") String id, RestSchoolAdmin restSchoolAdmin, MethodCallback<Boolean> callback);
    @PUT
    @Path("/sec:{id}/schooladmin/school/removeSingleSchoolStudentFromSchool")
    public void removeSingleSchoolStudentFromSchool(@PathParam("id") String id, RestStudent restStudent, MethodCallback<Boolean> callback);
    @PUT
    @Path("/sec:{id}/schooladmin/school/removeStudent")
    public void removeStudentFromSchool(@PathParam("id") String id, RestStudent restStudent, MethodCallback<Boolean> callback);
    @PUT
    @Path("/sec:{id}/schooladmin/school/removeTeacher")
    public void removeTeacherFromSchool(@PathParam("id") String id, RestTeacher restTeacher, MethodCallback<Boolean> callback);

    @PUT
    @Path("/sec:{id}/schooladmin/school/getStudentsInSchool")
    public void getStudentsInSchool(@PathParam("id") String id, RestSchoolOrganisation rest, MethodCallback<DomSchoolOrganisation> callback);
    
}
