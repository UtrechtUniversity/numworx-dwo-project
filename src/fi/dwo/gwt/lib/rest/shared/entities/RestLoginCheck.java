/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.gwt.lib.rest.shared.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RestLoginCheck{

    private DomContext restContext;
    private DomLoginCheck loginCheck;
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
     * @return the domNewUser
     */
    public DomLoginCheck getDomLoginCheck() {
        return loginCheck;
    }

    /**
     * @param loginCheck the domNewUser to set
     */
    public void setDomLoginCheck(DomLoginCheck loginCheck) {
        this.loginCheck = loginCheck;
    }

}
