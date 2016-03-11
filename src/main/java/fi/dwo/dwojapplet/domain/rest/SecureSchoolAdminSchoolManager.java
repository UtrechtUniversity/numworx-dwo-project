package fi.dwo.dwojapplet.domain.rest;

import fi.dwo.commons.dom.entities.DomContext;
import fi.dwo.commons.dom.entities.DomGetSingleSchoolStudent;
import fi.dwo.commons.dom.entities.DomSchoolAdmin;
import fi.dwo.commons.dom.entities.DomSingleSchoolStudent;
import fi.dwo.commons.dom.entities.DomStudent;
import fi.dwo.commons.dom.entities.DomTeacher;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.rest.RestListClassTypes;
import fi.dwo.commons.rest.entities.RestGetSingleSchoolStudent;
import fi.dwo.commons.rest.entities.RestSingleSchoolStudent;
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
public class SecureSchoolAdminSchoolManager {

    private static final Logger LOG = Logger.getLogger(SecureSchoolAdminSchoolManager.class.getName());

    public static List<DomTeacher> getTeachersInSchool() throws Dwo2Exception {
        List<DomTeacher> src;
        src = StoredRestManager.getInstance().getList("/rest/secure/schooladmin/school/getTeachersInSchoolList", RestListClassTypes.DomTeacher);
        LOG.log(Level.FINE, "Retrieved list of teachers in the school for the schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

    public static List<DomStudent> getStudentsInSchool() throws Dwo2Exception {
        List<DomStudent> src;
        src = StoredRestManager.getInstance().getList("/rest/secure/schooladmin/school/getStudentsInSchoolList", RestListClassTypes.DomStudent);
        LOG.log(Level.FINE, "Retrieved list of students in the school for the schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

    public static List<DomSchoolAdmin> getSchoolAdminsInSchool() throws Dwo2Exception {
        List<DomSchoolAdmin> src;
        src = StoredRestManager.getInstance().getList("/rest/secure/schooladmin/school/getSchoolAdminList", RestListClassTypes.DomSchoolAdmin);
        LOG.log(Level.FINE, "Retrieved list of schooladmins in the school for the schooladmin with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId()});
        return src;
    }

    public static DomSingleSchoolStudent getSingleSchoolStudent(DomGetSingleSchoolStudent submit) throws Dwo2Exception {
        RestGetSingleSchoolStudent sts = new RestGetSingleSchoolStudent();
        sts.setRestContext(new DomContext());
        sts.setDomGetSingleSchoolStudent(submit);
        DomSingleSchoolStudent result = StoredRestManager.getInstance().put("/rest/secure/schooladmin/school/getSingleSchoolStudent", DomSingleSchoolStudent.class, sts);
        LOG.log(Level.FINE, "Retrieved full single school student {1} for  teacher with id {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sts.getDomGetSingleSchoolStudent().getDomStudent().getId()});
        return result;
    }

    public static Boolean updateSingleSchoolStudent(DomSingleSchoolStudent submit) throws Dwo2Exception {
        RestSingleSchoolStudent sts = new RestSingleSchoolStudent();
        sts.setRestContext(new DomContext());
        sts.setDomSingleSchoolStudent(submit);
        Boolean result = StoredRestManager.getInstance().put("/rest/secure/schooladmin/school/updateSingleSchoolStudent", Boolean.class, sts);
        LOG.log(Level.FINE, "Updated acount data for singlschoolstudent {1} by user {0}.", new Object[]{DwoHelper.getCurrentUser().getId(), sts.getDomSingleSchoolStudent().getId()});
        return result;
    }    
}
