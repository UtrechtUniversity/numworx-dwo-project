package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.commons.persistence.entities.PersistentClassCourse;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentScoData;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.SchoolAdminTeacherDomainAuthorizer.SchoolAdminTeacherPersistentContext;
import fi.dwo.server.PersistentDataManagers.actions.MySQLTeacherActions;
import fi.dwo.server.PersistentDataManagers.actions.TeacherActions;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.core.UriInfo;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextPatch;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 * Builder to retrieve persistence data in a cascading way an verify access and
 * dynamic model rules. This builder is fluid builder. Technically the class
 * forms a state machine where the interfaces denote the possible transitions
 * (edges in a directed graph). Thus a regular language for the security access
 * can be built.
 *
 * @author G.A.J. van der Plas
 */
public class TeacherDomainAuthorizer {

    private static final Logger LOG = Logger.getLogger(TeacherDomainAuthorizer.class.getName());

    private Context context;
    protected TeacherActions teacherActions = new MySQLTeacherActions();

    public static class Context {

        private AnonDomainAuthorizer.AnonPersistentContext anonCtx;
        private UserDomainAuthorizer.UserPersistentContext userCtx;
        private SchoolAdminTeacherDomainAuthorizer.SchoolAdminTeacherPersistentContext schooladminTeacherCtx;
        private TeacherPersistentContext teacherCtx;

        public Context(SchoolAdminTeacherDomainAuthorizer.Context ctx) {
            this.anonCtx = ctx.getAnonCtx();
            this.userCtx = ctx.getUserCtx();
            this.schooladminTeacherCtx = ctx.getSchooladminTeacherCtx();
            teacherCtx = new TeacherPersistentContext();
        }

        /**
         * @return the anonCtx
         */
        public AnonDomainAuthorizer.AnonPersistentContext getAnonCtx() {
            return anonCtx;
        }

        /**
         * @return the userCtx
         */
        public UserDomainAuthorizer.UserPersistentContext getUserCtx() {
            return userCtx;
        }

        /**
         * @param anonCtx the anonCtx to set
         */
        public void setAnonCtx(AnonDomainAuthorizer.AnonPersistentContext anonCtx) {
            this.anonCtx = anonCtx;
        }

        /**
         * @param userCtx the userCtx to set
         */
        public void setUserCtx(UserDomainAuthorizer.UserPersistentContext userCtx) {
            this.userCtx = userCtx;
        }

        /**
         * @return the schooladminTeacherCtx
         */
        public SchoolAdminTeacherPersistentContext getSchooladminTeacherCtx() {
            return schooladminTeacherCtx;
        }

        /**
         * @param schooladminTeacherCtx the schooladminTeacherCtx to set
         */
        public void setSchooladminTeacherCtx(SchoolAdminTeacherPersistentContext schooladminTeacherCtx) {
            this.schooladminTeacherCtx = schooladminTeacherCtx;
        }

        /**
         * @return the teacherCtx
         */
        public TeacherPersistentContext getTeacherCtx() {
            return teacherCtx;
        }

        /**
         * @param teacherCtx the teacherCtx to set
         */
        protected void setTeacherCtx(TeacherPersistentContext teacherCtx) {
            this.teacherCtx = teacherCtx;
        }

    }

    public static class TeacherPersistentContext {

        public TeacherPersistentContext() {
            super();
        }

        protected PersistentSchoolClass schoolClass;
        protected PersistentDwoProfile profile;
        protected PersistentCourse course;
        protected PersistentClassCourse classCourse;
        protected PersistentScoContext scoContext;

        /**
         * @return the schoolClass
         */
        public PersistentSchoolClass getSchoolClass() {
            return schoolClass;
        }

        /**
         * @param schoolClass the schoolClass to set
         */
        public void setSchoolClass(PersistentSchoolClass schoolClass) {
            this.schoolClass = schoolClass;
        }

        /**
         * @return the profile
         */
        public PersistentDwoProfile getProfile() {
            return profile;
        }

        /**
         * @param profile the profile to set
         */
        public void setProfile(PersistentDwoProfile profile) {
            this.profile = profile;
        }

        /**
         * @return the course
         */
        public PersistentCourse getCourse() {
            return course;
        }

        /**
         * @param course the course to set
         */
        public void setCourse(PersistentCourse course) {
            this.course = course;
        }

        /**
         * @return the classCourse
         */
        public PersistentClassCourse getClassCourse() {
            return classCourse;
        }

        /**
         * @param classCourse the classCourse to set
         */
        public void setClassCourse(PersistentClassCourse classCourse) {
            this.classCourse = classCourse;
        }

        /**
         * @return the scoContext
         */
        public PersistentScoContext getScoContext() {
            return scoContext;
        }

        /**
         * @param scoContext the scoContext to set
         */
        public void setScoContext(PersistentScoContext scoContext) {
            this.scoContext = scoContext;
        }
    }

    protected TeacherDomainAuthorizer() {
        super();
    }

    public interface TeacherState_C_CC_HR_P_R_S_SC_SCO_SG_U {

        //Build getContext();
        PersistentUser getUser();

        PersistentHasRole getHasRole();

        RoleType getRoleType();

        PersistentSchool getSchool();

        PersistentSchoolGroup getSchoolGroup();

        PersistentSchoolClass getSchoolClass();

        PersistentDwoProfile getDwoProfile();

        PersistentCourse getCourse();

        PersistentClassCourse getClassCourse();

        PersistentScoContext getScoContext();

        Boolean removeStudentScoforClassAndCourse() throws Dwo2Exception;

        Boolean removeStudentScoWithClassCourse() throws Dwo2Exception;

        PersistentScoData getScoData() throws Dwo2Exception;
    }

    public interface TeacherState_HR_P_R_S_SC_SG_U {

        //Build getContext();
        PersistentUser getUser();

        PersistentHasRole getHasRole();

        RoleType getRoleType();

        PersistentSchool getSchool();

        PersistentSchoolGroup getSchoolGroup();

        PersistentSchoolClass getSchoolClass();

        PersistentDwoProfile getDwoProfile();

        TeacherState_C_CC_HR_P_R_S_SC_SG_U addCourse(DomCourse c) throws Dwo2Exception;

        TeacherState_C_CC_HR_P_R_S_SC_SCO_SG_U addScoContext(DomScoContext s) throws Dwo2Exception;
    }

    public interface TeacherState_C_CC_HR_P_R_S_SC_SG_U {

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
        PersistentSchoolClass getSchoolClass();

        PersistentDwoProfile getDwoProfile();

        PersistentCourse getCourse();

        PersistentClassCourse getClassCourse();

        TeacherState_C_CC_HR_P_R_S_SC_SCO_SG_U addScoContext(DomScoContext s) throws Dwo2Exception;
        
        Boolean attachCourseToClass()  throws Dwo2Exception;
        
        Boolean addCourseToClass(CourseType courseType, Date from, Date to, String accessKey)  throws Dwo2Exception;
        
        Boolean detachCourseFromClass()  throws Dwo2Exception;
    }

    public interface TeacherState_HR_R_S_SC_SG_U {

        PersistentUser getUser();

        PersistentHasRole getHasRole();

        RoleType getRoleType();

        PersistentSchool getSchool();

        PersistentSchoolGroup getSchoolGroup();

        PersistentSchoolClass getSchoolClass();

        TeacherState_HR_R_S_SC_SG_U addStudent(DomSchoolClass sc, DomStudent s) throws Dwo2Exception;

        //       TeacherState_HR_R_S_SC_SG_U moveStudent(DomSchoolClass sc, DomStudent s);
    }

    public interface TeacherState_HR_R_S_SG_U {

        List<DomStudentModelContext> getStudentModels() throws Dwo2Exception;
        DomStudentModelContext getStudentModel(DomStudentModelContextId id) throws Dwo2Exception;

        TeacherState_HR_P_R_S_SG_U addProfile(DomDwoProfileId domDwoProfileId) throws Dwo2Exception;

        List<DomSchoolClass> getSchoolClasses() throws Dwo2Exception;

        List<DomStudent> getTeachersStudents() throws Dwo2Exception;

        List<DomSchoolClassId> getTeachersClassesOfStudent(DomStudent student) throws Dwo2Exception;

        List<DomSchoolClassId> getSharedTeacherClasses(DomTeacher aTeacher) throws Dwo2Exception;

        DomStudentModelContext updateStudentModel(DomStudentModelContext domStudentModelContext) throws Dwo2Exception;

        Boolean removeStudentModel(DomStudentModelContext domStudentModelContext) throws Dwo2Exception;

		DomStudentModelScorePerTeacher getScores(DomStudentModelScorePerTeacher domStudentModelScorePerTeacher, UriInfo info) throws Dwo2Exception;

        DomLRS getLRS(UriInfo info);
        @Deprecated
		DomStudentModelContext patchStudentModel(DomStudentModelContextPatch domPatch) throws Dwo2Exception;
		List<DomStudentModelContext> getMergedStudentModels() throws Dwo2Exception;
//		List<DomStudentModelContext> getReducedStudentModels() throws Dwo2Exception;

//        TeacherState_HR_R_S_SC_SG_U addASchoolClass(DomSchoolClass schoolClassFrom)  throws Dwo2Exception;
    }

    public interface TeacherState_HR_P_R_S_SG_U {

        TeacherState_HR_P_R_S_SC_SG_U addSchoolClass(DomSchoolClass s) throws Dwo2Exception;
        List<DomSchoolClass> getSchoolClasses() throws Dwo2Exception;
		List<DomStudentModelContext> getReducedStudentModels() throws Dwo2Exception;
		DomStudentModelContext addStudentModel(DomStudentModelContext domStudentModelContext) throws Dwo2Exception;
		List<DomMethod> getMethods() throws Dwo2Exception;
		DomMethod addMethod(DomMethod domMethod);
		PersistentDwoProfile getDwoProfile();
		DomStudentModelContext patchStudentModel(DomStudentModelContextPatch domPatch) throws Dwo2Exception;
		DomStudentModelContext updateStudentModel(DomStudentModelContext domStudentModelContext) throws Dwo2Exception;

    }

    public interface Build {

//        TeacherState_HR_R_S_SG_U setTeacher() throws Dwo2Exception;
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
    }

    /**
     * @return the context
     */
    protected Context getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    protected void setContext(Context context) {
        this.context = context;
    }

}
