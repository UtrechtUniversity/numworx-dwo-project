package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.rest.entities.RestSchoolClass;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the school roles and classes registered in HasRole.
 *
 * @author G.A.J. van der Plas
 */
public class SecureStudentSchoolClassManager {

    private static final Logger LOG = Logger.getLogger(SecureStudentSchoolClassManager.class.getName());

    public static Boolean setActiveSchoolClass(RestSchoolClass schoolClass) throws Dwo2Exception {
        Boolean result = StoredRestManager.getInstance().put("/rest/secure/student/schoolclass/select", Boolean.class, schoolClass);
        LOG.log(Level.FINE, "Submitted schoolclass {1} for student with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(),schoolClass.getDomSchoolClass().getSchoolClassName()});
        return result;
    }

    public static Boolean removeSchoolClass(RestSchoolClass schoolClass) throws Dwo2Exception {
        Boolean result = StoredRestManager.getInstance().put("/rest/secure/student/schoolclass/remove", Boolean.class, schoolClass);
        LOG.log(Level.FINE, "Removed schoolclass with id {0} for student with id {1}.", new Object[]{schoolClass.getDomSchoolClass().getId(),DwoHelper.getCurrentUser().getId()});
        return result;
    }

    public static Boolean registerStudentForSchoolClass(RestSchoolClass submit) throws Dwo2Exception {
        Boolean result = StoredRestManager.getInstance().put("/rest/secure/student/schoolclass/submit", Boolean.class, submit);
        LOG.log(Level.FINE, "Submitted schoolclass {1} for registration by student with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(),submit.getDomSchoolClass().getId()});
        return result;
    }

}
