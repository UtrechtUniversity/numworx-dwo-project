package nl.uu.fi.dwo.interaction.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;


public class Stub implements OpdrNavIF, FormuleKeyboardIF {
	
	private InteractionStub view;
	private static FormuleEditorIF editor;
	private static Logger logger = Logger.getLogger("Stub");
	private Stub(InteractionStub view) {
		this.view = view;
	}
	
	private String getState() {
		Map<String,Object> map = view.getState();
		return JSONUtilities.toJSONObject(map).toString();
	}
	
	private void setState(String jso) {
		JSONObject js = JSONParser.parseLenient(jso).isObject();
		HashMap<String,Object> result = JSONUtilities.fromJSONObject(js);
		view.setState(result);
	}
	
	private void init(int width, int height, String launchdata, JavaScriptObject randomValues) {
		Map<String, Number> numbers = new HashMap<String,Number>();
		Map<String,Object> result;

		Set<String> keys;
		JSONObject values;
		
		values = new JSONObject(randomValues);
		keys = values.keySet();
		for(String key: keys) {
			numbers.put(key, values.get(key).isNumber().doubleValue());
		}

		result  = JSONUtilities.fromJSONObject(JSONParser.parseLenient(launchdata).isObject());
		
		try {
			view.init(width, height, result, numbers);
			view.setCommunicationRoot(this);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "init: " + e.getMessage(), e);
		}
	}
	
	
	private static native Object publish0(Object o) /*-{
		$wnd.inner = {
			getScore: function() {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::view.@nl.uu.fi.dwo.interaction.client.InteractionView::getScore()();
			},
			getState: function() {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::getState()();
			},
			setState: function(jso) {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::setState(Ljava/lang/String;)(jso);
			},
			isCorrect: function() {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::view.@nl.uu.fi.dwo.interaction.client.InteractionView::isCorrect()();
			},
			init: function(width, height, launchdata,values) {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::init(IILjava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(width, height,launchdata,values);
			},
			enter: function() {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::enter()();
			},
			clearAll: function() {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::clearAll()();
			}
			insert: function(string) {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::insert(Ljava/lang/String;)(string);
			}
			
		};
		$wnd.inner.stub = o;
		$wnd.publish($wnd.inner, $wnd.outer);
		return $wnd.inner
		
	}-*/;
	
	public static void publish(InteractionStub view) 
	{
		try { 
			publish0(new Stub(view));
		} catch(Exception e) {
			logger.log(Level.SEVERE, "publish", e);
		}
	}

	@Override
	public void setChanged() {
		setChanged0();
	}
	
	private static native void setChanged0() /*-{
		$wnd.setChanged($wnd.outer);
	}-*/;

	@Override
	public FormuleKeyboardIF getKeyboard() {
		return this;
	}

	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		editor = formuleEditor;
		setFocus0(formuleEditor != null);
	}

	private static native void setFocus0(boolean b)
	/*-{
		$wnd.setFocus(b, $wnd.outer)
	}-*/;
	
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
	public FormuleEditorIF getEditor() {
		return editor;
	}
	
	public void clearAll() {
		if (editor != null) 
			editor.clearAll();
	}

	public void setCurrentElementRepaint() {
		if (editor != null) 
		editor.setCurrentElementRepaint();
	}

	public void cursorToLeft() {
		if (editor != null) 
		editor.cursorToLeft();
	}

	public void cursorToRight() {
		if (editor != null) 
		editor.cursorToRight();
	}

	public String getSelectionString() {
		if (editor != null) 
			return editor.getSelectionString();
		return "";
	}

	public void insert(String text) {
		editor.insert(text);
	}

	public void insert(char charAt) {
		editor.insert(charAt);
	}

}
