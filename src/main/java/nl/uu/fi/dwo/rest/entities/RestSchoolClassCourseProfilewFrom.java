/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewFrom;

/**
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestSchoolClassCourseProfilewFrom {

    private DomContext restContext;
    private DomSchoolClassCourseProfilewFrom domSchoolClassCourseProfilewFrom;


    public RestSchoolClassCourseProfilewFrom() {

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
    public DomSchoolClassCourseProfilewFrom getDomSchoolClassCourseProfilewFrom() {
        return domSchoolClassCourseProfilewFrom;
    }

    /**
     * @param domData the domSchoolClassAndProfile to set
     */
    public void setDomSchoolClassCourseProfilewFrom(DomSchoolClassCourseProfilewFrom domData) {
        this.domSchoolClassCourseProfilewFrom = domData;
    }

}
