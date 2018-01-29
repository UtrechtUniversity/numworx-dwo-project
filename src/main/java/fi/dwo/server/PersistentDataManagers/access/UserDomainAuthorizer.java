package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.server.PersistentDataManagers.actions.MySQLUserActions;
import fi.dwo.server.PersistentDataManagers.actions.UserActions;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
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

    private UserPersistentContext context = new UserPersistentContext();
    private UserActions userActions = new MySQLUserActions();

    protected class UserPersistentContext extends AnonPersistentContext {
        protected PersistentUser user;
        protected PersistentHasRole hasRole;
        protected RoleType roleType;
        protected PersistentSchool school;
        protected PersistentSchoolGroup schoolGroup;
    }

    protected UserDomainAuthorizer() {
        super();
    }

    public static UserState_U user(String username) throws Dwo2Exception {
        return new UserDomainAuthorizer.Builder(username);
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

    }

    public interface UserState_HR_R_S_SC_SG_U {

        PersistentUser getUser();

        PersistentHasRole getHasRole();

        RoleType getRoleType();

        PersistentSchool getSchool();

        PersistentSchoolGroup getSchoolGroup();

    }

    public interface Build {

        PersistentHasRole getHasRole();

        PersistentUser getUser();

        PersistentSchool getSchool();

        RoleType getRoleType();
    }

    private static class Builder implements UserState_U, UserState_HR_R_S_SG_U, UserState_HR_R_S_SC_SG_U,
            Build {

        private UserDomainAuthorizer instance = new UserDomainAuthorizer();

        public Builder(String username) throws Dwo2Exception {
            this.setUser(username);
        }

        /**
         * Verifies and stores the PersistentUser into the context.
         *
         * @param username
         * @return
         * @throws Dwo2Exception
         */
        UserState_U setUser(String username) throws Dwo2Exception {
            this.instance.context.user = UserManager.findByUserName(username);
            if (instance.context.user == null) {
                LOG.log(Level.WARNING, "Username {0}: Internal error user does not exist.", new Object[]{username});
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Internal error user does not exist.");
            }
            return this;
        }

        /**
         * Verifies the existence of the default hasRole in the PersistentUser
         * and sets it as the active hasRole into the context.
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
                    this.instance.context.user.getId(),
                    this.instance.context.user.getPersistentSchoolGroup().getSchoolGroupID()
            );

            return setHasRole(phrPK);
        }

        /**
         * Verifies the existence of the hasRole in the PersistentUser and sets
         * it as the active hasRole into the context.
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
         * it as the active hasRole into the context.
         *
         * @param hr
         * @param r
         * @return
         */
        private UserState_HR_R_S_SG_U setHasRole(PersistentHasRolePK phrPK) throws Dwo2Exception {
            PersistentHasRole phr = null;
            //determine default hasRole.
            phr = HasRoleManager.findEntity(phrPK);
            if (phr == null) {
                String msg = MessageFormat.format("Hasrole {1} for userlogin {0} could not be found.",
                        new Object[]{instance.context.user.getUsername(), this.instance.context.hasRole.getPersistentHasRolePK()});
                LOG.log(Level.SEVERE, msg);
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
            this.instance.context.hasRole = phr;
            return this;
        }

        /**
         * Verifies the existence of the hasRole for the given RoleType and
         * stores it and the RoleType into the context.
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
         * stores it and the RoleType into the context.
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
                        new Object[]{instance.context.user.getUsername(), this.instance.context.hasRole.getPersistentHasRolePK()});
                LOG.log(Level.SEVERE, msg);
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
            Long roleId = (long) RoleType.NONE.ordinal();
            try {
                roleId = phr.getSchoolGroup().getRole().getGroupID();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "RoleId of hasRole {1} for userlogin {0} could not be found.",
                        new Object[]{instance.context.user.getUsername(), this.instance.context.hasRole.getPersistentHasRolePK()});
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Current Role could not be found.");
            }
            if (roleId.intValue() == r.ordinal()) {
                this.instance.context.hasRole = phr;
                this.instance.context.roleType = r;
                return this;
            } else {
                String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Trying to access non-existing role by user with usercode {0}.", new Object[]{instance.context.user.getUsername()});
                LOG.log(Level.WARNING, msg);
                throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, msg);
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
        public PersistentSchoolGroup getSchoolGroup() {
            return instance.context.schoolGroup;
        }

        @Override
        public RoleType getRoleType() {
            return instance.context.roleType;
        }

        public DomUserFull UpdateAccount(DomUserFull user) throws Dwo2Exception {
            if (user.getUserName().matches(instance.context.user.getUsername())) {

            } else {
                String msg = MessageFormat.format("Trying to change the usercode from {0} to {1}", new Object[]{instance.context.user.getUsername(), user.getUserName()});
                LOG.log(Level.WARNING,msg);
                throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
            }
            return instance.userActions.UpdateAccount(instance.context.user, user).buildDomUserFull();
        }
    }
}
