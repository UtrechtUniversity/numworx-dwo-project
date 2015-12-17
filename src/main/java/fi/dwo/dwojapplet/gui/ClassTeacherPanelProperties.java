/**
 * Copyrighted Dec 17, 2015
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomSchoolClass4Teacher;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.rest.SecureTeacherSchoolClassManager;
import java.util.logging.Logger;

/**
 * Property class for ClassTeacherPanel
 * @author G.A.J. van der Plas
 */
public class ClassTeacherPanelProperties {

    private static final Logger LOG = Logger.getLogger(ClassTeacherPanelProperties.class.getName());
    private DomSchoolClass4Teacher sc = new DomSchoolClass4Teacher();

    public ClassTeacherPanelProperties(){
        
    }

    /**
     * @return the sc
     */
    public DomSchoolClass4Teacher getSc() {
        return sc;
    }

    /**
     * @param sc the sc to set
     */
    public void setSc(DomSchoolClass4Teacher sc) {
        this.sc = sc;
    }
    
    public Boolean addClass() throws Dwo2Exception{
        return SecureTeacherSchoolClassManager.SubmitSchoolClass(sc);
    }
    
}
