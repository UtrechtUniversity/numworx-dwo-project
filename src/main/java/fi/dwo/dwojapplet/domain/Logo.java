package fi.dwo.dwojapplet.domain;

import java.awt.Image;
import java.awt.Toolkit;

public class Logo {

    private byte[] imageData;
    private int id;

    public Logo() {

    }

    public Logo(byte[] imageData) {
        setImageData(imageData);
    }

    /**
     * @return the id
     */
    public int getID() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * @return the data
     */
    public byte[] getImageData() {
        return imageData;
    }

    /**
     * @param data the data to set
     */
    public void setImageData(byte[] data) {
        this.imageData = data;
    }

    public Image getImage() {
        return Toolkit.getDefaultToolkit().createImage(imageData);
    }

}
