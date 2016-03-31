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
public class DomStudent extends DomUser {

    public DomStudent(){
        super();
    }
       
//    @Deprecated
//    public DomStudent(PersistentUser u) {
//        super(u);
//    }
    
    public DomStudent(DomUser u) {
    	super(u);
    }
}
