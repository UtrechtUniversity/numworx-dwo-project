package nl.uu.fi.dwo.rest.dom.entities;

import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * DomCourse. 
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomCourse {
    private static Logger LOG = Logger.getLogger(DomCourse.class.getName());

    private PersistenceId id;
    private PersistenceId schoolId;
    private String name;
    private Boolean withChildren;
    private PersistenceId parentID;
    private Long sequenceNr;
    private String treeIndex;
    private Long  lastChangeTimeStamp;    

    /**
     * @return the id
     */
    public PersistenceId getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(PersistenceId id) {
        this.id = id;
    }

    /**
     * @return the schoolId
     */
    public PersistenceId getSchoolId() {
        return schoolId;
    }

    /**
     * @param schoolId the schoolId to set
     */
    public void setSchoolId(PersistenceId schoolId) {
        this.schoolId = schoolId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return the withChildren
     */
    public Boolean getWithChildren() {
        return withChildren;
    }

    /**
     * @param withChildren the withChildren to set
     */
    public void setWithChildren(Boolean withChildren) {
        this.withChildren = withChildren;
    }

    /**
     * @return the parentID
     */
    public PersistenceId getParentID() {
        return parentID;
    }

    /**
     * @param parentID the parentID to set
     */
    public void setParentID(PersistenceId parentID) {
        this.parentID = parentID;
    }

    /**
     * @return the sequenceNr
     */
    public Long getSequenceNr() {
        return sequenceNr;
    }

    /**
     * @param sequenceNr the sequenceNr to set
     */
    public void setSequenceNr(Long sequenceNr) {
        this.sequenceNr = sequenceNr;
    }

    /**
     * @return the treeIndex
     */
    public String getTreeIndex() {
        return treeIndex;
    }

    /**
     * @param treeIndex the treeIndex to set
     */
    public void setTreeIndex(String treeIndex) {
        this.treeIndex = treeIndex;
    }

    /**
     * @return the lastChangeTimeStamp
     */
    public Long getLastChangeTimeStamp() {
        return lastChangeTimeStamp;
    }

    /**
     * @param lastChangeTimeStamp the lastChangeTimeStamp to set
     */
    public void setLastChangeTimeStamp(Long lastChangeTimeStamp) {
        this.lastChangeTimeStamp = lastChangeTimeStamp;
    }
}
