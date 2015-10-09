package nl.uu.fi.dwo.interaction.client.keyboard;

import java.awt.event.KeyEvent;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
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
import com.google.gwt.user.client.ui.Widget;

final public class FocusOnTouch implements MouseUpHandler, KeyDownHandler, KeyPressHandler
{
	private FocusPanel focusPanel;
	private static FocusPanel mainPanel;
	
	static public FocusPanel wrap ( Widget w) {
		return wrap(w, true);
	}
	static public FocusPanel wrap ( Widget w , boolean main) {
		FocusPanel focus;
		if(w instanceof FocusPanel) 
			focus = (FocusPanel) w;
		else {
			focus = new FocusPanel();
			focus.add(w);
		}
		if(main)
			mainPanel = focus;
		boolean hastouch = com.google.gwt.event.dom.client.TouchStartEvent.isSupported();
		FocusOnTouch handler = new FocusOnTouch(focus);
		focus.addKeyDownHandler(handler);
		focus.addKeyPressHandler(handler);
		if (!hastouch)
		{
//			focus.addMouseUpHandler(handler); // XXX WAS focus on touch/mouse up
		}
		return focus;
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
				focusPanel.setFocus(true);
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

	@Override
	public void onKeyPress(KeyPressEvent event)
	{
		if(kb == null || kb.getEditor() == null || kb.getEditor() == AbstractEditor.NULL)
			return;
	
		char ch = event.getCharCode();
		FormuleEditorIF editor = kb.getEditor();
		
		if (event.isAltKeyDown() || event.isControlKeyDown() || event.isMetaKeyDown())
		{	return;
		}
		if (kb != null && kb.getEditor() != null)
		{
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

	private boolean allowed(char ch)
	{
		if (ch >= ' ' && ch < '\u007F' && ch != '^')
			return true;
		switch (ch)
		{
		case '^': // expliciet macht verheffen..
			return true;
		default:
			return Character.isLetterOrDigit(ch);
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
			else if(code == 86 && event.isControlKeyDown()) //ctrl+v
			{
				editor.plak(clip);
				event.preventDefault();
				event.stopPropagation();
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
}