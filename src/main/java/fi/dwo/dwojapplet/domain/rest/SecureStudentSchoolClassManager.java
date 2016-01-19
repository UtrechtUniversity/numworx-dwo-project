package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.dom.entities.DomContext;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.rest.RestListClassTypes;
import fi.dwo.commons.rest.entities.RestSchoolClass;
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
        
        Boolean result = StoredRestManager.getInstance().put("/rest/secure/student/schoolclass/select", Boolean.class, restSchoolClass);
        LOG.log(Level.FINE, "Submitted schoolclass {1} for student with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(),restSchoolClass.getDomSchoolClass().getSchoolClassName()});
        return result;
    }

    public static Boolean removeSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);

        Boolean result = StoredRestManager.getInstance().put("/rest/secure/student/schoolclass/remove", Boolean.class, restSchoolClass);
        LOG.log(Level.FINE, "Removed schoolclass with id {0} for student with id {1}.", new Object[]{restSchoolClass.getDomSchoolClass().getId(),DwoHelper.getCurrentUser().getId()});
        return result;
    }

    public static List<DomSchoolClass> getStudentsSchoolClasses() throws Dwo2Exception {
        List<DomSchoolClass> src;
        src = StoredRestManager.getInstance().getList("/rest/secure/student/schoolclass/getList", RestListClassTypes.DomSchoolClass);
        LOG.log(Level.FINE, "Retrieved list of schoolclasses of the student with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }
    
    public static Boolean registerStudentForSchoolClass(DomSchoolClass submit) throws Dwo2Exception {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(submit);

        Boolean result = StoredRestManager.getInstance().put("/rest/secure/student/schoolclass/submit", Boolean.class, restSchoolClass);
        LOG.log(Level.FINE, "Submitted schoolclass {1} for registration by student with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(),restSchoolClass.getDomSchoolClass().getId()});
        return result;
    }

}
