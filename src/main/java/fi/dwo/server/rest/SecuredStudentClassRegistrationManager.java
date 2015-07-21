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
import fi.dwo.server.persistence.DwoEmfFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * Operations for the GUI Component that manages the class registration for students section.
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@Path("/secure/student/classregistration")
public class SecuredStudentClassRegistrationManager {

    private static final Logger LOG = Logger.getLogger(SecuredStudentClassRegistrationManager.class.getName());
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
//
//    /**
//     * Returns the currentUser. The information is extracted from the security
//     * context.
//     *
//     * @param sc
//     * @return Returns null if there was an error.
//     */
//    @GET
//    @Produces({"application/json"})
//    @Path("/get/json")
//    public List<SchoolClass> getClassesAtSchool(@Context SecurityContext sc) {
//        EntityManager em = DwoEmfFactory.getEntityManager();
//
//        PersistentUser user;
//        List<SchoolClass> scList = (List<SchoolClass>) new ArrayList<SchoolClass>();
//        SchoolClass s;
//
//        // fetch the authenticated user
//        try {
//            javax.persistence.Query q = em.createNamedQuery("PersistentUser.findByUsername");
//            String userName = sc.getUserPrincipal().getName();
//            q.setParameter("username", userName);
//            user = (PersistentUser) q.getSingleResult();
//            String name = user.getUsername();
//            long userId = user.getUserID();
//            LOG.log(Level.FINE, "Username {0}: Fetched User with username {1} and id {2}", new Object[]{sc.getUserPrincipal().getName(),name, userId});
//            
//            //Retrieve the list of existing classes in the school. 
//            q = em.createQuery("select h.schoolGroup.schoolID, h.schoolGroup.school.schoolName, "
//                    + "h.schoolGroup.groupID, h.schoolGroup.role.groupname, h.classID, h.persistentHasRolePK.userID , h.schoolGroup.schoolGroupID  "
//                    + "from PersistentHasRole h where h.persistentHasRolePK.userID = :userID "
//                    + " ");
//            //Sample query
//            //select h.schoolGroup.schoolID, h.schoolGroup.school.schoolName, h.schoolGroup.groupID, h.schoolGroup.role.groupname, h.classID from PersistentHasRole h where h.persistentHasRolePK.userID = 184690
//            q.setParameter("userID", userId);
//            List<Object[]> resultList = q.getResultList();
//            LOG.log(Level.FINE, "Username {0}: Fetched {2} HasRoles for userId {1}.", new Object[]{sc.getUserPrincipal().getName(),userId, resultList.size()});
//            SchoolRoleAndClass sac;
//            for (Object[] oList : resultList) {
//                LOG.log(Level.FINE, "Fetched hasRole tuple <schoolID, schoolName, groupID, groupname, classID, userID, schoolGroupID>: {0}, {1}, {2}, {3}, {4}, {5}, {6}.", new Object[]{oList[0], oList[1], oList[2], oList[3], oList[4], oList[5],oList[6]});
//                sac = new SchoolRoleAndClass();
//                sac.setSchoolId((PersistenceId) MySQLPersistenceId.createPersistenceId((Integer) oList[0], PersistenceClassType.PersistentSchool));
//                sac.setSchoolName((String) oList[1]);
//                sac.setRoleId((PersistenceId) MySQLPersistenceId.createPersistenceId((Integer) oList[2], PersistenceClassType.PersistentRole));
//                sac.setRoleName((String) oList[3]);
//                if (oList[4] != null) {
//                    sac.setSchoolClassId((PersistenceId) MySQLPersistenceId.createPersistenceId((Integer) oList[0], PersistenceClassType.PersistentSchoolClass));
//                    sac.setSchoolClassName((String) em.createQuery("select c.class1 from PersistentSchoolClass c where c.classID = :id ").setParameter("id", (Integer) oList[4]).getSingleResult());
//                } else {
//                    sac.setSchoolClassId(null);
//                    sac.setSchoolClassName(null);
//                }
//                sac.setUserId((PersistenceId) MySQLPersistenceId.createPersistenceId((Integer) oList[5], PersistenceClassType.PersistentUser));
//                sac.setSchoolGroupId((PersistenceId) MySQLPersistenceId.createPersistenceId((Integer) oList[6], PersistenceClassType.PersistentSchoolGroup));
//                sacList.add(sac);
//            }
//
//        } catch (Exception e) {
//            LOG.log(Level.WARNING, "Username "+sc.getUserPrincipal().getName()+" Unexpected exception.", e);
//            return new SchoolsRolesAndClasses();
//        } finally {
//            LOG.log(Level.FINER, " closed em.");
//            em.close();
//        }
//
//        curSac = this.getCurrentSchoolRoleAndClass(sc.getUserPrincipal().getName(),user.getUserID());
//        sacs.setActiveSchoolRoleAndClass(curSac);
//        sacs.setSchoolsRolesAndClassesList(sacList);
//        return sacs;
//
//    }        // Create all the tuples.

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
    @Path("/update/json")
    public SchoolRoleAndClass setActiveSchoolRoleAndClass(@Context SecurityContext sc, SchoolRoleAndClass sarc) {
        EntityManager em = DwoEmfFactory.getEntityManager();

        PersistentUser user;
        SchoolRoleAndClass curSac;

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

    @GET
    @Produces({"application/json"})
    @Path("/classinfo/json")
    public String info() {
        return this.getClass().getName();
    }
}
