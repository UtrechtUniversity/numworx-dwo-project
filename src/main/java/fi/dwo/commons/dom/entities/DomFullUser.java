/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.dom.entities;

import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.PersistenceId;
import fi.dwo.commons.persistence.entities.PersistentUser;
import java.io.Serializable;

/**
 * DomUser
 *
 * @author G.A.J. van der Plas
 */
public class DomFullUser implements Serializable {
    private PersistenceId id;
    private String username;
    private String password;
    private String GivenName;
    private String insertion;
    private String FamilyName;
    private String email;
    

    public DomFullUser() {
    }

    public DomFullUser(PersistentUser user){
        this.id = MySQLPersistenceId.createPersistentId(user);
        this.username = user.getUsername();
        this.password = user.getPasswd();
        this.GivenName = user.getFirstname();
        this.FamilyName = user.getLastname();
        this.insertion = user.getMiddlename();
        this.email = user.getEmail();
    }

    /**
     * @return the id
     */
    public PersistenceId getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(PersistenceId id) {
        this.id = id;
    }
    
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
