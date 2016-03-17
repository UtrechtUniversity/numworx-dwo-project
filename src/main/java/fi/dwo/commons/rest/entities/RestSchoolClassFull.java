/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomContext;
import fi.dwo.commons.dom.entities.DomSchoolClassFull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Schools and classes transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestSchoolClassFull {
    private DomContext restContext;
    private DomSchoolClassFull domSchoolClassFull;
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
     * @return the domSchoolClass4Teacher
     */
    public DomSchoolClassFull getDomSchoolClassFull() {
        return domSchoolClassFull;
    }

    /**
     * @param domSchoolClass4Teacher the domSchoolClass4Teacher to set
     */
    public void setDomSchoolClassFull(DomSchoolClassFull domSchoolClass4Teacher) {
        this.domSchoolClassFull = domSchoolClass4Teacher;
    }
    
    
    
    
}
