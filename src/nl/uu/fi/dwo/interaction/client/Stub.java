package nl.uu.fi.dwo.interaction.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.event.shared.HandlerRegistration;


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
	
	private String isCorrect() {
		return String.valueOf(view.isCorrect());
	}
	
	
	private void setState(String jso) {
		JSONObject js = JSONParser.parseLenient(jso).isObject();
		HashMap<String,Object> result = JSONUtilities.wrapMap(js);
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

		result  = JSONUtilities.wrapMap(JSONParser.parseLenient(launchdata).isObject());
		
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
			setNagekeken: function(arg) {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::view.@nl.uu.fi.dwo.interaction.client.InteractionView::zetNagekeken(Z)(arg);
			},
			getState: function() {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::getState()();
			},
			setState: function(jso) {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::setState(Ljava/lang/String;)(jso);
			},
			isCorrect: function() {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::isCorrect()();
			},
			init: function(width, height, launchdata,values) {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::init(IILjava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(width, height,launchdata,values);
			},
			enter: function() {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::enter()();
			},
			clearAll: function() {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::clearAll()();
			},
			insert: function(string) {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::insert(Ljava/lang/String;)(string);
			},
			cursorToRight: function() {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::cursorToRight()();
			},
			cursorToLeft: function() {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::cursorToLeft()();
			},
			getSelectionString: function() {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::getSelectionString()();
			},
			removeNextElement: function() {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::delete()();
			},
			backspace: function() {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::backspace()();
			},
			
			
		};
		$wnd.inner.stub = o;
		$wnd.publish($wnd.inner, $wnd.outer);
		return $wnd.inner
		
	}-*/;
	
	public static void publish(InteractionStub view) 
	{
		final Stub stub = new Stub(view);
		try { 
			publish0(stub);
		} catch(Exception e) {
			logger.log(Level.SEVERE, "publish", e);
			view.setCommunicationRoot(stub);
		}
	}

	@Override
	public void setChanged(boolean fout) {
		try {
			setChanged0(fout);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "setChanged", e);
		}
	}
	
	private static native void setChanged0(boolean fout) /*-{
		$wnd.setChanged(fout, $wnd.outer);
	}-*/;
	
	@Override
	public FormuleKeyboardIF getKeyboard() {
		return this;
	}

	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		editor = formuleEditor;
		try {
			setFocus0(formuleEditor != null);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "setEditor", e);
		}
	}

	private static native void setFocus0(boolean b)
	/*-{
		$wnd.setFocus(b, $wnd.outer)
	}-*/;
	
	private static native String getBackground0() /*-{
		return $wnd.getBackground($wnd.outer)
	}-*/;
	
	private static native void fireEvent(Object o) /*-{
		$wnd.fireEvent(o, $wnd.outer)
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

	@Override
	public void focus() {		
	}
	
	@Override
	public void softFocus() {		
	}
	
	@Override
	public native int getMode() /*-{
		return $wnd.getMode($wnd.outer);
	}-*/;
	

	@Override
	public native String getLearnerId() /*-{
		return $wnd.getLearnerId($wnd.outer)
	}-*/;

	@Override
	public native String getLearnerName() /*-{
		return $wnd.getLearnerName($wnd.outer)
	}-*/;
	
	@Override
	public CssColor getBackground() {
		return CssColor.make(getBackground0());
	}

	@Override
	public native String getUUID() /*-{
		return $wnd.getUUID($wnd.outer)
	}-*/;

	@Override
	public HandlerRegistration addCBookEventListener(String command,
			CBookEventListener listener) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	@Override
	public void fireEvent(CBookEvent event) {
		// TODO Auto-generated method stub
		
	}

}
