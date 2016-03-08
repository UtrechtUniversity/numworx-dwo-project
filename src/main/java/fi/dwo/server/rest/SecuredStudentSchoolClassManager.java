package fi.dwo.server.rest;

import fi.dwo.commons.dom.entities.DomNewSchoolClass4Student;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceId;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.rest.entities.RestNewSchoolClass4Student;
import fi.dwo.commons.rest.entities.RestSchoolClass;
import fi.dwo.commons.util.DwoDateUtilities;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import java.util.ArrayList;
import java.util.List;
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
 * Operations for the GUI Component that manages the school classes.
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@PermitAll
@Path("/secure/student/schoolclass")
public class SecuredStudentSchoolClassManager {

    private static final Logger LOG = Logger.getLogger(SecuredStudentSchoolClassManager.class.getName());

@GET
    @Produces({"application/json"})
    @Path("/getActive")
    public DomSchoolClass getActiveSchoolClass(@Context SecurityContext sc) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolClass schoolClass = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.STUDENT);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access student functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        if(phr.getClassID()==null) return null;
        
        schoolClass = SchoolClassManager.findEntity(phr.getClassID());

        if (schoolClass == null || !schoolClass.getSchoolID().equals(school.getSchoolID())) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Active schoolClass {2} from a different school that registered for hasRole in school {1} with usercode {0}.", new Object[]{sc.getUserPrincipal().getName(), school.getSchoolID(), schoolClass.getClassID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + sc.getUserPrincipal().getName() + ".");
        }
        return new DomSchoolClass(schoolClass);
    }

    
    @PUT
    @Produces({"application/json"})
    @Path("/select")
    public Boolean setActiveSchoolClass(@Context SecurityContext sc, RestSchoolClass restSchoolClass) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        PersistentSchoolClass schoolClass = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.STUDENT);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access student functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(restSchoolClass.getDomSchoolClass().getId()));

        if (schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
            try {
                phr.setClassID(schoolClass.getClassID());
                HasRoleManager.edit(phr);
            }
            catch (PersistenceException e) {
                LOG.log(Level.WARNING, "Unexpected persistence exception", e);
            }
            return true;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access student functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    /**
     * Removes a student from a school class and returns true if the remove
     * occurred.
     *
     * @param sc
     * @param restSchoolClass
     * @return true if success, false if the student does not exists to be
     * removed
     */
    @PUT
    @Produces({"application/json"})
    @Path("/remove")
    public Boolean removeStudentFromSchoolClass(@Context SecurityContext sc, RestSchoolClass restSchoolClass) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.STUDENT);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access student functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(restSchoolClass.getDomSchoolClass().getId()));

        if (phr != null && schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())) {
            try {
                PersistentStudentOfClassPK socId = new PersistentStudentOfClassPK(phr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID());
                StudentOfClassManager.destroy(socId);
            }
            catch (PersistenceException e) {
                return false;
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to remove self from a schoolclass id {1} while one or both do not exists or are not in the same school.", new Object[]{sc.getUserPrincipal().getName(), schoolClass.getClassID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to remove yourself from the school class.");
        }

        return true;
    }

    /**
     * Removes a student from a school class and returns true if the remove
     * occurred.
     *
     * @param sc
     * @param restSchoolClass
     * @return true if success, false if the student does not exists to be
     * removed
     */
    @PUT
    @Produces({"application/json"})
    @Path("/submit")
    public Boolean registerStudentForSchoolClass(@Context SecurityContext sc, RestNewSchoolClass4Student restSchoolClass) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.STUDENT);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access student functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
        DomNewSchoolClass4Student q = restSchoolClass.getDomNewSchoolClass4Student();
        PersistenceId id = q.getId();
        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity((Long) MySQLPersistenceId.getId(id));

        if (phr != null && schoolClass != null && schoolClass.getSchoolID().equals(school.getSchoolID())
                && (schoolClass.getRegistrationKey() == null || schoolClass.getRegistrationKey().equals(restSchoolClass.getDomNewSchoolClass4Student().getRegistrationKey()))) {
            try {
                PersistentStudentOfClassPK socId = new PersistentStudentOfClassPK(phr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID());
                PersistentStudentOfClass soc = new PersistentStudentOfClass();
                soc.setPersistentStudentOfClassPK(socId);
                soc.setRegisterDate(DwoDateUtilities.getCurrentDwoDate());
                StudentOfClassManager.create(soc);
            }
            catch (PersistenceException e) {
                return false;
            }
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to remove self from a schoolclass id {1} while one or both do not exists or are not in the same school.", new Object[]{sc.getUserPrincipal().getName(), schoolClass.getClassID()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to submit yourself to this school class.");
        }

        return true;
    }

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getList")
    public List<DomSchoolClass> getStudentsSchoolClasses(@Context SecurityContext sc) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.STUDENT);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }

        if (phr != null && school != null) {
            List<DomSchoolClass> domSchoolClasses;
            try {
                List<PersistentStudentOfClass> tocList = StudentOfClassManager.findEntities(phr.getPersistentHasRolePK());
                domSchoolClasses = new ArrayList<DomSchoolClass>(tocList.size());
                for (PersistentStudentOfClass toc : tocList) {
                    PersistentSchoolClass s = SchoolClassManager.findEntity(toc.getPersistentStudentOfClassPK().getClassID());
                    domSchoolClasses.add(new DomSchoolClass(s));
                }
                LOG.log(Level.FINER, "Fetched all {0} schoolClasses of student {1]. ", new Object[]{domSchoolClasses.size(), phr.getPersistentHasRolePK().getUserID()});
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
            }
            return domSchoolClasses;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access student functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getSchoolsList")
    public List<DomSchoolClass> getSchoolsClasses(@Context SecurityContext sc) {
        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            phr = HasRoleUtilManager.getCurrentHasRole(sc.getUserPrincipal().getName(), RoleType.STUDENT);
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new Dwo2RestException(ex);
        }

        if (phr != null && school != null) {
            List<PersistentSchoolClass> scList;
            List<DomSchoolClass> domList;
            try {
                scList = SchoolClassManager.findEntities(school);
                LOG.log(Level.FINER, "Fetched all {0} schoolClasses of school {1]. ", new Object[]{scList.size(), school.getSchoolID()});
                domList = new ArrayList<DomSchoolClass>(scList.size());
                for (PersistentSchoolClass schoolClass : scList) {
                    domList.add(new DomSchoolClass(schoolClass));
                }
            }
            catch (Exception e) {
                LOG.log(Level.WARNING, "Unexpected exception", e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
            }
            return domList;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access student functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }
}
