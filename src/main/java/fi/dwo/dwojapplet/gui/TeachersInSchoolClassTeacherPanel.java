/*
 * Created on Mar 24, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.domutils.DomUserListCellRenderer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
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
public class TeachersInSchoolClassTeacherPanel extends JPanel implements CenterSubPanel, ActionListener {

    private static final Logger LOG = Logger.getLogger(TeachersInSchoolClassTeacherPanel.class.getName());

    private TeachersInSchoolClassTeacherPanelProperties prop = new TeachersInSchoolClassTeacherPanelProperties();
    private TeachersInSchoolClassTeacherPanelTableModel tableModel;
    private DomSchoolClass schoolClass;
    private CenterPanel center;

    private JButton backButton;
    private JButton addTeacherButton;
    private JComboBox addTeacherBox;

    private Image removeImage, emptyImage;

    private JPanel jtbl;
    private TableRowSorter rowSorter;

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
//            final GuiCreator instance = GuiCreator.instance();
            if (value == removeImage) {
                try {
                    DomTeacher teacher = (DomTeacher) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
                    String msg = MessageFormat.format(TextMapper.getText(TextMapper.DLG_Q_REMOVE_TEACHER_BY_NAME), teacher.getUniqueDisplayName());                    
                    if (GuiCreator.instance().ShowConfirmDialog(GuiCreator.instance().getMainPanel(), msg) == JOptionPane.OK_OPTION) {
                        //persist returned values	
                        prop.removeTeacherFromSchoolClass(getSchoolClass(), teacher);
                        addTeacherBox.setSelectedItem(-1);
                        addTeacherBox.removeAllItems();
                        tableModel.init(prop.getTeachersInSchoolClass(schoolClass), removeImage, emptyImage);
                        GuiCreator.instance().getMainPanel().center.loadMenu(); // XXX fix list of classes
                    }
                } catch (Dwo2Exception ex) {
                    Logger.getLogger(TeachersInSchoolClassTeacherPanel.class.getName()).log(Level.FINE, "", ex);
                    GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), ex);
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

        JTable jtable = new JTable();
        jtable.setMinimumSize(new Dimension(400, 300));
        jtable.getTableHeader().setReorderingAllowed(false);
        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.Y_AXIS));
        jtbl.add(jtable.getTableHeader());
        jtbl.add(jtable);
        jtbl.add(Box.createHorizontalGlue());
        //jtbl.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);
        tableModel = new TeachersInSchoolClassTeacherPanelTableModel();

        tableModel.init(prop.getTeachersInSchoolClass(schoolClass), removeImage, emptyImage);
        jtable.setModel(tableModel);

        if (jtable.getRowCount() > 0) {
            jtable.setRowSelectionInterval(0, 0);
        }
        jtable.setRowSelectionAllowed(false);
        jtable.setColumnSelectionAllowed(false);
        jtable.setCellSelectionEnabled(false);
        TableUtil.setDefaults(jtable, true, new TeachersInSchoolClassTeacherPanel.ImageRenderer(), new TeachersInSchoolClassTeacherPanel.ImageButtonEditor());
        TableUtil.setJTableSizes(jtable);
        TableUtil.setBorder(jtable);
        jtbl.setVisible(false);
        rowSorter = new TableRowSorter(tableModel);
        rowSorter.toggleSortOrder(3);//
        jtable.setRowSorter(rowSorter);
        this.add(jtbl);
        jtbl.setVisible(true);

    }

    /**
     * Creates a new ClassPanel which shows a list of classes.
     *
     */
    public TeachersInSchoolClassTeacherPanel(final DomSchoolClass sc) throws Dwo2Exception {
        super(null);
        this.schoolClass = sc;
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
        emptyImage = DwoHelper.getResourceImage(GuiConstants.EMPTY_IMAGE);
        tr.addImage(removeImage, 0);
        tr.addImage(emptyImage, 1);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }

        //FontMetrics fm;
        backButton = new JButton(TextMapper.getText(TextMapper.BTN_BACK));
        backButton.setSize(backButton.getPreferredSize());
        backButton.addActionListener(this);
        addTeacherButton = new JButton(TextMapper.getText(TextMapper.BTN_ADD));
        addTeacherButton.setSize(addTeacherButton.getPreferredSize());
        addTeacherButton.addActionListener(this);
//        Vector<DomTeacher> teacherVector = new Vector<DomTeacher>(prop.getTeachersInSchoolNotInClass(sc));
//        Collections.sort(teacherVector, new Comparator<DomTeacher>() {
//            public int compare(DomTeacher a, DomTeacher b) {
//                return a.getFamilyName().compareTo(b.getFamilyName());
//            }
//        });
        addTeacherBox = new JComboBox();
        addTeacherBox.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
//                JComboBox comboBox = (JComboBox) e.getSource();
                Vector<DomTeacher> schoolClassVector;
                try {
                    schoolClassVector = new Vector<DomTeacher>(prop.getTeachersInSchoolNotInClass(sc));
                    Collections.sort(schoolClassVector, new Comparator<DomTeacher>() {
                        public int compare(DomTeacher a, DomTeacher b) {
                            return a.getFamilyName().compareTo(b.getFamilyName());
                        }
                    });
                    DefaultComboBoxModel model = new DefaultComboBoxModel(schoolClassVector);
                    addTeacherBox.setModel(model);
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

        DomUserListCellRenderer renderer = new DomUserListCellRenderer(TextMapper.getText(TextMapper.LBL_CLICK_TO_SELECT_A_TEACHER));
//        if (teacherVector.size() > 0) {
//            addTeacherBox.setSelectedIndex(0);
//        }
//        if (teacherVector.isEmpty()) {
//            addTeacherButton.setEnabled(false);
//            addTeacherBox.setEnabled(false);
//        } else {
//            addTeacherButton.setEnabled(true);
//            addTeacherBox.setEnabled(true);
//        }
//
        addTeacherBox.setRenderer(renderer);
        addTeacherBox.setMaximumRowCount(10);
        addTeacherBox.addActionListener(this);

        Box header = Box.createHorizontalBox();
//        header.setAlignmentX(Component.RIGHT_ALIGNMENT);
        header.setMaximumSize(new Dimension(3000, 100));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.add(backButton);
        header.add(Box.createRigidArea(new Dimension(30, 0)));
//        header.add(Box.createRigidArea(new Dimension(10, 0)));
        header.add(addTeacherButton);
        header.add(Box.createRigidArea(new Dimension(10, 0)));
        header.add(addTeacherBox);
        header.add(Box.createHorizontalGlue());
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
        return new HeaderPanel(TextMapper.getText(TextMapper.GUIC_CLASS_MANAGEMENT) + " - " + TextMapper.getText(TextMapper.HDR_EDITTEACHERS) + " - " + TextMapper.getText(TextMapper.HDR_SCHOOLCLASS) + ": " + schoolClass.getSchoolClassName());
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addTeacherButton) {
            DomTeacher teacher = (DomTeacher) addTeacherBox.getSelectedItem();
            try {
                prop.submitTeacherToSchoolClass(schoolClass, teacher);
//                Vector<DomTeacher> teacherVector = new Vector<DomTeacher>(prop.getTeachersInSchoolNotInClass(schoolClass));
//                Collections.sort(teacherVector, new Comparator<DomTeacher>() {
//                    public int compare(DomTeacher a, DomTeacher b) {
//                        return a.getFamilyName().compareTo(b.getFamilyName());
//                    }
//                });
//                DefaultComboBoxModel model = new DefaultComboBoxModel(teacherVector);
//                addTeacherBox.setModel(model);
//                if (teacherVector.isEmpty()) {
//                    addTeacherButton.setEnabled(false);
//                    addTeacherBox.setEnabled(false);
//                } else {
//                    addTeacherButton.setEnabled(true);
//                    addTeacherBox.setEnabled(true);
//                }
                addTeacherBox.setSelectedItem(-1);
                addTeacherBox.removeAllItems();
                tableModel.init(prop.getTeachersInSchoolClass(schoolClass), removeImage, emptyImage);
                tableModel.fireTableDataChanged();

            } catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, "", ex);
                GuiCreator.instance().ShowErrorDialog(this, ex);
            }
        } else if (e.getSource() == backButton) {
            try {
                ClassTeacherPanel panel = new ClassTeacherPanel();
                center.loadCenter(panel);

            } catch (Dwo2Exception ex) {
                LOG.log(Level.FINE, "", ex);
                GuiCreator.instance().ShowErrorDialog(this, ex);
            }
        } else if (e.getSource() == addTeacherBox) {
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
}
