package nl.uu.fi.dwo.mobile;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.promise.Success;

import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Cookies;

import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.CallManagers.OAuthManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentSchoolClassManager;
import fi.dwo.gwt.lib.rest.CallManagers.XapiManager;
import fi.dwo.gwt.lib.rest.util.Base64;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.TrafficAgent;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFullwLoginContext;

@Singleton
public final class DWO2RPCHandler extends nl.uu.fi.dwo.account.client.RPCHandlerV3 implements RPCHandler {

		private final DwoGlobalVars vars;
		private final DWOplayerParameters PARAMETERS;

		@Inject DWO2RPCHandler(@Named("profile") int profile, TrafficAgent agent, DwoGlobalVars vars, DWOplayerParameters PARAMETERS) {
			super(null, profile, false);
			this.agent = agent;
			this.vars = vars;
			this.PARAMETERS = PARAMETERS;
			
		}
// MISSING clear schoollogins etc.
		@Override
		public Promise<Void> logout() {
			return super.logout().then ( p -> {
				vars.setSchoolLogins(null);
				vars.setActiveSchoolRoleAndClass(null);
				xapi = null;
				return p;
			});
		}
		
		private Promise<XapiManager> xapi; // caching xapi 1 per login
		
		@Override
		public Promise<XapiManager> getLRS() {
		  if (xapi == null) {
		    xapi = super.getLRS();
		  }
		  return xapi;
		}
    @Override
    public Promise<DomUserFullwLoginContext> getUserFromAuthToken(String authToken) {
      if (PARAMETERS.getDwoEnv().contains("test")||PARAMETERS.getDwoEnv().contains("saml"))
        return super.getUserFromOAuthToken(authToken);
      else
        return super.getUserFromAuthToken(authToken);
    }

    @Override
    public Promise<DomUserFullwLoginContext> samlLogin(String name, String org) {
      String authToken = Cookies.getCookie(DWO_SAML_AUTH_TOKEN);
      authToken = "3\f" + name + '\f' + org + '\f' + authToken;
      return super.getUserFromOAuthToken(Base64.btoa(authToken));
    }
		
    public Promise<JSONValue> refreshExam() {
    	return accountManager.verifyTOTP(context).then(p -> { 
    		JSONString str = p.getValue().isString();
    		if (str != null) {
    			GwtRestVars vars = GwtRestVars.getInstance();
    			Map<String,String> headers = vars.getCustomHeaders();
			    headers.put("X-TOTP", str.stringValue());
    		}
    		return p;
    	} );
    }
    
    private final SecuredStudentSchoolClassManager classManager = new SecuredStudentSchoolClassManager();
    	
    public Promise<List<DomSchoolClass>> getStudentsSchoolClasses() {
    		return classManager.getStudentsSchoolClasses(context);
    	}

    public Promise<Boolean> setActiveSchoolClass(DomSchoolClass schoolClass) {
    	return classManager.setActiveSchoolClass(context, schoolClass).then(p -> { 
    		if (p.getValue())
    			vars.getActiveSchoolRoleAndClass().setSchoolClass(schoolClass);
    		return p;
    	});
    }

	
	private DomClassCourse exam;
	private final TrafficAgent agent;	
	
	@Override
	public Promise<Void> startExam(final DomClassCourse classCourse, final String password) {
		Promise<Void> p = agent.barrier()
			.then(new Success<Void, Void>() {

					@Override
					public Promise<Void> call(Promise<Void> resolved)
							throws Exception {
						return DWO2RPCHandler.super.startExam(classCourse.getId().toString(), password);
					}
			})
			.then(new Success<Void, Void>() {

				@Override
				public Promise<Void> call(Promise<Void> resolved)
						throws Exception {
					exam = classCourse;
					return null;
				}
			});
		agent.addBarrier(p); // 
		return p;
	}
	
	@Override
	public boolean inExam(DomClassCourse classCourse) {
		return (exam != null) &&
			exam.getId().equals(classCourse.getId());
	}
	@Override
	public Promise<DomUserFullwLoginContext> login(String name, String password) {
		// TODO Auto-generated method stub
		Promise<DomUserFullwLoginContext> login = super.login(name, password);
		if (!PARAMETERS.getDwoEnv().contains("test")) return login;

		return login
		   .flatMap(
				(DomUserFullwLoginContext dom) ->
				{
					if (!dom.getDomUserFull().getSingleSchool()) return login;
					
					DomContext context = new DomContext();
					context.setDomHasRole(new DomHasRole());
					context.getDomHasRole().setId(dom.getDomLoginContext().getHasRoleId());
					return
					accountManager.getBearerToken(context)
					.then(
				    	(Promise<String> p) -> { 
				            OAuthManager oauth = new OAuthManager();
				            String token = Base64.btoa("2\f" + p.getValue()); // Format 2 
				            return oauth.authorization_token(token);
				    	}   		  
				        ).then(p -> { 
				      	  GwtRestVars.getInstance().setBearerToken(p.getValue().getAccess_token());
				      	  GwtRestVars.getInstance().setRefreshToken(p.getValue().getRefresh_token());
				      	  return login;
				      	  
				        });
					
				});
	}
	@Override
	public Promise<DomUserFullwLoginContext> loginMD5(String name, String password) {
		// TODO Auto-generated method stub
		return super.loginMD5(name, password);
	}

	
	
	
	}