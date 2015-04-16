package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;
import com.googlecode.mgwt.ui.client.widget.touch.TouchDelegate;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Expressie;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

public class TextEditor  implements InteractionView, TouchStartHandler, FormuleEditorIF, FacetAware {
	
	class Tapper implements TapHandler {
		private FormuleEditorIF deze;
		private Element target;
		

		public Tapper(FormuleEditorIF deze, Element target) {
			this.deze = deze;
			this.target = target;
		}

		@Override
		public void onTap(TapEvent event) {
			Element targetElement = event.getTargetElement();
			if(targetElement == target || targetElement.getParentElement() == target)
			{	comRoot.getKeyboard().setEditor(deze);
				setCursorWidget(cursorWidget);
				comRoot.getKeyboard().softFocus();
			}
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
			editor.clearMain();
			editor.insert(text.substring(2, text.length()-1));
		}
		
		private FormuleEditor editor;
		
		private FormulaVak() {
			editor = new FormuleEditor();
			editor.insert("?");
			Panel panel = editor.getAsPanel();
			//comRoot.getKeyboard().setEditor(editor);
			TouchDelegate wrap = new TouchDelegate(panel);
			wrap.addTapHandler(new Tapper(editor, panel.getElement()));
			panel.setWidth("30px");
			panel.setHeight("30px");
			panel.getElement().getStyle().setBackgroundColor("#808080");
			panel.getElement().getStyle().setDisplay(Style.Display.INLINE);
			initWidget(panel);
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

		public CalculatorVak() {
			editor = new FormuleEditor();
			editor.insert('0');
			HorizontalPanel hbox = new HorizontalPanel();
			Panel panel = editor.getAsPanel();
			TouchDelegate wrap = new TouchDelegate(panel);
			wrap.addTapHandler(new Tapper(editor, panel.getElement()));
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
			String x = editor.toString();
			Expressie antwoord = FormuleParser.geefExpressie("$f" + x + "@");
			viewer.getAsPanel().removeFromParent();
			if(antwoord != null) 
			{
				x = String.valueOf(antwoord.geefWaarde());
			} 
			viewer = new FormuleViewer(x);
			((Panel) getWidget()).add(viewer.getAsPanel());
		}
		
		
	}
	
	
	
	public class FXHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			FormulaVak panel = new FormulaVak();
			sb.insert(cursor, '@');
			flow.insert(panel, cursor++);
			comRoot.getKeyboard().setEditor(panel.editor);
		}
	}
	
	class CalcHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			CalculatorVak panel = new CalculatorVak();
			sb.insert(cursor, '@');
			flow.insert(panel, cursor++);
			comRoot.getKeyboard().setEditor(panel.editor);
		}
		
	}

	StringBuilder sb = new StringBuilder();
	
	private int width;
	private int height;
	private boolean volledigeBreedte;
	private int asHoogte = 17;
	private OpdrNavIF comRoot;
	private FormuleFont defaultfont = FormuleFont.createFromFontSize(14);
	private FormuleFont font;
	
	private FlowPanel  flow;
	private int cursor;
	
	public TextEditor(HashMap<String, Object> currentVakGegevens,
			String[] randomVarNamen, HashMap<String, Object> randomVarWaarden) {
		ObjectMap h = JSONUtilities.wrapMap(currentVakGegevens);
		ObjectMap launchdata = h.getObjectMap("interactiePanelLaunchState");
		width = h.getInt("breedte");
		height = h.getInt("hoogte");
		volledigeBreedte = h.getBoolean("volledigeBreedte", false);
		int menuheight = 0;
		boolean boxMetRand;
		boxMetRand = launchdata.getBoolean("boxMetRand", true);
		int boxsize = boxMetRand?2:0;
		FlowPanel hbox = new FlowPanel();
		Widget menubar, content;
		menubar = getMenuBar(launchdata);
		if(menubar != null) {
			menubar.setPixelSize(width-boxsize, menuheight=30);
			hbox.add(menubar);
		}
		int padding = 4; // TODO bepaal padding;
		content = getContent(launchdata);
		content.setPixelSize(width-boxsize-padding, height-menuheight-boxsize-padding);
		Style style = content.getElement().getStyle();
		style.setPadding(padding/2, Unit.PX);
		style.setBackgroundColor("white");
		hbox.add(content);
		hbox.getElement().getStyle().setBackgroundColor("#C0C0C0");
		hbox.setPixelSize(width-boxsize, height-boxsize);
		if(boxMetRand)
			hbox.getElement().getStyle().setProperty("border", "1px solid gray");
		initWidget(hbox);
	}

	private void setState(ObjectMap h) {
		String tekst = h.getString("tekst");
		if(tekst == null) tekst = "";
		sb.setLength(0);
		cursor = 0;
		flow.clear();
		flow.add(setCursorWidget(new InlineHTML(" \u00A0")));
		insert(tekst);
		setCurrentElementRepaint();
	}

	private Widget cursorWidget;
	private Widget widget;
	
	private Widget getContent(ObjectMap launchdata) {
		TouchPanel touch = new TouchPanel();
		touch.addTapHandler(new Tapper(this,touch.getElement()));
		flow = touch; // XXX voorlopig ok
		setState(launchdata);
		return touch;
	}

	private Widget setCursorWidget(Widget widget) {
		if(widget == null) return cursorWidget;
		setCurrentElementRepaint();
		widget.setStyleDependentName("cursor", true);
		cursorWidget = widget;
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
		Button fx = new Button("f(x)"); if(formuleKnop) menubar.add(fx);
		fx.addClickHandler(new FXHandler());
		Button calc = new Button("calc"); if(rekentool) menubar.add(calc);
		calc.addClickHandler(new CalcHandler());
		Button graph = new Button("gr");  if(graftool) menubar.add(graph);
		
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
	{
		if(volledigeBreedte)
			this.width = breedte;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		this.asHoogte = ashoogte;
	}

	@Override
	public HashMap<String, Object> getState() {
		String sb = getAllText();
		HashMap<String,Object> state = new HashMap<String,Object>();
		state.put("tekst", sb.toString());
		return state;
	}

	private String getAllText() {
		StringBuilder sb = new StringBuilder();
		int count = flow.getWidgetCount();
		for(int i=0; i < count; i++) {
			Widget child = flow.getWidget(i);
			if(child instanceof HasText) {
				sb.append(((HasText) child).getText());
			}
		}
		return sb.toString();
	}

	private String getText() {
		return sb.toString();
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		setState( JSONUtilities.wrapMap(h));
	}

	@Override
	public int getScore() {
		return 0;
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
	
	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;

	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.stopPropagation();
		event.preventDefault();
		comRoot.getKeyboard().setEditor(this);
		
	}

	@Override
	public void clearAll() {
		flow.clear();
	}

	@Override
	public void insert(String text) {
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
						sb.insert(cursor, '@');
						flow.insert(fv,cursor++);
						break;
				}
				if (chars[i+1] == 'R') {
						CalculatorVak cv = new CalculatorVak();
						cv.setText(string);
						sb.insert(cursor, '@');
						flow.insert(cv, cursor++);
				}
				break;
			}
		}
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

	@Override
	public void setCurrentElementRepaint() {
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
		sb.insert( cursor, '\n');
		flow.insert(new Enter(), cursor); cursor++;
	}

	@Override
	public void removeCurrentElement() {
		if(cursor > 0)
		{	flow.remove(--cursor);
			sb.replace(cursor, cursor+1, "");
		}
		
	}

	@Override
	public void removeNextElement() {
		int max = flow.getWidgetCount()-1;
		if(cursor < max){
			flow.remove(cursor);
			setCursorWidget(flow.getWidget(cursor));
			sb.replace(cursor, cursor+1, "");
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
		SafeHtml html;
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(charAt);
		html = builder.toSafeHtml();
		sb.insert(cursor, charAt);
		flow.insert(new InlineHTML(html),cursor++);
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
	public void getResponses(List<String> responses) {
		responses.add(getAllText());
	}


}
