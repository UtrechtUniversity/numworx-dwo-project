package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.rest.dom.entities.DomGetSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.domutils.DomUserListCellRenderer;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

/**
 * The panel which shows the school classes for a teacher.
 */
public class StudentsInSchoolClassSchoolAdminPanel extends JPanel implements CenterSubPanel, ActionListener {

    private static final Logger LOG = Logger.getLogger(StudentsInSchoolClassSchoolAdminPanel.class.getName());

    private StudentsInSchoolClassSchoolAdminPanelProperties prop = new StudentsInSchoolClassSchoolAdminPanelProperties();
    private StudentsInSchoolClassTeacherPanelTableModel tableModel;
    private DomSchoolClass schoolClass;
    private CenterPanel center;

    private JButton backButton;
    private JComboBox studentBox;
    private JButton deleteButton;
    private JButton copyToSchoolClassButton;
    private JButton addStudentsButton;
    private JButton toggleSelectButton;
    private JButton deleteFromSchoolButton;

    private Image editImage;
    private Image emptyImage;
    private Image loginImage;

    private JPanel jtbl;
    private TableRowSorter rowSorter;

	private JTable jtable;

    /**
     * @return the schoolClass
     */
    public DomSchoolClass getSchoolClass() {
        return schoolClass;
    }

    /**
     * @param schoolClass the schoolClass to set
     */
    public void setSchoolClass(DomSchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }

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
                    getStudent.setDomSchoolClass(schoolClass);
                    getStudent.setDomStudent(student);
                    DomSingleSchoolStudent user = prop.getSingleSchoolStudent(getStudent);
                    AccountDataFullStudentJPanel panel = new AccountDataFullStudentJPanel();
                    panel.setUser(user);
                    panel.setVisible(true);
                    int result = JOptionPane.showConfirmDialog(GuiCreator.instance().mainPanel, panel, TextMapper.getText(TextMapper.GUIP_ACCOUNTANDCONTACTINFO),
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    //case OK persist returned values
                    //user = new DomSingleSchoolStudent(panel.getUser()); superfluous.
                    if (result == JOptionPane.OK_OPTION) {
                        //persist returned values
                        user = new DomSingleSchoolStudent(panel.getUser());
                        prop.updateSingleSchoolStudent(user);
                        tableModel.init(prop.getStudentsInSchoolClass(schoolClass), loginImage, editImage, emptyImage);
                        tableModel.fireTableDataChanged();
                    }
                } catch (Dwo2Exception ex) {
                    LOG.log(Level.FINE, "", ex);
                    JOptionPane.showMessageDialog(null, ex.getLocalizedCodeExplanation(DwoHelper.getLocale()), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
                } finally {
                    fireEditingStopped();
                }
            } else if (value == loginImage) {
                fireEditingStopped();
//            //get Table setting
//                int col = tableModel.getSelectedColumn();
                int row = tableModel.getSelectedRow();
                try {
                    //set prop to table setting
                    DomStudent student = (DomStudent) tableModel.getValueAt(row, tableModel.getColumnCount());
                    DomGetSingleSchoolStudent getStudent = new DomGetSingleSchoolStudent();
                    getStudent.setDomSchoolClass(schoolClass);
                    getStudent.setDomStudent(student);
                    DomSingleSchoolStudent user = prop.getSingleSchoolStudent(getStudent);
                    GuiCreator.instance().loginWithMd5(user.getUserName(), user.getPassword());
                } catch (LoginException ex) {
                    Dwo2Exception err = new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, ex.getMessage());
                    LOG.log(Level.SEVERE, "", ex);
                    GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), err);
                } catch (Dwo2Exception e) {
                    LOG.log(Level.SEVERE, "", e);
                    GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), e);
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

        jtable = new JTable();
        jtable.getTableHeader().setReorderingAllowed(false);
        //jtable.setMinimumSize(new Dimension(400, 300));
        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.Y_AXIS));
        jtbl.add(jtable.getTableHeader());
        jtbl.add(jtable);
        jtbl.add(Box.createHorizontalGlue());
        //jtbl.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);
        tableModel = new StudentsInSchoolClassTeacherPanelTableModel();

        tableModel.init(prop.getStudentsInSchoolClass(schoolClass), loginImage, editImage, emptyImage);
        
        jtable.setModel(tableModel);

        /**
         * testing *
         */
        final TableCellRenderer r = jtable.getTableHeader().getDefaultRenderer();
        TableCellRenderer wrapper = new TableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component comp = r.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
                if (comp instanceof JLabel && column == 6) {
                    JLabel label = (JLabel) comp;
                }
                return comp;
            }
//
//            /**
//             * Implements the logic to choose the appropriate icon.
//             */
//            private Icon getSortIcon(JTable table, int column) {
//                SortOrder sortOrder = getColumnSortOrder(table, column);
//                if (SortOrder.UNSORTED == sortOrder) {
//                    return null;
//                }
//                
//                return SortOrder.ASCENDING == sortOrder ? new ImageIcon(editImage) : new ImageIcon(loginImage);
//            }
//
//            private SortOrder getColumnSortOrder(JTable table, int column) {
//                if (table == null || table.getRowSorter() == null) {
//                    return SortOrder.UNSORTED;
//                }
//                List<? extends SortKey> keys = table.getRowSorter().getSortKeys();
//                if (keys.size() > 0) {
//                    SortKey key = keys.get(0);
//                    if (key.getColumn() == table.convertColumnIndexToModel(column)) {
//                        return key.getSortOrder();
//                    }
//                }
//                return SortOrder.UNSORTED;
//            }

        };
        jtable.getTableHeader().setDefaultRenderer(wrapper);

        /**
         * testing *
         */
        rowSorter = new TableRowSorter(tableModel);
        rowSorter.toggleSortOrder(3);//
        jtable.setRowSorter(rowSorter);

        if (jtable.getRowCount() > 0) {
            jtable.setRowSelectionInterval(0, 0);
        }
        jtable.setRowSelectionAllowed(false);
        jtable.setColumnSelectionAllowed(false);
        jtable.setCellSelectionEnabled(false);
        TableUtil.setDefaults(jtable, true, new StudentsInSchoolClassSchoolAdminPanel.ImageRenderer(), new StudentsInSchoolClassSchoolAdminPanel.ImageButtonEditor());
        TableUtil.setJTableSizes(jtable);
        TableUtil.setBorder(jtable);
        jtbl.setVisible(false);
        this.add(jtbl);
        jtbl.setVisible(true);

    }

    /**
     * Creates a new ClassPanel which shows a list of classes.
     *
     */
    public StudentsInSchoolClassSchoolAdminPanel(final DomSchoolClass sc) throws Dwo2Exception {
        super(null);
        this.schoolClass = sc;
        //this.setSize(480, 500);
        //this.setPreferredSize(new Dimension(300, 300));

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
        loginImage = DwoHelper.getResourceImage(GuiConstants.STUDENT_IMAGE);
        tr.addImage(editImage, 0);
        tr.addImage(emptyImage, 1);
        tr.addImage(loginImage, 2);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }

        //FontMetrics fm;
        backButton = new JButton(TextMapper.getText(TextMapper.BTN_BACK));
        backButton.setSize(backButton.getPreferredSize());
        backButton.addActionListener(this);
        deleteButton = new JButton(TextMapper.getText(TextMapper.BTN_DELSELECTED));
        deleteButton.setSize(deleteButton.getPreferredSize());
        deleteButton.addActionListener(this);
        copyToSchoolClassButton = new JButton(TextMapper.getText(TextMapper.BTN_ADD) + ":");
        copyToSchoolClassButton.setSize(copyToSchoolClassButton.getPreferredSize());
        copyToSchoolClassButton.addActionListener(this);
//        Vector<DomStudent> schoolClassVector = new Vector<DomStudent>(prop.getStudentsInSchoolNotInClass(sc));
//        Collections.sort(schoolClassVector, new Comparator<DomStudent>() {
//            public int compare(DomStudent a, DomStudent b) {
//                return a.getFamilyName().compareTo(b.getFamilyName());
//            }
//        });
//        studentBox = new JComboBox(schoolClassVector);
        studentBox = new JComboBox();
//        studentBox.setSize(70,studentBox.getHeight());
        studentBox.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
//                JComboBox comboBox = (JComboBox) e.getSource();
                Vector<DomStudent> schoolClassVector;
                try {
                    schoolClassVector = new Vector<DomStudent>(prop.getStudentsInSchoolNotInClass(sc));
                    Collections.sort(schoolClassVector, new Comparator<DomStudent>() {
                        public int compare(DomStudent a, DomStudent b) {
                            return a.getFamilyName().compareTo(b.getFamilyName());
                        }
                    });
                    DefaultComboBoxModel model = new DefaultComboBoxModel(schoolClassVector);
                    studentBox.setModel(model);
                } catch (Dwo2Exception ex) {
                    LOG.log(Level.SEVERE, "", ex);
                    GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), ex);
                }
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }
        });

        DomUserListCellRenderer renderer = new DomUserListCellRenderer(TextMapper.getText(TextMapper.LBL_CLICK_TO_SELECT_A_STUDENT));
//        if (schoolClassVector.size() > 0) {
//            studentBox.setSelectedIndex(0);
//        } else {
//            studentBox.setEnabled(false);
//        }
        studentBox.setRenderer(renderer);
        studentBox.setMaximumRowCount(10);
        studentBox.addActionListener(this);
        Box header = Box.createHorizontalBox();
//        header.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        header.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        header.setPreferredSize(new Dimension(-1, studentBox.getMinimumSize().height));
        header.setMaximumSize(new Dimension(3000, 100));
        header.setBorder(BorderFactory.createEmptyBorder());//25, 25, 25, 25, Color.BLACK));
//        header.add(Box.createVerticalStrut(100));
        header.add(backButton);
        header.add(Box.createRigidArea(new Dimension(30, 0)));
        header.add(deleteButton);
        header.add(Box.createRigidArea(new Dimension(30, 0)));
        header.add(copyToSchoolClassButton);
        header.add(Box.createRigidArea(new Dimension(10, 0)));
        header.add(studentBox);
        header.add(Box.createHorizontalGlue());
//        header.add(Box.createHorizontalGlue());
        this.add(header);
        this.add(Box.createRigidArea(new Dimension(0, 30)));
        buildJTable();
        addStudentsButton = new JButton(TextMapper.getText(TextMapper.BTN_NEW_STUDENTS));
        addStudentsButton.setSize(addStudentsButton.getPreferredSize());
        addStudentsButton.addActionListener(this);
        deleteFromSchoolButton = new JButton(
                Dwo2ExceptionTranslator.getLocalizedCodeExplanation(
                        DwoHelper.getLocale(), Dwo2ExceptionCode.GUI_BTN_deleteFromSchool));
        deleteFromSchoolButton.setSize(deleteFromSchoolButton.getPreferredSize());
        deleteFromSchoolButton.addActionListener(this);
        toggleSelectButton = new JButton(
                Dwo2ExceptionTranslator.getLocalizedCodeExplanation(
                        DwoHelper.getLocale(), Dwo2ExceptionCode.GUI_BTN_toggleSelect));
        toggleSelectButton.setSize(toggleSelectButton.getPreferredSize());
        toggleSelectButton.addActionListener(this);
        Box footer = Box.createHorizontalBox();
//        footer.setAlignmentX(Component.RIGHT_ALIGNMENT);
        footer.setPreferredSize(header.getMinimumSize());
        footer.setBorder(BorderFactory.createEmptyBorder());//25, 25, 25, 25, Color.BLACK));
        this.add(Box.createVerticalGlue());
        footer.add(toggleSelectButton);
        footer.add(Box.createRigidArea(new Dimension(10, 0)));
        footer.add(deleteFromSchoolButton);
        footer.add(Box.createHorizontalGlue());
        footer.add(addStudentsButton);
        this.add(footer);
//        this.add(Box.createVerticalGlue());
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
        return new HeaderPanel(TextMapper.getText(TextMapper.GUIC_CLASS_MANAGEMENT) + " - " + TextMapper.getText(TextMapper.HDR_EDITSTUDENTS) + " - " + TextMapper.getText(TextMapper.HDR_SCHOOLCLASS) + ": " + schoolClass.getSchoolClassName());
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == copyToSchoolClassButton) {
            DomStudent student = (DomStudent) studentBox.getSelectedItem();
            if (student != null) {
                try {
                    prop.submitStudentToSchoolClass(schoolClass, student);
                    studentBox.setSelectedIndex(-1);
                    tableModel.init(prop.getStudentsInSchoolClass(schoolClass), loginImage, editImage, emptyImage);
                    //confirm is overkill
                    //GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_DONE_MSG));
                } catch (Dwo2Exception ex) {
                    LOG.log(Level.FINE, "", ex);
                    GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), ex);
                }
            }
        } else if (e.getSource() == deleteButton) {
            try {
                int cnt = 0;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (((Boolean) tableModel.getValueAt(i, 6)).equals(true)) {
                        cnt++;
                    }
                }
                if (cnt == 0) {
                    GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_NO_STUDENTS_SELECTED));
                } else {
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        if (((Boolean) tableModel.getValueAt(i, 6)).equals(true)) {
                            DomStudent student = (DomStudent) tableModel.getValueAt(i, tableModel.getColumnCount());
                            prop.removeStudentFromSchoolClass(schoolClass, student);
                        }
                    }
                    studentBox.setSelectedIndex(-1);
                    tableModel.init(prop.getStudentsInSchoolClass(schoolClass), loginImage, editImage, emptyImage);
                    GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_DONE_MSG));
                    StudentsInSchoolClassSchoolAdminPanel panel = new StudentsInSchoolClassSchoolAdminPanel(schoolClass);
                    center.loadCenter(panel);
                }
            } catch (Dwo2Exception ex) {
                LOG.log(Level.FINE, "", ex);
                GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), ex);
            }
        } else if (e.getSource() == backButton) {
            try {
                SchoolClassesSchoolAdminPanel panel = new SchoolClassesSchoolAdminPanel();
                center.loadCenter(panel);

            } catch (Dwo2Exception ex) {
                LOG.log(Level.FINE, "", ex);
                GuiCreator.instance().ShowErrorDialog(this, ex);
            }
        } else if (e.getSource() == addStudentsButton) {
            try {
                NewSingleSchoolStudentsTeacherPanel panel
                        = new NewSingleSchoolStudentsTeacherPanel(schoolClass,
                                NewSingleSchoolStudentsTeacherPanel.UserType.SCHOOLADMIN);
                center.loadCenter(panel);
            } catch (Dwo2Exception ex) {
                LOG.log(Level.FINE, "", ex);
                GuiCreator.instance().ShowErrorDialog(center, ex);
            }
        } else if (e.getSource() == toggleSelectButton && tableModel.getRowCount() > 0) {
            boolean val = !(Boolean) tableModel.getValueAt(0, 6);
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(val, i, 6);
            }
        } else if (e.getSource() == deleteFromSchoolButton) {
            try {
                int cnt = 0;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (((Boolean) tableModel.getValueAt(i, 6)).equals(true)) {
                        cnt++;
                    }
                }
                if (cnt == 0) {
                    GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_NO_STUDENTS_SELECTED));
                } else {
                    if (GuiCreator.instance().ShowConfirmDialog(center,
                            Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoHelper.getLocale(),
                                    Dwo2ExceptionCode.User_ConfirmDeleteMultiUsersFromSchool))
                            == JOptionPane.OK_OPTION) {
                        for (int i = 0; i < tableModel.getRowCount(); i++) {
                            if (((Boolean) tableModel.getValueAt(i, 6)).equals(true)) {
                                DomStudent student = (DomStudent) tableModel.getValueAt(i, tableModel.getColumnCount());
                                if (student.getSingleSchool()) {
                                    prop.removeSingleSchoolStudentFromSchool(student);
                                } else {
                                    prop.removeStudentFromSchool(student);
                                }
                            }
                        }
                        studentBox.setSelectedIndex(-1);
                        tableModel.init(prop.getStudentsInSchoolClass(schoolClass), loginImage, editImage, emptyImage);
                        GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_DONE_MSG));
                        StudentsInSchoolClassSchoolAdminPanel panel = new StudentsInSchoolClassSchoolAdminPanel(schoolClass);
                        center.loadCenter(panel);
                    }
                }
            } catch (Dwo2Exception ex) {
                LOG.log(Level.FINE, "", ex);
                GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), ex);
            }
        }
        tableModel.fireTableDataChanged();
        TableUtil.setJTableSizes(jtable);
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
    public void stateChanged(ChangeEvent e
    ) {
    }
}
