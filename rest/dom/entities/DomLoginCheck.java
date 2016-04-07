/**
 * Copyrighted Mar 15, 2016
 */
package fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DomLoginCheck. Contains the data for doing a loginCheck.
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomLoginCheck {
    private String username;
    private String password;

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public static String crypt(String password) {
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if       (c >= 'a' && c <= 'm') c += 13;
            else if  (c >= 'A' && c <= 'M') c += 13;
            else if  (c >= 'n' && c <= 'z') c -= 13;
            else if  (c >= 'N' && c <= 'Z') c -= 13;
            else if  (c >= '0' && c <= '4') c += 5;
            else if  (c >= '5' && c <= '9') c -= 5;
            result.append(c);
        }         
        return result.toString();
    }   
}
