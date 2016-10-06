/**
 * Copyrighted Dec 18, 2015
 */
package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureStudentSchoolClassManager;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
public class StudentMenuPanelProperties {

    private static final Logger LOG = Logger.getLogger(StudentMenuPanelProperties.class.getName());
    private List<DomSchoolClass> schoolClassList;    
    
    StudentMenuPanelProperties() throws Dwo2Exception{
        init();
    }
    
    public void init() throws Dwo2Exception{
        schoolClassList = SecureStudentSchoolClassManager.getStudentsSchoolClasses();
    }

    /**
     * @return the schoolClassList
     */
    public List<DomSchoolClass> getSchoolClassList() {
        return schoolClassList;
    }
    
}
