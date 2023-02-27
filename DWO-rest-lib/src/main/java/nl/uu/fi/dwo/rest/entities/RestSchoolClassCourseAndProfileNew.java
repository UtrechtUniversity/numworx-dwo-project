package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseAndProfileNew;

/**
 *
 * @author Gert van der Plas
 */
@XmlRootElement
public class RestSchoolClassCourseAndProfileNew {
    private DomContext restContext;
    private DomSchoolClassCourseAndProfileNew domSchoolClassCourseAndProfileNew;

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
     * @return the domSchoolClassCourseAndProfileNew
     */
    public DomSchoolClassCourseAndProfileNew getDomSchoolClassCourseAndProfileNew() {
        return domSchoolClassCourseAndProfileNew;
    }

    /**
     * @param domSchoolClassCourseAndProfileNew the domSchoolClassCourseAndProfileNew to set
     */
    public void setDomSchoolClassCourseAndProfileNew(DomSchoolClassCourseAndProfileNew domSchoolClassCourseAndProfileNew) {
        this.domSchoolClassCourseAndProfileNew = domSchoolClassCourseAndProfileNew;
    }
}
