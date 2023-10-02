package nl.uu.fi.dwo.interaction.client.keyboard;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.Letter;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent;

import static com.google.gwt.event.dom.client.KeyCodes.*;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

final public class FocusOnTouch implements MouseUpHandler, KeyDownHandler, KeyPressHandler
{
	private FocusPanel focusPanel;
	private static FocusPanel mainPanel;
	public static boolean AREA = false;
	private static TextArea area;
	
	static public FocusPanel wrap ( Widget w) {
		return wrap(w, true);
	}
 

	static public FocusPanel wrap ( Widget w , boolean main) {
		FocusPanel focus;
		if(w instanceof FocusPanel) 
			focus = (FocusPanel) w;
		else {
			focus = w instanceof RequiresResize ? new ResizeFocusPanel(): new FocusPanel();
			focus.add(w);
		}
		FocusOnTouch handler = new FocusOnTouch(focus);
		if(main)
		{
			mainPanel = focus;
			if (AREA) {
			area = new FocusArea(handler);
			RootLayoutPanel r = RootLayoutPanel.get();
			r.add(area);
			r.setWidgetTopHeight(area, 0, Unit.EM, 1, Unit.EM);
			r.setWidgetRightWidth(area, 1, Unit.EM, 1, Unit.EM);
			area.addKeyDownHandler(handler);
			area.addKeyPressHandler(handler);
			}
			
		}
		boolean hastouch = com.google.gwt.event.dom.client.TouchStartEvent.isSupported();
		focus.addKeyDownHandler(handler);
		focus.addKeyPressHandler(handler);
		if (!hastouch)
		{
//			focus.addMouseUpHandler(handler); // XXX WAS focus on touch/mouse up
		}
		return focus;
	}
	
	protected void onPaste(String data) {
		clip.setClipboard(data);
		kb.getEditor().insert(data);
		focus();
	}

	public static void installKeyboard(FormuleKeyboardIF keyb, FormuleClipboardIF clp) {
		kb = keyb;
		clip = clp;
	}
	
	public static void requestFocus(final FocusPanel focusPanel)
	{
		Scheduler.get().scheduleDeferred(new ScheduledCommand() // voor firefox delayed focus.
		{
			public void execute()
			{
				if (AREA) area.setFocus(true); else focusPanel.setFocus(true); // was area
			}
		});
	}

	private FocusOnTouch(FocusPanel focusPanel) {
		super();
		this.focusPanel = focusPanel;
	}

	public void onMouseUp(MouseUpEvent event)
	{
		requestFocus(focusPanel);
	}
	
	private static FormuleKeyboardIF kb;
	private static FormuleClipboardIF clip;

	private void backspace(DomEvent<?> event)
	{
		kb.backspace();
		event.stopPropagation();
		event.preventDefault();
	}
	
	private void delete(DomEvent<?> event)
	{
		kb.delete();
		event.stopPropagation();
		event.preventDefault();
	}

	private void enter(DomEvent<?> event)
	{
		kb.enter();
		event.preventDefault();
		event.stopPropagation();
	}

	java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(getClass().getName());
	
	@Override
	public void onKeyPress(KeyPressEvent event)
	{
		if(kb == null || kb.getEditor() == null || kb.getEditor() == AbstractEditor.NULL)
			return;
	
		char ch = event.getCharCode();
		FormuleEditorIF editor = kb.getEditor();
		
		if (event.isAltKeyDown() || event.isControlKeyDown() || event.isMetaKeyDown())
		{
//			boolean down = event.isAltKeyDown() && !event.isControlKeyDown(); // alt of alt-shift
//
//			if (down && kb != null && kb.getEditor() != null)
//			{
//				ch = greek(ch); 
//				LOG.info("alt - " + ch + " = " + (int) ch);
//				
//				if (ch != '\0') {
//					editor.insert( ch);
//					event.preventDefault();
//					event.stopPropagation();
//				}
//			}
			return;
		}
		if (kb != null && kb.getEditor() != null)
		{
			switch(ch) {
			case '@':
			case '#':
			case '$': 
				editor.insertcp((int)ch);
				event.preventDefault();
				event.stopPropagation();
				return;
			}
			
			
			
			if (allowed(ch))
			{
				editor.insert( ch);
				event.preventDefault();
				event.stopPropagation();
			}
			else if (ch == '\b')
			{
				backspace(event);
			}
			else if (ch == KeyCodes.KEY_DELETE)
			{
				delete(event);
			}
			else if (ch == '\u007F')
			{
				delete(event);
			}
			//else if (ch == '^')
			//{
			//	macht(event, editor);
			//	
			//}
			else if (ch == '\n' || ch == '\r') // enter?
			{
				enter(event);
			}
		}

	}
 
	private char greek(int code) {
		switch(code) {
		case KeyCodes.KEY_A: return 'α';
		case KeyCodes.KEY_B: return 'β';
		case KeyCodes.KEY_C: return 'χ';
		case KeyCodes.KEY_D: return 'δ';
		case KeyCodes.KEY_E: return 'ε';
		case KeyCodes.KEY_V:
		case KeyCodes.KEY_F: return '\u03c6';
		case KeyCodes.KEY_G: return 'γ';
		case KeyCodes.KEY_H: return 'η';
		case KeyCodes.KEY_I: return 'ι';
		case KeyCodes.KEY_J: return '∆'; // Op mac toetsenbord Option-J
		case KeyCodes.KEY_K: return '\u03ba';
		case KeyCodes.KEY_L: return '\u03bb';
		case KeyCodes.KEY_M: return '\u03bc';
		case KeyCodes.KEY_N: return '\u03bd';
		case KeyCodes.KEY_O: return '\u03bf';
		case KeyCodes.KEY_P: return '\u03c0';
		case KeyCodes.KEY_Q: return '\u03b8';
		case KeyCodes.KEY_R: return '\u03c1';
		case KeyCodes.KEY_S: return '\u03c3';
		case KeyCodes.KEY_T: return '\u03c4';
		case KeyCodes.KEY_U: return '\u03c5';
		case KeyCodes.KEY_W: return '\u03c9';
		case KeyCodes.KEY_X: return '\u03be';
		case KeyCodes.KEY_Y: return '\u03c8';
		case KeyCodes.KEY_Z: return '\u03b6';
		}
		return '\0';
	}
	private boolean allowed(char ch)
	{
		if (ch >= ' ' && ch < '\u007F' && ch != '^')
			return true;
		switch (ch)
		{
		case '^': // expliciet macht verheffen..
			return true;
		default:
			return Character.isLetterOrDigit(ch)||Letter.isLetter(ch);
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event)
	{
		if(kb == null)
			return;
		FormuleEditorIF editor = kb.getEditor();
		
		if (event.isAltKeyDown() || event.isControlKeyDown() || event.isMetaKeyDown() || kb == null)
		{	int code = event.getNativeKeyCode();
		
			if (code == KeyCodes.KEY_CTRL || code == KeyCodes.KEY_WIN_KEY_LEFT_META) return;
			if (code == KeyCodes.KEY_A && (event.isControlKeyDown()||event.isMetaKeyDown()))
			{
				editor.selectAll();
				event.preventDefault();
				event.stopPropagation();
			} else
		
		
			if(code == 88 && event.isControlKeyDown()) //ctrl+x
			{
				editor.knip(clip);
				event.preventDefault();
				event.stopPropagation();
			}
			else if(code == 67 && event.isControlKeyDown()) //ctrl+c
			{
				editor.kopieer(clip);
				event.preventDefault();
				event.stopPropagation();
			}
			else if(code == 86 && event.isControlKeyDown() && !AREA) //ctrl+v
			{
				editor.plak(clip);
				event.preventDefault();
				event.stopPropagation();
			}
			boolean down = event.isAltKeyDown() && !event.isControlKeyDown()
					&& code >= KeyCodes.KEY_A && code <= KeyCodes.KEY_Z; // alt of alt-shift

LOG.severe("on key down " + down + " " + code);					
					
			if (down && kb != null && kb.getEditor() != null)
			{
				
				char ch = greek(code);
				if (ch != '\0') {
					editor.insert( ch);
					event.preventDefault();
					event.stopPropagation();
				}
			}
		
			return;
		}
		if (editor == null || editor == AbstractEditor.NULL)
			return;

		if (event.isLeftArrow())
		{
			if(event.isShiftKeyDown())
				editor.cursorToLeftShift();
			else
				editor.cursorToLeft();
			event.preventDefault();
			event.stopPropagation();
		}
		else if (event.isRightArrow())
		{
			if(event.isShiftKeyDown())
				editor.cursorToRightShift();
			else
				editor.cursorToRight();
			event.preventDefault();
			event.stopPropagation();
		}		
		else if (event.isUpArrow())
		{
			editor.cursorUp();
			event.preventDefault();
			event.stopPropagation();
		}
		else if (event.isDownArrow())
		{
			editor.cursorDown();
			event.preventDefault();
			event.stopPropagation();
		}
		
		
		
		else
		{
			switch (event.getNativeKeyCode())
			{
			case 8: // firefox
				backspace(event);
				break;
			case 9: // tab/shifttab
				if(event.isShiftKeyDown())
					editor.shiftTab();
				else 
					editor.tab();
				event.stopPropagation();
				event.preventDefault();
				break;
			case KeyCodes.KEY_DELETE: // firefox
				delete(event);
				break;
//In chrome: zowel keydown als keypres op 'enter' 
			case 13: //firefox
				if(! Window.Navigator.getUserAgent().toLowerCase().contains("chrome"))
					enter(event);
				break;
			case '6': // shift-6 (asus transformer)
				if (event.isShiftKeyDown())
				{
					//System.out.println("met shifttoets down");
					//macht(event, editor);
					//Hier nog niet, want het wordt alleen een machtvak als het volgende character een getal is.
				}
				break;
			case 16: // shift
				break;
			default: // unknown
				int code = minF(event.getNativeKeyCode());
				if(1 <= code && code <= 12)
				{	kb.functionKey(code);
					event.stopPropagation();
					event.preventDefault();
				}
			}

		}

	}

// Browser dependent function. Keycode of Function key	
	private int F(int i) {
		return 111 + i;
	}
// de inverse van F
	private int minF(int i) {
		return i-111;
	}

	private void macht(DomEvent<?> event, FormuleEditorIF editor)
	{
		editor.macht();
		event.preventDefault();
		event.stopPropagation();
	}

	public static void focus() {
		if(mainPanel != null)
			requestFocus(mainPanel);
	}

	public String doCut() {
		FormuleEditorIF editor = kb.getEditor();
		editor.knip(clip);
		return clip.getClipboard();
	}


	public String doCopy() {
		FormuleEditorIF editor = kb.getEditor();
		editor.kopieer(clip);
		return clip.getClipboard();
	}
}