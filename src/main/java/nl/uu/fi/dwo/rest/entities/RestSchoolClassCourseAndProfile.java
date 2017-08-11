/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseAndProfile;

/**
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestSchoolClassCourseAndProfile {

    private DomContext restContext;
    private DomSchoolClassCourseAndProfile domSchoolClassCourseAndProfile;


    public RestSchoolClassCourseAndProfile() {

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
    public DomSchoolClassCourseAndProfile getDomSchoolClassCourseAndProfile() {
        return domSchoolClassCourseAndProfile;
    }

    /**
     * @param aDomSchoolClassCourseAndProfile the domSchoolClassAndProfile to set
     */
    public void setDomSchoolClassCourseAndProfile(DomSchoolClassCourseAndProfile aDomSchoolClassCourseAndProfile) {
        this.domSchoolClassCourseAndProfile = aDomSchoolClassCourseAndProfile;
    }

}
