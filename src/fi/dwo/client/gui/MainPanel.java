// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\MainPanel.java

package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Point;

import javax.swing.JLabel;

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

    private Panel header;
    
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
        Panel p;
        JLabel l;
        
        if(GuiConstants.GUI_IMAGE_BG) guiImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE);

        //l = new Label(TextMapper.getText(TextMapper.GUIM_DWO_FULL));
        l = new JLabel(" "+dwoProfile.getDescription());
        l.setFont(GuiConstants.RED_TEXT);
        l.setOpaque(false);
        l.setForeground(GuiConstants.RED_COLOR);
        l.setForeground(new Color(3,65,123));
        fm = l.getFontMetrics(l.getFont());
        l.setBounds(0, 2, fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setVisible(false);
        this.add(l);
        if(GuiConstants.GUI_IMAGE_BG) remove(l);
        l.setVisible(true);
        
        User u = GuiCreator.instance().getUser();
        if(u != null) {
            School s = u.getSchool();
            if(s != null) {
                l = new JLabel(s.getName());
                l.setOpaque(false);
                l.setFont(GuiConstants.RED_TEXT_ITALIC);
                l.setForeground(GuiConstants.RED_COLOR);
                fm = l.getFontMetrics(l.getFont());
                l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
                l.setLocation(this.getSize().width - 10 - l.getSize().width, 2);
                l.setVisible(false);
                this.add(l);
                l.setVisible(true);                
            }
        }
       
        p = new BorderedPanel(null);
        if(GuiConstants.GUI_IMAGE_BG) {
        	p = new BorderedPanel(null,0)
            {
	        	public void paint(Graphics g)
	            {	Point p = DwoHelper.getComponentLocation(this);
	            	System.out.println(""+p);
	            	g.drawImage(guiImage,-p.x,-p.y,null);
	            	super.paint(g);
	            }
	        };
        }
        p.setBackground(GuiConstants.MAIN_BACKGROUND);
        p.setBounds(5, 20, 151, 71);
        p.setVisible(false);
        this.add(p);
        p.setVisible(true);

        /* Add FI logo */
        Image fiLogo;
        fiLogo = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.WISWEB_LOGO_SMALL_LOCATION);
        MediaTracker tr = new MediaTracker(this);
        tr.addImage(fiLogo, 0);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }
        Panel ip = new ImagePanel(fiLogo);
        ip.setLocation(40, 1);
        ip.setVisible(false);
        p.add(ip);
        if(GuiConstants.GUI_IMAGE_BG) p.remove(ip);
        ip.setVisible(true);

        /* DWO-Label 
        l = new Label(TextMapper.getText(TextMapper.GUIM_DWO_SHORT));
        l.setFont(GuiConstants.HEADER_TEXT);
        l.setForeground(GuiConstants.RED_COLOR);
        fm = l.getFontMetrics(l.getFont());
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setLocation(ip.getSize().width + ip.getLocation().x, (p.getSize().height / 2)
                - (l.getSize().height / 2));
        l.setVisible(false);
        p.add(l);
        l.setVisible(true);*/
        
        l = new JLabel(TextMapper.getText(TextMapper.GUIM_FI_NAME));
        l.setOpaque(false);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(new Color(3,65,123));
        fm = l.getFontMetrics(l.getFont());
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setLocation(20, 50);
        l.setVisible(false);
        p.add(l);
        if(GuiConstants.GUI_IMAGE_BG) p.remove(l);
        l.setVisible(true);

        header = new BorderedPanel(null);
        if(GuiConstants.GUI_IMAGE_BG) {
        	header = new BorderedPanel(null,0)
            {
	        	public void paint(Graphics g)
	            {	Point p = DwoHelper.getComponentLocation(this);
	            	System.out.println(""+p);
	            	g.drawImage(guiImage,-p.x,-p.y,null);
	            	super.paint(g);
	            }
	        };
        }
        header.setLayout(null);
        header.setBackground(GuiConstants.MAIN_BACKGROUND);
        header.setBounds(171, 10, 469, 81);
        header.setVisible(false);
        this.add(header);
        header.setVisible(true);

        /* DWO-full-Label */
        l = new JLabel(TextMapper.getText(TextMapper.GUIM_MAIN_MENU));
        l.setOpaque(false);
        l.setFont(GuiConstants.HEADER_TEXT);
        l.setForeground(new Color(3,65,123));
        fm = l.getFontMetrics(l.getFont());
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setLocation((header.getSize().width / 2) - (l.getSize().width / 2), (header.getSize().height / 2)
                - (l.getSize().height / 2));
        l.setVisible(false);
        header.add(l);
        l.setVisible(true);

        /* Logged In panel */
        loggedIn = new LoggedInPanel();
        loggedIn.setBounds(645, 20, 151, 71);
        loggedIn.doLayout();
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
    public void setHeaderPanel(Panel p) {
        if (this.header != null) {
            header.setVisible(false);
            this.remove(header);
            header.setVisible(true);
        }

        header = p;
        header.setVisible(false);
        this.add(header);
        header.setBounds(166, 20, 469, 71);
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