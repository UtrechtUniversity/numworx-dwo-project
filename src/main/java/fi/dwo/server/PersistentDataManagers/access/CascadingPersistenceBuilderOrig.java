package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.CourseManager;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolClassManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.PersistentDataManagers.util.HasRoleUtilManager;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

/**
 * Builder to retrieve persistence data in a cascading way an verify access and
 * dynamic model rules. This builder is fluid builder. Technically the class forms
 * a state machine where the interfaces denote the possible transitions (edges
 * in a directed graph). Thus a regular language for the security access can be built.
 *
 * @author G.A.J. van der Plas
 */
public class CascadingPersistenceBuilderOrig {

    private static final Logger LOG = Logger.getLogger(CascadingPersistenceBuilderOrig.class.getName());

    private PersistentContext context = new PersistentContext();

    public class PersistentContext {

        private PersistentUser user;
        private PersistentHasRole hasRole;
        private PersistentSchool school;
        private PersistentSchoolGroup schoolGroup;
        private PersistentSchoolClass schoolClass;
        private PersistentCourse course;
        private PersistentDwoProfile profile;

        /**
         * @return the user
         */
        public PersistentUser getUser() {
            return user;
        }

        /**
         * @param user the user to set
         */
        public void setUser(PersistentUser user) {
            this.user = user;
        }

        /**
         * @return the hasRole
         */
        public PersistentHasRole getHasRole() {
            return hasRole;
        }

        /**
         * @param hasRole the hasRole to set
         */
        public void setHasRole(PersistentHasRole hasRole) {
            this.hasRole = hasRole;
        }

        /**
         * @return the school
         */
        public PersistentSchool getSchool() {
            return school;
        }

        /**
         * @param school the school to set
         */
        public void setSchool(PersistentSchool school) {
            this.school = school;
        }

        /**
         * @return the schoolGroup
         */
        public PersistentSchoolGroup getSchoolGroup() {
            return schoolGroup;
        }

        /**
         * @param schoolGroup the schoolGroup to set
         */
        public void setSchoolGroup(PersistentSchoolGroup schoolGroup) {
            this.schoolGroup = schoolGroup;
        }

        /**
         * @return the course
         */
        public PersistentCourse getCourse() {
            return course;
        }

        /**
         * @param aCourse the Course to set
         */
        public void setCourse(PersistentCourse aCourse) {
            this.course = aCourse;
        }

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
    }

    private CascadingPersistenceBuilderOrig() {

    }

    public static ToHasRole user(String username) throws Dwo2Exception {
        return new CascadingPersistenceBuilderOrig.Builder(username);
    }

    public interface ToHasRole {

        Build getContext();

        FromHasRole addHasRoleIfType(DomHasRole hr, RoleType r) throws Dwo2Exception;
        //ToCourse        
    }

    public interface FromHasRole {

        Build getContext();

        FromSchoolClass addSchoolClass(DomSchoolClass s) throws Dwo2Exception;
//        FromCourse addCourse(DomCourse c) throws Dwo2Exception;
//        ToStudentScoData addSco(DomSco s);
    }

    public interface FromSchoolClass {

        Build getContext();

        FromProfile addProfile(DomDwoProfile p) throws Dwo2Exception;
    }

    public interface FromProfile {

        Build getContext();

        FromCourse addCourse(DomCourse c) throws Dwo2Exception;
    }

    public interface FromCourse {

        Build getContext();
    }

    public interface Build {

        PersistentHasRole getHasRole();

        PersistentUser getUser();

        PersistentCourse getCourse();

        PersistentSchool getSchool();

        PersistentSchoolClass getSchoolClass();

        PersistentDwoProfile getProfile();
    }

    private static class Builder implements ToHasRole, FromHasRole,
            FromSchoolClass, FromProfile, FromCourse, Build {

        private CascadingPersistenceBuilderOrig instance = new CascadingPersistenceBuilderOrig();

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
        ToHasRole user(String username) throws Dwo2Exception {
            this.instance.context.setUser(UserManager.findByUserName(username));
            if (getUser() == null) {
                LOG.log(Level.WARNING, "Username {0}: Internal error user does not exist.", new Object[]{username});
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Internal error user does not exist.");
            }
            return this;
        }

        /**
         * Verifies the existence of the hasRole for the given RoleType and
         * stores it into the context.
         *
         * @param hr
         * @param r
         * @return
         */
        @Override
        public FromHasRole addHasRoleIfType(DomHasRole hr, RoleType r) {
            PersistentHasRole phr = null;
            //check if user has matching hasRole
            try {
                PersistentHasRolePK phrPK = MySQLPersistenceId.getNativeId(hr);
                if (!this.instance.context.getUser().getId().equals(phrPK.getUserID())) {
                    throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this using usercode " + getUser().getUsername() + ".");
                }
                phr = HasRoleManager.findEntity(phrPK);
                if (phr==null) {
                    String msg = MessageFormat.format("Hasrole {1} for userlogin {0} could not be found.",
                            new Object[]{getUser().getUsername(), this.instance.context.getHasRole().getPersistentHasRolePK()});
                    LOG.log(Level.SEVERE, msg);
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
                }
                this.instance.context.setHasRole(phr);
                Long roleId = (long) RoleType.NONE.ordinal();
                try {
                    roleId = this.instance.context.getHasRole().getSchoolGroup().getRole().getGroupID();
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "RoleId of hasRole {1} for userlogin {0} could not be found.",
                            new Object[]{getUser().getUsername(), this.instance.context.getHasRole().getPersistentHasRolePK()});
                    throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Current Role could not be found.");
                }
                if (roleId.intValue() == r.ordinal()) {
                    this.instance.context.setHasRole(phr);
                    return this;
                } else {
                    String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Trying to access non-existing role by user with usercode {0}.", new Object[]{getUser().getUsername()});
                    LOG.log(Level.WARNING, msg);
                    throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, msg);
                }
            } catch (Dwo2Exception ex) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Trying to access non-existing role by user with usercode {0}.", new Object[]{getUser().getUsername()});
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, "You Don't Have Permission to access this role using usercode " + getUser().getUsername() + ".");
            }
        }

        /**
         * Verifies the existence of the schoolClass for the data in the context
         * and adds it to the context.
         *
         * @param s
         * @return
         * @throws Dwo2Exception
         */
        @Override
        public FromSchoolClass addSchoolClass(DomSchoolClass s) throws Dwo2Exception {
            //fetch school
            PersistentSchool school = HasRoleUtilManager.getSchoolforHasRole(this.instance.context.hasRole);
            if (school == null) {
                String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: School for used HasRole does not exists.", new Object[]{getUser().getUsername()});
                LOG.log(Level.WARNING, msg);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
            this.instance.context.setSchool(school);

            //verify if schoolClass is in school
            Long classID = MySQLPersistenceId.getNativeId(s);
            PersistentSchoolClass schoolClass = SchoolClassManager.findEntity(classID);
            if (schoolClass == null || !schoolClass.getSchoolID().equals(school.getSchoolID())) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Active schoolClass {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{instance.context.getUser().getUsername(), school.getSchoolID(), (schoolClass != null) ? schoolClass.getClassID() : null});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.context.getUser().getUsername() + ".");
            }
            this.instance.context.setSchoolClass(schoolClass);
            return this;
        }

        /**
         * Verifies the existence of the profile for the data in the context
         * and adds it to the context.
         *
         * @param p
         * @return
         * @throws Dwo2Exception
         */
        @Override
        public FromProfile addProfile(DomDwoProfile p) throws Dwo2Exception {
            //fetch profile
            Long profileId = MySQLPersistenceId.getNativeId(p);
            PersistentDwoProfile profile = DwoProfileManager.findEntity(profileId);
            if (profile == null) {
                String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Profile {1} does not exists.", new Object[]{getUser().getUsername(), p.getId()});
                LOG.log(Level.WARNING, msg);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
            this.instance.context.setProfile(profile);
            return this;
        }

        /**
         * Verifies the existence of the course for the data in the context
         * and adds it to the context.
         * 
         * @param c
         * @return
         * @throws Dwo2Exception 
         */
        @Override
        public FromCourse addCourse(DomCourse c) throws Dwo2Exception {
            Long courseId = MySQLPersistenceId.getNativeId(c);
            PersistentCourse course = CourseManager.findEntity(courseId);
            if (course==null || course.getDwoProfileID()!=instance.context.profile.getDwoProfileID()) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested course {2} is not available in the profile {1} with usercode {0}.", new Object[]{this.instance.context.getUser().getUsername(), instance.context.getProfile().getDwoProfileID(), c.getId()});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.context.getUser().getUsername() + ".");
            }
            //verify if course is in school
            if (course == null || (course.getSchoolID() != null && !this.instance.context.getCourse().getSchoolID().equals(instance.context.getSchool().getSchoolID()))) {
                LOG.log(Level.WARNING, "Username {0}: ILLEGAL USER-OPERATION: Requested course {2} is from a different school that is registered for hasRole in school {1} with usercode {0}.", new Object[]{this.instance.context.getUser().getUsername(), instance.context.getSchool().getSchoolID(), (course != null) ? course.getSchoolID() : "course==null"});
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, "Database error using usercode " + this.instance.context.getUser().getUsername() + ".");
            }
            this.instance.context.setCourse(course);
            return this;
        }

        /**
         * Returns the context data.
         * 
         * @return 
         */
        @Override
        public Build getContext() {
            return this;
        }
        
        @Override
        public PersistentUser getUser() {
            return instance.context.getUser();
        }

        @Override
        public PersistentHasRole getHasRole() {
            return instance.context.getHasRole();
        }

        @Override
        public PersistentSchool getSchool() {
            return instance.context.getSchool();
        }

        @Override
        public PersistentCourse getCourse() {
            return instance.context.getCourse();
        }

        @Override
        public PersistentSchoolClass getSchoolClass() {
            return instance.context.getSchoolClass();
        }

        @Override
        public PersistentDwoProfile getProfile() {
            return instance.context.getProfile();
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
