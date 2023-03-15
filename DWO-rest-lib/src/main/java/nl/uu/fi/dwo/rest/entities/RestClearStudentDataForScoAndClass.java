package nl.uu.fi.dwo.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomClearStudentDataForScoAndClass;

/**
 * Transmits ScoContext and SchoolClass information needed to clear the data.While the
 * Student
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestClearStudentDataForScoAndClass extends RestContext{
    private DomClearStudentDataForScoAndClass clearStudentDataForScoAndClass;

    /**
     * @return the clearStudentDataForScoAndClass
     */
    public DomClearStudentDataForScoAndClass getClearStudentDataForScoAndClass() {
        return clearStudentDataForScoAndClass;
    }

    /**
     * @param clearStudentDataForScoAndClass the clearStudentDataForScoAndClass to set
     */
    public void setClearStudentDataForScoAndClass(DomClearStudentDataForScoAndClass clearStudentDataForScoAndClass) {
        this.clearStudentDataForScoAndClass = clearStudentDataForScoAndClass;
    }
    
}
