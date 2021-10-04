/** Copyrighted Mar 13, 2018 */
package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentDwoProfile;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.server.PersistentDataManagers.access.SchoolAdminTeacherDomainAuthorizer.SchoolAdminTeacherState_HR_R_S_SG_U;
import fi.dwo.server.PersistentDataManagers.core.DwoProfileManager;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

/**
 *
 * @author plas0006
 */
class SchoolAdminTeacherBuilder implements SchoolAdminTeacherDomainAuthorizer.SchoolAdminTeacherState_HR_R_S_SG_U {

    private static final Logger LOG = Logger.getLogger(SchoolAdminTeacherBuilder.class.getName());

    private SchoolAdminTeacherDomainAuthorizer instance;

//    public SchoolAdminTeacherBuilder(UserDomainAuthorizer auth) throws Dwo2Exception {
//        instance = new SchoolAdminTeacherDomainAuthorizer(auth);
//    }
    public SchoolAdminTeacherBuilder() throws Dwo2Exception {
        super();
        instance = new SchoolAdminTeacherDomainAuthorizer();
    }

//    @Override
//    public SchoolAdminTeacherDomainAuthorizer.SchoolAdminTeacherState_HR_R_S_SG_U buildSchoolAdminTeacher() throws Dwo2Exception {
//        if (instance.getContext().getUserCtx().roleType == RoleType.SCHOOLADMIN || instance.getContext().getUserCtx().roleType == RoleType.TEACHER) {
//            return this;
//        } else {
//            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Not a schooladmin or teacher");
//        }
//    }
    @Override
    public TeacherDomainAuthorizer.TeacherState_HR_R_S_SG_U setTeacher() throws Dwo2Exception {
        TeacherBuilder builder = new TeacherBuilder();        
        return builder.init(this.instance.getContext());
        //return new TeacherBuilder(instance).setTeacher();
    }

    @Override
    public PersistentStudentModelContext getStudentModel(DomScoContextId ctxId) throws Dwo2Exception {
        PersistentScoContext scoCtx = ScoContextManager.findEntity(MySQLPersistenceId.getNativeId(ctxId));
        if (instance.getContext().getUserCtx().school.getSchoolID() != scoCtx.getSchoolID().longValue()) {
            String msg = MessageFormat.format("Username {0}: SchoolId {1} of sco mismatches hasrole for the given StudentModelContext: {2}", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), instance.getContext().getUserCtx().getSchool().getSchoolID(), ctxId.getId().toString()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
        } else if (scoCtx.getSchoolID() == null) {
            String msg = MessageFormat.format("StudentModelContext not set for Sco {0}", new Object[]{ctxId.getId().toString()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_StudentModelNotSet, msg);
        } else {
            try {
                return instance.schoolAdminTeacherActions.getStudentModel(instance.getContext().getUserCtx(), ctxId);
            } catch (Dwo2Exception e) {
                String msg = MessageFormat.format("Username {0}: Internal error: {1}", new Object[]{instance.getContext().getUserCtx().getUser().getUsername(), e.getMessage()});
                LOG.log(Level.WARNING, msg, e);
                throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
        }
    }

    public SchoolAdminTeacherState_HR_R_S_SG_U init(UserDomainAuthorizer.Context ctx) throws Dwo2Exception {
        if (ctx.getUserCtx().roleType == RoleType.SCHOOLADMIN || ctx.getUserCtx().roleType == RoleType.TEACHER) {
            this.instance.setContext(new SchoolAdminTeacherDomainAuthorizer.Context(ctx));
            return this;
        } else {
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Not a schooladmin or teacher");
        }
    }

	@Override
	public int countStudents(DomScoContextId sco) throws Dwo2Exception {
      PersistentScoContext scoCtx = ScoContextManager.findEntity(MySQLPersistenceId.getNativeId(sco));
      if (scoCtx == null) return 0;
	  return instance.schoolAdminTeacherActions.countStudents(instance.getContext().getUserCtx().hasRole, scoCtx);
	}

	@Override
	public List<DomStudentModelContext> getReducedStudentModels(DomDwoProfileId profile) throws Dwo2Exception {
		PersistentDwoProfile persistentProfile = DwoProfileManager.findEntity(MySQLPersistenceId.getNativeId(profile));
        List<PersistentStudentModelContext> pModels = instance.schoolAdminTeacherActions.getReducedStudentModels(instance.getContext(), persistentProfile);
        List<DomStudentModelContext> result = new ArrayList<>(pModels.size());
        pModels.forEach((m) -> result.add(m.buildDomStudentModelContext()));
        return result;
	}

	@Override
	public DomStudentModelContext getStudentModel(DomStudentModelContextId id) throws Dwo2Exception {
    	PersistentStudentModelContext result = instance.schoolAdminTeacherActions.getStudentModel(instance.getContext(), id);
    	return result.buildDomStudentModelContext();
	}

}
