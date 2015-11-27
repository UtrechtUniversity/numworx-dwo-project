package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.dom.entities.DomNewUser;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import java.util.logging.Logger;

/**
 * Manages the user profile.
 *
 * @author G.A.J. van der Plas
 */
public class PublicUserRegistrationManager {

    private static final Logger LOG = Logger.getLogger(PublicUserRegistrationManager.class.getName());

    public static boolean RegisterNewUser(DomNewUser newUserReg) throws Dwo2Exception {
        boolean r;
        r = StoredRestManager.getInstance().put("/public/registration/submit", Boolean.class, newUserReg);

        return r;
    }



}
