/**
 * Copyrighted Jan 30, 2018
 */
package fi.dwo.commons.persistence.entities;

import fi.dwo.commons.persistence.JpaEclipseConverterDomStudentModelStructureScore;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelData;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;

/**
 * <p>
 * A set of Analytical models that describing a performance model for analyzing
 * student performance. Each models contains categories and each category
 * contains at least one (educational) objective.
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
@Table(name = "tblstudentmodeldata", schema = "")
@Converter(name = "studentModelScoreConverter", converterClass = JpaEclipseConverterDomStudentModelStructureScore.class)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentStudentModelData.findByModelDataId", query = "SELECT p FROM PersistentStudentModelData p WHERE p.modelDataId = :modelDataId"),
    @NamedQuery(name = "PersistentStudentModelData.findByUniqueKeys", query = "SELECT p FROM PersistentStudentModelData p WHERE p.scoID = :scoID and p.modelID = :modelID and p.persistentHasRolePK = :persistentHasRolePK"),
    @NamedQuery(name = "PersistentStudentModelData.findByScoId", query = "SELECT p FROM PersistentStudentModelData p WHERE p.scoID = :scoID"),
    @NamedQuery(name = "PersistentStudentModelData.findByHasRolePK", query = "SELECT p FROM PersistentStudentModelData p WHERE p.persistentHasRolePK = :persistentHasRolePK"),
    @NamedQuery(name = "PersistentStudentModelData.findStudentScoresOfModel", query = "SELECT p FROM PersistentStudentModelData p WHERE p.modelID = :modelID and p.persistentHasRolePK = :persistentHasRolePK")})
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
    @Basic(optional = false)
    @NotNull
    @Column(name = "modelID", nullable = false)
    private Long modelID;
//    @Basic(optional = false)
//    @NotNull
//    @Column(name = "schoolID", nullable = false)
//    private Long schoolID;
//    @Basic(optional = false)
    @NotNull
    private PersistentHasRolePK persistentHasRolePK;
    @Convert("studentModelScoreConverter")
    @Lob
    @Column(name = "modelData", length = 16777215, columnDefinition="JSON")
    private DomStudentModelStructureScore modelData;
    @Column(name = "optlock")
    @Version
    private Long optlock;
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
    public DomStudentModelStructureScore getModelData() {
        return modelData;
    }

    /**
     * @param modelData the modelData to set
     */
    public void setModelData(DomStudentModelStructureScore modelData) {
        this.modelData = modelData;
    }

    /**
     * @return the optlock
     */
    public Long getOptlock() {
        return optlock;
    }

    /**
     * @param optlock the optlock to set
     */
    public void setOptlock(Long optlock) {
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
     * Builds a PersistenceId using this object's data.
     *
     * @return
     */
    public PersistenceId buildPersistenceId() {
        return buildPersistenceId(modelDataId);
    }

    /**
     * Builds a persistenceId from the parameters given.
     *
     * @param aModelData
     * @return
     */
    public static PersistenceId buildPersistenceId(Long aModelData) {
    	if(aModelData == null) return null;
        PersistenceId id = new PersistenceId();
        id.setIdString(String.format("MYSQL;%s;%020d",
                PersistenceClassType.PersistentStudentModelData.name(), aModelData));
        return id;
    }
    
    public DomStudentModelData buildDomStudentModelData(){
        DomStudentModelData data = new DomStudentModelData();
        fillDomStudentModelData(data);
        return data;
    }
    
    public void fillDomStudentModelData(DomStudentModelData data){
        data.setId(buildPersistenceId());
        DomScoContextId dScoId = new DomScoContextId();
        dScoId.setId(PersistentScoContext.buildPersistenceId(scoID));
        data.setOptLock(optlock);
        data.setScoContextId(dScoId);
        data.setDomStudentModelStructureScore(modelData);
    }
//
//    /**
//     * @return the schoolID
//     */
//    public Long getSchoolID() {
//        return schoolID;
//    }
//
//    /**
//     * @param schoolID the schoolID to set
//     */
//    public void setSchoolID(Long schoolID) {
//        this.schoolID = schoolID;
//    }

    /**
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

}
