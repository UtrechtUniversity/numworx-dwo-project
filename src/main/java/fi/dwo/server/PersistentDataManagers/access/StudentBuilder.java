/**
 * Copyrighted Mar 7, 2018
 */
package fi.dwo.server.PersistentDataManagers.access;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
import fi.dwo.server.PersistentDataManagers.core.ScoContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelDataManager;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
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

    /**
     * If a studentModelData id is set, and update is expected. If it is null an insert or update is expected.
     * @param data
     * @throws Dwo2Exception 
     */
    @Override
    public DomStudentModelData setStudentModelData(DomStudentModelData data) throws Dwo2Exception {
        String msg = null;
        PersistentStudentModelData pData;
        PersistentScoContext pScoContext = ScoContextManager.findEntity(MySQLPersistenceId.getNativeId(data.getScoContextId()));
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
        //find StudentModelData
        Long id = MySQLPersistenceId.getNativeId(data);
        if (id != null) {
            pData = StudentModelDataManager.findEntity(id);
            if (pData != null) {
                //data exists
                //check if HasRole matches with user state
                if (pData.getPersistentHasRolePK().getUserID().longValue() != instance.userCtx.user.getId().longValue()
                        && pData.getPersistentHasRolePK().getSchoolGroupID().longValue() != instance.userCtx.schoolGroup.getSchoolGroupID().longValue()) {
                    msg = MessageFormat.format("Username {0}: ILLEGAL USER-OPERATION: Can't update, hasRole mismatch.", new Object[]{instance.userCtx.getUser().getUsername()});
                    LOG.log(Level.WARNING, msg);
                    throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
                }
            } else if (pData == null) {                
                    //StudentModelData id was given but not found.
                    msg = MessageFormat.format("Username {0}: ILLEGAL OPERATION: Can't update, does not exist.", new Object[]{instance.userCtx.getUser().getUsername()});
                    LOG.log(Level.WARNING, msg);
                    throw new Dwo2Exception(Dwo2ExceptionCode.User_IllegalAction, msg);
                }
        }else{
            //create a PersistentStudentModelData and do an insert or update
                pData = new PersistentStudentModelData();
                pData.setScoID(MySQLPersistenceId.getNativeId(data.getScoContextId()));
                pData.setPersistentHasRolePK(instance.userCtx.hasRole.getPersistentHasRolePK());                
        }
            
            //insert or updateModelDataScore in pData.
            pData.setModelData(data.getDomStudentModelStructureScore());
            return instance.getStudentActions().setStudentModelData(instance.studentCtx, pData);
        }

        @Override
        public DomStudentModelData getStudentModelData
        (DomScoContextId domScoId) throws Dwo2Exception {
            return instance.getStudentActions().getStudentModelData(instance.studentCtx, domScoId).buildDomStudentModelData();
        }

    }
