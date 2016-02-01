/*
 * Created on Mar 24, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.dom.entities.DomSchoolClass4Teacher;
import fi.dwo.commons.dom.entities.DomTeacher;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.awt.Component;
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
public class TeachersInSchoolClassTeacherPanel extends JPanel implements CenterSubPanel, ActionListener {

    private static final Logger LOG = Logger.getLogger(TeachersInSchoolClassTeacherPanel.class.getName());

    private TeachersInSchoolClassTeacherPanelProperties prop = new TeachersInSchoolClassTeacherPanelProperties();
    private TeachersInSchoolClassTeacherPanelTableModel tableModel;
    private DomSchoolClass schoolClass;
    private CenterPanel center;

    private JButton addTeacherButton;

    private Image removeImage;

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
        if (value == removeImage) {
                try {
                    DomTeacher teacher = (DomTeacher) tableModel.getValueAt(row, tableModel.getColumnCount());

                    if (GuiCreator.instance().ShowConfirmDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_CONFIRM)) == JOptionPane.OK_OPTION) {
                        //persist returned values	
                        prop.removeTeacherFromSchoolClass(getSchoolClass(),teacher);
                        tableModel.init(prop, getSchoolClass(), removeImage);
                        tableModel.fireTableDataChanged();
                    }
                }
                catch (Dwo2Exception ex) {
                    Logger.getLogger(TeachersInSchoolClassTeacherPanel.class.getName()).log(Level.FINE, null, ex);
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
        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.Y_AXIS));
        jtbl.add(jtable.getTableHeader());
        jtbl.add(jtable);
        jtbl.add(Box.createHorizontalGlue());
        //jtbl.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);
        tableModel = new TeachersInSchoolClassTeacherPanelTableModel();

        tableModel.init(prop,schoolClass, removeImage);
        jtable.setModel(tableModel);
        if (jtable.getRowCount() > 0) {
            jtable.setRowSelectionInterval(0, 0);
        }
        jtable.setRowSelectionAllowed(false);
        jtable.setColumnSelectionAllowed(false);
        jtable.setCellSelectionEnabled(false);
        TableUtil.setDefaults(jtable, true, new TeachersInSchoolClassTeacherPanel.ImageRenderer(), new TeachersInSchoolClassTeacherPanel.ImageButtonEditor());
        TableUtil.setJTableSizes(jtable);

//        TableUtil.setDefaults(jtable, false, new ImageRenderer(), new ImageButtonEditor());
//        TableUtil.setJTableSizes(jtable);
// TODO shrink to fit heeft 520 als breedte
//        Dimension size = jtable.getPreferredSize();
//        if (size.width < 520) {
//            size.width = 520;
//        }
//        jtable.setMaximumSize(size);
        jtbl.setLocation(30, addTeacherButton.getSize().height
                + addTeacherButton.getLocation().y + 15);
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
    public TeachersInSchoolClassTeacherPanel() throws Dwo2Exception {
        super(null);
        this.setSize(480, 500);

        //fetch user details.

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        /* Add Remove-class image */
        MediaTracker tr = new MediaTracker(this);
        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_STUDENT_IMAGE);
        tr.addImage(removeImage, 0);
        try {
            tr.waitForAll();
        }
        catch (Exception e) {
        }

        //FontMetrics fm;
        addTeacherButton = new JButton(TextMapper.getText(TextMapper.GUIC_ADD_CLASS));
        //fm = addClassButton.getFontMetrics(addClassButton.getFont());
        addTeacherButton.setSize(addTeacherButton.getPreferredSize());
        addTeacherButton.addActionListener(this);
        //addClassButton.setLocation(30, 10);
//        addClassButton.setVisible(GuiCreator.instance().getUser().hasRight(User.CHANGE_CLASS_RIGHT_TEACHER));
        Box header = Box.createHorizontalBox();
        header.add(addTeacherButton);
        header.add(Box.createHorizontalGlue());
        this.add(header);
        //addClassButton.setVisible(true);
        this.add(Box.createVerticalStrut(15));
        buildJTable();
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
        if (e.getSource() == addTeacherButton) {
  //          ClassConfigurePanel panel = new TeachersInSchoolClassAddTeacherTeacherPanel();
  //          int row = tableModel.getSelectedRow();
  //          schoolClass = (DomSchoolClass) tableModel.getValueAt(row, 3);
//            panel.setSchoolClass(sc); 
            

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
