package nl.uu.fi.dwo.mobile.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleobjects.TouchButton;
import nl.uu.fi.dwo.interaction.client.FormuleClipboardIF;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Role;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.DWOLogger;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;

import fi.wiskopdr.text.Text;

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
	public static String[][] misconceptions;
	private static String[] mccCategorieString;
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
	private int maxAantalOnBar = 15;
	private int currentShift = 0;

	private boolean[][] buttonsEnabled;

	private HashMap<String, Object>[][] opdrachten;
	private HashMap<String, Object>[][] states;
	private int[][] scoresMax;
	private int[][] scores;
	/**
	 * Scores (per activiteit, per opdracht/pagina, vgl. scores) die getoond worden 
	 * in een nagekeken zelftoets. Als een antwoord gewijzigd is na
	 * het nakijken van de zelftoets, kan de score van de pagina met de huidige 
	 * ingevulde antwoorden anders zijn dan die opgeslagen in scoresZelftoets.
	 * De score bij de huidige ingevulde antwoorden moet pas getoond
	 * worden als op de nakijk-knop wordt gedrukt.
	 */
	private int[][] scoresZelftoets;
	/**
	 * De high score van de zelftoets. Wordt in memento opgeslagen,
	 * en ook hier zodat bij het afsluiten de high score kan
	 * worden doorgegeven t.b.v. activiteitenoverzicht met progress bar.
	 */
	private int zelftoetsHighScore;
	private boolean[][] isCorrect;
	/**
	 * Correctheid (per activiteit, per opdracht/pagina, vgl. isCorrect) die getoond wordt
	 * in een nagekeken zelftoets dmv een groen of rood bolletje.
	 * Als een antwoord gewijzigd is na het nakijken van de zelftoets, 
	 * kan de correctheid van de pagina met de huidige
	 * ingevulde antwoorden anders zijn dan die opgeslagen in isCorrectZelftoets.
	 * De correctheid bij de huidige ingevulde antwoorden moet pas getoond
	 * worden als op de nakijk-knop wordt gedrukt.
	 */
	private boolean[][] isCorrectZelftoets;
	/**
	 * Een array (per activiteit, per opdracht/pagina) die aangeeft of er voor de 
	 * betreffende opdracht een zelftoets nakijken pending is. Als de zelftoets is nagekeken
	 * en een opdracht wordt daarna voor het eerst bezocht, dan is pending true. Daarna false.
	 */
	private boolean[][] nakijkenZelftoetsPending;
	
	private boolean[][] opdrachtenCorrect;
	private int[] aantalNakijken; // per activiteit

	private int[][][][] scoresMaxObjectives;
	private int[][][][] scoresObjectives;
	private int[][][][] possibleMisconceptions;
	private int[][][][] measuredMisconceptions;

	private MyDialog scoresObjectivesDialog;
	private ScoresObjectivesPanel scoresObjectivesPanel;
	private MyDialog viewMisconceptionsDialog;
	private ScoresObjectivesPanel viewMisconceptionsPanel;

	private int mode;
	private int[][] strafpunten;

	private int scoreMax;
	private int currentOpdracht = 0;
	private int currentActiviteit = 0;
	private ArrayList<TouchButton> buttons = new ArrayList<TouchButton>();
	private Memento memento;
	private final static EventBus BUS = DWOplayer.PARAMETERS.getEventBus();

	static private Prepare prepare = DWOplayer.PARAMETERS.getPrepareInstance();

	public static class Prepare
	{
		void defer(ScheduledCommand cmd)
		{
			Scheduler.get().scheduleDeferred(cmd);
		}

		@Deprecated
		void immediate(ScheduledCommand cmd)
		{
			cmd.execute();
		}
	}

	public static class MC2Prepare extends Prepare
	{
		private static final CBookEvent STOP = new CBookEvent("stop");

		void defer(final ScheduledCommand cmd)
		{
			BUS.fireEvent(STOP); // ask CBook Widgets to send 'getState' events.
			Timer t = new Timer()
			{
				@Override
				public void run()
				{
					cmd.execute();
				}
			};
			t.schedule(100);
		}
	}

	@Deprecated
	public static void immediate(ScheduledCommand cmd)
	{
		prepare.immediate(cmd);
	}

	public static void defer(ScheduledCommand cmd)
	{
		prepare.defer(cmd);
	}

	public OpdrNav()
	{
	};

	public void init(HashMap<String, Object> launchData, ViewModuleViewImpl ev, Memento memento)
	{
		this.entry = ev;
		this.memento = memento;
		memento.setUnload(this);
		aantalActiviteiten = Integer.parseInt((String) launchData.get("aantalActiviteiten"));
		activiteitNamen = new String[aantalActiviteiten];
		aantalOpdrachten = new int[aantalActiviteiten];
		buttonsEnabled = new boolean[aantalActiviteiten][];

		mode = Integer.parseInt((String) launchData.get("mode"));

		ObjectMap wrap = JSONUtilities.wrapMap(launchData);
		if (wrap.containsKey("instellingen"))
		{
			instellingen = wrap.getObjectMap("instellingen");
			if (instellingen.containsKey("aftrekCorrectieZelftoets"))
			{
				aftrekCorrectieZelftoets = instellingen.getInt("aftrekCorrectieZelftoets");
			}
			else
			{
				aftrekCorrectieZelftoets = 5; // the old default
			}
		}
		else
		{
			aftrekCorrectieZelftoets = 5; // the old default
		}

		maxAantalOpdrachten = 1;
		for (int i = 0; i < aantalActiviteiten; i++)
		{
			activiteitNamen[i] = (String) launchData.get("activiteit_" + (i + 1));
			String aantalString = (String) launchData.get("aantalOpdrachten_" + (i + 1));
			aantalOpdrachten[i] = Integer.parseInt(aantalString);
			maxAantalOpdrachten = Math.max(maxAantalOpdrachten, aantalOpdrachten[i]);
			buttonsEnabled[i] = new boolean[aantalOpdrachten[i]];
			for (int j = 0; j < aantalOpdrachten[i]; j++)
				buttonsEnabled[i][j] = true;
		}

		opdrachten = new HashMap[aantalActiviteiten][maxAantalOpdrachten];
		scoresMax = new int[aantalActiviteiten][maxAantalOpdrachten];
		scores = new int[aantalActiviteiten][maxAantalOpdrachten];
		scoresZelftoets = new int[aantalActiviteiten][maxAantalOpdrachten];
		isCorrect = new boolean[aantalActiviteiten][maxAantalOpdrachten];
		isCorrectZelftoets = new boolean[aantalActiviteiten][maxAantalOpdrachten];
		nakijkenZelftoetsPending = new boolean[aantalActiviteiten][maxAantalOpdrachten];
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
		if (misconceptions != null)
		{
			possibleMisconceptions = new int[aantalActiviteiten][maxAantalOpdrachten][misconceptions.length][];
			measuredMisconceptions = new int[aantalActiviteiten][maxAantalOpdrachten][misconceptions.length][];
			for (int k = 0; k < aantalActiviteiten; k++)
				for (int j = 0; j < maxAantalOpdrachten; j++)
					for (int i = 0; i < misconceptions.length; i++)
					{
						possibleMisconceptions[k][j][i] = new int[misconceptions[i].length];
						measuredMisconceptions[k][j][i] = new int[misconceptions[i].length];
					}
		}

		if (mode == OEFENEN_STRAFPUNTEN)
			strafpunten = new int[aantalActiviteiten][maxAantalOpdrachten];

		for (int i = 0; i < aantalActiviteiten; i++)
		{
			for (int j = 0; j < aantalOpdrachten[i]; j++)
			{
				Object object = launchData.get("opdracht_" + (i + 1) + "_" + (j + 1));
				if (!(object instanceof HashMap))
				{
					object = new HashMap(); // XXX Wat is de minimum hashmap hier?
					// minimaal: hasAntwoordVak = Boolean
					java.util.logging.Logger.getLogger("OpdrNav").severe("Opdracht " + (j + 1) + " geen map ");
				}
				opdrachten[i][j] = (HashMap<String, Object>) object;
				HashMap<String, Object> opdrachtInfo = opdrachten[i][j];
				ObjectMap ht = JSONUtilities.wrapMap(opdrachtInfo);
				if (ht != null && objectives != null && ht.containsKey("scoreMaxObjectives"))
				{
					ObjectList scoreList = ht.getObjectList("scoreMaxObjectives");
					scoresMaxObjectives[i][j] = new int[scoreList.size()][];
					for (int k = 0; k < scoreList.size(); k++)
					{
						try
						{
							scoresMaxObjectives[i][j][k] = scoreList.getIntArray(k);
						}
						catch (Exception e)
						{
						}
					}

					// scoresMaxObjectives[i][j] = (int[][]) ht.get("scoreMaxObjectives");
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
		if (objectives != null)
			memento.getScoresObjectives(scoresObjectives);
		if (misconceptions != null)
		{
			memento.getPossibleMisconceptions(possibleMisconceptions);
			memento.getMeasuredMisconceptions(measuredMisconceptions);
			entry.setMeasuredMisconceptions(measuredMisconceptions);
		}

		contentPanel = new FlowPanel();
		contentPanel.getElement().getStyle().setMargin(0, Unit.PX);
		
		if (mode == ZELFTOETS)
		{
			if (memento.getScoresZelftoets() != null)
				setScoresZelftoets(memento.getScoresZelftoets());
			else
				setScoresZelftoets(scores);
			
			if (memento.isCorrectZelftoets() != null)
				setIsCorrectZelftoets(memento.isCorrectZelftoets());
			else
				setIsCorrectZelftoets(isCorrect);
			
			if (memento.nakijkenZelftoetsPending() != null)
				setNakijkenZelftoetsPending(memento.nakijkenZelftoetsPending());
			else
				setNakijkenZelftoetsPending(nakijkenZelftoetsPending);
		}

		setOpdrachten(currentActiviteit); // kan dat nu al? of anders bij
											// setchanged testen op
											// buttons.get() != null

		// initializeer bezocht
		boolean[][] bezocht;
		bezocht = new boolean[getAantalActiviteiten()][];
		for (int j = 0; j < getAantalActiviteiten(); j++)
		{
			bezocht[j] = new boolean[getAantalOpdrachten(j)]; // all false
		}
		// bezocht[0][0] = true; // eerste niet standaard bezocht zetten,
		// anders kun je nooit checken of hij al eerder bezocht is
		entry.bezocht = bezocht;

		
		memento.getBezocht(entry.bezocht);
		entry.zelftoetsNagekeken = memento.getZelftoetsNagekeken();
		aantalNakijken = memento.getAantalNakijken();
		initializeScoresZelftoets();
		initializeIsCorrectZelftoets();
		initializeNakijkenZelftoetsPending();

		final HashMap<String, Object> state = states[currentActiviteit][currentOpdracht];

		entry.scoreNav.setAantalOpdrachten(getAantalOpdrachten(), getMaxScores(), getCurrentOpdracht());

		if (state == null)
		{
			// logger.info("zetOpdracht no state");
			entry.zetOpdracht(opdrachten[currentActiviteit][currentOpdracht]);
		}
		else
		{
			// logger.info("zetOpdracht plus state");
			entry.zetOpdrachtPlusState(opdrachten[currentActiviteit][currentOpdracht], state);
		}
	}

	/**
	 * Initialiseer scores voor zelftoets, zo mogelijk met
	 * de scores voor zelftoets die memento heeft opgeslagen.
	 * 
	 */
	private void initializeScoresZelftoets()
	{
		if (getScoresZelftoets() != null)
			scoresZelftoets = getScoresZelftoets();
		else
			scoresZelftoets = new int[getAantalActiviteiten()][getMaxAantalOpdrachten()];
	}
	
	/**
	 * Initialiseer isCorrect voor zelftoets, zo mogelijk met
	 * de isCorrect voor zelftoets die memento heeft opgeslagen.
	 * 
	 */
	private void initializeIsCorrectZelftoets()
	{
		if (isCorrectZelftoets() != null)
			isCorrectZelftoets = isCorrectZelftoets();
		else
			isCorrectZelftoets = new boolean[getAantalActiviteiten()][getMaxAantalOpdrachten()];
	}
	
	/**
	 * Initialiseer nakijkenZelftoetsPending, zo mogelijk met
	 * de nakijkenZelftoetsPending die memento heeft opgeslagen.
	 * 
	 */
	private void initializeNakijkenZelftoetsPending()
	{
		if (nakijkenZelftoetsPending() != null)
			nakijkenZelftoetsPending = nakijkenZelftoetsPending();
		else
			nakijkenZelftoetsPending = new boolean[getAantalActiviteiten()][getMaxAantalOpdrachten()];
	}
	
	public boolean isTemptoetsVerlopen()
	{
		return memento.isTempotoetsVerlopen();
	}

	public int getTempotoetsSecondsLeft()
	{
		return memento.getTempotoetsSecondsLeft();
	}

	public boolean isVerzegeld()
	{
		return memento.isEindtoetsVerzegeld();
	}

	public static void setObjectives(String[][] o)
	{
		objectives = o;
	}

	public static void setCategorieString(String[] c)
	{
		categorieString = c;
	}

	public static void setMisconceptions(String[][] m)
	{
		misconceptions = m;
	}

	public static void setMccCategorieString(String[] c)
	{
		mccCategorieString = c;
	}

	public Panel getAsPanel()
	{
		mainPanel = new FlowPanel();
		//mainPanel.getElement().addClassName("opdrnav-bottombar");
		contentPanel = new FlowPanel();
		//contentPanel.getElement().addClassName("opdrnav-bottombar");
		contentPanel.getElement().getStyle().setMargin(5, Unit.PX);

		mainPanel.add(contentPanel);

		lb_activiteiten = new ListBox();
		// We bieden (in elk geval tijdelijk) geen support voor verschillende niveaus.
		// Als we de verschillende niveaus er helemaal uit halen, kan de code op
		// den duur ook flink versimpeld.. Maar dat is iets voor langere termijn.
		// if (aantalActiviteiten == 1)
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

	/**
	 * For some reason opdrachtenCorrect[][] in getOpdrachtCorrect() is not set
	 * and isCorrect[][] is.
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	public boolean isCorrect(int i, int j)
	{
		return isCorrect[i][j];
	}

	public boolean getOpdrachtCorrect(int i, int j)
	{
		return opdrachtenCorrect[i][j];
	}

	public void setChanged(boolean fout) // FIXME Trifork: hier safepoint?
	{
		Boolean check = entry.isCorrect();
		boolean correct = Boolean.TRUE.equals(check);
		isCorrect[currentActiviteit][currentOpdracht] = correct;
		opdrachtenCorrect[currentActiviteit][currentOpdracht] = correct;

		// met nieuwe implementatie strafpunten: niet meer hier regelen maar in
		// losse antwoordvakken.
		// boolean fout is ook niet meer nodig.
		// if(strafpunten != null && mode == OEFENEN_STRAFPUNTEN && fout)
		// strafpunten[currentActiviteit][currentOpdracht] += foutStraf;

		// save current state voor het zetten van de buttons, want hierin wordt
		// de score gezet
		// (ging bijv. mis voor geval je het goede antw weghaalt in
		// uitklapvak/popup en daarna vak/popup sluit; button werd weer rood,
		// maar score bleef staan)
		saveCurrentState();

		if (buttons != null && buttons.size() > currentOpdracht)
		{
			// logger.fine("setChanged zet Button " + currentOpdracht + "
			// correct; correct = " + correct);

			// niet voor toets!
			if (scoresVisible())
			{
				setButtonCorrect(buttons.get(currentOpdracht), correct, currentOpdracht);

				if (entry.isTempotoets() && entry.isAllCorrect())
				{
					entry.setTempotoetsLocked();
				}
			}
		}
		// saveCurrentState(); // naar hierboven verplaatst, want er wordt score
		// gezet
		// entry.stelNavigatieIn(); gebeurt al op een andere plek; niet hier
		// anders gaat oefenen met geen correctie eerdere pagina's mis na
		// kijkNa()
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
		
		if(aantalOpdrachten[currentActiviteit]>maxAantalOnBar)
		{	
			TouchButton shiftButtonLeft = new TouchButton();
			shiftButtonLeft.setStylePrimaryName("shiftBtn");
			addScrollButtonHandler(shiftButtonLeft,-1);
			shiftButtonLeft.setText("◀◀");
			fp_opdrachten.add(shiftButtonLeft);
			Label spaceStart = new Label();
			spaceStart.setStylePrimaryName("spaceShiftLabel");
			spaceStart.setText("...");
			fp_opdrachten.add(spaceStart);
		}
		
		for (int j = 0; j < aantalOpdrachten[index]; j++)
		{
			setButton(j);
			if (mode == ZELFTOETS) 
				setButtonCorrectZelftoets(buttons.get(j), isCorrectZelftoets[index][j], j);
			else
				setButtonCorrect(buttons.get(j), isCorrect[index][j], j);
		}
		
		if(aantalOpdrachten[currentActiviteit]>maxAantalOnBar)
		{		
			Label spaceEnd = new Label();
			spaceEnd.setStylePrimaryName("spaceShiftLabel");
			spaceEnd.setText("...");
			fp_opdrachten.add(spaceEnd);
			TouchButton shiftButtonRight = new TouchButton();
			shiftButtonRight.setStylePrimaryName("shiftBtn");
			addScrollButtonHandler(shiftButtonRight,1);
			shiftButtonRight.setText("▶▶");
			fp_opdrachten.add(shiftButtonRight);
		}
		fp_opdrachten.getElement().getStyle().setFloat(Style.Float.LEFT);
		contentPanel.add(fp_opdrachten);
	}

	private void setButton(int j)
	{
		TouchButton button = new TouchButton();
		Label space = new Label();
		// enable scores, geen toets en scoreMax > 0
		if (!geefNoScore(currentActiviteit, j))
		{
			TouchDown handler = new TouchDown(j);
			button.addDomHandler(handler, MouseOverEvent.getType());
			button.addDomHandler(handler, com.google.gwt.event.dom.client.TouchEndEvent.getType());
		}

		button.setStylePrimaryName("scoreBtn");
		space.setStylePrimaryName("spaceLabel");
		final int button_id = j;
		if (geefNoScore(currentActiviteit, j))
		{
			button.addStyleDependentName("max0");
		}
		if (!buttonsEnabled[currentActiviteit][j])
		{
			button.addStyleDependentName("disabled");
		}
		
		String nul = j<9 ? "0" : "";
		button.setText("" + nul + (j + 1) + "");
		space.setText("−");
		if (currentOpdracht == j)
		{
			setButtonCursor(button);
		}
		addButtonHandler(button, j);
		
		buttons.add(button);
		if(aantalOpdrachten[currentActiviteit]>1 && j>currentShift-1 && j<aantalOpdrachten[currentActiviteit] && (j<maxAantalOnBar-2+currentShift || aantalOpdrachten[currentActiviteit]<maxAantalOnBar+1))
			fp_opdrachten.add(button);
		if(j>currentShift-1 && j<aantalOpdrachten[currentActiviteit]-1 && (j<maxAantalOnBar-3+currentShift || aantalOpdrachten[currentActiviteit]<maxAantalOnBar+1))
			fp_opdrachten.add(space);
		
	}

	/**
	 * wordt gebruikt aan het begin, als de pagina gestart wordt.
	 * 
	 * @return
	 */
	private boolean scoresVisible()
	{
		boolean visible = false;
		
		
		if (memento.isReview())
			visible = true;
		else
		if (entry.getZelftoetsNagekeken())
			visible = true;
		else if (mode == EINDTOETS && memento.isEindtoetsVerzegeld())
			visible = true;
		else if (mode == OEFENEN || mode == OEFENEN_STRAFPUNTEN)
			visible = true;
		
		return visible;
	}

	/**
	 * Wordt in saveCurrentState_stap1() gebruikt om bij verandering de scores
	 * van de huidige activiteit en opdracht automatisch bij te werken. De 'kijk
	 * na'-knop voor zelftoets nakijken doet het zelf voor de hele activiteit.
	 * 
	 * @return
	 */
	private boolean scoresEnabled()
	{
		boolean enabled = false;

		if (mode == OEFENEN)
			enabled = true;
		else if (mode == OEFENEN_STRAFPUNTEN)
			enabled = true;
		else if (mode == EINDTOETS && !memento.isEindtoetsVerzegeld())
			enabled = true;
		else if (mode == ZELFTOETS) // altijd voor een zelftoets. We willen altijd de scores weten. Voor het tonen worden scoresZelftoets gebruikt. En pas getoond als nagekeken
			enabled = true;

		return enabled;
	}

	public TouchButton getButton(int j)
	{
		try
		{
			return buttons.get(j);
		}
		catch (Exception e)
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
				if (buttonsEnabled[currentActiviteit][button_id])
				{
					entry.p();
					ScheduledCommand run = new ScheduledCommand()
					{

						@Override
						public void execute()
						{
							gotoOpdracht(button_id, entry.scoreNav);
							entry.v();
						}
					};
					defer(run);
				}
			}

		});
	}
	
	
	private void addScrollButtonHandler(TouchButton button, int s)
	{
		final int shift = s;
		button.addTouchStartHandler(new TouchStartHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				currentShift+=shift;
				if(currentShift<0)
					currentShift = 0;
				if(currentShift>aantalOpdrachten[currentActiviteit] - maxAantalOnBar+2)
					currentShift = aantalOpdrachten[currentActiviteit] - maxAantalOnBar+2;
					
				System.out.println("currentShift"+currentShift);
				setOpdrachten(currentActiviteit);
					
				
			}

		});
	}

	private Timer popupTimer;
	private int aftrekCorrectieZelftoets;

	private void schedule(final int index, boolean touch)
	{
		if (popupTimer != null)
		{
			popupTimer.cancel();
			popupTimer.run();
		}
		if (touch && index != getCurrentOpdracht())
			return;

		if (geefNoScore(currentActiviteit, index) || !buttonsEnabled[currentActiviteit][index] || !scoresVisible())
			return;

		final TouchButton btn = buttons.get(index);
		final int score;
		
		if (mode == ZELFTOETS)
			score = getScoresZelftoets(getCurrentActiviteit(), index);
		else
			score = getScoresHuidigeActiviteit()[index];
		
		if (score == 0)
			return; // dit is het enige waarvoor score wordt gebruikte; De tooltip wordt gezet in setButtonCorrect() en setButtonCorrectZelftoets() 
		
		popupTimer = new Timer()
		{

			@Override
			public void run()
			{
				popupTimer = null;
				btn.setStyleDependentName("popupTime", false);
				String nul = index<9 ? "0" : "";
				btn.setText(nul + Integer.toString(index + 1));
				// logger.info("timer for "+ index + " fired");
			}
		};
		//btn.setStyleDependentName("popupTime", true);
		//btn.setText("score\n"+Integer.toString(score));
		popupTimer.schedule(2000);
	}

	class TouchDown implements MouseUpHandler, TouchEndHandler, MouseOverHandler
	{
		private int index;

		public TouchDown(int index)
		{
			this.index = index;
		}

		@Override
		public void onTouchEnd(com.google.gwt.event.dom.client.TouchEndEvent event)
		{
			schedule(index, true);
		}

		@Override
		public void onMouseUp(MouseUpEvent event)
		{
			schedule(index, false);
		}

		@Override
		public void onMouseOver(MouseOverEvent event)
		{
			schedule(index, false);
		}

	}

	/**
	 * 
	 * @param button
	 * @param b
	 * @param opdrNr
	 */
	private void setButtonCorrect(TouchButton button, boolean b, int opdrNr)
	{
		if (geefNoScore(currentActiviteit, opdrNr))
		{
			button.setStyleDependentName("max0", geefNoScore(currentActiviteit, opdrNr));
			return;
		}
		button.setStyleDependentName("max0", false);
		if (scoresVisible())
		{
			button.setStyleDependentName("correct", b);
			String value = String.valueOf(getScores()[getCurrentActiviteit()][opdrNr]);
			int correctie = getItemCorrectie(opdrNr);
			if (correctie > 0)
			{
				value = value + "+" + correctie;
			}
			else if (correctie < 0)
			{
				value = value + correctie;
			}
			button.getElement().setPropertyString("title", value);
		}
	}

	/**
	 * 
	 * @param button
	 * @param b
	 * @param opdrNr
	 */
	private void setButtonCorrectZelftoets(TouchButton button, boolean b, int opdrNr)
	{
		if (geefNoScore(currentActiviteit, opdrNr))
		{
			button.setStyleDependentName("max0", geefNoScore(currentActiviteit, opdrNr));
			return;
		}
		button.setStyleDependentName("max0", false);
		if (scoresVisible())
		{
			button.setStyleDependentName("correct", b);
			String value = String.valueOf(getScoresZelftoets()[getCurrentActiviteit()][opdrNr]);
			int correctie = getItemCorrectie(opdrNr);
			if (correctie > 0)
			{
				value = value + "+" + correctie;
			}
			else if (correctie < 0)
			{
				value = value + correctie;
			}
			button.getElement().setPropertyString("title", value);
		}
	}

	private int getItemCorrectie(int j)
	{
		HashMap state = states[currentActiviteit][j];
		ObjectMap map = JSONUtilities.wrapMap(state);
		return getItemCorrectie(map);
	}

	private int getItemCorrectie(ObjectMap map)
	{
		if (map == null)
			return 0;
		if (map.containsKey("reviewScoreCorrectie"))
			return map.getInt("reviewScoreCorrectie");
		if (map.containsKey("reviewInteractieData"))
			return getItemCorrectie(map.getObjectMap("reviewInteractieData"));
		if (map.containsKey("interactiePanelStates"))
		{
			int s = 0;
			ObjectList list = map.getObjectList("interactiePanelStates");
			int len = list.size();
			for (int i = 0; i < len; i++)
			{
				s += getItemCorrectie(list.getObjectMap(i));
			}
			return s;
		}
		return 0;
	}

	public boolean geefNoScore(int actNr, int opdrNr)
	{
		return scoresMax[actNr][opdrNr] == 0;
	}

	public void setButtonEnabled(int j, boolean b)
	{
		if (!entry.bolletjesZichtbaar() || buttons.size() < j)
			return;
		buttonsEnabled[currentActiviteit][j] = b;
		if (b)
		{
			buttons.get(j).setStyleDependentName("disabled", false);
			if (mode == ZELFTOETS)
				setButtonCorrectZelftoets(buttons.get(j), isCorrectZelftoets[currentActiviteit][j], j);
			else
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
	 * naar 100%. Als het een zelftoets is het high score, dan wordt de
	 * high score teruggegeven.
	 */
	public double getScore()
	{
		if (entry.zelftoetsHighScore)
			return getZelftoetsHighScore();
		else
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
	}

	/**
	 * Berekent de totale score van het applet, en geeft deze terug, geschaald
	 * naar 100%.
	 */
	public double getActualScore()
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
	 * Berekent de totale score van de activiteit (over alle activiteiten (?) en
	 * opdrachten), inclusief nakijkstraf indien van toepassing.
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
					int hulp = (getAantalNakijken(i) * aftrekCorrectieZelftoets - aftrekCorrectieZelftoets); // een
																												// keer
																												// gratis
																												// nakijken.

					if (hulp < 0)
						hulp = 0;

					hulp = -hulp; // aftrek!
					int size = aantalOpdrachten[i];

					for (int j = 0; j < size; j++)
					{
						hulp += scoresZelftoets[i][j];
					}

					if (hulp > 0)
						totaalScore += hulp;
					if (getAantalNakijken(i) == 0)
						totaalScore = 0; // geen score tonen voor een niet nagekeken zelftoets

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

	/**
	 * Geef het totaal aantal punten over alle activiteiten (?)
	 * en opdrachten.
	 * 
	 * @return
	 */
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
	
	public int[] getScores(int activiteitNr)
	{
		return scores[activiteitNr];
	}

	public int getStrafpunten(int actNr, int opdrNr)
	{
		if (strafpunten != null)
		{
			try
			{
				return strafpunten[actNr][opdrNr];
			}
			catch (Exception e)
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
	private ObjectMap instellingen;

	public void saveCurrentState()
	{
		try
		{
			if (scsInUse++ == 0)
			{
				saveCurrentState0();
				scsPending = false;
			}
			else
			{
				if (goon)
					saveCurrentState_stap1(); // dit MOET altijd voor kijkna,
												// behalve in setState/getState
												// zelf
				else
				{
					GWT.log("break recursion");
				}
				scsPending = true;
			}
		}
		finally
		{
			--scsInUse;
		}
	}

	public void pause()
	{
		scsInUse++;
	}

	public void unpause()
	{
		--scsInUse;
		if (scsPending && scsInUse == 0)
			saveCurrentState();
		if (scsInUse == 0)
			goon = true;
	}

	public void unpause(boolean old)
	{
		unpause();
		goon = old;
	}

	private void saveCurrentState0()
	{
		saveCurrentState_stap1();
		memento.setCurrentActiviteit(currentActiviteit);
		memento.setCurrentOpdracht(currentOpdracht);
		memento.setOrGoedFout(isCorrect);
		double score = getScore();
		// THIS ORDER!!!!!
		memento.setScore(score);
		memento.setScores(scores);
		if (misconceptions != null)
		{
			memento.setPossibleMisconceptions(possibleMisconceptions);
			memento.setMeasuredMisconceptions(measuredMisconceptions);
		}
		if (objectives != null)
			memento.setScoresObjectives(scoresObjectives);
		memento.setBezocht(entry.bezocht);
		memento.setZelftoetsNagekeken(entry.zelftoetsNagekeken);

		// tempotoets locked opslaan
		memento.setTempotoetsLocked(entry.tempotoetsLocked);
		// tempotoets time left
		memento.setTempotoetsSecondsLeft(entry.getTimeLimitSecondsLeft());

		// update zelftoets scores en isCorrect
		memento.setScoresZelftoets(getScoresZelftoets());
		memento.setIsCorrectZelftoets(isCorrectZelftoets());
		memento.setNakijkenZelftoetsPending(nakijkenZelftoetsPending());

		memento.setAantalNakijken(aantalNakijken);
		memento.setCompletion(suspendDataCompleted(currentActiviteit, currentOpdracht));
		memento.setOpdrContStates(states);
		memento.flush();

		// send data to GUI (why here?)
		saveCurrentState_stap3(score);
	}

	private void saveCurrentState_stap3(double score)
	{
		ScoreNavIF source = entry.scoreNav;
		if (scoresVisible())
		{
			source.setItemScore(currentOpdracht, scores[currentActiviteit][currentOpdracht]);
			source.setTotaalScore((int) score);
		}
		source.setBeantwoord(getAantalBeantwoord()); // noordhoff
	}

	private void saveCurrentState_stap1()
	{
		if (!memento.isEindtoetsVerzegeld())
		{
			boolean old = goon;
			try
			{
				goon = false;
				states[currentActiviteit][currentOpdracht] = entry.getState();
			}
			finally
			{
				goon = old;
			}
		}

		if (scoresEnabled())
		{
			fetchScores();
		}
	}

	private void fetchScores()
	{
		int scoreCorrected = entry.getScore();
		scores[currentActiviteit][currentOpdracht] = scoreCorrected;
		isCorrect[currentActiviteit][currentOpdracht] = Boolean.TRUE == entry.isCorrect();
		if (objectives != null)
			scoresObjectives[currentActiviteit][currentOpdracht] = entry.getScoreObjectives();
		if (misconceptions != null)
		{
			possibleMisconceptions[currentActiviteit][currentOpdracht] = entry.getPossibleMisconceptions();
			measuredMisconceptions[currentActiviteit][currentOpdracht] = entry.getMeasuredMisconceptions();
		}
	}

	/**
	 * Zet scores en isCorrect op de waarden die de zelftoets moet tonen.
	 * 
	 * @param opdrNr
	 */
	private void fetchScoresZelftoets(int opdrNr)
	{
		int scoreCorrected = getScoresZelftoets()[currentActiviteit][opdrNr];
		scores[currentActiviteit][opdrNr] = scoreCorrected;
		boolean isCorrectVoorZelftoets = isCorrectZelftoets[currentActiviteit][opdrNr];
		isCorrect[currentActiviteit][opdrNr] = Boolean.TRUE == isCorrectVoorZelftoets;
		if (objectives != null)
			scoresObjectives[currentActiviteit][opdrNr] = entry.getScoreObjectives();
		if (misconceptions != null)
		{
			possibleMisconceptions[currentActiviteit][opdrNr] = entry.getPossibleMisconceptions();
			measuredMisconceptions[currentActiviteit][opdrNr] = entry.getMeasuredMisconceptions();
		}
	}

	public void kijkToetsNa()
	{
		setNakijkenZelftoetsPending();
		
		incrAantalNakijken(currentActiviteit);
		int opdrachtNr = currentOpdracht;
		pause();
		ScoreNavIF source = entry.scoreNav;

		// deze om het huidige bolletje na te kijken
		entry.kijkNa();
		entry.zetNagekeken(true);

		// update zelftoets scores en isCorrect
		memento.setScoresZelftoets(getScores());
		memento.setIsCorrectZelftoets(isCorrect);
		
		// scores en isCorrect overzetten naar soresZelftoets en isCorrectZelftoets
		setScoresZelftoets(scores);
		setIsCorrectZelftoets(isCorrect);

		memento.setZelftoetsHighScore(getActualScore());
		setZelftoetsHighScore((int) getActualScore());
		memento.addScoreZelftoetsHistorie((int) getActualScore());

		for (int j = 0; j < aantalOpdrachten[currentActiviteit]; j++)
		{
			setButtonCorrectZelftoets(buttons.get(j), isCorrectZelftoets[currentActiviteit][j], j);
			source.setItemScore(j, scores[currentActiviteit][j]);
		}

		source.setTotaalScore((int) getScore());
		unpause();
	}

	/**
	 * Zet de high score op de gegeven score als deze hoger is
	 * dan de huidige.
	 * 
	 * @param actualScore
	 */
	private void setZelftoetsHighScore(int score)
	{
		if (zelftoetsHighScore < score)
			zelftoetsHighScore = score;
	}

	/**
	 * Zet zelftoetsNakijken, een array van booleans per opdracht die aangeeft of een opdracht
	 * moet worden nagekeken. Als op de knop 'zelftoets nakijken' wordt gedrukt, moet
	 * voor alle behalve de huidige opdracht zelftoets nakijken pending op true worden gezet. 
	 */
	private void setNakijkenZelftoetsPending()
	{
		for (int actNr = 0; actNr < aantalActiviteiten; actNr++)
		{
			for (int opdrNr = 0; opdrNr < getAantalOpdrachten(); opdrNr++)
			{
				nakijkenZelftoetsPending[actNr][opdrNr] = true;
			}
		}
		
		// alleen de huidige wordt al nagekeken, dus false
		nakijkenZelftoetsPending[getCurrentActiviteit()][getCurrentOpdracht()] = false; 
		
		// memento updaten
		memento.setNakijkenZelftoetsPending(nakijkenZelftoetsPending);
	}

	private void incrAantalNakijken(int i)
	{
		if (aantalNakijken == null)
			aantalNakijken = new int[aantalActiviteiten]; // lazy initialization
		if (i >= 0 && i < aantalActiviteiten)
			aantalNakijken[i]++;
	}

	private int getAantalNakijken(int i)
	{
		if (aantalNakijken == null)
			return 0;
		if (i < 0 && i >= aantalActiviteiten)
			return 0;
		return aantalNakijken[i];
	}

	/**
	 * Is er state bij alle andere opdrachten van deze activiteit? Behalve de
	 * opgegeven opdrNr, die heeft zeker state!
	 * 
	 * @param actNr
	 * @param opdrNr
	 * @return true bij state
	 */
	private boolean suspendDataCompleted(int actNr, int opdrNr)
	{
		HashMap<String, Object>[] actState = states[actNr];
		if (actState == null)
			return false;
		int aantal = aantalOpdrachten[actNr];
		for (int j = 0; j < aantal; j++)
		{
			if (j != opdrNr && actState[j] == null)
				return false;
		}
		return true;
	}

	public void close()
	{
		memento.close();
		memento = null;
	}

	@Override
	public FormuleKeyboardIF getKeyboard()
	{
		return entry.getKeyboard();
	}

	@Override
	public FormuleClipboardIF getFormuleClipboard()
	{
		return entry.getClipboard();
	}

	public void gotoOpdracht(final int opdracht)
	{
		saveCurrentState();
		if (scoresVisible())
		{
			if (mode == ZELFTOETS)
				setButtonCorrectZelftoets(buttons.get(currentOpdracht), isCorrectZelftoets[currentActiviteit][currentOpdracht],
					currentOpdracht);
			else
				setButtonCorrect(buttons.get(currentOpdracht), isCorrect[currentActiviteit][currentOpdracht],
					currentOpdracht);
		}

		removeButtonCursor(buttons.get(currentOpdracht));
		currentOpdracht = opdracht;
		setButtonCursor(buttons.get(currentOpdracht));

		entry.clearContentPanel();
		if (states[currentActiviteit][currentOpdracht] == null)
			entry.zetVolgendeOpdracht(opdrachten[currentActiviteit][currentOpdracht]);
		else
		{
			entry.zetOpdrachtPlusState(opdrachten[currentActiviteit][currentOpdracht],
				states[currentActiviteit][currentOpdracht]);
			if (misconceptions != null)
				entry.setMeasuredMisconceptions(measuredMisconceptions);
		}
		if (entry.isPilotObjectives())
		{
			if (currentOpdracht == states[currentActiviteit].length - 1 && entry.isPilotObjectives())
				entry.scoreNav.setScoresObjectivesKnop(zijnObjectivesAanwezig());
			else
				entry.scoreNav.setScoresObjectivesKnop(false);

		}

	}

	public int getCurrentOpdracht()
	{
		return currentOpdracht;
	}

	public int getCurrentActiviteit()
	{
		return currentActiviteit;
	}

	public int getAantalOpdrachten()
	{
		return aantalOpdrachten[currentActiviteit];
	}

	public int getAantalOpdrachten(int act)
	{
		return aantalOpdrachten[act];
	}

	public int getAantalActiviteiten()
	{
		return aantalActiviteiten;
	}

	public int getAantalBeantwoord()
	{
		int len = getAantalOpdrachten();
		int totaal = 0;
		for (int i = 0; i < len; i++)
			if (states[currentActiviteit] != null && states[currentActiviteit][i] != null
				|| scores[currentActiviteit][i] != 0)
				totaal++;
		return totaal;
	}

	@Override
	public void gotoOpdracht(int i, ScoreNavIF source)
	{
		if (i == currentOpdracht)
			return;
		int oldOpdr = currentOpdracht;

		gotoOpdracht(i);

		if (source != null)
		{
			source.setBeantwoord(getAantalBeantwoord());
			source.setOpdracht(getCurrentOpdracht());
			source.setTotaalScore((int) getScore());
			source.setItemScore(oldOpdr, getScoresHuidigeActiviteit()[oldOpdr]);
		}

		if (DWOplayer.PARAMETERS.isNavTitle())
			entry.setTitle("Vraag " + (getCurrentOpdracht() + 1) + " van " + getAantalOpdrachten());
	}

	public int[] getMaxScores()
	{
		return scoresMax[currentActiviteit];
	}

	public int[] getScoresHuidigeActiviteit()
	{
		return scores[currentActiviteit];
	}

	public boolean zijnObjectivesAanwezig()
	{
		return objectivesAanwezig;
	}

	public boolean zijnMisconceptionsAanwezig()
	{
		return misconceptions != null;
	}

	public class MyDialog extends DialogBox
	{
		Image close = new Image(DWOplayer.DWO_BUNDLE.closebutton().getSafeUri());
		HTML title = new HTML("");
		HorizontalPanel captionPanel = new HorizontalPanel();

		public MyDialog(boolean autoHide, boolean modal)
		{
			super(autoHide, modal);
			Element td = getCellElement(0, 1);
			DOM.removeChild(td, (Element) td.getFirstChildElement());
			DOM.appendChild(td, captionPanel.getElement());
			captionPanel.setWidth("100%");
			// captionPanel.getElement().getStyle().setOpacity(0);
			captionPanel.setStyleName("Caption");// width: 100%
			// captionPanel.getElement().getStyle().s
			captionPanel.add(title);
			// close.addStyleName("CloseButton");//float:right
			captionPanel.add(close);
			close.getElement().getStyle().setFloat(Float.RIGHT);

			super.setGlassEnabled(true);
			super.setAnimationEnabled(true);
		}

		public MyDialog(boolean autoHide)
		{
			this(autoHide, true);
		}

		public MyDialog()
		{
			this(false);
		}

		@Override
		public String getHTML()
		{
			return this.title.getHTML();
		}

		@Override
		public String getText()
		{
			return this.title.getText();
		}

		@Override
		public void setHTML(String html)
		{
			this.title.setHTML(html);
		}

		@Override
		public void setText(String text)
		{
			this.title.setText(text);
		}

		@Override
		protected void onPreviewNativeEvent(NativePreviewEvent event)
		{
			NativeEvent nativeEvent = event.getNativeEvent();

			if (!event.isCanceled() && (event.getTypeInt() == Event.ONCLICK) && isCloseEvent(nativeEvent))
			{
				this.hide();
			}
			super.onPreviewNativeEvent(event);
		}

		private boolean isCloseEvent(NativeEvent event)
		{
			return event.getEventTarget().equals(close.getElement());
		}
	}

	public void openObjectivesPanel(boolean pilot)
	{
		/**
		 * Maakt panel met deelscores zichtbaar mbv een popup-venster
		 */
		int aantalDiagrammen = 0;
		for (int k = 0; k < objectives.length; k++)
		{
			int somObjective = 0;
			for (int i = 0; i < scoresMaxObjectives.length; i++)
				for (int j = 0; j < scoresMaxObjectives[i].length; j++)
				{
					try
					{
						for (int l = 0; l < scoresMaxObjectives[i][j][k].length; l++)
							somObjective += scoresMaxObjectives[i][j][k][l];
					}
					catch (Exception e)
					{
						somObjective = 0;
					}
				}
			if (somObjective > 0)
				aantalDiagrammen++;
		}
		scoresObjectivesDialog = new MyDialog(true); // evt argument true
														// meegeven voor
														// autohide.

		// Misschien geen dialogbox maar een popup. Moet in elk geval ook weer
		// te sluiten zijn.
		// scoresObjectivesDialog = new DialogBox(true);
		scoresObjectivesDialog.setText(Text.constants.objectivesKnopLabel());
		// scoresObjectivesDialog = new DialogBox(this,"deelscores", true);

		/*
		 * pilot doet nu: - Weergave niet lijst (met uitklapbare onderwerpen) in
		 * plaats van cirkeldiagrammen - Berekening scores op basis van attempts
		 * en niet alleen laatste status - Zichtbaarheid knop: alleen op laatste
		 * pagina - Berekening en weergave categorie-score
		 */
		if (pilot)
			scoresObjectivesPanel = new ScoresObjectivesPanel(getScoresObjectivesForDiagramFromLogs(), pilot);
		else
			scoresObjectivesPanel = new ScoresObjectivesPanel(getScoresObjectivesForDiagram(), pilot);

		// if(aantalDiagrammen < 4)
		// scoresObjectivesPanel.setBounds(0, 0, 400 * aantalDiagrammen, 350);
		// else
		// scoresObjectivesPanel.setBounds(0, 0, 1200, 700);
		scoresObjectivesDialog.add(scoresObjectivesPanel.asWidget());
		scoresObjectivesDialog.show();
		scoresObjectivesDialog.center();
		// scoresObjectivesDialog.setSize(scoresObjectivesPanel.getSize());
		int width = 1200;
		int height = 730;

		if (pilot)
		{
			width = 500;
			height = scoresObjectivesPanel.asWidget().getOffsetHeight();
		}
		else if (aantalDiagrammen < 4)
		{
			width = 400 * aantalDiagrammen;
			height = 380;
		}
		scoresObjectivesDialog.setWidth(width + "px");
		scoresObjectivesDialog.setHeight(height + "px");

	}

	public void openMisconceptionsPanel()
	{
		/**
		 * Maakt panel met analyse van misconcepties zichtbaar mbv een
		 * popup-venster
		 */
		int aantalDiagrammen = misconceptions.length;
		viewMisconceptionsDialog = new MyDialog(true); // evt argument true
														// meegeven voor
														// autohide.
		// Misschien geen dialogbox maar een popup. Moet in elk geval ook weer
		// te sluiten zijn.
		// scoresObjectivesDialog = new DialogBox(true);
		viewMisconceptionsDialog.setText(Text.constants.viewMisconceptionsKnopLabel());
		// scoresObjectivesDialog = new DialogBox(this,"deelscores", true);
		viewMisconceptionsPanel = new ScoresObjectivesPanel(getMisconceptionsForDiagram(), false);
		viewMisconceptionsPanel.zetKleurNeutraal();

		// if(aantalDiagrammen < 4)
		// scoresObjectivesPanel.setBounds(0, 0, 400 * aantalDiagrammen, 350);
		// else
		// scoresObjectivesPanel.setBounds(0, 0, 1200, 700);
		viewMisconceptionsDialog.add(viewMisconceptionsPanel.asWidget());
		// scoresObjectivesDialog.setSize(scoresObjectivesPanel.getSize());
		int width = 1200;
		int height = 730;
		if (aantalDiagrammen < 4)
		{
			width = 400 * aantalDiagrammen;
			height = 380;
		}
		viewMisconceptionsDialog.setWidth(width + "px");
		viewMisconceptionsDialog.setHeight(height + "px");
		viewMisconceptionsDialog.show();
		// scoresObjectivesDialog.setVisible(true);
	}

	/**
	 * Verzamelt de maximale scores per leerdoel, de gerealiseerde scores per
	 * leerdoel en de leerdoelen zelf en geeft deze terug tbv het diagram.
	 * Gebruikt als input de huidige score bij elke opgave
	 */
	public HashMap<String, Object> getScoresObjectivesForDiagram()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		if (objectives == null)
			return h;

		double[][] totaalScoreObjectives = null;
		double[][] totaalMaxObjectives = null;
		double[][] scoresPercObjectives = null;

		totaalScoreObjectives = new double[objectives.length][];
		totaalMaxObjectives = new double[objectives.length][];
		scoresPercObjectives = new double[objectives.length][];

		for (int i = 0; i < objectives.length; i++)
		{
			totaalScoreObjectives[i] = new double[objectives[i].length];
			totaalMaxObjectives[i] = new double[objectives[i].length];
			scoresPercObjectives[i] = new double[objectives[i].length];
		}

		for (int i = 0; i < aantalActiviteiten; i++)
		{
			for (int j = 0; j < aantalOpdrachten[i]; j++)
			{
				if (scoresObjectives[i][j] != null)
					for (int k = 0; k < objectives.length && k < scoresObjectives[i][j].length; k++)
					{
						if (scoresObjectives[i][j][k] != null)
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

	/**
	 * Verzamelt de maximale scores per leerdoel, de gerealiseerde scores per
	 * leerdoel en de leerdoelen zelf en geeft deze terug tbv het diagram.
	 * Gebruikt als input de logfiles per opgave (dus attempts).
	 */
	public HashMap<String, Object> getScoresObjectivesForDiagramFromLogs()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		if (objectives == null)
			return h;

		double[][] totaalScoreObjectives = null;
		double[][] totaalMaxObjectives = null;
		double[] categorieScoreObjectives = null;
		double[] categorieMaxObjectives = null;

		totaalScoreObjectives = new double[objectives.length][];
		totaalMaxObjectives = new double[objectives.length][];
		categorieScoreObjectives = new double[objectives.length];
		categorieMaxObjectives = new double[objectives.length];

		for (int i = 0; i < objectives.length; i++)
		{
			totaalScoreObjectives[i] = new double[objectives[i].length];
			totaalMaxObjectives[i] = new double[objectives[i].length];
		}

		JSONObject logState = memento.getLogState();
		Set<String> keys = logState.keySet();

		for (String key : keys)
		{
			try
			{
				JSONArray attempts = logState.get(key).isObject().get(DWOLogger.LOG_ATTEMPTS).isArray();
				JSONArray logObjectives = logState.get(key).isObject().get("logObjectives").isArray();

				// score berekenen op basis van attempts
				double score = 0;
				for (int i = 0; i < attempts.size(); i++)
				{
					String attempt = attempts.get(i).isString().stringValue();
					String[] attemptSplit = attempt.split(";");
					if (attemptSplit.length > 1 && attemptSplit[1].trim().equals("goed"))
						score += 1;
					else if (attemptSplit.length > 1 && attemptSplit[1].trim().equals("half"))
						score += 0.5;
				}
				if (attempts.size() > 0)
					score = score / attempts.size();

				// voor elk aan deze opdracht gekoppelde leerdoel score optellen
				// en maxscore ophogen.
				for (int i = 0; i < objectives.length; i++)
				{
					for (int j = 0; j < objectives[i].length; j++)
					{
						boolean categorieGescoord = false;
						if (logObjectives.get(i).isArray().get(j).isBoolean().booleanValue())
						{
							totaalScoreObjectives[i][j] += score;
							totaalMaxObjectives[i][j]++;
							if (!categorieGescoord)
							{
								categorieScoreObjectives[i] += score;
								categorieMaxObjectives[i]++;
								categorieGescoord = true;
							}
						}

					}
				}
			}
			catch (Exception e)
			{
			}
		}

		h.put("objectives", objectives);
		h.put("totaalScoreObjectives", totaalScoreObjectives);
		h.put("totaalMaxObjectives", totaalMaxObjectives);
		h.put("categorieString", categorieString);
		h.put("categorieScoreObjectives", categorieScoreObjectives);
		h.put("categorieMaxObjectives", categorieMaxObjectives);

		return h;
	}

	/**
	 * Verzamelt de maximale scores per leerdoel, de gerealiseerde scores per
	 * leerdoel en de leerdoelen zelf en geeft deze terug tbv het diagram.
	 */
	public HashMap<String, Object> getMisconceptionsForDiagram()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		if (misconceptions == null)
			return h;

		double[][] totaalMeasuredMisconceptions = null;
		double[][] totaalPossibleMisconceptions = null;
		// double[][] scoresPercObjectives = null;

		totaalMeasuredMisconceptions = new double[misconceptions.length][];
		totaalPossibleMisconceptions = new double[misconceptions.length][];
		// scoresPercObjectives = new double[objectives.length][];

		for (int i = 0; i < misconceptions.length; i++)
		{
			totaalMeasuredMisconceptions[i] = new double[misconceptions[i].length];
			totaalPossibleMisconceptions[i] = new double[misconceptions[i].length];
			// scoresPercObjectives[i] = new double[objectives[i].length];
		}

		for (int i = 0; i < aantalActiviteiten; i++)
		{ // String scoreString = scores[i].getText();
			// int score = Integer.parseInt(scoreString.substring(7));
			// totaalScore += score;
			for (int j = 0; j < aantalOpdrachten[i]; j++)
			{
				if (measuredMisconceptions[i][j] != null)
					for (int k = 0; k < misconceptions.length && k < measuredMisconceptions[i][j].length; k++)
					{
						if (measuredMisconceptions[i][j][k] != null)
							for (int l = 0; l < misconceptions[k].length
								&& l < measuredMisconceptions[i][j][k].length; l++)
								totaalMeasuredMisconceptions[k][l] += measuredMisconceptions[i][j][k][l];
					}
			}

			for (int j = 0; j < aantalOpdrachten[i]; j++)
			{
				if (possibleMisconceptions[i][j] != null)
					for (int k = 0; k < misconceptions.length && k < possibleMisconceptions[i][j].length; k++)
					{
						if (possibleMisconceptions[i][j][k] != null)
							for (int l = 0; l < misconceptions[k].length
								&& l < possibleMisconceptions[i][j][k].length; l++)
								totaalPossibleMisconceptions[k][l] += possibleMisconceptions[i][j][k][l];
					}
			}
		}

		h.put("objectives", misconceptions);
		h.put("totaalScoreObjectives", totaalMeasuredMisconceptions);
		h.put("totaalMaxObjectives", totaalPossibleMisconceptions);
		h.put("categorieString", mccCategorieString);

		return h;
	}

	public void reloadOpdracht(int opdracht, ScoreNavIF source)
	{
		saveCurrentState();
		removeButtonCursor(buttons.get(currentOpdracht));
		boolean randomize;
		if (opdracht < 0) // alles opnieuw
		{
			randomize = true; // Alles opnieuw, dus ook nieuwe random variabelen
			currentOpdracht = 0;

			for (opdracht = 0; opdracht < aantalOpdrachten[currentActiviteit]; opdracht++)
			{
				clearState(opdracht, source);
				setButtonCorrect(buttons.get(opdracht), isCorrect[currentActiviteit][opdracht], opdracht);
			}

			entry.setZelftoetsNagekeken(false);
			if (entry.isTempotoets())
			{
				entry.resetTimer();
			}

			if (mode == ZELFTOETS)
			{
				clearScoresZelftoets();
				clearIsCorrectZelftoets();
				clearNakijkenZelftoetsPending();
			}

			if (getAantalNakijken(currentActiviteit) > 0)
				aantalNakijken[currentActiviteit] = 0;

			// reset totaalscore en keer nagekeken
//			entry.scoreNav.setTotaalScoreLabel(getTotaalScore());
			entry.scoreNav.setTotaalScoreLabel((int) getScore()); // toon percentagescore
			entry.scoreNav.setKeerNagekekenLabel(getKeerNagekeken());

			// reset bezocht
			resetBezocht();
		} // alles opnieuwe
		else
		{
			// opdracht opnieuw
			randomize = false; // TODO moet wel, tenzij er nu geen enkele state
								// meer is. Peter vragen?
			clearState(opdracht, source);
			setButtonCorrect(buttons.get(opdracht), isCorrect[currentActiviteit][opdracht], opdracht);

			resetBezocht(currentActiviteit, opdracht);
		}
		source.setBeantwoord(getAantalBeantwoord());
		source.setTotaalScore((int) getScore());

		// setButtonCorrect(buttons.get(currentOpdracht),
		// isCorrect[currentActiviteit][currentOpdracht], currentOpdracht);

		setButtonCursor(buttons.get(currentOpdracht));

		entry.clearContentPanel();
		if (states[currentActiviteit][currentOpdracht] == null)
		{
			if (randomize)
				entry.zetOpdracht(opdrachten[currentActiviteit][currentOpdracht]);
			else
				entry.zetVolgendeOpdracht(opdrachten[currentActiviteit][currentOpdracht]);
		}
		else
			entry.zetOpdrachtPlusState(opdrachten[currentActiviteit][currentOpdracht],
				states[currentActiviteit][currentOpdracht]);

		if (DWOplayer.PARAMETERS.isNavTitle())
			entry.setTitle("Vraag " + (getCurrentOpdracht() + 1) + " van " + getAantalOpdrachten());
	}

	/**
	 * Reset bezocht
	 */
	public void resetBezocht()
	{
		boolean[][] bezocht = new boolean[getAantalActiviteiten()][];
		for (int j = 0; j < getAantalActiviteiten(); j++)
		{
			bezocht[j] = new boolean[getAantalOpdrachten(j)]; // all false
		}
		entry.bezocht = bezocht;
	}

	/**
	 * Reset bezocht voor de huidige activiteit en de gegeven opdracht.
	 * 
	 * @param currentActiviteit
	 * @param opdracht
	 */
	public void resetBezocht(int currentActiviteit, int opdracht)
	{
		entry.bezocht[currentActiviteit][opdracht] = false;
	}

	public void clearState(int opdracht, ScoreNavIF source)
	{
		isCorrect[currentActiviteit][opdracht] = false;
		scores[currentActiviteit][opdracht] = 0;
		states[currentActiviteit][opdracht] = null;
		if (objectives != null)
		{
			for (int i = 0; i < objectives.length; i++)
			{
				scoresObjectives[currentActiviteit][opdracht][i] = new int[objectives[i].length];
			}
		}
		if (misconceptions != null)
		{
			for (int i = 0; i < misconceptions.length; i++)
			{
				possibleMisconceptions[currentActiviteit][opdracht][i] = new int[misconceptions[i].length];
				measuredMisconceptions[currentActiviteit][opdracht][i] = new int[misconceptions[i].length];
			}
		}
		if (strafpunten != null)
			strafpunten[currentActiviteit][opdracht] = 0;
		source.setItemScore(opdracht, 0);
	}

	public int getMode()
	{
		return mode;
	}

	@Override
	public String getLearnerId()
	{
		return memento.getLearnerId();
	}

	@Override
	public String getLearnerName()
	{
		return memento.getLearnerName();
	}

	@Override
	public CssColor getBackground()
	{
		return CssColor.make("white");
	}

	@Override
	public String getUUID()
	{
		return entry.getUnitId() + "-" + getCurrentOpdracht() + "-" + getWidgetId();
	}

	protected String getWidgetId()
	{
		return "XXXXXXXX";
	}

	/*
	 * Event handling: on top: OpdrNavIF.addCBookEventListener(command,
	 * listener); en OpdrNavIf.fireEvent(event);
	 * 
	 * 
	 * lowestlevel BUS.addHandlerToSource(TYPE, listener, UUID_of_listener +
	 * command) forall dest in destinations of command;
	 * BUS.fireEventFromSource(event, UUID_dest + command);
	 * 
	 */

	@Override
	public HandlerRegistration addCBookEventListener(String command, CBookEventListener listener)
	{
		return null;
	}

	@Override
	public void fireEvent(CBookEvent event)
	{
	}

	public static EventBus getEventBus()
	{
		return BUS;
	}

	@Override
	public LessonMode getLessonMode()
	{
		return memento.getLessonMode();
	}

	@Override
	public Role getRole()
	{
		return memento.getRole();
	}

	public boolean pause(boolean b)
	{
		boolean old = goon;
		goon = !b;
		pause();
		return old;

	}

	@Override
	public boolean hasListeners(String command)
	{
		return false;
	}

	public ObjectMap getConfiguration()
	{
		return instellingen;
	}

	/**
	 * Geef het maximum aantal opdrachten over alle activiteiten.
	 * 
	 * @return
	 */
	public int getMaxAantalOpdrachten()
	{
		return maxAantalOpdrachten;
	}

	/**
	 * Zet de scores voor zelftoets. Dit zijn de scores zoals
	 * de zelftoets ze moet tonen. Antwoorden die na het
	 * nakijken van de zelftoets zijn veranderd worden genegeerd en pas
	 * getoond als op de kijkna-knop wordt gedrukt.
	 * 
	 * @param scores
	 */
	public void setScoresZelftoets(int[][] scores)
	{
		if (scores.length == 0)
			scoresZelftoets = new int[0][0];
		else
		{
			scoresZelftoets = new int[scores.length][scores[0].length];
			for (int i = 0; i < scores.length; i++)
			{
				System.arraycopy(scores[i], 0, scoresZelftoets[i], 0, scores[0].length);
			}
		}
	}
	
	/**
	 * Zet isCorrect voor zelftoets. Dit is de correctheid zoals
	 * de zelftoets die moet tonen in groene/rode bolletjes. Antwoorden die na het
	 * nakijken van de zelftoets zijn veranderd worden genegeerd en pas
	 * getoond als op de kijkna-knop wordt gedrukt.
	 * 
	 * @param isCorrect
	 */
	public void setIsCorrectZelftoets(boolean[][] isCorrect)
	{
		if (isCorrect.length == 0)
			isCorrectZelftoets = new boolean[0][0];
		else
		{
			isCorrectZelftoets = new boolean[isCorrect.length][isCorrect[0].length];
			for (int i = 0; i < isCorrect.length; i++)
			{
				System.arraycopy(isCorrect[i], 0, isCorrectZelftoets[i], 0, isCorrect[0].length);
			}
		}
	}
	
	/**
	 * Zet de score voor zelftoets voor de gegeven activiteit en opdracht.
	 * Dit zijn de scores zoals de zelftoets ze moet tonen. Antwoorden die na het
	 * nakijken van de zelftoets zijn veranderd worden genegeerd en pas
	 * getoond als op de kijkna-knop wordt gedrukt.
	 * 
	 * @param scores
	 */
	public void setScoresZelftoets(int activiteitNr, int opdrachtNr, int score)
	{
		scoresZelftoets[activiteitNr][opdrachtNr] = score;
	}
	
	/**
	 * Zet de score voor de gegeven activiteit en opdracht.
	 * 
	 * @param scores
	 */
	public void setScores(int activiteitNr, int opdrachtNr, int score)
	{
		scores[activiteitNr][opdrachtNr] = score;
	}
	
	/**
	 * Zet nakijken zelftoets pending.
	 * 
	 * @param pending
	 */
	public void setNakijkenZelftoetsPending(boolean[][] pending)
	{
		if (pending.length == 0)
			nakijkenZelftoetsPending = new boolean[0][0];
		else
		{
			nakijkenZelftoetsPending = new boolean[pending.length][pending[0].length];
			for (int i = 0; i < pending.length; i++)
			{
				System.arraycopy(pending[i], 0, nakijkenZelftoetsPending[i], 0, pending[0].length);
			}
		}
	}
	
	/**
	 * Zet nakijken zelftoets pending voor de gegeven activiteit en opdracht.
	 * 
	 * @param activiteitNr
	 * @param opdrachtNr
	 * @param b
	 */
	public void setNakijkenZelftoetsPending(int activiteitNr, int opdrachtNr, boolean b)
	{
		nakijkenZelftoetsPending[activiteitNr][opdrachtNr] = b;
	}
	
	/**
	 * Zet de correctheid voor zelftoets voor de gegeven activiteit en opdracht.
	 * Dit is de correctheid zoals de zelftoets die moet tonen. Antwoorden die na het
	 * nakijken van de zelftoets zijn veranderd worden genegeerd en pas
	 * verwerkt als op de kijkna-knop wordt gedrukt.
	 * 
	 * @param activiteitNr
	 * @param opdrachtNr
	 * @param b
	 */
	public void setIsCorrectZelftoets(int activiteitNr, int opdrachtNr, boolean b)
	{
		isCorrectZelftoets[activiteitNr][opdrachtNr] = b;
	}
	
	/**
	 * Zet de correctheid voor de gegeven activiteit en opdracht.
	 * 
	 * @param activiteitNr
	 * @param opdrachtNr
	 * @param b
	 */
	public void setIsCorrect(int activiteitNr, int opdrachtNr, boolean b)
	{
		isCorrect[activiteitNr][opdrachtNr] = b;
	}
	
	public int[][] getScores()
	{
		return scores;
	}
	
	public int[][] getScoresZelftoets()
	{
//		return memento.getScoresZelftoets();
		return scoresZelftoets;
	}
	
	public boolean[][] isCorrectZelftoets()
	{
		return isCorrectZelftoets;
	}

	public boolean[][] nakijkenZelftoetsPending()
	{
		return nakijkenZelftoetsPending;
	}

	public int getScoresZelftoets(int activiteitNr, int opdrachtNr)
	{
		return scoresZelftoets[activiteitNr][opdrachtNr];
	}
	
	/**
	 * Zet de scores voor zelftoets op 0 voor de huidige 
	 * acitiviteit en de gegeven opdracht.
	 * 
	 * @param opdracht
	 */
	public void clearScoresZelftoets(int opdracht)
	{
		setScoresZelftoets(getCurrentActiviteit(), opdracht, 0);
	}

	/**
	 * Zet isCorrect voor zelftoets op false voor de huidige
	 * activiteit en de gegeven opdracht.
	 * 
	 * @param opdracht
	 */
	public void clearIsCorrectZelftoets(int opdracht)
	{
		setIsCorrectZelftoets(getCurrentActiviteit(), opdracht, false);
	}

	/**
	 * Zet nakijkenZelftoetsPending op false voor de huidige
	 * activiteit en de gegeven opdracht.
	 * 
	 * @param opdracht
	 */
	public void clearNakijkenZelftoetsPending(int opdracht)
	{
		setNakijkenZelftoetsPending(getCurrentActiviteit(), opdracht, false);
	}

	/**
	 * Zet de scores voor zelftoets op 0.
	 * 
	 * @param opdracht
	 */
	public void clearScoresZelftoets()
	{
		scoresZelftoets = new int[getAantalActiviteiten()][getMaxAantalOpdrachten()];
	}
	
	/**
	 * Zet de correctheid voor zelftoets op false.
	 * 
	 * @param opdracht
	 */
	public void clearIsCorrectZelftoets()
	{
		isCorrectZelftoets = new boolean[getAantalActiviteiten()][getMaxAantalOpdrachten()];
	}

	public boolean isReview() {
		return memento.isReview();
	}
	
	/**
	 * Zet nakijken zelftoets pending op false.
	 * 
	 * @param opdracht
	 */
	public void clearNakijkenZelftoetsPending()
	{
		nakijkenZelftoetsPending = new boolean[getAantalActiviteiten()][getMaxAantalOpdrachten()];
	}

	/**
	 * Geef de lijst van scores van de zelftoetshistorie.
	 * 
	 * @return
	 */
	public List<? extends String> getScoresZelftoetsHistorie()
	{
		List<String> lijst = Arrays.asList(this.memento.getScoresZelftoetsHistorie());
		return lijst;
	}

	/**
	 * Geef de high score van de zelftoets.
	 * 
	 * @return
	 */
	public int getZelftoetsHighScore()
	{
		return zelftoetsHighScore;
	}
}
