package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.server.PersistentDataManagers.actions.MySQLUserActions;
import fi.dwo.server.PersistentDataManagers.actions.UserActions;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.AnonDomainAuthorizer.AnonPersistentContext;
import fi.dwo.server.PersistentDataManagers.access.DwoAdminDomainAuthorizer.DwoAdminState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.SchoolAdminTeacherDomainAuthorizer.SchoolAdminTeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer.StudentState_HR_R_S_SG_U;
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
public class UserDomainAuthorizer {

    private static final Logger LOG = Logger.getLogger(UserDomainAuthorizer.class.getName());
    private UserDomainAuthorizer.Context context;
    private UserActions userActions;

//
//    /** Creates a builder and initializes a context if given. */    
//    protected static UserState buildUser(AnonBuilder anon) throws Dwo2Exception {
//        return new AnonBuilder();
//    }
    protected UserDomainAuthorizer() {
        super();
        userActions = new MySQLUserActions();
    }

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


    public static class Context {
        private AnonPersistentContext anonCtx;
        private UserPersistentContext userCtx;
        
        
        public Context(AnonDomainAuthorizer.Context ctx){
            this.anonCtx = ctx.getAnonCtx();
            userCtx = new UserPersistentContext();
        }

        /**
         * @return the anonCtx
         */
        public AnonPersistentContext getAnonCtx() {
            return anonCtx;
        }

        /**
         * @return the userCtx
         */
        public UserPersistentContext getUserCtx() {
            return userCtx;
        }

        /**
         * @param anonCtx the anonCtx to set
         */
        protected void setAnonCtx(AnonPersistentContext anonCtx) {
            this.anonCtx = anonCtx;
        }

        /**
         * @param userCtx the userCtx to set
         */
        protected void setUserCtx(UserPersistentContext userCtx) {
            this.userCtx = userCtx;
        }

    }
    
    public static class UserPersistentContext {

        public PersistentUser user;
        public PersistentHasRole hasRole;
        public RoleType roleType;
        public PersistentSchool school;
        public PersistentSchoolGroup schoolGroup;
        public String realm;

        protected UserPersistentContext() {
            super();
        }

        protected UserPersistentContext(UserPersistentContext ctx) {
            this.hasRole = ctx.hasRole;
            this.roleType = ctx.roleType;
            this.school = ctx.school;
            this.schoolGroup = ctx.schoolGroup;
            this.user = ctx.user;
            this.realm = ctx.realm;
        }

        /**
         * @return the user
         */
        public PersistentUser getUser() {
            return user;
        }

        /**
         * @param user the user to set
         */
        protected void setUser(PersistentUser user) {
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
        protected void setHasRole(PersistentHasRole hasRole) {
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
        protected void setRoleType(RoleType roleType) {
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
        protected void setSchool(PersistentSchool school) {
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
        protected void setSchoolGroup(PersistentSchoolGroup schoolGroup) {
            this.schoolGroup = schoolGroup;
        }

        protected void setRealm(String of) {
          this.realm = of;          
        }
        
        public String getRealm() {
          return realm;
        }
    }

//
//    public static UserState_U user(AnonDomainAuthorizer auth, String username) throws Dwo2Exception {
//        UserDomainAuthorizer.buildUser().setUser(username);
//        return 
//    }
    public interface UserState_U {

        PersistentUser getUser();

        UserState_HR_R_S_SG_U setDefaultHasRole() throws Dwo2Exception;

        UserState_HR_R_S_SG_U setHasRole(DomHasRole hr) throws Dwo2Exception;

        UserState_HR_R_S_SG_U setHasRoleIfType(DomHasRole hr, RoleType r) throws Dwo2Exception;

        public DomUserFull UpdateAccount(DomUserFull user) throws Dwo2Exception;

        UserState_U setRealm(String realm);
    }

    public interface UserState_HR_R_S_SG_U extends PublicContext{

        PersistentUser getUser();

        PersistentHasRole getHasRole();

        RoleType getRoleType();

        PersistentSchool getSchool();

        PersistentSchoolGroup getSchoolGroup();

        public PersistentStudentModelContext getStudentModel(DomScoContextId id) throws Dwo2Exception;

        SchoolAdminTeacherState_HR_R_S_SG_U buildSchoolAdminTeacher() throws Dwo2Exception;

        StudentState_HR_R_S_SG_U buildStudent() throws Dwo2Exception;

        DwoAdminState_HR_R_S_SG_U buildDwoAdmin() throws Dwo2Exception;

        String getRealm();
    }
    
   public interface PublicContext {

        public UserDomainAuthorizer.Context getContext();

        public void setContext(UserDomainAuthorizer.Context context);
    }
    //return any public server info here. For example version info

    /**
     * @return the context
     */
    public UserDomainAuthorizer.Context getContext() {
        return this.context;
    }

    /**
     * @param context the context to set
     */
    protected void setContext(UserDomainAuthorizer.Context context) {
        this.context = context;
    }


}
