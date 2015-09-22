/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.server.rest;

import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceId;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentFromTo;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.server.PersistentEntityManagers.FromToManager;
import fi.dwo.server.PersistentEntityManagers.HasRoleManager;
import fi.dwo.server.PersistentEntityManagers.SchoolManager;
import fi.dwo.server.PersistentEntityManagers.StudentOfClassManager;
import fi.dwo.server.PersistentEntityManagers.StudentScoContextManager;
import fi.dwo.server.PersistentEntityManagers.StudentScoDataManager;
import fi.dwo.server.PersistentEntityManagers.TeacherOfClassManager;
import fi.dwo.server.PersistentEntityManagers.UserManager;
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
public class SecuredDwoadminSchoolManager {

    private static final Logger LOG = Logger.getLogger(SecuredDwoadminSchoolManager.class.getName());

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
    public PersistentSchool addSchool(@Context SecurityContext sc, PersistentSchool school) {
        PersistentHasRole hr = RoleChecker.getCurrentRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
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
        PersistentHasRole hr = RoleChecker.getCurrentRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
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
    @Path("/getlist")
    public List<PersistentSchool> getSchools(@Context SecurityContext sc) {
        List<PersistentSchool> schools = null;
        PersistentHasRole hr = RoleChecker.getCurrentRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        if (hr != null) {
            try {
                schools = SchoolManager.findEntities();
                LOG.log(Level.FINER, "Fetched all {0} schools. ", new Object[]{schools.size()});
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schools.");
            }
            return schools;
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
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/update")
    public PersistentSchool updateSchool(@Context SecurityContext sc, PersistentSchool school) {
        PersistentHasRole hr = RoleChecker.getCurrentRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
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
     * @param school
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/remove")
    public Boolean removeSchool(@Context SecurityContext sc, PersistentSchool school) {
//        Verwijder in de volgende vollegorde de volgende entity instances van een de school in kwestie: 
//        \texttt{StudentScoData}, \texttt{StudentScoContext}, \texttt{TeacherOf}, \texttt{StudentOf}, 
//                \texttt{HasRole}, \texttt{SchoolClass}, \texttt{ClassCourse}, \texttt{ScoData},
//                        \texttt{ScoContext}, \texttt{CourseSequence}, \texttt{FromTo}, 
//                                 instanties, alle users die een single schoolaccount hebben,
//                                 \texttt{SchoolGroup}, \texttt{School}. 
        PersistentHasRole hr = RoleChecker.getCurrentRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        if (hr != null) {
            try {
                    //Loop FromTo's in School
                    List<PersistentFromTo> ftList = FromToManager.findEntities(school);
                    for (PersistentFromTo ft : ftList) {
                        //Remove FromTo
                        FromToManager.destroy(ft.getPersistentFromToPK());
                    }

                    //Loop CourseSequences in School
                        //Remove CourseSequence
                    
                    //Loop SchoolGroups in School
                        //Loop hasRoles in SchoolGroups
                            //Loop StudentOf in hasRole
                                //Remove StudentOf
                            //Loop TeacherOf in hasRole
                                //Remove TeacherOf
                            //Loop StudentScoContext in hasRole
                                //Remove StudentScoData
                                //Remove StudentScoContext
                            //Remove hasRole
                    //Remove SchoolGroup
                    
                    //Loop SchoolClasses in School
                        //Loop ClassCourses in SchoolClass
                            //Remove ClassCourse
                    //Loop Courses in School
                        //Loop ScoContext in Course
                            //Remove ScoData
                            //Remove ScoContext
                        //Remove Course
                    
                    
                }
                catch (Exception e) {
                    LOG.log(Level.SEVERE, "Username " + sc.getUserPrincipal().getName() + ": Unexpected exception", e);
                    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Failed to remove school with id " + school.getSchoolID() + " .");
                }
            }else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to remove the school with id {1}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to rempve the school.");
        }

            return true;
        }

    }
