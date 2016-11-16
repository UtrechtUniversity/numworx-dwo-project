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
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolClassUtilManager;
import fi.dwo.server.PersistentDataManagers.util.UserUtilManager;
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
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.entities.RestContext;
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
     * Returns the school data to be displayed.
     *
     * @param sc
     * @return
     */
    @PUT
    @Produces({"application/json"})
    @Path("/getTeachersResults")
    public DomResultsPerTeacher getTeachersResults(@Context SecurityContext sc, RestContext aContext) {

        DomContext context = aContext.getRestContext();
        DomHasRole domHasRole = context.getDomHasRole();

        if(domHasRole==null){
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "User "+sc.getUserPrincipal().getName() + "didn't submit a hasRole in his RestContext.");
        }
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
            // DomResultsPerTeacher requires teacher, schoolclasses, students, 
            // courses, scocontext's, studentscocontext's and a timestamp

            //Fetch Teacher
            DomTeacher teacher = UserManager.findEntity(phr.getPersistentHasRolePK().getUserID()).buildDomTeacher();
            results.setTeacher(teacher);
            
            //Fetch SchoolClasses and students
            List<PersistentSchoolClass> schoolClasses = SchoolClassUtilManager.getSchoolClassesOfTeacher(phr);
            Map<PersistenceId, DomSchoolClass> domSchoolClasses = new HashMap<>(schoolClasses.size());
            HashMap<PersistentStudentOfClassPK, PersistentStudentOfClass> socMap = new HashMap<>();
            HashMap<Long, PersistentUser> studentMap = new HashMap<>();
            schoolClasses.stream().map((schoolClass) -> {
                DomSchoolClass s = schoolClass.buildDomSchoolClass();
                domSchoolClasses.putIfAbsent(s.getId(), s);
                return schoolClass;
            }).forEach((schoolClass) -> {
                try {
                    for (PersistentStudentOfClass soc : StudentOfClassManager.findEntities(schoolClass)) {
                        socMap.putIfAbsent(soc.getPersistentStudentOfClassPK(), soc);
                        PersistentUser user = UserManager.findEntity(soc.getPersistentStudentOfClassPK());
                        studentMap.putIfAbsent(user.getId(), user);
                    }
                    //TODO optimize: remove the multiple user fetches.
                    for (PersistentUser user : UserUtilManager.getUsersforStudentsInSchoolClass(schoolClass)) {
                        studentMap.putIfAbsent(user.getId(), user);
                    }
                } catch (Dwo2Exception ex) {
                    Logger.getLogger(SecuredTeacherResultsManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            results.setSchoolClasses(domSchoolClasses);
            
            //convert studentMap and set in result
            Map<PersistenceId, DomStudent> domStudents = new HashMap<>(studentMap.size());
            studentMap.entrySet().stream().forEach((keyValuePair) -> {
                DomStudent s = keyValuePair.getValue().buildDomStudent();
                domStudents.put(s.getId(), s);
            });
            results.setStudents(domStudents);

            //Fetch courses for all classes ClassCourses. No filtering occurs on
            //CourseType, notBefore and notAfter for results.
            //
            //Note that course are not always leaves and all subcourses are not
            //indexed. Create a Courses Map to recurse until Sco leaves
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
            Map<PersistenceId, DomClassCourse> domClassCourses = new HashMap<>(classCoursesMap.size());
            classCoursesMap.entrySet().forEach((keyValuePair) -> {
                DomClassCourse c = keyValuePair.getValue().buildDomClassCourse();
                domClassCourses.put(c.getId(), c);
            });
            results.setClassCourses(domClassCourses);

            Map<PersistenceId, DomCourse> domCourses = new HashMap<>();
            Queue<PersistentCourse> courseQueue = new LinkedList<>();
            List<PersistentCourse> leaves = new LinkedList<>();
            courseQueue.addAll(coursesMap.values());
            //Danger Will Robinson, circular reference will hang thread forever.
            //TODO Loop items in the map to determine the distance to the rootnode.
            //Set the treeindex depth and commit all that are found
            //if the child depth less or equal to the parent then there is an issue.
            //if child depth empty set it.
            
            //ensure sequence is always set.
            while (!courseQueue.isEmpty()) {
                PersistentCourse course = courseQueue.remove();
                if (course.getWithChildren()) {
                    List<PersistentCourse> childrenCourses = CourseManager.findChildrenOf(course);
//                    CourseManager.findEntities(school);
                    courseQueue.addAll(childrenCourses);
                } else {//leave
                    leaves.add(course);
                }
                DomCourse c = course.createDomCourse();
                domCourses.put(c.getId(), c);
            }
            results.setCourses(domCourses);

            //process leaves and fill hashmap scoContext
            HashMap<Long, PersistentScoContext> scosMap = new HashMap<>();
            leaves.forEach((leave) -> {
                List<PersistentScoContext> scoContexts = ScoContextManager.findEntities(leave);
                scoContexts.forEach((scoContext) -> {
                    scosMap.putIfAbsent(scoContext.getScoID(), scoContext);
                });
            });
            Map<PersistenceId, DomScoContext> domScoContexts = new HashMap<>(scosMap.size());
            scosMap.entrySet().stream().forEach((keyValuePair) -> {
                DomScoContext s = keyValuePair.getValue().buildDomScoContext();
                domScoContexts.put(s.getId(), s);
            });
            results.setScoContexts(domScoContexts);

            //fill hashmap studenSco
            HashMap<Long, PersistentStudentScoContext> studentScosMap = new HashMap<>();
            scosMap.entrySet().forEach((sco) -> {
                List<PersistentStudentScoContext> studentScos = StudentScoContextManager.findEntities(sco.getValue());
                studentScos.forEach((studentSco) -> {
                    studentScosMap.putIfAbsent(studentSco.getStudentSco(), studentSco);
                });
            });
            Map<PersistenceId, DomStudentScoContext> domStudentScoContexts = new HashMap<>(studentScosMap.size());
            studentScosMap.entrySet().stream().forEach((keyValuePair) -> {
                DomStudentScoContext s = keyValuePair.getValue().buildDomStudentScoContext();
                domStudentScoContexts.put(s.getId(), s);
            });
            results.setStudentScoContexts(domStudentScoContexts);

            return results;
            // recurse here using Java queue
        } else {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access teacher functionality by user with usercode {0}.", new Object[]{sc.getUserPrincipal().getName()});
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
        }
    }

}
