package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolGroup;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.persistence.DbAccessCreator;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class RightsDialog extends JDialog implements ActionListener {

    private static final Logger log = Logger.getLogger(RightsDialog.class.getName());

    private static final int RIGHTSCOUNT = 3; // a s p
    private static final Object[] HEADERS = new String[]{"naam", "applet", "scorm", "profiel"};
    private static final char[] RIGHTS = new char[]{'a', 's', 'p'};
    private static final String APPLY = "apply";
    private static final String OK = "ok";
    private static final String CANCEL = "cancel";

    JTable table;
    DefaultTableModel model;
    User[] userList;
    int profileID = 1;

    private Boolean hasRight(User u, char right) {
        String rights = u.getRights();
        String id = "[" + profileID + "]";
        int index = rights.indexOf(id);
        if (index < 0) {
            id = "[]";
            index = rights.indexOf(id);
            if (index < 0) {
                id = "";
                index = 0;
            }
        }
        int end = rights.indexOf('[', index + id.length());
        if (end < 0) {
            end = rights.length();
        }
        return rights.substring(index, end).indexOf(right) >= 0;
    }

    private School school;

    public RightsDialog(Frame owner, String title, boolean modal)
            throws HeadlessException {
        super(owner, title, modal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Box vbox, hbox;
        vbox = Box.createVerticalBox();
        model = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                return columnIndex == 0 ? String.class : Boolean.class;
            }

        };
        model.setColumnCount(RIGHTSCOUNT + 1);
        model.setColumnIdentifiers(HEADERS);
        table = new JTable(model);
        // TODO model column class = Boolean voor 1..3
        vbox.add(new JScrollPane(table));
        hbox = Box.createHorizontalBox();

        JButton btn;
        btn = new JButton("Toepassen");
        btn.addActionListener(this);
        btn.setActionCommand(APPLY);
        hbox.add(btn);
        btn = new JButton("OK");
        btn.addActionListener(this);
        btn.setActionCommand(OK);
        hbox.add(btn);
        btn = new JButton("Annuleren");
        btn.addActionListener(this);
        btn.setActionCommand(CANCEL);
        hbox.add(btn);

        vbox.add(hbox);
        JPanel opaque = new JPanel(new BorderLayout()); // v/h box is transparant.
        opaque.add(vbox, BorderLayout.CENTER);
        setContentPane(opaque);
        pack();
    }

    public RightsDialog(SchoolPanel schoolPanel, School sc) {
        this(DwoHelper.getFrameForComponent(schoolPanel), "Rechten voor " + sc.getName(), true);
        setProfileID(GuiCreator.instance().dwo.getDwoProfile().getID());
        setSchool(sc);
    }

    /**
     * @param args
     * @throws PersistenceException
     */
    public static void main(String[] args) throws PersistenceException {
        RightsDialog d = new RightsDialog(null, "Test", false);
        School sc = (School) PersistenceFacade.instance().get(22, School.class);
        d.setSchool(sc);
        d.setVisible(true);
    }

    School getSchool() {
        return school;
    }

    void setSchool(School school) {
        this.school = school;
        SchoolGroup[] groups = school.getSchoolGroupList();
        userList = new User[0];
        for (int i = 0; i < groups.length; i++) {
            SchoolGroup schoolGroup = groups[i];
            try {
                if (schoolGroup.getGroupID() == SchoolGroup.TEACHER
                        || schoolGroup.getGroupID() == SchoolGroup.SCHOOLADMIN) {
                    User[] u = (User[]) PersistenceFacade.instance().get(User.class, schoolGroup);
                    merge(u);
                }
            } catch (PersistenceException e) {
                log.log(Level.SEVERE, null, e);
            }
        }
        Arrays.sort(userList);
        model.setRowCount(userList.length);
        for (int i = 0; i < userList.length; i++) {
            User user = userList[i];
            model.setValueAt(user.getName() + " (" + user.getUsername() + ")", i, 0);
            for (int j = 0; j < RIGHTS.length; j++) {
                model.setValueAt(hasRight(user, RIGHTS[j]), i, j + 1);
            }
        }

        TableUtil.setJTableSizes(table);
    }

    private void merge(User[] u) {
        if (u == null || u.length == 0) {
            return;
        }
        if (userList.length == 0) {
            userList = u;
        } else {
            User[] nu = new User[userList.length + u.length];
            System.arraycopy(u, 0, nu, 0, u.length);
            System.arraycopy(userList, 0, nu, u.length, userList.length);
            userList = nu;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd == APPLY || cmd == OK) {
            for (int i = 0; i < userList.length; i++) {
                User user = userList[i];
                String newrights = "";
                for (int j = 0; j < RIGHTS.length; j++) {
                    if (Boolean.TRUE.equals(table.getValueAt(i, j + 1))) {
                        newrights += RIGHTS[j];
                    }

                }
// naar persistencefacade TODO
                try {
                    user.setRights(
                            DbAccessCreator.instance().setRights(user.getID(), profileID, newrights)
                    );
                } catch (Exception e1) {
                    log.log(Level.SEVERE, null, e1);
                }
            }
        }
        if (cmd == OK || cmd == CANCEL) {
            dispose();
        }

    }

    void setProfileID(int profileID) {
        this.profileID = profileID;
    }

}
