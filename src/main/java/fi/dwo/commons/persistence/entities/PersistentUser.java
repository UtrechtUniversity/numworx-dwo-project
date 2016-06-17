/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.dwo.commons.persistence.entities;

import fi.dwo.rest.dom.entities.DomSchoolAdmin;
import fi.dwo.rest.dom.entities.DomStudent;
import fi.dwo.rest.dom.entities.DomTeacher;
import fi.dwo.rest.dom.entities.DomUser;
import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import fi.dwo.rest.persistence.PersistenceClassType;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheType;

/**
 * PersistentUser. 
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
    @NamedQuery(name = "PersistentUser.findByUsernameAndPassword", query = "SELECT p FROM PersistentUser p WHERE p.username = :username AND p.password = :password"),
    @NamedQuery(name = "PersistentUser.findByPasswd", query = "SELECT p FROM PersistentUser p WHERE p.password = :passwd"),
    @NamedQuery(name = "PersistentUser.findByEmail", query = "SELECT p FROM PersistentUser p WHERE p.email = :email"),
    @NamedQuery(name = "PersistentUser.findByRegisterDate", query = "SELECT p FROM PersistentUser p WHERE p.registerDate = :registerDate"),
    @NamedQuery(name = "PersistentUser.findByLastLogin", query = "SELECT p FROM PersistentUser p WHERE p.lastLogin = :lastLogin")})
/**
 * @Cacheable(true) instead do
 * <cache type="SOFT" size="64000" expiry="36000000" coordination-type="INVALIDATE_CHANGED_OBJECTS"/> - See more at: http://www.eclipse.org/eclipselink/documentation/2.5/jpa/extensions/a_cache.htm#sthash.jkf8vpLB.dpuf
 */
@Cache( type=CacheType.SOFT, // Cache everything until the JVM decides memory is low. 
        size=10000, // Use 64,000 as the initial cache size. 
        expiry=36000000 // 10 minutes 
)
public class PersistentUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "userID", nullable = false)
    private Long userID;
    @Column(name = "schoolGroupID")
    private Long schoolGroupID;
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
    private String password;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "schoolGroupID")
    private PersistentSchoolGroup schoolGroup;
    @Basic(optional = false)
    @NotNull
    @Column(name = "singleschool", nullable = false)
    private Boolean singleSchoolAccount;
    @Column(name = "lastLoginTime")
    private java.sql.Time lastLoginTime;

    public PersistentUser() {
    }

    public PersistentUser(Long userID) {
        this.userID = userID;
    }

    public PersistentUser(Long userID, String firstname, String lastname, String username, String passwd, String email, Date registerDate) {
        this.userID = userID;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = passwd;
        this.email = email;
        this.registerDate = registerDate;
    }

    public Long getId() {
        return userID;
    }

    public void setId(Long userID) {
        this.userID = userID;
    }

    public Long getSchoolGroupId() {
        return schoolGroupID;
    }

    public void setSchoolGroupId(Long schoolGroupID) {
        this.schoolGroupID = schoolGroupID;
    }

    public String getGivenName() {
        return firstname;
    }

    public void setGivenName(String givenName) {
        this.firstname = givenName;
    }

    public String getInsertion() {
        return middlename;
    }

    public void setInsertion(String insertion) {
        this.middlename = insertion;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String familyName) {
        this.lastname = familyName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    /**
     * Returns true if the username indicates a single user account. It contains
     * a '#'token.
     *
     * @return
     */
    public Boolean isSingleSchoolAccount() {
        return singleSchoolAccount;
    }

    /**
     * Returns true if the username indicates a single user account. It contains
     * a '#'token.
     *
     * @return
     */
    public void setSingleSchoolAccount(Boolean b) {
        singleSchoolAccount = b;
    }

    /**
     * @return the lastLoginTime
     */
    public java.sql.Time getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * @param lastLoginTime the lastLoginTime to set
     */
    public void setLastLoginTime(java.sql.Time lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
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
        return schoolGroup;
    }

    public boolean similar(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentUser)) {
            return false;
        }
        PersistentUser other = (PersistentUser) object;
        if ((this.username != null && this.username.equals(other.username))
                && (this.firstname != null && this.firstname.equals(other.firstname))
                && ((this.middlename == null && other.middlename == null) || (this.middlename != null && this.middlename.equals(other.middlename)))
                && (this.lastname != null && this.lastname.equals(other.lastname))
                && (this.email != null && this.email.equals(other.email))
                && (this.password != null && this.password.equals(other.password))
                && ((this.schoolGroupID == null && other.schoolGroupID == null) || (this.schoolGroupID != null && this.schoolGroupID.equals(other.schoolGroupID)))
                && (this.email != null && this.email.equals(other.email))
                && ((this.schoolGroupID == null && other.schoolGroupID == null) || (this.schoolGroupID != null && this.schoolGroupID.equals(other.schoolGroupID)))
                && (this.registerDate != null && (new SimpleDateFormat("MM-dd-yyyy").format(this.registerDate)).equals(new SimpleDateFormat("MM-dd-yyyy").format(other.registerDate)))
                && ((this.lastLogin == null && other.lastLogin == null) || (this.lastLogin != null && (new SimpleDateFormat("MM-dd-yyyy").format(this.registerDate)).equals(new SimpleDateFormat("MM-dd-yyyy").format(other.lastLogin))))) {
            return true;
        }
        return false;
    }

    public DomUser buildDomUser() {
        DomUser user = new DomUser();
        fillDomUser(user);
        return user;
    }

    public DomUserFull buildDomUserFull() {
        DomUserFull user = new DomUserFull();
        fillDomUserFull(user);
        return user;
    }

    public DomStudent buildDomStudent() {
        DomStudent user = new DomStudent();
        fillDomUser(user);
        return user;
    }

    public DomTeacher buildDomTeacher() {
        DomTeacher user = new DomTeacher();
        fillDomUser(user);
        return user;
    }

    public DomSchoolAdmin buildDomSchoolAdmin() {
        DomSchoolAdmin user = new DomSchoolAdmin();
        fillDomUser(user);
        return user;
    }

    public DomSingleSchoolStudent buildDomSingleSchoolStudent() {
        DomSingleSchoolStudent user = new DomSingleSchoolStudent();
        fillDomUser(user);
        user.setPassword(password);
        return user;
    }

    /**
     * Fills the user with data from the current object.
     *
     * @param user
     */
    private void fillDomUser(DomUser user) {
        if (getId() != null) {
            user.setId(MySQLPersistenceId.createPersistenceId(getId().intValue(), PersistenceClassType.PersistentUser));
        } else {
            user.setId(null);
        }
        user.setUserName(getUsername());
        user.setGivenName(getGivenName());
        user.setFamilyName(getLastname());
        user.setInsertion(getInsertion());
        user.setSingleSchool(isSingleSchoolAccount());
    }

    private void fillDomUserFull(DomUserFull user) {
        fillDomUser(user);
        user.setPassword(getPassword());
        user.setEmail(getEmail());

    }

}
