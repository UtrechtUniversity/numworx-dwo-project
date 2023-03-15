package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.dom.entities.util.PublishState;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * A StudentModelContext node. 
 * 
 * @author plas0006
 */
@XmlRootElement
public class DomStudentModelContextPatch extends DomStudentModelContextId {

    private String patch;
    private PublishState publishState;
    private Long lastChangeTimeStamp;
    private String digest;

    public DomStudentModelContextPatch(DomStudentModelContextId id) {
    	setId(id.getId());
    	setOptLock(id.getOptLock());
    }

    public DomStudentModelContextPatch(DomStudentModelContext context) {
    	setId(context.getId());
    	setOptLock(context.getOptLock());
    	setLastChangeTimeStamp(context.getLastChangeTimeStamp());
    	setPublishState(context.getPublishState());
    }
    
    
	public DomStudentModelContextPatch() {
		super();
	}

	public DomStudentModelContextPatch(PersistenceId persistenceId) {
		super(persistenceId);
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

	/**
	 * @return the patch
	 */
	public String getPatch() {
		return patch;
	}

	/**
	 * @param patch the patch to set
	 */
	public void setPatch(String patch) {
		this.patch = patch;
	}

	/**
	 * @return the digest
	 */
	public String getDigest() {
		return digest;
	}

	/**
	 * @param digest the digest to set
	 */
	public void setDigest(String digest) {
		this.digest = digest;
	}
    
}
