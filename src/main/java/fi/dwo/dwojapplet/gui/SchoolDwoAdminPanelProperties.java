/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureDwoAdminSchoolManager;
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
    
    public List<DomSchool4DwoAdmin> getSchoolList() throws Dwo2Exception {
        return SecureDwoAdminSchoolManager.getSchoolList();
    }

    public Boolean deleteSchool(DomSchool4DwoAdmin school)  throws Dwo2Exception  {
        return SecureDwoAdminSchoolManager.removeSchool(school);
    }

}
