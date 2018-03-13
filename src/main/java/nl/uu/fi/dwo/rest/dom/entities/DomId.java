package nl.uu.fi.dwo.rest.dom.entities;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;
/**
 * 
 * @author Wim van Velthoven
 *
 */
@XmlRootElement
public abstract class DomId {
	private PersistenceId id;
        private Long optLock;

	public DomId(PersistenceId id) {
		super();
		this.id = id;
	}

	public DomId() {
		super();
	}

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
     * @return the optLock
     */
    public Long getVersion() {
        return optLock;
    }

    /**
     * @param version the optLock to set
     */
    public void setVersion(Long version) {
        this.optLock = version;
    }
	
}
