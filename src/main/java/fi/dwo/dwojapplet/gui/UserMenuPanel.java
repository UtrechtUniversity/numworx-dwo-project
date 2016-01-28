// Source file:
// N:\\transferzone\\intern\\Afstudeerders_basw_thijsk\\April\\Implementatie\\fi\\dwo\\client\\gui\\StudentMenuPanel.java
package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.system.TextMapper;
import java.awt.event.ActionEvent;
import javax.swing.Box;
import javax.swing.JButton;

/**
 * This class is the menu panel for a user who logged in. It only adds support 
 * for the profile menubutton.
 *
 * @author M.J.B. Kupers
 *
 */
public class UserMenuPanel extends GuestMenuPanel {

    private JButton myProfileButton;
//    private JButton classManagementButton;
//    private StudentMenuPanelProperties prop;

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
// Simpel User has no classes
//        createGap();
//        /* Add ClassManagement button */
//        classManagementButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_CLASS_MANAGEMENT));
//        classManagementButton.addActionListener(this);
//        this.add(classManagementButton);
     }

    /**
     * Creates a new MenuPanel for the user. It contains the parent items (from
     * GuestMenuPanel) and a button to show the profile for editing.
     */
    public UserMenuPanel() {
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
        //Handig om een lijst van klassen te hebben waar een student de active klas 
        //mee kan schakelen.
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
        if (src == myProfileButton) {
            center.loadCenter(GuiCreator.instance().getProfilePanel());
            center.reset();
            return;
        }
        super.actionPerformed(e);
    }
}
