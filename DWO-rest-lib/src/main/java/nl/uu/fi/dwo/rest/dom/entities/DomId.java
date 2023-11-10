package nl.uu.fi.dwo.rest.dom.entities;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;
/**
 * 
 * @author Wim van Velthoven
 *
 */
@XmlRootElement
public abstract class DomId implements Serializable {
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
    public Long getOptLock() {
        return optLock;
    }

    /**
     * @param version the optLock to set
     */
    public void setOptLock(Long version) {
        this.optLock = version;
    }
	
}
