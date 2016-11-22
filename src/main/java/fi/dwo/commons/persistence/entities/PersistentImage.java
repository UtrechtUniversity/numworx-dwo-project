/* Copyrighted 2015.  */
package fi.dwo.commons.persistence.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 *
 * @author G.A.J. van der Plas
 */
@Entity
@Table(name = "tblimage", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentImage.findAll", query = "SELECT p FROM PersistentImage p"),
    @NamedQuery(name = "PersistentImage.findByCourseID", query = "SELECT p FROM PersistentImage p WHERE p.courseID = :courseID")})
public class PersistentImage implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "courseID", nullable = false)
    private Long courseID;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(name = "image", nullable = false)
    private byte[] image;

    public PersistentImage() {
    }

    public PersistentImage(Long courseID) {
        this.courseID = courseID;
    }

    public PersistentImage(Long courseID, byte[] image) {
        this.courseID = courseID;
        this.image = image;
    }

    public Long getCourseID() {
        return courseID;
    }

    public void setCourseID(Long courseID) {
        this.courseID = courseID;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (courseID != null ? courseID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PersistentImage)) {
            return false;
        }
        PersistentImage other = (PersistentImage) object;
        //test for null due to constructor.
        if ((this.courseID == null && other.courseID != null) || (this.courseID != null && !this.courseID.equals(other.courseID))) {
            return false;
        }        
        return true;
    }

    @Override
    public String toString() {
        return "fi.dwo.server.persistence.PersistentImage[ courseID=" + courseID + " ]";
    }
   /**
     * Builds a PersistenceId using this object's data.
     *
     * @return
     */
    public PersistenceId buildPersistenceId() {
        return buildPersistenceId(courseID);
    }

    /**
     * Builds a persistenceId from the parameters given.
     *
     * @param aCourseId
     * @return
     */
    public static PersistenceId buildPersistenceId(Long aCourseId) {
        PersistenceId id = new PersistenceId();
        id.setIdString(String.format("MYSQL;%s;%020d",
                PersistenceClassType.PersistentImage.name(), aCourseId));
        return id;
    }
}
