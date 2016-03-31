/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.dom.entities;

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
    

    public DomSchoolAdmin(DomUser u) {
    	super(u);
    }
}
