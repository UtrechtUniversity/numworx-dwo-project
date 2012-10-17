package fi.dwo.client.gui;

import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.table.AbstractTableModel;

import fi.dwo.client.domain.DwoIF;
import fi.dwo.client.domain.User;
import fi.dwo.client.gui.GuestMenuPanel.MenuPanelButton;
import fi.dwo.client.gui.action.SchoolConfigAction;
import fi.dwo.client.system.TextMapper;

public class ContactDocentMenuPanel extends TeacherMenuPanel {

	protected MenuPanelButton userManagementButton;
	protected MenuPanelButton klasKeuzeButton;

	
	public ContactDocentMenuPanel(DwoIF dwo) {
		super(dwo);
	}

	/* (non-Javadoc)
	 * @see fi.dwo.client.gui.TeacherMenuPanel#createMenuButtons()
	 */
	protected void createMenuButtons() {
		super.createMenuButtons();
		add(Box.createVerticalStrut(20));
		add(new JLabel(TextMapper.getText(TextMapper.GUIMNU_FEATURES_SCHOOLADMIN)));
		createGap();
		this.userManagementButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_USERS_SCHOOL));
		userManagementButton.addActionListener(this);
		add(userManagementButton);
		createGap();
		klasKeuzeButton = new MenuPanelButton(TextMapper.getText(TextMapper.GUIMNU_CLASSES_SCHOOL));
		klasKeuzeButton.addActionListener(this);
		add(klasKeuzeButton);
        
        JButton schoolsetup = new MenuPanelButton( new SchoolConfigAction() );
        createGap();add(schoolsetup);


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
		} else if(e.getSource() == klasKeuzeButton)
		{
			GuiCreator.instance().setWait();
	        CenterSubPanel cp = GuiCreator.instance().getClassAdminPanel();
	        center.loadCenter(cp);
	        GuiCreator.instance().setReady();
		}

	}

}
