/**
 * Copyrighted Nov 20, 2015
 */
package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomMoveStudentToSchoolClass;

/**
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class RestMoveStudentToSchoolClass {
    private DomContext restContext;
    private DomMoveStudentToSchoolClass domMoveStudentToSchoolClass;
    /**
     * @return the restContext
     */
    public DomContext getRestContext() {
        return restContext;
    }

    /**
     * @param restContext the restContext to set
     */
    public void setRestContext(DomContext restContext) {
        this.restContext = restContext;
    }

    /**
     * @return the domSubmitStudentToSchoolClass
     */
    public DomMoveStudentToSchoolClass getDomMoveStudentToSchoolClass() {
        return domMoveStudentToSchoolClass;
    }

    /**
     * @param domMoveStudentToSchoolClass the domSubmitStudentToSchoolClass to set
     */
    public void setDomMoveStudentToSchoolClass(DomMoveStudentToSchoolClass domMoveStudentToSchoolClass) {
        this.domMoveStudentToSchoolClass = domMoveStudentToSchoolClass;
    }
    
}
