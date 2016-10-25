package fi.dwo.server.rest;

import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherAndHasRole;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentCourseSequence;
import fi.dwo.commons.persistence.entities.PersistentFromTo;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSamlUser;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchool;
import nl.uu.fi.dwo.rest.entities.RestHasRole;
import nl.uu.fi.dwo.rest.entities.RestNewSchool;
import nl.uu.fi.dwo.rest.entities.RestSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseSequenceManager;
import fi.dwo.server.PersistentDataManagers.core.FromToManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SamlUserManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.ScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

/**
 * Operations for the GUI Component that manages the User Profile.
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@PermitAll
@Path("/secure/dwoadmin/school")
public class SecuredDwoAdminSchoolManager {

    private static final Logger LOG = Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName());

    /**
     * Registers a new school and only a school. This operation is
     * semi-idempotent. School and SchoolGroup objects are only created if they
     * do not exists. Failed creations are logged but are non-fatal for the
     * execution. This allows to recreate a school if the creation process was
     * aborted during execution. I.e. some SchoolGroup objects are missing.
     *
     * @param sc
     * @param restSchool
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submit")
    public Boolean submitSchool(@Context SecurityContext sc, RestNewSchool restSchool) {
        if (restSchool == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole hr = null;
        DomNewSchool newSchool = restSchool.getDomNewSchool();
        try {
            hr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        } catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName()).log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        if (hr != null) {
            // allowed user role
            PersistentSchool s = new PersistentSchool();
            s.setExpire(newSchool.getDomSchoolFull().getExpire());
            s.setExport(newSchool.getDomSchoolFull().getExport());
            s.setImage(newSchool.getDomSchoolFull().getImage());
            s.setSchoolLogin(newSchool.getDomSchoolFull().getSchoolLogin());
            s.setSchoolName(newSchool.getDomSchoolFull().getSchoolName());
            s.setSchoolRights(newSchool.getDomSchoolFull().getSchoolRights());
            try {
                SchoolManager.create(s);
                s = SchoolManager.findBySchoolLogin(newSchool.getDomSchoolFull().getSchoolLogin());
                LOG.log(Level.INFO, "Username {0}: created school with schoollogin {1} and id {2}.", new Object[]{sc.getUserPrincipal().getName(), s.getSchoolLogin(), s.getSchoolID()});
                //add user roles
            } catch (PersistenceException e) {
                //non-fatal for semi-idempotent operation
                LOG.log(Level.INFO, "A Persistence exception occured while creating school with schoollogin {0}.", s.getSchoolLogin());
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while creating school " + newSchool.getDomSchoolFull().getSchoolLogin() + ".");
            }
            for (Map.Entry<RoleType, String> entry : newSchool.getRoleTypePasswords().entrySet()) {
                PersistentSchoolGroup newSg = new PersistentSchoolGroup();
                newSg.setSchoolID(s.getSchoolID().intValue());
                newSg.setGroupID(entry.getKey().ordinal());
                newSg.setPasswd(entry.getValue());
                try {
                    SchoolGroupManager.create(newSg);
                } catch (PersistenceException e) {
                    //non-fatal for idempotent operation
                    String msg = MessageFormat.format("A Persistence exception occured while creating schoolgroup for school "
                            + "with logincode {0} and RoleType {1} (with groupid {2}).",
                            new Object[]{s.getSchoolLogin(), entry.getKey().name(), newSg.getGroupID()});
                    LOG.log(Level.INFO, msg);
                    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
                }
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
        return true;
    }

    /**
     * Returns a school from its persistent id.
     *
     * @param sc
     * @param school
     * @return Returns null if there was an error.
     */
    @PUT
    @Produces({"application/json"})
    @Path("/get")
    public DomSchoolFull getSchool(@Context SecurityContext sc, RestSchool4DwoAdmin school
    ) {
        if (school == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole hr = null;
        try {
            hr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        } catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName()).log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        if (hr != null) {
            PersistentSchool s = null;
            try {
                s = SchoolManager.findEntity((Long) MySQLPersistenceId.getId(school.getDomSchool4DwoAdmin().getId()));
                LOG.log(Level.FINER, "Fetched school with id {0}. ", new Object[]{s.getSchoolID()});
                return s.buildDomSchoolFull();
            } catch (Exception e) {
                LOG.log(Level.WARNING, "School " + school.getDomSchool4DwoAdmin().getId() + "Could not be found.", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the school.");

            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    ;

        /**
         * Returns the school data to be displayed.
         *
         * @param sc
         * @return
         */
        @GET
    @Produces({"application/json"})
    @Path("/getList")
    public List<DomSchool4DwoAdmin> getSchools(@Context SecurityContext sc
    ) {
        PersistentHasRole hr = null;
        try {
            hr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        } catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName()).log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        if (hr != null) {
            List<PersistentSchool> schools = null;
            List<DomSchool4DwoAdmin> domSchools;
            try {
                schools = SchoolManager.findEntities();
                LOG.log(Level.FINER, "Fetched all {0} schools. ", new Object[]{schools.size()});
                domSchools = new ArrayList<>(schools.size());
                for (PersistentSchool s : schools) {
                    domSchools.add(s.buildDomSchool4DwoAdmin());
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schools.");
            }
            return domSchools;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    /**
     * Updates the User data of the current user and returns a copy of the
     * updated data. Ignores any schoolID values.
     *
     * @param sc
     * @param restSchool
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/update")
    public Boolean updateSchool(@Context SecurityContext sc, RestSchoolFull restSchool
    ) {
        if (restSchool == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole hr = null;
        DomSchoolFull school = restSchool.getDomSchoolFull();
        try {
            hr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        } catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName()).log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        if (hr != null) {
            try {
                PersistentSchool editSchool = SchoolManager.findBySchoolLogin(school.getSchoolLogin());
                //User to update is logged in user.
                editSchool.setExpire(school.getExpire());
                editSchool.setExport(school.getExport());
                editSchool.setImage(school.getImage());
                editSchool.setSchoolLogin(school.getSchoolLogin());
                editSchool.setSchoolName(school.getSchoolName());
                editSchool.setSchoolRights(school.getSchoolRights());
                SchoolManager.edit(editSchool);
                return true;
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to update school with login " + school.getSchoolLogin() + " .");
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to update the school with login {1}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolLogin()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to update the school data.");
        }
    }

    /**
     * Removes all the school data of the current school and returns true.
     *
     * @param sc
     * @param restSchool
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/remove")
    public Boolean removeSchool(@Context SecurityContext sc, RestSchool4DwoAdmin restSchool
    ) {
        if (restSchool == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        //unwrap persistentid
        PersistentSchool school = SchoolManager.findEntity((Long) MySQLPersistenceId.getId(restSchool.getDomSchool4DwoAdmin().getId()));

        PersistentHasRole hr = null;
        try {
            hr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        } catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName()).log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        if (hr != null) {
            try {
                //Loop FromTos in School
                List<PersistentFromTo> ftList = FromToManager.findEntities(school);
                for (PersistentFromTo ft : ftList) {
                    //Remove FromTo
                    FromToManager.destroy(ft.getPersistentFromToPK());
                }

                //Loop CourseSequences in School
                List<PersistentCourseSequence> csList = CourseSequenceManager.findEntities(school);
                for (PersistentCourseSequence cs : csList) {
                    //Remove CourseSequence
                    CourseSequenceManager.destroy(cs.getCoursesequenceID());
                }

                //Loop SchoolGroups in School
                List<PersistentSchoolGroup> sgList = SchoolGroupManager.findEntities(school);
                for (PersistentSchoolGroup sg : sgList) {
                    //Loop hasRoles in SchoolGroups
                    List<PersistentHasRole> hrList = HasRoleManager.findEntities(sg);
                    for (PersistentHasRole phr : hrList) {

                        //Loop StudentOf in hasRole
                        List<PersistentStudentOfClass> soList = StudentOfClassManager.findEntities(phr.getPersistentHasRolePK());
                        for (PersistentStudentOfClass so : soList) {
                            //Remove StudentOf
                            StudentOfClassManager.destroy(so.getPersistentStudentOfClassPK());
                        }

                        //Loop TeacherOf in hasRole
                        List<PersistentTeacherOfClass> toList = TeacherOfClassManager.findEntities(phr.getPersistentHasRolePK());
                        for (PersistentTeacherOfClass to : toList) {
                            //Remove TeacherOf
                            TeacherOfClassManager.destroy(to.getPersistentTeacherOfClassPK());
                        }

                        //Loop StudentScoContext in hasRole
                        List<PersistentStudentScoContext> sscList = StudentScoContextManager.findEntities(phr.getPersistentHasRolePK());
                        for (PersistentStudentScoContext ssc : sscList) {
                            //Remove StudentScoData
                            StudentScoDataManager.destroy(ssc.getStudentSco());
                            //Remove StudentScoContext
                            StudentScoContextManager.destroy(ssc.getStudentSco());
                        }
                        //Remove hasRole
                        HasRoleManager.destroy(phr.getPersistentHasRolePK());
                        PersistentUser u = UserManager.findEntity(phr.getUser().getId());

                        if (u != null && u.isSingleSchoolAccount()) {
                            //Loop samlusers in user
                            List<PersistentSamlUser> suList = SamlUserManager.findEntities(u);
                            for (PersistentSamlUser su : suList) {
                                //remove saml user
                                SamlUserManager.destroy(su.getId());
                            }
                            //remove user
                            UserManager.destroy(u.getId());
                        }
                    }
                    //Clear tblUser schoolgroup values
                    PersistentSchoolGroup nulSg = (PersistentSchoolGroup) SchoolGroupManager.findEntity(SchoolManager.findBySchoolLogin("null"), RoleType.STUDENT);
                    List<PersistentUser> userList = UserManager.findEntities(sg);
                    if (userList != null) {
                        for (PersistentUser u : userList) {
                            u.setSchoolGroupId(nulSg.getSchoolGroupID());
                            UserManager.edit(u);
                        }
                    }

                    //Remove SchoolGroup
                    SchoolGroupManager.destroy(sg.getSchoolGroupID());
                }

                //Loop SchoolClasses in School
                List<PersistentSchoolClass> clList = SchoolClassManager.findEntities(school);
                for (PersistentSchoolClass cl : clList) {
                    //Loop ClassCourses in SchoolClass
                    List<PersistentClassCourse> ccList = ClassCourseManager.findEntities(cl);
                    for (PersistentClassCourse cc : ccList) {
                        //Remove ClassCourse
                        ClassCourseManager.destroy(cc.getClassCourseID());
                    }

                    //Remove FromTo
                    SchoolClassManager.destroy(cl.getClassID());
                }

                //Loop Courses in School
                List<PersistentCourse> cList = CourseManager.findEntities(school);
                for (PersistentCourse c : cList) {
                    //Loop ScoContext in Course
                    List<PersistentScoContext> pscList = ScoContextManager.findEntities(c);
                    for (PersistentScoContext psc : pscList) {
                        //Remove ScoData
                        ScoDataManager.destroy(psc.getScoID());
                        //Remove ScoContext
                        ScoContextManager.destroy(psc.getScoID());
                    }
                    ///Remove Course
                    CourseManager.destroy(c.getCourseID());
                }
                SchoolManager.destroy(school.getSchoolID());
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to remove school with id " + school.getSchoolID() + " .");
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to remove the school with id {1}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to rempve the school.");
        }

        return true;
    }

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @param restSchool
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/getTeachersAndHasRoleInSchool")
    public List<DomTeacherAndHasRole> getTeachersAndHasRoleInSchool(@Context SecurityContext sc, RestSchool4DwoAdmin restSchool
    ) {
        if (restSchool == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        List<DomTeacherAndHasRole> resultList = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        school = SchoolManager.findBySchoolLogin(restSchool.getDomSchool4DwoAdmin().getSchoolLogin());
        if (school == null) {
            LOG.log(Level.SEVERE, "School with login {0} was not found.", restSchool.getDomSchool4DwoAdmin().getSchoolLogin());
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "School not found.");
        }

        List<PersistentHasRole> hrList;
        try {
            hrList = HasRoleUtilManager.getHasRolesInSchoolAndRole(school, RoleType.TEACHER);
            resultList = new ArrayList<>(hrList.size());
            for (PersistentHasRole hr : hrList) {
                PersistentUser user = (PersistentUser) UserManager.findEntity(hr.getPersistentHasRolePK().getUserID());
                DomTeacherAndHasRole domTAHR = new DomTeacherAndHasRole();
                domTAHR.setTeacher(user.buildDomTeacher());
                domTAHR.setHasRole(hr.buildDomHasRole());
                resultList.add(domTAHR);
            }
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        return resultList;
    }

    /**
     * Updates the User data of the current user and returns a copy of the
     * updated data. Ignores any schoolID values.
     *
     * @param sc
     * @param restHasRole
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/updateHasRoleRights")
    public Boolean updateHasRoleRights(@Context SecurityContext sc, RestHasRole restHasRole
    ) {
        if (restHasRole == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_FormatError, "Incorrect formatted REST-request.");
        }
        PersistentHasRole hr = null;
        DomHasRole domHasRole = restHasRole.getDomHasRole();
        try {
            hr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        } catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName()).log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        if (hr != null) {
            try {
                PersistentHasRole pHasRole = HasRoleManager.findEntity(
                        new PersistentHasRolePK(MySQLPersistenceId.getId(domHasRole.getUserId()),
                                MySQLPersistenceId.getId(domHasRole.getSchoolGroupId()))
                );
                pHasRole.setRights(domHasRole.getRights());
                HasRoleManager.editRights(pHasRole);
                return true;
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "User with hasRole " + hr.getPersistentHasRolePK() + " failed to update rights of hasrole " + domHasRole.getId() + " to rightsString " + domHasRole.getRights() + " .");
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to update the hasRole {0} with user login {1}.", new Object[]{domHasRole.getId(), sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to update the school data.");
        }
    }
}
