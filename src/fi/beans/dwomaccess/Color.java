package fi.beans.dwomaccess;

import java.io.Serializable;

/**
 * Stub voor java.awt.Color
 * @author velth101
 * @see java.awt.Color
 */

public class Color implements Serializable {
	private int red, green, blue, alpha;

	public int getRed() {
		return red;
	}
	public int getGreen() {
		return green;
	}
	public int getBlue() {
		return blue;
	}

	public Color(int red, int green, int blue) {
		super();
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = 255;
	}
	
	public int getAlpha() {
		return alpha;
	}

	public Color(int red, int green, int blue, int alpha) {
		super();
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

}
