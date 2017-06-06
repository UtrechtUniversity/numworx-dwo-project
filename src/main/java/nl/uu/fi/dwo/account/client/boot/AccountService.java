package nl.uu.fi.dwo.account.client.boot;

import com.google.gwt.i18n.client.NumberFormat;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredUserAccountManager;
import java.util.logging.Logger;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import org.osgi.util.promise.Promise;


/**
 *
 * @author G.A.J. van der Plas
 */
public class AccountService {
    
    private static final Logger LOG = Logger.getLogger(AccountService.class.getName());

    private SecuredUserAccountManager manager = new SecuredUserAccountManager();

   public Promise<DomUserFull> getUserData() {
        DomContext context = new DomContext();
        context.setDomHasRole(DwoGlobalVars.instance().getActiveSchoolRoleAndClass().getHasRole());
//        DomDwoProfile profile = new DomDwoProfile();
//        int profileId = DwoGlobalVars.instance().getProfileId();
//        String formattedId = NumberFormat.getFormat("00000000000000000000").format(profileId);
//        profile.setId(new PersistenceId("MYSQL;PersistentDwoProfile;"+formattedId));
//        profile.setDwoProfileName("test");
//        profile.setDwoProfileRights("_");
        return manager.getAccountData();
    }
   
//   public Promise<DomUserFull> UpdateUserData(DomUserFull user){
//        DomContext context = new DomContext();
//        context.setDomHasRole(DwoGlobalVars.instance().getActiveSchoolRoleAndClass().getHasRole());
//        DomDwoProfile profile = new DomDwoProfile();
//        int profileId = DwoGlobalVars.instance().getProfileId();
//        String formattedId = NumberFormat.getFormat("00000000000000000000").format(profileId);
//        profile.setId(new PersistenceId("MYSQL;PersistentDwoProfile;"+formattedId));
//        profile.setDwoProfileName("test");
//        profile.setDwoProfileRights("_");
//        return manager.updateAccountData(user, callBack);       
//   }
}
