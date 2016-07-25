package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.entities.RestUserFull;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.net.Authenticator;
import java.net.CookieManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the user profile.
 *
 * @author G.A.J. van der Plas
 */
public class SecureUserAccountManager {

    private static final Logger LOG = Logger.getLogger(SecureUserAccountManager.class.getName());

    /**
     * Returns the current user 'logged in'. The information is extracted from
     * the security context which depends on the credentials used for accessing
     * the rest interface. Technically it should be equal to the data in the
     * DwoHelper.
     *
     * @return
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static DomUserFull getAccountData() throws Dwo2Exception {
        DomUserFull user;
        user = StoredRestManager.getInstance().get("rest/secure/user/account/get", DomUserFull.class);
        return user;
    }

    /**
     * Login for a user. Registers service that the user is logging in. As the
     * REST interface is stateless this is merely for gathering statistics.
     *
     * @return
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static DomUserFull loginUser() throws Dwo2Exception {
        DomUserFull user;
        user = StoredRestManager.getInstance().get("rest/secure/user/account/login", DomUserFull.class);
        return user;
    }

    /**
     * Registers that the user logs out. When doing basic authentication basicAuthLogout is 
     * recommended to be used.
     *
     * @return
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static Boolean logoutUser() throws Dwo2Exception {
        Boolean result;
        result = StoredRestManager.getInstance().get("rest/secure/user/account/logout", Boolean.class);
        //ensures basic auth data and cookies are wiped from Java Browser-like framework
        Authenticator.setDefault(null);
        CookieManager.setDefault(null);
        return result;
    }

    /**
     * Registers that the user logs out, clears client-side cookies and authenticator data,
     * and clears the session server-side.
     *
     * @return
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static Boolean basicAuthLogout() throws Dwo2Exception {
        Boolean result;
        result = StoredRestManager.getInstance().get("rest/secure/user/account/basicAuthLogout", Boolean.class);
        //ensures basic auth data and cookies are wiped from Java Browser-like framework
        Authenticator.setDefault(null);
        CookieManager.setDefault(null);

        return result;
    }
    
    /**
     * Updates the user profile of a user.
     *
     * Fields updated are email, password and the full name of the user. The
     * full name exists out of the first, insertion and family name.
     *
     * @param user
     * @return
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static DomUserFull updateAccountData(DomUserFull user) throws Dwo2Exception {
        RestUserFull restUser = new RestUserFull();
        restUser.setRestContext(new DomContext());
        restUser.setDomUserFull(user);

        user = StoredRestManager.getInstance().put("rest/secure/user/account/update", DomUserFull.class, restUser);
//        Client client = ClientBuilder.newClient().register(feature);
//        WebTarget target = client.target(DwoHelper.getServerUrlPath().toString());
//        StoredRestManager.setWebTargetAndCredentials(target);
        StoredRestManager.setBasicAuthString(null);

        DwoHelper.setCurrentUser(user);
        LOG.log(Level.FINE, "Updated user profile of username {0}.", new Object[]{restUser.getDomUserFull().getUserName()});
        return user;
    }

    /**
     * Updates the user profile of a user.
     *
     * Fields updated are email, password and the full name of the user. The
     * full name exists out of the first, insertion and family name.
     *
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static Boolean removeAccountData() throws Dwo2Exception {
        Boolean b;
        b = StoredRestManager.getInstance().get("rest/secure/user/account/remove", Boolean.class);
        return b;
    }
}
