/*
 * Created on Mar 24, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomSchoolClassFull;
import fi.dwo.rest.dom.entities.DomStudent;
import fi.dwo.rest.dom.entities.DomTeacher;
import fi.dwo.rest.dom.entities.DomUser;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.domutils.DomSchoolClassListCellRenderer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class UsersSchoolClassesSchoolAdminPanel extends JPanel implements CenterSubPanel, ActionListener {

    private static final Logger LOG = Logger.getLogger(UsersSchoolClassesSchoolAdminPanel.class.getName());

    private UsersSchoolClassesSchoolAdminPanelProperties prop = new UsersSchoolClassesSchoolAdminPanelProperties();
    private UsersSchoolClassesSchoolAdminPanelTableModel tableModel;
    private DomUser domUser;

    public enum UserType {
        STUDENT, TEACHER
    };
    
    private UserType userType;
    private CenterPanel center;

    private JButton backButton;
    private JButton addSchoolClassBtn;
    private JComboBox addSchoolClassBox;

    private Image removeImage;
    private Image classImage;
    private Image editImage;

    private JPanel jtbl;

    /**
     * @param user
     * @return the schoolClass
     */
    public DomUser getDomUser(DomUser user) {
        return domUser;
    }

    /**
     * @return the schoolClass
     */
    public UserType getUserType() {
        return userType;
    }

    /**
     * @param user the schoolClass to set
     * @param type
     */
    public void setDomUserAndType(DomUser user, UserType type) throws Dwo2Exception {
        if((user instanceof DomTeacher && type==UserType.TEACHER) || (user instanceof DomStudent && type == UserType.STUDENT)){
        this.domUser = user;
        this.userType=type;
        }
        else{
            throw new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, "Programmers error");
        }
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
//            final GuiCreator instance = GuiCreator.instance();
            fireEditingStopped();
            if (value == editImage) {
                try{
                    DomSchoolClass sc = (DomSchoolClass) tableModel.getValueAt(row, tableModel.getColumnCount());
                    ClassConfigurePanel panel = new ClassConfigurePanel();
                    DomSchoolClassFull fullSchoolClass = prop.getFullSchoolClass(sc);
                    panel.setSchoolClass(fullSchoolClass);
                    int result = JOptionPane.showConfirmDialog(GuiCreator.instance().getMainPanel(), panel, TextMapper.getText(TextMapper.GUIC_MSG_CLASS_CONFIGURATION),
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    fullSchoolClass.setSchoolClassName(panel.getClassName());
                    fullSchoolClass.setRegistrationKey(panel.getRegistrationKey());
                    fullSchoolClass.setIconizer(panel.isIconizer());
                    //case OK persist returned values
                    if (result == JOptionPane.OK_OPTION) {
                        //persist returned values	
                        prop.updateSchoolClass(fullSchoolClass);
                        tableModel.init(getCurSchoolClassList(), editImage, removeImage);
                    }
                }
                catch (Dwo2Exception ex) {
                    LOG.log(Level.FINE, null, ex);
                    JOptionPane.showMessageDialog(null, ex.getLocalizedCodeExplanation(DwoHelper.getLocale()), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
                }
                finally {
                    fireEditingStopped();
                }

            } else if (value == removeImage) {
                try {
                    if (GuiCreator.instance().ShowConfirmDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_CONFIRM)) == JOptionPane.OK_OPTION) {
                        DomSchoolClass domSchoolClass = (DomSchoolClass) tableModel.getValueAt(tableModel.getSelectedRow(),tableModel.getColumnCount());
                        prop.removeUserFromSchoolClass(domUser, userType, domSchoolClass);
                        tableModel.init(getCurSchoolClassList(), editImage, removeImage);
                    }
                }
                catch (Dwo2Exception ex) {
                    Logger.getLogger(UsersSchoolClassesSchoolAdminPanel.class.getName()).log(Level.FINE, null, ex);
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
        jtable.setMinimumSize(new Dimension(400, 300));
        jtable.getTableHeader().setReorderingAllowed(false);
        
        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.Y_AXIS));
        jtbl.add(jtable.getTableHeader());
        jtbl.add(jtable);
        jtbl.add(Box.createHorizontalGlue());
        //jtbl.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);
        tableModel = new UsersSchoolClassesSchoolAdminPanelTableModel();
        tableModel.init(getCurSchoolClassList(), editImage, removeImage);
        jtable.setModel(tableModel);
        if (jtable.getRowCount() > 0) {
            jtable.setRowSelectionInterval(0, 0);
        }
        jtable.setRowSelectionAllowed(false);
        jtable.setColumnSelectionAllowed(false);
        jtable.setCellSelectionEnabled(false);
        TableUtil.setDefaults(jtable, true, new UsersSchoolClassesSchoolAdminPanel.ImageRenderer(), new UsersSchoolClassesSchoolAdminPanel.ImageButtonEditor());
        TableUtil.setJTableSizes(jtable);
        TableUtil.setBorder(jtable);
        jtbl.setVisible(false);
        this.add(jtbl);
        jtbl.setVisible(true);

    }

    /**
     * Creates a new ClassPanel which shows a list of classes.
     *
     * @param user
     * @param type
     * @throws fi.dwo.rest.exceptions.Dwo2Exception
     */
    public UsersSchoolClassesSchoolAdminPanel(DomUser user, UserType type) throws Dwo2Exception {
        super(null);
        this.setDomUserAndType(user, type);
        this.setSize(480, 500);

        //fetch user details.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.setAlignmentY(TOP_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        /* Add Remove-class image */
        MediaTracker tr = new MediaTracker(this);
        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_STUDENT_IMAGE);
        editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_CLASS_IMAGE);
        classImage = DwoHelper.getResourceImage(GuiConstants.STUDENT_IMAGE);
        tr.addImage(removeImage, 0);
        tr.addImage(editImage, 1);
        tr.addImage(classImage, 2);
        try {
            tr.waitForAll();
        }
        catch (Exception e) {
        }

        //FontMetrics fm;
        backButton = new JButton(TextMapper.getText(TextMapper.BTN_BACK));
        backButton.setSize(backButton.getPreferredSize());
        backButton.addActionListener(this);
        addSchoolClassBtn = new JButton(TextMapper.getText(TextMapper.BTN_ADD));
        addSchoolClassBtn.setSize(addSchoolClassBtn.getPreferredSize());
        addSchoolClassBtn.addActionListener(this);
        Vector<DomSchoolClass> userVector = new Vector<DomSchoolClass>(prop.getOtherSchoolClasses(domUser, userType));
        addSchoolClassBox = new JComboBox(userVector);
        DomSchoolClassListCellRenderer renderer = new DomSchoolClassListCellRenderer();
        if (userVector.size() > 0) {
            addSchoolClassBox.setSelectedIndex(0);
        }

        addSchoolClassBox.setRenderer(renderer);
        addSchoolClassBox.setMaximumRowCount(10);
        addSchoolClassBox.addActionListener(this);

        Box header = Box.createHorizontalBox();
        header.setAlignmentX(Component.RIGHT_ALIGNMENT);
        header.setMaximumSize(new Dimension(3000, 100));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.add(backButton);
        header.add(Box.createHorizontalGlue());
//        header.add(Box.createRigidArea(new Dimension(10, 0)));
        header.add(addSchoolClassBox);
        header.add(Box.createRigidArea(new Dimension(10, 0)));
        header.add(addSchoolClassBtn);
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
        return new HeaderPanel(TextMapper.getText(TextMapper.TBL_CLASSLIST) + ": " + domUser.getUniqueDisplayName());
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addSchoolClassBtn) {
            DomSchoolClass schoolClass = (DomSchoolClass) addSchoolClassBox.getSelectedItem();
            try {
                prop.submitUserToSchoolClass(domUser, userType, schoolClass);                
                tableModel.init(getCurSchoolClassList(), editImage, removeImage);

            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                GuiCreator.instance().ShowErrorDialog(this, ex);
            }
        } else if (e.getSource() == backButton) {
            try {
                UsersInSchoolSchoolAdminPanel panel = new UsersInSchoolSchoolAdminPanel();
                center.loadCenter(panel);

            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.FINE, null, ex);
                GuiCreator.instance().ShowErrorDialog(this, ex);
            }
        } else if (e.getSource() == addSchoolClassBox) {
            //addTeacherBox.get
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
    public void stateChanged(ChangeEvent e) {
    }

    private List<DomSchoolClass> getCurSchoolClassList() throws Dwo2Exception {
        try {
            if (domUser instanceof DomStudent) {
                DomStudent student = (DomStudent) domUser;
                return prop.getStudentsSchoolClasses(student);
            } else if (domUser instanceof DomTeacher) {
                DomTeacher teacher = (DomTeacher) domUser;
                return prop.getTeachersSchoolClasses(teacher);
            }
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw ex;
        }
        return null;
    }

}
