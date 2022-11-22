package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Promise;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.HandlerRegistrations;
import com.vaadin.pointerevents.client.PointerDownEvent;
import com.vaadin.pointerevents.client.PointerDownHandler;
import com.vaadin.pointerevents.client.PointerMoveEvent;
import com.vaadin.pointerevents.client.PointerMoveHandler;
import com.vaadin.pointerevents.client.PointerUpEvent;
import com.vaadin.pointerevents.client.PointerUpHandler;

import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.WiskOpdr;
import fi.wiskopdr.expressies.Algebra;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.text.Text;
import fi.wiskopdr.expressies.DecRound;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.ideas.client.AbstractRule;
import nl.uu.fi.dwo.ideas.client.RuleIF;
import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleEditorIF;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.keyboard.EnterType;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.DWOplayerCss;
import nl.uu.fi.dwo.mobile.client.sco.CorrectieFacade;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.SVGButton.ButtonListener;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.utils.LogBuilder;
import nl.uu.fi.dwo.mobile.utils.Logging;

@SuppressWarnings("deprecation")
public class TextEditor  implements InteractionStub, TouchStartHandler, FormuleEditorIF, FacetAware, CBookEventListener, TekstElementWithFont, HasText {
	
	private static final class PreventTapper implements PointerDownHandler {
    @Override
    public void onPointerDown(PointerDownEvent event) {
    	GWT.log(" on pointer ");
    	event.preventDefault();
    	event.stopPropagation();
    }
  }



  public interface IsEditable {

		void setEditable(boolean b);

	}

	private static final int EXECUTE_HEIGHT = 30;
	private static final Logger LOGGER = Logger.getLogger("TextEditor");
	private static DWOplayerCss css = DWOplayer.DWO_BUNDLE.dwoplayercss();
	
	private final static String CIRCA = "\u2248";
	private final static String EXACT = "=";
	
	private static final char Σ = 'Σ';
	private static final char KWADRAAT = '²';
	private static final char WORTEL = '√';
	private static final char INTEGRAAL = '∫';

	private static final String ACTION_NOT_EDITIABLE = "action.setNotEditable";
	private static final String TEXT = "text";
	
	private boolean editable = true;
	private int lineHeight = 20;
	
	private int width;
	private int height;
	private boolean volledigeBreedte, pasAanH;
	private int asHoogte = 17;
	OpdrNavIF comRoot;
	private FormuleFont defaultfont = FormuleFont.createFromFontSize(14);
	private FormuleFont font = defaultfont.createCopy();
	
	private FlowPanel  flow;
	private int cursor, selectionEnd;
	private Widget menubar, content;
	FlowPanel hbox;
	
	private Widget cursorWidget;
	private Widget widget;
	private boolean shown, childfocus;
	
	private boolean boxMetRand;
	private int borderWidth = (Integer)DWOplayer.templateConstants.answerboxFEWA("border-width");
	private int boxsize;
	int menuheight = 0;
	int padding = 4; // TODO bepaal padding;
	int paddingH = 0; // padding Hbox, actief bij pasAanH
	
	Logging logging;
	private String lastAttempt;
	private CorrectieFacade correctie;
	private boolean teltMee;
    private int scoreMax;
	private String loggingID;
	private AnimationHandle handle;
	private final ActivityInterface activity;
	private TekstRegel regel;
	private com.google.gwt.user.client.Element formuleElement;
	
	TextEditor(ActivityInterface a, int breedte, int hoogte, boolean boxMetRand)
	{
		this.activity = a;
		// voor checktextantwoordvak
		this.width = breedte;
		this.height = hoogte;
		this.boxMetRand = boxMetRand;
		this.volledigeBreedte = false;
		this.padding = 0;
		boxsize = boxMetRand?2*borderWidth:0;
		hbox = new FlowPanel();
		hbox.setStyleName(css.textEditor());
		hbox.addStyleName(css.textEditor_nowrap());
		initWidget(hbox);
		menubar = null;
		content = getContent(null);
		content.setPixelSize(width - boxsize - padding, 13);
		Style style = content.getElement().getStyle();
		style.setPadding(padding / 2, Unit.PX);
		int top = (height - menuheight - boxsize - padding - 13) / 2;
		style.setMarginTop(top, Unit.PX);
		//style.setBackgroundColor("white");
		style.setOverflow(Overflow.HIDDEN);
		hbox.add(content);
		//hbox.getElement().getStyle().setBackgroundColor("#C0C0C0");
		hbox.setPixelSize(width - boxsize, height - boxsize);
		//if (boxMetRand)
		//	hbox.getElement().getStyle().setProperty("border", "1px solid gray");
		if (!boxMetRand) {
				hbox.getElement().getStyle().setProperty("border", "none");
				hbox.getElement().getStyle().setBackgroundColor("transparent");
				content.getElement().getStyle().setBackgroundColor("transparent");
		}
		logging = null;
		//shown = true;
	}
	
	public TextEditor(ActivityInterface a, HashMap<String, Object> currentVakGegevens,
			String[] randomVarNamen, HashMap<String, Number> randomVarWaarden)
	{
		this.activity = a;
		ObjectMap h = JSONUtilities.wrapMap(currentVakGegevens);
		ObjectMap launchdata = h.getObjectMap("interactiePanelLaunchState");
		width = h.getInt("breedte");
		height = h.getInt("hoogte");
		volledigeBreedte = h.getBoolean("volledigeBreedte", false);
		hbox = new FlowPanel();
		initWidget(hbox);
		init0(launchdata);
	}

	public void init0(ObjectMap launchdata) {
		boxMetRand = launchdata.getBoolean("boxMetRand", true);
		pasAanH = launchdata.getBoolean("pasAanH", false);

		if(teltMee = launchdata.containsKey("scoreMax")) 
			scoreMax = launchdata.getInt("scoreMax");
//		if(launchdata.containsKey("teltMee")) 
//			teltMee = launchdata.getBoolean("teltMee");
		
		
		boxsize = boxMetRand?2*borderWidth:0; // eigenlijk afhankelijk van de borderwidth. die is max 2
		hbox.setStyleName(css.textEditor());
		hbox.addStyleName(DWOplayer.templateCss().answerboxFEWS());
		
		menubar = getMenuBar(launchdata);
		if (menubar != null)
		{
			menuheight = 30;
			menubar.setPixelSize(width-boxsize, menuheight);
			Style style = menubar.getElement().getStyle();
			style.setBackgroundColor("transparent");//CssColor.make(229,240,249).toString());
			style.setOverflowY(Overflow.HIDDEN);
			if (pasAanH) {
				style.clearWidth();
				style.setFloat(Style.Float.RIGHT);
				menuheight=10;
			}
			hbox.add(menubar);
		}
		
		content = getContent(launchdata);
		content.setPixelSize(width-boxsize-padding, pasAanH ? -1 : height-menuheight-boxsize-padding);
		Style style = content.getElement().getStyle();
		if (pasAanH) {
			style.setDisplay(Style.Display.INLINE);
			style.clearWidth();
			paddingH = padding; // Effect
			padding = 0; // No effect 
			hbox.getElement().getStyle().setPadding(paddingH/2, Style.Unit.PX);
		} else {
			style.setPadding(padding/2, Unit.PX);
		}
		
		//style.setBackgroundColor("white");
		//style.setOverflow(Overflow.AUTO);
		hbox.add(content);
		//hbox.getElement().getStyle().setBackgroundColor("#C0C0C0");
		hbox.setPixelSize(width-boxsize-paddingH, height-boxsize-paddingH);
		if (boxMetRand)
			;//hbox.getElement().getStyle().setProperty("border", "1px solid gray");
		else
		{	updateEmpty();
			style.setBackgroundColor("transparent");
			hbox.getElement().getStyle().setProperty("border", "none");
			hbox.getElement().getStyle().setBackgroundColor("transparent");
			
		}
		
		LogBuilder logBuilder = activity.logBuilder().setClassName("fi.wiskopdr.tekstobjects.TekstEditor").setLaunchData(launchdata);
		loggingID = logBuilder.getLogID();
		logging = logBuilder.build();
		//shown = true;
	}

	public TextEditor(ActivityInterface a, int w, int h, boolean rand, boolean formule) {
		this.activity = a;
		width = w;
		height = h;
		volledigeBreedte = false;
		boxMetRand = rand;

		boxsize = boxMetRand?2:0;
		hbox = new FlowPanel();
		hbox.setStyleName(css.textEditor());
		initWidget(hbox);
		
		menubar = formule ? getMenuBar(formule, false, false) : null;
		if (menubar != null)
		{
			menuheight = 30;
			menubar.setPixelSize(width-boxsize, menuheight);
			hbox.add(menubar);
		}
		
		content = getContent(null);
		content.setPixelSize(width-boxsize-padding, height-menuheight-boxsize-padding);
		Style style = content.getElement().getStyle();
		style.setPadding(padding/2, Unit.PX);
		//style.setBackgroundColor("white");
		//style.setOverflow(Overflow.AUTO);
		hbox.add(content);
		//hbox.getElement().getStyle().setBackgroundColor("#C0C0C0");
		hbox.setPixelSize(width-boxsize, height-boxsize);
		if (boxMetRand)
			hbox.getElement().getStyle().setProperty("border", "1px solid gray");
		else
		{
			updateEmpty();
		}
	}
	
	
	
	public TextEditor(ActivityInterface a) {
		this.activity = a;
		hbox = new FlowPanel();
		initWidget(hbox);
	}
	
	public void init(int width, int height, Map<String, Object> launchData, Map<String, Number> values) {
		this.width = width;
		this.height = height;
		this.widget.setPixelSize(width, height);
		init0(JSONUtilities.wrapMap(launchData));
	}

	void updateEmpty() {
		if (!boxMetRand || menubar!=null) {
			hbox.setStyleName(css.textEditor_empty(), isContentEmpty());
		}
	}
		
	private boolean isContentEmpty() {
		return flow.getWidgetCount() < 2;
	}

	protected void requestFocus() {
		comRoot.getKeyboard().setEditor(this);
		childfocus = false;
		FocusOnTouch.focus();
		shown = true; // mag dat hier al? nee dus. -- Syl: Waarom niet? Waar dan wel? deze wordt tevroeg aangeroepen, als de teksteditor nog niet in de div-tree zit
		setCursorWidget(cursorWidget);
		hideEmpty();
	}

	private void hideEmpty() {
		hbox.removeStyleName(css.textEditor_empty());
	}

	private void setState(ObjectMap h)
	{
		String tekst = h == null ? "" : h.getString("tekst");
		// h kan null zijn!
		editable = true;
		shown = false;
		if (tekst == null)
			tekst = "";
		else if (tekst.endsWith("\n"))
			tekst = tekst.substring(0, tekst.length()-1);
		//sb.setLength(0);
		clearAll();
		insert(tekst);
		lastAttempt = tekst;
		removeCursor();
		editable = h == null || h.getBoolean("editable", true);
		if (!editable)
			setReadonly();
		//shown = true;
		updateEmpty();
		pasAanH();
	}

	private Widget getContent(ObjectMap launchdata)
	{
		FlowPanel touch = new FlowPanel();
		Tapper tapper = new Tapper(this,touch.getElement());		
        tapper.initHandlers(pasAanH ? hbox : touch); // 
		flow = touch; // XXX voorlopig ok
		setState(launchdata);
		return touch;
	}

	private Widget setCursorWidget(Widget widget)
	{
		if(widget == null) return cursorWidget;
		removeCursor();
		widget.setStyleName(css.textEditor_cursor(), true);
		cursorWidget = widget;
		int c = flow.getWidgetIndex(widget);
		if(c >= 0)
			cursor = c;
		LOGGER.info("set cursor at " + c); if(c==35) {
			RuntimeException r = new RuntimeException();
			r.fillInStackTrace();
			LOGGER.log(Level.SEVERE, "setcursor", r);
		}
		showCursor();
		return widget;
	}

	private Widget getMenuBar(ObjectMap launchdata)
	{
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
		
		return getMenuBar(formuleKnop, rekentool, graftool);
	}

	private Widget getMenuBar(boolean formuleKnop, boolean rekentool, boolean graftool) {
		FlowPanel menubar = new FlowPanel();
		menubar.setStyleName(css.balk());
		
		FEWSButton formuleButton = new FEWSButton("formule",isNoordhoff());
		formuleButton.addButtonListener(new FXHandler());
		formuleButton.setTooltip(Text.constants.tooltip_formuleButton());
		formuleButton.setSize(27, 27);
		Style formuleStyle = formuleButton.getElement().getStyle();
		formuleStyle.setDisplay(Display.INLINE_BLOCK);
		if (pasAanH) {
			formuleStyle.setMarginBottom(2, Unit.PX);
			formuleStyle.setMarginLeft(2, Unit.PX);
			if(rekentool) 
				formuleStyle.setMarginRight(2, Unit.PX);
		} else
			formuleStyle.setMargin(2,Unit.PX);
		if(formuleKnop) {
			formuleButton.addPointerDownHandler();
/* Dit is nodig, anders komt de "tapper" er doorheen */
			formuleButton.addDomHandler(new PreventTapper(), PointerDownEvent.getType());		
			menubar.add(formuleButton);
		}
		
		//Button calc = new Button("calc"); 
		Image upImage = new Image(activity.getResource("images/resources/rmknop.gif"));
		upImage.getElement().setAttribute("width","18");
		upImage.getElement().setAttribute("height","18");
		
		FEWSButton calcButton = new FEWSButton("rekenmachine", isNoordhoff());
		calcButton.addButtonListener(new CalcHandler());
		calcButton.setTooltip(Text.constants.tooltip_calcButton());
		calcButton.setSize(27, 27);
		Style calcStyle = calcButton.getElement().getStyle();
		calcStyle.setDisplay(Display.INLINE_BLOCK);
		if (pasAanH) {
			calcStyle.setMarginBottom(2, Unit.PX);
			calcStyle.setMarginLeft(2, Unit.PX);
		} else
			calcStyle.setMargin(2,Unit.PX);
		if(rekentool) {
		  calcButton.addPointerDownHandler();
		  calcButton.addDomHandler(new PreventTapper(), PointerDownEvent.getType());
		  menubar.add(calcButton);
		}
// wordt niet gebruikt		
//		PushButton graph = new PushButton(new Image(activity.parameters().getResource("images/resources/grafiekknop.gif")));
//		if(graftool) menubar.add(graph);
		
		return menubar;
	}

	private boolean isNoordhoff() {
		return activity.isNoordhoff();
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	private void initWidget(Widget w) {
		this.widget = CorrectieFacade.wrap(w, activity);
		this.widget.setPixelSize(width, height);
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
		{
			this.width = breedte;
			if (menubar != null && !pasAanH)
				menubar.setPixelSize(width-boxsize, menuheight);
			content.setPixelSize(width-boxsize-padding, pasAanH ? -1 : height-menuheight-boxsize-padding);
			hbox.setPixelSize(width-boxsize-paddingH, height-boxsize-paddingH);
			if (widget != hbox) widget.setPixelSize(breedte, -1);
		}
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		this.asHoogte = ashoogte;
	}

	private void adviseMe() {
		Optional<DwoGlobalVars> vars = activity.vars();
		if (vars.isPresent() && vars.get().withUser() && logging != null && comRoot.getLessonMode() == LessonMode.normal ) {
			String id = loggingID;
			if(id == null || !id.startsWith("adviseMe:")) 
				return;
			String[] split = id.split(":");
			String math = toMathML();
			String userid = vars.get().getUserID().toString();
			String classid;
			try {
				classid = vars.get().getCurrentSchoolClass().getId().getIdString();
			} catch (Exception e) {
				classid = "";
			}
			String exerciseid = split[1];
			String id2 = split[2];
			Map<String,String> context = new HashMap<>();
			context.put("userid", userid);
			context.put("groupid", classid);
			context.put("language", StubView.getLocale());
			RuleIF rule = new AbstractRule() {

				@Override
				public String getExpr() {
					return math;
				}

				@Override
				public String getId() {
					return id2;
				}

				@Override
				public Map getContext() {
					return context;
				}
				
			};
			PromiseCallback<RuleIF> defer = new PromiseCallback<>();
			WiskOpdr.ideas.adviseMe(new RuleIF[] { rule }, exerciseid, defer );
			activity.agent().addBarrier(defer.getPromise());
			Logger LOG = Logger.getLogger("TextEditor");
			defer.getPromise().onResolve(() -> { 
				Promise<RuleIF> p = defer.getPromise();
				Throwable t = p.getFailure();
				if ( t != null) {
					LOG.log(Level.SEVERE, "adviseMe", t);
				} else {
					RuleIF r = p.getValue();
					if ( r.isException()) {
						LOG.severe(r.getExpr());
					} else {
						LOG.info(r.getExpr());
					}
				}
			} );
		}
	}
	
	
	@Override
	public HashMap<String, Object> getState() {
		adviseMe();
		StringBuilder allText = getAllText();
		setAttempt(allText);
		String sb = allText.append('\n').toString();
		HashMap<String,Object> state = new HashMap<String,Object>();
		state.put("tekst", sb);
		state.put("editable", editable);
		if(correctie != null) correctie.correctie(state);
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
	    int size = flow.getWidgetCount()-1;
	    return  getAllText(0, size);
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		addExecuteBtn(comRoot); // last change, all listeners are there.
		if(h == null) setStateNull();
		else {
			AcceptsOneWidget cmd = widget instanceof AcceptsOneWidget ? (AcceptsOneWidget) widget : null;
			CorrectieFacade.showReview(h, cmd , this, getScoreMax(), activity);
			setState( JSONUtilities.wrapMap(h));
			correctie = CorrectieFacade.get(h, this, getScoreMax(),comRoot, logging, activity, true);
		}
	}

	private int getScoreMax() {
		if (!teltMee)
			return 0;
		return scoreMax;
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
		if(scoreMax==0)
			return Boolean.TRUE;
		return Boolean.FALSE;
	}

	@Override
	public void kijkNa() {
		
		//TODO voor Wim:
		//log ID opvragen
		//bevat logID "adviseme": 
		//opsturen naar IDEAS.
		adviseMe();
	}

	@Override
	public void zetNagekeken(boolean b) {
		
	}
	
	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		comRoot.addCBookEventListener(ACTION_NOT_EDITIABLE, this);
		comRoot.addCBookEventListener(TEXT, this);
		if(logging != null)
			logging.setCommunicationRoot(comRoot);
	}

	private int execute_height = 0;
	protected void addExecuteBtn(final OpdrNavIF comRoot) {
		if(comRoot.hasListeners(TEXT)) {
			Button btn = new Button(fi.wiskopdr.text.Text.constants.executeLabel());
			Style style = btn.getElement().getStyle();
			style.setWidth(100, Style.Unit.PCT);
			style.setHeight(EXECUTE_HEIGHT, Style.Unit.PX);
			execute_height = EXECUTE_HEIGHT;
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
		selectionEnd = -1;
		flow.clear();
		flow.add(setCursorWidget(new InlineHTML(" \u200A")));
		pasAanH();
	}

	@Override
	public void insert(String text)
	{
		if (!editable )
			return;
		
		char[] chars = text.toCharArray();
		int next = 1;
		if(chars.length > 0) deleteSelection();
		for (int i = 0; i < chars.length; i += next)
		{
			next = 1;
			switch (chars[i])
				{
				case '\n':
					enter0();
					break;
				default:
					insert0(chars[i]);
					break;
				case '$':
					next = findAt(chars, i, chars.length);
					String string = new String(chars, i, next);
					if (chars[i + 1] == 'f')
					{
						FormulaVak fv = new FormulaVak();
						fv.setText(string);
						fv.editor.setCurrentElementRepaint(); // no cursor here!
						// sb.insert(cursor, '@');
						flow.insert(fv, cursor++);
						break;
					}
					if (chars[i + 1] == 'R')
					{
						CalculatorVak cv = new CalculatorVak();
						cv.setText(string);
						// sb.insert(cursor, '@');
						flow.insert(cv, cursor++);
					}
					break;
				}
		}
		showCursor();
		pasAanH();
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
		return defaultfont.createCopy();
	}

	@Override
	public void setFont(FormuleFont font)
	{
		this.font = font;
	}

	@Override // loose focus
	public void setCurrentElementRepaint()
	{
		deSelection();removeCursor();
		setAttempt();
		if (comRoot != null)
			comRoot.getKeyboard().setEnterType(EnterType.APPLY);
		updateEmpty();
	}

	void removeCursor()
	{
		if (cursorWidget != null)
		{	GWT.log("remove cursor");
			cursorWidget.setStyleName(css.textEditor_cursor(), false);
		}
	}

	@Override
	public void enter() {
		if(!editable) return;
//		sb.insert( cursor, '\n');
		deleteSelection();
		enter0();
		showCursor();
		setAttempt();
		pasAanH();
	}

	private void enter0() {
		flow.insert(new Enter(), cursor); cursor++;
	}

	private void setAttempt() {
		if(shown && logging != null) setAttempt(getAllText());
	}

	private void removeCurrentElement1() {
	    if(cursor > 0 && editable)
	    {   flow.remove(--cursor);
//	      sb.replace(cursor, cursor+1, "");
	        showCursor(); pasAanH();
	    }
	  }
	  @Override
	  public void removeCurrentElement() {
	    if(hasSelection()) {
	      deleteSelection();
	    } else {
	      removeCurrentElement1();
	    }
	  }

	  @Override
	  public void removeNextElement() {
	    if (hasSelection()) {
	      deleteSelection();
	    } else {
	      removeNextElement1();
	    }
	  }
	  
	  private void removeNextElement1() {
	    int max = flow.getWidgetCount()-1;
	    if(cursor < max && editable){
	        flow.remove(cursor);
	        setCursorWidget(flow.getWidget(cursor)); pasAanH();
//	      sb.replace(cursor, cursor+1, "");
	    }
	  }

	  private boolean hasSelection() {
	    return selectionEnd != -1;
	  }
	  
	  private void deleteSelection() {
	    if(hasSelection()) {
	      while(cursor > selectionEnd) removeCurrentElement1();
	      while(cursor < selectionEnd--) {
	        removeNextElement1();
	      }
	      selectionEnd = -1;
	    }
	  }

	  private void deSelection() {
// niet altijd goed
		int size = flow.getWidgetCount();  
		for (int i = 0; i < size; i++) flow.getWidget(i).removeStyleName(css.textEditor_select());
		  
		  
	    if(hasSelection()) {
	      while(cursor > selectionEnd) 
	        flow.getWidget(selectionEnd++).removeStyleName(css.textEditor_select());
	      while(cursor < selectionEnd)
	        flow.getWidget(--selectionEnd).removeStyleName(css.textEditor_select());
	      selectionEnd = -1;
	    }
	  }

	  private void cursorToLeft1() {
	    if (cursor > 0) {
	      cursor --;
	      setCursorWidget(flow.getWidget(cursor));
	    }
	  }
	  @Override
	  public void cursorToLeft() {
	    deSelection();
	    cursorToLeft1();
	  }
	  @Override
	  public void cursorToLeftShift() {
	    if (cursor > 0) {
	      if (!hasSelection()) selectionEnd = cursor;
	      if (cursor > selectionEnd) 
	        flow.getWidget(cursor-1).removeStyleName(css.textEditor_select());
	      else
	        flow.getWidget(cursor-1).addStyleName(css.textEditor_select());
	    }
	    cursorToLeft1();
	    if (cursor == selectionEnd) selectionEnd = -1;
	  }

	  private void cursorToRight1() {
	    int max = flow.getWidgetCount()-1;
	    if (cursor < max) {
	        cursor ++;
	        setCursorWidget(flow.getWidget(cursor));
	    }
	  }
	  @Override
	  public void cursorToRight() {
	    deSelection();
	    cursorToRight1();
	  }
	
	  @Override
	  public void cursorToRightShift() {
	    int max = flow.getWidgetCount()-1;
	    if (cursor < max) {    
	      if (!hasSelection()) selectionEnd = cursor;
	      if (cursor < selectionEnd)
	        flow.getWidget(cursor).removeStyleName(css.textEditor_select());
	      else
	        flow.getWidget(cursor).addStyleName(css.textEditor_select());
	      cursorToRight1();
	      if (cursor == selectionEnd) selectionEnd = -1;
	    }
	  }
	
	
	@Override
	public void cursorUp() {
	}
	
	@Override
	public void cursorDown() {
		
	}
	
	protected void pasAanH() {
		if (pasAanH && regel != null && visibleChain()) {
			int offsetHeight = flowHeight();
			if (height != offsetHeight + boxsize + paddingH + menuheight + execute_height) {
				height  = offsetHeight + boxsize + paddingH + menuheight+ execute_height;
				hbox.setPixelSize(-1, height-boxsize-paddingH);
				if (widget != hbox) widget.setPixelSize(-1, height);
				regel.resize();
		}}
	}

	private int flowHeight() {
		int xmin = flow.getAbsoluteTop();
		int xmax = xmin + flow.getOffsetHeight(); // Helaas geen uitsteeksels
		int size = flow.getWidgetCount();
		int i;
		for (i = 0; i < size; i++) { // eigenlijk alleen de eerste en de laatste regel nodig, FIXED
			Widget w = flow.getWidget(i);
			int min = w.getAbsoluteTop();
			int max = w.getOffsetHeight() + min;
			if (max > xmax) xmax = max;
			if (min < xmin) xmin = min;
			if (w instanceof Enter) break;
		}
		for (int j = size-1; j > i; j--) {
			Widget w = flow.getWidget(j);
			int min = w.getAbsoluteTop();
			int max = w.getOffsetHeight() + min;
			if (max > xmax) xmax = max;
			if (min < xmin) xmin = min;
			if (w instanceof Enter) break;			
		}
		
		
		return xmax - xmin;
	}
	
	
	@Override
	public void insert(char charAt)
	{
		if (!editable)
			return;
	    deleteSelection();
	    insert0(charAt);
		showCursor();
		pasAanH();
	}

	private void insert0(char charAt) {
		SafeHtml html;
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(charAt);
		html = builder.toSafeHtml();
//		sb.insert(cursor, charAt);
		InlineHTML w = new InlineHTML(html);
		new TapForFocus(w);
		flow.insert(w,cursor++);
	}

	private void showCursor()
	{
		if (shown && visibleChain())
			OpdrNav.defer(()->cursorWidget.getElement().scrollIntoView());
	}

	private boolean visibleChain() {
		if ( widget.isAttached() ) {
			Widget w = widget;
			Widget root = RootLayoutPanel.get();
			while (w != root && w != null) {
				if (!w.isVisible()) return false;
				w = w.getParent();
			}
			return true;
		} 
		return false;
	}

	  private StringBuilder getAllText(int start, int end) {
	    StringBuilder sb = new StringBuilder();
	    for(int i = start; i < end; i++) {
	      Widget child = flow.getWidget(i);
	      if(child instanceof HasText) {
	          sb.append(((HasText) child).getText());
	      }
	    }
	    return sb;
	  }
	  @Override
	  public String getSelectionString() {
	    int start, end;
	    if(hasSelection()) {
	      if (selectionEnd < cursor) {
	        start = selectionEnd;
	        end = cursor;
	      } else {
	        start = cursor;
	        end = selectionEnd;
	      }
	      return getAllText(start, end).toString();
	    }
	    return "";
	  }
	
	@Override
	public void kopieer(FormuleClipboardIF clip) {
	    clip.setClipboard(getSelectionString());
	}
	
	@Override
	public void knip(FormuleClipboardIF clip) {
	    kopieer(clip);
	    deleteSelection();
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
    public void stelsel()
    {
    }

    @Override
    public void stelsel(int aantalRijen)
    {
    }

	@Override
	public void vectornotatie()
	{
	}

	@Override
	public void vector()
	{
	}

	@Override
	public void vector(int aantalRijen)
	{
	}

	@Override
	public void matrix()
	{
	}
	
	@Override
	public void matrix(int aantalRijen, int aantalKolommen)
	{
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
			updateEmpty();
		}
		
	}
	
	private String toMathML() {
		StringBuilder sb = new StringBuilder();
		int count = flow.getWidgetCount()-1;
		for(int i=0; i < count; i++) {
			Widget child = flow.getWidget(i);
			if (child instanceof FormulaVak) {
				sb.append("<math xmlns=\"http://www.w3.org/1998/Math/MathML\">").append(((FormulaVak) child).editor.getMainRegel().toMathML()).append("</math>");
			} else
			if (child instanceof CalculatorVak) {
				CalculatorVak vak = (CalculatorVak) child;
				sb.append("<math xmlns=\"http://www.w3.org/1998/Math/MathML\" class=\"calculator\" >");
				sb.append("<mrow>").append(vak.editor.getMainRegel().toMathML()).append("</mrow>");
				sb.append("<mo>").append(vak.btn.getText()).append("</mo>");
				sb.append("<mn>").append(vak.waarde).append("</mn>");
				sb.append("</math>");				
			} else
			if(child instanceof HasText) {
				sb.append(xmlEncode(((HasText) child).getText()));
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return getText();
	}

	private String xmlEncode(String text) {
		return text.replace("&", "&amp;").replace("<", "&lt;");
	}

	private void setReadonly() {
		editable = false;
		widget.setStyleName(css.textEditor_readonly(), !editable);
		int count = flow.getWidgetCount()-1;
		for(int i=0; i < count; i++) {
			Widget child = flow.getWidget(i);
			if(child instanceof IsEditable) {
				((IsEditable) child).setEditable(false);
			}
		}

	}

	public boolean isReadOnly() {
		return !editable;
	}
	
	@Override
	public void setFontSize(int font_size)
	{
		widget.getElement().getStyle().setFontSize(font_size, Unit.PX);
		// moet onderstaande of overbodig?
//		content.setPixelSize(width-boxsize-padding, font_size);
//		content.getElement().getStyle().setFontSize(font_size, Unit.PX);
	}

	@Override
	public void setFontName(String name)
	{
		widget.getElement().getStyle().setProperty("fontFamily", name);
		content.getElement().getStyle().setProperty("fontFamily", name);
	}

	@Override
	public void setFontStyle(int font_style) {
	}

	@Override
	public void setParentRegel(TekstRegel regel)
	{
		font = regel.getFont();
		defaultfont = font;
		this.regel = regel;
		pasAanH();
	}

	@Override
	public void tab() {
		Widget parent = asWidget();
		while (parent != null && !(parent instanceof TekstVak))
		{
			parent = parent.getParent();
		}
		
		if(parent != null && parent instanceof TekstVak)
		{
			((TekstVak) parent).tabFocus(this, true);
		}
	}

	@Override
	public void shiftTab()
	{
		Widget parent = asWidget();
		while (parent != null && !(parent instanceof TekstVak))
		{
			parent = parent.getParent();
		}
		
		if (parent != null && parent instanceof TekstVak)
		{
			((TekstVak) parent).shiftTabFocus(this, true);
		}
	}

	public String getText()
	{
		return getAllText().toString();
	}

	/**
	 * Zet de hoogte van de content.
	 * Gebruikt door AntwoordTekstVak2.
	 * 
	 * @param h
	 */
	void setSize(int h)
	{
		content.setPixelSize(width-boxsize-padding, h + 2);
		content.getElement().getStyle().setFontSize(h, Unit.PX);
		
		Style style = content.getElement().getStyle();
		int top = Math.max((this.height - menuheight - boxsize - padding - (h + 2)) / 2, 0); // niet kleiner dan 0
		style.setMarginTop(top, Unit.PX);
	}

	class FormuleTapper implements ClickHandler {

		private FormuleEditor deze;
		private Widget widget;
		@Override
		public void onClick(ClickEvent event)
		{
			FormuleKeyboardIF keyboard = comRoot.getKeyboard();
//			keyboard.setEditor(deze);
			keyboard.setEnterType(EnterType.APPLY);
//			keyboard.focus();
//			deze.requestFocus();
			deSelection();
			setCursorWidget(widget);
			removeCursor(); // cursor is in formule editor
			event.stopPropagation();
		}
		public FormuleTapper(FormuleEditor deze, Widget widget) {
			super();
			this.deze = deze;
			this.widget = widget;
		}
		
	}
	
	
	
	class Tapper implements /*ClickHandler,*/ TouchStartHandler, TouchEndHandler, TouchMoveHandler, MouseDownHandler, MouseUpHandler, MouseMoveHandler, PointerDownHandler, PointerUpHandler, PointerMoveHandler {
		private FormuleEditorIF deze;
		private Element target;
        private int downX, lastX;
        private int downY, lastY;
        private Widget downWidget;
        private int down;
        private int move;
        
        HandlerRegistration mouseRegistration, pointerRegistration;
		HandlerRegistration touchRegistration;
  
        
        HandlerRegistration initHandlers(Widget w) {
        	touchRegistration = HandlerRegistrations.compose(
        			w.addDomHandler(this, TouchStartEvent.getType()),
        	        w.addDomHandler(this, TouchEndEvent.getType()),
        	        w.addDomHandler(this, TouchMoveEvent.getType())
        	);
        	mouseRegistration = HandlerRegistrations.compose(
        			touchRegistration,
        			w.addDomHandler(this, MouseDownEvent.getType()),
        	        w.addDomHandler(this, MouseUpEvent.getType()),
        	        w.addDomHandler(this, MouseMoveEvent.getType())
        	);
        	pointerRegistration = HandlerRegistrations.compose(
        			w.addDomHandler(this, PointerDownEvent.getType()),
        	        w.addDomHandler(this, PointerUpEvent.getType()),
        	        w.addDomHandler(this, PointerMoveEvent.getType())
        			);
        	return HandlerRegistrations.compose(mouseRegistration, pointerRegistration, touchRegistration);
        }
        
        
        
        
        
        
        public void onTouchMove(TouchMoveEvent event) {
          if (downWidget != null) {
            Touch t = event.getTouches().get(0);
            lastX = t.getPageX();
            lastY = t.getPageY();
            move(event);
          }
          
        }

		private void move(DomEvent<?> event) {
			event.preventDefault();
			Widget moveWidget = findWidget(lastX,lastY);
            downWidget.removeStyleName(css.textEditor_cursor());
            if( (moveWidget == downWidget || moveWidget == null) && downWidget != flow.getWidget(flow.getWidgetCount()-1)) {
              downWidget.addStyleName(css.textEditor_select());
              return;
            }
            clearDownMove();
            down = flow.getWidgetIndex(downWidget);
            move = flow.getWidgetIndex(moveWidget);
            GWT.log("down = " + down + ", move = " + move);
            if (move > down) {
              int m = move;
              while(--m >= down) {
                flow.getWidget(m).addStyleName(css.textEditor_select());
                GWT.log("select " + m);
              }
            } else {
              int m = move-1;
              while( ++m < down && m < flow.getWidgetCount()-1) 
                flow.getWidget(m).addStyleName(css.textEditor_select());
                GWT.log("select " + m);
              }
		}
		
		public void onTouchStart(TouchStartEvent event) {
		    Touch touch = event.getTouches().get(0);
		    lastX = downX = touch.getPageX();
		    lastY = downY = touch.getPageY();
		    start(event);
		  }

		private void start(DomEvent<?> event) {
		    GWT.log(event.getAssociatedType().getName()+  " " + lastX + " " + lastY);
		    
			downWidget = findWidget(downX, downY);
		    down=move=cursor;
		    deSelection();
		    setCursorWidget(downWidget);
            hideEmpty();
		    
		    if (downWidget != null) {
		      if (downWidget instanceof FormulaVak || downWidget instanceof CalculatorVak) {
		    	GWT.log("intern vak");
		        downWidget = null;
		        removeCursor();
		      }
		      event.preventDefault();
		      //DOM.setCapture(getElement());
		    }
		}
		  private void clearDownMove() {
		    int min = Math.min(down,move);
		    int max = Math.max(down,move);
		    while (min <= max) {
		      flow.getWidget(min++).removeStyleName(css.textEditor_select());
		      GWT.log("deselect " + min);

		    } 
		  }

		protected Widget findWidget(int clientX, int clientY) {
		    Widget wid = null;
		    int max = flow.getWidgetCount();
		    int px = clientX;
		    int py = clientY;
		    wid = flow.getWidget(max-1);
		    int bottom = wid.getAbsoluteTop() + wid.getOffsetHeight();
		    if (py >= bottom || (py >= wid.getAbsoluteTop() && px >= wid.getAbsoluteLeft())) { 
		      return wid;
		    }
		    LOGGER.fine("px = " + px + ", py = " + py);
		    while (--max > -1) {
		      wid = flow.getWidget(max);
		      int x = wid.getAbsoluteLeft();
		      int y = wid.getAbsoluteTop();
		      //LOGGER.fine("m=" + max + "x=" + x + "y=" + y);
		      if (y > py ) {
		        //LOGGER.fine("too low");
		        continue; // line below mouse
		      }
		      if (x > px ) {
		        //LOGGER.fine("too right");
		        continue;
		      }
		      int h = wid.getOffsetHeight(); h = Short.MAX_VALUE;
		      int w = wid.getOffsetWidth();
		      if (max < flow.getWidgetCount()-1) {
		    	  Widget n = flow.getWidget(max+1);
		    	  int www = n.getAbsoluteLeft() - wid.getAbsoluteLeft();
		    	  if (www > 0) w = www; else w = Short.MAX_VALUE;
		      }
		      
		      if (wid instanceof Enter) {
		          w = Short.MAX_VALUE;
		      }
		      if (px <= x + w || py >= y + h) {
		        LOGGER.fine("found " + max);
		        break;
		      }
		    }
		    return wid;
		  }
	
		  public void onTouchEnd(TouchEndEvent event) {
		    end(event);
		 }

		private void end(DomEvent<?> event) {
		    GWT.log("text editor End " +event.getAssociatedType().getName());
			//DOM.releaseCapture(getElement());
		    if (downWidget != null) {
		      event.preventDefault();
              FormuleKeyboardIF kb = comRoot.getKeyboard();
		      kb.blur();
		      kb.setEditor(deze);
		      kb.setEnterType(EnterType.ENTER);
		      kb.softFocus(); hideEmpty(); // undo blur, (blur zet ... en APPLY)
		      clearDownMove();
		      int r = Math.abs(downX-lastX) + Math.abs(downY-lastY);
		      Widget moveWidget = findWidget(lastX, lastY);
		      if(moveWidget == downWidget || moveWidget == null) {
		        if(r < 3) {
		          setCursorWidget(downWidget);
		          downWidget = null;
		          GWT.log(event.getAssociatedType().getName() + " cursor");
		          return;
		        }
		      }
		      downWidget.removeStyleName(css.textEditor_cursor());
		      int down = flow.getWidgetIndex(downWidget);
		      int move = flow.getWidgetIndex(moveWidget);
		      downWidget = null;
		    if (move > down) {
		        cursor = move;
		        selectionEnd = down;
		        while(--move >= down) {
		          flow.getWidget(move).addStyleName(css.textEditor_select());
		          GWT.log("mouse select " + move);
		        }
		      } else {
		        cursor = move;
		        selectionEnd = down;
		        while( move++ < down) 
		          flow.getWidget(move-1).addStyleName(css.textEditor_select());
		          GWT.log("mouse select " + move);
		      }
		      GWT.log(event.getAssociatedType().getName() + " select");
	          setCursorWidget(flow.getWidget(cursor));
		    }
		}

		public Tapper(FormuleEditorIF deze, Element target) {
			this.deze = deze;
			this.target = target;
		}

		public void onClick(ClickEvent event)
		{
//			Element targetElement = event.getTargetElement();
//			if(targetElement == null || targetElement == target || targetElement.getParentElement() == target)
			{
				FormuleKeyboardIF keyboard = comRoot.getKeyboard();
				keyboard.setEditor(deze);
				keyboard.setEnterType(EnterType.ENTER);
				keyboard.softFocus();
				hideEmpty();
				int flowTop = flow.getAbsoluteTop();
				int y = event.getClientY() - flowTop;
				int w;
				int i = 0;
				int max = flow.getWidgetCount() - 1;
				if (i == max)
					i -= 1;
				Widget widget;
				do
				{
					widget = flow.getWidget(++i);
					w = widget.getAbsoluteTop()-flowTop;
				} while ( i < max && w < y);
				
				LOGGER.fine("widget " + i + " at "  + w + " mouse at " + y + " c=" + cursor + " m=" + max);
				deSelection();
				setCursorWidget(widget);
				if (cursor != max || w >= y)
					cursorToLeft(); // 1 terug
			}
		}


		@Override
		public void onPointerMove(PointerMoveEvent event) {
			  mouseRegistration.removeHandler();
	          if (downWidget != null) {
	              lastX = event.getClientX();
	              lastY = event.getClientY();
	              move(event);
	            }
		}


		@Override
		public void onPointerUp(PointerUpEvent event) {
			mouseRegistration.removeHandler();
			end(event);
		}
		@Override
		public void onPointerDown(PointerDownEvent event) {
			mouseRegistration.removeHandler();
		    lastX = downX = event.getClientX();
		    lastY = downY = event.getClientY();
		    start(event);			
		}
		@Override
		public void onMouseMove(MouseMoveEvent event) {
	          if (downWidget != null) {
	              lastX = event.getClientX();
	              lastY = event.getClientY();
	              move(event);
	            }
		}

		@Override
		public void onMouseUp(MouseUpEvent event) {
			end(event);			
		}

		@Override
		public void onMouseDown(MouseDownEvent event) {
		    lastX = downX = event.getClientX();
		    lastY = downY = event.getClientY();
		    start(event);
		}

	}
	
	private class TapForFocus implements /*TouchStartHandler,*/ ClickHandler
	{
		private Widget cursorWidget;
		private HandlerRegistration registration;

//		/**
//		 * @param cursorWidget
//		 */
//		TapForFocus(Widget cursorWidget)
//		{
//			this.cursorWidget = cursorWidget;
//			registration = cursorWidget.addDomHandler(this, TouchStartEvent.getType());
//		}

		private TapForFocus(Widget cursorWidget, HasClickHandlers w)
		{
			this.cursorWidget = cursorWidget;
			registration = w.addClickHandler(this);
		}

		private TapForFocus(InlineHTML w) {
			this(w,w);
		}

		public void finalize()
		{
			registration.removeHandler();
			registration = null;
			cursorWidget = null;
		}
		
//		@Override
//		public void onTouchStart(TouchStartEvent event)
//		{
//			FormuleKeyboardIF keyboard = comRoot.getKeyboard();
//			keyboard.setEditor(TextEditor.this);
//			keyboard.setEnterType(EnterType.ENTER);
//			shown = true; childfocus = false;
//			deSelection();
//			setCursorWidget(cursorWidget);
//			hideEmpty();
//			keyboard.softFocus();
//			event.stopPropagation();
//			event.preventDefault();
//		}

		@Override
		public void onClick(ClickEvent event)
		{
			FormuleKeyboardIF keyboard = comRoot.getKeyboard();
			keyboard.setEditor(TextEditor.this);
			shown = true;childfocus = false;
			deSelection();
			setCursorWidget(cursorWidget);
			keyboard.setEnterType(EnterType.ENTER);
			keyboard.softFocus();
			hideEmpty();
			event.stopPropagation();
			event.preventDefault();
		}
	}
	
	private class FormulaVak extends Composite implements HasText, IsEditable
	{
		private FormuleEditor editor;
		final Panel panel;

		
		
		native final void nop(int n) /*-{ console.log(n); }-*/;
		
		private FormulaVak()
		{
		
			
			
			editor = new FormuleEditor() {
				int cnt;
				@Override
				public void resize()
				{	
					int h = editor.getHeight();
					int a = editor.getMainRegel().getAsHoogte();
					panel.getElement().getStyle().setVerticalAlign(a-h, Unit.PX);
					forceflow();
				}
				
				@Override
				public void enter()
				{
					FormuleKeyboardIF keyboard = comRoot.getKeyboard();
					setCurrentElementRepaint();
					keyboard.blur();
					TextEditor.this.cursorToRight();
					keyboard.setEnterType(EnterType.ENTER);
					TextEditor.this.requestFocus();
					keyboard.softFocus();
				}

				/* (non-Javadoc)
				 * @see nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor#requestFocus()
				 */
				@Override
				public void requestFocus() {
					super.requestFocus();
					childfocus = true;
				}

				/* (non-Javadoc)
				 * @see nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor#setCurrentElementRepaint()
				 */
				@Override
				public void setCurrentElementRepaint() {
					childfocus = false;
					super.setCurrentElementRepaint();
				}
				
			};
			editor.setFormuleToolBijFocus(true);
			setFont(defaultfont);
			//editor.insert();
			panel = editor.getAsPanel();
			//comRoot.getKeyboard().setEditor(editor);
			panel.addDomHandler(new FormuleTapper(editor, this), ClickEvent.getType());
			FormuleEditorTouchHandler h = new FormuleEditorTouchHandler(editor) {

				@Override
				public void onEnd(EndEvent event) {
					super.onEnd(event);
					deSelection();
					setCursorWidget(FormulaVak.this);
					removeCursor();
				} 
			};
			(h).initHandler();
			editor.resize();
			initWidget(panel);
			setStyleName(css.insert_formule());

		}

		@Override
		public String getText()
		{
			return "$f" + editor.toString() + "@";
		}

		@Override
		public void setText(String text)
		{
			if (!editable) 
				return;
			editor.clearMain();
			editor.insert(text.substring(2, text.length() - 1));
		}
		
		private void setFont(FormuleFont font)
		{
			editor.setFont(font);
			editor.setDefaultFont(font);
		}
		
		private void start()
		{
			editor.clearSelection();
			editor.startSelection(0, 0);
			editor.endSelection(0, 0);
		}
		
		public void setEditable(boolean b) {
			panel.setStyleName(css.insert_formule_readonly(), !b);
			Style s = panel.getElement().getStyle();
			if (b) 
				s.clearProperty("pointerEvents");
			else 
			{
				s.setProperty("pointerEvents", "none");
				editor.getKeyboard().blur();
			}
		}
	}
	
	private class CalculatorVak extends Composite implements HasText, ButtonListener, IsEditable
	{
		public Object waarde = "";
		static final double E_MAX = 1.0E7;
		static final double E_MIN = 1.0E-3;
		static final double MARGE = 0.00000000000000001;

		private FormuleEditor editor;
		private FormuleViewer viewer;
		private FEWSButton btn;
		private boolean op3;
		private Panel panel;
		private FlowPanel calculator;
		public CalculatorVak()
		{
			editor = new FormuleEditor()
			{
				@Override
				public void enter()
				{
					calculate();
				}

				@Override
				public void resize() {
					int h = editor.getHeight();
					int a = editor.getMainRegel().getAsHoogte();
					panel.getElement().getStyle().setVerticalAlign(a-h, Unit.PX);
					forceflow();
				}
				
			};
			panel = editor.getAsPanel();
			editor.setFormuleToolBijFocus(true);
			editor.insert('0');
			calculator = new FlowPanel();
			calculator.setStyleName(css.insert_calculator());
			panel.setStyleName(css.insert_formule());
			FormuleEditorTouchHandler h = new FormuleEditorTouchHandler(editor)
			{
				@Override
				public void onEnd(EndEvent event)
				{
					super.onEnd(event);
					deSelection();
					setCursorWidget(CalculatorVak.this);
					removeCursor();
				} 
			};
			h.initHandler();
			calculator.addDomHandler(new FormuleTapper(editor, this), ClickEvent.getType());
			calculator.add(panel);
			btn = new FEWSButton("=", isNoordhoff());
			btn.addPointerDownHandler(); // Alleen voor deze [=]
			btn.addButtonListener(this);
			btn.setSize(20, 20);
			btn.getElement().getStyle().setMargin(2, Unit.PX);
			btn.setStyleName(css.insert_button());
			calculator.add(btn);
			viewer = new FormuleViewer("0");
			viewer.getAsPanel().setStyleName(css.insert_result());
			calculator.add(viewer.getAsPanel());
			initWidget(calculator);
		}

		@Override
		public String getText()
		{
			return "$R" + editor.toString() + "@";
		}

		@Override
		public void setText(String text)
		{
			editor.clearMain();
			editor.insert(text.substring(2, text.length()-1));
			onClick(null);
		}		

		
		public void onClick(Object sender)
		{
			calculate();
			editor.requestFocus();
		}
		
		private void calculate()
		{
			op3 = !op3;
			String x = editor.toString();
			Expressie antwoord = FormuleParser.geefExpressie("$f" + x + "@");
			viewer.getAsPanel().removeFromParent();
			this.waarde = x = "";
			if (antwoord != null) 
			{
				double waarde = antwoord.geefWaarde();
				double afgerond = new DecRound(new BasisExpressie(waarde), new BasisExpressie(3)).geefWaarde();
				boolean afgerondOp3 = 
						! Algebra.isGelijkDouble(waarde, afgerond, MARGE);
				if (Double.isNaN(waarde))
				{	x = "?";
					btn.setText(EXACT);
					this.waarde = "NaN"; // Nan
				}
				else
				{ 
					btn.setText(afgerondOp3 ? CIRCA : EXACT);
					if (op3)
					{
						x = Expressie.df3.format(waarde);
						this.waarde = x;
					}
					else 
					{
						double abs = Math.abs(waarde);
						if ( (abs > 0 && abs < E_MIN) || abs >= E_MAX) 
						{
							x = Expressie.dfe.format(waarde);						
							this.waarde = x;
							x = x.replace("E", "*10$m") + "@";
						}
						else
						{
							x = Expressie.df.format(waarde);
							this.waarde = x;
						}
					}
				}
				//x = String.valueOf(antwoord.geefWaarde());
			}
			
			viewer = new FormuleViewer(x);
			Panel result = viewer.getAsPanel();
			result.setStyleName(css.insert_result());
			((Panel) getWidget()).add(result);
		}

		@Override
		public void setEditable(boolean b) {
			panel.setStyleName(css.insert_formule_readonly(), !b);
			btn.setEnabled(b);
			Style s = panel.getElement().getStyle();
			if (b) 
				s.clearProperty("pointerEvents");
			else 
			{
				s.setProperty("pointerEvents", "none");
				editor.getKeyboard().blur();
			}
		}
	}// class CalculatorVak
	
	public class FXHandler implements ButtonListener
	{
		public void onClick(Object sender)
		{
			if (!editable)
				return;
			GWT.log("childfocus " + childfocus);
			if ( childfocus ) return;
			if (comRoot.getKeyboard().getEditor() != TextEditor.this)
			{	FormuleKeyboardIF kb = comRoot.getKeyboard();
				kb.setEditor(TextEditor.this);
				kb.setEnterType(EnterType.APPLY);
				shown = true;
				deSelection();
				setCursorWidget(cursorWidget);
				hideEmpty();
				//event.stopPropagation();
				//event.preventDefault();
			}
			FormulaVak panel = new FormulaVak();
			panel.start();
			//sb.insert(cursor, '@');
			flow.insert(panel, cursor++);
			//comRoot.getKeyboard().setEditor(panel.editor);
			panel.editor.requestFocus();
			//FocusOnTouch.focus();
			pasAanH();
		}
	}
	
	public class CalcHandler implements ButtonListener
	{
		public void onClick(Object sender)
		{
			if (!editable)
				return;
			deleteSelection();
			CalculatorVak panel = new CalculatorVak();
			// sb.insert(cursor, '@');
			flow.insert(panel, cursor++);
			panel.editor.requestFocus();
			hideEmpty();
			pasAanH();
		}
		
	}

	private class Enter extends InlineHTML implements HasText
	{
		private Enter()
		{
			super("\u200A<br>");
		}
		
		public String getText()
		{
			return "\n";
		}
	}

	public void setEditable(boolean b)
	{
		editable = b;
	}

	@Override
	public void setText(String tekst) {
		editable = true;
		shown = false;
		if (tekst == null)
			tekst = "";
		else if (tekst.endsWith("\n"))
			tekst = tekst.substring(0, tekst.length()-1);
		clearAll();
		insert(tekst);
		lastAttempt = tekst;
		removeCursor();
		updateEmpty();
	}

	
	private void forceflow() {
		if (handle == null)
			handle = AnimationScheduler.get().requestAnimationFrame( (t) -> {
				if (!pasAanH) {				
					String display = flow.getElement().getStyle().getDisplay();
					if (!Display.INLINE_BLOCK.getCssName().equals( display))
						flow.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
					else 
						flow.getElement().getStyle().setDisplay(Display.BLOCK);
				}
				handle = null;
				pasAanH();
			}, flow.getElement());
	}

}
