package nl.uu.fi.dwo.mobile.client.ui;

import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleClientBundle;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.views.MyPopup;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckButton;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.DockPanel.DockLayoutConstant;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.theme.base.ButtonCss;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
//import com.googlecode.mgwt.ui.client.widget.ScrollPanel;







import fi.wiskopdr.text.Text;

public class ScoreNavPanel extends Composite implements ScoreNavIF, CBookEventListener {
	
	class CheckHandler implements TapHandler {

		@Override
		public void onTap(TapEvent event) {
			if(checker != null)
				checker.checkOpdracht(ScoreNavPanel.this);
		}
	}

	static Logger logger = Logger.getLogger("ScoreNavPanel");
	
	
	ScoreNavIF.GotoOpdracht listener;
	ScoreNavIF.Checker checker;
	
	
	class TouchHandler implements TapHandler {

		int opdracht;

		public TouchHandler(int opdracht) {
			super();
			this.opdracht = opdracht;
		}
		@Override
		public void onTap(TapEvent event) {
			if( listener != null)
			{	listener.gotoOpdracht(this.opdracht, ScoreNavPanel.this);
			}
			if(popup != null) popup.hide();
		}
		
	}

	class ReloadHandler implements TapHandler {

		int opdracht;

		public ReloadHandler(int opdracht) {
			super();
			this.opdracht = opdracht;
		}
		@Override
		public void onTap(TapEvent event) {
			if( listener != null)
			{	listener.reloadOpdracht(this.opdracht, ScoreNavPanel.this);
			}
			if(popup != null) popup.hide();
		}
		
	}

	
	VerticalPanel top;
	Label beantwoord;
	Label totaalscore;
	SimpleProgressBar totaalscoreBar, beantwoordBar;
	Grid  vragen;
	int rows = 10;
	public ScoreNavPanel() {
		top = new VerticalPanel();
		initialize();
		dock = new DockLayoutPanel(Unit.PX);
		dock.setWidth("426px");
		dock.addNorth(top, 140);
		dock.add(sp);
		initWidget(dock);
		popup = new MyPopup(this);
	}
	
	private void initialize() {
		Label text;
		setFontFamily(top);
		
		text = new Label("Score"); text.addStyleDependentName("bold");
		text.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		top.add(setFontFamily(text));
		top.add(new InlineHTML("<hr>"));
		Grid grid = new Grid(2,4);
		grid.getColumnFormatter().setWidth(0, "80px");
		grid.getColumnFormatter().setWidth(1, "160px");
		grid.getColumnFormatter().setWidth(2, "70px");
		grid.setCellPadding(10);
		grid.setCellSpacing(10);
		reloadTotal = new Button(DWOplayer.DWO_BUNDLE.imgbutton());
		reloadTotal.addTapHandler(new ReloadHandler(-1));
		reloadTotal.getElement().getStyle().setBackgroundImage("url('" + DWOplayer.DWO_BUNDLE.reload().getSafeUri().asString() + "')");
		grid.setWidget(0, 3, reloadTotal);
		checkBtn = new Button( DWOplayer.DWO_BUNDLE.txtbutton(),Text.constants.nakijkKnopLabel());
		checkBtn.addTapHandler(new CheckHandler());
		grid.setWidget(1, 3, checkBtn);
		checkBtn.setVisible(false);
		top.add(grid);

		reloadTotal.setVisible(false);
		text = new Label("Beantwoord");
		grid.setWidget(0, 0, setFontFamily(text));
		text = new Label("Totaalscore");
		grid.setWidget(1, 0, setFontFamily(text));
		// dummy
		grid.setWidget(0, 1, beantwoordBar = new SimpleProgressBar(30));
		grid.setWidget(1, 1, totaalscoreBar = new SimpleProgressBar(84));

		text = beantwoord = new Label("10 / 10");
		setFontFamily(text).getElement().getStyle().setFontWeight(FontWeight.BOLD);
		text = totaalscore = new Label("0 %");
		setFontFamily(text).getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		grid.setWidget(0, 2, setFontFamily(beantwoord));
		grid.setWidget(1, 2, setFontFamily(totaalscore));
		
		text = new Label("Voortgang"); text.addStyleDependentName("bold");
		setFontFamily(text).getElement().getStyle().setFontWeight(FontWeight.BOLD);
		top.add(text);
		top.add(new InlineHTML("<hr>"));
		vragen = new Grid(rows,4);
		vragen.setCellPadding(10);
		vragen.setCellSpacing(10);

		int[] max = new int[rows];
		for (int i = 0; i < max.length; i++) {
			max[i] = 10;
		}
		createVragen(max);
		sp = new ScrollPanel();
		sp.add(vragen);
		top.add(sp);
	}

	public Widget setFontFamily(Widget widget) {
		widget.getElement().getStyle().setFontSize(16, Unit.PX);
		widget.getElement().getStyle().setProperty("fontFamily", "Arial");
		return widget;
	}

	private Label[] vraagLabels;
	private SimpleProgressBar[] vraagBars;
	private Label[] vraagPunten;
	private int[] scoreMax;
	private int currentOpdracht;
	private SlidingPopup popup;
	ScrollPanel  sp;
	public SlidingPopup getPopup() {
		return popup;
	}

	private boolean opnieuw;
	private boolean itemOpnieuw;
	private Button reloadTotal;
	private Button checkBtn;

	private void createVragen(int[] scoreMax) {
		vragen.clear(true);
		vragen.resize(rows, 4 + (itemOpnieuw?1:0));
		vragen.getColumnFormatter().setWidth(0, "80px");
		vragen.getColumnFormatter().setWidth(1, "160px");
		vragen.getColumnFormatter().setWidth(2, "80px");
		vragen.getColumnFormatter().setWidth(3, "8px");
		if(itemOpnieuw) vragen.getColumnFormatter().setWidth(4, "70px");
		Label text;
		int totaal = 0;
		vraagLabels = new Label[rows];
		vraagBars = new SimpleProgressBar[rows];
		vraagPunten = new Label[rows];
		this.scoreMax = scoreMax;
		for(int i = 0; i < rows; i++) {
			text = new Label("Vraag " + (i+1)); vragen.setWidget(i, 0, text);
			vraagLabels[i] = text;
			if(i == currentOpdracht) text.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			setFontFamily(text);
			Widget widget = vraagBars[i] = new SimpleProgressBar(0); vragen.setWidget(i, 1, widget); // dummy
			widget.setVisible(scoreMax[i] != 0);
			text = vraagPunten[i] = new Label(""); vragen.setWidget(i,2, text); // dummy
			setFontFamily(text);
			text.getElement().getStyle().setFontWeight(FontWeight.BOLD);
// Wat voor button moet hier komen?
			ButtonCss css = DWOplayer.DWO_BUNDLE.imgbutton();
			Button p = new Button(css);
			p.getElement().getStyle().setBackgroundImage("url('" + DWOplayer.DWO_BUNDLE.arrowRightBig().getSafeUri().asString() + "')");
			
			vragen.setWidget(i, 3, p);
			//p.setStylePrimaryName("vraagButton");
			p.addTapHandler(new TouchHandler(i));
			
			Button reload = new Button(css);
			reload.getElement().getStyle().setBackgroundImage("url('" + DWOplayer.DWO_BUNDLE.reload().getSafeUri().asString() + "')");
			if(itemOpnieuw) 
				vragen.setWidget(i, 4, reload);
			reload.addTapHandler(new ReloadHandler(i));
		};
		setTotaalScore(totaal);
		
	}

	public void setTotaalScore(int totaal) {
		totaalscore.setText(totaal + " %");
		totaalscoreBar.setProgress(totaal);
	}
	
	public void setAantalOpdrachten(int aantal, int[] max, int current) {
		rows = aantal;
		currentOpdracht = current;
		createVragen(max);
	}
	
	public void setItemScore(int item, int score) {
		if(scoreMax[item] <= 0) return;
		//logger.info("score[" + item + "]=" + score + "/" + scoreMax[item]);
		int percent = 100 * score / scoreMax[item];
		if(score < 0 ) {
			score = 0;
			percent = 0;
		}
		else if(percent > 100) percent = 100;
		vraagBars[item].setProgress(percent);
		vraagPunten[item].setText(score + " punt" + (score != 1?"en":""));
	}
	
	public void setItemEnabled(int item, boolean enable) {
		vragen.getWidget(item, 3).setVisible(enable);
	}
	
	public void setItemScores(int[] scores) {
		for (int i = 0; i < rows; i++) {
			setItemScore(i, scores[i]);
		}
	}
	
	public void setBeantwoord(int aantal) {
		if(rows > 0)
		{
			beantwoord.setText( aantal + " / " + rows);
			beantwoordBar.setProgress(aantal * 100 / rows);
		}
	}
	
	public void setGotoOpdracht(ScoreNavIF.GotoOpdracht listener)
	{
		this.listener = listener;
	}

	public void setOpdracht(int currentOpdracht) {
		vraagLabels[this.currentOpdracht].getElement().getStyle().setFontWeight(FontWeight.NORMAL);
		this.currentOpdracht = currentOpdracht;
		vraagLabels[currentOpdracht].getElement().getStyle().setFontWeight(FontWeight.BOLD);	
	}

	public void setOpnieuw(boolean opnieuw) {
		this.opnieuw = opnieuw;
		reloadTotal.setVisible(opnieuw);
	}

	public void setItemOpnieuw(boolean itemOpnieuw) {
		this.itemOpnieuw = itemOpnieuw;
	}
	
	public void setKijkNa(ScoreNavIF.Checker checker) {
		this.checker = checker;
		setKijkNaEnabled(checker != null);
	}

	public void setKijkNaEnabled(boolean enable) {
		checkBtn.setVisible(enable);
	}

	private NextPrevHandler nextprev;
	
	@Override
	public void setNextPrevHandler(NextPrevHandler nextprev) {
		this.nextprev = nextprev;
	}

	HeaderButton next, prev;
	private boolean nextEnabled = true;
	private boolean prevEnabled = true;


	private DockLayoutPanel dock;

	@Override
	public Widget getNextButton() {
		if(next == null) {
			next = new HeaderButton(DWOplayer.PARAMETERS.headercss()); next.setText("Volgende >");
			next.addTapHandler(new TapHandler() {
				
				@Override
				public void onTap(TapEvent event) {
					if(nextEnabled)
						nextprev.gotoNext(ScoreNavPanel.this);
				}
			});
		}
		return next;
	}

	@Override
	public Widget getPrevButton() {
		if(prev == null) {
			prev = new HeaderButton(DWOplayer.PARAMETERS.headercss()); prev.setText("< Vorige");
			prev.addTapHandler(new TapHandler() {
				
				@Override
				public void onTap(TapEvent event) {
					if(prevEnabled)
						nextprev.gotoPrev(ScoreNavPanel.this);
				}
			});
		}
		return prev;
	}
	
	@Override
	public Widget getEndButton() {
		return null;
	}

	@Override
	public void setVolgendeEnabled(boolean enable) {
		nextEnabled = enable;	
	}

	@Override
	public void setVorigeEnabled(boolean enable) {
		prevEnabled = enable;
	}

	@Override
	public PushButton getKijkNaButton() {
		return new PushButton();
	}

	@Override
	public void setVolgendeVisible(boolean visible) {
	}

	@Override
	public void setVorigeVisible(boolean visible) {
	}

	@Override
	public void setButtonEnabled(int opdrNr, boolean b) {
		vragen.getWidget(opdrNr, 3).setVisible(b);
	}

	@Override
	public void setStatusBar(StatusBarIF bar) {
	}

	@Override
	public void showScore() {
	}

	@Override
	public void setScoresObjectivesKnop(boolean b) {
	}
	
	@Override
	public void setViewMisconceptionsKnop(boolean b) {
	}

	@Override
	public void setObjectivesHandler(ObjectivesHandler objectivesHandler) {
	}

	@Override
	public void setMisconceptionsHandler(MisconceptionsHandler misconceptionsHandler) {
	}
	
	@Override
	public Label getTotaalScoreLabel()
	{
		return null;
	}

	@Override
	public Label getKeerNagekekenLabel()
	{
		return null;
	}

	@Override
	public void setTotaalScoreLabel(int score)
	{
	}

	@Override
	public void setKeerNagekekenLabel(int aantal)
	{
	}

	@Override
	public void setAuthELOcheck(boolean b) {
	}

	@Override
	public void setAuthELOhelp(boolean b) {
	}

	HandlerRegistration registration;
	
	@Override
	public void started() {
		registration = DWOplayer.clientfactory.getEventBus().addHandler(CBookEvent.TYPE, this);
	}

	@Override
	public void stopped() {
		if(registration != null) {
			registration.removeHandler();
			registration = null;
		}
	}

	public void refresh() {
		//sp.setPixelSize(-1, 200);
		//if(sp != null) sp.refresh(); // alleen bij mgwt scrollpanel
		
	}

	@Override
	public void acceptCBookEvent(CBookEvent event) {
		if(checker != null && CheckButton.CHECK.equals(event.getCommand()))
			checker.checkOpdracht(ScoreNavPanel.this);
	}

}


