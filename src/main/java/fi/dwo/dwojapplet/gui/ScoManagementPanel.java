// Source file: C:\\parameters\\fi\\dwo\\client\\gui\\ScoManagementPanel.java
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.AppletConfig;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.gui.action.BackupModuleAction;
import fi.dwo.dwojapplet.gui.action.DeleteAction;
import fi.dwo.dwojapplet.gui.action.ImportModuleAction;
import fi.dwo.dwojapplet.gui.action.NewAction;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import fi.wiskopdr.WiskOpdr;
import fi.wiskopdr.WiskOpdrEditPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * This class is a panel containing a list of SCO's to edit, delete or add. It
 * is used for SCO-management.
 *
 * @author M.J.B. Kupers
 * @author Wim van Velthoven
 *
 */
public class ScoManagementPanel extends JPanel implements CenterSubPanel, ActionListener {
    private static final Logger log = Logger.getLogger(ScoManagementPanel.class.getName());

    private CenterPanel center;

    private JButton addScoButton, exportCourseButton, importScosButton;
    private JButton courseLogoButton;

    private Image removeImage, editImage, courseImage, parametersImage, upImage, downImage;

    private Course course;

    private JLabel label;

    private JLabel noScosLabel;
    private FileDialog saveDial, openDial;

    private JCheckBox editorCB;
    private Box editorBox = Box.createVerticalBox();

    //private JButton publishButton;
    /**
     * @param course
     */
    public ScoManagementPanel(Course course) {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 30, 0, 10));
        this.course = course;
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        //this.setSize(620, 485);
        //this.setSize(600, 470);
        //setPreferredSize(getSize());
        course.loadScos();
        Image logo = course.getCourseLogo();
        /* Add Remove-course image */
        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_SCO_IMAGE);
        editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_SCO_IMAGE);
        courseImage = DwoHelper.getResourceImage(GuiConstants.COURSE_SCO_IMAGE);
        parametersImage = DwoHelper.getResourceImage(GuiConstants.PARAMETERS_SCO_IMAGE);

        upImage = DwoHelper.getResourceImage(GuiConstants.UP_SCO_IMAGE);
        downImage = DwoHelper.getResourceImage(GuiConstants.DOWN_SCO_IMAGE);

        Box top = Box.createHorizontalBox();
        //top.add(Box.createHorizontalStrut(30));
        addScoButton = new JButton(TextMapper
                .getText(TextMapper.GUIS_ADD_SCO));
        addScoButton.setSize(addScoButton.getPreferredSize());
        //addScoButton.addActionListener(this);
        addScoButton.addActionListener(new NewAction(course, false));
        addScoButton.setLocation(30, 10);
        top.add(addScoButton);
        top.add(Box.createHorizontalGlue());
        /*if(course.getSchoolID()>0 && GuiCreator.instance().dwo.getUser().hasRight(User.PROFILE_ADMIN_RIGHT))
         {
         publishButton = new JButton("Publiceer");
         publishButton.setToolTipText("Verplaats module naar profiel");
         publishButton.addActionListener(this);
         top.add(publishButton);
         top.add(Box.createHorizontalStrut(10));
         }*/
        if (DwoHelper.isSecure()) {
            exportCourseButton = new JButton(new BackupModuleAction(course));
            exportCourseButton.setSize(exportCourseButton.getPreferredSize());
            //exportCourseButton.addActionListener(this);
            exportCourseButton.setVisible(false);
            top.add(exportCourseButton);
            if (DwoHelper.isSecure())// && !CenterPanel.isIconizer()) // TODO verplaatsen naar menu van tree
            {
                exportCourseButton.setVisible(true);
            }

            top.add(Box.createHorizontalStrut(10));
            importScosButton = new JButton(new ImportModuleAction(course));
            importScosButton.setSize(importScosButton.getPreferredSize());
            importScosButton.addActionListener(this);
            importScosButton.setVisible(false);
            top.add(importScosButton);
            //top.add(Box.createHorizontalStrut(10));

        }
        courseLogoButton = new JButton(new ImageIcon(logo));
        courseLogoButton.setBorderPainted(false);
// TODO Mac?
        courseLogoButton.setBorder(BorderFactory.createLineBorder(getForeground()));
        courseLogoButton.setContentAreaFilled(false);
        top.setBounds(0, 10, getWidth(), addScoButton.getPreferredSize().height);
        add(top, BorderLayout.NORTH);
        top.doLayout();
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        cpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cpanel.setOpaque(false);
        panel.add(cpanel, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
        //if(false && DwoHelper.isApplication())
        if (DwoHelper.isSecure()) {
            importScosButton.setVisible(true);// && !CenterPanel.isIconizer()); // TODO verplaatsen naar Tree Menu
            courseLogoButton.addActionListener(this);
            courseLogoButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            courseLogoButton.setBorderPainted(true);
        }

        String[] arguments = new String[1];
        arguments[0] = course.getName();
        label = new JLabel(TextMapper.format((TextMapper.GUIS_LBL_SCO_OF_COURSE), arguments));

        label.setFont(GuiConstants.SCO_TEXT);
        label.setSize(label.getPreferredSize());
        label.setLocation(30, 50);
        panel.add(label, BorderLayout.NORTH);
        courseLogoButton.setLocation(520, label.getLocation().y);
        courseLogoButton.setSize(courseLogoButton.getPreferredSize());
        Box hulp = Box.createVerticalBox();
        hulp.add(courseLogoButton);

        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.setOpaque(false);
        panel.add(panel1, BorderLayout.CENTER);

        editorCB = new JCheckBox("Editor");
        editorCB.addActionListener(this);
        editorBox.add(editorCB);
        if (course.getDescription().startsWith("H4sIAAAAAA")) {
            editorCB.setSelected(true);
//TODO WiskOpdr
            wiskOpdrEditPanel = WiskOpdr.getWiskOpdrEditPanel(course.getDescription());
            wiskOpdrEditPanel.setPreferredSize(new Dimension(700, 300));
            editorBox.add(wiskOpdrEditPanel);
        } else {
            pane = new JTextArea();
            pane.setText(course.getDescription());
            pane.setBorder(BorderFactory.createLineBorder(Color.black));
            editorBox.add(pane);
        }
        panel1.add(editorBox, BorderLayout.NORTH);

        panel1.add(cpanel, BorderLayout.CENTER);
        hulp.add(Box.createVerticalGlue());
        this.add(hulp, BorderLayout.EAST);
        arguments = new String[1];
        arguments[0] = course.getName();
        noScosLabel = new JLabel(TextMapper.format((TextMapper.GUIS_NO_SCOS), arguments));
        noScosLabel.setFont(GuiConstants.SCO_TEXT);
        noScosLabel.setSize(noScosLabel.getPreferredSize());
        noScosLabel.setLocation((this.getSize().width / 2) - (noScosLabel.getSize().width / 2), 100);

        addScoTable();
        if (DwoHelper.isSecure()) {
            final Frame topFrame = DwoHelper.getFrameForComponent(null);
            saveDial = new FileDialog(topFrame, exportCourseButton.getToolTipText(), FileDialog.SAVE);
            saveDial.setDirectory(System.getProperty("user.dir", "."));
            openDial = new FileDialog(topFrame, importScosButton.getToolTipText(), FileDialog.LOAD);
            openDial.setDirectory(System.getProperty("user.dir", "."));
        }
    }

    JTable jtbl;

    private JPanel cpanel;

    private JButton stopBtn;

    public class ImageRenderer extends JLabel implements TableCellRenderer {

        ImageRenderer(boolean iconizer) {
            super();
            this.iconizer = iconizer;
        }

        private ImageIcon icon = new ImageIcon();
        private boolean iconizer;

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean selected, boolean hasFocus, int row, int col) {
            Image image = (Image) value;
            if (image != null) {
                icon.setImage(image);
                setIcon(icon);
            } else {
                setIcon(null);
            }
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            Object[] arguments = new Object[]{table.getValueAt(row, 0)};

            if (iconizer && col > 0) {
                col++;
            }
            if (iconizer && col > 2) {
                col++;
            }

            switch (col) {
                case 1:
                    setToolTipText(TextMapper.format((TextMapper.GUIS_TLTP_COURSE_SCO), arguments));
                    break;
                case 2:
                    setToolTipText(TextMapper.getText(TextMapper.GUIS_TLTP_EDIT_SCO));
                    break;
                case 3:
                    setToolTipText(TextMapper.getText(TextMapper.GUIS_TLTP_PARAMETERS_SCO));
                    break;
                case 7:
                    String format = TextMapper.getText(TextMapper.GUIS_TLTP_DELETE_SCO);
                    setToolTipText(MessageFormat.format(format, arguments));
                    break;
                default:
                    setToolTipText(null); // TODO ....
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
        AbstractTableModel model;
        int row;

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean arg2, int row, int col) {
            this.value = value;
            JButton button = new JButton(new ImageIcon((Image) value));
            button.addActionListener(this);
            this.row = row;
            model = (AbstractTableModel) table.getModel();
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return value;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            Sco s = course.getScoList()[row];
            if (value == courseImage) {
                /* Show the Course Panel */
                int id = course.getParentID();
                if (id != 0) {
                    CourseMap map;
                    try {
                        map = (CourseMap) PersistenceFacade.instance().get(id, Course.class);
                        center.loadCenter(GuiCreator.instance().getCourseManagementPanel(map));
                    } catch (PersistenceException e) {
        
                        log.log(Level.SEVERE,null,e);
                    }
                } else {
                    center.loadCenter(GuiCreator.instance().getCourseManagementPanel());
                }
            } else if (value == editImage) {
                if (ScoNameDialog.editSco(s)) {
                    model.fireTableCellUpdated(row, 0);
                    noUpdateCourse();
                }
            } else if (value == removeImage) {
                /* Delete the Sco */
                if (DeleteAction.deleteSco(s)) {
                    model.fireTableRowsDeleted(row, row);
                    noUpdateCourse();
                    if (model.getRowCount() == 0) {
                        addScoTable();
                    }
                }

            } else if (value == parametersImage) {
                GuiCreator.instance().loadParameterManagementPanel(s);
            } else if (value == upImage) {
                Sco s2 = course.getScoList()[row - 1];
                swapSco(s, s2);
                model.fireTableRowsUpdated(row - 1, row);
                noUpdateCourse();
            } else if (value == downImage) {
                Sco s2 = course.getScoList()[row + 1];
                swapSco(s, s2);
                model.fireTableRowsUpdated(row, row + 1);
                noUpdateCourse();
            }
            fireEditingStopped();
        }

    }

    class ScoModelForTree extends AbstractTableModel {

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public int getRowCount() {
            return course.getScoList().length;
        }

        @Override
        public Class getColumnClass(int col) {
            if (col > 0) {
                return Image.class;
            }
            return super.getColumnClass(col);
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            if (col == 2) {
                return row != 0;
            }
            if (col == 3) {
                return row != getRowCount() - 1;
            }
            return true;
        }

        @Override
        public Object getValueAt(int row, int col) {
            switch (col) {
                case 0:
                    return course.getScoList()[row].getScoName();
                case 1:
                    return editImage;
                case 2:
                    if (row != 0) {
                        return upImage;
                    }
                    break;
                case 3:
                    if (row != getRowCount() - 1) {
                        return downImage;
                    }
                    break;
                case 4:
                    return removeImage;
            }
            return null;
        }

    }

    class ScoModel extends AbstractTableModel {

        @Override
        public int getColumnCount() {
            return 7;
        }

        @Override
        public int getRowCount() {
            return course.getScoList().length;
        }

        @Override
        public Class getColumnClass(int col) {
            if (col > 0) {
                return Image.class;
            }
            return super.getColumnClass(col);
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            if (col == 4) {
                return row != 0;
            }
            if (col == 7) {
                return row != getRowCount() - 1;
            }
            return col > 0;
        }

        @Override
        public Object getValueAt(int row, int col) {
            switch (col) {
                case 0:
                    return course.getScoList()[row].getScoName();
                case 1:
                    return courseImage;
                case 2:
                    return editImage;
                case 3:
                    return parametersImage;
                case 6:
                    return removeImage;

                case 4:
                    if (row != 0) {
                        return upImage;
                    }
                    break;
                case 5:
                    if (row != getRowCount() - 1) {
                        return downImage;
                    }
                    break;
            }
            return null;
        }

    }

    private JComponent buildJTable() {
        if (jtbl != null) {
            if (jtbl.getParent() != null) {
                jtbl.getParent().remove(jtbl);
            }
            jtbl = null;
        } else {
            if (noScosLabel.isShowing()) {
                noScosLabel.getParent().remove(noScosLabel);
            }
        }
        Sco[] scos = course.getScoList();
        if (scos == null || scos.length == 0) {
            noScosLabel.setVisible(true);
            label.setVisible(false);
            return noScosLabel;
        } else {
            noScosLabel.setVisible(false);
            label.setVisible(true);
        }
        TableModel model = new ScoModel();
        if (CenterPanel.isIconizer()) {
            model = new ScoModelForTree();
        }
        JTable table = new JTable(model);
        TableUtil.setDefaults(table, false, new ImageRenderer(CenterPanel.isIconizer()), new ImageButtonEditor());

        TableUtil.setJTableSizes(table);
        //table.setSize(table.getPreferredSize());
        //table.setMaximumSize(table.getPreferredSize());
        jtbl = table;
        //TableUtil.setBorder(jtbl);
        //jtbl.setBorder(TableUtil.tableBorder);
        //jtbl.setLocation(30, label.getSize().height
        //        + label.getLocation().y+10);
        //TableUtil.shrinkToFit(table, jtbl, 520-30, 405);
        return table;
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

    @Override
    public Component getHeaderPanel() {
        HeaderPanel hp = new HeaderPanel(TextMapper.getText(TextMapper.GUIS_SCO_MANAGEMENT));
        stopBtn = new JButton(TextMapper.getText(TextMapper.GUIH_STOP_EDIT));
        stopBtn.addActionListener(this);
        hp.setButtonBox(GuiCreator.instance().fx(stopBtn));
        return hp;
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        Object src = e.getSource();
        if (src == editorCB) {
            if (editorCB.isSelected()) {
//TODO WiskOpdr
                if (wiskOpdrEditPanel == null) {
                    wiskOpdrEditPanel = WiskOpdr.getWiskOpdrEditPanel(course.getDescription());
                    wiskOpdrEditPanel.setPreferredSize(new Dimension(700, 300));
                    editorBox.add(wiskOpdrEditPanel);
                }
                wiskOpdrEditPanel.setVisible(true);
                pane.setVisible(false);
            } else if (wiskOpdrEditPanel != null) {
                if (pane == null) {
                    pane = new JTextArea();
                    pane.setText("");
                    pane.setBorder(BorderFactory.createLineBorder(Color.black));
                    editorBox.add(pane);
                }
                wiskOpdrEditPanel.setVisible(false);
                pane.setVisible(true);
            }
        }

        if (src == stopBtn) {
            end();
            center.select(course);
        }

        if (src == courseLogoButton) {
            try {
                importCourseLogo();
            } catch (IOException e1) {
                log.log(Level.SEVERE,null,e1);
            }
            return;
        }
        if (src == exportCourseButton) {
            return;
        } else if (src == importScosButton) {
//    		try { 
//    			importScos();
//    		} catch (Exception e2) {
//    			log.log(Level.SEVERE,null,e2);			
//    		}
        }
        if (src == addScoButton) {
            Sco s = null;

            // speciaal voor de SAG en REV: er kan maar 1 soort appletConfig gebruikt worden, nl WiskOpdr
            if (course.getDwoProfile() == 15) {
                try {
                    AppletConfig ac = (AppletConfig) (PersistenceFacade.instance().get(55, AppletConfig.class));
                    s = ScoNameDialog.addSco(this, course, ac);
                } catch (PersistenceException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            } //
            else {
                s = AddScoDialog.addSco(this, course);
            }
            if (s != null) {
// FIXME addSco kan de sco al in de lijst gezet hebben....
                Sco[] as = course.getScoList();
                /* Create a larger array and add the item */
                Sco[] tmp = new Sco[as.length + 1];
                System.arraycopy(as, 0, tmp, 0, as.length);
                tmp[tmp.length - 1] = s;
                course.setScoList(tmp);
                center.updateCourse(course);
            }

        } //else if( src == publishButton)
        //{
        //	publishCourse();
        //}

    }

    /*private void publishCourse() {
     if(course.getSchoolID()>0)
     {
     course.setSchoolID(0);
     course.setExport(false); // ik denk dat een gepubliceerde course niet exporteerbaar is!
     publishButton.setEnabled(false); // gray out
     noUpdateCourse();
     }
	
     }*/
    private void importCourseLogo() throws IOException {
        String naam; // FIXME
        openDial.setTitle(TextMapper.format(TextMapper.GUIS_LOAD_LOGO, new Object[]{course.toString()}));
        openDial.show();
        naam = openDial.getFile();
        if (naam != null) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            File dir = new File(openDial.getDirectory());
            File file = new File(dir, naam);
            BufferedImage img = ImageIO.read(file);
            Image reduced;
            if (img.getWidth() <= 64 && img.getHeight() <= 64) {
                reduced = img;
            } else {
                reduced = img.getScaledInstance(Math.min(64, img.getWidth()), Math.min(64, img.getHeight()), Image.SCALE_SMOOTH);
            }
            if (reduced instanceof BufferedImage) {
                img = (BufferedImage) reduced;
            } else {
                img = new BufferedImage(Math.min(64, img.getWidth()), Math.min(64, img.getHeight()), BufferedImage.TYPE_INT_ARGB);
                img.createGraphics().drawImage(reduced, 0, 0, null);
            }
            ImageIO.write(img, "png", output);
            output.close();
            byte[] data = output.toByteArray();
            reduced = Toolkit.getDefaultToolkit().createImage(data);
            course.setImageData(data);
            course.setCourseLogo(reduced);
            courseLogoButton.setIcon(new ImageIcon(reduced));
// TODO omzetten in PersistenceFacade!
            try {
                PersistenceFacade.instance().setLogo(course.getID(), data);
            } catch (Exception e) {
                log.log(Level.SEVERE,null,e);
            }

        }
    }

    private void swapSco(Sco s1, Sco s2) {
        if (GuiCreator.instance().swapSco(s1, s2))
    		/*buildJTable()*/;
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

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.CenterSubPanel#end()
     */
    @Override
    public void end() {
//TODO WiskOpdr
        if (editorCB.isSelected() && !wiskOpdrEditPanel.getText().equals(course.getDescription())) {
            course.setDescription(wiskOpdrEditPanel.getText());
            GuiCreator.instance().updateCourse(course);
        } else
            if (!editorCB.isSelected() && pane != null && !pane.getText().equals(course.getDescription())) {
            course.setDescription(pane.getText());
            GuiCreator.instance().updateCourse(course);
        }
        center.getMenu().setEditing(false);
        center.setStrategy(null);
    }

    /**
     *
     */
    private void addScoTable() {
        cpanel.removeAll();
        JComponent comp = buildJTable();
        comp.setAlignmentY(0.0f);
        cpanel.add(comp);
        cpanel.invalidate();
        validate();
        repaint();
    }

    @Override
    public Object getUserObject() {
        return course;
    }

    private boolean ok = true;

    private JTextArea pane;
//TODO WiskOpdr
    private WiskOpdrEditPanel wiskOpdrEditPanel;

    @Override
    public void stateChanged(ChangeEvent e) {
        //System.out.println("ChangeEvent " + e);
        if (ok && course == e.getSource()) {
            addScoTable();
        }
    }

    private void noUpdateCourse() {
        ok = false;
        center.updateCourse(course);
        ok = true;
    }

}
