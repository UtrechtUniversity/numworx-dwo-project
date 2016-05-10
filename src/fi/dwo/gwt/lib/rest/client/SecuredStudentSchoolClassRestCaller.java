package fi.dwo.gwt.lib.rest.client;

import fi.dwo.gwt.lib.rest.CallManagers.Callback;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.fusesource.restygwt.client.RestService;

import fi.dwo.rest.entities.RestNewSchoolClass4Student;
import fi.dwo.rest.entities.RestSchoolClass;
import java.util.List;

public interface SecuredStudentSchoolClassRestCaller extends RestService {

    @PUT
    @Path("/rest/secure/student/schoolclass/select")
    public void setActiveSchoolClass(RestSchoolClass restSchoolClass, Callback<Boolean> callback);

    todo
    public void removeSchoolClass(RestSchoolClass restSchoolClass, Callback<Boolean> callback);

    public void getStudentsSchoolClasses(Callback<List<DomSchoolClass>> callback);

    public void registerStudentForSchoolClass(RestNewSchoolClass4Student restData, Callback<Boolean> callback);

    public void getSchoolsClasses(Callback<List<DomSchoolClass>> callback);

    public void getActiveSchoolClass(Callback<DomSchoolClass> callback);
}
