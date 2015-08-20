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
 * Handles the public registration of new users.
 *
 * @author G.A.J. van der Plas
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
    public Response registerNewUser(NewUserRegistration newUserReg) {
        EntityManager em = DwoEmfFactory.getEntityManager();
        PersistentUser u;
            u = UserManager.findByUserName(newUserReg.getUsername());
        if (u != null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_UserName_exists, "User with user id " + u.getUsername() + " already exists.");
        }
        //invariant: usercode does not exists

        //fetch schoolgroup id.     
        PersistentSchoolGroup sg;
        
        PersistentSchool school = null;
        //set null school values if appropiate.
        if(newUserReg.getSchoolLogin()==null && newUserReg.getSchoolCode()==null){
            newUserReg.setSchoolLogin("null"); //TODO retrieve the null school login and code from the DwoSystemParameters.
            newUserReg.setSchoolCode("null");
        }
        try {
            javax.persistence.Query q = em.createQuery(" select sg from PersistentSchoolGroup sg join PersistentSchool s where s.schoollogin = :schoollogin and sg.role.groupname = :role and sg.passwd = :schoolcode");
            q.setParameter("schoollogin", newUserReg.getSchoolLogin());
            q.setParameter("schoolcode", newUserReg.getSchoolCode());
            q.setParameter("role", (newUserReg.getRole().getGroupname()));
            sg = (PersistentSchoolGroup) q.getSingleResult();
            school = sg.getSchool(); // Sadly, another query.
            if (school == null) {
                LOG.log(Level.INFO, "Registration failde for school {0} with school login {1} and school code {2} for usercode {3}.", new Object[]{school.getSchoolName(), newUserReg.getSchoolLogin(), newUserReg.getSchoolCode(), newUserReg.getUsername()});
                String msg = String.format("Registration failde for school {0} with school login {1} and school code {2} for usercode {3}.",new Object[]{school.getSchoolName(), newUserReg.getSchoolLogin(), newUserReg.getSchoolCode(), newUserReg.getUsername()});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Invalid_school_role_credentials, msg);                
            }
            //invariant: usercode does not exists and a school exists for schoollogin and schoolcode
            LOG.log(Level.FINE, "School-manager retrieved school {0} from school login and school code for usercode {3}.", new Object[]{school.getSchoolName(), newUserReg.getSchoolLogin(), newUserReg.getSchoolCode(), newUserReg.getUsername()});
        } catch (Exception ex) {
                LOG.log(Level.WARNING, "School registration authentication failed.", ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_School_authentication_failed, "School registration authentication failed, please try again.");
        } finally {
            em.close();
        }

        if (!school.licenseIsValid()) {
                LOG.log(Level.INFO, "Registration failde for school {0}, school id {1}, the license expired on {1}.", new Object[]{school.getSchoolName(), school.getSchoolID(), school.getExpire()});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_School_license_expired, "The license expired on "+school.getExpire());                
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
        //user add success
        user = UserManager.findByUserName(user.getUsername());
        LOG.log(Level.INFO,"User {0} {1} {2} with usercode {3} and index {4} was added to the database.", new Object[]{user.getFirstname(), user.getMiddlename(), user.getLastname(), user.getUsername(), user.getUserID()});

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
        HasRoleManager.create(hasRole);
        LOG.log(Level.INFO,"HasRole for user, schoolgroup index {0} {1} and role {3} was added to the database.", new Object[]{hasRole.getPersistentHasRolePK().getUserID(), hasRole.getPersistentHasRolePK().getSchoolGroupID(), newUserReg.getRole().getGroupname()});
        //success
        return Response.status(200).entity(true).build();
    }
}
