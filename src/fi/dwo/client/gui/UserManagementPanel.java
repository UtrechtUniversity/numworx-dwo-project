package fi.dwo.client.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Panel;

import javax.swing.JLabel;
import javax.swing.JPanel;

import fi.dwo.client.domain.DwoIF;

public class UserManagementPanel extends JPanel implements CenterSubPanel {

	private CenterPanel center;
	private DwoIF dwo;

	public UserManagementPanel(DwoIF dwo) {
		this.dwo = dwo;
	}

	public void end() {
		// TODO Auto-generated method stub

	}

	public Component getComponent() {
		return this;
	}

	public Container getHeaderPanel() {
		// TODO Auto-generated method stub
		return new HeaderPanel("Gebruikers beheren");
	}

	public void setCenterPanel(CenterPanel centerPanel) {
		center = centerPanel;

	}

}
