package nl.numworx.uploadwidget;

import java.util.Hashtable;

import javax.inject.Inject;

import fi.beans.wiskopdrbeans.InteractieEditPanel;

public class UploadInteractieEditPanel implements InteractieEditPanel {

	@Inject UploadInteractieEditPanel() { }
	
	@Override
	public Hashtable getEditState() {
		Hashtable editstate = new Hashtable();
		return editstate;
	}

	@Override
	public void setEditState(Hashtable arg0) {
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public void zetBreedte(int arg0) {
	}

	@Override
	public void zetHoogte(int arg0) {
	}

}
