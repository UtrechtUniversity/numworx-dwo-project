/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Schools and classes transported over the REST interface.
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomNewSchoolClass4Student extends DomSchoolClass {

    private String registrationKey = null;

    
    public DomNewSchoolClass4Student(){
        
    }

    /** Initializes the object with a clone of the parameter and set the 
     * registrationKey to null;
     * 
     * @param sc
     */
    public DomNewSchoolClass4Student(DomSchoolClass sc){
        DomSchoolClass clone = (DomSchoolClass) sc.clone();
        this.setId(clone.getId());
        this.setHasRegKey(clone.getHasRegKey());
        this.setSchoolClassName(clone.getSchoolClassName());
        registrationKey = null;
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
