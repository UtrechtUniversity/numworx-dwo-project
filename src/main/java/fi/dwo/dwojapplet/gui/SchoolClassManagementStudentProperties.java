/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomNewSchoolClass4Student;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureStudentSchoolClassManager;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author Gert van der Plas
 */
public class SchoolClassManagementStudentProperties {

    private static final Logger LOG = Logger.getLogger(SchoolClassManagementStudentProperties.class.getName());
    private DomSchoolClass activeSchoolClass;
    private List<DomSchoolClass> scList;

    public void init() throws Dwo2Exception {
        try {
            scList = SecureStudentSchoolClassManager.getStudentsSchoolClasses();
            activeSchoolClass = SecureStudentSchoolClassManager.getActiveSchoolClass();
        } catch (Dwo2Exception ex) {
            if (ex.getDwo2Code().equals(Dwo2ExceptionCode.Rest_User_Has_No_Active_SchoolClass)) {
                LOG.log(Level.FINE, ex.getMessage());
            } else {
                LOG.log(Level.SEVERE, ex.getMessage());
                if (scList == null) {
                    scList = new ArrayList<DomSchoolClass>();
                }
                activeSchoolClass = null;
                throw ex;
            }
        }
    }

    /**
     * @return the user
     */
    public List<DomSchoolClass> getStudentsSchoolClasses() {
        return scList;
    }

    /**
     * @return the user
     */
    public List<DomSchoolClass> getSchoolsClasses() throws Dwo2Exception {
        return SecureStudentSchoolClassManager.getSchoolsClasses();
    }

    /**
     * @return the user
     */
    public DomSchoolClass getActiveSchoolClass() {
        return activeSchoolClass;
    }

    public void removeSchoolClass(DomSchoolClass sc) throws Dwo2Exception {
        if (activeSchoolClass != sc) {
            SecureStudentSchoolClassManager.removeSchoolClass(sc);
            init();
        }
    }

    /**
     * @param activeSchoolClass
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public void setActiveSchoolClass(DomSchoolClass activeSchoolClass) throws Dwo2Exception {
        SecureStudentSchoolClassManager.setActiveSchoolClass(activeSchoolClass);
    }

    public void registerStudentForSchoolClass(DomNewSchoolClass4Student submit) throws Dwo2Exception {
        SecureStudentSchoolClassManager.registerStudentForSchoolClass(submit);
        init();
    }

    public List<DomSchoolClass> getUnregisteredSchoolClasses() throws Dwo2Exception {
        List<DomSchoolClass> schoolsClasses = getSchoolsClasses();
        List<DomSchoolClass> studentsClasses = getStudentsSchoolClasses();
        List<DomSchoolClass> result = new ArrayList<>(schoolsClasses.size() - studentsClasses.size());
        for (DomSchoolClass c : schoolsClasses) {
            Boolean flag = true; //add teacher to result list
            for (DomSchoolClass sc : studentsClasses) {
                if (sc.getId().equals(c.getId())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result.add(c);
            }
        }
        return result;
    }
}
