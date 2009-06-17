// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\CoursePanel.java

package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JLabel;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.ResultsModuleIF;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.Teacher;
import fi.dwo.client.domain.UserResultList;
import fi.dwo.client.system.TextMapper;

import fi.beans.tekstobjects.TekstArea;

/**
 * This class is a panel witch shows a list of all the SCO's in the specified Course.
 * @author M.J.B. Kupers
 *  
 */
public class CoursePanel extends Panel implements CenterSubPanel,
        ActionListener {
	
	private static final int MINWIDTH = 600;

	// if endy + bottom > MINHEIGT -> setBOUNDS(...)
    private static final int MINHEIGHT = 480, BOTTOM = 20;

	CenterPanel center;

    private Course course;
    
    private Label scoListHeader;
    
    private TekstArea courseDescription;

    private DwoButton showResultsButton;
    
    private boolean courseView;

	private ImagePanel ip;

	private int startY;

	
    /**
     * Creates a new Course Panel. The CoursePanel shows an overview of all the
     * sco's of the specified Course.
     * 
     * @param course The Course to show.
     */
    public CoursePanel(Course course) {
        this.course = course;
        this.setLayout(null);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setSize(MINWIDTH, MINHEIGHT);
        
        ScoLinkedLabel l;
        FontMetrics fm;
        
        startY = 30;
		String s = course.getDescription();
		courseDescription = new TekstArea();
		courseDescription.setBounds(20,startY,550,0);
		courseDescription.setText(s);
		courseDescription.resize();
		if((s != null) && (!s.trim().equals(""))) 
		{	add(courseDescription);
			startY += courseDescription.getSize().height + 10;
		}

        scoListHeader = new Label(TextMapper.getText(TextMapper.GUICO_SCO_LIST_TITLE));
        scoListHeader.setFont(GuiConstants.SCO_HEADER_TEXT);
        fm = scoListHeader.getFontMetrics(scoListHeader.getFont());
        scoListHeader.setSize(fm.stringWidth(scoListHeader.getText()) + 10, fm.getHeight());
        scoListHeader.setLocation(30, startY);
        add(scoListHeader);
        
        startY += scoListHeader.getSize().height + 10;
        
        Image courseLogo = course.getCourseLogo();
        MediaTracker tr = new MediaTracker(this);
        tr.addImage(courseLogo, 0);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }
        ip = new ImagePanel(courseLogo);
		ip.setLocation(this.getSize().width - ip.getSize().width - 50, startY);
        add(ip);

        int nextY = startY;
        int i;
        for (i = 0; i < course.getScoList().length; i++) {
            l = new ScoLinkedLabel(course.getScoList()[i]);
            l.setForeground(Color.black);
            l.setFont(GuiConstants.SCO_TEXT);
            fm = l.getFontMetrics(l.getFont());
            l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
            l.setLocation(30, nextY);
            l.addActionListener(this);
            this.add(l);
            nextY += l.getSize().height + 3;
        }
        
        /* If the user is a teacher, show a button to go to the results */
        if(GuiCreator.instance().getUser() instanceof Teacher) {
            showResultsButton = new DwoButton(TextMapper.getText(TextMapper.GUIMNU_RESULTS));
            fm = showResultsButton.getFontMetrics(showResultsButton.getFont());
            showResultsButton.setSize(fm.stringWidth(showResultsButton.getLabel()) + 20, fm.getHeight() + 10);
            showResultsButton.setLocation(30, nextY+20);
            showResultsButton.addActionListener(this);
            this.add(showResultsButton);
            
            nextY += showResultsButton.getSize().height + 10;
            
        }
// resize panel.
        if(nextY + BOTTOM > MINHEIGHT)
        	setSize(MINWIDTH, nextY + BOTTOM);   
    }
    
    public void setCourseView(boolean b) {
    	courseView = b;
    }
    
    public boolean getCourseView() {
    	return courseView;
    }

    /**
     * Indicate that another panel is loaded and the connections of this panel
     * must be closed.
     */
    public void end() {
        center.getMenu().showClassList();
        removeButtons();
    }

    /**
     * remove DwoButtons from the panel.
     * refresh() will add them again.
     * @see #refresh()
     */
    private void removeButtons() {
    	Component[] components = getComponents();
    	for (int i = 0; i < components.length; i++) {
			Component comp = components[i];
			if(comp instanceof ResultScoreButton)
				remove(comp);
		}
    }
    
    /**
     * Refresh the sco view. Update the buttons width scores.
     */
    private void refresh() {
    	removeButtons();
        ResultsModuleIF results = GuiCreator.instance().dwo.getUserResultsModule(course);
        UserResultList scoresults = null;
        if(results != null)
        {	
        	Vector v = results.getResults();
        	if(v.size()>0)
        		scoresults = (UserResultList) v.elementAt(0);
        }
        if(scoresults == null)
        	return;
        int ipx = ip.getLocation().x-10-30;
        int nextY = startY;
        int i;
// TODO dit moet veel beter.....
        ScoLinkedLabel l;
        FontMetrics fm;
        for (i = 0; i < course.getScoList().length; i++) {
            l = new ScoLinkedLabel(course.getScoList()[i]);
            l.setForeground(Color.black);
            l.setFont(GuiConstants.SCO_TEXT);
            fm = l.getFontMetrics(l.getFont());
            l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
            	Component button = scoresults.getResultScore()[i].getGui();
            	//System.out.println(i + ": " + scoresults.getResultScore()[i].getScore());
            	button.setSize(fm.stringWidth("100 %")+30, l.getSize().height);
            	button.setLocation(ipx - button.getSize().width, nextY-5);
            	((ResultScoreButton)button).setBarMode();
            	
            	this.add(button);
            nextY += l.getSize().height + 3;
        }
    }
    
    
    /**
     * Sets the centerpanel to communicate with.
     * 
     * @param centerPanel The centerPanel to communicate with.
     */
    public void setCenterPanel(CenterPanel centerPanel) {
        center = centerPanel;
        center.getMenu().hideClassList();
        refresh();
    }

    /**
     * Returns a Panel that can function as a header panel.
     * 
     * @return A panel that can function as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    public Container getHeaderPanel() {
        Panel p = new BorderedPanel(null);
        if(GuiConstants.GUI_IMAGE_BG) {
        	p = new BorderedPanel(null,0)
            {  	public void paint(Graphics g)
	            {	Point p = DwoHelper.getComponentLocation(this);
	            	g.drawImage(DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.GUI_IMAGE_COURSE),-p.x,-p.y,null);
	            	super.paint(g);
	            }
	        };
        }
        p.setBackground(GuiConstants.MAIN_BACKGROUND);
        p.setBounds(181, 20, 449, 71);
        this.add(p);

        Image courseLogo;
        courseLogo = course.getCourseLogo();
        MediaTracker tr = new MediaTracker(this);
        tr.addImage(courseLogo, 0);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }
        courseLogo = courseLogo.getScaledInstance(courseLogo.getWidth(null)/2 , courseLogo.getHeight(null)/2 , Image.SCALE_SMOOTH);
        tr.addImage(courseLogo, 0);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }
        ImagePanel ip = new ImagePanel(courseLogo);
        //ip.setLocation(p.getSize().width - ip.getSize().width - 2 , 2);
        ip.setLocation(10, (p.getSize().height / 2) - (ip.getSize().height / 2));
        p.add(ip, 0);
        if(GuiConstants.GUI_IMAGE_BG) p.remove(ip);

        /* My Course-Label */
        JLabel l = new JLabel(course.getName());
        l.setOpaque(false);
        l.setFont(GuiConstants.HEADER_TEXT);
        FontMetrics fm = l.getFontMetrics(l.getFont());
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());

        /* Scale the fontsize */
        Font f = null;
        int maxHeight = (int) p.getSize().height - 4;
        int maxWidth = p.getSize().width - ip.getSize().width + ip.getLocation().x - 40;
        
        if(GuiConstants.GUI_IMAGE_BG) maxHeight = 26;

        /* Scale the fontsize */
        while ((l.getSize().width > maxWidth)
                || (l.getSize().height > maxHeight)) {
            f = l.getFont();
            l.setFont(new Font(f.getName(), f.getStyle(), f.getSize() - 1));
            fm = l.getFontMetrics(l.getFont());
            l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        }
        l.setLocation(ip.getLocation().x + ip.getSize().width + 20, (p.getSize().height / 2) - (l.getSize().height / 2));
        if(GuiConstants.GUI_IMAGE_BG) l.setLocation(ip.getLocation().x + ip.getSize().width + 20, 42);
        //l.setLocation(30, (p.getSize().height / 2) - (l.getSize().height / 2));
        p.add(l);

        return p;
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof ScoLinkedLabel) {
            Sco sco = ((ScoLinkedLabel) e.getSource()).getSco();
            GuiCreator.instance().setWait();
            final Sco s = sco;
            Thread thread = new Thread() {	
                public void run() {	
                    CenterSubPanel csp = GuiCreator.instance().getScoPanel(s);
                    if(csp != null) {
                    	s.setLessonMode(Sco.NORMAL);
                        center.loadTotal(csp);
                    }
                    GuiCreator.instance().setReady();
				}
			};
            thread.start();/**/
        } else if (e.getSource() == showResultsButton) {
            CenterSubPanel cp = GuiCreator.instance().getResultPanel(course);
            center.loadCenter(cp);
        }
        

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