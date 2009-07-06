package fi.dwo.client.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
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
import fi.dwo.client.domain.SchoolGroup;
import fi.dwo.client.domain.User;
import fi.dwo.client.gui.SchoolPanel.SchoolModel;

import fi.dwo.client.persistence.PersistenceFacade;
import fi.dwo.client.system.LoginException;
import fi.dwo.client.system.PersistenceException;
import fi.dwo.client.system.RegisterException;
import fi.dwo.client.system.SchoolException;
import fi.dwo.client.system.TextMapper;

public class UserManagementPanel extends JPanel implements CenterSubPanel {

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
			if(value == userImage)
			{
				try {
					GuiCreator.instance().login(userList[row].getUsername(), null);
				} catch (LoginException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
			if(value == editImage)
			{
				try {
					// TODO edit password, naam
					JOptionPane.showInputDialog(this,"PASSWORD");
					model.fireTableRowsUpdated(row, row);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else
			if (value == removeImage)
			{
                /* Delete the school */
                User u = userList[row];
                if (u != docent) {
					final String title = TextMapper.getText(TextMapper.GUIS_DELETE_STUDENT);
					final String text = MessageFormat.format(TextMapper.getText(TextMapper.GUIS_MSG_DELETE_STUDENT), new Object[] { u.getName() });
					if (JOptionPane.showConfirmDialog(UserManagementPanel.this, text
					       + "?", title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					    //deleteUser(u);
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
    private Image removeImage, editImage, userImage;

	
	class UserModel extends AbstractTableModel {

		public int getColumnCount() {
			// TODO Auto-generated method stub
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
			case 0: 
					return userList[rowIndex].getUsername();
			case 1:
					return userList[rowIndex].getName();
			case 2:
					return userImage;
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
			case 0: return "Login";
			case 1: return "Naam";
			case 2: return "Ga naar";
			case 3: return "Edit";
			case 4: return "Verwijder";
			}
			return "";
		}
		public boolean isCellEditable(int row, int col) {
			return col >= 2;
		}

	}
	
	JScrollPane jtbl;

	public UserManagementPanel(DwoIF dwo) {
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setSize(620, 485);
        removeImage = DwoHelper.getImage(GuiConstants.RESOURCES
                + GuiConstants.REMOVE_SCO_IMAGE);
        editImage = DwoHelper.getImage(GuiConstants.RESOURCES
                + GuiConstants.EDIT_SCO_IMAGE);
        userImage = DwoHelper.getImage(GuiConstants.RESOURCES + GuiConstants.USERS_CLASS_IMAGE);

		this.dwo = dwo;
		docent = (ContactDocent) dwo.getUser();
		School school = docent.getSchool();
		SchoolGroup[] groups = school.getSchoolGroupList();
		userList = new User[0];
		for (int i = 0; i < groups.length; i++) {
			SchoolGroup schoolGroup = groups[i];
			try {
				User[] u = (User[]) PersistenceFacade.instance().get(User.class, schoolGroup);
				merge(u);
			} catch (PersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		JTable table = new JTable(new UserModel());
    	TableUtil.setDefaults(table, true, new ImageRenderer(), new ImageButtonEditor());

    	TableUtil.setJTableSizes(table);

		jtbl = new JScrollPane(table);
		add(jtbl);
		
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

}
