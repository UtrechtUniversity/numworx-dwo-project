/*
 * Created on Mar 24, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSchoolClass4Teacher;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.persistence.MapperCreator;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.apache.xmlrpc.applet.XmlRpcException;

/**
 * The panel which shows the school classes for a teacher.
 */
public class ClassTeacherPanel extends JPanel implements CenterSubPanel, ActionListener {

    private static final Logger LOG = Logger.getLogger(ClassTeacherPanel.class.getName());

    private ClassTeacherPanelProperties prop = new ClassTeacherPanelProperties();
    private ClassTeacherPanelTableModel tableModel;

    private CenterPanel center;

    private JButton addClassButton;
    private JButton addStudentsButton;

    private Image editImage, modulesImage, studentsImage, teachersImage, removeImage;

    private JPanel jtbl;

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
                    DomSchoolClass sc = (DomSchoolClass) tableModel.getValueAt(row, tableModel.getColumnCount());
                    ClassConfigurePanel panel = new ClassConfigurePanel();
                    DomSchoolClass4Teacher fullSchoolClass = prop.getFullSchoolClass(sc);
                    panel.setSchoolClass(fullSchoolClass);
                    int result = JOptionPane.showConfirmDialog(ClassTeacherPanel.this, panel, TextMapper.getText(TextMapper.GUIC_MSG_CLASS_CONFIGURATION),
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    fullSchoolClass.setSchoolClassName(panel.getClassName());
                    fullSchoolClass.setRegistrationKey(panel.getRegistrationKey());
                    fullSchoolClass.setIconizer(panel.isIconizer());
                    //case OK persist returned values
                    if (result == JOptionPane.OK_OPTION) {
                        //persist returned values	
                        prop.updateSchoolClass(fullSchoolClass);
                        tableModel.init(prop, editImage, modulesImage, studentsImage, teachersImage, removeImage);
                        tableModel.fireTableDataChanged();
                    }
                }
                catch (Dwo2Exception ex) {
                    LOG.log(Level.FINE, null, ex);
                    JOptionPane.showMessageDialog(null, ex.getLocalizedCodeExplanation(DwoHelper.getLocale()), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
                }
                finally {
                    fireEditingStopped();
                }

            } else if (value == modulesImage) {
                Course[] allCourses = null;
                Course[] selectedSchoolCourses = null;
                SchoolClass sc = null;
                try {
                    DomSchoolClass schoolClass = (DomSchoolClass) tableModel.getValueAt(row, tableModel.getColumnCount());
                    sc = (SchoolClass) MapperCreator.instance(SchoolClass.class).get((int) MySQLPersistenceId.getId(schoolClass.getId()));
                    GuiCreator.instance().getDWO().setWait();
                    allCourses = GuiCreator.instance().getDWO().getCourses();
                    selectedSchoolCourses = sc.getSelectedSchoolCourses();
                }
                catch (IOException ex) {
                    Logger.getLogger(ClassTeacherPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (XmlRpcException ex) {
                    Logger.getLogger(ClassTeacherPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (SQLException ex) {
                    Logger.getLogger(ClassTeacherPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                finally {
                    GuiCreator.instance().getDWO().setReady();
                }
                Course[] selectedCourses = SelectCoursesDialog.selectCourses(ClassTeacherPanel.this, allCourses, selectedSchoolCourses, sc);
                if (selectedCourses != null) {
                    GuiCreator.instance().getDWO().setWait();
                    try {
                        sc.saveSelectedCourses(allCourses, selectedCourses);
                    }
                    finally {
                        GuiCreator.instance().getDWO().setReady();
                    }
                }
                fireEditingStopped();
//
            } else if (value == studentsImage) {
                try {
                    DomSchoolClass sc = (DomSchoolClass) tableModel.getValueAt(row, tableModel.getColumnCount());
                    StudentsInSchoolClassTeacherPanel panel = new StudentsInSchoolClassTeacherPanel(sc);
                    center.loadCenter(panel);
                }
                catch (Dwo2Exception ex) {
                    Logger.getLogger(ClassTeacherPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                fireEditingStopped();
            } else if (value == teachersImage) {
                try {
                    DomSchoolClass sc = (DomSchoolClass) tableModel.getValueAt(row, tableModel.getColumnCount());
                    TeachersInSchoolClassTeacherPanel panel = new TeachersInSchoolClassTeacherPanel(sc);
                    center.loadCenter(panel);
                }
                catch (Dwo2Exception ex) {
                    Logger.getLogger(ClassTeacherPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                fireEditingStopped();
            } else if (value == removeImage) {
                try {
                    DomSchoolClass sc = (DomSchoolClass) tableModel.getValueAt(row, tableModel.getColumnCount());

                    if (GuiCreator.instance().ShowConfirmDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_CONFIRM)) == JOptionPane.OK_OPTION) {
                        //persist returned values	
                        prop.removeSchoolClass(sc);
                        tableModel.init(prop, editImage, modulesImage, studentsImage, teachersImage, removeImage);
                        tableModel.fireTableDataChanged();
                    }
                }
                catch (Dwo2Exception ex) {
                    Logger.getLogger(ClassTeacherPanel.class.getName()).log(Level.FINE, null, ex);
                    GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), ex);
                }
                finally {
                    fireEditingStopped();
                }
            }
        }
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
        if (jtable.getRowCount() > 0) {
            jtable.setRowSelectionInterval(0, 0);
        }
        jtable.setRowSelectionAllowed(false);
        jtable.setColumnSelectionAllowed(false);
        jtable.setCellSelectionEnabled(false);
        TableUtil.setDefaults(jtable, true, new ClassTeacherPanel.ImageRenderer(), new ClassTeacherPanel.ImageButtonEditor());
        TableUtil.setJTableSizes(jtable);

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
        }
        catch (Dwo2Exception e) {
            LOG.log(Level.SEVERE, "Can't retrieve initial user settings.", e);
            GuiCreator.instance().ShowErrorDialog(this, e);
        }

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
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
        }
        catch (Exception e) {
        }

        //FontMetrics fm;
        addClassButton = new JButton(TextMapper.getText(TextMapper.BTN_NEW_CLASS));
        addClassButton.setSize(addClassButton.getPreferredSize());
        addClassButton.addActionListener(this);
        addStudentsButton = new JButton(TextMapper.getText(TextMapper.BTN_NEW_STUDENTS));
        addStudentsButton.setSize(addStudentsButton.getPreferredSize());
        addStudentsButton.addActionListener(this);
        //addClassButton.setLocation(30, 10);
//        addClassButton.setVisible(GuiCreator.instance().getUser().hasRight(User.CHANGE_CLASS_RIGHT_TEACHER));
        Box header = Box.createHorizontalBox();
        header.add(addClassButton);
        header.add(Box.createHorizontalGlue());
        header.add(addStudentsButton);
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
    public Component getHeaderPanel() {
        return new HeaderPanel(TextMapper.getText(TextMapper.GUIC_CLASS_MANAGEMENT));
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
            DomSchoolClass4Teacher sc = new DomSchoolClass4Teacher();

            panel.setSchoolClass(sc);
            int result = JOptionPane.showConfirmDialog(ClassTeacherPanel.this, panel, TextMapper.getText(TextMapper.GUIC_MSG_CLASS_CONFIGURATION),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
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

                }
                catch (Dwo2Exception ex) {
                LOG.log(Level.FINE, null, ex);
                GuiCreator.instance().ShowErrorDialog(center, ex);
                }
            }
        } else if (e.getSource() == addStudentsButton) {
            try {
                NewSingleSchoolStudentsTeacherPanel panel = new NewSingleSchoolStudentsTeacherPanel();
                center.loadCenter(panel);
            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.FINE, null, ex);
                GuiCreator.instance().ShowErrorDialog(center, ex);
            }
        }
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
