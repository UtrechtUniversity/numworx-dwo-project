// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\LoggedInPanel.java

package fi.dwo.client.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.User;
import fi.dwo.client.domain.Guest;
import fi.dwo.client.system.TextMapper;

/**
 * This class represents a panel that shows who is logged in and a button to
 * logg of.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class LoggedInPanel extends Box implements ActionListener {
    private User user;

    private MainPanel mainPanel;

    private JButton logoffButton;

    private boolean layoutDone = false;
    
	private static final int MARGIN = 8;
	
    final class LoggedInPanelButton extends JButton {

		LoggedInPanelButton(String label) {
			super(label);
		}

		/* (non-Javadoc)
		 * @see javax.swing.JComponent#getMaximumSize()
		 */
		public Dimension getMaximumSize() {
			return new Dimension(LoggedInPanel.this.getWidth()-MARGIN,getPreferredSize().height);
		}
	}

    /**
     * Creates a new LoggedInPanel. It shows who is logged in, and a button to
     * logg of.
     */
    public LoggedInPanel() {
    	super(BoxLayout.Y_AXIS);
    	if(GuiConstants.GUI_IMAGE_BG)
    	{
    		setOpaque(false);
    	} else {
    		setBorder(LineBorder.createBlackLineBorder());
    		setBackground(GuiConstants.MAIN_BACKGROUND);
    		setOpaque(true);
    	}
    	// fill the box
    	// label Ingelogd: 
    	// label user 
    	// button 'logout'
    	User user = GuiCreator.instance().getUser();
    	boolean gast = user instanceof Guest;
    	JLabel loggedin = new JLabel(
    			gast?
    			TextMapper.getText(TextMapper.GUIL_NOT_LOGGED_IN).trim():		
    			TextMapper.getText(TextMapper.GUIL_LOGGED_IN_AS)+ ":");
    	loggedin.setFont(GuiConstants.NORMAL_TEXT);
    	loggedin.setAlignmentX(CENTER_ALIGNMENT);
    	JLabel username = null;
    	if(!gast)
    	{
    		if(GuiConstants.GUI_IMAGE_BG)
    		{
    			loggedin.setText(user.getName());
    			loggedin.setHorizontalAlignment(SwingConstants.RIGHT);
    		} else {
    			username = new JLabel(user.getName());
    			username.setAlignmentX(CENTER_ALIGNMENT);
    			username.setFont(GuiConstants.NORMAL_TEXT);
    		}
    	}
    	logoffButton  = new JButton(
    			TextMapper.getText(gast?TextMapper.GUIL_BTN_LOGIN:TextMapper.GUIL_BTN_LOGOFF));
    	logoffButton.addActionListener(this);
    	logoffButton.setAlignmentX(CENTER_ALIGNMENT);

    	add(Box.createVerticalStrut(4));
    	add(loggedin);
    	add(Box.createVerticalStrut(2));
    	if(username != null) {
        	add(Box.createVerticalStrut(2));
    		add(username);
    	}
    	add(Box.createVerticalGlue());
    	if(user.canLogout())
    	{
    		add(logoffButton);
        	add(Box.createVerticalStrut(4));
    	}
    }
    
    public void setGuiImage(Image image)
    {
    }
    
    /**
     * Lays-out the component. Shows a text with the user who is logged in and a
     * button to logoff.
     * 
     * @see java.awt.Component#doLayout()
     */
    public void doLayout()
    {
    	super.doLayout();
    }
    
    /**
     * check valid!
     * Bij een setLayout(null) wordt niet meer automatische gevalideerd.
     * Kan weg als DWO een LayoutManager heeft.
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     * @see java.awt.LayoutManager
     */
    public void paint(Graphics g)
    {
//    	if(!isValid()) {
//    		validate();
//    	}
    	if(isOpaque())
    	{	g.setColor(getBackground());
    		g.fillRect(0, 0, getWidth(), getHeight());
    	}
    	super.paint(g);
    }
//    public void doLayout_old() {
//
//        /*
//         * Only once This can't done in the constructor, because of the panel
//         * has than no size.
//         */
//        if (!layoutDone) {
//            layoutDone = true;
//            super.doLayout();
//            this.setBackground(GuiConstants.MAIN_BACKGROUND);
//
//            boolean loggedIn = true;
//            user = GuiCreator.instance().getUser();
//            if(user instanceof Guest)   {
//                loggedIn = false;
//            }
//            
//            /* Variables used to create items */
//            FontMetrics fm;
//            JLabel l;
//            
//            user = GuiCreator.instance().getUser();
//
//            l = new JLabel(TextMapper.getText(TextMapper.GUIL_LOGGED_IN_AS)+ ":");
//            if(GuiConstants.GUI_IMAGE_BG) l = new JLabel(TextMapper.getText(TextMapper.GUIL_LOGGED_IN_AS)+ ": " + user.getName());
//            if(!loggedIn) {
//                l = new JLabel(TextMapper.getText(TextMapper.GUIL_NOT_LOGGED_IN));
//            }
//            l.setFont(GuiConstants.NORMAL_TEXT);
//            l.setOpaque(false);
//            fm = l.getFontMetrics(l.getFont());
//            l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
//            l.setLocation((getSize().width / 2) - (l.getSize().width / 2), 5);
//            if(GuiConstants.GUI_IMAGE_BG)l.setLocation((getSize().width / 2) - (l.getSize().width / 2), 2);
//            this.add(l);
//
//            /* Add Username */
//            
//            l = new JLabel(user.getName());
//            l.setFont(GuiConstants.NORMAL_TEXT);
//            fm = l.getFontMetrics(l.getFont());
//            l.setOpaque(false);
//            l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
//            l.setLocation((getSize().width / 2) - (l.getSize().width / 2), 24);
//            if(loggedIn) this.add(l);
//            if(GuiConstants.GUI_IMAGE_BG) remove(l);
//            
//            /* Add Logoff button */
//            if(!user.canLogout()) return;
//            
//            logoffButton = new JButton(TextMapper.getText(TextMapper.GUIL_BTN_LOGOFF));//, GuiConstants.MAIN_BACKGROUND);
//            if(user instanceof Guest)   {
//                logoffButton = new JButton(TextMapper.getText(TextMapper.GUIL_BTN_LOGIN));//, GuiConstants.MAIN_BACKGROUND);
//            }
//            fm = logoffButton.getFontMetrics(logoffButton.getFont());
//            logoffButton.setSize(fm.stringWidth(logoffButton.getLabel()) + 20, fm.getHeight() + 10);
//            logoffButton.setLocation((getSize().width / 2)
//                    - (logoffButton.getSize().width / 2), 40);
//            if(GuiConstants.GUI_IMAGE_BG) logoffButton.setLocation((getSize().width / 2)
//                    - (logoffButton.getSize().width / 2), 43);
//            logoffButton.addActionListener(this);
//            this.add(logoffButton);
//        }
//
//    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logoffButton) {
            GuiCreator.instance().logoff();
        }

    }
}