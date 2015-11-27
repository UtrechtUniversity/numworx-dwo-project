/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.dom.entities;

import fi.dwo.commons.persistence.RoleType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Carries the information for a new user registration. Note that the password is
 * expected to be MD5 encrypted. If a registration without school is made.
 * schoolLogin and schoolCode should both be null.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomNewSchoolLogin {
    private String schoolLogin;
    private String schoolCode;
    private RoleType role;

    
    public DomNewSchoolLogin(){
        
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
    public RoleType getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(RoleType role) {
        this.role = role;
    }
    
}
