package fi.dwo.gwt.lib.rest.client.RestCallers;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherScormValues;
import nl.uu.fi.dwo.rest.entities.RestTeacherScormValues;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

public interface SecuredTeacherScormValuesRestCaller extends RestService {

	@PUT
    @Path("/secure/teacher/scormValues/get")
    public void get(RestTeacherScormValues rest,MethodCallback<DomTeacherScormValues> callback);

    @PUT
    @Path("/secure/teacher/scormValues/set")
    public void set(RestTeacherScormValues rest,MethodCallback<DomStudentScoContext> callback);
}
