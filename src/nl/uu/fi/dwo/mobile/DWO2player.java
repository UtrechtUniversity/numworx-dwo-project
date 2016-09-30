package nl.uu.fi.dwo.mobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.account.client.UserBar;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_DWO2;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactoryImpl;
import nl.uu.fi.dwo.mobile.client.ui.activities.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.places.LoginPlace;
import nl.uu.fi.dwo.mobile.client.ui.places.ReloginPlace;
import nl.uu.fi.dwo.mobile.client.ui.views.Login2ViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginViewImpl;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.theme.base.HeaderCss;

import fi.dwo.gwt.lib.rest.DwoConstants;
import fi.dwo.gwt.lib.rest.CallManagers.LoginPresenter;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManager;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;
import fi.dwo.rest.dom.entities.DomLoginContext;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomSchoolRoleAndClass;
import fi.dwo.rest.dom.entities.DomSchoolsRolesAndClasses;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.persistence.PersistenceClassType;
import fi.dwo.rest.persistence.PersistenceId;
import fi.dwo.rest.util.Dwo2ExceptionTranslator;

public class DWO2player extends DWOplayer implements EntryPoint {

//	final class DubbeleLogin implements LoginPresenter {
//
//		@Override
//		public void otherlogin(AsyncCallback<Boolean> callback) {
//			boolean ok = Window.confirm(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoGlobalVars.getDwoLocale(), Dwo2ExceptionCode.User_ConfirmNewLoginSession));
//			callback.onSuccess(Boolean.valueOf(ok)); // DOORGAAN
//		}
//		
//	}
	
	
	
	final class AsyncUserCallback implements AsyncCallback<DomUserFullwLoginContext> {
		private final SecuredUserSchoolLoginManager schoolManager;
		Map<String,Object> profile = new HashMap<String,Object>();
		AsyncCallback<? super Map<String,Object>> callback;

		AsyncUserCallback(SecuredUserSchoolLoginManager schoolManager, AsyncCallback<? super Map<String,Object>> callback) {
			this.schoolManager = schoolManager;
			this.callback = callback;
		}

		@Override
		public void onSuccess(DomUserFullwLoginContext result) {
			DomLoginContext context = result.getDomLoginContext();
			timezone =  context.getLastLoginTimeStamp().longValue() - System.currentTimeMillis();
			DomUserFull user = result.getDomUserFull();
			DwoGlobalVars.getInstance().setCurrentUser(user);
			DwoGlobalVars.getInstance().setCurrentLoginContext(context);
			toProfile(user, profile);
			schoolManager.getSchoolLogins(new AsyncCallback<DomSchoolsRolesAndClasses>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);			
					}

					@Override
					public void onSuccess(DomSchoolsRolesAndClasses result) {
						toProfile(result, profile);
						callback.onSuccess(profile);
					}
				});
			
			
		}

		@Override
		public void onFailure(Throwable caught) {
			if(caught.getMessage().contains("Cancelled"))
				return; // Probeer het nog eens...
			callback.onFailure(caught);
		}
	}

	public DWO2player() {
        //Initialize an Exception translator.
        Dwo2ExceptionTranslator.setTranslator(new Dwo2ExceptionGWTTranslator());
        
        getUserBar().setResetLogin(new Command() {
        	Place place = new ReloginPlace(); // FIXME met een hash?
        	
			@Override
			public void execute() {
				toProfile(DwoGlobalVars.instance().getCurrentUser(), profiledata); // refresh profiledata esp. password
				clientfactory.getPlaceController().goTo(place);
			}
        	
        });
        
        
	}
	
	
	@Override
	public void setupDWOPlayer() {
		super.setupDWOPlayer();
		if( MGWT.getOsDetection().isAndroid() )
			getUserBar().getElement().getStyle().setColor("white");
	}


	private static final String DWO_SAML_AUTH_TOKEN = "dwoSAMLAuthToken";
	private UserBar userBar = new UserBar();

	protected UserBar getUserBar() {
		return userBar;
	}

	
	protected ClientFactory createClientFactory() {
		ClientFactoryImpl factory = new ClientFactoryImpl() { 
			
			private IsWidget  menuWidget;
			@Override
			public IsWidget getMenuWidget() {
				return menuWidget;
			}

			@Override
			public LoginView getLoginView()
			{
				if (loginView == null)
					loginView = new Login2ViewImpl();
				return loginView;
			}

			@Override
			public void logout() {
				super.logout();
				menuWidget = null;
				if(profiledata != null)
				{
					getRPCHandler().logout();
				}
			}

			public SCORM_guest setupAPI(final Map<String, Object> profiledata) {
				SCORM_guest api;
				if(profiledata == null) {
					api = new SCORM_guest();
					menuWidget = null;
				} else {
					Object userID = profiledata.get("userID");
					Object sgID = profiledata.get("schoolGroupID");
					api = new SCORM_DWO2(userID, sgID);
					menuWidget = getUserBar();
				}
				return api;
			}

		};
		String host = PARAMETERS.getHost();
		String http = Window.Location.getProtocol();
		final SecuredUserAccountManager accountManager = new SecuredUserAccountManager();
		final SecuredUserSchoolLoginManager schoolManager = new SecuredUserSchoolLoginManager();
		final LoginPresenter ontdubbel = null; // new DubbeleLogin();
		factory.setRPCHandler(new RPCHandler(http + "//" + host + "/dwo/xmlrpc"){

			public void loginMD5(final String name, final String password, final AsyncCallback<? super Map<String,Object>> callback)
			{
				final AsyncCallback<DomUserFullwLoginContext> userCallback = new AsyncUserCallback(schoolManager, callback);
				accountManager.loginUserMD5(name, password, userCallback, null);
				
			}
			public void login(final String name, final String password, final AsyncCallback<? super Map<String,Object>> callback)
			{
				final AsyncCallback<DomUserFullwLoginContext> userCallback = new AsyncUserCallback(schoolManager, callback);
				accountManager.loginUser(name, password, userCallback, ontdubbel);
				
			}
			
			@Override
			public <T> void getUserResults(Object courseID, Object userID,
					AsyncCallback<T> getUserResultsCallback) {
				Object schoolGroupID = DWOplayer.profiledata.get("schoolGroupID");
				Object[] params = { courseID, userID, schoolGroupID };
				XmlRpcClient client = getClient();
				XmlRpcRequest<T> request = new XmlRpcRequest<T>(client, "getUserResults", params, getUserResultsCallback);
				request.execute();
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
				final AsyncUserCallback userCallback = new AsyncUserCallback(schoolManager, callback);
				accountManager.samlLogin(name, org, authToken, userCallback);
			}

			/* (non-Javadoc)
			 * @see nl.uu.fi.dwo.mobile.client.ui.activities.RPCHandler#getAuthTokenUser(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
			 */
			@Override
			public void getAuthTokenUser(String authToken,
					AsyncCallback<? super Map<String, Object>> callback) {
				final AsyncUserCallback userCallback = new AsyncUserCallback(schoolManager, callback);
				accountManager.getAuthTokenUser(authToken, userCallback);
			}
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

		});
		return factory;
	}

// From RestRpcHandler
	void toProfile(DomSchoolsRolesAndClasses result, Map<String, Object> profile) {
		DomSchoolRoleAndClass active = result.getActiveSchoolRoleAndClass();
		PersistenceId userId = active.getUserId();
		PersistenceId classId = active.getSchoolClassId();
		PersistenceId schoolId = active.getSchoolId();
		PersistenceId sgId = active.getSchoolGroupId();

		PersistenceIdDecoderInterface instance = PersistenceIdDecoderInterface.instance;
		profile.put("userID", instance.idOf(userId, PersistenceClassType.PersistentUser));
		profile.put("iconizer", active.getIconizer());
		profile.put("classID", classId == null ? "" :
				instance.idOf(classId, PersistenceClassType.PersistentSchoolClass));
		profile.put("schoolID", schoolId == null ? "" :
				instance.idOf(schoolId, PersistenceClassType.PersistentSchool));
		profile.put("schoolName", active.getSchoolName());
		profile.put("groupname",  active.getRoleName());
		profile.put("class", active.getSchoolClassName());
		profile.put("groupID", instance.idOf(active.getRoleId(), PersistenceClassType.PersistentRole));
		profile.put("schoolGroupID", instance.idOf(sgId, PersistenceClassType.PersistentSchoolGroup));
		DomSchoolClass schoolClass = null;
		if(classId != null) {
			schoolClass = new DomSchoolClass();
			schoolClass.setId(classId);
			schoolClass.setSchoolClassName(active.getSchoolClassName());
			schoolClass.setHasRegKey(Boolean.TRUE); // unknown?
		}
		DwoGlobalVars.instance().setCurrentSchoolClass(schoolClass);
		RoleType role = RoleType.NONE;
		try { role = RoleType.valueOf(active.getRoleName()); } catch(Exception ignore) {}
		getUserBar().setRole(role);
	}

	void toProfile(DomUserFull result, Map<String, Object> profile) {
		profile.put("firstname", result.getGivenName());
		profile.put("middlename", result.getInsertion());
		profile.put("lastname", result.getFamilyName());
		profile.put("userID", PersistenceIdDecoderInterface.instance.idOf(result.getId(), PersistenceClassType.PersistentUser));
		profile.put("username", result.getUserName());
		profile.put("password",result.getPassword());
		
		getUserBar().setSingleSchool(result.getSingleSchool());
		
	}

	
}
