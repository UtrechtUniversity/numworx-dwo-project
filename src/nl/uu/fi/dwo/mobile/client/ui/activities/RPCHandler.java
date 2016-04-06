package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.utils.MD5;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * equivalent van de PersistenceFacade
 * @author velth101
 *
 */
public class RPCHandler {

	private static final List<String> SCO_KEYS = Arrays.asList("scoID", "appletID", "sconame", "description", "showscore", "sequencenr", "courseID" );
	private String server;

	public RPCHandler(String server) {
		this.server = server;
	}
	
	public RPCHandler() {
		this(Window.Location.getProtocol() +  "//" + DWOplayer.PARAMETERS.getHost() + "/DWOmAccess/dbaccess");
	}
	
	private static int PROFILE_OFFSET = -1234;
	
	public void login(String name, String password, AsyncCallback<? super Map<String,Object>> callback)
	{
		String pwmd5 = MD5.md5(password);

		XmlRpcClient client = getClient();

		String method = "login";
		Object[] params = { name, pwmd5 };

		@SuppressWarnings({ "unchecked", "rawtypes" })
		XmlRpcRequest request = new XmlRpcRequest(client, method, params, callback);
		request.execute();
	}

	public void samlLogin(String name, String org, AsyncCallback<? super Map<String,Object>> callback)
	{
		XmlRpcClient client = getClient();
		String method = "login_saml";
		Object[] params = { name, org };

		@SuppressWarnings({ "unchecked", "rawtypes" })
		XmlRpcRequest request = new XmlRpcRequest(client, method, params, callback);
		request.execute();
	}

	private XmlRpcClient xmlRpcClient;
	
	public XmlRpcClient getClient() {
		if( xmlRpcClient == null)
		{
			xmlRpcClient = new XmlRpcClient(server);
			xmlRpcClient.setTimeoutMillis(1000000);
		}
		return xmlRpcClient;
	}

	public <T> void getCourses(Map<String, Object> userData,
			AsyncCallback<T> getCoursesCallback) {
		
		String method = "getCourses";
		int profileID = getDwoProfile();
		int guestID = PROFILE_OFFSET - profileID;
		
		XmlRpcClient client = getClient();

		Object[] params = { guestID };

		XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, getCoursesCallback);

		request.execute();
	}
	
	public <T> void getCoursesSchool(Map<String, Object> userData, AsyncCallback<T> getCoursesCallback) {
		String method = "getTable";
		HashMap<String,Object> g = new HashMap<String,Object>();
		g.put("parentID", 0);
		Object schoolID = userData.get("schoolID");
		g.put("schoolID", schoolID);
		g.put("dwoProfileID", getDwoProfile());
		Object[] params = {"tblCourse", g, "name" };
		XmlRpcClient client = getClient();
		XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, getCoursesCallback);
		request.execute();
	}
	
	public <T> void getCoursesClass(Map<String,Object> userData, AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
		String method = "getCoursesForClass";
		Object classid = userData.get("classID");
		Object[] params = { classid };
		XmlRpcClient client = getClient();
		XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, filterProfile(getCoursesCallback));
		request.execute();
	}

	
	private  AsyncCallback<List<Map<String,Object>>> filterProfile(final AsyncCallback<List<Map<String,Object>>> callback) {
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
					final Integer dwoProfile = getDwoProfile();
					if(! map.get("dwoProfileID").equals( dwoProfile))
						i.remove();
				}
				callback.onSuccess(result);
			}
			
		};
	}
	
	private final int getDwoProfile() {
		return DWOplayer.PROFILE_ID;
	}

	public <T> void getCourses(Object id, AsyncCallback<T> getCoursesCallback) {
		HashMap<String, Object> g = new HashMap<String,Object>();
		g.put("parentID", id);
		String method = "getTable";
		Object[] params = {"tblCourse", g, "name" };
		XmlRpcClient client = getClient();
		XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, getCoursesCallback);
		request.execute();
	}

	public <T> void getScos(Object id, AsyncCallback<T> getScosCallback) {
		HashMap<String, Object> g = new HashMap<String,Object>();
		g.put("courseID", id);
		String method = "getTable";
		Object[] params = {"tblSco", SCO_KEYS, g, "sequencenr" };
		XmlRpcClient client = getClient();
		XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, getScosCallback);
		request.execute();
	}

	public <T> void getDwoProfile(AsyncCallback<T> getProfileCallback) {
		String method = "getRecord";
		Object[] params = { "tblDwoProfile", "dwoProfileID", getDwoProfile() };
		XmlRpcClient client = getClient();
		XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, getProfileCallback);
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
	
	public <T> void getUserResults(Object courseID, Object userID, AsyncCallback<T> getUserResultsCallback) {
		Object[] params = { courseID, userID };
		XmlRpcClient client = getClient();
		XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, "getUserResults", params, getUserResultsCallback);
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
	
	
}
