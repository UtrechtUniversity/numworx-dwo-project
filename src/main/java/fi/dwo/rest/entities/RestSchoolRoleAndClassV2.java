/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.rest.entities;

import fi.dwo.rest.dom.entities.DomContext;
import fi.dwo.rest.dom.entities.DomSchoolRoleAndClassV2;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Schools and classes transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class RestSchoolRoleAndClassV2 {
    private DomContext restContext;
    private DomSchoolRoleAndClassV2 domSchoolRoleAndClass;
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
    public DomSchoolRoleAndClassV2 getDomSchoolRoleAndClass() {
        return domSchoolRoleAndClass;
    }

    /**
     * @param domSchoolRoleAndClass the domSchoolRoleAndClass to set
     */
    public void setDomSchoolRoleAndClass(DomSchoolRoleAndClassV2 domSchoolRoleAndClass) {
        this.domSchoolRoleAndClass = domSchoolRoleAndClass;
    }


}
