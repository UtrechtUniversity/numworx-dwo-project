/**
 * Copyrighted Nov 20, 2015
 */
package fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * School transported over the REST interface.
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomSamlUser {
    private String samlUserId;
    private String samlOrgId;
    private String authToken;    

    /**
     * @return the samlUserId
     */
    public String getSamlUserId() {
        return samlUserId;
    }

    /**
     * @param samlUserId the samlUserId to set
     */
    public void setSamlUserId(String samlUserId) {
        this.samlUserId = samlUserId;
    }

    /**
     * @return the samlOrgId
     */
    public String getSamlOrgId() {
        return samlOrgId;
    }

    /**
     * @param samlOrgId the samlOrgId to set
     */
    public void setSamlOrgId(String samlOrgId) {
        this.samlOrgId = samlOrgId;
    }

    /**
     * @return the authToken
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * @param authToken the authToken to set
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
