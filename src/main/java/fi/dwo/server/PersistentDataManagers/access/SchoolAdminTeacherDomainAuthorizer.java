package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.actions.SchoolAdminTeacherActions;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
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
public class SchoolAdminTeacherDomainAuthorizer extends UserDomainAuthorizer {

    private static final Logger LOG = Logger.getLogger(SchoolAdminTeacherDomainAuthorizer.class.getName());
    protected SchoolAdminTeacherPersistentContext schoolAdminTeacherCtx;
    private SchoolAdminTeacherActions schoolAdminTeacherActions;

    public class SchoolAdminTeacherPersistentContext extends UserPersistentContext {

        public SchoolAdminTeacherPersistentContext() {

        }

        public SchoolAdminTeacherPersistentContext(UserPersistentContext ctx) {
            this.hasRole = ctx.hasRole;
            this.roleType = ctx.roleType;
            this.school = ctx.school;
            this.schoolGroup = ctx.schoolGroup;
            this.user = ctx.user;
        }
    }

    public interface SchoolAdminTeacherState_HR_R_S_SG_U extends UserState_HR_R_S_SG_U {
        TeacherState_HR_R_S_SG_U setTeacher() throws Dwo2Exception;

    }

    protected SchoolAdminTeacherDomainAuthorizer() {
        super();
    }

    protected SchoolAdminTeacherDomainAuthorizer(UserDomainAuthorizer userAuth) {
        super();
        schoolAdminTeacherCtx = new SchoolAdminTeacherPersistentContext(userAuth.userCtx);
        //schoolAdminTeacherActions = new SchoolAdminTeacherActions();
    }

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

    protected static class Builder extends UserBuilder implements SchoolAdminTeacherState_HR_R_S_SG_U,
            Build {

        private SchoolAdminTeacherDomainAuthorizer instance;

        public Builder(UserDomainAuthorizer auth) throws Dwo2Exception {
            instance = new SchoolAdminTeacherDomainAuthorizer(auth);
        }

        @Override
        public PersistentUser getUser() {
            return instance.schoolAdminTeacherCtx.user;
        }

        @Override
        public PersistentHasRole getHasRole() {
            return instance.schoolAdminTeacherCtx.hasRole;
        }

        @Override
        public PersistentSchool getSchool() {
            return instance.schoolAdminTeacherCtx.school;
        }

        @Override
        public PersistentSchoolGroup getSchoolGroup() {
            return instance.schoolAdminTeacherCtx.schoolGroup;
        }

        @Override
        public RoleType getRoleType() {
            return instance.schoolAdminTeacherCtx.roleType;
        }

        @Override
        public SchoolAdminTeacherState_HR_R_S_SG_U buildSchoolAdminTeacher() throws Dwo2Exception {
            if (instance.schoolAdminTeacherCtx.roleType == RoleType.SCHOOLADMIN || instance.schoolAdminTeacherCtx.roleType == RoleType.TEACHER) {
                return this;
            } else {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Not a schooladmin or teacher");
            }
        }

        @Override
        public TeacherState_HR_R_S_SG_U setTeacher() throws Dwo2Exception {
            return new TeacherDomainAuthorizer.Builder(instance).setTeacher();
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
                    return instance.schoolAdminTeacherActions.getStudentModel(instance.userCtx, ctxId);
                } catch (Dwo2Exception e) {
                    String msg = MessageFormat.format("Username {0}: Internal error: {1}", new Object[]{instance.userCtx.getUser().getUsername(), e.getMessage()});
                    LOG.log(Level.WARNING, msg, e);
                    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
                }
            }
        }        
    }
}
