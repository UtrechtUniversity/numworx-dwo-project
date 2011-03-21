// Source file: C:\\parameters\\fi\\dwo\\client\\gui\\CourseManagementPanel.java

package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.School;
import fi.dwo.client.gui.SchoolPanel.ImageButtonEditor;
import fi.dwo.client.gui.SchoolPanel.ImageRenderer;
import fi.dwo.client.gui.SchoolPanel.SchoolModel;
import fi.dwo.client.persistence.DbAccessCreator;
import java.util.Collections;

import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.SchoolException;
import fi.dwo.client.system.TextMapper;
import fi.dwo.server.form.DWOFile;

/**
 * This class is a panel containing a list of courses to edit, delete or add.
 * It is used for course-management.
 * @author M.J.B. Kupers
 *
 */
public class CourseManagementPanel extends JPanel implements CenterSubPanel, ActionListener {
    private CenterPanel center;


    private JButton addCourseButton, uploadCourseButton, shareCourseButton;

    private Image removeImage, editImage, scoImage;
    
    private Course[] courses;

    private JLabel noCoursesLabel;

	private FileDialog openDial;


	private JTable jTable;


	private JComponent tablePane;

	class CourseModel extends AbstractTableModel {

		public Class getColumnClass(int col) {
			if(col >= 1)
				return Image.class;
			return super.getColumnClass(col);
		}

		public boolean isCellEditable(int row, int col) {
			if(col >= 1)
				return true;
			return super.isCellEditable(row, col);
		}

		public int getColumnCount() {
			return 4;
		}

		public int getRowCount() {
			return courses.length;
		}

		public Object getValueAt(int row, int col) {
			switch(col) {
			case 0:
				return courses[row].getName();
			case 1:
				return scoImage;
			case 2:
				return editImage;
			case 3:
				return removeImage;
			}
			return null;
		}
		
	}
	public class ImageRenderer extends JLabel implements TableCellRenderer {

		private ImageIcon icon = new ImageIcon();

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean hasFocus, int row, int col) {
			Image image = (Image)value;
			icon.setImage(image);
			setIcon(icon);
			setHorizontalAlignment(SwingConstants.CENTER);
			setOpaque(true);
			Object[] arguments = new Object[]  { table.getValueAt(row, 0) };
			switch(col) {
			case 1:	String s = TextMapper.getText(TextMapper.GUIC_TLTP_SCO_COURSE);
	    			setToolTipText(MessageFormat.format(s, arguments));
	    			break;
			case 2: setToolTipText(TextMapper.getText(TextMapper.GUIC_TLTP_EDIT_COURSE));
				break;
			case 3: String format = TextMapper.getText(TextMapper.GUIC_TLTP_DELETE_COURSE);
					setToolTipText(MessageFormat.format(format, arguments));
				break;
			default:
				setToolTipText("Message " + col); // TODO ....
			}
			if(selected)
			{
				setBackground(table.getSelectionBackground());
			} else {
				setBackground(table.getBackground());
			}
			return this;
		}

	}

    public class ImageButtonEditor extends AbstractCellEditor implements
	TableCellEditor, ActionListener {

    	Object value;
    	CourseModel model;
    	int row;

    	public Component getTableCellEditorComponent(JTable table, Object value,
    			boolean arg2, int row, int col) {
    		this.value = value;
    		JButton button = new JButton(new ImageIcon((Image)value));
    		button.addActionListener(this);
    		this.row = row;
    		model = (CourseModel) table.getModel();
    		return button;
    	}

    	public Object getCellEditorValue() {
    		return value;
    	}

    	public void actionPerformed(ActionEvent event) {
    		if(value == editImage)
    		{
                Course c = courses[row];
                if (CourseNameDialog.editCourse(c)) {
                    model.fireTableCellUpdated(row,0);
                }
 
    		} else if (value == removeImage)
    		{
                /* Delete the course */
                Course c = courses[row];
                c.loadScos();
                String message;
                if(c.getScoList().length > 0) {
                    message = TextMapper.getText(TextMapper.GUIC_MSG_COURSE_DELETE);
                } else {
                    message = TextMapper.getText(TextMapper.GUIC_MSG_COURSE_DELETE_NO_SCO);
                }
                if (JOptionPane.showConfirmDialog(CourseManagementPanel.this, message, TextMapper.getText(TextMapper.GUIC_MSG_TTL_COURSE_DELETE), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    if (GuiCreator.instance().deleteCourse(c)) {
                        Course[] ac = new Course[courses.length - 1];
            			System.arraycopy(courses, 0, ac, 0, row);
            			System.arraycopy(courses, row+1, ac, row, ac.length-row);
                        courses = ac;
                        model.fireTableRowsDeleted(row,row);
                    }
                }
                if(courses.length == 0) {
                    noCoursesLabel.setVisible(true);
                } else {
                    noCoursesLabel.setVisible(false);            
                }
    		
    		} else if (value == scoImage) {
                /* Show the scos of the course */
                Course c = courses[row];
                center.loadCenter(GuiCreator.instance().getScoManagementPanel(c));

    		}
    		fireEditingStopped();
    	}

}

	
	
	
	
	/**
     * @param courses
     */
    public CourseManagementPanel(Course[] courses) {
        super(new BorderLayout(10,10));
       // System.out.println(java.util.Locale.getDefault());
        this.courses = courses;
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        //this.setSize(620, 485);
        //this.setSize(600, 470);
        //this.setPreferredSize(getSize());
        tablePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tablePane.setOpaque(false);
        add(tablePane, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(10,30,5,10));
        Box header = Box.createHorizontalBox();
        add(header, BorderLayout.NORTH);
        /* Add Remove-class image */
        
        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_COURSE_IMAGE);
        editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_COURSE_IMAGE);
        scoImage = DwoHelper.getResourceImage(GuiConstants.SCO_COURSE_IMAGE);
        

        addCourseButton = new JButton(TextMapper.getText(TextMapper.GUIC_ADD_COURSE));
        //addCourseButton.setSize(addCourseButton.getPreferredSize());
        addCourseButton.addActionListener(this);
        //addCourseButton.setLocation(30, 10);
        header.add(addCourseButton);
        header.add(Box.createHorizontalGlue());
        
        
        shareCourseButton = new JButton("Modules delen");
        shareCourseButton.addActionListener(this);
        header.add(shareCourseButton);
        if(DwoHelper.isApplication())
        	header.add(Box.createHorizontalStrut(10));
        
        
        uploadCourseButton = new JButton("Restore module backup"); // TODO TextMapper
        //uploadCourseButton.setSize(uploadCourseButton.getPreferredSize());
        uploadCourseButton.addActionListener(this);
        //uploadCourseButton.setLocation(200+addCourseButton.getWidth()+10, 10);
        uploadCourseButton.setVisible(false);
        header.add(uploadCourseButton);
        if(DwoHelper.isApplication()) 
        	uploadCourseButton.setVisible(true);
        
        Arrays.sort(courses);

        
        noCoursesLabel = new JLabel(TextMapper.getText(TextMapper.GUIC_NO_COURSES));
        noCoursesLabel.setFont(GuiConstants.SCO_TEXT);
        noCoursesLabel.setSize(noCoursesLabel.getPreferredSize());
        noCoursesLabel.setLocation((this.getSize().width/2) - (noCoursesLabel.getSize().width/2), 100);
        //this.add(noCoursesLabel);
        buildJTable();

        if(DwoHelper.isApplication()) {
        	final Frame topFrame = DwoHelper.getFrameForComponent(null);
        	openDial = new FileDialog(topFrame, uploadCourseButton.getLabel(), FileDialog.LOAD);
        	openDial.setDirectory(System.getProperty("user.dir","."));
        }
    }

 
    private void buildJTable() {
    	if(jTable != null)
    		tablePane.remove(jTable);
        if(courses.length == 0) {
            noCoursesLabel.setVisible(true);
            tablePane.add(noCoursesLabel);
            return;
        } else {
            noCoursesLabel.setVisible(false);
            tablePane.remove(noCoursesLabel);
        }

    	CourseModel tm = new CourseModel();
    	jTable = new JTable(tm);
    	jTable.setTableHeader(null);
    	//jScrollPane = new JScrollPane(jTable);
    	TableUtil.setDefaults(jTable, false, new ImageRenderer(), new ImageButtonEditor());
   	
    	TableUtil.setJTableSizes(jTable);
       	//TableUtil.setBorder(jScrollPane);
       	TableUtil.setBorder(jTable);
       	//jTable.setLocation(30, addCourseButton.getSize().height
        //        + addCourseButton.getLocation().y + 15);
       	//TableUtil.shrinkToFit(jTable, jScrollPane, 520, 405);
        jTable.setSize(jTable.getPreferredSize());
        tablePane.add(jTable);
        tablePane.invalidate();
        validate();
        repaint();
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
     * Returns a Panel that can functionate as a header panel.
     * 
     * @return A panel that can functionate as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    
    public Component getHeaderPanel() {
    	return new HeaderPanel(TextMapper.getText(TextMapper.GUIC_COURSE_MANAGEMENT));
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     */
    public void actionPerformed(ActionEvent e) {
    	Object src = e.getSource();
            
        if(src == addCourseButton) {
        	Course c = CourseNameDialog.addCourse(this);
        	if(c != null) {
                Course[] ac = new Course[courses.length + 1];
                System.arraycopy(courses, 0, ac, 0, courses.length);
                ac[ac.length - 1] = c;
                courses = ac;
                Arrays.sort(courses);
                buildJTable();                
            }
        } else if(src == uploadCourseButton) {
        	try {
				upload();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        } else if(src == shareCourseButton) {
        	
    		ExportImportDialog dialog;
			try {
				dialog = new ExportImportDialog(DwoHelper.getFrameForComponent(this), GuiCreator.instance().getUser(), GuiCreator.instance().dwo.getDwoProfile());
				dialog.setVisible(true);
			} catch (PersistenceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		
    		

        }
            
    }

    private void upload() throws Exception {
    	String naam;
		openDial.show();
		naam = openDial.getFile();
		if(naam!=null)
		{	
			File dir = new File(openDial.getDirectory());
			File file = new File(dir, naam);
			FileInputStream input = new FileInputStream(file);
			DWOFile zipper = new DWOFile(DbAccessCreator.instance());
			Hashtable result = zipper.inputIMSManifest(input);

// TODO deze code verplaatsen naar DWOFile?
// of ?copieren? naar ScoManagementPanel.
			Set  names = new HashSet();
			for (int i = 0; i < courses.length; i++) {
				names.add(courses[i].getName());
			}
			String title = (String)result.get("name");
			title = replaceDuplicate(title, names);
			result.put("name", title);

			final DwoIF dwo = GuiCreator.instance().dwo;
			zipper.addCourse(result, dwo.getDwoProfile().getID(), dwo.getUser().getSchool().getSchoolID());
			courses = dwo.getEditableCourses();
			buildJTable();
		}
	}

	/**
	 * @param title
	 * @param names
	 * @return
	 */
	static String replaceDuplicate(String title, Set names) {
		boolean again;
		do {
			again = names.contains(title);
			if(again)
			{
				int i = title.lastIndexOf(';')+1;
				if(i>0)
				{
					try {
						int m = Integer.parseInt(title.substring(i)) + 1;
						title = title.substring(0,i) + m;
					} catch (NumberFormatException e) {
						title += ";1";
					}
				} else {
					title += ";1";
				}
			}
		} while(again);
		return title;
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

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.CenterSubPanel#end()
     */
    public void end() {
        // TODO Auto-generated method stub
        
    }


	public Object getUserObject() {
		// TODO Auto-generated method stub
		return null;
	}
    
}