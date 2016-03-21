// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\TeacherMenuPanel.java
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.exceptions.PersistenceException;
import fi.dwo.commons.system.TextMapper;
import fi.dwo.dwojapplet.domain.Course;
import fi.dwo.dwojapplet.domain.CourseMap;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.domain.SchoolClass;
import fi.dwo.dwojapplet.domain.Sco;
import fi.dwo.dwojapplet.domain.Teacher;
import fi.dwo.dwojapplet.domain.User;
import fi.dwo.dwojapplet.gui.action.TeacherStrategy;
import fi.dwo.dwojapplet.persistence.PersistenceFacade;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

/**
 * Teacher menu panel, adds course-management and overloads class-management functionality
 *
 * original version by M.J.B. Kupers
 *
 */
public class TeacherMenuPanel extends UserMenuPanel implements SelectStrategy {

    private static final Border TITLE_BORDER = BorderFactory.createEmptyBorder(0, 10, 0, 0);
    private static final Border CLASS_BORDER = BorderFactory.createEmptyBorder(0, 20, 0, 0);
    private JButton classManagementButton;

    private JButton courseManagementButton;

    private ClassLinkedLabel[] classLinkedList;

    private JScrollPane classPanel;

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.StudentMenuPanel#createButtons()
     */
    @Override
    protected void createMenuButtons() {
        super.createMenuButtons();
        createGap();
        /* Add ClassManagement button */
        classManagementButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_CLASS_MANAGEMENT));
        classManagementButton.addActionListener(this);
        this.add(classManagementButton);
        /* Als dwo in Deeplink mode, geen coursemanagement */
        if (GuiCreator.instance().getDWO().getCourseViewNr() > 0 || !GuiCreator.instance().getDWO().getUser().hasRight(User.MODIFY_MODULES_RIGHT) || CenterPanel.isIconizer()) {
            return;
        }
        createGap();
        /* Add CourseManagement Button */
        courseManagementButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_COURSE_MANAGEMENT));
        courseManagementButton.addActionListener(this);
        add(courseManagementButton);

    }

    /**
     * Creates a new StudentMenuPanel for the user. It contains the parent items
     * (from StudentMenuPanel) and buttons to show the result of students, and
     * to add a class.
     *
     * @param dwo
     */
    public TeacherMenuPanel() {
        super();

        try {
            School school = (School) PersistenceFacade.instance().get(DwoHelper.getActiveSchoolId(), School.class);
            hasAdminRight = school.hasRight(User.PROFILE_ADMIN_RIGHT);
            schoolID = DwoHelper.getActiveSchoolId();
        }
        catch (PersistenceException ex) {
            Logger.getLogger(TeacherMenuPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Adds the name of the classes wherefrom the user is teacher. Can be
     * overridden by subclasses.
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
        if (DwoHelper.getCurrentFacadeUser() instanceof Teacher) {
            Teacher t = (Teacher) DwoHelper.getCurrentFacadeUser();
            if ((t.getClasses() != null) && (t.getClasses().length != 0)) {
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
        if (!isEnabled()) {
            return;
        }

        Object src = e.getSource();
        GuiCreator instance = GuiCreator.instance();
        if (src == mainMenuButton) {
            instance.getMainPanel().getCenter().select(ModuleTreePanel.ALLE_MODULES);
            return;
        }
        if (src == classManagementButton) {
            instance.setWait();
            CenterSubPanel cp = instance.getClassPanel();
            center.reset();
            center.loadCenter(cp);
            instance.setReady();
        }

        if (src instanceof ClassLinkedLabel) {
            instance.setWait();
            CenterSubPanel cp = instance.getResultPanel(((ClassLinkedLabel) src).getSchoolClass());
            center.reset();
            center.loadCenter(cp);
            instance.setReady();
            return;
        } else if (src == courseManagementButton) {
            instance.setWait();
            CenterSubPanel cp = instance.getCourseManagementPanel();
            center.loadCenter(cp);
            center.setStrategy(this);
            instance.setReady();
            return;
        }
        super.actionPerformed(e);
    }

    @Override
    public void hideClassList() {
        if (isEnabled()) {
            classPanel.setVisible(false);
        }
    }

    @Override
    public void showClassList() {
        if (isEnabled()) {
            classPanel.setVisible(true);
        }
    }

    @Override
    public void nodeSelected(CourseMap node) {
        Object u = node.getUserObject();
        GuiCreator instance = GuiCreator.instance();
        if (u instanceof String) {
            if (u == ModuleTreePanel.ALLE_MODULES) {
                return;
            }
            if (u == ModuleTreePanel.STANDAARD_DWO_MODULES && !hasAdminRight) {
                return;
            }
            CenterSubPanel cp = new CourseManagementPanel(node);
            center.loadCenter(cp);
        } else if (u instanceof Course) {
            Course c = (Course) u;
            if (c.getSchoolID() != 0 || hasAdminRight) // allowed?
            {
                if (c.isWithChildren()) {
                    center.loadCenter(instance.getCourseManagementPanel(c));
                } else {
                    center.loadCenter(instance.getScoManagementPanel(c));
                }
            }
        } else if (u instanceof Sco) {
            Sco s = (Sco) u;
            Course c = s.getCourse();
            if (c.getSchoolID() != 0 || hasAdminRight) // allowed?
            {
                instance.loadParameterManagementPanel(s);
            }
        }
    }

    private boolean hasAdminRight;
    private int schoolID;
    TeacherStrategy delegate = new TeacherStrategy();

    @Override
    public JPopupMenu nodeAction(CourseMap node) {
        return delegate.nodeAction(node);

    }

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.GuestMenuPanel#setEditing(boolean)
     */
    @Override
    public void setEditing(boolean b) {
        boolean enabled = !b;
        setEnabled(enabled);
        for (int i = 0; i < getComponentCount(); i++) {
            getComponent(i).setEnabled(enabled);
        }
        classPanel.setVisible(!b);
        //repaint();

    }

}
