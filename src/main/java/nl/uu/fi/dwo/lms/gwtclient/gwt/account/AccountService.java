package nl.uu.fi.dwo.lms.gwtclient.gwt.account;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserSchoolLoginManagerV2;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolLogin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import org.osgi.util.promise.Promise;


/**
 *
 * @author G.A.J. van der Plas
 */
public class AccountService {
    
    private static final Logger LOG = Logger.getLogger(AccountService.class.getName());

    private SecuredUserAccountManager accountManager = new SecuredUserAccountManager();
    private SecuredUserSchoolLoginManagerV2 schoolLoginManager = new SecuredUserSchoolLoginManagerV2();

    private final DwoGlobalVars dwoGlobalVars;
    
    public AccountService(DwoGlobalVars aDwoGlobalVars){
        dwoGlobalVars = aDwoGlobalVars; // for future use (hasRole fetch i.e.)
    }
   public Promise<DomUserFull> getUserData() {
        return accountManager.getAccountData();
    }
   
   public Promise<DomUserFull> UpdateUserData(DomUserFull user){
        return accountManager.updateAccountData(user);       
   }

    Promise<DomSchoolRoleAndClassV2> switchToSchoolLogin(DomSchoolRoleAndClassV2 srac) {
        return schoolLoginManager.switchToSchoolLogin(srac);
    }
    
    Promise<Boolean> addASchoolLogin(String role, String schoolLogin, String accessCode) {
        DomNewSchoolLogin data = new DomNewSchoolLogin();
        data.setRole(RoleType.valueOf(role));
        data.setSchoolLogin(schoolLogin);
        data.setSchoolCode(accessCode);
        return schoolLoginManager.addASchoolLogin(data);
    }

    Promise<Boolean> removeASchoolLogin(DomSchoolRoleAndClassV2 data) {
        return schoolLoginManager.removeASchoolLogin(data);
    }
    
    public Promise<String> getBearerToken() {
      return accountManager.getBearerToken();
    }
 }
