/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dom.commons.dom.entities.DomSchool4Admin;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestSchool4Admin extends DomSchool4Admin{
    private RestContext restContext;
   
    public RestSchool4Admin(PersistentSchool s) {
        super(s);
    }

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
}
