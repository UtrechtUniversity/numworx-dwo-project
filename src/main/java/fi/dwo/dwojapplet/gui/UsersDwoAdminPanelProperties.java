/**
 * Copyrighted Mar 11, 2016
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.dwojapplet.domain.rest.SecureDwoAdminUserManager;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import java.util.List;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;

/**
 *
 * @author Gert van der Plas
 */
public class UsersDwoAdminPanelProperties {

    private static final Logger LOG = Logger.getLogger(UsersDwoAdminPanelProperties.class.getName());

    public UsersDwoAdminPanelProperties() {

    }

    public List<DomUser> getUserList() throws Dwo2Exception {
        return SecureDwoAdminUserManager.getUserList();
    }
    
}
