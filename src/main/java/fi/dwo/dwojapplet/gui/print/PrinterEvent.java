package fi.dwo.dwojapplet.gui.print;

import java.util.EventObject;

public class PrinterEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7173800877033034188L;
	public static final int STARTED = 0;
	public static final int STOPPED = 1;
	final private int type;
	
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	PrinterEvent(Object source, int type) {
		super(source);
		this.type = type;
	}

}
