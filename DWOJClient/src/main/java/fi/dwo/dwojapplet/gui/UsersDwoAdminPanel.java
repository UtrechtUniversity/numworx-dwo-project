/**
 * Copyrighted Mar 11, 2016
 */
package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JOptionPane;
import fi.beans.numworxlf.JTextField;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import nl.uu.fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.dom.entities.DomUserFull;

/**
 * Panel that displays a list of users of a type selected by a radio button. The
 * panels allows to remove users from the school and edit their profile and
 * password if they are a singleschoolstudent member of that school.
 *
 * Clicking on the class symbol in the list shows a list of the classes they are
 * a member of.
 *
 * @author Gert van der Plas
 */
public class UsersDwoAdminPanel extends JPanel implements CenterSubPanel, ActionListener {

    private static final Logger LOG = Logger.getLogger(UsersDwoAdminPanel.class.getName());

    private UsersDwoAdminPanelProperties prop = new UsersDwoAdminPanelProperties();
    private UsersDwoAdminPanelTableModel tableModel;
    private CenterPanel center;

    private Image editImage;
    private Image emptyImage;
    private Image studentImage;
    private Image teacherImage;
    private Image removeImage;
    private Image searchImage;
    private Image clearImage;

    private JTextField zoekField;
    private JButton zoekBtn, clrBtn;

    private JPanel jtbl;
    private TableRowSorter rowSorter;
    private RowFilter<SchoolDwoAdminPanelTableModel, Object> tableFilter;

    private JTable jtable;

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
        UsersDwoAdminPanelTableModel model;
        int row;

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean arg2, int row, int col) {
            this.value = value;
            JButton button = new JButton(new ImageIcon((Image) value));
            button.addActionListener(this);
            this.row = row;
            model = (UsersDwoAdminPanelTableModel) table.getModel();
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
                    DomUser user = (DomUser) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
                    DomUserFull fullUser = prop.get(user);
                    AccountDataFullStudentJPanel panel = new AccountDataFullStudentJPanel();
                    panel.setUser(fullUser);
                    panel.setVisible(true);
                    int result = JOptionPane.showConfirmDialog(GuiCreator.instance().mainPanel, panel, TextMapper.getText(TextMapper.GUIP_ACCOUNTANDCONTACTINFO),
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    //case OK persist returned values
                    //user = new DomSingleSchoolStudent(panel.getUser()); superfluous.
                    if (result == JOptionPane.OK_OPTION) {
                        //persist returned values
                        fullUser = panel.getUser();
                        prop.update(fullUser);
                        tableModel.init(prop, removeImage, studentImage, editImage, emptyImage);
                        tableModel.fireTableDataChanged();
                    }
                } catch (Dwo2Exception ex) {
                    LOG.log(Level.FINE, "", ex);
                    JOptionPane.showMessageDialog(null, ex.getLocalizedCodeExplanation(DwoHelper.getLocale()), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
                } finally {
                    fireEditingStopped();
                }
            } else if (value == removeImage) {
                try {
                    DomUser user = (DomUser) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
                    //prop.removeUser();
                    GuiCreator.instance().ShowMessageDialog(center, "Under Construction");
                    tableModel.init(prop, removeImage, studentImage, editImage, emptyImage);
                } catch (Dwo2Exception ex) {
                    LOG.log(Level.FINE, "", ex);
                    JOptionPane.showMessageDialog(null, ex.getLocalizedCodeExplanation(DwoHelper.getLocale()), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
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
//        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.PAGE_AXIS));

        jtable = new JTable();
        jtable.getTableHeader().setReorderingAllowed(false);
        jtable.setMinimumSize(new Dimension(400, 300));
        jtable.setFillsViewportHeight(true);
        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.Y_AXIS));
        jtbl.add(jtable.getTableHeader());
        jtbl.add(Box.createHorizontalGlue());
        jtbl.setBackground(GuiConstants.MAIN_BACKGROUND);
        tableModel = new UsersDwoAdminPanelTableModel();
        List userList = null;
        Image image = emptyImage;
        tableModel.init(prop, removeImage, image, editImage, emptyImage);
        jtable.setModel(tableModel);
        rowSorter = new TableRowSorter(tableModel);
        tableFilter = RowFilter.regexFilter(".*", 0);
        rowSorter.setRowFilter(tableFilter);
        rowSorter.toggleSortOrder(3);//
        jtable.setRowSorter(rowSorter);
        TableUtil.setDefaults(jtable, true, new UsersDwoAdminPanel.ImageRenderer(), new UsersDwoAdminPanel.ImageButtonEditor());
        TableUtil.setJTableSizes(jtable);
                TableColumnModel m = jtable.getColumnModel();
        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.Y_AXIS));
        for (int i = 0; i < jtable.getColumnCount()-1; i++) {
            m.getColumn(i).setMinWidth(70);
            m.getColumn(i).setMaxWidth(100);
        }
        m.getColumn(jtable.getColumnCount()-1).setMinWidth(150);
        jtable.setSize(jtable.getPreferredSize());
        jtbl.add(jtable.getTableHeader());
        jtbl.add(jtable);
        jtbl.add(Box.createVerticalGlue());
        //TableUtil.setBorder(table);
//        int h = addSchoolButton.getSize().height + addSchoolButton.getLocation().y + 5;
        //jtbl.setBounds(5, h, 627 - 5 , 492 - h - 5);
        jtbl.validate();
//        if (jtable.getRowCount() > 0) {
//            jtable.setRowSelectionInterval(0, 0);
//        }
//        jtable.setRowSelectionAllowed(false);
//        jtable.setColumnSelectionAllowed(false);
//        jtable.setCellSelectionEnabled(false);
//        jtable
//                .setDefaultRenderer(Image.class, new UsersDwoAdminPanel.ImageRenderer());
//        jtable
//                .setDefaultEditor(Image.class, new UsersDwoAdminPanel.ImageButtonEditor());
//        jtable.setBackground(GuiConstants.CELL_BACKGROUND);
//        jtable.setGridColor(Color.white);
//        jtable.setRowMargin(8);
//        jtable.getColumnModel().setColumnMargin(2);
//        jtable.setBorder(null);
//
//        TableUtil.setJTableSizes(jtable);
//        TableUtil.setBorder(jtable);
//        jtbl.add(jtable);
//        jtbl.setVisible(false);
////        JScrollPane scrollPane = new JScrollPane();
////        jtable.setSize(100, 100);
//////        scrollPane.getViewport().add(jtable);
////        scrollPane.setVisible(true);
////        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
////        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
////        this.add(scrollPane,BorderLayout.NORTH);
////        jtbl.add(Box.createVerticalGlue());
//
//        this.add(jtbl);
//        jtbl.setVisible(true);

    }

    /**
     * Creates a new ClassPanel which shows a list of classes.
     *
     */
    public UsersDwoAdminPanel() throws Dwo2Exception {
        super(null);
        init();
    }

    private void init() throws Dwo2Exception {
        this.setSize(480, 500);
        //Create the radio buttons.

        //fetch user details.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.setAlignmentY(TOP_ALIGNMENT);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        /* Add Remove-class image */
        MediaTracker tr = new MediaTracker(this);
        editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_CLASS_IMAGE);
        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_CLASS_IMAGE);
        searchImage = DwoHelper.getResourceImage(GuiConstants.SEARCH_IMAGE);
        clearImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_CLASS_IMAGE);
        tr.addImage(editImage, 0);
        tr.addImage(emptyImage, 1);
        tr.addImage(studentImage, 2);
        tr.addImage(removeImage, 4);
        

        try {
            tr.waitForAll();
        } catch (Exception e) {
        }
        Box header = Box.createHorizontalBox();
//        header.add(Box.createHorizontalStrut(25));
        zoekField = new JTextField();
        zoekField.setToolTipText("Zoek user");
        zoekField.addActionListener(this);
        zoekField.setColumns(8);
        zoekField.setMinimumSize(zoekField.getPreferredSize());
        header.add(zoekField);
        zoekBtn = new JButton(new ImageIcon(searchImage));
        zoekBtn.setBorderPainted(false);
        zoekBtn.setContentAreaFilled(false);
        zoekBtn.addActionListener(this);
        zoekBtn.setSize(zoekBtn.getPreferredSize());
        header.add(zoekBtn);
        header.add(Box.createHorizontalStrut(5));
        clrBtn = new JButton(new ImageIcon(clearImage));
        clrBtn.setBorderPainted(false);
        clrBtn.setContentAreaFilled(false);
        clrBtn.addActionListener(this);
        clrBtn.setSize(clrBtn.getPreferredSize());
        header.add(clrBtn);
        header.add(Box.createHorizontalStrut(5));
        header.add(Box.createHorizontalGlue());
        header.add(Box.createRigidArea(new Dimension(10, 0)));
//        header.setPreferredSize(header.getMinimumSize());
        header.setMaximumSize(header.getMinimumSize());
        this.add(header);

        this.add(Box.createVerticalStrut(15));
        try {
            buildJTable();
        }
        catch (Dwo2Exception ex) {
            GuiCreator.instance().ShowErrorDialog(this, ex);
            LOG.log(Level.SEVERE, "", ex);
        }
        this.add(jtbl);

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
    public JComponent getHeaderPanel() {
        return new HeaderPanel(TextMapper.getText(TextMapper.GUIMNU_USERS_SCHOOL));
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
       Object source = e.getSource();
        if (zoekField == source || zoekBtn == source) {
            //zoek(zoekField.getText().trim().toLowerCase());
            tableFilter = RowFilter.regexFilter(zoekField.getText().trim());
            rowSorter.setRowFilter(tableFilter);
            //table.setRowSorter(rowSorter);

            return;
        } else if (clrBtn == source) {
            zoekField.setText("");
            tableFilter = RowFilter.regexFilter(".*", 0);
            rowSorter.setRowFilter(tableFilter);
            //table.setRowSorter(rowSorter);

            return;
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
