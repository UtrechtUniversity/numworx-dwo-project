/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomStudent;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.system.TextMapper;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
class StudentsInSchoolClassTeacherPanelTableModel extends AbstractTableModel {

    private String[] columnNames = {TextMapper.getText(TextMapper.TBL_USERNAME),
            TextMapper.getText(TextMapper.TBL_GIVENNAME),
            TextMapper.getText(TextMapper.TBL_INSERTION),
            TextMapper.getText(TextMapper.TBL_FAMILYNAME),
            TextMapper.getText(TextMapper.TBL_LOGIN),
            TextMapper.getText(TextMapper.TBL_EDIT),
            TextMapper.getText(TextMapper.TBL_SELECT)};
    
    static boolean DEBUG = false;

    private int selectedRow, selectedColumn;

    private Object[][] data;

    public void init(List<DomStudent> userList, Image loginImage, Image editImage, Image noImage) throws Dwo2Exception {

        int rows = 0;
        if (userList == null) {
            userList = new ArrayList<DomStudent>();
        }

        for (DomStudent u : userList) {
            rows++; // one for each item in List
        }

        data = new Object[rows][8];
        int j = 0;
        for (DomStudent u : userList) {
            data[j][0] = u.getUserName();
            data[j][1] = u.getGivenName();
            data[j][2] = u.getInsertion();
            data[j][3] = u.getFamilyName();
            if (u.getSingleSchool()) {
                data[j][4] = loginImage;
                data[j][5] = editImage;
            } else {
                data[j][4] = noImage;
                data[j][5] = noImage;
            }
            data[j][6] = new Boolean(false);
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
        if (col < 4) {
            return false;
        } else if ((col == 4 || col==5) && ((DomStudent) data[row][7]).getSingleSchool()) {
            return true;
        } else if (col == 6) {
            return true;
        } else {
            return false;
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
        data[row][col] = value;
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
