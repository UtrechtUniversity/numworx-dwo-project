package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.rest.dom.entities.DomGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestNewSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestRemoveStudentFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestRemoveTeacherFromSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolClassFull;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSubmitTeacherToSchoolClass;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;

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
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    public static List<DomSchoolClass> getTeachersSchoolClasses() throws Dwo2Exception {
        List<DomSchoolClass> src;
        src = StoredRestManager.getInstance().getList("rest/secure/teacher/schoolclass/getList", RestListClassTypes.DomSchoolClass);
        LOG.log(Level.FINE, "Retrieved list of schoolclasses of the teacher with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername()});
        return src;
    }

    public static List<DomTeacher> getTeachersInSchool() throws Dwo2Exception {
        List<DomTeacher> src;
        src = StoredRestManager.getInstance().getList("rest/secure/teacher/schoolclass/getTeachersInSchoolList", RestListClassTypes.DomTeacher);
        LOG.log(Level.FINE, "Retrieved list of teachers in the school for the teacher with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername()});
        return src;
    }

    public static List<DomStudent> getStudentsInSchool() throws Dwo2Exception {
        List<DomStudent> src;
        src = StoredRestManager.getInstance().getList("rest/secure/teacher/schoolclass/getStudentsInSchoolList", RestListClassTypes.DomStudent);
        LOG.log(Level.FINE, "Retrieved list of single school students in the school for the teacher with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername()});
        return src;
    }

    public static Boolean submitSchoolClass(DomSchoolClassFull schoolClass) throws Dwo2Exception {
        RestSchoolClassFull rest = new RestSchoolClassFull();
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
        rest.setDomSchoolClassFull(schoolClass);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/submit", Boolean.class, rest);
        LOG.log(Level.FINE, "Submitted schoolclass {1} for teacher with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername(), rest.getDomSchoolClassFull().getSchoolClassName()});
        return result;
    }

    public static List<DomTeacher> getTeachersInSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass rest = new RestSchoolClass();
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
        rest.setDomSchoolClass(schoolClass);
        List<DomTeacher> result = StoredRestManager.getInstance().getPutList("rest/secure/teacher/schoolclass/getTeacherList", RestListClassTypes.DomTeacher, rest);
        LOG.log(Level.FINE, "Retrieved {1} teachers that are in schoolclass {2} for user with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername(), result.size(), rest.getDomSchoolClass().getId()});
        return result;
    }

    public static List<DomStudent> getStudentsInSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass rest = new RestSchoolClass();
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
        rest.setDomSchoolClass(schoolClass);
        List<DomStudent> result = StoredRestManager.getInstance().getPutList("rest/secure/teacher/schoolclass/getStudentList", RestListClassTypes.DomStudent, rest);
        LOG.log(Level.FINE, "Retrieved {1} students that are in schoolclass {2} for user with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername(), result.size(), rest.getDomSchoolClass().getId()});
        return result;
    }

    public static Boolean removeSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass rest = new RestSchoolClass();
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
        rest.setDomSchoolClass(schoolClass);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/remove", Boolean.class, rest);
        LOG.log(Level.FINE, "Removed schoolclass with username {0} for user with id {1}.", new Object[]{rest.getDomSchoolClass().getId(), RestAuthenticator.getInstance().getUsername()});
        return result;
    }

    public static Boolean submitTeacherToSchoolClass(DomSubmitTeacherToSchoolClass submit) throws Dwo2Exception {
        RestSubmitTeacherToSchoolClass rest = new RestSubmitTeacherToSchoolClass();
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
        rest.setDomSubmitTeacherToSchoolClass(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/submitTeacher", Boolean.class, rest);
        LOG.log(Level.FINE, "Submitted teacher {1} to schoolclass {2} for user with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername(), rest.getDomSubmitTeacherToSchoolClass().getTeacher().getId(), rest.getDomSubmitTeacherToSchoolClass().getSchoolClass().getId()});
        return result;
    }

    public static Boolean submitStudentToSchoolClass(DomSubmitStudentToSchoolClass submit) throws Dwo2Exception {
        RestSubmitStudentToSchoolClass rest = new RestSubmitStudentToSchoolClass();
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
        rest.setDomSubmitStudentToSchoolClass(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/submitStudent", Boolean.class, rest);
        LOG.log(Level.FINE, "Submitted student {1} to schoolclass {2} for user with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername(), rest.getDomSubmitStudentToSchoolClass().getStudent().getId(), rest.getDomSubmitStudentToSchoolClass().getSchoolClassTo().getId()});
        return result;
    }

    public static Boolean updateSchoolClass(DomSchoolClassFull schoolClass) throws Dwo2Exception {
        RestSchoolClassFull rest = new RestSchoolClassFull();
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
        rest.setDomSchoolClassFull(schoolClass);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/update", Boolean.class, rest);
        LOG.log(Level.FINE, "Updated schoolclass {1} for teacher with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername(), rest.getDomSchoolClassFull().getSchoolClassName()});
        return result;
    }

    public static DomSchoolClassFull getFullSchoolClass(DomSchoolClass schoolClass) throws Dwo2Exception {
        RestSchoolClass rest = new RestSchoolClass();
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
        rest.setDomSchoolClass(schoolClass);
        DomSchoolClassFull result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/getFull", DomSchoolClassFull.class, rest);
        LOG.log(Level.FINE, "Retrieved full schoolclass {1} for teacher with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername(), rest.getDomSchoolClass().getSchoolClassName()});
        return result;
    }

    public static Boolean removeTeacherFromSchoolClass(DomRemoveTeacherFromSchoolClass submit) throws Dwo2Exception {
        RestRemoveTeacherFromSchoolClass rest = new RestRemoveTeacherFromSchoolClass();
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
        rest.setDomRemoveTeacherFromSchoolClass(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/removeTeacher", Boolean.class, rest);
        LOG.log(Level.FINE, "Submitted teacher {1} to remove from schoolclass {2} for user with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername(), rest.getDomRemoveTeacherFromSchoolClass().getTeacher().getId(), rest.getDomRemoveTeacherFromSchoolClass().getSchoolClass().getId()});
        return result;
    }

    public static Boolean removeStudentFromSchoolClass(DomRemoveStudentFromSchoolClass submit) throws Dwo2Exception {
        RestRemoveStudentFromSchoolClass rest = new RestRemoveStudentFromSchoolClass();
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
        rest.setDomRemoveStudentFromSchoolClass(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/removeStudent", Boolean.class, rest);
        LOG.log(Level.FINE, "Submitted student {1} to remove from schoolclass {2} for user with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername(), rest.getDomRemoveStudentFromSchoolClass().getStudent().getId(), rest.getDomRemoveStudentFromSchoolClass().getSchoolClass().getId()});
        return result;
    }

    public static List<DomStudent> getSingleSchoolStudentsInSchool() throws Dwo2Exception {
        List<DomStudent> src;
        src = StoredRestManager.getInstance().getList("rest/secure/teacher/schoolclass/getSingleSchoolStudentsInSchoolList", RestListClassTypes.DomStudent);
        LOG.log(Level.FINE, "Retrieved list of single school students in the school for the teacher with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername()});
        return src;
    }

    public static Boolean submitSingleSchoolStudent(DomNewSingleSchoolStudent submit) throws Dwo2Exception {
        RestNewSingleSchoolStudent rest = new RestNewSingleSchoolStudent();
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
        rest.setDomNewSingleSchoolStudent(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/submitSingleSchoolStudent", Boolean.class, rest);
        LOG.log(Level.FINE, "Submitted teacher {1} to schoolclass {2} for user with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername(), rest.getDomNewSingleSchoolStudent().getDomSingleSchoolStudent().getId(), rest.getDomNewSingleSchoolStudent().getDomSchoolClass().getId()});
        return result;
    }

    public static DomSingleSchoolStudent getSingleSchoolStudent(DomGetSingleSchoolStudent submit) throws Dwo2Exception {
        RestGetSingleSchoolStudent rest = new RestGetSingleSchoolStudent();
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
        rest.setDomGetSingleSchoolStudent(submit);
        DomSingleSchoolStudent result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/getSingleSchoolStudent", DomSingleSchoolStudent.class, rest);
        LOG.log(Level.FINE, "Retrieved full single school student {1} for  teacher with username {0}.", new Object[]{RestAuthenticator.getInstance().getUsername(), rest.getDomGetSingleSchoolStudent().getDomStudent().getId()});
        return result;
    }

    public static Boolean updateSingleSchoolStudent(DomSingleSchoolStudent submit) throws Dwo2Exception {
        RestSingleSchoolStudent rest = new RestSingleSchoolStudent();
    	rest.setRestContext(RestAuthenticator.getInstance().getContext());
        rest.setDomSingleSchoolStudent(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/teacher/schoolclass/updateSingleSchoolStudent", Boolean.class, rest);
        LOG.log(Level.FINE, "Updated acount data for singlschoolstudent {1} by user {0}.", new Object[]{RestAuthenticator.getInstance().getUsername(), rest.getDomSingleSchoolStudent().getId()});
        return result;
    }
}
