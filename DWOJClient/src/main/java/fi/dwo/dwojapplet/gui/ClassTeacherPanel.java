/*
 * Created on Mar 24, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureTeacherSchoolClassManager;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewAccessKey;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewFrom;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewTo;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassCourseProfilewType;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassFull;
import nl.uu.fi.dwo.rest.dom.entities.util.CourseType;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.beans.numworxlf.Constants;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JOptionPane;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.persistence.entities.PersistentCourse;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.ClassCourse;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.Teacher;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

/**
 * The panel which shows the school classes for a teacher.
 */
public class ClassTeacherPanel extends JPanel implements CenterSubPanel, ActionListener {

    private static final Logger LOG = Logger.getLogger(ClassTeacherPanel.class.getName());

    private ClassTeacherPanelProperties prop = new ClassTeacherPanelProperties();
    private ClassTeacherPanelTableModel tableModel;

    private CenterPanel center;

    private JButton addClassButton;
//    private JButton addStudentsButton;

    private Image editImage, modulesImage, studentsImage, teachersImage, removeImage;

    private JPanel jtbl;
    private TableRowSorter rowSorter;
//
//    class ClassModel extends AbstractTableModel {
//
//        int cols = 5;
//
//        SchoolClass[] classes;
//
//        public ClassModel(SchoolClass[] classes) {
//            super();
//            this.classes = classes;
//            if (!GuiCreator.instance().getUser().hasRight(User.CHANGE_CLASS_RIGHT_TEACHER)) {
//                cols = 4;
//            }
//        }
//
//        @Override
//        public int getColumnCount() {
//            return cols;
//        }
//
//        @Override
//        public int getRowCount() {
//            return classes.length;
//        }
//
//        @Override
//        public Object getValueAt(int row, int col) {
//            switch (col) {
//                case 0:
//                    return classes[row].getName();
//                case 1:
//                    return usersImage;
//                case 2:
//                    return editImage;
//                case REMOVE_COL:
//                    return removeImage;
//                case ASSIGN_COL:
//                    return assignImage;
//            }
//            return null;
//        }
//
//        @Override
//        public Class getColumnClass(int col) {
//            if (col > 0) {
//                return Image.class;
//            }
//            return super.getColumnClass(col);
//        }
//
//        @Override
//        public boolean isCellEditable(int row, int col) {
//            if (col == REMOVE_COL) {
//                return GuiCreator.instance().getUser().hasRight(User.CHANGE_CLASS_RIGHT_TEACHER);
//            }
//            return col > 0;
//        }
//
//        public void removeRow(int row) {
//            SchoolClass[] sc = new SchoolClass[classes.length - 1];
//            System.arraycopy(classes, 0, sc, 0, row);
//            System.arraycopy(classes, row + 1, sc, row, sc.length - row);
//            classes = sc;
//            fireTableRowsDeleted(row, row);
//        }
//
//    }

    public class ImageRenderer extends JLabel implements TableCellRenderer {

        private ImageIcon icon = new ImageIcon();

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean selected, boolean hasFocus, int row, int col) {
            Image image = (Image) value;
            icon.setImage(image);
            setIcon(icon);
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            Object[] arguments = new Object[]{table.getValueAt(row, 0)};
//            switch (col) {
//                case 1:
//                    String s = TextMapper.getText(TextMapper.GUIC_TLTP_USERS_CLASS);
//                    setToolTipText(MessageFormat.format(s, arguments));
//                    break;
//                case 2:
//                    setToolTipText(TextMapper.getText(TextMapper.GUIC_TLTP_EDIT_CLASS));
//                    break;
////                case REMOVE_COL:
////                    String format = TextMapper.getText(TextMapper.GUIC_TLTP_DELETE_CLASS);
////                    setToolTipText(MessageFormat.format(format, arguments));
////                    break;
////                case ASSIGN_COL:
////                    format = TextMapper.getText(TextMapper.GUIC_TLTP_ASSIGN_CLASS);
////                    setToolTipText(MessageFormat.format(format, arguments));
////                    break;
//                default:
//                    setToolTipText("Message " + col); // TODO ....
//            }
            if (selected) {
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
//        ClassTeacherPanelTableModel model;
        int row;

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean arg2, int row, int col) {
            this.value = value;
            JButton button = new JButton(new ImageIcon((Image) value));
            button.addActionListener(this);
            this.row = row;
            //model = (ClassTeacherPanelTableModel) table.getModel();
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return value;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
//            final GuiCreator instance = GuiCreator.instance();
            if (value == editImage) {
                try {
                    DomSchoolClass sc = (DomSchoolClass) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
                    ClassConfigurePanel panel = new ClassConfigurePanel();
                    DomSchoolClassFull fullSchoolClass = prop.getFullSchoolClass(sc);
                    panel.setSchoolClass(fullSchoolClass);
                    int result = JOptionPane.showConfirmDialog(ClassTeacherPanel.this, panel, TextMapper.getText(TextMapper.GUIC_MSG_CLASS_CONFIGURATION),
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (panel.getClassName().isEmpty()) result = JOptionPane.CANCEL_OPTION; // iets met lege klasnamen.
                    fullSchoolClass.setSchoolClassName(panel.getClassName());
                    fullSchoolClass.setRegistrationKey(panel.getRegistrationKey());
                    fullSchoolClass.setIconizer(panel.isIconizer());
                    //case OK persist returned values
                    if (result == JOptionPane.OK_OPTION) {
                        //persist returned values	
                        prop.updateSchoolClass(fullSchoolClass);
                        tableModel.init(prop, editImage, modulesImage, studentsImage, teachersImage, removeImage);
                        tableModel.fireTableDataChanged();

                        //FIXED legacy schoolclass updaten!
                        try {
                            SchoolClass scold = PersistenceFacade.instance().toSchoolClass(Collections.singleton(fullSchoolClass))[0];
                            scold.setClassName(fullSchoolClass.getSchoolClassName());
                        } catch (PersistenceException e) {
                            LOG.log(Level.SEVERE, "should not happen!", e);
                        }
                        center.loadMenu();

                    }
                } catch (Dwo2Exception ex) {
                    LOG.log(Level.FINE, "", ex);
                    JOptionPane.showMessageDialog(null, ex.getLocalizedCodeExplanation(DwoHelper.getLocale()), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
                } finally {
                    fireEditingStopped();
                }

            } else if (value == modulesImage) {
              DomSchoolClass schoolClass = null;
              schoolClass = (DomSchoolClass) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
              new ClassTeacherModules().edit(schoolClass, ClassTeacherPanel.this);
              
              
              
//                Course[] allCourses = null;
//                Course[] selectedSchoolCourses = null;
//                SchoolClass sc = null;
//                try {
//                    sc = (SchoolClass) PersistenceFacade.instance().get(MySQLPersistenceId.getNativeId(schoolClass).intValue(),SchoolClass.class);
//                    GuiCreator.instance().getDWO().setWait();
//  ///////
//                    allCourses = GuiCreator.instance().getDWO().getCourses();
//                    selectedSchoolCourses = sc.getSelectedSchoolCourses();
////////
//                } catch (Dwo2Exception ex) {
//                    LOG.log(Level.SEVERE, null, ex);
//                } catch (PersistenceException ex) {
//                    LOG.log(Level.SEVERE, null, ex);
//				} finally {
//                    GuiCreator.instance().getDWO().setReady();
//                }
//                Course[] selectedCourses = SelectCoursesDialog.selectCourses(ClassTeacherPanel.this, allCourses, selectedSchoolCourses, sc);
//                if (selectedCourses != null) {
//                    GuiCreator.instance().getDWO().setWait();
//                    try {
//                        //sc.saveSelectedCourses(allCourses, selectedCourses);
//                        saveSelectedCourses(schoolClass, selectedSchoolCourses, selectedCourses);
//                    } finally {
//                        GuiCreator.instance().getDWO().setReady();
//                    }
//                }
                fireEditingStopped();
//
            } else if (value == studentsImage) {
                try {
                    DomSchoolClass sc = (DomSchoolClass) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
                    StudentsInSchoolClassTeacherPanel panel = new StudentsInSchoolClassTeacherPanel(sc);
                    center.loadCenter(panel);
                } catch (Dwo2Exception ex) {
                    Logger.getLogger(ClassTeacherPanel.class.getName()).log(Level.SEVERE, "", ex);
                }
                fireEditingStopped();
            } else if (value == teachersImage) {
                try {
                    DomSchoolClass sc = (DomSchoolClass) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
                    TeachersInSchoolClassTeacherPanel panel = new TeachersInSchoolClassTeacherPanel(sc);
                    center.loadCenter(panel);
                } catch (Dwo2Exception ex) {
                    Logger.getLogger(ClassTeacherPanel.class.getName()).log(Level.SEVERE, "", ex);
                }
                fireEditingStopped();
            } else if (value == removeImage) {
                try {
                    DomSchoolClass sc = (DomSchoolClass) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
                    String msg = MessageFormat.format(TextMapper.getText(TextMapper.DLG_Q_REMOVE_SCHOOLCLASS_MEMBERSHIP), sc.getSchoolClassName());
                    if (GuiCreator.instance().ShowConfirmDialog(GuiCreator.instance().getMainPanel(), msg) == JOptionPane.OK_OPTION) {
                        //persist returned values	
                        prop.removeSchoolClass(sc);
                        tableModel.init(prop, editImage, modulesImage, studentsImage, teachersImage, removeImage);
                        tableModel.fireTableDataChanged();
                    }
                    // update legacy stuff
                    refreshSchoolClasses();
                } catch (Dwo2Exception ex) {
                    Logger.getLogger(ClassTeacherPanel.class.getName()).log(Level.FINE, "", ex);
                    GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), ex);
                } finally {
                    fireEditingStopped();
                }
            }
        }

//        private DomCourse toDomCourse(Course c) {
//          DomCourse result = new DomCourse();
//          result.setId(PersistentCourse.buildPersistenceId(Long.valueOf(c.getID())));
//          result.setName(c.getName());
//          result.setWithChildren(c.isWithChildren());
//          result.setParentID(PersistentCourse.buildPersistenceId(Long.valueOf(c.getParentID())));
//          
//          return result;
//        }
        
//        private void saveSelectedCourses(DomSchoolClass schoolClass, Course[] oldselected,
//            Course[] selected) {
//          // TODO attach selected & detach oldselected
//          DomSchoolClassCourseProfilewAccessKey key = new DomSchoolClassCourseProfilewAccessKey();
//          DomSchoolClassCourseAndProfile dom;
//          dom = key;
//          dom.setDomSchoolClass(schoolClass);
//          dom.setDomDwoProfile(DWO.getDwoProfile());
//          List<Course> selectedList = Arrays.asList(selected);
//          for(Course c : oldselected) {
//            while(c != null && ! selectedList.contains(c) && c.getDwoProfile() == DWO.getDwoProfileID()) {
//                DomCourse domcourse = toDomCourse(c);
//                dom.setCourse(domcourse);
//                try {
//                  SecureTeacherSchoolClassManager.detachCourseFromClass(dom);
//                  int parent = c.getParentID();
//                  if (parent == 0) break;
//                  c = (Course) PersistenceFacade.instance().get(parent, Course.class);
//                  
//                } catch (Dwo2Exception e) {
//                  LOG.log(Level.SEVERE, "detach", e);
//                  return;
//                } catch (PersistenceException e) {
//                  LOG.log(Level.WARNING, "detach parent", e);
//                  break;
//                }
//            }
//          }
//          DomSchoolClassCourseProfilewFrom from = new DomSchoolClassCourseProfilewFrom();
//          from.setDomDwoProfile(DWO.getDwoProfile());
//          from.setDomSchoolClass(schoolClass);
//          DomSchoolClassCourseProfilewTo to = new DomSchoolClassCourseProfilewTo();
//          to.setDomDwoProfile(DWO.getDwoProfile());
//          to.setDomSchoolClass(schoolClass);
//
//          DomSchoolClassCourseProfilewType type = new DomSchoolClassCourseProfilewType();
//          type.setDomDwoProfile(DWO.getDwoProfile());
//          type.setDomSchoolClass(schoolClass);
//          
//          selectedList = Arrays.asList(oldselected);
//          for(Course c: selected) {
//            if(c.link == null) 
//              c.link = new ClassCourse();
//            DomCourse domcourse = toDomCourse(c);
//
//            if (!selectedList.contains(c)) {
//              try {
//                dom.setCourse(domcourse);
//                SecureTeacherSchoolClassManager.attachCourseToClass(dom); // Prepare attach
//              } catch (Dwo2Exception e) {
//                LOG.log(Level.SEVERE, "attach", e);
//                return;
//              }
//            }
//
//            from.setCourse(domcourse);
//            from.setFrom(c.link.getNotBefore());
//            try {
//              SecureTeacherSchoolClassManager.setFromDataClassCourse(from);
//            } catch (Dwo2Exception e) {
//              LOG.log(Level.SEVERE, "from", e);
//              return;
//            }
//
//            to.setCourse(domcourse);
//            to.setTo(c.link.getNotAfter());
//            try {
//              SecureTeacherSchoolClassManager.setToDataClassCourse(to);
//            } catch (Dwo2Exception e) {
//              LOG.log(Level.SEVERE, "to", e);
//              return;
//            }
//            type.setCourse(domcourse);
//            type.setType(CourseType.values()[c.link.getType()]);
//            try {
//              SecureTeacherSchoolClassManager.setClassCourseType(type);
//            } catch (Dwo2Exception e) {
//              LOG.log(Level.SEVERE, "type", e);
//              return;
//            }
//            key.setCourse(domcourse);
//            key.setAccessKey(c.link.getAccessKey());;
//            try {
//              SecureTeacherSchoolClassManager.setAccessKeyClassCourse(key);
//            } catch (Dwo2Exception e) {
//              LOG.log(Level.SEVERE, "key", e);
//              return;
//            }
//           
//            if (!selectedList.contains(c)) {
//              try {
//                SecureTeacherSchoolClassManager.attachCourseToClass(dom); // commit attach
//              } catch (Dwo2Exception e) {
//                LOG.log(Level.SEVERE, "attach", e);
//                return;
//              }
//            }
//           
//            
//          }
//          
//          
//        }

    }

    private int getSchoolClassID(DomSchoolClass sc) {
        try {
            return MySQLPersistenceId.getNativeId(sc).intValue();
        } catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    private void buildJTable() throws Dwo2Exception {
        if (jtbl != null) {
            remove(jtbl);
            jtbl = null;
        }
        jtbl = new JPanel();

        JTable jtable = new JTable();
        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.Y_AXIS));
        jtbl.add(jtable.getTableHeader());
        jtbl.add(jtable);
        jtbl.add(Box.createHorizontalGlue());
        //jtbl.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);
        tableModel = new ClassTeacherPanelTableModel();

        tableModel.init(prop, editImage, modulesImage, studentsImage, teachersImage, removeImage);
        jtable.setModel(tableModel);
        rowSorter = new TableRowSorter(tableModel);
        rowSorter.toggleSortOrder(0);//
        jtable.setRowSorter(rowSorter);

        if (jtable.getRowCount() > 0) {
            jtable.setRowSelectionInterval(0, 0);
        }
        jtable.setRowSelectionAllowed(false);
        jtable.setColumnSelectionAllowed(false);
        jtable.setCellSelectionEnabled(false);
        TableUtil.setDefaults(jtable, true, new ClassTeacherPanel.ImageRenderer(), new ClassTeacherPanel.ImageButtonEditor());
        TableUtil.setJTableSizes(jtable);
        jtable.setForeground(GuiConstants.MAIN_FOREGROUND);
        jtable.getTableHeader().setForeground(GuiConstants.MAIN_FOREGROUND);

//        TableUtil.setDefaults(jtable, false, new ImageRenderer(), new ImageButtonEditor());
//        TableUtil.setJTableSizes(jtable);
// TODO shrink to fit heeft 520 als breedte
//        Dimension size = jtable.getPreferredSize();
//        if (size.width < 520) {
//            size.width = 520;
//        }
//        jtable.setMaximumSize(size);
        jtbl.setLocation(30, addClassButton.getSize().height
                + addClassButton.getLocation().y + 15);
        TableUtil.setBorder(jtable);
        //TableUtil.shrinkToFit(table, jtbl, 520, 405);
        jtbl.setBorder(BorderFactory.createLineBorder(Constants.COLOR13));
        jtbl.setVisible(false);
        this.add(jtbl);
        jtbl.setVisible(true);

    }

    /**
     * Creates a new ClassPanel which shows a list of classes.
     *
     */
    public ClassTeacherPanel() throws Dwo2Exception {
        super(null);
        this.setSize(480, 500);

        //fetch user details.
        try {
            prop.init();
        } catch (Dwo2Exception e) {
            LOG.log(Level.SEVERE, "Can't retrieve initial user settings.", e);
            GuiCreator.instance().ShowErrorDialog(this, e);
        }

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(getSubHeaderColor());
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        /* Add Remove-class image */
        MediaTracker tr = new MediaTracker(this);
        editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_CLASS_IMAGE);
        modulesImage = DwoHelper.getResourceImage(GuiConstants.ASSIGN_CLASS_IMAGE);
        studentsImage = DwoHelper.getResourceImage(GuiConstants.USERS_CLASS_IMAGE);
        teachersImage = DwoHelper.getResourceImage(GuiConstants.TEACHER_CLASS_IMAGE);
        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_CLASS_IMAGE);
        tr.addImage(editImage, 0);
        tr.addImage(modulesImage, 1);
        tr.addImage(studentsImage, 2);
        tr.addImage(teachersImage, 3);
        tr.addImage(removeImage, 4);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }

        //FontMetrics fm;
        addClassButton = new JButton(TextMapper.getText(TextMapper.BTN_NEW_CLASS));
        addClassButton.setSize(addClassButton.getPreferredSize());
        addClassButton.addActionListener(this);
//        addStudentsButton = new JButton(TextMapper.getText(TextMapper.BTN_NEW_STUDENTS));
//        addStudentsButton.setSize(addStudentsButton.getPreferredSize());
//        addStudentsButton.addActionListener(this);
        //addClassButton.setLocation(30, 10);
//        addClassButton.setVisible(GuiCreator.instance().getUser().hasRight(User.CHANGE_CLASS_RIGHT_TEACHER));
        Box header = Box.createHorizontalBox();
        header.add(addClassButton);
        header.add(Box.createHorizontalGlue());
//        header.add(addStudentsButton);
        header.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(header);
        //addClassButton.setVisible(true);
        this.add(Box.createVerticalStrut(15));
        buildJTable();
    }

    /**
     * Indicate that another panel is loaded and the connections of this panel
     * must be closed.
     */
    @Override
    public void end() {

    }

    /**
     * Sets the centerpanel to communicate with.
     *
     * @param centerPanel The centerPanel to communicate with.
     */
    @Override
    public void setCenterPanel(CenterPanel centerPanel) {
        center = centerPanel;
    }

    /**
     * Returns a Panel that can function as a header panel.
     *
     * @return A panel that can function as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    @Override
    public JComponent getHeaderPanel() {
        HeaderPanel header = new HeaderPanel(TextMapper.getText(TextMapper.GUIC_CLASS_MANAGEMENT));
        header.setBackground(getSubHeaderColor());
        return header;
    }
    @Override
    public Color getSubHeaderColor() {
      return Constants.COLOR20;
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addClassButton) {
            ClassConfigurePanel panel = new ClassConfigurePanel();
            DomSchoolClassFull sc = new DomSchoolClassFull();

            panel.setSchoolClass(sc);
            int result = JOptionPane.showConfirmDialog(ClassTeacherPanel.this, panel, TextMapper.getText(TextMapper.GUIC_MSG_CLASS_CONFIGURATION),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (panel.getClassName().isEmpty()) result = JOptionPane.CANCEL_OPTION; // iets met lege klasnamen.
            
            
            sc.setSchoolClassName(panel.getClassName());
            sc.setRegistrationKey(panel.getRegistrationKey());
            sc.setIconizer(panel.isIconizer());
            //case OK persist returned values
            if (result == JOptionPane.OK_OPTION) {
                //persist returned values	
                try {
                    prop.addClass(sc);
                    tableModel.init(prop, editImage, modulesImage, studentsImage, teachersImage, removeImage);
                    tableModel.fireTableDataChanged();
                    refreshSchoolClasses();
                } catch (Dwo2Exception ex) {
                    LOG.log(Level.FINE, "", ex);
                    GuiCreator.instance().ShowErrorDialog(center, ex);
                }
            }
        }
//        else if (e.getSource() == addStudentsButton) {
//            try {
//                NewSingleSchoolStudentsTeacherPanel panel = new NewSingleSchoolStudentsTeacherPanel();
//                center.loadCenter(panel);
//            }
//            catch (Dwo2Exception ex) {
//                LOG.log(Level.FINE, null, ex);
//                GuiCreator.instance().ShowErrorDialog(center, ex);
//            }
//        }
    }

    private void refreshSchoolClasses() {
        // update schoolclass[] of teacher and school
        try {
            SchoolClass[] scold = PersistenceFacade.instance().getSchoolClass(DwoHelper.getCurrentFacadeUser());
            ((Teacher) DwoHelper.getCurrentFacadeUser()).setClasses(scold);
            School school = DwoHelper.getCurrentFacadeUser().getSchool();
            if (school != null) {
                school.setClassList( PersistenceFacade.instance().getSchoolClass(school));
            }
        } catch (PersistenceException e1) {
            LOG.log(Level.SEVERE, "should not happen!", e1); // famous last words
        }
        center.loadMenu();
    }

    /**
     * Returns the current object, as the object to add to a gui.
     *
     * @return the current object.
     * @see fi.dwo.client.gui.CenterSubPanel#getComponent()
     */
    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public Object getUserObject() {
        return null;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    }
}
