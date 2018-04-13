/** Copyrighted Feb 12, 2018 */
package fi.dwo.server.PersistentDataManagers.actions;

import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentUser;
import fi.dwo.server.PersistentDataManagers.access.TeacherDomainAuthorizer;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import fi.dwo.server.PersistentDataManagers.util.SchoolClassUtilManager;
import fi.dwo.server.PersistentDataManagers.util.StudentInClassManager;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.exceptions.Dwo2RestException;

/**
 *
 * @author plas0006
 */
public class MySQLTeacherActions implements TeacherActions {

    private static final Logger LOG = Logger.getLogger(MySQLTeacherActions.class.getName());

    public PersistentStudentModelContext addStudentModel(TeacherDomainAuthorizer.Context context, PersistentStudentModelContext model) throws Dwo2Exception {
        try {
                PersistentStudentModelContext pModel = new PersistentStudentModelContext();
                pModel.setModelStructure(model.getModelStructure());
                pModel.setSchoolID(model.getSchoolID());
                pModel.setPublishState(PublishState.published);                
                return StudentModelContextManager.create(pModel);
        } catch (Exception e) {
            String msg = MessageFormat.format("Username {0}: Internal error: {1}", new Object[]{context.getUserCtx().getUser().getUsername(), e.getMessage()});
            LOG.log(Level.WARNING, msg, e);
            throw new Dwo2RestException(Dwo2ExceptionCode.Rest_InternalError, msg);
        }
    }

    public List<PersistentStudentModelContext> getStudentModels(TeacherDomainAuthorizer.Context context) throws Dwo2Exception {           
            List<PersistentStudentModelContext> pModels =  StudentModelContextManager.findEntities(context.getUserCtx().getSchool());
            return pModels;
    }

    @Override
    public List<PersistentSchoolClass> getSchoolClasses(TeacherDomainAuthorizer.Context context) throws Dwo2Exception {
        List<PersistentSchoolClass> schoolClasses = SchoolClassUtilManager.getSchoolClassesOfTeacher(context.getUserCtx().getHasRole());
            return schoolClasses;
    }

    @Override
    public List<PersistentUser> getTeachersStudents(TeacherDomainAuthorizer.Context context) throws Dwo2Exception {
        Map<String, PersistentUser> students = new HashMap<>();
        List<PersistentSchoolClass> schoolClasses  = getSchoolClasses(context);
        for(PersistentSchoolClass sc : schoolClasses){
            StudentInClassManager.findEntities(sc).forEach((k-> students.putIfAbsent(k.getUser().buildPersistenceId().getIdString(), k.getUser())));
        }
        List<PersistentUser> results = new ArrayList<>(students.size());
        students.forEach((k, v) -> results.add(v));
        return results;
    }


}
