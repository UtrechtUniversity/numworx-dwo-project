/**
 * Copyrighted Jan 30, 2018
 */
package fi.dwo.commons.persistence.entities;

import fi.dwo.commons.persistence.JpaEclipseConverter4JsonObject;
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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.json.simple.JSONObject;

/**
 * <p>
 A set of Analytical models that describing a performance modelStructure for analyzing 
 student performance. Each models contains categories and each category contains 
 at least one (educational) objective. 
 <p>
 * @author Gert van der Plas
 */

//CREATE TABLE `dwo_devel`.`tblanalyticalmodel` (
//  `schoolID` INT(11) NOT NULL,
//  `modelID` INT(11) NOT NULL,
//  `title` JSON NOT NULL,
//  `description` JSON NOT NULL,
//  PRIMARY KEY (`modelID`),
//  UNIQUE INDEX `modelID_UNIQUE` (`modelID` ASC),
//  UNIQUE INDEX `schoolID_UNIQUE` (`schoolID` ASC));

@Entity
@Table(name = "tblStudentModelContext", schema = "")
@Converter(name = "jsonObjectConverter",converterClass = JpaEclipseConverter4JsonObject.class)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentStudentModelContext.findBySchoolID", query = "SELECT p FROM PersistentStudentModelContext p WHERE p.schoolID = :schoolID")})
public class PersistentStudentModelContext implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "modelID", nullable = false)
    private Long modelID;
    @Column(name = "schoolID", nullable = false)
    private Long schoolID;
    @NotNull
    @Column(name = "model", nullable = false)
    @Convert("jsonObjectConverter")
    private JSONObject title;
    @Basic(optional = false)
//    @NotNull
    @Column(name = "description", nullable = true)
    @Convert("jsonObjectConverter")
    private JSONObject description;
    @NotNull
    @Column(name = "title", nullable = false)
    @Convert("jsonObjectConverter")
    private JSONObject modelStructure;
    @Column(name = "optlock")
    @Version private int optlock;
    @Column(name = "lastChangeTimeStamp")
    private long lastChangeTimeStamp;
    @NotNull
    @Column(name="publishState", nullable = false)
    private PublishState publishState = PublishState.published;    /**
     * @return the modelID
     */
    public Long getModelID() {
        return modelID;
    }

    /**
     * @param modelID the modelID to set
     */
    public void setModelID(Long modelID) {
        this.modelID = modelID;
    }

    /**
     * @return the schoolID
     */
    public Long getSchoolID() {
        return schoolID;
    }

    /**
     * @param schoolID the schoolID to set
     */
    public void setSchoolID(Long schoolID) {
        this.schoolID = schoolID;
    }


    /**
     * @param description the description to set
     */
    public void setDescription(JSONObject description) {
        this.description = description;
    }

    /**
     * @return the modelStructure
     */
    public JSONObject getModelStructure() {
        return modelStructure;
    }

    /**
     * @param modelStructure the modelStructure to set
     */
    public void setModelStructure(JSONObject modelStructure) {
        this.modelStructure = modelStructure;
    }

    /**
     * @return the optlock
     */
    public int getOptlock() {
        return optlock;
    }

    /**
     * @param optlock the optlock to set
     */
    public void setOptlock(int optlock) {
        this.optlock = optlock;
    }

    /**
     * @return the lastChangeTimeStamp
     */
    public long getLastChangeTimeStamp() {
        return lastChangeTimeStamp;
    }

    /**
     * @param lastChangeTimeStamp the lastChangeTimeStamp to set
     */
    public void setLastChangeTimeStamp(long lastChangeTimeStamp) {
        this.lastChangeTimeStamp = lastChangeTimeStamp;
    }

    /**
     * @return the publishState
     */
    public PublishState getPublishState() {
        return publishState;
    }

    /**
     * @param publishState the publishState to set
     */
    public void setPublishState(PublishState publishState) {
        this.publishState = publishState;
    }
}
