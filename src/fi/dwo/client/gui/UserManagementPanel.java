package fi.dwo.client.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

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

public class UserManagementPanel extends JPanel implements CenterSubPanel, Comparator {

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
			User user = userList[row];
			if(value == userImage || value == teacherImage)
			{
				try {
					MapperCreator.instance(User.class).removeObject(user.getID()); // not good enough, need fresh copy.
					GuiCreator.instance().login(user.getUsername(), null);
				} catch (LoginException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
			if(value == editImage)
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
			if (value == removeImage)
			{
                /* Delete the school */
                User u = user;
                if (u != docent) {
					final String title = TextMapper.getText(TextMapper.GUIS_DELETE_STUDENT);
					final String text = MessageFormat.format(TextMapper.getText(TextMapper.GUIS_MSG_DELETE_STUDENT), new Object[] { u.getName() });
					if (JOptionPane.showConfirmDialog(UserManagementPanel.this, text
					       + "?", title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						u.setSchool(docent.getSchool());
					    PersistenceFacade.instance().deleteUserFromSchool(u);
					    //center.loadMenu();
					    model.deleteRow(row);
					       
					}
				}
			}
			fireEditingStopped();
		}
	}

	private CenterPanel center;
	private DwoIF dwo;
	private ContactDocent docent;

	User[] userList;
    private Image removeImage, editImage, userImage, teacherImage;

	
	class UserModel extends AbstractTableModel {

		public int getColumnCount() {
			return 5;
		}

		public void deleteRow(int row) {
			User[] nu = new User[userList.length-1];
			System.arraycopy(userList, 0, nu, 0, row);
			System.arraycopy(userList, row+1, nu, row, nu.length-row);
			userList = nu;
			fireTableRowsDeleted(row, row);
		}

		public int getRowCount() {
			return userList.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex) {
			case 1: 
					return userList[rowIndex].getUsername();
			case 0:
					return userList[rowIndex].getName();
			case 2:
					return userList[rowIndex] instanceof Teacher ? teacherImage: userImage;
			case 3: 
					return editImage;
			case 4:
					return removeImage;
			}
			return null;
		}

		public Class getColumnClass(int col) {
			if(col > 1)
				return Image.class;
			return super.getColumnClass(col);
		}

		public String getColumnName(int col) {
			switch(col) {
			case 1: return "Gebruikersnaam";
			case 0: return "Naam";
			case 2: return "Login als";
			case 3: return "Wachtwoord";
			case 4: return "Verwijder";
			}
			return "";
		}
		public boolean isCellEditable(int row, int col) {
			return col >= 2;
		}

	}
	
	public UserManagementPanel(DwoIF dwo) {
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setSize(620, 485);
        removeImage = DwoHelper.getImage(GuiConstants.RESOURCES
                + GuiConstants.REMOVE_SCO_IMAGE);
        editImage = DwoHelper.getImage(GuiConstants.RESOURCES
                + GuiConstants.EDIT_SCO_IMAGE);
        userImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.USERS_CLASS_IMAGE);
        teacherImage = DwoHelper.getImage(GuiConstants.RESOURCES + "resources/assign.gif");
		this.dwo = dwo;
		docent = (ContactDocent) dwo.getUser();
		School school = docent.getSchool();
		SchoolGroup[] groups = school.getSchoolGroupList();
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
		Arrays.sort(userList, this);
		
		JTable table = new JTable(new UserModel());
    	TableUtil.setDefaults(table, true, new ImageRenderer(), new ImageButtonEditor());

    	TableUtil.setJTableSizes(table);
    	Box b = Box.createVerticalBox();
    	b.add(table.getTableHeader());
    	b.add(table);
		
		add(b);
		
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

	public Component getComponent() {
		return this;
	}

	public Component getHeaderPanel() {
		// TODO Auto-generated method stub
		return new HeaderPanel("Gebruikers beheren");
	}

	public void setCenterPanel(CenterPanel centerPanel) {
		center = centerPanel;

	}
	
	/**
	 * Compare 2 users
	 * @param o1 user 1
	 * @param o2 user 2
	 * @return -1/0/+1
	 */
	
	public int compare(Object o1, Object o2) {
		User u1 = (User)o1;
		User u2 = (User)o2;
		int r = u1.getName().compareToIgnoreCase(u2.getName());
		if(r == 0)
			r = u1.getUsername().compareToIgnoreCase(u2.getUsername());
		return r;
	}

}
