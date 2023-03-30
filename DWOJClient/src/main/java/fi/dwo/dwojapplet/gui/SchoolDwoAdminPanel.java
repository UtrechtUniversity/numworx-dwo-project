/*
 * Created on Mar 24, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminSchoolManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JOptionPane;
import fi.beans.numworxlf.JTextField;
import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.exceptions.SchoolException;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.util.Dwo2ExceptionTranslator;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * The panel where a School can be managed.
 *
 * @author M.J.B. Kupers
 * @author Wim van Velthoven
 *
 */
public class SchoolDwoAdminPanel extends JPanel implements CenterSubPanel, ActionListener {

    private static final Logger LOG = Logger.getLogger(SchoolDwoAdminPanel.class.getName());

    private SchoolDwoAdminPanelProperties prop = new SchoolDwoAdminPanelProperties();
    private SchoolDwoAdminPanelTableModel tableModel;
    private JTable table;
    private TableRowSorter rowSorter;
    private RowFilter<SchoolDwoAdminPanelTableModel, Object> tableFilter;

    public class ImageButtonEditor extends AbstractCellEditor implements
            TableCellEditor, ActionListener {

        Object value;
        SchoolDwoAdminPanelTableModel model;
        int row;

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean arg2, int row, int col) {
            this.value = value;
            JButton button = new JButton(new ImageIcon((Image) value));
            button.addActionListener(this);
            this.row = row;
            model = (SchoolDwoAdminPanelTableModel) table.getModel();
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return value;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            fireEditingStopped();
            School oldSchool = null;
            int lclRow=-1;
            if (value == editImage || value == removeImage || value == rightsImage|| value == imageStats) {
                lclRow = rowSorter.convertRowIndexToModel(row);
                DomSchool4DwoAdmin school =  tableModel.getSchool(lclRow);
                oldSchool = PersistenceFacade.instance().toSchool(Collections.singleton(school))[0];

            }
            if (value == editImage) {
                try {
                    DomSchool4DwoAdmin school = tableModel.getSchool(lclRow);
                    DomSchoolFull full = SecureDwoAdminSchoolManager.getSchool(school);
                    School s = AddSchoolDialog.editSchool(SchoolDwoAdminPanel.this.center, oldSchool,full);
                    if (s != null) {
                        tableModel.setValueAt(s.getSchoolLogin(), lclRow, 1);
                        tableModel.setValueAt(s.getName(), lclRow, 0);
                        school.setExpire(s.getExpire());
                        school.setAboType(s.getAboType());
                        model.fireTableRowsUpdated(lclRow, lclRow);
                    }
                }
                catch (Exception e) {
                    LOG.log(Level.SEVERE, null, e);
                }
            } else if (value == removeImage) {
                /* Delete the school */
                    //int row = tableModel.getSelectedRow();
                    DomSchool4DwoAdmin school = tableModel.getSchool(lclRow);
                    String msg = MessageFormat.format(
                            Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoHelper.getLocale(), 
                                    Dwo2ExceptionCode.User_ConfirmSchoolDelete),school.getSchoolName());
                if (GuiCreator.instance().ShowConfirmDialog(GuiCreator.instance().getMainPanel(), msg) == JOptionPane.OK_OPTION) {
                    try {
                        prop.deleteSchool(school);
                        tableModel.init(prop, editImage, rightsImage, emptyImage, removeImage, imageStats);
                        GuiCreator.instance().ShowMessageDialog(center, TextMapper.getText(TextMapper.DLG_DONE_MSG));
                    }
                    catch (Dwo2Exception ex) {
                        LOG.log(Level.SEVERE, "", ex);
                        GuiCreator.instance().ShowErrorDialog(center, ex);
                    }
                }
            } else if (value == rightsImage) {
                JDialog rightsDialog;
                try {
                    rightsDialog = new RightsDialog(tableModel.getSchool(lclRow));
                    rightsDialog.show();
                }
                catch (Dwo2Exception ex) {
                        LOG.log(Level.SEVERE, "", ex);
                        GuiCreator.instance().ShowErrorDialog(center, ex);
                }
            } else if (value == imageStats) {
              try {
                DomSchool4DwoAdmin school = tableModel.getSchool(lclRow);
                SchoolStatisticsPanel panel = new SchoolStatisticsPanel(school);
                JOptionPane.showMessageDialog(center, panel, school.getSchoolName(), JOptionPane.INFORMATION_MESSAGE);
                
              } catch(Exception ex) {
                LOG.log(Level.SEVERE, "Statistics", ex);           
              }
            }

        }

    }

    public class ImageRenderer extends JLabel implements TableCellRenderer {

        private ImageIcon icon = new ImageIcon();

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean selected, boolean hasFocus, int row, int col) {
            Image image = (Image) value;
           if (image == null) {
             setIcon(null);
           } else {
            icon.setImage(image);
            setIcon(icon);
           }
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            Object[] arguments = new Object[]{table.getValueAt(row, 0)};
//            switch (col) {
//                case 4 - 2:
//                    String s = TextMapper.getText(TextMapper.GUIS_TLTP_USERS_SCHOOL);
//                    setToolTipText(MessageFormat.format(s, arguments));
//                    break;
//                case 5 - 2:
//                    setToolTipText(TextMapper.getText(TextMapper.GUIS_TLTP_EDIT_SCHOOL));
//                    break;
//                case 6 - 2:
//                    String format = TextMapper.getText(TextMapper.GUIS_TLTP_DELETE_SCHOOL);
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


    private CenterPanel center;

    private JButton addSchoolButton, copyButton, clrBtn;

    private Image removeImage, editImage, rightsImage, emptyImage, assignImage, imageStats;

    private JTextField zoekField;

    private JButton zoekBtn;

    /**
     * Creates a new SchoolPanel witch shows a list of schools.
     *
     */
    public SchoolDwoAdminPanel() {
        super(null);
        
        try {
          PersistenceFacade.instance().toSchool(prop.getSchoolList());
        } catch (Dwo2Exception e1) {
        }
        
        
        this.setSize(480, 500);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        //this.setSize(627, 485);
        //this.setSize(600, 470);
        //setPreferredSize(getSize());
        /* Add Remove-school image */
        MediaTracker tr = new MediaTracker(this);
        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_CLASS_IMAGE);
        editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_CLASS_IMAGE);
        emptyImage = DwoHelper.getResourceImage(GuiConstants.EMPTY_IMAGE);
        rightsImage = DwoHelper.getResourceImage(GuiConstants.GUI_RIGHTS_IMAGE);
        assignImage = DwoHelper.getResourceImage(GuiConstants.ASSIGN_CLASS_IMAGE);
        imageStats = DwoHelper.getResourceImage(GuiConstants.RESULTS_STATS);

        Image clearImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_CLASS_IMAGE);
        Image searchImage = DwoHelper.getResourceImage(GuiConstants.SEARCH_IMAGE);
        tr.addImage(removeImage, 0);
        tr.addImage(editImage, 1);
        tr.addImage(rightsImage, 2);
        tr.addImage(assignImage, 3);
        tr.addImage(searchImage, 4);
        tr.addImage(clearImage, 5);
        tr.addImage(emptyImage, 6);
        tr.addImage(imageStats, 7);
        try {
            tr.waitForAll();
        }
        catch (Exception e) {
        }

        addSchoolButton = new JButton(TextMapper.getText(TextMapper.GUIS_ADD_SCHOOL));
        addSchoolButton.addActionListener(this);
        addSchoolButton.setSize(addSchoolButton.getPreferredSize());

        //addSchoolButton.setLocation(30, 10);
        Box header = Box.createHorizontalBox();
//        header.add(Box.createHorizontalStrut(25));
        header.add(addSchoolButton);
        header.add(Box.createHorizontalStrut(30));
        copyButton = new JButton(/*FIXME*/"Copy");
        //copyButton.setSize(copyButton.getPreferredSize());
        copyButton.addActionListener(this);
        copyButton.setSize(copyButton.getPreferredSize());
        //copyButton.setLocation(30 + w + 10, 10);
        header.add(copyButton);
        header.add(Box.createHorizontalStrut(30));
        zoekField = new JTextField();
        zoekField.setToolTipText("Zoek school");
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

    private JPanel jtbl;

    private void buildJTable() throws Dwo2Exception {
        if (jtbl != null) {
            remove(jtbl);
            jtbl = null;
        }
        jtbl = new JPanel();
        jtbl.setBackground(GuiConstants.MAIN_BACKGROUND);
        table = new JTable();
        table.getTableHeader().setReorderingAllowed(false);
        zoekPos = -1;
        tableModel = new SchoolDwoAdminPanelTableModel();
        tableModel.init(prop, editImage, rightsImage, emptyImage, removeImage, imageStats);
        table.setModel(tableModel);
        rowSorter = new TableRowSorter(tableModel);
        tableFilter = RowFilter.regexFilter(".*", 0);
        rowSorter.setRowFilter(tableFilter);
        rowSorter.toggleSortOrder(0);//
        table.setRowSorter(rowSorter);
        TableUtil.setDefaults(table, true, new ImageRenderer(), new ImageButtonEditor());
        TableUtil.setJTableSizes(table);
        TableColumnModel m = table.getColumnModel();
//        for (int i = 4; i < table.getColumnCount(); i++) {
//            m.getColumn(i).setMinWidth(m.getColumn(i).getPreferredWidth());
//            m.getColumn(i).setMaxWidth(m.getColumn(i).getPreferredWidth());
//        }
//        table.setSize(table.getPreferredSize());

        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.Y_AXIS));

        jtbl.add(table.getTableHeader());
        jtbl.add(table);
        jtbl.add(Box.createVerticalGlue());
        //TableUtil.setBorder(table);
//        int h = addSchoolButton.getSize().height + addSchoolButton.getLocation().y + 5;
        //jtbl.setBounds(5, h, 627 - 5 , 492 - h - 5);
        jtbl.validate();
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
    public JComponent getHeaderPanel() {
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
            //zoek(zoekField.getText().trim().toLowerCase());
            tableFilter = RowFilter.regexFilter(zoekField.getText().trim(), 0);
            rowSorter.setRowFilter(tableFilter);
            //table.setRowSorter(rowSorter);

            return;
        } else if (clrBtn == source) {
            zoekField.setText("");
            tableFilter = RowFilter.regexFilter(".*", 0);
            rowSorter.setRowFilter(tableFilter);
            //table.setRowSorter(rowSorter);

            return;
        } else if (source == copyButton) {
            int size = tableModel.getRowCount();
            DomSchoolFull schools[] = new DomSchoolFull[size];
            for(int i = 0; i < size; i++ ) {
              int n = i; // sorting?
              n = rowSorter.convertRowIndexToModel(i);
              DomSchool4DwoAdmin s = tableModel.getSchool(n);
              try {
                schools[i] = SecureDwoAdminSchoolManager.getSchool(s);
              } catch (Dwo2Exception e1) {
                LOG.log(Level.WARNING, "getSchool " + n, e1);
              }
            }
            ClipboardExport.instance().export(schools);
            return;
        }
        if (source == addSchoolButton) {
            try {
                School s = AddSchoolDialog.addSchool(center);
                if (s != null) {
                    tableModel.init(prop, editImage, rightsImage, emptyImage, removeImage, imageStats);
                }
            }
            catch (Dwo2Exception ex) {
                GuiCreator.instance().ShowErrorDialog(this, ex);
            }
            catch (SchoolException ex) {
                GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), ex.getMessage());
                Logger.getLogger(SchoolDwoAdminPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private int zoekPos = -1;

//    private boolean zoek(String text, int i) {
//        String data = table.getValueAt(i, 0) + " " + table.getValueAt(i, 1);
//        return data.toLowerCase().indexOf(text) >= 0;
//    }
//
//    private void zoek(String text) {
//        TableModel model = table.getModel();
//        int size = model.getRowCount();
//        if (text.length() > 0) {
//            for (int i = zoekPos + 1; i < size; i++) {
//                if (zoek(text, i)) {
//                    select(i);
//                    return;
//                }
//            }
//            for (int i = 0; i < size && i <= zoekPos; i++) {
//                if (zoek(text, i)) {
//                    select(i);
//                    return;
//                }
//            }
//
//        }
//
//    }
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
