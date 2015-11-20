/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Schools and classes transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestSchoolClass4Teacher extends RestSchoolClass {
    private String registrationKey;
    private int iconizer;

    /**
     * @return the iconizer
     */
    public int getIconizer() {
        return iconizer;
    }

    /**
     * @param iconizer the iconizer to set
     */
    public void setIconizer(int iconizer) {
        this.iconizer = iconizer;
    }

    /**
     * @return the registrationKey
     */
    public String getRegistrationKey() {
        return registrationKey;
    }

    /**
     * @param registrationKey the registrationKey to set
     */
    public void setRegistrationKey(String registrationKey) {
        this.registrationKey = registrationKey;
    }
    
    
}
