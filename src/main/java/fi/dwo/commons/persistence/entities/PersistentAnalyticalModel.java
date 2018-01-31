/**
 * Copyrighted Jan 30, 2018
 */
package fi.dwo.commons.persistence.entities;

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
@Table(name = "tblAnalyticalModel", schema = "")
@Converter(name = "jsonObjectConverter",converterClass = fi.dwo.commons.persistence.JpaEclipseConverter4JsonObject.class)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersistentAnalyticalModel.findBySchoolID", query = "SELECT p FROM PersistentAnalyticalModel p WHERE p.schoolID = :schoolID")})
public class PersistentAnalyticalModel implements Serializable {
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
    private JSONObject model;

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
     * @return the title
     */
    public JSONObject getModel() {
        return model;
    }

    /**
     * @param aModel the title to set
     */
    public void setModel(JSONObject aModel) {
        this.model = aModel;
    }

}