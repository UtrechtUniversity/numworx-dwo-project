/**
 * Copyrighted Mar 11, 2016
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomGetSingleSchoolStudent;
import fi.dwo.rest.dom.entities.DomSchoolAdmin;
import fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import fi.dwo.rest.dom.entities.DomStudent;
import fi.dwo.rest.dom.entities.DomTeacher;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.util.Dwo2ExceptionTranslator;
import java.awt.Color;
import java.awt.Component;
import static java.awt.Component.LEFT_ALIGNMENT;
import static java.awt.Component.TOP_ALIGNMENT;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

/**
 * Panel that displays a list of users of a type selected by a radio button. The
 * panels allows to remove users from the school and edit their profile and
 * password if they are a singleschoolstudent member of that school.
 *
 * Clicking on the class symbol in the list shows a list of the classes they are
 * a member of.
 *
 * @author Gert van der Plas
 */
public class UsersInSchoolSchoolAdminPanel extends JPanel implements CenterSubPanel, ActionListener {

    private static final Logger LOG = Logger.getLogger(UsersInSchoolSchoolAdminPanel.class.getName());

    private UsersInSchoolSchoolAdminPanelProperties prop = new UsersInSchoolSchoolAdminPanelProperties();
    private UsersInSchoolSchoolAdminPanelTableModel tableModel;
    private CenterPanel center;

    public enum UserType {

        STUDENT, TEACHER, SCHOOLADMIN
    };
    UserType userType = UserType.STUDENT;

//    private JButton deleteButton;
    private JButton addTeachersButton;
    JRadioButton studentRadio;
    JRadioButton teacherRadio;
    JRadioButton schoolAdminRadio;
    ButtonGroup userTypeButtonGroup;

    private Image editImage;
    private Image emptyImage;
    private Image studentImage;
    private Image teacherImage;
    private Image removeImage;

    private JPanel jtbl;
    private TableRowSorter rowSorter;

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
            this.fireEditingStopped();
//            final GuiCreator instance = GuiCreator.instance();
            if (value == editImage) {
                try {
                    DomStudent student = (DomStudent) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
                    DomGetSingleSchoolStudent getStudent = new DomGetSingleSchoolStudent();
                    getStudent.setDomStudent(student);
                    DomSingleSchoolStudent user = prop.getSingleSchoolStudent(getStudent);
                    AccountDataFullStudentJPanel panel = new AccountDataFullStudentJPanel();
                    panel.setUser(user);
                    panel.setVisible(true);
                    int result = JOptionPane.showConfirmDialog(GuiCreator.instance().mainPanel, panel, TextMapper.getText(TextMapper.GUIC_MSG_CLASS_CONFIGURATION),
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    //case OK persist returned values
                    //user = new DomSingleSchoolStudent(panel.getUser()); superfluous.
                    if (result == JOptionPane.OK_OPTION) {
                        //persist returned values
                        user = new DomSingleSchoolStudent(panel.getUser());
                        prop.updateSingleSchoolStudent(user);
                        tableModel.init(prop.getStudentsInSchool(), removeImage, studentImage, editImage, emptyImage);
                        tableModel.fireTableDataChanged();
                    }
                } catch (Dwo2Exception ex) {
                    LOG.log(Level.FINE, "", ex);
                    JOptionPane.showMessageDialog(null, ex.getLocalizedCodeExplanation(DwoHelper.getLocale()), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
                } finally {
                    fireEditingStopped();
                }
            } else if (value == studentImage || value == teacherImage) {
                try {
                    if (studentRadio.isSelected()) {
                        DomStudent user = (DomStudent) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
                        UsersSchoolClassesSchoolAdminPanel panel = new UsersSchoolClassesSchoolAdminPanel(user, UsersSchoolClassesSchoolAdminPanel.UserType.STUDENT);
                        center.loadCenter(panel);
                    } else if (teacherRadio.isSelected()) {
                        DomTeacher user = (DomTeacher) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
                        UsersSchoolClassesSchoolAdminPanel panel = new UsersSchoolClassesSchoolAdminPanel(user, UsersSchoolClassesSchoolAdminPanel.UserType.TEACHER);
                        center.loadCenter(panel);
                    }
                } catch (Dwo2Exception ex) {
                    LOG.log(Level.SEVERE, "", ex);
                    GuiCreator.instance().ShowErrorDialog(center, ex);
                }
            } else if (value == removeImage) {
                try {
                    if (studentRadio.isSelected()) {
                        DomStudent user = (DomStudent) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());

                        int answer;
                        if (user.getSingleSchool()) {
                            String msg = MessageFormat.format(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoHelper.getLocale(), Dwo2ExceptionCode.User_ConfirmSingleSchoolStudentDelete), user.getUniqueDisplayName());
                            answer = GuiCreator.instance().ShowConfirmDialog(center, msg);
                        } else {
                            String msg = MessageFormat.format(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoHelper.getLocale(), Dwo2ExceptionCode.User_ConfirmRegularSchoolStudentDelete), user.getUniqueDisplayName());
                            answer = GuiCreator.instance().ShowConfirmDialog(center, msg);
                        }
                        if (answer == JOptionPane.OK_OPTION) {
                            if (user.getSingleSchool()) {
                                prop.removeSingleSchoolStudentFromSchool(user);
                            } else {
                                prop.removeStudentFromSchool(user);
                            }
                            tableModel.init(prop.getStudentsInSchool(), removeImage, studentImage, editImage, emptyImage);
                        }
                    } else if (teacherRadio.isSelected()) {
                        DomTeacher user = (DomTeacher) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
                        String msg = MessageFormat.format(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoHelper.getLocale(), Dwo2ExceptionCode.User_ConfirmTeacherFromSchoolDelete), user.getUniqueDisplayName());
                        if (JOptionPane.OK_OPTION
                                == GuiCreator.instance().ShowConfirmDialog(center, msg)) {
                            prop.removeTeacherFromSchool(user);
                            tableModel.init(prop.getTeachersInSchool(), removeImage, studentImage, editImage, emptyImage);
                        }
                    } else if (schoolAdminRadio.isSelected()) {
                        DomSchoolAdmin user = (DomSchoolAdmin) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
                        String msg = MessageFormat.format(Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoHelper.getLocale(), Dwo2ExceptionCode.User_ConfirmSchoolAdminFromSchoolDelete), user.getUniqueDisplayName());
                        if (JOptionPane.OK_OPTION
                                == GuiCreator.instance().ShowConfirmDialog(center, msg)) {
                            prop.removeSchoolAdminFromSchool(user);
                            tableModel.init(prop.getSchoolAdminsInSchool(), removeImage, studentImage, editImage, emptyImage);
                        }
                    }
                } catch (Dwo2Exception ex) {
                    LOG.log(Level.FINE, "", ex);
                    JOptionPane.showMessageDialog(null, ex.getLocalizedCodeExplanation(DwoHelper.getLocale()), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
                } finally {
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
        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.PAGE_AXIS));

        JTable jtable = new JTable();
        jtable.getTableHeader().setReorderingAllowed(false);
        jtable.setMinimumSize(new Dimension(400, 300));
        jtable.setFillsViewportHeight(true);
        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.Y_AXIS));
        jtbl.add(jtable.getTableHeader());
        jtbl.add(Box.createHorizontalGlue());
        jtbl.setBackground(GuiConstants.MAIN_BACKGROUND);
        tableModel = new UsersInSchoolSchoolAdminPanelTableModel();
        List userList = null;
        Image image = emptyImage;
        switch (userType) {
            case STUDENT:
                userList = prop.getStudentsInSchool();
                image = studentImage;
                break;
            case TEACHER:
                userList = prop.getTeachersInSchool();
                image = teacherImage;
                break;
            case SCHOOLADMIN:
                userList = prop.getSchoolAdminsInSchool();
                image = emptyImage;
                break;
        }

        tableModel.init(userList, removeImage, image, editImage, emptyImage);
        jtable.setModel(tableModel);
        rowSorter = new TableRowSorter(tableModel);
        rowSorter.toggleSortOrder(3);//
        jtable.setRowSorter(rowSorter);

        if (jtable.getRowCount() > 0) {
            jtable.setRowSelectionInterval(0, 0);
        }
        jtable.setRowSelectionAllowed(false);
        jtable.setColumnSelectionAllowed(false);
        jtable.setCellSelectionEnabled(false);
        jtable
                .setDefaultRenderer(Image.class, new UsersInSchoolSchoolAdminPanel.ImageRenderer());
        jtable
                .setDefaultEditor(Image.class, new UsersInSchoolSchoolAdminPanel.ImageButtonEditor());
        jtable.setBackground(GuiConstants.CELL_BACKGROUND);
        jtable.setGridColor(Color.white);
        jtable.setRowMargin(8);
        jtable.getColumnModel().setColumnMargin(2);
        jtable.setBorder(null);

        TableUtil.setJTableSizes(jtable);
        TableUtil.setBorder(jtable);
        jtbl.add(jtable);
        jtbl.setVisible(false);
//        JScrollPane scrollPane = new JScrollPane();
//        jtable.setSize(100, 100);
////        scrollPane.getViewport().add(jtable);
//        scrollPane.setVisible(true);
//        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        this.add(scrollPane,BorderLayout.NORTH);
//        jtbl.add(Box.createVerticalGlue());

        this.add(jtbl);
        jtbl.setVisible(true);

    }

    public UsersInSchoolSchoolAdminPanel() throws Dwo2Exception {
        super(null);
        init(UserType.STUDENT);
    }

    /**
     * Creates a new ClassPanel which shows a list of classes.
     *
     */
    public UsersInSchoolSchoolAdminPanel(UserType type) throws Dwo2Exception {
        super(null);
        init(type);
    }

    private void init(UsersInSchoolSchoolAdminPanel.UserType type) throws Dwo2Exception {
        this.setSize(480, 500);
        userType = type;
        //Create the radio buttons.
        studentRadio = new JRadioButton(TextMapper.getText(TextMapper.LBL_STUDENTS));
        studentRadio.setMnemonic(KeyEvent.VK_S);
        studentRadio.setActionCommand("students");
        if (type == UserType.STUDENT) {
            studentRadio.setSelected(true);
        } else {
            studentRadio.setSelected(false);
        }
        studentRadio.addActionListener(this);
        teacherRadio = new JRadioButton(TextMapper.getText(TextMapper.LBL_TEACHERS));
        teacherRadio.setActionCommand("teachers");
        studentRadio.setMnemonic(KeyEvent.VK_T);
        if (type == UserType.TEACHER) {
            teacherRadio.setSelected(true);
        } else {
            teacherRadio.setSelected(false);
        }
        teacherRadio.addActionListener(this);
        schoolAdminRadio = new JRadioButton(TextMapper.getText(TextMapper.LBL_SCHOOLADMINS));
        schoolAdminRadio.setActionCommand("schooladmins");
        studentRadio.setMnemonic(KeyEvent.VK_A);
        if (type == UserType.SCHOOLADMIN) {
            schoolAdminRadio.setSelected(true);
        } else {
            schoolAdminRadio.setSelected(false);
        }
        schoolAdminRadio.addActionListener(this);

        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(studentRadio);
        group.add(teacherRadio);
        group.add(schoolAdminRadio);

        //fetch user details.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.setAlignmentY(TOP_ALIGNMENT);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        /* Add Remove-class image */
        MediaTracker tr = new MediaTracker(this);
        editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_CLASS_IMAGE);
        emptyImage = DwoHelper.getResourceImage(GuiConstants.EMPTY_IMAGE);
        studentImage = DwoHelper.getResourceImage(GuiConstants.USERS_CLASS_IMAGE);
        teacherImage = DwoHelper.getResourceImage(GuiConstants.TEACHER_CLASS_IMAGE);
        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_CLASS_IMAGE);
        tr.addImage(editImage, 0);
        tr.addImage(emptyImage, 1);
        tr.addImage(studentImage, 2);
        tr.addImage(teacherImage, 3);
        tr.addImage(removeImage, 4);

        try {
            tr.waitForAll();
        } catch (Exception e) {
        }

        //FontMetrics fm;
//        deleteButton = new JButton(TextMapper.getText(TextMapper.BTN_NEW_STUDENTS));
//        deleteButton.setSize(deleteButton.getPreferredSize());
//        deleteButton.addActionListener(this);
        Box header = Box.createHorizontalBox();
        header.setAlignmentX(Component.RIGHT_ALIGNMENT);
        header.setMaximumSize(new Dimension(3000, 100));
        header.setBorder(BorderFactory.createEmptyBorder());//25, 25, 25, 25, Color.BLACK));
//        header.add(Box.createRigidArea(new Dimension(30, 0)));
        header.add(studentRadio);
        header.add(teacherRadio);
        header.add(schoolAdminRadio);
        header.setBackground(GuiConstants.MAIN_BACKGROUND);
        addTeachersButton = new JButton(TextMapper.getText(TextMapper.BTN_NEW_TEACHERS));
        addTeachersButton.setVisible(false);
        addTeachersButton.setSize(addTeachersButton.getPreferredSize());
        addTeachersButton.addActionListener(this);
        header.add(Box.createGlue());
        header.add(addTeachersButton);
        if (type == UserType.TEACHER) {
            addTeachersButton.setVisible(true);
        }
//        header.add(deleteButton);
        this.add(header);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        buildJTable();
//        addStudentsButton = new JButton(TextMapper.getText(TextMapper.BTN_NEW_STUDENTS));
//        addStudentsButton.setSize(addStudentsButton.getPreferredSize());
//        addStudentsButton.addActionListener(this);
//        Box footer = Box.createHorizontalBox();
//        footer.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        footer.setMaximumSize(new Dimension(3000, 100));
//        footer.setBorder(BorderFactory.createEmptyBorder());//25, 25, 25, 25, Color.BLACK));
//        footer.add(addStudentsButton);
//        this.add(footer);
        this.add(Box.createVerticalGlue());
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
        return new HeaderPanel(TextMapper.getText(TextMapper.GUIMNU_USERS_SCHOOL));
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == studentRadio) {
            //redo table
            try {
                List userList = prop.getStudentsInSchool();
                tableModel.init(userList, removeImage, studentImage, editImage, emptyImage);
                tableModel.fireTableDataChanged();
                addTeachersButton.setVisible(false);
//                addStudentsButton.setVisible(true);

            } catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, "", ex);
            }
        } else if (e.getSource() == teacherRadio) {
            //redo table
            try {
                //              addStudentsButton.setVisible(false);
                List userList = prop.getTeachersInSchool();
                tableModel.init(userList, removeImage, teacherImage, editImage, emptyImage);
                tableModel.fireTableDataChanged();
                addTeachersButton.setVisible(true);
            } catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, "", ex);
            }
        } else if (e.getSource() == schoolAdminRadio) {
            //redo table
            try {
                //            addStudentsButton.setVisible(false);
                List userList = prop.getSchoolAdminsInSchool();
                tableModel.init(userList, removeImage, teacherImage, editImage, emptyImage);
                tableModel.fireTableDataChanged();
                addTeachersButton.setVisible(false);
            } catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, "", ex);
            }
        } else if (e.getSource() == addTeachersButton) {
            try {
                NewTeacherSchoolAdminPanel panel = new NewTeacherSchoolAdminPanel();
                center.loadCenter(panel);
            } catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, "", ex);
                GuiCreator.instance().ShowErrorDialog(this, ex);
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
