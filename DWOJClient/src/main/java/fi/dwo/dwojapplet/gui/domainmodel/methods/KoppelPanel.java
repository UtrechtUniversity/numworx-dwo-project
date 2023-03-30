package fi.dwo.dwojapplet.gui.domainmodel.methods;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JCheckBox;
import fi.beans.numworxlf.JScrollPane;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.gui.ConfirmDialog;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.gui.TableUtil;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;

public class KoppelPanel extends JPanel {
  private static final Font font = Constants.FONT12;
  private JButton ok;
  private JButton cancel;
  private JTable settings;
  private List<DomMethod> all = MethodsProperties.instance();
  

  class TableModel extends AbstractTableModel {

    @Override
    public int getRowCount() {
      return all.size();
    }

    @Override
    public int getColumnCount() {
      return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      DomMethod dm = all.get(rowIndex);
      switch(columnIndex) {
        case 0: return dm.toString();
        case 1: return dm.key() == null || methods.contains(dm.key());
      }
      return null;
    }

    @Override
    public String getColumnName(int column) {
      return columnName[column];
    }

    Class<?>[] columnClass = new Class[] { String.class, Boolean.class };
    String[] columnName = new String[] { "Lesmethode", "Koppeling" };
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
      return columnClass[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return rowIndex>0 && columnIndex>0;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      if (isCellEditable(rowIndex, columnIndex)) {
        DomMethod dm = all.get(rowIndex);
        String key = dm.key();
        if (Boolean.TRUE.equals(aValue)) {
          methods.add(key);
        } else {
          methods.remove(key);
        }
      }
    }    
  }
  
  
  static class BooleanRenderer extends JCheckBox implements TableCellRenderer {

    private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    public BooleanRenderer() {
        super();
        setHorizontalAlignment(JLabel.CENTER);
        setBorderPainted(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        }
        else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        setSelected((value != null && ((Boolean)value).booleanValue()));

        if (hasFocus) {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        } else {
            setBorder(noFocusBorder);
        }

        return this;
    }
  }
  
  public void initTable(JTable jtable) {
    jtable.setForeground(GuiConstants.MAIN_FOREGROUND);
    jtable.getTableHeader().setForeground(GuiConstants.MAIN_FOREGROUND);
    jtable.getTableHeader().setReorderingAllowed(false);
    if (jtable.getRowCount() > 0) {
      jtable.setRowSelectionInterval(0, 0);
    }
    jtable.setRowSelectionAllowed(false);
    jtable.setColumnSelectionAllowed(false);
    jtable.setCellSelectionEnabled(false);
    TableUtil.setJTableSizes(jtable);
    int w = 80; // icon width
    for( int i = 1; i < jtable.getColumnCount(); i++) {
      TableColumn column = jtable.getColumnModel().getColumn(i);
      column.setPreferredWidth(w);
      column.setMinWidth(w);
      column.setMaxWidth(w);
    }
    
    jtable.setDefaultRenderer(Boolean.class, new BooleanRenderer());
 }

  
    public KoppelPanel() {
      super(new BorderLayout());
      JLabel header = new JLabel("Koppel aan methode");
      Box box = Box.createHorizontalBox();
      box.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
      box.setOpaque(true);
      box.setBackground(Constants.COLOR15);
      box.setAlignmentX(0);
      header.setForeground(Constants.COLOR20);
      header.setFont(font.deriveFont(24f));
      header.setHorizontalAlignment(JLabel.CENTER);
      box.add(Box.createHorizontalGlue());
      box.add(header);
      box.add(Box.createHorizontalGlue());
      JLabel title = new JLabel("Gekoppelde methodes");
      title.setFont(Constants.FONT13/*.deriveFont(18f)*/);
      title.setHorizontalAlignment(JLabel.LEADING);
      Border left = BorderFactory.createMatteBorder(0, 40, 0, 0, getBackground());
      title.setBorder(left);
      title.setForeground(Constants.COLOR15);
      title.setAlignmentX(0);
      ok = new JButton(TextMapper.getText(TextMapper.BTN_OK));
      ok.setPreferredSize(new Dimension(100, 24));
      ok.setBackground(Constants.COLOR15);
      ok.setForeground(Constants.COLOR20);
      cancel = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));    
      cancel.setPreferredSize(new Dimension(100, 24));
      cancel.setBackground(Constants.COLOR15);
      cancel.setForeground(Constants.COLOR20);
      settings = new JTable(new TableModel());
      initTable(settings);
      Box south = Box.createHorizontalBox();
      south.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      south.setOpaque(true);
      south.setBackground(Constants.COLOR21);
      south.add(Box.createHorizontalGlue());
      south.add(ok);
      south.add(Box.createHorizontalStrut(20));
      south.add(cancel);
      south.add(Box.createHorizontalGlue());
      add(south, BorderLayout.SOUTH);
      
      Box vbox = Box.createVerticalBox();
      vbox.add(box);
      vbox.add(Box.createVerticalStrut(10)); 
      vbox.add(title);
      add(vbox, BorderLayout.NORTH);
      
      JPanel pane = new JPanel(new BorderLayout());
      pane.add(settings, BorderLayout.CENTER);
      pane.add(settings.getTableHeader(), BorderLayout.NORTH);
      pane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20), BorderFactory.createLineBorder(Constants.COLOR14)));
      add(pane, BorderLayout.CENTER);
      
    }
    
    Set<String> methods = Collections.emptySet();
    
    public List<String> getMethods() {     
      return new ArrayList<>(methods);
    }
    
    public void setMethods(List<String> methods) {
      if (methods == null) this.methods = new TreeSet<>();
      else this.methods = new TreeSet<>(methods);
    }
    
    public int showDialog(JComponent parent) {
      ConfirmDialog dialog = new ConfirmDialog(parent, "");
      dialog.setContentPane(this);
      ok.addActionListener(dialog::ok);
      cancel.addActionListener(dialog::cancel);
      dialog.pack();
      dialog.center();
      dialog.setVisible(true);
      return dialog.getOption();
    }

}
