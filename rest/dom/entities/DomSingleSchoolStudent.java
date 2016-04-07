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
public class DomSingleSchoolStudent extends DomUserFull {

    public DomSingleSchoolStudent() {
        super();
    }

    public DomSingleSchoolStudent(DomUserFull user) {
        super(user);
    }

}
