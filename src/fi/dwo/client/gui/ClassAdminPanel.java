package fi.dwo.client.gui;

import java.awt.Component;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import fi.dwo.client.domain.ContactDocent;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.SchoolGroup;
import fi.dwo.client.domain.Teacher;
import fi.dwo.client.domain.User;
import fi.dwo.client.gui.UserManagementPanel.TeacherDelegate;
import fi.dwo.client.persistence.MapperCreator;
import fi.dwo.client.persistence.MapperIF;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.TextMapper;

public class ClassAdminPanel extends JPanel implements CenterSubPanel, Comparator {


	SchoolClass[] classes;
	Teacher[] teachers;
	HashMap teacherMap = new HashMap();
	DwoIF dwo;
	
	class ClassModel extends AbstractTableModel
	{
		public int getColumnCount() {
			return 2;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
		 */
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			if(columnIndex == 1)
			{
				teacherMap.put(classes[rowIndex], value);
			}
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		public String getColumnName(int column) {
			switch(column)
			{
			case 0: return "Klas";
			case 1: return "Docent";
			}
			return super.getColumnName(column);
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			return columnIndex == 1;
		}

		public int getRowCount() {
			return classes.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			SchoolClass c = classes[rowIndex];
			switch(columnIndex)
			{
			case 0: 
				return c.getName();
			case 1:
				return teacherMap.get(c);
			}
			return null;
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
	private CenterPanel center;

	public void end() {
	}

	public Component getComponent() {
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
		this.dwo = dwo;
		ContactDocent docent = (ContactDocent) dwo.getUser();
		School school = docent.getSchool();
		SchoolGroup[] groups = school.getSchoolGroupList();
		classes = school.getClassList();
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
			items[i] = teacher.getName();
			SchoolClass[] classlist = teacher.getClasses();
			for (int j = 0; j < classlist.length; j++) {
				SchoolClass schoolClass = classlist[j];
				teacherMap.put(schoolClass, teacher.getName());
			}
		}
		JTable table = new JTable(new ClassModel());
		ComboBoxEditor editor = new ComboBoxEditor(items);
		ComboBoxRenderer renderer = new ComboBoxRenderer(items);
		TableColumn col = table.getColumnModel().getColumn(1); 
		col.setCellEditor(editor);
		col.setCellRenderer(renderer);
		TableUtil.setDefaults(table, true, new ImageRenderer(), null);
		TableUtil.setJTableSizes(table);
		add(table.getTableHeader());
		add(table);
		
		JPanel box = new JPanel();
		add(box);
		JButton okBtn = new JButton("Opslaan");
		box.add(okBtn);
	}

	public int compare(Object arg0, Object arg1) {
		User u0 = (User) arg0;
		User u1 = (User) arg1;
		return u0.getName().compareTo(u1.getName());
	}

}
