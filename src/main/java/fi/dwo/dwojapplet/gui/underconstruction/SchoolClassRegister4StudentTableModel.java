/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui.underconstruction;

import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSchoolRoleAndClass;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
class SchoolClassRegister4StudentTableModel extends AbstractTableModel {

    private String[] columnNames = {"SchoolClassName", "SchoolClass"};//schoolclass is hidden
    static boolean DEBUG = false;

    private Object[][] data;

    public void init(List<DomSchoolClass> scList) {
        int rows = 0;
        for (DomSchoolClass sc : scList) {
            rows++; // one for each item in List
        }
        data = new Object[rows][2];
        int j = 0;
        for (DomSchoolClass sc : scList) {
            data[j][0] = sc.getSchoolClassName();
            data[j][1] = sc;
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
//
//    /*
//     * Don't need to implement this method unless your table's editable.
//     */
//    @Override
//    public boolean isCellEditable(int row, int col) {
//      //Note that the data/cell address is constant,
//      //no matter where the cell appears onscreen.
//      if (col < 2) {
//        return false;
//      } else {
//        return true;
//      }
//    }
//
//    /*
//     * Don't need to implement this method unless your table's data can
//     * change.
//     */
//    @Override
//    public void setValueAt(Object value, int row, int col) {
//      if (DEBUG) {
//        System.out.println("Setting value at " + row + "," + col
//            + " to " + value + " (an instance of "
//            + value.getClass() + ")");
//      }
//
//      data[row][col] = value;
//      fireTableCellUpdated(row, col);
//
//      if (DEBUG) {
//        System.out.println("New value of data:");
//        printDebugData();
//      }
//    }
//
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
}
