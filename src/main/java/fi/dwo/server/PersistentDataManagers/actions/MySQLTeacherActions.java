/** Copyrighted Feb 12, 2018 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.core.StudentModelManager;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

/**
 *
 * @author plas0006
 */
public class MySQLTeacherActions extends MySQLSchoolAdminTeacherActions implements TeacherActions {

    private static final Logger LOG = Logger.getLogger(MySQLTeacherActions.class.getName());

    public PersistentStudentModelContext addStudentModel(TeacherDomainAuthorizer.TeacherPersistentContext context, PersistentStudentModelContext model) throws Dwo2Exception {
        try {
                PersistentStudentModelContext pModel = new PersistentStudentModelContext();
                pModel.setModelStructure(model.getModelStructure());
                pModel.setSchoolID(model.getSchoolID());
                pModel.setPublishState(PublishState.published);                
                return StudentModelManager.create(pModel);
        } catch (Exception e) {
            String msg = MessageFormat.format("Username {0}: Internal error: {1}", new Object[]{context.getUser().getUsername(), e.getMessage()});
            LOG.log(Level.WARNING, msg, e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
    }

    public List<DomStudentModelContext> getStudentModels(TeacherDomainAuthorizer.TeacherPersistentContext context) throws Dwo2Exception {           
            List<PersistentStudentModelContext> pModels =  StudentModelManager.findEntities(context.getSchool());
            List<DomStudentModelContext>  result = new ArrayList<>(pModels.size());
            pModels.stream().forEach(m -> result.add(m.buildDomStudentModelContext()));
            return result;
    }


}
