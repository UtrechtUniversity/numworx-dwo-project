package fi.dwo.dwojapplet.gui;

import fi.dwo.dwojapplet.domain.ContactDocent;
import fi.dwo.dwojapplet.domain.DwoIF;
import fi.dwo.dwojapplet.domain.User;

public class GuiCreatorContactDocent extends GuiCreatorTeacher {

	public GuiCreatorContactDocent(DwoIF dwo) {
		super(dwo);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.gui.GuiCreatorTeacher#getMenuPanel()
	 */

        @Override
	public GuestMenuPanel getMenuPanel() {
        User u = dwo.getUser();

        if (u instanceof ContactDocent) {
            return new ContactDocentMenuPanel(dwo);
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
