package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolGroup;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

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
    protected SchoolAdminTeacherPersistentContext context;
    //private SchoolAdminTeacherActions schoolAdminTeacherActions;

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
        context = new SchoolAdminTeacherPersistentContext(userAuth.context);
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

    protected static class Builder implements SchoolAdminTeacherState_HR_R_S_SG_U,
            Build {

        private SchoolAdminTeacherDomainAuthorizer instance;

        public Builder(UserDomainAuthorizer auth) throws Dwo2Exception {
            instance = new SchoolAdminTeacherDomainAuthorizer(auth);
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

        @Override
        public SchoolAdminTeacherState_HR_R_S_SG_U setSchoolAdminTeacher() throws Dwo2Exception {
            if (instance.context.roleType == RoleType.SCHOOLADMIN || instance.context.roleType == RoleType.TEACHER) {
                return this;
            } else {
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Not a schooladmin or teacher");
            }
        }

        @Override
        public TeacherState_HR_R_S_SG_U setTeacher() throws Dwo2Exception {
            return new TeacherDomainAuthorizer.Builder(instance).setTeacher();
        }

    }
}
