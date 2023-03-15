package nl.uu.fi.dwo.rest.dom.entities;

import nl.uu.fi.dwo.rest.persistence.PersistenceId;

/**
 * @author peterboon
 *
 */
public class DomScoContextFull extends DomScoContext {

	private byte[] imageData; 		// from urn/image
	private String description; 	// from scoData;
	private PersistenceId urnID; 	// reference to an image
	
	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PersistenceId getUrnId() {
		return urnID;
	}

	public void setUrnId(PersistenceId urnID) {
		this.urnID = urnID;
	}
	
}
