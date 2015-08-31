package nl.uu.fi.dwo.mobile.client.ui.views;

import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF;
import nl.uu.fi.dwo.mobile.client.ui.SlidingPopup;
import nl.uu.fi.dwo.mobile.client.ui.StatusBarIF;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.text.Text;

public class ScoreNavFacade implements ScoreNavIF {

	private StatusBarIF sb;
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
	private NextPrevHandler nextprev;
	private ObjectivesHandler objectivesHandler;
	private PushButton allesOpnieuwKnop, opnieuwKnop;
	private GotoOpdracht gotoOpdracht;
	private int currentOpdracht;

	public ScoreNavFacade() {
		nakijkKnop = new PushButton(Text.constants.nakijkKnopLabel());
		nakijkKnop.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{	e.stopPropagation();
				checker.checkOpdracht(ScoreNavFacade.this);
			}
		});
		vorigeKnop = new PushButton(new Image(DWOplayer.DWO_BUNDLE.vorigeknop().getSafeUri()));
				//Text.constants.vorigeKnopLabel());
		
		vorigeKnop.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		vorigeKnop.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				e.stopPropagation();
				nextprev.gotoPrev(ScoreNavFacade.this);
			}
		});
		
		volgendeKnop = new PushButton(new Image(DWOplayer.DWO_BUNDLE.volgendeknop().getSafeUri()));//Text.constants.volgendeKnopLabel());
		volgendeKnop.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		volgendeKnop.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				e.stopPropagation();
				nextprev.gotoNext(ScoreNavFacade.this);
			}
		});
		
		eindeKnop = new PushButton("Einde");
		eindeKnop.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				//TODO: invullen. Kan pas als goto:0 goed geimplementeerd?
			}
		});
	
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
	public void setKijkNaEnabled(boolean enable) {
		nakijkKnop.setEnabled(enable);		
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
				scoresObjectivesKnop = new PushButton("Deelscores");
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
	public void setVolgendeEnabled(boolean enable) {
		
		vorigeKnop.removeFromParent();
		volgendeKnop.removeFromParent();
		eindeKnop.removeFromParent();
		if(enable)
			sb.addKnop(volgendeKnop, true);
		else
			sb.addKnop(eindeKnop, true);
		sb.addKnop(vorigeKnop, true);
		volgendeKnop.setVisible(volgendeKnopZichtbaar);
		vorigeKnop.setVisible(vorigeKnopZichtbaar);
		//volgendeKnop.setEnabled(enable);	
	}

	@Override
	public void setVorigeEnabled(boolean enable) {
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
			final DialogBox box = new DialogBox();
			
			FlowPanel contents = new FlowPanel();
			Label titel = new Label(ViewModuleViewImpl.rb.getString("opnieuwPanelTitel"));
			titel.getElement().getStyle().setFontSize(16, Style.Unit.PX);
			titel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			titel.getElement().getStyle().setPaddingBottom(10, Style.Unit.PX);
			contents.add(titel);
			Label meldingTekst1 = new Label(ViewModuleViewImpl.rb.getString("opnieuwPanelTekst1"));
			meldingTekst1.getElement().getStyle().setFontSize(14, Style.Unit.PX);
			meldingTekst1.getElement().getStyle().setPaddingBottom(10, Style.Unit.PX);
			Label meldingTekst2 = new Label(ViewModuleViewImpl.rb.getString("opnieuwPanelTekst2"));
			meldingTekst2.getElement().getStyle().setFontSize(14, Style.Unit.PX);
			meldingTekst2.getElement().getStyle().setPaddingBottom(10, Style.Unit.PX);
			
			contents.add(meldingTekst1);
			contents.add(meldingTekst2);
			Button jaKnop = new Button(ViewModuleViewImpl.rb.getString("jaTekst"));
			jaKnop.getElement().getStyle().setPaddingLeft(20, Style.Unit.PX);
		    jaKnop.addClickHandler(new ClickHandler() {
		        public void onClick(ClickEvent event) {
		        	box.hide();
		        	gotoOpdracht.reloadOpdracht(opdr, ScoreNavFacade.this);
		        	
		        	
		        }
		    });
		    Button neeKnop = new Button(ViewModuleViewImpl.rb.getString("neeTekst"));
		    neeKnop.getElement().getStyle().setFloat(Float.RIGHT);
		    neeKnop.getElement().getStyle().setPaddingRight(20, Style.Unit.PX);
		    neeKnop.addClickHandler(new ClickHandler() { 
		    	public void onClick(ClickEvent event) {
		    		box.hide();
		    	}
		    });
		    contents.add(jaKnop);
		    contents.add(neeKnop);
		    box.setWidget(contents);
		    box.show();
		}
		else
			gotoOpdracht.reloadOpdracht(opdracht, ScoreNavFacade.this);
	}

	@Override
	public SlidingPopup getPopup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showScore() {
		sb.showScore(this);
	}
	
}