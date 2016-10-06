package fi.dwo.dwojapplet.domain.rest;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.entities.RestGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestSchoolAdmin;
import nl.uu.fi.dwo.rest.entities.RestSingleSchoolStudent;
import nl.uu.fi.dwo.rest.entities.RestStudent;
import nl.uu.fi.dwo.rest.entities.RestTeacher;
import fi.dwo.dwojapplet.REST.StoredRestManager;
import fi.dwo.dwojapplet.domain.DwoHelper;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import nl.uu.fi.dwo.rest.entities.RestUserFull;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the school roles and classes registered in HasRole.
 *
 * @author G.A.J. van der Plas
 */
public class SecureSchoolAdminSchoolManager {

    private static final Logger LOG = Logger.getLogger(SecureSchoolAdminSchoolManager.class.getName());

    public static List<DomTeacher> getTeachersInSchool() throws Dwo2Exception {
        List<DomTeacher> src;
        src = StoredRestManager.getInstance().getList("rest/secure/schooladmin/school/getTeachersInSchoolList", RestListClassTypes.DomTeacher);
        LOG.log(Level.FINE, "Retrieved list of teachers in the school for the schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

    public static List<DomStudent> getStudentsInSchool() throws Dwo2Exception {
        List<DomStudent> src;
        src = StoredRestManager.getInstance().getList("rest/secure/schooladmin/school/getStudentsInSchoolList", RestListClassTypes.DomStudent);
        LOG.log(Level.FINE, "Retrieved list of students in the school for the schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

    public static List<DomSchoolAdmin> getSchoolAdminsInSchool() throws Dwo2Exception {
        List<DomSchoolAdmin> src;
        src = StoredRestManager.getInstance().getList("rest/secure/schooladmin/school/getSchoolAdminList", RestListClassTypes.DomSchoolAdmin);
        LOG.log(Level.FINE, "Retrieved list of schooladmins in the school for the schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

    public static DomSingleSchoolStudent getSingleSchoolStudent(DomGetSingleSchoolStudent submit) throws Dwo2Exception {
        RestGetSingleSchoolStudent sts = new RestGetSingleSchoolStudent();
        sts.setRestContext(new DomContext());
        sts.setDomGetSingleSchoolStudent(submit);
        DomSingleSchoolStudent result = StoredRestManager.getInstance().put("rest/secure/schooladmin/school/getSingleSchoolStudent", DomSingleSchoolStudent.class, sts);
        LOG.log(Level.FINE, "Retrieved full single school student {1} for  teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sts.getDomGetSingleSchoolStudent().getDomStudent().getId()});
        return result;
    }

    public static Boolean updateSingleSchoolStudent(DomSingleSchoolStudent submit) throws Dwo2Exception {
        RestSingleSchoolStudent sts = new RestSingleSchoolStudent();
        sts.setRestContext(new DomContext());
        sts.setDomSingleSchoolStudent(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/schooladmin/school/updateSingleSchoolStudent", Boolean.class, sts);
        LOG.log(Level.FINE, "Updated acount data for singlschoolstudent {1} by user {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sts.getDomSingleSchoolStudent().getId()});
        return result;
    }    


    public static Boolean removeStudentFromSchool(DomStudent submit) throws Dwo2Exception {
        RestStudent restStudent = new RestStudent();
        restStudent.setRestContext(new DomContext());
        restStudent.setDomStudent(submit);

        Boolean result = StoredRestManager.getInstance().put("rest/secure/schooladmin/school/removeStudent", Boolean.class, restStudent);
        LOG.log(Level.FINE, "Submitted student {1} for removal from school by user with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), restStudent.getDomStudent().getId()});
        return result;
    }

    public static Boolean removeSingleSchoolStudentFromSchool(DomStudent submit) throws Dwo2Exception {
        RestStudent restStudent = new RestStudent();
        restStudent.setRestContext(new DomContext());
        restStudent.setDomStudent(submit);

        Boolean result = StoredRestManager.getInstance().put("rest/secure/schooladmin/school/removeSingleSchoolStudentFromSchool", Boolean.class, restStudent);
        LOG.log(Level.FINE, "Submitted student {1} for removal from school by user with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), restStudent.getDomStudent().getId()});
        return result;
    }

    public static Boolean removeTeacherFromSchool(DomTeacher submit) throws Dwo2Exception {
        RestTeacher restTeacher = new RestTeacher();
        restTeacher.setRestContext(new DomContext());
        restTeacher.setDomTeacher(submit);

        Boolean result = StoredRestManager.getInstance().put("rest/secure/schooladmin/school/removeTeacher", Boolean.class, restTeacher);
        LOG.log(Level.FINE, "Submitted student {1} for removal from school by user with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), restTeacher.getDomTeacher().getId()});
        return result;
    }

    public static Boolean removeSchoolAdminFromSchool(DomSchoolAdmin submit) throws Dwo2Exception {
        RestSchoolAdmin restSchoolAdmin = new RestSchoolAdmin();
        restSchoolAdmin.setRestContext(new DomContext());
        restSchoolAdmin.setDomSchoolAdmin(submit);

        Boolean result = StoredRestManager.getInstance().put("rest/secure/schooladmin/school/removeSchoolAdmin", Boolean.class, restSchoolAdmin);
        LOG.log(Level.FINE, "Submitted student {1} for removal from school by user with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), restSchoolAdmin.getDomSchoolAdmin().getId()});
        return result;
    }

    public static List<DomSchoolClass> GetTeachersSchoolClasses(DomTeacher domTeacher) throws Dwo2Exception {
        RestTeacher restTeacher = new RestTeacher();
        restTeacher.setRestContext(new DomContext());
        restTeacher.setDomTeacher(domTeacher);
        List<DomSchoolClass>  result = StoredRestManager.getInstance().getPutList("rest/secure/schooladmin/school/getTeachersSchoolClassList", RestListClassTypes.DomSchoolClass, restTeacher);
        LOG.log(Level.FINE, "Retrieved {1} schoolclasses of teacher {2} for schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), result.size(), restTeacher.getDomTeacher().getId()});
        return result;
    }

    public static List<DomSchoolClass> GetStudentsSchoolClasses(DomStudent domStudent) throws Dwo2Exception {
        RestStudent restStudent = new RestStudent();
        restStudent.setRestContext(new DomContext());
        restStudent.setDomStudent(domStudent);
        List<DomSchoolClass>  result = StoredRestManager.getInstance().getPutList("rest/secure/schooladmin/school/getStudentsSchoolClassList", RestListClassTypes.DomSchoolClass, restStudent);
        LOG.log(Level.FINE, "Retrieved {1} schoolclasses of student {2} for schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), result.size(), restStudent.getDomStudent().getId()});
        return result;
    }

    public static Boolean submitTeacher(DomUserFull submit) throws Dwo2Exception {
        RestUserFull sts = new RestUserFull();
        sts.setRestContext(new DomContext());
        sts.setDomUserFull(submit);
        Boolean result = StoredRestManager.getInstance().put("rest/secure/schooladmin/school/submitTeacher", Boolean.class, sts);
        LOG.log(Level.FINE, "Submitted new user {1} enlisted as teacher in the school by user {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sts.getDomUserFull().getId()});
        return result;
    }

    
            
}
