/*
 * Created on Mar 2, 2005
 *
 */
package fi.dwo.client.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

/**
 * A simple button with an image on it.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class ImageButton extends DwoButton {

    private Image image;

    private int marginWidth = 4;

    private int marginHeight = 4;

    /**
     * Creates a new button with the specified image.
     * 
     * @param i The image to show.
     */
    public ImageButton(Image i) {
        super();
        image = i;
        setSize(image.getWidth(this) + marginWidth * 2, image.getHeight(this)
                + marginHeight * 2);
        this.invalidate();
    }

    /**
     * Paints the image on the center of the button, and paints the parent.
     * 
     * @param g The graphics context to use for painting.
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        validate();
        super.paint(g);
        g.drawImage(image, (getSize().width / 2) - (image.getWidth(this) / 2), (getSize().height / 2)
                - (image.getHeight(this) / 2), this);
    }

    /**
     * Returns the marginHeight around the image on the button.
     * 
     * @return The marginHeight around the image on the button.
     */
    public int getMarginHeight() {
        return marginHeight;
    }

    /**
     * Sets the marginHeight around the image on the button. The button is
     * resized to the imagessize + the marginHeight.
     * 
     * @param marginHeight The marginHeight to set.
     */
    public void setMarginHeight(int marginHeight) {
        this.marginHeight = marginHeight;
        setSize(image.getWidth(this) + marginWidth * 2, image.getHeight(this)
                + marginHeight * 2);
    }

    /**
     * Returns the marginWidth around the image on the button.
     * 
     * @return The marginWidth around the image on the button.
     */
    public int getMarginWidth() {
        return marginWidth;
    }

    /**
     * Sets the marginWidth around the image on the button. The button is
     * resized to the imagessize + the marginWidth.
     * 
     * @param marginWidth The marginWidth to set.
     */
    public void setMarginWidth(int marginWidth) {
        this.marginWidth = marginWidth;
        setSize(image.getWidth(this) + marginWidth * 2, image.getHeight(this)
                + marginHeight * 2);
    }

    /**
     * Gets the mininimum size of this component. The minimum size is the image
     * size + marginWidth/marginHeight.
     * 
     * @return A dimension object indicating this component's minimum size.
     */
    public Dimension getMinimumSize() {
        return new Dimension(image.getWidth(this) + marginWidth * 2, image.getHeight(this)
                + marginHeight * 2);
    }

    /**
     * Gets the preferred size of this component. The preferred size is the
     * image size + marginWidth/marginHeight.
     * 
     * @return A dimension object indicating this component's preferred size.
     */
    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(this) + marginWidth * 2, image.getHeight(this)
                + marginHeight * 2);
    }

	public void setImage(Image image) {
		this.image = image;
		repaint(); // no resize!!
	}
}