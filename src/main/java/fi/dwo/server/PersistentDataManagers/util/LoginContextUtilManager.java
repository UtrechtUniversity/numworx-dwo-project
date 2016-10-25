/**
 * Copyrighted Jul 28, 2016
 */
package fi.dwo.server.PersistentDataManagers.util;

import fi.dwo.commons.persistence.entities.PersistentLoginContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.util.DwoDateUtilities;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.server.PersistentDataManagers.core.LoginContextManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
public class LoginContextUtilManager {

    private static final Logger LOG = Logger.getLogger(LoginContextUtilManager.class.getName());

    /**
     * Gets the current LoginContext for the user, if none exists throws an exception.
     * 
     * @param user
     * @return the current LoginContext for the user or null otherwise.
     * @throws Dwo2Exception of with {@link Dwo2ExceptionCode#Rest_No_LoginContext_exists}
     */
    public static PersistentLoginContext getCurrentLoginContext(PersistentUser user) throws Dwo2Exception {
        PersistentLoginContext loginContext = null;
        try {
            List<PersistentLoginContext> loginContextList = LoginContextManager.findEntities(user.getId());
            loginContext = null;
            switch (loginContextList.size()) {
                case 0:
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_No_LoginContext_exists, "There is no current LoginContext set for the user.");
                case 1:
                    //update if exists
                    loginContext = loginContextList.get(0);
                    break;
                default:
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't get or set a LoginContext for a user", e);
            Dwo2Exception ex = new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Can't set a LoginContextSession for a user");
            throw ex;
        }
        return loginContext;
    }

    /**
     * return a valid LoginContext when a new session is set, returns null if
     * one exists.
     *
     * @param user
     * @return
     * @throws Dwo2Exception of with {@link Dwo2ExceptionCode#Rest_LoginContext_exists}
     */
    public static PersistentLoginContext reqLoginContextSession(PersistentUser user) throws Dwo2Exception {
        PersistentLoginContext loginContext = null;
        try {
            List<PersistentLoginContext> loginContextList = LoginContextManager.findEntities(user.getId());
            loginContext = new PersistentLoginContext();
            switch (loginContextList.size()) {
                case 0:
                    //none yet
                    loginContext.setUserId(user.getId());
                    loginContext.setLastLogin(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
                    loginContext.setRegisterTimeStamp(user.getRegisterDate().getTime());
                    //loginContextList.add(loginContext);
                    LoginContextManager.create(loginContext);
                    break;
                case 1:
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_LoginContext_exists, "There already is a LoginContext set for the user.");
                default:
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't get or set a LoginContextSession for a User", e);
            Dwo2Exception ex = new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Can't set a LoginContextSession for a User");
            throw ex;
        }
        return loginContext;
    }

   /**
     * return a valid LoginContext, if one exists its values are overwritten and
     * a new session is started.
     *
     * @param user
     * @return
     * @throws Dwo2Exception
     */
    public static PersistentLoginContext forceNewLoginContextSession(PersistentUser user) throws Dwo2Exception {
        PersistentLoginContext loginContext = null;
        try {
            List<PersistentLoginContext> loginContextList = LoginContextManager.findEntities(user.getId());
            loginContext = new PersistentLoginContext();
            switch (loginContextList.size()) {
                case 0:
                    //none yet
                    loginContext.setUserId(user.getId());
                    loginContext.setLastLogin(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
                    loginContext.setRegisterTimeStamp(user.getRegisterDate().getTime());
                    //loginContextList.add(loginContext);
                    LoginContextManager.create(loginContext);
                    break;
                case 1:
                    //update if exists
                    loginContext = loginContextList.get(0);
                    loginContext.setLastLogin(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
                    LoginContextManager.edit(loginContext);
                    break;
                default:
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't get or set a LoginContextSession for a User", e);
            Dwo2Exception ex = new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Can't set a LoginContextSession for a User");
            throw ex;
        }
        return loginContext;
    }    
}
