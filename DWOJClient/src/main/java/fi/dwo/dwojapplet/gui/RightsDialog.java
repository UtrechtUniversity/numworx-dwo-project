package fi.dwo.dwojapplet.gui;

import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool4DwoAdmin;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolAdminAndHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacherAndHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import fi.beans.numworxlf.JButton;
import fi.beans.numworxlf.JScrollPane;
import fi.dwo.dwojapplet.domain.DWO;
import fi.dwo.dwojapplet.domain.DwoHelper;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminSchoolManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class RightsDialog extends JDialog implements ActionListener {

    private static final Logger LOG = Logger.getLogger(RightsDialog.class.getName());

    private static final int RIGHTSCOUNT = 3; // a s p
    private static final Object[] HEADERS = new String[]{"naam", "applet", "scorm", "profiel"};
    private static final char[] RIGHTS = new char[]{'a', 's', 'p'};
    private static final String APPLY = "apply";
    private static final String OK = "ok";
    private static final String CANCEL = "cancel";

    JTable table;
    DefaultTableModel model;
    List<DomTeacherAndHasRole> theListT;
    List<DomSchoolAdminAndHasRole> theListA;
    LinkedHashMap<PersistenceId, DomUser> theMap = new LinkedHashMap<PersistenceId, DomUser>();
    Map<PersistenceId, DomHasRole> rolesT, rolesA;
 
    int profileID = 1;

    private Boolean hasRight(DomHasRole hr, char right) {
        String rights = hr.getRights();
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

    private DomSchool4DwoAdmin school;

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

    public RightsDialog(Component owner, DomSchool4DwoAdmin sc) throws Dwo2Exception {
        this(DwoHelper.getFrameForComponent(owner), "Rechten voor " + sc.getSchoolName(), true);
        setProfileID(DWO.getDwoProfileID());
        setSchool(sc);
    }

    public RightsDialog(DomSchool4DwoAdmin sc) throws Dwo2Exception {
        this(DwoHelper.getFrameForComponent(GuiCreator.instance().getMainPanel()), "Rechten voor " + sc.getSchoolName(), true);
        setProfileID(DWO.getDwoProfileID());
        setSchool(sc);
    }
//
//    /**
//     * @param args
//     * @throws PersistenceException
//     */
//    public static void main(String[] args) throws PersistenceException {
//        RightsDialog d = new RightsDialog(null, "Test", false);
//        School sc = (School) PersistenceFacade.instance().get(22, School.class);
//        d.setSchool(sc);
//        d.setVisible(true);
//    }

    DomSchool4DwoAdmin getSchool() {
        return school;
    }

    void setSchool(DomSchool4DwoAdmin school) throws Dwo2Exception {
        this.school = school;
        theListT = SecureDwoAdminSchoolManager.getTeachersAndHasRoleInSchool(school);
        theListA = SecureDwoAdminSchoolManager.getSchoolAdminsAndHasRoleInSchool(school);
        theMap.clear();
        theListT.forEach(dom -> theMap.put(dom.getTeacher().getId(), 	 dom.getTeacher()));
        theListA.forEach(dom -> theMap.put(dom.getSchoolAdmin().getId(), dom.getSchoolAdmin()));
        rolesA = theListA.stream().collect(Collectors.toMap(d -> d.getHasRole().getUserId(), DomSchoolAdminAndHasRole::getHasRole));
        rolesT = theListT.stream().collect(Collectors.toMap(d -> d.getHasRole().getUserId(), DomTeacherAndHasRole::getHasRole));
        
        model.setRowCount(theMap.size());
        //#LMS-165  insert role into school
        Iterator<Entry<PersistenceId,DomUser>> iterator = theMap.entrySet().iterator();
        for (int i = 0; i < theMap.size(); i++) {
        	Entry<PersistenceId, DomUser> entry = iterator.next();
        	PersistenceId id  = entry.getKey();
        	DomUser user = entry.getValue();
            model.setValueAt(user.getDisplayName() + " (" + user.getUserName() + ")", i, 0);
            DomHasRole role = rolesT.get(id);
            if (role == null) role = rolesA.get(id);
            for (int j = 0; j < RIGHTS.length; j++) {
                model.setValueAt(hasRight(role, RIGHTS[j]), i, j + 1);
            }
        }

        TableUtil.setJTableSizes(table);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd == APPLY || cmd == OK) {
            try {
               Iterator<Entry<PersistenceId,DomUser>> iterator = theMap.entrySet().iterator();
               for (int i = 0; i < theMap.size(); i++) {
            	   Entry<PersistenceId, DomUser> entry = iterator.next();
            	   PersistenceId id  = entry.getKey();
                    String newrights = "";
                    for (int j = 0; j < RIGHTS.length; j++) {
                        if (Boolean.TRUE.equals(table.getValueAt(i, j + 1))) {
                            newrights += RIGHTS[j];
                        }

                    }
                    DomHasRole role = rolesT.get(id);
                    if (role != null)
                    	updateRoleRights(role, newrights);
                    role = rolesA.get(id);
                    if (role != null)
                    	updateRoleRights(role, newrights);
                }
            }
            catch (Dwo2Exception ex) {
                LOG.log(Level.SEVERE, "", ex);
                GuiCreator.instance().ShowErrorDialog(rootPane, ex);
            }
        }
        if (cmd == OK || cmd == CANCEL) {
            dispose();
        }

    }

	private void updateRoleRights(DomHasRole role, String newrights) throws Dwo2Exception {
		String oldrights = role.getRights();
		String pstr = "[" + profileID + "]";
		int start = oldrights.indexOf(pstr);
		if (start < 0) {
		    oldrights = oldrights + pstr;
		    start = oldrights.length();
		} else {
		    start += pstr.length();
		}
		int end = oldrights.indexOf("[", start);
		if (end < 0) {
		    end = oldrights.length();
		}
		String rights = oldrights.substring(0, start) + newrights + oldrights.substring(end);

		role.setRights(rights);
		SecureDwoAdminSchoolManager.updateHasRoleRights(role);
	}

    void setProfileID(int profileID) {
        this.profileID = profileID;
    }

}
