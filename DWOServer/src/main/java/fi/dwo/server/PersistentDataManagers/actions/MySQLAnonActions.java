/**
 * Copyrighted Jan 19, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginCheck;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 *
 * @author Gert van der Plas
 */
public class MySQLAnonActions implements AnonActions {
    @Override
    public boolean getLoginCheck(DomLoginCheck check) throws Dwo2Exception {
        PersistentUser user = UserManager.login(check.getUsername(), DomLoginCheck.crypt(check.getPassword()));
        return (user != null);
    }
    
}
