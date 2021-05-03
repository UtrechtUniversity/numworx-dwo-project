/**
 * Copyrighted Mar 13, 2018
 */
package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.commons.domainmodel.XapiResultsManager;
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
import fi.dwo.commons.persistence.entities.PersistentStudentInClass;
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
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.ScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentScoDataManager;
import fi.dwo.server.PersistentDataManagers.core.TeacherOfClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import fi.dwo.server.PersistentDataManagers.util.StudentInClassManager;
import fi.dwo.server.PersistentDataManagers.util.StudentModelContextUtilManager;
import fi.dwo.server.PersistentDataManagers.util.StudentModelDataUtilManager;
import fi.dwo.server.rest.util.Digest;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.osgi.util.promise.Promise;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextPatch;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataStudentScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentOfClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.dom.entities.util.GensonMapConverter;
import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;
import nl.uu.fi.dwo.rest.dom.entities.util.ViewState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.DwoDateUtilities;

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
            String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: ScoContext not set.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername()});
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
                //throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
                instance.getContext().getTeacherCtx().setClassCourse(null);
            } else 
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
                try {
                    StudentScoDataManager.destroy(ssc.getStudentSco()); //  non-fatal. studentscodata
                } catch (EntityNotFoundException e1) {
                }
                try {
                    StudentScoContextManager.destroy(ssc.getStudentSco());
                } catch (EntityNotFoundException e) {
                }
            }
        }
        return true;
    }

    /**
     * Removes StudentSco and corresponding ClassCourses. FIXME Issue met
     * DWOJClient (Wim)
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
                try {
                    StudentScoDataManager.destroy(ssc.getStudentSco()); //  non-fatal. studentscodata
                } catch (EntityNotFoundException e1) {
                }
                try {
                    StudentScoContextManager.destroy(ssc.getStudentSco());
                } catch (EntityNotFoundException e) {
                }
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
        pModels.forEach((m) -> result.add(m.buildDomStudentModelContext()));
        return result;
    }
    @Override
    public List<DomStudentModelContext> getReducedStudentModels() throws Dwo2Exception {
        List<PersistentStudentModelContext> pModels = instance.teacherActions.getReducedStudentModels(instance.getContext());
        List<DomStudentModelContext> result = new ArrayList<>(pModels.size());
        pModels.forEach((m) -> result.add(m.buildDomStudentModelContext()));
        return result;
    }
    @Override
    public List<DomStudentModelContext> getMergedStudentModels() throws Dwo2Exception {
        List<PersistentStudentModelContext> pModels = instance.teacherActions.getStudentModels(instance.getContext());
        pModels.forEach(StudentModelContextUtilManager::merge);
        List<DomStudentModelContext> result = new ArrayList<>(pModels.size());
        pModels.forEach((m) -> result.add(m.buildDomStudentModelContext()));
        return result;
    }
    
    @Override
    public DomStudentModelContext getStudentModel(DomStudentModelContextId id) throws Dwo2Exception {
    	PersistentStudentModelContext result = instance.teacherActions.getStudentModel(instance.getContext(), id);
    	return result.buildDomStudentModelContext();
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

    @Override
    public DomStudentModelContext updateStudentModel(DomStudentModelContext model) throws Dwo2Exception {
        try {            
            Long id = MySQLPersistenceId.getNativeId(model);
            PersistentStudentModelContext pModel = StudentModelContextManager.findEntity(id);
            if ( pModel == null) {
              throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Illegal operation");
            }
            //verify if course is in school
            if ( !pModel.getSchoolID().equals(instance.getContext().getUserCtx().school.getSchoolID())) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested studentmode {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{this.instance.getContext().getUserCtx().getUser().getUsername(), instance.getContext().getUserCtx().getSchool().getSchoolID(), (pModel != null) ? pModel.getSchoolID() : "model==null"});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.getContext().getUserCtx().getUser().getUsername() + ".");
            }
            if ( model.getOptLock() != null && !pModel.getOptlock() .equals (model.getOptLock())) {
              LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested studentmode {2} is from a different optlock that is registered for hasRole in school {1} with usercode {0}.", new Object[]{this.instance.getContext().getUserCtx().getUser().getUsername(), instance.getContext().getUserCtx().getSchool().getSchoolID(), (pModel != null) ? pModel.getSchoolID() : "model==null"});     
              throw new WebApplicationException(Status.CONFLICT);
            } else 
            if (model.getOptLock() != null) {
              pModel.setOptlock(model.getOptLock());
            }
            pModel.setModelStructure(model.getModelStructure());
            pModel.setPublishState(model.getPublishState());
            //return instance.teacherActions.updateStudentModel(instance.getContext(), pModel).buildDomStudentModelContext();
            
            return StudentModelContextUtilManager.edit(pModel).buildDomStudentModelContext(); // FIXME netjes maken!
        } catch (RollbackException|OptimisticLockException rb) {
        	LOG.log(Level.SEVERE, "conflict", rb);
            throw new WebApplicationException(Status.CONFLICT);
        } catch (Dwo2Exception e) {
            String msg = MessageFormat.format("Username {0}: Internal error: {1}", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), e.getMessage()});
            LOG.log(Level.WARNING, msg, e);
            throw new Dwo2RestException(e.getDwo2Code(), msg);
        }
    }
   
    @Override
    public Boolean removeStudentModel(DomStudentModelContext model) throws Dwo2Exception {
        try {
          Long id = MySQLPersistenceId.getNativeId(model);
          PersistentStudentModelContext pModel = StudentModelContextManager.findEntity(id);
          if ( pModel == null) {
            return Boolean.FALSE;
          }
          if (model.getOptLock() != null) pModel.setOptlock(model.getOptLock());
          //verify if course is in school
          if ( !pModel.getSchoolID().equals(instance.getContext().getUserCtx().school.getSchoolID())) {
              LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested studentmode {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{this.instance.getContext().getUserCtx().getUser().getUsername(), instance.getContext().getUserCtx().getSchool().getSchoolID(), (pModel != null) ? pModel.getSchoolID() : "model==null"});
              throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.getContext().getUserCtx().getUser().getUsername() + ".");
          }
          StudentModelContextManager.destroy(id);
          return Boolean.TRUE;
        } catch (RollbackException|OptimisticLockException e) {
        	throw new WebApplicationException(Status.CONFLICT);
        } catch (EntityNotFoundException e) {
          return Boolean.FALSE;
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
        String realm = instance.getContext().getUserCtx().getRealm();
        List<DomStudent> result = new ArrayList<>(students.size());
        students.forEach((k -> result.add(k.buildDomStudent(realm))));
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
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Target student {2} is not valid for the given school {1} for usercode {0}.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), instance.getContext().getUserCtx().school.getSchoolID(), (schoolClass != null) ? schoolClass.getClassID() : null});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.getContext().getUserCtx().getUser().getUsername() + ".");
            }
            instance.teacherActions.addStudent(instance.getContext(), schoolClass, shr);
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
        return this;
    }

    /**
     * Returns the subset of current teacher's classes a aTeacher is a member
     * of.
     */
    @Override
    public List<DomSchoolClassId> getSharedTeacherClasses(DomTeacher aTeacher) throws Dwo2Exception {
        try {
            PersistentUser teacher = UserManager.findEntity(MySQLPersistenceId.getNativeId(aTeacher));
            if (teacher == null) {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Could not find student to add.");
            }
            PersistentHasRole shr = null;
            shr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(teacher, instance.getContext().getUserCtx().school, RoleType.TEACHER);
            if (shr == null) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Target teacher {2} is not valid for the given school {1} for usercode {0}.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), instance.getContext().getUserCtx().school.getSchoolID(), aTeacher.getUserName()});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.getContext().getUserCtx().getUser().getUsername() + ".");
            }
            List<PersistenceId> r = instance.teacherActions.getSharedTeacherClasses(instance.getContext(), teacher);
            List<DomSchoolClassId> result = new ArrayList<>(r.size());
            r.forEach((k -> result.add(new DomSchoolClassId(k))));
            return result;
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
    }

    /**
     * Returns the subset of current teacher's classes a aTeacherr is a member
     * of.
     */
    @Override
    public List<DomSchoolClassId> getTeachersClassesOfStudent(DomStudent aStudent) throws Dwo2Exception {
        try {
            PersistentUser student = UserManager.findEntity(MySQLPersistenceId.getNativeId(aStudent));
            if (student == null) {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Could not find student to add.");
            }
            PersistentHasRole shr = null;
            shr = HasRoleUtilManager.getUsersHasRoleInSchoolAndRole(student, instance.getContext().getUserCtx().school, RoleType.STUDENT);
            if (shr == null) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Target teacher {2} is not valid for the given school {1} for usercode {0}.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), instance.getContext().getUserCtx().school.getSchoolID(), aStudent.getUserName()});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.getContext().getUserCtx().getUser().getUsername() + ".");
            }
            List<PersistenceId> r = instance.teacherActions.getTeachersClassesOfStudent(instance.getContext(), shr.getSchoolGroup(), student);
            List<DomSchoolClassId> result = new ArrayList<>(r.size());
            r.forEach((k -> result.add(new DomSchoolClassId(k))));
            return result;
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
            throw new Dwo2RestException(ex);
        }
    }

    @Override
    public Boolean attachCourseToClass() throws Dwo2Exception {
        return instance.teacherActions.attachCourseToClass(instance.getContext());
    }

    @Override
    public Boolean addCourseToClass(CourseType courseType, Date from, Date to, String accessKey) throws Dwo2Exception {
        if (from==null || to==null || from.before(to)) {
            return instance.teacherActions.addCourseToClass(instance.getContext(), courseType, from, to, accessKey);
        } else {
            throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "Date range is invalid.");
        }
    }

    @Override
    public Boolean detachCourseFromClass() throws Dwo2Exception {
        return instance.teacherActions.detachCourseFromClass(instance.getContext());
    }

	@Override
	public DomStudentModelScorePerTeacher getScores(DomStudentModelScorePerTeacher dom, UriInfo info) throws Dwo2Exception {
	  PersistentSchool school = getSchool();
	  PersistentSchoolGroup studentgroup = SchoolGroupManager.findBySchoolAndRole(school, RoleType.STUDENT);
	  URL url = null;
    try {
      url = new URL("https://repos.dwo.nl/");
    } catch (MalformedURLException e1) {
    }
    XapiResultsManager xapi = new XapiResultsManager(getLRS(info), url);
	  dom.setFetchTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
		String realm = instance.getContext().getUserCtx().getRealm();
		dom.setTeacher(instance.getContext().getUserCtx().user.buildDomTeacher(realm));
		if (dom.getSchoolClasses() == null) {
			dom.setSchoolClasses(
					getSchoolClasses().stream().map(s -> new DomMapEntry<>(s.getId(), s)).collect(Collectors.toList()));
		}
		if (dom.getStudents() == null) {
			dom.setStudents(
					getTeachersStudents().stream().map(s -> new DomMapEntry<>(s.getId(), s)).collect(Collectors.toList()));
		}
		
		dom.setStudentScores(Collections.emptyList());
		Stream<DomSchoolClass> l1 = dom.getSchoolClasses().stream()
			.map(x -> x.getValue());
		Stream<PersistentSchoolClass> l2 = l1
			.map(id -> {
				try {
					return SchoolClassManager.findEntity(MySQLPersistenceId.getNativeId(id));
				} catch (Dwo2Exception e) {
					throw new Dwo2RestException(e);
				}
			});
		Stream<PersistentStudentInClass> l3 = l2
				.flatMap(c -> StudentInClassManager.findEntities(c).stream());
		List<PersistentStudentInClass> students = l3.collect(Collectors.toList());
		l3 = students.stream();
		List<DomMapEntry<PersistenceId, DomStudentOfClass>> aStudentsOfClasses =
				l3
					.map(PersistentStudentInClass::getStudentOfClass)
					.map(PersistentStudentOfClass::buildDomStudentOfClass)
					.map(t -> new DomMapEntry<>(t.getId(), t))
					.collect(Collectors.toList());
		dom.setStudentsOfClasses(aStudentsOfClasses );
		
		// reduce students
		l3 = students.stream();
		Set<PersistenceId> set = l3.map(PersistentStudentInClass::getUser).map(PersistentUser::buildPersistenceId).collect(Collectors.toSet());
		dom.setStudents(dom.getStudents()
				.stream()
				.filter( t -> set.contains(t.getKey()))
				.collect(Collectors.toList()));

// fill studentmodelcontext
		
		dom.getStudentModelContexts().forEach(
			entry -> {
				try {
					PersistenceId key = entry.getKey();
					DomStudentModelContextId cid = new DomStudentModelContextId(key);
					PersistentStudentModelContext entity = StudentModelContextManager.findEntity(MySQLPersistenceId.getNativeId(cid));
					entity = StudentModelContextUtilManager.merge(entity);
					entry.setValue(entity.buildDomStudentModelContext()); // FIXME zonder description
				} catch (Dwo2Exception e) {
					throw new Dwo2RestException(e);
				}
			}
		);
// iterate over students and contexts	
        dom.setStudentScores(Collections.emptyList());
        Stream<DomStudentModelDataStudentScore> stream = 
        dom.getStudents().stream()
          .map(DomMapEntry::getValue)
          .map(arg0 -> {
        try {
          return MySQLPersistenceId.getNativeId(arg0);
        } catch (Dwo2Exception e) {
          throw new Dwo2RestException(e);
        }
      })
          .map( t -> new PersistentHasRole(new PersistentHasRolePK(t, studentgroup.getSchoolGroupID())))
          .flatMap(
              hr -> 
                  {
                    Stream<DomStudentModelContext> stream2 = dom.getStudentModelContexts().stream().map(DomMapEntry::getValue);
 
                    Stream<DomStudentModelDataStudentScore> stream3 = stream2
                    .map(
                      t -> {                        
                        DomStudentModelStructureScore score;
                    try {
                      score = StudentModelDataUtilManager.calculateStudentModelScore(t, hr);
                    } catch (Dwo2Exception e) {
                      throw new Dwo2RestException(e);
                    }
                        DomStudentModelDataStudentScore result = new DomStudentModelDataStudentScore();
                        result.setDomStudentModelStructureScore(score);
                        result.setModelId(t);
                        result.setStudentId(PersistentUser.buildPersistenceId(hr.getPersistentHasRolePK().getUserID()));
                        return result;
                      });
                    return stream3;
                  }
              )
        ;  
          
        dom.setStudentScores(stream
          .collect(Collectors.toList()));

		
		
		
		
		
		
		
		
// new		
		Promise<DomStudentModelScorePerTeacher> prom = xapi.fromXAPI(dom);
		try {
      return prom.getValue();
    } catch (InvocationTargetException e) {
       LOG.log(Level.SEVERE, "getScores", e);
    } catch (InterruptedException e) {
    }
		return dom;
	}

  @Override
  public DomLRS getLRS(UriInfo info) {
    return instance.teacherActions.getLRS(instance.getContext(), info);
  }

@Override
public DomStudentModelContext patchStudentModel(DomStudentModelContextPatch domPatch) throws Dwo2Exception {
	PersistentStudentModelContext result = instance.teacherActions.getStudentModel(instance.getContext(), domPatch);
	if (result.getOptlock().equals(domPatch.getOptLock()) && result.getLastChangeTimeStamp()==domPatch.getLastChangeTimeStamp()) {
		
		if (domPatch.getPublishState() != null) result.setPublishState(domPatch.getPublishState());
		// patch
		String value = domPatch.getPatch();
		String digest = domPatch.getDigest();
		Genson g = new GensonBuilder().withConverters(new GensonMapConverter()).create(); // met de juiste opties
		String oldValue = g.serialize(result.getModelStructure());
        JsonParser parser = Json.createParser(new StringReader(oldValue));
        parser.next();
        JsonObject oldObject = parser.getObject();
        parser = Json.createParser(new StringReader(value));
        parser.next();
        JsonArray  patch     = parser.getArray();
        JsonObject newObject = Json.createPatch(patch).apply(oldObject);
        if (digest != null) {
          String patched = new Digest().digest(newObject);
          if( !digest.equals(patched)) {
            LOG.severe("patch digest error " + patched + " " + digest);
            throw new WebApplicationException(Status.PRECONDITION_FAILED);
          }
        }
        StringWriter newValue = new StringWriter();
        Json.createWriter(newValue).write(newObject);
        DomStudentModelStructure deserialize = g.deserialize(newValue.toString(), DomStudentModelStructure.class);
		result.setModelStructure(deserialize);

		try {
			DomStudentModelContext context = StudentModelContextUtilManager.edit(result).buildDomStudentModelContext();
			context.setModelStructure(null);
			return context;
        } catch (RollbackException|OptimisticLockException|EntityNotFoundException e) {
        	throw new WebApplicationException(Status.CONFLICT);
        }
	}
	throw new WebApplicationException(Status.CONFLICT);
}
}
