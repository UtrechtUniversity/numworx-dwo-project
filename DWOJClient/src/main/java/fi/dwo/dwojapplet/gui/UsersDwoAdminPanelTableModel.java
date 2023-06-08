/**
 * Copyrighted Mar 11, 2016
 */
package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.commons.system.TextMapper;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 *
 * @author Gert van der Plas
 */
class UsersDwoAdminPanelTableModel extends AbstractTableModel {

    private String[] columnNames = {TextMapper.getText(TextMapper.TBL_USERNAME),
        TextMapper.getText(TextMapper.TBL_GIVENNAME),
        TextMapper.getText(TextMapper.TBL_INSERTION),
        TextMapper.getText(TextMapper.TBL_FAMILYNAME),
        TextMapper.getText(TextMapper.TBL_EMAIL),
        TextMapper.getText(TextMapper.TBL_DB_KEY),
//        TextMapper.getText(TextMapper.TBL_EDIT),
//            TextMapper.getText(TextMapper.TBL_SELECT)
    };

    static boolean DEBUG = false;

    private int selectedRow, selectedColumn;

    private Object[][] data;

    public void init(UsersDwoAdminPanelProperties props, Image removeImage, Image klassenImage, Image editImage, Image emptyImage) throws Dwo2Exception {

        List<DomUserFull> userList = props.getUserList();
        int rows = 0;
        if (userList == null) {
            userList = new ArrayList<DomUserFull>();
        }

//        for (T u : userList) {
//            rows++; // one for each item in List
//        }
        rows = userList.size();

        data = new Object[rows][8];
        int j = 0;
        for (DomUserFull u : userList) {
            data[j][0] = u.getUserName();
            data[j][1] = u.getGivenName();
            data[j][2] = u.getInsertion();
            data[j][3] = u.getFamilyName();
            data[j][4] = u.getEmail();
            data[j][5] = u.getId().getIdString();
//            data[j][4] = editImage;
//            data[j][5] = emptyImage;
            data[j][6] = u;
            j++;
        }
        fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/ editor for
     * each cell. If we didn't implement this method, then the last column
     * would contain text ("true"/"false"), rather than a check box.
     */
    @Override
    public Class getColumnClass(int c) {
        if (getRowCount() > 0 && getValueAt(0, c) != null) {
            return getValueAt(0, c).getClass();
        }
        return super.getColumnClass(c);
    }

    /*
     * Don't need to implement this method unless your table's editable.
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
//        if (col < 3) {
            return false;
//        } else {
//            return true;
//        }
    }


    /*
     * Don't need to implement this method unless your table's data can
     * change.
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        //don't change any setting, but update selected values.
        setSelectedRow(row);
        setSelectedColumn(col);
    }

    /**
     * @return the selectedRow
     */
    public int getSelectedRow() {
        return selectedRow;
    }

    /**
     * @param selectedRow the selectedRow to set
     */
    public void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
    }

    /**
     * @return the selectedColumn
     */
    public int getSelectedColumn() {
        return selectedColumn;
    }

    /**
     * @param selectedColumn the selectedColumn to set
     */
    public void setSelectedColumn(int selectedColumn) {
        this.selectedColumn = selectedColumn;
    }
}
