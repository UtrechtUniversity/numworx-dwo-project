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
public class RestTeacher extends RestUser {

    public RestTeacher(){
        super();
    }
        
    public RestTeacher(PersistentUser u) {
        super(u);
    }

}
