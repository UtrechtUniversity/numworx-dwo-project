/**
 * Copyrighted Dec 18, 2015
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureTeacherSchoolClassManager;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Gert van der Plas
 */
public class TeacherMenuPanelProperties {

    private static final Logger LOG = Logger.getLogger(TeacherMenuPanelProperties.class.getName());
    private List<DomSchoolClass> schoolClassList;    
    
    TeacherMenuPanelProperties() throws Dwo2Exception{
        init();
    }
    
    public void init() throws Dwo2Exception{
        schoolClassList = SecureTeacherSchoolClassManager.getTeachersSchoolClasses();
    }

    /**
     * @return the schoolClassList
     */
    public List<DomSchoolClass> getSchoolClassList() {
        return schoolClassList;
    }
    
}
