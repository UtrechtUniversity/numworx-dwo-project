/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */

@XmlRootElement
public class DomSchool4DwoAdmin extends DomSchool{
    private String schoolLogin;

    public DomSchool4DwoAdmin() {
        
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
    
    public String getUniqueDisplayName() {
        StringBuilder result = new StringBuilder();
        result.append(this.schoolLogin);
        result.append(" - ");
        result.append(this.getSchoolName());

        return result.toString();
    }
    
}
