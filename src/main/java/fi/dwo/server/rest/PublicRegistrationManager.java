/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.rest.entities.NewUserRegistration;
import fi.dwo.server.PersistentEntityManagers.SchoolManager;
import fi.dwo.server.PersistentEntityManagers.UserManager;
import fi.dwo.server.persistence.DwoEmfFactory;
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
    @Path("/registerNewUser/json")
    public boolean registerNewUser(@Context SecurityContext sc, NewUserRegistration newUserReg) {
        EntityManager em = DwoEmfFactory.createEntityManager();

        //Check for userid, should not exist.
        PersistentUser u = UserManager.findByUserName(newUserReg.getUsername());
        if(u!=null){
            return false;
        }
        
        //Check for valid schoolcode
//            private final static String QRY_CHECK_SCHOOLGROUP = "SELECT schoolGroupID, expire, tblSchoolGroup.schoolID "
//            + "FROM tblSchoolGroup, tblSchool "
//            + "WHERE (tblSchoolGroup.schoolID = tblSchool.schoolID) "
//            + "AND   (schoollogin = ?) " + "AND   (groupID = ?) "
//            + "AND   (passwd = ?) ";
//         
// protected boolean checkValidLicence(Date date, int int1) {
//        if (date == null) {
//            return false;
//        }
//        return date.getTime() < System.currentTimeMillis();
//    }        

        PersistentSchool school = SchoolManager.findBySchoolCode(newUserReg.getUsername());
        if(u!=null){
            return false;
        }
        
        //Set new user
        PersistentUser user =new PersistentUser();
        user.setFirstname(newUserReg.getGivenName());
        user.setMiddlename(newUserReg.getInsertion());
        user.setLastname(newUserReg.getFamilyName());
        user.setEmail(newUserReg.getEmail());
        user.setPasswd(newUserReg.getPassword());
        user.setUsername(newUserReg.getUsername());

        //Set new school
        
        //Add all to PersistentStore.
        try {
            //User to update is logged as user.
            em.getTransaction().begin();
            // add user
            //dfgsd
            em.getTransaction()
                    .commit();
            LOG.log(Level.INFO,"Added new user {0} in school {1} to persistent store.", new Object[]{user.getUserID(), school.getSchoolID()});
                    
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e);
            return false;
        } finally {
            em.close();
        }
        return true;
    }        // Create all the tuples.  
}
