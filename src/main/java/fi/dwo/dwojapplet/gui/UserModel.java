/**
 *
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.exceptions.RegisterException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.Teacher;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import java.awt.Image;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;

class UserModel extends AbstractTableModel {

    private static final Logger log = Logger.getLogger(UserModel.class.getName());

    UserModel() {
        if (!GuiCreator.instance().getUser().hasRight(User.CHANGE_CLASS_RIGHT_TEACHER)) {
            cols = 4;
        }
    }

    private int cols = 6;
    User[] userList;
    Image removeImage, editImage, userImage, teacherImage;

    @Override
    public int getColumnCount() {
        return cols;
    }

    public void deleteRow(int row) {
        User[] nu = new User[userList.length - 1];
        System.arraycopy(userList, 0, nu, 0, row);
        System.arraycopy(userList, row + 1, nu, row, nu.length - row);
        userList = nu;
        fireTableRowsDeleted(row, row);
    }

    @Override
    public int getRowCount() {
        return userList.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 1:
                return userList[rowIndex].getUsername();
            case 0:
                return userList[rowIndex].getName();
            case 2:
                return userList[rowIndex] instanceof Teacher ? teacherImage : userImage;
            case 3:
                return editImage;
            case 4:
                return removeImage;
            case 5:
                return userList[rowIndex].getInClass();
        }
        return null;
    }

    @Override
    public Class getColumnClass(int col) {
        if (col == 5) {
            return SchoolClass.class;
        }
        if (col > 1) {
            return Image.class;
        }
        return super.getColumnClass(col);
    }

    @Override
    public String getColumnName(int col) {
        switch (col) {
            case 1:
                return TextMapper.getText("Username");
            case 0:
                return TextMapper.getText("Name");
            case 2:
                return TextMapper.getText("Login as");
            case 3:
                return TextMapper.getText("Password");
            case 4:
                return TextMapper.getText("Remove");
            case 5:
                return TextMapper.getText("In class");
        }
        return "";
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        boolean b = col >= 2;
        if (b && col == 5) {
            return !(userList[row] instanceof Teacher);
        }
        return b;
    }

    @Override
    public void setValueAt(Object aValue, int row, int col) {
        User user = userList[row];
        if (col == 5 && aValue != user.getInClass()) {
            //System.out.println("change user " + row + " to " + aValue);
            SchoolClass c = (SchoolClass) aValue;
            // TODO persist....
            try {
                PersistenceFacade.instance().changeAccount(user, null, null, user.getFirstname(), user.getMiddleName(), user.getLastName(), user.getEmail(), c);
                user.setInClass(c);
                fireTableCellUpdated(row, col);
            } catch (RegisterException e) {
                log.log(Level.SEVERE, null, e);
            }
            return;
        }
        super.setValueAt(aValue, row, col);
    }

}
