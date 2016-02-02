/**
 * Copyrighted Nov 20, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomContext;
import fi.dwo.commons.dom.entities.DomRemoveStudentFromSchoolClass;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class RestRemoveStudentFromSchoolClass {
    private DomRemoveStudentFromSchoolClass domRemoveStudentFromSchoolClass;
    private DomContext restContext;

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
     * @return the domRemoveStudentFromSchoolClass
     */
    public DomRemoveStudentFromSchoolClass getDomRemoveStudentFromSchoolClass() {
        return domRemoveStudentFromSchoolClass;
    }

    /**
     * @param domRemoveStudentFromSchoolClass the domRemoveStudentFromSchoolClass to set
     */
    public void setDomRemoveStudentFromSchoolClass(DomRemoveStudentFromSchoolClass domRemoveStudentFromSchoolClass) {
        this.domRemoveStudentFromSchoolClass = domRemoveStudentFromSchoolClass;
    }
}
