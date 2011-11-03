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
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.text.JTextComponent;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.ResultsModuleIF;
import fi.dwo.client.domain.Sco;
import fi.dwo.client.domain.Teacher;
import fi.dwo.client.domain.UserResultList;
import fi.dwo.client.system.TextMapper;

import fi.beans.mathkit.JMathPane;
import fi.beans.tekstobjects.TekstArea;

/**
 * This class is a panel witch shows a list of all the SCO's in the specified Course.
 * @author M.J.B. Kupers
 *  
 */
public class CoursePanel extends JPanel implements CenterSubPanel,
        ActionListener {
	
	private static final int MINWIDTH = 600;

	// if endy + bottom > MINHEIGT -> setBOUNDS(...)
    private static final int MINHEIGHT = (GuiConstants.getDwoProfile()==51 || GuiConstants.getDwoProfile()==27) ? 380 : 470, BOTTOM = 20;

	CourseContainer center;
    private String lessonMode = Sco.NORMAL;

    Course course;
    
    private JLabel scoListHeader;
    
    private JTextArea courseDescription;
    private JTextComponent courseDescriptionHTML;

    private JButton showResultsButton;
    
    private boolean courseView;

	private JLabel ip;

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
		courseDescription = new JTextArea();
		boolean htmlMode = false;
		
		if((s != null) && (!s.trim().equals(""))) 
		{
			if(s.length()>5 && s.substring(0,6).equals("<html>"))
			{	htmlMode=true;
				courseDescriptionHTML = new JMathPane(); // was JLABEL
				courseDescriptionHTML.setText(s);
				courseDescriptionHTML.setEditable(false);
				add(courseDescriptionHTML);
				courseDescriptionHTML.setFont(new Font("SansSerif", Font.PLAIN, 13));
				courseDescriptionHTML.setOpaque(false);
				courseDescriptionHTML.setBounds(20,startY,550,110);
				courseDescriptionHTML.setSize(courseDescriptionHTML.getPreferredSize());
				startY += courseDescriptionHTML.getHeight() + 10;
			}
			else
			{	add(courseDescription);
				courseDescription.setFont(new Font("SansSerif", Font.PLAIN, 13));
				courseDescription.setOpaque(false);
				courseDescription.setLineWrap(true);
				courseDescription.setEditable(false);
				courseDescription.setWrapStyleWord(true);
				courseDescription.setBounds(20,startY,550,110);
				courseDescription.setText(s);
				courseDescription.setSize(courseDescription.getPreferredSize());
				startY += courseDescription.getHeight() + 10;
			}
		}

        scoListHeader = new JLabel(TextMapper.getText(TextMapper.GUICO_SCO_LIST_TITLE));
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
        ip = new JLabel(new ImageIcon(courseLogo));
        ip.setSize(courseLogo.getWidth(null), courseLogo.getHeight(null));
		ip.setLocation(this.getSize().width - ip.getSize().width - 50, startY);
        if(!htmlMode)add(ip);

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
            showResultsButton = new JButton(TextMapper.getText(TextMapper.GUIMNU_RESULTS));
            showResultsButton.setLocation(30, nextY+20);
            showResultsButton.addActionListener(this);
            this.add(showResultsButton);
            showResultsButton.setSize(showResultsButton.getPreferredSize());
            
            nextY += showResultsButton.getSize().height + 10;
            
        }
// resize panel.
        if(nextY + BOTTOM > MINHEIGHT)
        	setSize(MINWIDTH, nextY + BOTTOM);   
        
        setPreferredSize(getSize());
    }
    
    public void setCourseView(boolean b) {
    	// if(b) then deeplink, geen beheerknop?
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
        center.showClassList();
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
        //ResultsModuleIF results = GuiCreator.instance().dwo.getUserResultsModule(course);
        ResultsModuleIF results = center.getUserResultsModule(course);
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
            Sco sco = course.getScoList()[i];
			l = new ScoLinkedLabel(sco);
            l.setForeground(Color.black);
            l.setFont(GuiConstants.SCO_TEXT);
            fm = l.getFontMetrics(l.getFont());
            l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
            	Component button = scoresults.getResultScore()[i].getGui();
            	//System.out.println(i + ": " + scoresults.getResultScore()[i].getScore());
            	button.setSize(fm.stringWidth("100 %")+30, l.getSize().height);
            	button.setLocation(ipx - button.getSize().width, nextY-5);
            	((ResultScoreButton)button).setBarMode();
            	button.setVisible(sco.isShowScore());
            	this.add(button);
            nextY += l.getSize().height + 3;
        }
    }
    
    
    /**
     * Sets the centerpanel to communicate with.
     * 
     * @param centerPanel The centerPanel to communicate with.
     */
    public void setCenterPanel(CourseContainer centerPanel) {
        center = centerPanel;
        center.hideClassList();
        refresh();
    }

    /**
     * Returns a Panel that can function as a header panel.
     * 
     * @return A panel that can function as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    public Component getHeaderPanel() {   
    	
    	HeaderPanel hp = new HeaderPanel(course.getName(), true);
    	if (!GuiConstants.GUI_IMAGE_BG)
    	{
    	    hp.setHorizontalAlignment(SwingConstants.LEFT);
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
    		ImageIcon icon = new ImageIcon(courseLogo);
            hp.setIcon(icon);
            hp.setIconTextGap(20); 
            Border oldBorder = hp.getBorder();
            Border gapBorder = BorderFactory.createEmptyBorder(0, 10, 0, 0);
            hp.setBorder(BorderFactory.createCompoundBorder(oldBorder, gapBorder));
    	}
    	if(!getCourseView())
    		hp.setButtonBox(GuiCreator.instance().getButtonBox(this));
    	return hp; 
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
                    	s.setLessonMode(getLessonMode());
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
    public JComponent getComponent() {
        return this;
    }

	public void setCenterPanel(CenterPanel centerPanel) {
        center = centerPanel;
        center.hideClassList();
        refresh();
	}

	/**
	 * @return the lessonMode
	 */
	String getLessonMode() {
		return lessonMode;
	}

	/**
	 * @param lessonMode the lessonMode to set
	 */
	void setLessonMode(String lessonMode) {
		this.lessonMode = lessonMode;
		if (showResultsButton != null)
		{
				showResultsButton.setVisible(lessonMode != Sco.BROWSE);
		}
			
	}

	public Object getUserObject() {
		return course;
	}

	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}
}