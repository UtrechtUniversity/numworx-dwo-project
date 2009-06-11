// Source file: C:\\parameters\\fi\\dwo\\client\\gui\\ScoNameDialog.java

package fi.dwo.client.gui;

import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Point;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;


import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.School;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.TextMapper;
import fi.dwo.client.system.SchoolException;

/**
 * This is a dialog for editing the SCO name and description.
 * 
 * @author M.J.B. Kupers
 *
 */
public class AddSchoolDialog extends Dialog implements ActionListener,
        WindowListener {

    private static final Object ZERO = new Integer(0);

	private String schoolName;
    
    private String schoolLogin;
    
   	private String studentPasswd;
   
   	private String teacherPasswd;

    private boolean confirmed;

    private Component schoolNameField;
    
    private TextField schoolLoginField;

    private TextField studentPasswdField;
    
    private TextField teacherPasswdField;

    private DwoButton okButton;

    private DwoButton cancelButton;

    public AddSchoolDialog(Component owner, String windowTitle, String schoolName, String schoolLogin,
            String studentPasswd, String teacherPasswd) {
        super(DwoHelper.getFrameForComponent(owner),
                windowTitle, true);
        this.setLayout(null);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.schoolName = schoolName;
        this.schoolName = schoolLogin;
        this.studentPasswd = studentPasswd;
        this.teacherPasswd = teacherPasswd;
        confirmed = false;

        Label l;
        FontMetrics fm;

        /* schoolName label */
        l = new Label("Naam van de school");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 30);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setVisible(false);
        this.add(l);
        l.setVisible(true);

        /* schoolName field */
        if(!"".equals(schoolName)) {
        	TextField tf = new TextField(schoolName);
        	tf.setEditable(PersistenceFacade.instance().getFidentitySchools()==null);
        	schoolNameField = tf;
        } else {
        	schoolIdVector.clear();
        	schoolIdVector.addElement(ZERO);
        	Hashtable v = 
        	PersistenceFacade.instance().getFidentitySchools();
        	if(v == null)
        		schoolNameField = new TextField();
        	else {
                schoolNameField = new Choice(); 
        		Enumeration enumeration = v.keys();
        		while (enumeration.hasMoreElements()) {
        			Object element = enumeration.nextElement();
        			schoolIdVector.addElement(new Integer(element.toString()));
        			((Choice)schoolNameField).add(v.get(element).toString());
        		}
        	}
        }
        schoolNameField.setBounds(150, 28, 300, 20);
        schoolNameField.setVisible(false);
        this.add(schoolNameField);
        schoolNameField.setVisible(true);
        
        /* schoolLogin label */
        l = new Label("Schoollogin");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 80);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setVisible(false);
        this.add(l);
        l.setVisible(true);

        /* schoolLogin field */
        schoolLoginField = new TextField(schoolLogin);
        schoolLoginField.setBounds(150, 78, 150, 20);
        schoolLoginField.setVisible(false);
        this.add(schoolLoginField);
        schoolLoginField.setVisible(true);
        
        /* studentPasswd label */
        l = new Label("Wachtwoord Leerlingen");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 110);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setVisible(false);
        this.add(l);
        l.setVisible(true);

        /* studentPasswd field */
        studentPasswdField = new TextField(studentPasswd);
        studentPasswdField.setBounds(150, 108, 150, 20);
        studentPasswdField.setVisible(false);
        this.add(studentPasswdField);
        studentPasswdField.setVisible(true);
        
        
        /* teacherPasswd label */
        l = new Label("Wachtwoord Docenten");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 140);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setVisible(false);
        this.add(l);
        l.setVisible(true);

        /* teacherPasswd field */
        teacherPasswdField = new TextField(teacherPasswd);
        teacherPasswdField.setBounds(150, 138, 150, 20);
        teacherPasswdField.setVisible(false);
        this.add(teacherPasswdField);
        teacherPasswdField.setVisible(true);

        

        this.setSize(460, 280);

        /* Register button */
        okButton = new DwoButton(TextMapper.getText(TextMapper.BTN_OK),
                GuiConstants.MAIN_BACKGROUND);
        fm = okButton.getFontMetrics(okButton.getFont());
        okButton.setSize(fm.stringWidth(okButton.getLabel()) + 20, fm
                .getHeight() + 10);
        okButton.addActionListener(this);

        /* Reset button */
        cancelButton = new DwoButton(TextMapper.getText(TextMapper.BTN_CANCEL),
                GuiConstants.MAIN_BACKGROUND);
        fm = cancelButton.getFontMetrics(cancelButton.getFont());
        cancelButton.setSize(fm.stringWidth(cancelButton.getLabel()) + 20, fm
                .getHeight() + 10);
        cancelButton.addActionListener(this);

        okButton.setLocation(
                (getSize().width / 2)
                        - ((okButton.getSize().width
                                + cancelButton.getSize().width + 5) / 2), 163);
        add(okButton);

        cancelButton.setLocation(
                (getSize().width / 2)
                        - ((okButton.getSize().width
                                + cancelButton.getSize().width + 5) / 2)
                        + okButton.getSize().width + 5, 163);
        add(cancelButton);

        Point p = owner != null ? owner.getLocation() : new Point(0, 0);
        Dimension parentSize = owner != null ? owner.getSize() : Toolkit
                .getDefaultToolkit().getScreenSize();
        Dimension mySize = getSize();
        int x = p.x + (parentSize.width - mySize.width) / 2;
        int y = p.y + (parentSize.height - mySize.height) / 2;

        setLocation(x, y);
        this.addWindowListener(this);
    }

    public static School addSchool()  throws SchoolException {
        return addSchool(null);
    }

    /**
     * @return fi.dwo.client.domain.Sco
     */
    public static School addSchool(Component owner)  throws SchoolException {
        AddSchoolDialog asd = new AddSchoolDialog(owner, "Nieuwe school", "", "", "", "");
        asd.show();
        if (asd.isConfirmed()) {
            School s = GuiCreator.instance().addSchool(asd.getSchoolId(), asd.getSchoolName(), asd.getSchoolLogin(), asd.getStudentPasswd(), asd.getTeacherPasswd());
            if(s == null) { //something went wrong, reshow the dialog
                s = addSchool(owner);
            }
            return s;
        } else { //action canceled
            return null;
        }
    }

	public static School editSchool(School school)  throws SchoolException {
        return editSchool(null, school);
    }

    /**
     * @return fi.dwo.client.domain.Sco
     */
    public static School editSchool(Component owner, School school)  throws SchoolException {
        String sn = school.getName();
        String sl = school.getSchoolLogin();
        String ps = school.getPasswd(1);
        String pt = school.getPasswd(2);
        if(sn==null) sn = "";
        if(sl==null) sl = "";
        if(ps==null) ps = "";
        if(pt==null) pt = "";
                
        AddSchoolDialog asd = new AddSchoolDialog(owner, "Schoolgegevens wijzigen", sn, sl, ps, pt);
        asd.show();
        if (asd.isConfirmed()) {
            School s = GuiCreator.instance().editSchool(school.getSchoolID(), asd.getSchoolName(), asd.getSchoolLogin(), asd.getStudentPasswd(), asd.getTeacherPasswd());
            if(s == null) { //something went wrong, reshow the dialog
                s = editSchool(owner, school);
            }
            return s;
        } else { //action canceled
            return null;
        }
    }
    

   
    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
            this.setVisible(false);
        } else if (e.getSource() == okButton) {
        	if(schoolNameField instanceof Choice)
        		schoolName = ((Choice) schoolNameField).getSelectedItem();
        	else
        		schoolName = ((TextComponent) schoolNameField).getText();
        	
            schoolLogin = schoolLoginField.getText();
            studentPasswd = studentPasswdField.getText();
            teacherPasswd = teacherPasswdField.getText();
            confirmed = true;
            this.setVisible(false);
        }

    }

    /**
     * Invoked when the window is set to be the user's active window, which
     * means the window (or one of its subcomponents) will receive keyboard
     * events.
     * 
     * @param e
     *            The WindowEvent.
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    public void windowActivated(WindowEvent e) {
    }

    /**
     * Invoked when a window has been closed as the result of calling dispose on
     * the window.
     * 
     * @param e
     *            The WindowEvent.
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    public void windowClosed(WindowEvent e) {
    }

    /**
     * Invoked when the user attempts to close the window from the window's
     * system menu. If the program does not explicitly hide or dispose the
     * window while processing this event, the window close operation will be
     * cancelled.
     * 
     * @param e
     *            The WindowEvent.
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    public void windowClosing(WindowEvent e) {
        setVisible(false);
        dispose();
    }

    /**
     * Invoked when a window is no longer the user's active window, which means
     * that keyboard events will no longer be delivered to the window or its
     * subcomponents.
     * 
     * @param e
     *            The WindowEvent.
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    public void windowDeactivated(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a minimized to a normal state.
     * 
     * @param e
     *            The WindowEvent.
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    public void windowDeiconified(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a minimized to a normal state.
     * 
     * @param e
     *            The WindowEvent.
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    public void windowIconified(WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a normal to a minimized state. For
     * many platforms, a minimized window is displayed as the icon specified in
     * the window's iconImage property.
     * 
     * @param e
     *            The WindowEvent.
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    public void windowOpened(WindowEvent e) {
    }

    /**
     * @return Returns the confirmed.
     */
    public boolean isConfirmed() {
        return confirmed;
    }
    
    /**
     * @return Returns the schoolLogin.
     */
    public String getSchoolLogin() {
        return schoolLogin;
    }

    private Vector schoolIdVector = new Vector();
    
    private int getSchoolId() { 
    	if(schoolNameField instanceof Choice)
    	{ 
    		Integer n = (Integer) schoolIdVector.get(((Choice) schoolNameField).getSelectedIndex()+1);
    		return n.intValue();
    	}
    	return 0;
    }
    /**
     * @return Returns the studentPasswd.
     */
    public String getStudentPasswd() {
        return studentPasswd;
    }
    
    /**
     * @return Returns the teacherPasswd.
     */
    public String getTeacherPasswd() {
        return teacherPasswd;
    }

    /**
     * @return Returns the schoolName.
     */
    public String getSchoolName() {
        return schoolName;
    }
}