/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomContext;
import fi.dwo.commons.dom.entities.DomSchool4DwoAdmin;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestSchool4Admin {
    private DomContext restContext;
    private DomSchool4DwoAdmin domSchool4Admin;

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
     * @return the domSchool4Admin
     */
    public DomSchool4DwoAdmin getDomSchool4Admin() {
        return domSchool4Admin;
    }

    /**
     * @param domSchool4Admin the domSchool4Admin to set
     */
    public void setDomSchool4Admin(DomSchool4DwoAdmin domSchool4Admin) {
        this.domSchool4Admin = domSchool4Admin;
    }
}
