package fi.dwo.server.rest;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolClassUtilManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.entities.RestContext;

/**
 * Operations for the GUI Component that manages the school classes.
 *
 * @see fi.dwo.dwojapplet.gui.panels.JPanel.MyProfile
 *
 * @author G.A.J. van der Plas
 */
@PermitAll
@Path("/secure/teacher/results")
public class SecuredTeacherResultsManager extends AbstractSchoolClassManager {

    private static final Logger LOG = Logger.getLogger(SecuredTeacherResultsManager.class.getName());

    /**
     * Returns the school data to be displayed.
     *
     * @param sc
     * @return
     */
    @GET
    @Produces({"application/json"})
    @Path("/getTeachersResults")
    public DomResultsPerTeacher getTeachersResults(@Context SecurityContext sc, RestContext aContext) {

        DomContext context = aContext.getRestContext();
        DomHasRole domHasRole = context.getDomHasRole();

        PersistentHasRole phr = null;
        PersistentSchool school = null;

        try {
            PersistentHasRolePK hasRoleKey = MySQLPersistenceId.extractHasRoleKey(domHasRole.getId());
            phr = HasRoleManager.findEntity(hasRoleKey);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "Username {0}: ILLEGAL USER-OPERATION: Trying to hack the persistentId.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }

        try {
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        if (phr != null && school != null) {
            DomResultsPerTeacher results = new DomResultsPerTeacher();
            //Fetch Teacher
            DomTeacher teacher = UserManager.findEntity(phr.getPersistentHasRolePK().getUserID()).buildDomTeacher();
            results.setTeacher(teacher);
            //Fetch SchoolClasses
            List<PersistentSchoolClass> schoolClasses = SchoolClassUtilManager.getSchoolClassesOfTeacher(phr);
            List<DomSchoolClass> domSchoolClasses = new ArrayList<DomSchoolClass>(schoolClasses.size());
            for(PersistentSchoolClass schoolClass : schoolClasses){
                domSchoolClasses.add(schoolClass.createDomSchoolClass());
            }            
            
            //Fetch courses for all classes ClassCourses, note course are not always leaves and all subcourses are not indexed.
            HashMap<Long,PersistentClassCourse> classCourses = new HashMap<>();
            schoolClasses.stream().forEach((schoolClass) -> {
                List<PersistentClassCourse> ccList =  ClassCourseManager.findEntities(schoolClass);
                ccList.forEach((classCourse) -> {
                   classCourses.putIfAbsent(classCourse.getClassCourseID() , classCourse);
                });
            });
            List<DomClassCourse> domClassCourses = new ArrayList<>(classCourses.size());
            classCourses.entrySet().forEach((keyValuePair)->{
                domClassCourses.add(keyValuePair.getValue().createDomClassCourse());
            });
            
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError,"Under development.");
            
            //Recurse all Courses into a map they are unique.
            
            //ScoContext's
            //StudentScoContext's
//            results.
            // fetch DomResultsPerTeacher data
            
//            List<DomSchoolClass> domSchoolClasses;
//            try {
//                List<PersistentTeacherOfClass> tocList = TeacherOfClassManager.findEntities(phr.getPersistentHasRolePK());
//                domSchoolClasses = new ArrayList<DomSchoolClass>(tocList.size());
//                for (PersistentTeacherOfClass toc : tocList) {
//                    PersistentSchoolClass s = SchoolClassManager.findEntity(toc.getPersistentTeacherOfClassPK().getClassID());
//                    domSchoolClasses.add(s.createDomSchoolClass());
//                }
//                LOG.log(Level.FINER, "Fetched all {0} schoolClasses of teacher {1]. ", new Object[]{domSchoolClasses.size(), phr.getPersistentHasRolePK().getUserID()});
//            } catch (Exception e) {
//                LOG.log(Level.WARNING, "Unexpected exception", e);
//                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "An exception occured while fetching the schoolclasses.");
//            }
//            return domSchoolClasses;
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

}
