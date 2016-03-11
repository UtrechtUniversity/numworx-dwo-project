/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.dom.entities;

import fi.dwo.commons.persistence.entities.PersistentUser;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class DomSchoolAdmin extends DomUser {

    public DomSchoolAdmin(){
        super();
    }
    
    public DomSchoolAdmin(PersistentUser u) {
        super(u);
    }

}
