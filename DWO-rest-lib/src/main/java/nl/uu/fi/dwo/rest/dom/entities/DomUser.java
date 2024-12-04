/**
 * Copyrighted Sep 24, 2015
 */
package nl.uu.fi.dwo.rest.dom.entities;

import java.beans.Transient;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A class for transferring need-to-know User data over the REST-interface.
 *
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomUser extends DomId {

    private String userName;
    private String givenName;
    private String familyName;
    private String insertion;
    private Boolean singleSchool;

    /**
     * Constructor
     */
    public DomUser() {

    }

    public DomUser(DomUser user) {
        setId(user.getId());
        setUserName(user.getUserName());
        setGivenName(user.getGivenName());
        setFamilyName(user.getFamilyName());
        setInsertion(user.getInsertion());
        setSingleSchool(user.getSingleSchool());
    }

    public void clearSettings() {
        setId(null);
        userName = "";
        givenName = "";
        familyName = "";
        insertion = "";
        singleSchool = Boolean.TRUE;
    }

    /**
     * @return the givenName
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * @param givenName the givenName to set
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    /**
     * @return the familyName
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * @param familyName the familyName to set
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    /**
     * @return the insertion
     */
    public String getInsertion() {
        return insertion;
    }

    /**
     * @param familyNamePrefix the insertion to set
     */
    public void setInsertion(String familyNamePrefix) {
        this.insertion = familyNamePrefix;
    }

    /**
     * @return the usercode
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param usercode the usercode to set
     */
    public void setUserName(String usercode) {
        this.userName = usercode;
    }

    /**
     * @return the singleSchool
     */
    public Boolean getSingleSchool() {
        return this.singleSchool;
    }

    /**
     * @param singleSchool the singleSchool to set
     */
    public void setSingleSchool(Boolean singleSchool) {
        this.singleSchool = singleSchool;
    }

    /**
     * Returns a unique display name, the display name prefixed with the username
     * separated by a dash.
     * 
     * @return the user's unique display name ' &lt;username &gt; - &lt; display 
     * name &gt;'.
     */
    @Transient
    @XmlTransient
    public String getUniqueDisplayName() {
        StringBuilder result = new StringBuilder();
        result.append(this.userName)
        	.append(" - ")
        	.append(this.givenName)
        	.append(" ")
        	.append((this.insertion == null) ? "" : this.insertion)
        	.append(" ")
        	.append(this.familyName);

        return result.toString();
    }

    /**
     Returns the display name. The display name is the given name followed by the
     * insertion followed by the family name. 
     * 
     * @return the user's display name.
     * @return 
     */
    @Transient
    @XmlTransient
    public String getDisplayName() {
        StringBuilder result = new StringBuilder();
        result.append(this.givenName)
        	.append(" ")
        	.append((this.insertion == null) ? "" : this.insertion)
        	.append(" ")
        	.append(this.familyName);

        return result.toString();
    }

    public DomUser duplicate() {
        DomUser user = new DomUser();
        fill(user);
        return user;
    }

    protected void fill(DomUser user) {
        user.setId(this.getId() == null ? null : this.getId().duplicate());
        user.setOptLock(getOptLock());
        user.userName = this.userName;
        user.singleSchool = this.singleSchool;
        user.givenName = this.givenName;
        user.insertion = this.insertion;
        user.familyName = this.familyName;
    }
}
