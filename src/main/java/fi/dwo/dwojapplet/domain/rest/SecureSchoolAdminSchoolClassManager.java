package fi.dwo.dwojapplet.domain.rest;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassFull;
import nl.uu.fi.dwo.rest.entities.RestSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSubmitTeacherToSchoolClass;
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
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static List<DomSchoolClass> getSchoolClasses() throws Dwo2Exception {
        List<DomSchoolClass> src;
        src = StoredRestManager.getInstance().getList("rest/secure/schooladmin/schoolclass/getList", RestListClassTypes.DomSchoolClass);
        LOG.log(Level.FINE, "Retrieved list of schoolclasses of the schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

    public static List<DomTeacher> getTeachersInSchool() throws Dwo2Exception {
        List<DomTeacher> src;
        src = StoredRestManager.getInstance().getList("rest/secure/schooladmin/schoolclass/getTeachersInSchoolList", RestListClassTypes.DomTeacher);
        LOG.log(Level.FINE, "Retrieved list of teachers in the school for the schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

    public static List<DomStudent> getStudentsInSchool() throws Dwo2Exception {
        List<DomStudent> src;
        src = StoredRestManager.getInstance().getList("rest/secure/schooladmin/schoolclass/getStudentsInSchoolList", RestListClassTypes.DomStudent);
        LOG.log(Level.FINE, "Retrieved list of students in the school for the schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }
    
    public static Boolean submitSchoolClass(DomSchoolClassFull schoolClass) throws Dwo2Exception {
        RestSchoolClassFull restSchoolClassFull = new RestSchoolClassFull();
        restSchoolClassFull.setRestContext(new DomContext());
        restSchoolClassFull.setDomSchoolClassFull(schoolClass);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/schooladmin/schoolclass/submit", Boolean.class, restSchoolClassFull);
        LOG.log(Level.FINE, "Submitted schoolclass {1} for schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(),schoolClass.getSchoolClassName()});
        return result;
    }

    public static List<DomTeacher> getTeachersInSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass sts = new RestSchoolClass();
        sts.setRestContext(new DomContext());
        sts.setDomSchoolClass(schoolClass);
        List<DomTeacher> result = StoredRestManager.getInstance().getPutList("rest/secure/schooladmin/schoolclass/getTeacherList", RestListClassTypes.DomTeacher, sts);
        LOG.log(Level.FINE, "Retrieved {1} teachers that are in schoolclass {2} for schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), result.size(), schoolClass.getId()});
        return result;
    }

    public static List<DomStudent> getStudentsInSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass sts = new RestSchoolClass();
        sts.setRestContext(new DomContext());
        sts.setDomSchoolClass(schoolClass);
        List<DomStudent> result = StoredRestManager.getInstance().getPutList("rest/secure/schooladmin/schoolclass/getStudentList", RestListClassTypes.DomStudent, sts);
        LOG.log(Level.FINE, "Retrieved {1} students that are in schoolclass {2} for schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), result.size(), schoolClass.getId()});
        return result;
    }

    public static Boolean removeSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/schooladmin/schoolclass/remove", Boolean.class, restSchoolClass);
        LOG.log(Level.FINE, "Removed schoolclass with id {0} for user with id {1}.", new Object[]{schoolClass.getId(),DwoHelper.getCurrentUser().getId()});
        return result;
    }
    
    public static Boolean submitTeacherToSchoolClass(DomSubmitTeacherToSchoolClass submit) throws Dwo2Exception {
        RestSubmitTeacherToSchoolClass sts = new RestSubmitTeacherToSchoolClass();
        sts.setRestContext(new DomContext());
        sts.setDomSubmitTeacherToSchoolClass(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/schooladmin/schoolclass/submitTeacher", Boolean.class, sts);
        LOG.log(Level.FINE, "Submitted teacher {1} to schoolclass {2} for schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), submit.getTeacher().getId(), submit.getSchoolClass().getId()});
        return result;
    }

    public static Boolean submitStudentToSchoolClass(DomSubmitStudentToSchoolClass submit) throws Dwo2Exception {
        RestSubmitStudentToSchoolClass sts = new RestSubmitStudentToSchoolClass();
        sts.setRestContext(new DomContext());
        sts.setDomSubmitStudentToSchoolClass(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/schooladmin/schoolclass/submitStudent", Boolean.class, sts);
        LOG.log(Level.FINE, "Submitted student {1} to schoolclass {2} for schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), submit.getStudent().getId(), submit.getSchoolClassTo().getId()});
        return result;
    }
    
    
    public static Boolean removeTeacherFromSchoolClass(DomRemoveTeacherFromSchoolClass submit) throws Dwo2Exception {
        RestRemoveTeacherFromSchoolClass sts = new RestRemoveTeacherFromSchoolClass();
        sts.setRestContext(new DomContext());
        sts.setDomRemoveTeacherFromSchoolClass(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/schooladmin/schoolclass/removeTeacher", Boolean.class, sts);
        LOG.log(Level.FINE, "Submitted teacher {1} to remove from schoolclass {2} for user with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sts.getDomRemoveTeacherFromSchoolClass().getTeacher().getId(), sts.getDomRemoveTeacherFromSchoolClass().getSchoolClass().getId()});
        return result;
    }

    public static Boolean removeStudentFromSchoolClass(DomRemoveStudentFromSchoolClass submit) throws Dwo2Exception {
        RestRemoveStudentFromSchoolClass sts = new RestRemoveStudentFromSchoolClass();
        sts.setRestContext(new DomContext());
        sts.setDomRemoveStudentFromSchoolClass(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/schooladmin/schoolclass/removeStudent", Boolean.class, sts);
        LOG.log(Level.FINE, "Submitted student {1} to remove from schoolclass {2} for user with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sts.getDomRemoveStudentFromSchoolClass().getStudent().getId(), sts.getDomRemoveStudentFromSchoolClass().getSchoolClass().getId()});
        return result;
    }

    public static DomSchoolClassFull getFullSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        DomSchoolClassFull result = StoredRestManager.getInstance().put("rest/secure/schooladmin/schoolclass/getFull", DomSchoolClassFull.class, restSchoolClass);
        LOG.log(Level.FINE, "Retrieved full schoolclass {1} for teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), restSchoolClass.getDomSchoolClass().getSchoolClassName()});
        return result;
    }

    public static Boolean updateSchoolClass(DomSchoolClassFull schoolClass) throws Dwo2Exception {
        RestSchoolClassFull restSchoolClass = new RestSchoolClassFull();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClassFull(schoolClass);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/schooladmin/schoolclass/update", Boolean.class, restSchoolClass);
        LOG.log(Level.FINE, "Updated schoolclass {1} for teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), restSchoolClass.getDomSchoolClassFull().getSchoolClassName()});
        return result;
    }

    public static Boolean submitSingleSchoolStudent(DomNewSingleSchoolStudent submit) throws Dwo2Exception {
        RestNewSingleSchoolStudent newStudent = new RestNewSingleSchoolStudent();
        newStudent.setRestContext(new DomContext());
        newStudent.setDomNewSingleSchoolStudent(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/schooladmin/schoolclass/submitSingleSchoolStudent", Boolean.class, newStudent);
        LOG.log(Level.FINE, "Submitted new student with username {1} for schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), submit.getDomSingleSchoolStudent().getUserName()});
        return result;
    }
}
