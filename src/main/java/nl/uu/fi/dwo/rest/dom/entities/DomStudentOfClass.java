/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * 
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class DomStudentOfClass {
    PersistenceId classId;
    PersistenceId studentId;

    public DomStudentOfClass(){
        super();
    }
       
}
