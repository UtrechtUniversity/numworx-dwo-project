/**
 * Copyrighted Mar 11, 2016
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomSchoolAdmin;
import fi.dwo.rest.dom.entities.DomStudent;
import fi.dwo.rest.dom.entities.DomTeacher;
import fi.dwo.rest.dom.entities.DomUser;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Gert van der Plas
 */
class UsersInSchoolSchoolAdminPanelTableModel extends AbstractTableModel {

    private String[] columnNames = {TextMapper.getText(TextMapper.TBL_USERNAME),
            TextMapper.getText(TextMapper.TBL_GIVENNAME),
            TextMapper.getText(TextMapper.TBL_INSERTION),
            TextMapper.getText(TextMapper.TBL_FAMILYNAME),
            TextMapper.getText(TextMapper.TBL_CLASSLIST),
            TextMapper.getText(TextMapper.TBL_EDIT),
            TextMapper.getText(TextMapper.TBL_DELETE)
//            TextMapper.getText(TextMapper.TBL_SELECT)
    };
    
    static boolean DEBUG = false;

    private int selectedRow, selectedColumn;

    private Object[][] data;

    public <T extends DomUser> void init(List<T> userList, Image removeImage, Image klassenImage, Image editImage, Image emptyImage) {

        int rows = 0;
        if (userList == null) {
            userList = new ArrayList<T>();
        }

        for (T u : userList) {
            rows++; // one for each item in List
        }

        data = new Object[rows][8];
        int j = 0;
        for (T u : userList) {
            data[j][0] = u.getUserName();
            data[j][1] = u.getGivenName();
            data[j][2] = u.getInsertion();
            data[j][3] = u.getFamilyName();
            if (u instanceof DomStudent || u instanceof DomTeacher) {
                data[j][4] = klassenImage;
            } else {
                data[j][4] = emptyImage;
            }
            if (u.getSingleSchool()) {
                data[j][5] = editImage;
            } else {
                data[j][5] = emptyImage;
            }
            if (u instanceof DomSchoolAdmin &&  u.getId().equals(DwoHelper.getCurrentUser().getId())) {
                data[j][6] = emptyImage;
            } else {
                data[j][6] = removeImage;
            }
            data[j][7] = u;
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
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's editable.
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 1) {
            return false;
        } else {
            return true;
        }
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
