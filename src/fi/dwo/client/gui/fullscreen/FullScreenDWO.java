package fi.dwo.client.gui.fullscreen;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import fi.dwo.client.domain.DwoHelper;

public class FullScreenDWO extends JDialog implements ChangeListener, ActionListener, InternalFrameListener {

	private static final String CONFIRM_TITLE = "Wil je de toets afsluiten?";
	private JTextArea log;
	private JDesktopPane content;
	private boolean fuse, fuse2;
	private Timer   timer;
	
	
	
	private FullScreenDWO(Frame frame) {
		super(frame, true);
	}

	public void initialize(JComponent component) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Digitale Wiskunde Omgeving");
        //setResizable(false);  
        setUndecorated(true);  ////?????
        //setAlwaysOnTop(true);  // security error voor applet
		Box box = Box.createVerticalBox();
		log = new JTextArea();
		log.setWrapStyleWord(true);
		box.add(new JScrollPane(log));
		JButton btn = new JButton("Exit");
		btn.addActionListener(this);
		box.add(btn);		
		
		content = new JDesktopPane();
		JInternalFrame frame = new JInternalFrame("Logging", true, false, true, true);
		frame.setBounds(10, 20, 300, 400);
		frame.setContentPane(box);
		content.add(frame);
		frame.show();

		frame = new JInternalFrame("Activiteit", true, true, true, true);
		frame.setBounds(component.getBounds()); // initiele maten.....
		frame.setContentPane(component);
		frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		frame.addInternalFrameListener(this);
		content.add(frame);
		frame.show();
		
		
		setContentPane(content);
		pack();		
	}

	public void stateChanged(ChangeEvent event) {
		Object source = event.getSource();
		log.append(String.valueOf(source));
		log.append("\n");
		
		if(source instanceof WindowEvent) {
			WindowEvent we = (WindowEvent) source;
			if(we.getID() == WindowEvent.WINDOW_DEACTIVATED && !fuse)
			{
				Window w = we.getOppositeWindow();
				if(w != null)
				{	Container c = w.getParent();
					Frame mainFrame = JOptionPane.getFrameForComponent(DwoHelper.getApplet());
					if( w == mainFrame || c == this || c == mainFrame)
					{
						
						// monitor w for deactivate events met NULL als opposite. 
						w.addWindowListener(new WindowLogger(this));
						
						return; // is ok.  IS NIET OK, You loose control!
						
					}
					if(w instanceof Dialog) {
						Dialog d = (Dialog)w;
						if(d.isModal())
						{
							return;
						}
					}
				}
				
				fuse = true;
				toFront();
				log.append("DEACTIVATION DETECTED...");
				Timer t = timer = new Timer(10000, this);
				t.setRepeats(false);
				t.start();
				int r = JOptionPane.showInternalConfirmDialog(content, "Weet je dat wel zeker", CONFIRM_TITLE, JOptionPane.YES_NO_OPTION);
				t.stop();
				timer = null;
				log.append(", result = "+ r + "\n");
				if(r != JOptionPane.NO_OPTION)
				{
					setVisible(false);
				}
				else
					fuse = false;
			} else if (we.getID() == WindowEvent.WINDOW_ICONIFIED && !fuse && !fuse2) 
			{	fuse2 = true; // oneshot....
			    log.append("setstate(NORMAL)\n");
				//setState(NORMAL);
				//fuse2 = false;
			} else if (we.getID() == WindowEvent.WINDOW_CLOSING) {
				System.out.println(log.getText());
				System.out.println("Closing");
				//fuse = true;
			}
		}
		
	}
	

	static void showInFrame(Frame f, JComponent content) {
		FullScreenDWO frame = new FullScreenDWO(f);
		frame.initialize(content);
		
		WindowLogger logger = new WindowLogger(frame);
		frame.addComponentListener(logger);
		frame.addWindowListener(logger);
		frame.addWindowStateListener(logger);
		frame.addWindowFocusListener(logger);
		
		frame.setSize(frame.getToolkit().getScreenSize());
		frame.validate();
		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent event) {
		
		if(event.getSource() == timer && timer != null ) {
			JInternalFrame selectedFrame = content.getSelectedFrame();
			if ( selectedFrame != null && selectedFrame.getTitle()== CONFIRM_TITLE )  // check if toplevel is confirm dialog.
			{				
				selectedFrame.doDefaultCloseAction();
			}
			return;
		}
		
        setVisible(false);        
	}

	public void internalFrameActivated(InternalFrameEvent e) {
	}

	public void internalFrameClosed(InternalFrameEvent e) {
		setVisible(false);	
		fuse = true;
	}

	public void internalFrameClosing(InternalFrameEvent e) {
		int r = JOptionPane.showInternalConfirmDialog(content, "Weet je dat wel zeker", CONFIRM_TITLE, JOptionPane.YES_NO_OPTION);
		if(r == JOptionPane.YES_OPTION)
		{	setVisible(false);
			fuse = true;
		} else {
			e.getInternalFrame().show();
		}
	}

	public void internalFrameDeactivated(InternalFrameEvent e) {
	}

	public void internalFrameDeiconified(InternalFrameEvent e) {
	}

	public void internalFrameIconified(InternalFrameEvent e) {
	}

	public void internalFrameOpened(InternalFrameEvent e) {
	}
	
	
}
