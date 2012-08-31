// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\CourseChoisePanel.java

package fi.dwo.client.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.text.JTextComponent;


import fi.beans.mathkit.JMathPane;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.domain.Descriptor;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoProfile;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.TextMapper;
import fi.wiskopdr.WiskOpdr;
import fi.wiskopdr.WiskOpdrPanel;

/**
 * This class is a panel where the user gets a overview of the different courses.
 * @author M.J.B. Kupers
 *  
 */
public class CourseChoisePanel extends JPanel implements ActionListener,
        CenterSubPanel, Scrollable {
    private CenterPanel center;

    private int NR_COLUMNS = 4;
    
    private JTextComponent profileTextArea;
    private WiskOpdrPanel wiskOpdrPanel;
    private Dimension unit = new Dimension(1,1);
    private Descriptor dwoProfile;

	private Object userObject;

    /**
	 * Creates a new instance of a CourseChoisePanel This panel gives an
	 * overview of all the available courses to the user.
	 * FIXME ModuleTreePanel moet worden GuiConstants!
	 */
	private CourseChoisePanel(DwoProfile dwoProfile) {
		this(dwoProfile, GuiCreator.instance().getCourseList(), ModuleTreePanel.ALLE_MODULES);
	}

	public CourseChoisePanel(Descriptor descriptor, Object userObject) {
		this(descriptor, descriptor.getChildren(), userObject);
	}
	
	public static CourseChoisePanel newInstance() {
		if(CenterPanel.isIconizer())
			return new CourseChoisePanel(ModuleTreePanel.TOP_LEVEL, ModuleTreePanel.ALLE_MODULES);
		return new CourseChoisePanel(GuiCreator.instance().dwo.getDwoProfile(), ModuleTreePanel.ALLE_MODULES);
	}
	
	
	public Object getUserObject() {
		return userObject;
	}

	/**
     * Creates a new instance of a CourseChoisePanel This panel gives an
     * overview of all the available courses to the user.
     * @param courseList TODO
     */
    public CourseChoisePanel(Descriptor dwoProfile, CourseMap[] courseList, Object userObject) {
        super();
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        setLayout(new BorderLayout());

        this.dwoProfile = dwoProfile;
        this.userObject = userObject;
        
        initialize(dwoProfile, courseList);
    }

	private void initialize(Descriptor dwoProfile, CourseMap[] courseList) {
		CourseMap[] courses = courseList;

        //Panel ph;
        //ph = new Panel(null);
        
        
        String s = dwoProfile.getText();
        
        if((s != null) && (!s.trim().equals(""))){
        	if(s.startsWith("<html>")) {	
        		URL base = DwoHelper.getURL(".");
        		profileTextArea = new JMathPane(base);
            } 
            else if(s.startsWith("H4sIAAAAAA")) {
            	wiskOpdrPanel = WiskOpdr.getWiskOpdrPanel(s);
            	if(!DwoHelper.isApplication())wiskOpdrPanel.setJSObjectOwner(DwoHelper.getApplet());
                wiskOpdrPanel.setLocation(20,20);
                JPanel wrapPanel = new JPanel();
                wrapPanel.setOpaque(false);
                wrapPanel.setLayout(null);
                wrapPanel.setPreferredSize(new Dimension(wiskOpdrPanel.getWidth()+40,(wiskOpdrPanel.getHeight()+40)));
                wrapPanel.add(wiskOpdrPanel);
                add(wrapPanel, BorderLayout.NORTH);
            }
            else {	
            	JTextArea area = new JTextArea();
                area.setLineWrap(true);
                area.setWrapStyleWord(true);
                area.setColumns(20);
            	profileTextArea = area;
            }
        	
        	if(profileTextArea!=null) {
	            profileTextArea.setFont(new Font("SansSerif",Font.PLAIN,13));
	            profileTextArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	            profileTextArea.setEditable(false);
	            profileTextArea.setBounds(20,20,600,110);
	            profileTextArea.setOpaque(false);
	            profileTextArea.setText(s);
	            add(profileTextArea, BorderLayout.NORTH);
        	}
        	
		}
        
		//ph.setBounds(0,0,200, profileTextArea.getSize().height+40);
        //profileTextArea.setMinimumSize(new Dimension(100, 100));
        //profileTextArea.setPreferredSize(profileTextArea.getMinimumSize());
       //ph.setPreferredSize(getSize());
       // ph.setMaximumSize(getSize());

        JPanel pp = new JPanel();
        pp.setBackground(GuiConstants.MAIN_BACKGROUND);
        pp.setOpaque(true);
        pp.setDoubleBuffered(false);
        add(pp);
        
        GridLayout gl = new GridLayout();
        gl.setColumns(NR_COLUMNS);
        gl.setRows((courses.length / NR_COLUMNS) + 1);
        pp.setLayout(gl);
        
        CourseIcon courseIcon = null;
       
        
        JPanel p = new JPanel(null);
        p.setOpaque(false);
        p.setDoubleBuffered(false);
        p.setSize(0, 10);
        
        int maxWidth = 10;
        int maxHeight = 10;

        for (int i = 0; i < courses.length; i++) {
            p = new JPanel(new FlowLayout());
            p.setOpaque(false);
            p.setDoubleBuffered(false);
            courseIcon = new CourseIcon((Course) courses[i]);
            courseIcon.addActionListener(this);
            if(courseIcon.getSize().width > maxWidth) {
                maxWidth = courseIcon.getSize().width; 
            }
            if(courseIcon.getSize().height > maxHeight) {
                maxHeight = courseIcon.getSize().height; 
            }
            courseIcon.setLocation(0, 0);
            p.add(courseIcon);
            pp.add(p);
            
        }

        if (courseIcon != null) {
        	int descriptionHeight = 0;
        	if(profileTextArea!=null)
        		descriptionHeight = profileTextArea.getSize().height;
        	else if(wiskOpdrPanel!=null)
        		descriptionHeight = wiskOpdrPanel.getSize().height;
        	
        	if((maxWidth * NR_COLUMNS) < 600) {
            	
	            this.setSize(600, maxHeight * gl.getRows() + descriptionHeight);                
            } 
            else {
	            this.setSize(maxWidth * NR_COLUMNS, maxHeight
	                    * gl.getRows() + descriptionHeight);
            }
        }
        //profileTextArea.invalidate();
        //doLayout();
// calculate preferred size and keep it that way!
        //setSize(GuiConstants.CENTER_WIDTH, GuiConstants.CENTER_HEIGHT);
        Dimension pref = getPreferredSize();
        setPreferredSize(pref);
        if(courseIcon != null)
        	pref.width = maxWidth * NR_COLUMNS;
        unit.width = maxWidth/2;
        unit.height = maxHeight/4;
        setMinimumSize(pref);
	}
    
  
   public void paint(Graphics g)
   {
	   //validate();
	   super.paint(g);
   }
    
    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof CourseIcon) {
            Course course = ((CourseIcon) e.getSource()).getCourse();
//            CenterSubPanel cp;
//            if(course.isWithChildren())
//            	cp = GuiCreator.instance().getCourseChoisePanel(course);
//            else
//            	cp = GuiCreator.instance().getCoursePanel(course);
//            center.loadCenter(cp);
            center.select(course);
        }
    }

    /**
     * Indicate that another panel is loaded and the connections of this panel
     * must be closed.
     */
    public void end() {

    }

    
    /**
     * Returns a Panel that can function as a header panel.
     * 
     * @return A panel that can function as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    public Component getHeaderPanel() {
        HeaderPanel p; //  = new BorderedPanel(null); 

        p = new HeaderPanel(TextMapper.getText(TextMapper.GUIM_MAIN_MENU));
        String s = dwoProfile.getText();
        if(s != null && s.trim().length()>0 || dwoProfile instanceof Course || CenterPanel.isIconizer())
        	p = new HeaderPanel(dwoProfile.getHeader(), true); // wim: Wat wordt hier bedoeld?
        
        p.setButtonBox(GuiCreator.instance().getButtonBox(this));
        
        
        return p;
    }

    

    /**
     * Sets the centerpanel to communicate with.
     * 
     * @param centerPanel The centerPanel to communicate with.
     */
    public void setCenterPanel(CenterPanel centerPanel) {
        center = centerPanel;
    }

    /**
     * Returns the current object, as the object to add to a gui.
     * 
     * @return the current object.
     * @see fi.dwo.client.gui.CenterSubPanel#getComponent()
     */
    public JComponent getComponent() {
        return this;
    }


	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		if(orientation == SwingConstants.VERTICAL)			
		{
			return Math.max(unit.height,visibleRect.height-unit.height); // 1 page
		}
		else 
			return visibleRect.width;
	}
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	public boolean getScrollableTracksViewportWidth() {
		return getParent().getWidth() > getMinimumSize().width;
	}
// onder windows xp, een mousewheel click is 3 units.	
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		if(orientation == SwingConstants.VERTICAL)			
		{	return unit.height; // 1 line
		} else 
			return unit.width;
	}

	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if(source instanceof CourseMap)
			source = ((CourseMap) source).getUserObject();
		if(source == userObject||userObject == ModuleTreePanel.ALLE_MODULES)
		{
			//System.out.println("stateChanged(" + e + ")");
			removeAll();
			setPreferredSize(null);
			setMinimumSize(null);
			invalidate();
			initialize(dwoProfile, dwoProfile.getChildren());
			validate();
			repaint();
		}
		
		
		
	}
}