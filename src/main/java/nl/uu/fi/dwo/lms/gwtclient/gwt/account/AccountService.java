package nl.uu.fi.dwo.lms.gwtclient.gwt.account;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import org.osgi.util.promise.Promise;


/**
 *
 * @author G.A.J. van der Plas
 */
public class AccountService {
    
    private static final Logger LOG = Logger.getLogger(AccountService.class.getName());

    private SecuredUserAccountManager manager = new SecuredUserAccountManager();
    private final DwoGlobalVars dwoGlobalVars;
    
    public AccountService(DwoGlobalVars aDwoGlobalVars){
        dwoGlobalVars = aDwoGlobalVars; // for future use (hasRole fetch i.e.)
    }
   public Promise<DomUserFull> getUserData() {
        return manager.getAccountData();
    }
   
   public Promise<DomUserFull> UpdateUserData(DomUserFull user){
        return manager.updateAccountData(user);       
   }
}
