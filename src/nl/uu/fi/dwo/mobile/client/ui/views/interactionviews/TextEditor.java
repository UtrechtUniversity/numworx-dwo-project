package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;

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

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

public class TextEditor  implements InteractionView, TouchStartHandler, FormuleEditorIF {
	
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
			System.out.println("mgwt.onTap: " + targetElement);
			if(targetElement == target || targetElement.getParentElement() == target)
				comRoot.getKeyboard().setEditor(deze);
				comRoot.getKeyboard().focus();
			
		}

	}

	private static final char Σ = 'Σ';
	private static final char KWADRAAT = '²';

	public class FXHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			FormuleEditor editor = new FormuleEditor();
			editor.insert("?");
			Panel panel = editor.getAsPanel();
			comRoot.getKeyboard().setEditor(editor);
			TouchDelegate wrap = new TouchDelegate(panel);
			wrap.addTapHandler(new Tapper(editor, panel.getElement()));
			panel.setWidth("30px");
			panel.setHeight("30px");
			panel.getElement().getStyle().setBackgroundColor("#808080");
			panel.getElement().getStyle().setDisplay(Style.Display.INLINE);
			sb.insert(cursor, '@');
			flow.insert(panel, cursor++);
		}

	}

	StringBuilder sb = new StringBuilder();
	
	private int width;
	private int height;
	private boolean volledigeBreedte;
	private int asHoogte;
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
		volledigeBreedte = h.getBoolean("volledigeBreedte");
		int menuheight = 0;
		VerticalPanel hbox = new VerticalPanel();
		Widget menubar, content;
		menubar = getMenuBar(launchdata);
		if(menubar != null) {
			menubar.setPixelSize(width, menuheight=30);
			hbox.add(menubar);
		}
		boolean boxMetRand = true;
		if(launchdata.containsKey("boxMetRand"))
				boxMetRand = launchdata.getBoolean("boxMetRand");
		
		content = getContent(launchdata);
		content.setPixelSize(width, height-menuheight);
		content.getElement().getStyle().setBackgroundColor("white");
		hbox.add(content);
		hbox.getElement().getStyle().setBackgroundColor("#C0C0C0");
		hbox.setPixelSize(width, height);
		if(boxMetRand)
			hbox.getElement().getStyle().setProperty("border", "thin solid black");
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
		if(cursorWidget != null)
			cursorWidget.setStyleDependentName("cursor", false);
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
		boolean graftool = true;
		if(launchdata.containsKey("formuleKnop")) formuleKnop = launchdata.getBoolean("formuleKnop");
		if(launchdata.containsKey("grafTool")) graftool = launchdata.getBoolean("grafTool");
		if(launchdata.containsKey("rekenTool")) rekentool = launchdata.getBoolean("rekenTool");
		
		FlowPanel menubar = new FlowPanel();
		Button fx = new Button("f(x)"); if(formuleKnop) menubar.add(fx);
		fx.addClickHandler(new FXHandler());
		Button calc = new Button("calc"); if(rekentool) menubar.add(calc);
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
		HashMap<String,Object> state = new HashMap<String,Object>();
		state.put("tekst", getText());
		return state;
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
		for (int i = 0; i < chars.length; i++) {
			if(chars[i] == '\n')
				enter();
			else
				insert(chars[i]);
		}
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enter() {
		sb.insert( cursor, '\n');
		flow.insert(new InlineHTML("<br>"), cursor); cursor++;
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
	}

	@Override
	public void breuk() {
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
	}

	@Override
	public void abs() {
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
	}

	@Override
	public void conjug() {
	}

	@Override
	public void sigma() {
		insert(Σ);
	}


}
