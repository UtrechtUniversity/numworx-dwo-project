/**
 * 
 */
package fi.dwo.client.gui;

import java.awt.Image;

import javax.swing.table.AbstractTableModel;

import fi.dwo.client.domain.Teacher;
import fi.dwo.client.domain.User;

class UserModel extends AbstractTableModel {

	User[] userList;
    Image removeImage, editImage, userImage, teacherImage;

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