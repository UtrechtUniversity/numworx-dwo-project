package nl.uu.fi.dwo.mobile.client.ui;

import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.DomCourseFull;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;

import org.osgi.util.promise.Promise;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RPCHandler {

	void logout();

	Promise<DomDwoProfile> getDwoProfile();

	void getCoursesClass(Object classid,
			AsyncCallback<List<Map<String, Object>>> callback);
	Promise<List<DomCourseFull>> getCoursesClass(DomSchoolClass schoolClass);
	
	void getCoursesSchool(Object schoolID,
			AsyncCallback<List<Map<String, Object>>> callback);
	Promise<List<DomCourseFull>> getCoursesSchool(DomSchool school);
	
	void getCourses(
			AsyncCallback<List<Map<String, Object>>> callback_final);
	Promise<List<DomCourseFull>> getCourses();
	
	XmlRpcClient getClient();

	void getCourse(Object id,
			AsyncCallback<Map<String, Object>> getCoursesCallback);

	void samlLogin(String user_id, String org_id,
			AsyncCallback<Map<String, Object>> lOGIN_CALLBACK);

	void getUserFromAuthToken(String authToken,
			AsyncCallback<Map<String, Object>> lOGIN_CALLBACK);

	void login(String username, String password,
			AsyncCallback<Map<String, Object>> lOGIN_CALLBACK);

	void loginMD5(String username, String password,
			AsyncCallback<Map<String, Object>> lOGIN_CALLBACK);

	void getSco(Object id, AsyncCallback<Map<String, Object>> callback);

	void getUserResults(Object courseID, Object userID,
			AsyncCallback<List<Map<String, Object>>> getUserResultsCallback);

	void getScos(Object id, AsyncCallback<List<Map<String,Object>>> getScosCallback);

	void getCourses(Object id, AsyncCallback<List<Map<String,Object>>> getCoursesCallback);

	Promise<DomSchoolsRolesAndClassesV2> getSchoolLogins();

	Promise<DomUserFullwLoginContext> samlLogin(String user_id, String org_id);
	Promise<DomUserFullwLoginContext> getUserFromAuthToken(String authToken);
	Promise<DomUserFullwLoginContext> login(String username, String password);
	Promise<DomUserFullwLoginContext> loginMD5(String username, String password);


}
