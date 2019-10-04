package nl.uu.fi.dwo.rest.dom.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DomCourse. 
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomCourseStudent extends DomCourse{
    private String image;
    private byte[] imageData;
    private String description;
    private boolean notVisible;
    private List<DomACL> acls;
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
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

    /**
     * @return the imageData
     */
    public byte[] getImageData() {
        return imageData;
    }

    /**
     * @param imageData the imageData to set
     */
    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

	public boolean isNotVisible() {
		return notVisible;
	}

	public void setNotVisible(boolean notVisible) {
		this.notVisible = notVisible;
	}

  /**
   * @return the acls
   */
  public List<DomACL> getAcls() {
    return acls;
  }

  /**
   * @param acls the acls to set
   */
  public void setAcls(List<DomACL> acls) {
    this.acls = acls;
  } 
    
}
