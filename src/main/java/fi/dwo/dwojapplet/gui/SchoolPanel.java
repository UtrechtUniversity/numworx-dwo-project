/*
 * Created on Mar 24, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.exceptions.SchoolException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.School;
import java.awt.Component;
import java.awt.Image;
import java.awt.Rectangle;
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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * The panel where a School can be managed.
 *
 * @author M.J.B. Kupers
 * @author Wim van Velthoven
 *
 */
public class SchoolPanel extends JPanel implements CenterSubPanel, ActionListener {
    private static final Logger LOG = Logger.getLogger(SchoolPanel.class.getName());

    public class ImageButtonEditor extends AbstractCellEditor implements
            TableCellEditor, ActionListener {

        Object value;
        SchoolModel model;
        int row;

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean arg2, int row, int col) {
            this.value = value;
            JButton button = new JButton(new ImageIcon((Image) value));
            button.addActionListener(this);
            this.row = row;
            model = (SchoolModel) table.getModel();
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return value;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            if (value == editImage) {
                try {
                    School s = AddSchoolDialog.editSchool(SchoolPanel.this.center, model.school[row]);
                    if (s != null) {
                        model.fireTableRowsUpdated(row, row);
                    }
                } catch (SchoolException e) {
                    LOG.log(Level.SEVERE,null,e);
                }
            } else if (value == removeImage) {
                /* Delete the school */
                School sc = model.school[row];
                if (JOptionPane.showConfirmDialog(SchoolPanel.this, TextMapper.getText(TextMapper.GUIS_MSG_DELETE_SCHOOL)
                        + "?", TextMapper.getText(TextMapper.GUIS_DELETE_SCHOOL), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    if (GuiCreator.instance().deleteSchool(sc)) {
                        center.loadMenu();
                        model.deleteRow(row);
                    }
                }
            } else if (value == usersImage) {
                School sc = model.school[row];
                JDialog rightsDialog = new RightsDialog(SchoolPanel.this, sc);
                rightsDialog.show();
            }
            fireEditingStopped();
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
            switch (col) {
                case 4 - 2:
                    String s = TextMapper.getText(TextMapper.GUIS_TLTP_USERS_SCHOOL);
                    setToolTipText(MessageFormat.format(s, arguments));
                    break;
                case 5 - 2:
                    setToolTipText(TextMapper.getText(TextMapper.GUIS_TLTP_EDIT_SCHOOL));
                    break;
                case 6 - 2:
                    String format = TextMapper.getText(TextMapper.GUIS_TLTP_DELETE_SCHOOL);
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

    class SchoolModel extends AbstractTableModel implements TableModel {

        private School[] school;

        public SchoolModel(School[] school) {
            this.school = school;
        }

        @Override
        public int getColumnCount() {
            return 7 - 2;
        }

        @Override
        public Class getColumnClass(int col) {
            if (col >= 4 - 2) {
                return Image.class;
            }
            return super.getColumnClass(col);
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            if (col >= 4 - 2) {
                return true;
            }
            return super.isCellEditable(row, col);
        }

        @Override
        public int getRowCount() {
            return school.length;
        }

        @Override
        public Object getValueAt(int row, int col) {
            School s = school[row];
            switch (col) {
                case 0:
                    return s.getName();
                case 1:
                    return s.getSchoolLogin();
//			case 2: try {
//						return s.getSchoolGroupList()[0].getPasswd();
//					} catch (RuntimeException e) {
//					} 
//					break;
//			case 3: try {
//						return s.getSchoolGroupList()[1].getPasswd();
//					} catch (RuntimeException e) {
//					}
//					break;
                case 4 - 2:
                    return usersImage;
                case 5 - 2:
                    return editImage;
                case 6 - 2:
                    return removeImage;
            }

            return "";
        }

        @Override
        public String getColumnName(int col) {
            switch (col) {
                case 0:
                    return "School";
                case 1:
                    return "Login";
//			case 2: return "Leerling";
//			case 3: return "Docent";
            }
            return "";
        }

        void deleteRow(int row) {
            School[] newSchool = new School[school.length - 1];
            System.arraycopy(school, 0, newSchool, 0, row);
            System.arraycopy(school, row + 1, newSchool, row, newSchool.length - row);
            school = newSchool;
            fireTableRowsDeleted(row, row);
        }
    }

    private CenterPanel center;

    private JButton addSchoolButton, copyButton;

    private Image removeImage, editImage, usersImage, assignImage;

    private JTextField zoekField;

    private JButton zoekBtn;

    /**
     * Creates a new SchoolPanel witch shows a list of schools.
     *
     */
    public SchoolPanel() {
        super(null);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 10));
        //this.setSize(627, 485);
        //this.setSize(600, 470);
        //setPreferredSize(getSize());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        /* Add Remove-school image */

        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_CLASS_IMAGE);
        editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_CLASS_IMAGE);
        usersImage = DwoHelper.getResourceImage(GuiConstants.USERS_CLASS_IMAGE);
        assignImage = DwoHelper.getResourceImage(GuiConstants.ASSIGN_CLASS_IMAGE);
        Image searchImage = DwoHelper.getResourceImage(GuiConstants.SEARCH_IMAGE);

        int w;
        addSchoolButton = new JButton(TextMapper.getText(TextMapper.GUIS_ADD_SCHOOL));
        //addSchoolButton.setSize(addSchoolButton.getPreferredSize());
        w = addSchoolButton.getWidth();
        addSchoolButton.addActionListener(this);
        //addSchoolButton.setLocation(30, 10);
        Box header = Box.createHorizontalBox();
        header.add(Box.createHorizontalStrut(25));
        header.add(addSchoolButton);
        header.add(Box.createHorizontalStrut(10));
        copyButton = new JButton(/*FIXME*/"Copy");
        //copyButton.setSize(copyButton.getPreferredSize());
        copyButton.addActionListener(this);
        //copyButton.setLocation(30 + w + 10, 10);
        header.add(copyButton);

        header.add(Box.createHorizontalStrut(30));
        zoekField = new JTextField();
        zoekField.setToolTipText("Zoek school");
        zoekField.addActionListener(this);
        zoekField.setColumns(8);
        zoekField.setMaximumSize(zoekField.getPreferredSize());
        header.add(zoekField);
        zoekBtn = new JButton(new ImageIcon(searchImage));
        zoekBtn.setBorderPainted(false);
        zoekBtn.setContentAreaFilled(false);
        zoekBtn.addActionListener(this);
        header.add(zoekBtn);

        header.add(Box.createHorizontalGlue());
        add(header);
        add(Box.createVerticalStrut(5));
        buildJTable();

    }

    /**
     * Indicate that another panel is loaded and the connections of this panel
     * must be closed.
     */
    @Override
    public void end() {

    }

    private Box jtbl;

    private void buildJTable() {
        if (jtbl != null) {
            remove(jtbl);
            jtbl = null;
        }
        table = new JTable();
        zoekPos = -1;
        table.setModel(new SchoolModel(GuiCreator.instance().getSchool()));
        TableUtil.setDefaults(table, true, new ImageRenderer(), new ImageButtonEditor());
// de volgende regel heeft geen effect.
// je moet dan zelf een TableCellRenderer installeren.
        //table.getTableHeader().setBackground(GuiConstants.CELL_BACKGROUND);
        TableUtil.setJTableSizes(table);
        //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel m = table.getColumnModel();
        for (int i = 4; i < table.getColumnCount(); i++) {
            m.getColumn(i).setMinWidth(m.getColumn(i).getPreferredWidth());
            m.getColumn(i).setMaxWidth(m.getColumn(i).getPreferredWidth());
        }
        table.setSize(table.getPreferredSize());
        //JPanel panel = new JPanel(new BorderLayout());
        //panel.add(table.getTableHeader(),BorderLayout.NORTH);
        //panel.add(table, BorderLayout.CENTER);
        //panel.setPreferredSize(table.getPreferredSize());
        jtbl = Box.createVerticalBox();
        jtbl.add(table.getTableHeader());
        jtbl.add(table);
        //TableUtil.setBorder(table);
        int h = addSchoolButton.getSize().height + addSchoolButton.getLocation().y + 5;
        //jtbl.setBounds(5, h, 627 - 5 , 492 - h - 5);
        jtbl.validate();
        this.add(jtbl);
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
     * Returns a Panel that can functionate as a header panel.
     *
     * @return A panel that can functionate as a header panel.
     * @see fi.dwo.client.gui.CenterSubPanel#getHeaderPanel()
     */
    @Override
    public Component getHeaderPanel() {
        return new HeaderPanel(TextMapper.getText(TextMapper.GUIS_SCHOOL_MANAGEMENT));
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
            zoek(zoekField.getText().trim().toLowerCase());
            return;
        }
        if (source == copyButton) {
            ClipboardExport.instance().export(GuiCreator.instance().getSchool());
            return;
        }
        if (source == addSchoolButton) {
            try {
                School s = AddSchoolDialog.addSchool(center);
                if (s != null) {
                    buildJTable();
                }
            } catch (SchoolException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private int zoekPos = -1;

    private JTable table;

    private boolean zoek(String text, int i) {
        String data = table.getValueAt(i, 0) + " " + table.getValueAt(i, 1);
        return data.toLowerCase().indexOf(text) >= 0;
    }

    private void zoek(String text) {
        TableModel model = table.getModel();
        int size = model.getRowCount();
        if (text.length() > 0) {
            for (int i = zoekPos + 1; i < size; i++) {
                if (zoek(text, i)) {
                    select(i);
                    return;
                }
            }
            for (int i = 0; i < size && i <= zoekPos; i++) {
                if (zoek(text, i)) {
                    select(i);
                    return;
                }
            }

        }

    }

    private void select(int row) {
        table.setRowSelectionInterval(row, row);
        Rectangle rect = table.getCellRect(row, 0, true);
        table.scrollRectToVisible(new Rectangle(rect));
        zoekPos = row;
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
