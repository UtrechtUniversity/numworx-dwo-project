/**
 * Copyrighted Jan 30, 2018
 */
package fi.dwo.commons.persistence.entities;

import fi.dwo.commons.persistence.JpaEclipseConverterDomStudentModelStructure;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;
import nl.uu.fi.dwo.rest.dom.entities.util.StudentModelClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;

/**
 * <p>
 * A set of Analytical models that describing a performance modelStructure for
 * analyzing student performance. Each models contains categories and each
 * category contains at least one (educational) objective.
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
@Table(name = "tblstudentmodelcontext", schema = "")
@Converter(name = "studentModelStructureConverter", converterClass = JpaEclipseConverterDomStudentModelStructure.class)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentStudentModelContext.findBySchoolID", query = "SELECT p FROM PersistentStudentModelContext p WHERE p.schoolID = :schoolID")})
public class PersistentStudentModelContext implements Serializable, PersistentEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "modelID", nullable = false)
    private Long modelID;
    @Column(name = "schoolID", nullable = false)
    private Long schoolID;
    @NotNull
    @Lob
    @Column(name = "model", nullable = false, length = 16777215, columnDefinition="JSON")
    @Convert("studentModelStructureConverter")
    private DomStudentModelStructure modelStructure;
    @Column(name = "optlock")
    @Version
    private Long optlock;
    @Column(name = "lastChangeTimeStamp")
    private long lastChangeTimeStamp;
    @NotNull
    @Column(name = "publishState", nullable = false)
    private PublishState publishState = PublishState.published;
    @Column(name = "dwoProfileID", nullable = true)
    private Long dwoProfileID;
    @ManyToMany
    @JoinTable(
    		name = "tblstudentmodelperprofile",
    		joinColumns = @JoinColumn( name = "modelID", referencedColumnName = "modelID" ),
    		inverseJoinColumns = @JoinColumn( name = "dwoProfileID", referencedColumnName = "dwoProfileID" )
    		)
    private Set<PersistentDwoProfile> profiles = new HashSet<>();
    
    
    public Set<PersistentDwoProfile> getProfiles() {
		return profiles;
	}

    
    
    
//Future design
//    @NotNull
//    @Column(name="classType", nullable = false)
//    private StudentModelClassType classType = StudentModelClassType.scoreAndCount;
    /** 
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
     * @return the modelStructure
     */
    public DomStudentModelStructure getModelStructure() {
        return modelStructure;
    }

    /**
     * @param modelStructure the modelStructure to set
     */
    public void setModelStructure(DomStudentModelStructure modelStructure) {
        this.modelStructure = modelStructure;
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

    /**
     * Builds a PersistenceId using this object's data.
     *
     * @return
     */
    public PersistenceId buildPersistenceId() {
        return buildPersistenceId(modelID);
    }

    /**
     * Builds a persistenceId from the parameters given.
     *
     * @param aModelId
     * @return
     */
    public static PersistenceId buildPersistenceId(Long aModelId) {
    	if(aModelId == null) return null;
        PersistenceId id = new PersistenceId();
        id.setIdString(String.format("MYSQL;%s;%020d",
                PersistenceClassType.PersistentStudentModelContext.name(), aModelId));
        return id;
    }
    
    public DomStudentModelContext buildDomStudentModelContext(){
        DomStudentModelContext ctx = new DomStudentModelContext();
        fillDomStudentModelContext(ctx);
        return ctx;
    }
    
    public void fillDomStudentModelContext(DomStudentModelContext context){
        context.setId(buildPersistenceId(modelID));
        context.setOptLock(optlock);
        context.setModelStructure(modelStructure);
        context.setLastChangeTimeStamp(Long.valueOf(lastChangeTimeStamp));
        context.setPublishState(publishState);
    }
//    Future design
//    /**
//     * @return the classType
//     */
//    public StudentModelClassType getClassType() {
//        return classType;
//    }
//
//    /**
//     * @param classType the classType to set
//     */
//    public void setClassType(StudentModelClassType classType) {
//        this.classType = classType;
//    }
    
    public void changeTimestamp() {
        lastChangeTimeStamp = System.currentTimeMillis();
    }

    public void setDwoProfileID(Long dwoProfileID) {
      this.dwoProfileID = dwoProfileID;
    }

    public Long getDwoProfileID() {
      return dwoProfileID;
    }

}
