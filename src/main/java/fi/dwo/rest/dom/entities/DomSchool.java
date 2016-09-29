/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.rest.dom.entities;

import fi.dwo.rest.persistence.PersistenceId;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * School transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomSchool {
    private PersistenceId id;
    private String schoolName;
    private String schoolRights;

    public DomSchool(){
        
    }
    
    /**
     * @return the id
     */
    public PersistenceId getId() {
        return id;
    }

    /**
     * @param schoolId the id to set
     */
    public void setId(PersistenceId schoolId) {
        this.id = schoolId;
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

    /**
     * @return the schoolRights
     */
    public String getSchoolRights() {
        return schoolRights;
    }

    /**
     * @param schoolRights the schoolRights to set. 
     */
    public void setSchoolRights(String schoolRights) {
        this.schoolRights = schoolRights;
    }
    
    public Boolean StudentsCanRegisterForSchoolClasses(){
        return schoolRights.contains("c");
    }
}
