package nl.uu.fi.dwo.mobile.client.ui;

import java.util.ArrayList;
import java.util.HashMap;

import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleTeken;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Breukvak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Haakjesvak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.IntegraalVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Kwadraatvak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.Machtvak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.NdeWortelVak;
import nl.uu.fi.dwo.formule.client.formuleobjects.vakken.WortelVak;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.touch.TouchStartEvent;
import nl.uu.fi.dwo.interaction.client.touch.TouchStartHandler;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithAnswer;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;

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

	private static class ButtonListener implements TouchStartHandler
	{
		private String code;
		private FormuleKeyboard kb;

		public ButtonListener(String code, FormuleKeyboard kb)
		{
			this.code = code;
			this.kb = kb;
		}

		@Override
		public void onTouchStart(TouchStartEvent event)
		{
			if(TouchStartEvent.isSupported())
			{
				event.preventDefault();
				event.stopPropagation();
			}
			
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
			else if (code.equals("shift"))
			{
				String panel = "ABCShift";
				if (kb.isCurrent(panel))
					kb.goTo("ABC");
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
			if (code.equals (FormuleKeyboard._123))
			{
				kb.focus();
			}
			if( FormuleKeyboard.VVV.equals(code))
			{
				kb.tp.hideKeyboard();
			}
			
			else if (code.length() == 1)
				editor.insert(code.charAt(0));
		}
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
		buttonImages.put("wortel", "images/resources/mw_wortel.gif");
		buttonImages.put("breuk", "images/resources/mw_breuk.gif");
		buttonImages.put("macht", "images/resources/mw_macht.gif");
		buttonImages.put("kwadraat", "images/resources/mw_kwadraat.gif");
		buttonImages.put("ndewortel", "images/resources/mw_ndewortel.gif");
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
		
		buttonImages.put(FormuleKeyboard.VVV, "images/resources/keyboardremovebutton.png");

		buttonTexts = new HashMap<String, String>();
		buttonTexts.put("key", "value");
	}

	public static TouchButton getButton(String key, FormuleKeyboard kb)
	{
		setUp();
		ButtonListener listener = new ButtonListener(key, kb);
		TouchButton b;
		String[] diabledItems =
		//{ "integraal", "prv", "ndelog", "abs", "subscript", "bin", "diff", "limiet0", "limiet1", "limiet2", "primitieve", "conjug", "sigma", "diff_partial" };
			{ "diff_partial" };
		ArrayList<String> disabled = new ArrayList<String>();
		java.util.Collections.addAll(disabled, diabledItems);

		if (buttonImages.containsKey(key) == true){
			b = getImageButton(buttonImages.get(key));
			b.getElement().getStyle().setBackgroundImage("url(images/resources/buttongradient.png)");
		}
		else if (buttonTexts.containsKey(key) == true){
			b = getNewButton(buttonTexts.get(key));
			b.getElement().getStyle().setBackgroundImage("url(images/resources/buttongradient.png)");
		}
		else
		{
			b = getNewButton(key);
			if (key.length() == 1 && Character.isDigit(key.charAt(0))){
				b.getElement().addClassName("numeric");
				b.getElement().getStyle().setBackgroundImage("url(images/resources/numericbuttongradient.png)");
			}
			else b.getElement().getStyle().setBackgroundImage("url(images/resources/buttongradient.png)"); 
				
		}
		
		
		if (disabled.contains(key)){
			b.getElement().addClassName("disabled");
			b.getElement().getStyle().setBackgroundImage(null); 
			b.getElement().getStyle().setBackgroundColor(CssColor.make(160,162,168).toString()); 
		}
		if (key == "apply")
			b.getElement().addClassName("apply");

		if (key.equals("backspace") || key.equals("back"))
			b.getElement().getStyle().setProperty("textAlign", "right");

		b.addTouchStartHandler(listener);
		return b;
	}

	public static TouchButton getNewButton(String t)
	{
		//Button b = new Button();
		TouchButton b = new TouchButton();
		//b.setText(t);
		b.getElement().setInnerText(t);
		b.getElement().getStyle().setFloat(Style.Float.LEFT);
		b.getElement().addClassName("button");
		return b;
	}

	public static TouchButton getImageButton(String src)
	{
		TouchButton b = getNewButton("");
		Image img = new Image(src);
		img.setHeight("16px");
		if("images/resources/zoomuitknop.gif".equals(src) || "images/resources/zoominknop.gif".equals(src))
		{
			img.setHeight("32px");
		
		}
		b.getElement().appendChild(img.getElement());
		return b;
	}
}
