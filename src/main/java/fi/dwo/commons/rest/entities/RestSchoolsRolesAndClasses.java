/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.rest.entities;

import fi.dwo.commons.dom.entities.DomSchoolsRolesAndClasses;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Schools, roles and classes transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestSchoolsRolesAndClasses {
    private RestContext restContext;
    private DomSchoolsRolesAndClasses domSchoolsRolesAndClasses;

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
     * @return the domSchoolsRolesAndClasses
     */
    public DomSchoolsRolesAndClasses getDomSchoolsRolesAndClasses() {
        return domSchoolsRolesAndClasses;
    }

    /**
     * @param domSchoolsRolesAndClasses the domSchoolsRolesAndClasses to set
     */
    public void setDomSchoolsRolesAndClasses(DomSchoolsRolesAndClasses domSchoolsRolesAndClasses) {
        this.domSchoolsRolesAndClasses = domSchoolsRolesAndClasses;
    }
    
}
