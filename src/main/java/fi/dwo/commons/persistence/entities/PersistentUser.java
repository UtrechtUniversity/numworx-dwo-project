/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.persistence.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * PersistentUser
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tbluser", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"username"})})
@NamedQueries({
    @NamedQuery(name = "PersistentUser.findAll", query = "SELECT p FROM PersistentUser p"),
    @NamedQuery(name = "PersistentUser.findByUserID", query = "SELECT p FROM PersistentUser p WHERE p.userID = :userID"),
    @NamedQuery(name = "PersistentUser.findBySchoolGroupID", query = "SELECT p FROM PersistentUser p WHERE p.schoolGroupID = :schoolGroupID"),
    @NamedQuery(name = "PersistentUser.findByFirstname", query = "SELECT p FROM PersistentUser p WHERE p.firstname = :firstname"),
    @NamedQuery(name = "PersistentUser.findByMiddlename", query = "SELECT p FROM PersistentUser p WHERE p.middlename = :middlename"),
    @NamedQuery(name = "PersistentUser.findByLastname", query = "SELECT p FROM PersistentUser p WHERE p.lastname = :lastname"),
    @NamedQuery(name = "PersistentUser.findByUsername", query = "SELECT p FROM PersistentUser p WHERE p.username = :username"),
    @NamedQuery(name = "PersistentUser.findByUsernameAndPassword", query = "SELECT p FROM PersistentUser p WHERE p.username = :username AND p.passwd = :password"),
    @NamedQuery(name = "PersistentUser.findByPasswd", query = "SELECT p FROM PersistentUser p WHERE p.passwd = :passwd"),
    @NamedQuery(name = "PersistentUser.findByEmail", query = "SELECT p FROM PersistentUser p WHERE p.email = :email"),
    @NamedQuery(name = "PersistentUser.findByRegisterDate", query = "SELECT p FROM PersistentUser p WHERE p.registerDate = :registerDate"),
    @NamedQuery(name = "PersistentUser.findByLastLogin", query = "SELECT p FROM PersistentUser p WHERE p.lastLogin = :lastLogin")})

public class PersistentUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "userID", nullable = false)
    private Integer userID;
    @Column(name = "schoolGroupID")
    private Integer schoolGroupID;
    @Basic(optional = false)
    @Column(name = "firstname", nullable = false, length = 50)
    private String firstname;
    @Column(name = "middlename", length = 15)
    private String middlename;
    @Basic(optional = false)
    @Column(name = "lastname", nullable = false, length = 100)
    private String lastname;
    @Basic(optional = false)
    @Column(name = "username", nullable = false, length = 128)
    private String username;
    @Basic(optional = false)
    @Column(name = "passwd", nullable = false, length = 128)
    private String passwd;
    @Basic(optional = false)
    @Column(name = "email", nullable = false, length = 128)
    private String email;
    @Basic(optional = false)
    @Column(name = "registerDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date registerDate;
    @Column(name = "lastLogin")
    @Temporal(TemporalType.DATE)
    private Date lastLogin;
//    @OneToMany(mappedBy = "schoolGroupID")
//    private List<PersistentSchoolGroup> schoolGroups;
    @ManyToOne(fetch=FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "schoolGroupID")
    private PersistentSchoolGroup schoolGroup;

    public PersistentUser() {
    }

    public PersistentUser(Integer userID) {
        this.userID = userID;
    }

    public PersistentUser(Integer userID, String firstname, String lastname, String username, String passwd, String email, Date registerDate) {
        this.userID = userID;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.passwd = passwd;
        this.email = email;
        this.registerDate = registerDate;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getSchoolGroupID() {
        return schoolGroupID;
    }

    public void setSchoolGroupID(Integer schoolGroupID) {
        this.schoolGroupID = schoolGroupID;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userID != null ? userID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentUser)) {
            return false;
        }
        PersistentUser other = (PersistentUser) object;
        if ((this.userID == null && other.userID != null) || (this.userID != null && !this.userID.equals(other.userID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentUser[ userID=" + userID + " ]";
    }

    public PersistentSchoolGroup getPersistentSchoolGroup() {
     return  schoolGroup;
    }
    

    public boolean similar(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentUser)) {
            return false;
        }
        PersistentUser other = (PersistentUser) object;
        if ((this.username != null && this.username.equals(other.username))
                && (this.firstname != null && this.firstname.equals(other.firstname))
                && ((this.middlename ==null && other.middlename == null) || (this.middlename != null && this.middlename.equals(other.middlename)))
                && (this.lastname != null && this.lastname.equals(other.lastname))
                && (this.email != null && this.email.equals(other.email))
                && (this.passwd != null && this.passwd.equals(other.passwd))
                && ((this.schoolGroupID ==null && other.schoolGroupID == null) || (this.schoolGroupID != null  && this.schoolGroupID.equals(other.schoolGroupID)))
                && (this.email != null && this.email.equals(other.email))
                && ((this.schoolGroupID ==null && other.schoolGroupID == null) ||(this.schoolGroupID != null && this.schoolGroupID.equals(other.schoolGroupID)))
                && (this.registerDate !=null && (new SimpleDateFormat("MM-dd-yyyy").format(this.registerDate)).equals(new SimpleDateFormat("MM-dd-yyyy").format(other.registerDate)))
                && ((this.lastLogin ==null && other.lastLogin == null) || (this.lastLogin != null && (new SimpleDateFormat("MM-dd-yyyy").format(this.registerDate)).equals(new SimpleDateFormat("MM-dd-yyyy").format(other.lastLogin))))
                ) {
            return true;
        }       
        return false;    
    }
}
