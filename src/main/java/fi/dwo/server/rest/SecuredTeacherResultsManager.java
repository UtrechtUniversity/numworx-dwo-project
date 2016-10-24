package fi.dwo.server.rest;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolClassUtilManager;
import fi.dwo.server.PersistentDataManagers.util.UserUtilManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
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
            // DomResultsPerTeacher
            // requires teacher, schoolclasses, students, courses, scocontext's, studentscocontext's and a timestamp

            //Fetch Teacher
            DomTeacher teacher = UserManager.findEntity(phr.getPersistentHasRolePK().getUserID()).buildDomTeacher();
            results.setTeacher(teacher);
            //Fetch SchoolClasses and students
            List<PersistentSchoolClass> schoolClasses = SchoolClassUtilManager.getSchoolClassesOfTeacher(phr);
            List<DomSchoolClass> domSchoolClasses = new ArrayList<DomSchoolClass>(schoolClasses.size());
            HashMap<Long, PersistentUser> studentMap = new HashMap<>();
            for (PersistentSchoolClass schoolClass : schoolClasses) {
                domSchoolClasses.add(schoolClass.createDomSchoolClass());
                try {
                    //TODO optimize: remove the multiple user fetches.
                    for (PersistentUser user : UserUtilManager.getUsersforStudentsInSchoolClass(schoolClass)) {
                        studentMap.putIfAbsent(user.getId(), user);
                    }
                } catch (Dwo2Exception ex) {
                    Logger.getLogger(SecuredTeacherResultsManager.class.getName()).log(Level.SEVERE, null, ex);
                };
            }
            //convert studentMap and set in result
            List<DomStudent> domStudents = new ArrayList<>(studentMap.size());
            studentMap.entrySet().stream().forEach((keyValuePair) -> {
                domStudents.add(keyValuePair.getValue().buildDomStudent());
            });
            results.setStudents(domStudents);

            //Fetch courses for all classes ClassCourses, note course are not always leaves and all subcourses are not indexed.
            //create a Courses Map to recurse until Sco leaves
            HashMap<Long, PersistentClassCourse> classCoursesMap = new HashMap<>();
            HashMap<Long, PersistentCourse> coursesMap = new HashMap<>();
            schoolClasses.stream().forEach((schoolClass) -> {
                List<PersistentClassCourse> ccList = ClassCourseManager.findEntities(schoolClass);
                ccList.forEach((classCourse) -> {
                    //push to class courses 
                    classCoursesMap.putIfAbsent(classCourse.getClassCourseID(), classCourse);
                    //push to courses map for recursive collection
                    coursesMap.putIfAbsent(classCourse.getClassCourseID(), CourseManager.findEntity(classCourse.getClassID()));
                });
            });

            //fill DomClassCourse List
            List<DomClassCourse> domClassCourses = new ArrayList<>(classCoursesMap.size());
            classCoursesMap.entrySet().forEach((keyValuePair) -> {
                domClassCourses.add(keyValuePair.getValue().createDomClassCourse());
            });

            Queue<PersistentCourse> courseQueue = new LinkedList<>();
            courseQueue.addAll(coursesMap.values());
            
            //pop and push course map until no kids.
//            HashMap<Long, PersistentCourse> courseMap = new HashMap<>();

            // recurse here using Java queue

//    for (Iterator<HashMap.Entry<Long, PersistentCourse>> iterator = courseMap.entrySet().iterator(); iterator.hasNext();) {
//        PersistentCourse tmpCourse = iterator.next().getValue();
        
//    if (course is leave) {
            // add sco to scomap, add course to comcourses, remove.
//        // Remove the current element from the iterator and the list.
//        iterator.remove();
//    }else{
// add children to coursemap
//    }            
              
            //Convert course map to course list
//            List<DomCourse> domCourses = new ArrayList<>(courseMap.size());
//            courseMap.entrySet().forEach((keyValuePair)->{
//                domClassCourses.add(keyValuePair.getValue().createDomCourse());
//            });
            
            //Convert sco map to course list and fetch studentsco's and add to studentsco list
            
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Under development.");

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
