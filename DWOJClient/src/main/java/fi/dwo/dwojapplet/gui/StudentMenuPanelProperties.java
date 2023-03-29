/**
 * Copyrighted Dec 18, 2015
 */
package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureStudentSchoolClassManager;

import java.util.Collections;
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
        schoolClassList = Collections.emptyList(); // SecureStudentSchoolClassManager.getStudentsSchoolClasses();
    }

    /**
     * @return the schoolClassList
     */
    public List<DomSchoolClass> getSchoolClassList() {
        return schoolClassList;
    }
    
}
