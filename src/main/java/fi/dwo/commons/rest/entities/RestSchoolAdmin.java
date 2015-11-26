/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dom.commons.dom.entities.DomSchoolAdmin;
import fi.dwo.commons.persistence.entities.PersistentUser;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestSchoolAdmin extends DomSchoolAdmin {
    private RestContext restContext;
    
    public RestSchoolAdmin(PersistentUser u) {
        super(u);
    }

    /**
     * @return the restContext
     */
    public RestContext getRestContxt() {
        return restContext;
    }

    /**
     * @param restContxt the restContext to set
     */
    public void setRestContxt(RestContext restContxt) {
        this.restContext = restContxt;
    }

}
