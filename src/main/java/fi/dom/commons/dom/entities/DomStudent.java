/**
 * Copyrighted Sep 24, 2015
 */
package fi.dom.commons.dom.entities;

import fi.dwo.commons.persistence.entities.PersistentUser;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class DomStudent extends DomUser {

    public DomStudent(){
        super();
    }
        
    public DomStudent(PersistentUser u) {
        super(u);
    }
}
