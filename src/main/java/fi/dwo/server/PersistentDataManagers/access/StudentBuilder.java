/**
 * Copyrighted Mar 7, 2018
 */
package fi.dwo.server.PersistentDataManagers.access;

import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

/**
 *
 * @author Gert van der Plas
 */
class StudentBuilder extends UserBuilder implements StudentDomainAuthorizer.StudentState_HR_R_S_SG_U, StudentDomainAuthorizer.Build {

    protected StudentDomainAuthorizer instance = new StudentDomainAuthorizer();

    @Override
    public StudentDomainAuthorizer.StudentState_HR_R_S_SG_U setStudent() throws Dwo2Exception {
        if (this.instance.userCtx.roleType.equals(RoleType.STUDENT)) {
            return this;
        } else {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Not a student.");
        }
    }

    public StudentBuilder() throws Dwo2Exception {
        super();
    }

    protected StudentBuilder(UserBuilder builder) throws Dwo2Exception {
        super();
        instance.userCtx = builder.instance.userCtx;
    }


//        private StudentDomainAuthorizer instance;
//
//        public Builder(UserDomainAuthorizer auth) throws Dwo2Exception {
//            instance = new StudentDomainAuthorizer(auth);
//        }
//
//        @Override
//        public PersistentUser getUser() {
//            return instance.schoolAdminTeacherCtx.user;
//        }
//
//        @Override
//        public PersistentHasRole getHasRole() {
//            return instance.schoolAdminTeacherCtx.hasRole;
//        }
//
//        @Override
//        public PersistentSchool getSchool() {
//            return instance.schoolAdminTeacherCtx.school;
//        }
//
//        @Override
//        public PersistentSchoolGroup getSchoolGroup() {
//            return instance.schoolAdminTeacherCtx.schoolGroup;
//        }
//
//        @Override
//        public RoleType getRoleType() {
//            return instance.schoolAdminTeacherCtx.roleType;
//        }
//
//        @Override
//        public SchoolAdminTeacherState_HR_R_S_SG_U setSchoolAdminTeacher() throws Dwo2Exception {
//            if (instance.schoolAdminTeacherCtx.roleType == RoleType.SCHOOLADMIN || instance.schoolAdminTeacherCtx.roleType == RoleType.TEACHER) {
//                return this;
//            } else {
//                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Not a schooladmin or teacher");
//            }
//        }
//
//        @Override
//        public TeacherState_HR_R_S_SG_U setTeacher() throws Dwo2Exception {
//            return new TeacherDomainAuthorizer.Builder(instance).setTeacher();
//        }
//
//        @Override
//        public PersistentStudentModelContext getStudentModel(DomScoContextId ctxId) throws Dwo2Exception {
//            PersistentScoContext scoCtx = ScoContextManager.findEntity(MySQLPersistenceId.getNativeId(ctxId));
//
//            if (instance.userCtx.school.getSchoolID() != scoCtx.getSchoolID().longValue()) {
//                String msg = MessageFormat.format("Username {0}: SchoolId {1} of sco mismatches hasrole for the given StudentModelContext: {2}", new Object[]{instance.userCtx.getUser().getUsername(), instance.userCtx.school.getSchoolID(), ctxId.getId().toString()});
//                LOG.log(Level.WARNING, msg);
//                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
//            } else if (scoCtx.getSchoolID() == null) {
//                String msg = MessageFormat.format("StudentModelContext not set for Sco {0}", new Object[]{ctxId.getId().toString()});
//                LOG.log(Level.WARNING, msg);
//                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_StudentModelNotSet, msg);
//            } else {
//                try {
//                    return instance.schoolAdminTeacherActions.getStudentModel(instance.userCtx, ctxId);
//                } catch (Dwo2Exception e) {
//                    String msg = MessageFormat.format("Username {0}: Internal error: {1}", new Object[]{instance.userCtx.getUser().getUsername(), e.getMessage()});
//                    LOG.log(Level.WARNING, msg, e);
//                    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
//                }
//            }
//        }
//        private StudentDomainAuthorizer instance;
//
//        public Builder(UserDomainAuthorizer auth) throws Dwo2Exception {
//            instance = new StudentDomainAuthorizer(auth);
//        }
//
//        @Override
//        public PersistentUser getUser() {
//            return instance.schoolAdminTeacherCtx.user;
//        }
//
//        @Override
//        public PersistentHasRole getHasRole() {
//            return instance.schoolAdminTeacherCtx.hasRole;
//        }
//
//        @Override
//        public PersistentSchool getSchool() {
//            return instance.schoolAdminTeacherCtx.school;
//        }
//
//        @Override
//        public PersistentSchoolGroup getSchoolGroup() {
//            return instance.schoolAdminTeacherCtx.schoolGroup;
//        }
//
//        @Override
//        public RoleType getRoleType() {
//            return instance.schoolAdminTeacherCtx.roleType;
//        }
//
//        @Override
//        public SchoolAdminTeacherState_HR_R_S_SG_U setSchoolAdminTeacher() throws Dwo2Exception {
//            if (instance.schoolAdminTeacherCtx.roleType == RoleType.SCHOOLADMIN || instance.schoolAdminTeacherCtx.roleType == RoleType.TEACHER) {
//                return this;
//            } else {
//                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Not a schooladmin or teacher");
//            }
//        }
//
//        @Override
//        public TeacherState_HR_R_S_SG_U setTeacher() throws Dwo2Exception {
//            return new TeacherDomainAuthorizer.Builder(instance).setTeacher();
//        }
//
//        @Override
//        public PersistentStudentModelContext getStudentModel(DomScoContextId ctxId) throws Dwo2Exception {
//            PersistentScoContext scoCtx = ScoContextManager.findEntity(MySQLPersistenceId.getNativeId(ctxId));
//
//            if (instance.userCtx.school.getSchoolID() != scoCtx.getSchoolID().longValue()) {
//                String msg = MessageFormat.format("Username {0}: SchoolId {1} of sco mismatches hasrole for the given StudentModelContext: {2}", new Object[]{instance.userCtx.getUser().getUsername(), instance.userCtx.school.getSchoolID(), ctxId.getId().toString()});
//                LOG.log(Level.WARNING, msg);
//                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
//            } else if (scoCtx.getSchoolID() == null) {
//                String msg = MessageFormat.format("StudentModelContext not set for Sco {0}", new Object[]{ctxId.getId().toString()});
//                LOG.log(Level.WARNING, msg);
//                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_StudentModelNotSet, msg);
//            } else {
//                try {
//                    return instance.schoolAdminTeacherActions.getStudentModel(instance.userCtx, ctxId);
//                } catch (Dwo2Exception e) {
//                    String msg = MessageFormat.format("Username {0}: Internal error: {1}", new Object[]{instance.userCtx.getUser().getUsername(), e.getMessage()});
//                    LOG.log(Level.WARNING, msg, e);
//                    throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
//                }
//            }
//        }
}
