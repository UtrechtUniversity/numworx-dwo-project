/**
 * Copyrighted Jan 19, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 *
 * @author Gert van der Plas
 */
public class MySQLUserActions implements UserActions {
    @Override
    public PersistentUser UpdateAccount(PersistentUser pUser, DomUserFull dUser) throws Dwo2Exception{
                pUser.setGivenName(dUser.getGivenName());
                pUser.setLastname(dUser.getFamilyName());
                pUser.setInsertion(dUser.getInsertion());
                pUser.setEmail(dUser.getEmail());
                pUser.setPassword(dUser.getPassword());
                //User to update is logged in user.
                pUser = UserManager.updateAccount(pUser);
                return pUser;
            }
}
