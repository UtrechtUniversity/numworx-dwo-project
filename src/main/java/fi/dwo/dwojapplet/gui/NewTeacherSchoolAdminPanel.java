/*
 * Created on Mar 24, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.system.MD5;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.rest.dom.entities.DomUserFull;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * The panel which shows the school classes for a teacher.
 */
public class NewTeacherSchoolAdminPanel extends JPanel implements CenterSubPanel, ActionListener {

    private static final Logger LOG = Logger.getLogger(NewTeacherSchoolAdminPanel.class.getName());

    private NewTeacherSchoolAdminPanelProperties prop = null;
    private NewTeacherSchoolAdminPanelTableModel tableModel;
    private CenterPanel center;

    private JButton backButton;
    private JButton addButton;
    private JButton importButton;
//    private JComboBox schoolClassComboBox;
    private Clipboard systemClipboard;
    String[] columnNames = {
        TextMapper.getText(TextMapper.GUIR_FIRSTNAME),
        TextMapper.getText(TextMapper.GUIR_MIDDLENAME),
        TextMapper.getText(TextMapper.GUIR_LASTNAME),
        TextMapper.getText(TextMapper.GUIR_USERNAME),
        TextMapper.getText(TextMapper.GUIR_PASSWORD),
        TextMapper.getText(TextMapper.GUIR_EMAIL),
        TextMapper.getText(TextMapper.BTN_DELSELECTED)
    };

    private Image delImage;

    private JPanel jtbl;

//    public enum UserType {
//        ADMIN, SCHOOLADMIN
//    };
//
//    private UserType userType = UserType.SCHOOLADMIN;
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
            if (value == delImage) {
                tableModel.deleteSelectedRow(row);

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
        jtable.setBackground(Color.LIGHT_GRAY);
        jtable.getTableHeader().setReorderingAllowed(false);

        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.Y_AXIS));
        jtbl.add(jtable.getTableHeader());
        jtbl.add(jtable);
        jtbl.add(Box.createHorizontalGlue());
        tableModel = new NewTeacherSchoolAdminPanelTableModel();
        List<DomUserFull> users = new ArrayList<DomUserFull>(1);
        tableModel.init(prop, columnNames, users, delImage);

        jtable.setModel(tableModel);
        if (jtable.getRowCount() > 0) {
            jtable.setRowSelectionInterval(0, 0);
        }
        jtable.setRowSelectionAllowed(true);
        jtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jtable.setColumnSelectionAllowed(false);
        jtable.setCellSelectionEnabled(false);

        InputMap im = jtable.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke tab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        KeyStroke cursorRight = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
        Action tabAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTable t = (JTable) e.getSource();
                int column = t.getSelectedColumn();
                int row = t.getSelectedRow();
                //t.getCellEditor().stopCellEditing();

                do {
                    if (row == -1) {
//                        if ((e.getModifiers() & InputEvent.SHIFT_DOWN_MASK) > 0) {
//                            //shift pressed
//                            row = t.getRowCount() - 1;
//                        } else {
                        row = 0;//handle no selection                            
//                        }
                    }
                    if (column == -1) {
//                        if ((e.getModifiers() & InputEvent.SHIFT_DOWN_MASK) > 0) {
//                            //shift pressed
//                            column = t.getColumnCount() - 2;
//                        } else {
                        column = 0;
//                        }//ditto
                    }

                    if ((e.getModifiers() & InputEvent.SHIFT_DOWN_MASK) > 0) {
                        //shift pressed
                        column--;
                    } else {
                        column++;
                    }

                    if (column == -1) {
                        column = t.getColumnCount() - 2;
                        row--;
                        if (row == -1) {
                            row = t.getRowCount() - 1;
                        }
                    } else if (column == t.getColumnCount() - 1) {
                        column = 0;
                        row++;
                        if (row == t.getRowCount()) {
                            row = 0;
                        }
                    }
                } while (t.isCellEditable(row, column) == false);
                if (row == t.getRowCount() - 1) {
                    NewTeacherSchoolAdminPanelTableModel model = (NewTeacherSchoolAdminPanelTableModel) t.getModel();
                    model.getData().add(new DomUserFull());
                    model.setSelectedColumn(column + 1);
                    model.setSelectedRow(model.getData().size() - 1);
                    model.fireTableDataChanged();
                }
//                else {
//                    model.setSelectedColumn(column);
//                    model.setSelectedRow(row);
//                    model.fireTableDataChanged();
//                }

                t.changeSelection(row, column, false, false);
                t.editCellAt(row, column);
            }
        };
        jtable.getActionMap()
                .put(im.get(tab), tabAction);
        jtable.getActionMap()
                .put(im.get(enter), tabAction);

        Action cursorAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTable t = (JTable) e.getSource();
                int column = t.getSelectedColumn();
                int row = t.getSelectedRow();
                //t.getCellEditor().stopCellEditing();

                do {
                    if (row == -1) {
//                        if ((e.getModifiers() & InputEvent.SHIFT_DOWN_MASK) > 0) {
//                            //shift pressed
//                            row = t.getRowCount() - 1;
//                        } else {
                        row = 0;//handle no selection                            
//                        }
                    }
                    if (column == -1) {
//                        if ((e.getModifiers() & InputEvent.SHIFT_DOWN_MASK) > 0) {
//                            //shift pressed
//                            column = t.getColumnCount() - 2;
//                        } else {
                        column = 0;
//                        }//ditto
                    }

                    column++;

                    if (column == t.getColumnCount() - 1) {
                        column = t.getColumnCount() - 2;
                    }
                } while (t.isCellEditable(row, column) == false);
                t.changeSelection(row, column, false, false);
                t.editCellAt(row, column);
            }
        };

        jtable.getActionMap()
                .put(im.get(cursorRight), cursorAction);

        jtable.putClientProperty(
                "terminateEditOnFocusLost", Boolean.TRUE);
        TableUtil.setDefaults(jtable,
                true, new NewTeacherSchoolAdminPanel.ImageRenderer(), new NewTeacherSchoolAdminPanel.ImageButtonEditor());
        TableUtil.setJTableSizes(jtable);
//        TableUtil.setBorder(jtable);
        //Override default settings for spreadsheet like border.

        jtable.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        jtable.setGridColor(Color.LIGHT_GRAY);

        jtable.setShowGrid(
                true);
        jtbl.setVisible(
                false);

        this.add(jtbl);

        jtbl.setVisible(
                true);
    }

//    public NewTeacherSchoolAdminPanel(NewTeacherSchoolAdminPanel.UserType type) throws Dwo2Exception {
//        super(null);
//        init(type);
//    }
    public NewTeacherSchoolAdminPanel() throws Dwo2Exception {
        super(null);
        init();
    }

    /**
     * Creates a new ClassPanel which shows a list of classes.
     *
     * @param sc
     * @param userType
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    private void init() throws Dwo2Exception {
//        this.userType = userType;
        this.setSize(480, 500);

        //fetch user details.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.setAlignmentY(TOP_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        /* Add Remove-class image */
        MediaTracker tr = new MediaTracker(this);
        delImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_STUDENT_IMAGE);
        tr.addImage(delImage, 0);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }
        try {
            systemClipboard = getToolkit().getSystemClipboard();
        } catch (Exception e) {
            systemClipboard = null;
        }

        //FontMetrics fm;
        backButton = new JButton(TextMapper.getText(TextMapper.BTN_BACK));
        backButton.setSize(backButton.getPreferredSize());
        backButton.addActionListener(this);
        addButton = new JButton(TextMapper.getText(TextMapper.BTN_CREATE_STUDENTACCOUNTS));
        addButton.setSize(addButton.getPreferredSize());
        addButton.addActionListener(this);
//        Vector<DomSchoolClass> classList = new Vector<DomSchoolClass>(prop.getTeachersSchoolClasses());
//        schoolClassComboBox = new JComboBox(classList);
//        if (classList.size() > 0) {
//            schoolClassComboBox.setSelectedIndex(0);
//        }
//        DomSchoolClassListCellRenderer renderer = new DomSchoolClassListCellRenderer();
//        schoolClassComboBox.setRenderer(renderer);
//        schoolClassComboBox.setMaximumRowCount(10);
//        schoolClassComboBox.addActionListener(this);
        importButton = new JButton(TextMapper.getText("Import from clipboard"));
        importButton.setSize(importButton.getPreferredSize());
        importButton.addActionListener(this);

        Box header = Box.createHorizontalBox();
        header.setAlignmentX(Component.RIGHT_ALIGNMENT);
        header.setMaximumSize(new Dimension(3000, 100));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.add(backButton);
        header.add(Box.createRigidArea(new Dimension(30, 0)));
        header.add(importButton);
        header.add(Box.createHorizontalGlue());
        header.add(addButton);
        header.add(Box.createRigidArea(new Dimension(10, 0)));
//        header.add(schoolClassComboBox);
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
        return new HeaderPanel(TextMapper.getText(TextMapper.GUIMNU_USERS_SCHOOL));
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
//            if (schoolClassComboBox.getSelectedItem() == null) {
//                return;
//            }
            try {
                List<DomUserFull> submitList = tableModel.getData();
                List<DomUserFull> resultList = new ArrayList<DomUserFull>();
                boolean failFlag = false;
                boolean fatalFlag = false;
                String tmpPassword = null;
                int cnt = 0;
                for (DomUserFull submit : submitList) {
                    if (NewTeacherSchoolAdminPanelProperties.IsValidUserDataInput(submit)) {
                        cnt++;
                        try {
                            tmpPassword = submit.getPassword();
                            submit.setPassword(MD5.getHashString(submit.getPassword()));
                            NewTeacherSchoolAdminPanelProperties.submitNewTeacher(submit);
                        } catch (Dwo2Exception ex) {
                            submit.setPassword(tmpPassword);
                            resultList.add(submit);
                            if (ex.getDwo2Code() == Dwo2ExceptionCode.Rest_Registration_UserName_exists) {
                                LOG.log(Level.FINE, "", ex);
                                failFlag = true;
                            } else {
                                fatalFlag = true;
                                LOG.log(Level.SEVERE, "", ex);
                            }
                        }
                    }
                }
                tableModel.init(prop, columnNames, resultList, delImage);
                tableModel.fireTableDataChanged();
                if (cnt == 0) {
                    GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_NO_USERS_SELECTED));
                }
                if (failFlag == true) {
                    GuiCreator.instance().ShowMessageDialog(this, TextMapper.getText(TextMapper.DLG_CREATETEACHERERROR));
                }
                if (fatalFlag == true) {
                    GuiCreator.instance().ShowMessageDialog(this, TextMapper.getText(TextMapper.EX_UNKNOWN_ERROR));
                }
                if (fatalFlag == false && failFlag == false) {
                    GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_DONE_MSG));
                }
            } catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, "", ex);
                GuiCreator.instance().ShowErrorDialog(this, ex);
            }
        } else if (e.getSource() == importButton) {
            pasteFromSystemClipboard();
        } else if (e.getSource() == backButton) {
            if (tableModel.getRowCount() > 1) {
                if (GuiCreator.instance().ShowConfirmDialog(center, TextMapper.getText(TextMapper.DLG_Q_LOSE_NEW_STUDENT_ACCOUNTS)) != JOptionPane.OK_OPTION) {
                    return;
                }
            }
            try {
                UsersInSchoolSchoolAdminPanel panel = new UsersInSchoolSchoolAdminPanel(UsersInSchoolSchoolAdminPanel.UserType.TEACHER);
                center.loadCenter(panel);

            } catch (Dwo2Exception ex) {
                LOG.log(Level.FINE, "", ex);
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

    public boolean pasteFromSystemClipboard() {
        if (systemClipboard == null) {
            return false;
        }
        Transferable clipboardContent = systemClipboard.getContents(this);

        if ((clipboardContent != null) && (clipboardContent.isDataFlavorSupported(DataFlavor.stringFlavor))) {
            try {
                String tempString;
                tempString = (String) clipboardContent.getTransferData(DataFlavor.stringFlavor);
                String[] rowStrings = tempString.split("\n"); // was: StringUtils.split(tempString, "\n");
                String[][] celStrings = new String[rowStrings.length][];
                List<DomUserFull> newUserList = new ArrayList<DomUserFull>();
                for (int i = 0; i < rowStrings.length; i++) {
                    newUserList.add(new DomUserFull());
                    //userList.get(userList.size()).clearSettings();
                    celStrings[i] = rowStrings[i].split("\t", columnNames.length);
                    newUserList.get(newUserList.size() - 1).setGivenName(celStrings[i][0]);
                    newUserList.get(newUserList.size() - 1).setInsertion(celStrings[i][1]);
                    newUserList.get(newUserList.size() - 1).setFamilyName(celStrings[i][2]);
                    newUserList.get(newUserList.size() - 1).setUserName(celStrings[i][3]);
                    newUserList.get(newUserList.size() - 1).setPassword(celStrings[i][4]);
                    newUserList.get(newUserList.size() - 1).setEmail(celStrings[i][5]);
                    // a teacher-account!
                    newUserList.get(newUserList.size() - 1).setSingleSchool(false);
//                    System.out.println(celStrings[i]);
                }
                tableModel.addRows(newUserList);
                return true;
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "", e);
                return false;
            }
        } else {
            return false;
        }
    }
}
