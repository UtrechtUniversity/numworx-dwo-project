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
import fi.dwo.server.PersistentDataManagers.access.CascadingPersistenceBuilder.State_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.ClassCourseManager;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityNotFoundException;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
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
@Deprecated
public class CascadingPersistenceBuilder {

    private static final Logger LOG = Logger.getLogger(CascadingPersistenceBuilder.class.getName());

    private PersistentContext context = new PersistentContext();

    /**
     * enum denoting the different states the Builder can have. Each enum
     * denotes an interface that defines the transitions it may have to other
     * states via methods and their returned interfaces. U is user, HR is
     * hasrole, R is RoleType, S is School, SG is school group SC is
     * schoolclass, P is profile, SCO is sco(context).
     */
    private static enum InterfaceType {
        State_U,
        State_HR_R_S_SG_U,
        State_HR_R_S_SC_SG_U,
        State_HR_P_R_S_SC_SG_U,
        State_C_CC_HR_P_R_S_SC_SG_U,
        State_C_CC_HR_P_R_S_SC_SCO_SG_U
    }

    public class PersistentContext {

        private PersistentUser user;
        private PersistentHasRole hasRole;
        private RoleType roleType;
        private PersistentSchool school;
        private PersistentSchoolGroup schoolGroup;
        private PersistentSchoolClass schoolClass;
        private PersistentDwoProfile profile;
        private PersistentCourse course;
        private PersistentClassCourse classCourse;
        private PersistentScoContext scoContext;
//
//        /**
//         * @return the user
//         */
//        public PersistentUser user {
//            return user;
//        }
//
//        /**
//         * @param user the user to set
//         */
//        public void setUser(PersistentUser user) {
//            this.user = user;
//        }
//
//        /**
//         * @return the hasRole
//         */
//        public PersistentHasRole getHasRole() {
//            return hasRole;
//        }
//
//        /**
//         * @param hasRole the hasRole to set
//         */
//        public void setHasRole(PersistentHasRole hasRole) {
//            this.hasRole = hasRole;
//        }
//
//        /**
//         * @return the school
//         */
//        public PersistentSchool school {
//            return school;
//        }
//
//        /**
//         * @param school the school to set
//         */
//        public void setSchool(PersistentSchool school) {
//            this.school = school;
//        }
//
//        /**
//         * @return the schoolGroup
//         */
//        public PersistentSchoolGroup getSchoolGroup() {
//            return schoolGroup;
//        }
//
//        /**
//         * @param schoolGroup the schoolGroup to set
//         */
//        public void setSchoolGroup(PersistentSchoolGroup schoolGroup) {
//            this.schoolGroup = schoolGroup;
//        }
//
//        /**
//         * @return the course
//         */
//        public PersistentCourse getCourse() {
//            return course;
//        }
//
//        /**
//         * @param course the Course to set
//         */
//        public void course=(PersistentCourse aCourse) {
//            this.course = aCourse;
//        }
//
//        /**
//         * @return the schoolClass
//         */
//        public PersistentSchoolClass getSchoolClass() {
//            return schoolClass;
//        }
//
//        /**
//         * @param schoolClass the schoolClass to set
//         */
//        public void setSchoolClass(PersistentSchoolClass schoolClass) {
//            this.schoolClass = schoolClass;
//        }
//
//        /**
//         * @return the profile
//         */
//        public PersistentDwoProfile getProfile() {
//            return profile;
//        }
//
//        /**
//         * @param profile the profile to set
//         */
//        public void setProfile(PersistentDwoProfile profile) {
//            this.profile = profile;
//        }
//
//        /**
//         * @return the roleType
//         */
//        public RoleType getRoleType() {
//            return roleType;
//        }
//
//        /**
//         * @param roleType the roleType to set
//         */
//        public void setRoleType(RoleType roleType) {
//            this.roleType = roleType;
//        }
//
//        /**
//         * @return the scoContext
//         */
//        public PersistentScoContext getScoContext() {
//            return scoContext;
//        }
//
//        /**
//         * @param scoContext the scoContext to set
//         */
//        public void setScoContext(PersistentScoContext scoContext) {
//            this.scoContext = scoContext;
//        }
//
//        /**
//         * @return the classCourse
//         */
//        public PersistentClassCourse getClassCourse() {
//            return classCourse;
//        }
//
//        /**
//         * @param classCourse the classCourse to set
//         */
//        public void setClassCourse(PersistentClassCourse classCourse) {
//            this.classCourse = classCourse;
//        }
    }

    private CascadingPersistenceBuilder() {

    }

    public static State_U user(String username) throws Dwo2Exception {
        return new CascadingPersistenceBuilder.Builder(username);
    }

    public interface State_U {
//        Build getContext();

        PersistentUser getUser();

        State_HR_R_S_SG_U addHasRoleIfType(DomHasRole hr, RoleType r) throws Dwo2Exception;
        //ToCourse        
    }

    public interface State_HR_R_S_SG_U {
//        Build getContext();

        PersistentUser getUser();

        PersistentHasRole getHasRole();

        RoleType getRoleType();

        PersistentSchool getSchool();

        PersistentSchoolGroup getSchoolGroup();

        State_HR_R_S_SC_SG_U addSchoolClass(DomSchoolClassId s) throws Dwo2Exception;
    }

    public interface State_HR_R_S_SC_SG_U {
//      Build getContext();

        PersistentUser getUser();

        PersistentHasRole getHasRole();

        RoleType getRoleType();

        PersistentSchool getSchool();

        PersistentSchoolGroup getSchoolGroup();

        PersistentSchoolClass getSchoolClass();

        State_HR_P_R_S_SC_SG_U addProfile(DomDwoProfileId profileid) throws Dwo2Exception;
    }

    public interface State_C_CC_HR_P_R_S_SC_SCO_SG_U {

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

    public interface State_HR_P_R_S_SC_SG_U {

        //Build getContext();
        PersistentUser getUser();

        PersistentHasRole getHasRole();

        RoleType getRoleType();

        PersistentSchool getSchool();

        PersistentSchoolGroup getSchoolGroup();

        PersistentSchoolClass getSchoolClass();

        PersistentDwoProfile getDwoProfile();

        State_C_CC_HR_P_R_S_SC_SG_U addCourse(DomCourse c) throws Dwo2Exception;

        State_C_CC_HR_P_R_S_SC_SCO_SG_U addScoContext(DomScoContextId scoid) throws Dwo2Exception;
    }

    public interface State_C_CC_HR_P_R_S_SC_SG_U {

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

        State_C_CC_HR_P_R_S_SC_SCO_SG_U addScoContext(DomScoContextId s) throws Dwo2Exception;
    }

    public interface Build {

        PersistentHasRole getHasRole();

        PersistentUser getUser();

        PersistentCourse getCourse();

        PersistentSchool getSchool();

        PersistentSchoolClass getSchoolClass();

        PersistentDwoProfile getDwoProfile();

        RoleType getRoleType();
    }

    private static class Builder implements State_U, State_HR_R_S_SG_U,
            State_HR_R_S_SC_SG_U, State_HR_P_R_S_SC_SG_U, State_C_CC_HR_P_R_S_SC_SG_U, State_C_CC_HR_P_R_S_SC_SCO_SG_U, Build {

        private CascadingPersistenceBuilder instance = new CascadingPersistenceBuilder();

        public Builder(String username) throws Dwo2Exception {
            this.user(username);
        }

        /**
         * Verifies and stores the PersistentUser into the context.
         *
         * @param username
         * @return
         * @throws Dwo2Exception
         */
        State_U user(String username) throws Dwo2Exception {
            this.instance.context.user = UserManager.findByUserName(username);
            if (instance.context.user == null) {
                LOG.log(Level.WARNING, "Username {0}: Internal error user does not exist.", new Object[]{username});
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Internal error user does not exist.");
            }
            return this;
        }

        /**
         * Verifies the existence of the hasRole for the given RoleType and
         * stores it and the RoleType into the context. If hr is null the
         * default hasRole configured in the user entity is tried.
         *
         * @param hr
         * @param r
         * @return
         */
        @Override
        public State_HR_R_S_SG_U addHasRoleIfType(DomHasRole hr, RoleType r) {
            PersistentHasRole phr = null;
            //check if user has matching hasRole
            try {
                PersistentHasRolePK phrPK;
                if (hr == null) {
                    phrPK = new PersistentHasRolePK(
                            this.instance.context.user.getId(),
                            this.instance.context.user.getPersistentSchoolGroup().getSchoolGroupID()
                    );
                } else {
                    phrPK = MySQLPersistenceId.getNativeId(hr);
                }
                if (!this.instance.context.user.getId().equals(phrPK.getUserID())) {
                    throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + instance.context.user.getUsername() + ".");
                }
                phr = HasRoleManager.findEntity(phrPK);
                if (phr == null) {
                    String msg = MessageFormat.format("Hasrole {1} for userlogin {0} could not be found.",
                            new Object[]{instance.context.user.getUsername(), this.instance.context.hasRole.getPersistentHasRolePK()});
                    LOG.log(Level.SEVERE, msg);
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
                }
                this.instance.context.hasRole = phr;
                int roleId = RoleType.NONE.ordinal();
                try {
                    roleId = this.instance.context.hasRole.getSchoolGroup().getGroupID();
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "RoleId of hasRole {1} for userlogin {0} could not be found.",
                            new Object[]{instance.context.user.getUsername(), this.instance.context.hasRole.getPersistentHasRolePK()});
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Current Role could not be found.");
                }
                if (roleId == r.ordinal()) {
                    this.instance.context.hasRole = phr;
                    this.instance.context.roleType = r;
                    return this;
                } else {
                    String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Trying to access non-existing role by user with usercode {0}.", new Object[]{instance.context.user.getUsername()});
                    LOG.log(Level.WARNING, msg);
                    throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, msg);
                }
            } catch (Dwo2Exception ex) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access non-existing role by user with usercode {0}.", new Object[]{instance.context.user.getUsername()});
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this role using usercode " + instance.context.user.getUsername() + ".");
            }
        }

        /**
         * Verifies the existence of the schoolClass for the data in the context
         * and adds it and the school to the context.
         *
         * @param s
         * @return
         * @throws Dwo2Exception
         */
        @Override
        public State_HR_R_S_SC_SG_U addSchoolClass(DomSchoolClassId s) throws Dwo2Exception {
            //fetch school
            PersistentSchool school = HasRoleUtilManager.getSchoolforHasRole(this.instance.context.hasRole);
            if (school == null) {
                String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: School for used HasRole does not exists.", new Object[]{instance.context.user.getUsername()});
                LOG.log(Level.WARNING, msg);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
            this.instance.context.school = school;

            //verify if schoolClass is in school
            Long classID = MySQLPersistenceId.getNativeId(s);
            PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(classID);
            if (schoolClass == null || !schoolClass.getSchoolID().equals(school.getSchoolID())) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Active schoolClass {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{instance.context.user.getUsername(), school.getSchoolID(), (schoolClass != null) ? schoolClass.getClassID() : null});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.context.user.getUsername() + ".");
            }
            //verify if roleType is in SchoolClass.
            switch (this.getRoleType()) {
                case TEACHER:
                    PersistentTeacherOfClass toc = TeacherOfClassManager.findEntity(
                            new PersistentTeacherOfClassPK(this.instance.context.hasRole.getPersistentHasRolePK().getUserID(),
                                    schoolClass.getClassID(), this.instance.context.hasRole.getPersistentHasRolePK().getSchoolGroupID()));
                    if (!toc.getPersistentTeacherOfClassPK().getClassID().equals(schoolClass.getClassID())) {
                        //invalid schoolClass
                        String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Not a teacher in the SchoolClass.", new Object[]{instance.context.user.getUsername()});
                        LOG.log(Level.WARNING, msg);
                        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
                    }
                    break;
                case STUDENT:
                    PersistentStudentOfClass soc = StudentOfClassManager.findEntity(
                            new PersistentStudentOfClassPK(this.instance.context.hasRole.getPersistentHasRolePK().getUserID(),
                                    schoolClass.getClassID(), this.instance.context.hasRole.getPersistentHasRolePK().getSchoolGroupID()));
                    if (!soc.getPersistentStudentOfClassPK().getClassID().equals(schoolClass.getClassID())) {
                        //invalid schoolClass
                        String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Not a student in the SchoolClass.", new Object[]{instance.context.user.getUsername()});
                        LOG.log(Level.WARNING, msg);
                        throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
                    }
                    break;
                default:
                    String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Invalid roleType for setting a SchoolClass.", new Object[]{instance.context.user.getUsername()});
                    LOG.log(Level.WARNING, msg);
                    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
            }

            this.instance.context.schoolClass = schoolClass;
            return this;
        }

        /**
         * Verifies the existence of the profile for the data in the context and
         * adds it to the context.
         *
         * @param p
         * @return
         * @throws Dwo2Exception
         */
        @Override
        public State_HR_P_R_S_SC_SG_U addProfile(DomDwoProfileId p) throws Dwo2Exception {
            //fetch profile
            Long profileId = MySQLPersistenceId.getNativeId(p);
            PersistentDwoProfile profile = DwoProfileManager.findEntity(profileId);
            if (profile == null) {
                String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Profile {1} does not exists.", new Object[]{instance.context.user.getUsername(), p.getId()});
                LOG.log(Level.WARNING, msg);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
            this.instance.context.profile = profile;
            return this;
        }

        /**
         * Verifies the existence of the course for the data in the context and
         * adds it to the context.
         *
         * @param c
         * @return
         * @throws Dwo2Exception
         */
        @Override
        public State_C_CC_HR_P_R_S_SC_SG_U addCourse(DomCourse c) throws Dwo2Exception {
            Long courseId = MySQLPersistenceId.getNativeId(c);
            PersistentCourse course = CourseManager.findEntity(courseId);
            if (course == null || course.getDwoProfileID().longValue() != instance.context.profile.getDwoProfileID().longValue()) { // XXX expliciet unboxen
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested course {2} is not available in the profile {1} with usercode {0}.", new Object[]{this.instance.context.user.getUsername(), instance.context.profile.getDwoProfileID(), c.getId()});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.context.user.getUsername() + ".");
            }
            //verify if course is in school
            if (course.getSchoolID() != null && !course.getSchoolID().equals(instance.context.school.getSchoolID())) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested course {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{this.instance.context.user.getUsername(), instance.context.school.getSchoolID(), (course != null) ? course.getSchoolID() : "course==null"});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.context.user.getUsername() + ".");
            }

            this.instance.context.course = (course);
            
            if (instance.context.schoolClass != null) {
            	List<PersistentClassCourse> ccList = ClassCourseManager.findEntities(instance.context.schoolClass, course);
            	if (ccList.size() == 0) {
            		String msg = MessageFormat.format("Username {0}: ClassCourse {1} not found.", new Object[]{instance.context.user.getUsername(), course.getCourseID()});
            		LOG.log(Level.INFO, msg);
            		//throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg); NOT FATAL?
            		instance.context.classCourse = null;
            	} else {
            		instance.context.classCourse = ccList.get(0);
            	}
            }
            return this;
        }

        /**
         *
         * @param s
         * @return
         * @throws Dwo2Exception
         */
        @Override
        public State_C_CC_HR_P_R_S_SC_SCO_SG_U addScoContext(DomScoContextId s) throws Dwo2Exception {
            if (s == null) {
                String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: ScoContext {1} not set.", new Object[]{instance.context.user.getUsername(), s.getId()});
                LOG.log(Level.WARNING, msg);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
            //fetch course and class course from sco
            PersistentScoContext sco = ScoContextManager.findEntity(MySQLPersistenceId.getNativeId(s));
            if (sco == null) {
                String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: ScoContext {1} not found.", new Object[]{instance.context.user.getUsername(), s.getId()});
                LOG.log(Level.WARNING, msg);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
            if (instance.context.course == null) {
                PersistentCourse c = CourseManager.findEntity(sco.getCourseID());
                if (c == null || !c.getDwoProfileID().equals(instance.context.profile.getDwoProfileID())) {
                    String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Course {1} not found.", new Object[]{instance.context.user.getUsername(), c.getCourseID()});
                    LOG.log(Level.WARNING, msg);
                    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
                }

                List<PersistentClassCourse> ccList = ClassCourseManager.findEntities(instance.context.schoolClass, c);
                if (ccList.size() == 0) {
                    String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: ClassCourse {1} not found.", new Object[]{instance.context.user.getUsername(), c.getCourseID()});
                    LOG.log(Level.WARNING, msg);
                    if (false && c.getSchoolID() != null) { // never fatal
                      throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
                    } else
                      instance.context.classCourse = null; // public course, not fatal!
                } else 
                  instance.context.classCourse = ccList.get(0);
                instance.context.course = c;
            }
            instance.context.scoContext = sco;
            return this;
        }

        @Override
        public Boolean removeStudentScoforClassAndCourse() throws Dwo2Exception {
            List<PersistentStudentOfClass> socList = StudentOfClassManager.findEntities(instance.context.schoolClass);
            for (PersistentStudentOfClass soc : socList) {
                PersistentHasRolePK key = new PersistentHasRolePK(soc.getPersistentStudentOfClassPK().getUserID(), soc.getPersistentStudentOfClassPK().getSchoolGroupID());
                List<PersistentStudentScoContext> sscList = StudentScoContextManager.findEntities(instance.context.scoContext, key);
                for (PersistentStudentScoContext ssc : sscList) {
                    String msg = MessageFormat.format("Username {0} is clearing studentSco id {1} for userid  {2} schoolgroupid {3} and course {4} {5}.", new Object[]{instance.context.user.getUsername(), ssc.getScoID(), ssc.getPersistentHasRolePK().getUserID(), ssc.getPersistentHasRolePK().getSchoolGroupID(), instance.context.course.getCourseID(), instance.context.course.getName()});
                    LOG.log(Level.INFO, msg);
                    try {
                      StudentScoDataManager.destroy(ssc.getStudentSco()); //  non-fatal. studentscodata
                    } catch (EntityNotFoundException e1) {}
                    try {
                      StudentScoContextManager.destroy(ssc.getStudentSco());
                    } catch (EntityNotFoundException e) {}
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
            List<PersistentStudentOfClass> socList = StudentOfClassManager.findEntities(instance.context.schoolClass);
            //detach classcourse to ensure no new results occur.
            //TODO mark marked for deleted in the future.
            instance.context.classCourse = 
            ClassCourseManager.editResults(this.getClassCourse().getClassCourseID(), Boolean.FALSE);
            //clean all existing results
            for (PersistentStudentOfClass soc : socList) {
                PersistentHasRolePK key = new PersistentHasRolePK(soc.getPersistentStudentOfClassPK().getUserID(), soc.getPersistentStudentOfClassPK().getSchoolGroupID());
                List<PersistentStudentScoContext> sscList = StudentScoContextManager.findEntities(instance.context.scoContext, key);
                for (PersistentStudentScoContext ssc : sscList) {
                    String msg = MessageFormat.format("Username {0} is clearing studentSco id {1} for userid  {2} schoolgroupid {3} and course {4} {5}.", new Object[]{instance.context.user.getUsername(), ssc.getScoID(), ssc.getPersistentHasRolePK().getUserID(), ssc.getPersistentHasRolePK().getSchoolGroupID(), instance.context.course.getCourseID(), instance.context.course.getName()});
                    LOG.log(Level.INFO, msg);
                    if(StudentScoDataManager.findEntity(ssc.getStudentSco()) != null)
                      StudentScoDataManager.destroy(ssc.getStudentSco());
                    StudentScoContextManager.destroy(ssc.getStudentSco());
                }
            }
            //remove classcourse to ensure no new attachments occur. FIXME Issue met DWOJClient (Wim) daarom comment
            //ClassCourseManager.destroy(this.getClassCourse().getClassCourseID());
            return true;
        }
        
        @Override
        public PersistentScoData getScoData() throws Dwo2Exception {
            if (instance.context.scoContext != null) {
                return ScoDataManager.findEntity(instance.context.scoContext.getScoID());
            } else {
                String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: ScoContext not set.", new Object[]{instance.context.user.getUsername()});
                LOG.log(Level.WARNING, msg);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
        }

        @Override
        public PersistentUser getUser() {
            return instance.context.user;
        }

        @Override
        public PersistentHasRole getHasRole() {
            return instance.context.hasRole;
        }

        @Override
        public PersistentSchool getSchool() {
            return instance.context.school;
        }

        @Override
        public PersistentCourse getCourse() {
            return instance.context.course;
        }

        @Override
        public PersistentSchoolClass getSchoolClass() {
            return instance.context.schoolClass;
        }

        @Override
        public PersistentSchoolGroup getSchoolGroup() {
            return instance.context.schoolGroup;
        }

        @Override
        public PersistentClassCourse getClassCourse() {
            return instance.context.classCourse;
        }

        @Override
        public PersistentScoContext getScoContext() {
            return instance.context.scoContext;
        }

        @Override
        public PersistentDwoProfile getDwoProfile() {
            return instance.context.profile;
        }

        @Override
        public RoleType getRoleType() {
            return instance.context.roleType;
        }
    }

//               
//        public addProfile
//        profile = DwoProfileManager.findEntity(MySQLPersistenceId.getNativeId(domProfile));
//            if (profile == null) {
//                LOG.log(Level.SEVERE, "Username {0}: ILLEGAL USER-OPERATION: Using unknown profileId {1}.", new Object[]{sc.getUserPrincipal().getName(), domProfile.getId()});
//                throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + sc.getUserPrincipal().getName() + ".");
//            }
}
