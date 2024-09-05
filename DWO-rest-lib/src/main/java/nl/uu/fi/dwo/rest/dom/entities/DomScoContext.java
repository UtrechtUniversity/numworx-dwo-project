package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.util.ScoType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * 
 * @author Gert van der Plas
 */
@XmlRootElement
public class DomScoContext extends DomScoContextId {
    private PersistenceId courseId;
    private PersistenceId appletId;
    private String scoName;
    private Boolean showScore;
    private Long sequencenr;
    private String image; // zie DomCourseStudent
    private ScoType scoType;
    
    private PersistenceId schoolId; // Zie domCourse
    private PersistenceId studentModelContext; // XXX overleg nodig ID of compleet?
    
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
     * @return the appletId
     */
    public PersistenceId getAppletId() {
        return appletId;
    }

    /**
     * @param appletId the appletId to set
     */
    public void setAppletId(PersistenceId appletId) {
        this.appletId = appletId;
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
		return scoType;
	}

	public void setScoType(ScoType scoType) {
		if (scoType == ScoType.normal) scoType = ScoType.OEFENEN; // Backwards compatibility
		this.scoType = scoType;
	}

	public PersistenceId getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(PersistenceId schoolId) {
		this.schoolId = schoolId;
	}

	/**
	 * @return the studentModelContext
	 */
	public PersistenceId getStudentModelContext() {
		return studentModelContext;
	}

	/**
	 * @param persistenceId the studentModelContext to set
	 */
	public void setStudentModelContext(PersistenceId persistenceId) {
		this.studentModelContext = persistenceId;
	}
}
