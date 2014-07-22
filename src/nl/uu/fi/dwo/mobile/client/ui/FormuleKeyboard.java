package nl.uu.fi.dwo.mobile.client.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithAnswer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.writemathgwt.client.Rectangle;
import fi.writemathgwt.client.WritePanel;
import fi.writemathgwt.client.WritePanelHolder;

/**
 * Keyboard layout
 * 
 * @author Danny Hendrix
 * 
 */
public class FormuleKeyboard implements WritePanelHolder, FormuleKeyboardIF
{
	static final String VVV = "VVV";
	static final String _123 = "123";
	static final String ΑΒ = "\u03b1\u03b2..";
	static final String QWERTY = "ABC";
	static final String qwerty = "abc";
	static final String SCRIBBLE = "Scribble";
	static final boolean hasKeyboard  = ! TouchEvent.isSupported();
	private FormuleEditorIF editor;
	public KeyBoardTabPanel tp;
	private WritePanel writePanel;

	private static String clipboard = "";
	private static int zoomed = 0;

	private String[][] buttonCodes =
	{
	{ "zoomIn", "zoomOut", "copy", "paste", "del", "back", null, "left", "right", null, "apply" },
	{ "wortel", "macht", "kwadraat", "breuk", "haakjes", "ndewortel", "x", "y", "(", ")", "1", "2", "3", "/" },
	{ "integraal", "prv", "ndelog", "abs", "subscript", "bin", "a", "b", "k", "e", "pi", "4", "5", "6", "maal" },
	{ "diff", "limiet0", "limiet1", "limiet2", "\u221e", "primitieve", "p", "q", "t", "<", ">", "7", "8", "9", "min" },
	{ "conjug", "\u2192", "sigma", "\u3008", "\u3009", "diff_partial", "space", "of", "\u2248", "0", ".", "=", "plus" } };

	private String[][] buttonCodes_geavanceerd =
		{
			{ "7", "8", "9", "haakjes", null, "=",   "macht", "wortel",   "ndewortel", "\u00B1", "(",      ")",      null, "apply"     },
			{ "4", "5", "6", "0",       null, "maal", "/",    "kwadraat", "x",         "\u2248", "<",      ">" ,     null, "back",  QWERTY },
			{ "1", "2", "3", "komma",   null, "plus", "min",  "breuk",    "y",         "pi",     "\u2264", "\u2265", null, "enter", VVV }
		};
	private double[][] buttonWidths_geavanceerd =
		{
			{ 1, 1, 1, 1, 0.6, 1,   1, 1, 1, 1, 1, 1, 0.65, 2.153   },
			{ 1, 1, 1, 1, 0.6, 1,   1, 1, 1, 1, 1, 1, 0.65, 1,    1 },
			{ 1, 1, 1, 1, 0.6, 1,   1, 1, 1, 1, 1, 1, 0.65, 1,    1 },
			
		};

	
	private double[][] buttonWidths =
	{
	{ 1, 1, 1, 1, 2, 2, 1, 1, 1, 2, 2 },
	{ 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1 },
	{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
	{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
	{ 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1 }, };

	private String[][] buttonCodesGR =
	{
	{ "wortel", "macht", "kwadraat", "breuk", "haakjes", "ndewortel", "integraal", "prv", "ndelog", "abs", "subscript", "bin" ,ΑΒ },
	{ "diff", "limiet0", "limiet1", "limiet2", "\u221e", "primitieve", "e", "pi", "<", ">", "\u2228", "\u2248", VVV }

	};

	private double[][] buttonWidthsGR =
	{
	{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 , 1.1},
	{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  1.1}

	};

	private String[][] buttonCodesMW =
	{
	{ "haakjes", "breuk", "kwadraat", "macht", "wortel", "ndewortel", "ndelog" },
	{ "diff", "primitieve", "integraal", "prv", "abs", "subscript", "bin" },
	{ "limiet0", "limiet1", "limiet2", "\u221e", "�", "\u2248", "\u2260" },
	{ "<", "\u2264", "\u2265", ">", "\u2227", "\u2228", "\u2205" },
	{ "[", "]", "\u3008", "\u3009", "\u2190", "\u2192", "\u2218" },
	{ "pi", "e", "\u03b1", "\u03b2", "\u03b3", "\u03bc", "\u03c3" }

	};

	private double[][] buttonWidthsMW =
	{
	{ 1, 1, 1, 1, 1, 1, 1 },
	{ 1, 1, 1, 1, 1, 1, 1 },
	{ 1, 1, 1, 1, 1, 1, 1 },
	{ 1, 1, 1, 1, 1, 1, 1 },
	{ 1, 1, 1, 1, 1, 1, 1 },
	{ 1, 1, 1, 1, 1, 1, 1 }

	};

	private String[][] buttonCodesAbc =
	{
	{ "copy", "paste", "zoomIn", "zoomOut", "del", "back", null, "apply" },
	{ "@", "q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "back", "1", "2", "3" },
	{ "tab", "a", "s", "d", "f", "g", "h", "j", "k", "l", "enter", "4", "5", "6" },
	{ "shift", "z", "x", "c", "v", "b", "n", "m", ";", "'", "shift", "7", "8", "9" },
	{ "{", "}", "\\", " ", ",", ".", "/", "%", "0", "." }

	};

	private String[][] buttonCodes_abc = 
	{
		{ "q","w","e","r","t","y","u","i","o","p", null, "apply" },
		{null,"a","s","d","f","g","h","j","k","l", null, " ",null,"back",_123  },
		{"shift", "z","x","c","v","b","n","m","!","?", null, "enter", VVV }
	};
	private String[][] buttonCodes_ABC = 
	{
		{ "Q","W","E","R","T","Y","U","I","O","P",null,          "apply" },
		{null,"A","S","D","F","G","H","J","K","L",null, " ",null,"back",_123  },
		{"SHIFT", "Z","X","C","V","B","N","M",",",".", null, "enter", VVV }
	};
	
	private double[][] buttonWidths_abc = 
	{
		{ 1,1,1,1,1,1,1,1,1,1,   2.4,       2.15 },
		{ 0.5,1,1,1,1,1,1,1,1,1,0.7 ,2.02,0.2 , 1,1},
		{ 1,1,1,1,1,1,1,1,1,1,   2.4,       1,1},
	};
	
	
	
	private double[][] buttonWidthsAbc =
	{
	{ 1, 1, 1, 1, 2, 2, 5, 2 },
	{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
	{ 1.34, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1.7, 1, 1, 1 },
	{ 1.67, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1.4, 1, 1, 1 },
	{ 1, 1, 1, 5, 1, 1, 1, 1, 1, 2 },

	};

	private String[][] buttonCodesAlpha =
	{
	{ "\u2190", "\u2192", "[", "]", "\u3008", "\u3009", "\u211d", "\u03a6", "\u03a7", "\u03a8", "\u03a9", "\u0393", "\u0394", },
	{ "\u03b1", "\u03b2", "\u03b3", "\u03b4", "\u03b5", "\u03b6", "\u03b7", "\u03b8", "\u03b9", "\u03ba", "\u03bb", "\u03bc", "\u03bd" },
	{ "\u03be", "\u03A3", "\u03c0", "\u03c1", "\u03c2", "\u03c3", "\u03c4", "\u03c5", "\u03c6", "\u03c7", "\u03c8", "\u03c9", "\u221e" },
	{ "\u2264", "\u2265", "\u00b1", "\u2260", "\u00f7", "\u00d7", "\u00b0", "\u2030", "\u2202", "\u2206", "\u2220", "\u2227", "\u2228" },
	{ _123, "\u2200", "\u2203", "\u2204", "\u2205", "\u00ac", "\u2229", "\u222a", "\u2208", "\u2209", "\u2282", "\u2283", "\u00a9" } //"\u2284"      	},

	};

	private double[][] buttonWidthsAlpha =
	{
	{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
	{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
	{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
	{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
	{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },

	};

	private String[][] buttonCodesAbcShift =
	{
	{ "copy", "paste", "zoomIn", "zoomOut", "del", "back", null, "apply" },
	{ "@", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "back", "1", "2", "3" },
	{ "tab", "A", "S", "D", "F", "G", "H", "J", "K", "L", "enter", "4", "5", "6" },
	{ "shift", "Z", "X", "C", "V", "B", "N", "M", ":", "\"", "shift", "7", "8", "9" },
	{ "{", "}", "|", " ", "<", ">", "?", "^" }

	};

	private double[][] buttonWidthsAbcShift =
	{
	{ 1, 1, 1, 1, 2, 2, 5, 2 },
	{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
	{ 1.34, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1.7, 1, 1, 1 },
	{ 1.67, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1.4, 1, 1, 1 },
	{ 1, 1, 1, 5, 1, 1, 1, 1, 1, 2 },

	};

	
	private String[][] buttonCodesWN = 
		{
			{
				"macht", "breuk", "wortel", "ndewortel", "pi", "kwadraat", "\u2264", "\u2265", "\u00b1", "\u2248", null, VVV 
			}
		};
	
	
	private double[][] buttonWidthsWN = 
		{
			{
				1,1,1,1,1,1,1,1,1,1, 3.5, 1
			}
		};

	
	public Panel getAsPanel()
	{
		Logger.getLogger("FormuleKeyboard").log(Level.INFO," Create kb panel");
		if (tp != null)
		{	tp.setEnabled(false);
			return tp.getPanel();
		}
		tp = new KeyBoardTabPanel();

		if(hasKeyboard)
		{
			if(isNoordhoff())
				tp.addTab("Toetsenbord", this.getKeyBoard(buttonCodesWN, buttonWidthsWN), 69); // was GR
			else
				tp.addTab("Toetsenbord", this.getKeyBoard(buttonCodesGR, buttonWidthsGR), 108);
		}
		else 
		{
			if(isNoordhoff())
			{
				tp.addTab("Toetsenbord", getKeyBoard(buttonCodes_geavanceerd, buttonWidths_geavanceerd),181);
				tp.addTab(qwerty,         getKeyBoard(buttonCodes_abc, buttonWidths_abc),181);
				tp.addTab(QWERTY,    getKeyBoard(buttonCodes_ABC, buttonWidths_abc),181);
			}
			else
			{
				tp.addTab("Toetsenbord", this.getKeyBoard(buttonCodes, buttonWidths),69);
			}
		}
		//tp.addTab("ABC", this.getKeyBoard(buttonCodesAbc, buttonWidthsAbc));
	    //tp.addTab("ABCShift", this.getKeyBoard(buttonCodesAbcShift, buttonWidthsAbcShift));
		tp.addTab("Alpha", this.getKeyBoard(buttonCodesAlpha, buttonWidthsAlpha),270);
		//tp.addTab("GR", this.getKeyBoard(buttonCodesGR, buttonWidthsGR));
		//tp.addTab("MW", this.getKeyBoard(buttonCodesMW, buttonWidthsMW));

		//tp.addTab("Verberg", new SimplePanel(),0);

		writePanel = new WritePanel(this);
		TouchPanel b = FormuleKeyBoardButtons.getButton("apply", this);
		b.setWidth("30px");
		b.setHeight("16px");
		writePanel.add(b);
		
		if (!hasKeyboard)
			tp.addTab(SCRIBBLE, writePanel, 69);

		//tp.hideTabButton("ABCShift");
		//SliderPanel sp = new SliderPanel(100, this);
		//tp.getStaticPanel().add(sp.getPanel());
		tp.apply();

		return tp.getPanel();
	}
	
	public void writePanelChanged() 
	{
		String text = writePanel.parseFormule();
		
		editor.clearAll();
		editor.insert(text);
	}

	public void addNavPanel(Panel opdrnav)
	{
		opdrnav.getElement().getStyle().setFloat(Style.Float.RIGHT);
		opdrnav.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		tp.clearStaticPanel();
		if(!isNoordhoff())
			tp.getStaticPanel().add(opdrnav);
	}

	static boolean isNoordhoff() {
		return "noordhoff".equals(FormuleKeyBoardButtons.getDependentName());
	}

	public void goTo(String panel)
	{
		tp.goTo(panel);
	}

	public boolean isCurrent(String panel)
	{
		return tp.isCurrent(panel);
	}

	private Panel getKeyBoard(String[][] buttons, double[][] widths)
	{
		int defwidth = 16;
		int padding = 16;
// if(NOORDHOFF)
		if(isNoordhoff())
		{ defwidth = 52; padding = 0; }
		double width = 0;

		FlowPanel fp = new FlowPanel(), fp2 = new FlowPanel();
		for (int j = 0; j < buttons.length; j++)
		{
			fp2 = new FlowPanel();
			//fp2.getElement().getStyle().setProperty("clear", "both");
			for (int i = 0; i < buttons[j].length; i++)
			{
				if (buttons[j][i] == null)
				{
					SimplePanel b = new SimplePanel();
					width = (defwidth + 2 * (padding)) * widths[j][i] - 2 * padding;
					//width += 32;
					b.setWidth(Math.round(width) + "px");
					b.setHeight("16px");

					//b.getElement().getStyle().setFloat(Style.Float.LEFT);
					b.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
					if (b != null)
						fp2.add(b);
					continue;
				}
				TouchPanel b = FormuleKeyBoardButtons.getButton(buttons[j][i], this);
				width = (defwidth + 2 * padding) * widths[j][i] - 2 * padding;
				b.setWidth(Math.round(width) + "px"); // alleen DWO niet noordhoff.?????
				//b.setHeight("16px");
				if ("zoomOut".equals(buttons[j][i]) || "zoomIn".equals(buttons[j][i]))
				{
					b.getElement().getStyle().setWidth(32, Unit.PX);
					b.getElement().getStyle().setHeight(32, Unit.PX);
					b.getElement().getStyle().setPadding(2, Unit.PX);
				}
				if (b != null)
					fp2.add(b);
			}
			fp.add(fp2);
		}
		
		return fp;
	}

	public static Panel getSelectionMenu(FormuleHolder editor, int x, int y)
	{
		RoundPanel fp = new RoundPanel();

		//fp.add(FormuleKeyBoardButtons.getCopyButton());
		//fp.add(FormuleKeyBoardButtons.getPasteButton());

		Style s = fp.getElement().getStyle();

		s.setPosition(Position.ABSOLUTE);
		s.setTop(y, Unit.PX);
		s.setLeft(x, Unit.PX);
		return fp;
	}

	public static String getClipboard()
	{
		return clipboard;
	}

	public static void setClipboard(String text)
	{
		clipboard = text;
	}

	public boolean zoomIn()
	{

		if (zoomed <= 0)
		{
			zoomed = 0;
			return false;
		}
		zoomed--;
		this.setEditorZoomed();
		return true;
	}

	public boolean zoomOut()
	{
		int maxzoom = 10;

		if (zoomed >= maxzoom)
		{
			zoomed = maxzoom;
			return false;
		}
		zoomed++;
		this.setEditorZoomed();
		return true;
	}

	private void setEditorZoomed()
	{
		int maxzoom = 10;

		int min = getEditor().getDefaultFont().getFontSize();
		int max = min * 3;
		float dif = max - min;

		float percSlided = (float) zoomed / (float) maxzoom * 100;

		int value = min + Math.round((dif / 100) * percSlided);

		getEditor().setFont(FormuleFont.createFromFontSize(value));
	}

	public void setEditor(FormuleEditorIF editor)
	{
		if (this.editor == editor)
			return;
		tp.setEnabled(editor != null);
		if (this.editor != null)
		{
			//reset zoom
			this.editor.setFont(this.editor.getDefaultFont());
			this.editor.setCurrentElementRepaint();
		} 
		this.editor = editor;
		//if(editor != null) 
		//	this.setEditorZoomed();
		//else
		if(editor == null || editor instanceof FormuleViewer)
			tp.hideKeyboard();
	}

	public FormuleEditorIF getEditor()
	{
		return editor;
	}

	public void enter()
	{
		FormuleEditorIF editor = getEditor();
		if (editor != null)
			editor.enter();
	}

	public void backspace()
	{
		getEditor().removeCurrentElement();
	}

	public void delete()
	{
		getEditor().removeNextElement();
	}

	@Override
	public void focus() {
		FocusOnTouch.focus();
		tp.showKeyboard();
	}
	
	@Override
	public void softFocus() {
		FocusOnTouch.focus();
		tp.showSoftKeyboard();
	}

}
