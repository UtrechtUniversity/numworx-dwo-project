/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Minimal school transported over the REST interface for from/to
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomSchoolFrom extends DomSchoolId {
    private String schoolName;

    public DomSchoolFrom(){
        
    }
    
    /**
     * @return the schoolName
     */
    public String getSchoolName() {
        return schoolName;
    }

    /**
     * @param schoolName the schoolName to set
     */
    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
   
}
