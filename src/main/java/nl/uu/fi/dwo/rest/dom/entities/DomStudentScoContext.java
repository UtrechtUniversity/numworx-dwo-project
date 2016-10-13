package nl.uu.fi.dwo.rest.dom.entities;

import java.util.Date;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * 
 * @author Gert van der Plas
 */
public class DomStudentScoContext {
    private PersistenceId id;
    private PersistenceId scoID;
    private PersistenceId userID;
    private PersistenceId schoolGroupID;
    private float score;

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
    public float getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(float score) {
        this.score = score;
    }

}
