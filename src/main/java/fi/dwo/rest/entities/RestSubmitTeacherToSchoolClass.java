/**
 * Copyrighted Nov 20, 2015
 */
package fi.dwo.rest.entities;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomSubmitTeacherToSchoolClass;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class RestSubmitTeacherToSchoolClass{
    private DomContext restContext;
    private DomSubmitTeacherToSchoolClass domSubmitTeacherToSchoolClass;

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
