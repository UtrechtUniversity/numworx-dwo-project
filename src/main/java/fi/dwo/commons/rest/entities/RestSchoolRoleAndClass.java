/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.rest.entities;

import fi.dom.commons.dom.entities.DomSchoolRoleAndClass;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Schools and classes transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestSchoolRoleAndClass {
    private RestContext restContext;
    private DomSchoolRoleAndClass domSchoolRoleAndClass;
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
     * @return the domSchoolRoleAndClass
     */
    public DomSchoolRoleAndClass getDomSchoolRoleAndClass() {
        return domSchoolRoleAndClass;
    }

    /**
     * @param domSchoolRoleAndClass the domSchoolRoleAndClass to set
     */
    public void setDomSchoolRoleAndClass(DomSchoolRoleAndClass domSchoolRoleAndClass) {
        this.domSchoolRoleAndClass = domSchoolRoleAndClass;
    }


}
