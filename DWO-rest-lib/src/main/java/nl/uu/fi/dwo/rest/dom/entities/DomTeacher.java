/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class DomTeacher extends DomUser {

    public DomTeacher(){
        super();
    }
      
    public DomTeacher(DomUser u) {
    	super(u);
    }
    
}
