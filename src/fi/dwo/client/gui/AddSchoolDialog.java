// Source file: C:\\parameters\\fi\\dwo\\client\\gui\\ScoNameDialog.java

package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JSpinnerDateEditor;


import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.SchoolGroup;
import fi.dwo.client.domain.SchoolPasswdMap;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.TextMapper;
import fi.dwo.client.system.SchoolException;

/**
 * This is a dialog for editing the SCO name and description.
 * 
 * @author M.J.B. Kupers
 *
 */
public class AddSchoolDialog extends JDialog implements ActionListener,
        WindowListener {

    private static final Object ZERO = new Integer(0);

	private String schoolName;
    
    private String schoolLogin;
    
    private boolean confirmed;

    private Component schoolNameField;
    
    private JTextField schoolLoginField;

    private JTextField passwdField[] = new JTextField[SchoolGroup.LENGTH];
    private String   passwdLabel[] = new String[SchoolGroup.LENGTH];
    
    { 
    	passwdLabel[SchoolGroup.STUDENT] = "Wachtwoord Leerlingen";
    	passwdLabel[SchoolGroup.TEACHER] = "Wachtwoord Docenten";
    	passwdLabel[SchoolGroup.SCHOOLADMIN] = "Wachtwoord Schooladmin";
    	passwdLabel[SchoolGroup.ADMIN] = "Wachtwoord Administrator";
    }
    private boolean usePasswd[] = new boolean[SchoolGroup.LENGTH];
    {
    	usePasswd[SchoolGroup.STUDENT] = true;
    	usePasswd[SchoolGroup.TEACHER] = true;
    	usePasswd[SchoolGroup.SCHOOLADMIN] = true;
    }
    
    private SchoolPasswdMap passwdMap;
    
    private JButton okButton;

    private JButton cancelButton;

    public AddSchoolDialog(Component owner, String windowTitle, String schoolName, String schoolLogin,
            SchoolPasswdMap spm, Date expire) {
        super(DwoHelper.getFrameForComponent(owner),
                windowTitle, true);
        Container contentPane = getContentPane();
        SpringLayout layout = new SpringLayout();
        contentPane.setLayout(new BorderLayout());
        JPanel form = new JPanel(layout); // form
        contentPane.setBackground(GuiConstants.MAIN_BACKGROUND);
        form.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.schoolName = schoolName;
        this.schoolName = schoolLogin;
        this.passwdMap =  spm;
        confirmed = false;

        JLabel l;
        FontMetrics fm;

        /* schoolName label */
        l = new JLabel("Naam van de school");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 30);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setVisible(false);
        form.add(l);
        l.setVisible(true);

        /* schoolName field */
        Hashtable fidentitySchools = PersistenceFacade.instance().getFidentitySchools();
		if(!"".equals(schoolName)) {
        	JTextField tf = new JTextField(schoolName);
        	tf.setEditable(fidentitySchools==null);
        	schoolNameField = tf;
        } else {
        	schoolIdVector.clear();
        	schoolIdVector.addElement(ZERO);
        	if(fidentitySchools == null)
        		schoolNameField = new JTextField();
        	else {
        		TreeMap reversemap = new TreeMap();
        		Iterator iter = fidentitySchools.entrySet().iterator();
        		while (iter.hasNext()) {
					Entry object = (Entry) iter.next();
					reversemap.put(object.getValue(), object.getKey());
				}
        		JComboBox combo;
                schoolNameField = combo = new JComboBox(); 
        		Iterator enumeration = reversemap.keySet().iterator();
        		while (enumeration.hasNext()) {
        			Object element = enumeration.next();
        			schoolIdVector.addElement(new Integer(reversemap.get(element).toString()));
        			combo.addItem(element.toString());
        		}
        	}
        }
        
        int w = Math.max(300, schoolNameField.getPreferredSize().width);
		schoolNameField.setBounds(150, 28, w, 20);
        schoolNameField.setVisible(false);
        form.add(schoolNameField);
        schoolNameField.setVisible(true);
        
        /* schoolLogin label */
        l = new JLabel("Schoollogin");
        l.setForeground(Color.black);
        l.setFont(GuiConstants.NORMAL_TEXT);
        fm = l.getFontMetrics(l.getFont());
        l.setLocation(10, 80);
        l.setSize(fm.stringWidth(l.getText()) + 10, fm.getHeight());
        l.setVisible(false);
        form.add(l);
        l.setVisible(true);

        /* schoolLogin field */
        schoolLoginField = new JTextField(schoolLogin);
        schoolLoginField.setBounds(150, 78, 150, 20);
        schoolLoginField.setVisible(false);
        form.add(schoolLoginField);
        schoolLoginField.setVisible(true);
        
        for (int groupId = 0; groupId < SchoolGroup.LENGTH; groupId++)
        {
        	String text = spm.getPasswd(groupId);
        	if(text.length()!=0)
        		usePasswd[groupId] = true;
        	if(usePasswd[groupId]) {
                l = new JLabel(passwdLabel[groupId]);
                l.setForeground(Color.black);
                l.setFont(GuiConstants.NORMAL_TEXT);
                form.add(l);
                passwdField[groupId] = new JTextField(text);
                form.add(passwdField[groupId]);
        	}
        }
        
        l = new JLabel("Expire");
        l.setFont(GuiConstants.NORMAL_TEXT);
        form.add(l);
        JSpinnerDateEditor dateEditor = new JSpinnerDateEditor(); // zie ook selectcoursesdialog
        dateField = new JDateChooser(null, expire, null, dateEditor);
        dateField.setEnabled(fidentitySchools == null);
        form.add(dateField);
        
        
        //this.setSize(460, 280);
        Box okbox = Box.createHorizontalBox();
        okbox.add(Box.createHorizontalGlue());
        /* Register button */
        okButton = new JButton(TextMapper.getText(TextMapper.BTN_OK));//,GuiConstants.MAIN_BACKGROUND);
        //fm = okButton.getFontMetrics(okButton.getFont());
        okButton.setSize(okButton.getPreferredSize());
        okButton.addActionListener(this);

        /* Reset button */
        cancelButton = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));//, GuiConstants.MAIN_BACKGROUND);
        //fm = cancelButton.getFontMetrics(cancelButton.getFont());
        cancelButton.setSize(cancelButton.getPreferredSize());
        cancelButton.addActionListener(this);

        okButton.setLocation(
                (getSize().width / 2)
                        - ((okButton.getSize().width
                                + cancelButton.getSize().width + 5) / 2), 163);
        okbox.add(okButton);
        okbox.add(Box.createHorizontalStrut(10));
        cancelButton.setLocation(
                (getSize().width / 2)
                        - ((okButton.getSize().width
                                + cancelButton.getSize().width + 5) / 2)
                        + okButton.getSize().width + 5, 163);
        okbox.add(cancelButton);
        okbox.add(Box.createHorizontalGlue());
        makeCompactGrid(form, //parent
                form.getComponentCount()/2, 2,
                10, 10,  //initX, initY
                10, 10); //xPad, yPad

        
        contentPane.add(form, BorderLayout.CENTER);
        contentPane.add(okbox, BorderLayout.SOUTH);
        pack();
        //setSize(460, getHeight());
        Point p = owner != null ? owner.getLocationOnScreen() : new Point(0, 0);
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
        AddSchoolDialog asd = new AddSchoolDialog(owner, "Nieuwe school", "", "", new SchoolPasswdMap(), null);
        asd.show();
        if (asd.isConfirmed()) {
            School s = GuiCreator.instance().addSchool(asd.getSchoolId(), asd.getSchoolName(), asd.getSchoolLogin(), asd.getSchoolPasswdMap(), asd.dateField.getDate());
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
        SchoolPasswdMap spm = new SchoolPasswdMap(school);
        Date  expire = school.getExpire();
                
        AddSchoolDialog asd = new AddSchoolDialog(owner, "Schoolgegevens wijzigen", sn, sl, spm, expire);
        asd.show();
        if (asd.isConfirmed()) {
            School s = GuiCreator.instance().editSchool(school.getSchoolID(), asd.getSchoolName(), asd.getSchoolLogin(), asd.getSchoolPasswdMap(),asd.dateField.getDate());
            if(s == null) { //something went wrong, reshow the dialog
                s = editSchool(owner, school);
            }
            return s;
        } else { //action canceled
            return null;
        }
    }
    

   
    private SchoolPasswdMap getSchoolPasswdMap() {
    	SchoolPasswdMap result = new SchoolPasswdMap(passwdMap);
    	for (int i = 0; i < passwdField.length; i++) {
			JTextField field = passwdField[i];
			if(field != null && field.getText().trim().length()>0)
				result.setPasswd(i, field.getText());
		}
		return result;
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
        	if(schoolNameField instanceof JComboBox)
        		schoolName = (String) ((JComboBox) schoolNameField).getSelectedItem();
        	else
        		schoolName = ((JTextField) schoolNameField).getText();
        	
            schoolLogin = schoolLoginField.getText();
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

	private JDateChooser dateField;
    
    private int getSchoolId() { 
    	if(schoolNameField instanceof JComboBox)
    	{ 
    		Integer n = (Integer) schoolIdVector.get(((JComboBox) schoolNameField).getSelectedIndex()+1);
    		return n.intValue();
    	}
    	return 0;
    }
   
    /**
     * @return Returns the schoolName.
     */
    public String getSchoolName() {
        return schoolName;
    }
    
    
    /**
     * Aligns the first <code>rows</code> * <code>cols</code>
     * components of <code>parent</code> in
     * a grid. Each component in a column is as wide as the maximum
     * preferred width of the components in that column;
     * height is similarly determined for each row.
     * The parent is made just big enough to fit them all.
     *
     * @param rows number of rows
     * @param cols number of columns
     * @param initialX x location to start the grid at
     * @param initialY y location to start the grid at
     * @param xPad x padding between cells
     * @param yPad y padding between cells
     */
    public static void makeCompactGrid(Container parent,
                                       int rows, int cols,
                                       int initialX, int initialY,
                                       int xPad, int yPad) {
        SpringLayout layout;
        try {
            layout = (SpringLayout)parent.getLayout();
        } catch (ClassCastException exc) {
            System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
            return;
        }

        //Align all cells in each column and make them the same width.
        Spring x = Spring.constant(initialX);
        for (int c = 0; c < cols; c++) {
            Spring width = Spring.constant(0);
            for (int r = 0; r < rows; r++) {
                width = Spring.max(width,
                                   getConstraintsForCell(r, c, parent, cols).
                                       getWidth());
            }
            for (int r = 0; r < rows; r++) {
                SpringLayout.Constraints constraints =
                        getConstraintsForCell(r, c, parent, cols);
                constraints.setX(x);
                constraints.setWidth(width);
            }
            x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
        }

        //Align all cells in each row and make them the same height.
        Spring y = Spring.constant(initialY);
        for (int r = 0; r < rows; r++) {
            Spring height = Spring.constant(0);
            for (int c = 0; c < cols; c++) {
                height = Spring.max(height,
                                    getConstraintsForCell(r, c, parent, cols).
                                        getHeight());
            }
            for (int c = 0; c < cols; c++) {
                SpringLayout.Constraints constraints =
                        getConstraintsForCell(r, c, parent, cols);
                constraints.setY(y);
                constraints.setHeight(height);
            }
            y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
        }

        //Set the parent's size.
        SpringLayout.Constraints pCons = layout.getConstraints(parent);
        pCons.setConstraint(SpringLayout.SOUTH, y);
        pCons.setConstraint(SpringLayout.EAST, x);
    }
    /* Used by makeCompactGrid. */
    private static SpringLayout.Constraints getConstraintsForCell(
                                                int row, int col,
                                                Container parent,
                                                int cols) {
        SpringLayout layout = (SpringLayout) parent.getLayout();
        Component c = parent.getComponent(row * cols + col);
        return layout.getConstraints(c);
    }


    
}