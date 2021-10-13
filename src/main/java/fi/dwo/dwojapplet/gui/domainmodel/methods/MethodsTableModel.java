package fi.dwo.dwojapplet.gui.domainmodel.methods;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.EventObject;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

import com.owlike.genson.Genson;

import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JOptionPane;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.GuiConstants;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;

public class MethodsTableModel extends AbstractTableModel {

  private static final Logger LOG = Logger.getLogger(MethodsTableModel.class.getName());

  public final MethodsProperties model;
  Icon view, delete, copy;
  Genson genson = StoredRestManager.getInstance().getGenson();

  public MethodsTableModel(MethodsProperties model) {
    this.model = model;
    view = new ImageIcon(DwoHelper.getResourceImage(GuiConstants.EDIT_STUDENTMODEL_IMAGE));
    delete = new ImageIcon(DwoHelper.getResourceImage(GuiConstants.REMOVE_STUDENTMODEL_IMAGE));
    copy = new ImageIcon(DwoHelper.getResourceImage(GuiConstants.COPY_IMAGE));
  }

  public MethodsTableModel() {
    this(MethodsProperties.instance());
  }

  @Override
  public int getRowCount() {
    return model.size()-1;
  }

  public void delete(int row) {
    model.remove(row+1);
    fireTableRowsDeleted(row, row);
  }

  @Override
  public int getColumnCount() {
    return 4;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    DomMethod row = model.get(rowIndex+1);
    switch(columnIndex) {
      case 0: return row.method;
      case 1: return view;
      case 2: return row.standard ? null : delete;
      case 3: return copy;
    }
    return null;
  }

  void add(DomMethod row) {
    int size = getRowCount();
    model.add(row);
    fireTableRowsInserted(size, size);     
  }

  @Override
  public String getColumnName(int column) {
    switch(column) {
      case 0: return "Lesmethodes";
      case 1: return TextMapper.getText(TextMapper.TBL_VIEW);
      case 2: return TextMapper.getText(TextMapper.TBL_DELETE);
      case 3: return TextMapper.getText("copy");

    }
    return super.getColumnName(column);
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    switch(columnIndex) {
      case 0: return String.class;
      case 1: case 3:
      case 2: return Icon.class;
    }
    return super.getColumnClass(columnIndex);
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    if (columnIndex == 0) return false; // edit via column 1
    return getValueAt(rowIndex, columnIndex) != null;    // buttons
  }

  class Editor extends AbstractCellEditor implements TableCellEditor, ActionListener  {

    @Override
    public Object getCellEditorValue() {
      return value;
    }

    int row;
    Object value;
    JTable table;
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
        int row, int column) {
      this.row = row;
      this.value = value;
      this.table = table;
      JButton btn = new JButton( (Icon) value);
      btn.addActionListener(this);
      return btn;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      if (value == delete) {
         int ok = JOptionPane.showConfirmDialog(table, "Weet je het zeker?", "verwijderen", JOptionPane.YES_NO_OPTION);
      } else if (value == copy) {
          String input = JOptionPane.showInputDialog(table, "Nieuwe naam");
        
      } else if (value == view) {
          JOptionPane.showMessageDialog(table, "Edit " + getValueAt(row,0));
       
      }
      fireEditingStopped();
    }

    
  }
  
  class ImportAction extends AbstractAction {
    
    JFileChooser choose = new JFileChooser();
    
    
    public ImportAction() {
      super("Import");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      choose.setCurrentDirectory(DwoHelper.getCurrentDirectory());
      if (JFileChooser.APPROVE_OPTION  == choose.showOpenDialog((Component) e.getSource()))  {
        try {
          DwoHelper.setCurrentDirectory(choose.getCurrentDirectory());
          FileInputStream in = new FileInputStream(choose.getSelectedFile());
          DomMethod row = genson.deserialize(in, DomMethod.class);
          in.close();
          row = model.persist(row);
          MethodsTableModel.this.add(row);
        } catch (Exception e1) {
          LOG.log(Level.SEVERE, "Import", e1);
        }
      }
    }
    
  }
  
  
  
  public TableCellEditor getEditor() {
    return new Editor();
  }

  public Action getImportAction() {
    return new ImportAction();
  }
}
