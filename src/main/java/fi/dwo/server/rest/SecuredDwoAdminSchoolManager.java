package fi.dwo.server.rest;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceId;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentCourseSequence;
import fi.dwo.commons.persistence.entities.PersistentFromTo;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSamlUser;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.rest.entities.RestSchool4Admin;
import fi.dwo.commons.rest.entities.RestTeacher;
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
import static fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager.getHasRoleInSchool;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
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
     * Registers a new school.
     *
     * @param sc
     * @param school
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submit")
    public PersistentSchool submitSchool(@Context SecurityContext sc, PersistentSchool school) {
        PersistentHasRole hr = null;
        try {
            hr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }

        if (hr != null) {
            // allowed user role
            PersistentSchool s = null;
            try {
                SchoolManager.create(school);
                LOG.log(Level.INFO, "Username {0}: created school with schoollogin {1} and id {2}.", new Object[]{sc.getUserPrincipal().getName(), s.getSchoolLogin(), s.getSchoolID()});
                s = SchoolManager.findBySchoolLogin(school.getSchoolLogin());
                return s;
            }
            catch (Exception e) {
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while creating school " + school.getSchoolName() + ".");
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    /**
     * Returns a school from its persistent id.
     *
     * @param sc
     * @param pid
     * @return Returns null if there was an error.
     */
    @PUT
    @Produces({"application/json"})
    @Path("/get")
    public PersistentSchool getSchool(@Context SecurityContext sc, PersistenceId pid) {
        PersistentHasRole hr = null;
        try {
            hr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }
        if (hr != null) {
            PersistentSchool s = null;
            try {
                s = SchoolManager.findEntity((int) MySQLPersistenceId.getId(pid));
                LOG.log(Level.FINER, "Fetched school with id {0}. ", new Object[]{s.getSchoolID()});
                return s;
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "School " + pid + "Could not be found.", e);
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
    public List<RestSchool4Admin> getSchools(@Context SecurityContext sc) {
        PersistentHasRole hr = null;
        try {
            hr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }
        if (hr != null) {
            List<PersistentSchool> schools = null;
            List<RestSchool4Admin> restSchools;
            try {
                schools = SchoolManager.findEntities();
                LOG.log(Level.FINER, "Fetched all {0} schools. ", new Object[]{schools.size()});
                restSchools = new ArrayList<RestSchool4Admin>(schools.size());
                for (PersistentSchool s : schools) {
                    restSchools.add(new RestSchool4Admin(s));
                }
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schools.");
            }
            return restSchools;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    /**
     * Updates the User data of the current user and returns a copy of the
     * updated data.
     *
     * @param sc
     * @param school
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/update")
    public PersistentSchool updateSchool(@Context SecurityContext sc, PersistentSchool school) {
        PersistentHasRole hr = null;
        try {
            hr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }
        if (hr != null) {
            try {
                //User to update is logged in user.
                SchoolManager.edit(school);
                return SchoolManager.findBySchoolLogin(school.getSchoolLogin());
            }
            catch (Exception e) {
                LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to update school with id " + school.getSchoolID() + " .");
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to update the school with id {1}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolID()});
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
    public Boolean removeSchool(@Context SecurityContext sc, RestSchool4Admin restSchool) {
        //unwrap persistentid
        PersistentSchool school = SchoolManager.findEntity((int) (long) MySQLPersistenceId.getId(restSchool.getId()));

        PersistentHasRole hr = null;
        try {
            hr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        }
        catch (Dwo2Exception ex) {
            Logger.getLogger(SecuredDwoAdminSchoolManager.class.getName()).log(Level.SEVERE, null, ex);
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
                        List<PersistentTeacherOfClass> toList = TeacherOfClassManager.findEntities(hr);
                        for (PersistentTeacherOfClass to : toList) {
                            //Remove TeacherOf
                            TeacherOfClassManager.destroy(to.getPersistentTeacherOfClassPK());
                        }

                        //Loop StudentScoContext in hasRole
                        List<PersistentStudentScoContext> sscList = StudentScoContextManager.findEntities(hr.getPersistentHasRolePK());
                        for (PersistentStudentScoContext ssc : sscList) {
                            //Remove StudentScoData
                            StudentScoDataManager.destroy(ssc.getStudentSco());
                            //Remove StudentScoContext
                            StudentScoContextManager.destroy(ssc.getStudentSco());
                        }
                        //Remove hasRole
                        HasRoleManager.destroy(hr.getPersistentHasRolePK());
                        PersistentUser u = UserManager.findEntity(hr.getUser().getUserID());

                        if (u != null && u.isSingleSchoolAccount()) {
                            //Loop samlusers in user
                            List<PersistentSamlUser> suList = SamlUserManager.findEntities(u);
                            for (PersistentSamlUser su : suList) {
                                //remove saml user
                                SamlUserManager.destroy(su.getId());
                            }
                            //remove user
                            UserManager.destroy(u.getUserID());
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

                    //Loop Courses in School
                    List<PersistentCourse> cList = CourseManager.findEntities(school);
                    for (PersistentCourse c : cList) {
                        //Loop ScoContext in Course
                        List<PersistentScoContext> pscList = ScoContextManager.findEntities(c);
                        for (PersistentScoContext psc : pscList) {
                            //Remove ScoData
                            ScoDataManager.destroy(psc.getScoID());
                            //Remove ScoContext
                            ScoDataManager.destroy(psc.getScoID());
                        }
                        ///Remove Course
                        CourseManager.destroy(c.getCourseID());
                    }
                    //Remove FromTo
                    SchoolClassManager.destroy(cl.getClassID());
                }
            }
            catch (Exception e) {
                LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to remove school with id " + school.getSchoolID() + " .");
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to remove the school with id {1}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to rempve the school.");
        }

        return true;
    }
  
}
