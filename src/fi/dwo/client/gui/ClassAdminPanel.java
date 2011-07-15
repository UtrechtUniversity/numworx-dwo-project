package fi.dwo.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.AbstractCellEditor;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.xmlrpc.applet.XmlRpcException;

import fi.beans.jdbc.DbConnect;
import fi.dwo.client.domain.ContactDocent;
import fi.dwo.client.domain.Course;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.SchoolGroup;
import fi.dwo.client.domain.Teacher;
import fi.dwo.client.domain.User;
import fi.dwo.client.gui.ClassPanel.ClassModel;
import fi.dwo.client.gui.UserManagementPanel.TeacherDelegate;
import fi.dwo.client.persistence.DbAccessCreator;
import fi.dwo.client.persistence.MapperCreator;
import fi.dwo.client.persistence.MapperIF;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.ClassException;
import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.TextMapper;
import fi.dwo.server.persistence.DwoXmlRpcException;

public class ClassAdminPanel extends JPanel implements CenterSubPanel, Comparator, ActionListener {


	SchoolClass[] classes;
	boolean[] dirty;
	Teacher[] teachers;
	HashMap teacherMap = new HashMap();
	HashMap nameMap = new HashMap();
	HashMap oldTeacher = new HashMap();
	DwoIF dwo;
	
	static final int CLASS_NAME = 0;
	static final int CLASS_USER = 1;
	static final int CLASS_MEMBERS = 2;
	static final int CLASS_DELETE = 3;
	
	Image usersImage, removeImage;
	
	class ClassModel extends AbstractTableModel
	{
		public int getColumnCount() {
			return 4;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
		 */
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			if(columnIndex == CLASS_USER)
			{
				dirty[rowIndex] = true;
				SchoolClass c = classes[rowIndex];
				Teacher t = (Teacher) nameMap.get(value);
				Teacher o = (Teacher) oldTeacher.get(c);
				try {
					if(DbAccessCreator.instance().reassignClass(c.getID(), t.getID()))
					{
						teacherMap.put(classes[rowIndex], value);	
						o.deleteClass(c);
						t.addClass(c);
						oldTeacher.put(c, t);
						center.loadMenu();
						fireTableCellUpdated(rowIndex, columnIndex);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 

			} else if(columnIndex == CLASS_NAME)
			{
				SchoolClass schoolClass = classes[rowIndex];
				if(dwo.renameClass(schoolClass, value.toString())) 
				{
					dirty[rowIndex] = true;
					center.loadMenu();
					fireTableCellUpdated(rowIndex, columnIndex);
				}
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		public String getColumnName(int column) {
			switch(column)
			{
			case CLASS_NAME: return "Klas";
			case CLASS_USER: return "Docent";
			case CLASS_MEMBERS: return "Leerlingen";
			case CLASS_DELETE: return "Verwijder";

			}
			return super.getColumnName(column);
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		public int getRowCount() {
			return classes.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			SchoolClass c = classes[rowIndex];
			switch(columnIndex)
			{
			case CLASS_NAME: 
				return c.getName();
			case CLASS_USER:
				return teacherMap.get(c);
			case CLASS_MEMBERS:
				return usersImage;
			case CLASS_DELETE:
				return removeImage;
			}
			return null;
		}

		public Class getColumnClass(int columnIndex) {
			switch(columnIndex)
			{
			case CLASS_NAME:
			case CLASS_USER:
					return String.class;
			case CLASS_MEMBERS:
			case CLASS_DELETE:
					return Image.class;
			}
			return super.getColumnClass(columnIndex);
		}

		public void removeRow(int row) {	
			SchoolClass[] sc = new SchoolClass[classes.length-1];
			System.arraycopy(classes, 0, sc, 0, row);
			System.arraycopy(classes, row+1, sc,row, sc.length-row);
			classes = sc;
			fireTableRowsDeleted(row,row);
		}

	}

	public class ComboBoxRenderer extends JComboBox implements TableCellRenderer
	{ 
		public ComboBoxRenderer(String[] items)
		{ 
			super(items); 
		}
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{ 
			if (isSelected) 
			{ 
				setForeground(table.getSelectionForeground()); 
				super.setBackground(table.getSelectionBackground()); 
			} else
			{ 
				setForeground(table.getForeground());
				setBackground(table.getBackground()); 
			} // Select the current value 
			setSelectedItem(value);
			return this;
		}
	} 
	
	public class ComboBoxEditor extends DefaultCellEditor
	{ 
		public ComboBoxEditor(String[] items)
		{ super(new JComboBox(items)); 
		}
	}

    public class ImageButtonEditor extends AbstractCellEditor implements
	TableCellEditor, ActionListener {

    	Object value;
    	ClassModel model;
    	int row;

    	public Component getTableCellEditorComponent(JTable table, Object value,
    			boolean arg2, int row, int col) {
    		this.value = value;
    		JButton button = new JButton(new ImageIcon((Image)value));
    		button.addActionListener(this);
    		this.row = row;
    		model = (ClassModel) table.getModel();
    		return button;
    	}

    	public Object getCellEditorValue() {
    		return value;
    	}

    	public void actionPerformed(ActionEvent event) {
            SchoolClass sc = classes[row];
    		if (value == removeImage) {
                /* Delete the course */
                if (JOptionPane.showConfirmDialog(ClassAdminPanel.this, TextMapper.getText(TextMapper.GUIC_MSG_DELETE_CLASS)
                        + "?", TextMapper.getText(TextMapper.GUIC_DELETE_CLASS), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    if (GuiCreator.instance().deleteClass(sc)) {
                        center.loadMenu();
        	            model.removeRow(row);
                    }
                }
    		} else if (value == usersImage) {
                center.loadCenter(GuiCreator.instance().getClassUsersPanel(sc));
    		}
    		fireEditingStopped();
    	}

}
	
	private CenterPanel center;

	public void end() {
	}

	public JComponent getComponent() {
		return this;
	}

	public Component getHeaderPanel() {
    	return new HeaderPanel("Klassen toewijzen");
	}

	public void setCenterPanel(CenterPanel centerPanel) {
		center = centerPanel;
	}

	private void merge(Teacher[] u) {
		if(u == null || u.length == 0)
			return;
		if(teachers.length == 0)
			teachers = u;
		else {
			Teacher[] nu = new Teacher[teachers.length+u.length];
			System.arraycopy(u, 0, nu, 0, u.length);
			System.arraycopy(teachers, 0, nu, u.length, teachers.length);
			teachers = nu;
		}
	}

	/**
	 * 
	 */
	public ClassAdminPanel(DwoIF dwo) {
		super(null);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setOpaque(false);
		setBackground(GuiConstants.MAIN_BACKGROUND);
		this.dwo = dwo;
        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_CLASS_IMAGE);
        usersImage = DwoHelper.getResourceImage(GuiConstants.USERS_CLASS_IMAGE);
		ContactDocent docent = (ContactDocent) dwo.getUser();
		School school = docent.getSchool();
		SchoolGroup[] groups = school.getSchoolGroupList();
		classes = school.getClassList();
		if(classes == null)
			classes = new SchoolClass[0];
		dirty = new boolean[classes.length];
		MapperIF usermapper =  MapperCreator.instance(User.class);
		teachers = new Teacher[0];
		for (int i = 0; i < groups.length; i++) {
			SchoolGroup schoolGroup = groups[i];
			try {
				if(schoolGroup.getGroupID()==SchoolGroup.TEACHER ||
				   schoolGroup.getGroupID()== SchoolGroup.SCHOOLADMIN)
				{
					User[] u = (User[]) PersistenceFacade.instance().get(User.class, schoolGroup);
					Teacher[] t = new Teacher[u.length];
					for (int j = 0; j < u.length; j++) {
						User uj = u[j];
						if(!(uj instanceof Teacher))
						{
							usermapper.removeObject(uj.getUserID());
							t[j] = (Teacher) PersistenceFacade.instance().login(uj.getUsername());
						} else
							t[j] = (Teacher) uj;
							
					}
					merge(t);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Arrays.sort(teachers, this);

		String[] items = new String[teachers.length];
		for(int i = 0; i < teachers.length; i++)
		{	
			Teacher teacher = teachers[i];
			items[i] = teacher.getName() + " (" + teacher.getUsername() + ")";
			nameMap.put(items[i], teacher);
			SchoolClass[] classlist = teacher.getClasses();
			for (int j = 0; j < classlist.length; j++) {
				SchoolClass schoolClass = classlist[j];
				teacherMap.put(schoolClass, items[i]);
				oldTeacher.put(schoolClass, teacher);
			}
		}
		
		JTable table = new JTable(new ClassModel());
		ComboBoxEditor editor = new ComboBoxEditor(items);
		ComboBoxRenderer renderer = new ComboBoxRenderer(items);
		TableColumn col = table.getColumnModel().getColumn(1); 
		col.setCellEditor(editor);
		col.setCellRenderer(renderer);
		TableUtil.setDefaults(table, true, new ImageRenderer(), new ImageButtonEditor());
		TableUtil.setJTableSizes(table);
		add(table.getTableHeader());
		add(table);
		
		JPanel box = new JPanel();
		box.setOpaque(true);
		box.setBackground(GuiConstants.MAIN_BACKGROUND);
		add(box);

	}

	public int compare(Object arg0, Object arg1) {
		User u0 = (User) arg0;
		User u1 = (User) arg1;
		return u0.getName().compareTo(u1.getName());
	}

	public void actionPerformed(ActionEvent e) {
		for(int i = 0; i < dirty.length; i++)
			if(dirty[i])
			{
				SchoolClass c = classes[i];
				String name = teacherMap.get(c).toString();
				Teacher t = (Teacher) nameMap.get(name);
				Teacher o = (Teacher) oldTeacher.get(c);
				o.deleteClass(c);
				t.addClass(c);
				oldTeacher.put(c, t);
				// dbaccess.....
				dirty[i] = false;
			}
		
		center.loadMenu();
	}

	public Object getUserObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
