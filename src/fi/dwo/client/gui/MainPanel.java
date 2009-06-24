// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\MainPanel.java

package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.User;
import fi.dwo.client.domain.DwoProfile;
import fi.dwo.client.system.TextMapper;

/**
 * This class is the main panel of the application. It shows the FI-logo, the
 * header panel, the logg-of panel and the center panel.
 * 
 * @author M.J.B. Kupers
 *  
 */
public class MainPanel extends Panel {
    private LoggedInPanel loggedIn;

    private CenterPanel center;

    private Component header;
    
    private Image guiImage;

    /**
     * Creates a new instance of the MainPanel. The main panel shows the
     * FI-logo, the header panel, the logg-of panel and the center panel.
     */
    public MainPanel(DwoProfile dwoProfile) {
        this.setVisible(false);
        this.setBackground(GuiConstants.SUB_BACKGROUND);
        this.setLayout(null);
        this.setSize(GuiConstants.DWO_WIDTH, GuiConstants.DWO_HEIGHT);

        /* Variables used to create items */
        FontMetrics fm;
        Container p;
        JLabel l;
        
        if(GuiConstants.GUI_IMAGE_BG) guiImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE);

        Box hbox = Box.createHorizontalBox();
        if(!GuiConstants.GUI_IMAGE_BG)
        { 
            hbox.add(Box.createHorizontalStrut(5));
        	l = new JLabel(dwoProfile.getDescription());
        	l.setFont(GuiConstants.RED_TEXT);
        	l.setOpaque(false);
        	l.setForeground(GuiConstants.RED_COLOR);
        	l.setForeground(new Color(3,65,123));
        	hbox.add(l);
        }
        hbox.add(Box.createGlue());
// als alles ontbreekt, creeer een riggel, nodig bij variable layout.        
//      hbox.add(Box.createVerticalStrut(getFontMetrics(GuiConstants.RED_TEXT).getHeight()));
        User u = GuiCreator.instance().getUser();
        if(u != null) {
            School s = u.getSchool();
            if(s != null) {
                l = new JLabel(s.getName());
                l.setOpaque(false);
                l.setFont(GuiConstants.RED_TEXT_ITALIC);
                l.setForeground(GuiConstants.RED_COLOR);
                hbox.add(l);
                hbox.add(Box.createHorizontalStrut(20));
            }
        }
        add(hbox);
        hbox.setBounds(0, 0, getWidth(), hbox.getPreferredSize().height);
        hbox.doLayout();
        
        if(!GuiConstants.GUI_IMAGE_BG)
        {
		
		    /* Add FI logo */
		    Image fiLogo;
		    fiLogo = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.WISWEB_LOGO_SMALL_LOCATION);
		    ImageIcon ip = new ImageIcon(fiLogo);
		    
		    l = new JLabel(TextMapper.getText(TextMapper.GUIM_FI_NAME));
		    l.setIcon(ip);
		    l.setVerticalTextPosition(JLabel.BOTTOM);
		    l.setHorizontalTextPosition(JLabel.CENTER);
		    l.setHorizontalAlignment(JLabel.CENTER);
		    l.setVerticalAlignment(JLabel.CENTER);
		    l.setBackground(GuiConstants.MAIN_BACKGROUND);
		    l.setOpaque(true);
		    l.setFont(new Font("SansSerif", Font.BOLD, 12));
		    l.setForeground(new Color(3,65,123));
		    l.setBorder(BorderFactory.createCompoundBorder(LineBorder.createBlackLineBorder(),
		    		BorderFactory.createEmptyBorder(2, 0, 4, 0)));
// nota bene: het CenterPanel ligt als heavy weight over de bottom-borderline heen
// daarom wordt daar dit lijntje hertekend.
		    l.setBounds(5, 20, 151, 71); add(l);
        }

        header = new HeaderPanel(TextMapper.getText(TextMapper.GUIM_MAIN_MENU));
        this.add(header);

        /* Logged In panel */
        loggedIn = new LoggedInPanel();
        loggedIn.setBounds(645, 20, 151, 71);
        //loggedIn.doLayout();
        loggedIn.setVisible(false);
        this.add(loggedIn);
        loggedIn.setVisible(true);

        center = new CenterPanel(this);
        center.setVisible(false);
        center.setLocation(5, 90);
        this.add(center);
        center.setVisible(true);
        this.setVisible(true);

    }
    
    public void setGuiImage(Image image)
    {	loggedIn.setGuiImage(image);
    	guiImage = image;
    }

    public void paint(Graphics g) {
    	if(GuiConstants.GUI_IMAGE_BG) {
	       	Point p = DwoHelper.getComponentLocation(this);
	       	g.drawImage(guiImage,0,0,null);
    	}       
    	super.paint(g);
    }
    
    /**
     * Removes the old headerpanel and sets a new Panel as a header.
     * 
     * @param p The panel to set as a header.
     */
    public void setHeaderPanel(Component p) {
        if (this.header != null) {
            header.setVisible(false);
            this.remove(header);
            header.setVisible(true);
        }

        header = p;
        header.setVisible(false);
        this.add(header);
// EPN-logo hok is wat breder, 
        int margin = GuiConstants.GUI_IMAGE_BG?30:0;
        int x = 166 + margin;
		int width = 469 - margin;
		header.setBounds(x, 20, width, 71);
        header.setVisible(true);
    }

    /**
     * Called when this panel is closed. Can be used to save session-data, for
     * example the sco-data
     *  
     */
    public void end() {
        center.end();

    }

//    public void setVisible(boolean b) {
//        Exception e = new Exception();
//        e.printStackTrace();
//        super.setVisible(b);
//    }
    /**
     * @return Returns the center.
     */
    public CenterPanel getCenter() {
        return center;
    }
}