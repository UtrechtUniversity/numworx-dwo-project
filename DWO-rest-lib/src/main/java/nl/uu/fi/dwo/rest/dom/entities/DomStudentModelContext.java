package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;

/**
 * A StudentModelContext node. 
 * 
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelContext extends DomStudentModelContextId {

    private DomStudentModelStructure modelStructure;
    private PublishState publishState;
    private Long lastChangeTimeStamp;

    /**
     * @return the context
     */
    public DomStudentModelStructure getModelStructure() {
        return modelStructure;
    }

    /**
     * @param context the context to set
     */
    public void setModelStructure(DomStudentModelStructure context) {
        this.modelStructure = context;
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
