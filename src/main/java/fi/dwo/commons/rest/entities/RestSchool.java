/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceId;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * School transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestSchool {
    private PersistenceId schoolId;
    private String schoolName;

    RestSchool(){
        
    }
    
    RestSchool(PersistentSchool s){
        this.schoolId = MySQLPersistenceId.createPersistentId(s);
        this.schoolName=s.getSchoolName();
    }
    
    /**
     * @return the schoolId
     */
    public PersistenceId getSchoolId() {
        return schoolId;
    }

    /**
     * @param schoolId the schoolId to set
     */
    public void setSchoolId(PersistenceId schoolId) {
        this.schoolId = schoolId;
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
