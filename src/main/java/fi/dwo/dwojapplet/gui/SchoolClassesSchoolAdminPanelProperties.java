/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureSchoolAdminSchoolClassManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Property class for ClassTeacherPanel
 * @author G.A.J. van der Plas
 */
public class SchoolClassesSchoolAdminPanelProperties {

    private static final Logger LOG = Logger.getLogger(SchoolClassesSchoolAdminPanelProperties.class.getName());
//    private DomSchoolClassFull schoolClass = new DomSchoolClassFull();
    private List<DomSchoolClass> scList;

    public SchoolClassesSchoolAdminPanelProperties(){
        
    }

    public void init() throws Dwo2Exception {
        try {
            scList = SecureSchoolAdminSchoolClassManager.getSchoolClasses();
        }
        catch (Dwo2Exception ex) {

            LOG.log(Level.SEVERE, ex.getMessage());
            scList = new ArrayList<DomSchoolClass>();
//            selectedSrc = null;
            throw ex;
        }
    }
    
//    
//    public DomSchoolClassFull getSchoolClass() {
//        return schoolClass;
//    }
//
//    public void setSchoolClass(DomSchoolClassFull sc) {
//        this.schoolClass = sc;
//    }
    
    public Boolean addClass(DomSchoolClassFull sc) throws Dwo2Exception{
        return SecureSchoolAdminSchoolClassManager.submitSchoolClass(sc);
    }

    public Boolean updateSchoolClass(DomSchoolClassFull sc) throws Dwo2Exception{
        return SecureSchoolAdminSchoolClassManager.updateSchoolClass(sc);
    }

    public Boolean removeSchoolClass(DomSchoolClass sc) throws Dwo2Exception{
        return SecureSchoolAdminSchoolClassManager.removeSchoolClass(sc);
    }

    public List<DomSchoolClass> getSchoolClassList() throws Dwo2Exception {
        // can changed to caching. However low frequent operation.
        init();
        return scList;
    }

    public DomSchoolClassFull getFullSchoolClass(DomSchoolClass sc) throws Dwo2Exception {
        return SecureSchoolAdminSchoolClassManager.getFullSchoolClass(sc);
    }
    
}
