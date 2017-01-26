package nl.uu.fi.dwo.account.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.util.promise.Promise;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManagerV2;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 *
 * @author plas0006
 */
@Deprecated
public abstract class RPCHandlerV2 extends RPCHandlerV1 {

    /**
     *
     */
    protected final SecuredUserAccountManager accountManager = new SecuredUserAccountManager();

    /**
     *
     */
    protected final SecuredUserSchoolLoginManagerV2 schoolManager = new SecuredUserSchoolLoginManagerV2();
	private static final String DWO_SAML_AUTH_TOKEN = "dwoSAMLAuthToken";

    /**
     *
     * @param server
     * @param profile
     */
    public RPCHandlerV2(String server, int profile) {
		super(server, profile);
	}
	
    /**
     *
     * @param name
     * @param password
     * @return
     */
    public Promise<DomUserFullwLoginContext> login(String name, String password) {
		PromiseCallback<DomUserFullwLoginContext> defer = new PromiseCallback<>();
		accountManager.loginUser(name,  password, defer, null);
		return defer.getPromise();
	}

    /**
     *
     * @param name
     * @param password
     * @return
     */
    public Promise<DomUserFullwLoginContext> loginMD5(String name, String password) {
		PromiseCallback<DomUserFullwLoginContext> defer = new PromiseCallback<>();
		accountManager.loginUserMD5(name,  password, defer, null);
		return defer.getPromise();
	}
	
    /**
     *
     * @return
     */
    public Promise<DomSchoolsRolesAndClassesV2> getSchoolLogins() {
		PromiseCallback<DomSchoolsRolesAndClassesV2> defer = new PromiseCallback<>();
		schoolManager.getSchoolLoginsV2(defer);
		return defer.getPromise();
	}
		
    /**
     *
     * @param courseID
     * @param userID
     * @param schoolGroupID
     * @param getUserResultsCallback
     */
    protected void getUserResultsHelper(Object courseID, Object userID, Object schoolGroupID,
			AsyncCallback<List<Map<String,Object>>> getUserResultsCallback) {
		Object[] params = { courseID, userID, schoolGroupID };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, "getUserResults", params, getUserResultsCallback);
		request.execute();
	}
	
    /**
     *
     * @param courseID
     * @param userID
     * @param getUserResultsCallback
     */
    @Override 
	public abstract void getUserResults(Object courseID, Object userID,
			AsyncCallback<List<Map<String,Object>>> getUserResultsCallback);
	
    /**
     *
     * @param getCoursesCallback
     */
    public void getCourses(
			AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
		
		String method = "getCoursesJS"; // sort sequencenr
		int profileID = getProfile();
		int guestID = PROFILE_OFFSET - profileID;
		
		XmlRpcClient client = getClient();

		Object[] params = { guestID };

		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, getCoursesCallback);

		request.execute();
	}

    /**
     *
     * @param id
     * @param getCoursesCallback
     */
    public void getCourses(Object id, AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
		HashMap<String, Object> g = new HashMap<String,Object>();
		g.put("parentID", id);
		String method = "getTableJS";
		Object[] params = {"tblCourse", g, "name" };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, getCoursesCallback);
		request.execute();
	}

    /**
     *
     * @param schoolID
     * @param getCoursesCallback
     */
    public void getCoursesSchool(Object schoolID, AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
		String method = "getTableJS";
		HashMap<String,Object> g = new HashMap<String,Object>();
		g.put("parentID", 0);
		g.put("schoolID", schoolID);
		g.put("dwoProfileID", getProfile());
		Object[] params = {"tblCourse", g, "name" };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, getCoursesCallback);
		request.execute();
	}

    /**
     *
     * @param classid
     * @param getCoursesCallback
     */
    public void getCoursesClass(Object classid, AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
		String method = "getCoursesForClassJS";
		Object[] params = { classid };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, filterProfile(getCoursesCallback));
		request.execute();
	}

    /**
     *
     * @param name
     * @param org
     * @param callback
     */
    protected void samlLoginHelper(String name, String org,
			AsyncCallback<DomUserFullwLoginContext> callback) {
		String authToken = Cookies.getCookie(DWO_SAML_AUTH_TOKEN);
		accountManager.samlLogin(name, org, authToken, callback);
	}

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.mobile.client.ui.activities.RPCHandler#getAuthTokenUser(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
	 */

    /**
     *
     * @param authToken
     * @param callback
     */

	public void getUserFromAuthTokenHelper(String authToken,
			AsyncCallback<DomUserFullwLoginContext> callback) {
		accountManager.getUserFromAuthToken(authToken, callback);
	}
	
    /**
     *
     * @param authToken
     * @return
     */
    public Promise<DomUserFullwLoginContext> getUserFromAuthToken(String authToken) {
		PromiseCallback<DomUserFullwLoginContext> defer = new PromiseCallback<>();
		accountManager.getUserFromAuthToken(authToken, defer);
		return defer.getPromise();
	}
		
    /**
     *
     */
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
	
    /**
     *
     * @param name
     * @param org
     * @return
     */
    public Promise<DomUserFullwLoginContext> samlLogin(String name, String org) {
		PromiseCallback<DomUserFullwLoginContext> defer = new PromiseCallback<>();
		samlLoginHelper(name, org, defer);
		return defer.getPromise();
	}

	
}
