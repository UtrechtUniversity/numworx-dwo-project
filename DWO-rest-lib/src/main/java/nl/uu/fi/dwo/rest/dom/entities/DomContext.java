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
    private DomHasRole domHasRole; //security
    private String realm; // user namespace
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

    /**
     * @return the realm
     */
    public String getRealm() {
      return realm;
    }

    /**
     * @param realm the realm to set
     */
    public void setRealm(String realm) {
      this.realm = realm;
    }    
}
