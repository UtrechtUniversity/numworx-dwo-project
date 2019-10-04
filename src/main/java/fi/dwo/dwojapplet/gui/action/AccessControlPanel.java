package fi.dwo.dwojapplet.gui.action;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SingleSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JComboBox;
import fi.beans.numworxlf.JOptionPane;
import fi.beans.numworxlf.JRadioButton;
import fi.beans.numworxlf.JScrollPane;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.AddSchoolDialog;
import fi.dwo.dwojapplet.gui.CourseNameDialog;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.gui.GuiCreator;
import fi.dwo.dwojapplet.gui.TableUtil;
import fi.dwo.dwojapplet.gui.CourseManagementPanel.ImageButtonEditor;
import fi.dwo.dwojapplet.gui.CourseManagementPanel.ImageRenderer;
import fi.dwo.dwojapplet.gui.action.AccessControlPanel.AddAccessPanel;
import nl.uu.fi.dwo.rest.dom.entities.DomACL;
import nl.uu.fi.dwo.rest.dom.entities.DomId;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.dom.entities.util.ACL;
import nl.uu.fi.dwo.rest.persistence.PersistenceClassType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public class AccessControlPanel extends JPanel implements ActionListener {
  
  public class AddAccessPanel extends JPanel {

    private JRadioButton t,c,s;
    private ButtonModel m;
    private JComboBox<String> access, teachers, classes, school;
    private DomACL acl = new DomACL();
    
    
    AddAccessPanel() {
      super(new SpringLayout());
      Vector<String> tv = new Vector<>();
      AccessControlPanel.this.teachers.keySet().forEach(t -> tv.add(AccessControlPanel.this.toString(t)));
      Vector<String> cv = new Vector<>();
      AccessControlPanel.this.classes.keySet().forEach(t -> cv.add(AccessControlPanel.this.toString(t)));
      Vector<String> sv = new Vector<>();
      AccessControlPanel.this.school.keySet().forEach(t -> sv.add(AccessControlPanel.this.toString(t)));
      Vector<String> av = new Vector<>();
      for (ACL v: ACL.values()) { av.add(AccessControlPanel.this.toString(v));}
      
      t = new JRadioButton("Teacher"); teachers = new JComboBox<>(tv);
      c = new JRadioButton("Schoolclass"); classes = new JComboBox<>(cv);
      s = new JRadioButton("School"); school = new JComboBox<>(sv);
      access = new JComboBox<>(av);
 // insert     
      add(t); add(teachers);
      add(c); add(classes);
      add(s); add(school);
      add(new JLabel("Toegang")); add(access);
      ButtonGroup group = new ButtonGroup();
      t.getModel().setGroup(group);
      c.getModel().setGroup(group);
      s.getModel().setGroup(group);
      t.setSelected(true);
      teachers.setSelectedItem(DwoHelper.getCurrentUser().getDisplayName());
      Object name = DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getSchool().getSchoolName();
      school.setSelectedItem(name);
      
      AddSchoolDialog.makeCompactGrid(this, //parent
        getComponentCount() / 2, 2,
        10, 10, //initX, initY
        10, 10); //xPad, yPad
    }


    public DomACL getAcl() {
      acl.setAccess(ACL.values()[access.getSelectedIndex()]);    
      PersistenceId entity = null;
      if (s.isSelected()) entity = (PersistenceId) AccessControlPanel.this.school.keySet().toArray()[school.getSelectedIndex()];
      else if (c.isSelected()) entity = (PersistenceId) AccessControlPanel.this.classes.keySet().toArray()[classes.getSelectedIndex()];
      else entity = (PersistenceId) AccessControlPanel.this.teachers.keySet().toArray()[teachers.getSelectedIndex()];    
      acl.setEntity(entity);
      return acl;
    }


    public void setAcl(DomACL c2) {
      acl = c2;
      access.setSelectedIndex(c2.getAccess().ordinal());
      PersistenceClassType type = c2.getEntity().getType();
      String string = AccessControlPanel.this.toString(c2.getEntity());
      switch(type) {
        case PersistentUser: 
          t.setSelected(true);
          teachers.setSelectedItem(string);
          break;
        case PersistentSchool:
          s.setSelected(true);
          school.setSelectedItem(string);
          break;
        default:
        case PersistentSchoolClass:
          c.setSelected(true);
          classes.setSelectedItem(string);
      }
      classes.setEnabled(false);c.setEnabled(false);
      school.setEnabled(false); s.setEnabled(false);
      teachers.setEnabled(false); t.setEnabled(false);
    }
  }


  private static final DomTeacher NO_TEACHER = new DomTeacher();
  private static final DomSchoolClass NO_CLASS = new DomSchoolClass();
  private static final DomSchool NO_SCHOOL = new DomSchool();

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
          if(value == editImage)
          {
              DomACL c = model.acls.get(row);
              if (editACL(c)) {
                  model.fireTableCellUpdated(row,0);
              }
          } else if (value == deleteImage)
          {
              /* Delete the acl */
              DomACL c = model.acls.get(row);;
              if(deleteACL(c)) {
                  model.acls.remove(row);
                  model.fireTableRowsDeleted(row,row);
              }        
          } 
          fireEditingStopped();
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

  class TableModel extends AbstractTableModel {

    private List<DomACL> acls;

    TableModel(List<DomACL> acls) {
      this.acls = acls;
    }

    @Override
    public int getRowCount() {
      return acls.size();
    }

    @Override
    public int getColumnCount() {
      return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      DomACL a = acls.get(rowIndex);
      switch(columnIndex) {
        case 0:
           return AccessControlPanel.this.toString(a.getEntity().getType());
        case 1:
          return AccessControlPanel.this.toString(a.getEntity());
        case 2:
          return AccessControlPanel.this.toString(a.getAccess());
        case 3:
          return editImage;
        case 4:
          return deleteImage;
      }
      return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      if (columnIndex >= 3)
        return Image.class;
      return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return columnIndex >= 3;
    }

    String[] names = { "Soort", "Naam", "Toegang", "Edit", "Verwijder" };
    @Override
    public String getColumnName(int column) {
      return names[column];
    }
  
  }

  private String toString(ACL access) {
    return access.name();
  }

  public boolean deleteACL(DomACL c) {
    return true;
  }

  public boolean editACL(DomACL c) {
    AddAccessPanel panel = new AddAccessPanel();
    panel.setAcl(c);
    int ok = JOptionPane.showConfirmDialog(this, panel, "Toegang", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (ok == JOptionPane.OK_OPTION) {
      c = panel.getAcl();
      return true;
    }
    return false;
  }

  public String toString(PersistenceClassType type) {
    switch(type) {
      case PersistentUser: return "Docent";
      case PersistentSchool: return "School";
      case PersistentSchoolClass: return "Klas";
      default:
    }
    return null;
  }

  private String toString(PersistenceId entity) {
    switch(entity.getType()) {
      case PersistentUser: return teachers.getOrDefault(entity, NO_TEACHER).getDisplayName();
      case PersistentSchool: return school.getOrDefault(entity, NO_SCHOOL).getSchoolName();
      case PersistentSchoolClass: return classes.getOrDefault(entity,NO_CLASS).getSchoolClassName();
      default:
    }
    return null;
  }

  private Map<PersistenceId,DomSchoolClass> classes;
  private Map<PersistenceId,DomSchool> school;
  private Map<PersistenceId,DomTeacher> teachers;
  private TableModel model;
  private JTable table;
  private JButton addButton;
  Image  editImage, deleteImage;


  public AccessControlPanel(List<DomACL> acls, List<DomTeacher> teachers,
      List<DomSchoolClass> classes, DomSchool school) {
    super(new BorderLayout(5,5));
    
    deleteImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_COURSE_IMAGE);
    editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_COURSE_IMAGE);

    this.classes = toMap(classes);
    this.school = Collections.singletonMap(school.getId(),school);
    this.teachers = toMap(teachers);
    this.model = new TableModel(Collections.singletonList(new DomACL()));
    this.table = new JTable(model);
    TableUtil.setDefaults(table, true, new ImageRenderer(), new ImageButtonEditor());
    this.model.acls = new ArrayList<>(acls);
    
    this.addButton = new JButton("Toegang toevoegen");
    this.addButton.addActionListener(this);
    add(new JScrollPane(table), BorderLayout.CENTER);
    add(addButton, BorderLayout.NORTH);
  }


  private <T extends DomId> Map<PersistenceId, T> toMap(List<T> classes2) {
    Map<PersistenceId, T> map = new LinkedHashMap<>();
    classes2.forEach(x -> map.put(x.getId(), x));
    return map;
  }


  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == addButton) {
      AddAccessPanel panel = new AddAccessPanel();
      int ok = JOptionPane.showConfirmDialog(this, panel, e.getActionCommand(), JOptionPane.OK_CANCEL_OPTION);
      if (ok == JOptionPane.OK_OPTION) {
        int i = model.getRowCount();
        model.acls.add(panel.getAcl());
        model.fireTableRowsInserted(i, i);
      }
    }
    
  }

  public List<DomACL> getAcls() {
    return model.acls;
  }

}
