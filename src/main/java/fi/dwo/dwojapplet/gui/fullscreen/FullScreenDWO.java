package fi.dwo.dwojapplet.gui.fullscreen;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.BackgroundPanel;
import fi.dwo.dwojapplet.gui.GuiConstants;

public class FullScreenDWO extends JDialog implements ChangeListener, ActionListener, InternalFrameListener {

	private static final String CONFIRM_TITLE = "Wil je de toets afsluiten?";
	private JTextArea log;
	private JDesktopPane content;
	private boolean fuse, fuse2;
	private Timer   timer;
	private JButton button;
	
	
	
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
		//frame.show(); // FIXME in commentaar bij productie
		try {
			frame.setIcon(true);
		} catch (PropertyVetoException e) {
		}

		frame = new JInternalFrame("Activiteit", true, true, true, true);
		
		//hier is de juiste maat van de activiteit nodig (zonder scrollbalken!!)
		// nu even hard
		component.setBounds(20,110,1024,500);
		frame.setBounds(component.getBounds()); // initiele maten.....
		Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + 10 + insets.left + insets.right+30, insets.top + insets.bottom + frame.getHeight() + 140); // marge
		
		//Snelle fix: alles absoluut gepositioneerd. later beter maken
		BackgroundPanel p = new BackgroundPanel(new BorderLayout());
		
		//p.setOpaque(false);
	  	p.setGuiImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_SCO));
	  	p.add(component, BorderLayout.CENTER);
	  	p.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 5)); // met die 0 kan ik de knop laten zakken
	  	JPanel pp = new JPanel(false);
	  	pp.setOpaque(false);
	  	pp.add(button = new JButton("Afsluiten en inleveren"));
	  	button.addActionListener(this);
	  	pp.setPreferredSize(new Dimension(300, 90));
	  	pp.setMaximumSize(new Dimension(6000,90));
	  	pp.setBounds(400,55,300,50);
	  	Box ppp = Box.createHorizontalBox();
	  	ppp.add(Box.createRigidArea(new Dimension(400,100)));
	  	ppp.add(pp);
	  	ppp.add(Box.createHorizontalGlue());
	  	p.add(ppp,BorderLayout.NORTH);
		
		frame.setContentPane(p);
		frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		frame.addInternalFrameListener(this);
		frame.setDoubleBuffered(true);
		content.add(frame);
		frame.show();
		
		
		setContentPane(content);
		
		Dimension screenSize = DwoHelper.getApplet().getToolkit().getScreenSize();
		frame.setLocation((int)screenSize.getWidth()/2-frame.getWidth()/2, (int)screenSize.getHeight()/2-frame.getHeight()/2);
		pack();		
	}

        @Override
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
					if( w == mainFrame || c == this || c == mainFrame || c == null)
					{

						WindowListener[] listeners = w.getWindowListeners();
						if(listeners != null) {
							for (int i = 0; i < listeners.length; i++) {
								if(listeners[i] instanceof WindowLogger)
									return;
							}
						}
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
				
				Box vbox = Box.createVerticalBox();
				vbox.add(new JLabel("Weet je dat wel zeker"));
				final JProgressBar bar = new JProgressBar();
				new Timer(1000, new ActionListener() {
					
                        @Override
					public void actionPerformed(ActionEvent e) {
						bar.setValue(Math.max(bar.getValue()-1, 0));
						if(bar.getValue() == 0) {
							((Timer) e.getSource()).stop();
						}
					}
				}).start();
				bar.setMaximum(10);
				bar.setValue(10);
				vbox.add(bar);
				
				int r = JOptionPane.showInternalConfirmDialog(content, vbox, CONFIRM_TITLE, JOptionPane.YES_NO_OPTION);
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
	
	static FullScreenDWO showInFrame(Frame f, JComponent content) {
		FullScreenDWO frame = new FullScreenDWO(f);
		frame.initialize(content);
		
		WindowLogger logger = new WindowLogger(frame);
		frame.addComponentListener(logger);
		frame.addWindowListener(logger);
		frame.addWindowStateListener(logger);
		frame.addWindowFocusListener(logger);
		
		frame.setSize(frame.getToolkit().getScreenSize());
		frame.validate();
		return frame;
	}

        @Override
	public void actionPerformed(ActionEvent event) {
		
		if(event.getSource() == timer && timer != null ) {
			JInternalFrame selectedFrame = content.getSelectedFrame();
			if ( selectedFrame != null && selectedFrame.getTitle()== CONFIRM_TITLE )  // check if toplevel is confirm dialog.
			{				
				selectedFrame.doDefaultCloseAction();
			}
			return;
		}
		if(event.getSource() != button || weetJeHetZeker())
		{	internalFrameClosed(null);        
		}
	}

        @Override
	public void internalFrameActivated(InternalFrameEvent e) {
	}

        @Override
	public void internalFrameClosed(InternalFrameEvent e) {
		setVisible(false);	
		fuse = true;
	}

	private boolean weetJeHetZeker() {
		int r = JOptionPane.showInternalConfirmDialog(content, "Weet je dat wel zeker", CONFIRM_TITLE, JOptionPane.YES_NO_OPTION);
		return r == JOptionPane.YES_OPTION;
	}
	
	
        @Override
	public void internalFrameClosing(InternalFrameEvent e) {
		if(weetJeHetZeker())
		{	internalFrameClosed(null);
		} else {
			e.getInternalFrame().show();
		}
	}

        @Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
	}

        @Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
	}

        @Override
	public void internalFrameIconified(InternalFrameEvent e) {
	}

        @Override
	public void internalFrameOpened(InternalFrameEvent e) {
	}

	public void tearDown() {
		fuse = fuse2 = true;
		if(timer != null ) { timer.stop(); timer = null; }
		JOptionPane.showInternalMessageDialog(content, "De toets wordt afgesloten en ingeleverd");
		setVisible(false);
	}
	
	
}
