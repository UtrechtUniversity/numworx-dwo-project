/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Schools and classes transported over the REST interface.
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomSchoolClassFull extends DomSchoolClass {
    
    private String registrationKey = null;
    
    public DomSchoolClassFull() {
        
    }
    
    /**
     * @return the registrationKey
     */
    public String getRegistrationKey() {
        return registrationKey;
    }

    /**
     * Updates the hasRegKey property.
     * 
     * @param registrationKey the registrationKey to set
     */
    public void setRegistrationKey(String registrationKey) {
        this.registrationKey = registrationKey;
        if (registrationKey != null) {
            super.setHasRegKey(true);
        } else {
            super.setHasRegKey(false);
        }        
    }
    
}
