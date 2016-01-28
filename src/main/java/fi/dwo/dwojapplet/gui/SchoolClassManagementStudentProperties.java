/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomNewSchoolClass4Student;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureStudentSchoolClassManager;
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
        }
        catch (Dwo2Exception ex) {

            LOG.log(Level.SEVERE, ex.getMessage());
            if (scList == null) {
                scList = new ArrayList<DomSchoolClass>();
            }
            activeSchoolClass = null;
            throw ex;
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
        }
    }

    /**
     * @param activeSchoolClass
     * @throws fi.dwo.commons.exceptions.Dwo2Exception
     */
    public void setActiveSchoolClass(DomSchoolClass activeSchoolClass) throws Dwo2Exception {
        SecureStudentSchoolClassManager.setActiveSchoolClass(activeSchoolClass);
    }

    public void registerStudentForSchoolClass(DomNewSchoolClass4Student submit) throws Dwo2Exception {
        SecureStudentSchoolClassManager.registerStudentForSchoolClass(submit);
        init();
    }
    
    
}
