/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherAndHasRole;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestTeacherAndHasRole {
    private DomContext restContext;
    private DomTeacherAndHasRole domTeacherAndHasRole;

    /**
     * @return the restContext
     */
    public DomContext getRestContext() {
        return restContext;
    }

    /**
     * @param restContext the restContext to set
     */
    public void setRestContext(DomContext restContext) {
        this.restContext = restContext;
    }

    /**
     * @return the domTeacherAndHasRole
     */
    public DomTeacherAndHasRole getDomTeacherAndHasRole() {
        return domTeacherAndHasRole;
    }

    /**
     * @param domTeacherAndHasRole the domTeacherAndHasRole to set
     */
    public void setDomTeacherAndHasRole(DomTeacherAndHasRole domTeacherAndHasRole) {
        this.domTeacherAndHasRole = domTeacherAndHasRole;
    }

}
