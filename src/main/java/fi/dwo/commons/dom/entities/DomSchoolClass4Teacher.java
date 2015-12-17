/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.dom.entities;

import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Schools and classes transported over the REST interface.
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomSchoolClass4Teacher extends DomSchoolClass {

    private String registrationKey = null;
    private Boolean iconizer = Boolean.FALSE;

    public DomSchoolClass4Teacher(){
        
    }

    
    public DomSchoolClass4Teacher(PersistentSchoolClass sc) {
        super(sc);
        iconizer = sc.getIconizer();
        registrationKey = sc.getRegistrationKey();
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
     * @param registrationKey the registrationKey to set
     */
    public void setRegistrationKey(String registrationKey) {
        this.registrationKey = registrationKey;
    }

}
