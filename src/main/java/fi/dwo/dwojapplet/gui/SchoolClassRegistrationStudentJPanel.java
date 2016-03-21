package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomUserFull;
import fi.dwo.commons.dom.entities.DomNewSchoolClass4Student;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSchoolClassFull;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.awt.Color;

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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * This panel allows one to manage and switch between SchoolLogins.
 *
 */
public class SchoolClassRegistrationStudentJPanel extends JPanel implements ActionListener {

//    protected User user;
    private SchoolClassManagementStudentProperties prop = new SchoolClassManagementStudentProperties();
    private SchoolClassRegistrationStudentTableModel tableModel;

    private static final Logger LOG = Logger.getLogger(SchoolClassRegistrationStudentJPanel.class.getName());

    private JDialog parentDialog;
    private JButton addButton = null;
    private JButton backButton = null;
    private JPanel jtbl;
    private JTable jt;

    /**
     * Creates a new ProfilePanel for the current user. The account of the
     * current user can be changed.
     *
     */
    public SchoolClassRegistrationStudentJPanel() {
        super(null);
        this.setSize(480, 500);

        //fetch user details.
        try {
            prop.init();
        }
        catch (Dwo2Exception e) {
            //           LOG.log(Level.SEVERE, "Can't retrieve initial user settings.", e);
            //           GuiCreator.instance().ShowMessageDialog(this, e.getLocalizedCodeExplanation(DwoHelper.getLocale()), "", JOptionPane.ERROR_MESSAGE);
        }

        //init gui (old code)
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        //this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.setAlignmentY(TOP_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        /* Add Remove-class image */
        MediaTracker tr = new MediaTracker(this);
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
        try {
            addButton = new JButton(TextMapper.getText(TextMapper.GUIC_REGISTER_FOR_CLASS));
            addButton.setSize(addButton.getPreferredSize());
            addButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
            backButton = new JButton(TextMapper.getText(TextMapper.BTN_CLOSE));
            backButton.setSize(addButton.getPreferredSize());
            backButton.setAlignmentX(Component.RIGHT_ALIGNMENT);

            buildJTable();
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            GuiCreator.instance().ShowErrorDialog(this, ex);
        }

        addButton.addActionListener(this);
        addButton.setVisible(true);
        backButton.addActionListener(this);
        backButton.setVisible(true);
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.X_AXIS));
        footer.add(addButton);
        footer.add(Box.createHorizontalStrut(15));
        footer.add(backButton);
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));
        //footer.setBackground(GuiConstants.MAIN_BACKGROUND);
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
                LOG.log(Level.INFO, "switching role now");
                GuiCreator.instance().loginWithMd5(user.getUserName(), user.getPassword());
            }
            catch (LoginException ex) {
                LOG.log(Level.SEVERE, null, ex);
                GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.GUIW_ERR_LOGIN));
            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), ex);
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
//            try {
            int row = tableModel.getSelectedRow();
            //set prop to table setting
            DomSchoolClass schoolClass = (DomSchoolClass) tableModel.getValueAt(row, 3);
            //popup dialog for password and or confirmation.
            //           GuiCreator.instance().(null, schoolClass.getSchoolClassName(), "Error", JOptionPane.ERROR_MESSAGE);
            //          }
//            catch (Dwo2Exception e) {
//                LOG.log(Level.SEVERE, null, e);
//                GuiCreator.instance().ShowMessageDialog(null, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            }
//            catch (LoginException ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                GuiCreator.instance().ShowMessageDialog(null, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            }
        }
    }

    private void buildJTable() throws Dwo2Exception {
        if (jtbl != null) {
            remove(jtbl);
            jtbl = null;
        }

        JTable jtable = new JTable();
        jt = jtable;
        jtbl = new JPanel();
        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.Y_AXIS));
        jtbl.add(jtable.getTableHeader());
        //addClassButton.setVisible(true);
        jtbl.add(jtable);
        //jtbl.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);
        tableModel = new SchoolClassRegistrationStudentTableModel();
        tableModel.init(prop);
        jtable.setModel(tableModel);
        JScrollPane js = new JScrollPane(jtable);
        js.setVisible(true);
        this.add(js);
        jtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jtable.setSelectionBackground(Color.BLUE);
        jtable.setRowSelectionAllowed(true);
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
//        jtbl.setLocation(30, cancelButton.getSize().height
//                + cancelButton.getLocation().y + 15);
        jtbl.setLocation(30, 15);
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
        if (e.getSource() == addButton) {
            Boolean doIt = true;
            try {
                //TODO Register SchoolClass
                int i = jt.getSelectedRow();
                DomSchoolClass sc = (DomSchoolClass) tableModel.getValueAt(i, tableModel.getColumnCount());
                DomNewSchoolClass4Student newSchoolClass = new DomNewSchoolClass4Student(sc);
                if (sc.getHasRegKey()) {
                    SchoolClassRegistrationAskKeyJPanel panel = new SchoolClassRegistrationAskKeyJPanel();
                    panel.setSchoolClass(newSchoolClass);
                    panel.setRegistrationKey("");
                    int result = JOptionPane.showConfirmDialog(this, panel, TextMapper.getText(""),
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
//                    ShowJPanelAsDialog dialog = new ShowJPanelAsDialog(panel);
//                    panel.setParent(dialog);
//                    dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
//                    dialog.setAlwaysOnTop(true);
//                    dialog.setVisible(true);
                    doIt = result == JOptionPane.OK_OPTION;
                    if (doIt) {
                        newSchoolClass.setRegistrationKey(panel.getRegistrationKey());
                    }
                }
                if (doIt) {
                    prop.registerStudentForSchoolClass(newSchoolClass);
                    prop.init();
                    tableModel.init(prop);
                    tableModel.fireTableDataChanged();
                }
            }
            catch (Dwo2Exception ex) {
                Logger.getLogger(SchoolClassRegistrationStudentJPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (e.getSource() == backButton) {
            parentDialog.dispose();
        }
    }

    public void setParent(JDialog parent) {
        parentDialog = parent;
    }
}
