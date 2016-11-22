package fi.dwo.server.rest;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.entities.RestUserFull;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.persistence.PersistenceException;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * Operations for the GUI Component that manages the User Profile.
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@PermitAll
@Path("/secure/dwoadmin/user")
public class SecuredDwoAdminUserManager {

    private static final Logger LOG = Logger.getLogger(SecuredDwoAdminUserManager.class.getName());

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getList")
    public static List<DomUser> getUsersInSchool(@Context SecurityContext sc) {
        PersistentHasRole phr = null;
        List<DomUser> domUsers = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
//todo page the results
        List<PersistentUser> userList = UserManager.findEntities();
        domUsers = new ArrayList<DomUser>(userList.size());
        for (PersistentUser u : userList) {
            domUsers.add(u.buildDomUserFull());
        }

        return domUsers;
    }

    /**
     * Edits a singleSchoolStudent.
     *
     * @param sc
     * @param user
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/update")
    public Boolean updateUser(@Context SecurityContext sc, RestUserFull restUser) {
        if (restUser == null || restUser.getDomUserFull() == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        DomUserFull domUser = restUser.getDomUserFull();
        PersistentHasRole phr = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access schooladmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        if (phr != null) {
            PersistentUser user;
            try {
                user = UserManager.findEntity(MySQLPersistenceId.getNativeId(domUser));
            } catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
            }
//            if (user == null) {
//                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: could not find user with id to update {1}.", new Object[]{sc.getUserPrincipal().getName(), nssStudent.getDomSingleSchoolStudent().getId()});
//                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Could not update user with username " + nssStudent.getDomSingleSchoolStudent().getUserName() + ".");
//            }
//            if (!user.isSingleSchoolAccount()) {
//                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to change a non-single school user with username {1} by schooladmin {0}.", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
//                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
//            }
            user.setUsername(domUser.getUserName());
            user.setEmail(domUser.getEmail());
            user.setGivenName(domUser.getGivenName());
            user.setInsertion(domUser.getInsertion());
            user.setLastname(domUser.getFamilyName());
            user.setPassword(domUser.getPassword());
            try {
                UserManager.edit(user);
            } catch (PersistenceException ex) {
                LOG.log(Level.WARNING, "User {0} could not update user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName(), domUser.getUserName()});
                LOG.log(Level.SEVERE, "", ex);
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Could not update user " + sc.getUserPrincipal().getName() + ".");
            }
            return true;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to change a user with username {1} by dwoadmin {0}.", new Object[]{sc.getUserPrincipal().getName(), domUser.getUserName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }
}
