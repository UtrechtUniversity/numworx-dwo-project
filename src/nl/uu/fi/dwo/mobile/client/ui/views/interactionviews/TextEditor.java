package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;

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
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class TextEditor extends Composite implements InteractionView, TouchStartHandler, FormuleEditorIF, TapHandler {

	public class FXHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			FormuleEditor editor = new FormuleEditor();
			editor.insert("?");
			Panel panel = editor.getAsPanel();
			panel.setWidth("30px");
			panel.setHeight("30px");
			panel.getElement().getStyle().setBackgroundColor("#808080");
			flow.insert(panel, cursor++);
		}

	}

	private int width;
	private int height;
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
		
		VerticalPanel hbox = new VerticalPanel();
		Widget menubar, content;
		menubar = getMenuBar(launchdata);
		menubar.setPixelSize(width, 30);
		content = getContent(launchdata);
		content.setPixelSize(width, height-30);
		content.getElement().getStyle().setBackgroundColor("#F0F0F0");
		hbox.add(menubar);
		hbox.add(content);
		hbox.getElement().getStyle().setBackgroundColor("#C0C0C0");
		hbox.setPixelSize(width, height);
		initWidget(hbox);
	}

	private Widget cursorWidget;
	private Widget getContent(ObjectMap launchdata) {
		TouchPanel touch = new TouchPanel();
		touch.addTapHandler(this);
		flow = touch; // XXX voorlopig ok
		flow.add(setCursorWidget(new InlineHTML(" \u00A0")));
		
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
		FlowPanel menubar = new FlowPanel();
		Button fx = new Button("f(x)"); menubar.add(fx);
		fx.addClickHandler(new FXHandler());
		Button calc = new Button("calc"); menubar.add(calc);
		Button graph = new Button("gr");  menubar.add(graph);
		return menubar;
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public int getAsHoogte() {
		return 0;
	}

	@Override
	public int getHeight() {
		return width;
	}

	@Override
	public int getWidth() {
		return height;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		this.asHoogte = ashoogte;

	}

	@Override
	public HashMap<String, Object> getState() {
		HashMap<String,Object> state = new HashMap<String,Object>();
		return state;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getScore() {
		return 0;
	}

	@Override
	public boolean isCorrect() {
		return true;
	}

	@Override
	public void kijkNa() {
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
		SafeHtml html;
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.appendEscaped(text);
		html = builder.toSafeHtml();
		flow.insert(new InlineHTML(html),cursor);		
		
		
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
		flow.insert(new InlineHTML("<br>"), cursor); cursor++;
		
	}

	@Override
	public void removeCurrentElement() {
		if(cursor > 0)
			flow.remove(--cursor);
		
	}

	@Override
	public void removeNextElement() {
		int max = flow.getWidgetCount()-1;
		if(cursor < max){
			flow.remove(cursor);
			setCursorWidget(flow.getWidget(cursor));
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
	public void insert(char charAt) {
		SafeHtml html;
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(charAt);
		html = builder.toSafeHtml();
		flow.insert(new InlineHTML(html),cursor++);		
	}

	@Override
	public String getSelectionString() {
		return "";
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
		flow.insert(new InlineHTML("²"), cursor);cursor++;
	}

	@Override
	public void ndewortel() {
	}

	@Override
	public void haakjes() {
		flow.insert(new InlineHTML("()"), cursor);cursor++;
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
		flow.insert(new InlineHTML("Σ"), cursor);cursor++;
	}

	@Override
	public void onTap(TapEvent event) {
		comRoot.getKeyboard().setEditor(this);
	}

}
