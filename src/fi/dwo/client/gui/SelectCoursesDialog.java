// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\SelectCoursesDialog.java

package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import fi.dwo.client.domain.ClassCourse;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.domain.DWO;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.User;
import fi.dwo.client.gui.ClassPanel.ClassModel;
import fi.dwo.client.gui.action.TeacherStrategy;
import fi.dwo.client.persistence.DbAccessCreator;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.TextMapper;

class CourseData {
	Course course;
	Object select;
	Date van, tot;
	
	public CourseData(Course course) {
		this.course = course;
		if(course.link != null)
		{
			van = course.link.getNotBefore();
			tot = course.link.getNotAfter();
		}
	}
	Image data;
	public String toString() {
		return String.valueOf(course);
	}
	public boolean isSelected() {
		return Boolean.TRUE.equals(select);
	}	
	CourseData[] children;
	public int type;	
	
}
/**
 * This class represents a dialog for selecting courses.
 * 
 * @author M.J.B. Kupers
 *  
 */
public final class SelectCoursesDialog extends JDialog implements ActionListener {

    public boolean updown, vantot;


	public class ImageEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

		private Object value;
		private int row;
		private CoursesModel model;
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
    		this.value = value;
    		JButton button = new JButton(new ImageIcon((Image)value));
    		button.addActionListener(this);
    		this.row = row;
    	    model = (CoursesModel) table.getModel();
    		return button;
		}

		public Object getCellEditorValue() {
			return value;
		}

		public void actionPerformed(ActionEvent e) {
			if (value == removeImage) {
                /* Delete the leerlingdata */
                if (eraseClassData(cd[row].course)) {
                        value = null;
                        ((JButton) e.getSource()).setIcon(null);
                    
                }
    		} else if(value == upImage)
    		{
    			CourseData s2 = cd[row-1];
    			CourseData s  = cd[row];
    			cd[row] = s2;
    			cd[row-1] = s;
    			model.fireTableRowsUpdated(row-1, row);
    			updown = true;
   			
    		} else if(value == downImage)
    		{
    			
    		}
		}
	}

    boolean eraseClassData(Course course)
    {
    	if (JOptionPane.showConfirmDialog(SelectCoursesDialog.this, "Wilt u alle resultaten van " + course + " voor " + sc.getName() + " verwijderen"
                + "?", "Leerlinggegevens verwijderen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (PersistenceFacade.instance().deleteCourseClassData(course, sc)) {
            	return true;
            }
        }
    	return false;
    }
    
    
	private Course[] selectedCourses;

    private JButton okButton;

    private JButton cancelButton;
    
    private JButton selectAllButton;
    
    private JButton deselectAllButton;

	private JTable jTable;

	private Image removeImage, upImage, downImage;

	private SchoolClass sc;

	CourseData[] cd;

	private ModuleTreePanel tree;
	private DefaultTreeModel treeModel;

	class CoursesModel extends AbstractTableModel {

    	int columnCount = 2;

    	CourseData[] getCD() { return cd; }
    	
		public int getColumnCount() {
			return columnCount;
		}

		void setColumnCount(int columnCount) {
			this.columnCount = columnCount;
		}

		public int getRowCount() {
			return cd.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex) {
			case 0: return cd[rowIndex].select;
			case 1: return cd[rowIndex].course.getName();
			case 2: return cd[rowIndex].data;
			case 3: if(rowIndex != 0) return upImage;
					break;
			case 4: if(rowIndex != getRowCount()-1)
						return downImage;
			}
			return null;
		}

		public Class getColumnClass(int columnIndex) {
			if(columnIndex >= 2)
				return Image.class;
			if(columnIndex != 1)
				return Boolean.TRUE.getClass();
			return super.getColumnClass(columnIndex);
		}

		public String getColumnName(int column) {
			switch(column) {
			case 3: 
			case 4:
			case 0: return "";
			case 1: return "Module";
			case 2: return "Leerlinggegevens aanwezig";
			}
			return super.getColumnName(column);
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			switch(columnIndex) {
			case 0: return true;
			case 1: return false;
			case 2: return cd[rowIndex].data != null;
			case 3: return rowIndex != 0;
			case 4: return rowIndex != getRowCount()-1;
			}
			return super.isCellEditable(rowIndex, columnIndex);
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			switch(columnIndex) {
			case 0:
				cd[rowIndex].select = aValue;
				break;
			case 2:
				cd[rowIndex].data = (Image) aValue;
			}
			fireTableCellUpdated(rowIndex, columnIndex);
		}
		
    }
    
    class CheckBoxNodeRenderer implements TreeCellRenderer {
    	  JCheckBox leafRenderer = new JCheckBox();
    	  JButton   eraseBtn = new JButton();
    	  JButton 	vanBtn = new JButton();
    	  JButton	totBtn = new JButton();
    	  Box box;
    	  boolean boksAan;
    	  private DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();

    	  Color selectionBorderColor, selectionForeground, selectionBackground,
    	      textForeground, textBackground;

    	  protected JComponent getLeafRenderer() {
    	      if(boksAan)
    	    	  return box;
    		  return leafRenderer;
    	  }

    	  public CheckBoxNodeRenderer(boolean b) {
    		boksAan = b; 
    		if(b)
    		{
    			box = Box.createHorizontalBox();
    			box.add(leafRenderer);
    			eraseBtn.setBorderPainted(false);
    			eraseBtn.setContentAreaFilled(false);
    			
    			box.add(eraseBtn);
    			if(vantot)
    			{ 	box.add(vanBtn);
    				box.add(totBtn);
    			}
    		}
    	    Font fontValue;
    	    fontValue = UIManager.getFont("Tree.font");
    	    if (fontValue != null) {
    	      leafRenderer.setFont(fontValue);
    	    }
    	    Boolean booleanValue = (Boolean) UIManager
    	        .get("Tree.drawsFocusBorderAroundIcon");
    	    leafRenderer.setFocusPainted((booleanValue != null)
    	        && (booleanValue.booleanValue()));
    	    leafRenderer.setContentAreaFilled(true);
    	    selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
    	    selectionForeground = UIManager.getColor("Tree.selectionForeground");
    	    selectionBackground = UIManager.getColor("Tree.selectionBackground");
    	    textForeground = UIManager.getColor("Tree.textForeground");
    	    textBackground = UIManager.getColor("Tree.textBackground");
    	    
    	    leafRenderer.setBorder(BorderFactory.createLineBorder(Color.green));
    	    selectionForeground = Color.blue;
    	    
    	  }

    	  public Component getTreeCellRendererComponent(JTree tree, Object value,
    	      boolean selected, boolean expanded, boolean leaf, int row,
    	      boolean hasFocus) {

    	    Component returnValue;
    	    if(leaf && value instanceof DefaultMutableTreeNode)
    	    {
    	    	Object userdata = ((DefaultMutableTreeNode) value).getUserObject();
    	    	if(userdata instanceof CourseData)
    	    		leaf =  ! ((CourseData)userdata).course.isWithChildren();
    	    	else
    	    		leaf = false;
    	    	expanded = false;
    	    }
    	    if (leaf) {

    	      String stringValue = tree.convertValueToText(value, selected,
    	          expanded, leaf, row, false);
    	      leafRenderer.setText(stringValue);
    	      leafRenderer.setSelected(false);
    	      leafRenderer.setEnabled(tree.isEnabled());

    	      if (selected) {
    	        leafRenderer.setForeground(selectionForeground);
    	        leafRenderer.setBackground(selectionBackground);
    	      } else {
    	        leafRenderer.setForeground(textForeground);
    	        leafRenderer.setBackground(textBackground);
    	      }

    	      if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
    	        Object userObject = ((DefaultMutableTreeNode) value)
    	            .getUserObject();
    	        if (userObject instanceof CourseData) {
    	        	CourseData node = (CourseData) userObject;
    	          leafRenderer.setText(node.toString());
    	          leafRenderer.setSelected(node.isSelected());
    	          eraseBtn.setVisible(node.data != null);
    	          if(node.data != null)
    	          {
    	        	eraseBtn.setIcon(new ImageIcon(node.data));  
    	          } 	          
    	          if(node.van != null) {
    	        	  vanBtn.setText(DATE_TIME.format(node.van));
    	          } else 
    	        	  vanBtn.setText("");
    	          if(node.tot != null) {
    	        	  totBtn.setText(DATE_TIME.format(node.tot));
    	          } else 
    	        	  totBtn.setText("");
    	          
    	          
    	        }
    	      }
    	      returnValue = getLeafRenderer();
    	    } else {
    	      returnValue = nonLeafRenderer.getTreeCellRendererComponent(tree,
    	          value, selected, expanded, leaf, row, hasFocus);
    	    }
    	    return returnValue;
    	  }
    	}

	static final DateFormat DATE_TIME = DateFormat.getDateTimeInstance();
    class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {


		CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer(true);

    	  ChangeEvent changeEvent = null;

    	  JTree tree;

    	  public CheckBoxNodeEditor(JTree tree) {
    	    this.tree = tree;
      	    	renderer.leafRenderer.addItemListener(itemListener);
      	    	renderer.eraseBtn.addActionListener(eraseAction );
      	    	renderer.vanBtn.addActionListener(vanAction);
      	    	renderer.totBtn.addActionListener(totAction);      	    
    	  }

    	  public Object getCellEditorValue() {
    	    JCheckBox checkbox = renderer.leafRenderer;
     	    CourseData checkBoxNode = (CourseData)userObject;
    	       checkBoxNode.select = new Boolean(checkbox.isSelected());
    	    return checkBoxNode;
    	  }
     	  Object userObject;

		private ActionListener eraseAction = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
	     	    CourseData checkBoxNode = (CourseData)userObject;
	     	    if(eraseClassData(checkBoxNode.course))
	     	    	checkBoxNode.data = null;
	     	    itemListener.itemStateChanged(null);
			}}
		;
		
		private ActionListener vanAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	     	    CourseData checkBoxNode = (CourseData)userObject;
	     	    checkBoxNode.van = changeDate(checkBoxNode.course, checkBoxNode.van);
	     	    itemListener.itemStateChanged(null);				
			}
		};
		private ActionListener totAction = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
	     	    CourseData checkBoxNode = (CourseData)userObject;
	     	    checkBoxNode.tot = changeDate(checkBoxNode.course, checkBoxNode.tot);
	     	    itemListener.itemStateChanged(null);
			}
		};
		

		private ItemListener itemListener = new ItemListener() {
		  public void itemStateChanged(ItemEvent itemEvent) {
		    if (stopCellEditing()) {
		      fireEditingStopped();
		    }
		  }
		};

    	  public boolean isCellEditable(EventObject event) {
    	    boolean returnValue = false;
    	    if (event instanceof MouseEvent) {
    	      MouseEvent mouseEvent = (MouseEvent) event;
    	      TreePath path = tree.getPathForLocation(mouseEvent.getX(),
    	          mouseEvent.getY());
    	      if (path != null) {
    	        Object node = path.getLastPathComponent();
    	        if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
    	          DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
    	          userObject = treeNode.getUserObject();
    	          returnValue = ((treeNode.isLeaf()) && (userObject instanceof CourseData));
    	        }
    	      }
    	    }
    	    return returnValue;
    	  }

    	  protected Date changeDate(Course course, Date van) {
    		Date orig = van;
    		if(van == null) 
    			van = new Date();
    		String out = 
			JOptionPane.showInputDialog("Geef tijdstip", DATE_TIME.format(van));
    		if(out == null) return orig;
    		if(out.equals("")) return null;
    		try {
				return DATE_TIME.parse(out);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return orig;
		}

		public Component getTreeCellEditorComponent(JTree tree, Object value,
    	      boolean selected, boolean expanded, boolean leaf, int row) {

    	    Component editor = renderer.getTreeCellRendererComponent(tree, value,
    	        true, expanded, leaf, row, true);

    	    return editor;
    	  }
    	}
    
    /**
     * Creates a new instance of a SelectCoursesDialog. It shows a overview of
     * the selected courses, and gives an opportunity to select courses.
     * 
     * @param owner The owner component of the dialog.
     * @param title The title of the dialog.
     * @param modal If true, the dialog is modal.
     * @param allCourses A list of all course to select.
     * @param selectedCourses A list of all the currently selected courses.
     */
    private SelectCoursesDialog(Component owner, String title, boolean modal,
            Course[] allCourses, Course[] selectedCourses, int cnt) {
        super(DwoHelper.getFrameForComponent(owner), title, modal);
//        vantot = cnt != 2;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(GuiConstants.MAIN_BACKGROUND);
        setBackground(GuiConstants.MAIN_BACKGROUND);
        setSize(600, 310);
        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_CLASS_IMAGE);
        upImage = DwoHelper.getResourceImage(GuiConstants.UP_SCO_IMAGE);
        downImage = DwoHelper.getResourceImage(GuiConstants.DOWN_SCO_IMAGE);

        cd = new CourseData[allCourses.length];        

        for (int i = 0; i < allCourses.length; i++) {
			cd[i] = new CourseData(allCourses[i]);
		}

        this.selectedCourses = null;
        
        /*
         * Create a Vector with all the selected courses. We can now easily
         * check if a course is selected
         */
        final Vector vSelectedCourses = new Vector(selectedCourses.length);
        for (int i = 0; i < selectedCourses.length; i++) {
            vSelectedCourses.addElement(selectedCourses[i]);
        }
        for (int i = 0; i < allCourses.length; i++) {
            if (vSelectedCourses.contains(allCourses[i])) {
                cd[i].select = Boolean.TRUE;
            }
        }

        if(CenterPanel.isIconizer() )
        {
        	
        	CourseMap schoolMap = ModuleTreePanel.SCHOOL_MAP;
        	CourseMap dwoMap = ModuleTreePanel.STANDAARD_DWO_MAP;
        	tree = new ModuleTreePanel() {

        		protected void createModel(DwoIF dwo) {
        			super.createModel(null);
        			setStrategy(new SelectStrategy() {

						public void nodeSelected(CourseMap node) {
						}

						public JPopupMenu nodeAction(CourseMap node) {
							return null;
						}});
        			DefaultMutableTreeNode root, schoolnode, dwonode;
        			JTree tree2 = this.tree;
					root = (DefaultMutableTreeNode) tree2.getModel().getRoot();
        			dwonode = root.getFirstLeaf();
        			schoolnode = dwonode;
                	GuiCreator instance = GuiCreator.instance();
                	if(instance.getMainPanel()!= null)
                		setCenterPanel(instance.getMainPanel().getCenter());
        			User u = instance.getUser();
                	School school = u.getSchool();
                	if(school != null)
                	{  	schoolnode = new DefaultMutableTreeNode("Modules " + school);
                		if(u.hasRight(User.MODIFY_MODULES_RIGHT))
                			root.add(schoolnode);
                	}
                	DefaultMutableTreeNode node; 
                	for (int i = 0; i < cd.length; i++) {
        				CourseData course = cd[i];
        				node = new DefaultMutableTreeNode(course);
        				
        				if(cd[i].course.getSchoolID() == 0)
        				{
        					dwonode.add(node);
        				} else {
        					schoolnode.add(node);
        				}
        				appendCourseData(course, node, vSelectedCourses);
        				
        				
        			}
                	treeModel = new DefaultTreeModel(root);
                	setModel(treeModel);
                	tree2.setCellRenderer(new CheckBoxNodeRenderer(true));
                	tree2.setCellEditor(new CheckBoxNodeEditor(tree2));
                	tree2.setEditable(true);
        		}
			
        		protected void createCloseBtn(Box bar) {}
        		protected void createMenubar(Box bar) {}
        	};
			tree.createModel(null);
			// restore.... FIXME HACK HACK HACK, WIM!
        	ModuleTreePanel.SCHOOL_MAP = schoolMap;
        	ModuleTreePanel.STANDAARD_DWO_MAP = dwoMap;
	        contentPane.add(tree, BorderLayout.CENTER);
        } else {
            CoursesModel cm = new CoursesModel(); 
            cm.setColumnCount(cnt);
        	TableCellRenderer imageRenderer = new ImageRenderer();
        	TableCellEditor imageEditor = new ImageEditor();
        	jTable = new JTable(cm);
        	TableUtil.setDefaults(jTable, true, imageRenderer, imageEditor);
        	jTable.setRowMargin(2);
        	TableUtil.setJTableSizes(jTable);
		
	        TableColumn column = jTable.getColumnModel().getColumn(0);
	        int w = column.getPreferredWidth();
	        column.setMaxWidth(w);
	        column.setMinWidth(w);
	        if(cnt > 2)
	        {
	        	column = jTable.getColumnModel().getColumn(2);
	        	w = column.getPreferredWidth();
	            column.setMaxWidth(w);
	            column.setMinWidth(w);
	        }
	        JScrollPane pane = new JScrollPane(jTable);
	        pane.getViewport().setBackground(getBackground());
	        pane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
	        pane.setOpaque(false);
	        contentPane.add(pane, BorderLayout.CENTER);
        }
        Box southPane = Box.createHorizontalBox();
        southPane.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
        contentPane.add(southPane, BorderLayout.SOUTH);
        /* Select All Button */
        selectAllButton = new JButton(TextMapper.getText(TextMapper.GUISC_BTN_SELECT_ALL));
        selectAllButton.addActionListener(this);
        southPane.add(selectAllButton);
        southPane.add(Box.createHorizontalStrut(5));
        /* Deselect All Button */
        deselectAllButton = new JButton(TextMapper.getText(TextMapper.GUISC_BTN_DESELECT_ALL));
        deselectAllButton.addActionListener(this);
        southPane.add(deselectAllButton);

        southPane.add(Box.createGlue());
        
        /* Cancel button */
        cancelButton = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));
        cancelButton.addActionListener(this);
        southPane.add(cancelButton);
        southPane.add(Box.createHorizontalStrut(5));

        /* Ok button */
        okButton = new JButton(TextMapper.getText(TextMapper.BTN_OK));
        okButton.addActionListener(this);
        southPane.add(okButton);


        // set location to center of parent
        int x = 0;
        int y = 0;

        Point p = owner != null ? owner.getLocation() : new Point(0, 0);
        Dimension parentSize = owner != null ? owner.getSize()
                : Toolkit.getDefaultToolkit().getScreenSize();
        Dimension mySize = getSize();
        x = p.x + (parentSize.width - mySize.width) / 2;
        y = p.y + (parentSize.height - mySize.height) / 2;

        setLocation(x, y);
    }

    /**
     * Shows the SelectCoursesDialog and returns the selected courses.
     * 
     * @param parent The parent component of the dialog.
     * @param allCourses A list of all the possible courses.
     * @param selectedCourses A list of all the selected courses.
     * @return A list of all the selected courses.
     */
    public static Course[] selectCourses(Component parent, Course[] allCourses,
            Course[] selectedCourses) {
        String title = TextMapper.getText(TextMapper.GUISC_TITLE);
        SelectCoursesDialog scd = new SelectCoursesDialog(parent, title, true, allCourses, selectedCourses, 2);
        scd.show();
        return scd.getSelectedCourses();
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e The ActionEvent.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
        	selectedCourses = null;
        	setVisible(false);
        } else if (e.getSource() == okButton) {
            Vector tmpSelected = new Vector();
            
            addSelected(tmpSelected, cd);
            if(updown && DWO.SEQUENCE)
            {	updown = false;
            	Course[] courses = new Course[cd.length];
            	for (int i = 0; i < courses.length; i++) {
					courses[i] = cd[i].course;
				}
            	try {
					PersistenceFacade.instance().setCourseSequence(courses, null, sc);
				} catch (PersistenceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
            
            
            selectedCourses = new Course[tmpSelected.size()];
            tmpSelected.copyInto(selectedCourses);
            setVisible(false);

        } else if (e.getSource() == selectAllButton) {
        	select(Boolean.TRUE);      	
        } else if (e.getSource() == deselectAllButton) {
            select(Boolean.FALSE);
        }
    }

	private void addSelected(Vector vector, CourseData[] cds) {
		int len = cds.length;
		for(int i = 0; i < len; i++ )
		{
			CourseData children[] = cds[i].children;
			if(children == null && Boolean.TRUE.equals(cds[i].select))
			{
				Course course = cds[i].course;
				if(sc != null) {
					ClassCourse link = course.link = new ClassCourse();
					link.setCourseID(course.getID());
					link.setClassID(sc.getID());
					link.setNotAfter(cds[i].tot);
					link.setNotBefore(cds[i].van);
					link.setType(cds[i].type);
				}
				vector.addElement(course);
				addParent(vector, course);
			}
			if(children != null)
				addSelected(vector, children);
		}
	}

	private void addParent(Vector vector, Course course) {
		CourseMap pa = course.getParentMap();
		if(pa instanceof Course) {
			addParentInsert(vector, pa);
		} else {
			int parentID = course.getParentID();
			if(parentID != 0) {
				try {
					pa = (Course) PersistenceFacade.instance().get(parentID, Course.class);
					addParentInsert(vector, pa);
				} catch (PersistenceException e) {
					// should not happen
				}				
			}
		}
		
	}

	private void addParentInsert(Vector vector, CourseMap pa) {
		if(! vector.contains(pa))
		{
			vector.addElement(pa);
			addParent(vector, (Course)pa);
		}
	}

	private void select(Boolean value) {
		selectCD(value, cd);
		if(jTable != null)
		{
		    CoursesModel model = (CoursesModel) jTable.getModel();
		    model.fireTableDataChanged();
		} else if(treeModel != null)
		{
			treeModel.nodeChanged((TreeNode) treeModel.getRoot());
		}
	}

	private void selectCD(Boolean value, CourseData[] cds) {
		int len = cds.length;
		for(int i = 0; i < len; i++)
		{	
			cds[i].select = value;
			CourseData[] children = cds[i].children;
			if(children!=null)
				selectCD(value, children);
		}
	}

    /**
     * Returns all the selected courses.
     * 
     * @return All the selected courses.
     */
    public Course[] getSelectedCourses() {
        return selectedCourses;
    }

	public static Course[] selectCourses(ClassPanel parent,
			Course[] allCourses, Course[] selectedCourses, SchoolClass sc) {
        String title = TextMapper.getText(TextMapper.GUISC_TITLE);
        allCourses = 
        GuiCreator.instance().dwo.sequence(allCourses, sc);
        
        
        SelectCoursesDialog scd = new SelectCoursesDialog(parent, title, true, allCourses, selectedCourses, 3+(DWO.SEQUENCE?2:0));
        scd.sc = sc;
// persistencefacade....
        try {
        	
        	Vector result = DbAccessCreator.instance().getResultCount(allCourses[0].getDwoProfile(), sc.getID());
        	Enumeration e = result.elements();
    		CourseData[] cd = scd.cd;
    		while (e.hasMoreElements()) {
				Hashtable object = (Hashtable) e.nextElement();
				int courseID = ((Number)object.get("courseID")).intValue();
				scd.setResults(courseID, cd);
			}
        
        } catch(Exception e) {e.printStackTrace();}
        
        scd.show();
        return scd.getSelectedCourses();
	}

	
	private boolean setResults(int courseID, CourseData[] cd) {
		for (int i = 0; i < cd.length; i++) {
			Course course = cd[i].course;
			if(course.getID() == courseID)
			{	cd[i].data = removeImage;
				return true;
			}
			if(cd[i].children!=null)
				if(setResults(courseID, cd[i].children))
					return true;
		}
		return false;
	}
	
	public void appendCourseData(CourseData data, DefaultMutableTreeNode node, Vector vector) {
		Course map = data.course;
		if(map.isWithChildren())
		{		
			Course[] courses = map.getChildren();
			data.children = new CourseData[courses.length];
	    	DefaultMutableTreeNode child; 
	    	for (int i = 0; i < courses.length; i++) {
				Course course = courses[i];
				CourseData coursedata = new CourseData(course);
				data.children[i] = coursedata;
				coursedata.select = Boolean.valueOf(vector.contains(course));
				child = new DefaultMutableTreeNode(coursedata);
				node.add(child);
				appendCourseData(coursedata, child, vector);
	    	}
		}
	}

}