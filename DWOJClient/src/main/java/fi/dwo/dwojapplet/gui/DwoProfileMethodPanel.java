package fi.dwo.dwojapplet.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JPanel;
import javax.swing.JTable;

import fi.dwo.dwojapplet.gui.DwoProfileModelPanel.Model;
import nl.uu.fi.dwo.lms.jclient.lib.rest.managers.SecureDwoAdminMethodManager;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomId;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class DwoProfileMethodPanel extends JPanel {
	
	final DomDwoProfileId profile;

	public DwoProfileMethodPanel(DomDwoProfile p) {
		super();
		profile = p;
		try {
			methods = SecureDwoAdminMethodManager.getList(null);
			enabled = new Boolean[methods.size()];
			List<DomMethod> selected = SecureDwoAdminMethodManager.getList(p);
			List<?> ids = selected.stream().map(DomId::getId).collect(Collectors.toList());
			for (int i = 0; i < enabled.length; i++) {
				enabled[i] = ids.contains(methods.get(i).getId());
			}
			
		} catch (Exception oops) {
			
		}
		JTable tabel = new JTable(new Model<DomMethod>(methods, enabled, DomMethod::getMethod ));
		add(tabel);
	}
	
	List<DomMethod> methods = new ArrayList<>();
	Boolean[] enabled = new Boolean[0];
	
	public void commit() {
		for(int i = 0; i < enabled.length; i++) {
			DomMethod model = methods.get(i);
			if (enabled[i]) {
				System.out.println("enable " + i);
				try {
					SecureDwoAdminMethodManager.addProfile(model, profile);
				} catch (Dwo2Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("disable " + i);
				try {
					SecureDwoAdminMethodManager.removeProfile(model, profile);
				} catch (Dwo2Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}


}
