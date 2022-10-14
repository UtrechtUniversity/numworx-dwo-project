package nl.numworx.notebookgwt.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class NotebookGWTDebug extends NotebookGWT implements OpdrNavIF {

	@Override
	public void onModuleLoad() {
		Defaults.setServiceRoot("/");
	    Defaults.setDispatcher(DefaultFilterawareDispatcher.singleton());
    	DefaultFilterawareDispatcher.singleton().addFilter(this);
		RootLayoutPanel.get().add(this);
		Map<String, Object> launchState = new HashMap<>();
		launchState.put(PROJECT, "projectfolder");
		launchState.put(NOTEBOOK, "Untitled3.ipynb");
		Map<String,String> upload = new HashMap<>();
		upload.put("name", "kladje.txt");
		upload.put("type", "text");
		upload.put("content", "dit is kladje.txt");
		launchState.put(UPLOAD, Collections.singletonList(upload));
		Map<String, Number> randomVarWaarden = new HashMap<>();
		init(800, 600, launchState, randomVarWaarden);
		setCommunicationRoot(this);
	
	}

	@Override
	public void setChanged(boolean fout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FormuleKeyboardIF getKeyboard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FormuleClipboardIF getFormuleClipboard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getLearnerId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLearnerName() {
		return "project_wim";
	}

	@Override
	public CssColor getBackground() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUUID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LessonMode getLessonMode() {
		return LessonMode.normal;
	}

	@Override
	public Role getRole() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HandlerRegistration addCBookEventListener(String command, CBookEventListener listener) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fireEvent(CBookEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasListeners(String command) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unpause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ObjectMap getConfiguration() {
		return JSONUtilities.wrapMap(Collections.emptyMap());
	}

	@Override
	public ObjectMap getContext() {
		Map<String,Object> m = Collections.singletonMap("Authorization", "Basic base64");
		return JSONUtilities.wrapMap(m);
	}

}
