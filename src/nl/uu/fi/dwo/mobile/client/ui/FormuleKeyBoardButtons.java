package nl.uu.fi.dwo.mobile.client.ui;

import java.util.ArrayList;
import java.util.HashMap;

import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.DWOkeyboardBundle;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.googlecode.mgwt.dom.client.event.touch.TouchCancelEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import static nl.uu.fi.dwo.mobile.utils.ImageUtils.newImage;

/**
 * Button implementations for the keyboard
 * 
 * @author Danny Hendrix
 * 
 */
public abstract class FormuleKeyBoardButtons
{
	private static HashMap<String, String> buttonImages;
	private static HashMap<String, String> buttonTexts;

	private static class ButtonListener implements TouchHandler
	{
		private String code;
		private FormuleKeyboard kb;

		public ButtonListener(String code, FormuleKeyboard kb)
		{
			this.code = code;
			this.kb = kb;
		}

		@Override
		public void onTouchEnd(TouchEndEvent event)
		{
				event.preventDefault();
				event.stopPropagation();
				FocusOnTouch.focus();
			final FormuleEditorIF editor = kb.getEditor();
			if (code.equals("wortel") == true)
				editor.wortel();
			else if (code.equals("breuk") == true)
				editor.breuk();
			else if (code.equals("macht") == true)
				editor.macht();
			else if (code.equals("kwadraat") == true)
				editor.kwadraat();
			else if (code.equals("ndewortel") == true)
				editor.ndewortel();
			else if (code.equals("haakjes") == true)
				editor.haakjes();
			else if (code.equals("integraal") == true)
				editor.integraal();
			else if (code.equals("prv") == true)
				editor.prv();
			else if (code.equals("ndelog") == true)
				editor.ndelog();
			else if (code.equals("abs") == true)
				editor.abs();
			else if (code.equals("subscript") == true)
				editor.subscript();
			else if (code.equals("bin") == true)
				editor.bin();
			else if (code.equals("diff") == true)
				editor.diff();
			else if (code.equals("limiet0") == true)
				editor.limiet0();
			else if (code.equals("limiet1") == true)
				editor.limiet1();
			else if (code.equals("limiet2") == true)
				editor.limiet2();
			else if (code.equals("primitieve") == true)
				editor.primitieve();
			else if (code.equals("conjug") == true)
				editor.conjug();
			else if (code.equals("sigma") == true)
				editor.sigma();
			else if (code.equals("copy") == true)
				FormuleKeyboard.setClipboard(editor.getSelectionString());
			else if (code.equals("paste") == true)
				editor.insert(FormuleKeyboard.getClipboard());
			else if (code.equals("backspace"))
				kb.backspace();
			else if (code.equals("back"))
				kb.backspace();
			else if (code.equals("delete") || code.equals("del"))
				editor.removeNextElement();
			else if (code.equals("space"))
				editor.insert( ' ');
			else if (code.equals("min"))
				editor.insert('-');
			else if (code.equals("komma"))
				editor.insert(',');
			else if (code.equals("plus"))
				editor.insert('+');
			else if (code.equals("maal"))
				editor.insert('*');
			else if (code.equals("pi"))
				editor.insert('\u03C0');
			else if (code.equals("of"))
			{	editor.insert(" of ");
			}
			else if (code.equals("apply") || code.equals("enter"))
			{
				kb.enter();
			}
			else if (code.toLowerCase().equals("shift"))
			{
				String panel = FormuleKeyboard.QWERTY;
				if (kb.isCurrent(panel))
					kb.goTo(FormuleKeyboard.qwerty);
				else
					kb.goTo(panel);
			}
			else if (code.equals("zoomIn"))
			{
				kb.zoomIn();
			}
			else if (code.equals("zoomOut"))
			{
				kb.zoomOut();
			}
			else if (code.equals("right"))
			{
				editor.cursorToRight();
			}
			else if (code.equals("left"))
			{
				editor.cursorToLeft();
			} else 
			if (code.equals (FormuleKeyboard.ΑΒ))
			{
				kb.goTo("Alpha");
			}
			if (code.equals (FormuleKeyboard.QWERTY))
			{
				kb.goTo(FormuleKeyboard.qwerty);
			}
			if (code.equals (FormuleKeyboard._123))
			{
				kb.goTo(FormuleKeyboard.KEYBOARD);
			}
			if( FormuleKeyboard.VVV.equals(code))
			{
				kb.tp.hideKeyboard();
			}
			
			else if (code.length() == 1)
				editor.insert(code.charAt(0));
		}

		@Override
		public void onTouchStart(TouchStartEvent event) {
			event.preventDefault();
			event.stopPropagation();
		}

		@Override
		public void onTouchMove(TouchMoveEvent event) {
			event.preventDefault();
			event.stopPropagation();
			
		}

		@Override
		public void onTouchCanceled(TouchCancelEvent event) {
			// TODO Auto-generated method stub
			
		}
	}

	private static void setupBundle() {
		DWOkeyboardBundle KB = GWT.create(DWOkeyboardBundle.class);
		buttonImages_put("breuk", KB.breuk());
		buttonImages_put("kwadraat", KB.kwadraat());
		buttonImages_put("macht", KB.macht());
		buttonImages_put("ndewortel", KB.ndewortel());
		buttonImages_put("wortel", KB.wortel());
		buttonImages_put(FormuleKeyboard.VVV, KB.VVV());
	}
	
	private static void buttonImages_put(String key, ImageResource resource) {
		buttonImages.put(key, resource.getSafeUri().asString());
	}

	private static void setUp()
	{
		if (buttonImages != null)
			return;

		/*
		 private String[][] buttonCodes =
		{
		{ "copy", "paste", "del", "back", null, "apply" },
		{ "wortel", "macht", "kwadraat", "breuk", "haakjes", "ndewortel", "x", "y", "(", ")", "1", "2", "3", "/" },
		{ "integraal", "prv", "ndelog", "abs", "subscript", "bin", "a", "b", "k", "e", "pi", "4", "5", "6", "maal" },
		{ "diff", "limiet0", "limiet1", "limiet2", "\u221e", "primitieve", "p", "q", "t", "<", ">", "7", "8", "9", "min" },
		{ "conjug", "\u2192", "sigma", "\u3008", "\u3009", "diff_partial", "space", "of", "\u2248", "0", ".", "=", "plus" } };
		 */
		buttonImages = new HashMap<String, String>();
		//buttonImages.put("wortel", "images/resources/mw_wortel.gif");
		//buttonImages.put("breuk", "images/resources/mw_breuk.gif");
		//buttonImages.put("macht", "images/resources/mw_macht.gif");
		//buttonImages.put("kwadraat", "images/resources/mw_kwadraat.gif");
		//buttonImages.put("ndewortel", "images/resources/mw_ndewortel.gif");
		buttonImages.put("haakjes", "images/resources/mw_haakjes.gif");
		buttonImages.put("min", "images/resources/mw_min.gif");
		buttonImages.put("plus", "images/resources/mw_plus.gif");
		buttonImages.put("maal", "images/resources/mw_maal.gif");

		buttonImages.put("integraal", "images/resources/mw_integraal.gif");
		buttonImages.put("prv", "images/resources/mw_prv.gif");
		buttonImages.put("ndelog", "images/resources/mw_ndelog.gif");
		buttonImages.put("abs", "images/resources/mw_abs.gif");
		buttonImages.put("subscript", "images/resources/mw_subscript.gif");
		buttonImages.put("bin", "images/resources/mw_bin.gif");
		buttonImages.put("diff", "images/resources/mw_diff.gif");
		buttonImages.put("limiet0", "images/resources/mw_limiet0.gif");
		buttonImages.put("limiet1", "images/resources/mw_limiet1.gif");
		buttonImages.put("limiet2", "images/resources/mw_limiet2.gif");
		buttonImages.put("primitieve", "images/resources/mw_primitieve.gif");
		
		buttonImages.put("conjug", "images/resources/mw_conjug.gif");
		buttonImages.put("sigma", "images/resources/mw_sigma.gif");
		buttonImages.put("diff_partial", "images/resources/mw_partialdiff.gif");
		
		buttonImages.put("copy", "images/resources/CopyIcon.png");
		buttonImages.put("paste", "images/resources/PasteIcon.png");
		buttonImages.put("apply", "images/resources/vinkje.png");
		buttonImages.put("backspace", "images/resources/BackSpaceIcon.png");
		buttonImages.put("back", "images/resources/BackSpaceIcon.png");

		buttonImages.put("zoomIn", "images/resources/zoomuitknop.gif");
		buttonImages.put("zoomOut", "images/resources/zoominknop.gif");
		buttonImages.put("right", "images/resources/pijlrechts.gif");
		buttonImages.put("left", "images/resources/pijllinks.gif");
		
		//buttonImages.put(FormuleKeyboard.VVV, "images/resources/keyboardremovebutton.png");

		buttonTexts = new HashMap<String, String>();
		//buttonTexts.put("key", "value");
		buttonTexts.put("pi", "\u03C0");
// ook in DWO modes
		buttonImages.put("shift", "images/resources/shift_2.jpg");
		buttonImages.put("SHIFT", "images/resources/shift_1.jpg");

		
		setupBundle();
		
		DWOplayer.PARAMETERS.keyboardSetup();
		
	}

	public static TouchPanel getButton(String key, FormuleKeyboard kb)
	{
		setUp();
		ButtonListener listener = new ButtonListener(key, kb);
		TouchPanel b;
		String[] diabledItems =
		//{ "integraal", "prv", "ndelog", "abs", "subscript", "bin", "diff", "limiet0", "limiet1", "limiet2", "primitieve", "conjug", "sigma", "diff_partial" };
			{ "diff_partial" };
		ArrayList<String> disabled = new ArrayList<String>();
		java.util.Collections.addAll(disabled, diabledItems);

		if (buttonImages.containsKey(key) == true){
			b = getImageButton(buttonImages.get(key));
			//b.getElement().getStyle().setBackgroundImage("url(images/resources/buttongradient.png)");
		}
		else if (buttonTexts.containsKey(key) == true){
			b = getNewButton(buttonTexts.get(key));
			//b.getElement().getStyle().setBackgroundImage("url(images/resources/buttongradient.png)");
		}
		else
		{
			b = getNewButton(key);
			if (key.length() == 1 && Character.isDigit(key.charAt(0))){
				b.addStyleDependentName(getDependentName() + "-numeric");
				//b.getElement().getStyle().setBackgroundImage("url(images/resources/numericbuttongradient.png)");
			}
			//else b.getElement().getStyle().setBackgroundImage("url(images/resources/buttongradient.png)"); 
				
		}
		
		
		if (disabled.contains(key)){
			b.getElement().addClassName("disabled");
			//b.getElement().getStyle().setBackgroundImage(null); 
			//b.getElement().getStyle().setBackgroundColor(CssColor.make(160,162,168).toString()); 
		}
		if (key == "apply")
			b.getElement().addClassName("apply");

		if (key.equals("backspace") || key.equals("back"))
			b.getElement().getStyle().setProperty("textAlign", "right");

		b.addTouchHandler(listener);
		return b;
	}

	public static TouchPanel getNewButton(String t)
	{
		//Button b = new Button();
		TouchPanel b = new TouchPanel();
		b.setStylePrimaryName("kbd-Button");
		//b.setText(t);
		b.getElement().setInnerText(t);
		//b.getElement().getStyle().setFloat(Style.Float.LEFT);
		b.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		b.addStyleDependentName(getDependentName());
		return b;
	}

	static String getDependentName() {
		return DWOplayer.PARAMETERS.keyboardStyle();
	}
	
	
	public static TouchPanel getImageButton(String src)
	{
		TouchPanel b = new TouchPanel();
		b.setStylePrimaryName("kbd-Button");
		b.addStyleDependentName(getDependentName()); // else buttonDWO
		//b.getElement().getStyle().setFloat(Style.Float.LEFT);
		b.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		Image img = newImage(src);
		img.addStyleDependentName(getDependentName());
		if("images/resources/zoomuitknop.gif".equals(src) || "images/resources/zoominknop.gif".equals(src))
		{
			img.setHeight("32px");
		
		}
		b.getElement().appendChild(img.getElement()); // geen b.add(img);
		return b;
	}

	public static void setupWN() {
		// Noordhoff extra's				
				buttonImages.put(FormuleKeyboard.VVV, "images/resources/hidekeyboard.jpg");
				buttonImages.put("kwadraat", "images/resources/icon-2.jpg");
				buttonImages.put("pi", "images/resources/pi.jpg");
				buttonImages.put("wortel", "images/resources/wortel.jpg");
				buttonImages.put("macht", "images/resources/icon.jpg");
				buttonImages.put("breuk", "images/resources/icon-3.jpg");
				buttonImages.put("ndewortel", "images/resources/wortel2.jpg");
		//"\u2264", "\u2265", "\u00b1"
				buttonImages.put("\u2248", "images/resources/ongeveer.jpg");
				buttonImages.put("\u2264", "images/resources/le.jpg"); // FIXME ONTBREEKT 
				buttonImages.put("\u2265", "images/resources/__.jpg");
				//buttonImages.put("\u00B1", "images/resources/+-.jpg");
				buttonImages.put("\u2260", "images/resources/!=.jpg");

				buttonImages.put("back", "images/resources/delete.jpg");
				buttonImages.put("backspace", "images/resources/delete.jpg");
				
				buttonImages.put("0", "images/resources/0.jpg");
				buttonImages.put("1", "images/resources/1.jpg");
				buttonImages.put("2", "images/resources/2.jpg");
				buttonImages.put("3", "images/resources/3.jpg");
				buttonImages.put("4", "images/resources/4.jpg");
				buttonImages.put("5", "images/resources/5.jpg");
				buttonImages.put("6", "images/resources/6.jpg");
				buttonImages.put("7", "images/resources/7.jpg");
				buttonImages.put("8", "images/resources/8.jpg");
				buttonImages.put("9", "images/resources/9.jpg");
				buttonImages.put("(", "images/resources/(.jpg");
				buttonImages.put(")", "images/resources/).jpg");
				buttonImages.put("haakjes", "images/resources/(-).jpg");
				buttonImages.put("plus", "images/resources/+.jpg");
				buttonImages.put("min", "images/resources/min.jpg");
				buttonImages.put("maal", "images/resources/vermenigvuldigen.jpg");
				buttonImages.put("/", "images/resources/delen.jpg");
				buttonImages.put("komma", "images/resources/komma.jpg");
				buttonImages.put("apply", "images/resources/controleer.jpg");
				buttonImages.put("enter", "images/resources/enter.jpg");
				buttonImages.put(FormuleKeyboard.ΑΒ, "images/resources/switch.jpg");
				buttonImages.put(FormuleKeyboard.QWERTY, "images/resources/switch.jpg");
				buttonImages.put(FormuleKeyboard._123, "images/resources/switch.jpg");
				buttonImages.put("=", "images/resources/=.jpg");
				buttonImages.put("<", "images/resources/lt.jpg");
				buttonImages.put(">", "images/resources/gt.jpg");
				buttonImages.put("!", "images/resources/!.jpg");
				buttonImages.put("?", "images/resources/_.jpg");

				buttonImages.put("shift", "images/resources/shift_2.jpg");
				buttonImages.put("SHIFT", "images/resources/shift_1.jpg");

				for (char i = 'a'; i <= 'z'; i++) {
					String string = Character.toString(i);
					buttonImages.put(string, "images/resources/" + string + string + ".jpg");
				}
				for (char i = 'A'; i <= 'Z'; i++) {
					String string = Character.toString(i);
					buttonImages.put(string, "images/resources/" + string + ".jpg");
				}

				//buttonTexts.put(" ", "space");
				
	}
}
