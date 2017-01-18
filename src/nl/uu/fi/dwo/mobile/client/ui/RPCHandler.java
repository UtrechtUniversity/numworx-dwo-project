package nl.uu.fi.dwo.mobile.client.ui;

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
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;

import org.osgi.util.promise.Promise;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RPCHandler {

	void logout();

	Promise<DomDwoProfileFull> getDwoProfile();

//	void getCoursesClass(Object classid,
//			AsyncCallback<List<Map<String, Object>>> callback);
	Promise<DomCoursesOfSchoolClass> getCoursesClass(DomSchoolClass schoolClass);
	
//	void getCoursesSchool(Object schoolID,
//			AsyncCallback<List<Map<String, Object>>> callback);
	Promise<List<DomCourseStudent>> getCoursesSchool(DomSchool school);
	
	void getCourses(
			AsyncCallback<List<Map<String, Object>>> callback_final);
	Promise<List<DomCourseStudent>> getCourses();
	
	XmlRpcClient getClient();

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


}
