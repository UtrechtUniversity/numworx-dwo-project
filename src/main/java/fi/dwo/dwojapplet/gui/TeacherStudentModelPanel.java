package fi.dwo.dwojapplet.gui;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.util.GensonMapConverter;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

/**
 *
 *
 * @author plas0006
 */
public class TeacherStudentModelPanel extends JPanel implements CenterSubPanel, ActionListener {

    private static final Logger LOG = Logger.getLogger(TeacherStudentModelPanel.class.getName());

    private TeacherStudentModelPanelProperties prop = new TeacherStudentModelPanelProperties();
    private TeacherStudentModelPanelTableModel tableModel;

    private CenterPanel center;

    private JButton addModelButton;
    private JButton cancelButton;
    private JButton viewButton;
    private DomainModelEditor textArea;

    private JPanel jtbl;
    private TableRowSorter rowSorter;

    private Image searchImage;
    int row;

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
//        ClassTeacherPanelTableModel model;

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean arg2, int aRow, int aCol) {
            this.value = value;
            JButton button = new JButton(new ImageIcon((Image) value));
            button.addActionListener(this);
            row = aRow;
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
            if (value == searchImage) {
                DomStudentModelContext model = (DomStudentModelContext) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());

                textArea.setModel(model.getModelStructure());
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
        jtable.getTableHeader().setReorderingAllowed(false);
        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.Y_AXIS));
        jtbl.add(jtable.getTableHeader());
        jtbl.add(jtable);
        jtbl.add(Box.createHorizontalGlue());
        //jtbl.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);
        tableModel = new TeacherStudentModelPanelTableModel();

        tableModel.init(prop.getModelList(), searchImage);
        jtable.setModel(tableModel);
        rowSorter = new TableRowSorter(tableModel);
        rowSorter.toggleSortOrder(0);//
        jtable.setRowSorter(rowSorter);

        if (jtable.getRowCount() > 0) {
            jtable.setRowSelectionInterval(0, 0);
        }
        jtable.setRowSelectionAllowed(false);
        jtable.setColumnSelectionAllowed(false);
        jtable.setCellSelectionEnabled(false);
        TableUtil.setDefaults(jtable, true, new TeacherStudentModelPanel.ImageRenderer(), new TeacherStudentModelPanel.ImageButtonEditor());
        TableUtil.setJTableSizes(jtable);

//        TableUtil.setDefaults(jtable, false, new ImageRenderer(), new ImageButtonEditor());
//        TableUtil.setJTableSizes(jtable);
// TODO shrink to fit heeft 520 als breedte
//        Dimension size = jtable.getPreferredSize();
//        if (size.width < 520) {
//            size.width = 520;
//        }
//        jtable.setMaximumSize(size);
        jtbl.setLocation(30, addModelButton.getSize().height
                + addModelButton.getLocation().y + 15);
        TableUtil.setBorder(jtable);
        //TableUtil.shrinkToFit(table, jtbl, 520, 405);
        jtbl.setVisible(false);
        this.add(jtbl);
        jtbl.setVisible(true);

    }

    /**
     * Creates a new ClassPanel which shows a list of classes.
     *
     * @throws nl.uu.fi.dwo.rest.exceptions.Dwo2Exception
     */
    public TeacherStudentModelPanel() throws Dwo2Exception {
        super(null);
        this.setSize(480, 500);

        //fetch user details.
        try {
            prop.init();
        } catch (Dwo2Exception e) {
            LOG.log(Level.SEVERE, "Can't retrieve initial user settings.", e);
            GuiCreator.instance().ShowErrorDialog(this, e);
        }

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        MediaTracker tr = new MediaTracker(this);
        searchImage = DwoHelper.getResourceImage(GuiConstants.SEARCH_IMAGE);
        tr.addImage(searchImage, 0);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }

        //FontMetrics fm;
        addModelButton = new JButton(TextMapper.getText(TextMapper.BTN_ADD));
        addModelButton.setSize(addModelButton.getPreferredSize());
        addModelButton.addActionListener(this);
        cancelButton = new JButton(TextMapper.getText(TextMapper.BTN_CANCEL));
        cancelButton.setSize(addModelButton.getPreferredSize());
        cancelButton.addActionListener(this);
        //addClassButton.setLocation(30, 10);
//        addClassButton.setVisible(GuiCreator.instance().getUser().hasRight(User.CHANGE_CLASS_RIGHT_TEACHER));
        Box header = Box.createHorizontalBox();
        header.add(addModelButton);
        header.add(Box.createHorizontalGlue());
        header.add(cancelButton);
        header.add(Box.createRigidArea(new Dimension(10, 0)));
        header.setPreferredSize(header.getMinimumSize());
        this.add(header);
        //addClassButton.setVisible(true);
        this.add(Box.createVerticalStrut(15));
        buildJTable();
        this.add(Box.createVerticalStrut(15));
        textArea = new DomainModelEditor();
        textArea.setEditable(false);
        textArea.setBackground(Color.LIGHT_GRAY);
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane);

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
        if (e.getSource() == addModelButton) {
            if (textArea.isEditable()) {
                //parse and save
                Genson g = new GensonBuilder().withConverters(new GensonMapConverter()).useIndentation(true).create();
                //String language = DwoHelper.getLocale().getLocale();
                try {
                    DomStudentModelStructure modelStructure = g.deserialize(textArea.getText(), DomStudentModelStructure.class);
                    DomStudentModelContext model = new DomStudentModelContext();
                    model.setModelStructure(modelStructure);
                    //TODO Set and Add schoolId!
                    prop.addModel(model);
                } catch (Dwo2Exception ex) {
                    Logger.getLogger(TeacherStudentModelPanel.class.getName()).log(Level.SEVERE, null, ex);
                GuiCreator.instance().ShowErrorDialog(center, ex);
                }
                //saved, reset ui
                addModelButton.setText(TextMapper.getText(TextMapper.BTN_ADD));
                cancelButton.enable(true);
                textArea.setEditable(false);
                GuiCreator.instance().ShowMessageDialog(center, "Saving");

            } else {
                addModelButton.setText(TextMapper.getText(TextMapper.BTN_UPDATE));
                textArea.setModel(null);
                textArea.setEditable(true);
            }
        } else if (e.getSource() == cancelButton) {
            textArea.setEditable(false);
            DomStudentModelContext model = (DomStudentModelContext) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
            Genson g = new GensonBuilder().withConverters(new GensonMapConverter()).useIndentation(true).create();
            //String language = DwoHelper.getLocale().getLocale();
            String jsonModel = g.serialize(model.getModelStructure());
            textArea.setText(jsonModel);
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
