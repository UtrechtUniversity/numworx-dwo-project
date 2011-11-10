package fi.dwo.client.gui.action;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import fi.dwo.client.domain.CourseMap;

public class Clipboard {
	public static String cmd;
	static PropertyChangeSupport support = new PropertyChangeSupport(new Clipboard());
	private static CourseMap clipboard;
	private static CourseMap selection;

	public static void addPropertyChangeListener(String string,
			PropertyChangeListener listener) {
			support.addPropertyChangeListener(string, listener);
	}
	public static CourseMap getClipboard() {
		return clipboard;
	}
	public static void setClipboard(CourseMap clipboard) {
		CourseMap old = Clipboard.clipboard;
		Clipboard.clipboard = clipboard;
		support.firePropertyChange("clipboard", old, clipboard);
	}
	public static CourseMap getSelection() {
		return selection;
	}
	public static void setSelection(CourseMap selection) {
		CourseMap old = Clipboard.selection;
		Clipboard.selection = selection;
		System.out.println("set selection from " + old + " to " + selection);
		support.firePropertyChange("selection", old, selection);
	}


}
