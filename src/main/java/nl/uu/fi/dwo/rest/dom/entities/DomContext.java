package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Transfers context information of a Rest call. Typical this user-state information
 * like hasRole. Defining the context in which a REST-function must be executed.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomContext {
    //TODO Insert hasRole property when legacy code is extinct and we can go stateless.
    private DomHasRole domHasRole; //security

    /**
     * @return the domHasRole
     */
    public DomHasRole getDomHasRole() {
        return domHasRole;
    }

    /**
     * @param domHasRole the domHasRole to set
     */
    public void setDomHasRole(DomHasRole domHasRole) {
        this.domHasRole = domHasRole;
    }
    
    public static DomContext buildDomContext(DomHasRole aHasRole){
        DomContext context = new DomContext();
        context.setDomHasRole(aHasRole);
        return context;
    }
}
