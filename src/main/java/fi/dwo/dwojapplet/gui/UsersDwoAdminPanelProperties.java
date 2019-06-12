/**
 * Copyrighted Mar 11, 2016
 */
package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminUserManager;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import java.util.List;
//import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

/**
 *
 * @author Gert van der Plas
 */
public class UsersDwoAdminPanelProperties {

    //private static final Logger LOG = Logger.getLogger(UsersDwoAdminPanelProperties.class.getName());

    public UsersDwoAdminPanelProperties() {

    }

    public List<DomUserFull> getUserList() throws Dwo2Exception {
        return SecureDwoAdminUserManager.getUserList();
    }
    public DomUserFull get(DomUser user) throws Dwo2Exception {
        return SecureDwoAdminUserManager.get(user);
    }
    public DomUserFull update(DomUserFull user) throws Dwo2Exception {
        return SecureDwoAdminUserManager.update(user);
    }
    
}
