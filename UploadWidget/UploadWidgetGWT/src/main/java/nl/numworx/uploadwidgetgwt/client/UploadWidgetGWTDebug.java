package nl.numworx.uploadwidgetgwt.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

import gwtupload.client.IFileInput.FileInputType;
import nl.numworx.uploadwidgetgwt.shared.Constants;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.keyboard.EnterType;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;

public class UploadWidgetGWTDebug extends UploadWidgetGWT {
	private class MockOpdrNav implements OpdrNavIF, FormuleKeyboardIF, FormuleClipboardIF {

		private FormuleEditorIF editor;

		@Override
		public void setChanged(boolean fout) {

		}

		public ObjectMap getContext() {
		  HashMap<String,Object> context = new HashMap<>();
		  context.put("premium", Boolean.TRUE);
		  context.put(Constants.AUTHORIZATION, "Bearer dummybear");
		  context.put("registration", "f7b1437e-5100-11ec-bf63-0242ac130002");
		  return JSONUtilities.wrapMap(context);
		}
		
		@Override
		public FormuleKeyboardIF getKeyboard() {
			return this;
		}

		@Override
		public FormuleClipboardIF getFormuleClipboard() {
			return this;
		}

		@Override
		public int getMode() {
			return 0;
		}

		@Override
		public String getLearnerId() {
			return "1-0000000000-00000000000";
		}

		@Override
		public String getLearnerName() {
			return "student";
		}

		@Override
		public CssColor getBackground() {
			return CssColor.make("white");
		}

		@Override
		public String getUUID() {
			return "0-1-0";
		}

		@Override
		public LessonMode getLessonMode() {
			return LessonMode.normal;
		}

		@Override
		public Role getRole() {
			return Role.Learner;
		}

		@Override
		public HandlerRegistration addCBookEventListener(String command, CBookEventListener listener) {
			return null;
		}

		@Override
		public void fireEvent(CBookEvent event) {

		}

		@Override
		public boolean hasListeners(String command) {
			return false;
		}

		@Override
		public void pause() {

		}

		@Override
		public void unpause() {

		}

		@Override
		public ObjectMap getConfiguration() {
			return JSONUtilities.wrapMap(Collections.emptyMap());
		}

		@Override
		public void setEditor(FormuleEditorIF formuleEditor) {
			this.editor = formuleEditor;
		}

		@Override
		public void backspace() {
			if(editor != null)
				editor.removeCurrentElement();
		}

		@Override
		public void delete() {
			if(editor != null)
				editor.removeNextElement();
			
		}

		@Override
		public void enter() {
			if(editor != null)
				editor.enter();
		}

		@Override
		public void focus() {
			FocusOnTouch.focus();
		}

		@Override
		public FormuleEditorIF getEditor() {
			return editor;
		}

		@Override
		public void softFocus() {
			FocusOnTouch.focus();
		}

		@Override
		public void blur() {
			editor = null;
		}

		@Override
		public void functionKey(int minF) {
			
		}

		@Override
		public void setEnterType(EnterType type) {
			
		}

		@Override
		public String getClipboard() {
			return "";
		}

		@Override
		public void setClipboard(String formule) {
			
		}

	}

	@Override
	public void onModuleLoad() {
		int width = 300;
		int height = 100;
		
		Map<String, Object> launchdata = new HashMap<>();
		launchdata.put(Constants.FILE_INPUT_TYPE, FileInputType.BROWSER_INPUT.name());
		launchdata.put(Constants.MEDIATYPES, "gif, png, jpg, jpeg");
		launchdata.put(Constants.ITEMS_MAX, 2);
		launchdata.put(Constants.SCORE_MAX, 10);
		launchdata.put(Constants.AUTO_SUBMIT, true);
		Map<String,String> file = Collections.singletonMap("name", "aap.txt");
		List<Map<String,String>> fileinputmodel = Collections.singletonList(file);
		launchdata.put(Constants.FILE_INPUT_MODEL, fileinputmodel);
		
		init(width, height, launchdata, Collections.emptyMap());
		MockOpdrNav opdrnav = new MockOpdrNav();
		FocusOnTouch.installKeyboard(opdrnav, opdrnav);
		FocusOnTouch.focus();
		setCommunicationRoot(opdrnav);
		
		Widget w = asWidget();
		
		RootLayoutPanel root = RootLayoutPanel.get();
		root.add(w);
		root.setWidgetTopHeight(w, 0, Unit.PX, height, Unit.PX);
		root.setWidgetLeftWidth(w, 0, Unit.PX, width, Unit.PX);
		
	}

}
