/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.persistence.entities.PersistentRole;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Carries the information for a new user registration. Note that the password is
 * expected to be MD5 encrypted. If a registration without school is made.
 * schoolLogin and schoolCode should both be null.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class KnownUserRegistration {
    private String schoolLogin;
    private String schoolCode;
    private PersistentRole role;

    
    public KnownUserRegistration(){
        
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

    /**
     * @return the schoolCode
     */
    public String getSchoolCode() {
        return schoolCode;
    }

    /**
     * @param schoolCode the schoolCode to set
     */
    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    /**
     * @return the role
     */
    public PersistentRole getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(PersistentRole role) {
        this.role = role;
    }
    
}
