package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.Teacher;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.panels.JPanelSchoolsandRolesProperties;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * This class represents a panel for the current user to change his account.
 *
 * @author M.J.B. Kupers
 *
 */
public class AccountSchoolRolesJPanel extends JPanel implements ActionListener {

//    protected User user;
    private JPanelSchoolsandRolesProperties prop = new JPanelSchoolsandRolesProperties();
    private static final Logger LOG = Logger.getLogger(AccountSchoolRolesJPanel.class.getName());

    private JButton addClassButton;

    private Image removeImage, editImage, usersImage, assignImage;

    private Box jtbl;

    private static final int ASSIGN_COL = 3;
    private static final int REMOVE_COL = 4;

    /**
     * Creates a new ProfilePanel for the current user. The account of the
     * current user can be changed.
     *
     */
    public AccountSchoolRolesJPanel() {
        super(null);

        //fetch user details.
        try {
            prop.init();
        }
        catch (Dwo2Exception e) {
            LOG.log(Level.SEVERE, "Can't retrieve initial user settings.", e);
            GuiCreator.instance().ShowMessageToUser(this, e.getLocalizedCodeExplanation(DwoHelper.getLocale()), "", JOptionPane.ERROR_MESSAGE);
        }

        //init gui (old code)
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
        //this.setSize(620, 485);
        //this.setSize(600, 470);
        //this.setPreferredSize(getSize());
        setBorder(BorderFactory.createEmptyBorder(10, 30, 0, 0));
        /* Add Remove-class image */
        MediaTracker tr = new MediaTracker(this);
        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_CLASS_IMAGE);
        editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_CLASS_IMAGE);
        usersImage = DwoHelper.getResourceImage(GuiConstants.USERS_CLASS_IMAGE);
        assignImage = DwoHelper.getResourceImage(GuiConstants.ASSIGN_CLASS_IMAGE);
        tr.addImage(removeImage, 0);
        tr.addImage(editImage, 1);
        tr.addImage(usersImage, 2);
        tr.addImage(assignImage, 3);
        try {
            tr.waitForAll();
        }
        catch (Exception e) {
        }

        //FontMetrics fm;
        addClassButton = new JButton(TextMapper.getText(TextMapper.GUIC_ADD_CLASS));
        //fm = addClassButton.getFontMetrics(addClassButton.getFont());
        addClassButton.setSize(addClassButton.getPreferredSize());
        addClassButton.addActionListener(this);
        //addClassButton.setLocation(30, 10);
        addClassButton.setVisible(GuiCreator.instance().getUser().hasRight(User.CHANGE_CLASS_RIGHT_TEACHER));
        Box header = Box.createHorizontalBox();
        header.add(addClassButton);
        header.add(Box.createHorizontalGlue());
        this.add(header);
        //addClassButton.setVisible(true);
        this.add(Box.createVerticalStrut(15));
        buildJTable();
        
    }

    /*******************************************************************************
     * Encapsulation of old code starts here
    /*******************************************************************************/
    
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
                case REMOVE_COL:
                    String format = TextMapper.getText(TextMapper.GUIC_TLTP_DELETE_CLASS);
                    setToolTipText(MessageFormat.format(format, arguments));
                    break;
                case ASSIGN_COL:
                    format = TextMapper.getText(TextMapper.GUIC_TLTP_ASSIGN_CLASS);
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

    public class ImageButtonEditor extends AbstractCellEditor implements
            TableCellEditor, ActionListener {

        Object value;
        ClassPanel.ClassModel model;
        int row;

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean arg2, int row, int col) {
            this.value = value;
            JButton button = new JButton(new ImageIcon((Image) value));
            button.addActionListener(this);
            this.row = row;
            model = (ClassPanel.ClassModel) table.getModel();
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return value;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
//            SchoolClass sc = model.classes[row];
//            final GuiCreator instance = GuiCreator.instance();
//            if (value == editImage) {
//                ClassRenamePanel panel = new ClassRenamePanel();
//                panel.setSchoolClass(sc);
//    //            int result = JOptionPane.showConfirmDialog(ClassPanel.this, panel, TextMapper.getText(TextMapper.GUIC_MSG_CLASS_CONFIGURATION),
//                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
//                //case OK persist returned values
//                if (result == JOptionPane.OK_OPTION) {
//                    //persist returned values					
//                    if (instance.renameClass(sc, panel.getClassName(), panel.getRegistrationKey(), panel.isIconizer()));
//                    //update gui when done
//                    center.loadMenu();
//                    model.fireTableCellUpdated(row, 0);
//                }
//
//            } else if (value == removeImage) {
//                /* Delete the course */
//                if (JOptionPane.showConfirmDialog(ClassPanel.this, TextMapper.getText(TextMapper.GUIC_MSG_DELETE_CLASS)
//      //                  + "?", TextMapper.getText(TextMapper.GUIC_DELETE_CLASS), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//                    if (instance.deleteClass(sc)) {
//                        center.loadMenu();
//                        model.removeRow(row);
//                    }
//                }
//
//            } else if (value == usersImage) {
//                center.loadCenter(instance.getClassUsersPanel(sc));
//            } else if (value == assignImage) {
//                instance.getDWO().setWait();
//                long t = System.currentTimeMillis();
//                //setData(domain.selectCourses(SelectCoursesDialog.selectCourses(this, domain.getAllCourses(), domain.getSelectedCourse()), true));
//                Course[] allCourses = instance.getCourseList();
//                System.out.println("getCourselist " + (System.currentTimeMillis() - t));
//                t = System.currentTimeMillis();
//                Course[] selectedSchoolCourses = sc.getSelectedSchoolCourses();
//                System.out.println("getSelected list " + (System.currentTimeMillis() - t));
//                instance.getDWO().setReady();
//                Course[] selectedCourses = SelectCoursesDialog.selectCourses(ClassPanel.this, allCourses, selectedSchoolCourses, sc);
//                if (selectedCourses != null) {
//                    sc.saveSelectedCourses(allCourses, selectedCourses);
//                }
//            }
//            fireEditingStopped();
// 
        }
    }

    private void buildJTable() {
        if (jtbl != null) {
            remove(jtbl);
            jtbl = null;
        }

        JTable table = new JTable();
        jtbl = Box.createHorizontalBox();
        jtbl.add(table);
        jtbl.add(Box.createHorizontalGlue());
        //jtbl.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);
   make nice proper table.
        if (GuiCreator.instance().getUser() instanceof Teacher) {
            Teacher t = (Teacher) GuiCreator.instance().getUser();
            SchoolClass[] classes = t.getClasses();
            table.setModel(new AccountSchoolsRolesTableModel());
        } else {
            return;
        }

        TableUtil.setDefaults(table, false, new ImageRenderer(), new ImageButtonEditor());
        TableUtil.setJTableSizes(table);
// TODO shrink to fit heeft 520 als breedte
        Dimension size = table.getPreferredSize();
        if (size.width < 520) {
            size.width = 520;
        }
        table.setMaximumSize(size);
        jtbl.setLocation(30, addClassButton.getSize().height
                + addClassButton.getLocation().y + 15);
        TableUtil.setBorder(table);
        //TableUtil.shrinkToFit(table, jtbl, 520, 405);
        jtbl.setVisible(false);
        this.add(jtbl);
        jtbl.setVisible(true);

    }
    /*******************************************************************************
     * Encapsulation of old code ends here
    /*******************************************************************************/

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     */
    public void actionPerformed(ActionEvent e) {
//        if (e.getSource() == addClassButton) {
//            try {
//                GuiCreator.instance().addClass();
//                center.loadMenu();
//                buildJTable();
//            } catch (ClassException e1) {
//                JOptionPane.showMessageDialog(this, e1.getMessage(), TextMapper.getText(TextMapper.GUIR_ERR_REGISTER), JOptionPane.ERROR_MESSAGE);
//            }
//
//        }

    }
}
