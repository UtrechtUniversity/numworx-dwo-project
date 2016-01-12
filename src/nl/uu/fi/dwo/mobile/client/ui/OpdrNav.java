package nl.uu.fi.dwo.mobile.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;

/**
 * Used for navigation between assignments
 * 
 * @author Evertson Croes
 * 
 */
public class OpdrNav implements OpdrNavIF, Runnable, ScoreNavIF.GotoOpdracht
{
	public final static int OEFENEN = 0;
	public final static int OEFENEN_STRAFPUNTEN = 1;
	public final static int ZELFTOETS = 2;
	public final static int EINDTOETS = 3;
	
	private static String[][] objectives;
	private static String[] categorieString;
	private boolean objectivesAanwezig = false;
	
	private static final int foutStraf = 2;
	private static final Logger logger = Logger.getLogger("OpdrNav");

	private ViewModuleViewImpl entry;
	private ListBox lb_activiteiten;
	private Panel fp_opdrachten;
	private Panel mainPanel;
	private Panel contentPanel;
	private int aantalActiviteiten;
	private int[] aantalOpdrachten;
	private String[] activiteitNamen;
	private int maxAantalOpdrachten = 50;
	
	private boolean[][] buttonsEnabled;
	
	private HashMap<String, Object>[][] opdrachten;
	private HashMap<String, Object>[][] states;
	private int[][] scoresMax;
	private int[][] scores;
	private boolean[][] isCorrect;
	private boolean[][] opdrachtenCorrect;
	private int[] aantalNakijken; // per activiteit
	
	private int[][][][] scoresMaxObjectives;
	private int[][][][] scoresObjectives;
	private DialogBox scoresObjectivesDialog;
	private ScoresObjectivesPanel scoresObjectivesPanel;
	
	private int mode;
	private int[][] strafpunten;

	private int scoreMax;
	private int currentOpdracht = 0;
	private int currentActiviteit = 0;
	private ArrayList<TouchButton> buttons = new ArrayList<TouchButton>();
	private Memento memento;
	private final static EventBus BUS = 
			DWOplayer.PARAMETERS.getEventBus();
	

	static private Prepare prepare = DWOplayer.PARAMETERS.getPrepareInstance();
	
	public static class Prepare {
		void defer(ScheduledCommand cmd) {
			Scheduler.get().scheduleDeferred(cmd);
		}
		@Deprecated void immediate(ScheduledCommand cmd) {
			cmd.execute();
		}
	}
	
	public static class MC2Prepare extends Prepare {
		private static final CBookEvent STOP = new CBookEvent("stop");
		void defer(final ScheduledCommand cmd) {
			BUS.fireEvent(STOP); // ask CBook Widgets to send 'getState' events.
			Timer t = new Timer() {
				@Override public void run() {
					cmd.execute();				
				}
			};
			t.schedule(100);
		}
	}
	
	@Deprecated public static void immediate(ScheduledCommand cmd) {
		prepare.immediate(cmd);
	}
	public static void defer(ScheduledCommand cmd) {
		prepare.defer(cmd);
	}
	
	public OpdrNav() {};
	public void init(HashMap<String, Object> launchData, ViewModuleViewImpl ev, Memento memento)
	{
		this.entry = ev;
		this.memento = memento;
		memento.setUnload(this);
		aantalActiviteiten = Integer.parseInt((String) launchData.get("aantalActiviteiten"));
		activiteitNamen = new String[aantalActiviteiten];
		aantalOpdrachten = new int[aantalActiviteiten];
		buttonsEnabled = new boolean[aantalActiviteiten][];
		
		mode = Integer.parseInt((String)launchData.get("mode"));
		maxAantalOpdrachten = 1;
		for (int i = 0; i < aantalActiviteiten; i++)
		{
			activiteitNamen[i] = (String) launchData.get("activiteit_" + (i + 1));
			String aantalString = (String) launchData.get("aantalOpdrachten_" + (i + 1));
			aantalOpdrachten[i] = Integer.parseInt(aantalString);
			maxAantalOpdrachten = Math.max(maxAantalOpdrachten, aantalOpdrachten[i]);
			buttonsEnabled[i] = new boolean[aantalOpdrachten[i]];
			for(int j = 0; j < aantalOpdrachten[i]; j++)
				buttonsEnabled[i][j] = true;
		}
		
		
		opdrachten = new HashMap[aantalActiviteiten][maxAantalOpdrachten];
		scoresMax = new int[aantalActiviteiten][maxAantalOpdrachten];
		scores = new int[aantalActiviteiten][maxAantalOpdrachten];
		isCorrect = new boolean[aantalActiviteiten][maxAantalOpdrachten];
		opdrachtenCorrect = new boolean[aantalActiviteiten][maxAantalOpdrachten];
		states = new HashMap[aantalActiviteiten][maxAantalOpdrachten];
		scoreMax = 0;
		if (objectives != null)
		{
			scoresObjectives = new int[aantalActiviteiten][maxAantalOpdrachten][objectives.length][];
			scoresMaxObjectives = new int[aantalActiviteiten][maxAantalOpdrachten][objectives.length][];
			for (int k = 0; k < aantalActiviteiten; k++)
				for (int j = 0; j < maxAantalOpdrachten; j++)
					for (int i = 0; i < objectives.length; i++)
					{
						scoresObjectives[k][j][i] = new int[objectives[i].length];
						scoresMaxObjectives[k][j][i] = new int[objectives[i].length];
					}
		}
		
		if(mode == OEFENEN_STRAFPUNTEN)
			strafpunten = new int[aantalActiviteiten][maxAantalOpdrachten];
		

		for (int i = 0; i < aantalActiviteiten; i++)
		{
			for (int j = 0; j < aantalOpdrachten[i]; j++)
			{
				Object object = launchData.get("opdracht_" + (i + 1) + "_" + (j + 1));
				if(! (object instanceof HashMap)){
					object = new HashMap(); // XXX Wat is de minimum hashmap hier? 
					// minimaal: hasAntwoordVak = Boolean
					java.util.logging.Logger.getLogger("OpdrNav").severe("Opdracht " + (j+1)  + " geen map ");
				}
				opdrachten[i][j] = (HashMap<String, Object>) object;
				HashMap<String, Object> opdrachtInfo = opdrachten[i][j];
				ObjectMap ht = JSONUtilities.wrapMap(opdrachtInfo);
				if (ht != null && objectives != null && ht.containsKey("scoreMaxObjectives"))
				{	
					ObjectList scoreList = ht.getObjectList("scoreMaxObjectives");
					scoresMaxObjectives[i][j] = new int[scoreList.size()][];
					for(int k = 0; k < scoreList.size(); k++)
					{	try{
						scoresMaxObjectives[i][j][k] = scoreList.getIntArray(k);
						}
						catch(Exception e)
						{}
					}
					
					
					//scoresMaxObjectives[i][j] = (int[][]) ht.get("scoreMaxObjectives");
				}
				
				
				if (ht != null && ht.containsKey("scoreMax"))
					scoresMax[i][j] = ht.getInt("scoreMax");
				else
					scoresMax[i][j] = 10;
				scoreMax += scoresMax[i][j];
			}
		}
		
		int sumScoresMaxObjectives = 0;
		try
		{
			for (int i = 0; i < scoresMaxObjectives.length; i++)
				for (int j = 0; j < scoresMaxObjectives[i].length; j++)
					for (int k = 0; k < scoresMaxObjectives[i][j].length; k++)
						for (int l = 0; l < scoresMaxObjectives[i][j][k].length; l++)
							sumScoresMaxObjectives += scoresMaxObjectives[i][j][k][l];
		}
		catch (Exception e)
		{
		}

		if (sumScoresMaxObjectives > 0)
			objectivesAanwezig = true;
		else
			objectivesAanwezig = false;
		entry.setCommunicationRoot(this);
		
		states = memento.getOpdrContStates(states);
		currentActiviteit = memento.getCurrentActiviteit();
		currentOpdracht = memento.getCurrentOpdracht();
		
		memento.getStrafpunten(strafpunten);
		memento.getOrGoedFout(isCorrect);
		memento.getScores(scores);
		
		contentPanel = new FlowPanel();
		contentPanel.getElement().getStyle().setMargin(5, Unit.PX);
		
		setOpdrachten(currentActiviteit); // kan dat nu al? of anders bij setchanged testen op  buttons.get() != null
		
		// initializeer bezocht		
		{	boolean[][] bezocht;
			bezocht = new boolean[getAantalActiviteiten()][];
			for(int j = 0; j < getAantalActiviteiten(); j++)
			{	bezocht[j] = new boolean[getAantalOpdrachten(j)]; // all false
			}
			bezocht[0][0] = true;
			entry.bezocht = bezocht;
		}
		
		memento.getBezocht(entry.bezocht);
		entry.zelftoetsNagekeken = memento.getZelftoetsNagekeken();
		aantalNakijken = memento.getAantalNakijken();
		
		final HashMap<String, Object> state = states[currentActiviteit][currentOpdracht];

		entry.scoreNav.setAantalOpdrachten(getAantalOpdrachten(), getMaxScores(), getCurrentOpdracht());

		if (state == null)
		{
			//logger.info("zetOpdracht no state");
			entry.zetOpdracht(opdrachten[currentActiviteit][currentOpdracht]);
		}
		else
		{
			//logger.info("zetOpdracht plus state");
			entry.zetOpdrachtPlusState(opdrachten[currentActiviteit][currentOpdracht], state);
			
		}
		
		

	}
	
	public boolean isEindtoetsVerzegeld() {
		return mode == EINDTOETS && memento.isEindtoetsVerzegeld();
	}
	public static void setObjectives(String[][] o)
	{
		objectives = o;
	}
	
	public static void setCategorieString(String[] c)
	{
		categorieString = c;
	}

	public Panel getAsPanel()
	{
		mainPanel = new FlowPanel();
		contentPanel = new FlowPanel();
		contentPanel.getElement().getStyle().setMargin(5, Unit.PX);
		
		mainPanel.add(contentPanel);

		lb_activiteiten = new ListBox();
		//We bieden (in elk geval tijdelijk) geen support voor verschillende niveaus. 
		//Als we de verschillende niveaus er helemaal uit halen, kan de code op den duur ook flink versimpeld.. Maar dat is iets voor langere termijn.
		//if (aantalActiviteiten == 1)
			lb_activiteiten.setVisible(false);
		lb_activiteiten.getElement().getStyle().setFloat(Style.Float.LEFT);
		lb_activiteiten.getElement().getStyle().setMarginRight(10, Unit.PX);
		lb_activiteiten.addChangeHandler(new ChangeHandler()
		{
			public void onChange(ChangeEvent event)
			{
				saveCurrentState();
				int selectedIndex = lb_activiteiten.getSelectedIndex();
				currentActiviteit = selectedIndex;
				currentOpdracht = 0;
				setOpdrachten(selectedIndex);
				entry.clearContentPanel();
				entry.zetOpdracht(opdrachten[currentActiviteit][currentOpdracht]);
				if (states[currentActiviteit][currentOpdracht] != null)
					entry.setState(states[currentActiviteit][currentOpdracht]);
			}
		});
		for (int i = 0; i < aantalActiviteiten; i++)
		{
			lb_activiteiten.addItem(activiteitNamen[i]);
		}

		contentPanel.add(lb_activiteiten);
		setOpdrachten(0);
		return mainPanel;
	}

	public Panel getPanel()
	{
		return mainPanel;
	}
	
	public boolean getOpdrachtCorrect(int i, int j)
	{
		return opdrachtenCorrect[i][j];
	}

	public void setChanged(boolean fout) // FIXME Trifork: hier safepoint?
	{
		Boolean check = entry.isCorrect();
		boolean correct =  Boolean.TRUE.equals(check);
		isCorrect[currentActiviteit][currentOpdracht] = correct;
		opdrachtenCorrect[currentActiviteit][currentOpdracht] = correct;
		
		//met nieuwe implementatie strafpunten: niet meer hier regelen maar in losse antwoordvakken. 
		//boolean fout is ook niet meer nodig.
//		if(strafpunten != null && mode == OEFENEN_STRAFPUNTEN && fout)
//			strafpunten[currentActiviteit][currentOpdracht] += foutStraf;
		if (buttons != null && buttons.size() > currentOpdracht)
		{	//logger.fine("setChanged zet Button " + currentOpdracht + " correct; correct = " + correct);
			setButtonCorrect(buttons.get(currentOpdracht), correct, currentOpdracht);
		}
		saveCurrentState();
		entry.stelNavigatieIn();
	}
		
	private void setOpdrachten(int index)
	{
		if (fp_opdrachten != null)
		{
			contentPanel.remove(fp_opdrachten);
		}
		if (buttons != null)
		{
			buttons.clear();
		}

		fp_opdrachten = new FlowPanel();
		fp_opdrachten.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		//fp_opdrachten.getElement().addClassName("opdrbutton-container");
		for (int j = 0; j < aantalOpdrachten[index]; j++)
		{
			setButton(j);
			setButtonCorrect(buttons.get(j), isCorrect[index][j], j);
		}
		fp_opdrachten.getElement().getStyle().setFloat(Style.Float.LEFT);
		contentPanel.add(fp_opdrachten);
	}

	private void setButton(int j)
	{
		TouchButton button = new TouchButton();
// enable scores, geen toets en scoreMax > 0
		if (!geefNoScore(currentActiviteit, j))
		{
			TouchDown handler = new TouchDown(j);
			button.addDomHandler(handler, MouseOverEvent.getType());
			button.addDomHandler(handler, com.google.gwt.event.dom.client.TouchEndEvent.getType());
		}
		
		button.setStylePrimaryName("scoreBtn");
		final int button_id = j;
		if (geefNoScore(currentActiviteit, j))
		{
			button.addStyleDependentName("max0");
		}
		if(!buttonsEnabled[currentActiviteit][j])
		{
			button.addStyleDependentName("disabled");
		}

		button.setText(" " + (j + 1) + " ");
		if (currentOpdracht == j)
		{
			setButtonCursor(button);
		}
		addButtonHandler(button, j);

		buttons.add(button);
		fp_opdrachten.add(button);
	}

	
	/**
	 * wordt gebruikt aan het begin, als de pagina gestart wordt.
	 * 
	 * @return
	 */
	public boolean scoresVisible() {
		return 
				entry.getZelftoetsNagekeken() 
				||  (mode == EINDTOETS && memento.isEindtoetsVerzegeld()) 
				|| mode == OEFENEN 
				|| mode == OEFENEN_STRAFPUNTEN;
	}
	
	/**
	 * wordt gebruikt om bij verandering de scores automatisch bij te werken. 
	 * bijv. bij savecurrentstate.
	 * bij kijkna knop, die doet het zelf voor de hele activiteit.
	 * @return
	 */
	public boolean scoresEnabled() 
	{
		return mode == OEFENEN 
			|| mode == OEFENEN_STRAFPUNTEN 
			|| ( mode == EINDTOETS && !memento.isEindtoetsVerzegeld() ) ;
	}
	
	public TouchButton getButton(int j)
	{
		try{
			return buttons.get(j);
		}
		catch(Exception e)
		{
			return null;
		}
		
	}

	private void addButtonHandler(TouchButton button, int id)
	{
		final int button_id = id;
		button.addTouchStartHandler(new TouchStartHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				if(buttonsEnabled[currentActiviteit][button_id])
				{
					entry.p();
					ScheduledCommand run = new ScheduledCommand() {

						@Override
						public void execute() {
							gotoOpdracht(button_id, entry.scoreNav);		
							entry.v();
						}};
					defer(run);
				}
			}

		});
	}

	private Timer popupTimer;
	private final int nakijkStraf = 5; // voorlopig final
	
	private void schedule(final int index, boolean touch) {
		if(popupTimer != null) 
		{
			popupTimer.cancel();
			popupTimer.run();
		}
		if(touch && index != getCurrentOpdracht())
			return;
		
		
		if (geefNoScore(currentActiviteit, index) ||
			!buttonsEnabled[currentActiviteit][index] ||
			!scoresVisible()
		) 
			return; 
		
		final TouchButton btn = buttons.get(index);
		final int score = getItemScores()[index];
		if(score == 0) return;
		popupTimer = new Timer() {

			@Override
			public void run() {
				popupTimer = null;
				btn.setStyleDependentName("popupTime", false);
				btn.setText(Integer.toString(index+1));
				//logger.info("timer for "+ index + " fired");
			} };
		btn.setStyleDependentName("popupTime", true);
		btn.setText(Integer.toString(score));
		popupTimer.schedule(2000);
	}
	
	class TouchDown implements MouseUpHandler, TouchEndHandler, MouseOverHandler  {
		private int index;
		public TouchDown(int index) {
			this.index = index;
		}

		@Override
		public void onTouchEnd(
				com.google.gwt.event.dom.client.TouchEndEvent event) {
			schedule(index, true);
		}

		@Override
		public void onMouseUp(MouseUpEvent event) {
			schedule(index, false);
		}

		@Override
		public void onMouseOver(MouseOverEvent event) {
			schedule(index, false);			
		}
		
	}
	
	
	
	private void setButtonCorrect(TouchButton button, boolean b, int j)
	{
		if (geefNoScore(currentActiviteit, j))
		{
			button.setStyleDependentName("max0", geefNoScore(currentActiviteit, j) );
			return;
		}
		button.setStyleDependentName("max0", false);
		if(scoresVisible())
		{
			button.setStyleDependentName("correct", b);
			button.getElement().setPropertyInt("title", getItemScores()[j]);
		}
	}
	
	public boolean geefNoScore(int actNr, int opdrNr)
	{
		return scoresMax[actNr][opdrNr] == 0;
	}
	
	public void setButtonEnabled(int j, boolean b)
	{
		if(!entry.bolletjesZichtbaar() || buttons.size() < j)
			return;
		buttonsEnabled[currentActiviteit][j] = b;
		if(b)
		{	buttons.get(j).setStyleDependentName("disabled", false);
			setButtonCorrect(buttons.get(j), isCorrect[currentActiviteit][j], j);
		}
		else
		{
			buttons.get(j).setStyleDependentName("disabled", true);			
		}
	}

	public void setButtonCursor(TouchButton button)
	{
		button.addStyleDependentName("cursor");
	}

	public void removeButtonCursor(TouchButton button)
	{
		button.removeStyleDependentName("cursor");
	}

	/**
	 * Berekent de totale score van het applet, en geeft deze terug, geschaald
	 * naar 100%.
	 */
	public double getScore()
	{
		int totaalScore = getTotaalScore();
		int totaalMax = getTotaalMax();
		
		if (totaalMax == 0)
			return 0;

		double doubleScore = Math.round(100.0 * totaalScore / totaalMax);
		
		if (Double.isInfinite(doubleScore) || Double.isNaN(doubleScore))
			doubleScore = 0;
		
		return doubleScore;
	}
	
	/**
	 * Berekent de totale score van de activiteit (over alle activiteiten (?) en opdrachten),
	 * inclusief nakijkstraf indien van toepassing.
	 */
	public int getTotaalScore()
	{

		// int mode = EINDTOETS; // TODO wat zijn de modes? Launchdata?
		int totaalScore = 0;

		for (int i = 0; i < aantalActiviteiten; i++)
		{
			switch (mode)
			{
				case ZELFTOETS:
					int hulp = (getAantalNakijken(i) * nakijkStraf - nakijkStraf); // een keer gratis nakijken.
					
					if (hulp < 0)
						hulp = 0;
					
					hulp = -hulp; // aftrek!
					int size = aantalOpdrachten[i];
					
					for (int j = 0; j < size; j++)
					{
						hulp += scores[i][j];
					}
					
					if (hulp > 0)
						totaalScore += hulp;
					
					break;
				case EINDTOETS:
					for (int j = 0; j < aantalOpdrachten[i]; j++)
					{
						totaalScore += scores[i][j];
					}
					break;
				default:
					for (int j = 0; j < aantalOpdrachten[i]; j++)
					{
						totaalScore += scores[i][j];
					}
			}

		} // for-loop i

		return totaalScore;
	}

	/**
	 * Berekent het aantal keer nagekeken (over alle activiteiten (?)).
	 */
	public int getKeerNagekeken()
	{
		int keerNagekeken = 0;

		for (int i = 0; i < aantalActiviteiten; i++)
		{
			keerNagekeken =+ getAantalNakijken(i);
		}

		return keerNagekeken;
	}

	private int getTotaalMax()
	{
		int totaalMax = 0;
		
		for (int i = 0; i < aantalActiviteiten; i++)
		{
			for (int j = 0; j < aantalOpdrachten[i]; j++)
			{
				totaalMax += scoresMax[i][j];
			}
		}

		return totaalMax;
	}

	public int getScore(int actNr, int opdrNr)
	{
		return scores[actNr][opdrNr];
	}
	
	public int getStrafpunten(int actNr, int opdrNr)
	{
		if(strafpunten != null)
		{	try{
				return strafpunten[actNr][opdrNr];
			}
			catch(Exception e)
			{
				return 0;
			}
		}
		else
			return 0;
		
	}
	
	public int getMaxScore(int actNr, int opdrNr)
	{
		return scoresMax[actNr][opdrNr];
	}

	public void run()
	{
		saveCurrentState();
	}

	private int scsInUse;
	private boolean scsPending;
	private boolean goon = true;
	
	public void saveCurrentState()
	{
		try {
			if(scsInUse ++ == 0)
			{
				saveCurrentState0();
				scsPending = false;
			} else {
				if(goon) saveCurrentState_stap1(); // dit MOET altijd voor kijkna, behalve in setState/getState zelf
				else {
					GWT.log("break recursion");
				}
				scsPending = true;
			}
		} finally {
			--scsInUse;
		}
	}	
	
	public void pause() {
		scsInUse++;
	}

	public void unpause() {
		--scsInUse;
		if(scsPending && scsInUse == 0) saveCurrentState();
		if(scsInUse == 0) goon = true;
	}
	
	public void unpause(boolean old) {
		unpause();
		goon = old;
	}
	
	private void saveCurrentState0() {	
		saveCurrentState_stap1();		
		
		memento.setCurrentActiviteit(currentActiviteit);
		memento.setCurrentOpdracht(currentOpdracht);
		memento.setOrGoedFout(isCorrect);
		double score = getScore();
// THIS ORDER!!!!!
		memento.setScore(score);
		memento.setScores(scores);
		memento.setBezocht(entry.bezocht);
		memento.setZelftoetsNagekeken(entry.zelftoetsNagekeken);
		memento.setAantalNakijken(aantalNakijken);
		memento.setCompletion(suspendDataCompleted(currentActiviteit, currentOpdracht));
		memento.setOpdrContStates(states);
		memento.flush();

// send data to GUI (why here?)
		saveCurrentState_stap3(score);
	}
	private void saveCurrentState_stap3(double score) {
		ScoreNavIF source = entry.scoreNav;
		if(scoresVisible())
		{ 
			source.setItemScore(currentOpdracht, scores[currentActiviteit][currentOpdracht]);		
			source.setTotaalScore((int) score); 
		}
		source.setBeantwoord(getAantalBeantwoord()); // noordhoff
	}
	
	
	private void saveCurrentState_stap1() {
		if( ! memento.isEindtoetsVerzegeld())
		{	boolean old = goon;
			try { 
				goon = false;
				states[currentActiviteit][currentOpdracht] = entry.getState();
			} finally {
				goon = old;
			}
		}

		if(scoresEnabled()) 
		{
			fetchScores();
		}
	}

	private void fetchScores() {
		int scoreCorrected = entry.getScore();
		
		scores[currentActiviteit][currentOpdracht] = scoreCorrected;
		isCorrect[currentActiviteit][currentOpdracht] = Boolean.TRUE == entry.isCorrect();
		if (objectives != null)
			scoresObjectives[currentActiviteit][currentOpdracht] = entry.getScoreObjectives();
	}	
	
	public void kijkToetsNa()
	{
		incrAantalNakijken(currentActiviteit);
		int opdrachtNr = currentOpdracht;
		pause();
		ScoreNavIF source = entry.scoreNav;
		for (int j = 0; j < aantalOpdrachten[currentActiviteit]; j++)
		{
			gotoOpdracht(j);
			entry.kijkNa();
			entry.zetNagekeken(true);
			fetchScores();
			setButtonCorrect(buttons.get(j), isCorrect[currentActiviteit][j], j);
			source.setItemScore(j, scores[currentActiviteit][j]);
		}
		//entry.zetOpdrachtPlusState(opdrachten[currentActiviteit][currentOpdracht], !(gekoppeldeOpdrachten || globalParam), states[currentActiviteit][currentOpdracht]);
		source.setTotaalScore((int)getScore());
		gotoOpdracht(opdrachtNr);
		unpause();
		
//		if (mode == ZELFTOETS || mode == EINDTOETS) // if (mode == 2 || mode == 3)
//		{
//			if (mode == 2 && zelftoetsGeenCorr)
//			{
//				// laatste kans op update sessiontime
//				if (!zelftoetsNagekeken)
//				{
//					entry.sessionStop();
//					times[currentActiviteit][currentOpdracht] = entry.getSessionTime();
//				}
//				zetAfdekPanelLeeg(true);
//			}
//			zelftoetsNagekeken = true;
//			nakijkKnop.setEnabled(lessonMode.equals("review") || !zelftoetsGeenCorr);
//			scoresObjectivesKnop.setEnabled(true);//goed? nodig?
//			vorigeKnop.setVisible(vorigeKnopZichtbaar || !bolletjesZichtbaar && zelftoetsNagekeken);

//			totaal = Math.max(0, totaal - (Math.max(0, aantalNakijken[currentActiviteit] - 1)) * nakijkStraf);
//			aantalNakijkLabel.setText("" + aantalNakijken[currentActiviteit] + " keer nagekeken");
//			if (aantalNakijken[currentActiviteit] > 0 && !zelftoetsGeenCorr)
//				aantalNakijkLabel.setVisible(true);
	//	}
//		activiteitScoreLabels[currentActiviteit].setText(WiskOpdr.rb.getString("score") + totaal);
//		if (aantalActiviteiten == 1)
//		{	activiteitScoreLabels[currentActiviteit].setText(WiskOpdr.rb.getString("totaal") + totaal);
//			if(voortgang)
//				activiteitScoreLabels[0].setText(WiskOpdr.rb.getString("voortgang") + bepaalVoortgangPercentage(currentActiviteit, currentOpdracht) + "%");
//		}

//		if (mode == OEFENEN || mode == OEFENEN_STRAFPUNTEN)
//		{
//			WiskOpdr.setLMSScore();
//			WiskOpdr.setLMSState();
//			setMWScoreLabel();
//		}
	}
	
	
	private void incrAantalNakijken(int i) {
		if(aantalNakijken == null) aantalNakijken = new int[aantalActiviteiten]; // lazy initialization
		if(i >= 0 && i < aantalActiviteiten) 
			aantalNakijken[i] ++;		
	}
	
	private int getAantalNakijken(int i) {
		if(aantalNakijken == null) return 0;
		if(i < 0 && i >= aantalActiviteiten) return 0;
		return aantalNakijken[i];
	}
	
	
	
	/**
	 * Is er state bij alle andere opdrachten van deze activiteit? Behalve de opgegeven opdrNr, die heeft zeker state!
	 * @param actNr
	 * @param opdrNr
	 * @return true bij state
	 */
	private boolean suspendDataCompleted(int actNr,	int opdrNr) {
		HashMap<String, Object>[] actState = states[actNr];
		if(actState == null)
			return false;
		int aantal = aantalOpdrachten[actNr];
		for(int j = 0; j < aantal; j++ )
		{
			if(j != opdrNr && actState[j] == null)
				return false;
		}
		return true;
	}

	public void close() {
		memento.close();
		memento = null;
	}

	@Override
	public FormuleKeyboardIF getKeyboard() {
		return entry.getKeyboard();
	}

	@Override
	public FormuleClipboardIF getFormuleClipboard() {
		return entry.getClipboard();
	}

	public void gotoOpdracht(final int opdracht) {
		saveCurrentState();
		if(scoresVisible())
			setButtonCorrect(buttons.get(currentOpdracht), isCorrect[currentActiviteit][currentOpdracht], currentOpdracht);

		removeButtonCursor(buttons.get(currentOpdracht));
		currentOpdracht = opdracht;
		setButtonCursor(buttons.get(currentOpdracht));

		entry.clearContentPanel();
		if (states[currentActiviteit][currentOpdracht] == null)
			entry.zetVolgendeOpdracht(opdrachten[currentActiviteit][currentOpdracht]);
		else
			entry.zetOpdrachtPlusState(opdrachten[currentActiviteit][currentOpdracht], states[currentActiviteit][currentOpdracht]);
	}
	
	public int getCurrentOpdracht() { 
		return currentOpdracht;	
	}
	
	public int getCurrentActiviteit() {
		return currentActiviteit;
	}
	
	public int getAantalOpdrachten() {
		return aantalOpdrachten[currentActiviteit];
	}

	public int getAantalOpdrachten(int act) {
		return aantalOpdrachten[act];
	}
	
	public int getAantalActiviteiten() {
		return aantalActiviteiten;
	}

	public int getAantalBeantwoord() {
		int len = getAantalOpdrachten();
		int totaal = 0;
		for(int i = 0; i < len; i++ )
			if( 	states[currentActiviteit] != null &&
					states[currentActiviteit][i] != null || scores[currentActiviteit][i]!= 0) totaal++;
		return totaal;
	}

	@Override
	public void gotoOpdracht(int i, ScoreNavIF source) 
	{
		if (i == currentOpdracht) return;
		int oldOpdr = currentOpdracht;
		
		gotoOpdracht(i);
		
		if (source != null)
		{ 	source.setBeantwoord(getAantalBeantwoord());
			source.setOpdracht(getCurrentOpdracht());
			source.setTotaalScore((int) getScore()); 
			source.setItemScore(oldOpdr, getItemScores()[oldOpdr]);
		}
		
		if (DWOplayer.PARAMETERS.isNavTitle())
			entry.setTitle("Vraag " + (getCurrentOpdracht()+1) + " van " + getAantalOpdrachten());
	}

	public int[] getMaxScores() {
		return scoresMax[currentActiviteit];
	}
	
	public int[] getItemScores() {
		return scores[currentActiviteit];
	}
	
	public boolean zijnObjectivesAanwezig()
	{
		return objectivesAanwezig;
	}
	
	public void openObjectivesPanel()
	{
		/**
		 * Maakt panel met deelscores zichtbaar mbv een popup-venster
		 */
		    int aantalDiagrammen = 0;
	        for(int k = 0; k < objectives.length; k++)
	        {	int somObjective = 0;
	        	for(int i = 0; i < scoresMaxObjectives.length; i++)
	        		for(int j = 0; j < scoresMaxObjectives[i].length; j++)
	        		{	try{
	        			for(int l = 0; l < scoresMaxObjectives[i][j][k].length; l++)
	        				somObjective += scoresMaxObjectives[i][j][k][l];
	        			}
	        			catch(Exception e){somObjective = 0;
	        			}
	        		}
	        	if(somObjective > 0) aantalDiagrammen++;
	        }
			scoresObjectivesDialog = new DialogBox(true); //evt argument true meegeven voor autohide.
	        //Misschien geen dialogbox maar een popup. Moet in elk geval ook weer te sluiten zijn.
	        //scoresObjectivesDialog = new DialogBox(true);
	        scoresObjectivesDialog.setText("Deelscores"); //TODO: woord uit textbundle halen.
	       // scoresObjectivesDialog = new DialogBox(this,"deelscores", true);
	        scoresObjectivesPanel = new ScoresObjectivesPanel(getScoresObjectivesForDiagram());
//	        if(aantalDiagrammen < 4)
//	        	scoresObjectivesPanel.setBounds(0, 0, 400 * aantalDiagrammen, 350);
//	        else 
//	        	scoresObjectivesPanel.setBounds(0, 0, 1200, 700);
	        scoresObjectivesDialog.add(scoresObjectivesPanel.asWidget());
	        //scoresObjectivesDialog.setSize(scoresObjectivesPanel.getSize());
	        int width = 1200;
			int height = 730;
			if(aantalDiagrammen < 4)
			{	
				width = 400 * aantalDiagrammen;
				height = 380;
			}
	        scoresObjectivesDialog.setWidth(width + "px");
	        scoresObjectivesDialog.setHeight(height + "px");
	        scoresObjectivesDialog.show();
	       // scoresObjectivesDialog.setVisible(true);
	}
	
	/**
	 * Verzamelt de maximale scores per leerdoel, de gerealiseerde scores per
	 * leerdoel en de leerdoelen zelf en geeft deze terug tbv het diagram.
	 */
	public HashMap<String, Object> getScoresObjectivesForDiagram()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		if (objectives == null)
			return h;
		
		int[][] totaalScoreObjectives = null;
		int[][] totaalMaxObjectives = null;
		double[][] scoresPercObjectives = null;

		totaalScoreObjectives = new int[objectives.length][];
		totaalMaxObjectives = new int[objectives.length][];
		scoresPercObjectives = new double[objectives.length][];

		for (int i = 0; i < objectives.length; i++)
		{
			totaalScoreObjectives[i] = new int[objectives[i].length];
			totaalMaxObjectives[i] = new int[objectives[i].length];
			scoresPercObjectives[i] = new double[objectives[i].length];
		}

		for (int i = 0; i < aantalActiviteiten; i++)
		{ 	for (int j = 0; j < aantalOpdrachten[i]; j++)
			{	if(scoresObjectives[i][j] != null)
					for (int k = 0; k < objectives.length && k < scoresObjectives[i][j].length; k++)
					{	if (scoresObjectives[i][j][k] != null)
							for (int l = 0; l < objectives[k].length && l < scoresObjectives[i][j][k].length; l++)
								totaalScoreObjectives[k][l] += scoresObjectives[i][j][k][l];
					}	
			}

			for (int j = 0; j < aantalOpdrachten[i]; j++)
			{
				for (int k = 0; k < objectives.length && k < scoresMaxObjectives[i][j].length; k++)
				{
					for (int l = 0; l < objectives[k].length && l < scoresMaxObjectives[i][j][k].length; l++)
					{
						if (scoresMaxObjectives[i][j][k] != null)
							totaalMaxObjectives[k][l] += scoresMaxObjectives[i][j][k][l];
					}
				}
			}
		}

		h.put("objectives", objectives);
		h.put("totaalScoreObjectives", totaalScoreObjectives);
		h.put("totaalMaxObjectives", totaalMaxObjectives);
		h.put("categorieString", categorieString);

		return h;
	}

	public void reloadOpdracht(int opdracht, ScoreNavIF source) {
		saveCurrentState();
		removeButtonCursor(buttons.get(currentOpdracht));
		boolean randomize;
		if (opdracht < 0) 
		{	randomize = true; // Alles opnieuw, dus ook nieuwe random variabelen
			currentOpdracht = 0;
			for (opdracht = 0; opdracht < aantalOpdrachten[currentActiviteit]; opdracht ++)
			{	clearState(opdracht, source);
				setButtonCorrect(buttons.get(opdracht), isCorrect[currentActiviteit][opdracht], opdracht);
			}
			
			if (getAantalNakijken(currentActiviteit) > 0)
				aantalNakijken[currentActiviteit] = 0;
			
			//reset totaalscore en keer nagekeken
			entry.scoreNav.setTotaalScoreLabel(getTotaalScore());
			entry.scoreNav.setKeerNagekekenLabel(getKeerNagekeken());
		} 
		else
		{	
			randomize = false; // TODO moet wel, tenzij er nu geen enkele state meer is. Peter vragen?
			clearState(opdracht,source);
			setButtonCorrect(buttons.get(opdracht), isCorrect[currentActiviteit][opdracht], opdracht);
		}
		source.setBeantwoord(getAantalBeantwoord());
		source.setTotaalScore((int) getScore()); 
		
		//setButtonCorrect(buttons.get(currentOpdracht), isCorrect[currentActiviteit][currentOpdracht], currentOpdracht);

		setButtonCursor(buttons.get(currentOpdracht));

		entry.clearContentPanel();
		if (states[currentActiviteit][currentOpdracht] == null)
		{
			if(randomize)
				entry.zetOpdracht(opdrachten[currentActiviteit][currentOpdracht]);
			else
				entry.zetVolgendeOpdracht(opdrachten[currentActiviteit][currentOpdracht]);
		}
		else
			entry.zetOpdrachtPlusState(opdrachten[currentActiviteit][currentOpdracht], states[currentActiviteit][currentOpdracht]);
		
		if(DWOplayer.PARAMETERS.isNavTitle())
			entry.setTitle("Vraag " + (getCurrentOpdracht()+1) + " van " + getAantalOpdrachten());
	}

	public void clearState(int opdracht, ScoreNavIF source) {
		isCorrect[currentActiviteit][opdracht] = false;
		scores[currentActiviteit][opdracht] = 0;
		states[currentActiviteit][opdracht] = null;
		if(strafpunten != null) 
			strafpunten[currentActiviteit][opdracht] = 0;
		source.setItemScore(opdracht, 0);
	}

	
	public int getMode() {
		return mode;
	}

	@Override
	public String getLearnerId() {
		return memento.getLearnerId();
	}

	@Override
	public String getLearnerName() {
		return memento.getLearnerName();
	}

	@Override
	public CssColor getBackground() {
		return CssColor.make("white");
	}

	@Override
	public String getUUID() {
		return entry.getUnitId() + "-" + getCurrentOpdracht() + "-" + getWidgetId();
	}

	protected String getWidgetId() {
		return "XXXXXXXX";
	}

		
	/* Event handling:
	 * on top: OpdrNavIF.addCBookEventListener(command, listener);
	 * en      OpdrNavIf.fireEvent(event);
	 * 
	 * 
	 * lowestlevel
	 * 		   BUS.addHandlerToSource(TYPE, listener, UUID_of_listener + command)
	 * 		   forall dest in destinations of command;
	 * 		   BUS.fireEventFromSource(event, UUID_dest + command);
	 * 
	 */
	
	
	
	@Override
	public HandlerRegistration addCBookEventListener(String command,
			CBookEventListener listener) {
		return null;
	}
	
	

	@Override
	public void fireEvent(CBookEvent event) {
	}

	public static EventBus getEventBus() {
		return BUS;
	}

	@Override
	public LessonMode getLessonMode() {
		return memento.getLessonMode();
	}

	@Override
	public Role getRole() {
		return memento.getRole();
	}

	public boolean pause(boolean b) {
		boolean old = goon;
		goon = !b;
		pause();
		return old;
		
	}
}
