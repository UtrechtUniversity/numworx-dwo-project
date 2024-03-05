package fi.dwo.dwojapplet.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JTextArea;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fi.beans.numworxlf.JOptionPane;
import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.dwojapplet.domain.School;
import fi.dwo.dwojapplet.gui.GuiCreator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminSchoolDataManager;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolDataFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class SchoolDataAction extends AbstractAction {
	
	JDialog parent;
	DomSchoolDataFull data;
	JTextArea area;
	boolean commit;

	public SchoolDataAction() {
		this("Advanced...");
	}

	public SchoolDataAction(String name) {
		super(name);
		data = new DomSchoolDataFull();
		data.setSchoolData("{}");
		area = new JTextArea(20, 60);
		area.setLineWrap(false);
		area.setWrapStyleWord(true);
	}

	public void setParent(JDialog p) {
		parent = p;
	}
	
	public void setSchool(School s) {
		int id = s.getSchoolID();
		DomSchool dom = new DomSchool();
		dom.setId(PersistentSchool.buildPersistenceId(Long.valueOf(id)));
		try {
			DomSchoolDataFull result = SecureDwoAdminSchoolDataManager.get(dom);
			if (commit) {
				result.setSchoolData(data.getSchoolData());
			}
			data = result;
		} catch (Dwo2Exception e) {
			GuiCreator.instance().ShowErrorDialog(parent, e);		
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		area.setText(data.getSchoolData());
		int ok = JOptionPane.showConfirmDialog(parent, area, "Extra", JOptionPane.OK_CANCEL_OPTION);
		if (ok == JOptionPane.OK_OPTION) {
			// validate
			String tekst = area.getText();
			JSONParser parser  = new JSONParser();
			try {
				parser.parse(tekst);
				commit = true;			
				data.setSchoolData(area.getText());
			} catch (ParseException e) {
				GuiCreator.instance().ShowWarningDialog(parent, e);
			}
		}
	}

	public void commit() {
		if (commit) {
			commit = false;
			try {
				data = SecureDwoAdminSchoolDataManager.update(data);
			} catch (Dwo2Exception e) {
				GuiCreator.instance().ShowErrorDialog(parent, e);
			}
		}
		
	}
}
