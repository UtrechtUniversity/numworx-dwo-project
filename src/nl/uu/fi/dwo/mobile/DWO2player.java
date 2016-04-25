package nl.uu.fi.dwo.mobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.mobile.client.sco.SCORM_DWO2;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactoryImpl;
import nl.uu.fi.dwo.mobile.client.ui.activities.RPCHandler;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fi.restrpcgwt.client.RestRPCHandler;

public class DWO2player extends DWOplayer implements EntryPoint {

	public DWO2player() {

	}
	private static final String DWO_SAML_AUTH_TOKEN = "dwoSAMLAuthToken";

	
	protected ClientFactory createClientFactory() {
		ClientFactoryImpl factory = new ClientFactoryImpl() { 
			
			public SCORM_guest setupAPI(final Map<String, Object> profiledata) {
				SCORM_guest api;
				if(profiledata == null) {
					api = new SCORM_guest();
					menuWidget = null;
				} else {
					Object userID = profiledata.get("userID");
					Object sgID = profiledata.get("schoolGroupID");
					api = new SCORM_DWO2(userID, sgID);
					getUserBar().setProfile(profiledata);
				}
				return api;
			}

		};
		String host = PARAMETERS.getHost();
		String http = Window.Location.getProtocol();
		final RestRPCHandler restHandler = new RestRPCHandler(http + "//" + host + "/dwo/rest/");
		factory.setRPCHandler(new RPCHandler(http + "//" + host + "/dwo/xmlrpc"){

			@Override
			public void login(String name, String password,
					AsyncCallback<? super Map<String, Object>> callback) {
				restHandler.login(name, password, callback);
			}
			
			public <T> void getCourses(Map<String, Object> userData,
					AsyncCallback<T> getCoursesCallback) {
				
				String method = "getCoursesJS"; // sort sequencenr
				int profileID = PROFILE_ID;
				int guestID = PROFILE_OFFSET - profileID;
				
				XmlRpcClient client = getClient();

				Object[] params = { guestID };

				XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, getCoursesCallback);

				request.execute();
			}

			public <T> void getCourses(Object id, AsyncCallback<T> getCoursesCallback) {
				HashMap<String, Object> g = new HashMap<String,Object>();
				g.put("parentID", id);
				String method = "getTableJS";
				Object[] params = {"tblCourse", g, "name" };
				XmlRpcClient client = getClient();
				XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, getCoursesCallback);
				request.execute();
			}

			public <T> void getCoursesSchool(Map<String, Object> userData, AsyncCallback<T> getCoursesCallback) {
				String method = "getTableJS";
				HashMap<String,Object> g = new HashMap<String,Object>();
				g.put("parentID", 0);
				Object schoolID = userData.get("schoolID");
				g.put("schoolID", schoolID);
				g.put("dwoProfileID", PROFILE_ID);
				Object[] params = {"tblCourse", g, "name" };
				XmlRpcClient client = getClient();
				XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, method, params, getCoursesCallback);
				request.execute();
			}

			public <T> void getCoursesClass(Map<String,Object> userData, AsyncCallback<List<Map<String,Object>>> getCoursesCallback) {
				String method = "getCoursesForClassJS";
				Object classid = userData.get("classID");
				Object[] params = { classid };
				XmlRpcClient client = getClient();
				XmlRpcRequest<List<Map<String,Object>>> request = new XmlRpcRequest<List<Map<String,Object>>>(client, method, params, filterProfile(getCoursesCallback));
				request.execute();
			}

			@Override
			public void samlLogin(String name, String org,
					AsyncCallback<? super Map<String, Object>> callback) {
				String authToken = Cookies.getCookie(DWO_SAML_AUTH_TOKEN);
				restHandler.samlLogin(name, org, authToken, callback);
			}

		});
		return factory;
	}

}
