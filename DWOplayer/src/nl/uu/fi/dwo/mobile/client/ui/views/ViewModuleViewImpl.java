package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.StateLess;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;
import nl.uu.fi.dwo.interaction.client.keyboard.ResizeFocusPanel;
import nl.uu.fi.dwo.keyboard.client.AbstractKeyboard.HasHeight;
import nl.uu.fi.dwo.keyboard.client.Combined;
import nl.uu.fi.dwo.keyboard.client.CombinedState;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.DWOplayerCss;
import nl.uu.fi.dwo.mobile.client.sco.DWOLogger;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.text.Text;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF.MisconceptionsHandler;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF.NextPrevHandler;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF.ObjectivesHandler;
import nl.uu.fi.dwo.mobile.client.ui.SelectModuleItem;
import nl.uu.fi.dwo.mobile.client.ui.WaitScreen;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.InteractionViewWithMisconceptions;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;
import nl.uu.fi.dwo.mobile.utils.RandomValues;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.util.ScoType;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.googlecode.mgwt.ui.client.MGWT;



/**
 * 
 * @author Danny Hendrix, Evertson Croes, Sietske Tacoma, Wim van Velthoven
 * 
 */
public class ViewModuleViewImpl extends XMLView implements ViewModuleViewBuilder, NextPrevHandler, ObjectivesHandler, MisconceptionsHandler, HasHeight, CombinedState
{
	private static final String RANDOM_VAR_WAARDEN = "RandomVarWaarden";
	private static final String RANDOM_VAR_NAMEN = "RandomVarNamen";
	private static final String KEYBOARD = "keyboardNr";
	private static final String SOORT_KEYBOARD = "soortKeyboard";
	private static final String WRITE_MATH_SET = "writeMathSetNr";
	private static final CBookEvent ACTION_READONLY = new CBookEvent("action.setNotEditable");
	
	private static DWOplayerCss dwoplayercss = DWOplayer.DWO_BUNDLE.dwoplayercss();

	static Logger logger = Logger.getLogger("ViewModuleViewImpl");
	private boolean standalone = false;

	private static ViewModuleViewImplUiBinder uiBinder = GWT.create(ViewModuleViewImplUiBinder.class);

	interface ViewModuleViewImplUiBinder extends UiBinder<Widget, ViewModuleViewImpl> {
	}

	static class ResizeFlowPanel extends FlowPanel implements RequiresResize, ProvidesResize {
	  
      @Override
      public void onResize() {       
        for (Widget w: getChildren()) {
          if (w instanceof RequiresResize) ((RequiresResize) w).onResize();
        }
      }
      
	}
	
	
	private Widget createAndBindUI() {
	    content = new ResizeFlowPanel();
	    content.setStylePrimaryName("resizeFlowPanel");
		return uiBinder.createAndBindUi(this);
	}
	
	
	OpdrNav on;
	String location;
	
	protected Widget mainPanel;
	@UiField(provided=true) SimplePanel contentScrollPanel =
		new ScrollPanel() { 
		@Override
		public void setAlwaysShowScrollBars(boolean alwaysShow) {
			getScrollableElement().getStyle().setOverflowX(Overflow.HIDDEN);
			getScrollableElement().getStyle().setOverflowY(alwaysShow ? Overflow.SCROLL : Overflow.AUTO);
		}
// WITH TOUCH SCROLLING
		@Override
		public boolean setTouchScrollingDisabled(boolean isDisabled) {
			return super.setTouchScrollingDisabled(isDisabled);
		}
		/* (non-Javadoc)
		 * @see com.google.gwt.user.client.ui.ScrollPanel#onResize()
		 */
//		@Override
//		public void onResize() {
//			logger.info("On Resize h=" + getOffsetHeight());
//			super.onResize();
//		}
	};
	private Panel tekst = null;
	//private ArrayList<TouchButton> buttons = new ArrayList<TouchButton>();
	private double zoom = 1;
	
	public boolean zelftoetsNagekeken = false;
	
//	private HeaderButton hb;
//	private HeaderPanel hp;
	private WaitScreen waitscreen = WaitScreen.instance();
	
	private PopupPanel timerPanel;
	private Label timerMessage;
	private PopupPanel timerMessagePopupPanel;
	private Canvas timerCanvas;
	private Timer timerTempoToets;
	private int timeLimitSecondsLeft;
	
	private Widget next, prev, end;
	private int[][][] beginStateMeasuredMisconceptions;

	private Scorm2004IF api;
	private DWOLogger dwologger;

	public ViewModuleViewImpl(ActivityComponent a, RPCHandler rpc, boolean b, Scorm2004IF api) 
	{
		this(a,rpc, api);
		standalone = b;
	}
	
	public ViewModuleViewImpl(ActivityComponent a, RPCHandler rpc, Scorm2004IF api) {
		super(rpc,a);
		this.api = api;
	}

	public ViewModuleViewImpl(ActivityComponent a, RPCHandler rpc) {
		super(rpc,a);
	}

	public void initialize(Scorm2004IF api) {
	  this.api = api;
	  initialize();
	  zetMaat();
	}
	
//	public HeaderButton getBackButton()
//	{
//		return hb;
//	}

	@Override
	public Promise<Boolean> setupModule(String name, String file)
	{
		contentPanel.clear();
		readonly = false;
		if(!activity.parameters().isNavTitle()) setTitle(name);
		return loadJSON(file);
	}

	public void preSetupModule(final String url)
	{
		{
			setupModule(url, url);
		}
	}

	public void clearContentPanel()
	{
		contentPanel.clear();
		contentPanel.getElement().setScrollTop(0);
		contentPanel.getElement().setScrollLeft(0);
		PopupFacade.hide();
		clearElements();
		kb.blur();
	}

	private void removeTitle() {
//		hp.removeFromParent();
		setWindowTop(0);
//		int h = mainPanel.getOffsetHeight();
//		sb.setScrollPanel(this, h);
	}
	private void addTitle() {
//	  if (standalone) headerView.setWidget(hp);
	  setWindowTop(extraHeight);
	}
	
	class StatusBarWidth implements RequiresResize {

		int widthScore, widthOnp, widthOpnieuw, widthAllesOpnieuw, widthKijkNaKnop, widthZelftoetsGeschiedenis, 
			widthVolgende, widthVorige, widthKeerNagekeken, widthVerzegeld;
		int width;

		int widthOpdrachtBol;
		int widthOpdrachtSpace;
		int widthShiftButton;
		int widthSpaceStartButton;
		
		
		@Override
		public void onResize() {onResize1(); }
		
		void onResize0() {
			widthOpdrachtBol = on.getOpdrachtButtonWidth();
			widthOpdrachtSpace = on.getOpdrachtSpaceWidth();
			widthShiftButton = on.getShiftButtonWidth();
			widthSpaceStartButton = on.getSpaceStartWidth();
			onResize1(); }
		
		private void onResize1() {
			// resize opdrachtbolletjes if necessary
			int availableWidth = sb.asWidget().getOffsetWidth();
			width = widthScore + widthOnp + widthOpnieuw + widthAllesOpnieuw + widthKijkNaKnop + widthZelftoetsGeschiedenis + widthVolgende + widthVorige 
				 + widthKeerNagekeken + widthVerzegeld;
			logger.info("ViewModuleViewImpl.setupView(): availableWidth = " + availableWidth + ", width = " + width 
				+ ", widthScore = " + widthScore 
				+ ", widthOnp = " + widthOnp 
				+ ", widthOpnieuw = " + widthOpnieuw 
				+ ", widthAllesOpnieuw = " + widthAllesOpnieuw 
				+ ", widthKijkNaKnop = " + widthKijkNaKnop 
				+ ", widthZelftoetsGeschiedenis = " + widthZelftoetsGeschiedenis 
				+ ", widthVolgende = " + widthVolgende 
				+ ", widthVorige = " + widthVorige 
				+ ", widthKeerNagekeken = " + widthKeerNagekeken
				+ ", widthVerzegeld = " + widthVerzegeld);
			if (width > availableWidth)
			{
				int newMaxOnBar = 0;
				int availableForOpdrachtenRij = availableWidth - width + widthOnp; 
				if (widthOpdrachtBol + widthOpdrachtSpace > 0)
				{
					newMaxOnBar = Math.max(
						(availableForOpdrachtenRij - 2 * widthShiftButton - 2 * widthSpaceStartButton) / (widthOpdrachtBol + widthOpdrachtSpace),
						2);
					logger.info("ViewModuleViewImpl.setupView(): newMaxOnBar = " + newMaxOnBar);
					setMaxOnBar(newMaxOnBar+1);
				}
			}
			else
			{
				// als de breedte niet hoeft worden aangepast, dan hier definitief de opdrachtbollen zetten al dan niet met shiftbuttons
				setMaxOnBar(25);
			}
		}

		int lastmax = -1;
		private void setMaxOnBar(int newMaxOnBar) {
			if(newMaxOnBar != lastmax) {
				lastmax = newMaxOnBar;		
				on.setMaxOnBar(newMaxOnBar);
		        on.setOpdrachtButtonWidth(widthOpdrachtBol);
		}
		}
	}
		
	public void setupView(HashMap<String, Object> launchData)
	{
		StatusBarWidth sbw = new StatusBarWidth();
		// breedte bijhouden van de componenten op de navigatiebalk
		sbw.width = 0;
		// margin left and right is 10, set in css
		int margin = 20;
		sbw.widthOnp = 0;
		Label score = null;
		sbw.widthScore = 0;
		sbw.widthOpnieuw = 0;
		sbw.widthAllesOpnieuw = 0;
		sbw.widthKijkNaKnop = 0;
		sbw.widthZelftoetsGeschiedenis = 0;
		sbw.widthVorige = 0;
		sbw.widthVolgende = 0;
		sbw.widthKeerNagekeken = 0;
		sbw.widthVerzegeld = 0;
		
//		for (int i = 0; i < buttons.size(); i++)
//			contentPanel.remove(buttons.get(i));
		
		// initialize mode
		int mode = OpdrNav.OEFENEN;
		
		try
		{
			super.setupView(launchData);
			ObjectMap wrap = (instellingen);
			// wanneer verschijnt de opnieuwknop?
			boolean opnieuwMogelijk = "true".equals(launchData
				.get("opnieuwMogelijk"));
			boolean opnieuw = false;
			if (wrap != null && wrap.containsKey("opnieuw"))
			{
				opnieuw = wrap.getBoolean("opnieuw");
			}

			sb.asWidget().setStyleName(dwoplayercss.navigatiebalk(), true);
			
			if (wrap.containsKey(KEYBOARD))
			{
				sb.setKeyboard(wrap.getInt(KEYBOARD));
			}
			else
				sb.setKeyboard(-1);
			if (wrap.containsKey(SOORT_KEYBOARD)) {
				sb.setSoortKeyboard(soortKeyboard = wrap.getInt(SOORT_KEYBOARD));
			} else {
				sb.setSoortKeyboard(soortKeyboard = 0);
			}
			if (soortKeyboard != 0) setCombined(Combined.NONE);
			
			if (wrap.containsKey(WRITE_MATH_SET))
			{
				sb.setWriteMathSet(wrap.getInt(WRITE_MATH_SET));
			}
			else
				sb.setKeyboard(-1);
			
			contentPanel.getElement().getStyle()
				.setFontSize(font_size, Unit.PX);
			contentPanel.getElement().getStyle().setPadding(0, Unit.PX); // XXX
																			// was
			String SMintern = instellingen.getString("studentModelId");															// 15
			if(studentModel == null && SMintern != null) {
			  studentModel = Promises.resolved(new DomStudentModelContextId(new PersistenceId(SMintern)));
			}
			// GEEN randje aan de linkerkant, want dan klopt de maat (100%) niet
			// meer bij noordhoff
// ExtensionPoint here.
			(on = GWT.create(OpdrNav.class)).init(launchData, this, createMemento()); // hierin worden bollen gezet
			mode = on.getMode();
			mainPanel.setStyleName(DWOplayer.DWO_BUNDLE.dwoplayercss().review(), on.isReview());

			// voor noordhoff
			int aantalOpdrachten = on.getAantalOpdrachten();
			if (standalone && !bolletjesZichtbaar() && !volgendeKnopZichtbaar
				&& !vorigeKnopZichtbaar && aantalOpdrachten == 1)
				removeTitle();
			else {
			  addTitle();
			}
			
			// voor numworx
			if (   !bolletjesZichtbaar() 
			    && !volgendeKnopZichtbaar
			    && !vorigeKnopZichtbaar
			    && mode != OpdrNav.ZELFTOETS
			    && !wrap.getBoolean("itemOpnieuw", false)
			    && ! (opnieuw||opnieuwMogelijk)
			    && ! scoresZichtbaar	    
			    ) {
			  sb.hide();
			}
			
			
			FlowPanel onp = (FlowPanel) on.getAsPanel();
			
			onp.setStyleName(dwoplayercss.opdrachtbollen(), true);
			
			if (bolletjesZichtbaar() ) // altijd bollen in review
			{
				sb.addNavPanel(onp);
				sbw.widthOnp = onp.getOffsetWidth();
			}
			
			if (mode != OpdrNav.OEFENEN) // alleen voor oefenen mag de optie 'score zichtbaar' uit staan
				scoresZichtbaar = true;
			
			if (scoresZichtbaar && (!(on.getMode() == OpdrNav.EINDTOETS) || on.scoresVisible()))// niet tonen in niet-verzegelde eindtoets
			{
				score = scoreNav.getTotaalScoreLabel();
				if (score != null)
				{ // Bij NOORDHOFF is deze null
					//score.getElement().getStyle().setFloat(Style.Float.LEFT);
					sb.addLabel(score);
					onp.removeFromParent();
					// addKnop() voegt een widget toe 
					sb.addKnop(onp, false);
				}
			}
			
			boolean itemOpnieuw = false;
			if (wrap != null && wrap.containsKey("itemOpnieuw"))
			{
				itemOpnieuw = wrap.getBoolean("itemOpnieuw") && !on.isVerzegeld();
				scoreNav.setItemOpnieuw(itemOpnieuw);
			}
			scoreNav.setOpnieuw((opnieuw || opnieuwMogelijk) && !on.isVerzegeld());
			
			if ( (opnieuw || opnieuwMogelijk) && scoreNav.getAllesOpnieuwButton() != null) // alles opnieuw; let op: Noordhoff heeft geen 'alles opnieuw'-knop
			{
				sbw.widthAllesOpnieuw = scoreNav.getAllesOpnieuwButton().getOffsetWidth() + margin;
			}
			if (itemOpnieuw && scoreNav.getOpnieuwButton() != null) // item opnieuw; let op: Noordhoff heeft geen 'opnieuw'-knop
			{
				sbw.widthOpnieuw = scoreNav.getOpnieuwButton().getOffsetWidth() + margin;
			}

			// pas vanaf hier toevoegen mogelijk.
			scoreNav.setBeantwoord(on.getAantalBeantwoord());
			scoreNav.setItemScores(on.getScoresHuidigeActiviteit());
			//scoreNav.setTotaalScore((int) on.getScore()); later 
			scoreNav.setGotoOpdracht(on);
// FIXME authELO
			if (false)
			{
				boolean authELOcheck = wrap.getBoolean("authELOcheck", false);
				scoreNav.setAuthELOcheck(authELOcheck);
				boolean authELOhelp = wrap.getBoolean("authELOhelp", false);
				scoreNav.setAuthELOhelp(authELOhelp);
				if (authELOcheck || authELOhelp)
				{
					HTML w = new HTML(
							"<img id=\"helper\" data-toggle=\"popover\" title=\"Feedback\" data-placement=\"auto\" data-trigger=\"focus\" data-content=\"I have nothing to tell you right now\"  src=\"http://hansen.dcs.bbk.ac.uk/authELO/public_html/img/glasses_owl.png\" alt=\"Helper\" />");
					w.getElement().getStyle().setPosition(Position.ABSOLUTE);
					w.getElement().getStyle().setTop(0, Unit.PX);
					RootPanel.get().add(w);
				}
			}
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "setupView()", e);
			Window.alert("Exception in setup: " + e.toString()
				+ "\nActivity might be instable");
		}
		if (activity.parameters().isNavTitle())
			setTitle("Vraag " + (1 + on.getCurrentOpdracht()) + " van "
				+ on.getAantalOpdrachten());
		// call SetupDone Handler, if an object is provided
		if (this.loadingHandler != null)
		{
			this.loadingHandler.viewModuleViewSetupDone();
		}
		
		// benodigde knoppen toevoegen.
		if (mode == OpdrNav.ZELFTOETS)
		{
			scoreNav.setKijkNaEnabled(isKijkNaEnabled());
			
			sb.addKnop(scoreNav.getKijkNaButton(), false);
			sbw.widthKijkNaKnop = scoreNav.getKijkNaButton().getOffsetWidth() + margin;
			
			if (zelftoetsGeschiedenis)
			{
				// voeg geschiedenis-knop toe
				PushButton zelftoetsGeschiedenisButton = getZelftoetsGeschiedenisButton();
				sb.addKnop(zelftoetsGeschiedenisButton, false);
				sbw.widthZelftoetsGeschiedenis = zelftoetsGeschiedenisButton.getOffsetWidth() + margin;
			}
		}
		scoreNav.setKijkNa(new ScoreNavIF.Checker()
		{
			@Override
			public Promise<Void> checkOpdracht(final ScoreNavIF source)
			{	final Deferred<Void> defer = new Deferred<Void>();
				activity.agent().addBarrier(defer.getPromise());
				p();
				OpdrNav.defer(new ScheduledCommand()
				{

					@Override
					public void execute()
					{
						// omgedraaid: keerNagekeken moet wel verhoogd zijn voor
						// zetToetsNagekeken()
						zelftoetsNagekeken = true;
						on.kijkToetsNa();
						zetToetsNagekeken(source);
						
						// zet de nakijkknop enabled/disabled
						scoreNav.setKijkNaEnabled(isKijkNaEnabled());
						
						v();
						
						// focus weghalen uit antwoordvak als afdekpanel
						if (isDisabled())
						{
							getKeyboard().setEditor(null);
							getKeyboard().blur();
						}
						on.saveCurrentState();
						defer.resolve(null);
					}
				});
				return defer.getPromise();
			}
		});
		
		// vorige/volgende knop toevoegen
		scoreNav.setVorigeVisible(vorigeKnopZichtbaar);
		if (vorigeKnopZichtbaar && scoreNav.getPrevButton() != null)
			sbw.widthVorige = scoreNav.getPrevButton().getOffsetWidth() + margin;
		scoreNav.setVolgendeVisible(volgendeKnopZichtbaar);
		if (volgendeKnopZichtbaar && scoreNav.getNextButton() != null)
			sbw.widthVolgende = scoreNav.getNextButton().getOffsetWidth() + margin;
		
		scoreNav.setNextPrevHandler(this);
		scoreNav.setScoresObjectivesKnop(on.zijnObjectivesAanwezig()
			&& mode != OpdrNav.EINDTOETS);// && !pilotObjectives);
		scoreNav.setViewMisconceptionsKnop(on.zijnMisconceptionsAanwezig() 
			&& mode != OpdrNav.EINDTOETS);
		scoreNav.setObjectivesHandler(this);
		scoreNav.setMisconceptionsHandler(this);
		//stelNavigatieIn(); // kan weg; gebeurt al in zetOpdracht(); i.v.m. bijhouden bezocht[]

		if (scoresZichtbaar && (!(on.getMode() == OpdrNav.EINDTOETS) || on.scoresVisible()))// scores niet tonen in niet-verzegelde eindtoets
		{
			scoreNav.setTotaalScoreLabel((int) on.getScore()); // toon percentagescore
			// nu de score gezet is kunnen we de breedte bepalen
			if (score != null) sbw.widthScore = score.getOffsetWidth() + margin;
		}
		
		if (mode == OpdrNav.ZELFTOETS)
		{
          // set values
          scoreNav.setKeerNagekekenLabel(on.getKeerNagekeken());
          if (scoreNav.getKeerNagekekenLabel() != null) {
            // add totaalscore and keer nagekeken labels
            sb.addLabel(scoreNav.getKeerNagekekenLabel());
            // Laatste label wordt bij te weinig ruimte heel smal gemaakt, pas als hij verdwijnt, is
            // offsetwidth weer de gewenste breedte.
            sbw.widthKeerNagekeken = Math.max(50, scoreNav.getKeerNagekekenLabel().getOffsetWidth());
            // Kan ook buitensporig groot worden
            if (sbw.widthKeerNagekeken > 500) sbw.widthKeerNagekeken = 100;
          }
		}
		
		if (on.isVerzegeld())
		{
			Label verzegeld = new Label(Text.constants.lockToetsLabel());
			sb.addLabel(verzegeld);
			sbw.widthVerzegeld = Math.max(50, verzegeld.getOffsetWidth());
			// Kan ook buitensporig groot worden
			if (sbw.widthVerzegeld > 500)
				sbw.widthVerzegeld = 100;
		}

		if (on.isReview())
		{
			sb.addLabel(new Label(Text.constants.attemps() + on.getAantalSessies()));
		}

		sbw.onResize0();
		sb.setOnResize(sbw);
		scoreNav.started(activity.getEventBus());
	
		if (isTempotoets())
		{
			if (on.getTempotoetsSecondsLeft() > -1)
				timeLimitSecondsLeft = on.getTempotoetsSecondsLeft();
			else
				timeLimitSecondsLeft = timeLimitSeconds; // volledige duur
			
			// create the timer message
			timerMessage = new Label("");
			timerMessage.getElement().getStyle().setFontSize(2.0, Unit.EM);
			timerMessage.getElement().getStyle().setProperty("margin", "15px");
			timerMessage.getElement().getStyle().setProperty("padding", "25px");
			timerMessagePopupPanel = new PopupPanel(false, false);
			timerMessagePopupPanel.setStylePrimaryName("tempotoetsPopupPanel");
			timerMessagePopupPanel.add(timerMessage);
			timerMessagePopupPanel.center();
			
			// create the timer panel
			timerPanel = new PopupPanel(false, false);
			timerPanel.setStylePrimaryName("tempotoetsPopupPanel");
			
			timerCanvas = Canvas.createIfSupported();
			timerCanvas.getCanvasElement().setWidth(100);
			timerCanvas.getCanvasElement().setHeight(100);
			timerPanel.add(timerCanvas);

			if (timerCanvas == null)
			{
				throw new RuntimeException("Timercanvas could not be created.");
			}

			int left = contentPanel.getOffsetWidth() - timerCanvas.getCanvasElement().getWidth();
			int top = contentPanel.getOffsetHeight() - timerCanvas.getCanvasElement().getHeight();
			
			timerPanel.setPopupPosition(left - 20, top + 20);
			timerPanel.show();
			paintTimer();
			
			if (on.isTemptoetsVerlopen())
			{
				//timeLimitSecondsLeft = 0;				
				tempotoetsLocked = true;
			}
			else if (isAllCorrect())
			{
				tempotoetsLocked = true;
			}
			else
			{
				tempotoetsLocked = false;
				scheduleTimerPainting();
			} // tempotoets niet verlopen

			zetAfdekPanelTempotoets(tempotoetsLocked, isAllCorrect());
		}
		
		on.setReviewLocation();
		
	}

	/**
	 * True als de activiteit disabled is (verlopen tempotoets
	 * of zelftoets niet je niet mag corrigeren).
	 * 
	 * @return
	 */
	private boolean isDisabled()
	{
		String disabled = dwoplayercss.disabled();
		if (contentPanel.getStyleName().contains(disabled))
			return true;
		else
			return false;
	}

	private PushButton getZelftoetsGeschiedenisButton()
	{
		final PushButton zelftoetsGeschiedenisKnop = new PushButton(fi.wiskopdr.text.Text.constants.zelftoetsGeschiedenisKnopLabel());
        zelftoetsGeschiedenisKnop.setStylePrimaryName(dwoplayercss.myPushButton());
		zelftoetsGeschiedenisKnop.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				e.stopPropagation();
				// toon popup met scores
				final PopupPanel panel = new PopupPanel(true);
				panel.add(getScorePanel());
				OpdrNav.defer( // defer want het vullen van het scorepanel is deferred
				new ScheduledCommand() {
					public void execute() {
						panel.setPopupPositionAndShow(new PositionCallback() {

							@Override
							public void setPosition(int offsetWidth, int offsetHeight) {
								panel.setPopupPosition(zelftoetsGeschiedenisKnop.getAbsoluteLeft(),
										zelftoetsGeschiedenisKnop.getAbsoluteTop() - offsetHeight);
							}
						});
					}
				});

			}
		});
		
		return zelftoetsGeschiedenisKnop;
	}

	/**
	 * Geef een panel met op elke regel een score uit de
	 * zelftoets-score-geschiedenis.
	 * 
	 * @return
	 */
	protected Widget getScorePanel()
	{
		TextCell textCell = new TextCell();
		CellList<String> scoreList = new CellList<>(textCell);
		if (on.getScoresZelftoetsHistorie() == null || on.getScoresZelftoetsHistorie().size() == 0)
			scoreList.setRowData(Arrays.asList("leeg"));
		else
			scoreList.setRowData(on.getScoresZelftoetsHistorie());
		
		return scoreList;
	}

	/**
	 * De 'kijk na'-knop is enabled als:
	 * - er maar 1 opdracht is, of
	 * - als al eerder is nagekeken en niet geen correctiemogelijkheid zelftoets of eerdere pagina's, of
	 * - als alles bezocht is
	 *  
	 * @return
	 */
	boolean isKijkNaEnabled()
	{
		boolean enabled = false;
		
		if (on.getAantalOpdrachten() == 1)
			enabled = true;
		else if ((on.getKeerNagekeken() > 0) && (eerderGeenCorr || zelftoetsGeenCorr))
			enabled = on.isReview();
		else if (on.getKeerNagekeken() > 0) // in andere gevallen van een nagekeken zelftoets enabled = true
			enabled = true;
		else if (allesBezocht(on.getCurrentActiviteit()))
			enabled = true;
		return enabled;
	}
	
	/**
	 * Retourneert true als alle opdrachten zijn bezocht in de gegeven activiteit.
	 * 
	 * @param activiteitNr
	 * @return
	 */
	private boolean allesBezocht(int activiteitNr)
	{
		boolean allesBezocht = true;
		
		for (int i = 0; i < bezocht[activiteitNr].length; i++)
		{
			if (!bezocht[activiteitNr][i])
			{
				allesBezocht = false;
				break;
			}
		}
		
		return allesBezocht;
	}

	protected Memento createMemento() {
		Memento m = activity.memento();
		m.setView(this);
		m.setStudentModelStructure(studentModel);
		try { 
			m.setCurrentOpdracht(Integer.parseInt(location));
		} catch(Exception oops) {}
		return m;
	}
	

	void zetToetsNagekeken(ScoreNavIF source)
	{
		int mode = on.getMode();
		if (mode == OpdrNavIF.ZELFTOETS || mode == OpdrNavIF.EINDTOETS)
		{
			zelftoetsNagekeken = true;

			if (mode == OpdrNavIF.ZELFTOETS && zelftoetsGeenCorr)
			{
				// laatste kans op update sessiontime
//				if (!zelftoetsNagekeken)
//				{
//					opdrContainer.sessionStop();
//					times[activiteitNr][opdrachtNr] = opdrContainer.getSessionTime();
//				}
//				zetAfdekPanelLeeg(true);
				
				if (zelftoetsNagekeken)
					zetAfdekPanel( !on.isReview());
				else
					zetAfdekPanel(false);
			}
			source.setKijkNaEnabled(!zelftoetsGeenCorr || on.isReview());
			//scoresObjectivesKnop.setEnabled(true);//goed? nodig?
			prev.setVisible(vorigeKnopZichtbaar || !bolletjesZichtbaar() && zelftoetsNagekeken);
			scoreNav.setScoresObjectivesKnop(on.zijnObjectivesAanwezig());
			scoreNav.setViewMisconceptionsKnop(on.zijnMisconceptionsAanwezig());
			
//			scoreNav.setTotaalScoreLabel(on.getTotaalScore());
			scoreNav.setTotaalScoreLabel((int) on.getScore()); // toon percentagescore
			scoreNav.setKeerNagekekenLabel(on.getKeerNagekeken());
		}
	}
	
	public boolean getZelftoetsNagekeken()
	{
		return zelftoetsNagekeken;
	}
	
	public void setZelftoetsNagekeken(boolean b)
	{
		zelftoetsNagekeken = b;
	}

	/**
	 * Reset de tempotoets timer, verwijder het afdekpanel.
	 */
	public void resetTimer()
	{
		zetAfdekPanelTempotoets(false, false);
		timeLimitSecondsLeft = timeLimitSeconds;
		tempotoetsLocked = false;
		if (timerTempoToets != null)
			timerTempoToets.cancel();
		paintTimer();
		scheduleTimerPainting();
	}
	
	public void gaNaarVolgendeOpdracht()
	{
		if (condNav && condNavVoorwaarden)
		{
			
//		{	states[activiteitNr][opdrachtNr] = opdrContainer.getState();
//			scores[activiteitNr][opdrachtNr] = opdrContainer.getScore();
//			if (objectives != null)
//				scoresObjectives[activiteitNr][opdrachtNr] = opdrContainer.getScoreObjectives();
//			isCorrect[activiteitNr][opdrachtNr] = opdrContainer.isCorrect();
//			stelNavigatieIn(activiteitNr, opdrachtNr);
//			gaNaarVolgendeOpdracht(activiteitNr, opdrachtNr);
			int cur = bepaalVolgendeOpdracht(on.getCurrentActiviteit(), on.getCurrentOpdracht());
			if(cur >= on.getAantalOpdrachten()) 
				cur = on.getAantalOpdrachten()-1;
			on.gotoOpdracht(cur, scoreNav);
			setCurrentOpdracht(cur, null);
		}
		else
		{	int cur = on.getCurrentOpdracht() + 1;
			if(cur >= on.getAantalOpdrachten()) 
				cur = on.getAantalOpdrachten()-1;
			on.gotoOpdracht(cur, scoreNav);
		}
	}
	
	public boolean nextPageAction() {
	  if(on.getCurrentOpdracht() >= on.getAantalOpdrachten()-1)
	      return false;
	  gaNaarVolgendeOpdracht();
	  return true;
	}
	public void gaNaarVorigeOpdracht()
	{
		if(condNav && condNavVoorwaarden)
		{
			int cur = Math.max(on.getCurrentOpdracht() - 1, 0);
			while(!bezocht[on.getCurrentActiviteit()][cur] && cur > 0)
				cur--;
			on.gotoOpdracht(cur, scoreNav);
			setCurrentOpdracht(cur, null);
		}
		else
		{
			int cur = on.getCurrentOpdracht() - 1;
			if(cur < 0) 
				cur = 0 ;
			on.gotoOpdracht(cur, scoreNav);
			setCurrentOpdracht(cur, null);
		}
	}
	
	public final void zetVolgendeOpdracht(HashMap<String,Object> opdracht, RandomValues rv) {
		zetOpdracht(opdracht, !globalParam, rv);
	}
	
	/**
	 * voor 'globale parameters'
	 * @param opdracht launchdata
	 * @param randomise (initial|| !globalparameters)
	 */
	
	public void zetOpdracht(HashMap<String, Object> opdracht, boolean randomise, RandomValues vc)
	{

		String[] varnamen = null;
		HashMap waarden = null;
		if(randomise)
		{
			try
			{
				varnamen = vc.getVariableNames();
				waarden = vc.getRandomValues();
			}
			catch (Exception ex)
			{
			}
		} else {
			varnamen = this.randomVarNamen; // keep from last time
			waarden = this.randomVarWaarden;
		}

		this.randomVarNamen = varnamen;
		this.randomVarWaarden = waarden;

		opdrachtObjects = new ArrayList<Object>();
		List<Object> opdrachtGegevens = JSONUtilities.toArrayList( opdracht.get("interactiePanelLaunchData") );
		TekstBuffer tb = new TekstBuffer(activity, varnamen, waarden, getAnchorContext());
		int[] breedtes = new int[] { 800 };
		tb.zetVolleBreedtes(breedtes);
		newVersion = Boolean.FALSE.equals( opdracht.get("hasAntwoordVak") );
		//New editor version
		if (opdrachtGegevens != null || newVersion)
		{
			if (Boolean.TRUE.equals( opdracht.get("hasTitle")))
			{
				SimplePanel title = new SimplePanel();
				title.getElement().setInnerHTML((String) opdracht.get("titel") + "<br />");
				title.getElement().getStyle().setProperty("fontWeight", "bold");
				title.getElement().getStyle().setFontSize(font_size * 1.33, Unit.PX);
				title.getElement().getStyle().setPaddingBottom(5, Unit.PX);
				title.getElement().getStyle().setPaddingTop(5, Unit.PX);
				//title.getElement().getStyle().setFloat(Float.LEFT);
				contentPanel.add(title);
			}
			
			setObjects(opdracht, contentPanel, on);

			String[] ongezien = JSONUtilities.toStringArray(opdracht.get("ongezien"));
			if (ongezien != null) {
				Collection<String> v = new ArrayList<>(Arrays.asList(ongezien));
				on.setVisited(v);
			}
 			
			
			
			setStateNull();
			stelNavigatieIn();
		}
		else if (!newVersion)
		{ //Old editor version 
			if (opdrachtGegevens != null && opdrachtGegevens.size() == 1)
			{
				HashMap<String, Object> ips = (HashMap<String, Object>) opdrachtGegevens.get(0);
				HashMap<String, Object> state = (HashMap<String, Object>) ips.get("interactiePanelLaunchState");
				opdracht.put("antwoordString", state.get("antwoordString"));
			}

			setupOldVersion(opdracht, tb);
		}
		
		if(on.isVerzegeld() && !on.isReview() /*||on.isReview()*/) {
			seal(); // push action.setNotEditable
		}

	}

/**
 *  Always set state to something. Pick up shared state.
 */
	private void setStateNull() {
		boolean old = on.pause(true);
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionView)
			{
				HashMap<String, Object> state = null;
				try {
					((InteractionView) currentObject).setState(state);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "setStateNull", e);
				}
			}
		}
		on.unpause(old);
	}

	public void zetOpdrachtPlusState(HashMap<String, Object> opdracht, HashMap<String, Object> state, RandomValues vc)
	{
		
//		//System.out.println("zetOpdrachtPlusState");
//		String randVarString;
//		randVarString = (String) opdracht.get("randVarString");
//		if(randVarString == null ) randVarString = "";
//		VariableCollection vc = new VariableCollection();
//		boolean wellSet = vc.setVariables(randVarString);
//
//		String[] varnamen = null;
//		HashMap waarden = null;
//		//if(randomise)
//		{
//			try
//			{
//				varnamen = vc.getVariableNames();
//				waarden = vc.getRandomValues();
//			}
//			catch (Exception ex)
//			{
//				wellSet = false;
//			}
//		}
//		
//		this.randomVarNamen = varnamen;
//		this.randomVarWaarden = waarden;

		if (state.get(RANDOM_VAR_NAMEN) != null)
			this.randomVarNamen = JSONUtilities.toStringArray(state.get(RANDOM_VAR_NAMEN));
		else 
			this.randomVarNamen = vc.getVariableNames();
		if (state.get(RANDOM_VAR_WAARDEN) != null)
		{
			this.randomVarWaarden = (HashMap<String, Object>) state.get(RANDOM_VAR_WAARDEN);
			vc.fromState(randomVarWaarden);
		} else
			this.randomVarWaarden = vc.getRandomValues();

		opdrachtObjects = new ArrayList<Object>();
		List<Object> opdrachtGegevens = JSONUtilities.toArrayList( opdracht.get("interactiePanelLaunchData") );
		TekstBuffer tb = new TekstBuffer(activity, randomVarNamen, randomVarWaarden, getAnchorContext());
		int[] breedtes = new int[] { 800 };
		tb.zetVolleBreedtes(breedtes);
		newVersion = Boolean.FALSE.equals( opdracht.get("hasAntwoordVak") );
		//New editor version
		
		if (opdrachtGegevens != null || newVersion)
		{
			if (Boolean.TRUE.equals( opdracht.get("hasTitle")))
			{
				SimplePanel title = new SimplePanel();
				title.getElement().setInnerHTML((String) opdracht.get("titel") + "<br />");
				title.getElement().getStyle().setProperty("fontWeight", "bold");
				title.getElement().getStyle().setFontSize(font_size * 1.33, Unit.PX);
				title.getElement().getStyle().setPaddingBottom(5, Unit.PX);
				title.getElement().getStyle().setPaddingTop(5, Unit.PX);
				//title.getElement().getStyle().setFloat(Float.LEFT);
				contentPanel.add(title);
			}
			
			setObjects(opdracht, contentPanel, on);
			//stelNavigatieIn(on.getCurrentActiviteit(), on.getCurrentOpdracht());
		}
		else if (!newVersion)
		{ //Old editor version 
			if (opdrachtGegevens != null && opdrachtGegevens.size() == 1)
			{
				HashMap<String, Object> ips = (HashMap<String, Object>) opdrachtGegevens.get(0);
				HashMap<String, Object> interactiePanelLaunchState = (HashMap<String, Object>) ips.get("interactiePanelLaunchState");
				opdracht.put("antwoordString", interactiePanelLaunchState.get("antwoordString"));
			}

			setupOldVersion(opdracht, tb);
		}
		
		setState(state);
		
		if (on.getMode() == OpdrNav.ZELFTOETS && getZelftoetsNagekeken() && isPending())
		{
			kijkNaPending(); // er is al eerder een keer gedrukt op de knop 'kijk zelftoets na', het nakijken van deze pagina is pending
			clearPending();
		}
		else if (on.getMode() == OpdrNav.EINDTOETS && on.isVerzegeld())
		{
			zetNagekeken(true);
			kijkNa();
		}

		if(on.isVerzegeld() && !on.isReview()) 
		{
			seal(); // push action.setNotEditable
		}
			
 	}

	/**
	 * Zet nakijken zelftoets pending voor de huidige activiteit en de huidige opdracht
	 * op false.
	 * 
	 */
	void clearPending()
	{
		on.setNakijkenZelftoetsPending(on.getCurrentActiviteit(), on.getCurrentOpdracht(), false);
	}

	/**
	 * True als er nog een nakijken zelftoets pending is, anders false.
	 */
	boolean isPending()
	{
		return on.nakijkenZelftoetsPending()[on.getCurrentActiviteit()][on.getCurrentOpdracht()];
	}

	protected void seal() {
		for(Object o: opdrachtObjects) {
			if(o instanceof CBookEventListener) {
				((CBookEventListener) o).acceptCBookEvent(ACTION_READONLY);
			}
		}
	}
	
	public void stelNavigatieIn()
	{
		//Omzetting in GWT: overal opdrachtenCorrect[actNr][opdrNr] vervangen door on.getOpdrachtCorrect(actNr, opdrNr)
		int opdrNr = on.getCurrentOpdracht();
		int actNr = on.getCurrentActiviteit();
		
		if ((eerderGeenCorr && bezocht[actNr][opdrNr])
			|| (getZelftoetsNagekeken() && zelftoetsGeenCorr)
			|| isVerlopenTempotoets())
		{
			zetAfdekPanel(!on.isReview());
		}
		else if (isTempotoets())
		{
			if (isAllCorrect())
			{
				tempotoetsLocked = true;
				if (timerTempoToets != null)
				{
					timerTempoToets.cancel();
				}
			}
			zetAfdekPanelTempotoets(tempotoetsLocked && !on.isReview(), isAllCorrect() && !on.isReview());
		}
		else
		{
			zetAfdekPanel(false);
		}

		try
		{
			if(bezocht!=null && opdrNr >= 0 && opdrNr < bezocht[actNr].length) // ook voor pagina 0!
			{
				bezocht[actNr][opdrNr] = true;
			}
		} 
		catch(Exception e) {}
		
		//bolletje zelf moet altijd enabled zijn, als het al een keer is bezocht.
		try
		{	
			if(bezocht[actNr][opdrNr])
			{
				scoreNav.setButtonEnabled(opdrNr,true);
				//or[actNr].setEnabled(true, opdrNr + 1);
			}
		}
		catch(Exception e){}
		
		//Als op laatste pagina: geen bolletjes in te stellen, einde-knop neerzetten
		if(opdrNr == on.getAantalOpdrachten() - 1)
		{
			zetVolgendeKnoppenEnabled(false);
			//volgendeKnop.setEnabled(false);
			
			//Nog invoegen: Einde-knop
			/*
			if(!"GR".equals(WiskOpdr.deployVariant) && !"MW".equals(WiskOpdr.deployVariant))
			{	volgendeKnop.setVisible(false);
				eindeKnop.setVisible(volgendeKnopZichtbaar);
				eindeKnop.setEnabled(true);
			}
			*/
		}
		//Als conditionele navigatie met voorwaarden, en van huidige pagina word je naar
		//menu gestuurd: einde-knop neerzetten, alle volgende bolletjes disabled.
		else if(condNav && condNavVoorwaarden && bepaalVolgendeOpdracht(actNr, opdrNr) == -1)
		{
			//if("GR".equals(WiskOpdr.deployVariant) || "MW".equals(WiskOpdr.deployVariant))
				//volgendeKnop.setEnabled(false);
				zetVolgendeKnoppenEnabled(false);
			//else
			//{	volgendeKnop.setVisible(false);
			//	eindeKnop.setVisible(volgendeKnopZichtbaar);
			//}
			for(int i = opdrNr + 1; i < on.getAantalOpdrachten(); i++)
				//or[actNr].setEnabled(false, i + 1);
				scoreNav.setButtonEnabled(i, false);
			
//			if(allesCorrectNodig && !on.getOpdrachtCorrect(actNr, opdrNr) && !on.geefNoScore(actNr, opdrNr + 1)) // klopt die + 1??
//				eindeKnop.setEnabled(false);
//			else
//				eindeKnop.setEnabled(true);
		}
		else
		{	//eindeKnop.setVisible(false);
			//eindeKnop.setEnabled(false);

			//Als leerling pas door mag als alles op pagina correct: volgende bolletjes en volgende/einde-knop disablen.
			if (allesCorrectNodig && !on.getOpdrachtCorrect(actNr, opdrNr) && !on.geefNoScore(actNr, opdrNr + 1)) //klopt die + 1??
			{
				for(int i = opdrNr + 1; i < on.getAantalOpdrachten(); i++)
				{
					scoreNav.setButtonEnabled(i, false);
				}
				zetVolgendeKnoppenEnabled(false);
				//eindeKnop.setEnabled(false);
				zetVorigeKnoppenEnabled(opdrNr > 0);
				zetNakijkKnopEnabled();
				return;
			}
			
			if (condNav && condNavPerc)
			{	//boolean conditie = on.geefNoScore(actNr, opdrNr + 1) || //or[actNr].geefNoScore(opdrNr + 1) || 
				//		100.0 * (Math.max(0, on.getScore(actNr, opdrNr) - on.getStrafpunten(actNr, opdrNr))) / on.getMaxScore(actNr, opdrNr) >= condPerc;
				boolean conditie = on.geefNoScore(actNr, opdrNr + 1) || 100 * on.getScore(actNr, opdrNr) / on.getMaxScore(actNr, opdrNr) >= condPerc;
				for (int i = opdrNr + 2; i < on.getAantalOpdrachten(); i++)
				{	//or[actNr].setEnabled(bezocht[actNr][i], i + 1);
					scoreNav.setButtonEnabled(i, bezocht[actNr][i]);
				}
				//or[actNr].setEnabled(conditie, opdrNr + 2);
				scoreNav.setButtonEnabled(opdrNr + 1, conditie);
				//volgendeKnop.setEnabled(conditie);
				zetVolgendeKnoppenEnabled(conditie);
				//eindeKnop.setEnabled(conditie);
			}
			else
			//	volgendeKnop.setEnabled(true);
				
				zetVolgendeKnoppenEnabled(true);
			if (condNav && condNavVoorwaarden)
			{
				for(int i = opdrNr + 1; i < on.getAantalOpdrachten(); i++)
				//	or[actNr].setEnabled(bezocht[actNr][i], i + 1);
					scoreNav.setButtonEnabled(i, bezocht[actNr][i]);
				if (bepaalVolgendeOpdracht(actNr, opdrNr) > -1)
				{
					scoreNav.setButtonEnabled(bepaalVolgendeOpdracht(actNr, opdrNr), !allesCorrectNodig || on.geefNoScore(actNr, opdrNr + 1) || on.getOpdrachtCorrect(actNr, opdrNr));
					if (!allesCorrectNodig)
					{
						int volgende = bepaalVolgendeOpdracht(actNr, opdrNr);
						while (bepaalVolgendeOpdracht(actNr, volgende) > -1)
						{
							if(bepaalVolgendeOpdracht(actNr, volgende) > volgende + 1)
								for(int i = volgende + 1; i < bepaalVolgendeOpdracht(actNr, volgende); i++)
								{
								//	or[actNr].setEnabled(false, i + 1);
									scoreNav.setButtonEnabled(i, false);
								}
							//or[actNr].setEnabled(true, bepaalVolgendeOpdracht(actNr, volgende) + 1);
							scoreNav.setButtonEnabled(bepaalVolgendeOpdracht(actNr, volgende), true);
							volgende = bepaalVolgendeOpdracht(actNr, volgende);
						}
						if (volgende + 1 < on.getAantalOpdrachten())
						{
							for(int i = volgende + 1; i < on.getAantalOpdrachten(); i++)
							//	or[actNr].setEnabled(false, i + 1);
							scoreNav.setButtonEnabled(i, false);
						}
					}
				}
			}
		}
		
		zetVorigeKnoppenEnabled(opdrNr > 0);
		zetNakijkKnopEnabled();
		if (!isDisabled() && isDesktop())
			hoofdPanel.tabFocus(null, false);
	}
	
	/**
	 * True als de timer van de tempotoets is verlopen,
	 * anders false.
	 * 
	 */
	private boolean isVerlopenTempotoets()
	{
		boolean verlopen = false;
		
		if (isTempotoets() && getTimeLimitSecondsLeft() == 0)
		{
			verlopen = true;
		}

		return verlopen;
	}

	private  boolean isDesktop() {
		boolean isTablet = lastState == Combined.TABLET_ACTIVE || lastState == Combined.TABLET_ACTIVE_SOFT;
		return (!isTablet || soortKeyboard == 1) && soortKeyboard != 2 ;
	}

	/**
	 * Als de tempotoets gelocked is, zet dan een afdekpanel met de bijpassende melding.
	 * Als de tempotoets niet gelocked is, verwijder dan het afdekpanel.
	 * 
	 * @param locked
	 * 		of de tempotoets gelocked is
	 * @param allCorrect
	 * 		of alle opgaven op alle bolletjes goed zijn gemaakt
	 */
	private void zetAfdekPanelTempotoets(boolean locked, Boolean allCorrect)
	{
		String disabled = dwoplayercss.disabled();
		contentPanel.setStyleName(disabled, locked);
		
		if (locked)
		{
			// geen keyboard
			getKeyboard().blur();
			// geen focus in editor
			getKeyboard().setEditor(null);
			// geen popups
			PopupFacade.hide();
			
			if (allCorrect != null && allCorrect.booleanValue())
			{
				if (timerMessage != null && timerMessagePopupPanel != null)
				{
					// toon "Op tijd klaar"
					timerMessage.setText("Op tijd klaar");
					timerMessage.getElement().getStyle().setBackgroundColor(TimerImageGenerator.greenColor);
					timerMessagePopupPanel.show();
				}
			}
			else
			{
				if (timerMessage != null && timerMessagePopupPanel != null)
				{
					// toon "De tijd is om"
					timerMessage.setText("De tijd is om");
					timerMessage.getElement().getStyle().setBackgroundColor(TimerImageGenerator.redColor);
					timerMessagePopupPanel.show();
				}
			}
		}
		else
		{
			if (timerMessagePopupPanel != null)
			{
				timerMessagePopupPanel.hide();
			}
		}
	}

	public void zetVolgendeKnoppenEnabled(boolean b)
	{	
		scoreNav.setVolgendeEnabled(b);
	}
	
	public void zetVorigeKnoppenEnabled(boolean b)
	{
		scoreNav.setVorigeEnabled(b);
	}
	
	public void zetNakijkKnopEnabled()
	{
//		boolean enable = !(zelftoetsNagekeken && zelftoetsGeenCorr) && suspendDataCompleted(on.getCurrentActiviteit(), on.getCurrentOpdracht());
		boolean enable = isKijkNaEnabled();
		scoreNav.setKijkNaEnabled(enable);
	}
	
	public int bepaalVolgendeOpdracht(int actNr, int opdrNr)
	{
		int scoreSelectie = 0;
		int scoreMaxSelectie = 0;
		int scorePercTotHier;
		int volgendeOpdracht = 0;
		
		try
		{	int[] naarPaginas = navVoorwaarden[0][opdrNr];//kan fout gaan als navVoorwaarden leeg (of niet gevuld voor opdrNr)
			int[] scorePaginas = navVoorwaarden[1][opdrNr];
			int[] grensScores = navVoorwaarden[2][opdrNr];
			
			
			for(int i = 0; i < scorePaginas.length; i++)//kan fout gaat als scorePaginas leeg
			{	if(bezocht[actNr][scorePaginas[i]-1])
				{	scoreSelectie = scoreSelectie + on.getScore(actNr, scorePaginas[i]-1);//-on.getStrafpunten(actNr, scorePaginas[i]-1);
					scoreMaxSelectie += on.getMaxScore(actNr, scorePaginas[i]-1);
				}
			}
			scorePercTotHier = 100 * scoreSelectie / scoreMaxSelectie;//kan fout gaan bij delen door 0
			
			if(scorePercTotHier <= grensScores[0])
				volgendeOpdracht = naarPaginas[0] - 1;
			else
			{	for(int i = 1; i < grensScores.length; i++)//kan fout gaat als grensscores leeg
					if(scorePercTotHier > grensScores[i-1] && scorePercTotHier <= grensScores[i])
						volgendeOpdracht = naarPaginas[i] - 1;
			}
			if(scorePercTotHier > grensScores[grensScores.length - 1])
				volgendeOpdracht = naarPaginas[grensScores.length - 1] - 1;
		}
		catch(Exception e)//als bovenstaande niet lukt, ga je gewoon naar de volgende pagina.
		{	
			if(opdrNr < on.getAantalOpdrachten() - 1)
				volgendeOpdracht = opdrNr + 1;	
			else
				volgendeOpdracht = -1;
		}
		if(volgendeOpdracht >= on.getAantalOpdrachten())
			volgendeOpdracht = -1;
		return volgendeOpdracht;
		
	}

	//Sets up a FormuleEditorWithSteps for each assignment
	private void setupOldVersion(HashMap<String, Object> opdracht, TekstBuffer tb)
	{
		tekst = new FlowPanel();
		Object object = opdracht.get("scheidingX");
		if(object == null ) object = new Integer ( 0 );
		tekst.getElement().getStyle().setWidth((Integer) object / 8, Unit.PCT);
		tekst.getElement().getStyle().setFloat(Float.LEFT);
		tekst.getElement().getStyle().setPadding(5, Unit.PX);
		SimplePanel title = new SimplePanel();
		title.getElement().setInnerText((String) opdracht.get("titel"));
		title.getElement().getStyle().setProperty("fontWeight", "bold");
		title.getElement().getStyle().setFontSize(font_size * 2, Unit.PX);
		title.getElement().getStyle().setPaddingBottom(5, Unit.PX);
		title.getElement().getStyle().setPaddingTop(5, Unit.PX);
		tekst.add(title);
		opdrachtObjects = tb.convertTekst(opdracht);
		//setObjects(opdrachtObjects, tekst);
		setObjects(opdracht, tekst, on);
		contentPanel.add(tekst);
		FormuleEditorWithSteps fews = new FormuleEditorWithSteps(activity, opdracht, false, tb.getVarNamen(), tb.getVarWaarden(), null);

		//fews.getEditor().requestFocus();
		

		contentPanel.add(fews.getAsPanel());
	}

	public void setMeasuredMisconceptions(int[][][][] mm)
	{
		if(mm == null)
			return;
		measuredMisconceptions = new int[mm.length][][][];
		for(int i = 0; i < mm.length; i++)
		{	measuredMisconceptions[i] = new int[mm[i].length][][];
			for(int j = 0; j < measuredMisconceptions[i].length; j++)
			{
				measuredMisconceptions[i][j] = new int[mm[i][j].length][];
				for(int k = 0; k < measuredMisconceptions[i][j].length; k++)
				{	measuredMisconceptions[i][j][k] = new int[mm[i][j][k].length];
					for(int l = 0; l < measuredMisconceptions[i][j][k].length; l++)
						measuredMisconceptions[i][j][k][l] = mm[i][j][k][l];
				}
			}
		}
	}
	
	public void setMeasuredMisconceptions(int actNr, int opdrNr, int[][] mm)
	{
		
	}
	
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.on = (OpdrNav) comRoot;
	}

	public HashMap<String, Object> getState() // equivalent met opdrContainer.getstate()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		int aantalInteractionViews = 5;
		ArrayList<Object> states = new ArrayList<Object>(opdrachtObjects.size() + 5);
		for (int i = 0; i < 5; i++)
			states.add(null);

		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionView && ! (currentObject instanceof StateLess) )
			{
				states.add(aantalInteractionViews, ((InteractionView) currentObject).getState());
				if (on.getMode() == OpdrNav.ZELFTOETS && !zelftoetsNagekeken)
				{
					// update de scores en isCorrect als de toets nog niet is nagekeken
					on.setScoresZelftoets(on.getCurrentActiviteit(), on.getCurrentOpdracht(), ((InteractionView) currentObject).getScore());
					on.setIsCorrectZelftoets(on.getCurrentActiviteit(), 
						on.getCurrentOpdracht(), 
						((InteractionView) currentObject).isCorrect());
				}
				aantalInteractionViews++;
			}
		}
		
		h.put("interactiePanelStates", states);
		h.put(RANDOM_VAR_NAMEN, randomVarNamen);
		h.put(RANDOM_VAR_WAARDEN, randomVarWaarden);

		
		return h;
	}

	public void setState(HashMap<String, Object> h)
	{
		if (h.get(RANDOM_VAR_NAMEN) != null)
			this.randomVarNamen = JSONUtilities.toStringArray(h.get(RANDOM_VAR_NAMEN));
		if (h.get(RANDOM_VAR_WAARDEN) != null)
			this.randomVarWaarden = (HashMap<String, Object>) h.get(RANDOM_VAR_WAARDEN);
		List<Object> states = JSONUtilities.toArrayList(h.get("interactiePanelStates"));
		int stateNr = 5;
		boolean old = on.pause(true);
		for (int i = 0; states != null && i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionView)
			{
				HashMap<String, Object> state = stateNr < states.size() ? (HashMap<String, Object>) states.get(stateNr) : new HashMap();
				// het nakijken van de zelftoets doorgeven
				((InteractionView) currentObject).setState(state);
				stateNr++;
			}
		}
		on.unpause(old);
		stelNavigatieIn();
		if(misconceptions != null)
		{
			beginStateMeasuredMisconceptions = new int[opdrachtObjects.size()][][];
			for (int i = 0; i < opdrachtObjects.size(); i++)
			{
				Object currentObject = opdrachtObjects.get(i);
				if (currentObject instanceof InteractionViewWithMisconceptions)
				{
					beginStateMeasuredMisconceptions[i] = ((InteractionViewWithMisconceptions) currentObject).getMeasuredMisconceptions();
				}
			}
		}
	}
	
	/**
	 * Deze methode kijkt de huidige opdracht van een zelftoets na.
	 */
	public void kijkNa()
	{
		on.pause();
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionView)
			{
				((InteractionView)currentObject).kijkNa();

				// update de scores en isCorrect
				on.setScoresZelftoets(on.getCurrentActiviteit(), on.getCurrentOpdracht(), ((InteractionView) currentObject).getScore());
				on.setIsCorrectZelftoets(on.getCurrentActiviteit(), 
					on.getCurrentOpdracht(), 
					((InteractionView) currentObject).isCorrect() );
				
				on.setScores(on.getCurrentActiviteit(), on.getCurrentOpdracht(), ((InteractionView) currentObject).getScore());
				on.setIsCorrect(on.getCurrentActiviteit(), 
					on.getCurrentOpdracht(), 
					((InteractionView) currentObject).isCorrect() );

			}
		}
		on.unpause();
	}
	
	/**
	 * Deze methode kijkt de huidige pagina van een zelftoets na 
	 * omdat deze nog pending is. De score-administratie is al tijdens het navigeren gedaan.
	 */
	public void kijkNaPending()
	{
		on.pause();
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionView)
			{
				((InteractionView)currentObject).kijkNa();
				((InteractionView)currentObject).zetNagekeken(true);
			}
		}
		on.unpause();
	}
	
	public void zetNagekeken(boolean b)
	{
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionView)
			{
				((InteractionView)currentObject).zetNagekeken(b);
			}
		}
	}
	
	public void zetAfdekPanel(boolean b)
	{
		String disabled = dwoplayercss.disabled();
		contentPanel.setStyleName(disabled, b);
		
		if(b)
		{
			// geen keyboard
			getKeyboard().blur();
			// geen focus in editor
			getKeyboard().setEditor(null);
			// geen popups
			PopupFacade.hide();
		}
	}
	
	/**
	 * Hiermee wordt gevraagd of er supenddata zijn van alle opdrachten van
	 * deze activiteit, behalve die met het meegegeven opdrachtnummer (huidige
	 * opdracht).
	 */
	public boolean suspendDataCompleted(int actNr, int opdrNr)
	{
		boolean completed = true;
		if(condNav && condNavVoorwaarden)
		{	if(bepaalVolgendeOpdracht(actNr, opdrNr) > -1 && opdrNr < on.getAantalOpdrachten() - 1)
			{
			completed = false;
			}
		}
		else
			for (int j = 0; j < on.getAantalOpdrachten(); j++)
			{
				if(opdrNr != j)
				{	completed = bezocht != null && bezocht[on.getCurrentActiviteit()] != null && bezocht[on.getCurrentActiviteit()][j];
					if(!completed)
						break;
				}
			}
		return completed;
	}

	public int[][] getScoreObjectives()
	{
		if (objectives == null)
			return null;
		int[][] scoreObjectives = new int[objectives.length][];
		for (int i = 0; i < objectives.length; i++)
			scoreObjectives[i] = new int[objectives[i].length];
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionView)
			{
				int[][] scoreObj = ((InteractionView) currentObject).getScoreObjectives();
				for (int j = 0; scoreObj != null && j < objectives.length && j < scoreObj.length; j++)
				{
					for (int k = 0; scoreObj[j] != null && k < objectives[j].length && k < scoreObj[j].length; k++)
						try{	scoreObjectives[j][k] += scoreObj[j][k];
						}
						catch(Exception e){}
					
				}
			}
		}
		return scoreObjectives;
	}
	
	public int[][] getPossibleMisconceptions()
	{
		if (misconceptions == null)
			return null;
		int[][] totalPossibleMisconceptions = new int[misconceptions.length][];
		for (int i = 0; i < misconceptions.length; i++)
			totalPossibleMisconceptions[i] = new int[misconceptions[i].length];
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionViewWithMisconceptions)
			{
				int[][] possibleMisconceptions = ((InteractionViewWithMisconceptions) currentObject).getPossibleMisconceptions();
				for (int j = 0; possibleMisconceptions != null && j < misconceptions.length && j < possibleMisconceptions.length; j++)
				{
					for (int k = 0; possibleMisconceptions[j] != null && k < misconceptions[j].length && k < possibleMisconceptions[j].length; k++)
						try{	totalPossibleMisconceptions[j][k] += possibleMisconceptions[j][k];
						}
						catch(Exception e){}
				}
			}
		}
		return totalPossibleMisconceptions;
	}
	
	public int[][] getMeasuredMisconceptions()
	{
		if (misconceptions == null)
			return null;
		int[][] totalMeasuredMisconceptions = new int[misconceptions.length][];
		for(int i = 0; i < misconceptions.length; i++)
		{	totalMeasuredMisconceptions[i] = new int[misconceptions[i].length];
			for(int j = 0; j < misconceptions[i].length; j++)
				totalMeasuredMisconceptions[i][j] += measuredMisconceptions[on.getCurrentActiviteit()][on.getCurrentOpdracht()][i][j]; 
		}
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionViewWithMisconceptions)
			{
				int[][] opdrMeasuredMisconceptions = ((InteractionViewWithMisconceptions) currentObject).getMeasuredMisconceptions();
				for (int j = 0; opdrMeasuredMisconceptions != null && j < misconceptions.length && j < opdrMeasuredMisconceptions.length; j++)
				{
					for (int k = 0; opdrMeasuredMisconceptions[j] != null && k < misconceptions[j].length && k < opdrMeasuredMisconceptions[j].length; k++)
					{	if(beginStateMeasuredMisconceptions == null || beginStateMeasuredMisconceptions[i][j][k] == 0)
						{	try{	totalMeasuredMisconceptions[j][k] += opdrMeasuredMisconceptions[j][k];
							}
							catch(Exception e){}
						}
					}
				}
			}
		}
		return totalMeasuredMisconceptions;
	}

	
	public int getScore()
	{
		int score = 0;
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionView)
			{
				score += ((InteractionView) currentObject).getScore();
			}
		}
		return score;
	}

	public Boolean isCorrect()
	{
		Boolean correct = Boolean.TRUE;
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionView)
			{
				Boolean check =  ((InteractionView) currentObject).isCorrect();
				if(check == null) correct = null;
				if(Boolean.FALSE.equals(check) ) return check;
			}
		}
		return correct;
	}
	
	/**
	 * Determine whether all opdrachten in the activiteit are correct.  
	 * 
	 * @return
	 */
	public Boolean isAllCorrect()
	{
		Boolean correct = Boolean.TRUE;
		
		// loop over alle bolletjes
		for (int j = 0; j < on.getAantalOpdrachten(); j++)
		{
			correct = on.isCorrect(on.getCurrentActiviteit(), j);
			if (!correct)
				return correct;
		}
		
		return correct;
	}
	
	public boolean isPilotObjectives()
	{
		return pilotObjectives;
	}


	public OpdrNavIF getOpdrNav()
	{
		return on;
	}

	public void zoomIn(double ratio)
	{
		if (zoom < 3)
		{
			zoom = zoom + ratio;
			if (zoom > 3)
			{
				zoom = 3;
			}
			//contentPanel.getElement().getStyle().setProperty("zoom", Double.toString(zoom));
			//if(kb!=null)kb.zoomIn();
		}

	}

	public void zoomOut(double ratio)
	{
		if (zoom > 1)
		{
			zoom = zoom - ratio;
			if (zoom < 1)
			{
				zoom = 1;
			}
			//if(kb!=null)kb.zoomOut();
			//contentPanel.getElement().getStyle().setProperty("zoom", Double.toString(zoom));
		}
	}

	private ViewModuleView.Loader loadingHandler = null;
	public ViewModuleViewImpl initialize(ViewModuleView.Loader pLoadingArea) {
		
		this.loadingHandler =  pLoadingArea;
		return this.initialize();
	}
	
//	private final class PinchContent implements PinchHandler, com.google.gwt.animation.client.AnimationScheduler.AnimationCallback {
//		double zoom = 1.0;
//		private AnimationHandle handle;
//		
//		@Override
//		public void onPinch(PinchEvent event) {
//			double factor = event.getScaleFactor();
//			int x = event.getX();
//			int y = event.getY();
//			zoom = zoom / factor;
//			zoom = Math.max(1.0, zoom);
//			zoom = Math.min(5.0, zoom);
//			logger.info("x=" + x + ", y= " + y + ", scale=" + factor + ", z=" + zoom);
//			if(handle == null) {
//				handle = AnimationScheduler.get().requestAnimationFrame(this,contentPanel.getElement());
//			}
//		}
//
//		@Override
//		public void execute(double timestamp) {
//			handle = null;
//			contentPanel.getElement().getStyle().setProperty("zoom", String.valueOf(zoom));
//		}
//	}

//	final class Resizer implements ResizeHandler {
//		@Override
//		public void onResize(ResizeEvent event) {
//			int h = event.getHeight() - extraHeight;
//			//logger.info("resize event " +  h);
//			sb.setScrollPanel(ViewModuleViewImpl.this, h);
//			
//		}
//	}

	private Map<String,Element> anchors;
	private void clearElements() { anchors = null; }
	
	class MyAnchorContext implements AnchorContext {

		@Override
		public void gotoUrl(String href) {
			if(href.startsWith("goto:."))
			{
				String[] split = href.split("#", 2);
				href = split[0];
				String hash = null;
				int opdrnr = Integer.parseInt(href.substring(6)) -1 ; // 0based
				on.gotoOpdracht(opdrnr, scoreNav);
				if (split.length > 1) {
					{
						gotoElement(hash = split[1]);
					}
				}
				setCurrentOpdracht(opdrnr, hash);
			} else {
				on.gotoUrl(href);
			}
			
			// iets met de place controller.....
			
		}

		@Override
		public void gotoPlace(String token) {
			on.gotoUrl("#" + token);
		}

		@Override
		public void addElement(String anchor, Element e) {
			if (anchors == null) anchors = new HashMap<>();
			anchors.put(anchor, e);
		}

		@Override
		public void gotoElement(String anchor) {
			if (anchors != null) {
				Element e = anchors.get(anchor);
				if (e != null) 
				OpdrNav.defer(
						() -> 
					
					AnimationScheduler.get().requestAnimationFrame(
							(double timestamp) -> {e.scrollIntoView();},
							e));
			}	
		}
	}
	
	
	AnchorContext anchorContext = new MyAnchorContext();
	
	private boolean inNavBtn;
	
	public ViewModuleViewImpl initialize()
	{
		mainPanel = createAndBindUI();
		//mainPanel = FocusOnTouch.wrap(mainPanel);
		mainPanel.setStylePrimaryName("mainPanel");
		mainPanel.addStyleDependentName(activity.parameters().keyboardStyle());
		mainPanel.setStyleDependentName("standalone", standalone);
		
		focusPanel = new ResizeFocusPanel(mainPanel); // wrap focuspanel

		FocusOnTouch.wrap(focusPanel);
		mainPanel = focusPanel;
		mainPanel.setStylePrimaryName("mainPanel");
		
		state = lastState;
		sb = activity.parameters().getStatusBar(activity); // new nl.uu.fi.dwo.mobile.client.ui.FormuleKeyboard();
		sb.setCombinedState(this);
		kb = sb.getFormuleKeyboard();
		cb = sb.getFormuleClipboard();
		scoreNav = activity.parameters().getScoreNav(activity);
		POPUP = scoreNav.getPopup();

		scoreNav.setStatusBar(sb);
		if(!standalone) setWindowTop(0);
		FocusOnTouch.installKeyboard(kb, cb);
		FormuleHolder.installKeyboard(kb);
		
//		hp = new HeaderPanel(activity.parameters().headercss());
		setTitle("");
		next = scoreNav.getNextButton();
		prev = scoreNav.getPrevButton();
		end = scoreNav.getEndButton();
		if(standalone) {
			HorizontalPanel hbox = new HorizontalPanel();
			hbox.add(prev); hbox.add(next);
			//tijdelijk: nog even weglaten.
			//if(end != null)
			//	hbox.add(end);
//			hp.setRightWidget(hbox);
//			hb = new HeaderButton(activity.parameters().headercss());
//			hb.getElement().getStyle().setBackgroundImage("url('" + DWOplayer.DWO_BUNDLE.menuIcon().getSafeUri().asString() + "')");
//			hp.setLeftWidget(hb);
//			headerView.setWidget(hp);
//			setWindowTop(extraHeight);
		}

		contentPanel = content;
// smooth scroll on ios devices:
		setWebkitScrolling(true);
// Hier moeten we een gesture recognizer maken:

//		if(TouchEvent.isSupported())
//		{
//			TouchDelegate touchDelegate = new TouchDelegate(contentScrollPanel);
//			//touchDelegate.addPinchHandler(new PinchContent());
//			
//			touchDelegate.addSwipeEndHandler(new SwipeEndHandler() {
//				
//				@Override
//				public void onSwipeEnd(SwipeEndEvent event) {
//					switch( event.getDirection()) {
//					case LEFT_TO_RIGHT:  gotoPrev(scoreNav); break;
//					case RIGHT_TO_LEFT: gotoNext(scoreNav); break;
//					// TODO case BOTTOM_TO_TOP: showScore(scoreNav);
//					default:
//					}
//					
//				}
//			});
//			
//			touchDelegate.addTouchEndHandler(new TouchEndHandler() {
//
//				@Override
//				public void onTouchEnd(TouchEndEvent event)
//				{
//					logger.info("ViewModuleViewImpl.touchDelegate.onTouchEnd()");
//				}
//				
//			});
//		}
		//ipv addContentPanelTouchListener(contentPanel);

//		contentScrollPanel.add(contentPanel);
//		contentPanel.getElement().getStyle().setOverflowY(Overflow.AUTO);
//		contentPanel.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
		
//		fp.add(contentScrollPanel);

		Widget kbp = sb.asWidget();
		statusView.setWidget(kbp);

// POPUP of floating in ????
//		if(hb != null && POPUP != null)
//		{  hb.addTapHandler(new TapHandler() {
//
//			@Override
//			public void onTap(TapEvent event) {
//				if(POPUP.isShowing())
//						POPUP.hide();
//				else
//						popupNavPanel();
//				
//			}});
//			POPUP.addAutoHidePartner(hb.getElement());
//		}
		//initWidget(mainPanel);
		
		return this;

	}

	private void setWebkitScrolling(boolean b) {
//		Style style = contentPanel.getElement().getStyle();
//		if (b)
//			style.setProperty("WebkitOverflowScrolling", "touch");
//		else
//			style.clearProperty("WebkitOverflowScrolling");
	}
	
	
	protected void showScore(ScoreNavIF nav) {
		nav.showScore();
	}

	//private ScoreNavPanel scoreNavPanel = new ScoreNavPanel();
    PopupPanel POPUP;
    
    public ScoreNavIF scoreNav; 
    
    protected void popupNavPanel() {
		 final PopupPanel popup = POPUP;
	        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
	          public void setPosition(int offsetWidth, int offsetHeight) {
	            int left = (Window.getClientWidth() - offsetWidth) / 3;
	            int top = (Window.getClientHeight() - offsetHeight) / 3;
	            left = 0;
	            top  = headerView.getOffsetHeight();
	            popup.setPopupPosition(left, top);
	            popup.setPixelSize(offsetWidth, Window.getClientHeight()-top-12);
	          }
	        });
		
	}

	public void zetMaatNoordhoff()
	{
		extraHeight = 40;
		//FIXME fp.setWidgetSize(headerView, extraHeight);
		sb.zetMaat();
		int size = sb.getStatusBarHeight();
		sb.setScrollPanel(this, -size);
	}
	
	protected int extraHeight = (MGWT.getOsDetection().isAndroid() ? 52:41); // header height in android 50+2 			
			
	private String unitId = "scoViewNr";
	
	public void setUnitId(String unitId) {
		this.unitId = unitId;
		api.setScoID(unitId);
	}

	public void setWindowTop(int top) {
		if(!standalone) top = 0; // force 0
		extraHeight = top;
		//FIXME fp.setWidgetSize(headerView, top);
		fp.setWidgetTopHeight(headerView, 0, Unit.PX, extraHeight, Unit.PX);
		fp.setWidgetTopBottom(contentScrollPanel, extraHeight, Unit.PX, lastSize, Unit.PX);
	}
	
	
	public void zetMaat() {

		// FIXME HACK voor DWOplayer zelf		
//		hb = new HeaderButton(activity.parameters().headercss()); hb.setBackButton(true);
//		hb.setText(fi.wiskopdr.text.Text.constants.terugKnopLabel());
//		hp.setLeftWidget(hb);
//		hp.setRightWidget(null);
		
		///contentPanel.getElement().getStyle().setMarginBottom(360, Unit.PX);
		//int contentHeight = Window.getClientHeight() - extraHeight;
		//Window.addResizeHandler(new Resizer());
		sb.zetMaat();
		sb.setScrollPanel(this, -sb.getStatusBarHeight());

	}

	@Override
	public Widget asWidget()
	{
		return mainPanel;
	}

	public Scorm2004IF getApi() {
		return api;
	}

	@Override
	public void close() {
		scoreNav.stopped(); // ASYNC
		if(on != null)
			on.close();
		PopupFacade.removeAll();
		clearElements();
		kb.blur();
		
		if (isTempotoets())
		{
			removeTimer();
		}
	}

	/**
	 * Remove the timer related to the tempotoets.
	 */
	private void removeTimer()
	{
		if (timerTempoToets != null)
			timerTempoToets.cancel();
		else
			System.out.println("ViewModuleViewImpl.removeTimer(): timerTempoToets == null!");
		
		timerPanel.hide();
		timerMessagePopupPanel.hide();
		timerMessagePopupPanel.setVisible(false);
	}

	public FormuleKeyboardIF getKeyboard() {
		return kb;
	}

	public void setTitle(String string) {
		if(string == null) string = "";
//		if(standalone) hp.setCenter(string);
		
	}

	@Override
	public AnchorContext getAnchorContext() {
		return anchorContext;
	}
	public void setAnchorContext(AnchorContext context) {
		if (context == null) context = new MyAnchorContext();
		anchorContext = context;
	}

	public String getUnitId() {
		return unitId;
	}

	public void gotoNext(ScoreNavIF source)
	{
		gotoNext(source.getNextButton());
	}
	
	private void gotoNext(final Widget next) {
		if(inNavBtn) {
			return;
		}
		inNavBtn = true;p();
		next.getElement().getStyle().setProperty("pointerEvents", "none");
		((Element) next.getElement().getLastChild()).getStyle().setBackgroundColor("gray");
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				gaNaarVolgendeOpdracht();			
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						inNavBtn = false;v();
							next.getElement().getStyle().clearProperty("pointerEvents");
							((Element) next.getElement().getLastChild()).getStyle().clearBackgroundColor();
					}});
			}});
	}

	public void gotoPrev(ScoreNavIF source) {
		gotoPrev(source.getPrevButton());
	}
	
	private void gotoPrev(final Widget prev) {
		if(inNavBtn) {
			return;
		}
		inNavBtn = true;p();
		prev.getElement().getStyle().setProperty("pointerEvents", "none");
		((Element) prev.getElement().getLastChild()).getStyle().setBackgroundColor("gray");
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				gaNaarVorigeOpdracht();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						inNavBtn = false;v();
						prev.getElement().getStyle().clearProperty("pointerEvents");
						((Element) prev.getElement().getLastChild()).getStyle().clearBackgroundColor();
					}});
			}});
	}

	@Override
	public void openObjectivesPanel(ScoreNavIF source) {
		on.openObjectivesPanel(pilotObjectives);
	}
	
	public void logObjectivesPanelOpen(String studentModel)
	{
		if(dwologger == null)
		{
			dwologger = new DWOLogger(activity);
		    dwologger.setMaxScore(0);
		    dwologger.setLogID("StudentModelButton");
		}
		
		Map<String, Object> log  = new HashMap<String, Object>();
		log.put("success", Boolean.TRUE);
		log.put("response", studentModel);
		log.put("score", Collections.singletonMap("raw", 0));
		log.put("step", "");
		
		dwologger.log(log);
	}
	
	public void logObjectivesPanelClose()
	{
		Map<String, Object> log  = new HashMap<String, Object>();
		log.put("success", Boolean.TRUE);
		log.put("response", "close");
		log.put("score", Collections.singletonMap("raw", 0));
		log.put("step", "");
		
		dwologger.log(log);
	}
	
	@Override
	public void openMisconceptionsPanel(ScoreNavIF source) {
		on.openMisconceptionsPanel();
	}

	// WaitScreen management: p(); .....; v();
	private int sema;
	private Deferred<Void> sema2;
	@UiField LayoutPanel fp;
	@UiField SimplePanel headerView;
	@UiField SimplePanel statusView;
	@UiField(provided=true) FlowPanel content;
	@UiField Widget kbd;
	
	
	private int soortKeyboard;
	@UiHandler("kbd") void onKBD(ClickEvent e) {
	  if (soortKeyboard != 0) return;
	  switch(state) {
		case TABLET:
			setCombined(Combined.TABLET_ACTIVE_SOFT);
			break;
		case DESKTOP_ACTIVE:
			setCombined(Combined.TABLET_ACTIVE);
			break;
		case NONE:
			break;		
		case TABLET_ACTIVE:
			setCombined(Combined.DESKTOP_ACTIVE);
			break;
		case TABLET_ACTIVE_SOFT:
			setCombined(Combined.TABLET);
			break;
	  }
	}

	private FocusPanel focusPanel;
	
	public void p() {
		if( sema++ == 0) {
			sema2 = new Deferred<Void>();
			activity.agent().addBarrier(sema2.getPromise());
			waitscreen.w();
		}
	}
		
	public void v() {
		if ( --sema <= 0) {
			sema = 0;
			waitscreen.hide();
			sema2.resolve(null);
		}
	}

	public FormuleClipboardIF getClipboard() {
		return cb;
	}

	private double lastSize = -1;
	@Override
	public void setHeight(int px) {
		setWebkitScrolling(false);
		//contentScrollPanel.setPixelSize(-1, px);
		double size = Math.abs(px); // FIXME berekening.....
		if(size != lastSize) {
			// FIXME fp.setWidgetSize(statusView, size);
		    fp.setWidgetBottomHeight(statusView, 0, Unit.PX, size, Unit.PX);
		    fp.setWidgetTopBottom(contentScrollPanel, extraHeight, Unit.PX, size, Unit.PX);
			lastSize = size;
			//fp.animate(300);
			//fp.setWidgetVisible(kbd, size == sb.getStatusBarHeight()); // XXX move kbd?
			if (state == Combined.DESKTOP_ACTIVE) setKbdCss(state);
		}
		setWebkitScrolling(true);
	}

	@Override
	public Number getScoreRaw() {
		if(on == null)
			return null;
		return Double.valueOf(on.getScore());
	}

	
	private boolean readonly;
	protected Promise<DomStudentModelContextId> studentModel;
	private Presenter presenter;

	/**
	 * @return the readonly
	 */
	public boolean isReadonly() {
		return readonly;
	}

	/**
	 * @param readonly the readonly to set
	 */
	public void setReadonly(boolean readonly) {
		boolean old = this.readonly;
		this.readonly = readonly;
		if(readonly != old) {
// FIXME ....
			if(readonly) 
				seal();
			// else no way to break seal();
		}
	}

	/**
	 * Schedule the painting of the timer for tempotoets.
	 * 
	 */
	private void scheduleTimerPainting()
	{
		timerTempoToets = new Timer()
		{
			@Override
			public void run()
			{
				timeLimitSecondsLeft = Math.max(timeLimitSecondsLeft - 1, 0); // nooit kleiner dan 0; 0 moet getekend worden, daarna niet meer
				paintTimer();
				//GWT.log("ViewModuleViewImpl.scheduleTimerPainting(): timeLimitSecondsLeft = " + timeLimitSecondsLeft);
				
				if (timeLimitSecondsLeft == 0)
				{
					// tijd is om!
					setTempotoetsLocked();
				}
				else
					scheduleTimerPainting();
			}
		};
		int delay = 1000 - (int) (System.currentTimeMillis() % 1000);
		timerTempoToets.schedule(delay);
	}

	private void paintTimer()
	{
		TimerImageGenerator.drawImage(timerCanvas.getCanvasElement(), timeLimitSeconds, timeLimitSecondsLeft);
	}

	/**
	 * Set tempotoets locked.
	 */
	public void setTempotoetsLocked()
	{
		timerPanel.setGlassEnabled(true);
		tempotoetsLocked = true;
		if (isAllCorrect())
		{
			timerTempoToets.cancel();
		}
		zetAfdekPanelTempotoets(tempotoetsLocked, isAllCorrect());
	}
	
	public int getTimeLimitSecondsLeft()
	{
		return timeLimitSecondsLeft;
	}



	/**
	 * Class TimerImageGenerator for the timer in a tempotoets.
	 * 
	 * @author Sylvia van Borkulo
	 *
	 */
	private static class TimerImageGenerator
	{
		private static final String secondsLabel = " sec";
		private static final String transparentColor = "rgba(0,0,0,0)";
		private static final String blackColor = "#000000";
		private static final String greenColor = "#00B400";// groen uit WiskOpdr
		private static final String redColor = "#FF9696";// rood uit WiskOpdr

		private Context2d context;
		private double width;
		private double height;
		private double r;
		private double centerX;
		private double centerY;
		
		private int timeLimitSeconds;
		private int timeLimitSecondsLeft;

		private TimerImageGenerator(CanvasElement canvas, int timeLimitSeconds, int timeLimitSecondsLeft)
		{
			context = canvas.getContext2d();
			width = canvas.getWidth();
			height = canvas.getHeight();
			r = Math.min(width, height) * 2 / 5;
			centerX = width / 2;
			centerY = 8 + (height * 2 / 3) / 2;
			this.timeLimitSeconds = timeLimitSeconds;
			this.timeLimitSecondsLeft = timeLimitSecondsLeft;
		}

		public static void drawImage(CanvasElement canvas, int timeLimitSeconds, int timeLimitSecondsLeft)
		{
			new TimerImageGenerator(canvas, timeLimitSeconds, timeLimitSecondsLeft).drawImage();
		}

		@SuppressWarnings("deprecation")
		private void drawImage()
		{
			// clear the canvas 
			context.clearRect(0, 0, width, height);

			// Paint outer background.
			context.save();
			context.setFillStyle(transparentColor);
			context.fillRect(0, 0, width, height);
			context.restore();
			
			// Paint the timer
			
			// green part (time left)
			context.save();
			context.setFillStyle(greenColor);
			context.beginPath();
			context.moveTo(centerX, centerY);
			context.arc(centerX, centerY, r, (2 * Math.PI * (timeLimitSeconds - timeLimitSecondsLeft) / timeLimitSeconds) - Math.PI / 2, 2 * Math.PI - Math.PI / 2);
			context.fill();
			
			// red part (time elapsed)
			context.save();
			context.setFillStyle(redColor);
			context.beginPath();
			context.moveTo(centerX, centerY);
			context.arc(centerX, centerY, r, - Math.PI / 2, (2 * Math.PI * (timeLimitSeconds - timeLimitSecondsLeft) / timeLimitSeconds) - Math.PI / 2);
			context.fill();

			// Draw text.
			drawSecondsLeft();
		}

		/**
		 * Draw the seconds left beneath the timer.
		 */
		private void drawSecondsLeft()
		{
			context.save();
			long fontSize = Math.round(r / 3);
			context.setFont("bold " + fontSize + "px Helvetica, sans-serif");
			context.setTextAlign(Context2d.TextAlign.CENTER);
			context.setTextBaseline(Context2d.TextBaseline.MIDDLE);
			context.setFillStyle(blackColor);
			double x = centerX;
			double y = height - 5;
			context.fillText(timeLimitSecondsLeft + secondsLabel, x, y);
			context.restore();
			
			//System.out.println("ViewModuleViewImpl.TimerImageGenerator.drawSecondsLeft(): left = " + timeLimitSecondsLeft);
		}

	} // end class ClockImageGenerator

	/**
	 * Geeft aan of de huidige opdracht in de huidige activiteit veranderd is na het
	 * nakijken van de zelftoets.
	 * 
	 * @param activiteitNr
	 * @param opdrachtNr
	 * @return
	 */
	public boolean isVeranderdNaNakijken(int activiteitNr, int opdrachtNr)
	{
		boolean isVeranderd = false;
		
		if (on.getScore(activiteitNr, opdrachtNr) != on.getScoresZelftoets(activiteitNr, opdrachtNr)) 
			// let op: dit zijn somscores per opdracht; dat die hetzelfde blijven, wil nog niet zeggen dat de antwoorden niet gewijzigd zijn
			// bovendien weet je nu niet welke interactionview op opdrachtNr al dan niet nagekeken moet worden
			isVeranderd = true;
		
		return isVeranderd;
	}

	@Override
	public void setTrail(List<SelectModuleItem> trail) {
	}

	public boolean isScoresZichtbaar()
	{
		return scoresZichtbaar;
	}

	@Override
	public void setPresenter(Presenter p) {
		this.presenter = p;
	}

	@Override
	public void removeBtns() {
	}

	@Override
	public void setScoType(ScoType type) {
	}

	@Override
	public void setModel(Promise<DomStudentModelContextId> studentModel) {
		this.studentModel = studentModel;
		if(on != null)
			on.setStudentModel(studentModel);
	}

  @Override
  public void showIcon(boolean b) {
    // TODO Auto-generated method stub
    
  }

@Override
public HandlerRegistration addChangeHandler(ChangeHandler handler) {
	this.handler = handler;
	return () -> { this.handler = null; };
}

Combined state = Combined.TABLET;
static Combined lastState = Combined.NONE;

static {
	if (MGWT.getOsDetection().isDesktop() && !TouchStartEvent.isSupported()) // INITIAL form of DESKTOP/MOBILE
		lastState = Combined.TABLET;
	else
		lastState = Combined.TABLET_ACTIVE_SOFT;
}



ChangeHandler handler;
@Override
public void setCombined(Combined state) {
	Combined old = this.state;
	this.state = state;
	fp.setWidgetVisible(kbd, soortKeyboard == 0 && state != Combined.NONE);
	if (state != Combined.NONE) lastState = state;
	if (state != old && handler != null) {
		handler.onChange(null);
		setKbdCss(state); // keyboard height is valid
	}
}

private void setKbdCss(Combined state) {
	if (soortKeyboard != 0) return;
	switch(state) {
	case DESKTOP_ACTIVE:
		double h = lastSize;
		if (h >= 100) {
			kbd.removeStyleName(dwoplayercss.tablet());
			kbd.removeStyleName(dwoplayercss.tablet_active());
			kbd.addStyleName(dwoplayercss.desktop_active());
			fp.setWidgetBottomHeight(kbd, sb.getStatusBarHeight()+8, Unit.PX, 62, Unit.PX);
			fp.setWidgetLeftWidth(kbd, 10, Unit.PX, 52, Unit.PX);
			break;
		}
		kbd.removeStyleName(dwoplayercss.desktop_active());
		kbd.removeStyleName(dwoplayercss.tablet_active());
		kbd.addStyleName(dwoplayercss.tablet());
		fp.setWidgetBottomHeight(kbd, sb.getStatusBarHeight()+10, Unit.PX, 17, Unit.PX);
		fp.setWidgetLeftWidth(kbd, 10, Unit.PX, 46, Unit.PX);
		break;
	case TABLET:
		kbd.removeStyleName(dwoplayercss.desktop_active());
		kbd.removeStyleName(dwoplayercss.tablet_active());
		kbd.addStyleName(dwoplayercss.tablet());
		fp.setWidgetBottomHeight(kbd, sb.getStatusBarHeight()+10, Unit.PX, 17, Unit.PX);
		fp.setWidgetLeftWidth(kbd, 10, Unit.PX, 46, Unit.PX);
		break;
	case TABLET_ACTIVE:
	case TABLET_ACTIVE_SOFT:
		kbd.removeStyleName(dwoplayercss.tablet());
		kbd.removeStyleName(dwoplayercss.desktop_active());
		kbd.addStyleName(dwoplayercss.tablet_active());
		fp.setWidgetBottomHeight(kbd, sb.getStatusBarHeight()+58, Unit.PX, 59, Unit.PX);
		fp.setWidgetLeftWidth(kbd, 10, Unit.PX, 54, Unit.PX);
		break;
	default:
	}
}

@Override
public Combined getCombined() {
	return state;
}

@Override
public int getWidth() {
	if(soortKeyboard != 0) return 0;
	return 70;
}

@Override
public boolean bolletjesZichtbaar() {
	return super.bolletjesZichtbaar() || on.isReview();
}

@Override
public void setLocation(String location) {
	if (on == null) this.location = location;
	else on.setLocation(location);
}

public void setCurrentOpdracht(int opdracht, String hash) {
	if (presenter != null) {
		presenter.gotoPage(String.valueOf(opdracht), hash);
	}
	
}

}
