/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.dom.entities.DomSchoolRoleAndClass;
import fi.dwo.commons.dom.entities.DomSchoolsRolesAndClasses;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.*;
import fi.dwo.commons.rest.entities.*;
import fi.dwo.commons.util.DwoDateUtilities;
import fi.dwo.server.PersistentDataManagers.core.DwoSystemParametersManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
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
    //TODO rewrite to use EntityManagers APIs.

    private static final Logger LOG = Logger.getLogger(SecuredUserAccountLoginsManager.class.getName());
//    @Context  //injected response proxy supporting multiple threads
//    private HttpServletResponse response;

    private DomSchoolRoleAndClass getCurrentSchoolRoleAndClass(String scUsername, Long userId) {
        EntityManager em = DwoEmfFactory.getEntityManager();

        DomSchoolRoleAndClass curSac = new DomSchoolRoleAndClass();
//        // retrieve the current active <school,role, class>
        try {
            javax.persistence.Query q = em.createQuery("select h.schoolGroup.schoolID, h.schoolGroup.school.schoolName, h.schoolGroup.groupID, h.schoolGroup.role.groupname, h.classID, h.persistentHasRolePK.userID , h.schoolGroup.schoolGroupID  from PersistentHasRole h where h.persistentHasRolePK.userID = :userID and h.user.schoolGroupID = h.persistentHasRolePK.schoolGroupID");
            //Sample query
            //select h.schoolGroup.schoolID, h.schoolGroup.school.schoolName, h.schoolGroup.groupID, h.schoolGroup.role.groupname, h.classID from PersistentHasRole h where h.persistentHasRolePK.userID = 184690
            q.setParameter("userID", userId);
            List<Object[]> resultList = q.getResultList();
            LOG.log(Level.FINER, "Username {0}: resultList size: {0}.", new Object[]{scUsername, resultList.size()});
            if (resultList.size() == 1) {
                LOG.log(Level.FINE, "Username {0}: Fetched current role tuple <schoolID, schoolName, groupID, groupname, classID, userID, groupID>: {1}, {2}, {3}, {4}, {5}, {6}, {7}.", new Object[]{scUsername, resultList.get(0)[0], resultList.get(0)[1], resultList.get(0)[2], resultList.get(0)[3], resultList.get(0)[4], resultList.get(0)[5], resultList.get(0)[6]});
                curSac.setSchoolId((PersistenceId) MySQLPersistenceId.createPersistenceId(((Integer) resultList.get(0)[0]).longValue(), PersistenceClassType.PersistentSchool));
                curSac.setSchoolName((String) resultList.get(0)[1]);
                curSac.setRoleId((PersistenceId) MySQLPersistenceId.createPersistenceId(((Integer) resultList.get(0)[2]).longValue(), PersistenceClassType.PersistentRole));
                curSac.setRoleName((String) resultList.get(0)[3]);
                if (resultList.get(0)[4] != null) {
                    curSac.setSchoolClassId((PersistenceId) MySQLPersistenceId.createPersistenceId((Long) resultList.get(0)[4], PersistenceClassType.PersistentSchoolClass));
                    curSac.setSchoolClassName((String) em.createQuery("select c.class1 from PersistentSchoolClass c where c.classID = :id ").setParameter("id", (Long) resultList.get(0)[4]).getSingleResult());
                } else {
                    curSac.setSchoolClassId(null);
                    curSac.setSchoolClassName(null);
                }
                curSac.setUserId((PersistenceId) MySQLPersistenceId.createPersistenceId((Long) resultList.get(0)[5], PersistenceClassType.PersistentUser));
                curSac.setSchoolGroupId((PersistenceId) MySQLPersistenceId.createPersistenceId((Long) resultList.get(0)[6], PersistenceClassType.PersistentSchoolGroup));
            }
        }
        catch (Exception e) {
            LOG.log(Level.WARNING, "Username " + scUsername + ": Unexpected exception.", e);
            return new DomSchoolRoleAndClass();
        }
        finally {
            em.close();
        }
        return curSac;
    }

    /**
     * Returns the current school role. The information is extracted from the
     * security context.
     *
     * @param sc
     * @return Returns null if there was an error.
     */
    @GET
    @Produces({"application/json"})
    @Path("/getList")
    public DomSchoolsRolesAndClasses getSchoolLogins(@Context SecurityContext sc) {
        EntityManager em = DwoEmfFactory.getEntityManager();

        DomSchoolsRolesAndClasses sacs = new DomSchoolsRolesAndClasses();
        PersistentUser user;
        List<DomSchoolRoleAndClass> sacList = (List<DomSchoolRoleAndClass>) new ArrayList<DomSchoolRoleAndClass>();
        DomSchoolRoleAndClass curSac;

        // fetch the authenticated user
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentUser.findByUsername");
            String userName = sc.getUserPrincipal().getName();
            q.setParameter("username", userName);
            user = (PersistentUser) q.getSingleResult();
            String name = user.getUsername();
            long userId = user.getUserID();
            LOG.log(Level.FINE, "Username {0}: Fetched User with username {1} and id {2}", new Object[]{sc.getUserPrincipal().getName(), name, userId});
            //Retrieve the list of possible <School, role, class>, class can be null.
            q = em.createQuery("select h.schoolGroup.schoolID, h.schoolGroup.school.schoolName, "
                    + "h.schoolGroup.groupID, h.schoolGroup.role.groupname, h.classID, h.persistentHasRolePK.userID , h.schoolGroup.schoolGroupID  "
                    + "from PersistentHasRole h where h.persistentHasRolePK.userID = :userID "
                    + " ");
            //Sample query
            //select h.schoolGroup.schoolID, h.schoolGroup.school.schoolName, h.schoolGroup.groupID, h.schoolGroup.role.groupname, h.classID from PersistentHasRole h where h.persistentHasRolePK.userID = 184690
            q.setParameter("userID", userId);
            List<Object[]> resultList = q.getResultList();
            LOG.log(Level.FINE, "Username {0}: Fetched {2} HasRoles for userId {1}.", new Object[]{sc.getUserPrincipal().getName(), userId, resultList.size()});
            DomSchoolRoleAndClass sac;
            for (Object[] oList : resultList) {
                LOG.log(Level.FINE, "Fetched hasRole tuple <schoolID, schoolName, groupID, groupname, classID, userID, schoolGroupID>: {0}, {1}, {2}, {3}, {4}, {5}, {6}.", new Object[]{oList[0], oList[1], oList[2], oList[3], oList[4], oList[5], oList[6]});
                sac = new DomSchoolRoleAndClass();
                Integer i = (Integer) oList[0];
                sac.setSchoolId((PersistenceId) MySQLPersistenceId.createPersistenceId(i.longValue(), PersistenceClassType.PersistentSchool));
                sac.setSchoolName((String) oList[1]);
                i = (Integer) oList[2];
                sac.setRoleId((PersistenceId) MySQLPersistenceId.createPersistenceId(i.longValue(), PersistenceClassType.PersistentRole));
                sac.setRoleName((String) oList[3]);
                if (oList[4] != null) {
                    i = (Integer) oList[0];
                    sac.setSchoolClassId((PersistenceId) MySQLPersistenceId.createPersistenceId(i.longValue(), PersistenceClassType.PersistentSchoolClass));
                    Long j = (Long) oList[4];
                    sac.setSchoolClassName((String) em.createQuery("select c.class1 from PersistentSchoolClass c where c.classID = :id ").setParameter("id", j.longValue()).getSingleResult());
                } else {
                    sac.setSchoolClassId(null);
                    sac.setSchoolClassName(null);
                }
                Long j = (Long) oList[5];
                sac.setUserId((PersistenceId) MySQLPersistenceId.createPersistenceId(j.longValue(), PersistenceClassType.PersistentUser));
                j = (Long) oList[6];
                sac.setSchoolGroupId((PersistenceId) MySQLPersistenceId.createPersistenceId(j.longValue(), PersistenceClassType.PersistentSchoolGroup));
                sacList.add(sac);
            }

        }
        catch (Exception e) {
            LOG.log(Level.WARNING, "Username " + sc.getUserPrincipal().getName() + " Unexpected exception.", e);
            return new DomSchoolsRolesAndClasses();
        }
        finally {
            LOG.log(Level.FINER, " closed em.");
            em.close();
        }

        curSac = this.getCurrentSchoolRoleAndClass(sc.getUserPrincipal().getName(), user.getUserID());
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
    @Path("/select")
    public DomSchoolRoleAndClass switchToSchoolLogin(@Context SecurityContext sc, RestSchoolRoleAndClass sarc) {
        EntityManager em = DwoEmfFactory.getEntityManager();

        PersistentUser user;
        DomSchoolRoleAndClass curSac = new DomSchoolRoleAndClass();

        // fetch the authenticated user
        try {
            javax.persistence.Query q = em.createNamedQuery("PersistentUser.findByUsername");
            String userName = sc.getUserPrincipal().getName();
            q.setParameter("username", userName);
            user = (PersistentUser) q.getSingleResult();
            String name = user.getUsername();
            long userId = user.getUserID();
            LOG.log(Level.INFO, "Username {0}: Fetched User with username {0}", new Object[]{sc.getUserPrincipal().getName(), name});
            LOG.log(Level.INFO, "And id is {0}", new Object[]{userId});

            if (userId != MySQLPersistenceId.getId(sarc.getDomSchoolRoleAndClass().getUserId())) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to update the user profile of user id {1}.", new Object[]{sc.getUserPrincipal().getName(), MySQLPersistenceId.getId(sarc.getDomSchoolRoleAndClass().getUserId())});
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to update usercode " + user.getUsername() + ".");
            }
            //update user.

            PersistentUser u = UserManager.findEntity(user.getUserID());
            u.setSchoolGroupID(
                    (Long) MySQLPersistenceId.getId(sarc.getDomSchoolRoleAndClass().getSchoolGroupId()));
            UserManager.edit(u);

            PersistentHasRole hr = HasRoleManager.findEntity(new PersistentHasRolePK(user.getUserID(), MySQLPersistenceId.getId(sarc.getDomSchoolRoleAndClass().getSchoolGroupId())));
            if (sarc.getDomSchoolRoleAndClass().getSchoolClassId() != null) {
                hr.setClassID(MySQLPersistenceId.getId(sarc.getDomSchoolRoleAndClass().getSchoolClassId()));
            }
            HasRoleManager.edit(hr);

            //update class in hasRole
            LOG.log(Level.INFO, "Username {0}: Updated SchoolGroupID to {1} for User with username {2}", new Object[]{sc.getUserPrincipal().getName(), u.getSchoolGroupID(), user.getUsername()});
        }
        catch (Exception e) {
            LOG.log(Level.WARNING, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception.", e);
            return new DomSchoolRoleAndClass();
        }
        finally {
            em.close();
        }
        curSac = getCurrentSchoolRoleAndClass(sc.getUserPrincipal().getName(), user.getUserID());
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
    public Boolean submitASchoolLogin(@Context SecurityContext sc, RestNewSchoolLogin existingUserReg) {
        EntityManager em = DwoEmfFactory.getEntityManager();

        //Check for userid, should exist.
        PersistentUser user = UserManager.findByUserName(sc.getUserPrincipal().getName());
        if (user == null) {
            LOG.log(Level.WARNING, "Username {0}: Authentication for schoollogin {1} and role {2} for usercode {3} failed.", new Object[]{sc.getUserPrincipal().getName(), existingUserReg.getDomNewSchoolLogin().getSchoolLogin(), existingUserReg.getDomNewSchoolLogin().getRole().name(), sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_AuthenticationError, "Authentication for " + sc.getUserPrincipal().getName() + " failed.");
        }
        //invariant: have user data

        //fetch schoolgroup id.     
        PersistentSchoolGroup sg;
        PersistentSchool school = null;
        if (existingUserReg.getDomNewSchoolLogin().getSchoolLogin() == null && existingUserReg.getDomNewSchoolLogin().getSchoolCode() == null) {
            existingUserReg.getDomNewSchoolLogin().setSchoolLogin("null"); //TODO retrieve the null school login and code from the DwoSystemParameters.
            existingUserReg.getDomNewSchoolLogin().setSchoolCode("null");
        }

        try {
            javax.persistence.Query q = em.createQuery(" select sg from PersistentSchool s join PersistentSchoolGroup sg where sg.school = s and s.schoolLogin = :schoollogin and sg.role.groupname = :role and sg.passwd = :schoolcode");
            q.setParameter("schoollogin", existingUserReg.getDomNewSchoolLogin().getSchoolLogin());
            q.setParameter("schoolcode", existingUserReg.getDomNewSchoolLogin().getSchoolCode());
            q.setParameter("role", (existingUserReg.getDomNewSchoolLogin().getRole().name()));
            sg = (PersistentSchoolGroup) q.getSingleResult();
            school = sg.getSchool(); // Sadly, another query.
            if (school == null) {
                String msg = String.format("Username {0}: Registration authentication failed for school {1} with school login {2} and school code {3} for usercode {4}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolName(), existingUserReg.getDomNewSchoolLogin().getSchoolLogin(), existingUserReg.getDomNewSchoolLogin().getSchoolCode(), user.getUsername()});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_Registration_Invalid_school_role_credentials, msg);
            }
            //invariant: usercode does not exists and a school exists for schoollogin and schoolcode
            LOG.log(Level.FINER, "Username {0}: School-manager retrieved school {1} from school login and school code for usercode {2}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolName(), user.getUsername()});
        }
        catch (Exception ex) {
            String msg = String.format("Username {0}: Registration authentication failed for school login {1} and school code {2} for usercode {4}.", new Object[]{sc.getUserPrincipal().getName(), existingUserReg.getDomNewSchoolLogin().getSchoolLogin(), existingUserReg.getDomNewSchoolLogin().getSchoolCode(), user.getUsername()});
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

        Date now = DwoDateUtilities.getCurrentDwoDate();

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
        LOG.log(Level.INFO, "Username {0}: Created a new HasRole for user index {1}, schoolgroup index {2} and role {3} was added to the database.", new Object[]{sc.getUserPrincipal().getName(), hasRole.getPersistentHasRolePK().getUserID(), hasRole.getPersistentHasRolePK().getSchoolGroupID(), existingUserReg.getDomNewSchoolLogin().getRole().name()});
        //success
        return true;
    }

    /**
     * Removes a school login ({@Link HasRole) for an existing user.
     *
     * @param sc
     * @param sarc
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/remove")
    public Boolean removeASchoolLogin(@Context SecurityContext sc, RestSchoolRoleAndClass sarc) {
        PersistentUser user = UserManager.findByUserName(sc.getUserPrincipal().getName());

        Long userId = (Long) MySQLPersistenceId.getId(sarc.getDomSchoolRoleAndClass().getUserId());
        Long schoolGroupId = (Long) MySQLPersistenceId.getId(sarc.getDomSchoolRoleAndClass().getSchoolGroupId());

        PersistentSchool nullSchool = SchoolManager.findBySchoolLogin(DwoSystemParametersManager.findByName("NullSchoolLogin").getValue());
        Long sgId = SchoolGroupManager.findEntity(nullSchool, RoleType.STUDENT).getSchoolGroupID();
        if(sarc.getDomSchoolRoleAndClass().getSchoolGroupId().equals(sgId)){
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: tried to remove the null school login of  user {1}.", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "Only system has the right to remove a null school login of user " + user.getUsername() + ".");
        }

        if (!user.getUserID().equals(userId)) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: tried to remove a school login of  user {1}.", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to remove school login of user " + user.getUsername() + ".");

        }
        PersistentHasRole hr = (PersistentHasRole) HasRoleManager.findEntity(new PersistentHasRolePK(userId, schoolGroupId));
        if (hr == null) {
            LOG.log(Level.FINE, "Username {0}: Tried to remove a non-existing hasRole: <userid, schoolgroupid> <{1},{2}>", new Object[]{sc.getUserPrincipal().getName(), sarc.getDomSchoolRoleAndClass().getUserId(), sarc.getDomSchoolRoleAndClass().getSchoolGroupId()});
            return true;
        }
        List<PersistentStudentScoContext> sscList = StudentScoContextManager.findEntities(hr.getPersistentHasRolePK());
        for (PersistentStudentScoContext ssc : sscList) {
            StudentScoDataManager.destroy(ssc.getStudentSco());
            StudentScoContextManager.destroy(ssc.getStudentSco());
        }
        //Remove StudentOf and TeacherOf
        List<PersistentStudentOfClass> soList = StudentOfClassManager.findEntities(hr.getPersistentHasRolePK());
        for (PersistentStudentOfClass so : soList) {
            StudentOfClassManager.destroy(so.getPersistentStudentOfClassPK());
        }
        List<PersistentTeacherOfClass> toList = TeacherOfClassManager.findEntities(hr.getPersistentHasRolePK());
        for (PersistentTeacherOfClass to : toList) {
            TeacherOfClassManager.destroy(to.getPersistentTeacherOfClassPK());
        }
        //Ready to remove hasRoles
        HasRoleManager.destroy(hr.getPersistentHasRolePK());

        //Update the default hasRole to the null school if user is in the current role.
        if (user.getSchoolGroupID().equals(hr.getPersistentHasRolePK().getSchoolGroupID())) //userid's already match...
        {
            RoleType type = RoleType.STUDENT;
            PersistentSchoolGroup sg = SchoolGroupManager.findBySchoolAndRole(SchoolManager.findBySchoolLogin("null"), type);
            PersistentUser u = UserManager.findEntity(user.getUserID());
            u.setSchoolGroupID(sg.getSchoolGroupID());
            UserManager.edit(u);
        }

        return true;
    }

}
