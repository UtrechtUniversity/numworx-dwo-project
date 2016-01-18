package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomFullUser;
import fi.dwo.commons.dom.entities.DomSchoolRoleAndClass;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.panels.JPanelSchoolsandRolesProperties;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * This class represents a panel for the current user to change his account.
 *
 * @author M.J.B. Kupers
 *
 */
public class AccountSchoolRolesJPanel extends JPanel implements ActionListener {

//    protected User user;
    private JPanelSchoolsandRolesProperties prop = new JPanelSchoolsandRolesProperties();
    private AccountSchoolsRolesTableModel tableModel;

    private static final Logger LOG = Logger.getLogger(AccountSchoolRolesJPanel.class.getName());

    private final JButton addRoleButton;

    private Image removeImage, loginImage;

    private JPanel jtbl;

    private static final int ASSIGN_COL = 3;
    private static final int REMOVE_COL = 4;

    /**
     * Creates a new ProfilePanel for the current user. The account of the
     * current user can be changed.
     *
     */
    public AccountSchoolRolesJPanel() {
        super(null);
        this.setSize(480, 500);

        //fetch user details.
        try {
            prop.init();
        }
        catch (Dwo2Exception e) {
            LOG.log(Level.SEVERE, "Can't retrieve initial user settings.", e);
            GuiCreator.instance().ShowMessageToUser(this, e.getLocalizedCodeExplanation(DwoHelper.getLocale()), "", JOptionPane.ERROR_MESSAGE);
        }

        //init gui (old code)
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.setAlignmentY(TOP_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 0, 0));
        /* Add Remove-class image */
        MediaTracker tr = new MediaTracker(this);
        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_CLASS_IMAGE);
        loginImage = DwoHelper.getResourceImage(GuiConstants.STUDENT_IMAGE); //"resources/student.png");
        tr.addImage(removeImage, 0);
        tr.addImage(loginImage, 1);
        try {
            tr.waitForAll();
        }
        catch (Exception e) {
        }

        //FontMetrics fm;
        /* registerinfo label */
        JLabel l = new JLabel(TextMapper.getText(TextMapper.GUIP_ROLE_OPTIONS) + ":");
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
        addRoleButton = new JButton(TextMapper.getText(TextMapper.GUIP_BTN_ADD_ROLE));
        addRoleButton.setSize(addRoleButton.getPreferredSize());
        addRoleButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        buildJTable();

        addRoleButton.addActionListener(this);
        addRoleButton.setVisible(true);
//        addRoleButton.setVisible(GuiCreator.instance().getUser().hasRight(User.CHANGE_CLASS_RIGHT_TEACHER));
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.X_AXIS));
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));
        footer.setBackground(GuiConstants.MAIN_BACKGROUND);
        footer.add(addRoleButton);
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
            switch (col) {
                case 1:
                    String s = TextMapper.getText(TextMapper.GUIC_TLTP_USERS_CLASS);
                    setToolTipText(MessageFormat.format(s, arguments));
                    break;
                case 2:
                    setToolTipText(TextMapper.getText(TextMapper.GUIC_TLTP_EDIT_CLASS));
                    break;
                case REMOVE_COL:
                    String format = TextMapper.getText(TextMapper.GUIC_TLTP_DELETE_CLASS);
                    setToolTipText(MessageFormat.format(format, arguments));
                    break;
                case ASSIGN_COL:
                    format = TextMapper.getText(TextMapper.GUIC_TLTP_ASSIGN_CLASS);
                    setToolTipText(MessageFormat.format(format, arguments));
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
        ClassTeacherPanel.ClassModel model;
        int row;

        /**
         * Switches/relogins to the active role set in the persistent store.
         *
         */
        private void switchToActiveSchoolLogin() {
            DomFullUser user = DwoHelper.getCurrentUser();
            try {
//                //switch role now
                LOG.log(Level.INFO, "switching role now");
                GuiCreator.instance().loginWithMd5(user.getUsername(), user.getPassword());
            }
            catch (LoginException ex) {
                LOG.log(Level.SEVERE, null, ex);
                GuiCreator.instance().ShowMessageToUser(null, ex.getLocalizedMessage(), "Error", JDialog.ERROR);
            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                GuiCreator.instance().ShowMessageToUser(null, ex.getLocalizedMessage(), "Error", JDialog.ERROR);
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
//            //get Table setting
//                int col = tableModel.getSelectedColumn();
                    int row = tableModel.getSelectedRow();

//            //set prop to table setting
                    prop.setSelectedSchoolRoleAndClass((DomSchoolRoleAndClass) tableModel.getValueAt(row, 4));
                    prop.setActiveSchoolRoleAndClass();
                    switchToActiveSchoolLogin();

                } else if (value == removeImage) {
                    int row = tableModel.getSelectedRow();

                    //set prop to table setting
                    DomSchoolRoleAndClass currSrac = prop.getActiveSchoolRoleAndClass();
                    DomSchoolRoleAndClass selectedSrac = (DomSchoolRoleAndClass) tableModel.getValueAt(row, 4);
                    prop.RemoveSchoolRoleAndClass(selectedSrac);
                    tableModel.init(prop, loginImage, removeImage);
                    tableModel.fireTableDataChanged();
                    
                    if (currSrac != selectedSrac) {
                        //update tableview
                        model.fireTableDataChanged();
                    } else {
                        switchToActiveSchoolLogin();
                    }
                    switchToActiveSchoolLogin();
                }
            }
            catch (Dwo2Exception e) {
                LOG.log(Level.SEVERE, null, e);
                GuiCreator.instance().ShowMessageToUser(null, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buildJTable() {
        if (jtbl != null) {
            remove(jtbl);
            jtbl = null;
        }

        JTable jtable = new JTable();
        jtbl = new JPanel();
        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.Y_AXIS));
        jtbl.add(jtable.getTableHeader());
        //addClassButton.setVisible(true);
        jtbl.add(jtable);
        //jtbl.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);
        tableModel = new AccountSchoolsRolesTableModel();

        tableModel.init(prop, loginImage, removeImage);
        jtable.setModel(tableModel);
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
        jtbl.setLocation(30, addRoleButton.getSize().height
                + addRoleButton.getLocation().y + 15);
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
        if (e.getSource() == this.addRoleButton) {
            ShowJPanelAsDialog dialog = new ShowJPanelAsDialog(new RegisterMoreSchoolsPanel());
            dialog.setVisible(true);
            //reload centerpanel

            LOG.log(Level.INFO, "add role");
        }
        if (e.getSource() == loginImage) {
//            //get Table setting
            int row = tableModel.getSelectedColumn();
            int col = tableModel.getSelectedRow();

//            //set prop to table setting
            prop.setSelectedSchoolRoleAndClass((DomSchoolRoleAndClass) tableModel.getValueAt(4, col));
            try {
                prop.setActiveSchoolRoleAndClass();
                tableModel.init(prop, loginImage, removeImage);
                tableModel.fireTableDataChanged();
                //get user data
                DomFullUser user = DwoHelper.getCurrentUser();
                //switch role now
                LOG.log(Level.FINE, "switching role now");
                GuiCreator.instance().loginWithMd5(user.getUsername(), user.getPassword());
            }
            catch (LoginException ex) {
                LOG.log(Level.SEVERE, null, ex);
                GuiCreator.instance().ShowMessageToUser(this, ex.getLocalizedMessage(), "Error", JDialog.ERROR);
            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                GuiCreator.instance().ShowMessageToUser(this, ex.getLocalizedMessage(), "Error", JDialog.ERROR);
            }

        } else if (e.getSource() == removeImage) {
            LOG.log(Level.INFO, "remove role");

////                if (JOptionPane.showConfirmDialog(TextMapper.getText(TextMapper.GUIC_MSG_DELETE_CLASS)))
//      //                  + "?", TextMapper.getText(TextMapper.GUIC_DELETE_CLASS), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//                    if (instance.deleteClass(sc)) {
//                        model.removeRow(row);
//                    }
        }
    }
}
