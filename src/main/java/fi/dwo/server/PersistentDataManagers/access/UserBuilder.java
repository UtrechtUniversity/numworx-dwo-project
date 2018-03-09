/**
 * Copyrighted Mar 7, 2018
 */
package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentHasRolePK;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

/**
 *
 * @author Gert van der Plas
 */
class UserBuilder extends AnonBuilder implements UserDomainAuthorizer.UserState_U, UserDomainAuthorizer.UserState_HR_R_S_SG_U, //UserState_HR_R_S_SC_SG_U,
        UserDomainAuthorizer.Build {

    private static final Logger LOG = Logger.getLogger(UserBuilder.class.getName());

    protected UserDomainAuthorizer instance = new UserDomainAuthorizer();

//    protected UserBuilder(AnonDomainAuthorizer auth) throws Dwo2Exception {
//    }
    protected UserBuilder() throws Dwo2Exception {
        super();
    }

    protected UserBuilder(AnonBuilder builder) throws Dwo2Exception {
        super();
        instance.userCtx = new UserDomainAuthorizer.UserPersistentContext(builder.instance.getAnonCtx());
    }

    /**
     * Verifies and stores the PersistentUser into the userCtx.
     *
     * @param username
     * @return
     * @throws Dwo2Exception
     */
    UserDomainAuthorizer.UserState_U setUser(String username) throws Dwo2Exception {
        this.instance.userCtx.setUser(UserManager.findByUserName(username));
        if (instance.userCtx.getUser() == null) {
            LOG.log(Level.WARNING, "Username {0}: Internal error user does not exist.", new Object[]{username});
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Internal error user does not exist.");
        }
        return this;
    }

    /**
     * Verifies the existence of the default hasRole in the PersistentUser and
     * sets it as the active hasRole into the userCtx.
     *
     * @param hr
     * @param r
     * @return
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception If the default hasRole
     * is invalid.
     */
    @Override
    public UserDomainAuthorizer.UserState_HR_R_S_SG_U setDefaultHasRole() throws Dwo2Exception {
        PersistentHasRole phr = null;
        //determine default hasRole.
        PersistentHasRolePK phrPK;
        phrPK = new PersistentHasRolePK(this.instance.userCtx.getUser().getId(), this.instance.userCtx.getUser().getPersistentSchoolGroup().getSchoolGroupID());
        return setHasRole(phrPK);
    }

    /**
     * Verifies the existence of the hasRole in the PersistentUser and sets it
     * as the active hasRole into the userCtx.
     *
     * @param hr
     * @param r
     * @return
     */
    @Override
    public UserDomainAuthorizer.UserState_HR_R_S_SG_U setHasRole(DomHasRole hr) throws Dwo2Exception {
        PersistentHasRolePK phrPK;
        phrPK = MySQLPersistenceId.getNativeId(hr);
        return setHasRole(phrPK);
    }

    /**
     * Verifies the existence of the hasRole in the PersistentUser and sets it
     * as the active hasRole into the userCtx.
     *
     * @return
     */
    private UserDomainAuthorizer.UserState_HR_R_S_SG_U setHasRole(PersistentHasRolePK phrPK) throws Dwo2Exception {
        PersistentHasRole phr = null;
        //determine default hasRole.
        phr = HasRoleManager.findEntity(phrPK);
        if (phr == null || phr.getUser().getId().longValue() != instance.userCtx.user.getId().longValue() //users is valid
                || phr.getSchoolGroup().getSchoolGroupID().longValue() != phrPK.getSchoolGroupID().longValue() //requested hasRole exists
                ) {
            String msg = MessageFormat.format("Hasrole {1} for userlogin {0} could not be found.", new Object[]{instance.userCtx.getUser().getUsername(), this.instance.userCtx.getHasRole().getPersistentHasRolePK()});
            LOG.log(Level.SEVERE, msg);
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
        instance.userCtx.school = phr.getSchoolGroup().getSchool();
        this.instance.userCtx.setHasRole(phr);
        instance.userCtx.setRoleType(RoleType.values()[phr.getSchoolGroup().getRole().getGroupID().intValue()]);
        return this;
    }

    /**
     * Verifies the existence of the hasRole for the given RoleType and stores
     * it and the RoleType into the userCtx.
     *
     * @return
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    @Override
    public StudentDomainAuthorizer.StudentState_HR_R_S_SG_U setStudent() throws Dwo2Exception {
        StudentBuilder builder = new StudentBuilder(this);
        return builder.setStudent();

    }

    /**
     * Verifies the existence of the hasRole for the given RoleType and stores
     * it and the RoleType into the userCtx.
     *
     * @param hr
     * @param r
     * @return
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    @Override
    public UserDomainAuthorizer.UserState_HR_R_S_SG_U setHasRoleIfType(DomHasRole hr, RoleType r) throws Dwo2Exception {
        PersistentHasRolePK phrPK;
        phrPK = MySQLPersistenceId.getNativeId(hr);
        return setHasRoleIfType(phrPK, r);
    }

    /**
     * Verifies the existence of the hasRole for the given RoleType and stores
     * it and the RoleType into the userCtx.
     *
     * @param phrPK
     * @param r
     * @return
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    private UserDomainAuthorizer.UserState_HR_R_S_SG_U setHasRoleIfType(PersistentHasRolePK phrPK, RoleType r) throws Dwo2Exception {
        //fetch PersistentHasRole if it exists.
        PersistentHasRole phr = null;
        phr = HasRoleManager.findEntity(phrPK);
        if (phr == null) {
            String msg = MessageFormat.format("Hasrole {1} for userlogin {0} could not be found.", new Object[]{instance.userCtx.getUser().getUsername(), this.instance.userCtx.getHasRole().getPersistentHasRolePK()});
            LOG.log(Level.SEVERE, msg);
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
        Long roleId = (long) RoleType.NONE.ordinal();
        try {
            roleId = phr.getSchoolGroup().getRole().getGroupID();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "RoleId of hasRole {1} for userlogin {0} could not be found.", new Object[]{instance.userCtx.getUser().getUsername(), this.instance.userCtx.getHasRole().getPersistentHasRolePK()});
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
            return instance.getUserActions().UpdateAccount(instance.userCtx.getUser(), user).buildDomUserFull();
        } else {
            String msg = MessageFormat.format("Trying to change the usercode from {0} to {1}", new Object[]{instance.userCtx.getUser().getUsername(), user.getUserName()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
        }
    }

    @Override
    public SchoolAdminTeacherDomainAuthorizer.SchoolAdminTeacherState_HR_R_S_SG_U setSchoolAdminTeacher() throws Dwo2Exception {
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
                return instance.getUserActions().getStudentModel(instance.userCtx, ctxId);
            } catch (Dwo2Exception e) {
                String msg = MessageFormat.format("Username {0}: Internal error: {1}", new Object[]{instance.userCtx.getUser().getUsername(), e.getMessage()});
                LOG.log(Level.WARNING, msg, e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
        }
    }

}
