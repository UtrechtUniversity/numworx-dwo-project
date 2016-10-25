package nl.uu.fi.dwo.mobile.client.ui;

import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;

import org.osgi.util.promise.Promise;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RPCHandler {

	void logout();

	Promise<DomDwoProfile> getDwoProfile();

	void getCoursesClass(Map<String, Object> profiledata,
			AsyncCallback<List<Map<String, Object>>> callback);

	void getCoursesSchool(Map<String, Object> profiledata,
			AsyncCallback<List<Map<String, Object>>> callback);

	void getCourses(Map<String, Object> profiledata,
			AsyncCallback<List<Map<String, Object>>> callback_final);

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


}
