/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.rest.entities;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomSchoolAdmin;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestSchoolAdmin {
    private DomContext restContext;
    private DomSchoolAdmin domSchoolAdmin;
    
    /**
     * @return the restContext
     */
    public DomContext getRestContext() {
        return restContext;
    }

    /**
     * @param restContxt the restContext to set
     */
    public void setRestContext(DomContext restContext) {
        this.restContext = restContext;
    }

    /**
     * @return the domSchoolAdmin
     */
    public DomSchoolAdmin getDomSchoolAdmin() {
        return domSchoolAdmin;
    }

    /**
     * @param domSchoolAdmin the domSchoolAdmin to set
     */
    public void setDomSchoolAdmin(DomSchoolAdmin domSchoolAdmin) {
        this.domSchoolAdmin = domSchoolAdmin;
    }

}
