package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacherv2;
import nl.uu.fi.dwo.rest.entities.RestClearStudentDataForScoAndClass;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestResultsPerTeacher;
import nl.uu.fi.dwo.rest.entities.RestResultsPerTeacherv2;

import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.MethodCallback;

public interface SecuredTeacherResultsRestCaller extends RestService {

    /** Returns a DomResultsPerTeacher object for the current teacher. An exception
     * is thrown if an error occurs.
     * 
     * @param aProfile
     * @param callback 
     */
    @PUT
    @Path("/sec:{id}/teacher/results/getTeachersResults")
    public void getTeachersResults(@PathParam("id") String id, RestDwoProfile aProfile,MethodCallback<DomResultsPerTeacher> callback);
    
    @PUT
    @Path("/sec:{id}/teacher/results/selectedTeachersResults")
    public void selectedTeachersResult(@PathParam("id") String id, RestResultsPerTeacher rest, MethodCallback<DomResultsPerTeacher> callback);
  
    @PUT
    @Path("/sec:{id}/teacher/results/selectedTeachersResultsv2")
    public void selectedTeachersResult(@PathParam("id") String id, RestResultsPerTeacherv2 rest, MethodCallback<DomResultsPerTeacherv2> callback);

    
    @PUT
    @Produces({"application/json"})
    @Path("/sec:{id}/teacher/results/clearStudentResults")
    public void clearStudentResults(@PathParam("id") String id, RestClearStudentDataForScoAndClass rest, MethodCallback<Boolean> callback);
    
    @PUT
    @Produces({"application/json"})
    @Path("/sec:{id}/teacher/results/createStudentResults")
    public void createStudentResults(@PathParam("id") String id, RestClearStudentDataForScoAndClass rest, MethodCallback<DomResultsPerTeacher> callback);

    @PUT
    @Produces({"application/json"})
    @Path("/sec:{id}/teacher/results/createStudentResultsv2")
    public void createStudentResultsv2(@PathParam("id") String id, RestClearStudentDataForScoAndClass rest, MethodCallback<DomResultsPerTeacherv2> callback);
}
