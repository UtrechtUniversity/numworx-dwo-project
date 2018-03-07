package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.server.PersistentDataManagers.actions.MySQLUserActions;
import fi.dwo.server.PersistentDataManagers.actions.UserActions;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.SchoolAdminTeacherDomainAuthorizer.SchoolAdminTeacherState_HR_R_S_SG_U;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
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
public class UserDomainAuthorizer extends AnonDomainAuthorizer {

    private static final Logger LOG = Logger.getLogger(UserDomainAuthorizer.class.getName());

    protected UserPersistentContext userCtx;
    private UserActions userActions;

    /**
     * @return the userActions
     */
    protected UserActions getUserActions() {
        return userActions;
    }

    /**
     * @param userActions the userActions to set
     */
    protected void setUserActions(UserActions userActions) {
        this.userActions = userActions;
    }

    public class UserPersistentContext extends AnonPersistentContext {

        public PersistentUser user;
        public PersistentHasRole hasRole;
        public RoleType roleType;
        public PersistentSchool school;
        public PersistentSchoolGroup schoolGroup;

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
         * @return the roleType
         */
        public RoleType getRoleType() {
            return roleType;
        }

        /**
         * @param roleType the roleType to set
         */
        public void setRoleType(RoleType roleType) {
            this.roleType = roleType;
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
    }

    protected UserDomainAuthorizer() {
        super();
        userCtx = new UserPersistentContext();
        userActions = new MySQLUserActions();
    }

    protected UserDomainAuthorizer(AnonDomainAuthorizer anon) {
        super();
        userCtx = new UserPersistentContext();
        userActions = new MySQLUserActions();
    }

    public static UserState_U user(AnonDomainAuthorizer auth, String username) throws Dwo2Exception {
        return new UserBuilder(auth).setUser(username);
    }

    public interface UserState_U {

        PersistentUser getUser();

        UserState_HR_R_S_SG_U setDefaultHasRole() throws Dwo2Exception;

        UserState_HR_R_S_SG_U setHasRole(DomHasRole hr) throws Dwo2Exception;

        UserState_HR_R_S_SG_U setHasRoleIfType(DomHasRole hr, RoleType r) throws Dwo2Exception;

        public DomUserFull UpdateAccount(DomUserFull user) throws Dwo2Exception;
    }

    public interface UserState_HR_R_S_SG_U {

        PersistentUser getUser();

        PersistentHasRole getHasRole();

        RoleType getRoleType();

        PersistentSchool getSchool();

        PersistentSchoolGroup getSchoolGroup();

        SchoolAdminTeacherState_HR_R_S_SG_U setSchoolAdminTeacher() throws Dwo2Exception;

        public PersistentStudentModelContext getStudentModel(DomScoContextId id) throws Dwo2Exception;
    }
//
//    public interface UserState_HR_R_S_SC_SG_U {
//
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
//    }

    public interface Build {
//
//        PersistentHasRole getHasRole();
//
//        PersistentUser getUser();
//
//        PersistentSchool getSchool();
//
//        RoleType getRoleType();
//        

    }

}
