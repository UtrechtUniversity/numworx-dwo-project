/** Copyrighted Mar 13, 2018 */
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
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
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
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

/**
 *
 * @author plas0006
 */
class TeacherBuilder implements TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U, TeacherDomainAuthorizer.TeacherState_HR_P_R_S_SG_U, TeacherDomainAuthorizer.TeacherState_HR_R_S_SC_SG_U, TeacherDomainAuthorizer.TeacherState_HR_P_R_S_SC_SG_U, TeacherDomainAuthorizer.TeacherState_C_CC_HR_P_R_S_SC_SG_U, TeacherDomainAuthorizer.TeacherState_C_CC_HR_P_R_S_SC_SCO_SG_U, TeacherDomainAuthorizer.Build {

    private static final Logger LOG = Logger.getLogger(TeacherBuilder.class.getName());

    private TeacherDomainAuthorizer instance;

    public TeacherBuilder() throws Dwo2Exception {
        super();
        instance = new TeacherDomainAuthorizer();
    }
    
    /**
     * Verifies the existence of the schoolClass for the data in the
     * schoolAdminTeacherCtx and adds it and the school to the
     * schoolAdminTeacherCtx.
     *
     * @param s
     * @return
     * @throws Dwo2Exception
     */
    @Override
    public TeacherDomainAuthorizer.TeacherState_HR_P_R_S_SC_SG_U addSchoolClass(DomSchoolClass s) throws Dwo2Exception {
        //fetch school
        PersistentSchool school = HasRoleUtilManager.getSchoolforHasRole(this.instance.getContext().getUserCtx().getHasRole());
        if (school == null) {
            String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: School for used HasRole does not exists.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
        this.instance.getContext().getUserCtx().setSchool(school);
        //verify if schoolClass is in school
        Long classID = MySQLPersistenceId.getNativeId(s);
        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(classID);
        if (schoolClass == null || !schoolClass.getSchoolID().equals(school.getSchoolID())) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Active schoolClass {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), school.getSchoolID(), (schoolClass != null) ? schoolClass.getClassID() : null});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.getContext().getUserCtx().getUser().getUsername() + ".");
        }
        //verify if roleType is in SchoolClass.
        switch (this.getRoleType()) {
            case TEACHER:
                PersistentTeacherOfClass toc = TeacherOfClassManager.findEntity(new PersistentTeacherOfClassPK(this.instance.getContext().getUserCtx().getHasRole().getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), this.instance.getContext().getUserCtx().getHasRole().getPersistentHasRolePK().getSchoolGroupID()));
                if (!toc.getPersistentTeacherOfClassPK().getClassID().equals(schoolClass.getClassID())) {
                    //invalid schoolClass
                    String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Not a teacher in the SchoolClass.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername()});
                    LOG.log(Level.WARNING, msg);
                    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
                }
                break;
            default:
                String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Invalid roleType for setting a SchoolClass.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername()});
                LOG.log(Level.WARNING, msg);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
        this.instance.getContext().getTeacherCtx().setSchoolClass(schoolClass);
        return this;
    }

    /**
     * Verifies the existence of the profile for the data in the
     * schoolAdminTeacherCtx and adds it to the schoolAdminTeacherCtx.
     *
     * @param p
     * @return
     * @throws Dwo2Exception
     */
    @Override
    public TeacherDomainAuthorizer.TeacherState_HR_P_R_S_SG_U addProfile(DomDwoProfile p) throws Dwo2Exception {
        //fetch profile
        Long profileId = MySQLPersistenceId.getNativeId(p);
        PersistentDwoProfile profile = DwoProfileManager.findEntity(profileId);
        if (profile == null) {
            String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Profile {1} does not exists.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), p.getId()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
        this.instance.getContext().getTeacherCtx().setProfile(profile);
        return this;
    }

    /**
     * Verifies the existence of the course for the data in the
     * schoolAdminTeacherCtx and adds it to the schoolAdminTeacherCtx.
     *
     * @param c
     * @return
     * @throws Dwo2Exception
     */
    @Override
    public TeacherDomainAuthorizer.TeacherState_C_CC_HR_P_R_S_SC_SG_U addCourse(DomCourse c) throws Dwo2Exception {
        Long courseId = MySQLPersistenceId.getNativeId(c);
        PersistentCourse course = CourseManager.findEntity(courseId);
        if (course == null || course.getDwoProfileID() != instance.getContext().getTeacherCtx().getProfile().getDwoProfileID()) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested course {2} is not available in the profile {1} with usercode {0}.", new Object[]{this.instance.getContext().getUserCtx().getUser().getUsername(), instance.getContext().getTeacherCtx().getProfile().getDwoProfileID(), c.getId()});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.getContext().getUserCtx().getUser().getUsername() + ".");
        }
        //verify if course is in school
        if (course.getSchoolID() != null && !course.getSchoolID().equals(instance.getContext().getUserCtx().school.getSchoolID())) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested course {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{this.instance.getContext().getUserCtx().getUser().getUsername(), instance.getContext().getUserCtx().getSchool().getSchoolID(), (course != null) ? course.getSchoolID() : "course==null"});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.getContext().getUserCtx().getUser().getUsername() + ".");
        }
        this.instance.getContext().getTeacherCtx().setCourse(course);
        return this;
    }

    /**
     *
     * @param s
     * @return
     * @throws Dwo2Exception
     */
    @Override
    public TeacherDomainAuthorizer.TeacherState_C_CC_HR_P_R_S_SC_SCO_SG_U addScoContext(DomScoContext s) throws Dwo2Exception {
        if (s == null) {
            String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: ScoContext {1} not set.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), s.getId()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
        //fetch course and class course from sco
        PersistentScoContext sco = ScoContextManager.findEntity(MySQLPersistenceId.getNativeId(s));
        if (sco == null) {
            String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: ScoContext {1} not found.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), s.getId()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
        if (instance.getContext().getTeacherCtx().getCourse() == null) {
            PersistentCourse c = CourseManager.findEntity(sco.getCourseID());
            if (c == null || !c.getDwoProfileID().equals(instance.getContext().getTeacherCtx().profile.getDwoProfileID())) {
                String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Course {1} not found.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), c.getCourseID()});
                LOG.log(Level.WARNING, msg);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
            List<PersistentClassCourse> ccList = ClassCourseManager.findEntities(instance.getContext().getTeacherCtx().getSchoolClass(), c);
            if (ccList.size() == 0) {
                String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: ClassCourse {1} not found.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), c.getCourseID()});
                LOG.log(Level.WARNING, msg);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
            instance.getContext().getTeacherCtx().setClassCourse(ccList.get(0));
            instance.getContext().getTeacherCtx().setCourse(c);
        }
        instance.getContext().getTeacherCtx().setScoContext(sco);
        return this;
    }

    @Override
    public Boolean removeStudentScoforClassAndCourse() throws Dwo2Exception {
        List<PersistentStudentOfClass> socList = StudentOfClassManager.findEntities(instance.getContext().getTeacherCtx().getSchoolClass());
        for (PersistentStudentOfClass soc : socList) {
            PersistentHasRolePK key = new PersistentHasRolePK(soc.getPersistentStudentOfClassPK().getUserID(), soc.getPersistentStudentOfClassPK().getSchoolGroupID());
            List<PersistentStudentScoContext> sscList = StudentScoContextManager.findEntities(instance.getContext().getTeacherCtx().getScoContext(), key);
            for (PersistentStudentScoContext ssc : sscList) {
                String msg = MessageFormat.format("Username {0} is learing studentSco id {1} for userid  {2} schoolgroupid {3} and course {4} {5}.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), ssc.getScoID(), ssc.getPersistentHasRolePK().getUserID(), ssc.getPersistentHasRolePK().getSchoolGroupID(), instance.getContext().getTeacherCtx().getCourse().getCourseID(), instance.getContext().getTeacherCtx().getCourse().getName()});
                LOG.log(Level.INFO, msg);
                StudentScoDataManager.destroy(ssc.getStudentSco());
                StudentScoContextManager.destroy(ssc.getStudentSco());
            }
        }
        return true;
    }

    /**
     * Removes StudentSco and corresponding ClassCourses.
     *
     * @return
     * @throws Dwo2Exception
     */
    @Override
    public Boolean removeStudentScoWithClassCourse() throws Dwo2Exception {
        List<PersistentStudentOfClass> socList = StudentOfClassManager.findEntities(instance.getContext().getTeacherCtx().getSchoolClass());
        //detach classcourse to ensure no new results occur.
        //TODO mark marked for deleted in the future.
        ClassCourseManager.editViewState(this.getClassCourse().getClassCourseID(), ViewState.invisible);
        //clean all existing results
        for (PersistentStudentOfClass soc : socList) {
            PersistentHasRolePK key = new PersistentHasRolePK(soc.getPersistentStudentOfClassPK().getUserID(), soc.getPersistentStudentOfClassPK().getSchoolGroupID());
            List<PersistentStudentScoContext> sscList = StudentScoContextManager.findEntities(instance.getContext().getTeacherCtx().getScoContext(), key);
            for (PersistentStudentScoContext ssc : sscList) {
                String msg = MessageFormat.format("Username {0} is learing studentSco id {1} for userid  {2} schoolgroupid {3} and course {4} {5}.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), ssc.getScoID(), ssc.getPersistentHasRolePK().getUserID(), ssc.getPersistentHasRolePK().getSchoolGroupID(), instance.getContext().getTeacherCtx().getCourse().getCourseID(), instance.getContext().getTeacherCtx().getCourse().getName()});
                LOG.log(Level.INFO, msg);
                StudentScoDataManager.destroy(ssc.getStudentSco());
                StudentScoContextManager.destroy(ssc.getStudentSco());
            }
        }
        //remove classcourse to ensure no new attachments occur.
        ClassCourseManager.destroy(this.getClassCourse().getClassCourseID());
        return true;
    }

    @Override
    public PersistentScoData getScoData() throws Dwo2Exception {
        if (instance.getContext().getTeacherCtx().getScoContext() != null) {
            return ScoDataManager.findEntity(instance.getContext().getTeacherCtx().getScoContext().getScoID());
        } else {
            String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: ScoContext not set.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
    }

    @Override
    public PersistentUser getUser() {
        return instance.getContext().getUserCtx().getUser();
    }

    @Override
    public PersistentHasRole getHasRole() {
        return instance.getContext().getUserCtx().getHasRole();
    }

    @Override
    public PersistentSchool getSchool() {
        return instance.getContext().getUserCtx().getSchool();
    }

    @Override
    public PersistentSchoolGroup getSchoolGroup() {
        return instance.getContext().getUserCtx().getSchoolGroup();
    }

    @Override
    public RoleType getRoleType() {
        return instance.getContext().getUserCtx().getRoleType();
    }

    @Override
    public PersistentCourse getCourse() {
        return instance.getContext().getTeacherCtx().getCourse();
    }

    @Override
    public PersistentSchoolClass getSchoolClass() {
        return instance.getContext().getTeacherCtx().getSchoolClass();
    }

    @Override
    public PersistentClassCourse getClassCourse() {
        return instance.getContext().getTeacherCtx().getClassCourse();
    }

    @Override
    public PersistentScoContext getScoContext() {
        return instance.getContext().getTeacherCtx().getScoContext();
    }

    @Override
    public PersistentDwoProfile getDwoProfile() {
        return instance.getContext().getTeacherCtx().getProfile();
    }

//    @Override
//    public TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U setTeacher() throws Dwo2Exception {
//        if (instance.getContext().getUserCtx().getRoleType() == RoleType.TEACHER) {
//            return this;
//        } else {
//            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Not a teacher");
//        }
//    }
    @Override
    public List<DomStudentModelContext> getStudentModels() throws Dwo2Exception {
        List<PersistentStudentModelContext> pModels = instance.teacherActions.getStudentModels(instance.getContext());
        List<DomStudentModelContext> result = new ArrayList<>(pModels.size());
        pModels.stream().forEach((m) -> result.add(m.buildDomStudentModelContext()));
        return result;
    }

    @Override
    public DomStudentModelContext addStudentModel(DomStudentModelContext model) throws Dwo2Exception {
        try {
            PersistentStudentModelContext pModel = new PersistentStudentModelContext();
            pModel.setModelStructure(model.getModelStructure());
            //requires the school context
            pModel.setSchoolID(instance.getContext().getUserCtx().school.getSchoolID());
            pModel.setPublishState(PublishState.published);
            return instance.teacherActions.addStudentModel(instance.getContext(), pModel).buildDomStudentModelContext();
        } catch (Dwo2Exception e) {
            String msg = MessageFormat.format("Username {0}: Internal error: {1}", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), e.getMessage()});
            LOG.log(Level.WARNING, msg, e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
    }

    public TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U init(SchoolAdminTeacherDomainAuthorizer.Context ctx) throws Dwo2Exception {
        if (ctx.getUserCtx().roleType == RoleType.TEACHER) {
            this.instance.setContext(new TeacherDomainAuthorizer.Context(ctx));
            return this;
        } else {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Not a schooladmin or teacher");
        }
    }

    @Override
    public List<DomSchoolClass> getSchoolClasses() throws Dwo2Exception {
        List<PersistentSchoolClass> classes = instance.teacherActions.getSchoolClasses(instance.getContext());
        List<DomSchoolClass> result = new ArrayList<>(classes.size());
        classes.forEach((k -> result.add(k.buildDomSchoolClass())));
        return result;
    }

    @Override
    public List<DomStudent> getTeachersStudents() throws Dwo2Exception {
        List<PersistentUser> students = instance.teacherActions.getTeachersStudents(instance.getContext());
        List<DomStudent> result = new ArrayList<>(students.size());
        students.forEach((k -> result.add(k.buildDomStudent())));
        return result;
    }

    /**
     *
     *
     * @param sc
     * @param s
     * @return
     */
    @Override
    public TeacherDomainAuthorizer.TeacherState_HR_R_S_SC_SG_U addStudent(DomSchoolClass sc, DomStudent s) throws Dwo2Exception {
        Long toId = null;
        try {
            toId = MySQLPersistenceId.getNativeId(sc);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(toId);
        if (schoolClass == null || !schoolClass.getSchoolID().equals(instance.getContext().getUserCtx().school.getSchoolID())) {
            LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Target schoolClass {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), instance.getContext().getUserCtx().school.getSchoolID(), (schoolClass != null) ? schoolClass.getClassID() : null});
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.getContext().getUserCtx().getUser().getUsername() + ".");
        }
        //verify if student is in SchoolClass.
        PersistentHasRole shr = null;
        try {
            PersistentUser student = UserManager.findEntity(MySQLPersistenceId.getNativeId(s));
            if (student == null) {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Could not find student to add.");
            }
            shr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(student, instance.getContext().getUserCtx().school, RoleType.STUDENT);
            if (shr == null) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Target student {2} is not valid for the given school {1} and schoolclass for usercode {0}.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), instance.getContext().getUserCtx().school.getSchoolID(), (schoolClass != null) ? schoolClass.getClassID() : null});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.getContext().getUserCtx().getUser().getUsername() + ".");
            }
             instance.teacherActions.addStudent(instance.getContext(), schoolClass, shr);
        }catch(Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        return this;
    }
//
//    @Override
//    public TeacherDomainAuthorizer.TeacherState_HR_R_S_SC_SG_U moveStudent(DomSchoolClass sc, DomStudent s) throws Dwo2Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
