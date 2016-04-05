/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Schools and classes transported over the REST interface.
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomSchoolClassFull extends DomSchoolClass {
    
    private String registrationKey = null;
    private Boolean iconizer = Boolean.FALSE;
    
    public DomSchoolClassFull() {
        
    }
    
    /**
     * @return the iconizer
     */
    public Boolean getIconizer() {
        return iconizer;
    }

    /**
     * @param iconizer the iconizer to set
     */
    public void setIconizer(Boolean iconizer) {
        this.iconizer = iconizer;
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
