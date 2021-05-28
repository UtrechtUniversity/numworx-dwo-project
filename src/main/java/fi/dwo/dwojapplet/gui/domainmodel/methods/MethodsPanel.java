package fi.dwo.dwojapplet.gui.domainmodel.methods;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.owlike.genson.Genson;

import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JTextField;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.GuiConstants;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class MethodsPanel extends JPanel implements ActionListener, ListSelectionListener {

  private static final String OPEN = "OPEN";
  private static final String SAVE = "SAVE";
  private static final String NEW  = "NEW";
  private static final Logger LOG = Logger.getLogger(MethodsPanel.class.getName());
  
  
  
  private JFileChooser choose = new JFileChooser();
 
  private JTable tbl;
  private Genson genson;
  private Image removeImage;
  private Icon  removeIcon;
  private JTextField txtField;
  private ChapterSettings settings;
    
  public static class Row {
    public PersistenceId id;
    public Long optLock;
    public String method;
    public String[] books;
    public String[][] chapters;
  }
  
  private List<Row> model = new ArrayList<>();
  private PersistenceId current;
  private Row rowSet;
  
  private Model tableModel;
  
    class Model extends AbstractTableModel {

    @Override
    public int getRowCount() {
      return model.size();
    }

    @Override
    public int getColumnCount() {
      return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      Row row = model.get(rowIndex);
      switch(columnIndex) {
        case 0: return row.method;
        case 1: return Objects.equals(row.id, current);
        case 2: return removeIcon;
      }
      return null;
    }

    void add(Row row) {
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

      }
      return super.getColumnName(column);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      switch(columnIndex) {
        case 0: return String.class;
        case 1: return Boolean.class;
        case 2: return Icon.class;
      }
      return super.getColumnClass(columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      Row row = model.get(rowIndex);
      switch(columnIndex) {
        case 0: row.method = Objects.toString(aValue, ""); break;
        case 1: if (Boolean.FALSE.equals(aValue) && Objects.equals(current,row.id)) current = null;
                if (Boolean.TRUE.equals(aValue)) current = row.id;
                fireTableDataChanged();
      }
    }
    
  }
  
    
    public void valueChanged(ListSelectionEvent event) {
      if (rowSet != null) {
        rowSet.method = txtField.getText();
        settings.makeObjects();
        rowSet.books = settings.getBooks();
        rowSet.chapters = settings.getChapters();
      }
      int rowIndex = tbl.getSelectedRow();
      if (!event.getValueIsAdjusting() && rowIndex != -1) {
        
        // do some actions here, for example
        // print first column value from selected row
        rowSet = model.get(rowIndex);
        txtField.setText(rowSet.method);
        settings.setBooks(rowSet.books);
        settings.setChapters(rowSet.chapters);
        settings.makeGUI();
      }
    }

  public MethodsPanel() {
    super(null);
    genson = StoredRestManager.getInstance().getGenson();
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    JLabel l;
    l = new JLabel("Gekoppelde methodes");
    add(l);
    tbl = new JTable(tableModel = new Model());
    
    tbl.getSelectionModel().addListSelectionListener(this);
    
    
    
    
    removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_STUDENTMODEL_IMAGE);
    removeIcon  = new ImageIcon(removeImage);
    add(new JScrollPane(tbl));
    
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
    settings.makeGUI(settings.aantalRijen, settings.aantalKolommen);
    
    add(settings);
    
    
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String action = e.getActionCommand();
    if (OPEN == action) {
      if (JFileChooser.APPROVE_OPTION  == choose.showOpenDialog(this))  {
        try {
          FileInputStream in = new FileInputStream(choose.getSelectedFile());
          Row row = genson.deserialize(in, Row.class);
          in.close();
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
      Row row = new Row();
      row.method = "Untitled";
      row.books = new String[] { "Untitled book" };
      row.chapters  = new String[][] {{ "Untitled chapter" }};
      row.id = new PersistenceId();
      Random random = new Random();
      row.id.setIdString("LOCAL;none;" + (random.nextLong() >>> 1));
      
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

  public void safeTo(DomStudentModelContext current) {
    // TODO Auto-generated method stub
    
  }

  public void loadFrom(DomStudentModelContext current) {
    // TODO Auto-generated method stub
    
  }
}
