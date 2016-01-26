/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomContext;
import fi.dwo.commons.dom.entities.DomNewSchoolClass4Student;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestNewSchoolClass4Student {
    private DomContext restContext;
    private DomNewSchoolClass4Student domSchoolClass;
    
    public RestNewSchoolClass4Student(){
        
    }

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
     * @return the domSchoolClass
     */
    public DomNewSchoolClass4Student getDomNewSchoolClass4Student() {
        return domSchoolClass;
    }

    /**
     * @param domSchoolClass the domSchoolClass to set
     */
    public void setDomNewSchoolClass4Student(DomNewSchoolClass4Student domSchoolClass) {
        this.domSchoolClass = domSchoolClass;
    }

}
