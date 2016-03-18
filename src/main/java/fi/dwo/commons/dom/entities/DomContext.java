/**
 * Copyrighted Nov 24, 2015
 */
package fi.dwo.commons.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Transfers context information of a Rest call. Typical this user-state information
 * like hasRole. Defining the context in which a REST-function must be executed.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomContext {
    //TODO Insert hasRole property when legacy code is extinct and we can go stateless.
//    DomHasRole domHasRole; //security
}
