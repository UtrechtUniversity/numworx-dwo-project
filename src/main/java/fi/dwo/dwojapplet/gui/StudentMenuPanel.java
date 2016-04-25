// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\StudentMenuPanel.java
package fi.dwo.dwojapplet.gui;

import fi.dwo.rest.dom.entities.DomUserFull;
import fi.dwo.rest.dom.entities.DomSchoolClass;
import fi.dwo.rest.exceptions.Dwo2Exception;
import fi.dwo.rest.dom.entities.RoleType;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.rest.SecureStudentSchoolClassManager;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

/**
 * This class is the menu panel for the user who logged in.
 *
 * @author M.J.B. Kupers
 *
 */
public class StudentMenuPanel extends UserMenuPanel {

//    private JButton myProfileButton;
    private JButton classManagementButton;
    private StudentMenuPanelProperties prop;
    private JScrollPane classPanel;

    private static final Border TITLE_BORDER = BorderFactory.createEmptyBorder(0, 10, 0, 0);
    private static final Border CLASS_BORDER = BorderFactory.createEmptyBorder(0, 20, 0, 0);
    private DomSchoolClassLinkedLabel[] classLinkedList;

    @Override
    public void createRuler() {
        add(Box.createVerticalStrut(10));
        add(new HRuler());
        add(Box.createVerticalStrut(15));
    }

    @Override
    protected void createMenuButtons() {
        super.createMenuButtons();
        createGap();
        /* Add ClassManagement button */
        classManagementButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_CLASS_MANAGEMENT));
        classManagementButton.addActionListener(this);
        this.add(classManagementButton);
    }

    /**
     * Creates a new MenuPanel for the user. It contains the parent items (from
     * GuestMenuPanel) and a button to show the profile for editing.
     */
    public StudentMenuPanel() {
        super();
//        setDebugGraphicsOptions(DebugGraphics.FLASH_OPTION);

    }

//    public StudentMenuPanel(DwoIF dwo) {
//        super(dwo);
//    }
    /**
     * Adds the name of the class of the user to the panel. Can be overridden by
     * subclasses.
     */
    protected void addClassList() {
        /* Variables used to create items */
        FontMetrics fm;
        JLabel l;

        createRuler();
        Box classBox = Box.createVerticalBox();
        classPanel = new JScrollPane(classBox) {
            @Override
            public void setVisible(boolean b) {

                super.setVisible(b);

            }
        };
        classPanel.setDoubleBuffered(false);
        classPanel.setOpaque(false);
        classPanel.getViewport().setOpaque(false);
        classPanel.setViewportBorder(null);
        classPanel.setBorder(null);
        /* Add class-info */
        DomUserFull t = (DomUserFull) DwoHelper.getCurrentUser();
        if (DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass()!=null && DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getRoleName().equals(RoleType.STUDENT.name())) {
            List<DomSchoolClass> scList = null;
            try {
                scList = SecureStudentSchoolClassManager.getStudentsSchoolClasses();
            }
            catch (Dwo2Exception ex) {
                Logger.getLogger(StudentMenuPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

            if ((scList != null) && (scList.size() > 0)) {
                l = new JLabel(TextMapper.getText(TextMapper.GUIMNU_STUDENT_IN_CLASS)
                        + ":");
                l.setOpaque(false);
                l.setFont(GuiConstants.NORMAL_TEXT);
                l.setBorder(TITLE_BORDER);
                classBox.add(l);

                classLinkedList = new DomSchoolClassLinkedLabel[scList.size()];
                DomSchoolClassLinkedLabel cll;

                for (int i = 0; i < scList.size(); i++) {
                    cll = new DomSchoolClassLinkedLabel(scList.get(i));
                    cll.setBorder(CLASS_BORDER);
                    cll.addActionListener(this);
                    classLinkedList[i] = cll;
                    cll.setFont(GuiConstants.NORMAL_TEXT);
                    classBox.add(cll);
                }
            }
            classPanel.setVisible(false);
            this.add(classPanel);
            classPanel.setVisible(true);
        }
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e The ActionEvent.
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        GuiCreator instance = GuiCreator.instance();
////        } else 
        if (src == classManagementButton) {
            instance.setWait();
            CenterSubPanel cp = instance.getClassPanel();
            center.reset();
            center.loadCenter(cp);
            instance.setReady();
            return;
        }
        super.actionPerformed(e);
    }
}
