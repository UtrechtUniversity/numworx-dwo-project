/*
 * Created on Mar 24, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomNewSingleSchoolStudent;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSingleSchoolStudent;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.domutils.DomSchoolClassListCellRenderer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
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
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * The panel which shows the school classes for a teacher.
 */
public class NewSingleSchoolStudentsTeacherPanel extends JPanel implements CenterSubPanel, ActionListener {

    private static final Logger LOG = Logger.getLogger(NewSingleSchoolStudentsTeacherPanel.class.getName());

    private NewSingleSchoolStudentsTeacherPanelProperties prop = new NewSingleSchoolStudentsTeacherPanelProperties();
    private NewSingleSchoolStudentsTeacherPanelTableModel tableModel;
    private CenterPanel center;

    private JButton backButton;
    private JButton addButton;
    private JButton importButton;
    private JComboBox schoolClassComboBox;
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
                tableModel.deleteSelectedRow(tableModel.getSelectedRow());

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
        tableModel = new NewSingleSchoolStudentsTeacherPanelTableModel();
        List<DomSingleSchoolStudent> students = new ArrayList<DomSingleSchoolStudent>(1);
        tableModel.init(prop, columnNames, students, delImage);

        jtable.setModel(tableModel);
        if (jtable.getRowCount() > 0) {
            jtable.setRowSelectionInterval(0, 0);
        }
        jtable.setRowSelectionAllowed(true);
        jtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jtable.setColumnSelectionAllowed(false);
        jtable.setCellSelectionEnabled(false);
        TableUtil.setDefaults(jtable, true, new NewSingleSchoolStudentsTeacherPanel.ImageRenderer(), new NewSingleSchoolStudentsTeacherPanel.ImageButtonEditor());
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
    public NewSingleSchoolStudentsTeacherPanel() throws Dwo2Exception {
        super(null);
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
        }
        catch (Exception e) {
        }
        try {
            systemClipboard = getToolkit().getSystemClipboard();
        }
        catch (Exception e) {
            systemClipboard = null;
        }

        //FontMetrics fm;
        backButton = new JButton(TextMapper.getText(TextMapper.BTN_BACK));
        backButton.setSize(backButton.getPreferredSize());
        backButton.addActionListener(this);
        addButton = new JButton(TextMapper.getText(TextMapper.BTN_ADD));
        addButton.setSize(addButton.getPreferredSize());
        addButton.addActionListener(this);
        Vector<DomSchoolClass> classList = new Vector<DomSchoolClass>(prop.getTeachersSchoolClasses());
        schoolClassComboBox = new JComboBox(classList);
        if (classList.size() > 0) {
            schoolClassComboBox.setSelectedIndex(0);
        }
        DomSchoolClassListCellRenderer renderer = new DomSchoolClassListCellRenderer();
        schoolClassComboBox.setRenderer(renderer);
        schoolClassComboBox.setMaximumRowCount(10);
        schoolClassComboBox.addActionListener(this);
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
        header.add(schoolClassComboBox);
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
        return new HeaderPanel(TextMapper.getText(TextMapper.GUIC_CLASS_MANAGEMENT)+"-"+TextMapper.getText(TextMapper.HDR_NEW_STUDENTS));
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            if (schoolClassComboBox.getSelectedItem() == null) {
                return;
            }
            try {
                List<DomSingleSchoolStudent> submitList = tableModel.getSubmitList();
                List<DomSingleSchoolStudent> resultList = new ArrayList<DomSingleSchoolStudent>();
                boolean failFlag = false;
                boolean fatalFlag = false;
                for (DomSingleSchoolStudent submit : submitList) {
                    if (submit.getUserName()!=null && !submit.getUserName().equals("")) {
                        try {
                            DomNewSingleSchoolStudent student = new DomNewSingleSchoolStudent();
                            student.setDomSingleSchoolStudent(submit);
                            student.setDomSchoolClass((DomSchoolClass) schoolClassComboBox.getSelectedItem());
                            prop.submitSingleSchoolStudent(student);
                        }
                        catch (Dwo2Exception ex) {
                            resultList.add(submit);
                            if (ex.getDwo2Code() == Dwo2ExceptionCode.Rest_Registration_UserName_exists) {
                                LOG.log(Level.FINE, null, ex);
                                failFlag = true;
                            } else {
                                fatalFlag = true;
                                LOG.log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
                tableModel.init(prop, columnNames, resultList, delImage);
                tableModel.fireTableDataChanged();
                if (failFlag == true) {
                    GuiCreator.instance().ShowMessageDialog(this, "De overgebleven studenten kunnen niet worden toegevoegd, vermoedelijk is de username niet uniek.");
                }
                if (fatalFlag == true) {
                    GuiCreator.instance().ShowMessageDialog(this, "Er was een systeem error, zie de log.");
                }
                if(fatalFlag == false && failFlag == false){
                    GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_DONE_MSG));
                }
            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                GuiCreator.instance().ShowErrorDialog(this, ex);
            }
        } else if (e.getSource() == importButton) {
            pasteFromSystemClipboard();
        } else if (e.getSource() == backButton) {
            try {
                ClassTeacherPanel panel = new ClassTeacherPanel();
                center.loadCenter(panel);

            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.FINE, null, ex);
                GuiCreator.instance().ShowErrorDialog(this, ex);
            }
        } else if (e.getSource() == schoolClassComboBox) {
                    tableModel.fireTableDataChanged();

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
                List<DomSingleSchoolStudent> newUserList = new ArrayList<DomSingleSchoolStudent>();
                for (int i = 0; i < rowStrings.length; i++) {
                    newUserList.add(new DomSingleSchoolStudent());
                    //userList.get(userList.size()).clearSettings();
                    celStrings[i] = rowStrings[i].split("\t", columnNames.length);
                    newUserList.get(newUserList.size() - 1).setGivenName(celStrings[i][0]);
                    newUserList.get(newUserList.size() - 1).setInsertion(celStrings[i][1]);
                    newUserList.get(newUserList.size() - 1).setFamilyName(celStrings[i][2]);
                    newUserList.get(newUserList.size() - 1).setUserName(celStrings[i][3]);
                    newUserList.get(newUserList.size() - 1).setPassword(celStrings[i][4]);
                    newUserList.get(newUserList.size() - 1).setEmail(celStrings[i][5]);
//                    System.out.println(celStrings[i]);
                }
                tableModel.addRows(newUserList);
                return true;
            }
            catch (Exception e) {
                LOG.log(Level.SEVERE, null, e);
                return false;
            }
        } else {
            return false;
        }
    }
}
