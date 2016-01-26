// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\StudentMenuPanel.java
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.dom.entities.DomFullUser;
import fi.dwo.commons.dom.entities.DomSchoolClass;
import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.commons.persistence.RoleType;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.rest.SecureStudentSchoolClassManager;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
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
public class StudentMenuPanel extends GuestMenuPanel {

    private JButton myProfileButton;
    private JButton classManagementButton;
    private StudentMenuPanelProperties prop;
    private JScrollPane classPanel;

    private static final Border TITLE_BORDER = BorderFactory.createEmptyBorder(0, 10, 0, 0);
    private static final Border CLASS_BORDER = BorderFactory.createEmptyBorder(0, 20, 0, 0);
    private ClassLinkedLabel[] classLinkedList;

    public void createRuler() {
        add(Box.createVerticalStrut(10));
        add(new HRuler());
        add(Box.createVerticalStrut(15));
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.GuestMenuPanel#createButtons()
     */
    @Override
    protected void createButtons() {
        super.createButtons();
        createMenuButtons();
        addClassList();
    }

    protected void createMenuButtons() {
        createGap();
        /* Add MainMenu button */
        myProfileButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_MY_PROFILE));
        myProfileButton.addActionListener(this);
        this.add(myProfileButton);

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
     *
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
            DomFullUser t = (DomFullUser) DwoHelper.getCurrentUser();
        if (DwoHelper.getSchoolLogins().getActiveSchoolRoleAndClass().getRoleName().equals(RoleType.STUDENT)) {
            List<DomSchoolClass> scList=null;
            try {
                scList = SecureStudentSchoolClassManager.getStudentsSchoolClasses();
            }
            catch (Dwo2Exception ex) {
                Logger.getLogger(StudentMenuPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if ((scList != null) && (scList.size() > 0)) {
                l = new JLabel(TextMapper.getText(TextMapper.GUIMNU_CLASS_RESULTS)
                        + ":");
                l.setOpaque(false);
                l.setFont(GuiConstants.NORMAL_TEXT);
                l.setBorder(TITLE_BORDER);
                classBox.add(l);

                SchoolClass[] classes = t.getClasses();
                classLinkedList = new ClassLinkedLabel[classes.length];
                ClassLinkedLabel cll;

                for (int i = 0; i < classes.length; i++) {
                    cll = new ClassLinkedLabel(classes[i]);
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
        super.actionPerformed(e);
        Object src = e.getSource();
        GuiCreator instance = GuiCreator.instance();
        if (src == myProfileButton) {
            center.loadCenter(GuiCreator.instance().getProfilePanel());
            center.reset();
        } else if (src == classManagementButton) {
            instance.setWait();
            CenterSubPanel cp = instance.getClassPanel();
            center.reset();
            center.loadCenter(cp);
            instance.setReady();
        }

    }
}
