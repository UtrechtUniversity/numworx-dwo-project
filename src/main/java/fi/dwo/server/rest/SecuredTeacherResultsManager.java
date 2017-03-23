package fi.dwo.server.rest;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.commons.util.DwoDateUtilities;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolClassUtilManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentOfClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

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
     * Returns the school data to be displayed. note
     *
     * @param sc
     * @param aProfile
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/getTeachersResults")
    public DomResultsPerTeacher getTeachersResults(@Context SecurityContext sc, RestDwoProfile aProfile) {

        DomDwoProfile domProfile = aProfile.getDomDwoProfile();
        DomContext context = aProfile.getRestContext();
        DomHasRole domHasRole = context.getDomHasRole();

        //check given role in RestContext
        if (domHasRole == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "User " + sc.getUserPrincipal().getName() + "didn't submit a hasRole in his RestContext.");
        } else if (domProfile == null) {
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "User " + sc.getUserPrincipal().getName() + "didn't submit a domProfile in his RestContext.");
        }
        PersistentHasRole phr = null;
        PersistentSchool school = null;
        final PersistentDwoProfile profile;

        try {
            PersistentHasRolePK hasRoleKey = MySQLPersistenceId.getNativeId(domHasRole);
            phr = HasRoleManager.findEntity(hasRoleKey);
            PersistentUser user = UserManager.findByUserName(sc.getUserPrincipal().getName());
            if (user == null || !user.getId().equals(phr.getPersistentHasRolePK().getUserID())) {
                LOG.log(Level.SEVERE, "Username {0}: ILLEGAL USER-OPERATION: Using uid {1} in HasRole differs from user principal name {0}.", new Object[]{sc.getUserPrincipal().getName(), user.getUsername()});
                throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
            }
            profile = DwoProfileManager.findEntity(MySQLPersistenceId.getNativeId(domProfile));
            if (profile == null) {
                LOG.log(Level.SEVERE, "Username {0}: ILLEGAL USER-OPERATION: Using unknown profileId {1} in HasRole differs from user principal name {0}.", new Object[]{sc.getUserPrincipal().getName(), domProfile.getId()});
                throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
            }
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        try {
            school = HasRoleUtilManager.getSchoolforHasRole(phr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }

        //fetch Profile
        if (phr != null && school != null) {
            DomResultsPerTeacher results = new DomResultsPerTeacher();
            results.setFetchTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
            // DomResultsPerTeacher requires a fetchTimeStamp, teacher, schoolclasses, 
            // studentsof, students, 
            // classcourses, courses, scocontext's and studentscocontext's.

            //Fetch teacher
            DomTeacher teacher = UserManager.findEntity(phr.getPersistentHasRolePK().getUserID()).buildDomTeacher();
            results.setTeacher(teacher);

            //Collect schoolClasses of the teacher
            List<PersistentSchoolClass> schoolClasses = SchoolClassUtilManager.getSchoolClassesOfTeacher(phr);
            HashMap<PersistenceId, DomSchoolClass> domSchoolClasses = new HashMap<>(schoolClasses.size());
            HashMap<PersistentStudentOfClassPK, PersistentStudentOfClass> socMap = new HashMap<>();
            HashMap<Long, PersistentUser> studentMap = new HashMap<>();
            //create the DomSchoolClasses
            schoolClasses.stream().map((schoolClass) -> {
                DomSchoolClass s = schoolClass.buildDomSchoolClass();
                domSchoolClasses.putIfAbsent(s.getId(), s);
                return schoolClass;
            }).forEach((schoolClass) -> {
                //And while at it, for each schoolClass fill the set of studentsOf and students
//                try {
                for (PersistentStudentOfClass soc : StudentOfClassManager.findEntities(schoolClass)) {
                    //add studentOf to set socMap
                    socMap.putIfAbsent(soc.getPersistentStudentOfClassPK(), soc);
                    //add user to set studentMap
                    PersistentUser user = UserManager.findEntity(soc.getPersistentStudentOfClassPK());
                    studentMap.putIfAbsent(user.getId(), user);
                }
                //TODO optimize: remove the multiple user fetches.
//                    for (PersistentUser user : UserUtilManager.getUsersforStudentsInSchoolClass(schoolClass)) {
//                        studentMap.putIfAbsent(user.getId(), user);
//                    }
//                } catch (Dwo2Exception ex) {
//                    Logger.getLogger(SecuredTeacherResultsManager.class.getName()).log(Level.SEVERE, null, ex);
//                }
            });
            //convert studentMap and set in result
            HashMap<PersistenceId, DomStudent> domStudents = new HashMap<>(studentMap.size());
            studentMap.entrySet().stream().forEach((keyValuePair) -> {
                DomStudent s = keyValuePair.getValue().buildDomStudent();
                domStudents.put(s.getId(), s);
            });
            List<DomMapEntry<PersistenceId, DomStudent>> entryList = new ArrayList<>(domStudents.size());
            domStudents.entrySet().stream().forEach((entry) -> {entryList.add(new DomMapEntry(entry));});
            results.setStudents(entryList);
            //convert StudentOfClass map (socMap) and set in result
            HashMap<PersistenceId, DomStudentOfClass> domSocs = new HashMap<>(socMap.size());
            socMap.entrySet().stream().forEach((keyValuePair) -> {
                DomStudentOfClass s = keyValuePair.getValue().buildDomStudentOfClass();
                domSocs.putIfAbsent(s.getId(), s);
            });
            List<DomMapEntry<PersistenceId, DomStudentOfClass>> socsList = new ArrayList<>(domSocs.size());
            domSocs.entrySet().stream().forEach((entry) -> {socsList.add(new DomMapEntry(entry));});
            results.setStudentsOfClasses(socsList);

            //Fetch courses for all classes ClassCourses. No filtering occurs on
            //CourseType, notBefore and notAfter for results. Filtering of Courses
            //occurs on Profile.
            HashMap<Long, PersistentClassCourse> classCoursesMap = new HashMap<>();
            HashMap<Long, PersistentCourse> coursesMap = new HashMap<>();
            schoolClasses.stream().forEach((schoolClass) -> {
                List<PersistentClassCourse> ccList = ClassCourseManager.findEntities(schoolClass);
                ccList.forEach((classCourse) -> {
                    //fetch course and check profile
                    PersistentCourse course = CourseManager.findEntity(classCourse.getClassID());
                    if (course != null && course.getDwoProfileID().equals(profile.getDwoProfileID())) {
                        //push to classCourses 
                        classCoursesMap.putIfAbsent(classCourse.getClassCourseID(), classCourse);
                        //push to courses map for recursive collection
                        coursesMap.putIfAbsent(course.getCourseID(), course);
                    }
                });
            });

            //fill DomClassCourse List
            HashMap<PersistenceId, DomClassCourse> domClassCourses = new HashMap<>(classCoursesMap.size());
            classCoursesMap.entrySet().forEach((keyValuePair) -> {
                DomClassCourse c = keyValuePair.getValue().buildDomClassCourse();
                domClassCourses.put(c.getId(), c);
            });
                        List<DomMapEntry<PersistenceId, DomClassCourse>> socsList = new ArrayList<>(domSocs.size());
            domSocs.entrySet().stream().forEach((entry) -> {socsList.add(new DomMapEntry(entry));});

            results.setClassCourses(new ArrayList<>(domClassCourses.entrySet()));

            HashMap<PersistenceId, DomCourse> domCourses = new HashMap<>();
            Map<Long, PersistentCourse> courses = new HashMap<>();
            Queue<PersistentCourse> courseQueue = new LinkedList<>();
            Map<Long, PersistentCourse> leaves = new HashMap<>();
            courseQueue.addAll(coursesMap.values());//note this is a set!

            //Danger Will Robinson, circular reference will hang thread forever.
            //hence map and queue approach and a Depth First Search queue approach. 
            //Trying to get the queue empty.
            while (!courseQueue.isEmpty()) {
                PersistentCourse course = courseQueue.remove();
                PersistentCourse r = courses.putIfAbsent(course.getCourseID(), course);
                if (r==null && course.isWithChildren()) {
                    //put current course in the courseMap
                    //put kids on the queue
                    List<PersistentCourse> childrenCourses = CourseManager.findChildrenOf(course);
                    //add courses to a map 
                    //if not in map add to queue
                    courseQueue.addAll(childrenCourses);
                } else {//course is aleave
                    leaves.putIfAbsent(course.getCourseID(), course);
                }
            }
            //export courses to result.
            courses.entrySet().stream().forEach((keyValuePair) -> {
                DomCourse c = keyValuePair.getValue().buildDomCourse();
                domCourses.putIfAbsent(c.getId(), c);
            });
            results.setCourses(new ArrayList<>(domCourses.entrySet()));

            //process leaves and fill hashmap scoContext
            HashMap<Long, PersistentScoContext> scosMap = new HashMap<>();
            leaves.entrySet().stream().forEach((keyValuePair) -> {
                List<PersistentScoContext> scoContexts = ScoContextManager.findEntities(keyValuePair.getValue());
                scoContexts.forEach((scoContext) -> {
                    scosMap.putIfAbsent(scoContext.getScoID(), scoContext);
                });
            });
            HashMap<PersistenceId, DomScoContext> domScoContexts = new HashMap<>(scosMap.size());
            scosMap.entrySet().stream().forEach((keyValuePair) -> {
                DomScoContext s = keyValuePair.getValue().buildDomScoContext();
                domScoContexts.put(s.getId(), s);
            });
            results.setScoContexts(new ArrayList<>(domScoContexts.entrySet()));

            //fill hashmap studenSco
            HashMap<Long, PersistentStudentScoContext> studentScosMap = new HashMap<>();
            scosMap.entrySet().forEach((sco) -> {
                List<PersistentStudentScoContext> studentScos = StudentScoContextManager.findEntities(sco.getValue());
                studentScos.forEach((studentSco) -> {
                    studentScosMap.putIfAbsent(studentSco.getStudentSco(), studentSco);
                });
            });
            HashMap<PersistenceId, DomStudentScoContext> domStudentScoContexts = new HashMap<>(studentScosMap.size());
            studentScosMap.entrySet().stream().forEach((keyValuePair) -> {
                DomStudentScoContext s = keyValuePair.getValue().buildDomStudentScoContext();
                domStudentScoContexts.put(s.getId(), s);
            });
            results.setStudentScoContexts(new ArrayList<>(domStudentScoContexts.entrySet()));

//            //test null returns
//            if(results.getClassCourses().isEmpty()){
//                results.setClassCourses(null);
//            }
//            if(results.getCourses().isEmpty()){
//                results.setCourses(null);
//            }
//            if(results.getSchoolClasses().isEmpty()){
//                results.setSchoolClasses(null);
//            }
//            if(results.getScoContexts().isEmpty()){
//                results.setScoContexts(null);
//            }
//            if(results.getStudents().isEmpty()){
//                results.setStudents(null);
//            }
//            if(results.getStudentsOfClasses().isEmpty()){
//                results.setStudentsOfClasses(null);
//            }
            return results;
            // recurse here using Java queue
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

}
