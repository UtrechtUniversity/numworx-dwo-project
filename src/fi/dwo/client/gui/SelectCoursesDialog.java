// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\SelectCoursesDialog.java

package fi.dwo.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SpinnerDateModel;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
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

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JSpinnerDateEditor;

import fi.dwo.client.domain.ClassCourse;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.CourseMap;
import fi.dwo.client.domain.DWO;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.User;
import fi.dwo.client.persistence.DbAccessCreator;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.TextMapper;

class CourseData implements CourseMap {
	Course course;
	Object select;
	Date van, tot;
	
	public CourseData(Course course) {
		this.course = course;
		if(!course.isWithChildren())
			select = Boolean.FALSE;
		if(course.link != null)
		{
			van = course.link.getNotBefore();
			tot = course.link.getNotAfter();
			type = course.link.getType();
		}
	}
	Image data;
	public String toString() {
		return String.valueOf(course);
	}
	
	public boolean isSelected() {
		if(course.isWithChildren()) {
			for(int i = 0; i < children.length; i++) {
				if(children[i].isSelected())
					return true;
			}
			return false;
		}		
		return Boolean.TRUE.equals(select);
	}	
	CourseData[] children;
	public int type;

	public void addChild(Course c) {
	}
	public Set getChildNames() {
		return null;
	}
	public CourseMap[] getChildren() {
		return children;
	}
	public CourseMap getParentMap() {
		return null;
	}
	public Object getUserObject() {
		return  course;
	}
	public void removeChild(int i) {
	}
	public void setChildren(CourseMap[] courses) {
	}	
	
}
/**
 * This class represents a dialog for selecting courses.
 * 
 * @author M.J.B. Kupers
 *  
 */
public final class SelectCoursesDialog extends JDialog implements ActionListener {

	class EnhancedIcon implements Icon {
		private Icon icon;
		private Color color = Color.red;
		Font font = new Font("Arial", Font.BOLD, 20);
		private String str;
		public int getIconHeight() {
			return icon.getIconHeight();
		}

		public int getIconWidth() {
			return icon.getIconWidth();
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			icon.paintIcon(c, g, x, y);
			g.setColor(color);
			Font f = g.getFont();
			g.setFont(font);
			g.drawString(str, x+3, y+getIconHeight());
			g.setFont(f);
		}

		public EnhancedIcon(Icon icon, Color color, String str) {
			super();
			this.icon = icon;
			this.color = color;
			this.str = str;
		}
		
	}
	
    public class SelectCellRenderer extends DefaultTreeCellRenderer implements
			TreeCellRenderer {

    	CourseData cd;
    	
    	Icon selectedLeafIcon = new EnhancedIcon(getDefaultLeafIcon(), Color.black, "√");
    	Icon selectedOpenIcon = new EnhancedIcon(getDefaultOpenIcon(), Color.black, "/");
    	Icon dataLeafIcon = new EnhancedIcon(getDefaultLeafIcon(), Color.red, "×");
    	
		public Icon getClosedIcon() {
			if(cd != null && cd.isSelected())
				return selectedOpenIcon;
			return super.getClosedIcon();
		}

		public Icon getLeafIcon() {
			if(cd != null && cd.isSelected())
				return selectedLeafIcon;
			if(cd != null && cd.data != null) 
				return dataLeafIcon;	 // met data.
			return super.getLeafIcon();
		}



		public Icon getOpenIcon() {
			if(cd != null && cd.isSelected())
				return selectedOpenIcon;
			return super.getOpenIcon();
		}



		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			
			cd = null;
			
			if(value instanceof DefaultMutableTreeNode)
			{
				Object o = ((DefaultMutableTreeNode) value).getUserObject();
				if(o instanceof CourseData) {
					cd = (CourseData)o;		
				}
			}
			
			
			
			return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
					row, hasFocus);
		}

    	
    	
    	
	}





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
			CourseData[] cd = model.getCD();
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
    			CourseData s2 = cd[row+1];
    			CourseData s  = cd[row];
    			cd[row] = s2;
    			cd[row+1] = s;
    			model.fireTableRowsUpdated(row, row+1);
    			updown = true;
    			
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
	private JTree  jTree;
	
	private Image removeImage, upImage, downImage;

	private SchoolClass sc;

	CourseData[] cd;

	private ModuleTreePanel tree;
	private DefaultTreeModel treeModel;

	class CoursesModel extends AbstractTableModel {

    	int columnCount = 2;
    	CourseData[] cd;
    	
    	CoursesModel() {
    		this.cd = SelectCoursesDialog.this.cd;
    	}
    	
    	CoursesModel(CourseData[] cd) {
    		this.cd = cd;
    	}
    	
    	
    	CourseData[] getCD() { return cd; }
    	void setCD(CourseData[] cd) {
    		this.cd = cd;
    		fireTableDataChanged();
    	}
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
			case 0: 
					if(cd[rowIndex].course.isWithChildren())
						return null; // no choice!
					return cd[rowIndex].select;
			case 1: return cd[rowIndex].course.getName();
			case 2: return cd[rowIndex].data;
			case 3: if(rowIndex != 0) return upImage;
					break;
			case 4: if(rowIndex != getRowCount()-1) return downImage;
					break;
			case 5: 
				if(cd[rowIndex].course.isWithChildren())
					return null; // no choice!
				return OBJECT_TYPE[ cd[rowIndex].type ];
			case 6: return cd[rowIndex].van;
			case 7: return cd[rowIndex].tot;
			}
			return null;
		}

		public Class getColumnClass(int columnIndex) {
			if(columnIndex >= 2 && columnIndex <= 4)
				return Image.class;
			if(columnIndex == 0)
				return Boolean.class;
			if(columnIndex >= 6 && columnIndex <= 7) {
				return Date.class;
			}
			return super.getColumnClass(columnIndex);
		}

		public String getColumnName(int column) {
			switch(column) {
			case 5: return "soort";
			case 6: return "vanaf";
			case 7: return "tot aan";
			
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
			case 5: case 6: case 7:
				return Boolean.TRUE.equals( cd[rowIndex].select );
			case 0: return !cd[rowIndex].course.isWithChildren();
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
				fireTableRowsUpdated(rowIndex, rowIndex); // effect op col 5,6,7
				if(jTree != null)
					jTree.repaint();
				break;
			case 2:
				cd[rowIndex].data = (Image) aValue;
				if(jTree != null)
					jTree.repaint();
				break;
			case 5: if (OBJECT_TYPE[1].equals(aValue))
						cd[rowIndex].type = 1;
					else
						cd[rowIndex].type = 0;
				break;
			case 6: cd[rowIndex].van = (Date) aValue;
				break;
			case 7: cd[rowIndex].tot = (Date) aValue;
			
			}
			fireTableCellUpdated(rowIndex, columnIndex);
		}
		
    }
	
	private static String date2String(Object value) {
		if(value != null) 
		{
			return DATE_TIME.format(value);
		} else 
			return "  :     -   -    ";
	}

	static class BooleanRenderer extends JCheckBox implements TableCellRenderer {
		private JLabel label;

		public BooleanRenderer() {
			super();
			setHorizontalAlignment(JLabel.CENTER);
			label = new JLabel();
			label.setIcon(UIManager.getIcon("Tree.openIcon")); // of closedIcon
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				super.setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(table.getBackground());
			}
			if (value == null)
				return label;

			setSelected(((Boolean) value).booleanValue());
			return this;
		}
	    }

	class DateCellRenderer extends DefaultTableCellRenderer
	{

		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
		 */
		protected void setValue(Object value) {
			value = date2String(value);
			super.setValue(value);
		}


		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean select, boolean focus, int row, int col) {
			super.getTableCellRendererComponent(table, value, select, focus, row, col);
			if( ! Boolean.TRUE .equals (table.getValueAt(row, 0)) )
					setText("");
			return this;
		}
		
	}
	
	static class TypeCellEditor extends DefaultCellEditor {

		public TypeCellEditor(JComboBox box) {
			super(box);
		}
		
		private static JComboBox createComboBox() {
			JComboBox box = new JComboBox(OBJECT_TYPE);
			return box;
		}

		public TypeCellEditor() {
			this(createComboBox());
		}		
	}
	
	static class DateCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener
	{

		private Date value;
		private JButton btn;
		private String wat;
		
		public DateCellEditor() {
			super();
			btn = new JButton();
			btn.setBorderPainted(false);
			btn.setContentAreaFilled(false);
			btn.addActionListener(this);
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			this.value = (Date)value;
			wat = ""; // of via constructor?
			if(column == 6) wat = "vanaf";
			if(column == 7) wat = "tot";
			btn.setText(date2String(value));
			return btn;
		}

		public Object getCellEditorValue() {
			return value;
		}

		public void actionPerformed(ActionEvent e) {
			value = changeDate(wat, value);
			fireEditingStopped();
		}
		
	}
	
	
	
	
    
	static final Object[] OBJECT_TYPE = new Object[] { "normaal", "afgeschermd" };
    class CheckBoxNodeRenderer implements TreeCellRenderer {
		JCheckBox leafRenderer = new JCheckBox();
    	  JButton   eraseBtn = new JButton();
    	  JButton 	vanBtn = new JButton("_________________");
    	  JButton	totBtn = new JButton("_________________");
    	  JComboBox typeBtn = new JComboBox(OBJECT_TYPE);
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
    				box.add(typeBtn);
        	        DateFormatSymbols newFormatSymbols =  new DateFormatSymbols(DwoHelper.getApplet().getLocale());
    				DATE_TIME.setDateFormatSymbols(newFormatSymbols);
    				vanBtn.setPreferredSize(vanBtn.getPreferredSize()); // fixed size.
    				vanBtn.setMinimumSize(vanBtn.getPreferredSize());
    				vanBtn.setMaximumSize(vanBtn.getPreferredSize());
    				totBtn.setPreferredSize(totBtn.getPreferredSize());
    				totBtn.setMaximumSize(totBtn.getPreferredSize());
    				totBtn.setMinimumSize(totBtn.getPreferredSize());
    				
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
    	          } else //          "HH:mm dd-MMM-yyyy"
    	        	  vanBtn.setText("  :     -   -    ");
    	          if(node.tot != null) {
    	        	  totBtn.setText(DATE_TIME.format(node.tot));
    	          } else 
    	        	  totBtn.setText("  :     -   -    ");
    	          typeBtn.setSelectedIndex(node.type);
    	          
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

    
	  static Date changeDate(String hoe, Date van) {
  		Date orig = van;
  		if(van == null) 
  			van = new Date();
  		Locale locale = DwoHelper.getApplet().getLocale();
  		Box message = Box.createHorizontalBox();
  		final Date datum = van;
  		SpinnerDateModel model = new SpinnerDateModel();
  		model.setValue(van);
  		model.setCalendarField(Calendar.HOUR_OF_DAY);
			final JSpinner timeChooser = new JSpinner(model);

  		JSpinner.DateEditor editor = new JSpinner.DateEditor(timeChooser, "HH:mm");
  		editor.getFormat().setDateFormatSymbols(new DateFormatSymbols(locale));
  		timeChooser.setEditor(editor);    		
  		message.add(new JLabel("tijd:"));
  		message.add(timeChooser);
  		JSpinnerDateEditor dateEditor = new JSpinnerDateEditor();
  		dateEditor.setLocale(locale);
			JDateChooser dayChooser = new JDateChooser(null, van, null,
  				dateEditor);
  		
			dayChooser.setLocale(locale);
			dayChooser.setDateFormatString("dd-MM-yyyy"); // bug in locale van spinnerdateeditor
  		message.add(new JLabel(" dag: "));
  		message.add(dayChooser);
  		int r = JOptionPane.showConfirmDialog(DwoHelper.getApplet(), message, "Geef tijdstip " + hoe, JOptionPane.YES_NO_CANCEL_OPTION);
  		if(r == JOptionPane.YES_OPTION) {
  			van = dayChooser.getDate();
  			Date t = (Date) timeChooser.getValue();
  			van.setMinutes(t.getMinutes());
  			van.setHours(t.getHours());
  			orig = van;
  		} else if (r == JOptionPane.NO_OPTION) {
  			orig = null;
  		}

			return orig;
		}

    
    
    
    
	static final SimpleDateFormat DATE_TIME = new SimpleDateFormat("HH:mm dd-MMM-yyyy");
    private class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {


		CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer(true);

    	  ChangeEvent changeEvent = null;

    	  JTree tree;

		private ItemListener typeAction = new ItemListener() {

			public void itemStateChanged(ItemEvent event) {
				System.out.println(event);
				if(event.getStateChange() == event.SELECTED)
				{
		     	    CourseData checkBoxNode = (CourseData)userObject;
		     	    checkBoxNode.type = renderer.typeBtn.getSelectedIndex();
				}
			}
			
		};

    	  public CheckBoxNodeEditor(JTree tree) {
    	    this.tree = tree;
      	    	renderer.leafRenderer.addItemListener(itemListener);
      	    	renderer.eraseBtn.addActionListener(eraseAction );
      	    	renderer.vanBtn.addActionListener(vanAction);
      	    	renderer.totBtn.addActionListener(totAction);
      	    	renderer.typeBtn.addItemListener(typeAction);
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
	     	    checkBoxNode.van = changeDate("vanaf", checkBoxNode.van);
	     	    itemListener.itemStateChanged(null);				
			}
		};
		private ActionListener totAction = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
	     	    CourseData checkBoxNode = (CourseData)userObject;
	     	    checkBoxNode.tot = changeDate("tot", checkBoxNode.tot);
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
// nog even uit bij productie.
        //vantot = cnt != 2;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(GuiConstants.MAIN_BACKGROUND);
        setBackground(GuiConstants.MAIN_BACKGROUND);
        setSize(cnt == 2 ? 600 : 800, 310);
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

        if(CenterPanel.isIconizer())
        {
        	
        	tree = new ModuleTreePanel() {

        		protected void createModel(DwoIF dwo) {
//        			super.createModel(null);
        			
        			
        	        DefaultMutableTreeNode root = new DefaultMutableTreeNode(ALLE_MODULES);
        	        DefaultMutableTreeNode dwonode  = new DefaultMutableTreeNode(STANDAARD_DWO_MODULES);
        	        final StandaardMap standaard_map = new StandaardMap(dwonode);
        	        dwonode.setUserObject(standaard_map);
        	        root.add(dwonode);        
        	        DefaultMutableTreeNode schoolnode = null;
        	        DefaultTreeModel model = new DefaultTreeModel(root);
        	        setModel(model);

        			setStrategy(new SelectStrategy() {

						public void nodeSelected(CourseMap node) {
							CourseData[] children;
								CourseMap[] courses = node.getChildren();
								if(courses == null)
								{
									children = new CourseData[0]; // op een verkeerde course.
									if(node instanceof CourseData) {
										children = new CourseData[] { (CourseData) node } ;
									}
								}
								else if(courses instanceof CourseData[])
									children  = (CourseData[]) courses;
								else if(courses.length > 0 && courses[0] instanceof CourseData) 
								{
									children = new CourseData[courses.length];
									System.arraycopy(courses, 0, children, 0, children.length);
								} else
									children = cd;
							((CoursesModel) jTable.getModel()).setCD(children);
						}

						public JPopupMenu nodeAction(CourseMap node) {
							return null;
						}});
// TODO moet een eigen renderer worden!
        			tree.setCellRenderer(new SelectCellRenderer());
        			schoolnode = dwonode;
                	GuiCreator instance = GuiCreator.instance();
                	if(instance.getMainPanel()!= null)
                		setCenterPanel(instance.getMainPanel().getCenter());
        			User u = instance.getUser();
                	School school = u.getSchool();
                	if(school != null)
                	{  	schoolnode = new DefaultMutableTreeNode("Modules " + school);
//                		if(u.hasRight(User.MODIFY_MODULES_RIGHT))
//                			root.add(schoolnode); // of alleen als leeg...
                	}
                	boolean needSchoolnode = false;
                	DefaultMutableTreeNode node; 
                	for (int i = 0; i < cd.length; i++) {
        				CourseData course = cd[i];
        				node = new DefaultMutableTreeNode(course);
        				
        				if(cd[i].course.getSchoolID() == 0)
        				{
        					dwonode.add(node);
        				} else {
        					schoolnode.add(node);
        					needSchoolnode = true;
        				}
        				appendCourseData(course, node, vSelectedCourses);
                	}
// FIX, altijd schoolnode toevoegen als nodig, ook zonder MODIFY_MODULES recht
                	if(needSchoolnode && schoolnode != dwonode)
                	{
                		root.add(schoolnode);
                		schoolnode.setUserObject(new StandaardMap(schoolnode));
                	}
                	treeModel = new DefaultTreeModel(root);
                	root.setUserObject(new StandaardMap(root)); // deze wil niet....
                	setModel(treeModel);
                	//tree2.setCellRenderer(new CheckBoxNodeRenderer(true));
                	//tree2.setCellEditor(new CheckBoxNodeEditor(tree2));
                	//tree2.setEditable(true);
        		}
			
        		protected void createCloseBtn(Box bar) {}
        		protected void createMenubar(Box bar) {}
        	};
			tree.createModel(null);
			
        	JSplitPane split = new JSplitPane();
        	JScrollPane comp; // = new JScrollPane(tree);
        	jTree = tree.tree;
        	tree.setMinimumSize(new Dimension(200,40));
			split.setLeftComponent(tree);
        	split.setDividerLocation(0.20);
        	
        	DefaultMutableTreeNode r = (DefaultMutableTreeNode) treeModel.getRoot();
        	// selecteer root!
        	
        	CoursesModel cm = new CoursesModel();
            cm.setColumnCount(cnt);
            jTable = new JTable(cm);
            initializeJTable(cnt);
            split.setRightComponent(new JScrollPane(jTable));
	        contentPane.add(split, BorderLayout.CENTER);
        } else {
            CoursesModel cm = new CoursesModel(); 
            cm.setColumnCount(cnt);
        	jTable = new JTable(cm);
        	initializeJTable(cnt);
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

	private void initializeJTable(int cnt) {
		TableCellRenderer imageRenderer = new ImageRenderer();
		TableCellEditor imageEditor = new ImageEditor();
		jTable.setDefaultRenderer(Date.class, new DateCellRenderer());
		jTable.setDefaultRenderer(Boolean.class, new BooleanRenderer());
		jTable.setDefaultEditor(Date.class, new DateCellEditor());
		if(cnt > 5)
			jTable.getColumnModel().getColumn(5).setCellEditor(new TypeCellEditor());
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
        int cnt = 3; // 2 voor select course voor resultaat. 3+2+3 voor selectcourse voor klas.
        if(DWO.SEQUENCE)
        {
        	cnt += 2;
        	if( CenterPanel.isIconizer())
        		cnt += 3; // VAN en TOT en AFGESCHERMD
        }
        SelectCoursesDialog scd = new SelectCoursesDialog(parent, title, true, allCourses, selectedCourses, cnt);
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
			CourseMap[] courses = map.getChildren();
			data.children = new CourseData[courses.length];
	    	DefaultMutableTreeNode child; 
	    	for (int i = 0; i < courses.length; i++) {
				Course course = (Course) courses[i];
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