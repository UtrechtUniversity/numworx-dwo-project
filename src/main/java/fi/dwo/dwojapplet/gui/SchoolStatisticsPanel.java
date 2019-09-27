package fi.dwo.dwojapplet.gui;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import fi.beans.numworxlf.JScrollPane;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminSchoolManager;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomStatistics;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class SchoolStatisticsPanel extends JPanel {

  private class TableModel extends AbstractTableModel {

    private final DomStatistics model;

    private TableModel(DomStatistics model) {
      this.model = model;
    }

    @Override
    public int getRowCount() {
      return model.getStatistics().size();
    }

    @Override
    public int getColumnCount() {
      return 2;
    }

    @Override
    public Object getValueAt(int row, int col) {
      DomMapEntry<String, String> entry = model.getStatistics().get(row);
      return col == 1 ? entry.getValue() : entry.getKey();
    }

    @Override
    public String getColumnName(int column) {
      switch (column) {
        case 0: return "var";
        case 1: return "value";
      }
      return super.getColumnName(column);
    }

  }
  
  
  public SchoolStatisticsPanel(DomSchool4DwoAdmin school) throws Dwo2Exception {
    
    JTable table = new JTable(new TableModel(SecureDwoAdminSchoolManager.getStatistics(school)));
    TableUtil.setJTableSizes(table);
    table.setMinimumSize(table.getPreferredSize());
    table.setSize(table.getPreferredSize());
    Dimension dim = table.getPreferredScrollableViewportSize();
    dim.width = table.getPreferredSize().width;
    table.setPreferredScrollableViewportSize(dim);
    JScrollPane scroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    add(scroll);
    
  }

}
