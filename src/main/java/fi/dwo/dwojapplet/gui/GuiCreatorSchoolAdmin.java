package fi.dwo.dwojapplet.gui;

import fi.dwo.dwojapplet.domain.SchoolAdmin;
import fi.dwo.dwojapplet.domain.DwoIF;
import fi.dwo.dwojapplet.domain.User;

public class GuiCreatorSchoolAdmin extends GuiCreatorTeacher {

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
        return new UserManagementPanel(dwo);
    }

    @Override
    public CenterSubPanel getClassAdminPanel() {
        return new ClassAdminPanel(dwo);
    }

}
