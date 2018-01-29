package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentStudentScoContext;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClass;
import fi.dwo.commons.persistence.entities.PersistentTeacherOfClassPK;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.ScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import static nl.uu.fi.dwo.rest.dom.entities.RoleType.STUDENT;
import static nl.uu.fi.dwo.rest.dom.entities.RoleType.TEACHER;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

/**
 * Builder to retrieve persistence data in a cascading way an verify access and
 * dynamic model rules. This builder is fluid builder. Technically the class
 * forms a state machine where the interfaces denote the possible transitions
 * (edges in a directed graph). Thus a regular language for the security access
 * can be built.
 *
 * @author G.A.J. van der Plas
 */
public class TeacherDomainAuthorizer extends SchoolAdminTeacherDomainAuthorizer {
//
//    private static final Logger LOG = Logger.getLogger(TeacherDomainAuthorizer.class.getName());
//
//    private TeacherPersistentContext context = new TeacherPersistentContext();
//
//    public class TeacherPersistentContext extends SchoolAdminTeacherPersistentContext {
//        private PersistentSchoolClass schoolClass;
//        private PersistentDwoProfile profile;
//        private PersistentCourse course;
//        private PersistentClassCourse classCourse;
//        private PersistentScoContext scoContext;
//    }
//
//    private TeacherDomainAuthorizer() {
//        super();
//    }
//
//
//    public interface TeacherState_C_CC_HR_P_R_S_SC_SCO_SG_U {
//
//        //Build getContext();
//        PersistentUser getUser();
//
//        PersistentHasRole getHasRole();
//
//        RoleType getRoleType();
//
//        PersistentSchool getSchool();
//
//        PersistentSchoolGroup getSchoolGroup();
//
//        PersistentSchoolClass getSchoolClass();
//
//        PersistentDwoProfile getDwoProfile();
//
//        PersistentCourse getCourse();
//
//        PersistentClassCourse getClassCourse();
//
//        PersistentScoContext getScoContext();
//
//        Boolean removeStudentScoforClassAndCourse() throws Dwo2Exception;
//
//        Boolean removeStudentScoWithClassCourse() throws Dwo2Exception;
//
//        PersistentScoData getScoData() throws Dwo2Exception;
//    }
//
//    public interface TeacherState_HR_P_R_S_SC_SG_U {
//
//        //Build getContext();
//        PersistentUser getUser();
//
//        PersistentHasRole getHasRole();
//
//        RoleType getRoleType();
//
//        PersistentSchool getSchool();
//
//        PersistentSchoolGroup getSchoolGroup();
//
//        PersistentSchoolClass getSchoolClass();
//
//        PersistentDwoProfile getDwoProfile();
//
//        TeacherState_C_CC_HR_P_R_S_SC_SG_U addCourse(DomCourse c) throws Dwo2Exception;
//
//        TeacherState_C_CC_HR_P_R_S_SC_SCO_SG_U addScoContext(DomScoContext s) throws Dwo2Exception;
//    }
//
//    public interface TeacherState_C_CC_HR_P_R_S_SC_SG_U {
//
//        //Build getContext();
//        PersistentUser getUser();
//
//        PersistentHasRole getHasRole();
//
//        RoleType getRoleType();
//
//        PersistentSchool getSchool();
//
//        PersistentSchoolGroup getSchoolGroup();
//
//        PersistentSchoolClass getSchoolClass();
//
//        PersistentDwoProfile getDwoProfile();
//
//        PersistentCourse getCourse();
//
//        PersistentClassCourse getClassCourse();
//
//        TeacherState_C_CC_HR_P_R_S_SC_SCO_SG_U addScoContext(DomScoContext s) throws Dwo2Exception;
//    }
//
//
//    public interface TeacherState_HR_R_S_SC_SG_U {
//        PersistentUser getUser();
//
//        PersistentHasRole getHasRole();
//
//        RoleType getRoleType();
//
//        PersistentSchool getSchool();
//
//        PersistentSchoolGroup getSchoolGroup();
//
//        PersistentSchoolClass getSchoolClass();
//
//        TeacherState_HR_P_R_S_SC_SG_U addProfile(DomDwoProfile p) throws Dwo2Exception;
//    }
//    
//    public interface Build {
//
//        PersistentHasRole getHasRole();
//
//        PersistentUser getUser();
//
//        PersistentCourse getCourse();
//
//        PersistentSchool getSchool();
//
//        PersistentSchoolClass getSchoolClass();
//
//        PersistentDwoProfile getDwoProfile();
//
//        RoleType getRoleType();
//    }
//
//    private static class Builder implements TeacherState_HR_R_S_SC_SG_U, TeacherState_HR_P_R_S_SC_SG_U, TeacherState_C_CC_HR_P_R_S_SC_SG_U, TeacherState_C_CC_HR_P_R_S_SC_SCO_SG_U, Build {
//
//        private TeacherDomainAuthorizer instance = new TeacherDomainAuthorizer();
//
//        public Builder(String username) throws Dwo2Exception {
//            this.user(username);
//        }
//
//
//        /**
//         * Verifies the existence of the schoolClass for the data in the context
//         * and adds it and the school to the context.
//         *
//         * @param s
//         * @return
//         * @throws Dwo2Exception
//         */
//        @Override
//        public TeacherState_HR_R_S_SC_SG_U addSchoolClass(DomSchoolClass s) throws Dwo2Exception {
//            //fetch school
//            PersistentSchool school = HasRoleUtilManager.getSchoolforHasRole(this.instance.context.hasRole);
//            if (school == null) {
//                String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: School for used HasRole does not exists.", new Object[]{instance.context.user.getUsername()});
//                LOG.log(Level.WARNING, msg);
//                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
//            }
//            this.instance.context.school = school;
//
//            //verify if schoolClass is in school
//            Long classID = MySQLPersistenceId.getNativeId(s);
//            PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(classID);
//            if (schoolClass == null || !schoolClass.getSchoolID().equals(school.getSchoolID())) {
//                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Active schoolClass {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{instance.context.user.getUsername(), school.getSchoolID(), (schoolClass != null) ? schoolClass.getClassID() : null});
//                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.context.user.getUsername() + ".");
//            }
//            //verify if roleType is in SchoolClass.
//            switch (this.getRoleType()) {
//                case TEACHER:
//                    PersistentTeacherOfClass toc = TeacherOfClassManager.findEntity(
//                            new PersistentTeacherOfClassPK(this.instance.context.hasRole.getPersistentHasRolePK().getUserID(),
//                                    schoolClass.getClassID(), this.instance.context.hasRole.getPersistentHasRolePK().getSchoolGroupID()));
//                    if (!toc.getPersistentTeacherOfClassPK().getClassID().equals(schoolClass.getClassID())) {
//                        //invalid schoolClass
//                        String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Not a teacher in the SchoolClass.", new Object[]{instance.context.user.getUsername()});
//                        LOG.log(Level.WARNING, msg);
//                        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
//                    }
//                    break;
//                case STUDENT:
//                    PersistentStudentOfClass soc = StudentOfClassManager.findEntity(
//                            new PersistentStudentOfClassPK(this.instance.context.hasRole.getPersistentHasRolePK().getUserID(),
//                                    schoolClass.getClassID(), this.instance.context.hasRole.getPersistentHasRolePK().getSchoolGroupID()));
//                    if (!soc.getPersistentStudentOfClassPK().getClassID().equals(schoolClass.getClassID())) {
//                        //invalid schoolClass
//                        String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Not a student in the SchoolClass.", new Object[]{instance.context.user.getUsername()});
//                        LOG.log(Level.WARNING, msg);
//                        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
//                    }
//                    break;
//                default:
//                    String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Invalid roleType for setting a SchoolClass.", new Object[]{instance.context.user.getUsername()});
//                    LOG.log(Level.WARNING, msg);
//                    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
//            }
//
//            this.instance.context.schoolClass = schoolClass;
//            return this;
//        }
//
//        /**
//         * Verifies the existence of the profile for the data in the context and
//         * adds it to the context.
//         *
//         * @param p
//         * @return
//         * @throws Dwo2Exception
//         */
//        @Override
//        public TeacherState_HR_P_R_S_SC_SG_U addProfile(DomDwoProfile p) throws Dwo2Exception {
//            //fetch profile
//            Long profileId = MySQLPersistenceId.getNativeId(p);
//            PersistentDwoProfile profile = DwoProfileManager.findEntity(profileId);
//            if (profile == null) {
//                String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Profile {1} does not exists.", new Object[]{instance.context.user.getUsername(), p.getId()});
//                LOG.log(Level.WARNING, msg);
//                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
//            }
//            this.instance.context.profile = profile;
//            return this;
//        }
//
//        /**
//         * Verifies the existence of the course for the data in the context and
//         * adds it to the context.
//         *
//         * @param c
//         * @return
//         * @throws Dwo2Exception
//         */
//        @Override
//        public TeacherState_C_CC_HR_P_R_S_SC_SG_U addCourse(DomCourse c) throws Dwo2Exception {
//            Long courseId = MySQLPersistenceId.getNativeId(c);
//            PersistentCourse course = CourseManager.findEntity(courseId);
//            if (course == null || course.getDwoProfileID() != instance.context.profile.getDwoProfileID()) {
//                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested course {2} is not available in the profile {1} with usercode {0}.", new Object[]{this.instance.context.user.getUsername(), instance.context.profile.getDwoProfileID(), c.getId()});
//                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.context.user.getUsername() + ".");
//            }
//            //verify if course is in school
//            if (course.getSchoolID() != null && !course.getSchoolID().equals(instance.context.school.getSchoolID())) {
//                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested course {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{this.instance.context.user.getUsername(), instance.context.school.getSchoolID(), (course != null) ? course.getSchoolID() : "course==null"});
//                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.context.user.getUsername() + ".");
//            }
//
//            this.instance.context.course = (course);
//            return this;
//        }
//
//        /**
//         *
//         * @param s
//         * @return
//         * @throws Dwo2Exception
//         */
//        @Override
//        public TeacherState_C_CC_HR_P_R_S_SC_SCO_SG_U addScoContext(DomScoContext s) throws Dwo2Exception {
//            if (s == null) {
//                String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: ScoContext {1} not set.", new Object[]{instance.context.user.getUsername(), s.getId()});
//                LOG.log(Level.WARNING, msg);
//                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
//            }
//            //fetch course and class course from sco
//            PersistentScoContext sco = ScoContextManager.findEntity(MySQLPersistenceId.getNativeId(s));
//            if (sco == null) {
//                String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: ScoContext {1} not found.", new Object[]{instance.context.user.getUsername(), s.getId()});
//                LOG.log(Level.WARNING, msg);
//                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
//            }
//            if (instance.context.course == null) {
//                PersistentCourse c = CourseManager.findEntity(sco.getCourseID());
//                if (c == null || !c.getDwoProfileID().equals(instance.context.profile.getDwoProfileID())) {
//                    String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Course {1} not found.", new Object[]{instance.context.user.getUsername(), c.getCourseID()});
//                    LOG.log(Level.WARNING, msg);
//                    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
//                }
//
//                List<PersistentClassCourse> ccList = ClassCourseManager.findEntities(instance.context.schoolClass, c);
//                if (ccList.size() == 0) {
//                    String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: ClassCourse {1} not found.", new Object[]{instance.context.user.getUsername(), c.getCourseID()});
//                    LOG.log(Level.WARNING, msg);
//                    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
//                }
//                instance.context.classCourse = ccList.get(0);
//                instance.context.course = c;
//            }
//            instance.context.scoContext = sco;
//            return this;
//        }
//
//        @Override
//        public Boolean removeStudentScoforClassAndCourse() throws Dwo2Exception {
//            List<PersistentStudentOfClass> socList = StudentOfClassManager.findEntities(instance.context.schoolClass);
//            for (PersistentStudentOfClass soc : socList) {
//                PersistentHasRolePK key = new PersistentHasRolePK(soc.getPersistentStudentOfClassPK().getUserID(), soc.getPersistentStudentOfClassPK().getSchoolGroupID());
//                List<PersistentStudentScoContext> sscList = StudentScoContextManager.findEntities(instance.context.scoContext, key);
//                for (PersistentStudentScoContext ssc : sscList) {
//                    String msg = MessageFormat.format("Username {0} is learing studentSco id {1} for userid  {2} schoolgroupid {3} and course {4} {5}.", new Object[]{instance.context.user.getUsername(), ssc.getScoID(), ssc.getPersistentHasRolePK().getUserID(), ssc.getPersistentHasRolePK().getSchoolGroupID(), instance.context.course.getCourseID(), instance.context.course.getName()});
//                    LOG.log(Level.INFO, msg);
//                    StudentScoDataManager.destroy(ssc.getStudentSco());
//                    StudentScoContextManager.destroy(ssc.getStudentSco());
//                }
//            }
//            return true;
//        }
//
//        /**
//         * Removes StudentSco and corresponding ClassCourses. 
//         * 
//         * @return
//         * @throws Dwo2Exception 
//         */
//        @Override
//        public Boolean removeStudentScoWithClassCourse() throws Dwo2Exception {
//            List<PersistentStudentOfClass> socList = StudentOfClassManager.findEntities(instance.context.schoolClass);
//            //detach classcourse to ensure no new results occur.
//            //TODO mark marked for deleted in the future.
//            ClassCourseManager.editViewState(this.getClassCourse().getClassCourseID(), ViewState.invisible);
//            //clean all existing results
//            for (PersistentStudentOfClass soc : socList) {
//                PersistentHasRolePK key = new PersistentHasRolePK(soc.getPersistentStudentOfClassPK().getUserID(), soc.getPersistentStudentOfClassPK().getSchoolGroupID());
//                List<PersistentStudentScoContext> sscList = StudentScoContextManager.findEntities(instance.context.scoContext, key);
//                for (PersistentStudentScoContext ssc : sscList) {
//                    String msg = MessageFormat.format("Username {0} is learing studentSco id {1} for userid  {2} schoolgroupid {3} and course {4} {5}.", new Object[]{instance.context.user.getUsername(), ssc.getScoID(), ssc.getPersistentHasRolePK().getUserID(), ssc.getPersistentHasRolePK().getSchoolGroupID(), instance.context.course.getCourseID(), instance.context.course.getName()});
//                    LOG.log(Level.INFO, msg);
//                    StudentScoDataManager.destroy(ssc.getStudentSco());
//                    StudentScoContextManager.destroy(ssc.getStudentSco());
//                }
//            }
//            //remove classcourse to ensure no new attachments occur.
//            ClassCourseManager.destroy(this.getClassCourse().getClassCourseID());
//            return true;
//        }
//        
//        @Override
//        public PersistentScoData getScoData() throws Dwo2Exception {
//            if (instance.context.scoContext != null) {
//                return ScoDataManager.findEntity(instance.context.scoContext.getScoID());
//            } else {
//                String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: ScoContext not set.", new Object[]{instance.context.user.getUsername()});
//                LOG.log(Level.WARNING, msg);
//                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
//            }
//        }
//
//        @Override
//        public PersistentCourse getCourse() {
//            return instance.context.course;
//        }
//
//        @Override
//        public PersistentSchoolClass getSchoolClass() {
//            return instance.context.schoolClass;
//        }
//
//        @Override
//        public PersistentClassCourse getClassCourse() {
//            return instance.context.classCourse;
//        }
//
//        @Override
//        public PersistentScoContext getScoContext() {
//            return instance.context.scoContext;
//        }
//
//        @Override
//        public PersistentDwoProfile getDwoProfile() {
//            return instance.context.profile;
//        }
//
//    }
}
