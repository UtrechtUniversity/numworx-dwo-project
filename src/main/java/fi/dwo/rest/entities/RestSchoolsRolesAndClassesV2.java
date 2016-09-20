/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.rest.entities;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomSchoolsRolesAndClassesV2;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Schools, roles and classes transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestSchoolsRolesAndClassesV2 {
    private DomContext restContext;
    private DomSchoolsRolesAndClassesV2 domSchoolsRolesAndClasses;

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
     * @return the domSchoolsRolesAndClasses
     */
    public DomSchoolsRolesAndClassesV2 getDomSchoolsRolesAndClasses() {
        return domSchoolsRolesAndClasses;
    }

    /**
     * @param domSchoolsRolesAndClasses the domSchoolsRolesAndClasses to set
     */
    public void setDomSchoolsRolesAndClasses(DomSchoolsRolesAndClassesV2 domSchoolsRolesAndClasses) {
        this.domSchoolsRolesAndClasses = domSchoolsRolesAndClasses;
    }
    
}
