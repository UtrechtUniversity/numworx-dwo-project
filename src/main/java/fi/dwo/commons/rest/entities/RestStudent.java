/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.persistence.PersistenceId;
import fi.dwo.commons.persistence.entities.PersistentUser;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class RestStudent extends RestUser {
    private PersistenceId id;
    private String givenName;
    private String familyName;
    private String familyNamePrefix;


    public RestStudent(){
        super();
    }
        
    public RestStudent(PersistentUser u) {
        super(u);
    }


}
