/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewTo;

/**
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestSchoolClassCourseProfilewTo {

    private DomContext restContext;
    private DomSchoolClassCourseProfilewTo domSchoolClassCourseProfilewTo;


    public RestSchoolClassCourseProfilewTo() {

    }

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
     * @return the domSchoolClassAndProfile
     */
    public DomSchoolClassCourseProfilewTo getDomSchoolClassCourseProfilewTo() {
        return domSchoolClassCourseProfilewTo;
    }

    /**
     * @param domData the domSchoolClassAndProfile to set
     */
    public void setDomSchoolClassCourseProfilewTo(DomSchoolClassCourseProfilewTo domData) {
        this.domSchoolClassCourseProfilewTo = domData;
    }

}
