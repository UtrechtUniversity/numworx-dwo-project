package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.DwoHelper;
import fi.dwo.dwojapplet.gui.panels.JPanelSchoolsandRolesProperties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * This class represents a panel for the current user to change his account.
 *
 * @author M.J.B. Kupers
 *
 */
public class AccountSchoolRolesJPanel extends JPanel {

//    protected User user;
    private JPanelSchoolsandRolesProperties prop = new JPanelSchoolsandRolesProperties();
    private static final Logger LOG = Logger.getLogger(AccountSchoolRolesJPanel.class.getName());

    /**
     * Creates a new ProfilePanel for the current user. The account of the
     * current user can be changed.
     *
     */
    public AccountSchoolRolesJPanel() {
        //fetch user details.
        try {
            prop.init();
        }
        catch (Dwo2Exception e) {
            LOG.log(Level.SEVERE, "Can't retrieve initial user settings.", e);
            GuiCreator.instance().ShowMessageToUser(this, e.getLocalizedCodeExplanation(DwoHelper.getLocale()), "", JOptionPane.ERROR_MESSAGE);
        }

        //init gui
        
        
        
    }

}