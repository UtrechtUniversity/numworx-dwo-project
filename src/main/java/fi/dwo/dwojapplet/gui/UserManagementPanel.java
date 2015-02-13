package fi.dwo.dwojapplet.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import fi.dwo.client.domain.ContactDocent;
import fi.dwo.client.domain.DwoHelper;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.School;
import fi.dwo.client.domain.SchoolClass;
import fi.dwo.client.domain.SchoolGroup;
import fi.dwo.client.domain.Teacher;
import fi.dwo.client.domain.User;
import fi.dwo.client.gui.SchoolPanel.SchoolModel;

import fi.dwo.client.persistence.MapperCreator;
import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.LoginException;
import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.RegisterException;
import fi.dwo.client.system.SchoolException;
import fi.dwo.client.system.TextMapper;

public class UserManagementPanel extends JPanel implements CenterSubPanel {

	static class TeacherDelegate extends Teacher {
		User u;

		TeacherDelegate(User u) {
			super();
			this.u = u;
		}

		public boolean canLogout() {
			return u.canLogout();
		}

		public String getChildTitle() {
			return u.getChildTitle();
		}

		public String getEmail() {
			return u.getEmail();
		}

		public String getFirstname() {
			return u.getFirstname();
		}

		public int getID() {
			return u.getID();
		}

		public SchoolClass getInClass() {
			return u.getInClass();
		}

		public String getLastName() {
			return u.getLastName();
		}

		public String getMiddleName() {
			return u.getMiddleName();
		}

		public String getName() {
			return u.getName();
		}

		public String getOrderAscTitle() {
			return u.getOrderAscTitle();
		}

		public String getOrderDescTitle() {
			return u.getOrderDescTitle();
		}

		public String getOrderName() {
			return u.getOrderName();
		}

		public String getParentTitle() {
			return u.getParentTitle();
		}

		public School getSchool() {
			return u.getSchool();
		}

		public String getTitle() {
			return u.getTitle();
		}

		public String getType() {
			return u.getType();
		}

		public int getUserID() {
			return u.getUserID();
		}

		public String getUsername() {
			return u.getUsername();
		}

		public boolean isDeepestLevel() {
			return u.isDeepestLevel();
		}

		public boolean isHighestLevel() {
			return u.isHighestLevel();
		}

		public boolean isReadonly() {
			return u.isReadonly();
		}

		public void setEmail(String email) {
			u.setEmail(email);
		}

		public void setFirstname(String firstname) {
			u.setFirstname(firstname);
		}

		public void setInClass(SchoolClass inClass) {
			u.setInClass(inClass);
		}

		public void setLastName(String lastName) {
			u.setLastName(lastName);
		}

		public void setMiddleName(String middleName) {
			u.setMiddleName(middleName);
		}

		public void setSchool(School school) {
			u.setSchool(school);
		}

		public void setUserID(int userID) {
			u.setUserID(userID);
		}

		public void setUsername(String username) {
			u.setUsername(username);
		}

		public String toString() {
			return u.toString();
		}
		
	}
	
	
	
	public class ImageButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

	   	Object value;
    	UserModel model;
    	int row;
    	
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean arg2, int row, int col) {
			this.value = value;
			JButton button = new JButton(new ImageIcon((Image)value));
			button.addActionListener(this);
			this.row = row;
			model = (UserModel) table.getModel();
			return button;
		}

		public Object getCellEditorValue() {
			return value;
		}

		public void actionPerformed(ActionEvent event) {
			User user = model.userList[row];
			if(value == model.userImage || value == model.teacherImage)
			{
				try {
					MapperCreator.instance(User.class).removeObject(user.getID()); // not good enough, need fresh copy.
					GuiCreator.instance().login(user.getUsername(), null);
				} catch (LoginException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
			if(value == model.editImage)
			{
				try {
					String newPassword =  JOptionPane.showInputDialog(UserManagementPanel.this, TextMapper.getText(TextMapper.GUIP_PASSWORD), user.getUsername(), JOptionPane.QUESTION_MESSAGE);
					if(newPassword != null)
					{
						PersistenceFacade.instance().changeAccount(user, null, newPassword, user.getFirstname(), user.getMiddleName(), user.getLastName(), user.getEmail());
						model.fireTableRowsUpdated(row, row);
						JOptionPane.showMessageDialog(UserManagementPanel.this, TextMapper.getText(TextMapper.GUIP_MSG_PROFILE_CHANGED));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else
			if (value == model.removeImage)
			{
                /* Delete the school */
                User u = user;
                if (u != docent) {
					final String title = TextMapper.getText(TextMapper.GUIS_DELETE_STUDENT);
					final String text = MessageFormat.format(TextMapper.getText(TextMapper.GUIS_MSG_DELETE_STUDENT), new Object[] { u.getName() });
					Box box = Box.createVerticalBox();
					box.add(new JLabel(text + "?"));
					JRadioButton delRadio = new JRadioButton(TextMapper.getText(TextMapper.GUIUMP_REMOVE_FROM_SCHOOL), true);
					JRadioButton rmRadio  = new JRadioButton(TextMapper.getText(TextMapper.GUIUMP_REMOVE_COMPLETE), false);
					ButtonGroup group = new ButtonGroup();
					group.add(delRadio);
					group.add(rmRadio);
					box.add(delRadio);
					box.add(rmRadio);
					if (JOptionPane.showConfirmDialog(UserManagementPanel.this, box,
					       title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						u.setSchool(docent.getSchool());
						if(delRadio.isSelected())
							PersistenceFacade.instance().deleteUserFromSchool(u);
					    if(rmRadio.isSelected())
							try {
								PersistenceFacade.instance().deleteUser(u);
							} catch (RegisterException e) {
					        	JOptionPane.showMessageDialog(UserManagementPanel.this, e.getMessage());
							}
						//center.loadMenu();
						fireEditingCanceled();
						model.deleteRow(row);
					    return;
					}
				}
			}
			fireEditingStopped();
		}
	}

	private CenterPanel center;
	private DwoIF dwo;
	private ContactDocent docent;
	private User[] userList;
	private RegisterClassListButton addDocentBtn;
	private SchoolGroup[] groups;
	private UserModel dm;
	
	public UserManagementPanel(DwoIF dwo) {
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setSize(620, 485);
        Image removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_STUDENT_IMAGE);
        Image editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_SCO_IMAGE);
        Image userImage = DwoHelper.getResourceImage("resources/student.png" );
        Image teacherImage = DwoHelper.getResourceImage("resources/docent.png");
		this.dwo = dwo;
		docent = (ContactDocent) dwo.getUser();
		School school = docent.getSchool();
		groups = school.getSchoolGroupList();
		getUserList();
		
		dm = new UserModel();
		dm.userList = userList; userList = null;
		dm.editImage = editImage;
		dm.removeImage = removeImage;
		dm.teacherImage = teacherImage;
		dm.userImage = userImage;
		JTable table = new JTable(dm);
    	TableUtil.setDefaults(table, true, new ImageRenderer(), new ImageButtonEditor());
    	
		SchoolClassTableRenderer renderer = new SchoolClassTableRenderer(school);
		table.setDefaultRenderer(SchoolClass.class, new SchoolClassTableRenderer(renderer.getItems()));
    	table.setDefaultEditor(SchoolClass.class, new DefaultCellEditor(renderer));
    	TableUtil.setJTableSizes(table);
    	Box b = Box.createVerticalBox();
    	b.add(table.getTableHeader());
    	b.add(table);
		addDocentBtn = new RegisterClassListButton();
		b.add(addDocentBtn);
		add(b);
		
	}
	private void getUserList() {
		userList = new User[0];
		for (int i = 0; i < groups.length; i++) {
			SchoolGroup schoolGroup = groups[i];
			try {
				User[] u = (User[]) PersistenceFacade.instance().get(User.class, schoolGroup);
				if(schoolGroup.getGroupID()==SchoolGroup.TEACHER ||
				   schoolGroup.getGroupID()== SchoolGroup.SCHOOLADMIN)
				{
					for (int j = 0; j < u.length; j++) {
						if(!(u[j] instanceof Teacher))
							u[j] = new TeacherDelegate(u[j]); // force Teacher!
					}
				}
				merge(u);
			} catch (PersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Arrays.sort(userList);
	}
    /**
     * Delete user! 
     * TODO delete user from School en in fidentity regelen
     * @param u
     */
	public void deleteUser(User u) {
        try {
            PersistenceFacade.instance().deleteUser(u);
        } catch (RegisterException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
	}

	private void merge(User[] u) {
		if(u == null || u.length == 0)
			return;
		if(userList.length == 0)
			userList = u;
		else {
			User[] nu = new User[userList.length+u.length];
			System.arraycopy(u, 0, nu, 0, u.length);
			System.arraycopy(userList, 0, nu, u.length, userList.length);
			userList = nu;
		}
	}

	public void end() {
		// TODO Auto-generated method stub

	}

	public JComponent getComponent() {
		return this;
	}

	public Component getHeaderPanel() {
		// TODO Auto-generated method stub
		return new HeaderPanel(TextMapper.getText(TextMapper.GUIUMP_MANAGE_USERS));
	}

	public void setCenterPanel(CenterPanel centerPanel) {
		center = centerPanel;

	}
	public Object getUserObject() {
		return docent.getSchool();
	}
	
	public void stateChanged(ChangeEvent e) {
		if(e.getSource() instanceof School || e.getSource() instanceof SchoolClass)
		{
			getUserList();
			dm.userList = userList; userList = null;
			dm.fireTableDataChanged();
			repaint();
		}
	}
	

}


class SchoolClassTableRenderer extends JComboBox implements TableCellRenderer
{
	private final DefaultTableCellRenderer NULL = new DefaultTableCellRenderer();
	private Vector items;
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component component = NULL.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if(!table.getModel().isCellEditable(row, column)) {
			return component;
		}
		setForeground(NULL.getForeground());
		setBackground(NULL.getBackground());
		setBorder(NULL.getBorder());
		setFont(NULL.getFont());
		
		setSelectedItem(value);
		
		return this;
	}

	SchoolClassTableRenderer(Vector items) {
		super(items);
		this.items = items;
		
	}
	
	Vector getItems() {
		return items;
	}
	
	SchoolClassTableRenderer(School school) {
		this(toVector(school));
	}

	SchoolClassTableRenderer(User user) {
		this(toVector(user));
	}

	private static Vector toVector(School school) {
	   	SchoolClass[] classes = school.getClassList();
	   	return toVector(classes);
	}
	
	private static Vector toVector(User teacher) {
		return toVector(teacher.getSchool());
	}
	
	private static Vector toVector(SchoolClass[] classes) {
		Vector v = new Vector();
    	v.add(null);
     	for (int i = 0; i < classes.length; i++) {
			v.add(classes[i]);
		}
    	return v;
	}
	
	
}
