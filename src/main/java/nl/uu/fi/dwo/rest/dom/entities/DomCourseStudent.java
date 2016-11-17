package nl.uu.fi.dwo.rest.dom.entities;

import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DomCourse. 
 * 
 * @author G.A.J. van der Plas
 */
@XmlRootElement
public class DomCourseStudent extends DomCourse{
    private static Logger LOG = Logger.getLogger(DomCourse.class.getName());
    private String image;
    private byte[] imageData;
    private String description;
    
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
}
