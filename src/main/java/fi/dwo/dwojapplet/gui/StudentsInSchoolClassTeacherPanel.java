/*
 * Created on Mar 24, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomGetSingleSchoolStudent;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSingleSchoolStudent;
import fi.dwo.commons.dom.entities.DomStudent;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.domutils.DomSchoolClassListCellRenderer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * The panel which shows the school classes for a teacher.
 */
public class StudentsInSchoolClassTeacherPanel extends JPanel implements CenterSubPanel, ActionListener {

    private static final Logger LOG = Logger.getLogger(StudentsInSchoolClassTeacherPanel.class.getName());

    private StudentsInSchoolClassTeacherPanelProperties prop = new StudentsInSchoolClassTeacherPanelProperties();
    private StudentsInSchoolClassTeacherPanelTableModel tableModel;
    private DomSchoolClass schoolClass;
    private CenterPanel center;

    private JButton backButton;
    private JComboBox targetSchoolClassBox;
    private JButton deleteButton;
    private JButton copyToSchoolClassButton;

    private Image editImage;
    private Image noImage;
    private Image loginImage;

    private JPanel jtbl;

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
            switch (col) {
                case 1:
                    String s = TextMapper.getText(TextMapper.GUIC_TLTP_USERS_CLASS);
                    setToolTipText(MessageFormat.format(s, arguments));
                    break;
                case 2:
                    setToolTipText(TextMapper.getText(TextMapper.GUIC_TLTP_EDIT_CLASS));
                    break;
                default:
                    setToolTipText("Message " + col); // TODO ....
            }
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
                    DomStudent student = (DomStudent) tableModel.getValueAt(row, tableModel.getColumnCount());
                    DomGetSingleSchoolStudent getStudent = new DomGetSingleSchoolStudent();
                    getStudent.setDomSchoolClass(schoolClass);
                    getStudent.setDomStudent(student);
                    DomSingleSchoolStudent user = prop.getSingleSchoolStudent(getStudent);
                    AccountDataFullUserJPanel panel = new AccountDataFullUserJPanel();
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
                        tableModel.init(prop, schoolClass, loginImage, editImage, noImage);
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
                }
                catch (LoginException ex) {
                    Dwo2Exception err=  new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, ex.getMessage());
                    LOG.log(Level.SEVERE, null, ex);
                    GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), err);
                }
                catch (Dwo2Exception e) {
                    LOG.log(Level.SEVERE, null, e);
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

        JTable jtable = new JTable();
        jtable.setMinimumSize(new Dimension(400, 300));
        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.Y_AXIS));
        jtbl.add(jtable.getTableHeader());
        jtbl.add(jtable);
        jtbl.add(Box.createHorizontalGlue());
        //jtbl.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);
        tableModel = new StudentsInSchoolClassTeacherPanelTableModel();

        tableModel.init(prop, schoolClass, loginImage, editImage, noImage);
        jtable.setModel(tableModel);
        if (jtable.getRowCount() > 0) {
            jtable.setRowSelectionInterval(0, 0);
        }
        jtable.setRowSelectionAllowed(false);
        jtable.setColumnSelectionAllowed(false);
        jtable.setCellSelectionEnabled(false);
        TableUtil.setDefaults(jtable, true, new StudentsInSchoolClassTeacherPanel.ImageRenderer(), new StudentsInSchoolClassTeacherPanel.ImageButtonEditor());
        TableUtil.setJTableSizes(jtable);

//        TableUtil.setDefaults(jtable, false, new ImageRenderer(), new ImageButtonEditor());
//        TableUtil.setJTableSizes(jtable);
// TODO shrink to fit heeft 520 als breedte
//        Dimension size = jtable.getPreferredSize();
//        if (size.width < 520) {
//            size.width = 520;
//        }
//        jtable.setMaximumSize(size);
//        jtbl.setLocation(30, addTeacherButton.getSize().height
//                + addTeacherButton.getLocation().y + 15);
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
    public StudentsInSchoolClassTeacherPanel(DomSchoolClass sc) throws Dwo2Exception {
        super(null);
        this.schoolClass = sc;
        this.setSize(480, 500);

        //fetch user details.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.setAlignmentY(TOP_ALIGNMENT);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        /* Add Remove-class image */
        MediaTracker tr = new MediaTracker(this);
        editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_CLASS_IMAGE);
        noImage = DwoHelper.getResourceImage(GuiConstants.EMPTY_IMAGE);
        loginImage = DwoHelper.getResourceImage(GuiConstants.STUDENT_IMAGE);
        tr.addImage(editImage, 0);
        tr.addImage(noImage, 1);
        tr.addImage(loginImage, 2);
        try {
            tr.waitForAll();
        }
        catch (Exception e) {
        }

        //FontMetrics fm;
        backButton = new JButton(TextMapper.getText(TextMapper.BTN_BACK));
        backButton.setSize(backButton.getPreferredSize());
        backButton.addActionListener(this);
        deleteButton = new JButton(TextMapper.getText(TextMapper.BTN_DELETE));
        deleteButton.setSize(deleteButton.getPreferredSize());
        deleteButton.addActionListener(this);
        copyToSchoolClassButton = new JButton(TextMapper.getText(TextMapper.BTN_COPYTOSCHOOLCLASS));
        copyToSchoolClassButton.setSize(copyToSchoolClassButton.getPreferredSize());
        copyToSchoolClassButton.addActionListener(this);
        targetSchoolClassBox = new JComboBox(new Vector<DomSchoolClass>(prop.getTeachersOtherSchoolClasses(sc)));
        DomSchoolClassListCellRenderer renderer = new DomSchoolClassListCellRenderer();
        targetSchoolClassBox.setRenderer(renderer);
        targetSchoolClassBox.setMaximumRowCount(10);
        targetSchoolClassBox.addActionListener(this);
        Box header = Box.createHorizontalBox();
        header.setAlignmentX(Component.RIGHT_ALIGNMENT);
        header.setMaximumSize(new Dimension(3000, 100));
        header.setBorder(BorderFactory.createEmptyBorder());//25, 25, 25, 25, Color.BLACK));
        header.add(backButton);
        header.add(Box.createRigidArea(new Dimension(30, 0)));
        header.add(deleteButton);
        header.add(Box.createRigidArea(new Dimension(30, 0)));
        header.add(copyToSchoolClassButton);
        header.add(Box.createRigidArea(new Dimension(10, 0)));
        header.add(targetSchoolClassBox);
        header.add(Box.createGlue());
        this.add(header);
        //addClassButton.setVisible(true);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
//        this.add(Box.createVerticalStrut(15));
        buildJTable();
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
        return new HeaderPanel(TextMapper.getText(TextMapper.GUIC_CLASS_MANAGEMENT));
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == copyToSchoolClassButton) {
            try {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (((Boolean) tableModel.getValueAt(i, 5)).equals(true)) {
                        DomStudent student = (DomStudent) tableModel.getValueAt(i, tableModel.getColumnCount());
                        DomSchoolClass toSchoolClass = (DomSchoolClass) targetSchoolClassBox.getSelectedItem();
                        prop.submitStudentToSchoolClass(schoolClass, toSchoolClass, student);
                    }
                }
                tableModel.init(prop, getSchoolClass(), loginImage, editImage, noImage);
                tableModel.fireTableDataChanged();
                GuiCreator.instance().ShowConfirmDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_DONE_MSG));
            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.FINE, null, ex);
                GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), ex);
            }
        } else if (e.getSource() == deleteButton) {
            try {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (((Boolean) tableModel.getValueAt(i, 5)).equals(true)) {
                        DomStudent student = (DomStudent) tableModel.getValueAt(i, tableModel.getColumnCount());
                        prop.removeStudentFromSchoolClass(schoolClass, student);
                    }
                }
                tableModel.init(prop, getSchoolClass(), loginImage, editImage, noImage);
                tableModel.fireTableDataChanged();
                GuiCreator.instance().ShowConfirmDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_DONE_MSG));
            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.FINE, null, ex);
                GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), ex);
            }
        } else if (e.getSource() == backButton) {
            try {
                ClassTeacherPanel panel = new ClassTeacherPanel();
                center.loadCenter(panel);

            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.FINE, null, ex);
                GuiCreator.instance().ShowErrorDialog(this, ex);
            }
        }
        tableModel.fireTableDataChanged();
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
