/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.rest.dom.entities;

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
       
    public DomStudent(DomUser u) {
    	super(u);
    }
}
