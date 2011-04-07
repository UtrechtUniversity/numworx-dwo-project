// Source file: C:\\parameters\\fi\\dwo\\client\\gui\\CourseNameDialog.java

package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.system.TextMapper;

/**
 * This is a dialog for editing the course name and description.
 * @author M.J.B. Kupers
 *
 */
public class CourseNameDialog extends JDialog implements ActionListener {

    private String courseName;
    private String courseDescription;
    private boolean confirmed;
    
    
    
    private JTextField name;
    private JTextArea description;
    private JCheckBox showScore;
    
    private JButton okButton;
    private JButton cancelButton;
	private JScrollPane pane;
    
    CourseNameDialog(Component owner, String windowTitle, int courseID, String courseName, String courseDescription, String courseNameLabel, String courseDescriptionLabel) {
        super(DwoHelper.getFrameForComponent(owner), windowTitle, true);
        //this.setLayout(new FlowLayout());
        //this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        confirmed = false;
        JPanel contentPane = new JPanel(null);
        contentPane.setBackground(GuiConstants.MAIN_BACKGROUND);
        setContentPane(contentPane);
        //add(contentPane);
        JLabel l;
        FontMetrics fm;
        
        /* CourseID label */
        l = new JLabel("" + courseID);
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 5);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setVisible(false);
        contentPane.add(l);
        l.setVisible(true);
        
        /* Coursename label */
        l = new JLabel(TextMapper.getText(courseNameLabel) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 30);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setVisible(false);
        contentPane.add(l);
        l.setVisible(true);

        /* Coursename field */
        name = new JTextField(courseName);
        name.setBounds(150, 28, 200, 20);
        name.setVisible(false);
        contentPane.add(name);
        name.setVisible(true);
        
        /* Coursedescription label */
        l = new JLabel(TextMapper.getText(courseDescriptionLabel) + ":");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 55);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setVisible(false);
        contentPane.add(l);
        l.setVisible(true);

        /* Coursedescription field */
        description = new JTextArea(courseDescription, 0, 0);//, TextArea.SCROLLBARS_VERTICAL_ONLY);
        pane = new JScrollPane(description, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setBounds(150, 53, 200, 100);
        contentPane.add(pane);
        
        contentPane.setPreferredSize(new Dimension(360, 220));
        contentPane.setSize(contentPane.getPreferredSize());
        /* Register button */
        okButton = new JButton(TextMapper.getText(TextMapper.BTN_OK));//, GuiConstants.MAIN_BACKGROUND);
        fm = okButton.getFontMetrics(okButton.getFont());
        okButton.setSize(okButton.getPreferredSize());
        okButton.addActionListener(this);

        /* Reset button */
        cancelButton = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));//, GuiConstants.MAIN_BACKGROUND);
        fm = cancelButton.getFontMetrics(cancelButton.getFont());
        cancelButton.setSize(cancelButton.getPreferredSize());
        cancelButton.addActionListener(this);

        okButton.setLocation((contentPane.getSize().width / 2)
                - ((okButton.getSize().width
                        + cancelButton.getSize().width + 5) / 2), 163);
        contentPane.add(okButton);

        cancelButton.setLocation((contentPane.getSize().width / 2)
                - ((okButton.getSize().width
                        + cancelButton.getSize().width + 5) / 2)
                + okButton.getSize().width + 5, 163);
        contentPane.add(cancelButton);
        owner = DwoHelper.getApplet(); // centreer t.o.v. dwo applet
        Point p = owner != null ? owner.getLocationOnScreen() : new Point(0, 0);
        Dimension parentSize = owner != null ? owner.getSize()
                : Toolkit.getDefaultToolkit().getScreenSize();
        pack();
        Dimension mySize = getSize();
        int x = p.x + (parentSize.width - mySize.width) / 2;
        int y = p.y + (parentSize.height - mySize.height) / 2;
        setLocation(x, y);
        //this.addWindowListener(this);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
     }
    
    public static Course addCourse() {
        return addCourse(null);
    }

    /**
     * @return fi.dwo.client.domain.Course
     */
    public static Course addCourse(Component owner) {
        CourseNameDialog cnd = new CourseNameDialog(owner, TextMapper.getText(TextMapper.GUICDLG_TTL_ADD_COURSE), 0, "", "", TextMapper.GUICDLG_COURSE_NAME, TextMapper.GUICDLG_COURSE_DESCRIPTION);
        cnd.show();
        if(cnd.isConfirmed()) {
            Course c = GuiCreator.instance().addCourse(cnd.getCourseName(), cnd.getCourseDescription());
            return c;
        } else { //action canceled
            return null;
        }
    }
    
    public static boolean editCourse(Course course) {
        return editCourse(course, DwoHelper.getApplet());
    }

    /**
     * @param course
     * @return boolean
     */
    public static boolean editCourse(Course course, Component owner) {
        CourseNameDialog cnd = new CourseNameDialog(owner, TextMapper.getText(TextMapper.GUICDLG_TTL_EDIT_COURSE), course.getID(), course.getName(), course.getDescription(), TextMapper.GUICDLG_COURSE_NAME, TextMapper.GUICDLG_COURSE_DESCRIPTION);
        cnd.show();
        if(cnd.isConfirmed()) {
            String oldName = course.getName();
            String oldDescription = course.getDescription();
            course.setName(cnd.getCourseName());
            course.setDescription(cnd.getCourseDescription());
            boolean result = GuiCreator.instance().updateCourse(course);
            if(!result) { //something went wrong. Reset the data.
                course.setName(oldName);
                course.setDescription(oldDescription);
            }
            
            return result;
        } else { //action canceled
            return false;
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == cancelButton) {
            this.setVisible(false);
        } else if (e.getSource() == okButton) {
            courseName = name.getText();
            courseDescription = description.getText();
            confirmed = true;
            this.setVisible(false);
        }
        
    }

    /**
     * @return Returns the confirmed.
     */
    public boolean isConfirmed() {
        return confirmed;
    }
    /**
     * @return Returns the courseDescription.
     */
    public String getCourseDescription() {
        return courseDescription;
    }
    
    public String getScoDescription() {
    	return courseDescription;
    }
    /**
     * @return Returns the courseName.
     */
    public String getCourseName() {
        return courseName;
    }
    public String getScoName() {
    	return courseName;
    }
    
    public boolean isShowScore()
    {
    	return showScore.isSelected();
    }
    
    public void setShowScore(boolean b)
    {
    	if(showScore == null)
    	{
    		showScore = new JCheckBox("Leerlingen zien hun score");
    		Container content = getContentPane();
    		showScore.setBackground(GuiConstants.MAIN_BACKGROUND);
    		showScore.setFont(GuiConstants.NORMAL_TEXT);
    		content.add(showScore);
    		int h = showScore.getPreferredSize().height;
    		int th = pane.getHeight();
    		int tw = pane.getWidth();
    		int x =  pane.getX();
    		int y =  pane.getY();
    		pane.setSize(tw, th-h);
    		showScore.setBounds(x, y+th-h, tw, h);
    	}
    	showScore.setSelected(b);
    }
}