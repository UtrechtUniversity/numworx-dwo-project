/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.rest.entities;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomSchoolRoleAndClass;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Schools and classes transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestSchoolRoleAndClass {
    private DomContext restContext;
    private DomSchoolRoleAndClass domSchoolRoleAndClass;
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
