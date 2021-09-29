package fi.dwo.dwojapplet.gui.domainmodel.methods;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

import com.owlike.genson.Genson;

import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JOptionPane;
import fi.beans.numworxlf.JTextField;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.GuiConstants;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherMethodManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class MethodsPanel extends JPanel implements ActionListener, ListSelectionListener {

  private static final String OPEN = "OPEN";
  private static final String SAVE = "SAVE";
  private static final String NEW  = "NEW";
  private static final Logger LOG = Logger.getLogger(MethodsPanel.class.getName());
  
  public class IconButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    Object value;
    int row;
//ClassTeacherPanelTableModel model;

  @Override
  public Component getTableCellEditorComponent(JTable table, Object value,
        boolean arg2, int aRow, int aCol) {
    this.value = value;
    JButton button = new JButton((Icon) value);
    button.addActionListener(this);
    row = aRow;
    //model = (ClassTeacherPanelTableModel) table.getModel();
    return button;
  }

  @Override
  public Object getCellEditorValue() {
    return value;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    if (value == removeIcon && row != 0) {
      int ok = JOptionPane.showConfirmDialog(MethodsPanel.this, "Weet je het zeker?", TextMapper.getText(TextMapper.TBL_DELETE), JOptionPane.YES_NO_OPTION);
      if (ok == JOptionPane.YES_OPTION)
      {
        tbl.getSelectionModel().clearSelection();
        DomMethod rowSet = model.get(row);
        tableModel.delete(row);
        delete.add(rowSet);
        txtField.invalidate();
        txtField.setSize(txtField.getPreferredSize());
        resize();
      }
    }
  }


  }
  void resize() {
    invalidate();
    Window w = SwingUtilities.windowForComponent(this);
    w.setSize(w.getPreferredSize());
    w.validate();
    w.pack();
    repaint();  
}

  private JFileChooser choose = new JFileChooser();
 
  private JTable tbl;
  private Genson genson;
  private Image removeImage;
  private Icon  removeIcon;
  private JTextField txtField;
  private ChapterSettings settings;
  private Set<DomMethod> update = new HashSet<>();
  private Set<DomMethod> delete = new HashSet<>();
    
  public final MethodsProperties model;
  private PersistenceId current;
  private DomMethod rowSet;
  
  private Model tableModel;
  
    class Model extends AbstractTableModel {

    @Override
    public int getRowCount() {
      return model.size();
    }

    public void delete(int row) {
      if (row == 0) return;
      model.remove(row);
      fireTableRowsDeleted(row, row);
    }

    @Override
    public int getColumnCount() {
      return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      DomMethod row = model.get(rowIndex);
      switch(columnIndex) {
        case 0: return row.method;
        case 1: return Objects.equals(row.getId(), current);
        case 3: return row.standard;
        case 2: 
          if (rowIndex > 0 && !row.standard)
            return removeIcon;
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
        case 0: return "Lesmethode";
        case 1: return "Actieve lesmethode";
        case 2: return TextMapper.getText(TextMapper.TBL_DELETE);
        case 3: return "Standaard";

      }
      return super.getColumnName(column);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      switch(columnIndex) {
        case 0: return String.class;
        case 3:
        case 1: return Boolean.class;
        case 2: return Icon.class;
      }
      return super.getColumnClass(columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      if (rowIndex == 0 && columnIndex != 1) return false;
      if (columnIndex == 3) return false;
      return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      if (! isCellEditable(rowIndex, columnIndex) || columnIndex == 2) return;
      DomMethod row = model.get(rowIndex);
      switch(columnIndex) {
        case 0: row.method = Objects.toString(aValue, ""); break;
        case 1: if (Boolean.FALSE.equals(aValue) && Objects.equals(current,row.getId())) current = null;
                if (Boolean.TRUE.equals(aValue)) current = row.getId();
                fireTableDataChanged();
      }
    }
    
  }
  
    
    public void valueChanged(ListSelectionEvent event) {
      System.err.println(event);
      int rowIndex = tbl.getSelectedRow();
      if (!event.getValueIsAdjusting())
      {
        updateRowSet();
        settings.setVisible(rowIndex > 0);
        if (rowIndex <= 0) {
          rowSet = null;
          txtField.setText("");
          txtField.setEnabled(false);
        }
      }
      if (!event.getValueIsAdjusting() && rowIndex != -1) {
        rowSet = model.get(rowIndex);
        txtField.setText(rowSet.method);
        txtField.setEnabled(rowIndex != 0);
        settings.setBooks(rowSet.books.toArray(new String[rowSet.books.size()]));
        String[][] chapters = new String[rowSet.chapters.size()][];
        for (int i = 0; i < chapters.length; i++) {
          List<String> list = rowSet.chapters.get(i);
          chapters[i] = list.toArray(new String[list.size()]);
        }
        settings.setChapters(chapters);
        settings.makeGUI();
        settings.setReadonly(rowSet.standard);
        if (rowIndex == 0) rowSet = null;
      }
    }

    private void updateRowSet() {
      if (rowSet != null && !rowSet.standard) {
        rowSet.method = txtField.getText();
        settings.makeObjects();
        rowSet.books = Arrays.asList(settings.getBooks());
        String[][] chapters = settings.getChapters();
        rowSet.chapters = new ArrayList<>(chapters.length);
        for (int i = 0; i < chapters.length; i++) {
          String[] strings = chapters[i];
          rowSet.chapters.add(Arrays.asList(strings));
        }   
        System.err.println("update rowset " + rowSet.method + " " + settings.aantalKolommen + " " + settings.aantalRijen  + " " + rowSet.books.size());
        update.add(rowSet);
      }
    }

  public MethodsPanel() {
    super(null);
    genson = StoredRestManager.getInstance().getGenson();
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    JLabel l;
    l = new JLabel("Gekoppelde methodes");
    add(l);
    model = MethodsProperties.instance();
    tbl = new JTable(tableModel = new Model());
    tbl.setDefaultEditor(Icon.class, new IconButtonEditor());

    
    
    tbl.getSelectionModel().addListSelectionListener(this);

    removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_STUDENTMODEL_IMAGE);
    removeIcon  = new ImageIcon(removeImage);
    JScrollPane sp = new JScrollPane(tbl);
    int rh = tbl.getRowHeight() + tbl.getRowMargin();
    Dimension dim = sp.getPreferredSize();
    dim.height = 5 * rh; // 5 rows preferred
    sp.setPreferredSize(dim);
    dim = sp.getMaximumSize();
    dim.height = 8 * rh; // 8 rows max
    sp.setMaximumSize(dim);
    add(sp);
    
    JButton btn;
    Box box = Box.createHorizontalBox();
    btn = new JButton("Open"); btn.addActionListener(this); btn.setActionCommand(OPEN);
    box.add(btn); box.add(Box.createHorizontalStrut(10));
    btn = new JButton("Save"); btn.addActionListener(this); btn.setActionCommand(SAVE);
    box.add(btn); box.add(Box.createHorizontalGlue());
    btn = new JButton("Lesmethode toevoegen");btn.addActionListener(this); btn.setActionCommand(NEW);
    box.add(btn);
    
    add(box);
    
    txtField = new JTextField(); txtField.addActionListener(this);
    
    add(txtField);
    
    settings = new ChapterSettings("Hoofdstuk", "Leerjaar");
    settings.makeTextFields();
    settings.makeGUI(0,0);
    settings.setVisible(false);
    
    add(settings);
    
    
  }


  @Override
  public void actionPerformed(ActionEvent e) {
    String action = e.getActionCommand();
    if (OPEN == action) {
      if (JFileChooser.APPROVE_OPTION  == choose.showOpenDialog(this))  {
        try {
          FileInputStream in = new FileInputStream(choose.getSelectedFile());
          DomMethod row = genson.deserialize(in, DomMethod.class);
          in.close();
          row = model.persist(row);
          update.add(row);
          tableModel.add(row);
        } catch (IOException e1) {
          LOG.log(Level.SEVERE, "Open", e1);
        }
      }
    } else
    if (SAVE == action) {
      int r = tbl.getSelectedRow();
      if (r >= 0 && JFileChooser.APPROVE_OPTION == choose.showSaveDialog(this)) {
        try {
          FileOutputStream out = new FileOutputStream(choose.getSelectedFile());
          genson.serialize(model.get(r), out);
          out.close();
        } catch (IOException e1) {
          LOG.log(Level.SEVERE, "Save", e1);
        }
      }
    } else
    if (NEW == action) {
      DomMethod row = new DomMethod();
      row.method = "Untitled";
      row.books = Collections.singletonList( settings.columnLabel + " 1" );
      row.chapters  = Collections.singletonList(Collections.singletonList( settings.rowLabel + " 1" ));
      row.edges = Collections.emptyList();
      row = model.persist(row);
      tableModel.add(row);
    } else
    if ( txtField == e.getSource()) {
      int sel = tbl.getSelectedRow();
      if (sel >= 0) {
        tableModel.setValueAt(action, sel, 0);
        tableModel.fireTableCellUpdated(sel, 0);
      }
    }
    
  }

  
  public void setActiveMethod(PersistenceId id) {
    current = id;
  }
  
  public PersistenceId getActiveMethod() {
    return current;
  }

  public void updateMethods() {
    updateRowSet();
    update.removeAll(delete);
    Iterator<DomMethod> iter = update.iterator();
    while(iter.hasNext()) {
      try {
        SecureTeacherMethodManager.updateModel(iter.next());
        iter.remove();
      } catch (Dwo2Exception e) {
        LOG.log(Level.SEVERE, "update methods", e);
      }
    }
    iter = delete.iterator();
    while(iter.hasNext()) {
      try {
        SecureTeacherMethodManager.removeMethod(iter.next());
        iter.remove();
      } catch (Dwo2Exception e) {
        LOG.log(Level.SEVERE, "delete methods", e);
      }
    }    
  }

  public void refresh() {
    model.refresh();
    update.clear();
    delete.clear();
    rowSet = null;
    tableModel.fireTableDataChanged();
  }
  
}
