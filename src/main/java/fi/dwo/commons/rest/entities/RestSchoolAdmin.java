/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomSchoolAdmin;
import fi.dwo.commons.persistence.entities.PersistentUser;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestSchoolAdmin {
    private RestContext restContext;
    private DomSchoolAdmin domSchoolAdmin;
    
    /**
     * @return the restContext
     */
    public RestContext getRestContext() {
        return restContext;
    }

    /**
     * @param restContxt the restContext to set
     */
    public void setRestContext(RestContext restContext) {
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
