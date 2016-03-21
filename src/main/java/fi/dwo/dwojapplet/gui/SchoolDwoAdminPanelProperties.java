/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomSchool4DwoAdmin;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureDwoAdminSchoolManager;
import fi.dwo.dwojapplet.domain.rest.SecureTeacherSchoolClassManager;
import java.util.List;
import java.util.logging.Logger;

/**
 * Property class for ClassTeacherPanel
 * @author G.A.J. van der Plas
 */
public class SchoolDwoAdminPanelProperties {

    private static final Logger LOG = Logger.getLogger(SchoolDwoAdminPanelProperties.class.getName());
//    private DomSchoolClassFull schoolClass = new DomSchoolClassFull();

    public SchoolDwoAdminPanelProperties(){
        
    }
    
    List<DomSchool4DwoAdmin> getSchoolList() throws Dwo2Exception {
        return SecureDwoAdminSchoolManager.getSchoolList();
    }

}
