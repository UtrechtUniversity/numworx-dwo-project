package nl.uu.fi.dwo.mobile.client.ui.activities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.mobile.utils.MD5;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * equivalent van de PersistenceFacade
 * @author velth101
 *
 */
public class RPCHandler {

	private static final List<String> SCO_KEYS = Arrays.asList("scoID", "appletID", "sconame", "description", "showscore", "sequencenr", "courseID" );
	private String server = "http://ws-dev.fisme.science.uu.nl/DWOmAccess/dbaccess";
	private static int PROFILE_OFFSET = -1234;
	
	public void login(String name, String password, AsyncCallback<? super Map<String,Object>> callback)
	{
		String pwmd5 = MD5.md5(password);

		XmlRpcClient client = new XmlRpcClient(server);

		String method = "login";
		Object[] params = { name, pwmd5 };

		@SuppressWarnings({ "unchecked", "rawtypes" })
		XmlRpcRequest request = new XmlRpcRequest(client, method, params, callback);
		request.execute();
	}

	public <T> void getCourses(Map<String, Object> userData,
			AsyncCallback<T> getCoursesCallback) {
		
		String method = "getCourses";
		int profileID = getDwoProfile();
		int guestID = PROFILE_OFFSET - profileID;
		
		XmlRpcClient client = new XmlRpcClient(server);

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
		XmlRpcClient client = new XmlRpcClient(server);
		XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, getCoursesCallback);
		request.execute();
	}
	
	public <T> void getCoursesClass(Map<String,Object> userData, AsyncCallback<T> getCoursesCallback) {
		String method = "getCoursesForClass";
		Object classid = userData.get("classID");
		Object[] params = { classid };
		XmlRpcClient client = new XmlRpcClient(server);
		XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, getCoursesCallback);
		request.execute();
	}

	private int getDwoProfile() {
		// TODO Auto-generated method stub
		return 1;
	}

	public <T> void getCourses(int id, AsyncCallback<T> getCoursesCallback) {
		HashMap<String, Object> g = new HashMap<String,Object>();
		g.put("parentID", id);
		String method = "getTable";
		Object[] params = {"tblCourse", g, "name" };
		XmlRpcClient client = new XmlRpcClient(server);
		XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, getCoursesCallback);
		request.execute();
	}

	public <T> void getScos(int id, AsyncCallback<T> getScosCallback) {
		HashMap<String, Object> g = new HashMap<String,Object>();
		g.put("courseID", id);
		String method = "getTable";
		Object[] params = {"tblSco", SCO_KEYS, g, "sequencenr" };
		XmlRpcClient client = new XmlRpcClient(server);
		XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, getScosCallback);
		request.execute();
	}

}
