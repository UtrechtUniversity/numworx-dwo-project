package fi.dwo.client.gui;

import java.awt.Image;
/**
 * ImageButton met een extra handje.
 * Daarmee kan je een extra parameter aan de methode actionCommand meegeven. 
 * @author Wim
 *
 */
public class ImageButtonPlus extends ImageButton {

	public ImageButtonPlus(Image i) {
		super(i);
	}

	private Object handle;

	/**
	 * @return Returns the handle.
	 */
	public Object getHandle() {
		return handle;
	}

	/**
	 * @param handle The handle to set.
	 */
	public void setHandle(Object handle) {
		this.handle = handle;
	}
	
}
