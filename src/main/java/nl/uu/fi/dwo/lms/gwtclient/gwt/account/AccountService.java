package nl.uu.fi.dwo.lms.gwtclient.gwt.account;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManagerV2;
import java.util.logging.Logger;

import javax.inject.Inject;

import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolLogin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import org.osgi.util.promise.Promise;

import dagger.Reusable;


/**
 *
 * @author G.A.J. van der Plas
 */
@Reusable
public class AccountService {
    
    private static final Logger LOG = Logger.getLogger(AccountService.class.getName());

    private final SecuredUserAccountManager accountManager;
    private SecuredUserSchoolLoginManagerV2 schoolLoginManager = new SecuredUserSchoolLoginManagerV2();

    private final DwoGlobalVars dwoGlobalVars;

    
    @Inject public AccountService(DwoGlobalVars aDwoGlobalVars, SecuredUserAccountManager accountManager) {
        this.accountManager = accountManager;
        dwoGlobalVars = aDwoGlobalVars; // for future use (hasRole fetch i.e.)
    }

    public Promise<DomUserFull> getUserData() {
       DomContext context = createContext();
       return accountManager.getAccountData(context);
    }

	private DomContext createContext() {
		DomContext context = new DomContext();
		context.setDomHasRole(dwoGlobalVars.getActiveSchoolRoleAndClass().getHasRole());
	    context.setRealm(dwoGlobalVars.getCurrentLoginContext().getRealm());
		return context;
	}
   
   public Promise<DomUserFull> UpdateUserData(DomUserFull user){
        return accountManager.updateAccountData(createContext(), user);       
   }

    Promise<DomSchoolRoleAndClassV2> switchToSchoolLogin(DomSchoolRoleAndClassV2 srac) {
        return schoolLoginManager.switchToSchoolLogin(srac);
    }
    
    Promise<Boolean> addASchoolLogin(String role, String schoolLogin, String accessCode) {
        DomNewSchoolLogin data = new DomNewSchoolLogin();
        data.setRole(RoleType.valueOf(role));
        data.setSchoolLogin(schoolLogin);
        data.setSchoolCode(accessCode);
        return schoolLoginManager.addASchoolLogin(createContext(),data);
    }

    Promise<Boolean> removeASchoolLogin(DomSchoolRoleAndClassV2 data) {
        return schoolLoginManager.removeASchoolLogin(createContext(),data);
    }
    
    public Promise<String> getBearerToken() {
      return accountManager.getBearerToken(createContext());
    }
    
    public Promise<DomSchoolsRolesAndClassesV2> getSchoolLogins(){
        return schoolLoginManager.getSchoolLogins();
    }
    public Promise<Boolean> removeCurrentUser() {
      return accountManager.removeCurrentUser(createContext());
    }
 }
