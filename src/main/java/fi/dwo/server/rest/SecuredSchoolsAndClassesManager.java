/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

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
import javax.ws.rs.NotAuthorizedException;
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
@Path("/secure/user/schoolsrolesandclasses")
public class SecuredSchoolsAndClassesManager {

    private static final Logger log = Logger.getLogger(SecuredSchoolsAndClassesManager.class.getName());
//    @Context  //injected response proxy supporting multiple threads
//    private HttpServletResponse response;

    /**
     * Returns the currentUser. The information is extracted from the security
     * context.
     *
     * @param sc
     * @return Returns null if there was an error.
     */
    @GET
    @Produces({"application/json"})
    @Path("/get/json")
    public SchoolsRolesAndClasses getSchoolsAndClasses(@Context SecurityContext sc) {
        EntityManager em = DwoEmfFactory.createEntityManager();
        
        SchoolsRolesAndClasses sacs = new SchoolsRolesAndClasses();
        PersistentUser user;
        List<SchoolRoleAndClass> sacList = (List<SchoolRoleAndClass>) new ArrayList<SchoolRoleAndClass>();
        SchoolRoleAndClass curSac = new SchoolRoleAndClass();

        // fetch the authenticated user
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentUser.findByUsername");
            String userName = sc.getUserPrincipal().getName();
            q.setParameter("username", userName);
            user = (PersistentUser) q.getSingleResult();
            String name = user.getUsername();
            long userId = user.getUserID();
            log.log(Level.INFO, "Fetched User with username {0}", new Object[]{name});
            log.log(Level.INFO, "And id is {0}", new Object[]{userId});
            try {
                //Retrieve the list of possible <School, role, class>, class can be null.
                q = em.createQuery("select h.schoolGroup.schoolID, h.schoolGroup.school.schoolName, "
                        + "h.schoolGroup.groupID, h.schoolGroup.role.groupname, h.classID "
                        + "from PersistentHasRole h where h.persistentHasRolePK.userID = :userID "
                        + " ");
                //Sample query
                //select h.schoolGroup.schoolID, h.schoolGroup.school.schoolName, h.schoolGroup.groupID, h.schoolGroup.role.groupname, h.classID from PersistentHasRole h where h.persistentHasRolePK.userID = 184690
                q.setParameter("userID", userId);
                List<Object[]> resultList = q.getResultList();
                log.log(Level.INFO, "Fetched {1} HasRoles for userId {0}.", new Object[]{userId, resultList.size()});
                SchoolRoleAndClass sac;
                for (Object[] oList : resultList) {
                    log.log(Level.INFO, "Fetched hasRole tuple <schoolID, schoolName, groupID, groupname, classID>: {0}, {1}, {2}, {3}, {4}.", new Object[]{oList[0], oList[1], oList[2], oList[3], oList[4]});
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
                    sacList.add(sac);
                }

                // retrieve the current active <school,role, class>
                q = em.createQuery("select h.schoolGroup.schoolID, h.schoolGroup.school.schoolName, h.schoolGroup.groupID, h.schoolGroup.role.groupname, h.classID from PersistentHasRole h where h.persistentHasRolePK.userID = :userID and h.user.schoolGroupID = h.persistentHasRolePK.schoolGroupID");
                //Sample query
                //select h.schoolGroup.schoolID, h.schoolGroup.school.schoolName, h.schoolGroup.groupID, h.schoolGroup.role.groupname, h.classID from PersistentHasRole h where h.persistentHasRolePK.userID = 184690
                q.setParameter("userID", userId);
                resultList = q.getResultList();
                log.log(Level.INFO, "resultList size: {0}.", new Object[]{resultList.size()});
                if(resultList.size()==1){
                    log.log(Level.INFO, "Fetched current role tuple <schoolID, schoolName, groupID, groupname, classID>: {0}, {1}, {2}, {3}, {4}.", new Object[]{resultList.get(0)[0], resultList.get(0)[1], resultList.get(0)[2], resultList.get(0)[3], resultList.get(0)[4]});
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
                }

            } catch (Exception e) {
                log.log(Level.WARNING, "Unexpected exception: {0}", new Object[]{e.getMessage()});
                return new SchoolsRolesAndClasses();
            }
        } finally {
            log.log(Level.INFO, " closed em.");
            em.close();
        }
        sacs.setCurrentSchoolRoleAndClass(curSac);
        sacs.setSchoolsRolesAndClassesList(sacList);
        //TODO run query for this.
        log.log(Level.INFO, " returning.");
        return sacs;
        
    }        // Create all the tuples.

    /**
     * Updates the User data of the current user and returns a copy of the
     * updated data.
     *
     * @param sc
     * @param src
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/update/json")
    public SchoolRoleAndClass setActiveSchoolRoleClass(@Context SecurityContext sc,
            SchoolRoleAndClass src) {

        EntityManager em = DwoEmfFactory.createEntityManager();
        PersistentUser user = null;
        // fetch the authenticated user
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentUser.findByUsername");
            String userName = sc.getUserPrincipal().getName();
            q.setParameter("username", userName);
            user = (PersistentUser) q.getSingleResult();
            String name = user.getUsername();
            long userId = user.getUserID();
            log.log(Level.INFO, "Fetched User with username {0}", new Object[]{name});
            log.log(Level.INFO, "And id is {0}", new Object[]{userId});

            //Find if a School, role and class exists  and set this as default.
            q = em.createQuery("select h from PersistentHasRole h where h.persistentHasRolePK.userID = :userID and h.schoolGroup.groupID = :groupID"
                    + " ");
            q.setParameter("userID", userId);
            q.setParameter("groupID", MySQLPersistenceId.getId(src.getRoleId()));
            List<PersistentHasRole> resultList = q.getResultList();
            log.log(Level.INFO, "Fetched {1} HasRoles for userId {0}.", new Object[]{userId, resultList.size()});

            if (resultList.size() == 1) {
                try {
                    em.getTransaction().begin();
                    PersistentHasRole h = (PersistentHasRole) resultList.get(0);
                    em.find(PersistentHasRole.class, h);
                    h.setClassID((int) (long) MySQLPersistenceId.getId(src.getSchoolClassId()));
                    em.find(PersistentUser.class, user);
                    user.setSchoolGroupID(h.getSchoolGroup().getSchoolGroupID());

                    em.getTransaction().commit();
                    log.log(Level.FINE, "Updated User with username {0}", new Object[]{user.getUsername()});
                } finally {
                    em.close();
                }
                return src;
            } else {
                log.log(Level.WARNING, "ILLEGAL USER-OPERATION: Trying to change the active role of username {0} to school {1} and role {2}.", new Object[]{sc.getUserPrincipal().getName(), src.getSchoolId(), src.getRoleId()});
                throw new NotAuthorizedException("You Don't Have Permission to update usercode " + user.getUsername() + ".");
            }
        } catch (Exception e) {
            log.log(Level.WARNING, "Unexpected exception: {0}", new Object[]{e.getMessage()});
            return null;
        } finally {
            em.close();
        }
    }

    @GET
    @Produces({"application/json"})
    @Path("/classinfo/json")
    public String info() {
        return this.getClass().getName();
    }
}
