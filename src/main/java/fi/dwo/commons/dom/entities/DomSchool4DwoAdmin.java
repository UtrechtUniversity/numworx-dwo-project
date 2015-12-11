/**
 * Copyrighted Sep 24, 2015
 */
package fi.dwo.commons.dom.entities;

import fi.dwo.commons.persistence.entities.PersistentSchool;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class DomSchool4DwoAdmin extends DomSchool{
    private String schoolLogin;

    public DomSchool4DwoAdmin(PersistentSchool s) {
        super(s);
        this.schoolLogin = s.getSchoolLogin();
    }

    /**
     * @return the schoolLogin
     */
    public String getSchoolLogin() {
        return schoolLogin;
    }

    /**
     * @param schoolLogin the schoolLogin to set
     */
    public void setSchoolLogin(String schoolLogin) {
        this.schoolLogin = schoolLogin;
    }
    
}
