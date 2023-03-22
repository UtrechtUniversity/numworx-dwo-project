package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_U;
import java.util.logging.Logger;

import javax.ws.rs.core.SecurityContext;

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

    private Context context;

    /**
     * Creates a builder and initializes a context if given.
     */
    public static AnonState build() throws Dwo2Exception {
        AnonBuilder builder = new AnonBuilder();
        return builder.init();
    }

    /**
     * Ensure that it is not called other than from the builder.
     */
    protected AnonDomainAuthorizer() {
        //TODO inject executror
    }

    public static class Context {

        private AnonPersistentContext anonCtx;

        public Context() {
            anonCtx = new AnonPersistentContext();
        }

        /**
         * @return the anonCtx
         */
        public AnonPersistentContext getAnonCtx() {
            return anonCtx;
        }

        /**
         * @param anonCtx the anonCtx to set
         */
        public void setAnonCtx(AnonPersistentContext anonCtx) {
            this.anonCtx = anonCtx;
        }
    }

    protected static class AnonPersistentContext {

        protected AnonPersistentContext() {

        }

        protected AnonPersistentContext(AnonPersistentContext ctx) {

        }
    }

    public interface AnonState {

        UserState_U submitUser(String u) throws Dwo2Exception;

        UserState_U submitUser(DomUser u) throws Dwo2Exception;

        public boolean LoginCheck(DomLoginCheck check) throws Dwo2Exception;

		UserState_U submitUser(SecurityContext sc) throws Dwo2Exception;
    }

    public interface PublicContext {

        public Context getContext();

        public void setContext(Context context);
    }
    //return any public server info here. For example version info

    /**
     * @return the context
     */
    protected Context getContext() {
        return this.context;
    }

    /**
     * @param context the context to set
     */
    protected void setContext(Context context) {
        this.context = context;
    }
}
