package fi.dwo.dwojapplet.gui.action;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JOptionPane;
import fi.beans.numworxlf.JScrollPane;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.gui.CourseManagementPanel;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.gui.TableUtil;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;

public class ScoUnTrashAction extends GuiAction {

  private static final Logger LOG =
      Logger.getLogger(ScoUnTrashAction.class.getName());

  class TableModel extends AbstractTableModel {
      final List<Sco> scos;
      
      TableModel(Sco[] items) {
        scos = new ArrayList<>(Arrays.asList(items));
      }

      @Override
      public int getRowCount() {
        return scos.size();
      }

      @Override
      public int getColumnCount() {
        return 4;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        Sco s = scos.get(rowIndex);
        switch(columnIndex) {
          case 0: return s.getScoName();
          case 1: return DateFormat.getDateInstance(DateFormat.SHORT).format(new Date(s.getSequencenr()));
          case 2: return resetImage;
          case 3: return removeImage;
        }
        return null;
      }

      @Override
      public String getColumnName(int column) {
        switch(column) {
          case 0: return "Activiteit";
          case 1: return "van";
          case 2: return "terugzetten";
          case 3: return "weggooien";
        }
        // TODO Auto-generated method stub
        return super.getColumnName(column);
      }

      @Override
      public Class<?> getColumnClass(int column) {
        if(column == 2 || column == 3) return Image.class;
        return super.getColumnClass(column);
      }

      @Override
      public boolean isCellEditable(int rowIndex, int column) {        
        return (column == 2 || column == 3);
      }

      void delete(int row) {
        scos.remove(row);
        fireTableRowsDeleted(row, row);
      }
    
    
  }
  
  class ImageRenderer extends JLabel implements TableCellRenderer {

    private ImageIcon icon = new ImageIcon();

    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean selected, boolean hasFocus, int row, int col) {
        Image image = (Image)value;
        if(image != null) {
            icon.setImage(image);
            setIcon(icon);
        } else {
            setIcon(null);
        }
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(true);
        if(selected)
        {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        return this;
    }

  }
  public class ImageButtonEditor extends AbstractCellEditor implements
  TableCellEditor, ActionListener {

      Object value;
      TableModel model;
      int row;

          @Override
      public Component getTableCellEditorComponent(JTable table, Object value,
              boolean arg2, int row, int col) {
          this.value = value;
          JButton button = new JButton(new ImageIcon((Image)value));
          button.addActionListener(this);
          this.row = row;
          model = (TableModel) table.getModel();
          return button;
      }

          @Override
      public Object getCellEditorValue() {
          return value;
      }

          @Override
      public void actionPerformed(ActionEvent event) {
          if(value == resetImage)
          {
              Sco sco = model.scos.get(row);
              Set set = course.getScoNames();
              String name = sco.getScoName();
              name = CourseManagementPanel.replaceDuplicate(name, set);
              sco.setSequencenr(course.getScoList().length+1);
              sco.setName(name);
              instance().updateSco(sco);
              Sco[] as = course.getScoList();
              /* Create a larger array and add the item */
              Sco[] tmp = new Sco[as.length + 1];
              System.arraycopy(as, 0, tmp, 0, as.length);
              tmp[tmp.length - 1] = sco;
              course.setScoList(tmp);
              getCenter().updateCourse(course);
              model.delete(row);
              
          } else if (value == removeImage)
          {
            Sco sco = model.scos.get(row);
            if (instance().deleteSco(sco))
              model.delete(row);
       
          } 
          fireEditingStopped();
      }


}

  
  private Course course;
  private Image removeImage, wasteImage, resetImage;
  
  public ScoUnTrashAction(Course course) {
    this("trash");
    setCourse(course);
  }

  ScoUnTrashAction(String name) {
    super(name);
    removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_COURSE_IMAGE);
    wasteImage = DwoHelper.getResourceImage("resources/wastebin.png");
    resetImage = DwoHelper.getResourceImage("resources/reseticon.gif");
    Icon icon = new ImageIcon(wasteImage);
    putValue(SMALL_ICON, icon);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Sco[] trash = null;
    try {
      trash = PersistenceFacade.instance().getTrash(course);
    } catch (PersistenceException e1) {
      LOG.log(Level.SEVERE, "gettrash " , e);
    }
    if (trash == null) return;
    String message = course.getName();
    JTable table = new JTable(new TableModel(trash));
    TableUtil.setDefaults(table, true, new ImageRenderer(), new ImageButtonEditor());
    TableUtil.setJTableSizes(table);
    JScrollPane panel = new JScrollPane(table);
    TableUtil.setBorder(panel);
    JOptionPane.showMessageDialog(getCenter(), panel, message, JOptionPane.PLAIN_MESSAGE);

  }

  public Course getCourse() {
    return course;
  }

  public void setCourse(Course course) {
    this.course = course;
  }

}
