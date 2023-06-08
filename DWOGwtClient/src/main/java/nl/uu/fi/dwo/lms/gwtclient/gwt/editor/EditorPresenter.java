package nl.uu.fi.dwo.lms.gwtclient.gwt.editor;

import javax.inject.Inject;

import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;

public class EditorPresenter {

	@Inject EditorPresenter() {
	}

	public interface Display extends BasicDisplay {
		
	}
	
	@Inject void setView(Display view) {
		
	}
}
