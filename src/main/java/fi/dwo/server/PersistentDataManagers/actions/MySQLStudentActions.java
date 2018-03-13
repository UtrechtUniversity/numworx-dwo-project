/**
 * Copyrighted Mar 9, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.entities.PersistentScoContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.core.StudentModelDataManager;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

/**
 *
 *
 * @author Gert van der Plas
 */
public class MySQLStudentActions implements StudentActions {

    private static final Logger LOG = Logger.getLogger(MySQLStudentActions.class.getName());

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

}
