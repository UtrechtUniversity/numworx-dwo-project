package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.StateLess;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.ui.FormuleKeyboard;
import nl.uu.fi.dwo.mobile.client.ui.KeyBoardTabPanel;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavPanel;
import nl.uu.fi.dwo.mobile.client.ui.SlidingPopup;
import nl.uu.fi.dwo.mobile.client.ui.TouchButton;
import nl.uu.fi.dwo.mobile.client.ui.views.AnchorView.AnchorContext;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;
import nl.uu.fi.dwo.mobile.utils.StringCodeToHashMap;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;
import nl.uu.fi.dwo.mobile.utils.VariableCollection;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.dom.client.recognizer.pinch.PinchEvent;
import com.googlecode.mgwt.dom.client.recognizer.pinch.PinchHandler;
import com.googlecode.mgwt.dom.client.recognizer.swipe.SwipeEndEvent;
import com.googlecode.mgwt.dom.client.recognizer.swipe.SwipeEndHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort.DENSITY;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.touch.TouchDelegate;

import fi.wiskopdr.text.Text;


/**
 * 
 * @author Danny Hendrix, Evertson Croes, Sietske Tacoma, Wim van Velthoven
 * 
 */
public class ViewModuleViewImpl extends XMLView implements ViewModuleView, EntryPoint
{
	private static final String RANDOM_VAR_WAARDEN = "RandomVarWaarden";
	private static final String RANDOM_VAR_NAMEN = "RandomVarNamen";
	private static Logger logger = Logger.getLogger("ViewModuleViewImpl");
	private boolean standalone = false;

	private OpdrNav on;
	private FocusPanel mainPanel;
	FlowPanel contentPanel = null;
	private SimplePanel contentScrollPanel = null;
	private Panel tekst = null;
	private ArrayList<TouchButton> buttons = new ArrayList<TouchButton>();
	private double zoom = 1;
	private PushButton volgendeKnop, vorigeKnop ;//, eindeKnop;
	private PushButton nakijkKnop;
	
	//private boolean zelftoetsGeenCorr = false;
	
	private boolean zelftoetsNagekeken = false;
	
	
	private Panel kbp = null;
	private HeaderButton hb;
	private HeaderPanel hp;
	
	private HeaderButton next, prev;
	private boolean nextEnabled = true;
	private boolean prevEnabled = true;
	

	private Scorm2004IF api;

	public ViewModuleViewImpl(boolean b) {
		standalone = b;
		if(!b) setWindowTop(0);
	}
	
	public ViewModuleViewImpl() {
	}

	public HeaderButton getBackButton()
	{
		return hb;
	}

	@Override
	public void setupModule(String name, String file)
	{
		contentPanel.clear();
		if(DWOplayer.JSON) loadJSON(file); else loadXML(file);
		if(!DWOplayer.PARAMETERS.isNavTitle()) setTitle(name);
	}

	private void loadJSON(String file) {
		 {
			RequestBuilder.Method method = RequestBuilder.GET;
			String url = file;
			logger.info("request " + method + " " + url);
			logger.fine("requesting url = " + Window.Location.getHref());
			RequestBuilder rb = new RequestBuilder(method, url);
			try
			{
				rb.sendRequest(null, new RequestCallback()
				{
		
					@Override
					public void onResponseReceived(Request request, Response response)
					{
						String responseText = response.getText();
						logger.info("Status: " + response.getStatusCode() + " " + response.getStatusText());
						logger.info(response.getHeadersAsString());
						logger.info("Data: " + responseText.substring(0, Math.min(30, responseText.length()) ));
						if (!responseText.isEmpty())
						{
							setupView(responseText);
						} else {
							logger.severe("response empty");
						}
		
					}
		
					@Override
					public void onError(Request request, Throwable exception)
					{
						Window.alert("error");
					}
				});
		
			}
			catch (RequestException e)
			{
				RootPanel.get().add(new Label("cannot load xml: " + e.getMessage()));
			}
		}
	}

	public void preSetupModule(final String link, final String url)
	{
		{
			setupModule(url, url);
		}
	}

	public void clearContentPanel()
	{
		contentPanel.clear();
		PopupFacade.hide();	
		kb.blur();
	}

	public void setupView(HashMap<String, Object> launchData)
	{
		for (int i = 0; i < buttons.size(); i++)
			contentPanel.remove(buttons.get(i));

		super.setupView(launchData);
		ObjectMap wrap = JSONUtilities.wrapMap(instellingen);
// wanneer verschijnt de opnieuwknop?		
		boolean opnieuwMogelijk = "true".equals(launchData.get("opnieuwMogelijk"));
		boolean opnieuw = false;
		if(wrap != null && wrap.containsKey("opnieuw"))
		{	opnieuw = wrap.getBoolean("opnieuw");
		}
		scoreNav.setOpnieuw(opnieuw || opnieuwMogelijk);
		if(wrap != null && wrap.containsKey("itemOpnieuw"))
		{
			scoreNav.setItemOpnieuw(wrap.getBoolean("itemOpnieuw"));
		}

		contentPanel.getElement().getStyle().setFontSize(font_size, Unit.PX);
		contentPanel.getElement().getStyle().setPadding(0, Unit.PX); // XXX was 15 
		// GEEN randje aan de linkerkant, want dan klopt de maat (100%) niet meer bij noordhoff

		on =  new OpdrNav(launchData, this, new Memento(api));
		FlowPanel onp = (FlowPanel) on.getAsPanel();
		if(bolletjesZichtbaar)
			kb.addNavPanel(onp);
		scoreNav.setAantalOpdrachten(on.getAantalOpdrachten(), on.getMaxScores());
		scoreNav.setBeantwoord(on.getAantalBeantwoord());
		scoreNav.setItemScores(on.getItemScores());
		scoreNav.setTotaalScore((int)on.getScore());
		scoreNav.setGotoOpdracht(on);
		if(DWOplayer.PARAMETERS.isNavTitle())
			setTitle("Vraag " + (1+on.getCurrentOpdracht()) + " van " + on.getAantalOpdrachten());
		//call SetupDone Handler, if an object is provided
		if (this.loadingHandler != null){
			this.loadingHandler.viewModuleViewSetupDone();;
		}
		
//		bezocht = new boolean[on.getAantalActiviteiten()][on.getAantalOpdrachten()];
//		for(int j = 0; j < on.getAantalActiviteiten(); j++)
//		{	for(int i = 0; i < bezocht[j].length; i++)
//				bezocht[j][i] = false;
//		}	
//		bezocht[0][0] = true;
		
		//benodigde knoppen toevoegen.
		int mode = on.getMode();
		if(mode == OpdrNav.ZELFTOETS)
		{
			nakijkKnop = new PushButton(Text.constants.nakijkKnopLabel());
			nakijkKnop.setEnabled(on.getAantalOpdrachten() == 1);
			kb.addKnop(nakijkKnop, false);
			nakijkKnop.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent e)
				{	e.stopPropagation();
					on.saveCurrentState();
//					if (!lessonMode.equals("review"))
//						aantalNakijken[activiteitNr]++;
					zetToetsNagekeken();
					on.kijkToetsNa();
					
					
					
					//System.out.println("toets nagekeken");
				}
			});
		}
		
		//uitzoeken of de volgende en vorige knop erin moeten.
		
		vorigeKnop = new PushButton(Text.constants.vorigeKnopLabel());
		vorigeKnop.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				e.stopPropagation();
//				int cur = on.getCurrentOpdracht() - 1;
//				if(cur < 0) cur = 0 ;
//				on.gotoOpdracht(cur, scoreNav);
				gaNaarVorigeOpdracht();
			}
		});
		
		volgendeKnop = new PushButton(Text.constants.volgendeKnopLabel());
		volgendeKnop.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				e.stopPropagation();
				
				gaNaarVolgendeOpdracht();
			}
		});
		
		/*
		eindeKnop = new PushButton(Text.constants.eindeKnopLabel());
		eindeKnop.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				e.stopPropagation();
				//TODO: functie geven.
			}
		});
		*/
		
		if(volgendeKnopZichtbaar)
			kb.addKnop(volgendeKnop, true);
		if(vorigeKnopZichtbaar)
			kb.addKnop(vorigeKnop, true);
		stelNavigatieIn();
	}
	
	public void zetToetsNagekeken()
	{
		int mode = on.getMode();
		if (mode == 2 || mode == 3)
		{
			if (mode == 2 && zelftoetsGeenCorr)
			{
				// laatste kans op update sessiontime
//				if (!zelftoetsNagekeken)
//				{
//					opdrContainer.sessionStop();
//					times[activiteitNr][opdrachtNr] = opdrContainer.getSessionTime();
//				}
//				zetAfdekPanelLeeg(true);
			}
			zelftoetsNagekeken = true;
			nakijkKnop.setEnabled(!zelftoetsGeenCorr);
			//scoresObjectivesKnop.setEnabled(true);//goed? nodig?
			vorigeKnop.setVisible(vorigeKnopZichtbaar || !bolletjesZichtbaar && zelftoetsNagekeken);

//			totaal = Math.max(0, totaal - (Math.max(0, aantalNakijken[activiteitNr] - 1)) * nakijkStraf);
//			aantalNakijkLabel.setText("" + aantalNakijken[activiteitNr] + " keer nagekeken");
//			if (aantalNakijken[activiteitNr] > 0 && !zelftoetsGeenCorr)
//				aantalNakijkLabel.setVisible(true);
		}
//		activiteitScoreLabels[activiteitNr].setText(WiskOpdr.rb.getString("score") + totaal);
//		if (aantalActiviteiten == 1)
//		{	activiteitScoreLabels[activiteitNr].setText(WiskOpdr.rb.getString("totaal") + totaal);
//			if(voortgang)
//				activiteitScoreLabels[0].setText(WiskOpdr.rb.getString("voortgang") + bepaalVoortgangPercentage(activiteitNr, opdrachtNr) + "%");
//		}

		
//		if (mode == 0 || mode == OEFENEN_STRAFPUNTEN)
//		{
//			WiskOpdr.setLMSScore();
//			WiskOpdr.setLMSState();
//			setMWScoreLabel();
//		}
	}
	
	public boolean getZelftoetsNagekeken()
	{
		return zelftoetsNagekeken;
	}
	
	public void gaNaarVolgendeOpdracht()
	{
		if(condNav && condNavVoorwaarden)
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
			stelNavigatieIn();
			
			
		}
		else
		{	int cur = on.getCurrentOpdracht() + 1;
			if(cur >= on.getAantalOpdrachten()) 
				cur = on.getAantalOpdrachten()-1;
			on.gotoOpdracht(cur, scoreNav);
			stelNavigatieIn();
			
		}
	}
	
	public void gaNaarVorigeOpdracht()
	{
		if(condNav && condNavVoorwaarden)
		{
			int cur = Math.max(on.getCurrentOpdracht() - 1, 0);
			while(!bezocht[on.getCurrentActiviteit()][cur] && cur > 0)
				cur--;
			on.gotoOpdracht(cur, scoreNav);
			stelNavigatieIn();
		}
		else
		{
			int cur = on.getCurrentOpdracht() - 1;
			if(cur < 0) 
				cur = 0 ;
			on.gotoOpdracht(cur, scoreNav);
			stelNavigatieIn();
			
		}
	}

	public void zetOpdracht(HashMap<String, Object> opdracht)
	{
		if(bezocht == null)
		{
			bezocht = new boolean[on.getAantalActiviteiten()][on.getAantalOpdrachten()];
			for(int j = 0; j < on.getAantalActiviteiten(); j++)
			{	for(int i = 0; i < bezocht[j].length; i++)
					bezocht[j][i] = false;
			}	
			bezocht[0][0] = true;
		}
		
		
		String randVarString = "";
		randVarString = (String) opdracht.get("randVarString");
		if(randVarString == null) randVarString = "";
		VariableCollection vc = new VariableCollection();
		boolean wellSet = vc.setVariables(randVarString);

		String[] varnamen = null;
		HashMap waarden = null;
		//if(randomise)
		{
			try
			{
				varnamen = vc.getVariableNames();
				waarden = vc.getRandomValues();
			}
			catch (Exception ex)
			{
				wellSet = false;
			}
		}

		this.randomVarNamen = varnamen;
		this.randomVarWaarden = waarden;

		opdrachtObjects = new ArrayList<Object>();
		List<Object> opdrachtGegevens = JSONUtilities.toArrayList( opdracht.get("interactiePanelLaunchData") );
		TekstBuffer tb = new TekstBuffer(varnamen, waarden);
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
	}

	public void zetOpdrachtPlusState(HashMap<String, Object> opdracht, HashMap<String, Object> state)
	{
		
		System.out.println("zetOpdrachtPlusState");
		String randVarString;
		randVarString = (String) opdracht.get("randVarString");
		if(randVarString == null ) randVarString = "";
		VariableCollection vc = new VariableCollection();
		boolean wellSet = vc.setVariables(randVarString);

		String[] varnamen = null;
		HashMap waarden = null;
		//if(randomise)
		{
			try
			{
				varnamen = vc.getVariableNames();
				waarden = vc.getRandomValues();
			}
			catch (Exception ex)
			{
				wellSet = false;
			}
		}
		
		this.randomVarNamen = varnamen;
		this.randomVarWaarden = waarden;

		if (state.get(RANDOM_VAR_NAMEN) != null)
			this.randomVarNamen = JSONUtilities.toStringArray(state.get(RANDOM_VAR_NAMEN));
		if (state.get(RANDOM_VAR_WAARDEN) != null)
			this.randomVarWaarden = (HashMap<String, Object>) state.get(RANDOM_VAR_WAARDEN);

		opdrachtObjects = new ArrayList<Object>();
		List<Object> opdrachtGegevens = JSONUtilities.toArrayList( opdracht.get("interactiePanelLaunchData") );
		TekstBuffer tb = new TekstBuffer(randomVarNamen, randomVarWaarden);
		int[] breedtes = new int[] { 800 };
		tb.zetVolleBreedtes(breedtes);
		newVersion = !(Boolean) opdracht.get("hasAntwoordVak");
		//New editor version
		
		if (opdrachtGegevens != null || newVersion)
		{
			if ((Boolean) opdracht.get("hasTitle"))
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
		
	}
	
	public void stelNavigatieIn()
	{	//Omzetting in GWT: overal opdrachtenCorrect[actNr][opdrNr] vervangen door on.getOpdrachtCorrect(actNr, opdrNr)
		int opdrNr = on.getCurrentOpdracht();
		int actNr = on.getCurrentActiviteit();
		
		try{
			if(bezocht!=null && opdrNr > 0 && opdrNr < bezocht[actNr].length)
				bezocht[actNr][opdrNr] = true;
			}
			catch(Exception e){}
		
		//bolletje zelf moet altijd enabled zijn, als het al een keer is bezocht.
		try{	
			if(bezocht[actNr][opdrNr])
				on.setButtonEnabled(opdrNr, true);
				//or[actNr].setEnabled(true, opdrNr + 1);
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
				on.setButtonEnabled(i, false);
			
//			if(allesCorrectNodig && !on.getOpdrachtCorrect(actNr, opdrNr) && !on.geefNoScore(actNr, opdrNr + 1)) // klopt die + 1??
//				eindeKnop.setEnabled(false);
//			else
//				eindeKnop.setEnabled(true);
		}
		else
		{	//eindeKnop.setVisible(false);
			//eindeKnop.setEnabled(false);
			if(volgendeKnop != null)
				volgendeKnop.setVisible(volgendeKnopZichtbaar);
		
			//Als leerling pas door mag als alles op pagina correct: volgende bolletjes en volgende/einde-knop disablen.
			if(allesCorrectNodig && !on.getOpdrachtCorrect(actNr, opdrNr) && !on.geefNoScore(actNr, opdrNr + 1)) //klopt die + 1??
			{	for(int i = opdrNr + 1; i < on.getAantalOpdrachten(); i++)
					on.setButtonEnabled(i, false);
				//volgendeKnop.setEnabled(false);
				zetVolgendeKnoppenEnabled(false);
				//eindeKnop.setEnabled(false);
				zetVorigeKnoppenEnabled(opdrNr > 0);
				zetNakijkKnopEnabled();
				return;
			}
			
			if(condNav && condNavPerc)
			{	//boolean conditie = on.geefNoScore(actNr, opdrNr + 1) || //or[actNr].geefNoScore(opdrNr + 1) || 
				//		100.0 * (Math.max(0, on.getScore(actNr, opdrNr) - on.getStrafpunten(actNr, opdrNr))) / on.getMaxScore(actNr, opdrNr) >= condPerc;
				boolean conditie = on.geefNoScore(actNr, opdrNr + 1) || 100 * on.getScore(actNr, opdrNr) / on.getMaxScore(actNr, opdrNr) >= condPerc;
				for(int i = opdrNr + 2; i < on.getAantalOpdrachten(); i++)
				{	//or[actNr].setEnabled(bezocht[actNr][i], i + 1);
					on.setButtonEnabled(i, bezocht[actNr][i]);
				}
				//or[actNr].setEnabled(conditie, opdrNr + 2);
				on.setButtonEnabled(opdrNr + 1, conditie);
				//volgendeKnop.setEnabled(conditie);
				zetVolgendeKnoppenEnabled(conditie);
				//eindeKnop.setEnabled(conditie);
			}
			else
			//	volgendeKnop.setEnabled(true);
				
				zetVolgendeKnoppenEnabled(true);
			if(condNav && condNavVoorwaarden)
			{	for(int i = opdrNr + 1; i < on.getAantalOpdrachten(); i++)
				//	or[actNr].setEnabled(bezocht[actNr][i], i + 1);
					on.setButtonEnabled(i, bezocht[actNr][i]);
				if(bepaalVolgendeOpdracht(actNr, opdrNr) > -1)
				{	on.setButtonEnabled(bepaalVolgendeOpdracht(actNr, opdrNr), !allesCorrectNodig || on.geefNoScore(actNr, opdrNr + 1) || on.getOpdrachtCorrect(actNr, opdrNr));
					if(!allesCorrectNodig)
					{	int volgende = bepaalVolgendeOpdracht(actNr, opdrNr);
						while(bepaalVolgendeOpdracht(actNr, volgende) > -1)
						{	if(bepaalVolgendeOpdracht(actNr, volgende) > volgende + 1)
								for(int i = volgende + 1; i < bepaalVolgendeOpdracht(actNr, volgende); i++)
								//	or[actNr].setEnabled(false, i + 1);
									on.setButtonEnabled(i, false);
							//or[actNr].setEnabled(true, bepaalVolgendeOpdracht(actNr, volgende) + 1);
							on.setButtonEnabled(bepaalVolgendeOpdracht(actNr, volgende), true);
							volgende = bepaalVolgendeOpdracht(actNr, volgende);
						}
						if(volgende + 1 < on.getAantalOpdrachten())
						{	for(int i = volgende + 1; i < on.getAantalOpdrachten(); i++)
							//	or[actNr].setEnabled(false, i + 1);
							on.setButtonEnabled(i, false);
						}
								
					}
				
				}
			}
		
		}
		zetVorigeKnoppenEnabled(opdrNr > 0);
		zetNakijkKnopEnabled();
	}
	
	public void zetVolgendeKnoppenEnabled(boolean b)
	{	if(volgendeKnop != null)
			volgendeKnop.setEnabled(b);
		nextEnabled = b;
	}
	
	public void zetVorigeKnoppenEnabled(boolean b)
	{
		if(vorigeKnop != null)
			vorigeKnop.setEnabled(b);
		prevEnabled = b;
	}
	
	public void zetNakijkKnopEnabled()
	{
		if(nakijkKnop != null)
			nakijkKnop.setEnabled(!(zelftoetsNagekeken && zelftoetsGeenCorr) && suspendDataCompleted(on.getCurrentActiviteit(), on.getCurrentOpdracht()));
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
				{	scoreSelectie = scoreSelectie + on.getScore(actNr, scorePaginas[i]-1)-on.getStrafpunten(actNr, scorePaginas[i]-1);
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
		{	if(opdrNr < on.getAantalOpdrachten() - 1)
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
		FormuleEditorWithSteps fews = new FormuleEditorWithSteps(opdracht, false, tb.getVarNamen(), tb.getVarWaarden());

		//fews.getEditor().requestFocus();
		

		contentPanel.add(fews.getAsPanel());
	}

	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.on = (OpdrNav) comRoot;
	}

	public HashMap<String, Object> getState()
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
				aantalInteractionViews++;
			}
		}
		
		h.put("activiteitNr", new Integer(on.getCurrentActiviteit()));
		h.put("opdrachtNr", new Integer(on.getCurrentOpdracht()));
		
		h.put("interactiePanelStates", states);
		h.put(RANDOM_VAR_NAMEN, randomVarNamen);
		h.put(RANDOM_VAR_WAARDEN, randomVarWaarden);
		h.put("zelftoetsNagekeken", new Boolean(zelftoetsNagekeken));
		if (bezocht != null)
			h.put("bezocht", bezocht);

		
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
		for (int i = 0; states != null && i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionView)
			{
				HashMap<String, Object> state = stateNr < states.size() ? (HashMap<String, Object>) states.get(stateNr) : new HashMap();
				((InteractionView) currentObject).setState(state);
				stateNr++;
			}
		}
		
		ObjectMap map = JSONUtilities.wrapMap(h);
		int activiteitNr = 0;
		int opdrachtNr = 0;
		if (map.containsKey("activiteitNr"))
			activiteitNr = map.getInt("activiteitNr");
		if (map.containsKey("opdrachtNr"))
			opdrachtNr = map.getInt("opdrachtNr");

		
		if (map.containsKey("bezocht"))
			try{	
				ObjectList bezochtList = ( map.getObjectList("bezocht") );
				bezocht = new boolean[bezochtList.size()][];
				for(int i = 0; i < bezochtList.size(); i++)
				{	bezocht[i] = bezochtList.getBooleanArray(i);
				}
				
				
			}
			catch(Exception e)
			{
				bezocht = new boolean[on.getAantalActiviteiten()][on.getAantalOpdrachten()];
				bezocht[0] = map.getBooleanArray("bezocht");
				if(on.getAantalActiviteiten() > 1)
					for(int j = 1; j < on.getAantalActiviteiten(); j++)
					{	for(int i = 0; i < on.getAantalOpdrachten(); i++)
							bezocht[j][i] = false;
					}
			}
		if(bezocht == null)
		{	bezocht = new boolean[on.getAantalActiviteiten()][on.getAantalOpdrachten()];
			for(int j = 0; j < on.getAantalActiviteiten(); j++)
			{	for(int i = 0; i < on.getAantalOpdrachten(); i++)
					bezocht[j][i] = false;
			}
			bezocht[activiteitNr][opdrachtNr] = true;
			
		}
		
		
		if (h.containsKey("zelftoetsNagekeken"))
			zelftoetsNagekeken = ((Boolean) h.get("zelftoetsNagekeken")).booleanValue();
//		if (h != null && h.containsKey("zelftoetsGeenCorr"))
//			zelftoetsGeenCorr = ((Boolean) h.get("zelftoetsGeenCorr")).booleanValue();
		stelNavigatieIn();

	}
	
	public void kijkNa()
	{
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionView)
			{
				((InteractionView)currentObject).kijkNa();
			}
		}
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
	
	class MyAnchorContext implements AnchorContext {

		@Override
		public void gotoUrl(String href) {
			if(href.startsWith("goto:."))
			{
				int opdrnr = Integer.parseInt(href.substring(6)) -1 ; // 0based
				on.gotoOpdracht(opdrnr, scoreNav);
			} // iets met de place controller.....
			
		}
		
	}
	AnchorContext anchorContext = new MyAnchorContext();
	
	private boolean inNavBtn;
	
	public ViewModuleViewImpl initialize()
	{
		api = GWT.create(Scorm2004IF.class);
		FlowPanel fp = new FlowPanel(); 
		mainPanel = FocusOnTouch.wrap(fp, true);
		
		
		mainPanel.setHeight("100%");
		mainPanel.setWidth("100%");
		//fp.setHeight("428px");
		//fp.setWidth("886px");

		kb = new FormuleKeyboard();
		FocusOnTouch.installKeyboard(kb);
		FormuleHolder.installKeyboard(kb);
		
		hp = new HeaderPanel(DWOplayer.PARAMETERS.headercss());
		setTitle("");
		//Style style = hp.getElement().getStyle();		
		next = new HeaderButton(DWOplayer.PARAMETERS.headercss()); next.setText("Volgende >");
		next.addTapHandler(new TapHandler() {
			
			@Override
			public void onTap(TapEvent event) {
				if(nextEnabled)
					gotoNext(next);
			}
		});
		prev = new HeaderButton(DWOplayer.PARAMETERS.headercss()); prev.setText("< Vorige");

		prev.addTapHandler(new TapHandler() {
			
			@Override
			public void onTap(TapEvent event) {
				if(prevEnabled)
					gotoPrev(prev);
			}
		});
		
		HorizontalPanel hbox = new HorizontalPanel();
		hbox.add(prev); hbox.add(next);
		hp.setRightWidget(hbox);
		
		hb = new HeaderButton(DWOplayer.PARAMETERS.headercss());
		hb.getElement().getStyle().setBackgroundImage("url('" + DWOplayer.DWO_BUNDLE.menuIcon().getSafeUri().asString() + "')");

		hp.setLeftWidget(hb);

		if(standalone) fp.add(hp);

		contentScrollPanel = new SimplePanel();contentScrollPanel.addStyleName("contentScrollPanel");
		contentScrollPanel.setWidth("100%");
		contentScrollPanel.setHeight("100%");
		contentScrollPanel.getElement().getStyle().setOverflowY(Overflow.HIDDEN);
		contentScrollPanel.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
//
		contentPanel = new FlowPanel();contentPanel.setStylePrimaryName("contentPanel");
		contentPanel.getElement().getStyle().setProperty("display", "inline-block");
		//contentPanel.getElement().getStyle().setMarginBottom(360, Unit.PX);
		contentPanel.setWidth("100%"); // hoeveel is 100% - 30px ?
		contentPanel.setHeight("100%");
// FIXME Hier moeten we een gesture recognizer maken:

		if(TouchEvent.isSupported()) {
			TouchDelegate touchDelegate = new TouchDelegate(contentPanel);
			touchDelegate.addPinchHandler(new PinchHandler() {
				double zoom = 1.0;
				@Override
				public void onPinch(PinchEvent event) {
					double factor = event.getScaleFactor();
					int x = event.getX();
					int y = event.getY();
					zoom = zoom / factor;
					zoom = Math.max(1.0, zoom);
					zoom = Math.min(5.0, zoom);
					logger.info("x=" + x + ", y= " + y + ", scale=" + factor + ", z=" + zoom);
					contentPanel.getElement().getStyle().setProperty("zoom", String.valueOf(zoom));
				}
				
			});
			
			touchDelegate.addSwipeEndHandler(new SwipeEndHandler() {
				
				@Override
				public void onSwipeEnd(SwipeEndEvent event) {
					switch( event.getDirection()) {
					case LEFT_TO_RIGHT:  gotoPrev(prev); break;
					case RIGHT_TO_LEFT: gotoNext(next); break;
					}
					
				}
			});
		}
		//ipv addContentPanelTouchListener(contentPanel);

		contentScrollPanel.setWidget(contentPanel);
//		contentScrollPanel.setScrollingEnabledX(false); // XXX IF NOORDHOFF 
		//contentScrollPanel.setScrollingEnabledY(false);
		contentPanel.getElement().getStyle().setOverflowY(Overflow.AUTO);
		contentPanel.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
		//contentScrollPanel.getElement().getStyle().setPadding(10, Unit.PX); WIM: dit is niet goed!!!! niet repareren!

		fp.add(contentScrollPanel);

		Panel kbp = kb.getAsPanel();
		//kbp.setWidth("886px");
		fp.add(kbp);

// POPUP of floating in ????
		hb.addTapHandler(new TapHandler() {

			@Override
			public void onTap(TapEvent event) {
				if(POPUP.isShowing())
						POPUP.hide();
				else
						popupNavPanel();
				
			}});
		POPUP.addAutoHidePartner(hb.getElement());

		//initWidget(mainPanel);
		return this;

	}
	
	
	static class MyPopup extends SlidingPopup {
	    public MyPopup(ScoreNavPanel w) {
	        // PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
	        // If this is set, the panel closes itself automatically when the user
	        // clicks outside of it.
	        super(true);
	        setGlassEnabled(true);
	        setAnimationEnabled(true);
	        // PopupPanel is a SimplePanel, so you have to set it's widget property to
	        // whatever you want its contents to be.
	        setWidget(w);
	        w.popup = this;
	      }
	}

	public ScoreNavPanel scoreNav = new ScoreNavPanel();
    MyPopup POPUP = new MyPopup(scoreNav);
	
	protected void popupNavPanel() {
		 final MyPopup popup = POPUP;
	        popup.setPopupPositionAndShow(new SlidingPopup.PositionCallback() {
	          public void setPosition(int offsetWidth, int offsetHeight) {
	            int left = (Window.getClientWidth() - offsetWidth) / 3;
	            int top = (Window.getClientHeight() - offsetHeight) / 3;
	            left = 0;
	            top  = hp.getOffsetHeight();
	            popup.setPopupPosition(left, top);
	            popup.setPixelSize(offsetWidth, Window.getClientHeight()-top);
	          }
	        });
		
	}

	public void zetMaatNoordhoff()
	{
		//FlowPanel fp = new FlowPanel();
		//mainPanel = FocusOnTouch.wrap(fp);
		mainPanel.setHeight("426px");
		mainPanel.setWidth("886px");
		
		//fp.setHeight("428px");
		//fp.setWidth("886px");
		//if(!standalone) fp.add(hp);
		final int contentHeight = 426 - 40; // 40 = hoogte headerpanel.
		contentScrollPanel.setPixelSize(886, contentHeight ); 
		//contentScrollPanel.setHeight("100%");
		//fp.add(contentScrollPanel);
		contentPanel.getElement().getStyle().clearMarginBottom();
// probeersel!
//
		contentPanel.getElement().getStyle().setMarginBottom(360, Unit.PX);
//		Panel kbp = kb.getAsPanel();
//		kbp.setWidth("886px");
		kb.tp.zetMaatNoordhoff();
		kb.tp.setScrollPanel(contentScrollPanel, contentHeight);
		//fp.add(kbp);

	}
	
	private int extraHeight = 41 + KeyBoardTabPanel.KEYB_STATIC_HEIGHT;
	private String unitId = "scoViewNr";
	
	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public void setWindowTop(int top) {
		extraHeight = top + KeyBoardTabPanel.KEYB_STATIC_HEIGHT;
	}
	
	
	public void zetMaat() {

		// FIXME HACK voor DWOplayer zelf		
		hb = new HeaderButton(DWOplayer.PARAMETERS.headercss()); hb.setBackButton(true);hb.setText("Terug");
		hp.setLeftWidget(hb);
		hp.setRightWidget(null);
		
		///contentPanel.getElement().getStyle().setMarginBottom(360, Unit.PX);
		int contentHeight = Window.getClientHeight() - extraHeight;
		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				int h = event.getHeight() - extraHeight;
				logger.info("resize event " +  h);
				kb.tp.setScrollPanel(contentScrollPanel, h);
				
			}});
		kb.tp.zetMaat();
		kb.tp.setScrollPanel(contentScrollPanel, contentHeight);

	}
	
	public void zetMaatTrifork() {
		extraHeight = 0;
		///contentPanel.getElement().getStyle().setMarginBottom(360, Unit.PX);
		int contentHeight = Window.getClientHeight() - extraHeight;
		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				int h = event.getHeight() - extraHeight;
				logger.info("resize event " +  h);
				kb.tp.setScrollPanel(contentScrollPanel, h);
				
			}});
		kb.tp.zetMaatTrifork();
		kb.tp.setScrollPanel(contentScrollPanel, contentHeight);

	}

	@Override
	public void onModuleLoad()
	{
		standalone = true;
		initialize();
		String url = "index.xml";
		String link = "index.xmr"; // reference.
		String path = Window.Location.getPath();
		// strip basename
		int slash = path.lastIndexOf('/');
		//if (slash >= 0)
		//	path = path.substring(slash + 1);
		// strip extension
		int dot = path.lastIndexOf('.');
		if (dot > 0)
		{
			path = path.substring(0, dot);
		}
		if (!path.isEmpty())
		{
			url = path + ".xml";
			link = path + ".xmr";
		}
		ViewPort viewport = new MGWTSettings.ViewPort();
		viewport.setTargetDensity(DENSITY.MEDIUM);
		viewport.setUserScaleAble(true);
		//viewport.setMinimumScale(0.2).setInitialScale(1.0).setMaximumScale(5);

		viewport.setMinimumScale(0.15);
		viewport.setMaximumScale(3.0);
		//viewport.setWidthToDeviceWidth();
		//viewport.setHeightToDeviceHeight();
		MGWTSettings settings = new MGWTSettings();
		settings.setViewPort(viewport);
		settings.setAddGlosToIcon(true);
		settings.setFullscreen(true);
		//settings.setPreventScrolling(true);
		MGWT.applySettings(settings);

		//RootPanel.get("viewholder").add(new Label("titel"));
		RootPanel.get("main").add(this);

		RequestBuilder.Method method = RequestBuilder.GET;
		preSetupModule(link, url);

		//contentPanel.add(kbp);

		/*RequestBuilder rb = new RequestBuilder(method, url);
		try
		{
			rb.sendRequest(null, new RequestCallback()
			{

				@Override
				public void onResponseReceived(Request request, Response response)
				{
					String responseText = response.getText();
					setupModule("",responseText);
					
				}

				@Override
				public void onError(Request request, Throwable exception)
				{
					Window.alert("error loading activity.xmx");
				}
			});

		}
		catch (RequestException e)
		{
			Window.alert("error loading activity.xmx");
		}*/

		//

		FocusOnTouch.focus();

	}

	@Override
	public Widget asWidget()
	{
		return mainPanel;
	}

	public Scorm2004IF getApi() {
		return api;
	}

	public void setApi(Scorm2004IF api) {
		this.api = api;
	}

	@Override
	public void close() {
		if(on != null)
			on.close();
		PopupFacade.removeAll();
		kb.blur();
	}

	public FormuleKeyboard getKeyboard() {
		return kb;
	}

	public void setTitle(String string) {
		if(standalone) hp.setCenter(string);
		
	}

	@Override
	public AnchorContext getAnchorContext() {
		return anchorContext;
	}
	public void setAnchorContext(AnchorContext context) {
		if (context == null) context = new MyAnchorContext();
		anchorContext = context;
	}

	public void setupView(String launchDataString) {
		contentPanel.clear();
		// voor huub: allow old XML data 
		if(launchDataString.startsWith("<"))
		{		
			Document dom = XMLParser.parse(launchDataString);
			StringCodeToHashMap sc = new StringCodeToHashMap();
			launchData = sc.decodeStringToHashMap(dom);

		} else
		{
			JSONValue dom = JSONParser.parseStrict(launchDataString);
			//launchData = JSONUtilities.fromJSONObject(dom.isObject());
			launchData = JSONUtilities.wrapMap(dom.isObject());
		}
		setupView(launchData);
	}

	public String getUnitId() {
		return unitId;
	}

	private void gotoNext(final HeaderButton next) {
		if(inNavBtn) {
			return;
		}
		inNavBtn = true;
		next.getElement().getStyle().setProperty("pointerEvents", "none");
		((Element) next.getElement().getLastChild()).getStyle().setBackgroundColor("gray");
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				gaNaarVolgendeOpdracht();
				
//				int cur = on.getCurrentOpdracht() + 1;
//				if(cur >= on.getAantalOpdrachten()) cur = on.getAantalOpdrachten()-1;
//					on.gotoOpdracht(cur, scoreNav);
				
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						inNavBtn = false;
							next.getElement().getStyle().clearProperty("pointerEvents");
							((Element) next.getElement().getLastChild()).getStyle().clearBackgroundColor();
					}});
			}});
	}

	private void gotoPrev(final HeaderButton prev) {
		if(inNavBtn) {
			//logger.info("disabled");
			return;
		}
		inNavBtn = true;
		//DOM.setElementPropertyBoolean(next.getElement(), "disabled", true);
		prev.getElement().getStyle().setProperty("pointerEvents", "none");
		((Element) prev.getElement().getLastChild()).getStyle().setBackgroundColor("gray");
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				//logger.info("enabled");
				gaNaarVorigeOpdracht();
//				int cur = on.getCurrentOpdracht() - 1;
//				if(cur < 0) cur = 0 ;
//				on.gotoOpdracht(cur, scoreNav);
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						inNavBtn = false;
						//DOM.removeElementAttribute(next.getElement(), "disabled");
						prev.getElement().getStyle().clearProperty("pointerEvents");
						((Element) prev.getElement().getLastChild()).getStyle().clearBackgroundColor();
						logger.info("enable");
					}});
			}});
	}

}
