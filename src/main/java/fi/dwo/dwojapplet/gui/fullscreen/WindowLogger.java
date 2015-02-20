package fi.dwo.dwojapplet.gui.fullscreen;

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

    @Override
    public void windowActivated(WindowEvent arg0) {
        //change(arg0);
    }

    @Override
    public void windowClosed(WindowEvent arg0) {
        change(arg0);
    }

    @Override
    public void windowClosing(WindowEvent arg0) {
        change(arg0);
    }

    @Override
    public void windowDeactivated(WindowEvent event) {
        change(event);
    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {
        //change(arg0);
    }

    @Override
    public void windowIconified(WindowEvent event) {
        change(event);
    }

    @Override
    public void windowOpened(WindowEvent arg0) {
        //change(arg0);
    }

    @Override
    public void componentHidden(ComponentEvent arg0) {
        change(arg0);
    }

    @Override
    public void componentMoved(ComponentEvent arg0) {
        change(arg0);
    }

    @Override
    public void componentResized(ComponentEvent arg0) {
        //change(arg0);
    }

    @Override
    public void componentShown(ComponentEvent arg0) {
        change(arg0);
    }

    @Override
    public void windowStateChanged(WindowEvent arg0) {
        change(arg0);
    }

    @Override
    public void windowGainedFocus(WindowEvent arg0) {
        change(arg0);
    }

    @Override
    public void windowLostFocus(WindowEvent arg0) {
        change(arg0);
    }

}
