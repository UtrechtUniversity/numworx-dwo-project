package fi.dwo.dwojapplet.gui;

import fi.dwo.commons.exceptions.Dwo2Exception;
import fi.dwo.dwojapplet.domain.SchoolAdmin;
import fi.dwo.dwojapplet.domain.DwoIF;
import fi.dwo.dwojapplet.domain.User;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GuiCreatorSchoolAdmin extends GuiCreatorTeacher {
    private static final Logger LOG = Logger.getLogger(GuiCreatorSchoolAdmin.class.getName());

    public GuiCreatorSchoolAdmin(DwoIF dwo) {
        super(dwo);
        
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.GuiCreatorTeacher#getMenuPanel()
     */
    @Override
    public GuestMenuPanel getMenuPanel() {
        User u = dwo.getUser();

        if (u instanceof SchoolAdmin) {
            return new SchoolAdminMenuPanel();
        }
        return super.getMenuPanel();
    }

    /* (non-Javadoc)
     * @see fi.dwo.client.gui.GuiCreator#getUserManagementPanel()
     */
    @Override
    public CenterSubPanel getUserManagementPanel() {
        //return new UserManagementPanel(dwo);
        CenterSubPanel csp=null;
        try {
            csp = new UsersInSchoolSchoolAdminPanel();
        }
        catch (Dwo2Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            GuiCreator.instance().ShowErrorDialog(mainPanel, ex);
        }
        return csp;
    }

    @Override
    public CenterSubPanel getClassAdminPanel() {
        return new ClassAdminPanel(dwo);
    }

}
