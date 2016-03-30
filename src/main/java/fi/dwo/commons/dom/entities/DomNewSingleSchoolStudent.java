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
public class DomNewSingleSchoolStudent {

    private DomSingleSchoolStudent domSingleSchoolStudent;
    private DomSchoolClass domSchoolClass;

    public DomNewSingleSchoolStudent() {
      
    }

    @Deprecated
    public DomNewSingleSchoolStudent(PersistentUser u) {
        domSingleSchoolStudent = new DomSingleSchoolStudent(u);
    }
    
    public DomNewSingleSchoolStudent(DomSingleSchoolStudent u) {
    	domSingleSchoolStudent = new DomSingleSchoolStudent(u);
    }
    
    public void clearSettings(){
        if(domSingleSchoolStudent==null){
            domSingleSchoolStudent= new DomSingleSchoolStudent();
            domSingleSchoolStudent.clearSettings();
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
    public DomSingleSchoolStudent getDomSingleSchoolStudent() {
        return domSingleSchoolStudent;
    }

    /**
     * @param domSingleSchoolStudent the domSingleSchoolStudent to set
     */
    public void setDomSingleSchoolStudent(DomSingleSchoolStudent domSingleSchoolStudent) {
        this.domSingleSchoolStudent = domSingleSchoolStudent;
    }

}
