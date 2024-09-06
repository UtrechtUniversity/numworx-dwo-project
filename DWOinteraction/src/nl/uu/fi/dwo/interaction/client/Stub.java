package nl.uu.fi.dwo.interaction.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.keyboard.EnterType;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class Stub implements OpdrNavIF, FormuleKeyboardIF, NativePreviewHandler {
	
	private InteractionStub view;
	private static FormuleEditorIF editor;
	private static boolean soft = true;
	private static Logger logger = Logger.getLogger("Stub");
	private Stub(InteractionStub view) {
		this.view = view;
		com.google.gwt.user.client.Event.addNativePreviewHandler(this);	}
	
	private String getState() {
		Map<String,Object> map = view.getState();
		return JSONUtilities.toJSONObject(map).toString();
	}
	
	private String isCorrect() {
		return String.valueOf(view.isCorrect());
	}
	
	@SuppressWarnings("deprecation")
    private void setState(String jso) {
		JSONObject js = JSONParser.parseLenient(jso).isObject();
		HashMap<String,Object> result = JSONUtilities.wrapMap(js);
		view.setState(result);
	}
	
	@SuppressWarnings("deprecation")
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
			stub: o,
			getScore: function() {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::view.@nl.uu.fi.dwo.interaction.client.InteractionView::getScore()();
			},
			setNagekeken: function(arg) {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::view.@nl.uu.fi.dwo.interaction.client.InteractionView::zetNagekeken(Z)(arg);
			},
			
			kijkNa: function() {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::view.@nl.uu.fi.dwo.interaction.client.InteractionView::kijkNa()();
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
			
			knip: function() {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::knip()();
			},
			kopieer: function() {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::kopieer()();
			},
			
			selectAll: function() {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::selectAll()();
			},
			
			cursorToRight: function() {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::cursorToRight()();
			},
			cursorToLeft: function() {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::cursorToLeft()();
			},
			cursorUp: function() {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::cursorUp()();
			},
			cursorDown: function() {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::cursorDown()();
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

			tab: function() {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::tab()();
			},
			shiftTab: function() {
				this.stub.@nl.uu.fi.dwo.interaction.client.Stub::shiftTab()();
			},
			editorToString: function() {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::editorToString()();
			},
			
			getConstantWidth: function() {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::getConstantWidth()();
			},

			getConstantHeight: function() {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::getConstantHeight()();
			},

			getWidth: function() {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::getWidth()();
			},

			getHeight: function() {
				return this.stub.@nl.uu.fi.dwo.interaction.client.Stub::getHeight()();
			},
			
			
		};
		$wnd.inner.stub = o;
		
		function doPublish() {
			if(!(typeof $wnd.publish === "undefined") && $wnd.publish)
				$wnd.publish($wnd.inner, $wnd.outer)
			else 
			{	console.log("window.publish not defined, waiting...");
				//setTimeout("doPublish()",50)
			}
		}		
		//$wnd.publish($wnd.inner, $wnd.outer);
		doPublish()
		
		return $wnd.inner
		
	}-*/;
	
	public static void publish(InteractionStub view) 
	{
		final Stub stub = new Stub(view);
		try { 
			publish0(stub);
		} catch(Exception e) {
			logger.log(Level.WARNING, "publish", e);
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
	
	private static native void tickle() /*-{
		$wnd.tickle()
	}-*/;
	
	@Override public native void setVisited()
	/*-{
		$wnd.setVisited($wnd.outer)
	}-*/
	; 
	
	
	@Override
	public FormuleKeyboardIF getKeyboard() {
		return this;
	}

	@Override
	public void setEditor(FormuleEditorIF formuleEditor) {
		editor = formuleEditor;
		try {
			setFocus2(formuleEditor != null, soft);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "setEditor", e);
		}
	}

	private static native void setFocus2(boolean b, boolean soft) 
	/*-{
	 	$wnd.setFocus2(b,soft, $wnd.outer)
	}-*/;
	
	private static native void setFocus0(boolean b)
	/*-{
		$wnd.setFocus(b, $wnd.outer)
	}-*/;
	
	private static native String getBackground0() /*-{
		return $wnd.getBackground($wnd.outer)
	}-*/;
	
	private static native void fireEvent0(String string) /*-{
		$wnd.fireEvent(string, $wnd.outer)
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
	
	private String editorToString() {
		return getEditor().toString();
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

	void cursorUp() {
		if (editor != null)
			editor.cursorUp();
	}
	void cursorDown() {
		if (editor != null)
			editor.cursorDown();
	}
	
	void selectAll() {
		if (editor != null)
			editor.selectAll();
	}
	
	public String getSelectionString() {
		if (editor != null) 
			return editor.getSelectionString();
		return "";
	}

	public void insert(String text) {
		if("$m@".equals(text)) editor.macht();
		else if ("$w@".equals(text)) editor.wortel();
		else if ("$b$n@@".equals(text)) editor.breuk();
		else if ("$m2@".equals(text)) editor.kwadraat();
		else if ("$W$n@@".equals(text)) editor.ndewortel();
		else if ("$h@".equals(text)) editor.haakjes();
		else if ("$i$n$k$l@@@@".equals(text)) editor.integraal();
		else if ("$q$n$k$l@@@@".equals(text)) editor.prv();
		else if ("$L$n@@".equals(text)) editor.ndelog();
		else if ("$r@".equals(text)) editor.abs();
		else if ("$s@".equals(text)) editor.subscript();
		else if ("$y$n@@".equals(text)) editor.bin();
		else if ("$d$n@@".equals(text)) editor.diff();
		else if ("$D$n@@".equals(text)) editor.diff_partial();
		else if ("$T$n$k$l@@@@".equals(text)) editor.limiet0();
		else if ("$T$n$k$l@@@@".equals(text)) editor.limiet1();
		else if ("$T$n$k$l@@@@".equals(text)) editor.limiet2();
		else if ("$P$n@@".equals(text)) editor.primitieve();
		else if ("$c@".equals(text)) editor.conjug();
		else if ("$S$n$k$l@@@@".equals(text)) editor.sigma();
		else if ("$Q@".equals(text)) editor.stelsel();
		else if ("$z@".equals(text)) editor.vectornotatie();
		else if ("$Y@".equals(text)) editor.vector();
		else if ("$M@".equals(text)) editor.matrix();
// @ $ #
		else if ("$Z64@".equals(text) || "$Z36@".equals(text) || "$Z35@".equals(text)) {
			int l = text.length();
			editor.insertcp(Integer.parseInt(text.substring(2, l-1)));
		}
		else 
			editor.insert(text);
	}

	public void tab() {
		editor.tab();
	}
	
	public void shiftTab() {
		editor.shiftTab();
	}
	
	public void insert(char charAt) {
		editor.insert(charAt);
	}

	@Override
	public void focus() {
		soft = false;
		setFocus2(editor != null, false);
	}
	
	@Override
	public void softFocus() {
		soft = true;
		setFocus2(editor != null, true);
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

	
	private static native void addListener(String command,
			ListenerForEvents listener) /*-{
		$wnd.addCBookEventListener(command, function(event) {
			if( typeof event === 'string' )
				event = JSON.parse(event)
			listener.@nl.uu.fi.dwo.interaction.client.ListenerForEvents::accept(Lcom/google/gwt/core/client/JavaScriptObject;)(event)
		}, $wnd.outer);
	}-*/;

	FormuleClipboardIF clip = new FormuleClipboardIF() {
		String content;
		
		@Override
		public String getClipboard() {
			return content;
		}

		@Override
		public void setClipboard(String formule) {
			content = formule;
		}
		
	};
	
	@Override
	public void fireEvent(CBookEvent event) {
		event.setSource(GWT.getModuleName());
		ObjectMap map = event.toObjectMap();
		String jso = JSONUtilities.toJSONObject(map).toString();
		fireEvent0(jso);
	}

	@Override
	public FormuleClipboardIF getFormuleClipboard() {
		return clip;
	}

	@Override
	public void blur() {
		
	}

	@Override
	public LessonMode getLessonMode() {
      try {
        return LessonMode.valueOf(getContext().getString("lesson_mode"));
      } catch (Throwable t) {
        return LessonMode.normal; // retrieve lesson_mode from 'StubView' 
      }
	}

	@Override
	public void functionKey(int minF) {
		// TODO F1 -- F12 to editor functions
	}

	@Override
	public Role getRole() {
	  try {
	    return Role.valueOf(getContext().getString("roles"));
	  } catch (Throwable t) {
	    return ROLE_LEARNER;
	  }
	}

	@Override
	public HandlerRegistration addCBookEventListener(String command,
			CBookEventListener listener) {
		addListener(command, new ListenerForEvents(listener));
		return null;
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void unpause() {
		// TODO Auto-generated method stub
	}

	private static native void setEnterType(String type) /*-{
		$wnd.setEnterType(type, $wnd.outer)
	}-*/;
	
	@Override
	public void setEnterType(EnterType type) {
		try {
			setEnterType(type.name());
		} catch (Exception e) {
		}
	}

	@Override
	public boolean hasListeners(String command) {
		return true;
	}

	private static native JavaScriptObject getConfiguration0() /*-{
		return $wnd.getConfiguration($wnd.outer);
	}-*/;

	private static JSONObject getConfiguration1() {
		try {
			JavaScriptObject configuration0 = getConfiguration0();
			if(configuration0 == null) return null; // no wrap if null
			return new JSONObject(configuration0);
		} catch(Throwable t) {
			return null;
		}
	}
	
	public ObjectMap getConfiguration() {
		return JSONUtilities.wrapMap(getConfiguration1());
	}

	private static native JavaScriptObject getContext0() /*-{
	  return $wnd.getContext($wnd.outer);
	}-*/;
	
	private static JSONObject getContext1() {
	  try {
	    JavaScriptObject context0 = getContext0();
	    if (context0 == null) return null; // no wrap if null
		return new JSONObject(context0);
	  } catch(Throwable t) {
	    return null;
	  }
	}
	
	
	@Override
	public ObjectMap getContext() {
		return JSONUtilities.wrapMap(getContext1());
	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		tickle();
	}
	
	public int getConstantWidth() {
		return view.getConstantWidth();
	}
	
	public int getConstantHeight() {
		return view.getConstantHeight();
	}

	public int getWidth() {
		return view.getWidth();
	}
	
	public int getHeight() {
		return view.getHeight();
	}
	
	private String knip() {
		clip.setClipboard(null);
		editor.knip(clip);
		return clip.getClipboard();
	}
	
	private String kopieer() {
		clip.setClipboard(null);
		editor.kopieer(clip);
		return clip.getClipboard();
	}
}
	
