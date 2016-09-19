/* Copyrighted 2015.  */
package fi.dwo.commons.persistence.entities;

import fi.dwo.rest.dom.entities.DomSchool;
import fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import fi.dwo.rest.dom.entities.DomSchoolFull;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.util.DwoDateUtilities;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheType;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblschool", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"schoollogin"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentSchool.findAll", query = "SELECT p FROM PersistentSchool p"),
    @NamedQuery(name = "PersistentSchool.findBySchoolID", query = "SELECT p FROM PersistentSchool p WHERE p.schoolID = :schoolID"),
    @NamedQuery(name = "PersistentSchool.findBySchoolName", query = "SELECT p FROM PersistentSchool p WHERE p.schoolName = :schoolName"),
    @NamedQuery(name = "PersistentSchool.findBySchoolLogin", query = "SELECT p FROM PersistentSchool p WHERE p.schoolLogin = :schoolLogin"),
    @NamedQuery(name = "PersistentSchool.findByExport", query = "SELECT p FROM PersistentSchool p WHERE p.export = :export"),
    @NamedQuery(name = "PersistentSchool.findBySchoolRights", query = "SELECT p FROM PersistentSchool p WHERE p.schoolRights = :schoolRights"),
    @NamedQuery(name = "PersistentSchool.findByImage", query = "SELECT p FROM PersistentSchool p WHERE p.image = :image"),
    @NamedQuery(name = "PersistentSchool.findByExpire", query = "SELECT p FROM PersistentSchool p WHERE p.expire = :expire")})
@Cache( type=CacheType.SOFT, // Cache everything until the JVM decides memory is low. 
        size=10000, // Use 64,000 as the initial cache size. 
        expiry=36000000 // 10 minutes 
)
public class PersistentSchool implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "schoolID", nullable = false)
    private Long schoolID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "schoolName", nullable = false, length = 128)
    private String schoolName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "schoollogin", nullable = false, length = 128)
    private String schoolLogin;
    @Column(name = "export")
    private Boolean export;
    @Size(max = 100)
    @Column(name = "schoolRights", length = 100)
    private String schoolRights;
    @Size(max = 128)
    @Column(name = "image", length = 128)
    private String image;
    @Column(name = "expire")
    @Temporal(TemporalType.DATE)
    private Date expire;

    public PersistentSchool() {
    }

    public PersistentSchool(Long schoolID) {
        this.schoolID = schoolID;
    }

    public PersistentSchool(Long schoolID, String schoolName, String schoollogin) {
        this.schoolID = schoolID;
        this.schoolName = schoolName;
        this.schoolLogin = schoollogin;
    }

    public Long getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(Long schoolID) {
        this.schoolID = schoolID;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolLogin() {
        return schoolLogin;
    }

    public void setSchoolLogin(String schoolLogin) {
        this.schoolLogin = schoolLogin;
    }

    public Boolean getExport() {
        return export;
    }

    public void setExport(Boolean export) {
        this.export = export;
    }

    public String getSchoolRights() {
        return schoolRights;
    }

    public void setSchoolRights(String schoolRights) {
        this.schoolRights = schoolRights;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (schoolID != null ? schoolID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentSchool)) {
            return false;
        }
        PersistentSchool other = (PersistentSchool) object;
        if ((this.schoolID == null && other.schoolID != null) || (this.schoolID != null && !this.schoolID.equals(other.schoolID))) {
            return false;
        }
        return true;
    }

    /**
     * License expires if the expiration day is past. The java.util.Date in the
     * persistent store is to be in UTC.
     *
     * @return
     */
    public boolean licenseIsValid() {

        if (getExpire() == null) {
            return true;
        } else {
            Calendar c = DwoDateUtilities.getCurrentDwoDateAsCalendarDate();
            if (c.after(getExpire())) //compare on UTC calendar.
            {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentSchool[ schoolID=" + schoolID + " ]";
    }

    public boolean similar(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PersistentSchool)) {
            return false;
        }
        PersistentSchool other = (PersistentSchool) object;
        if ((this.schoolLogin != null && this.schoolLogin.equals(other.schoolLogin))
                && (this.schoolName != null && this.schoolName.equals(other.schoolName))
                && (this.schoolRights != null && this.schoolRights.equals(other.schoolRights))
                && ((this.expire == null && other.expire == null) || (this.expire != null && (new SimpleDateFormat("MM-dd-yyyy").format(this.expire)).equals(new SimpleDateFormat("MM-dd-yyyy").format(other.expire))))
                && ((this.image == null && other.image == null) || (this.image != null && this.image.equals(other.image)))) {
            return true;
        }

        return false;
    }

    public DomSchool createDomSchool() {
        DomSchool school = new DomSchool();
        buildDomSchool(school);
        return school;
    }

    private void buildDomSchool(DomSchool school) {
        if (this.schoolID != null) {
            school.setId(MySQLPersistenceId.createPersistentId(this));
        }
        school.setSchoolName(this.schoolName);
        //TODO One should filter the rights depending on the security level
        school.setSchoolRights(schoolRights);
    }

    public DomSchool4DwoAdmin createDomSchool4DwoAdmin() {
        DomSchool4DwoAdmin school = new DomSchool4DwoAdmin();
        buildDomSchool4DwoAdmin(school);
        return school;
    }

    private void buildDomSchool4DwoAdmin(DomSchool4DwoAdmin school) {
        buildDomSchool(school);
        school.setSchoolLogin(this.schoolLogin);
    }

    public DomSchoolFull createDomSchoolFull() {
        DomSchoolFull school = new DomSchoolFull();
        buildDomSchoolFull(school);
        return school;
    }

    private void buildDomSchoolFull(DomSchoolFull school) {
        buildDomSchool(school);
        school.setSchoolLogin(schoolLogin);
        school.setExport(export);
        school.setImage(image);
        //seeing schoolRights moved to DomSchool
        //school.setSchoolRights(schoolRights);
        school.setExpire(expire);
    }
}
