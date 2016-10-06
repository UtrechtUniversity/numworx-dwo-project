/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.rest.entities;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomNewSchoolLogin;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Carries the information for a new user registration. Note that the password is
 * expected to be MD5 encrypted. If a registration without school is made.
 * schoolLogin and schoolCode should both be null.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestNewSchoolLogin {
    private DomContext restContext;
    private DomNewSchoolLogin domNewSchoolLogin;

    /**
     * @return the restContext
     */
    public DomContext getRestContext() {
        return restContext;
    }

    /**
     * @param restContext the restContext to set
     */
    public void setRestContext(DomContext restContext) {
        this.restContext = restContext;
    }

    /**
     * @return the domNewSchoolLogin
     */
    public DomNewSchoolLogin getDomNewSchoolLogin() {
        return domNewSchoolLogin;
    }

    /**
     * @param domNewSchoolLogin the domNewSchoolLogin to set
     */
    public void setDomNewSchoolLogin(DomNewSchoolLogin domNewSchoolLogin) {
        this.domNewSchoolLogin = domNewSchoolLogin;
    }

    
}
