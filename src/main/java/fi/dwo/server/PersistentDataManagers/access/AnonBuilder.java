/**
 * Copyrighted Mar 7, 2018
 */
package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.actions.AnonActions;
import fi.dwo.server.PersistentDataManagers.actions.MySQLAnonActions;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginCheck;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 *
 * @author Gert van der Plas
 */
class AnonBuilder implements AnonDomainAuthorizer.AnonState, AnonDomainAuthorizer.Build {
    
    private AnonDomainAuthorizer instance = new AnonDomainAuthorizer();
    private AnonActions anonActions = new MySQLAnonActions();

    protected AnonBuilder() throws Dwo2Exception {
    }

    /**
     * Verifies and stores the PersistentUser into the context.
     *
     * @param username
     * @return
     * @throws Dwo2Exception
     */
    public UserDomainAuthorizer.UserState_U submitUser(String username) throws Dwo2Exception {
        return UserDomainAuthorizer.user(instance, username);
    }

    public UserDomainAuthorizer.UserState_U submitUser(DomUser u) throws Dwo2Exception {
        return UserDomainAuthorizer.user(instance, u.getUserName());
    }

    protected UserDomainAuthorizer.UserState_U submitUser(PersistentUser u) throws Dwo2Exception {
        return UserDomainAuthorizer.user(instance, u.getUsername());
    }

    public boolean LoginCheck(DomLoginCheck check) throws Dwo2Exception {
        return anonActions.getLoginCheck(check);
    }
    
}
