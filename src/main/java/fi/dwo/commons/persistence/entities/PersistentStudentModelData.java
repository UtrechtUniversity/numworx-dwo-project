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
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.json.simple.JSONObject;

/**
 * <p>
 * A set of Analytical models that describing a performance model for analyzing 
 * student performance. Each models contains categories and each category contains 
 * at least one (educational) objective. 
 * <p>
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
@Table(name = "tblStudentModelData", schema = "")
@Converter(name = "jsonObjectConverter",converterClass = JpaEclipseConverter4JsonObject.class)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentStudentModelData.findByModelDataId", query = "SELECT p FROM PersistentStudentModelData p WHERE p.modelDataId = :modelDataId")})
public class PersistentStudentModelData implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "modelDataID", nullable = false)
    private Long modelDataId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "scoID", nullable = false)
    private Long scoID;
    private PersistentHasRolePK persistentHasRolePK;
    
    @Convert("jsonObjectConverter")
    private JSONObject modelData;
    @Column(name = "optlock")
    @Version private int optlock;
    @Column(name = "lastChangeTimeStamp")
    private long lastChangeTimeStamp;

    /**
     * @return the modelDataId
     */
    public Long getModelDataId() {
        return modelDataId;
    }

    /**
     * @param modelDataId the modelDataId to set
     */
    public void setModelDataId(Long modelDataId) {
        this.modelDataId = modelDataId;
    }

    /**
     * @return the scoID
     */
    public Long getScoID() {
        return scoID;
    }

    /**
     * @param scoID the scoID to set
     */
    public void setScoID(Long scoID) {
        this.scoID = scoID;
    }

    /**
     * @return the persistentHasRolePK
     */
    public PersistentHasRolePK getPersistentHasRolePK() {
        return persistentHasRolePK;
    }

    /**
     * @param persistentHasRolePK the persistentHasRolePK to set
     */
    public void setPersistentHasRolePK(PersistentHasRolePK persistentHasRolePK) {
        this.persistentHasRolePK = persistentHasRolePK;
    }

    /**
     * @return the modelData
     */
    public JSONObject getModelData() {
        return modelData;
    }

    /**
     * @param modelData the modelData to set
     */
    public void setModelData(JSONObject modelData) {
        this.modelData = modelData;
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
}