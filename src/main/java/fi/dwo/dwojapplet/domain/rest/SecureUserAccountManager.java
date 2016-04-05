package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.dom.entities.DomSchool;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.entities.RestFullUser;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

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
        user = StoredRestManager.getInstance().get("/rest/secure/user/account/get", DomUserFull.class);
        return user;
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
        RestFullUser restUser = new RestFullUser();
        restUser.setRestContext(new DomContext());
        restUser.setDomFullUser(user);

        user = StoredRestManager.getInstance().put("/rest/secure/user/account/update", DomUserFull.class, restUser);
        HttpAuthenticationFeature feature = null;
        switch (DwoHelper.getHttpAuthentication()) {
            case BASIC:
                feature = HttpAuthenticationFeature.universalBuilder().credentialsForBasic(user.getUserName(), user.getPassword()).build();
                break;
            case DIGEST:
                feature = HttpAuthenticationFeature.universalBuilder().credentialsForDigest(user.getUserName(), user.getPassword()).build();
        }
        Client client = ClientBuilder.newClient().register(feature);
        WebTarget target = client.target(DwoHelper.getServerUrlPath().toString());
        StoredRestManager.setWebTargetRest(target);

        DwoHelper.setCurrentUser(user);
        LOG.log(Level.FINE, "Updated user profile of username {0}.", new Object[]{restUser.getDomFullUser().getUserName()});
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
        b = StoredRestManager.getInstance().get("/rest/secure/user/account/remove", Boolean.class);
        return b;
    }

    public static DomSchool getNullSchool() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
