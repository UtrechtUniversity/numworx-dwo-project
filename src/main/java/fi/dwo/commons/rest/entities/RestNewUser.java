/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomNewUser;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Carries the information for a new user registration. Note that the password
 * is expected to be MD5 encrypted. If a registration without school is made.
 * schoolLogin and schoolCode should both be null.
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestNewUser{

    private RestContext restContext;
    private DomNewUser domNewUser;
    /**
     * @return the restContext
     */
    public RestContext getRestContext() {
        return restContext;
    }

    /**
     * @param restContext the restContext to set
     */
    public void setRestContext(RestContext restContext) {
        this.restContext = restContext;
    }

    /**
     * @return the domNewUser
     */
    public DomNewUser getDomNewUser() {
        return domNewUser;
    }

    /**
     * @param domNewUser the domNewUser to set
     */
    public void setDomNewUser(DomNewUser domNewUser) {
        this.domNewUser = domNewUser;
    }

}
