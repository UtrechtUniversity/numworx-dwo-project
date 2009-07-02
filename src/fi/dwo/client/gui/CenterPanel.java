// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\CenterPanel.java

package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Image;
import java.awt.ScrollPane;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import fi.dwo.client.domain.DwoHelper;

/**
 * The main-centerpanel.
 * This can be showed with or without menu. If there is a menu (e.g. te course overview) the CenterMainSubPanel is showed.
 * Otherwise (e.g. the panel with the sco), no menu is showed and the hole center is used.
 * @author M.J.B. Kupers
 *  
 */
public class CenterPanel extends JPanel {
    private static final Border MAIN_BORDER = BorderFactory.createEmptyBorder(18, 6, 8, 10);

	private static final Component RAND = Box.createHorizontalStrut(12);

	private final class RequestFocusAST implements Runnable {
		private final Component c;

		private RequestFocusAST(Component c) {
			this.c = c;
		}

		public void run() {
		       c.requestFocusInWindow();
		}
	}

	private MainPanel mainPanel;

    private GuestMenuPanel menu;

    private CenterSubPanel centerSubPanel;

    private Panel centermainSub;

    private JScrollPane sp;
    private Panel spRand;

    private boolean showMenu;
    
    private Image guiImage;

	private Panel spe;
    
    /**
     * Creates a new CenterPanel. Adds a menu to it and loads a new
     * CourseChoisePanel.
     * 
     * @param mp The mainpanel to communicate with
     */
    public CenterPanel(MainPanel mp) {
        mainPanel = mp;
        if(GuiConstants.GUI_IMAGE_BG) guiImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE); 
        this.setBackground(GuiConstants.SUB_BACKGROUND);
        this.setLayout(new BorderLayout());
        //this.setSize(GuiConstants.CENTER_WIDTH, GuiConstants.CENTER_HEIGHT);

        centermainSub = new CenterMainSubPanel(null, 0  // SOUTH
                /*| BorderedPanel.EAST  | BorderedPanel.WEST */);
        
        if(GuiConstants.GUI_IMAGE_BG) {
        	centermainSub = new CenterMainSubPanel(null, 0) /*{
	        	public void paint(Graphics g)
	            {	Point p = DwoHelper.getComponentLocation(this);
	            	g.drawImage(DwoHelper.getImage(GuiConstants.RESOURCES + "resources/EPN-sco.png"),-p.x,-p.y,null);
	            	super.paint(g);
	            }
	        }*/;
        }
        centermainSub.setLayout(new BoxLayout(centermainSub, BoxLayout.LINE_AXIS));
        centermainSub.setBackground(GuiConstants.MAIN_BACKGROUND);
       // centermainSub.setBounds(0, 10, GuiConstants.CENTER_WIDTH - 1, GuiConstants.CENTER_HEIGHT-10);
        this.add(centermainSub, BorderLayout.CENTER);
        if(!GuiConstants.GUI_IMAGE_BG) {
        	setBorder(BorderFactory.createMatteBorder(10, 5, 7, 4, GuiConstants.SUB_BACKGROUND));
        	setBorder(BorderFactory.createCompoundBorder(getBorder(),
        		BorderFactory.createMatteBorder(0, 1, 1, 1, Color.black)));
        } else {
// TODO Tuning!!!
        	setOpaque(false);
         	setDoubleBuffered(false);
// er is een andere border nodig voor sco's 
         	setBorder(MAIN_BORDER);
        }
        
        loadMenu();
        showMenu = true;

		//spRand = new Panel();
        //spRand.setBounds(162, -2, 2, 494);
		centermainSub.add(RAND);
       // centermainSub.add(spRand);
		
        sp = new JScrollPane();
        spe = new Panel(new BorderLayout());
		//sp.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        sp.setViewportBorder(null);
        sp.setBorder(null);
        //sp.setBounds(162, -2, 628, 494);
        spe.add(sp);
        centermainSub.add(spe);

        //loadCenter(new CourseChoisePanel());
        
        CenterSubPanel csp = GuiCreator.instance().getCourseChoisePanel();
        if(csp instanceof ScoPanel ) {
        	//centermainSub.remove(spRand);
        	centermainSub.remove(spe);
        	((CenterMainSubPanel)centermainSub).setGuiImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_SCO));
        	mainPanel.setGuiImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_SCO));
        	guiImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_SCO);
        	loadTotal(csp);
        }
        else if(csp instanceof CoursePanel ) {
        	menu.hideMainButton();
        	((CenterMainSubPanel)centermainSub).setGuiImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE));
        	mainPanel.setGuiImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE));
        	guiImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE);
        	loadCenter(csp);
        }
        else loadCenter(csp);

    }

    /**
     * Returns the current MainPanel.
     * 
     * @return The current mainpanel
     */
    public MainPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * Loads a panel in the center. The menu is showed on the left side.
     * 
     * @param panel The centerpanel to load.
     * @see fi.dwo.client.gui.CenterSubPanel
     */
    public void loadCenter(CenterSubPanel panel) {
        GuiCreator.instance().setWait();
        if(GuiConstants.GUI_IMAGE_BG)
        	setBorder(MAIN_BORDER);
        if (centerSubPanel != null) {
            centerSubPanel.end();
            if (spe.isVisible()) {
                ((Component) centerSubPanel).setVisible(false);
                sp.remove((Component) centerSubPanel);
                ((Component) centerSubPanel).setVisible(true);
            } else {
                ((Component) centerSubPanel).setVisible(false);
                centermainSub.remove((Component) centerSubPanel);
                ((Component) centerSubPanel).setVisible(true);
            }
        }

        centerSubPanel = panel;
        centerSubPanel.setCenterPanel(this);
        
        ((CenterMainSubPanel)centermainSub).setGuiImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE));
    	mainPanel.setGuiImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE));
    	guiImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE);

        /* If we didn't show the menu, show it */
        if (!showMenu) {
            menu.setVisible(true);
            //centermainSub.add(menu,0);
            RAND.setVisible(true);
        }

        final Component c = centerSubPanel.getComponent();

        if (c.getSize().height > sp.getSize().height) {
            c.setVisible(false);
            sp.setViewportView(c);
            c.setVisible(true);
            spe.setVisible(true);
            //spRand.setVisible(true);
            c.invalidate();
        } else {
            //sp.setVisible(false);
            //spRand.setVisible(false);
            //c.setLocation(162, 10);
            //c.setVisible(false);
            sp.setViewportView(c);
            //c.setVisible(true);
            c.invalidate();
        }
//        c.invalidate();
        showMenu = true;
        mainPanel.setHeaderPanel(panel.getHeaderPanel());

        GuiCreator.instance().setReady();
        SwingUtilities.invokeLater(new RequestFocusAST(c));
 
//        repaint();

    }

    /**
     * Loads a panel on the whole size. The menu is hidden.
     * 
     * @param panel The mainpanel to load.
     * @see fi.dwo.client.gui.CenterSubPanel
     */
    public void loadTotal(CenterSubPanel panel) {
    	if(GuiConstants.GUI_IMAGE_BG) // todo tuning
    		setBorder(BorderFactory.createEmptyBorder(10, 7, 8, 7));
    	invalidate();
        if (centerSubPanel != null) {
            if(DwoHelper.umpc) {
        	getParent().add(this,0);
        	setLocation(5,-9);
            }
           	centerSubPanel.end();
            if (sp.isVisible()) {
                ((Component) centerSubPanel).setVisible(false);
                sp.remove((Component) centerSubPanel);
                ((Component) centerSubPanel).setVisible(true);
            } else {
                ((Component) centerSubPanel).setVisible(false);
                centermainSub.remove((Component) centerSubPanel);
                ((Component) centerSubPanel).setVisible(true);
            }
        }
        spe.setVisible(false); // centerSubPanel direct aan centerMainSub -> sp.hide();
        //spRand.setVisible(false); // een extra randje wegwerken
        centerSubPanel = panel;
        panel.setCenterPanel(this);
        
        ((CenterMainSubPanel)centermainSub).setGuiImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_SCO));
    	mainPanel.setGuiImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_SCO));
    	guiImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_SCO);

        /* We don't want to see the menu */
        if (showMenu) {
            RAND.setVisible(false);
            //centermainSub.remove(menu);
            menu.setVisible(false);
            centermainSub.invalidate();
        }

        Component c = centerSubPanel.getComponent();
        c.setLocation(1, 0);
        c.setVisible(false);
        centermainSub.add(c);
        c.setVisible(true);
        SwingUtilities.invokeLater(new RequestFocusAST(c));
//        c.invalidate();

        showMenu = false;
        mainPanel.setHeaderPanel(panel.getHeaderPanel());
//        repaint();

    }

    /**
     * Draws the top lines and calls super.paint(g)
     * 
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
   /* public void paint(Graphics g) {
        validate();
        super.paint(g);

        if (showMenu) {
            g.setColor(Color.black);
            g.drawLine(151, 0, 629, 0);
            g.drawLine(150, 0, 150, 9);
            g.drawLine(161, 0, 161, 9);
            g.drawLine(0, 9, getSize().width - 2, 9);
        } else {
            g.setColor(GuiConstants.MAIN_BACKGROUND);
            g.fillRect(162, 0, 468, 10);

            g.setColor(Color.black);
            g.drawLine(161, 0, 161, 10);
            g.drawLine(629, 0, 629, 10);

            g.drawLine(0, 9, 161, 9);
            g.drawLine(629, 9, getSize().width - 2, 9);
        }
    }*/
    
    
    public void paint(Graphics g) {
    	validate();
    	if(GuiConstants.GUI_IMAGE_BG) {
	       	Point p = DwoHelper.getComponentLocation(this);
	       	g.drawImage(guiImage,-p.x,-p.y,null);
	       	super.paint(g);
    	}
    	else {
    		 super.paint(g);

	            int w = getWidth();
    	        if (showMenu) {
    	            g.setColor(Color.black);
    	            g.drawLine(151+5, 0, /*629+5*/ w - 155-11, 0);
    	            g.drawLine(150+5, 0, 150+5, 9);
    	            g.drawLine(161+5, 0, 161+5, 9);
    	            g.drawLine(0+5, 9, w - 5, 9);
    	        } else {
    	            g.setColor(GuiConstants.MAIN_BACKGROUND);
    	            g.fillRect(162+5, 0, w-155-12-162-5, 10);

    	            g.setColor(Color.black);
    	            g.drawLine(161+5, 0, 161+5, 10);
    	            g.drawLine(w-155-11, 0, w-155-11, 10);

    	            g.drawLine(0+5, 9,161+5 , 9);
    	            g.drawLine(w-155-10, 9, w - 5, 9);
    	        }
    	        g.drawLine(5,0,155,0);	// border-lijntje van filogo panel     	      
    	        g.drawLine(w-5, 0, w-155, 0); // border-lijntje loggedin panel
		}
    	
    }
    
    

    /**
     * Called when this panel is closes. Can be used to save session-data, for
     * example the sco-data
     *  
     */
    public void end() {
        if (centerSubPanel != null) {
            centerSubPanel.end();
        }
    }

    /**
     * Loads the menu and shows it.
     */
    public void loadMenu() {
        if (menu != null) {
            menu.setVisible(false);
            centermainSub.remove(menu);
            menu.setVisible(true);
        }

        menu = GuiCreator.instance().getMenuPanel();
        //menu.setLocation(1, 1);
        menu.setCenterPanel(this);
        menu.setVisible(false);
        centermainSub.add(menu, 0);
        menu.setMaximumSize(new Dimension(150-1, Short.MAX_VALUE));
        menu.setMinimumSize(new Dimension(150-1, 100));
        menu.setPreferredSize(menu.getMinimumSize());
        menu.setVisible(true);
        RAND.setVisible(true);
        menu.repaint();
        centermainSub.invalidate();
    }
    
    public GuestMenuPanel getMenu() {
        return menu;
    }
}