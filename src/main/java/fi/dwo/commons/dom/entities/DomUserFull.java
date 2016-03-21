/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.dom.entities;

import fi.dwo.commons.persistence.entities.PersistentUser;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DomUser
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomUserFull extends DomUser implements Serializable {
    private String password = "";
    private String email = "";

    public DomUserFull() {
    }

    public DomUserFull(DomUserFull user) {
        super(user);
        setPassword(user.getPassword());
        setEmail(user.getEmail());
    }
    
    public DomUserFull(PersistentUser user){
        super(user);
        setPassword(user.getPassword());
        setEmail(user.getEmail());
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

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

}
