package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.dom.entities.util.ScoType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * 
 * @author Gert van der Plas
 */
public class DomScoContext extends DomScoContextId {
    private PersistenceId courseId;
    private PersistenceId AppletId;
    private String scoName;
    private Boolean showScore;
    private Long sequencenr;
    private String image; // zie DomCourseStudent
    private ScoType scoType;

    /**
     * @return the courseId
     */
    public PersistenceId getCourseId() {
        return courseId;
    }

    /**
     * @param courseId the courseId to set
     */
    public void setCourseId(PersistenceId courseId) {
        this.courseId = courseId;
    }

    /**
     * @return the AppletId
     */
    public PersistenceId getAppletId() {
        return AppletId;
    }

    /**
     * @param AppletId the AppletId to set
     */
    public void setAppletId(PersistenceId AppletId) {
        this.AppletId = AppletId;
    }

    /**
     * @return the scoName
     */
    public String getScoName() {
        return scoName;
    }

    /**
     * @param scoName the scoName to set
     */
    public void setScoName(String scoName) {
        this.scoName = scoName;
    }

    /**
     * @return the showScore
     */
    public Boolean getShowScore() {
        return showScore;
    }

    /**
     * @param showScore the showScore to set
     */
    public void setShowScore(Boolean showScore) {
        this.showScore = showScore;
    }

    /**
     * @return the sequencenr
     */
    public Long getSequencenr() {
        return sequencenr;
    }

    /**
     * @param sequencenr the sequencenr to set
     */
    public void setSequencenr(Long sequencenr) {
        this.sequencenr = sequencenr;
    }

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	public ScoType getScoType() {
		if(scoType == null)
			return ScoType.normal;
		return scoType;
	}

	public void setScoType(ScoType scoType) {
		this.scoType = scoType;
	}
}
