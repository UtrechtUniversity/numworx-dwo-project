package fi.dwo.client.gui;

import fi.dwo.client.domain.ContactDocent;
import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.Teacher;
import fi.dwo.client.domain.User;

public class GuiCreatorContactDocent extends GuiCreatorTeacher {

	public GuiCreatorContactDocent(DwoIF dwo) {
		super(dwo);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.gui.GuiCreatorTeacher#getMenuPanel()
	 */

	public GuestMenuPanel getMenuPanel() {
        User u = dwo.getUser();

        if (u instanceof ContactDocent) {
            return new ContactDocentMenuPanel();
        }
		return super.getMenuPanel();
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.gui.GuiCreator#getUserManagementPanel()
	 */
	public CenterSubPanel getUserManagementPanel() {
		// TODO Auto-generated method stub
		return new UserManagementPanel(dwo);
	}

}
