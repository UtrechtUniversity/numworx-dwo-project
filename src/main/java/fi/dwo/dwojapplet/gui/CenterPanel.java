// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\CenterPanel.java

package fi.dwo.dwojapplet.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.Guest;
import fi.dwo.client.domain.ResultsModuleIF;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.User;
import fi.dwo.client.domain.Teacher;
import fi.dwo.client.gui.action.NullStrategy;
import fi.dwo.client.system.TextMapper;

/**
 * The main-centerpanel.
 * This can be showed with or without menu. If there is a menu (e.g. te course overview) the CenterMainSubPanel is showed.
 * Otherwise (e.g. the panel with the sco), no menu is showed and the hole center is used.
 * @author M.J.B. Kupers
 *  TODO major refactoring 
 */
public class CenterPanel extends JPanel implements CourseContainer {

	private static final Border DEFAULT_MAIN_BORDER = BorderFactory.createEmptyBorder(18, 6, 8, 10);
	private Border MAIN_BORDER = DEFAULT_MAIN_BORDER;

	private static final Component RAND = Box.createHorizontalStrut(12);
	private boolean iconizer;
	
	private IconizedPanel ip, ip2;
	private JPanel window;
	private Border scoBorder = BorderFactory.createEmptyBorder();
	
	private final static class RequestFocusAST implements Runnable {
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

    private JPanel centermainSub;

    private JScrollPane sp;
    private boolean showMenu;
    
    private Container spe;

	ModuleTreePanel tree;
    
    /**
     * Creates a new CenterPanel. Adds a menu to it and loads a new
     * CourseChoisePanel.
     * 
     * @param mp The mainpanel to communicate with
     */
    public CenterPanel(MainPanel mp) {
        mainPanel = mp;
        iconizer = isIconizer();
        
        this.setBackground(GuiConstants.SUB_BACKGROUND);
        this.setLayout(new BorderLayout());

        centermainSub = new CenterMainSubPanel(null);
        centermainSub.setLayout(new BoxLayout(centermainSub, BoxLayout.LINE_AXIS));
        centermainSub.setBackground(GuiConstants.MAIN_BACKGROUND);
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
// START INKLAPBAAR MENU 
         window = centermainSub; // alternatief
         ip = new IconizedPanel(TextMapper.getText("Menu"));
if(iconizer) {
		MAIN_BORDER = BorderFactory.createEmptyBorder(0, 0, 0, 0);
     	setBorder(MAIN_BORDER);
	
    	ip.setMaximumSize(new Dimension(150-1, Short.MAX_VALUE));
    	ip.getIcon().setFont(new Font("Arial", Font.BOLD, 12));
		Image img;
		img = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_BGIMAGE_ICON);
		Border border = new DWOBorder(img, GuiConstants.GUI_INSETS_ICON, GuiConstants.GUI_9PATCH_ICON);
		ip.setIconBorder(border);
		window = new JPanel(new BorderLayout());
        window.setOpaque(false);
		Image menuimg = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_BGIMAGE_MENU);
		Border borderm = new DWOBorder(menuimg, GuiConstants.GUI_INSETS_MENU, GuiConstants.GUI_9PATCH_MENU);
		window.setBorder(borderm);
        Box header = Box.createHorizontalBox();
        header.add(Box.createHorizontalGlue());
        JButton btn = new JButton(ip.getCloseAction());
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        header.add(btn);
        ip.setWindow(window);
        window.add(header, BorderLayout.NORTH);
        centermainSub.add(ip);
}// END       
        loadMenu();
        showMenu = true;
if(!iconizer)
		centermainSub.add(RAND);
if(iconizer)		
{	

		if(User.getCurrentUser() instanceof Teacher)
			tree = ModuleTreePanel.newInstance(GuiCreator.instance().dwo);
		else 
			tree = ModuleTreePanel.newStudentInstance(GuiCreator.instance().getDWO());
		
		
		ip2= tree.getIP();
		ip2.setIconBorder(ip.getIconBorder());
		Image imgs = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_BGIMAGE_SCO);
		Insets inset2 = (Insets) GuiConstants.GUI_INSETS_SCO.clone();
		scoBorder = new DWOBorder(imgs, GuiConstants.GUI_INSETS_SCO, GuiConstants.GUI_9PATCH_SCO);
		tree.add(new Mover(inset2.right), BorderLayout.EAST);
		inset2.right = 0; 
		DWOBorder treeBorder = new DWOBorder(imgs, inset2, GuiConstants.GUI_9PATCH_SCO);
		tree.setBorder(treeBorder);
		ip2.setMaximumSize(new Dimension(150-1, Short.MAX_VALUE));
		centermainSub.add(ip2);
		tree.setCenterPanel(this);
}
		
		
        sp = new JScrollPane();
        //spe = new Panel(new BorderLayout());
		//sp.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        sp.setViewportBorder(null);
        sp.setBorder(null);
        sp.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);
        //spe.add(sp);
        spe = sp; // alles is nu jpanel
        centermainSub.add(spe);
if(iconizer)
	{   
		sp.setBorder(scoBorder);
    }
// Dit moet eigenlijk "NA" de constructor van mainpanel. mp en guicreator variabelen zijn nog niet gezet.
		mp.center = this; // FIXME DIT WERKT ZO NIET.
		GuiCreator.instance().mainPanel = mp; // FIXME IDEM....
		
		CenterSubPanel csp = GuiCreator.instance().getCourseChoisePanel();
		
		if(GuiConstants.DEEP_LINK)
			loadTotal(csp);
//		else
//        if(csp instanceof ScoPanel ) {
//        	//centermainSub.remove(spe);
//        	loadTotal(csp);
//        }
//        else if(csp instanceof CoursePanel ) {
//        	menu.hideMainButton();
//        	//loadCenter(csp);
//        	loadTotal(csp);
//        }
        else loadCenter(csp);

    }

    static boolean isIconizer() {
    	User u = GuiCreator.instance().getUser();
// in productie 'false'
    	return (GuiConstants.GUI_ICONIZED) && u.hasIconizer();
    }

	/**
     * Returns the current MainPanel.
     * 
     * @return The current mainpanel
     */
    public MainPanel getMainPanel() {
        return mainPanel;
    }

    public void reset() {
    	if(tree != null)
    		tree.setStrategy(null);
    }
    
    /**
     * Loads a panel in the center. The menu is showed on the left side.
     * 
     * @param panel The centerpanel to load.
     * @see fi.dwo.client.gui.CenterSubPanel
     */
    public void loadCenter(CenterSubPanel panel) {
        
        if(tree != null)
        {
        	tree.select(panel.getUserObject());
        }
        
        
        if(GuiConstants.GUI_IMAGE_BG)
        	setBorder(MAIN_BORDER);
        if (centerSubPanel != null) {
            end();
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

        GuiCreator.instance().setWait();

        centerSubPanel = panel;
        hasStarted = true;
        centerSubPanel.setCenterPanel(this);
        if(GuiConstants.GUI_IMAGE_BG)
        	mainPanel.setGuiImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE));

        /* If we didn't show the menu, show it */
        if (!showMenu) {
            menu.setVisible(true);
            RAND.setVisible(true);
        }

        final Component c = centerSubPanel.getComponent();

        sp.setViewportView(c);
        c.setVisible(true);
        spe.setVisible(true);
        c.invalidate();
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
if(!iconizer)
{    	if(GuiConstants.GUI_IMAGE_BG) // todo tuning
    		setBorder(BorderFactory.createEmptyBorder(10, 7, 8, 7));
    	if(GuiConstants.getDwoProfile()==51 || GuiConstants.getDwoProfile()==27)setBorder(BorderFactory.createEmptyBorder(14, 28, 76, 30));
    	if(GuiConstants.getDwoProfile()==57 || GuiConstants.getDwoProfile()==65)setBorder(BorderFactory.createEmptyBorder(0, 28, 54, 30));
}
	if(tree != null)
	{
		tree.select(panel.getUserObject());
	}


invalidate();
        if (centerSubPanel != null) {
            if(DwoHelper.umpc) {
        	getParent().add(this,0);
        	setLocation(5,-9);
            }
           	end();
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
        centerSubPanel = panel;
        hasStarted = true;
        panel.setCenterPanel(this);
        
    	mainPanel.setGuiImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_SCO));

        /* We don't want to see the menu */
        if (showMenu && !iconizer) {
            RAND.setVisible(false);
            menu.setVisible(false);
            centermainSub.invalidate();
        }
        if(iconizer)
        {
        	centerSubPanel.getComponent().setBorder(scoBorder);
        }
        Component c = centerSubPanel.getComponent();
        centermainSub.add(c); // FIXME hier gebeurt het met een  FLITS 
        c.setVisible(true);
        SwingUtilities.invokeLater(new RequestFocusAST(c));
        c.invalidate();

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
       	super.paint(g);
    	if(!GuiConstants.GUI_IMAGE_BG) {

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
    
    
    private boolean hasStarted = true;
    /**
     * Called when this panel is closes. Can be used to save session-data, for
     * example the sco-data
     *  
     */
    public void end() {
        if (centerSubPanel != null && hasStarted) {
        	hasStarted = false;
            centerSubPanel.end();
            
        }
    }

    /**
     * Loads the menu and shows it.
     */
    public void loadMenu() {
        if (menu != null) {
            window.remove(menu); // was centermainSub
        }

        menu = GuiCreator.instance().getMenuPanel();
        menu.setCenterPanel(this);
        window.add(menu, 0);
        menu.setMaximumSize(new Dimension(150-1, Short.MAX_VALUE));
        menu.setMinimumSize(new Dimension(150-1, 100));
        menu.setPreferredSize(menu.getMinimumSize());
        ip.setIconized(false);
        ip.setVisible(true);
        menu.setVisible(true);
        RAND.setVisible(true);
        centermainSub.invalidate();
        centermainSub.repaint();
    }
    
    public GuestMenuPanel getMenu() {
        return menu;
    }

	public void showClassList() {
		getMenu().showClassList();
	}
	public void hideClassList() {
		getMenu().hideClassList();
	}

	public ResultsModuleIF getUserResultsModule(Course course) {
		return GuiCreator.instance().dwo.getUserResultsModule(course);
	}

// synchronize the ModuleTreePanel
	public void updateCourse(Course course) {
		if(tree != null)
		{
			if(course.isWithChildren())
				tree.updateNodeMap(course);
			else
				tree.updateNode(course);
		}
		centerSubPanel.stateChanged(new ChangeEvent(course));
	}

	public void deleteCourse(Course course) {
		// TODO Auto-generated method stub
		
	}

	public void addCourse(Course course) {
		// TODO Auto-generated method stub
		
	}

	public void setStrategy(SelectStrategy selector) {
		if(tree != null){
			tree.setStrategy(selector);	
			if(selector instanceof NullStrategy)tree.setEnabled(false);
			else tree.setEnabled(true);
		}
	}

	public void updateMap(CourseMap map) {
		if(tree != null)
		{
			tree.updateNodeMap(map);
		}
		centerSubPanel.stateChanged(new ChangeEvent(map));
		
	}
	
	public void select(Object object)
	{
		if(tree != null)
		{
			tree.select(object);
			tree.toSelectedNode();
		} else {
// TODO check all cases of select...
			if(object == ModuleTreePanel.ALLE_MODULES)
				loadCenter(GuiCreator.instance().getCourseChoisePanel());
			else if(object instanceof Course)
			{
				Course course = (Course)object;
				CenterSubPanel cp;
	            if(course.isWithChildren())
	            	cp = GuiCreator.instance().getCourseChoisePanel(course);
	            else
	            	cp = GuiCreator.instance().getCoursePanel(course);
	            DwoIF dwo = GuiCreator.instance().dwo;
// Deeplink modus
	            if(GuiConstants.DEEP_LINK)
	            	loadTotal(cp);
	            else
	            	loadCenter(cp);				
			} else if(object instanceof Sco) {
				Sco sco = (Sco) object;
				CenterSubPanel csp;
				// csp = sco.getScoPanel(GuiCreator.instance().getDWO(), GuiCreator.instance().getUser());
				csp = GuiCreator.instance().getScoPanel(sco);				
				loadTotal(csp);
			}
		}
	}

	public void updateClass(SchoolClass schoolClass) {
		centerSubPanel.stateChanged(new ChangeEvent(schoolClass));
	}

	public void updateSchool(School school) {
		centerSubPanel.stateChanged(new ChangeEvent(school));
	}
	
}