package nl.uu.fi.dwo.rest.dom.entities;

/**
 * @author peterboon
 *
 */
public class DomScoContextFull extends DomScoContext {

	private byte[] imageData; // from urn/image
	private String description; // from scoData;

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
	
}
