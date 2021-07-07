/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolClass4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureStudentSchoolClassManager;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author Gert van der Plas
 * @deprecated
 */
public class SchoolClassManagementStudentProperties {

    private static final Logger LOG = Logger.getLogger(SchoolClassManagementStudentProperties.class.getName());
    private DomSchoolClass activeSchoolClass;
    private List<DomSchoolClass> scList;

    public void init() throws Dwo2Exception {
        try {
            scList = initStudentsSchoolClasses(); 
            activeSchoolClass = null; // SecureStudentSchoolClassManager.getActiveSchoolClass();
        } catch (Dwo2Exception ex) {
            if (ex.getDwo2Code().equals(Dwo2ExceptionCode.Rest_Active_SchoolClass_Not_Set)) {
                LOG.log(Level.FINE, ex.getMessage());
            } else {
                LOG.log(Level.SEVERE, ex.getMessage());
                if (scList == null) {
                    scList = getStudentsSchoolClasses();
                }
                activeSchoolClass = null;
                throw ex;
            }
        }
    }

    ArrayList<DomSchoolClass> initStudentsSchoolClasses() throws Dwo2Exception {
      return new ArrayList<DomSchoolClass>(); //SecureStudentSchoolClassManager.getStudentsSchoolClasses();
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
        return new ArrayList<DomSchoolClass>(); //SecureStudentSchoolClassManager.getSchoolsClasses();
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
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
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
        List<DomSchoolClass> result = new ArrayList<DomSchoolClass>(schoolsClasses.size() - studentsClasses.size());
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
