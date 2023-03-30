package fi.dwo.dwojapplet.gui.action;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import fi.beans.numworxlf.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JComboBox;
import fi.beans.numworxlf.JRadioButton;
import fi.beans.numworxlf.JScrollPane;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.AddSchoolDialog;
import fi.dwo.dwojapplet.gui.ConfirmDialog;
import fi.dwo.dwojapplet.gui.GuiConstants;
import fi.dwo.dwojapplet.gui.TableUtil;
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
    private JComboBox<String> access, teachers, classes, school;
    private DomACL acl = new DomACL();
    
    
    AddAccessPanel() {
      super(new SpringLayout());
      Vector<String> tv = new Vector<>();
      AccessControlPanel.this.teachers.keySet().forEach(t -> tv.add(AccessControlPanel.this.toString(t)));
      tv.insertElementAt("Kies docent", 0);
      Vector<String> cv = new Vector<>();
      AccessControlPanel.this.classes.keySet().forEach(t -> cv.add(AccessControlPanel.this.toString(t)));
      cv.insertElementAt("Kies klas", 0);
      Vector<String> sv = new Vector<>();
      AccessControlPanel.this.school.keySet().forEach(t -> sv.add(AccessControlPanel.this.toString(t)));
      Vector<String> av = new Vector<>();
      for (ACL v: ACL.values()) { av.add(AccessControlPanel.this.toString(v));}
      
      t = new JRadioButton("Voor een docent"); teachers = new JComboBox<>(tv);
      t.addItemListener(new ItemListener() {
        
        @Override
        public void itemStateChanged(ItemEvent e) {
          teachers.setVisible(t.isSelected());
        }
      });
      c = new JRadioButton("Voor de docenten uit de klas"); classes = new JComboBox<>(cv);
      c.addItemListener(new ItemListener() {
        
        @Override
        public void itemStateChanged(ItemEvent e) {
          classes.setVisible(c.isSelected());
        }
      });
      classes.setVisible(false);
      s = new JRadioButton("Voor alle docenten"); school = new JComboBox<>(sv);
      school.setVisible(false);
      access = new JComboBox<>(av);
 // insert     
      add(new JLabel("Soort toegangsrecht")); add(access);
      add(new JLabel("Toegangsrecht voor wie?")); add(Box.createGlue());
      add(t); add(teachers);
      add(c); add(classes);
      add(s); add(school);
      ButtonGroup group = new ButtonGroup();
      t.getModel().setGroup(group);
      c.getModel().setGroup(group);
      s.getModel().setGroup(group);
      t.setSelected(true);
      teachers.setSelectedItem(DwoHelper.getCurrentUser().getUniqueDisplayName());
      Object name = DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getSchool().getSchoolName();
      school.setSelectedItem(name);
      
      AddSchoolDialog.makeCompactGrid(this, //parent
        getComponentCount() / 2, 2,
        10, 10, //initX, initY
        10, 10); //xPad, yPad
    }


    public DomACL getAcl() {
      acl.setAccess(ACL.values()[access.getSelectedIndex()]);
      if (!s.isEnabled()) return acl;
      PersistenceId entity = acl.getEntity();
      if (s.isSelected()) {
        entity = (PersistenceId) AccessControlPanel.this.school.keySet().toArray()[school.getSelectedIndex()];
      }
      else if (c.isSelected()) {
        if (classes.getSelectedIndex()==0) return null;
        entity = (PersistenceId) AccessControlPanel.this.classes.keySet().toArray()[classes.getSelectedIndex()-1];
      }
      else {
        if (teachers.getSelectedIndex()==0) return null;
        entity = (PersistenceId) AccessControlPanel.this.teachers.keySet().toArray()[teachers.getSelectedIndex()-1];    
      }
      acl.setEntity(entity);
      return acl;
    }

    public void setAcl(DomACL c2) {
      teachers.setEditable(true);
      classes.setEditable(true);
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


  private final DomTeacher NO_TEACHER = new DomTeacher();
  private final DomSchoolClass NO_CLASS = new DomSchoolClass();
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
                  model.fireTableCellUpdated(row,2);
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

    String[] names = { TextMapper.getText(TextMapper.GUIAC_TYPE), 
                       TextMapper.getText(TextMapper.GUIAC_NAME), 
                       TextMapper.getText(TextMapper.GUIAC_ACCESS), 
                       TextMapper.getText("edit"), 
                       TextMapper.getText("delete")
                       };
    @Override
    public String getColumnName(int column) {
      return names[column];
    }
  
  }

  private String toString(ACL access) {
    switch(access) {
      default:
      case NONE: return TextMapper.getText(TextMapper.GUIAC_NONE);
      case ACCESS: return TextMapper.getText(TextMapper.GUIAC_ACCESS);
      case READ: return TextMapper.getText(TextMapper.GUIAC_READ);
      case WRITE: return TextMapper.getText(TextMapper.GUIAC_WRITE);
      case FULL: return TextMapper.getText(TextMapper.GUIAC_FULL);
    }
  }

  public boolean deleteACL(DomACL c) {
    return true;
  }

  public boolean editACL(DomACL c) {
    AddAccessPanel panel = new AddAccessPanel();
    panel.setAcl(c);
    int ok = confirmDialog(panel);
    if (ok == JOptionPane.OK_OPTION) {
      c = panel.getAcl();
      return true;
    }
    return false;
  }

  private int confirmDialog(AddAccessPanel panel) {
    ConfirmDialog confirm = new ConfirmDialog(this, "");
    confirm.getContentPane().setLayout(new BorderLayout());
    confirm.getContentPane().add(panel);
    JButton okb = new JButton(TextMapper.getText(TextMapper.BTN_OK));
    okb.addActionListener(confirm::ok);
    okb.setBackground(GuiConstants.HEADER_COLOR);
    JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
    south.setBackground(Constants.COLOR21);
    south.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    south.add(okb);
    JButton cancel = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));
    cancel.addActionListener(confirm::cancel);
    cancel.setBackground(GuiConstants.HEADER_COLOR);
    okb.setPreferredSize(cancel.getPreferredSize());
    south.add(cancel);
    confirm.getContentPane().add(south, BorderLayout.SOUTH);
    JLabel title = new JLabel("Instellingen toegangsrecht");
    title.setHorizontalAlignment(JLabel.CENTER);
    title.setFont(GuiConstants.HEADER_TEXT);
    title.setBackground(GuiConstants.HEADER_COLOR); title.setOpaque(true);
    title.setForeground(GuiConstants.MAIN_BACKGROUND);
    Border outer = BorderFactory.createEmptyBorder(10, 0, 10, 0);
    title.setBorder(outer);
    confirm.getContentPane().add(title, BorderLayout.NORTH);
    confirm.pack();
    confirm.center();
    confirm.show();
    int ok = confirm.getOption();
    return ok;
  }

  public String toString(PersistenceClassType type) {
    switch(type) {
      case PersistentUser: return TextMapper.getText(TextMapper.GUIR_OPT_TEACHER);
      case PersistentSchool: return TextMapper.getText(TextMapper.TBL_SCHOOL);
      case PersistentSchoolClass: return TextMapper.getText(TextMapper.HDR_SCHOOLCLASS);
      default:
    }
    return null;
  }

  private String toString(PersistenceId entity) {
    switch(entity.getType()) {
      case PersistentUser: return teachers.getOrDefault(entity, NO_TEACHER).getUniqueDisplayName();
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
    super(new BorderLayout());
    
    NO_CLASS.setSchoolClassName(TextMapper.getText(TextMapper.GUIAC_NO_CLASS));
    NO_TEACHER.setGivenName(TextMapper.getText(TextMapper.GUIAC_NO_CLASS));
    NO_TEACHER.setFamilyName("");
    deleteImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_COURSE_IMAGE);
    editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_COURSE_IMAGE);
    Collections.sort(classes, (a,b)-> {
      return a.getSchoolClassName().compareToIgnoreCase(b.getSchoolClassName());
    });
    this.classes = toMap(classes);
    this.school = Collections.singletonMap(school.getId(),school);
    Collections.sort(teachers, (a,b) -> {
      int r = a.getFamilyName().compareToIgnoreCase(b.getFamilyName());
      if (r != 0) return r;
      return a.getUniqueDisplayName().compareToIgnoreCase(b.getUniqueDisplayName());
    });
    this.teachers = toMap(teachers);
    DomACL o = new DomACL();
    o.setEntity(school.getId());
    o.setAccess(ACL.ACCESS);
    this.model = new TableModel(Collections.singletonList(o));
    this.table = new JTable(model);
    TableUtil.setDefaults(table, true, new ImageRenderer(), new ImageButtonEditor());
    TableUtil.setJTableSizes(table);
    this.model.acls = new ArrayList<>(acls);
    
    this.addButton = new JButton(TextMapper.getText(TextMapper.GUIAC_ADD));
    this.addButton.addActionListener(this);
    JScrollPane scroll = new JScrollPane(table);
    scroll.setBackground(Constants.COLOR20);
    Border outer = BorderFactory.createEmptyBorder(20, 20, 10, 20);
    scroll.setBorder(BorderFactory.createCompoundBorder(outer, scroll.getBorder()));
    add(scroll, BorderLayout.CENTER);
    JPanel flow = new JPanel(new FlowLayout(FlowLayout.CENTER));
    flow.add(addButton);
    outer = BorderFactory.createEmptyBorder(0, 0, 20, 0);
    flow.setBorder(outer);
    add(flow, BorderLayout.SOUTH);
    JLabel title = new JLabel("Toegangsrechten voor docenten");
    title.setBackground(GuiConstants.HEADER_COLOR);
    title.setFont(GuiConstants.HEADER_TEXT);
    title.setForeground(GuiConstants.MAIN_BACKGROUND);
    title.setHorizontalAlignment(JLabel.CENTER);
    title.setOpaque(true);
    outer = BorderFactory.createEmptyBorder(10, 0, 10, 0);
    title.setBorder(outer);
    add(title, BorderLayout.NORTH);
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
      int ok = confirmDialog(panel);
      if (ok == JOptionPane.OK_OPTION) {
        int i = model.getRowCount();
        DomACL acl = panel.getAcl();
        if(acl != null) model.acls.add(acl);
        model.fireTableRowsInserted(i, i);
      }
    }
    
  }

  public List<DomACL> getAcls() {
    return model.acls;
  }

}
