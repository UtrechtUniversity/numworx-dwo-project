package nl.uu.fi.dwo.account.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManagerV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public abstract class RPCHandlerV2 extends RPCHandlerV1 {

	protected final SecuredUserAccountManager accountManager = new SecuredUserAccountManager();
	protected final SecuredUserSchoolLoginManagerV2 schoolManager = new SecuredUserSchoolLoginManagerV2();
	private static final String DWO_SAML_AUTH_TOKEN = "dwoSAMLAuthToken";


	public RPCHandlerV2(String server, int profile) {
		super(server, profile);
	}

	protected void loginMD5Helper(final String name, final String password, final AsyncCallback<DomUserFullwLoginContext> callback)
	{
		accountManager.loginUserMD5(name, password, callback, null);
	}
	
	@Override
	public abstract void loginMD5(String name, String pwmd5,
			AsyncCallback<Map<String, Object>> callback);

	protected void loginHelper(final String name, final String password, final AsyncCallback<DomUserFullwLoginContext> callback)
	{
		accountManager.loginUser(name, password, callback, null);
		
	}
	
	@Override
	public abstract void login(String name, String pwmd5,
			AsyncCallback<Map<String, Object>> callback);
	
	
	protected void getUserResultsHelper(Object courseID, Object userID, Object schoolGroupID,
			AsyncCallback<List<Map<String,Object>>> getUserResultsCallback) {
		Object[] params = { courseID, userID, schoolGroupID };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, "getUserResults", params, getUserResultsCallback);
		request.execute();
	}
	
	@Override 
	public abstract void getUserResults(Object courseID, Object userID,
			AsyncCallback<List<Map<String,Object>>> getUserResultsCallback);
	
	public void getCourses(Map<String, Object> userData,
			AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
		
		String method = "getCoursesJS"; // sort sequencenr
		int profileID = getProfile();
		int guestID = PROFILE_OFFSET - profileID;
		
		XmlRpcClient client = getClient();

		Object[] params = { guestID };

		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, getCoursesCallback);

		request.execute();
	}

	public void getCourses(Object id, AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
		HashMap<String, Object> g = new HashMap<String,Object>();
		g.put("parentID", id);
		String method = "getTableJS";
		Object[] params = {"tblCourse", g, "name" };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, getCoursesCallback);
		request.execute();
	}

	public void getCoursesSchool(Map<String, Object> userData, AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
		String method = "getTableJS";
		HashMap<String,Object> g = new HashMap<String,Object>();
		g.put("parentID", 0);
		Object schoolID = userData.get("schoolID");
		g.put("schoolID", schoolID);
		g.put("dwoProfileID", getProfile());
		Object[] params = {"tblCourse", g, "name" };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, getCoursesCallback);
		request.execute();
	}

	public void getCoursesClass(Map<String,Object> userData, AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
		String method = "getCoursesForClassJS";
		Object classid = userData.get("classID");
		Object[] params = { classid };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, filterProfile(getCoursesCallback));
		request.execute();
	}

	protected void samlLoginHelper(String name, String org,
			AsyncCallback<DomUserFullwLoginContext> callback) {
		String authToken = Cookies.getCookie(DWO_SAML_AUTH_TOKEN);
		accountManager.samlLogin(name, org, authToken, callback);
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.mobile.client.ui.activities.RPCHandler#getAuthTokenUser(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	public void getUserFromAuthTokenHelper(String authToken,
			AsyncCallback<DomUserFullwLoginContext> callback) {
		accountManager.getUserFromAuthToken(authToken, callback);
	}
	
	@Override
	public abstract void getUserFromAuthToken(String authToken,
			AsyncCallback<Map<String,Object>> callback);
	
	@Override
	public void logout() {
		super.logout();
		if(DwoGlobalVars.instance().getCurrentUser() != null)
			accountManager.logout(DwoGlobalVars.instance().getCurrentLoginContext(), new AsyncCallback<Dwo2Exception>() {

				@Override
				public void onFailure(Throwable caught) {
				}

				@Override
				public void onSuccess(Dwo2Exception result) {
					Window.alert(String.valueOf(result));						
				}});
	}
	
	@Override
	public abstract void samlLogin(String name, String org, AsyncCallback<Map<String,Object>> callback);

}
