package fi.dwo.server.rest;

import fi.dwo.server.rest.util.JoinDataManager;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.rest.entities.RestSchoolClass;
import fi.dwo.commons.rest.entities.RestStudent;
import fi.dwo.commons.rest.entities.RestTeacher;
import fi.dwo.server.PersistentEntityManagers.SchoolClassManager;
import fi.dwo.server.PersistentEntityManagers.StudentOfClassManager;
import fi.dwo.server.PersistentEntityManagers.TeacherOfClassManager;
import fi.dwo.server.PersistentEntityManagers.UserManager;
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
public class SecuredAdminSchoolClassManager {

    private static final Logger LOG = Logger.getLogger(SecuredAdminSchoolClassManager.class.getName());

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getlist")
    public List<RestSchoolClass> getSchoolClasses(@Context SecurityContext sc) {
        PersistentHasRole phr = JoinDataManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        PersistentSchool school = JoinDataManager.getSchoolforHasRole(phr);
        
        if (phr != null && school!=null) {
            List<PersistentSchoolClass> schoolClasses = null;
            List<RestSchoolClass> restSchoolClasses;
            try {
                schoolClasses = SchoolClassManager.findEntities(school);
                LOG.log(Level.FINER, "Fetched all {0} schools. ", new Object[]{schoolClasses.size()});
                restSchoolClasses = new ArrayList<RestSchoolClass>(schoolClasses.size());
                for (PersistentSchoolClass s : schoolClasses) {
                    restSchoolClasses.add(new RestSchoolClass(s));
                }
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
            }
            return restSchoolClasses;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @param restSchoolClass
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getlist")
    public List<RestTeacher> GetTeachersInSchoolClass(@Context SecurityContext sc, RestSchoolClass restSchoolClass) {
        PersistentHasRole phr = JoinDataManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        PersistentSchool school = JoinDataManager.getSchoolforHasRole(phr);
        
        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((int) (long) MySQLPersistenceId.getId(restSchoolClass.getId()));
        if (phr != null && school!=null && schoolClass.getSchoolID() == school.getSchoolID()) {
            //Fetch TeacherOfClass
            List<PersistentTeacherOfClass> teachersOfClass = TeacherOfClassManager.findEntities(schoolClass);
                LOG.log(Level.FINER, "Fetched all {0} teachers. ", new Object[]{teachersOfClass.size()});
            List<RestTeacher> restTeachers;
            try {
                restTeachers = new ArrayList<RestTeacher>(teachersOfClass.size());
                for (PersistentTeacherOfClass t : teachersOfClass) {
                    PersistentUser u = UserManager.findEntity(t.getPersistentTeacherOfClassPK().getUserID());
                    restTeachers.add(new RestTeacher(u));
                }
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the teachers.");
            }
            return restTeachers;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @param restSchoolClass
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getlist")
    public List<RestStudent> GetStudentsInSchoolClass(@Context SecurityContext sc, RestSchoolClass restSchoolClass) {
        PersistentHasRole phr = JoinDataManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        PersistentSchool school = JoinDataManager.getSchoolforHasRole(phr);
        
        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((int) (long) MySQLPersistenceId.getId(restSchoolClass.getId()));
        if (phr != null && school!=null && schoolClass.getSchoolID() == school.getSchoolID()) {
            //Fetch TeacherOfClass
            List<PersistentStudentOfClass> studentsOfClass = StudentOfClassManager.findEntities(schoolClass);
                LOG.log(Level.FINER, "Fetched all {0} teachers. ", new Object[]{studentsOfClass.size()});
            List<RestStudent> restStudents;
            try {
                restStudents = new ArrayList<RestStudent>(studentsOfClass.size());
                for (PersistentStudentOfClass t : studentsOfClass) {
                    PersistentUser u = UserManager.findEntity(t.getPersistentStudentOfClassPK().getUserID());
                    restStudents.add(new RestStudent(u));
                }
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the teachers.");
            }
            return restStudents;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access dwoadmin functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

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
    public Boolean AddTeacherToSchoolClass(@Context SecurityContext sc, RestTeacher restTeacher, RestSchoolClass restSchoolClass) {
        PersistentHasRole phr = JoinDataManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        PersistentSchool school = JoinDataManager.getSchoolforHasRole(phr);
        PersistentUser teacher = UserManager.findEntity((int) (long) MySQLPersistenceId.getId(restTeacher.getId()));
        PersistentHasRole thr = JoinDataManager.getHasRoleInSchool(teacher, school, RoleType.TEACHER);
        
        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((int) (long) MySQLPersistenceId.getId(restSchoolClass.getId()));
        if (phr != null && schoolClass!=null && thr!=null && schoolClass.getSchoolID() == school.getSchoolID()) {
            PersistentTeacherOfClass toc = new PersistentTeacherOfClass();
            toc.setPersistentTeacherOfClassPK(new PersistentTeacherOfClassPK(teacher.getUserID(),schoolClass.getClassID(), thr.getPersistentHasRolePK().getSchoolGroupID()));
            toc.setRegisterDate(null);
        }
        return true;
    }
    
    /**
     * Removes all the school data of the current school and returns true.
     *
     * @param sc
     * @param restSchoolClass
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/remove")
    public Boolean removeSchoolClass(@Context SecurityContext sc, RestSchoolClass restSchoolClass) {
        PersistentHasRole phr = JoinDataManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.ADMIN);
        PersistentSchool school = JoinDataManager.getSchoolforHasRole(phr);
        
        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((int) (long) MySQLPersistenceId.getId(restSchoolClass.getId()));
        if (phr != null && school!=null && schoolClass.getSchoolID() == school.getSchoolID()) {
            //Loop students in class
            List<PersistentStudentOfClass> studentList = StudentOfClassManager.findEntities(schoolClass);
            for (PersistentStudentOfClass t : studentList) {
                //remove teachers
                StudentOfClassManager.destroy(t.getPersistentStudentOfClassPK());
            }

            //Loop teachers in class
            List<PersistentTeacherOfClass> teacherList = TeacherOfClassManager.findEntities(schoolClass);
            for (PersistentTeacherOfClass t : teacherList) {
                //remove teachers
                TeacherOfClassManager.destroy(t.getPersistentTeacherOfClassPK());
            }
            SchoolClassManager.destroy(schoolClass.getClassID());
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to remove a schoolClass with id {1}.", new Object[]{sc.getUserPrincipal().getName(), schoolClass.getClassID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to rempve the school class.");
        }

        return true;
    }

}
