/**
 * Copyrighted Mar 7, 2018
 */
package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelDataManager;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelData;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

/**
 *
 * @author Gert van der Plas
 */
class StudentBuilder extends UserBuilder implements StudentDomainAuthorizer.StudentState_HR_R_S_SG_U, StudentDomainAuthorizer.Build {

    private static final Logger LOG = Logger.getLogger(StudentBuilder.class.getName());

    protected StudentDomainAuthorizer instance = new StudentDomainAuthorizer();

//    @Override
//    public StudentDomainAuthorizer.StudentState_HR_R_S_SG_U Student() throws Dwo2Exception {
//        if (this.instance.userCtx.roleType.equals(RoleType.STUDENT)) {
//            return this;
//        } else {
//            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Not a student.");
//        }
//    }
    public StudentBuilder() throws Dwo2Exception {
        super();
    }

    protected StudentBuilder(UserBuilder builder) throws Dwo2Exception {
        super();
        if (builder.instance.userCtx.roleType != null && builder.instance.userCtx.roleType == RoleType.STUDENT) {
            instance.userCtx = builder.instance.userCtx;
        } else {
            String msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: It is not in a student role.", new Object[]{instance.userCtx.getUser().getUsername()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
    }

    @Override
    public void updateStudentModelData(DomStudentModelData data) throws Dwo2Exception {
        long id = MySQLPersistenceId.getNativeId(data);
        PersistentStudentModelData pData = StudentModelDataManager.findEntity(id);
        //check security
        String msg = null;
        //data exists
        if (pData == null) {
            msg = MessageFormat.format("Username {0}: ILLEGAL OPERATION: Can't update, does not exist.", new Object[]{instance.userCtx.getUser().getUsername()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
        }
        PersistentScoContext pScoContext = ScoContextManager.findEntity(pData.getScoID());
        //scoContext exists
        if (pScoContext == null) {
            msg = MessageFormat.format("Username {0}: ILLEGAL OPERATION: Can't update, ScoContext nog given.", new Object[]{instance.userCtx.getUser().getUsername()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
        }
        //School matches with user state
        if (pScoContext.getSchoolID() == null && instance.userCtx.school.getSchoolID().longValue() != pScoContext.getSchoolID().longValue()) {
            msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Can't update, schoolID not given or wrong.", new Object[]{instance.userCtx.getUser().getUsername()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
        }
        //HasRole matches with user state
        if (pData.getPersistentHasRolePK().getUserID().longValue() != instance.userCtx.user.getId().longValue()
                && pData.getPersistentHasRolePK().getSchoolGroupID().longValue() != instance.userCtx.schoolGroup.getSchoolGroupID().longValue()) {
            msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Can't update, hasRole mismatch.", new Object[]{instance.userCtx.getUser().getUsername()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
        }
        //Sco has modelId
        if (pScoContext.getModelID() == null) {
            msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Can't update, modelID not given.", new Object[]{instance.userCtx.getUser().getUsername()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
        }
        //SchoolId of school matches user state
        if (pScoContext.getSchoolID() == null && instance.userCtx.school.getSchoolID().longValue() != pScoContext.getSchoolID().longValue()) {
            msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Can't update, schoolID not given or wrong.", new Object[]{instance.userCtx.getUser().getUsername()});
            LOG.log(Level.WARNING, msg);
            throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
        }
//        PersistentStudentModelContext pModelContext = StudentModelContextManager.findEntity(pScoContext.getModelID().longValue());
//        if (!pModelContext.getModelStructure().matches()) {
//            msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Can't update, schoolID not given or wrong.", new Object[]{instance.userCtx.getUser().getUsername()});
//            LOG.log(Level.WARNING, msg);
//            throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
//        };
        //StudentModel is in School?
//        if(pScoContext.getModelID()==null){
//            msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Can't update, modelID not given.", new Object[]{instance.userCtx.getUser().getUsername()});
//        }
        //StudentModelStructure and StudentModelStructureScore match
        instance.getStudentActions().setStudentModelData(instance.studentCtx, data);
    }
}
