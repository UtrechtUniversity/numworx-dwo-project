package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContext;

/**
 * Manages the school roles and classes registered in HasRole.
 *
 * @author G.A.J. van der Plas
 */
public class SecureTeacherStudentModelManager {

    private static final Logger LOG = Logger.getLogger(SecureTeacherStudentModelManager.class.getName());
    public static List<DomStudentModelContext> getList() throws Dwo2Exception {
        List<DomStudentModelContext> src;
        src = StoredRestManager.getInstance().getList("rest/secure/teacher/studentmodel/getList", RestListClassTypes.DomStudentModelContext);
        LOG.log(Level.FINE, "Retrieved list of studentmodels of the teacher with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername()});
        return src;
    }

    public static DomStudentModelContext addModel(DomStudentModelContext submit) throws Dwo2Exception {
        DomStudentModelContext src;
        RestStudentModelContext rest = new RestStudentModelContext();
        rest.setRestContext(RestAuthenticator.getInstance().getContext());
        rest.setDomStudentModelContext(submit);

        DomStudentModelContext result = StoredRestManager.getInstance().put("rest/secure/teacher/studentmodel/add", DomStudentModelContext.class, rest);
        LOG.log(Level.FINE, "Added studentmodel of teacher with username {0} to his school.", new Object[]{RestAuthenticator.getInstance().getUsername()});
        return result;
    }
}
