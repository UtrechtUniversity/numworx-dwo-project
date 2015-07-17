/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.rest.entities.NewUserRegistration;
import fi.dwo.server.PersistentEntityManagers.HasRoleManager;
import fi.dwo.server.PersistentEntityManagers.UserManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * Registration manager for known users.
 *
 * @author Gert van der Plas
 */
@Path("/secure/registration")
public class SecuredRegistrationManager {

    private static final Logger LOG = Logger.getLogger(SecuredRegistrationManager.class.getName());

    /**
     * Registers an existing user into a new <school,hasRole> tuple.
     *
     * @param sc
     * @param existingUserReg
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/existingUser/json")
    public Response registerExistingUser(@Context SecurityContext sc, NewUserRegistration existingUserReg) {
        EntityManager em = DwoEmfFactory.createEntityManager();

        //Check for userid, should exist.
        PersistentUser user = UserManager.findByUserName(sc.getUserPrincipal().getName());
        if (user == null) {
            LOG.log(Level.WARNING, "Username {0}: Authentication for schoollogin {1} and role {2} for usercode {3} failed.", new Object[]{sc.getUserPrincipal().getName(), existingUserReg.getSchoolLogin(), existingUserReg.getRole().getGroupname(), existingUserReg.getUsername()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_AuthenticationError, "Authentication for " + sc.getUserPrincipal().getName() + " failed.");
        }
        //invariant: have user data

        //fetch schoolgroup id.     
        PersistentSchoolGroup sg;
        PersistentSchool school = null;
        if (existingUserReg.getSchoolLogin() == null && existingUserReg.getSchoolCode() == null) {
            existingUserReg.setSchoolLogin("null"); //TODO retrieve the null school login and code from the DwoSystemParameters.
            existingUserReg.setSchoolCode("null");
        }

        try {
            javax.persistence.Query q = em.createQuery(" select sg from PersistentSchoolGroup sg join PersistentSchool s where s.schoollogin = :schoollogin and sg.role.groupname = :role and sg.passwd = :schoolcode");
            q.setParameter("schoollogin", existingUserReg.getSchoolLogin());
            q.setParameter("schoolcode", existingUserReg.getSchoolCode());
            q.setParameter("role", (existingUserReg.getRole().getGroupname()));
            sg = (PersistentSchoolGroup) q.getSingleResult();
            school = sg.getSchool(); // Sadly, another query.
            if (school == null) {
                String msg = String.format("Username {0}: Registration authentication failed for school {1} with school login {2} and school code {3} for usercode {4}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolName(), existingUserReg.getSchoolLogin(), existingUserReg.getSchoolCode(), existingUserReg.getUsername()});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Invalid_school_role_credentials, msg);
            }
            //invariant: usercode does not exists and a school exists for schoollogin and schoolcode
            LOG.log(Level.FINER, "Username {0}: School-manager retrieved school {1} from school login and school code for usercode {2}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolName(), existingUserReg.getUsername()});
        }
        catch (Exception ex) {
            LOG.log(Level.WARNING, "Registration authentication failed due to a possible software error.", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Registration authentication failed due to a software error, please try again.");
        }
        finally {
            em.close();
        }

        //invariant: have school data and user data
        if (!school.licenseIsValid()) {
            LOG.log(Level.INFO, "Username {0}: Registration failde for school {1}, school id {2}, the license expired on {3}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolName(), school.getSchoolID(), school.getExpire()});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_School_license_expired, "The license expired on " + school.getExpire());
        }

        Date now = new Date();

        //invariant: have school data and user data and school has a valid licence.
        //check for hasRole
        PersistentHasRolePK pk = new PersistentHasRolePK();
        pk.setSchoolGroupID(sg.getSchoolGroupID());
        pk.setUserID(user.getUserID());
        PersistentHasRole hasRole = HasRoleManager.findPersistentHasRole(pk);
        if (hasRole != null) {
            LOG.log(Level.FINE, "Username {0}: Registration failde for school {1}, schoolgroup id {2}, userid {3} already exists.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolName(), hasRole.getPersistentHasRolePK().getSchoolGroupID(), hasRole.getPersistentHasRolePK().getUserID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_hasRole_exists, "The user has already been registered.");
        }

        //invariant: usercode does exist and school exists for schoollogin and schoolcode and has a valid licence and the hasRole does not yet exist.
        //building hasRole
        //buiding compound key hasRole
        hasRole = new PersistentHasRole();
        hasRole.setPersistentHasRolePK(pk);

        hasRole.setClassID(null);
        hasRole.setLastLogin(now); //considering an account creation a first login as there is a password
        hasRole.setRegisterDate(now);
        hasRole.setRights("_");  //TODO make a rightsManager

        HasRoleManager.create(hasRole);
        LOG.log(Level.INFO, "Username {0}: Created a new HasRole for user index {1}, schoolgroup index {2} and role {3} was added to the database.", new Object[]{sc.getUserPrincipal().getName(), hasRole.getPersistentHasRolePK().getUserID(), hasRole.getPersistentHasRolePK().getSchoolGroupID(), hasRole.getSchoolGroup().getRole()});
        //success
        return Response.status(200).entity(true).build();
    }
}
