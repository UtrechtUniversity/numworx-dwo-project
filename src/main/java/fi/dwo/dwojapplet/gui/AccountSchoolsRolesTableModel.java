/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomSchoolRoleAndClass;
import fi.dwo.commons.system.TextMapper;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
class AccountSchoolsRolesTableModel extends AbstractTableModel {

    private String[] columnNames = {TextMapper.getText(TextMapper.TBL_SCHOOL),
        TextMapper.getText(TextMapper.TBL_ROLE),
        TextMapper.getText(TextMapper.TBL_LOGIN),
        TextMapper.getText(TextMapper.TBL_DELETE)};

    static boolean DEBUG = false;
    private AccountSchoolsRolesProperties prop;

    private int selectedRow, selectedColumn;

    private Object[][] data;

    public void init(AccountSchoolsRolesProperties props, Image loginImage, Image removeImage, Image emptyImage) {

        prop = props;
        List<DomSchoolRoleAndClass> srcList = prop.getSchoolsRolesAndClasses().getSchoolsRolesAndClassesList();
        int rows = 0;
        if (srcList == null) {
            srcList = new ArrayList();
        }
        for (DomSchoolRoleAndClass src : srcList) {
            rows++; // one for each item in List
        }
        data = new Object[rows][5];
        int j = 0;
        for (DomSchoolRoleAndClass src : srcList) {
            data[j][0] = src.getSchoolName();
            data[j][1] = TextMapper.getText(src.getRoleName());
                if (prop.getActiveSchoolRoleAndClass()!=null 
                    && prop.getActiveSchoolRoleAndClass().getSchoolId().equals(src.getSchoolId())     
                    && prop.getActiveSchoolRoleAndClass().getRoleId().equals(src.getRoleId())     
                    && prop.getActiveSchoolRoleAndClass().getUserId().equals(src.getUserId())
                    && ( (src.getSchoolClassId()==null && prop.getActiveSchoolRoleAndClass().getSchoolClassId()==null)|| 
                    prop.getActiveSchoolRoleAndClass().getSchoolClassId().equals(src.getSchoolClassId())
                    )) {
                data[j][2] = emptyImage;
            } else {
                data[j][2] = loginImage;
            }
            data[j][3] = removeImage; // delete 
            data[j][4] = src;
            j++;
        }
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
        if (col < 2) {
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

//    private void printDebugData() {
//      int numRows = getRowCount();
//      int numCols = getColumnCount();
//
//      for (int i = 0; i < numRows; i++) {
//        System.out.print("    row " + i + ":");
//        for (int j = 0; j < numCols; j++) {
//          System.out.print("  " + data[i][j]);
//        }
//        System.out.println();
//      }
//      System.out.println("--------------------------");
//    }
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
