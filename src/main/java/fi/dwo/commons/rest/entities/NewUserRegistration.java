/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.rest.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Carries the information for a new user registration.
 * 
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
@XmlRootElement
class NewUserRegistration {
    //GuiCreator.instance().register( lastname.getText(), email.getText());
    private String username;
    private String password;
    private String GivenName;
    private String insertion;
    private String FamilyName;
    private String lastName;
    private String email;

    /**
     * @return the GivenName
     */
    public String getGivenName() {
        return GivenName;
    }

    /**
     * @param GivenName the GivenName to set
     */
    public void setGivenName(String GivenName) {
        this.GivenName = GivenName;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the insertion
     */
    public String getInsertion() {
        return insertion;
    }

    /**
     * @param insertion the insertion to set
     */
    public void setInsertion(String insertion) {
        this.insertion = insertion;
    }

    /**
     * @return the FamilyName
     */
    public String getFamilyName() {
        return FamilyName;
    }

    /**
     * @param FamilyName the FamilyName to set
     */
    public void setFamilyName(String FamilyName) {
        this.FamilyName = FamilyName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
}
