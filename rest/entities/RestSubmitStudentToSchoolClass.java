/**
 * Copyrighted Nov 20, 2015
 */
package fi.dwo.rest.entities;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class RestSubmitStudentToSchoolClass {
    private DomContext restContext;
    private DomSubmitStudentToSchoolClass domSubmitStudentToSchoolClass;
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
    public DomSubmitStudentToSchoolClass getDomSubmitStudentToSchoolClass() {
        return domSubmitStudentToSchoolClass;
    }

    /**
     * @param domSubmitStudentToSchoolClass the domSubmitStudentToSchoolClass to set
     */
    public void setDomSubmitStudentToSchoolClass(DomSubmitStudentToSchoolClass domSubmitStudentToSchoolClass) {
        this.domSubmitStudentToSchoolClass = domSubmitStudentToSchoolClass;
    }
    
}
