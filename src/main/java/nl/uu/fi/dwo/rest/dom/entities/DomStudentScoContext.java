package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class DomStudentScoContext {
    private PersistenceId id;
    private PersistenceId scoID;
    private PersistenceId userID;
    private PersistenceId schoolGroupID;
    private double score;

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
     * @return the scoID
     */
    public PersistenceId getScoID() {
        return scoID;
    }

    /**
     * @param scoID the scoID to set
     */
    public void setScoID(PersistenceId scoID) {
        this.scoID = scoID;
    }

    /**
     * @return the userID
     */
    public PersistenceId getUserID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(PersistenceId userID) {
        this.userID = userID;
    }

    /**
     * @return the schoolGroupID
     */
    public PersistenceId getSchoolGroupID() {
        return schoolGroupID;
    }

    /**
     * @param schoolGroupID the schoolGroupID to set
     */
    public void setSchoolGroupID(PersistenceId schoolGroupID) {
        this.schoolGroupID = schoolGroupID;
    }

    /**
     * @return the score
     */
    public double getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(double score) {
        this.score = score;
    }

}
