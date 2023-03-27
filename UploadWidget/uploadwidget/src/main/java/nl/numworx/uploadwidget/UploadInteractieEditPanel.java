package nl.numworx.uploadwidget;

import java.util.Hashtable;

import javax.inject.Inject;
import javax.swing.JPanel;

import fi.beans.wiskopdrbeans.InteractieEditPanel;

@SuppressWarnings("serial")
public class UploadInteractieEditPanel extends JPanel implements InteractieEditPanel {

	private final Editor editor;

	@Inject UploadInteractieEditPanel(Editor editor) {
		this.editor = editor;
		add(editor);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Hashtable getEditState() {
		Hashtable editstate = editor.getLaunchData();
		return editstate;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setEditState(Hashtable arg0) {
		editor.setLaunchData(arg0);
	}

	@Override
	public void start() {
		editor.start();
	}

	@Override
	public void stop() {
		editor.stop();
	}

	@Override
	public void zetBreedte(int arg0) {
		editor.setInstanceWidth(arg0);
	}

	@Override
	public void zetHoogte(int arg0) {
		editor.setInstanceHeight(arg0);
	}

}
