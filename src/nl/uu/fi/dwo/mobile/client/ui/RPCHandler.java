package nl.uu.fi.dwo.mobile.client.ui;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomCoursesOfSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerStudentCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import org.osgi.util.promise.Promise;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.google.gwt.json.client.JSONValue;

import fi.dwo.gwt.lib.rest.CallManagers.XapiManager;

public interface RPCHandler {

	Promise<Void> logout();

	Promise<DomDwoProfileFull> getDwoProfile();

//	void getCoursesClass(Object classid,
//			AsyncCallback<List<Map<String, Object>>> callback);
	Promise<DomCoursesOfSchoolClass> getCoursesClass(DomSchoolClass schoolClass);
	Promise<DomCoursesOfSchoolClass> getCourseClass(Object course, DomSchoolClass schoolClass);
	Promise<DomCoursesOfSchoolClass> getScoContextClass(Object sco, DomSchoolClass schoolClass);
	
//	void getCoursesSchool(Object schoolID,
//			AsyncCallback<List<Map<String, Object>>> callback);
	Promise<List<DomCourseStudent>> getCoursesSchool(DomSchool school);
	
//	void getCourses(
//			AsyncCallback<List<Map<String, Object>>> callback_final);
	Promise<List<DomCourseStudent>> getCourses();
	
	//XmlRpcClient getClient();

	Promise<DomCourseStudent> getCourse(Object id);
	
	Promise<DomScoContext> getSco(Object id);

	Promise<DomResultsPerStudentCourse> getUserResults(Object courseID, Object userID);

	Promise<List<DomScoContext>> getScos(Object id);
	
	Promise<List<DomCourseStudent>> getCourses(Object parentID);
	
	Promise<DomSchoolsRolesAndClassesV2> getSchoolLogins();

	Promise<DomUserFullwLoginContext> samlLogin(String user_id, String org_id);
	Promise<DomUserFullwLoginContext> getUserFromAuthToken(String authToken);
	Promise<DomUserFullwLoginContext> login(String username, String password);
	Promise<DomUserFullwLoginContext> loginMD5(String username, String password);

// V3 stuff	
	Promise<Map<String,String>> getValues(Object scoID, Collection<String> keys);
	Promise<?> setValues(Object scoID, Map<String, String> values);
	Promise<JSONValue> getJSONLaunchDataBytes(Object scoID);
// V4 stuff
	Promise<Void> startExam(String id, String password);

	Promise<DomStudentModelContext> getStudentModel(PersistenceId id);
	Promise<List<DomStudentModelContext>> getStudentModels();
	Promise<DomStudentModelDataScore> getStudentModelDataScore(DomStudentModelContextId id);

// V5 stuff
	Promise<XapiManager> getLRS();

    Promise<JSONValue> getCourseDescription(Object file);

	Promise<JSONValue> refreshExam();

// schoolclasses for students	
	Promise<List<DomSchoolClass>> getStudentsSchoolClasses();
	Promise<Boolean> setActiveSchoolClass(DomSchoolClass schoolClass);
}
