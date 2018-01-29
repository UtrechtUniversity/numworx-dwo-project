package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.server.PersistentDataManagers.actions.AnonActions;
import fi.dwo.server.PersistentDataManagers.actions.MySQLAnonActions;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_U;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomLoginCheck;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * Builder to retrieve persistence data in a cascading way an verify access and
 * dynamic model rules. This builder is fluid builder. Technically the class
 * forms a state machine where the interfaces denote the possible transitions
 * (edges in a directed graph). Thus a regular language for the security access
 * can be built.
 *
 * @author G.A.J. van der Plas
 */
public class AnonDomainAuthorizer {

    private static final Logger LOG = Logger.getLogger(AnonDomainAuthorizer.class.getName());

    private AnonPersistentContext context = new AnonPersistentContext();

    public class AnonPersistentContext {
        //currently no context info (although HeaderInfo other REST payload
        //could be set here.
    }

    protected AnonDomainAuthorizer() {
        //TODO inject executror
    }

    public static AnonState build() throws Dwo2Exception {
        return new AnonDomainAuthorizer.Builder();
    }

    public interface AnonState {
        UserState_U setUser(String u) throws Dwo2Exception;
        UserState_U setUser(DomUser u) throws Dwo2Exception;
        public boolean LoginCheck(DomLoginCheck check) throws Dwo2Exception;
    }

    public interface Build {
        //return any public server info here.
    }

    private static class Builder implements AnonState, Build {
        private AnonDomainAuthorizer instance = new AnonDomainAuthorizer();
            private AnonActions anonActions = new MySQLAnonActions();

        public Builder() throws Dwo2Exception {
        }
    
    /**
     * Verifies and stores the PersistentUser into the context.
     *
     * @param username
     * @return
     * @throws Dwo2Exception
     */
    public UserState_U setUser(String username) throws Dwo2Exception {
        return UserDomainAuthorizer.user(username);
    }

    public UserState_U setUser(DomUser u) throws Dwo2Exception {
        return UserDomainAuthorizer.user(u.getUserName());
    }

    protected UserState_U setUser(PersistentUser u) throws Dwo2Exception {
        return UserDomainAuthorizer.user(u.getUsername());
    }
    
    public boolean LoginCheck(DomLoginCheck check) throws Dwo2Exception
    {
        return anonActions.getLoginCheck(check);
    }

}

}
