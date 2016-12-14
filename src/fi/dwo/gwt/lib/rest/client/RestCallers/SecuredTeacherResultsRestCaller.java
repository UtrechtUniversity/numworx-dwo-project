package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
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
    @Path("/secure/teacher/results/getTeachersResults")
    public void getTeachersResults(RestDwoProfile aProfile,MethodCallback<DomResultsPerTeacher> callback);
}
