package nl.uu.fi.dwo.mobile.client.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.StateLess;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.interaction.client.keyboard.FocusOnTouch;
import nl.uu.fi.dwo.keyboard.client.AbstractKeyboard.HasHeight;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.text.Text;
//import nl.uu.fi.dwo.mobile.client.ui.FormuleKeyboard;
//import nl.uu.fi.dwo.mobile.client.ui.KeyBoardTabPanel;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF.NextPrevHandler;
import nl.uu.fi.dwo.mobile.client.ui.ScoreNavIF.ObjectivesHandler;
import nl.uu.fi.dwo.mobile.client.ui.SlidingPopup;
import nl.uu.fi.dwo.mobile.client.ui.TouchButton;
import nl.uu.fi.dwo.mobile.client.ui.WaitScreen;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.dom.client.recognizer.swipe.SwipeEndEvent;
import com.googlecode.mgwt.dom.client.recognizer.swipe.SwipeEndHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort.DENSITY;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.touch.TouchDelegate;

import fi.wiskopdr.text.Text_nl;


/**
 * 
 * @author Danny Hendrix, Evertson Croes, Sietske Tacoma, Wim van Velthoven
 * 
 */
public class ViewModuleViewImpl extends XMLView implements ViewModuleView, EntryPoint, NextPrevHandler, ObjectivesHandler, HasHeight
{
	private static final String RANDOM_VAR_WAARDEN = "RandomVarWaarden";
	private static final String RANDOM_VAR_NAMEN = "RandomVarNamen";
	private static final String KEYBOARD = "keyboardNr";
	private static final String WRITE_MATH_SET = "writeMathSetNr";
	private static Logger logger = Logger.getLogger("ViewModuleViewImpl");
	private boolean standalone = false;

	//@Deprecated // FIXME NIET GEBRUIKEN, CONVERTEREN NAAR Text.constants.xxxx()
	//static Text_nl rb = new Text_nl();

	OpdrNav on;
	private FocusPanel mainPanel;
	FlowPanel contentPanel = null;
	LayoutPanel contentScrollPanel = null;
	private Panel tekst = null;
	private ArrayList<TouchButton> buttons = new ArrayList<TouchButton>();
	private double zoom = 1;
	
	//private boolean zelftoetsGeenCorr = false;
	
	public boolean zelftoetsNagekeken = false;
	
	
	private Panel kbp = null;
	private HeaderButton hb;
	private HeaderPanel hp;
	private WaitScreen waitscreen = WaitScreen.instance();
	Label disableScreen = new Label();
	
	private Widget next, prev, end;
	

	private Scorm2004IF api;

	public ViewModuleViewImpl(boolean b) {
		standalone = b;
		
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
			rb.setTimeoutMillis(1000000);
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
						if (responseText.length() > 4 && response.getStatusCode() == 200)
						{
							setupView(responseText);
						} else {
							Window.alert(Text.constants.noJSONreceived());
							logger.severe("response empty");
						}
					}
		
					@Override
					public void onError(Request request, Throwable exception)
					{
						Window.alert(Text.constants.noJSONreceived() + 
								"\nerror " + exception);
						logger.log(Level.SEVERE, exception.toString(), exception);
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

	public void removeTitle() {
		hp.removeFromParent();
		setWindowTop(0);
		int h = mainPanel.getOffsetHeight();
		sb.setScrollPanel(this, h);
	}
	
	
	
	public void setupView(HashMap<String, Object> launchData)
	{
		for (int i = 0; i < buttons.size(); i++)
			contentPanel.remove(buttons.get(i));
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

			if (wrap.containsKey(KEYBOARD))
			{
				sb.setKeyboard(wrap.getInt(KEYBOARD));
			}
			else
				sb.setKeyboard(-1);
			if (wrap.containsKey(WRITE_MATH_SET))
			{
				sb.setWriteMathSet(wrap.getInt(WRITE_MATH_SET));
			}
			else
				sb.setKeyboard(-1);
			//

			contentPanel.getElement().getStyle()
				.setFontSize(font_size, Unit.PX);
			contentPanel.getElement().getStyle().setPadding(0, Unit.PX); // XXX
																			// was
																			// 15
			// GEEN randje aan de linkerkant, want dan klopt de maat (100%) niet
			// meer bij noordhoff

			(on = new OpdrNav()).init(launchData, this, createMemento());
			// voor noordhoff
			int aantalOpdrachten = on.getAantalOpdrachten();
			if (standalone && !bolletjesZichtbaar && !volgendeKnopZichtbaar
				&& !vorigeKnopZichtbaar && aantalOpdrachten == 1)
				removeTitle();

			FlowPanel onp = (FlowPanel) on.getAsPanel();
			if (bolletjesZichtbaar)
				sb.addNavPanel(onp);
			if (wrap != null && wrap.containsKey("itemOpnieuw"))
			{
				scoreNav.setItemOpnieuw(wrap.getBoolean("itemOpnieuw"));
			}
			scoreNav.setOpnieuw(opnieuw || opnieuwMogelijk);

			// pas vanaf hier toevoegen mogelijk.
			scoreNav.setBeantwoord(on.getAantalBeantwoord());
			scoreNav.setItemScores(on.getItemScores());
			scoreNav.setTotaalScore((int) on.getScore());
			scoreNav.setGotoOpdracht(on);
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "setupView()", e);
			Window.alert("Exception in setup: " + e.toString()
				+ "\nActivity might be instable");
		}
		if (DWOplayer.PARAMETERS.isNavTitle())
			setTitle("Vraag " + (1 + on.getCurrentOpdracht()) + " van "
				+ on.getAantalOpdrachten());
		// call SetupDone Handler, if an object is provided
		if (this.loadingHandler != null)
		{
			this.loadingHandler.viewModuleViewSetupDone();
			;
		}

		// benodigde knoppen toevoegen.
		int mode = on.getMode();
		if (mode == OpdrNav.ZELFTOETS)
		{
			scoreNav.setKijkNaEnabled(on.getAantalOpdrachten() == 1);
			sb.addKnop(scoreNav.getKijkNaButton(), false);
			scoreNav.setKijkNa(new ScoreNavIF.Checker()
			{

				@Override
				public void checkOpdracht(ScoreNavIF source)
				{
					on.saveCurrentState(); // de wijzigingen van het huidige bolletje moeten wel verwerkt worden
//					zetToetsNagekeken(source);
//					on.kijkToetsNa();

					// omgedraaid: keerNagekeken moet wel verhoogd zijn voor zetToetsNagekeken()
					zelftoetsNagekeken = true;
					on.kijkToetsNa();
					zetToetsNagekeken(source);

					on.saveCurrentState(); // op speciaal verzoek
				}

			});
		}
		scoreNav.setNextPrevHandler(this);
		scoreNav.setScoresObjectivesKnop(on.zijnObjectivesAanwezig()
			&& mode != OpdrNav.EINDTOETS);
		scoreNav.setObjectivesHandler(this);
		stelNavigatieIn();

		if (mode == OpdrNav.ZELFTOETS)
		{
			// set values
			scoreNav.setTotaalScoreLabel(on.getTotaalScore());
			scoreNav.setKeerNagekekenLabel(on.getKeerNagekeken());

			// add totaalscore and keer nagekeken labels
			sb.addLabel(scoreNav.getTotaalScoreLabel());
			sb.addLabel(scoreNav.getKeerNagekekenLabel());
		}
		if ( on.isEindtoetsVerzegeld()) {
			sb.addLabel(new Label(Text.constants.lockToetsLabel()));
		}
//		else if (mode == OpdrNav.OEFENEN || mode == OpdrNav.OEFENEN_STRAFPUNTEN)
//		{
//			// Geen nakijkknop, dus ook niet keernagekeken
//			// maar wel totaalscore
//		}
	}

	protected Memento createMemento() {
		return new Memento(getApi());
	}
	
	void zetToetsNagekeken(ScoreNavIF source)
	{
		int mode = on.getMode();
		if (mode == 2 || mode == 3)
		{
			zelftoetsNagekeken = true;

			if (mode == 2 && zelftoetsGeenCorr)
			{
				// laatste kans op update sessiontime
//				if (!zelftoetsNagekeken)
//				{
//					opdrContainer.sessionStop();
//					times[activiteitNr][opdrachtNr] = opdrContainer.getSessionTime();
//				}
//				zetAfdekPanelLeeg(true);
				
				if (zelftoetsNagekeken)
					zetAfdekPanel(true);
				else
					zetAfdekPanel(false);
			}
			source.setKijkNaEnabled(!zelftoetsGeenCorr);
			//scoresObjectivesKnop.setEnabled(true);//goed? nodig?
			prev.setVisible(vorigeKnopZichtbaar || !bolletjesZichtbaar && zelftoetsNagekeken);
			scoreNav.setScoresObjectivesKnop(on.zijnObjectivesAanwezig());
			
			scoreNav.setTotaalScoreLabel(on.getTotaalScore());
			scoreNav.setKeerNagekekenLabel(on.getKeerNagekeken());
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
	
// deze zouden inline moeten worden gemaakt.
// gebruik zetVolgendeOpdracht in bijna alle gevallen
	@Deprecated
	public final void zetOpdracht(HashMap<String, Object> opdracht)
	{
		zetOpdracht(opdracht, true);
	}

	public final void zetVolgendeOpdracht(HashMap<String,Object> opdracht) {
		zetOpdracht(opdracht, !globalParam);
	}
	
	/**
	 * voor 'globale parameters'
	 * @param opdracht launchdata
	 * @param randomise (initial|| !globalparameters)
	 */
	
	public void zetOpdracht(HashMap<String, Object> opdracht, boolean randomise)
	{
		
		
		String randVarString = "";
		randVarString = (String) opdracht.get("randVarString");
		if(randVarString == null) randVarString = "";
		VariableCollection vc = new VariableCollection();
		boolean wellSet = vc.setVariables(randVarString);

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
				wellSet = false;
			}
		} else {
			varnamen = this.randomVarNamen; // keep from last time
			waarden = this.randomVarWaarden;
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

	public void zetOpdrachtPlusState(HashMap<String, Object> opdracht, HashMap<String, Object> state)
	{
		
		//System.out.println("zetOpdrachtPlusState");
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
		if(on.isEindtoetsVerzegeld()) {
			zetNagekeken(true);
			kijkNa();
		}
			
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
				scoreNav.setButtonEnabled(opdrNr,true);
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
				scoreNav.setButtonEnabled(i, false);
			
//			if(allesCorrectNodig && !on.getOpdrachtCorrect(actNr, opdrNr) && !on.geefNoScore(actNr, opdrNr + 1)) // klopt die + 1??
//				eindeKnop.setEnabled(false);
//			else
//				eindeKnop.setEnabled(true);
		}
		else
		{	//eindeKnop.setVisible(false);
			//eindeKnop.setEnabled(false);
			scoreNav.setVolgendeVisible(volgendeKnopZichtbaar);
			scoreNav.setVorigeVisible(vorigeKnopZichtbaar);
			//Als leerling pas door mag als alles op pagina correct: volgende bolletjes en volgende/einde-knop disablen.
			if(allesCorrectNodig && !on.getOpdrachtCorrect(actNr, opdrNr) && !on.geefNoScore(actNr, opdrNr + 1)) //klopt die + 1??
			{	for(int i = opdrNr + 1; i < on.getAantalOpdrachten(); i++)
					scoreNav.setButtonEnabled(i, false);
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
			if(condNav && condNavVoorwaarden)
			{	for(int i = opdrNr + 1; i < on.getAantalOpdrachten(); i++)
				//	or[actNr].setEnabled(bezocht[actNr][i], i + 1);
					scoreNav.setButtonEnabled(i, bezocht[actNr][i]);
				if(bepaalVolgendeOpdracht(actNr, opdrNr) > -1)
				{	scoreNav.setButtonEnabled(bepaalVolgendeOpdracht(actNr, opdrNr), !allesCorrectNodig || on.geefNoScore(actNr, opdrNr + 1) || on.getOpdrachtCorrect(actNr, opdrNr));
					if(!allesCorrectNodig)
					{	int volgende = bepaalVolgendeOpdracht(actNr, opdrNr);
						while(bepaalVolgendeOpdracht(actNr, volgende) > -1)
						{	if(bepaalVolgendeOpdracht(actNr, volgende) > volgende + 1)
								for(int i = volgende + 1; i < bepaalVolgendeOpdracht(actNr, volgende); i++)
								//	or[actNr].setEnabled(false, i + 1);
									scoreNav.setButtonEnabled(i, false);
							//or[actNr].setEnabled(true, bepaalVolgendeOpdracht(actNr, volgende) + 1);
							scoreNav.setButtonEnabled(bepaalVolgendeOpdracht(actNr, volgende), true);
							volgende = bepaalVolgendeOpdracht(actNr, volgende);
						}
						if(volgende + 1 < on.getAantalOpdrachten())
						{	for(int i = volgende + 1; i < on.getAantalOpdrachten(); i++)
							//	or[actNr].setEnabled(false, i + 1);
							scoreNav.setButtonEnabled(i, false);
						}
								
					}
				
				}
			}
		
		}
		zetVorigeKnoppenEnabled(opdrNr > 0);
		zetNakijkKnopEnabled();
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
		boolean enable = !(zelftoetsNagekeken && zelftoetsGeenCorr) && suspendDataCompleted(on.getCurrentActiviteit(), on.getCurrentOpdracht());
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
		FormuleEditorWithSteps fews = new FormuleEditorWithSteps(opdracht, false, tb.getVarNamen(), tb.getVarWaarden(), null);

		//fews.getEditor().requestFocus();
		

		contentPanel.add(fews.getAsPanel());
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
				((InteractionView) currentObject).setState(state);
				stateNr++;
			}
		}
		on.unpause(old);
		stelNavigatieIn();

	}
	
	public void kijkNa()
	{
		on.pause();
		for (int i = 0; i < opdrachtObjects.size(); i++)
		{
			Object currentObject = opdrachtObjects.get(i);
			if (currentObject instanceof InteractionView)
			{
				((InteractionView)currentObject).kijkNa();
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
		if (b)
		{
			contentScrollPanel.add(disableScreen);
		}
		else
		{
			contentScrollPanel.remove(disableScreen);
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

	final class Resizer implements ResizeHandler {
		@Override
		public void onResize(ResizeEvent event) {
			int h = event.getHeight() - extraHeight;
			//logger.info("resize event " +  h);
			sb.setScrollPanel(ViewModuleViewImpl.this, h);
			
		}
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
		sb = DWOplayer.PARAMETERS.getStatusBar(); // new nl.uu.fi.dwo.mobile.client.ui.FormuleKeyboard();
		kb = sb.getFormuleKeyboard();
		cb = sb.getFormuleClipboard();
		scoreNav = DWOplayer.PARAMETERS.getScoreNav();
		POPUP = scoreNav.getPopup();

		scoreNav.setStatusBar(sb);
		if(!standalone) setWindowTop(0);

		FocusOnTouch.installKeyboard(kb, cb);
		FormuleHolder.installKeyboard(kb);
		
		hp = new HeaderPanel(DWOplayer.PARAMETERS.headercss());
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
			hp.setRightWidget(hbox);
			hb = new HeaderButton(DWOplayer.PARAMETERS.headercss());
			hb.getElement().getStyle().setBackgroundImage("url('" + DWOplayer.DWO_BUNDLE.menuIcon().getSafeUri().asString() + "')");
			hp.setLeftWidget(hb);
			fp.add(hp);
		}
		contentScrollPanel = new LayoutPanel();
		contentScrollPanel.addStyleName("contentScrollPanel");
		contentScrollPanel.setWidth("100%");
		contentScrollPanel.setHeight("100%");
		contentScrollPanel.getElement().getStyle().setOverflowY(Overflow.HIDDEN);
		contentScrollPanel.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
//
		contentPanel = new FlowPanel();contentPanel.setStylePrimaryName("contentPanel");
		contentPanel.getElement().getStyle().setProperty("display", "inline-block");
// smooth scroll on ios devices:
		setWebkitScrolling(true);
		//contentPanel.getElement().getStyle().setMarginBottom(360, Unit.PX);
		contentPanel.setWidth("100%"); // hoeveel is 100% - 30px ?
		contentPanel.setHeight("100%");
// FIXME Hier moeten we een gesture recognizer maken:

		if(TouchEvent.isSupported()) {
			TouchDelegate touchDelegate = new TouchDelegate(contentPanel);
			//touchDelegate.addPinchHandler(new PinchContent());
			
			touchDelegate.addSwipeEndHandler(new SwipeEndHandler() {
				
				@Override
				public void onSwipeEnd(SwipeEndEvent event) {
					switch( event.getDirection()) {
					case LEFT_TO_RIGHT:  gotoPrev(scoreNav); break;
					case RIGHT_TO_LEFT: gotoNext(scoreNav); break;
					// TODO case BOTTOM_TO_TOP: showScore(scoreNav);
					default:
					}
					
				}
			});
		}
		//ipv addContentPanelTouchListener(contentPanel);

		contentScrollPanel.add(contentPanel);
		contentPanel.getElement().getStyle().setOverflowY(Overflow.AUTO);
		contentPanel.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
		
		fp.add(contentScrollPanel);

		Widget kbp = sb.asWidget();
		fp.add(kbp);

// POPUP of floating in ????
		if(hb != null && POPUP != null)
		{  hb.addTapHandler(new TapHandler() {

			@Override
			public void onTap(TapEvent event) {
				if(POPUP.isShowing())
						POPUP.hide();
				else
						popupNavPanel();
				
			}});
			POPUP.addAutoHidePartner(hb.getElement());
		}
		//initWidget(mainPanel);
		
		disableScreen.getElement().getStyle().setBackgroundColor("transparent");
		
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
    SlidingPopup POPUP;
    
    public ScoreNavIF scoreNav; 
    
    protected void popupNavPanel() {
		 final SlidingPopup popup = POPUP;
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
		//mainPanel.setHeight("426px");
		//mainPanel.setWidth("886px");
		extraHeight = 40;
		//fp.setHeight("428px");
		//fp.setWidth("886px");
		//if(!standalone) fp.add(hp);
		//final int contentHeight = 426 - 40; // 40 = hoogte headerpanel.
		int contentHeight = Window.getClientHeight() - extraHeight;
		Window.addResizeHandler(new Resizer());
		//contentScrollPanel.setPixelSize(886, contentHeight ); 
		//contentScrollPanel.setHeight("100%");
		//fp.add(contentScrollPanel);
//		contentPanel.getElement().getStyle().clearMarginBottom();
// probeersel!
//
//		contentPanel.getElement().getStyle().setMarginBottom(360, Unit.PX);
//		Panel kbp = kb.getAsPanel();
//		kbp.setWidth("886px");
		sb.zetMaat();
		sb.setScrollPanel(this, contentHeight);
		//fp.add(kbp);

	}
	
	protected int extraHeight = (MGWT.getOsDetection().isAndroid() ? 52:41) // header height in android 50+2 			
			+ 44 /*KeyBoardTabPanel.KEYB_STATIC_HEIGHT*/;
	private String unitId = "scoViewNr";
	
	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public void setWindowTop(int top) {
		extraHeight = top + sb.getStatusBarHeight();
	}
	
	
	public void zetMaat() {

		// FIXME HACK voor DWOplayer zelf		
		hb = new HeaderButton(DWOplayer.PARAMETERS.headercss()); hb.setBackButton(true);
		hb.setText(fi.wiskopdr.text.Text.constants.terugKnopLabel());
		hp.setLeftWidget(hb);
		hp.setRightWidget(null);
		
		///contentPanel.getElement().getStyle().setMarginBottom(360, Unit.PX);
		int contentHeight = Window.getClientHeight() - extraHeight;
		Window.addResizeHandler(new Resizer());
		sb.zetMaat();
		sb.setScrollPanel(this, contentHeight);

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

	public FormuleKeyboardIF getKeyboard() {
		return kb;
	}

	public void setTitle(String string) {
		if(string == null) string = "";
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
		on.openObjectivesPanel();
	}

	// WaitScreen management: p(); .....; v();
	private int sema;
	public void p() {
		if( sema++ == 0) {
			waitscreen.w();
		}
	}
		
	public void v() {
		if ( --sema <= 0) {
			sema = 0;
			waitscreen.hide();
		}
	}

	public FormuleClipboardIF getClipboard() {
		return cb;
	}

	@Override
	public void setHeight(int px) {
		setWebkitScrolling(false);
		contentScrollPanel.setPixelSize(-1, px);
		setWebkitScrolling(true);
	}

	@Override
	public Number getScoreRaw() {
		if(on == null)
			return null;
		return Double.valueOf(on.getScore());
	}
	
	
}
