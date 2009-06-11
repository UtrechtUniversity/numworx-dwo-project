package fi.dwo.client.gui;

import java.awt.TextField;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import fi.dwo.client.system.TextMapper;

class GroupItemListener implements ItemListener {
	private TextField schoolpassword;
	
	/**
	 * @param schoolpassword
	 */
	GroupItemListener(TextField schoolpassword) {
		this.schoolpassword = schoolpassword;
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
