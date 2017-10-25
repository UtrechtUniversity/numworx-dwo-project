/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewAccessKey;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestSchoolClassCourseProfilewAccessKey {

    private DomContext restContext;
    private DomSchoolClassCourseProfilewAccessKey domSchoolClassCourseProfilewAccessKey;


    public RestSchoolClassCourseProfilewAccessKey() {

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
	 * @return the domSchoolClassCourseProfilewAccessKey
	 */
	public DomSchoolClassCourseProfilewAccessKey getDomSchoolClassCourseProfilewAccessKey() {
		return domSchoolClassCourseProfilewAccessKey;
	}

	/**
	 * @param domSchoolClassCourseProfilewAccessKey the domSchoolClassCourseProfilewAccessKey to set
	 */
	public void setDomSchoolClassCourseProfilewAccessKey(DomSchoolClassCourseProfilewAccessKey domSchoolClassCourseProfilewAccessKey) {
		this.domSchoolClassCourseProfilewAccessKey = domSchoolClassCourseProfilewAccessKey;
	}

}
