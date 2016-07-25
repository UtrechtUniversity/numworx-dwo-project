package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.rest.util.Dwo2ExceptionTranslator;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

/**
 * This panel allows one to manage and switch between SchoolLogins.
 *
 */
public class SchoolClassManagementStudentJPanel extends JPanel implements ActionListener {

//    protected User user;
    private SchoolClassManagementStudentProperties prop = new SchoolClassManagementStudentProperties();
    private SchoolClassManagementStudentTableModel tableModel;

    private static final Logger LOG = Logger.getLogger(SchoolClassManagementStudentJPanel.class.getName());

    private final JButton registerSchoolClass;

    private JPanel jtbl;
    private TableRowSorter rowSorter;

    private Image removeImage, loginImage, emptyImage;
    private static final int SWITCH_COL = 1;
    private static final int REMOVE_COL = 2;

    /**
     * Creates a new ProfilePanel for the current user. The account of the
     * current user can be changed.
     *
     */
    public SchoolClassManagementStudentJPanel() {
        super(null);
        this.setSize(480, 500);

        //fetch user details.
        try {
            prop.init();
        }
        catch (Dwo2Exception e) {
            //Also trigggered in no active school exists.
//            LOG.log(Level.SEVERE, "Can't retrieve initial user settings.", e);
//            GuiCreator.instance().ShowMessageDialog(this, e.getLocalizedCodeExplanation(DwoHelper.getLocale()), "", JOptionPane.ERROR_MESSAGE);
        }

        //init gui (old code)
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.setAlignmentY(TOP_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        /* Add Remove-class image */
 /* Add Remove-class image */
        MediaTracker tr = new MediaTracker(this);
        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_CLASS_IMAGE);
        emptyImage = DwoHelper.getResourceImage(GuiConstants.EMPTY_IMAGE);
        loginImage = DwoHelper.getResourceImage(GuiConstants.STUDENT_IMAGE); //"resources/student.png");
        tr.addImage(removeImage, 0);
        tr.addImage(loginImage, 1);
        tr.addImage(emptyImage, 1);
        try {
            tr.waitForAll();
        }
        catch (Exception e) {
        }

        //FontMetrics fm;
        /* registerinfo label */
        JLabel l = new JLabel(TextMapper.getText(TextMapper.GUIC_CLASS_MANAGEMENT) + ":");
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setAlignmentY(TOP_ALIGNMENT);
        l.setForeground(GuiConstants.RED_COLOR);
        l.setFont(GuiConstants.RED_TEXT);
        FontMetrics fm = l.getFontMetrics(l.getFont());
        l.setBounds(10, 5, fm.stringWidth(l.getText()), fm.getHeight());
        Box b = Box.createHorizontalBox();
        b.add(l);
        b.add(Box.createHorizontalGlue());
        this.add(b);
        this.add(Box.createVerticalStrut(15));
        registerSchoolClass = new JButton(TextMapper.getText(TextMapper.GUIC_REGISTER_FOR_CLASS));
        registerSchoolClass.setSize(registerSchoolClass.getPreferredSize());
        registerSchoolClass.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        switchSchoolClass = new JButton(TextMapper.getText(TextMapper.GUIC_BTN_SWITCH_CLASS));
//        switchSchoolClass.setSize(switchSchoolClass.getPreferredSize());
//        switchSchoolClass.setAlignmentX(Component.RIGHT_ALIGNMENT);
        buildJTable();

        registerSchoolClass.addActionListener(this);
        registerSchoolClass.setVisible(true);
//        switchSchoolClass.addActionListener(this);
//        switchSchoolClass.setVisible(true);
//        addRoleButton.setVisible(GuiCreator.instance().getUser().hasRight(User.CHANGE_CLASS_RIGHT_TEACHER));
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.X_AXIS));
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));
        footer.setBackground(GuiConstants.MAIN_BACKGROUND);
        footer.add(registerSchoolClass);
//        footer.add(switchSchoolClass);
        this.add(footer);
    }

    /**
     * *****************************************************************************
     * Encapsulation of old code starts here
     * /******************************************************************************
     */
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
//                    String s = TextMapper.getText(TextMapper.GUIC_TBL_CLASSNAME);
//                    setToolTipText(MessageFormat.format(s, arguments));
//                    break;
//                case 2:
//                    setToolTipText(TextMapper.getText(TextMapper.GUIC_TLTP_EDIT_CLASS));
//                    break;
//                case REMOVE_COL:
//                    String format = TextMapper.getText(TextMapper.GUIC_TLTP_DELETE_CLASS);
//                    setToolTipText(MessageFormat.format(format, arguments));
//                    break;
//                case ASSIGN_COL:
//                    format = TextMapper.getText(TextMapper.GUIC_TLTP_ASSIGN_CLASS);
//                    setToolTipText(MessageFormat.format(format, arguments));
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

        /**
         * Switches/relogins to the active role set in the persistent store.
         *
         */
        private void switchToActiveSchoolClass(DomSchoolClass sc) {
            DomUserFull user = DwoHelper.getCurrentUser();
            try {
                prop.setActiveSchoolClass(sc);
//                //switch role now
                LOG.log(Level.INFO, "switching schoolclass now");
                GuiCreator.instance().loginWithMd5(user.getUserName(), user.getPassword());
            }
            catch (LoginException ex) {
                LOG.log(Level.SEVERE, null, ex);
                GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN));
            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                GuiCreator.instance().ShowErrorDialog(null, ex);
            }

        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean arg2, int row, int col) {
            this.value = value;
            JButton button = new JButton(new ImageIcon((Image) value));
            button.addActionListener(this);
            this.row = row;
            // model = (ClassTeacherPanel.ClassModel) table.getModel();
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return value;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            // note that we want to update the tableModel first!
            fireEditingStopped();
            //Let's check the selected col by the image and from the selected row value.
            try {
                if (value == loginImage) {
                    if (GuiCreator.instance().ShowConfirmDialog(GuiCreator.instance().getMainPanel(),
                            Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoHelper.getLocale(), Dwo2ExceptionCode.User_ConfirmSchoolClassSwitch)
                    ) != JOptionPane.OK_OPTION) {
                        return;
                    }
//            //get Table setting
//                int col = tableModel.getSelectedColumn();
                    int row = tableModel.getSelectedRow();

                    //set prop to table setting
                    DomSchoolClass schoolClass = (DomSchoolClass) tableModel.getValueAt(row, 3);
                    prop.setActiveSchoolClass(schoolClass);
                    DomUserFull user = DwoHelper.getCurrentUser();
                    //switch role now
                    GuiCreator.instance().loginWithMd5(user.getUserName(), user.getPassword());
                } else if (value == removeImage) {
                    if (GuiCreator.instance().ShowConfirmDialog(null, TextMapper.getText(TextMapper.DLG_Q_REMOVE)) == JOptionPane.OK_OPTION) {
                        int row = tableModel.getSelectedRow();
                        DomSchoolClass schoolClass = (DomSchoolClass) tableModel.getValueAt(row, 3);
                        prop.removeSchoolClass(schoolClass);
                        tableModel.init(prop, loginImage, removeImage,emptyImage);
                    }
//                    GuiCreator.instance().loginWithMd5(user.getUserName(), user.getPassword());
                } else {
                    // show warning
                    GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN));
                    return;
                }
            }
            catch (Dwo2Exception e) {
                LOG.log(Level.SEVERE, null, e);
                GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), e);
            }
            catch (LoginException ex) {
                LOG.log(Level.SEVERE, null, ex);
                GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN));
            }
        }
    }

    private void buildJTable() {
        if (jtbl != null) {
            remove(jtbl);
            jtbl = null;
        }

        JTable jtable = new JTable();
        jtable.getTableHeader().setReorderingAllowed(false);
        jtbl = new JPanel();
        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.Y_AXIS));
        jtbl.add(jtable.getTableHeader());
        //addClassButton.setVisible(true);
        jtbl.add(jtable);
        //jtbl.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);
        tableModel = new SchoolClassManagementStudentTableModel();

        tableModel.init(prop, loginImage, removeImage, emptyImage);
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
        TableUtil.setDefaults(jtable, true, new ImageRenderer(), new ImageButtonEditor());
        TableUtil.setJTableSizes(jtable);

// TODO shrink to fit heeft 520 als breedte
//        Dimension size = table.getPreferredSize();
//        if (size.width < 520) {
//            size.width = 520;
//        }
//        table.setMaximumSize(size);
        jtbl.setLocation(30, registerSchoolClass.getSize().height
                + registerSchoolClass.getLocation().y + 15);
        TableUtil.setBorder(jtable);
        //TableUtil.shrinkToFit(table, jtbl, 520, 405);
        jtbl.setVisible(false);
        this.add(jtbl);
        jtbl.setVisible(true);

    }

    /**
     * *****************************************************************************
     * Encapsulation of old code ends here
     * /******************************************************************************
     */
    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.registerSchoolClass) {
            SchoolClassRegistrationStudentJPanel panel = new SchoolClassRegistrationStudentJPanel();
            ShowJPanelAsDialog dialog = new ShowJPanelAsDialog(panel);
            panel.setParent(dialog);
            dialog.pack();
//            dialog.setLocationRelativeTo(null);
            dialog.setLocationRelativeTo(GuiCreator.instance().mainPanel);
            dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setAlwaysOnTop(true);
            dialog.setVisible(true);
            tableModel.init(prop, loginImage, removeImage,emptyImage);
            tableModel.fireTableDataChanged();
            CenterSubPanel cp = GuiCreator.instance().getClassPanel();
            GuiCreator.instance().getMainPanel().center.reset();
            GuiCreator.instance().getMainPanel().center.loadCenter(cp);

        }
//        else if (e.getSource() == removeImage) {
//            LOG.log(Level.INFO, "remove role");
////                if (JOptionPane.showConfirmDialog(TextMapper.getText(TextMapper.GUIC_MSG_DELETE_CLASS)))
//      //                  + "?", TextMapper.getText(TextMapper.GUIC_DELETE_CLASS), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//                    if (instance.deleteClass(sc)) {
//                        model.removeRow(row);
//                    }
//        }
    }
}
