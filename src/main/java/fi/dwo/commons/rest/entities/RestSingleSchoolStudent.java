/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomContext;
import fi.dwo.commons.dom.entities.DomSingleSchoolStudent;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestSingleSchoolStudent {
    private DomContext restContext;
    private DomSingleSchoolStudent domSingleSchoolStudent; 
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
     * @return the domSingleSchoolStudent
     */
    public DomSingleSchoolStudent getDomSingleSchoolStudent() {
        return domSingleSchoolStudent;
    }

    /**
     * @param domSingleSchoolStudent the domSingleSchoolStudent to set
     */
    public void setDomSingleSchoolStudent(DomSingleSchoolStudent domSingleSchoolStudent) {
        this.domSingleSchoolStudent = domSingleSchoolStudent;
    }
    
}
