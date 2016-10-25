package nl.uu.fi.dwo.mobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.account.client.RPCHandlerV2;
import nl.uu.fi.dwo.account.client.UserBar;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_DWO2;
import nl.uu.fi.dwo.mobile.client.sco.SCORM_guest;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactory;
import nl.uu.fi.dwo.mobile.client.ui.ClientFactoryImpl;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.places.ReloginPlace;
import nl.uu.fi.dwo.mobile.client.ui.views.Login2ViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.LoginView;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.DomRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.mgwt.ui.client.MGWT;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManagerV2;
import fi.dwo.gwt.lib.rest.util.Dwo2ExceptionGWTTranslator;
import fi.dwo.gwt.lib.rest.util.PersistenceIdDecoderInterface;

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
	
	private final class DWO2RPCHandler extends RPCHandlerV2 implements RPCHandler{
		private DWO2RPCHandler(String server, int profile) {
			super(server, profile);
		}

		@Override
		public void loginMD5(final String name, final String password, final AsyncCallback<Map<String,Object>> callback)
		{
			final AsyncCallback<DomUserFullwLoginContext> userCallback = new AsyncUserCallback(schoolManager, callback);
			loginMD5Helper(name, password, userCallback);	
		}

		@Override
		public void login(final String name, final String password, final AsyncCallback<Map<String,Object>> callback)
		{
			final AsyncCallback<DomUserFullwLoginContext> userCallback = new AsyncUserCallback(schoolManager, callback);
			loginHelper(name, password, userCallback);
			
		}

		@Override
		public void getUserResults(Object courseID, Object userID,
				AsyncCallback<List<Map<String,Object>>> getUserResultsCallback) {
			Object schoolGroupID = DWOplayer.profiledata.get("schoolGroupID");
			getUserResultsHelper(courseID, userID, schoolGroupID, getUserResultsCallback);
		}

		@Override
		public void samlLogin(String name, String org,
				AsyncCallback<Map<String, Object>> callback) {
			final AsyncUserCallback userCallback = new AsyncUserCallback(schoolManager, callback);
			samlLoginHelper(name, org, userCallback);
		}

		/* (non-Javadoc)
		 * @see nl.uu.fi.dwo.mobile.client.ui.activities.RPCHandler#getAuthTokenUser(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
		 */
		@Override
		public void getUserFromAuthToken(String authToken,
				AsyncCallback<Map<String, Object>> callback) {
			final AsyncUserCallback userCallback = new AsyncUserCallback(schoolManager, callback);
			getUserFromAuthTokenHelper(authToken, userCallback);
		}
	}

	final class AsyncUserCallback implements AsyncCallback<DomUserFullwLoginContext> {
		private final SecuredUserSchoolLoginManagerV2 schoolManager;
		Map<String,Object> profile = new HashMap<String,Object>();
		AsyncCallback<? super Map<String,Object>> callback;

		AsyncUserCallback(SecuredUserSchoolLoginManagerV2 schoolManager, AsyncCallback<? super Map<String,Object>> callback) {
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
			schoolManager.getSchoolLoginsV2(new AsyncCallback<DomSchoolsRolesAndClassesV2>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);			
					}

					@Override
					public void onSuccess(DomSchoolsRolesAndClassesV2 result) {
						DwoGlobalVars.instance().setSchoolLogins(result);
						DomSchoolRoleAndClassV2 active = result.getActiveSchoolRoleAndClass();
						DomSchoolClass schoolClass = active != null ? active.getSchoolClass() : null;
						DwoGlobalVars.instance().setCurrentSchoolClass(schoolClass);

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
		factory.setRPCHandler(new DWO2RPCHandler(http + "//" + host + "/dwo/xmlrpc", PROFILE_ID));
		return factory;
	}

// From RestRpcHandler
	void toProfile(DomSchoolsRolesAndClassesV2 result, Map<String, Object> profile) {
		PersistenceIdDecoderInterface instance = PersistenceIdDecoderInterface.instance;
		DomSchoolRoleAndClassV2 active = result.getActiveSchoolRoleAndClass();
		DomSchoolClass schoolClass;
		DomSchool      school;
		DomHasRole     hasRole;
		DomRole        role;
		schoolClass = active.getSchoolClass();
		school = active.getSchool();
		hasRole = active.getHasRole();
		role  = active.getRole();
		PersistenceId classId = schoolClass != null ? schoolClass.getId() : null;
		PersistenceId schoolId = school != null ? school.getId() : null;
		PersistenceId sgId = hasRole != null ? hasRole.getSchoolGroupId() : null;

		profile.put("iconizer", schoolClass != null ? schoolClass.getIconizer() : Boolean.FALSE);
		profile.put("classID", classId == null ? "" :
				instance.idOf(classId, PersistenceClassType.PersistentSchoolClass));
		profile.put("schoolID", schoolId == null ? "" :
				instance.idOf(schoolId, PersistenceClassType.PersistentSchool));
		profile.put("schoolName", school.getSchoolName());
		profile.put("groupname",  role.getRoleName());
		profile.put("class", schoolClass == null ? "" :schoolClass.getSchoolClassName());
		profile.put("groupID", instance.idOf(role.getId(), PersistenceClassType.PersistentRole));
		profile.put("schoolGroupID", instance.idOf(sgId, PersistenceClassType.PersistentSchoolGroup));
		RoleType roleType = RoleType.NONE;
		try { roleType = RoleType.valueOf(role.getRoleName()); } catch(Exception ignore) {}
		getUserBar().setRole(roleType);
	}

	void toProfile(DomUserFull result, Map<String, Object> profile) {
		profile.put("firstname", result.getGivenName());
		profile.put("middlename", result.getInsertion());
		profile.put("lastname", result.getFamilyName());
		profile.put("userID", PersistenceIdDecoderInterface.instance.idOf(result.getId(), PersistenceClassType.PersistentUser));
		profile.put("username", result.getUserName());
		profile.put("password",result.getPassword());
		PersistenceId userId = result.getId();
		PersistenceIdDecoderInterface instance = PersistenceIdDecoderInterface.instance;
		profile.put("userID", instance.idOf(userId, PersistenceClassType.PersistentUser));

		getUserBar().setSingleSchool(result.getSingleSchool());
		
	}

	
}
