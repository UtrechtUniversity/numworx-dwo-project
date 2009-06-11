// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\LoggedInPanel.java

package fi.dwo.client.gui;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

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
public class LoggedInPanel extends BorderedPanel implements ActionListener {
    private User user;

    private MainPanel mainPanel;

    private DwoButton logoffButton;

    private boolean layoutDone = false;
    
    private Image guiImage;

    /**
     * Creates a new LoggedInPanel. It shows who is logged in, and a button to
     * logg of.
     */
    public LoggedInPanel() {
    	if(GuiConstants.GUI_IMAGE_BG) setBorders(0);
    	guiImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE);
    }
    
    public void setGuiImage(Image image)
    {
    	guiImage = image;
    }
    
    public void paint(Graphics g) {
    	if(GuiConstants.GUI_IMAGE_BG) {
	       	Point p = DwoHelper.getComponentLocation(this);
	       	g.drawImage(guiImage ,-p.x,-p.y,null);
    	}       
    	super.paint(g);
    } 

    /**
     * Lays-out the component. Shows a text with the user who is logged in and a
     * button to logoff.
     * 
     * @see java.awt.Component#doLayout()
     */
    public void doLayout() {

        /*
         * Only once This can't done in the constructor, because of the panel
         * has than no size.
         */
        if (!layoutDone) {
            layoutDone = true;
            super.doLayout();
            this.setBackground(GuiConstants.MAIN_BACKGROUND);

            boolean loggedIn = true;
            user = GuiCreator.instance().getUser();
            if(user instanceof Guest)   {
                loggedIn = false;
            }
            
            /* Variables used to create items */
            FontMetrics fm;
            JLabel l;
            
            user = GuiCreator.instance().getUser();

            l = new JLabel(TextMapper.getText(TextMapper.GUIL_LOGGED_IN_AS)+ ":");
            if(GuiConstants.GUI_IMAGE_BG) l = new JLabel(TextMapper.getText(TextMapper.GUIL_LOGGED_IN_AS)+ ": " + user.getName());
            if(!loggedIn) {
                l = new JLabel(TextMapper.getText(TextMapper.GUIL_NOT_LOGGED_IN));
            }
            l.setFont(GuiConstants.NORMAL_TEXT);
            l.setOpaque(false);
            fm = l.getFontMetrics(l.getFont());
            l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
            l.setLocation((getSize().width / 2) - (l.getSize().width / 2), 5);
            if(GuiConstants.GUI_IMAGE_BG)l.setLocation((getSize().width / 2) - (l.getSize().width / 2), 2);
            this.add(l);

            /* Add Username */
            
            l = new JLabel(user.getName());
            l.setFont(GuiConstants.NORMAL_TEXT);
            fm = l.getFontMetrics(l.getFont());
            l.setOpaque(false);
            l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
            l.setLocation((getSize().width / 2) - (l.getSize().width / 2), 24);
            if(loggedIn) this.add(l);
            if(GuiConstants.GUI_IMAGE_BG) remove(l);
            
            /* Add Logoff button */
            if(!user.canLogout()) return;
            
            logoffButton = new DwoButton(TextMapper.getText(TextMapper.GUIL_BTN_LOGOFF), GuiConstants.MAIN_BACKGROUND);
            if(user instanceof Guest)   {
                logoffButton = new DwoButton(TextMapper.getText(TextMapper.GUIL_BTN_LOGIN), GuiConstants.MAIN_BACKGROUND);
            }
            fm = logoffButton.getFontMetrics(logoffButton.getFont());
            logoffButton.setSize(fm.stringWidth(logoffButton.getLabel()) + 20, fm.getHeight() + 10);
            logoffButton.setLocation((getSize().width / 2)
                    - (logoffButton.getSize().width / 2), 40);
            if(GuiConstants.GUI_IMAGE_BG) logoffButton.setLocation((getSize().width / 2)
                    - (logoffButton.getSize().width / 2), 43);
            logoffButton.addActionListener(this);
            this.add(logoffButton);
        }

    }

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