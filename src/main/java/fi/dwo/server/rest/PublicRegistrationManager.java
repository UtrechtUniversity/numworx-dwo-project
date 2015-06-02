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
import javax.ws.rs.core.SecurityContext;

/**
 * Handles the public registration of a new user.
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
@Path("/public/registration")
public class PublicRegistrationManager {

    private static final Logger LOG = Logger.getLogger(PublicRegistrationManager.class.getName());

    /**
     * Registers a new user.
     *
     * @param sc
     * @param newUserReg
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/newUser/json")
    public boolean registerNewUser(@Context SecurityContext sc, NewUserRegistration newUserReg) {
        EntityManager em = DwoEmfFactory.createEntityManager();
        PersistentUser u;
            u = UserManager.findByUserName(newUserReg.getUsername());
        if (u != null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_UserName_exists, "User with user id " + u.getUsername() + " already exists.");
        }
        //invariant: usercode does not exists

        //fetch schoolgroup id.     
        PersistentSchoolGroup sg;
        PersistentSchool school = null;
        try {
            javax.persistence.Query q = em.createQuery(" select sg from PersistentSchoolGroup sg join PersistentSchool s where s.schoollogin = :schoollogin and sg.role.groupname = :role and sg.passwd = :schoolcode");
            q.setParameter("schoollogin", newUserReg.getSchoolLogin());
            q.setParameter("schoolcode", newUserReg.getSchoolCode());
            q.setParameter("role", (newUserReg.getRole()));
            Error is here
            ! Type mismatch
            .
            sg = (PersistentSchoolGroup) q.getSingleResult();
            school = sg.getSchool(); // Sadly, another query.
            if (school == null) {
                LOG.log(Level.INFO, "Registration failde for school {0} with school login {1} and school code {2} for usercode {3}.", new Object[]{school.getSchoolName(), newUserReg.getSchoolLogin(), newUserReg.getSchoolCode(), newUserReg.getUsername()});
                return false;
            }
            //invariant: usercode does not exists and a school exists for schoollogin and schoolcode
            LOG.log(Level.FINE, "School-manager retrieved school {0} from school login and school code for usercode {3}.", new Object[]{school.getSchoolName(), newUserReg.getSchoolLogin(), newUserReg.getSchoolCode(), newUserReg.getUsername()});
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Unexpected software error.", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Unknown error.");
        } finally {
            em.close();
        }

        if (!school.licenseIsValid()) {
            return false;
        }

        Date now = new Date();

        //invariant: usercode does not exists and school exists for schoollogin and schoolcode and has a valid licence.
        //adding user to school in role.         
        PersistentUser user = new PersistentUser();
        user.setEmail(newUserReg.getEmail());
        user.setFirstname(newUserReg.getGivenName());
        user.setMiddlename(newUserReg.getInsertion());
        user.setLastname(newUserReg.getFamilyName());
        user.setPasswd(newUserReg.getPassword());
        user.setRegisterDate(now);
        user.setUsername(newUserReg.getUsername());
        user.setSchoolGroupID(sg.getSchoolGroupID());
        //add user
        UserManager.create(user);
        user = UserManager.findByUserName(user.getUsername());

        // building hasRole
        PersistentHasRole hasRole = new PersistentHasRole();
        // buiding compound key hasRole
        PersistentHasRolePK pk = new PersistentHasRolePK();
        pk.setSchoolGroupID(sg.getSchoolGroupID());
        pk.setUserID(user.getUserID());
        hasRole.setPersistentHasRolePK(pk);

        hasRole.setClassID(null);
        hasRole.setLastLogin(now); //considering an account creation a first login as there is a password
        hasRole.setRegisterDate(now);
        hasRole.setRights("_"); //TODO make a rights manager
        //success
        return true;
    }
}
