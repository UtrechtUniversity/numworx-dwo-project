/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

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
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author Gert van der Plas
 */
public class SecuredRegistrationManager {

    private static final Logger LOG = Logger.getLogger(SecuredRegistrationManager.class.getName());

    /**
     * Registers a new user.
     *
     * @param sc
     * @param existingUserReg
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/registerExistingUser/json")
    public boolean registerExistingUser(@Context SecurityContext sc, NewUserRegistration existingUserReg) {
        EntityManager em = DwoEmfFactory.createEntityManager();

        //Check for userid, should not exist.
        PersistentUser user = UserManager.findByUserName(existingUserReg.getUsername());
        if (user == null) {
            return false;
        }
        //invariant: usercode does exist

        //fetch schoolgroup id.     
        PersistentSchoolGroup sg;
        PersistentSchool school = null;
        try {
            javax.persistence.Query q = em.createQuery(" select sg from PersistentSchoolGroup sg join PersistentSchool s where s.schoollogin = :schoollogin and sg.role.groupname = :role and sg.passwd = :schoolcode");
            q.setParameter("schoollogin", existingUserReg.getSchoolLogin());
            q.setParameter("schoolcode", existingUserReg.getSchoolCode());
            q.setParameter("role", (existingUserReg.getRole()));
            sg = (PersistentSchoolGroup) q.getSingleResult();
            school = sg.getSchool(); // Sadly, another query.
            if (school == null) {
                LOG.log(Level.INFO, "Registration failde for school {0} with school login {1} and school code {2} for usercode {3}.", new Object[]{school.getSchoolName(), existingUserReg.getSchoolLogin(), existingUserReg.getSchoolCode(), existingUserReg.getUsername()});
                return false;
            }
            //invariant: usercode does not exists and a school exists for schoollogin and schoolcode
            LOG.log(Level.FINE, "School-manager retrieved school {0} from school login and school code for usercode {3}.", new Object[]{school.getSchoolName(), existingUserReg.getSchoolLogin(), existingUserReg.getSchoolCode(), existingUserReg.getUsername()});
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Unexpected software error.", ex);
            return false;
        } finally {
            em.close();
        }

        if (!school.licenseIsValid()) {
            return false;
        }

        Date now = new Date();

        //invariant: usercode does exist and school exists for schoollogin and schoolcode and has a valid licence.
        // check for hasRole
        PersistentHasRolePK pk = new PersistentHasRolePK();
        pk.setSchoolGroupID(sg.getSchoolGroupID());
        pk.setUserID(user.getUserID());
        PersistentHasRole hasRole = HasRoleManager.findPersistentHasRole(pk);
        if(hasRole==null){
            //user exists
            return false;
        }
        
        //invariant: usercode does exist and school exists for schoollogin and schoolcode and has a valid licence and the hasRole does not yet exist.
        // building hasRole
        // buiding compound key hasRole
        hasRole = new PersistentHasRole();
        hasRole.setPersistentHasRolePK(pk);

        hasRole.setClassID(null);
        hasRole.setLastLogin(now); //considering an account creation a first login as there is a password
        hasRole.setRegisterDate(now);
        hasRole.setRights("_");  //TODO make a rightsManager

        //success
        return true;
    }
}
