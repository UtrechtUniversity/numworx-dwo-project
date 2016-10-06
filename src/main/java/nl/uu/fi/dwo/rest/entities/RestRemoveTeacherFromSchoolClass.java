/**
 * Copyrighted Nov 20, 2015
 */
package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomRemoveTeacherFromSchoolClass;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class RestRemoveTeacherFromSchoolClass {
    private DomRemoveTeacherFromSchoolClass domRemoveTeacherFromSchoolClass;
    private DomContext restContext;

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
     * @return the domRemoveTeacherFromSchoolClass
     */
    public DomRemoveTeacherFromSchoolClass getDomRemoveTeacherFromSchoolClass() {
        return domRemoveTeacherFromSchoolClass;
    }

    /**
     * @param domRemoveTeacherFromSchoolClass the domRemoveTeacherFromSchoolClass to set
     */
    public void setDomRemoveTeacherFromSchoolClass(DomRemoveTeacherFromSchoolClass domRemoveTeacherFromSchoolClass) {
        this.domRemoveTeacherFromSchoolClass = domRemoveTeacherFromSchoolClass;
    }
}
