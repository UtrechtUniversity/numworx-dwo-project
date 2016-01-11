/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomContext;
import fi.dwo.commons.dom.entities.DomSchoolAdmin;
import fi.dwo.commons.persistence.entities.PersistentUser;
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
