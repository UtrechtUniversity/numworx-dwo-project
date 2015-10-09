/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.persistence.entities.PersistentUser;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestSchoolAdmin extends RestUser {

    public RestSchoolAdmin(){
        super();
    }
        
    public RestSchoolAdmin(PersistentUser u) {
        super(u);
    }

}
