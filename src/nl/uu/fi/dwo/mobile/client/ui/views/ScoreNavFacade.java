package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.CheckButton;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import fi.wiskopdr.text.Text;

public class ScoreNavFacade implements ScoreNavIF, CBookEventListener {

	private static final String MYPUSHBUTTON = DWOplayer.DWO_BUNDLE.dwoplayercss().myPushButton();
	private static final String MYPUSHBUTTON_DISABLED = DWOplayer.DWO_BUNDLE.dwoplayercss().myPushButton_disabled();
	private static final String SCORE = DWOplayer.DWO_BUNDLE.dwoplayercss().score();
	StatusBarIF sb;
	public void setStatusBar(StatusBarIF bar) {
		sb = bar;
	}
	
	private final class ReloadAllHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			event.stopPropagation();
			reloadOpdracht(-1);
		}
	}
	private final class ReloadHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			event.stopPropagation();
			reloadOpdracht(currentOpdracht);
		}
	}


	private PushButton nakijkKnop;
	private Checker checker;
	private PushButton volgendeKnop, vorigeKnop, eindeKnop;
	boolean vorigeKnopZichtbaar, volgendeKnopZichtbaar;
	private PushButton scoresObjectivesKnop;
	private PushButton viewMisconceptionsKnop;
	private NextPrevHandler nextprev;
	private ObjectivesHandler objectivesHandler;
	private MisconceptionsHandler misconceptionsHandler;
	private PushButton allesOpnieuwKnop, opnieuwKnop;
	/**
	 * Label met de totaalscore, bijv. "Totaal: 35".
	 */
	private Label totaalScoreLabel;
	/**
	 * Label met het aantal keer nagekeken, bijv. "2 keer nagekeken".
	 */
	private Label keerNagekekenLabel;
	protected GotoOpdracht gotoOpdracht;
	protected int currentOpdracht;

	public ScoreNavFacade(ActivityComponent a) {
		nakijkKnop = new PushButton(Text.constants.nakijkKnopLabel());
		nakijkKnop.setStylePrimaryName(MYPUSHBUTTON);
		nakijkKnop.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{	e.stopPropagation();
				checker.checkOpdracht(ScoreNavFacade.this);
			}
		});
		vorigeKnop = new PushButton("<");//(new Image(DWOplayer.DWO_BUNDLE.vorigeknop().getSafeUri()));
				//Text.constants.vorigeKnopLabel());
		vorigeKnop.setStylePrimaryName(MYPUSHBUTTON);
		//vorigeKnop.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		vorigeKnop.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				e.stopPropagation();
				nextprev.gotoPrev(ScoreNavFacade.this);
			}
		});
		
		volgendeKnop = new PushButton(">");//(new Image(DWOplayer.DWO_BUNDLE.volgendeknop().getSafeUri()));//Text.constants.volgendeKnopLabel());
		volgendeKnop.setStylePrimaryName(MYPUSHBUTTON);
		//volgendeKnop.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		volgendeKnop.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				e.stopPropagation();
				nextprev.gotoNext(ScoreNavFacade.this);
			}
		});
		
		eindeKnop = new PushButton(Text.constants.eindeKnopLabel());
		eindeKnop.setStylePrimaryName(MYPUSHBUTTON);
		eindeKnop.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				//TODO: invullen. Kan pas als goto:0 goed geimplementeerd?
			}
		});
		//Tijdelijk:
		eindeKnop.setVisible(false);
	
		// De labels voor totaalscore en aantal keer nagekeken
		totaalScoreLabel = new Label(); // zonder "Totaal:"
		totaalScoreLabel.setStyleName(SCORE);
		//totaalScoreLabel.getElement().getStyle().setPaddingTop(6, Style.Unit.PX);
		keerNagekekenLabel = new HTML(new SafeHtmlBuilder().appendEscapedLines(0 + Text.constants.nakijkLabel() + "\n" + Text.constants.nakijkLabel2()).toSafeHtml());
	}
	
	@Override
	public void setBeantwoord(int aantalBeantwoord) {
	}

	@Override
	public void setOpdracht(int currentOpdracht) {
		this.currentOpdracht = currentOpdracht;
	}

	@Override
	public void setTotaalScore(int score) {
	}

	@Override
	public void setItemScore(int oldOpdr, int i) {
	}

	@Override
	public void setKijkNaEnabled(boolean enable)
	{
		nakijkKnop.setEnabled(enable);	
		nakijkKnop.setStyleName(MYPUSHBUTTON_DISABLED, !enable);
	}

	@Override
	public void setAantalOpdrachten(int aantalOpdrachten, int[] maxScores, int current) {
	}

	@Override
	public void setItemScores(int[] itemScores) {
	}

	@Override
	public void setGotoOpdracht(GotoOpdracht gotoOpdracht) {
		this.gotoOpdracht = gotoOpdracht;
	}

	@Override
	public void setItemOpnieuw(boolean b) {
		if(b) {
			if(opnieuwKnop ==  null) {
				opnieuwKnop = new PushButton(Text.constants.opnieuwKnopLabel());
				opnieuwKnop.setStylePrimaryName(MYPUSHBUTTON);
				opnieuwKnop.addClickHandler(new ReloadHandler());
			}
			if(opnieuwKnop.getParent() == null) {
				sb.addKnop(opnieuwKnop, false); // (re)attach
			}
			opnieuwKnop.setVisible(true);
		} else if(opnieuwKnop != null) {
			opnieuwKnop.setVisible(false);
		}
	}

	@Override
	public void setOpnieuw(boolean b) {
		if(b) {
			if(allesOpnieuwKnop ==  null) {
				allesOpnieuwKnop = new PushButton(Text.constants.allesOpnieuwKnopLabel());
				allesOpnieuwKnop.setStylePrimaryName(MYPUSHBUTTON);
				allesOpnieuwKnop.addClickHandler(new ReloadAllHandler());
			} 
			if(allesOpnieuwKnop.getParent() == null) {
				sb.addKnop(allesOpnieuwKnop, false);
			}
			allesOpnieuwKnop.setVisible(true);
		} else if(allesOpnieuwKnop != null) {
			allesOpnieuwKnop.setVisible(false);
		}
	}
	
	public void setScoresObjectivesKnop(boolean b)
	{
		if(b)
		{
			if(scoresObjectivesKnop == null)
			{
				scoresObjectivesKnop = new PushButton(Text.constants.objectivesKnopLabel());
				scoresObjectivesKnop.setStylePrimaryName(MYPUSHBUTTON);
				scoresObjectivesKnop.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent e)
					{
						objectivesHandler.openObjectivesPanel(ScoreNavFacade.this);
					}
				});
			}
			if(scoresObjectivesKnop.getParent() == null)
				sb.addKnop(scoresObjectivesKnop, false);
			scoresObjectivesKnop.setVisible(true);
		}
		else if(scoresObjectivesKnop != null)
			scoresObjectivesKnop.setVisible(false);
	}

	public void setViewMisconceptionsKnop(boolean b)
	{
		if(b)
		{
			if(viewMisconceptionsKnop == null)
			{
				viewMisconceptionsKnop = new PushButton(Text.constants.viewMisconceptionsKnopLabel());
				viewMisconceptionsKnop.setStylePrimaryName(MYPUSHBUTTON);
				viewMisconceptionsKnop.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent e)
					{
						misconceptionsHandler.openMisconceptionsPanel(ScoreNavFacade.this);
					}
				});
			}
			if(viewMisconceptionsKnop.getParent() == null)
				sb.addKnop(viewMisconceptionsKnop, false);
			viewMisconceptionsKnop.setVisible(true);
		}
		else if(viewMisconceptionsKnop != null)
			viewMisconceptionsKnop.setVisible(false);
		
	}


	@Override
	public void setKijkNa(Checker checker) {
		this.checker = checker;			
	}

	@Override
	public void setNextPrevHandler(NextPrevHandler nextprev) {
		this.nextprev = nextprev;
	}
	
	@Override
	public void setObjectivesHandler(ObjectivesHandler objectivesHandler) {
		this.objectivesHandler = objectivesHandler;
	}
	
	@Override
	public void setMisconceptionsHandler(MisconceptionsHandler misconceptionsHandler) {
		this.misconceptionsHandler = misconceptionsHandler;
	}

	@Override
	public Widget getNextButton() {
		return volgendeKnop;
	}

	@Override
	public Widget getPrevButton() {
		return vorigeKnop;
	}

	@Override
	public Widget getEndButton() {
		return eindeKnop;
	}
	
	@Override
	public void setVolgendeEnabled(boolean enable) 
	{
		// deze remove zorgt ervoor dat de knoppen op een extra grijze balk onder de navigatiebalk komen...
//		vorigeKnop.removeFromParent();
//		volgendeKnop.removeFromParent();
//		eindeKnop.removeFromParent();
//		if(enable)
//			sb.addKnop(volgendeKnop, true);
//		else
//		{	sb.addKnop(eindeKnop, true);
//		//tijdelijk:
//		eindeKnop.setVisible(false);
//		}
//		sb.addKnop(vorigeKnop, true);
		
		volgendeKnop.setEnabled(enable);	
	}

	@Override
	public void setVorigeEnabled(boolean enable)
	{
		vorigeKnop.setEnabled(enable);
	}


	@Override
	public PushButton getKijkNaButton() {
		return nakijkKnop;
	}


	@Override
	public void setVolgendeVisible(boolean volgendeKnopZichtbaar) {
		this.volgendeKnopZichtbaar = volgendeKnopZichtbaar;
		volgendeKnop.setVisible(volgendeKnopZichtbaar);
		volgendeKnop.removeFromParent();
		if(volgendeKnopZichtbaar)
		{	
			sb.addKnop(volgendeKnop, true);
		}
	}


	@Override
	public void setVorigeVisible(boolean vorigeKnopZichtbaar) {
		this.vorigeKnopZichtbaar = vorigeKnopZichtbaar;
		vorigeKnop.setVisible(vorigeKnopZichtbaar);
		vorigeKnop.removeFromParent();
		if(vorigeKnopZichtbaar)
			sb.addKnop(vorigeKnop, true);
	}


	@Override
	public void setButtonEnabled(int opdrNr, boolean b) {
		if(gotoOpdracht != null)
			gotoOpdracht.setButtonEnabled(opdrNr, b);
	}


	private void reloadOpdracht(int opdracht) {
		if(opdracht < 0)
		{
			final int opdr = opdracht;
			final MessageDialog box = new MessageDialog();
			Label titel = new Label(Text.constants.opnieuwPanelTitel());
			titel.getElement().getStyle().setFontSize(16, Style.Unit.PX);
			titel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			titel.getElement().getStyle().setPaddingBottom(10, Style.Unit.PX);
			box.addLine(titel);
			Label meldingTekst1 = new Label(Text.constants.opnieuwPanelTekst1());
			meldingTekst1.getElement().getStyle().setFontSize(14, Style.Unit.PX);
			meldingTekst1.getElement().getStyle().setPaddingBottom(10, Style.Unit.PX);
			Label meldingTekst2 = new Label(Text.constants.opnieuwPanelTekst2());
			meldingTekst2.getElement().getStyle().setFontSize(14, Style.Unit.PX);
			meldingTekst2.getElement().getStyle().setPaddingBottom(10, Style.Unit.PX);
			
			box.addLine(meldingTekst1);
			box.addLine(meldingTekst2);
			box.addYes();
//			Button jaKnop = new Button(Text.constants.jaTekst());
//			jaKnop.getElement().getStyle().setPaddingLeft(20, Style.Unit.PX);
//		    jaKnop.addClickHandler(new ClickHandler() {
//		        public void onClick(ClickEvent event) {
//		        	box.hide();
//		        	gotoOpdracht.reloadOpdracht(opdr, ScoreNavFacade.this);
//		        	
//		        	
//		        }
//		    });
			box.addNo();
//			Button neeKnop = new Button(Text.constants.neeTekst());
//		    neeKnop.getElement().getStyle().setFloat(Float.RIGHT);
//		    neeKnop.getElement().getStyle().setPaddingRight(20, Style.Unit.PX);
//		    neeKnop.addClickHandler(new ClickHandler() { 
//		    	public void onClick(ClickEvent event) {
//		    		box.hide();
//		    	}
//		    });
//		    contents.add(jaKnop);
//		    contents.add(neeKnop);
//		    box.setWidget(contents);
//		    box.show();
			box.showDialog().then(new Success<Integer, Void>() {

				@Override
				public Promise<Void> call(Promise<Integer> resolved) throws Exception {
					if(resolved.getValue().intValue() == MessageDialog.YES)
						gotoOpdracht.reloadOpdracht(opdr, ScoreNavFacade.this);
					return null;
				}});
		}
		else
			gotoOpdracht.reloadOpdracht(opdracht, ScoreNavFacade.this);
	}

	@Override
	public PopupPanel getPopup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showScore() {
		sb.showScore(this);
	}

	@Override
	public Label getTotaalScoreLabel()
	{
		return totaalScoreLabel;
	}

	@Override
	public Label getKeerNagekekenLabel()
	{
		return keerNagekekenLabel;
	}
	
	@Override
	public void setTotaalScoreLabel(int score)
	{
		totaalScoreLabel.setText(score + "%");
	}

	@Override
	public void setKeerNagekekenLabel(int aantal)
	{
		((HTML) keerNagekekenLabel).setHTML(new SafeHtmlBuilder().appendEscapedLines(aantal + Text.constants.nakijkLabel() + "\n" + Text.constants.nakijkLabel2()).toSafeHtml());
	}

	@Override
	public void setAuthELOcheck(boolean b) {
	}

	@Override
	public void setAuthELOhelp(boolean b) {
	}

	HandlerRegistration registration;
	
	@Override
	public void started(EventBus bus) {
		registration = bus.addHandler(CBookEvent.TYPE, this);
	}

	@Override
	public void stopped() {
		if(registration != null) {
			registration.removeHandler();
			registration = null;
		}
	}

	@Override
	public void acceptCBookEvent(CBookEvent event) {
		if(checker != null && CheckButton.CHECK.equals(event.getCommand()))
			checker.checkOpdracht(this);
	}

	@Override
	public Widget getOpnieuwButton()
	{
		return opnieuwKnop;
	}

	@Override
	public Widget getAllesOpnieuwButton()
	{
		return allesOpnieuwKnop;
	}

}