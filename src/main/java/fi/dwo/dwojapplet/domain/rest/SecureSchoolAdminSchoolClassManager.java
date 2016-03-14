package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomStudent;
import fi.dwo.commons.dom.entities.DomTeacher;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.rest.RestListClassTypes;
import fi.dwo.commons.rest.entities.RestSchoolClass;
import fi.dwo.commons.rest.entities.RestSubmitTeacherToSchoolClass;
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
public class SecureSchoolAdminSchoolClassManager {

    private static final Logger LOG = Logger.getLogger(SecureSchoolAdminSchoolClassManager.class.getName());

    /**
     * Returns the current user 'logged in'. The information is extracted from
     * the security context which depends on the credentials used for accessing
     * the rest interface. Technically it should be equal to the data in the
     * DwoHelper.
     *
     * @return
     * @throws fi.dwo.commons.exceptions.Dwo2Exception
     */
    public static List<DomSchoolClass> getSchoolClasses() throws Dwo2Exception {
        List<DomSchoolClass> src;
        src = StoredRestManager.getInstance().getList("/rest/secure/schooladmin/schoolclass/getList", RestListClassTypes.DomSchoolClass);
        LOG.log(Level.FINE, "Retrieved list of schoolclasses of the schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

    public static List<DomTeacher> getTeachersInSchool() throws Dwo2Exception {
        List<DomTeacher> src;
        src = StoredRestManager.getInstance().getList("/rest/secure/schooladmin/schoolclass/getTeachersInSchoolList", RestListClassTypes.DomTeacher);
        LOG.log(Level.FINE, "Retrieved list of teachers in the school for the schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

//Not supported
//    public static Boolean SubmitSchoolClass(RestSchoolClass4Teacher schoolClass) throws Dwo2Exception {
//        Boolean result = StoredRestManager.getInstance().put("/rest/secure/teacher/schoolclass/submit", Boolean.class, schoolClass);
//        LOG.log(Level.FINE, "Submitted schoolclass {1} for schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(),schoolClass.getDomSchoolClass4Teacher().getSchoolClassName()});
//        return result;
//    }
    
    public static List<DomTeacher> GetTeachersInSchoolClass(RestSchoolClass schoolClass) throws Dwo2Exception {
        List<DomTeacher>  result = StoredRestManager.getInstance().getPutList("/rest/secure/schooladmin/schoolclass/getTeacherList", RestListClassTypes.DomTeacher, schoolClass);
        LOG.log(Level.FINE, "Retrieved {1} teachers that are in schoolclass {2} for schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), result.size(), schoolClass.getDomSchoolClass().getId()});
        return result;
    }

    public static List<DomStudent> GetStudentsInSchoolClass(RestSchoolClass schoolClass) throws Dwo2Exception {
        List<DomStudent>  result = StoredRestManager.getInstance().getPutList("/rest/secure/teacher/schoolclass/getStudentList", RestListClassTypes.DomStudent, schoolClass);
        LOG.log(Level.FINE, "Retrieved {1} students that are in schoolclass {2} for schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), result.size(), schoolClass.getDomSchoolClass().getId()});
        return result;
    }

//    public static Boolean removeSchoolClass(RestSchoolClass schoolClass) throws Dwo2Exception {
//        Boolean result = StoredRestManager.getInstance().put("/rest/secure/schooladmin/schoolclass/remove", Boolean.class, schoolClass);
//        LOG.log(Level.FINE, "Removed schoolclass with id {0} for user with id {1}.", new Object[]{schoolClass.getDomSchoolClass().getId(),DwoHelper.getCurrentUser().getId()});
//        return result;
//    }

    public static Boolean SubmitTeacherToSchoolClass(RestSubmitTeacherToSchoolClass submit) throws Dwo2Exception {
        Boolean result = StoredRestManager.getInstance().put("/rest/secure/schooladmin/schoolclass/submitTeacher", Boolean.class, submit);
        LOG.log(Level.FINE, "Submitted teacher {1} to schoolclass {2} for schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(),submit.getDomSubmitTeacherToSchoolClass().getTeacher().getId(),submit.getDomSubmitTeacherToSchoolClass().getSchoolClass().getId()});
        return result;
    }
        
}
