/*Copyrighted 2015. */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomSingleSchoolStudent;
import fi.dwo.commons.exceptions.Dwo2Exception;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
class NewSingleSchoolStudentsTeacherPanelTableModel extends AbstractTableModel {

    private String[] columnNames ;
    static boolean DEBUG = false;
    private NewSingleSchoolStudentsTeacherPanelProperties prop;

    private int selectedRow, selectedColumn;

    private List<DomSingleSchoolStudent> data =new ArrayList<DomSingleSchoolStudent>();
    private Image rmImage;

    public void init(NewSingleSchoolStudentsTeacherPanelProperties props, String[] colNames, Image delImage) throws Dwo2Exception {
        rmImage = delImage;
        columnNames = colNames;
        if(data.size()==0){
            DomSingleSchoolStudent student = new DomSingleSchoolStudent();
            data.add(student);
        }
        prop = props;


    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
//        TextMapper.getText(TextMapper.GUIR_FIRSTNAME),
//        TextMapper.getText(TextMapper.GUIR_MIDDLENAME),
//        TextMapper.getText(TextMapper.GUIR_LASTNAME),
//        TextMapper.getText(TextMapper.GUIR_USERNAME),
//        TextMapper.getText(TextMapper.GUIR_PASSWORD),
//        TextMapper.getText(TextMapper.GUIR_EMAIL)};
        switch(col){
            case 0: 
                return data.get(row).getGivenName();
            case 1:
                return data.get(row).getInsertion();
            case 2:
                return data.get(row).getFamilyName();
            case 3:
                return data.get(row).getUsername();
            case 4:
                return data.get(row).getPassword();
            case 5:
                return data.get(row).getEmail();
            case 6:
                return rmImage;
            default: 
                return data.get(row); 
        }        
    }

    /*
     * JTable uses this method to determine the default renderer/ editor for
     * each cell. If we didn't implement this method, then the last column
     * would contain text ("true"/"false"), rather than a check box.
     */
    @Override
    public Class getColumnClass(int c) {
         switch(c){
            default: 
                return String.class;
        }  
    }

    /*
     * Don't need to implement this method unless your table's editable.
     */
    @Override
    public boolean isCellEditable(int row, int col) {
            return true;
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
//        TextMapper.getText(TextMapper.GUIR_FIRSTNAME),
//        TextMapper.getText(TextMapper.GUIR_MIDDLENAME),
//        TextMapper.getText(TextMapper.GUIR_LASTNAME),
//        TextMapper.getText(TextMapper.GUIR_USERNAME),
//        TextMapper.getText(TextMapper.GUIR_PASSWORD),
//        TextMapper.getText(TextMapper.GUIR_EMAIL)};
        switch(col){
            case 0: 
                data.get(row).setGivenName((String)value);
                break;
            case 1:
                data.get(row).setInsertion((String)value);
                break;
            case 2:
                data.get(row).setFamilyName((String)value);
                break;
            case 3:
                data.get(row).setUsername((String)value);
                break;
            case 4:
                data.get(row).setPassword((String)value);
                break;
            case 5:
                data.get(row).setEmail((String)value);
                break;
            case 6:
                break;
//            default: 
//                data.set(row, (DomSingleSchoolStudent) value); 
        }        
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
