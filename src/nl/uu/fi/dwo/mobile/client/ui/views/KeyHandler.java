package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;

final class KeyHandler implements KeyDownHandler, KeyPressHandler
	{
		private FormuleKeyboardIF kb;
		public KeyHandler(FormuleKeyboardIF kb) {
			super();
			this.kb = kb;
		}

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
					editor.removeNextElement();
					event.preventDefault();
					event.stopPropagation();
				}
				else if (ch == '^')
				{
					macht(event, editor);
				}
				else if (ch == '\n' || ch == '\r') // enter?
				{
					enter(event);
				}
				//				else
				//					Window.alert(event.toDebugString());
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
// In chrome: zowel keydown als keypres op 'enter' 
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
				}

			}

		}

		private void macht(DomEvent<?> event, FormuleEditorIF editor)
		{
			editor.macht();
			event.preventDefault();
			event.stopPropagation();
		}
	}