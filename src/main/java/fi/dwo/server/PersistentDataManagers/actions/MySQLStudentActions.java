/**
 * Copyrighted Mar 9, 2018
 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.entities.PersistentStudentModelData;
import fi.dwo.server.PersistentDataManagers.access.StudentDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.core.StudentModelDataManager;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;

/**
 *
 * @author Gert van der Plas
 */
public class MySQLStudentActions implements StudentActions {

    private static final Logger LOG = Logger.getLogger(MySQLStudentActions.class.getName());

    @Override
    public void setStudentModelData(StudentDomainAuthorizer.StudentPersistentContext ctx, PersistentStudentModelData data) throws Dwo2Exception {
        try{
            StudentModelDataManager.edit(data);
        }catch(PersistenceException e){
            String msg = MessageFormat.format("Failed merging  PersistentStudentModelData {0}", data.getModelDataId());
            LOG.log(Level.WARNING, msg,e);
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError,msg);
        }
    }

    @Override
    public PersistentStudentModelData getStudentModelData(StudentDomainAuthorizer.StudentPersistentContext ctx, DomScoContextId domScoId) throws Dwo2Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
