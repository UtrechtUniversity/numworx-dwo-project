package fi.dwo.dwojapplet.gui;

import java.awt.Component;
import java.text.ParseException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.text.MaskFormatter;

import fi.beans.numworxlf.JFormattedTextField;
import fi.beans.numworxlf.JOptionPane;
import fi.beans.numworxlf.JScrollPane;
import fi.beans.numworxlf.JTextField;
import fi.dwo.commons.persistence.MySQLPersistenceId;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;

@SuppressWarnings("serial")
class AddProfileDwoAdminJPanel extends JPanel {

	private DomDwoProfileFull profile;
	private JTextField nameField;
	private JTextArea textField;
	private JTextField descField;
	private JLabel idField;
	private JTextField rightsField;
	private JFormattedTextField languageField;
	private JTextField titleField;
	private JFormattedTextField baseField;
		
	private AddProfileDwoAdminJPanel(DomDwoProfileFull profile) {
		super(new SpringLayout());
		this.profile = profile;
		
		nameField = new JTextField(profile.getDwoProfileName());
		textField = new JTextArea(profile.getDwoProfileText(), 5, 30);
		descField = new JTextField(profile.getDwoProfileDescription());
		MaskFormatter format;
		try {
			format = new MaskFormatter("LL");
		} catch (ParseException e1) {
			throw new RuntimeException(e1);
		}
		languageField = new JFormattedTextField(format);
		languageField.setValue(profile.getLanguage());
		titleField = new JTextField(profile.getTitle());
		try {
			format = new MaskFormatter("'/L***************************");
		} catch (ParseException e1) {
			throw new RuntimeException(e1);
		}
		baseField = new JFormattedTextField(format);
		baseField.setValue(profile.getBase());
		String id = "-"; 
		try {
			id = String.valueOf(MySQLPersistenceId.getNativeId(profile));
		} catch (Exception e) {
		}
		idField   = new JLabel(id);
		rightsField = new JTextField(profile.getDwoProfileRights());
		
		add( new JLabel("id"));add(idField);
		add( new JLabel("naam")); add(nameField);
		add( new JLabel("titel")); add(descField);
		add( new JLabel("rechten")); add(rightsField);
		add( new JLabel("beschrijving")); add(new JScrollPane(textField));
		add( new JLabel("locale")); add(languageField);
		add( new JLabel("paginatitel")); add(titleField);
		add( new JLabel("URL")); add(baseField);
		
        AddSchoolDialog.makeCompactGrid(this, //parent
                getComponentCount() / 2, 2,
                10, 10, //initX, initY
                10, 10); //xPad, yPad

	}

	DomDwoProfileFull edit() { 
		profile.setDwoProfileDescription(descField.getText());
		profile.setDwoProfileName(nameField.getText());
		profile.setDwoProfileRights(rightsField.getText());
		profile.setDwoProfileText(textField.getText());
		profile.setLanguage(languageField.getText().trim());
		profile.setBase(baseField.getText().trim());
		profile.setTitle(titleField.getText().trim());
		return profile;
	}
	
	static DomDwoProfileFull editDialog(Component parent, DomDwoProfileFull edit) {
		AddProfileDwoAdminJPanel panel = new AddProfileDwoAdminJPanel(edit);
		int ok = JOptionPane.showConfirmDialog(parent, panel, "Edit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if(ok == JOptionPane.OK_OPTION)
			return panel.edit();
		return null;
	}
	
	static DomDwoProfileFull addDialog(Component parent) {
		DomDwoProfileFull edit = new DomDwoProfileFull();
		// set reasonable defaults..
		edit.setDwoProfileRights("_");
		edit.setDwoProfileDescription("");
		edit.setDwoProfileName("");
		edit.setDwoProfileText("");
		edit.setId(null);
		
		AddProfileDwoAdminJPanel panel = new AddProfileDwoAdminJPanel(edit);
		int ok = JOptionPane.showConfirmDialog(parent, panel, "Nieuw", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if(ok == JOptionPane.OK_OPTION)
			return panel.edit();
		return null;
	}
		
}
