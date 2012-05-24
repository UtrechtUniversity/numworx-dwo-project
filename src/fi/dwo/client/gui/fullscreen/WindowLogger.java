package fi.dwo.client.gui.fullscreen;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class WindowLogger implements WindowListener, ComponentListener, WindowStateListener, WindowFocusListener {

	private ChangeListener listener;
	
	private void change(Object o) {
		listener.stateChanged(new ChangeEvent(o));
	}

	public WindowLogger(ChangeListener listener) {
		this.listener = listener;
	}

	public void windowActivated(WindowEvent arg0) {
		//change(arg0);
	}

	public void windowClosed(WindowEvent arg0) {
		change(arg0);
	}

	public void windowClosing(WindowEvent arg0) {
		change(arg0);
	}

	public void windowDeactivated(WindowEvent event) {
		change(event);
	}

	public void windowDeiconified(WindowEvent arg0) {
		//change(arg0);
	}

	public void windowIconified(WindowEvent event) {
		change(event);
	}

	public void windowOpened(WindowEvent arg0) {
		//change(arg0);
	}

	public void componentHidden(ComponentEvent arg0) {
		change(arg0);
	}

	public void componentMoved(ComponentEvent arg0) {
		change(arg0);
	}

	public void componentResized(ComponentEvent arg0) {
		//change(arg0);
	}
	public void componentShown(ComponentEvent arg0) {
		change(arg0);
	}

	public void windowStateChanged(WindowEvent arg0) {
		change(arg0);
	}

	public void windowGainedFocus(WindowEvent arg0) {
		change(arg0);
	}

	public void windowLostFocus(WindowEvent arg0) {
		change(arg0);
	}
	
	
	
}
