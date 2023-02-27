/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestSchool4DwoAdmin {
    private DomContext restContext;
    private DomSchool4DwoAdmin domSchool4DwoAdmin;

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
     * @return the domSchool4DwoAdmin
     */
    public DomSchool4DwoAdmin getDomSchool4DwoAdmin() {
        return domSchool4DwoAdmin;
    }

    /**
     * @param domSchool4DwoAdmin the domSchool4DwoAdmin to set
     */
    public void setDomSchool4DwoAdmin(DomSchool4DwoAdmin domSchool4DwoAdmin) {
        this.domSchool4DwoAdmin = domSchool4DwoAdmin;
    }
}
