/*
 * Created on Mar 24, 2005
 *
 */
package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomGetSingleSchoolStudent;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.dom.entities.DomSingleSchoolStudent;
import fi.dwo.rest.dom.entities.DomStudent;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.rest.SecureUserAccountManager;
import fi.dwo.dwojapplet.gui.domutils.DomSchoolClassListCellRenderer;
import fi.dwo.rest.dom.entities.DomLoginContext;
import fi.dwo.rest.util.Dwo2ExceptionTranslator;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
public class StudentsInSchoolClassTeacherPanel extends JPanel implements CenterSubPanel, ActionListener {

    private static final Logger LOG = Logger.getLogger(StudentsInSchoolClassTeacherPanel.class.getName());

    private StudentsInSchoolClassTeacherPanelProperties prop = new StudentsInSchoolClassTeacherPanelProperties();
    private StudentsInSchoolClassTeacherPanelTableModel tableModel;
    private DomSchoolClass schoolClass;
    private CenterPanel center;

    private JButton backButton;
    private JComboBox targetSchoolClassBox;
    private JButton deleteButton;
    private JButton copyToSchoolClassButton;
    private JButton addStudentsButton;

    private Image editImage;
    private Image emptyImage;
    private Image loginImage;

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
            this.fireEditingStopped();
//            final GuiCreator instance = GuiCreator.instance();
            if (value == editImage) {
                try {
                    DomStudent student = (DomStudent) tableModel.getValueAt(rowSorter.convertRowIndexToModel(row), tableModel.getColumnCount());
                    DomGetSingleSchoolStudent getStudent = new DomGetSingleSchoolStudent();
                    getStudent.setDomSchoolClass(schoolClass);
                    getStudent.setDomStudent(student);
                    DomSingleSchoolStudent user = prop.getSingleSchoolStudent(getStudent);
                    AccountDataFullStudentJPanel panel = new AccountDataFullStudentJPanel();
                    panel.setUser(user);
                    panel.setVisible(true);
                    int result = JOptionPane.showConfirmDialog(GuiCreator.instance().mainPanel, panel, TextMapper.getText(TextMapper.GUIP_ACCOUNTANDCONTACTINFO),
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    //case OK persist returned values
                    //user = new DomSingleSchoolStudent(panel.getUser()); superfluous.
                    if (result == JOptionPane.OK_OPTION) {
                        //persist returned values
                        user = new DomSingleSchoolStudent(panel.getUser());
                        prop.updateSingleSchoolStudent(user);
                        tableModel.init(prop.getStudentsInSchoolClass(schoolClass), loginImage, editImage, emptyImage);
                        tableModel.fireTableDataChanged();
                    }
                } catch (Dwo2Exception ex) {
                    LOG.log(Level.FINE, "", ex);
                    JOptionPane.showMessageDialog(null, ex.getLocalizedCodeExplanation(DwoHelper.getLocale()), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
                } finally {
                    fireEditingStopped();
                }
            } else if (value == loginImage) {
                fireEditingStopped();
//            //get Table setting
//                int col = tableModel.getSelectedColumn();
                int row = tableModel.getSelectedRow();
                try {
                    //set prop to table setting
                    DomStudent student = (DomStudent) tableModel.getValueAt(row, tableModel.getColumnCount());
                    DomGetSingleSchoolStudent getStudent = new DomGetSingleSchoolStudent();
                    getStudent.setDomSchoolClass(schoolClass);
                    getStudent.setDomStudent(student);
                    DomSingleSchoolStudent user = prop.getSingleSchoolStudent(getStudent);
                    DomLoginContext loginContext = SecureUserAccountManager.getLoginContext(user.getUserName(),
                            user.getPassword());
                    if (loginContext != null && loginContext.getLastLoginTimeStamp() != null) {
                        if (GuiCreator.instance().ShowConfirmDialog(GuiCreator.instance().getMainPanel(),
                                Dwo2ExceptionTranslator.getLocalizedCodeExplanation(DwoHelper.getLocale(), Dwo2ExceptionCode.User_ConfirmNewLoginSession)
                        ) != JOptionPane.OK_OPTION) {
                            return;
                        };
                    }
                    SecureUserAccountManager.logoutUser(DwoHelper.getCurrentLoginContext());
                    GuiCreator.instance().loginWithMd5(user.getUserName(), user.getPassword());

// HTML5
                    String student_player = GuiConstants.STUDENT_PLAYER;
                    if(student_player != null) {
                    	URL url = DwoHelper.getServerUrlPath();
                    	try {
// Hoe zit dit met de security? FIXME Gert?
                    		String a = "1\f" + System.currentTimeMillis() + "\f" + user.getUserName() +"\f"+ user.getPassword();
                    	    a = Base64.getUrlEncoder().encodeToString(a.getBytes(StandardCharsets.UTF_8));
                    		student_player = student_player + "?a=" + a; 
							url = new URL(url, student_player);
							if(DwoHelper.isApplication())
							{
								Desktop.getDesktop().browse(url.toURI());
								System.exit(0);
							} else {
								DwoHelper.getApplet().getAppletContext().showDocument(url, "_parent");
							}
						} catch (Exception e) {
							LOG.log(Level.SEVERE, "Login as Student", e);
						}
                    }
                  
                    
                    
                } catch (LoginException ex) {
                    Dwo2Exception err = new Dwo2Exception(Dwo2ExceptionCode.Rest_InternalError, ex.getMessage());
                    LOG.log(Level.SEVERE, "", ex);
                    GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), err);
                } catch (Dwo2Exception e) {
                    LOG.log(Level.SEVERE, "", e);
                    GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), e);
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
        jtable.getTableHeader().setReorderingAllowed(false);
        jtable.setMinimumSize(new Dimension(400, 300));
        jtbl.setLayout(new BoxLayout(jtbl, BoxLayout.Y_AXIS));
        jtbl.add(jtable.getTableHeader());
        jtbl.add(jtable);
//        jtbl.add(Box.createHorizontalGlue());
        //jtbl.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);
        tableModel = new StudentsInSchoolClassTeacherPanelTableModel();

        tableModel.init(prop.getStudentsInSchoolClass(schoolClass), loginImage, editImage, emptyImage);
        jtable.setModel(tableModel);

        if (jtable.getRowCount() > 0) {
            jtable.setRowSelectionInterval(0, 0);
        }
        jtable.setRowSelectionAllowed(false);
        jtable.setColumnSelectionAllowed(false);
        jtable.setCellSelectionEnabled(false);
        TableUtil.setDefaults(jtable, true, new StudentsInSchoolClassTeacherPanel.ImageRenderer(), new StudentsInSchoolClassTeacherPanel.ImageButtonEditor());
        TableUtil.setJTableSizes(jtable);
        for (int i = 0; i < jtable.getColumnModel().getColumnCount(); i++) {
            jtable.getColumnModel().getColumn(i).setPreferredWidth(jtable.getColumnModel().getColumn(i).getMinWidth());
        }

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
    public StudentsInSchoolClassTeacherPanel(final DomSchoolClass sc) throws Dwo2Exception {
        super(null);
        this.schoolClass = sc;
        this.setSize(480, 500);

        //fetch user details.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.setAlignmentY(TOP_ALIGNMENT);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        /* Add Remove-class image */
        MediaTracker tr = new MediaTracker(this);
        editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_CLASS_IMAGE);
        emptyImage = DwoHelper.getResourceImage(GuiConstants.EMPTY_IMAGE);
        loginImage = DwoHelper.getResourceImage(GuiConstants.STUDENT_IMAGE);
        tr.addImage(editImage, 0);
        tr.addImage(emptyImage, 1);
        tr.addImage(loginImage, 2);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }

        //FontMetrics fm;
        backButton = new JButton(TextMapper.getText(TextMapper.BTN_BACK));
        backButton.setSize(backButton.getPreferredSize());
        backButton.addActionListener(this);
        deleteButton = new JButton(TextMapper.getText(TextMapper.BTN_DELSELECTED));
        deleteButton.setSize(deleteButton.getPreferredSize());
        deleteButton.addActionListener(this);
        copyToSchoolClassButton = new JButton(TextMapper.getText(TextMapper.BTN_COPYSELECTEDTOCLASS));
        copyToSchoolClassButton.setSize(copyToSchoolClassButton.getPreferredSize());
        copyToSchoolClassButton.addActionListener(this);
//        Vector<DomSchoolClass> schoolClassVector = new Vector<DomSchoolClass>(prop.getTeachersOtherSchoolClasses(sc));
//        Collections.sort(schoolClassVector, new Comparator<DomSchoolClass>() {
//            public int compare(DomSchoolClass a, DomSchoolClass b) {
//                return a.getSchoolClassName().compareTo(b.getSchoolClassName());
//            }
//        });
        targetSchoolClassBox = new JComboBox();
        targetSchoolClassBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
//                JComboBox comboBox = (JComboBox) e.getSource();
                Vector<DomSchoolClass> schoolClassVector;
                try {
                    schoolClassVector = new Vector<DomSchoolClass>(prop.getTeachersOtherSchoolClasses(sc));
                    Collections.sort(schoolClassVector, new Comparator<DomSchoolClass>() {
                        public int compare(DomSchoolClass a, DomSchoolClass b) {
                            return a.getSchoolClassName().compareTo(b.getSchoolClassName());
                        }
                    });
                    DefaultComboBoxModel model = new DefaultComboBoxModel(schoolClassVector);
                    targetSchoolClassBox.setModel(model);
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

        DomSchoolClassListCellRenderer renderer = new DomSchoolClassListCellRenderer(TextMapper.getText(TextMapper.LBL_CLICK_TO_SELECT_A_SCHOOLCLASS));
//        if (schoolClassVector.size() > 0) {
//            targetSchoolClassBox.setSelectedIndex(0);
//            targetSchoolClassBox.setEnabled(true);
//            copyToSchoolClassButton.setEnabled(true);
//        } else {
//            targetSchoolClassBox.setEnabled(false);
//            copyToSchoolClassButton.setEnabled(false);
//        }
        targetSchoolClassBox.setRenderer(renderer);
        targetSchoolClassBox.setMaximumRowCount(10);
        targetSchoolClassBox.addActionListener(this);
//        targetSchoolClassBox.setMaximumSize(targetSchoolClassBox.getPreferredSize());
//        targetSchoolClassBox.setMinimumSize(new Dimension(40, targetSchoolClassBox.getPreferredSize().height));

        Box header = Box.createHorizontalBox();
        //header.setAlignmentX(Component.RIGHT_ALIGNMENT); ????????????
        //header.setBorder(BorderFactory.createEmptyBorder());//25, 25, 25, 25, Color.BLACK));
        header.add(backButton);
        header.add(new Box.Filler(new Dimension(0, 0), new Dimension(30, 0), new Dimension(30, 0)));
        header.add(deleteButton);
        header.add(new Box.Filler(new Dimension(0, 0), new Dimension(30, 0), new Dimension(30, 0)));
        header.add(copyToSchoolClassButton);
        header.add(new Box.Filler(new Dimension(0, 0), new Dimension(10, 0), new Dimension(10, 0)));
        header.add(targetSchoolClassBox);
        header.add(Box.createHorizontalGlue());
        header.setPreferredSize(new Dimension(400, backButton.getHeight()));
        header.setMaximumSize(new Dimension(3000, 100));

        this.add(header);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        buildJTable();
        addStudentsButton = new JButton(TextMapper.getText(TextMapper.BTN_NEW_STUDENTS));
        addStudentsButton.setSize(addStudentsButton.getPreferredSize());
        addStudentsButton.addActionListener(this);
        Box footer = Box.createHorizontalBox();
        footer.setAlignmentX(Component.RIGHT_ALIGNMENT);
        footer.setMaximumSize(new Dimension(3000, 100));
        footer.setBorder(BorderFactory.createEmptyBorder());//25, 25, 25, 25, Color.BLACK));
        footer.add(addStudentsButton);
        this.add(footer);
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
        return new HeaderPanel(TextMapper.getText(TextMapper.GUIC_CLASS_MANAGEMENT) + " - " + TextMapper.getText(TextMapper.HDR_EDITSTUDENTS) + " - " + TextMapper.getText(TextMapper.HDR_SCHOOLCLASS) + ": " + schoolClass.getSchoolClassName());
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == copyToSchoolClassButton) {
            boolean failed = false;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (((Boolean) tableModel.getValueAt(i, 6)).equals(true)) {
                    DomStudent student = (DomStudent) tableModel.getValueAt(i, tableModel.getColumnCount());
                    DomSchoolClass toSchoolClass = (DomSchoolClass) targetSchoolClassBox.getSelectedItem();
                    try {
                        prop.submitStudentToSchoolClass(schoolClass, toSchoolClass, student);
                    } catch (Dwo2Exception ex) {
                        LOG.log(Level.FINE, "", ex);
                        failed = true;
                    }
                }
            }
            if (failed == true) {
                GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_COPYSTUDENTERROR));
            }
            try {
//                Vector<DomSchoolClass> schoolClassVector = new Vector<DomSchoolClass>(prop.getTeachersOtherSchoolClasses(schoolClass));
//                Collections.sort(schoolClassVector, new Comparator<DomSchoolClass>() {
//                    public int compare(DomSchoolClass a, DomSchoolClass b) {
//                        return a.getSchoolClassName().compareTo(b.getSchoolClassName());
//                    }
//                });
//                DefaultComboBoxModel model = new DefaultComboBoxModel(schoolClassVector);
//                targetSchoolClassBox.setModel(model);
//                if (schoolClassVector.isEmpty()) {
//                    targetSchoolClassBox.setEnabled(false);
//                    copyToSchoolClassButton.setEnabled(false);
//                } else {
//                    targetSchoolClassBox.setEnabled(true);
//                    copyToSchoolClassButton.setEnabled(true);
//                }
//                targetSchoolClassBox.setMaximumSize(targetSchoolClassBox.getPreferredSize());
                targetSchoolClassBox.setSelectedIndex(-1);
                tableModel.init(prop.getStudentsInSchoolClass(schoolClass), loginImage, editImage, emptyImage);
                tableModel.fireTableDataChanged();
            } catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, "", ex);
                GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), ex);
            }
            tableModel.fireTableDataChanged();
            GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_DONE_MSG));
        } else if (e.getSource() == deleteButton) {
            try {
                int cnt = 0;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (((Boolean) tableModel.getValueAt(i, 6)).equals(true)) {
                        cnt++;
                    }
                }
                if (cnt == 0) {
                    GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_NO_STUDENTS_SELECTED));
                } else {
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        if (((Boolean) tableModel.getValueAt(i, 6)).equals(true)) {
                            DomStudent student = (DomStudent) tableModel.getValueAt(i, tableModel.getColumnCount());
                            prop.removeStudentFromSchoolClass(schoolClass, student);
                        }
                    }
//                    Vector<DomSchoolClass> schoolClassVector = new Vector<>(prop.getTeachersOtherSchoolClasses(schoolClass));
//                    DefaultComboBoxModel model = new DefaultComboBoxModel(schoolClassVector);
//                    targetSchoolClassBox.setModel(model);
//                    if (schoolClassVector.isEmpty()) {
//                        targetSchoolClassBox.setEnabled(false);
//                        copyToSchoolClassButton.setEnabled(false);
//                    } else {
//                        targetSchoolClassBox.setEnabled(true);
//                        copyToSchoolClassButton.setEnabled(true);
//                    }
//                    targetSchoolClassBox.setMaximumSize(targetSchoolClassBox.getPreferredSize());
                    targetSchoolClassBox.setSelectedIndex(-1);
                    tableModel.init(prop.getStudentsInSchoolClass(schoolClass), loginImage, editImage, emptyImage);
                    tableModel.fireTableDataChanged();
                    GuiCreator.instance().ShowMessageDialog(GuiCreator.instance().getMainPanel(), TextMapper.getText(TextMapper.DLG_DONE_MSG));
                }
            } catch (Dwo2Exception ex) {
                LOG.log(Level.FINE, "", ex);
                GuiCreator.instance().ShowErrorDialog(GuiCreator.instance().getMainPanel(), ex);
            }
        } else if (e.getSource()
                == backButton) {
            try {
                ClassTeacherPanel panel = new ClassTeacherPanel();
                center.loadCenter(panel);

            } catch (Dwo2Exception ex) {
                LOG.log(Level.FINE, "", ex);
                GuiCreator.instance().ShowErrorDialog(this, ex);
            }
        } else if (e.getSource()
                == addStudentsButton) {
            try {
                NewSingleSchoolStudentsTeacherPanel panel = new NewSingleSchoolStudentsTeacherPanel(schoolClass);
                center.loadCenter(panel);
            } catch (Dwo2Exception ex) {
                LOG.log(Level.FINE, "", ex);
                GuiCreator.instance().ShowErrorDialog(center, ex);
            }
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
    public void stateChanged(ChangeEvent e
    ) {
    }
}
