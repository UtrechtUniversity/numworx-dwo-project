package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;
import com.googlecode.mgwt.ui.client.widget.touch.TouchDelegate;












import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Algebra;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.DecRound;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.keyboard.EnterType;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.DWOLogger;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.utils.Logging;

public class TextEditor  implements InteractionView, TouchStartHandler, FormuleEditorIF, FacetAware, CBookEventListener, TekstElementWithFont {
	
	private static final int EXECUTE_HEIGHT = 30;
	private boolean editable = true;
	private static final Logger LOGGER = Logger.getLogger("TextEditor");
	private int lineHeight = 20;
	
	private final static String CIRCA = "\u2248";
	private final static String EXACT = "=";
	
	class FormuleTapper implements ClickHandler {

		private FormuleEditor deze;
		private Widget widget;
		@Override
		public void onClick(ClickEvent event) {
			FormuleKeyboardIF keyboard = comRoot.getKeyboard();
//			keyboard.setEditor(deze);
			keyboard.setEnterType(EnterType.APPLY);
//			keyboard.focus();
//			deze.requestFocus();
			setCursorWidget(widget);
			event.stopPropagation();
		}
		public FormuleTapper(FormuleEditor deze, Widget widget) {
			super();
			this.deze = deze;
			this.widget = widget;
		}
		
	}
	
	
	
	class Tapper implements ClickHandler {
		private FormuleEditorIF deze;
		private Element target;
		

		public Tapper(FormuleEditorIF deze, Element target) {
			this.deze = deze;
			this.target = target;
		}

		@Override
		public void onClick(ClickEvent event) {
//			Element targetElement = event.getTargetElement();
//			if(targetElement == null || targetElement == target || targetElement.getParentElement() == target)
			{	FormuleKeyboardIF keyboard = comRoot.getKeyboard();
				keyboard.setEditor(deze);
				keyboard.setEnterType(EnterType.ENTER);
				keyboard.softFocus();
				int flowTop = flow.getAbsoluteTop();
				int y = event.getClientY() - flowTop;
				int w;
				int i = 0;
				int max = flow.getWidgetCount()-1;
				if(i == max) i-=1;
				Widget widget;
				do {
					widget = flow.getWidget(++i);
					w = widget.getAbsoluteTop()-flowTop;
				} while ( i < max && w < y);
				LOGGER.fine("widget " + i + " at "  + w + " mouse at " + y + " c=" + cursor + " m=" + max);
				setCursorWidget(widget);
				if(cursor != max || w >= y) cursorToLeft(); // 1 terug
			}
		}

	}
	
	class TapForFocus implements TouchStartHandler, ClickHandler {
		private Widget cursorWidget;
		private HandlerRegistration registration;

		/**
		 * @param cursorWidget
		 */
		TapForFocus(Widget cursorWidget) {
			this.cursorWidget = cursorWidget;
			TouchDelegate wrap = new TouchDelegate(cursorWidget);
			registration = wrap.addTouchStartHandler(this);
		}

		TapForFocus(Widget cursorWidget, HasClickHandlers w) {
			this.cursorWidget = cursorWidget;
			registration = w.addClickHandler(this);
		}

		public void finalize() {
			registration.removeHandler();
			registration = null;
			cursorWidget = null;
		}
		
		@Override
		public void onTouchStart(TouchStartEvent event) {
			FormuleKeyboardIF keyboard = comRoot.getKeyboard();
			keyboard.setEditor(TextEditor.this);
			keyboard.setEnterType(EnterType.ENTER);
			setCursorWidget(cursorWidget);
			keyboard.softFocus();
			event.stopPropagation();
			event.preventDefault();
		}

		@Override
		public void onClick(ClickEvent event) {
			FormuleKeyboardIF keyboard = comRoot.getKeyboard();
			keyboard.setEditor(TextEditor.this);
			setCursorWidget(cursorWidget);
			keyboard.setEnterType(EnterType.ENTER);
			keyboard.softFocus();
			
			event.stopPropagation();
			event.preventDefault();
			
		}
		
	}

	private static final char Σ = 'Σ';
	private static final char KWADRAAT = '²';
	private static final char WORTEL = '√';
	private static final char INTEGRAAL = '∫';

	
	private class FormulaVak extends Composite implements HasText {

		
		@Override
		public String getText() {
			return "$f" + editor.toString() + "@";
		}

		@Override
		public void setText(String text) {
			if(!editable) return;
			editor.clearMain();
			editor.insert(text.substring(2, text.length()-1));
		}
		
		private FormuleEditor editor;
		final Panel panel;

		private void setFont(FormuleFont font) {
			editor.setFont(font);
			editor.setDefaultFont(font);
		}
		
		
		private FormulaVak() {
			editor = new FormuleEditor() {

				@Override
				public void resize() {
					int h = editor.getHeight();
					int a = editor.getMainRegel().getAsHoogte();
					panel.getElement().getStyle().setVerticalAlign(a-h, Unit.PX);
				}
				@Override
				public void enter() {
					setCurrentElementRepaint();
					TextEditor.this.cursorToRight();
					TextEditor.this.requestFocus();
				}
			};
			editor.setFormuleToolBijFocus(true);
			setFont(font);
			//editor.insert();
			panel = editor.getAsPanel();
			//comRoot.getKeyboard().setEditor(editor);
			TouchDelegate wrap = new TouchDelegate(panel);
			panel.addDomHandler(new FormuleTapper(editor, this), ClickEvent.getType());
			FormuleEditorTouchHandler h = new FormuleEditorTouchHandler(editor) {

				@Override
				public void onTouchEnd(TouchEndEvent event) {
					super.onTouchEnd(event);
					setCursorWidget(FormulaVak.this);
				} 
			};
			wrap.addTouchHandler(h);
			editor.resize();
			initWidget(panel);
		}

		private void start() {
			editor.clearSelection();
			editor.startSelection(0, 0);
			editor.endSelection(0, 0);
		}
	}
	
	private class CalculatorVak extends Composite implements HasText, ClickHandler {

		@Override
		public String getText() {
			return "$R" + editor.toString() + "@";
		}

		@Override
		public void setText(String text) {
			editor.clearMain();
			editor.insert(text.substring(2, text.length()-1));
			onClick(null);
		}		
		private FormuleEditor editor;
		private FormuleViewer viewer;
		private Button btn;
		private boolean op3;

		public CalculatorVak() {
			editor = new FormuleEditor() {

				@Override
				public void enter() {
					calculate();
				} };
			editor.setFormuleToolBijFocus(true);
			editor.insert('0');
			HorizontalPanel hbox = new HorizontalPanel();
			hbox.setStylePrimaryName("insert_calculator");
			Panel panel = editor.getAsPanel();
			TouchDelegate wrap = new TouchDelegate(panel);
			FormuleEditorTouchHandler h = new FormuleEditorTouchHandler(editor) {

				@Override
				public void onTouchEnd(TouchEndEvent event) {
					super.onTouchEnd(event);
					setCursorWidget(CalculatorVak.this);
				} 
			};
			wrap.addTouchHandler(h);
			hbox.addDomHandler(new FormuleTapper(editor, this), ClickEvent.getType());
			hbox.add(panel);
			btn = new Button("=");
			btn.addClickHandler(this);
			hbox.add(btn);
			viewer = new FormuleViewer("0");
			hbox.add(viewer.getAsPanel());
			initWidget(hbox);
		}

		@Override
		public void onClick(ClickEvent event) {
			calculate();
			editor.requestFocus();
		}

		static final double E_MAX = 1.0E7;
		static final double E_MIN = 1.0E-3;
		static final double MARGE = 0.00000000000000001;
		
		private void calculate() {
			op3 = !op3;
			String x = editor.toString();
			Expressie antwoord = FormuleParser.geefExpressie("$f" + x + "@");
			viewer.getAsPanel().removeFromParent();
			x="";
			if(antwoord != null) 
			{
				double waarde = antwoord.geefWaarde();
				double afgerond = new DecRound(new BasisExpressie(waarde), new BasisExpressie(3)).geefWaarde();
				boolean afgerondOp3 = 
						! Algebra.isGelijkDouble(waarde, afgerond, MARGE);
				if(Double.isNaN(waarde))
				{	x = "?";
					btn.setText(EXACT);
				} else
				{ 
					btn.setText(afgerondOp3 ? CIRCA : EXACT);
					if(op3)
					{
						x = Expressie.df3.format(waarde);
					}
					else 
					{	double abs = Math.abs(waarde);
						if( abs < E_MIN || abs >= E_MAX) 
						{
							x = Expressie.dfe.format(waarde);						
							x = x.replace("E", "*10$m") + "@";
						} else {
							x = Expressie.df.format(waarde);
						}
					}
				}
				//x = String.valueOf(antwoord.geefWaarde());
			} 
			viewer = new FormuleViewer(x);
			((Panel) getWidget()).add(viewer.getAsPanel());
		}
		
		
	}
	
	
	
	public class FXHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			if(!editable) return;
			FormulaVak panel = new FormulaVak();
			panel.start();
			//sb.insert(cursor, '@');
			flow.insert(panel, cursor++);
			//comRoot.getKeyboard().setEditor(panel.editor);
			panel.editor.requestFocus();
			//FocusOnTouch.focus();
		}
	}
	
	class CalcHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			if (!editable) return;
			CalculatorVak panel = new CalculatorVak();
			//sb.insert(cursor, '@');
			flow.insert(panel, cursor++);
			panel.editor.requestFocus();;
		}
		
	}

	//StringBuilder sb = new StringBuilder();
	
	private int width;
	private int height;
	private boolean volledigeBreedte;
	private int asHoogte = 17;
	OpdrNavIF comRoot;
	private FormuleFont defaultfont = FormuleFont.createFromFontSize(14);
	private FormuleFont font = defaultfont;
	
	private FlowPanel  flow;
	private int cursor;
	private Widget menubar, content;
	FlowPanel hbox;
	
	private boolean boxMetRand;
	private int boxsize;
	int menuheight = 0;
	int padding = 4; // TODO bepaal padding;
	
	Logging logging;
	private String lastAttempt;
	
	TextEditor(int breedte, int hoogte, boolean boxMetRand) { // voor checktextantwoordvak
		this.width = breedte;
		this.height = hoogte;
		this.boxMetRand = boxMetRand;
		this.volledigeBreedte = false;
		this.padding = 0;
		boxsize = boxMetRand?2:0;
		hbox = new FlowPanel();
		hbox.setStylePrimaryName("textEditor");
		initWidget(hbox);
		menubar = null;
		content = getContent(null);
		content.setPixelSize(width-boxsize-padding, height-menuheight-boxsize-padding);
		Style style = content.getElement().getStyle();
		style.setPadding(padding/2, Unit.PX);
		//style.setBackgroundColor("white");
		style.setOverflow(Overflow.HIDDEN);
		hbox.add(content);
		//hbox.getElement().getStyle().setBackgroundColor("#C0C0C0");
		hbox.setPixelSize(width-boxsize, height-boxsize);
		if(boxMetRand)
			hbox.getElement().getStyle().setProperty("border", "1px solid gray");
		logging = null;
		shown = true;
	}
	
	public TextEditor(HashMap<String, Object> currentVakGegevens,
			String[] randomVarNamen, HashMap<String, Number> randomVarWaarden) {
		ObjectMap h = JSONUtilities.wrapMap(currentVakGegevens);
		ObjectMap launchdata = h.getObjectMap("interactiePanelLaunchState");
		width = h.getInt("breedte");
		height = h.getInt("hoogte");
		volledigeBreedte = h.getBoolean("volledigeBreedte", false);
		boxMetRand = launchdata.getBoolean("boxMetRand", true);
		boxsize = boxMetRand?2:0;
		hbox = new FlowPanel();
		hbox.setStylePrimaryName("textEditor");
		initWidget(hbox);
		
		menubar = getMenuBar(launchdata);
		if(menubar != null) {
			menuheight = 30;
			menubar.setPixelSize(width-boxsize, menuheight);
			hbox.add(menubar);
		}
		
		content = getContent(launchdata);
		content.setPixelSize(width-boxsize-padding, height-menuheight-boxsize-padding);
		Style style = content.getElement().getStyle();
		style.setPadding(padding/2, Unit.PX);
		//style.setBackgroundColor("white");
		//style.setOverflow(Overflow.AUTO);
		hbox.add(content);
		//hbox.getElement().getStyle().setBackgroundColor("#C0C0C0");
		hbox.setPixelSize(width-boxsize, height-boxsize);
		if(boxMetRand)
			hbox.getElement().getStyle().setProperty("border", "1px solid gray");

		boolean logOption = launchdata.getBoolean("logOption", false);
		if(logOption) {
			String logID = launchdata.getString("logID");
			DWOLogger dwologger = new DWOLogger();
			dwologger.setClassName("fi.wiskopdr.tekstobjects.TekstEditor");
			dwologger.setLogID(logID);
			logging = dwologger;
		}
		shown = true;
	}

	protected void requestFocus() {
		comRoot.getKeyboard().setEditor(this);
		FocusOnTouch.focus();
		setCursorWidget(cursorWidget);
	}

	private void setState(ObjectMap h) { // h kan null zijn!
		editable = true;
		shown = false;
		String tekst = h == null ? "" : h.getString("tekst");
		if(tekst == null) tekst = "";
		else if(tekst.endsWith("\n"))
			tekst = tekst.substring(0, tekst.length()-1);
		//sb.setLength(0);
		clearAll();
		insert(tekst);
		lastAttempt = tekst;
		removeCursor();
		editable = h == null || h.getBoolean("editable", true);
		if(!editable) setReadonly();
		shown = true;
	}

	private Widget cursorWidget;
	private Widget widget;
	
	private Widget getContent(ObjectMap launchdata) {
		FlowPanel touch = new FlowPanel();
		touch.addDomHandler(new Tapper(this,touch.getElement()), ClickEvent.getType());
		flow = touch; // XXX voorlopig ok
		setState(launchdata);
		return touch;
	}

	private Widget setCursorWidget(Widget widget) {
		if(widget == null) return cursorWidget;
		removeCursor();
		widget.setStyleDependentName("cursor", true);
		cursorWidget = widget;
		int c = flow.getWidgetIndex(widget);
		if(c >= 0)
			cursor = c;
		showCursor();
		return widget;
	}

	private Widget getMenuBar(ObjectMap launchdata) {
		boolean balkZichtbaar = true;
		if(launchdata.containsKey("balkZichtbaar")) balkZichtbaar = launchdata.getBoolean("balkZichtbaar");
		if(!balkZichtbaar) return null;
		
		boolean rekentool = true;
		boolean formuleKnop = true;
		boolean formuleToolPopup = true;
		boolean graftool = false;
		formuleKnop = launchdata.getBoolean("formuleKnop", formuleKnop);
		//if(launchdata.containsKey("grafTool")) graftool = launchdata.getBoolean("grafTool");
		rekentool = launchdata.getBoolean("rekenTool", rekentool);
		
		FlowPanel menubar = new FlowPanel();
		menubar.setStylePrimaryName("balk");
		//Button fx = new Button("f(x)"); 
		PushButton fx = new PushButton(new Image(DWOplayer.PARAMETERS.getResource("images/resources/formuleknop.gif")));
		fx.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		if(formuleKnop) menubar.add(fx);
		fx.addClickHandler(new FXHandler());
		//Button calc = new Button("calc"); 
		Image upImage = new Image(DWOplayer.PARAMETERS.getResource("images/resources/rmknop.gif"));
		upImage.getElement().setAttribute("width","18");
		upImage.getElement().setAttribute("height","18");
		PushButton calc = new PushButton(upImage);
		calc.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		if(rekentool) menubar.add(calc);
		calc.addClickHandler(new CalcHandler());
		//Button graph = new Button("gr");  
		PushButton graph = new PushButton(new Image(DWOplayer.PARAMETERS.getResource("images/resources/grafiekknop.gif")));
		if(graftool) menubar.add(graph);
		
		return menubar;
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	private void initWidget(Widget w) {
		this.widget = w;
	}

	@Override
	public int getAsHoogte() {
		return (asHoogte);
	}

	@Override
	public int getHeight() {
		return (height);
	}

	@Override
	public int getWidth() {
		return (width);
	}
	
	public void zetVolledigeBreedte(int breedte)
	{	if(volledigeBreedte)
		{	this.width = breedte;
			if(menubar != null)
				menubar.setPixelSize(width-boxsize, menuheight);
			content.setPixelSize(width-boxsize-padding, height-menuheight-boxsize-padding);
			hbox.setPixelSize(width-boxsize, height-boxsize);
		}
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		this.asHoogte = ashoogte;
	}

	@Override
	public HashMap<String, Object> getState() {
		StringBuilder allText = getAllText();
		setAttempt(allText);
		String sb = allText.append('\n').toString();
		HashMap<String,Object> state = new HashMap<String,Object>();
		state.put("tekst", sb);
		state.put("editable", editable);
		return state;
	}

	private void setAttempt(StringBuilder s) {
		if(logging != null ) {
			String string = s.toString();
			if(! string.equals(lastAttempt)) {
				lastAttempt = string;
				HashMap<String, String> map = new HashMap<String, String>();
				string = string.replace('\n', ' ');
				string = string.replace(";", ".,");
				map.put("response", string);
				map.put("action", "modify");
				map.put("success", "null");
				logging.log(map);
		}}
		
	}

	private StringBuilder getAllText() {
		StringBuilder sb = new StringBuilder();
		int count = flow.getWidgetCount()-1;
		for(int i=0; i < count; i++) {
			Widget child = flow.getWidget(i);
			if(child instanceof HasText) {
				sb.append(((HasText) child).getText());
			}
		}
		return sb;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		addExecuteBtn(comRoot); // last change, all listeners are there.
		if(h == null) setStateNull();
		else setState( JSONUtilities.wrapMap(h));
	}

	private void setStateNull() {
	}

	@Override
	public int getScore() {
		return 0;
	}
	
	@Override
	public int[][] getScoreObjectives()
	{
		return null;
	}

	@Override
	public Boolean isCorrect() {
		return Boolean.TRUE;
	}

	@Override
	public void kijkNa() {
	}

	@Override
	public void zetNagekeken(boolean b) {
		
	}
	
	private static final String ACTION_NOT_EDITIABLE = "action.setNotEditable";
	private static final String TEXT = "text";
	
	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		comRoot.addCBookEventListener(ACTION_NOT_EDITIABLE, this);
		comRoot.addCBookEventListener(TEXT, this);
		if(logging != null)
			logging.setCommunicationRoot(comRoot);
	}

	private void addExecuteBtn(final OpdrNavIF comRoot) {
		if(comRoot.hasListeners(TEXT)) {
			Button btn = new Button(fi.wiskopdr.text.Text.constants.executeLabel());
			Style style = btn.getElement().getStyle();
			style.setWidth(100, Style.Unit.PCT);
			style.setHeight(EXECUTE_HEIGHT, Style.Unit.PX);
			content.setPixelSize(-1, height-menuheight-boxsize-padding-EXECUTE_HEIGHT);
			btn.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Map<String, String> map = new HashMap<String,String>();
					map.put("content", getAllText().toString());
					comRoot.fireEvent(new CBookEvent(TextEditor.this, TEXT, map));
					
				}});
			hbox.add(btn);
		}
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.stopPropagation();
		event.preventDefault();
		comRoot.getKeyboard().setEditor(this);
		
	}

	@Override
	public void clearAll() {
		cursor = 0;
		flow.clear();
		flow.add(setCursorWidget(new InlineHTML(" \u00A0")));
	}

	@Override
	public void insert(String text) {
		if(!editable) return;
		char[] chars = text.toCharArray();
		int next = 1;
		for (int i = 0; i < chars.length; i+=next) {
			next = 1;
			switch (chars[i])
			{
			case '\n': enter(); break;
			default: insert(chars[i]); break;
			case '$' : 
				next = findAt(chars, i, chars.length);
				String string = new String(chars, i, next);
				if(chars[i+1] == 'f')
				{		
						FormulaVak fv = new FormulaVak();
						fv.setText(string);
//						sb.insert(cursor, '@');
						flow.insert(fv,cursor++);
						break;
				}
				if (chars[i+1] == 'R') {
						CalculatorVak cv = new CalculatorVak();
						cv.setText(string);
//						sb.insert(cursor, '@');
						flow.insert(cv, cursor++);
				}
				break;
			}
		}
		showCursor();
	}

	private int findAt(char[] chars, int i, int length) {
		int bal = 0;
		for (int j = i+1; j < chars.length; j++) {
			if(chars[j] == '$') bal++;
			else if(chars[j]== '@') {
				if( bal-- <= 0) 
					return j-i+1;
			}
		}
		return length;
	}

	@Override
	public FormuleFont getDefaultFont() {
		return defaultfont;
	}

	@Override
	public void setFont(FormuleFont font) {
		this.font = font;
	}

	@Override // loose focus
	public void setCurrentElementRepaint() {
		removeCursor();
		setAttempt();
		if(comRoot != null)
			comRoot.getKeyboard().setEnterType(EnterType.APPLY);
	}

	private void removeCursor() {
		if(cursorWidget != null)
			cursorWidget.setStyleDependentName("cursor", false);
	}

	private class Enter extends InlineHTML implements HasText {
		private Enter() {
			super("<br>");
		}
		public String getText() { return "\n"; }
	}
	
	@Override
	public void enter() {
		if(!editable) return;
//		sb.insert( cursor, '\n');
		flow.insert(new Enter(), cursor); cursor++;
		showCursor();
		setAttempt();
	}

	private void setAttempt() {
		if(shown && logging != null) setAttempt(getAllText());
	}

	@Override
	public void removeCurrentElement() {
		if(cursor > 0 && editable)
		{	flow.remove(--cursor);
//			sb.replace(cursor, cursor+1, "");
			showCursor();
		}
	}

	@Override
	public void removeNextElement() {
		int max = flow.getWidgetCount()-1;
		if(cursor < max && editable){
			flow.remove(cursor);
			setCursorWidget(flow.getWidget(cursor));
//			sb.replace(cursor, cursor+1, "");
		}
	}

	@Override
	public void cursorToLeft() {
		if(cursor > 0){
			cursor --;
			setCursorWidget(flow.getWidget(cursor));
		}
	}

	@Override
	public void cursorToRight() {
		int max = flow.getWidgetCount()-1;
		if(cursor < max){
			cursor ++;
			setCursorWidget(flow.getWidget(cursor));	}
		}
	
	@Override
	public void cursorToLeftShift() {
		if(cursor > 0){
			cursor --;
			setCursorWidget(flow.getWidget(cursor));
		}
	}

	@Override
	public void cursorToRightShift() {
		int max = flow.getWidgetCount()-1;
		if(cursor < max){
			cursor ++;
			setCursorWidget(flow.getWidget(cursor));	}
	}
	
	
	@Override
	public void cursorUp() {
	}
	
	@Override
	public void cursorDown() {
		
	}

	
	
	
	@Override
	public void insert(char charAt) {
		if(!editable) return;
		SafeHtml html;
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(charAt);
		html = builder.toSafeHtml();
//		sb.insert(cursor, charAt);
		InlineHTML w = new InlineHTML(html);
		new TapForFocus(w,w);
		flow.insert(w,cursor++);
		showCursor();
	}

	private boolean shown;
	private void showCursor() {
		if(shown)
			cursorWidget.getElement().scrollIntoView();
	}

	@Override
	public String getSelectionString() {
		return "";
	}
	
	@Override
	public void kopieer(FormuleClipboardIF clip) {
	}
	
	@Override
	public void knip(FormuleClipboardIF clip) {
	}
	
	@Override
	public void plak(FormuleClipboardIF clip) {
		insert(clip.getClipboard());
	}

	@Override
	public void macht() {
	}

	@Override
	public void wortel() {
		insert(WORTEL);
	}

	@Override
	public void breuk() {
		insert('/');
	}

	@Override
	public void kwadraat() {
		insert(KWADRAAT);
	}

	@Override
	public void ndewortel() {
	}

	@Override
	public void haakjes() {
		insert('(');insert(')');
	}

	@Override
	public void integraal() {		
	}

	@Override
	public void prv() {
	}

	@Override
	public void ndelog() {
		insert("log");
	}

	@Override
	public void abs() {
		insert("| |");
	}

	@Override
	public void subscript() {
	}

	@Override
	public void bin() {
	}

	@Override
	public void diff() {
	}
	
	@Override
	public void diff_partial() {
	}

	@Override
	public void limiet0() {
	}

	@Override
	public void limiet1() {
	}

	@Override
	public void limiet2() {
	}

	@Override
	public void primitieve() {
		insert(INTEGRAAL);
	}

	@Override
	public void conjug() {
	}

	@Override
	public void sigma() {
		insert(Σ);
	}
	
	@Override
	public void stelsel() {
		
	}

	@Override
	public void getResponses(List<String> responses) {
		responses.add(getAllText().toString());
	}

	@Override
	public void acceptCBookEvent(CBookEvent event) {
		String command = event.getCommand();
		if(ACTION_NOT_EDITIABLE.equals(command)) {
			setReadonly();
		} else 
		if(TEXT.equals(command))
		{
			String text = (String)event.getParameter("content");
			if(text == null) text = "";
			clearAll();
			insert(text);
		}
		
	}

	private void setReadonly() {
		editable = false;
		widget.setStyleDependentName("readonly", !editable);
	}

	@Override
	public void setFontSize(int font_size) {
		widget.getElement().getStyle().setFontSize(font_size, Unit.PX);
	}

	@Override
	public void setFontName(String name) {
		widget.getElement().getStyle().setProperty("fontFamily", name);	
	}

	@Override
	public void setFontStyle(int font_style) {
	}

	@Override
	public void setParentRegel(TekstRegel regel) {
		font = regel.getFont();
	}

	@Override
	public void tab() {
		Widget parent = asWidget();
		while (parent != null && !(parent instanceof TekstRegel))
		{
			parent = parent.getParent();
		}
		
		if(parent != null && parent instanceof TekstRegel)
		{
			((TekstRegel) parent).getTekstVak().tabFocus(this, true);
		}
	}

	@Override
	public void shiftTab() {
		Widget parent = asWidget();
		while (parent != null && !(parent instanceof TekstRegel))
		{
			parent = parent.getParent();
		}
		
		if(parent != null && parent instanceof TekstRegel)
		{
			((TekstRegel) parent).getTekstVak().shiftTabFocus(this, true);
		}
	}

	String getText() {
		return getAllText().toString();
	}


}
