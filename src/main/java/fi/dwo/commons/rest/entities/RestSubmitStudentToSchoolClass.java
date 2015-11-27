/**
 * Copyrighted Nov 20, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomSubmitStudentToSchoolClass;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class RestSubmitStudentToSchoolClass extends DomSubmitStudentToSchoolClass{
    private RestContext restContext;
    private DomSubmitStudentToSchoolClass domSubmitStudentToSchoolClass;
    /**
     * @return the restContext
     */
    public RestContext getRestContext() {
        return restContext;
    }

    /**
     * @param restContext the restContext to set
     */
    public void setRestContext(RestContext restContext) {
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
