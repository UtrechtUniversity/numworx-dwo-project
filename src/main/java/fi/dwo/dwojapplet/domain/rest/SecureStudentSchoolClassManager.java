package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomNewSchoolClass4Student;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.RestListClassTypes;
import fi.dwo.rest.entities.RestNewSchoolClass4Student;
import fi.dwo.rest.entities.RestSchoolClass;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the school roles and classes registered in HasRole.
 *
 * @author G.A.J. van der Plas
 */
public class SecureStudentSchoolClassManager {

    private static final Logger LOG = Logger.getLogger(SecureStudentSchoolClassManager.class.getName());

    public static Boolean setActiveSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);

        Boolean result = StoredRestManager.getInstance().put("rest/secure/student/schoolclass/select", Boolean.class, restSchoolClass);
        LOG.log(Level.FINE, "Submitted schoolclass {1} for student with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), restSchoolClass.getDomSchoolClass().getSchoolClassName()});
        return result;
    }

    public static Boolean removeSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);

        Boolean result = StoredRestManager.getInstance().put("rest/secure/student/schoolclass/remove", Boolean.class, restSchoolClass);
        LOG.log(Level.FINE, "Removed schoolclass with id {0} for student with id {1}.", new Object[]{restSchoolClass.getDomSchoolClass().getId(), DwoHelper.getCurrentUser().getId()});
        return result;
    }

    public static List<DomSchoolClass> getStudentsSchoolClasses() throws Dwo2Exception {
        List<DomSchoolClass> src;
        src = StoredRestManager.getInstance().getList("rest/secure/student/schoolclass/getList", RestListClassTypes.DomSchoolClass);
        LOG.log(Level.FINE, "Retrieved list of schoolclasses of the student with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

    public static Boolean registerStudentForSchoolClass(DomNewSchoolClass4Student submit) throws Dwo2Exception {
        RestNewSchoolClass4Student restSchoolClass = new RestNewSchoolClass4Student();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomNewSchoolClass4Student(submit);

        Boolean result = StoredRestManager.getInstance().put("rest/secure/student/schoolclass/submit", Boolean.class, restSchoolClass);
        LOG.log(Level.FINE, "Submitted schoolclass {1} for registration by student with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), restSchoolClass.getDomNewSchoolClass4Student().getId()});
        return result;
    }

    public static List<DomSchoolClass> getSchoolsClasses() throws Dwo2Exception {
        List<DomSchoolClass> src;
        src = StoredRestManager.getInstance().getList("rest/secure/student/schoolclass/getSchoolsList", RestListClassTypes.DomSchoolClass);
        LOG.log(Level.FINE, "Retrieved list of schoolclasses of the school with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

    public static DomSchoolClass getActiveSchoolClass() throws Dwo2Exception {
        DomSchoolClass sc;
        sc = StoredRestManager.getInstance().get("rest/secure/student/schoolclass/getActive", DomSchoolClass.class);
        LOG.log(Level.FINE, "Retrieved the active schoolclass with id {1} of the student with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sc.getId()});
        return sc;
    }
}
