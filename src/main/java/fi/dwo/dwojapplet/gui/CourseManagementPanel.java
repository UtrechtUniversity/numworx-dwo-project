// Source file: C:\\parameters\\fi\\dwo\\client\\gui\\CourseManagementPanel.java

package fi.dwo.dwojapplet.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultTreeCellRenderer;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.action.DeleteAction;
import fi.dwo.dwojapplet.gui.action.ImportModuleAction;
import fi.dwo.dwojapplet.gui.action.ShareCourseAction;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;

import fi.wiskopdr.WiskOpdr;
import fi.wiskopdr.WiskOpdrEditPanel;

/**
 * This class is a panel containing a list of courses to edit, delete or add.
 * It is used for course-management.
 * @author M.J.B. Kupers
 *
 */
public class CourseManagementPanel extends JPanel implements CenterSubPanel, ActionListener, CourseMap {
     private static final Course STANDAARD_MODULE_PARENT = new Course();


	CourseManagementPanel(CourseMap map) {
		this(map.getChildren(), map.getUserObject());
		setMap(map);
	}

     CourseManagementPanel(CourseMap[] courses)
     {
    	 this(courses, (Object)ModuleTreePanel.SCHOOL_MODULES);
     }

	private CenterPanel center;


    private JButton addCourseButton, uploadCourseButton, shareCourseButton, addMapButton;

    private Image removeImage, editImage, scoImage;
    
    private CourseMap[] courses;

    private JLabel noCoursesLabel;

	private FileDialog openDial;
	private CourseMap  map = this;

	private JTable jTable;


	private JComponent tablePane;


	private Image upImage;
	private Image downImage;
	boolean updown;


	private Object userObject;


	private JTextComponent area;
	private WiskOpdrEditPanel wiskOpdrEditPanel;
	
	private JCheckBox editorCB;
	private Box editorBox = Box.createVerticalBox();

	class CourseModelForTree extends AbstractTableModel {

                @Override
		public Class getColumnClass(int col) {
			if(col == 0)
				return Boolean.class;
			if(col >= 2)
				return Image.class;
			return super.getColumnClass(col);
		}

                @Override
		public int getColumnCount() {
			return 6; // icon, naam, info, up, down, X
		}

                @Override
		public int getRowCount() {
			return courses.length;
		}

                @Override
		public boolean isCellEditable(int row, int col) {
			if(col == 3) // up
				return row != 0;
			if(col == 4) // down
				return row != getRowCount()-1;
			if(col >= 2)
				return true;
			return super.isCellEditable(row, col);
		}

                @Override
		public Object getValueAt(int row, int col) {
			Course course = (Course) courses[row];
			switch(col) {
			case 1: return course.getName();
			case 2: return editImage;
			case 3: if(row == 0) return null;
					return upImage;
			case 4: if(row == getRowCount()-1) return null;
					return downImage;
			case 5: return removeImage;
			
			case 0: return Boolean.valueOf(course.isWithChildren());
			}
			return null;
		}
		
	}
	
	
	
	class CourseModel extends AbstractTableModel {

                @Override
		public Class getColumnClass(int col) {
			if(col >= 1)
				return Image.class;
			return super.getColumnClass(col);
		}

                @Override
		public boolean isCellEditable(int row, int col) {
			if(col == 3) // up
				return row != 0 || !DWO.SEQUENCE;
			if(col == 4) // down
				return row != getRowCount()-1;
			if(col >= 1)
				return true;
			return super.isCellEditable(row, col);
		}

                @Override
		public int getColumnCount() {
			return DWO.SEQUENCE?6:4;
		}

                @Override
		public int getRowCount() {
			return courses.length;
		}

                @Override
		public Object getValueAt(int row, int col) {
			switch(col) {
			case 0:
				return courses[row].toString();
			case 1:
				return scoImage;
			case 2:
				return editImage;
			case 3: 
				if(DWO.SEQUENCE)
				{   if(row != 0)
						return upImage;
					break;
				}
			case 5:
				return removeImage;
			case 4: 
				if(row != getRowCount()-1)
					return downImage;
			}
			return null;
		}
		
	}
	
	public class BooleanRenderer extends DefaultTreeCellRenderer implements TableCellRenderer {

		
		
		private Dimension preferredSize;

                @Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if(Boolean.TRUE.equals(value))
				setIcon(getOpenIcon());
			else
				setIcon(getLeafIcon());
			return this;
		}

		public BooleanRenderer() {
			super();
			Image book = DwoHelper.getResourceImage("resources/book.png");
			setLeafIcon(new ImageIcon(book));
			
			setIcon(getLeafIcon());
			Dimension leaf = getPreferredSize();
			setIcon(getOpenIcon());
			Dimension open = getPreferredSize();
			
			int w = Math.max(leaf.width, open.width);
			int h = Math.max(leaf.height, open.height);
			preferredSize = new Dimension(w,h);
			setPreferredSize(preferredSize);
		}
		
	}
	
	public class ImageRenderer extends JLabel implements TableCellRenderer {

		private ImageIcon icon = new ImageIcon();

                @Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean hasFocus, int row, int col) {
			Image image = (Image)value;
			if(image != null) {
				icon.setImage(image);
				setIcon(icon);
			} else {
				setIcon(null);
			}
			setHorizontalAlignment(SwingConstants.CENTER);
			setOpaque(true);
			Object[] arguments = new Object[]  { courses[row].toString() };
			switch(col) {
			case 1:	String s = TextMapper.getText(TextMapper.GUIC_TLTP_SCO_COURSE);
	    			setToolTipText(MessageFormat.format(s, arguments));
	    			break;
			case 2: 
				// TODO isWithChildren?
				setToolTipText(TextMapper.getText(TextMapper.GUIC_TLTP_EDIT_COURSE));
				break;
			case 3: if(DWO.SEQUENCE)
				{
					setToolTipText(null);
					break;
				}
			case 5: String format = TextMapper.getText(TextMapper.GUIC_TLTP_DELETE_COURSE);
					setToolTipText(MessageFormat.format(format, arguments));
				break;
			default:
				//setToolTipText("Message " + col); // TODO ....
				setToolTipText(null);
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
    	AbstractTableModel model;
    	int row;

            @Override
    	public Component getTableCellEditorComponent(JTable table, Object value,
    			boolean arg2, int row, int col) {
    		this.value = value;
    		JButton button = new JButton(new ImageIcon((Image)value));
    		button.addActionListener(this);
    		this.row = row;
    		model = (AbstractTableModel) table.getModel();
    		return button;
    	}

            @Override
    	public Object getCellEditorValue() {
    		return value;
    	}

            @Override
    	public void actionPerformed(ActionEvent event) {
    		if(value == editImage)
    		{
                Course c = (Course) courses[row];
                if (CourseNameDialog.editCourse(c)) {
                    model.fireTableCellUpdated(row,0);
                }
 
    		} else if (value == removeImage)
    		{
                /* Delete the course */
                Course c = (Course) courses[row];
                if(DeleteAction.deleteCourse(c)) {
            		map.removeChild(row);
                    setChildren(map.getChildren());
                    model.fireTableRowsDeleted(row,row);
                    noUpdate();  
                }
                if(courses.length == 0) {
                    noCoursesLabel.setVisible(true);
                } else {
                    noCoursesLabel.setVisible(false);            
                }
    		
    		} else if (value == scoImage) {
                /* Show the scos of the course */
                Course c = (Course) courses[row];
                if(!c.isWithChildren())
                	center.loadCenter(GuiCreator.instance().getScoManagementPanel(c));
                else 
                	center.loadCenter(GuiCreator.instance().getCourseManagementPanel(c));

    		} else if (value == upImage) {
    			CourseMap s2 = courses[row-1];
    			CourseMap s  = courses[row];
    			courses[row] = s2;
    			courses[row-1] = s;
    			map.setChildren(courses);
    			model.fireTableRowsUpdated(row-1, row);
    			updown = true;
    			noUpdate();
    		} else if (value == downImage) {
    			CourseMap s2 = courses[row+1];
    			CourseMap s  = courses[row];
    			courses[row] = s2;
    			courses[row+1] = s;
    			map.setChildren(courses);
    			model.fireTableRowsUpdated(row, row+1);
    			updown = true;
    			noUpdate();
    		}
    		fireEditingStopped();
    	}


}
	
	/**
     * @param courses
     */
    public CourseManagementPanel(CourseMap[] courses, Object userObject) {
        super(new BorderLayout(10,10));
        this.userObject = userObject;
       // System.out.println(java.util.Locale.getDefault());
        upImage = DwoHelper.getResourceImage(GuiConstants.UP_SCO_IMAGE);
        downImage = DwoHelper.getResourceImage(GuiConstants.DOWN_SCO_IMAGE);
       this.courses = courses;
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        add(panel, BorderLayout.CENTER);
        //this.setSize(620, 485);
        //this.setSize(600, 470);
        //this.setPreferredSize(getSize());
        tablePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tablePane.setOpaque(false);
        panel.add(tablePane, BorderLayout.CENTER);
        if(userObject instanceof Course)
        {	
        	editorCB = new JCheckBox("Editor");
            editorCB.addActionListener(this);
            editorBox.add(editorCB);
            if(((Course) userObject).getText().startsWith("H4sIAAAAAA"))
            {	editorCB.setSelected(true);
            	wiskOpdrEditPanel = WiskOpdr.getWiskOpdrEditPanel(((Course) userObject).getText());
            	wiskOpdrEditPanel.setPreferredSize(new Dimension(700,300));
            	editorBox.add(wiskOpdrEditPanel);
            }
            else
            {	area = new JTextArea();
	            area.setText(((Course) userObject).getText());
	            area.setBorder(BorderFactory.createLineBorder(Color.black));
            	editorBox.add(area);
            }
            panel.add(editorBox, BorderLayout.NORTH);
        	
        	
        	
        	
        	
        	
        	
        	
        	
            /*
        	area = new JTextArea();
        	area.setText(((Course) userObject).getText());
        	area.setBorder(BorderFactory.createLineBorder(Color.black));
        	panel.add(area, BorderLayout.NORTH);
        	
        	
        	JPanel p = new JPanel();
        	p.setOpaque(false);
        	p.setLayout(null);
        	p.setPreferredSize(new Dimension(700,300));
        	
        	wiskOpdrEditPanel = WiskOpdr.getWiskOpdrEditPanel(((Course) userObject).getText());
        	wiskOpdrEditPanel.setPreferredSize(new Dimension(700,300));
        	p.add(wiskOpdrEditPanel);
        	panel.add(p, BorderLayout.NORTH);
        	*/
       }
        
        
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
        
        if(CenterPanel.isIconizer())
        {
        	addMapButton = new JButton(TextMapper.getText(TextMapper.GUIC_ADD_MAP));
        	addMapButton.addActionListener(this);
        	header.add(Box.createHorizontalStrut(4));
        	header.add(addMapButton);
        }

        header.add(new Box.Filler(new Dimension(4,0), new Dimension(4,0), new Dimension(Short.MAX_VALUE,0)));
        
        shareAction = new ShareCourseAction(this);
        shareCourseButton = new JButton(shareAction);
        header.add(shareCourseButton);
        
        if(DwoHelper.isSecure()){
        	header.add(Box.createHorizontalStrut(10));
        
        
	        importAction = new ImportModuleAction(this);
			uploadCourseButton = new JButton(importAction); // TODO TextMapper
	        //uploadCourseButton.setSize(uploadCourseButton.getPreferredSize());
	        uploadCourseButton.addActionListener(this);
	        //uploadCourseButton.setLocation(200+addCourseButton.getWidth()+10, 10);
	        uploadCourseButton.setVisible(false);
	        header.add(uploadCourseButton);
	        if(DwoHelper.isSecure()) 
	        	uploadCourseButton.setVisible(true);
        }
        
        if(!DWO.SEQUENCE)
        	Arrays.sort(courses);

        
        noCoursesLabel = new JLabel(TextMapper.getText(TextMapper.GUIC_NO_COURSES));
        noCoursesLabel.setFont(GuiConstants.SCO_TEXT);
        noCoursesLabel.setSize(noCoursesLabel.getPreferredSize());
        noCoursesLabel.setLocation((this.getSize().width/2) - (noCoursesLabel.getSize().width/2), 100);
        //this.add(noCoursesLabel);
        buildJTable();

        if(DwoHelper.isSecure()) {
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

    	AbstractTableModel tm = new CourseModel();
    	if(CenterPanel.isIconizer())
    		tm = new CourseModelForTree();
    	jTable = new JTable(tm);
    	jTable.setTableHeader(null);
    	//jScrollPane = new JScrollPane(jTable);
    	TableUtil.setDefaults(jTable, false, new ImageRenderer(), new ImageButtonEditor());
    	if(CenterPanel.isIconizer())
    		jTable.setDefaultRenderer(Boolean.class, new BooleanRenderer());
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
     @Override
    public void setCenterPanel(CenterPanel centerPanel) {
        center = centerPanel;
    }

    /**
     * Returns a Panel that can function as a header panel.
     * 
     * @return A panel that can function as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    
     @Override
    public Component getHeaderPanel() {
    	HeaderPanel hp = new HeaderPanel(TextMapper.getText(TextMapper.GUIC_COURSE_MANAGEMENT));
    	stopBtn = new JButton(TextMapper.getText(TextMapper.GUIH_STOP_EDIT));
    	stopBtn.setActionCommand("stop");
    	stopBtn.addActionListener(this);
    	hp.setButtonBox(GuiCreator.instance().fx(stopBtn));
		return hp;
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     */
     @Override
    public void actionPerformed(ActionEvent e) {
    	Object src = e.getSource();
            
    	if(src == editorCB)
    	{	if(editorCB.isSelected())
    		{	if(wiskOpdrEditPanel==null)
    			{	wiskOpdrEditPanel = WiskOpdr.getWiskOpdrEditPanel("");
            		wiskOpdrEditPanel.setPreferredSize(new Dimension(700,300));
            		editorBox.add(wiskOpdrEditPanel);
    			}
    			wiskOpdrEditPanel.setVisible(true);
    			area.setVisible(false);
    		}
    		else if(wiskOpdrEditPanel!=null)
    		{	if(area==null)
				{	area = new JTextArea();
					area.setText("");
					area.setBorder(BorderFactory.createLineBorder(Color.black));
		        	editorBox.add(area);
				}
    			wiskOpdrEditPanel.setVisible(false);
    			area.setVisible(true);
    		}
    	}
    	
    	if(src == stopBtn)
    	{
    		end();
    		center.select(map.getUserObject());
    	}
    	if(src == addMapButton)
    	{
    		Course c = CourseNameDialog.addMap(this, getParentCourse());
    		if(c != null)
    		{	
    			addChildToMap(c);                
    		}
    	} else
        if(src == addCourseButton) {
        	Course c = CourseNameDialog.addCourse(this, TextMapper.getText(TextMapper.GUICDLG_TTL_ADD_COURSE), getParentCourse(), false);
        	if(c != null) {
                addChildToMap(c);            
            }
        } else if(src == uploadCourseButton) {
//        	try {
//				upload();
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
        } 
            
    }


	private Course getParentCourse() {
		if(map.getUserObject() == ModuleTreePanel.STANDAARD_DWO_MODULES)
			return STANDAARD_MODULE_PARENT;
		if(map.getUserObject() instanceof Course)
			return (Course) map.getUserObject();
		return null;
	}


	private void addChildToMap(Course c) {
		map.addChild(c);
		courses = map.getChildren();
		if(!DWO.SEQUENCE)
		{	Arrays.sort(courses);
			map.setChildren(courses);
		} else
			updown = true; // sequencing needs an update
		buildJTable();
		noUpdate();
	}


	/**
	 * @param title
	 * @param names
	 * @return
	 */
	public static String replaceDuplicate(String title, Set names) {
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
     @Override
    public JComponent getComponent() {
        return this;
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.CenterSubPanel#end()
     */
     @Override
    public void end() {
		center.setStrategy(null);
		center.getMenu().setEditing(false);

        if(updown && DWO.SEQUENCE)
        {    	
        	updown = false;
        	try {
        		
				School school = User.getCurrentUser().getSchool();
// een profile admin mag de standaard modules sorteren, maar de school is dan wel null				
				if(userObject== ModuleTreePanel.STANDAARD_DWO_MAP)
					school = null;
				if(userObject instanceof Course)
				{
					if( ((Course) userObject).getSchoolID() == 0)
						school = null;
				}
				PersistenceFacade.instance().setCourseSequence(courses, school);
			} catch (PersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } 
        else if(userObject instanceof Course)
        {	Course course = (Course) userObject;
        	if(editorCB.isSelected() &&  !wiskOpdrEditPanel.getText().equals(course.getText()))
            {	course.setDescription(wiskOpdrEditPanel.getText());
    	    	GuiCreator.instance().updateCourse(course);
    	    }
        	else if(!editorCB.isSelected() && area!=null && !area.getText().equals(course.getText()))
            {	course.setDescription(area.getText());
            	GuiCreator.instance().updateCourse(course);
            }
        }
    }


     @Override
	public Object getUserObject() {
		return (map == this) ? userObject : map.getUserObject();
	}


	public void addChild(Course c) {
        Course[] ac = new Course[courses.length + 1];
        System.arraycopy(courses, 0, ac, 0, courses.length);
        ac[ac.length - 1] = c;
        courses = ac;
		if(!DWO.SEQUENCE)
		{	Arrays.sort(courses);
		}

	}


	public CourseMap[] getChildren() {
		return courses;
	}


	public void setChildren(CourseMap[] courses) {
		this.courses = courses;
	}


	public void removeChild(int row) {
		Course[] ac = new Course[courses.length - 1];
		System.arraycopy(courses, 0, ac, 0, row);
		System.arraycopy(courses, row+1, ac, row, ac.length-row);
		courses = ac;
	}

	boolean ok = true;


	private JButton stopBtn;


	private ImportModuleAction importAction;
	private ShareCourseAction  shareAction;
	private void noUpdate() {
		ok = false;
		center.updateMap(map);
		ok = true;
	}
	
     @Override
	public void stateChanged(ChangeEvent e) {
		if(ok && e.getSource() == getUserObject())
		{
			//System.out.println("UPDATE " + e);
			courses = map.getChildren();
			buildJTable();
		} else
		if(ok && e.getSource() instanceof CourseMap && ((CourseMap) e.getSource()).getUserObject() == getUserObject())
		{
			//System.out.println("UPDATE " + e);
			courses = ((CourseMap) e.getSource()).getChildren();
			buildJTable();
		}

	}

	public Set getChildNames() {
		HashSet names = new HashSet();
		for (int i = 0; i < courses.length; i++) {
			names.add(courses[i].toString());			
		}
		return names;
	}

	public CourseMap getParentMap() {
		return map.getParentMap();
	}

	public CourseMap getMap() {
		return map;
	}

	public void setMap(CourseMap map) {
		this.map = map;
		if(importAction!=null)importAction.setCourse(map);
		if(shareAction != null) shareAction.setMap(map);
	}
    
}