package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * DomCourse. 
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomCourseFull extends DomCourseStudent{
    private PersistenceId dwoProfileId;
    private Boolean export;

    /**
     * @return the dwoProfileId
     */
    public PersistenceId getDwoProfileId() {
        return dwoProfileId;
    }

    /**
     * @param dwoProfileId the dwoProfileId to set
     */
    public void setDwoProfileId(PersistenceId dwoProfileId) {
        this.dwoProfileId = dwoProfileId;
    }
    
    /**
     * @return the export
     */
    public Boolean getExport() {
        return export;
    }

    /**
     * @param export the export to set
     */
    public void setExport(Boolean export) {
        this.export = export;
    }

}
