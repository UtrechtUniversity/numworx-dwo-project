/**
 * Copyrighted Nov 20, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomContext;
import fi.dwo.commons.dom.entities.DomRemoveTeacherFromSchoolClass;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomTeacher;
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
