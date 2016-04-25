/* Copyrighted 2015.  */
package fi.dwo.commons.persistence.entities;

import fi.dwo.rest.dom.entities.DomNewSchoolClass4Student;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomSchoolClassFull;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblclass", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"class", "schoolID"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentSchoolClass.findAll", query = "SELECT p FROM PersistentSchoolClass p"),
    @NamedQuery(name = "PersistentSchoolClass.findByClassID", query = "SELECT p FROM PersistentSchoolClass p WHERE p.classID = :classID"),
    @NamedQuery(name = "PersistentSchoolClass.findBySchoolIDAndClassName", query = "SELECT p FROM PersistentSchoolClass p WHERE p.schoolID = :schoolID and p.class1 = :className"),
//    @NamedQuery(name = "PersistentSchoolClass.findByUserID", query = "SELECT p FROM PersistentSchoolClass p WHERE p.userID = :userID"),
    @NamedQuery(name = "PersistentSchoolClass.findBySchoolID", query = "SELECT p FROM PersistentSchoolClass p WHERE p.schoolID = :schoolID"),
    @NamedQuery(name = "PersistentSchoolClass.findByIconizer", query = "SELECT p FROM PersistentSchoolClass p WHERE p.iconizer = :iconizer"),
    @NamedQuery(name = "PersistentSchoolClass.findByClass1", query = "SELECT p FROM PersistentSchoolClass p WHERE p.class1 = :class1"),
    @NamedQuery(name = "PersistentSchoolClass.findByRegistrationKey", query = "SELECT p FROM PersistentSchoolClass p WHERE p.registrationKey = :registrationKey")})
public class PersistentSchoolClass implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "classID", nullable = false)
    private Long classID;
//    @Basic(optional = false)
//    @NotNull
//    @Column(name = "userID", nullable = false)
//    private Long userID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "schoolID", nullable = false)
    private Long schoolID;
    @Column(name = "iconizer")
    private Boolean iconizer;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "class", nullable = false, length = 100)
    private String class1;
    @Size(max = 100)
    @Column(name = "registrationKey", length = 100)
    private String registrationKey;

    public PersistentSchoolClass() {
    }

    public PersistentSchoolClass(Long classID) {
        this.classID = classID;
    }

    public PersistentSchoolClass(Long classID, Long schoolID, String class1) {
        this.classID = classID;
//        this.userID = userID;
        this.schoolID = schoolID;
        this.class1 = class1;
    }

    public Long getClassID() {
        return classID;
    }

    public void setClassID(Long classID) {
        this.classID = classID;
    }
//
//    public Long getUserID() {
//        return userID;
//    }
//
//    public void setUserID(Long userID) {
//        this.userID = userID;
//    }

    public Long getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(Long schoolID) {
        this.schoolID = schoolID;
    }

    public Boolean getIconizer() {
        return iconizer;
    }

    public void setIconizer(Boolean iconizer) {
        this.iconizer = iconizer;
    }

    public String getClass1() {
        return class1;
    }

    public void setClass1(String class1) {
        this.class1 = class1;
    }

    public String getRegistrationKey() {
        return registrationKey;
    }

    public void setRegistrationKey(String registrationKey) {
        this.registrationKey = registrationKey;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (classID != null ? classID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentSchoolClass)) {
            return false;
        }
        PersistentSchoolClass other = (PersistentSchoolClass) object;
        if ((this.classID == null && other.classID != null) || (this.classID != null && !this.classID.equals(other.classID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentSchoolClass[ classID=" + classID + " ]";
    }

    public DomSchoolClass createDomSchoolClass() {
        DomSchoolClass schoolClass = new DomSchoolClass();
        buildDomSchoolClass(schoolClass);
        return schoolClass;
    }

    private void buildDomSchoolClass(DomSchoolClass schoolClass) {
        schoolClass.setSchoolClassName(class1);
        if (this.classID != null) {
            schoolClass.setId(MySQLPersistenceId.createPersistentId(this));
        }
        schoolClass.setHasRegKey(this.registrationKey!=null);
    }

    public DomSchoolClassFull createDomSchoolClassFull() {
        DomSchoolClassFull schoolClass = new DomSchoolClassFull();
        buildDomSchoolClassFull(schoolClass);
        return schoolClass;
    }

    private void buildDomSchoolClassFull(DomSchoolClassFull schoolClass) {
        buildDomSchoolClass(schoolClass);
        schoolClass.setIconizer(iconizer);
        schoolClass.setRegistrationKey(registrationKey);
    }

    public DomNewSchoolClass4Student createDomNewSchoolClass4Student() {
        DomNewSchoolClass4Student schoolClass = new DomNewSchoolClass4Student();
        buildDomNewSchoolClass4Student(schoolClass);
        return schoolClass;
    }

    private void buildDomNewSchoolClass4Student(DomNewSchoolClass4Student schoolClass) {
        buildDomSchoolClass(schoolClass);
//        schoolClass.setRegistrationKey(registrationKey); // clearly this info should neve be passed to a student
    }

}
