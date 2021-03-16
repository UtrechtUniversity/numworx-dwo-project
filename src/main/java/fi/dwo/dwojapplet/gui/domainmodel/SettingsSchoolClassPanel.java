package fi.dwo.dwojapplet.gui.domainmodel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JOptionPane;
import fi.beans.numworxlf.JScrollPane;
import fi.dwo.dwojapplet.gui.ConfirmDialog;
import fi.dwo.dwojapplet.gui.TableUtil;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherStudentModelManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class SettingsSchoolClassPanel extends JPanel {
  
  private final String FILTER_LEERDOELEN = "Filter leerdoelen";
  private final JButton filterBtn = new JButton(FILTER_LEERDOELEN);
  private ClassTableModel model;

  class ClassTableModel extends AbstractTableModel {
    
    List<DomSchoolClass> classes;
    boolean[] check;
    DomStudentModelContext4Student[] list;
    private DomStudentModelContext id;
    private Map[] filter;
    
    
    public ClassTableModel(List<DomSchoolClass> classes, DomStudentModelContext id) {
      
      check = new boolean[classes.size()];
      filter = new Map[classes.size()];
      this.classes = classes;
      this.id = id;
      try {
        list = new DomStudentModelContext4Student[classes.size()];
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
        case 2: return check[rowIndex] ? filterBtn : null;
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
       return columnIndex == 1 || (columnIndex == 2 && check[rowIndex]);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      switch(columnIndex) {
        case 1: check[rowIndex] = Boolean.TRUE.equals(aValue); 
            fireTableCellUpdated(rowIndex, columnIndex+1);
            fireTableCellUpdated(rowIndex, columnIndex);
            if (!check[rowIndex]) {
              filter[rowIndex] = null;
            } else if (filter[rowIndex] == null) {
              filter[rowIndex] = Collections.emptyMap();
            }
            return;
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
        super("");
        setOpaque(true);
        setSize(filterBtn.getPreferredSize());
        setPreferredSize(getSize());
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
  class ButtonEditor extends AbstractCellEditor implements TableCellEditor, Action {

    public ButtonEditor(ClassTableModel model) {
      this.model = model;
    }

    int row;
    ClassTableModel model;

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
        int row, int column) {
      JButton btn = (JButton) value;
      this.row = row;
      btn.setAction(this);      
      return btn;
    }


    @Override
    public Object getCellEditorValue() {
      return filterBtn;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
      DomSchoolClass sc = model.classes.get(row);
      
      ConfirmDialog cd = new ConfirmDialog(SettingsSchoolClassPanel.this, "Leerdoelen filter voor " + sc.getSchoolClassName());
//      DomStudentModelStructure str = model.id.getModelStructure();
//      if (str.getCategories() == null) {
//        try {
//          model.id = SecureTeacherStudentModelManager.get(model.id);
//          str = model.id.getModelStructure();
//        } catch (Dwo2Exception e1) {
//        }
//      }
      FilterPanel panel = new FilterPanel();
      Map<String, Map<String, Set<Integer>>> filter = model.filter[row];
      if(filter == null) filter = Collections.emptyMap();
      panel.setFilter(filter);;
      cd.getContentPane().add(panel);
      JButton ok = new JButton("OK");
      ok.addActionListener(cd::ok);
      cd.getContentPane().add(ok, BorderLayout.SOUTH);
      cd.pack();
      cd.center();
      cd.setVisible(true);
      if (cd.getOption() == JOptionPane.OK_OPTION) {
        
        model.filter[row] = panel.getFilter();        
      }
    }


    @Override
    public Object getValue(String key) {
      if (key == Action.NAME) return FILTER_LEERDOELEN;
      return null;
    }


    @Override
    public void putValue(String key, Object value) {
    }


    @Override
    public void setEnabled(boolean b) {
    }


    @Override
    public boolean isEnabled() {
      return true;
    }


    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    }


    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    } 
    
  }
  
  
  

  public SettingsSchoolClassPanel(List<DomSchoolClass> classes, DomStudentModelContext context) {
    super();
    
    model = new ClassTableModel(classes, context);
    JTable table = new JTable(model);
    JScrollPane pane = new JScrollPane(table);
    TableUtil.setDefaults(table, true, null, null);
    TableCellRenderer renderer = new ButtonRenderer();
    table.setDefaultRenderer(JButton.class, renderer);
    table.setDefaultEditor(JButton.class, new ButtonEditor(model));
    TableUtil.setBorder(pane);
    TableUtil.setJTableSizes(table);
    add(pane);    
  }

  public void update() {
      DomStudentModelContext4Student[] models = model.list;
      List<DomSchoolClass> classes = model.classes;
      Map[] filters = model.filter;
      DomStudentModelContext id = model.id;
      int length = model.getRowCount();
      for (int i = 0; i < length; i++) {
        DomSchoolClass sc = classes.get(i);
        if (filters[i] == null && models[i] != null) {
          models[i].setFilter(null);
          updateModel(models[i]);
        } else if (filters[i] != null && models[i] != null) {
            models[i].setFilter(filters[i]);
            updateModel(models[i]);
        } else if (filters[i] != null && models[i] == null) {
            DomStudentModelContext4Student model = new DomStudentModelContext4Student(id.getId());
            model.setSchoolClass(sc);
            model.setFilter(filters[i]);
            updateModel(model);
        }        
      }
      
      
  }

  private void updateModel(DomStudentModelContext4Student m) {
    try {
      SecureTeacherStudentModelManager.updateModelForClass(m);
    } catch (Dwo2Exception e) {
        System.err.println(e);
    }
  }
  
  
}
