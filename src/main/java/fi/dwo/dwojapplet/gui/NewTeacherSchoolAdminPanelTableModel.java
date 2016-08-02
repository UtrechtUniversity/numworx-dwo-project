package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.rest.dom.entities.DomUserFull;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Gert van der Plas <gertvdplas@gmail.com>
 */
class NewTeacherSchoolAdminPanelTableModel extends AbstractTableModel {

    private String[] columnNames = {
        TextMapper.getText(TextMapper.TBL_GIVENNAME),
        TextMapper.getText(TextMapper.TBL_INSERTION),
        TextMapper.getText(TextMapper.TBL_FAMILYNAME),
        TextMapper.getText(TextMapper.TBL_USERNAME),
        TextMapper.getText(TextMapper.TBL_PASSWORD),
        TextMapper.getText(TextMapper.TBL_EMAIL),
        TextMapper.getText(TextMapper.TBL_DELETE)};

    static boolean DEBUG = false;
    private NewTeacherSchoolAdminPanelProperties prop;

    private int selectedRow, selectedColumn;

    //define an empty field to add things.
    private int emptyRow = 0;

    private List<DomUserFull> data = new ArrayList<DomUserFull>();
    private Image delImage;

    /**
     *
     * @param props
     * @param colNames
     * @param rmImage
     * @throws Dwo2Exception
     */
    public void init(NewTeacherSchoolAdminPanelProperties props, String[] colNames, List<DomUserFull> teachers, Image rmImage) throws Dwo2Exception {
        delImage = rmImage;
        columnNames = colNames;
        data = teachers;
        DomUserFull teacher = new DomUserFull();
//        teacher.clearSettings();
        data.add(teacher);
        prop = props;
        fireTableDataChanged();
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
        switch (col) {
            case 0:
                return data.get(row).getGivenName();
            case 1:
                return data.get(row).getInsertion();
            case 2:
                return data.get(row).getFamilyName();
            case 3:
                return data.get(row).getUserName();
            case 4:
                return data.get(row).getPassword();
            case 5:
                return data.get(row).getEmail();
            case 6:
                return delImage;
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
        switch (c) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            case 4:
                return String.class;
            case 5:
                return String.class;
            case 6:
                return Image.class;
            default:
                return DomUserFull.class;
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
//        TextMapper.getText(TextMapper.GUIR_FIRSTNAME),
//        TextMapper.getText(TextMapper.GUIR_MIDDLENAME),
//        TextMapper.getText(TextMapper.GUIR_LASTNAME),
//        TextMapper.getText(TextMapper.GUIR_USERNAME),
//        TextMapper.getText(TextMapper.GUIR_PASSWORD),
//        TextMapper.getText(TextMapper.GUIR_EMAIL)};
        switch (col) {
            case 0:
                data.get(row).setGivenName((String) value);
                break;
            case 1:
                data.get(row).setInsertion((String) value);
                break;
            case 2:
                data.get(row).setFamilyName((String) value);
                break;
            case 3:
                data.get(row).setUserName((String) value);
                break;
            case 4:
                data.get(row).setPassword((String) value);
                break;
            case 5:
                data.get(row).setEmail((String) value);
                break;
            case 6:
                break;
        }
    }

    public List<DomUserFull> getData(){
        return data;
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

    public void deleteSelectedRow(int row) {
        if (row != data.size() - 1) {
            emptyRow--;
            data.remove(row);
            this.fireTableDataChanged();
//            this.fireTableStructureChanged();
        }
    }

    public void addRows(List<DomUserFull> teachers) {
        DomUserFull temp = data.get(data.size() - 1);
        data.remove(data.size() - 1);
        for (DomUserFull s : teachers) {
            data.add(s);
        }
        data.add(temp);
        emptyRow = data.size() - 1;
        this.fireTableDataChanged();
//        this.fireTableStructureChanged();

    }
}
