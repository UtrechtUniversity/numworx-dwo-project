package nl.uu.fi.dwo.interaction.client.keyboard;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

final public class FocusOnTouch implements MouseUpHandler, KeyDownHandler, KeyPressHandler
{
	private static FocusPanel focusPanel;
	
	static public FocusPanel wrap ( Widget w ) {
		FocusPanel focus;
		if(w instanceof FocusPanel) 
			focus = (FocusPanel) w;
		else {
			focus = new FocusPanel();
			focus.add(w);
		}
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
	
	public static void installKeyboard(FormuleKeyboardIF keyb) {
		kb = keyb;
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
		if (event.isAltKeyDown() || event.isControlKeyDown() || event.isMetaKeyDown())
			return;
		if (kb != null && kb.getEditor() != null)
		{
			FormuleEditorIF editor = kb.getEditor();
			char ch = event.getCharCode();
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
			else if (ch == '^')
			{
				macht(event, editor);
			}
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
			return false;
		default:
			return Character.isLetterOrDigit(ch);
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event)
	{
		if (event.isAltKeyDown() || event.isControlKeyDown() || event.isMetaKeyDown() || kb == null)
			return;
		FormuleEditorIF editor = kb.getEditor();
		if (editor == null)
			return;

		if (event.isLeftArrow())
		{
			editor.cursorToLeft();
			event.preventDefault();
			event.stopPropagation();
		}
		else if (event.isRightArrow())
		{
			editor.cursorToRight();
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
				enter(event);
				break;
			case '6': // shift-6 (asus transformer)
				if (event.isShiftKeyDown())
				{
					macht(event, editor);
				}
				break;
			case 16: // shift
				break;
			default: // unknown
				int code = event.getNativeKeyCode();
				if(code == F(1)) {
					editor.wortel();
				} else if(code == F(2)) {
					editor.macht();
				} else if (code == F(3)) {
					editor.kwadraat();
				} else if (code == F(4)) {
					editor.breuk();
				} else if (code == F(5)) {
					editor.haakjes();
				} else if (code == F(6)) {
					editor.ndewortel();
				} else if (code == F(7)) {
					editor.integraal();
				} else if (code == F(8)) {
					editor.primitieve();
				} else if (code == F(9)) {
					editor.ndelog();
				} else if (code == F(10)) {
					editor.abs();
				} else if (code == F(11)) {
					editor.subscript();
				} else if (code == F(12)) {
					editor.bin();
				} else {
					GWT.log(" code = " + code);
					return;
				}
				event.stopPropagation();
				event.preventDefault();
			}

		}

	}

// Browser dependent function. Keycode of Function key	
	private int F(int i) {
		return 111 + i;
	}

	private void macht(DomEvent<?> event, FormuleEditorIF editor)
	{
		editor.macht();
		event.preventDefault();
		event.stopPropagation();
	}

	public static void focus() {
		if(focusPanel != null)
			requestFocus(focusPanel);
	}
}