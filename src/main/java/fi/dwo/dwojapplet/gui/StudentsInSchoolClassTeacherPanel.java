/*
 * Created on Mar 24, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.system.TextMapper;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
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
    private JButton moveToSchoolClassButton;
    private JButton copyToSchoolClassButton;
//    private JButton addTeacherButton;

    private Image select;

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
//            final GuiCreator instance = GuiCreator.instance();
            if (value == select) {
//                try {
//                    DomTeacher teacher = (DomTeacher) tableModel.getValueAt(row, tableModel.getColumnCount());
//
//                    if (GuiCreator.instance().ShowConfirmDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_CONFIRM)) == JOptionPane.OK_OPTION) {
//                        //persist returned values	
//                        prop.(getSchoolClass(), teacher);
//                        tableModel.init(prop, getSchoolClass(), select);
//                        tableModel.fireTableDataChanged();
//                    }
//                }
//                catch (Dwo2Exception ex) {
//                    Logger.getLogger(StudentsInSchoolClassTeacherPanel.class.getName()).log(Level.FINE, null, ex);
//                    GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), ex);
//                }
//                finally {
//                    fireEditingStopped();
//                }
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

        tableModel.init(prop, schoolClass, select);
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
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        /* Add Remove-class image */

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
//        addTeacherButton = new JButton(TextMapper.getText(TextMapper.BTN_ADD));
//        addTeacherButton.setSize(addTeacherButton.getPreferredSize());
//        addTeacherButton.addActionListener(this);
//        addTeacherBox = new JComboBox(new Vector<DomTeacher>(prop.getTeachersInSchoolNotInClass(sc)));
//        DomUserListCellRenderer renderer = new DomUserListCellRenderer();
//        addTeacherBox.setRenderer(renderer);
//        addTeacherBox.setMaximumRowCount(10);
//        addTeacherBox.addActionListener(this);
        Box header = Box.createHorizontalBox();
        header.setMaximumSize(new Dimension(520, 100));
        header.add(backButton);
        header.add(Box.createRigidArea(new Dimension(10, 0)));
        header.add(deleteButton);
        header.add(Box.createRigidArea(new Dimension(30, 0)));
        header.add(copyToSchoolClassButton);
        header.add(Box.createRigidArea(new Dimension(10, 0)));
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
//            DomTeacher teacher = (DomTeacher) addTeacherBox.getSelectedItem();
//            try {
//                prop.submitTeacherToSchoolClass(schoolClass, teacher);
//                tableModel.init(prop, schoolClass, select);
//                tableModel.fireTableDataChanged();
//                
//            }
//            catch (Dwo2Exception ex) {
//                LOG.log(Level.SEVERE, null, ex);
//                GuiCreator.instance().ShowErrorDialog(this, ex);
//            }
        } else if (e.getSource() == deleteButton) {
            //addTeacherBox.get
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
