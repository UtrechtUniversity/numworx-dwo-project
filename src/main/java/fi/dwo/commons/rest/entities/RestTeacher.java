/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomContext;
import fi.dwo.commons.dom.entities.DomTeacher;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestTeacher {
    private DomContext restContext;
    private DomTeacher domTeacher;

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
     * @return the domTeacher
     */
    public DomTeacher getDomTeacher() {
        return domTeacher;
    }

    /**
     * @param domTeacher the domTeacher to set
     */
    public void setDomTeacher(DomTeacher domTeacher) {
        this.domTeacher = domTeacher;
    }

}
