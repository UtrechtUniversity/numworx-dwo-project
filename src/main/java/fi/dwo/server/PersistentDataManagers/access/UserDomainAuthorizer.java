package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.server.PersistentDataManagers.actions.MySQLUserActions;
import fi.dwo.server.PersistentDataManagers.actions.UserActions;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.SchoolAdminTeacherDomainAuthorizer.SchoolAdminTeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
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
public class UserDomainAuthorizer extends AnonDomainAuthorizer {

    private static final Logger LOG = Logger.getLogger(UserDomainAuthorizer.class.getName());

    protected UserPersistentContext userCtx;
    private UserActions userActions;

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
        return new UserDomainAuthorizer.Builder(auth).setUser(username);
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

    protected static class Builder implements UserState_U, UserState_HR_R_S_SG_U, //UserState_HR_R_S_SC_SG_U,
            Build {

        private UserDomainAuthorizer instance = new UserDomainAuthorizer();

        private Builder(AnonDomainAuthorizer auth) throws Dwo2Exception {
        }

        /**
         * Verifies and stores the PersistentUser into the userCtx.
         *
         * @param username
         * @return
         * @throws Dwo2Exception
         */
        UserState_U setUser(String username) throws Dwo2Exception {
            this.instance.userCtx.setUser(UserManager.findByUserName(username));
            if (instance.userCtx.getUser() == null) {
                LOG.log(Level.WARNING, "Username {0}: Internal error user does not exist.", new Object[]{username});
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Internal error user does not exist.");
            }
            return this;
        }

        /**
         * Verifies the existence of the default hasRole in the PersistentUser
         * and sets it as the active hasRole into the userCtx.
         *
         * @param hr
         * @param r
         * @return
         * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception If the default
         * hasRole is invalid.
         */
        @Override
        public UserState_HR_R_S_SG_U setDefaultHasRole() throws Dwo2Exception {
            PersistentHasRole phr = null;
            //determine default hasRole.
            PersistentHasRolePK phrPK;
            phrPK = new PersistentHasRolePK(
                    this.instance.userCtx.getUser().getId(),
                    this.instance.userCtx.getUser().getPersistentSchoolGroup().getSchoolGroupID()
            );

            return setHasRole(phrPK);
        }

        /**
         * Verifies the existence of the hasRole in the PersistentUser and sets
         * it as the active hasRole into the userCtx.
         *
         * @param hr
         * @param r
         * @return
         */
        @Override
        public UserState_HR_R_S_SG_U setHasRole(DomHasRole hr) throws Dwo2Exception {
            PersistentHasRolePK phrPK;
            phrPK = MySQLPersistenceId.getNativeId(hr);
            return setHasRole(phrPK);
        }

        /**
         * Verifies the existence of the hasRole in the PersistentUser and sets
         * it as the active hasRole into the userCtx.
         *
         * @param hr
         * @param r
         * @return
         */
        private UserState_HR_R_S_SG_U setHasRole(PersistentHasRolePK phrPK) throws Dwo2Exception {
            PersistentHasRole phr = null;
            //determine default hasRole.
            phr = HasRoleManager.findEntity(phrPK);
            if (phr == null
                    || phr.getUser().getId().longValue() != instance.userCtx.user.getId().longValue() //users is valid
                    || phr.getSchoolGroup().getSchoolGroupID().longValue() != phrPK.getSchoolGroupID().longValue() //requested hasRole exists                   
                    ) {
                String msg = MessageFormat.format("Hasrole {1} for userlogin {0} could not be found.",
                        new Object[]{instance.userCtx.getUser().getUsername(), this.instance.userCtx.getHasRole().getPersistentHasRolePK()});
                LOG.log(Level.SEVERE, msg);
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
            instance.userCtx.school = phr.getSchoolGroup().getSchool();
            this.instance.userCtx.setHasRole(phr);
            instance.userCtx.setRoleType(RoleType.values()[phr.getSchoolGroup().getRole().getGroupID().intValue()]);
            return this;
        }

        /**
         * Verifies the existence of the hasRole for the given RoleType and
         * stores it and the RoleType into the userCtx.
         *
         * @param hr
         * @param r
         * @return
         * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
         */
        @Override
        public UserState_HR_R_S_SG_U setHasRoleIfType(DomHasRole hr, RoleType r) throws Dwo2Exception {
            PersistentHasRolePK phrPK;
            phrPK = MySQLPersistenceId.getNativeId(hr);
            return setHasRoleIfType(phrPK, r);

        }

        /**
         * Verifies the existence of the hasRole for the given RoleType and
         * stores it and the RoleType into the userCtx.
         *
         * @param phrPK
         * @param r
         * @return
         * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
         */
        private UserState_HR_R_S_SG_U setHasRoleIfType(PersistentHasRolePK phrPK, RoleType r) throws Dwo2Exception {
            //fetch PersistentHasRole if it exists.
            PersistentHasRole phr = null;
            phr = HasRoleManager.findEntity(phrPK);
            if (phr == null) {
                String msg = MessageFormat.format("Hasrole {1} for userlogin {0} could not be found.",
                        new Object[]{instance.userCtx.getUser().getUsername(), this.instance.userCtx.getHasRole().getPersistentHasRolePK()});
                LOG.log(Level.SEVERE, msg);
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
            Long roleId = (long) RoleType.NONE.ordinal();
            try {
                roleId = phr.getSchoolGroup().getRole().getGroupID();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "RoleId of hasRole {1} for userlogin {0} could not be found.",
                        new Object[]{instance.userCtx.getUser().getUsername(), this.instance.userCtx.getHasRole().getPersistentHasRolePK()});
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Current Role could not be found.");
            }
            if (roleId.intValue() == r.ordinal()) {
                this.instance.userCtx.setHasRole(phr);
                this.instance.userCtx.setRoleType(r);
                return this;
            } else {
                String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Trying to access non-existing role by user with usercode {0}.", new Object[]{instance.userCtx.getUser().getUsername()});
                LOG.log(Level.WARNING, msg);
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, msg);
            }
        }

        @Override
        public PersistentUser getUser() {
            return instance.userCtx.getUser();
        }

        @Override
        public PersistentHasRole getHasRole() {
            return instance.userCtx.getHasRole();
        }

        @Override
        public PersistentSchool getSchool() {
            return instance.userCtx.getSchool();
        }

        @Override
        public PersistentSchoolGroup getSchoolGroup() {
            return instance.userCtx.getSchoolGroup();
        }

        @Override
        public RoleType getRoleType() {
            return instance.userCtx.getRoleType();
        }

        @Override
        public DomUserFull UpdateAccount(DomUserFull user) throws Dwo2Exception {
            if (user.getUserName().matches(instance.userCtx.getUser().getUsername())) {
                return instance.userActions.UpdateAccount(instance.userCtx.getUser(), user).buildDomUserFull();
            } else {
                String msg = MessageFormat.format("Trying to change the usercode from {0} to {1}", new Object[]{instance.userCtx.getUser().getUsername(), user.getUserName()});
                LOG.log(Level.WARNING, msg);
                throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
            }
        }

        @Override
        public SchoolAdminTeacherState_HR_R_S_SG_U setSchoolAdminTeacher() throws Dwo2Exception {
            return new SchoolAdminTeacherDomainAuthorizer.Builder(instance).setSchoolAdminTeacher();
        }

        @Override
        public PersistentStudentModelContext getStudentModel(DomScoContextId ctxId) throws Dwo2Exception {
            PersistentScoContext scoCtx = ScoContextManager.findEntity(MySQLPersistenceId.getNativeId(ctxId));

            if (instance.userCtx.school.getSchoolID() != scoCtx.getSchoolID().longValue()) {
                String msg = MessageFormat.format("Username {0}: SchoolId {1} of sco mismatches hasrole for the given StudentModelContext: {2}", new Object[]{instance.userCtx.getUser().getUsername(), instance.userCtx.school.getSchoolID(), ctxId.getId().toString()});
                LOG.log(Level.WARNING, msg);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
            } else if (scoCtx.getSchoolID() == null) {
                String msg = MessageFormat.format("StudentModelContext not set for Sco {0}", new Object[]{ctxId.getId().toString()});
                LOG.log(Level.WARNING, msg);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_StudentModelNotSet, msg);
            } else {
                try {
                    return instance.userActions.getStudentModel(instance.userCtx, ctxId);
                } catch (Dwo2Exception e) {
                    String msg = MessageFormat.format("Username {0}: Internal error: {1}", new Object[]{instance.userCtx.getUser().getUsername(), e.getMessage()});
                    LOG.log(Level.WARNING, msg, e);
                    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
                }
            }
        }
    }
}
