package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomGetSingleSchoolStudent;
import fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import fi.dwo.rest.dom.entities.DomRemoveStudentFromSchoolClass;
import fi.dwo.rest.dom.entities.DomRemoveTeacherFromSchoolClass;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomSchoolClassFull;
import fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import fi.dwo.rest.dom.entities.DomStudent;
import fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import fi.dwo.rest.dom.entities.DomTeacher;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.RestListClassTypes;
import fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import fi.dwo.rest.entities.RestRemoveStudentFromSchoolClass;
import fi.dwo.rest.entities.RestRemoveTeacherFromSchoolClass;
import fi.dwo.rest.entities.RestSchoolClass;
import fi.dwo.rest.entities.RestSchoolClassFull;
import fi.dwo.rest.entities.RestSingleSchoolStudent;
import fi.dwo.rest.entities.RestSubmitStudentToSchoolClass;
import fi.dwo.rest.entities.RestSubmitTeacherToSchoolClass;
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
public class SecureTeacherSchoolClassManager {

    private static final Logger LOG = Logger.getLogger(SecureTeacherSchoolClassManager.class.getName());

    /**
     * Returns the current user 'logged in'. The information is extracted from
     * the security context which depends on the credentials used for accessing
     * the rest interface. Technically it should be equal to the data in the
     * DwoHelper.
     *
     * @return
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static List<DomSchoolClass> getTeachersSchoolClasses() throws Dwo2Exception {
        List<DomSchoolClass> src;
        src = StoredRestManager.getInstance().getList("rest/secure/teacher/schoolclass/getList", RestListClassTypes.DomSchoolClass);
        LOG.log(Level.FINE, "Retrieved list of schoolclasses of the teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

    public static List<DomTeacher> getTeachersInSchool() throws Dwo2Exception {
        List<DomTeacher> src;
        src = StoredRestManager.getInstance().getList("rest/secure/teacher/schoolclass/getTeachersInSchoolList", RestListClassTypes.DomTeacher);
        LOG.log(Level.FINE, "Retrieved list of teachers in the school for the teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

    public static List<DomStudent> getStudentsInSchool() throws Dwo2Exception {
        List<DomStudent> src;
        src = StoredRestManager.getInstance().getList("rest/secure/teacher/schoolclass/getStudentsInSchoolList", RestListClassTypes.DomStudent);
        LOG.log(Level.FINE, "Retrieved list of single school students in the school for the teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

    public static Boolean submitSchoolClass(DomSchoolClassFull schoolClass) throws Dwo2Exception {
        RestSchoolClassFull restSchoolClass = new RestSchoolClassFull();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClassFull(schoolClass);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/submit", Boolean.class, restSchoolClass);
        LOG.log(Level.FINE, "Submitted schoolclass {1} for teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), restSchoolClass.getDomSchoolClassFull().getSchoolClassName()});
        return result;
    }

    public static List<DomTeacher> getTeachersInSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        List<DomTeacher> result = StoredRestManager.getInstance().getPutList("rest/secure/teacher/schoolclass/getTeacherList", RestListClassTypes.DomTeacher, restSchoolClass);
        LOG.log(Level.FINE, "Retrieved {1} teachers that are in schoolclass {2} for user with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), result.size(), restSchoolClass.getDomSchoolClass().getId()});
        return result;
    }

    public static List<DomStudent> getStudentsInSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        List<DomStudent> result = StoredRestManager.getInstance().getPutList("rest/secure/teacher/schoolclass/getStudentList", RestListClassTypes.DomStudent, restSchoolClass);
        LOG.log(Level.FINE, "Retrieved {1} students that are in schoolclass {2} for user with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), result.size(), restSchoolClass.getDomSchoolClass().getId()});
        return result;
    }

    public static Boolean removeSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/remove", Boolean.class, restSchoolClass);
        LOG.log(Level.FINE, "Removed schoolclass with id {0} for user with id {1}.", new Object[]{restSchoolClass.getDomSchoolClass().getId(), DwoHelper.getCurrentUser().getId()});
        return result;
    }

    public static Boolean submitTeacherToSchoolClass(DomSubmitTeacherToSchoolClass submit) throws Dwo2Exception {
        RestSubmitTeacherToSchoolClass sts = new RestSubmitTeacherToSchoolClass();
        sts.setRestContext(new DomContext());
        sts.setDomSubmitTeacherToSchoolClass(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/submitTeacher", Boolean.class, sts);
        LOG.log(Level.FINE, "Submitted teacher {1} to schoolclass {2} for user with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sts.getDomSubmitTeacherToSchoolClass().getTeacher().getId(), sts.getDomSubmitTeacherToSchoolClass().getSchoolClass().getId()});
        return result;
    }

    public static Boolean submitStudentToSchoolClass(DomSubmitStudentToSchoolClass submit) throws Dwo2Exception {
        RestSubmitStudentToSchoolClass sts = new RestSubmitStudentToSchoolClass();
        sts.setRestContext(new DomContext());
        sts.setDomSubmitStudentToSchoolClass(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/submitStudent", Boolean.class, sts);
        LOG.log(Level.FINE, "Submitted student {1} to schoolclass {2} for user with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sts.getDomSubmitStudentToSchoolClass().getStudent().getId(), sts.getDomSubmitStudentToSchoolClass().getSchoolClassTo().getId()});
        return result;
    }

    public static Boolean updateSchoolClass(DomSchoolClassFull schoolClass) throws Dwo2Exception {
        RestSchoolClassFull restSchoolClass = new RestSchoolClassFull();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClassFull(schoolClass);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/update", Boolean.class, restSchoolClass);
        LOG.log(Level.FINE, "Updated schoolclass {1} for teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), restSchoolClass.getDomSchoolClassFull().getSchoolClassName()});
        return result;
    }

    public static DomSchoolClassFull getFullSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass restSchoolClass = new RestSchoolClass();
        restSchoolClass.setRestContext(new DomContext());
        restSchoolClass.setDomSchoolClass(schoolClass);
        DomSchoolClassFull result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/getFull", DomSchoolClassFull.class, restSchoolClass);
        LOG.log(Level.FINE, "Retrieved full schoolclass {1} for teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), restSchoolClass.getDomSchoolClass().getSchoolClassName()});
        return result;
    }

    public static Boolean removeTeacherFromSchoolClass(DomRemoveTeacherFromSchoolClass submit) throws Dwo2Exception {
        RestRemoveTeacherFromSchoolClass sts = new RestRemoveTeacherFromSchoolClass();
        sts.setRestContext(new DomContext());
        sts.setDomRemoveTeacherFromSchoolClass(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/removeTeacher", Boolean.class, sts);
        LOG.log(Level.FINE, "Submitted teacher {1} to remove from schoolclass {2} for user with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sts.getDomRemoveTeacherFromSchoolClass().getTeacher().getId(), sts.getDomRemoveTeacherFromSchoolClass().getSchoolClass().getId()});
        return result;
    }

    public static Boolean removeStudentFromSchoolClass(DomRemoveStudentFromSchoolClass submit) throws Dwo2Exception {
        RestRemoveStudentFromSchoolClass sts = new RestRemoveStudentFromSchoolClass();
        sts.setRestContext(new DomContext());
        sts.setDomRemoveStudentFromSchoolClass(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/removeStudent", Boolean.class, sts);
        LOG.log(Level.FINE, "Submitted student {1} to remove from schoolclass {2} for user with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sts.getDomRemoveStudentFromSchoolClass().getStudent().getId(), sts.getDomRemoveStudentFromSchoolClass().getSchoolClass().getId()});
        return result;
    }

    public static List<DomStudent> getSingleSchoolStudentsInSchool() throws Dwo2Exception {
        List<DomStudent> src;
        src = StoredRestManager.getInstance().getList("rest/secure/teacher/schoolclass/getSingleSchoolStudents", RestListClassTypes.DomStudent);
        LOG.log(Level.FINE, "Retrieved list of single school students in the school for the teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

    public static Boolean submitSingleSchoolStudent(DomNewSingleSchoolStudent submit) throws Dwo2Exception {
        RestNewSingleSchoolStudent sts = new RestNewSingleSchoolStudent();
        sts.setRestContext(new DomContext());
        sts.setDomNewSingleSchoolStudent(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/submitSingleSchoolStudent", Boolean.class, sts);
        LOG.log(Level.FINE, "Submitted teacher {1} to schoolclass {2} for user with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sts.getDomNewSingleSchoolStudent().getDomSingleSchoolStudent().getId(), sts.getDomNewSingleSchoolStudent().getDomSchoolClass().getId()});
        return result;
    }

    public static DomSingleSchoolStudent getSingleSchoolStudent(DomGetSingleSchoolStudent submit) throws Dwo2Exception {
        RestGetSingleSchoolStudent sts = new RestGetSingleSchoolStudent();
        sts.setRestContext(new DomContext());
        sts.setDomGetSingleSchoolStudent(submit);
        DomSingleSchoolStudent result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/getSingleSchoolStudent", DomSingleSchoolStudent.class, sts);
        LOG.log(Level.FINE, "Retrieved full single school student {1} for  teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sts.getDomGetSingleSchoolStudent().getDomStudent().getId()});
        return result;
    }

    public static Boolean updateSingleSchoolStudent(DomSingleSchoolStudent submit) throws Dwo2Exception {
        RestSingleSchoolStudent sts = new RestSingleSchoolStudent();
        sts.setRestContext(new DomContext());
        sts.setDomSingleSchoolStudent(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/updateSingleSchoolStudent", Boolean.class, sts);
        LOG.log(Level.FINE, "Updated acount data for singlschoolstudent {1} by user {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sts.getDomSingleSchoolStudent().getId()});
        return result;
    }
}
