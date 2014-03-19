package fi.beans.dwomaccess;

import java.io.Serializable;

/**
 * Stub voor java.awt.Font
 * @author velth101
 * @see java.awt.Font
 */
public class Font implements Serializable {

	private String family;
	private int style, size;

	public String getFamily() {
		return family;
	}

	public int getStyle() {
		return style;
	}
	public int getSize() {
		return size;
	}

	public Font(String family, int style, int size) {
		super();
		this.family = family;
		this.style = style;
		this.size = size;
	}

}
