/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomSchoolClassFull;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureTeacherSchoolClassManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Property class for ClassTeacherPanel
 * @author G.A.J. van der Plas
 */
public class ClassTeacherPanelProperties {

    private static final Logger LOG = Logger.getLogger(ClassTeacherPanelProperties.class.getName());
//    private DomSchoolClassFull schoolClass = new DomSchoolClassFull();
    private List<DomSchoolClass> scList;

    public ClassTeacherPanelProperties(){
        
    }

    public void init() throws Dwo2Exception {
        try {
            scList = SecureTeacherSchoolClassManager.getTeachersSchoolClasses();
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
        return SecureTeacherSchoolClassManager.submitSchoolClass(sc);
    }

    public Boolean updateSchoolClass(DomSchoolClassFull sc) throws Dwo2Exception{
        return SecureTeacherSchoolClassManager.updateSchoolClass(sc);
    }

    public Boolean removeSchoolClass(DomSchoolClass sc) throws Dwo2Exception{
        return SecureTeacherSchoolClassManager.removeSchoolClass(sc);
    }

    public List<DomSchoolClass> getSchoolClassList() throws Dwo2Exception {
        // can changed to caching. However low frequent operation.
        init();
        return scList;
    }

    public DomSchoolClassFull getFullSchoolClass(DomSchoolClass sc) throws Dwo2Exception {
        return SecureTeacherSchoolClassManager.getFullSchoolClass(sc);
    }
    
}
