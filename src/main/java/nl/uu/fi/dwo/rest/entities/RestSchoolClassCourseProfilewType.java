/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewType;

/**
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestSchoolClassCourseProfilewType {

    private DomContext restContext;
    private DomSchoolClassCourseProfilewType domSchoolClassCourseProfilewType;


    public RestSchoolClassCourseProfilewType() {

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
    public DomSchoolClassCourseProfilewType getDomSchoolClassCourseProfilewType() {
        return domSchoolClassCourseProfilewType;
    }

    /**
     * @param domData the domSchoolClassAndProfile to set
     */
    public void setDomSchoolClassCourseProfilewType(DomSchoolClassCourseProfilewType domData) {
        this.domSchoolClassCourseProfilewType = domData;
    }

}
