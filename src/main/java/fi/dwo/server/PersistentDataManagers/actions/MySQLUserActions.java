/**
 * Copyrighted Jan 19, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import javax.persistence.PersistenceException;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

/**
 *
 * @author Gert van der Plas
 */
public class MySQLUserActions extends MySQLAnonActions implements UserActions {

    @Override
    public PersistentUser UpdateAccount(PersistentUser pUser, DomUserFull dUser) throws Dwo2Exception {
        try {
            pUser.setGivenName(dUser.getGivenName());
            pUser.setLastname(dUser.getFamilyName());
            pUser.setInsertion(dUser.getInsertion());
            pUser.setEmail(dUser.getEmail());
            pUser.setPassword(dUser.getPassword());
            //User to update is logged in user.

            pUser = UserManager.updateAccount(pUser);
            return pUser;
        } catch (PersistenceException e) {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Failed to update user id " + pUser.getUsername() + " .");
        }
    }

    /**
     *
     * @param context
     * @param id persistenceId of a PersistentScoContext
     * @return
     * @throws Dwo2Exception
     */
    @Override
    public PersistentStudentModelContext getStudentModel(UserDomainAuthorizer.UserPersistentContext context, DomScoContextId id) throws Dwo2Exception {
        PersistentStudentModelContext model = StudentModelContextManager.findEntity(MySQLPersistenceId.getNativeId(id));
        return model;
    }
}
