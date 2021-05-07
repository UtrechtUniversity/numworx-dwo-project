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
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_HR_R_S_SG_U;
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
import nl.uu.fi.dwo.rest.dom.entities.ValidUserFieldsChecker;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestUser;

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
    public static List<DomUserFull> getUsersInSchool(@Context SecurityContext sc) {
        PersistentHasRole phr = null;
        List<DomUserFull> domUsers = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
//todo page the results
        List<PersistentUser> userList = UserManager.findEntities();
        domUsers = new ArrayList<DomUserFull>(userList.size());
        for (PersistentUser u : userList) {
            domUsers.add(u.buildDomUserFull());
        }

        return domUsers;
    }
    
    @PUT
    @Produces({"application/json"})
    @Path("/getList")
    public static List<DomUserFull> getUsersInSchool(@Context SecurityContext sc, RestContext rest) throws Dwo2Exception {
    	DwoAdminState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName()).setHasRoleIfType(rest.getRestContext().getDomHasRole(), RoleType.ADMIN).buildDwoAdmin();
    	String realm = rest.getRestContext().getRealm();
    	List<PersistentUser> userList = UserManager.findEntities();
        ArrayList<DomUserFull> domUsers = new ArrayList<DomUserFull>(userList.size());
        for (PersistentUser u : userList) {
            domUsers.add(u.buildDomUserFull(realm));
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
    @Path("/get")
    public DomUserFull getUser(@Context SecurityContext sc, RestUser restUser) {
        if(0==0)             throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");

        if (restUser == null || restUser.getDomUser() == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        DomUser domUser = restUser.getDomUser();
        PersistentHasRole phr = null;
        try {
        	UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName()).setHasRoleIfType(restUser.getRestContext().getDomHasRole(), RoleType.ADMIN);
            phr = state.getHasRole();
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
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
            return user.buildDomUserFull(restUser.getRestContext().getRealm());
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to change a user with username {1} by dwoadmin {0}.", new Object[]{sc.getUserPrincipal().getName(), domUser.getUserName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
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
    public DomUserFull updateUser(@Context SecurityContext sc, RestUserFull restUser) {
        if (restUser == null || restUser.getDomUserFull() == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        DomUserFull domUser = restUser.getDomUserFull();
        
        //passwords are already hashed.
        if (!ValidUserFieldsChecker.isValidEmail(domUser.getEmail())) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Email_Address_Invalid, "The email address does not  conform with RFC 5322.");
        }
        if (!ValidUserFieldsChecker.isValidUserName(domUser.getUserName())) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_UserName_Invalid, "The username address is not correctly formatted.");
        }
        if (!ValidUserFieldsChecker.isValidPassword(domUser.getPassword())) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Password_Invalid, "The password is not correctly formatted.");
        }
        
        PersistentHasRole phr = null;
        try {
        	UserState_HR_R_S_SG_U state = AnonDomainAuthorizer.build().submitUser(sc.getUserPrincipal().getName()).setHasRoleIfType(restUser.getRestContext().getDomHasRole(), RoleType.ADMIN);
            phr = state.getHasRole();
        } catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access admin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
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
            user.setUsername(domUser.getUserName());
            user.setEmail(domUser.getEmail());
            user.setGivenName(domUser.getGivenName());
            user.setInsertion(domUser.getInsertion());
            user.setLastname(domUser.getFamilyName());
            user.setPassword(domUser.getPassword());
            try {
                user = UserManager.edit(user);
            } catch (PersistenceException ex) {
                LOG.log(Level.WARNING, "User {0} could not update user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName(), domUser.getUserName()});
                LOG.log(Level.SEVERE, "", ex);
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Could not update user " + sc.getUserPrincipal().getName() + ".");
            }
            return user.buildDomUserFull(restUser.getRestContext().getRealm());
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to change a user with username {1} by dwoadmin {0}.", new Object[]{sc.getUserPrincipal().getName(), domUser.getUserName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }
}
