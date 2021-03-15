package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.Component;
import java.awt.Image;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JScrollPane;
import fi.dwo.dwojapplet.gui.TableUtil;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherStudentModelManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class SettingsSchoolClassPanel extends JPanel {
  
  class ClassTableModel extends AbstractTableModel {
    
    List<DomSchoolClass> classes;
    boolean[] check;
    JButton filter;
    DomStudentModelContext[] list;
    private DomStudentModelContextId id;
    
    
    public ClassTableModel(List<DomSchoolClass> classes, DomStudentModelContextId id) {
      filter = new JButton("Filter leerdoelen");
      check = new boolean[classes.size()];
      this.classes = classes;
      this.id = id;
      try {
        list = new DomStudentModelContext[classes.size()];
        for(int i = 0; i < classes.size(); i++) {
            list[i] = SecureTeacherStudentModelManager.getForClass(id, classes.get(i));
            check[i] = list[i] != null;
        }
      } catch (Dwo2Exception e) {
      }
    }

    @Override
    public int getRowCount() {
      return classes.size();
    }

    @Override
    public int getColumnCount() {
      return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      switch(columnIndex) {
        case 0: return classes.get(rowIndex).getSchoolClassName();
        case 1: return check[rowIndex];
        case 2: return check[rowIndex] ? filter : null;
        case 3: return classes.get(rowIndex);
        case 4: return list[rowIndex];
      }
      return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      switch(columnIndex) {
        case 0: return String.class;
        case 1: return Boolean.class;
        case 2: return JButton.class;
      }
      return super.getColumnClass(columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
       return columnIndex == 1 || columnIndex == 2;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      switch(columnIndex) {
        case 1: check[rowIndex] = Boolean.TRUE.equals(aValue); 
            fireTableCellUpdated(rowIndex, columnIndex+1);
            fireTableCellUpdated(rowIndex, columnIndex);
            return;
        case 4: list[rowIndex] = (DomStudentModelContext) aValue; return;
        default: super.setValueAt(aValue, rowIndex, columnIndex);
      }
    }

    @Override
    public String getColumnName(int column) {
      switch(column) {
        case 0: return "Klasnaam";
        case 1: return "Gebruik";
        case 2: return "leerdomein";
      }
      return super.getColumnName(column);
    }
    
  }
  
  class ButtonRenderer extends JLabel implements TableCellRenderer {


      public ButtonRenderer() {
      super();
        setOpaque(true);
    }

      @Override
      public Component getTableCellRendererComponent(JTable table,
            Object value, boolean selected, boolean hasFocus, int row, int col) {
        if (value == null) {
          if(selected)
          {
              setBackground(table.getSelectionBackground());
          } else {
              setBackground(table.getBackground());
          }
          return this;
        }
        JButton image = (JButton)value;
        
        return image;
    }

}


  public SettingsSchoolClassPanel(List<DomSchoolClass> classes, DomStudentModelContext context) {
    super();
    
    JTable table = new JTable(new ClassTableModel(classes, context));
    JScrollPane pane = new JScrollPane(table);
    TableUtil.setDefaults(table, true, null, null);
    TableCellRenderer renderer = new ButtonRenderer();
    table.setDefaultRenderer(JButton.class, renderer);
    TableUtil.setBorder(pane);
    TableUtil.setJTableSizes(table);
    add(pane);    
  }

}
