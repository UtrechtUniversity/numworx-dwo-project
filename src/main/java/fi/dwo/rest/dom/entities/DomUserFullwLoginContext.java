/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Carries the information for a new user registration. Note that the password is
 * expected to be MD5 encrypted. If a registration without school is made.
 * schoolLogin and schoolCode should both be null.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomUserFullwLoginContext {
    private DomUserFull domUserFull;
    private DomLoginContext domLoginContext;

    
    public DomUserFullwLoginContext(){
        
    }

    /**
     * @return the domLoginContext
     */
    public DomLoginContext getDomLoginContext() {
        return domLoginContext;
    }

    /**
     * @param domLoginContext the domLoginContext to set
     */
    public void setDomLoginContext(DomLoginContext domLoginContext) {
        this.domLoginContext = domLoginContext;
    }

    /**
     * @return the domUserFull
     */
    public DomUserFull getDomUserFull() {
        return domUserFull;
    }

    /**
     * @param domUserFull the domUserFull to set
     */
    public void setDomUserFull(DomUserFull domUserFull) {
        this.domUserFull = domUserFull;
    }
    
}
