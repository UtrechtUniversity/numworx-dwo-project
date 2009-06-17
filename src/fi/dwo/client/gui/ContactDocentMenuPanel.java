package fi.dwo.client.gui;

import java.awt.event.ActionEvent;

import fi.dwo.client.gui.GuestMenuPanel.MenuPanelButton;

public class ContactDocentMenuPanel extends TeacherMenuPanel {

	protected MenuPanelButton userManagementButton;

	public ContactDocentMenuPanel() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.gui.TeacherMenuPanel#createMenuButtons()
	 */
	protected void createMenuButtons() {
		super.createMenuButtons();
		
		this.userManagementButton = new MenuPanelButton("Gebruikers");
		userManagementButton.addActionListener(this);
		add(userManagementButton);
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.gui.TeacherMenuPanel#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		super.actionPerformed(e);
		if(e.getSource() == userManagementButton) 
		{    
			GuiCreator.instance().setWait();
	        CenterSubPanel cp = GuiCreator.instance().getUserManagementPanel();
	        center.loadCenter(cp);
	        GuiCreator.instance().setReady();
		}

	}

}
