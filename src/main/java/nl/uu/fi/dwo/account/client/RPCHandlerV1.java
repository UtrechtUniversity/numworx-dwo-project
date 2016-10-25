package nl.uu.fi.dwo.account.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.dwo.gwt.lib.rest.CallManagers.MD5;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;

/**
 * equivalent van de PersistenceFacade voor DWO v1.0
 * 
 * @author velth101
 *
 */
public class RPCHandlerV1 {

	private static final List<String> SCO_KEYS = Arrays.asList("scoID", "appletID", "sconame", "description", "showscore", "sequencenr", "courseID" );
	private String server;
	private int profile;
	
	public RPCHandlerV1(String server, int profile) {
		this.server = server;
		this.profile = profile;
	}
		
	protected static int PROFILE_OFFSET = -1234;
	
	public void login(String name, String password, AsyncCallback<Map<String,Object>> callback)
	{
		String pwmd5 = MD5.md5(password);
		loginMD5(name, pwmd5, callback);
	}

	public void loginMD5(String name, String pwmd5,
			AsyncCallback<Map<String, Object>> callback) {
		XmlRpcClient client = getClient();

		String method = "login";
		Object[] params = { name, pwmd5 };

		@SuppressWarnings({ "unchecked", "rawtypes" })
		XmlRpcRequest request = new XmlRpcRequest(client, method, params, callback);
		request.execute();
	}

	public void samlLogin(String name, String org, AsyncCallback<Map<String,Object>> callback)
	{
		XmlRpcClient client = getClient();
		String method = "login_saml";
		Object[] params = { name, org };

		@SuppressWarnings({ "unchecked", "rawtypes" })
		XmlRpcRequest request = new XmlRpcRequest(client, method, params, callback);
		request.execute();
	}
	
	public void getUserFromAuthToken(String authToken, AsyncCallback<Map<String,Object>> callback)
	{
		Throwable caught = new RuntimeException("");
		callback.onFailure(caught);
	}
	

	private XmlRpcClient xmlRpcClient;

	/**
	 * XML RPC Mapper voor DomDwoProfiles.
	 */
	private static final Function<Map<String, Object>, DomDwoProfile> TO_DWOPROFILE = new Function<Map<String,Object>, DomDwoProfile>() {
		
		@Override
		public DomDwoProfile apply(Map<String, Object> t) {
			DomDwoProfile result = new DomDwoProfile();
			result.setDwoProfileDescription(t.get("dwoProfileDescription").toString());
			result.setDwoProfileRights(t.get("dwoProfileRights").toString());
			result.setDwoProfileName(t.get("dwoProfileName").toString());
			result.setDwoProfileText(t.get("dwoProfileText").toString());
			result.setId(null); // FIXME wordt waarschijnlijk niet gebruikt! t.get(dwoProfileID)
			return result;
		}
	};
	
	public XmlRpcClient getClient() {
		if( xmlRpcClient == null)
		{
			xmlRpcClient = new XmlRpcClient(server);
			xmlRpcClient.setTimeoutMillis(1000000);
		}
		return xmlRpcClient;
	}

	public void getCourses(Map<String, Object> userData,
			AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
		
		String method = "getCourses";
		int profileID = getProfile();
		int guestID = PROFILE_OFFSET - profileID;
		
		XmlRpcClient client = getClient();

		Object[] params = { guestID };

		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, getCoursesCallback);

		request.execute();
	}
	
	public void getCoursesSchool(Map<String, Object> userData, AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
		String method = "getTable";
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
		String method = "getCoursesForClass";
		Object classid = userData.get("classID");
		Object[] params = { classid };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, filterProfile(getCoursesCallback));
		request.execute();
	}

	
	protected  AsyncCallback<List<Map<String,Object>>> filterProfile(final AsyncCallback<List<Map<String,Object>>> callback) {
		return new AsyncCallback<List<Map<String,Object>>>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
				
			}

			@Override
			public void onSuccess(List<Map<String, Object>> result) {
				Iterator<Map<String, Object>> i = result.iterator();
				while (i.hasNext()) {
					Map<java.lang.String, java.lang.Object> map = (Map<java.lang.String, java.lang.Object>) i
							.next();
					final Integer dwoProfile = getProfile();
					if(! map.get("dwoProfileID").equals( dwoProfile))
						i.remove();
				}
				callback.onSuccess(result);
			}
			
		};
	}
	
	final int getProfile() {
		return profile;
	}

	public void getCourses(Object id, AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
		HashMap<String, Object> g = new HashMap<String,Object>();
		g.put("parentID", id);
		String method = "getTable";
		Object[] params = {"tblCourse", g, "name" };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, getCoursesCallback);
		request.execute();
	}

	public void getScos(Object id, AsyncCallback<List<Map<String,Object>>> getScosCallback) {
		HashMap<String, Object> g = new HashMap<String,Object>();
		g.put("courseID", id);
		String method = "getTable";
		Object[] params = {"tblSco", SCO_KEYS, g, "sequencenr" };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, getScosCallback);
		request.execute();
	}

	@Deprecated
	public void getDwoProfile(AsyncCallback<Map<String,Object>> getProfileCallback) {
		String method = "getRecord";
		Object[] params = { "tblDwoProfile", "dwoProfileID", getProfile() };
		XmlRpcClient client = getClient();
		XmlRpcRequest<Map<String,Object>> request = new XmlRpcRequest<Map<String,Object>>(client, method, params, getProfileCallback);
		request.execute();
	}
	
	public void getCourse(Object courseID, AsyncCallback<Map<String, Object>> getCourseCallback) {
		String method = "getRecord";
		Object[] params = { "tblCourse", "courseID", objectToKey(courseID) };
		XmlRpcClient client = getClient();
		XmlRpcRequest<Map<String, Object>> request = new XmlRpcRequest<Map<String, Object>>(client, method, params, getCourseCallback);
		request.execute();
	}

	public void getSco(Object scoID, AsyncCallback<Map<String,Object>> callback) 
	{
		String method = "getRecord";
		Object[] params = { "tblScoContext", "scoID", objectToKey(scoID) };
		XmlRpcClient client = getClient();
		XmlRpcRequest<Map<String,Object>> request = new XmlRpcRequest<Map<String,Object>>(client, method, params, callback);
		request.execute();
	}
	
	
// In Mc2 new String()
	protected Object objectToKey(Object courseID) {
		return new Integer(courseID.toString());
	}
	
	
	public <T> void getClasses(Object userID, AsyncCallback<T> getClassesCallback) {
		HashMap<String, Object> g = new HashMap<String,Object>();
		g.put("userID", userID);
		String method = "getTable";
		Object[] params = {"tblClass", g, "class"};
		XmlRpcClient client = getClient();
		XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, getClassesCallback);
		request.execute();
	}
	
	public <T> void getStudents(int classID, AsyncCallback<T> getStudentsCallback) {
		HashMap<String, Object> g = new HashMap<String,Object>();
		g.put("classID", classID);
		String method = "getTable";
		Object[] params = {"tblUser", g, "username"};
		XmlRpcClient client = getClient();
		XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, getStudentsCallback);
		request.execute();
	}
	
	public void getUserResults(Object courseID, Object userID, AsyncCallback<List<Map<String,Object>>> getUserResultsCallback) {
		Object[] params = { courseID, userID };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, "getUserResults", params, getUserResultsCallback);
		request.execute();
	}
	
//	private <T> void getCourseSequence0(Object schoolID, AsyncCallback<T> callback) {
//		HashMap<String,Object> g = new HashMap<String,Object>();
//		g.put("classID", 0);
//		if(schoolID == null) schoolID = Integer.valueOf(0);
//		g.put("schoolID", schoolID);
//		g.put("profileID", getDwoProfile());
//		String method = "getTable";
//		Object[] params = { "tblCourseSequence", g, "sequencenr" };
//		XmlRpcClient client = getClient();
//		XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, callback);
//		request.execute();
//	}
	
//	Map<Object, Integer> courseSortMap = new HashMap<Object,Integer>();
	
//	class CourseSortCallback implements AsyncCallback<List<Map<String,Object>>> {
//
//		Runnable runner;
//		
//		@Override
//		public void onFailure(Throwable caught) {
//			runner.run();
//		}
//
//		@Override
//		public void onSuccess(List<Map<String, Object>> result) {
//			for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
//				Map<String, Object> map = iterator.next();
//				Object id = map.get("courseID");
//				Number n  = (Number) map.get("sequencenr");
//				courseSortMap.put(id, n.intValue());
//			}
//			runner.run();
//		}
//		
//	}
	
	public void getCourseSequence(final Object schoolID, final Runnable runner) {

//		if(!GWT.isProdMode()) {
			runner.run();
			return;
//		}
//		final CourseSortCallback csc = new CourseSortCallback();
//		if(schoolID != null || !"".equals(schoolID)) {
//			Runnable rnull = new Runnable() {
//
//				@Override
//				public void run() {
//					csc.runner = runner;
//					getCourseSequence0(schoolID, csc);
//				}
//				
//			};
//			csc.runner = rnull;
//		} else {
//			csc.runner = runner;
//		}
//		getCourseSequence0(null, csc);
	}

	public void logout() {
	}
	
	
	/**
	 * Get the DomDwoProfile.
	 * TODO voor Gert: in V2 is dit veeel makkelijker via de (TODO) PublicDwoProfileManager?
	 * @return a promise for the DwoProfile.
	 */
	
	public Promise<DomDwoProfile> getDwoProfile() {
		PromiseCallback<Map<String,Object>> defer = new PromiseCallback<>();
		getDwoProfile(defer);
		return defer.getPromise().map(TO_DWOPROFILE);
	}
	
}
