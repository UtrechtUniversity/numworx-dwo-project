package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.dom.entities.DomContext;
import fi.dwo.commons.dom.entities.DomRemoveStudentFromSchoolClass;
import fi.dwo.commons.dom.entities.DomRemoveTeacherFromSchoolClass;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSchoolClassFull;
import fi.dwo.commons.dom.entities.DomStudent;
import fi.dwo.commons.dom.entities.DomSubmitStudentToSchoolClass;
import fi.dwo.commons.dom.entities.DomSubmitTeacherToSchoolClass;
import fi.dwo.commons.dom.entities.DomTeacher;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.rest.RestListClassTypes;
import fi.dwo.commons.rest.entities.RestRemoveStudentFromSchoolClass;
import fi.dwo.commons.rest.entities.RestRemoveTeacherFromSchoolClass;
import fi.dwo.commons.rest.entities.RestSchoolClass;
import fi.dwo.commons.rest.entities.RestSchoolClassFull;
import fi.dwo.commons.rest.entities.RestSubmitStudentToSchoolClass;
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
//    public static Boolean SubmitSchoolClass(RestSchoolClassFull schoolClass) throws Dwo2Exception {
//        Boolean result = StoredRestManager.getInstance().put("/rest/secure/teacher/schoolclass/submit", Boolean.class, schoolClass);
//        LOG.log(Level.FINE, "Submitted schoolclass {1} for schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(),schoolClass.getDomSchoolClassFull().getSchoolClassName()});
//        return result;
//    }
    public static List<DomTeacher> getTeachersInSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass sts = new RestSchoolClass();
        sts.setRestContext(new DomContext());
        sts.setDomSchoolClass(schoolClass);
        List<DomTeacher> result = StoredRestManager.getInstance().getPutList("/rest/secure/schooladmin/schoolclass/getTeacherList", RestListClassTypes.DomTeacher, sts);
        LOG.log(Level.FINE, "Retrieved {1} teachers that are in schoolclass {2} for schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), result.size(), schoolClass.getId()});
        return result;
    }

    public static List<DomStudent> getStudentsInSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass sts = new RestSchoolClass();
        sts.setRestContext(new DomContext());
        sts.setDomSchoolClass(schoolClass);
        List<DomStudent> result = StoredRestManager.getInstance().getPutList("/rest/secure/schooladmin/schoolclass/getStudentList", RestListClassTypes.DomStudent, sts);
        LOG.log(Level.FINE, "Retrieved {1} students that are in schoolclass {2} for schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), result.size(), schoolClass.getId()});
        return result;
    }

//    public static Boolean removeSchoolClass(RestSchoolClass schoolClass) throws Dwo2Exception {
//        Boolean result = StoredRestManager.getInstance().put("/rest/secure/schooladmin/schoolclass/remove", Boolean.class, schoolClass);
//        LOG.log(Level.FINE, "Removed schoolclass with id {0} for user with id {1}.", new Object[]{schoolClass.getDomSchoolClass().getId(),DwoHelper.getCurrentUser().getId()});
//        return result;
//    }
    public static Boolean submitTeacherToSchoolClass(DomSubmitTeacherToSchoolClass submit) throws Dwo2Exception {
        RestSubmitTeacherToSchoolClass sts = new RestSubmitTeacherToSchoolClass();
        sts.setRestContext(new DomContext());
        sts.setDomSubmitTeacherToSchoolClass(submit);
        Boolean result = StoredRestManager.getInstance().put("/rest/secure/schooladmin/schoolclass/submitTeacher", Boolean.class, sts);
        LOG.log(Level.FINE, "Submitted teacher {1} to schoolclass {2} for schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), submit.getTeacher().getId(), submit.getSchoolClass().getId()});
        return result;
    }

    public static Boolean submitStudentToSchoolClass(DomSubmitStudentToSchoolClass submit) throws Dwo2Exception {
        RestSubmitStudentToSchoolClass sts = new RestSubmitStudentToSchoolClass();
        sts.setRestContext(new DomContext());
        sts.setDomSubmitStudentToSchoolClass(submit);
        Boolean result = StoredRestManager.getInstance().put("/rest/secure/schooladmin/schoolclass/submitStudent", Boolean.class, sts);
        LOG.log(Level.FINE, "Submitted teacher {1} to schoolclass {2} for schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), submit.getStudent().getId(), submit.getSchoolClassTo().getId()});
        return result;
    }
    
    
    public static Boolean removeTeacherFromSchoolClass(DomRemoveTeacherFromSchoolClass submit) throws Dwo2Exception {
        RestRemoveTeacherFromSchoolClass sts = new RestRemoveTeacherFromSchoolClass();
        sts.setRestContext(new DomContext());
        sts.setDomRemoveTeacherFromSchoolClass(submit);
        Boolean result = StoredRestManager.getInstance().put("/rest/secure/schooladmin/schoolclass/removeTeacher", Boolean.class, sts);
        LOG.log(Level.FINE, "Submitted teacher {1} to remove from schoolclass {2} for user with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sts.getDomRemoveTeacherFromSchoolClass().getTeacher().getId(), sts.getDomRemoveTeacherFromSchoolClass().getSchoolClass().getId()});
        return result;
    }

    public static Boolean removeStudentFromSchoolClass(DomRemoveStudentFromSchoolClass submit) throws Dwo2Exception {
        RestRemoveStudentFromSchoolClass sts = new RestRemoveStudentFromSchoolClass();
        sts.setRestContext(new DomContext());
        sts.setDomRemoveStudentFromSchoolClass(submit);
        Boolean result = StoredRestManager.getInstance().put("/rest/secure/schooladmin/schoolclass/removeStudent", Boolean.class, sts);
        LOG.log(Level.FINE, "Submitted student {1} to remove from schoolclass {2} for user with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sts.getDomRemoveStudentFromSchoolClass().getStudent().getId(), sts.getDomRemoveStudentFromSchoolClass().getSchoolClass().getId()});
        return result;
    }

    public static DomSchoolClassFull getFullSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        DomSchoolClassFull result = StoredRestManager.getInstance().put("/rest/secure/schooladmin/schoolclass/getFull", DomSchoolClassFull.class, restSchoolClass);
        LOG.log(Level.FINE, "Retrieved full schoolclass {1} for teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), restSchoolClass.getDomSchoolClass().getSchoolClassName()});
        return result;
    }

    public static Boolean updateSchoolClass(DomSchoolClassFull schoolClass) throws Dwo2Exception {
        RestSchoolClassFull restSchoolClass = new RestSchoolClassFull();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClassFull(schoolClass);
        Boolean result = StoredRestManager.getInstance().put("/rest/secure/schooladmin/schoolclass/update", Boolean.class, restSchoolClass);
        LOG.log(Level.FINE, "Updated schoolclass {1} for teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), restSchoolClass.getDomSchoolClassFull().getSchoolClassName()});
        return result;
    }
}
