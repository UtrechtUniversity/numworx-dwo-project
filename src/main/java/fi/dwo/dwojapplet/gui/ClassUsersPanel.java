// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\ClassUsersPanel.java
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.exceptions.LoginException;
import fi.dwo.commons.exceptions.RegisterException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.ContactDocent;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * This class is a panel where the users of a SchoolClass can be viewed and
 * removed.
 *
 * @author M.J.B. Kupers
 * @author Velth101
 *
 */
public class ClassUsersPanel extends JPanel implements CenterSubPanel/*, ActionListener*/ {

    private static final Logger LOG = Logger.getLogger(ImageButtonEditor.class.getName());

    private CenterPanel center;

    private SchoolClass schoolClass;

    Image removeImage, editImage, userImage;

    //private Box tbl;
    public class ImageRenderer extends JLabel implements TableCellRenderer {

        private ImageIcon icon = new ImageIcon();

        /**
         *
         */
        private ImageRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
        }

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
            if (selected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }

    }

    UserModel model;

    class ImageButtonEditor extends AbstractCellEditor implements
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
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return value;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            User u = model.userList[row];
            if (value == model.userImage) {
//                try {
                    //MapperCreator.instance(User.class).removeObject(u.getID()); // not good enough, need fresh copy.
                    PersistenceFacade.instance().clearObjectInMapperCache(User.class,u.getID());
                    GuiCreator.instance().logoff(u.getUsername());
//                } catch (LoginException e) {
//                    LOG.log(Level.SEVERE, null, e);
//                }
            } else if (value == model.editImage) {
                try {
                    String newPassword = JOptionPane.showInputDialog(ClassUsersPanel.this, TextMapper.getText(TextMapper.GUIP_PASSWORD), u.getUsername(), JOptionPane.QUESTION_MESSAGE);
                    if (newPassword != null) {
                        PersistenceFacade.instance().changeAccount(u, null, newPassword, u.getFirstname(), u.getMiddleName(), u.getLastName(), u.getEmail());
                        model.fireTableRowsUpdated(row, row);
                        JOptionPane.showMessageDialog(ClassUsersPanel.this, TextMapper.getText(TextMapper.GUIP_MSG_PROFILE_CHANGED));
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, null, e);
                }
            } else if (value == model.removeImage) {
                String[] arguments = new String[1];
                arguments[0] = u.getName();
                String msg = TextMapper.getText(TextMapper.GUIC_MSG_DELETE_STUDENT);
                msg = MessageFormat.format(msg, arguments);
                if (JOptionPane.showConfirmDialog(ClassUsersPanel.this, msg
                        + "?", TextMapper.getText(TextMapper.GUIC_DELETE_STUDENT), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                    removeUserFromClass(u, row);
                }
            }
            fireEditingStopped();
        }

    }

    void removeUserFromClass(User u, int row) {
        String[] arguments;
        schoolClass.disconnectStudent(DwoHelper.getSchoolClass().getID(),u);
        model.deleteRow(row);
        if (model.getRowCount() == 0) {
            // TODO dit is niet goed. createLabel(vbox) o.i.d.
            //tbl.setVisible(false);
            arguments = new String[1];
            arguments[0] = schoolClass.getName();
            String s = TextMapper.getText(TextMapper.GUIC_NO_STUDENTS);
            JLabel label = new JLabel(MessageFormat.format(s, (Object[]) arguments));
            label.setFont(GuiConstants.SCO_TEXT);
            label.setAlignmentY(0.24f);
            ClassUsersPanel.this.removeAll();
            ClassUsersPanel.this.add(label);
            ClassUsersPanel.this.repaint();
        }
    }

    class RemoveAllUsers extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            Component source = (Component) e.getSource();
            int how = NOT;
            final String text = TextMapper.format((TextMapper.GUIS_MSG_DELETE_STUDENT), new Object[]{TextMapper.getText("leerlingen ook")});

            Box box = Box.createVerticalBox();
            box.add(new JLabel(text + "?"));
// nadenken over de default, Henk wil 'rmRadio' : De school is eigenaar van de leerlinggegevens inclusief dwo-account
            JRadioButton noRadio = new JRadioButton(TextMapper.getText(TextMapper.BTN_NO), true);
            JRadioButton delRadio = new JRadioButton(TextMapper.getText(TextMapper.GUIUMP_REMOVE_FROM_SCHOOL), false);
            JRadioButton rmRadio = new JRadioButton(TextMapper.getText(TextMapper.GUIUMP_REMOVE_COMPLETE), false);
            ButtonGroup group = new ButtonGroup();
            group.add(noRadio);
            group.add(delRadio);
            group.add(rmRadio);
            box.add(noRadio);
            box.add(delRadio);
            box.add(rmRadio);

            String title = TextMapper.format(TextMapper.GUIUMP_REMOVE_CLASS, new Object[]{schoolClass.getName()});
            if (JOptionPane.showConfirmDialog(source, box, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (delRadio.isSelected()) {
                    how = FROM_SCHOOL;
                } else if (rmRadio.isSelected()) {
                    how = FROM_DWO;
                }

                removeAllUsersFromClass(how);
            }

        }

        public RemoveAllUsers(String name, Icon icon) {
            super(name, icon);
        }

    }

    private static final int NOT = 0, FROM_SCHOOL = 1, FROM_DWO = 2;

    void removeAllUsersFromClass(int how) {
        while (model.getRowCount() != 0) {
            User u = model.userList[0];
            removeUserFromClass(u, 0);
            switch (how) {
                case FROM_SCHOOL: // remove from school
                    PersistenceFacade.instance().deleteUserFromSchool(u);
                    break;
                case FROM_DWO: // remove compleet...
                    try {
                        PersistenceFacade.instance().deleteUser(u);
                    } catch (RegisterException e) {
                        JOptionPane.showMessageDialog(ClassUsersPanel.this, e.getMessage());
                    }
                    break;
            }
        }

    }

    /**
     * Creates a new ClassUsersPanel witch shows the students of the class.
     *
     * @param c The SchoolClass of the ClassUsersPanel.
     */
    public ClassUsersPanel(SchoolClass c) {
        super(null);
        this.setBackground(GuiConstants.MAIN_BACKGROUND);
// initialisatie images
        removeImage = DwoHelper.getResourceImage(GuiConstants.REMOVE_STUDENT_IMAGE);
        userImage = DwoHelper.getResourceImage(GuiConstants.STUDENT_IMAGE);
        editImage = DwoHelper.getResourceImage(GuiConstants.EDIT_SCO_IMAGE);

        MediaTracker tr = new MediaTracker(this);
        tr.addImage(removeImage, 0);
        tr.addImage(userImage, 0);
        tr.addImage(editImage, 0);
        try {
            tr.waitForAll();
        } catch (Exception e) {
        }

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEmptyBorder(15, 30, 0, 0));

        schoolClass = c;
        Box vbox = Box.createVerticalBox();
        User[] users = c.getStudents();
        if (users.length == 0) {
            String[] arguments = new String[1];
            arguments[0] = c.getName();
            JLabel label = new JLabel(TextMapper.format(TextMapper.GUIC_NO_STUDENTS, arguments));
            label.setFont(GuiConstants.SCO_TEXT);
            label.setAlignmentY(0.24f);

            vbox.setAlignmentX(0);
            vbox.setAlignmentY(0);
            vbox.add(label);

            vbox.add(Box.createVerticalStrut(20));
            add(vbox);

        } else {
            Arrays.sort(users);
            createJTable(vbox, users);
        }
        RegisterClassListButton registerClassListButton = new RegisterClassListButton(schoolClass);
        JButton removeStudentsButton;

        User user = GuiCreator.instance().getUser();
        if (user.hasRight(User.CHANGE_CLASS_RIGHT_TEACHER)) {
            Box hbox = Box.createHorizontalBox();
            hbox.add(registerClassListButton);
            if (true || user instanceof ContactDocent) {
                hbox.add(Box.createHorizontalStrut(10));
                removeStudentsButton = new JButton(new RemoveAllUsers(TextMapper.getText(TextMapper.GUIUMP_ALL_STUDENTS), new ImageIcon(removeImage)));
                hbox.add(removeStudentsButton);
            }
            vbox.add(hbox);
        }

    }

    /**
     * @param vbox
     * @param users
     */
    private void createJTable(Box vbox, User[] users) {

        UserModel dm = new UserModel();
        model = dm;
        dm.userList = users;
        users = null;
        dm.editImage = editImage;
        dm.removeImage = removeImage;
        dm.teacherImage = userImage;
        dm.userImage = userImage;
        JTable table = new JTable(dm);

        //JTable table = new JTable(new ClassUsersModel());
        TableUtil.setDefaults(table, true, new ImageRenderer(), new ImageButtonEditor());
        School school = dm.userList[0].getSchool();
        SchoolClassTableRenderer renderer = new SchoolClassTableRenderer(school);
        table.setDefaultRenderer(SchoolClass.class, new SchoolClassTableRenderer(renderer.getItems()));
        table.setDefaultEditor(SchoolClass.class, new DefaultCellEditor(renderer));

        TableUtil.setBorder(table);
        TableUtil.setJTableSizes(table);

        //vbox.setAlignmentX(0);
        vbox.setAlignmentY(0);
        Dimension size = table.getPreferredSize();
//			table.setMinimumSize(size);
//			if(size.width < 702)
//				size.width = 702;
//			table.setMaximumSize(size);
        JTableHeader tableHeader = table.getTableHeader();
        vbox.add(tableHeader);
        vbox.add(table);
        add(vbox);
        add(Box.createHorizontalGlue());
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
        return new HeaderPanel(TextMapper.getText(TextMapper.GUIC_STUDENTS) + " "
                + schoolClass.getName());
    }

//    /**
//     * Invoked when an action occurs.
//     * 
//     * @param e The ActionEvent.
//     */
//    public void actionPerformed(ActionEvent e) {
//        if (e.getSource() instanceof ImageButton) {
//            User u = (User) userDeletebuttons.get(e.getSource());
//            String[] arguments = new String[1];
//            arguments[0] = u.getName();
//            String msg = TextMapper.getText(TextMapper.GUIC_MSG_DELETE_STUDENT);
//            msg = MessageFormat.format(msg, arguments);
//            
//            if (DwoMessageDialog.showConfirmDialog(this, msg
//                    + "?", TextMapper.getText(TextMapper.GUIC_DELETE_STUDENT), DwoMessageDialog.YES_NO_OPTION) == DwoMessageDialog.YES_OPTION) {
//	            u.setInClass(null);
//	            schoolClass.disconnect(u);
//	            tbl.removeRow((ImageButton) e.getSource());
//	            if(tbl.getNrRows() == 0) {
//	                tbl.setVisible(false);
//	                arguments = new String[1];
//	                arguments[0] = schoolClass.getName();
//	                String s = TextMapper.getText(TextMapper.GUIC_NO_STUDENTS);
//	                Label label = new Label(MessageFormat.format(s, arguments));
//	                label.setFont(GuiConstants.SCO_TEXT);
//	                FontMetrics fm = label.getFontMetrics(label.getFont());
//	                label.setSize(fm.stringWidth(label.getText()) + 10, fm.getHeight());
//	                label.setLocation((this.getSize().width/2) - (label.getSize().width/2), 100);
//	                this.add(label);
//	                
//	            }
//            }
//        }
//
//    }
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
        return schoolClass;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (getUserObject() == e.getSource()) {
            if (model != null) {
                model.userList = schoolClass.getStudents();
                model.fireTableDataChanged();
            } else {
                if (schoolClass.getStudents().length > 0) {
                    Box box = (Box) getComponent(0);
                    int last = box.getComponentCount() - 1;
                    Component button = box.getComponent(last);
                    box.removeAll();
                    createJTable(box, schoolClass.getStudents());
                    box.add(button);
                    invalidate();
                }
            }
            repaint();
        }

    }
}
