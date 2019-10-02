package fi.dwo.dwojapplet.gui;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

public class ReducedImageIcon extends ImageIcon {

	public ReducedImageIcon() {
	}

	static private Image reduce(Image image) {
		image = image.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
		return image;
	}

	static private Image reduce(Image image, int w, int h) {
      image = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
      return image;
  }
	
	public ReducedImageIcon(Image image) {
		super(reduce(image));
	}
    public ReducedImageIcon(Image image, int w, int h) {
      super(reduce(image, w, h));
  }


	public ReducedImageIcon(Image image, String description) {
		super(reduce(image), description);
	}


}
