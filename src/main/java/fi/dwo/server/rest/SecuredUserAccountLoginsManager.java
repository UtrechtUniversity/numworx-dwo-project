/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.*;
import fi.dwo.commons.persistence.entities.*;
import fi.dwo.commons.rest.entities.*;
import fi.dwo.server.PersistentEntityManagers.HasRoleManager;
import fi.dwo.server.PersistentEntityManagers.UserManager;
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * Operations for the GUI Component that manages the role state data of a user.
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@Path("/secure/user/account/logins")
public class SecuredUserAccountLoginsManager {

    private static final Logger LOG = Logger.getLogger(SecuredUserAccountLoginsManager.class.getName());
//    @Context  //injected response proxy supporting multiple threads
//    private HttpServletResponse response;

    private SchoolRoleAndClass getCurrentSchoolRoleAndClass(String scUsername,long userId) {
        EntityManager em = DwoEmfFactory.getEntityManager();

        SchoolRoleAndClass curSac = new SchoolRoleAndClass();
//        // retrieve the current active <school,role, class>
        try {
            javax.persistence.Query q = em.createQuery("select h.schoolGroup.schoolID, h.schoolGroup.school.schoolName, h.schoolGroup.groupID, h.schoolGroup.role.groupname, h.classID, h.persistentHasRolePK.userID , h.schoolGroup.schoolGroupID  from PersistentHasRole h where h.persistentHasRolePK.userID = :userID and h.user.schoolGroupID = h.persistentHasRolePK.schoolGroupID");
            //Sample query
            //select h.schoolGroup.schoolID, h.schoolGroup.school.schoolName, h.schoolGroup.groupID, h.schoolGroup.role.groupname, h.classID from PersistentHasRole h where h.persistentHasRolePK.userID = 184690
            q.setParameter("userID", userId);
            List<Object[]> resultList = q.getResultList();
            LOG.log(Level.FINER, "Username {0}: resultList size: {0}.", new Object[]{scUsername,resultList.size()});
            if (resultList.size() == 1) {
                LOG.log(Level.FINE, "Username {0}: Fetched current role tuple <schoolID, schoolName, groupID, groupname, classID, userID, groupID>: {1}, {2}, {3}, {4}, {5}, {6}, {7}.", new Object[]{scUsername,resultList.get(0)[0], resultList.get(0)[1], resultList.get(0)[2], resultList.get(0)[3], resultList.get(0)[4], resultList.get(0)[5], resultList.get(0)[6]});
                curSac.setSchoolId((PersistenceId) MySQLPersistenceId.createPersistenceId((Integer) resultList.get(0)[0], PersistenceClassType.PersistentSchool));
                curSac.setSchoolName((String) resultList.get(0)[1]);
                curSac.setRoleId((PersistenceId) MySQLPersistenceId.createPersistenceId((Integer) resultList.get(0)[2], PersistenceClassType.PersistentRole));
                curSac.setRoleName((String) resultList.get(0)[3]);
                if (resultList.get(0)[4] != null) {
                    curSac.setSchoolClassId((PersistenceId) MySQLPersistenceId.createPersistenceId((Integer) resultList.get(0)[0], PersistenceClassType.PersistentSchoolClass));
                    curSac.setSchoolClassName((String) em.createQuery("select c.class1 from PersistentSchoolClass c where c.classID = :id ").setParameter("id", (Integer) resultList.get(0)[4]).getSingleResult());
                } else {
                    curSac.setSchoolClassId(null);
                    curSac.setSchoolClassName(null);
                }
                curSac.setUserId((PersistenceId) MySQLPersistenceId.createPersistenceId((Integer) resultList.get(0)[5], PersistenceClassType.PersistentUser));
                curSac.setSchoolGroupId((PersistenceId) MySQLPersistenceId.createPersistenceId((Integer) resultList.get(0)[6], PersistenceClassType.PersistentSchoolGroup));
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Username "+scUsername+": Unexpected exception.", e);
            return new SchoolRoleAndClass();
        } finally {
            em.close();
        }
        return curSac;
    }

    /**
     * Returns the current school role. The information is extracted from the security
     * context.
     *
     * @param sc
     * @return Returns null if there was an error.
     */
    @GET
    @Produces({"application/json"})
    @Path("/get")
    public SchoolsRolesAndClasses getSchoolLogins(@Context SecurityContext sc) {
        EntityManager em = DwoEmfFactory.getEntityManager();

        SchoolsRolesAndClasses sacs = new SchoolsRolesAndClasses();
        PersistentUser user;
        List<SchoolRoleAndClass> sacList = (List<SchoolRoleAndClass>) new ArrayList<SchoolRoleAndClass>();
        SchoolRoleAndClass curSac;

        // fetch the authenticated user
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentUser.findByUsername");
            String userName = sc.getUserPrincipal().getName();
            q.setParameter("username", userName);
            user = (PersistentUser) q.getSingleResult();
            String name = user.getUsername();
            long userId = user.getUserID();
            LOG.log(Level.FINE, "Username {0}: Fetched User with username {1} and id {2}", new Object[]{sc.getUserPrincipal().getName(),name, userId});
            //Retrieve the list of possible <School, role, class>, class can be null.
            q = em.createQuery("select h.schoolGroup.schoolID, h.schoolGroup.school.schoolName, "
                    + "h.schoolGroup.groupID, h.schoolGroup.role.groupname, h.classID, h.persistentHasRolePK.userID , h.schoolGroup.schoolGroupID  "
                    + "from PersistentHasRole h where h.persistentHasRolePK.userID = :userID "
                    + " ");
            //Sample query
            //select h.schoolGroup.schoolID, h.schoolGroup.school.schoolName, h.schoolGroup.groupID, h.schoolGroup.role.groupname, h.classID from PersistentHasRole h where h.persistentHasRolePK.userID = 184690
            q.setParameter("userID", userId);
            List<Object[]> resultList = q.getResultList();
            LOG.log(Level.FINE, "Username {0}: Fetched {2} HasRoles for userId {1}.", new Object[]{sc.getUserPrincipal().getName(),userId, resultList.size()});
            SchoolRoleAndClass sac;
            for (Object[] oList : resultList) {
                LOG.log(Level.FINE, "Fetched hasRole tuple <schoolID, schoolName, groupID, groupname, classID, userID, schoolGroupID>: {0}, {1}, {2}, {3}, {4}, {5}, {6}.", new Object[]{oList[0], oList[1], oList[2], oList[3], oList[4], oList[5],oList[6]});
                sac = new SchoolRoleAndClass();
                sac.setSchoolId((PersistenceId) MySQLPersistenceId.createPersistenceId((Integer) oList[0], PersistenceClassType.PersistentSchool));
                sac.setSchoolName((String) oList[1]);
                sac.setRoleId((PersistenceId) MySQLPersistenceId.createPersistenceId((Integer) oList[2], PersistenceClassType.PersistentRole));
                sac.setRoleName((String) oList[3]);
                if (oList[4] != null) {
                    sac.setSchoolClassId((PersistenceId) MySQLPersistenceId.createPersistenceId((Integer) oList[0], PersistenceClassType.PersistentSchoolClass));
                    sac.setSchoolClassName((String) em.createQuery("select c.class1 from PersistentSchoolClass c where c.classID = :id ").setParameter("id", (Integer) oList[4]).getSingleResult());
                } else {
                    sac.setSchoolClassId(null);
                    sac.setSchoolClassName(null);
                }
                sac.setUserId((PersistenceId) MySQLPersistenceId.createPersistenceId((Integer) oList[5], PersistenceClassType.PersistentUser));
                sac.setSchoolGroupId((PersistenceId) MySQLPersistenceId.createPersistenceId((Integer) oList[6], PersistenceClassType.PersistentSchoolGroup));
                sacList.add(sac);
            }

        } catch (Exception e) {
            LOG.log(Level.WARNING, "Username "+sc.getUserPrincipal().getName()+" Unexpected exception.", e);
            return new SchoolsRolesAndClasses();
        } finally {
            LOG.log(Level.FINER, " closed em.");
            em.close();
        }

        curSac = this.getCurrentSchoolRoleAndClass(sc.getUserPrincipal().getName(),user.getUserID());
        sacs.setActiveSchoolRoleAndClass(curSac);
        sacs.setSchoolsRolesAndClassesList(sacList);
        return sacs;

    }        // Create all the tuples.

    /**
     * Updates the User data of the current user and returns a copy of the
     * updated data.
     *
     * @param sc
     * @param sarc
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/switch")
    public SchoolRoleAndClass switchToSchoolLogin(@Context SecurityContext sc, SchoolRoleAndClass sarc) {
        EntityManager em = DwoEmfFactory.getEntityManager();

        PersistentUser user;
        SchoolRoleAndClass curSac = new SchoolRoleAndClass();

        // fetch the authenticated user
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentUser.findByUsername");
            String userName = sc.getUserPrincipal().getName();
            q.setParameter("username", userName);
            user = (PersistentUser) q.getSingleResult();
            String name = user.getUsername();
            long userId = user.getUserID();
            LOG.log(Level.INFO, "Username {0}: Fetched User with username {0}", new Object[]{sc.getUserPrincipal().getName(),name});
            LOG.log(Level.INFO, "And id is {0}", new Object[]{userId});

            if (userId != MySQLPersistenceId.getId(sarc.getUserId())) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to update the user profile of user id {1}.", new Object[]{sc.getUserPrincipal().getName(), MySQLPersistenceId.getId(sarc.getUserId())});
               throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to update usercode " + user.getUsername() + ".");
            }
            //User to update is logged as user.
            em.getTransaction().begin();
            PersistentUser u = em.find(PersistentUser.class, user.getUserID());
            u.setSchoolGroupID(
                    (int) (long) MySQLPersistenceId.getId(sarc.getSchoolGroupId()));
            em.getTransaction()
                    .commit();
            LOG.log(Level.INFO,
                    "Username {0}: Updated SchoolGroupID to {1} for User with username {2}", new Object[]{sc.getUserPrincipal().getName(),u.getSchoolGroupID(), user.getUsername()
                    }
            );
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Username "+sc.getUserPrincipal().getName()+": Unexpected exception.", e);
            return new SchoolRoleAndClass();
        } finally {
            em.close();
        }
        curSac = getCurrentSchoolRoleAndClass(sc.getUserPrincipal().getName(),user.getUserID());
        return curSac;
    }        // Create all the tuples.

    
    /**
     * Registers an existing user into a new <school,hasRole> tuple.
     *
     * @param sc
     * @param existingUserReg
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submit")
    public Response addASchoolLogin(@Context SecurityContext sc, KnownUserRegistration existingUserReg) {
        EntityManager em = DwoEmfFactory.getEntityManager();

        //Check for userid, should exist.
        PersistentUser user = UserManager.findByUserName(sc.getUserPrincipal().getName());
        if (user == null) {
            LOG.log(Level.WARNING, "Username {0}: Authentication for schoollogin {1} and role {2} for usercode {3} failed.", new Object[]{sc.getUserPrincipal().getName(), existingUserReg.getSchoolLogin(), existingUserReg.getRole().getGroupname(), sc.getUserPrincipal().getName()});
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
                String msg = String.format("Username {0}: Registration authentication failed for school {1} with school login {2} and school code {3} for usercode {4}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolName(), existingUserReg.getSchoolLogin(), existingUserReg.getSchoolCode(), user.getUsername()});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Invalid_school_role_credentials, msg);
            }
            //invariant: usercode does not exists and a school exists for schoollogin and schoolcode
            LOG.log(Level.FINER, "Username {0}: School-manager retrieved school {1} from school login and school code for usercode {2}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolName(), user.getUsername()});
        }
        catch (Exception ex) {
                String msg = String.format("Username {0}: Registration authentication failed for school login {1} and school code {2} for usercode {4}.", new Object[]{sc.getUserPrincipal().getName(), existingUserReg.getSchoolLogin(), existingUserReg.getSchoolCode(), user.getUsername()});
                LOG.log(Level.WARNING, msg, ex);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Invalid_school_role_credentials, msg);
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
        PersistentHasRole hasRole = HasRoleManager.findEntity(pk);
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
