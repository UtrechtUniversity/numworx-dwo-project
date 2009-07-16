package fi.dwo.client.gui;

import java.awt.TextField;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

import fi.dwo.client.system.TextMapper;

class GroupItemListener implements ItemListener {
	private JTextField schoolpassword;
	
	/**
	 * @param schoolpassword2
	 */
	GroupItemListener(JPasswordField schoolpassword2) {
		this.schoolpassword = schoolpassword2;
	}

	public void itemStateChanged(ItemEvent e) {
		// van en naar schoolcode, schoolwachtwoord onzichtbaar.
		if(TextMapper.getText(TextMapper.GUIR_OPT_SCHOOLCODE).equals(e.getItem()))
		{
			schoolpassword.setVisible(false);
			schoolpassword.setText("SCHOOLCODE");
		} else {
			if(!schoolpassword.isVisible())
			{	schoolpassword.setText("");
				schoolpassword.setVisible(true);
			}
		}
	}
}
