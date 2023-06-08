/**
 * Copyrighted Mar 9, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer.Context;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelDataManager;
import fi.dwo.server.PersistentDataManagers.core.XapiManager;
import fi.dwo.server.PersistentDataManagers.util.StudentModelDataUtilManager;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.UriInfo;

import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.util.DwoDateUtilities;

/**
 *
 *
 * @author Gert van der Plas
 */
public class MySQLStudentActions implements StudentActions {

    public static final Logger LOG = Logger.getLogger(MySQLStudentActions.class.getName());

    @Override
    public void setStudentModelData(StudentDomainAuthorizer.Context ctx, PersistentStudentModelData data) throws Dwo2Exception {
        if (data.buildPersistenceId() == null) {  //data is not expected to exist.
            try {
                StudentModelDataManager.create(data).buildDomStudentModelData();
            } catch (PersistenceException e) {
                //fails if data exists, clearly a system error or we are out of sync (working in more than one copy of the sco).
                //it is possible that studentmodeldata was inserted between the check and the insert.
                String msg = MessageFormat.format("Failed creating PersistentStudentModelData {0}", data.getModelDataId());
                LOG.log(Level.WARNING, msg, e);
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
        } else {
            //data exists, should be updated but fails if version mismatch.
            try {
                StudentModelDataManager.edit(data);
                data.buildDomStudentModelData();
            } catch (PersistenceException e) {
                String msg = MessageFormat.format("Failed merging  PersistentStudentModelData {0}", data.getModelDataId());
                LOG.log(Level.WARNING, msg, e);
                throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, msg);
            }
        }
    }

    @Override
    public PersistentStudentModelData getStudentModelData(StudentDomainAuthorizer.Context ctx, PersistentScoContext pScoContext) throws Dwo2Exception {
        return StudentModelDataManager.findEntity(pScoContext, ctx.getUserCtx().getHasRole());
    }

    @Override
    public DomStudentModelDataScore getStudentModelData(StudentDomainAuthorizer.Context ctx, PersistentStudentModelContext pStudentModel) throws Dwo2Exception {
        //get list of al StudentModelData
        PersistentHasRole hasRole = ctx.getUserCtx().getHasRole();

        
        DomStudentModelStructureScore score = StudentModelDataUtilManager.calculateStudentModelScore(pStudentModel, hasRole);
        DomStudentModelDataScore result = new DomStudentModelDataScore();
        result.setDomStudentModelStructureScore(score);
        DomStudentModelContextId id = new DomStudentModelContextId();
        id.setId(pStudentModel.buildPersistenceId());
        id.setOptLock(pStudentModel.getOptlock());
        result.setModelId(id);
        result.setFetchTimeStamp(DwoDateUtilities.getCurrentDwoUnixTimeStamp());
        return result;
    }

    @Override
        public List<PersistentStudentModelContext> getStudentModels(StudentDomainAuthorizer.Context context) throws Dwo2Exception {           
            List<PersistentStudentModelContext> pModels =  StudentModelContextManager.findEntities(context.getUserCtx().getSchool());
            return pModels;
    }

    @Override
    public DomLRS getLRS(Context context, UriInfo info) {
      PersistentUser user = context.getUserCtx().user;
      PersistentSchool school = context.getUserCtx().school;
      return XapiManager.getLRS(user, school, info);
    }


}
