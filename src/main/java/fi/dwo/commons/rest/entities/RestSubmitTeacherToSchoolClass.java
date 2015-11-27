/**
 * Copyrighted Nov 20, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomSubmitTeacherToSchoolClass;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class RestSubmitTeacherToSchoolClass extends DomSubmitTeacherToSchoolClass{
    private RestContext restContext;
    private DomSubmitTeacherToSchoolClass domSubmitTeacherToSchoolClass;

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
     * @return the domSubmitTeacherToSchoolClass
     */
    public DomSubmitTeacherToSchoolClass getDomSubmitTeacherToSchoolClass() {
        return domSubmitTeacherToSchoolClass;
    }

    /**
     * @param domSubmitTeacherToSchoolClass the domSubmitTeacherToSchoolClass to set
     */
    public void setDomSubmitTeacherToSchoolClass(DomSubmitTeacherToSchoolClass domSubmitTeacherToSchoolClass) {
        this.domSubmitTeacherToSchoolClass = domSubmitTeacherToSchoolClass;
    }
    
}
