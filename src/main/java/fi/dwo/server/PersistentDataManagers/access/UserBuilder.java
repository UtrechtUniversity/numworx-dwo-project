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
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserPersistentContext;
import fi.dwo.server.PersistentDataManagers.access.UserDomainAuthorizer.UserState_U;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolGroupManager;
import fi.dwo.server.PersistentDataManagers.core.SchoolManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.UserManager;
import fi.dwo.server.rest.jaxrsfilters.DwoUserPrincipal;

import java.security.Principal;
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
class UserBuilder implements UserDomainAuthorizer.UserState_U, UserDomainAuthorizer.UserState_HR_R_S_SG_U
        {

    private static final Logger LOG = Logger.getLogger(UserBuilder.class.getName());

    protected UserDomainAuthorizer instance;

//    protected UserBuilder(AnonDomainAuthorizer auth) throws Dwo2Exception {
//    }
    protected UserBuilder() throws Dwo2Exception {
        super();
        instance = new UserDomainAuthorizer();
    }

    /**
     * Verifies and stores the PersistentUser into the userCtx.
     *
     * @param username
     * @return
     * @throws Dwo2Exception
     */
    UserDomainAuthorizer.UserState_U setUser(String username) throws Dwo2Exception {
        this.instance.getContext().getUserCtx().setUser(UserManager.findByUserName(username));
        if (instance.getContext().getUserCtx().getUser() == null) {
            LOG.log(Level.WARNING, "Username {0}: Internal error user does not exist.", new Object[]{username});
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Internal error user does not exist.");
        }
        return this;
    }

    /**
     * Verifies the existence of the default hasRole in the PersistentUser and
     * sets it as the active hasRole into the userCtx.
     *
     * @return
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception If the default hasRole
     * is invalid.
     */
    @Override
    public UserDomainAuthorizer.UserState_HR_R_S_SG_U setDefaultHasRole() throws Dwo2Exception {
        PersistentHasRole phr = null;
        //determine default hasRole.
        PersistentHasRolePK phrPK;
        this.instance.getContext().getUserCtx().setSchoolGroup(this.instance.getContext().getUserCtx().getUser().getPersistentSchoolGroup());
        phrPK = new PersistentHasRolePK(this.instance.getContext().getUserCtx().getUser().getId(), this.instance.getContext().getUserCtx().getUser().getPersistentSchoolGroup().getSchoolGroupID());
        return setHasRole(phrPK);
    }

    /**
     * Verifies the existence of the hasRole in the PersistentUser and sets it
     * as the active hasRole into the userCtx.
     *
     * @param hr
     * @param r
     * @return
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
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
     * @param phrPK
     * @return
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    private UserDomainAuthorizer.UserState_HR_R_S_SG_U setHasRole(PersistentHasRolePK phrPK) throws Dwo2Exception {
        PersistentHasRole phr;
        //determine default hasRole.
        phr = HasRoleManager.findEntity(phrPK);
        if (phr == null || phr.getUser().getId().longValue() != instance.getContext().getUserCtx().user.getId().longValue() //users is valid
                || phr.getSchoolGroup().getSchoolGroupID().longValue() != phrPK.getSchoolGroupID().longValue() //requested hasRole exists
                ) {
            String msg = MessageFormat.format("Hasrole {1} for userlogin {0} could not be found.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), this.instance.getContext().getUserCtx().getHasRole().getPersistentHasRolePK()});
            LOG.log(Level.SEVERE, msg);
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
        instance.getContext().getUserCtx().setSchoolGroup(phr.getSchoolGroup());
        instance.getContext().getUserCtx().school = phr.getSchoolGroup().getSchool();
        instance.getContext().getUserCtx().setHasRole(phr);
        instance.getContext().getUserCtx().setRoleType(RoleType.values()[phr.getSchoolGroup().getRole().getGroupID().intValue()]);
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
    public StudentDomainAuthorizer.StudentState_HR_R_S_SG_U buildStudent() throws Dwo2Exception {
        StudentBuilder builder = new StudentBuilder();
        return builder.init(this.instance.getContext());
    }

    /**
     * Verifies the existence of the hasRole for the given RoleType and stores
     * it and the RoleType into the userCtx.
     *
     * @return
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    @Override
    public DwoAdminDomainAuthorizer.DwoAdminState_HR_R_S_SG_U buildDwoAdmin() throws Dwo2Exception {
        DwoAdminBuilder builder = new DwoAdminBuilder();
        return builder.init(this.instance.getContext());
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
        PersistentHasRole phr;
        phr = HasRoleManager.findEntity(phrPK);
        if (phr == null) {
            String msg = MessageFormat.format("Hasrole {1} for userlogin {0} could not be found.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), this.instance.getContext().getUserCtx().getHasRole().getPersistentHasRolePK()});
            LOG.log(Level.SEVERE, msg);
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
        Long roleId = (long) RoleType.NONE.ordinal();
        try {
            roleId = phr.getSchoolGroup().getRole().getGroupID();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "RoleId of hasRole {1} for userlogin {0} could not be found.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), this.instance.getContext().getUserCtx().getHasRole().getPersistentHasRolePK()});
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Current Role could not be found.");
        }
        if (roleId.intValue() == r.ordinal()) {
            this.instance.getContext().getUserCtx().setHasRole(phr);
            this.instance.getContext().getUserCtx().setRoleType(r);
            return this;
        } else {
            String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Trying to access non-existing role by user with usercode {0}.", new Object[]{instance.getContext().getUserCtx().getUser().getUsername()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2RestException(Dwo2ExceptionCode.User_IllegalAction, msg);
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
        UserPersistentContext userCtx = instance.getContext().getUserCtx();
        PersistentSchool school = userCtx.getSchool();
        if(school == null) {
          PersistentSchoolGroup schoolGroup = getSchoolGroup();
          school = schoolGroup.getSchool();
          
          userCtx.setSchool(school);
        }
        return school;
    }

    @Override
    public PersistentSchoolGroup getSchoolGroup() {
        UserPersistentContext userCtx = instance.getContext().getUserCtx();
        PersistentSchoolGroup schoolGroup = userCtx.getSchoolGroup();
        if(schoolGroup == null) {
          schoolGroup = SchoolGroupManager.findEntity(userCtx.getHasRole().getPersistentHasRolePK().getSchoolGroupID());
          userCtx.setSchoolGroup(schoolGroup);
        }
        return schoolGroup;
    }

    @Override
    public RoleType getRoleType() {
        return instance.getContext().getUserCtx().getRoleType();
    }

    @Override
    public DomUserFull UpdateAccount(DomUserFull user) throws Dwo2Exception {
        String userName = user.getUserName();
        String realm = instance.getContext().getUserCtx().getRealm();
		if (realm != null)
        	userName +=  realm;
		if (userName.matches(instance.getContext().getUserCtx().getUser().getUsername())) {
            return instance.getUserActions().UpdateAccount(instance.getContext().getUserCtx().getUser(), user).buildDomUserFull(realm);
        } else {
            String msg = MessageFormat.format("Trying to change the usercode from {0} to {1}", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), userName});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
        }
    }

    @Override
    public SchoolAdminTeacherDomainAuthorizer.SchoolAdminTeacherState_HR_R_S_SG_U buildSchoolAdminTeacher() throws Dwo2Exception {
        SchoolAdminTeacherBuilder builder = new SchoolAdminTeacherBuilder();
        return builder.init(this.instance.getContext());
//        return new SchoolAdminTeacherDomainAuthorizer.Builder(instance).buildSchoolAdminTeacher();
    }

    @Override
    public PersistentStudentModelContext getStudentModel(DomScoContextId ctxId) throws Dwo2Exception {
        PersistentScoContext scoCtx = ScoContextManager.findEntity(MySQLPersistenceId.getNativeId(ctxId));
        if (instance.getContext().getUserCtx().school.getSchoolID() != scoCtx.getSchoolID().longValue()) {
            String msg = MessageFormat.format("Username {0}: SchoolId {1} of sco mismatches hasrole for the given StudentModelContext: {2}", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), instance.getContext().getUserCtx().school.getSchoolID(), ctxId.getId().toString()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
        } else if (scoCtx.getSchoolID() == null) {
            String msg = MessageFormat.format("StudentModelContext not set for Sco {0}", new Object[]{ctxId.getId().toString()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_StudentModelNotSet, msg);
        } else {
            try {
                return instance.getUserActions().getStudentModel(instance.getContext().getUserCtx(), ctxId);
            } catch (Dwo2Exception e) {
                String msg = MessageFormat.format("Username {0}: Internal error: {1}", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), e.getMessage()});
                LOG.log(Level.WARNING, msg, e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
        }
    }

    public void init(AnonDomainAuthorizer.Context ctx) {
        this.instance.setContext(new UserDomainAuthorizer.Context(ctx));
    }


    @Override
    public UserDomainAuthorizer.Context getContext() {
        return instance.getContext();
    }

    @Override
    public void setContext(UserDomainAuthorizer.Context context) {
        instance.setContext(context);
    }

    @Override
    public String getRealm() {
      return instance.getContext().getUserCtx().getRealm();
    }

    @Override
    public UserState_U setRealm(String realm) {
      instance.getContext().getUserCtx().setRealm(realm);
      return this;
    }

    
	UserState_U setUser(Principal principal) throws Dwo2Exception {
		PersistentUser user;
		if (principal instanceof DwoUserPrincipal)
			user = ((DwoUserPrincipal) principal).getUser();
		else
			user = UserManager.findByUserName(principal.getName());
        this.instance.getContext().getUserCtx().setUser(user);
        if (user == null) {
            LOG.log(Level.WARNING, "Username {0}: Internal error user does not exist.", new Object[]{principal});
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Internal error user does not exist.");
        }
        return this;
	}

}
