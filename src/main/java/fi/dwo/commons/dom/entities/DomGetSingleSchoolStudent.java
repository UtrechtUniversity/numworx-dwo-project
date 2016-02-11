/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.dom.entities;

import fi.dwo.commons.persistence.entities.PersistentUser;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomGetSingleSchoolStudent {

    private DomStudent domStudent;
    private DomSchoolClass domSchoolClass;

    public DomGetSingleSchoolStudent() {
      
    }

    public DomGetSingleSchoolStudent(PersistentUser u) {
        domStudent = new DomStudent(u);
    }
    
    public void clearSettings(){
        if(domStudent==null){
            domStudent= new DomStudent();
            domStudent.clearSettings();
        }
        if(domSchoolClass==null){
            domSchoolClass= new DomSchoolClass();
            domSchoolClass.clearSettings();
        }
    }
    /**
     * @return the domSchoolClass
     */
    public DomSchoolClass getDomSchoolClass() {
        return domSchoolClass;
    }

    /**
     * @param domSchoolClass the domSchoolClass to set
     */
    public void setDomSchoolClass(DomSchoolClass domSchoolClass) {
        this.domSchoolClass = domSchoolClass;
    }

    /**
     * @return the domSingleSchoolStudent
     */
    public DomStudent getDomStudent() {
        return domStudent;
    }

    /**
     * @param domStudent the domSingleSchoolStudent to set
     */
    public void setDomStudent(DomStudent domStudent) {
        this.domStudent = domStudent;
    }

}
