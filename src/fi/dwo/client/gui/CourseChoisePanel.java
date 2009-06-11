// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\CourseChoisePanel.java

package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import fi.beans.tekstobjects.TekstArea;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoProfile;
import fi.dwo.client.system.TextMapper;

/**
 * This class is a panel where the user gets a overview of the different courses.
 * @author M.J.B. Kupers
 *  
 */
public class CourseChoisePanel extends Panel implements ActionListener,
        CenterSubPanel {
    private CenterPanel center;

    private int NR_COLUMNS = 4;
    
    private TekstArea profileTextArea;
    
    private DwoProfile dwoProfile;

    /**
     * Creates a new instance of a CourseChoisePanel This panel gives an
     * overview of all the available courses to the user.
     */
    public CourseChoisePanel(DwoProfile dwoProfile) {
        super();
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        setLayout(new BorderLayout());

        this.dwoProfile = dwoProfile;
        
        Course[] courses = GuiCreator.instance().getCourseList();

        Panel ph;
        ph = new Panel(null);
        ph.setLayout(null);
        
        
        String s = dwoProfile.getText();
        profileTextArea = new TekstArea();
        profileTextArea.setBounds(20,20,580,0);
        profileTextArea.setText(s);
        profileTextArea.resize();
		if((s != null) && (!s.trim().equals(""))) 
		{	ph.add(profileTextArea);
			ph.setBounds(0,0,600, profileTextArea.getSize().height+40);
			add(ph,BorderLayout.NORTH);
		}
		ph.setBounds(0,0,600, profileTextArea.getSize().height+40);
        
        Panel pp = new Panel();
        add(pp);
        
        GridLayout gl = new GridLayout();
        gl.setColumns(NR_COLUMNS);
        gl.setRows((courses.length / NR_COLUMNS) + 1);
        pp.setLayout(gl);

        CourseIcon courseIcon = null;
       
        
        Panel p = new Panel(null);
        p.setSize(0, 10);
        
        int maxWidth = 0;
        int maxHeight = 0;

        for (int i = 0; i < courses.length; i++) {
            p = new Panel(new FlowLayout());
            courseIcon = new CourseIcon(courses[i]);
            courseIcon.addActionListener(this);
            if(courseIcon.getSize().width > maxWidth) {
                maxWidth = courseIcon.getSize().width; 
            }
            if(courseIcon.getSize().height > maxHeight) {
                maxHeight = courseIcon.getSize().height; 
            }
            courseIcon.setLocation(0, 0);
            courseIcon.setVisible(false);
            p.add(courseIcon);
            courseIcon.setVisible(true);
            p.setVisible(false);
            pp.add(p);
            p.setVisible(true);
        }

        if (courseIcon != null) {
            if((maxWidth * NR_COLUMNS) < 600) {
	            this.setSize(600, maxHeight * gl.getRows() + ph.getSize().height);                
            } else {
	            this.setSize(maxWidth * NR_COLUMNS, maxHeight
	                    * gl.getRows() + ph.getSize().height);
            }
        }

        repaint();
    }

   
    
    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof CourseIcon) {
            CenterSubPanel cp = GuiCreator.instance().getCoursePanel(((CourseIcon) e.getSource()).getCourse());
            center.loadCenter(cp);
        }
    }

    /**
     * Indicate that another panel is loaded and the connections of this panel
     * must be closed.
     */
    public void end() {

    }

    
    /**
     * Returns a Panel that can functionate as a header panel.
     * 
     * @return A panel that can functionate as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    public Panel getHeaderPanel() {
        Panel p = new BorderedPanel(null); 
        if(GuiConstants.GUI_IMAGE_BG) {
        	p = new Panel()
            {
	        	public void paint(Graphics g)
	            {	Point p = DwoHelper.getComponentLocation(this);
	            	System.out.println(""+p);
	            	g.drawImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE),-p.x,-p.y,null);
	            	super.paint(g);
	            }
	        };
        }
        p.setBackground(GuiConstants.MAIN_BACKGROUND);
        p.setBounds(181, 20, 449, 71);
//        this.add(p);

        /* My Profile-Label */
       
        JLabel l = new JLabel(TextMapper.getText(TextMapper.GUIM_MAIN_MENU));
        l.setOpaque(false);
        String s = dwoProfile.getText();
        l.setFont(GuiConstants.HEADER_TEXT);
        if((s != null) && (!s.trim().equals(""))) l.setText(dwoProfile.getDescription());
        FontMetrics fm = l.getFontMetrics(l.getFont());
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setLocation((p.getSize().width / 2) - (l.getSize().width / 2), (p.getSize().height / 2)
                - (l.getSize().height / 2));
        /*l.setVisible(false);
        p.add(l);
        l.setVisible(true);*/
        
        /* Scale the fontsize */
        
        if((s != null) && (!s.trim().equals(""))) 
        {	l.setText(dwoProfile.getDescription());
	    	Font f = null;
	        int maxHeight = (int) p.getSize().height - 4;
	        int maxWidth = p.getSize().width - 40 ;
	        
	        if(GuiConstants.GUI_IMAGE_BG) maxHeight = 26;
	        
	        while ((l.getSize().width > maxWidth)
	                || (l.getSize().height > maxHeight)) {
	            f = l.getFont();
	            l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() - 1));
	            fm = l.getFontMetrics(l.getFont());
	            l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
	            
	        }
	        l.setLocation(30, (p.getSize().height / 2) - (l.getSize().height / 2));
	        
	    }
        
        p.add(l);
        if(GuiConstants.GUI_IMAGE_BG) 
        {	p.remove(l);
        	Font f = l.getFont();
            l.setFont(new Font(f.getName(), f.getStyle(), 26));
	        fm = l.getFontMetrics(l.getFont());
	        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
	        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
	        l.setLocation(60, 38);
	        p.add(l);
        }
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
    public Component getComponent() {
        return this;
    }
}