package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleClientBundle;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleButton;
import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.DWOLogger;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.TouchButton;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.wiskopdr.AntwoordFormuleVakChecker;
import fi.wiskopdr.AntwoordVakChecker;
import fi.wiskopdr.AntwoordVergelijkingVakChecker;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.RestartException;
import fi.wiskopdr.expressies.Algebra;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.DecRound;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.Vergelijking;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import fi.wiskopdr.expressies.repr.ContentMathML;
import fi.wiskopdr.text.Text;

/**
 * Used for showing formula's that can be solved in steps.
 * 
 * @author Evertson Croes
 * 
 */
public class FormuleEditorWithSteps implements InteractionViewWithMisconceptions, FacetAware, TekstElementWithFont
{
	private final static Logger logger = Logger.getLogger("FormuleEditorWithSteps");

	public static final String ACTION_CORRECT = "action.correct";
	public static final String ACTION_FALSE = "action.false";
	public static final String ACTION_FALSE2 = "action.false_2";

	private static final CBookEvent EVENT_CORRECT = new CBookEvent(ACTION_CORRECT); 
	private static final CBookEvent EVENT_FALSE = new CBookEvent(ACTION_FALSE); 
	private static final CBookEvent EVENT_FALSE2 = new CBookEvent(ACTION_FALSE2); 

	//static int GOED = 1;
	//static int FOUT = 0;
	//static int HALF = 2;
	public static final FormuleClientBundle FORMULE_BUNDLE = GWT.create(FormuleClientBundle.class);
	
	private String startString = "$f@";
	private String antwoordString = "$f@";
	private String prefix = "$f@";
	private boolean hasPrefix = false;
	boolean hasStartString = false;
	boolean boxMetRand = true;
	private Boolean exact = false;
	private boolean eigenOpdr = false;
	protected int breedte = 600;
	protected int hoogte = 250;
	private boolean volledigeBreedte = false;
	private HashMap<String, Object> launchState;
	private ObjectMap instellingen;
	protected ArrayList<FormuleViewer> viewers = new ArrayList<FormuleViewer>();
	protected FormuleEditorWithAnswer editor = null;
	private FormuleViewer prefixViewer;
	protected FormuleViewer latest_answer_viewer;
	private ScrollPanel sp = null;
	protected AntwoordVakChecker avChecker = null;
	
	private LayoutPanel contentPanel = null;
	private TekstVak feedbackPanel = null;
	int feedbackPanelHeight = 34;
	private FlowPanel mainPanel = null;
	protected FlowPanel headerPanel = null;
	private OpdrNavIF comRoot;
	protected int mode;
	String feedback = "";
	
	private PijlVak pijlVak;
	private boolean pijl = false;
	//private int pijlX = "GR".equals(WiskOpdr.deployVariant) ? 105 : 130;
	private int pijlX = 130;
	private int stepPanelY = 0; //locatie van bovenrand van het laatste (onderste) stepPanel
	protected int stapH = 21;
	
	private Expressie substitutie;
	private Vergelijking[] gebruikersSubstituties;
	private FormuleEditorWithSteps gebruikersSubstitutiesVak;
	private boolean substitutieVak = false;
	
	private TouchButton terugButton;
	private TouchButton downButton;
	private TouchButton copyButton;
	private TouchButton closeButton;
	private FormuleButton plusKnop, minKnop, maalKnop, deelKnop, haakjesKnop, herleidKnop, abcKnop, subKnop;
	private TouchButton rmKnop;
	private int aantalDecRm = 10;
	private FormuleButton ontbindKnop, splitsKnop, wortelBewerkKnop;
	private boolean abcVisible, subVisible, subExtra;
	private boolean bewerkingKnoppen, bewerkingKnoppenExtra;
	private int stapNr = 0;
	protected HashMap<String, Object> h = null;
	protected static String[] randomVarNamen = null;
	protected static HashMap randomVarWaarden = null;
	private ArrayList<LayoutPanel> stepPanels = new ArrayList<LayoutPanel>();
	private ArrayList<PijlVak> pijlVakken = new ArrayList<PijlVak>();
	
	private ArrayList<Image> imagesStappen = new ArrayList<Image>();
	protected Image checkimg;
	
	private boolean[][] logObjectives;
	DWOLogger dwologger;
	
	private boolean check;
	private boolean teltMee;

	private int score;
	private int errorCount = 0;
	private int scoreMax;
	private int foutStraf = 2;
	private Boolean correct;
	
	private boolean stapOk = true;

	private FormuleFont font;
	private boolean ingevuld = false;
	private boolean nagekeken = false;
	private boolean isVeranderdNaNakijken = false;
	private boolean hasFeedback = false;
	private int aantalStappen = 0;
	private boolean stepsForLinKwad = false;

	protected boolean isVergelijkingVak = false;
	private PopupFacade facade;
	
	private boolean bordjesMethode;
	private boolean linStrategieVersie; 
	private boolean linOefenVersie;
	
	private static boolean fontOvererving;

	private boolean isUitgeklapt;
	private boolean isBoss;
	
	public static void zetFontOverervingForm(boolean b)
	{	fontOvererving = b;
	}

	public FormuleEditorWithSteps(HashMap<String, Object> h, boolean isVergelijkingVak, String[] randomVarNamen, HashMap randomVarWaarden, AntwoordVakChecker avChecker)
	{
		font = FormuleFont.createFromFontSize(XMLView.getDefaultFontSize());
		
		FormuleEditorWithSteps.randomVarNamen = randomVarNamen;
		FormuleEditorWithSteps.randomVarWaarden = randomVarWaarden;
		this.isVergelijkingVak = isVergelijkingVak;
		this.h = h;
		
		ObjectMap map = JSONUtilities.wrapMap(h);
		if(map.containsKey("breedte"))
			breedte = map.getInt("breedte");
		if(map.containsKey("hoogte"))
			hoogte = map.getInt("hoogte");
		if(map.containsKey("volledigeBreedte"))
			volledigeBreedte = map.getBoolean("volledigeBreedte");
		
		facade = new PopupFacade(map);
		rmknop = false;
		if (h.get("interactiePanelLaunchState") != null)
		{
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
			ObjectMap launchStateMap = JSONUtilities.wrapMap(launchState);
			if(avChecker == null)
			{
				if (isVergelijkingVak)
					this.avChecker = new AntwoordVergelijkingVakChecker((HashMap<String, Object>) launchState, randomVarNamen, randomVarWaarden);
				else
					this.avChecker = new AntwoordFormuleVakChecker((HashMap<String, Object>) launchState, randomVarNamen, randomVarWaarden);
			}
			else
				this.avChecker = avChecker;
			
			if (launchStateMap.containsKey("startString"))
				startString = launchStateMap.getString("startString");
			if (launchState.get("exact") != null)
				exact = (Boolean) launchState.get("exact");
			if (launchState.get("eigenOpdr") != null)
				eigenOpdr = (Boolean) launchState.get("eigenOpdr");
			if (launchState.get("boxMetRand") != null)
				boxMetRand = (Boolean) launchState.get("boxMetRand");
			if (launchStateMap.containsKey("antwoordString"))
				antwoordString = launchStateMap.getString("antwoordString");
			if (launchState.get("scoreMax") != null)
				scoreMax = ((Number) launchState.get("scoreMax")).intValue();
			if (launchState.containsKey("abcKnop"))
				abcVisible = ((Boolean) launchState.get("abcKnop")).booleanValue();
			if (launchState.containsKey("subKnop"))
				subVisible = ((Boolean) launchState.get("subKnop")).booleanValue();
			if (launchState.containsKey("subKnopExtra"))
				subExtra = ((Boolean) launchState.get("subKnopExtra")).booleanValue();
			if (launchState.containsKey("bewerkingKnoppen"))
				bewerkingKnoppen = ((Boolean) launchState.get("bewerkingKnoppen")).booleanValue();
			if (launchState.containsKey("bewerkingKnoppenExtra"))
				bewerkingKnoppenExtra = ((Boolean) launchState.get("bewerkingKnoppenExtra")).booleanValue();
			if (launchState.containsKey("check"))
				check = ((Boolean) launchState.get("check")).booleanValue();
			if (launchState.containsKey("teltMee"))
				teltMee = ((Boolean) launchState.get("teltMee")).booleanValue();
			if(launchStateMap.containsKey("logObjectives"))
			{	ObjectList logObjectivesList = ( launchStateMap.getObjectList("logObjectives") );
				logObjectives = new boolean[logObjectivesList.size()][];
				for(int i = 0; i < logObjectivesList.size(); i++)
				{	logObjectives[i] = logObjectivesList.getBooleanArray(i);
				}
			}
			if (launchStateMap.getBoolean("logOption", false)) {
				dwologger = new DWOLogger();
				String type = isVergelijkingVak? "Vergelijking":"Formule";
				dwologger.setClassName("fi.wiskopdr.Antwoord" + type + "Vak");
				dwologger.setLogID(launchStateMap.getString("logID"));
				if(launchStateMap.containsKey("logIDLabel"))
					dwologger.setLogIDLabel(launchStateMap.getString("logIDLabel"));
				dwologger.setMaxScore(scoreMax);
				dwologger.setLogObjectives(logObjectives);
			}
			
			rmknop = !isVergelijkingVak && launchStateMap.getBoolean("rmKnop");
			if(launchStateMap.containsKey("aantalDecRm"))
				aantalDecRm = launchStateMap.getInt("aantalDecRm");
			if (launchState.containsKey("pijl"))
				pijl = ((Boolean) launchState.get("pijl")).booleanValue();
			if (launchState.containsKey("substitutieVak"))
				substitutieVak = ((Boolean) launchState.get("substitutieVak")).booleanValue();
			//op verzoek van Noordhoff:
			if(isNoordhoff())
				pijl = false;
			bordjesMethode = Boolean.TRUE.equals( launchState.get("bordjesMethode"));
			linStrategieVersie = Boolean.TRUE.equals(launchState.get("linStrategieVersie"));
			linOefenVersie = Boolean.TRUE.equals(launchState.get("linOefenVersie"));
			if(substitutieVak)
			{
				subVisible = false;
				abcVisible = false;
				startString = "$f@";
				check = false;
				teltMee = false;
				pijl = false;
				boxMetRand = true;
				bewerkingKnoppen = false;
				bewerkingKnoppenExtra = false;
				rmknop = false;
				bordjesMethode = false;
				linStrategieVersie = false;
				linOefenVersie = false;
			}
		
			
			
		}
		else
		{
			if (h.get("exact") != null)
				exact = (Boolean) h.get("exact");
			startString = extractStartString(h);
		}

		if (randomVarNamen != null && randomVarWaarden != null)
		{
			try
			{
				startString = FormuleParser.randomizeString(startString, randomVarNamen, randomVarWaarden);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if(!startString.equals("$f@"))
			hasStartString = true;
		
		if (randomVarNamen != null && randomVarWaarden != null)
		{
			try
			{
				antwoordString = FormuleParser.randomizeString(antwoordString, randomVarNamen, randomVarWaarden);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		if (!isVergelijkingVak)
		{
			int index = antwoordString.indexOf("=");
			if (index == -1)
				index = antwoordString.indexOf("\u2248");
			if (index > -1)
			{
				prefix = antwoordString.substring(0, index + 1) + "@";
				hasPrefix = true;
				antwoordString = "$f" + antwoordString.substring(index + 1);
			}

//			FormuleViewer f = new FormuleViewer(prefix);
//			f.setFont(defaultfont);
//			prefixViewer = f.getAsPanel();
//			prefixViewer.getElement().getStyle().setProperty("display", "inline-block");
//			prefixViewer.getElement().getStyle().setProperty("clear", "both");
//			prefixViewer.getElement().getStyle().setMarginLeft(23, Unit.PX);
			prefixViewer = new FormuleViewer(prefix);
			prefixViewer.setFont(font);
			prefixViewer.setSelectable(false);
		}
		
		mainPanel = new FlowPanel();
		mainPanel.addStyleName("formuleEditorWithSteps");
		mainPanel.setPixelSize(breedte-2, hoogte-2);
		mainPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		mainPanel.getElement().getStyle().setBorderColor("gray");
		mainPanel.getElement().getStyle().setBackgroundColor("white");
		mainPanel.getElement().getStyle().setBorderWidth(boxMetRand ? 1 : 0, Unit.PX);
		
		headerPanel = new FlowPanel();
		headerPanel.setPixelSize(breedte - 2, 22);//TODO: hoogte nog even checken
		headerPanel.getElement().getStyle().setBackgroundColor("white");
		headerPanel.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		
		Image buttonImg = new Image(DWOplayer.DWO_BUNDLE.pijlterug().getSafeUri());
		buttonImg.getElement().getStyle().setMargin(2, Unit.PX);
		terugButton = new TouchButton();
		terugButton.add(buttonImg);
		terugButton.getElement().getStyle().setFloat(Style.Float.RIGHT);
		if(hasStartString)
			terugButton.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		addButtonHandler(terugButton);
		
		Image downButtonImg = new Image(DWOplayer.DWO_BUNDLE.pijldown().getSafeUri());
		downButtonImg.getElement().getStyle().setMargin(2, Unit.PX);
		downButton = new TouchButton();
		downButton.add(downButtonImg);
		downButton.getElement().getStyle().setFloat(Style.Float.RIGHT);
		addDownButtonHandler(downButton);
		
		Image copyButtonImg = new Image(DWOplayer.DWO_BUNDLE.pijlcopy().getSafeUri());
		copyButtonImg.getElement().getStyle().setMargin(2, Unit.PX);
		copyButton = new TouchButton();
		copyButton.add(copyButtonImg);
		copyButton.getElement().getStyle().setFloat(Style.Float.RIGHT);
		addCopyButtonHandler(copyButton);
		copyButton.setVisible(!linStrategieVersie && !bordjesMethode && !substitutieVak);

		Image closeButtonImg = new Image(DWOplayer.DWO_BUNDLE.closebutton().getSafeUri());
		closeButtonImg.getElement().getStyle().setMargin(2, Unit.PX);
		closeButton = new TouchButton();
		closeButton.add(closeButtonImg);
		closeButton.getElement().getStyle().setFloat(Style.Float.RIGHT);
		addCloseButtonHandler(closeButton);
		closeButton.setVisible(substitutieVak);
		
		rmKnop = new TouchButton();
		Image rmImage = new Image(DWOplayer.DWO_BUNDLE.rmknop().getSafeUri());
		rmKnop.add(rmImage);
		Style style = rmKnop.getElement().getStyle();
		style.setFloat(Style.Float.RIGHT);
		style.setPadding(5, Style.Unit.PX);
		addRmKnopHandler(rmKnop);
		rmKnop.setVisible(rmknop);
		
		//FIXME: hoe onderscheid maken tussen Noordhoff en gewone DWO?
		abcKnop = new FormuleButton("abc", 1);
		abcKnop.getElement().getStyle().setFloat(Style.Float.RIGHT);
		addAbcButtonHandler(abcKnop);
		
		//Image subKnopImg = new Image(DWOplayer.DWO_BUNDLE.subknop().getSafeUri());
		subKnop = new FormuleButton("sub", FormuleButton.BEWERKINGSKNOP);
		subKnop.getElement().getStyle().setFloat(Style.Float.RIGHT);
		addSubButtonHandler(subKnop);
		
		plusKnop = new FormuleButton("plus", FormuleButton.BEWERKINGSKNOP);
		minKnop = new FormuleButton("min", FormuleButton.BEWERKINGSKNOP);
		maalKnop = new FormuleButton("maal", FormuleButton.BEWERKINGSKNOP);
		deelKnop = new FormuleButton("deel", FormuleButton.BEWERKINGSKNOP);
		deelKnop.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
		haakjesKnop = new FormuleButton("haakjesweg", FormuleButton.BEWERKINGSKNOP);
		herleidKnop = new FormuleButton("herleid", FormuleButton.BEWERKINGSKNOP);
		ontbindKnop = new FormuleButton("ontbind", FormuleButton.BEWERKINGSKNOP);
		ontbindKnop.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
		splitsKnop = new FormuleButton("splits", FormuleButton.BEWERKINGSKNOP);
		wortelBewerkKnop = new FormuleButton("wortelbewerk", FormuleButton.BEWERKINGSKNOP);
		
		plusKnop.getElement().getStyle().setFloat(Style.Float.LEFT);
		minKnop.getElement().getStyle().setFloat(Style.Float.LEFT);
		maalKnop.getElement().getStyle().setFloat(Style.Float.LEFT);
		deelKnop.getElement().getStyle().setFloat(Style.Float.LEFT);
		haakjesKnop.getElement().getStyle().setFloat(Style.Float.LEFT);
		herleidKnop.getElement().getStyle().setFloat(Style.Float.LEFT);
		ontbindKnop.getElement().getStyle().setFloat(Style.Float.LEFT);
		splitsKnop.getElement().getStyle().setFloat(Style.Float.LEFT);
		wortelBewerkKnop.getElement().getStyle().setFloat(Style.Float.LEFT);
		
		addPlusButtonHandler(plusKnop);
		addMinButtonHandler(minKnop);
		addMaalButtonHandler(maalKnop);
		addDeelButtonHandler(deelKnop);
		addHaakjesButtonHandler(haakjesKnop);
		addHerleidButtonHandler(herleidKnop);
		addOntbindButtonHandler(ontbindKnop);
		addSplitsButtonHandler(splitsKnop);
		addWortelButtonHandler(wortelBewerkKnop);
		
		if(substitutieVak)
			headerPanel.add(closeButton);
		if(!(linStrategieVersie || bordjesMethode))
		{	if(!substitutieVak)
				headerPanel.add(copyButton);
			headerPanel.add(downButton);
		}
		headerPanel.add(terugButton);
		if(rmknop)headerPanel.add(rmKnop);
		headerPanel.add(subKnop);
		headerPanel.add(abcKnop);
		headerPanel.add(plusKnop);
		headerPanel.add(minKnop);
		headerPanel.add(maalKnop);
		headerPanel.add(deelKnop);
		headerPanel.add(haakjesKnop);
		headerPanel.add(herleidKnop);
		headerPanel.add(ontbindKnop);
		headerPanel.add(splitsKnop);
		headerPanel.add(wortelBewerkKnop);
		mainPanel.add(headerPanel);
		
		abcKnop.setVisible(abcVisible);
		subKnop.setVisible(subVisible);
		plusKnop.setVisible(bewerkingKnoppen);
		minKnop.setVisible(bewerkingKnoppen);
		maalKnop.setVisible(bewerkingKnoppen);
		deelKnop.setVisible(bewerkingKnoppen);
		haakjesKnop.setVisible(bewerkingKnoppen);
		herleidKnop.setVisible(bewerkingKnoppen);
		ontbindKnop.setVisible(bewerkingKnoppenExtra);
		splitsKnop.setVisible(bewerkingKnoppenExtra);
		wortelBewerkKnop.setVisible(bewerkingKnoppenExtra);
		
		sp = new ScrollPanel();
		sp.setPixelSize(breedte-5, hoogte-50 + 20); // waar komt die 50 vandaan, er kan nog 20 pixels bij
		sp.getElement().getStyle().setOverflow(Overflow.AUTO);
		sp.getElement().getStyle().setFloat(Style.Float.LEFT);

		contentPanel = new LayoutPanel();
		
		feedbackPanel = new TekstVak();
		feedbackPanel.setSize(breedte - 25, feedbackPanelHeight);
		feedbackPanel.setFontSize(XMLView.getDefaultFontSize());
		feedbackPanel.setFontName(XMLView.getDefaultFontName());
		feedbackPanel.setColor(CssColor.make("black"));
		feedbackPanel.setMarges(5, 0);
		feedbackPanel.setCentering(false, true);
		feedbackPanel.setPasHoogteBreedteAan(true, false);
		feedbackPanel.getElement().getStyle().setBackgroundColor("#FFFFDD");

		// checkimg wordt in zetGoedFoutEditor() aan de parent van de betreffende editor toegevoegd
		checkimg = new Image(FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
		checkimg.setVisible(false);
		
		sp.setWidget(contentPanel);
		mainPanel.add(sp);
		
		if(subExtra && !substitutieVak)
		{
			HashMap<String, Object> hh = new HashMap<String,Object>();
			hh.put("volledigeBreedte", Boolean.FALSE);
			hh.put("breedte", breedte);
			hh.put("hoogte" , 150); 
			hh.put("breedte", 220);
			HashMap<String, Object> subLaunchState = new HashMap<String, Object>();
			subLaunchState.putAll(launchState);
			subLaunchState.put("substitutieVak", Boolean.TRUE);
			hh.put("interactiePanelLaunchState", subLaunchState);
			gebruikersSubstitutiesVak = new FormuleEditorWithSteps(hh, true, randomVarNamen, randomVarWaarden, null);
			
		}
		
		
			
		


		LayoutPanel stepPanel = maakNieuwStapPanel();
		//stepPanel.setWidth((breedte - 5) + "px");
		//layoutStepPanel(stepPanel);
//		highLight(stepPanel, true);
		if (hasPrefix)
			addPrefixViewer(stepPanel);
			//stepPanel.add(prefixViewer);

		if (!startString.equals("$f@") && stapNr == 0) //ik denk niet dat het stapNr hier al iets anders kan zijn dan 0..
		{
			if(bordjesMethode && isVergelijkingVak) {
				// convert to stringStrikt.
				VergelijkingMeerv e = FormuleParser.parseVergelijking(startString);
				startString = "$f"+ e.toStringStrikt() + "@";
			}
			
			if (!isVergelijkingVak && !hasPrefix && (startString.charAt(startString.length() - 2)) != '=')
				startString = startString.substring(0, startString.length() - 1) + "=@";

			FormuleViewer f = new FormuleViewer(prefix.substring(2, prefix.length() - 1) + startString.substring(2, startString.length() - 1));
			f.setFont(font);
			f.getMainRegel().getCanvas().getElement().getStyle().setMarginLeft(23, Unit.PX);

			viewers.add(f);
			latest_answer_viewer = f;
			addFormuleViewer(f, stepPanel);
			
			if(bordjesMethode){
				Logger.getLogger("FormuleEditorWithStep").info("bordjesmethode");
				addFormulePanelListeners((TouchPanel) f.getAsPanel(), f);
			}
			contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, f.getHeight(), Style.Unit.PX);
			
			stepsForLinKwad = false;
			try
			{
				VergelijkingMeerv v = FormuleParser.parseVergelijking(startString); 
				int graad = Algebra.geefCoefficienten(v.geefVergelijking(0)).length;
				stepsForLinKwad = graad < 4;
			}
			catch(Exception e){}
			
			
			if(!(linStrategieVersie || linOefenVersie || bordjesMethode))
			{	pijlVak = new PijlVak("", this, false);
				int y = stepPanelY + f.getHeight()/2;
				//if(pijl)
				contentPanel.add(pijlVak);
				contentPanel.setWidgetRightWidth(pijlVak, 0, Style.Unit.PX, pijlX, Style.Unit.PX);
				contentPanel.setWidgetTopHeight(pijlVak, y, Style.Unit.PX, pijlVak.getHeight(), Style.Unit.PX);
				pijlVak.setPijlVisible(pijl);
				
				pijlVakken.add(pijlVak);
				
				pijlVak.paintComponent();
				
				stapNr++;
				stepPanelY += f.getHeight() + stapH;
	
				LayoutPanel stepPanelNew = maakNieuwStapPanel();
				editor = addNewEditor(stepPanelNew);
				contentPanel.setWidgetTopHeight(stepPanelNew, stepPanelY, Style.Unit.PX, hoogteStepPanelMetEditor(), Style.Unit.PX);
				stapOk = false;
			}
		}
		else
		{
			editor = addNewEditor(stepPanel);
			contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, hoogteStepPanelMetEditor(), Style.Unit.PX);
		}

		contentPanel.getElement().addClassName("insert_formule_steps");

		//zorgen dat cursor niet direct, maar pas bij focus verschijnt
		if(editor != null)
			editor.setCurrentElementRepaint();
		
	}

	public void zetInstellingen(ObjectMap instellingen2)
	{
		this.instellingen = instellingen2;
	}
	
	
	public void addPrefixViewer(LayoutPanel p)
	{
//		editor.getPrefixPanel().add(prefixViewer.getAsPanel());
		Widget w = prefixViewer.getAsPanel();
		p.add(w);
		p.setWidgetLeftWidth(w, 23, Style.Unit.PX, prefixViewer.getWidth(), Style.Unit.PX);
		p.setWidgetTopHeight(w, 0, Style.Unit.PX, prefixViewer.getHeight(), Style.Unit.PX);
	}
	
	public void addFormuleViewer(FormuleViewer fv, LayoutPanel p)
	{
		Widget w = fv.getAsPanel();
		p.add(w);
		p.setWidgetLeftWidth(w, 0, Style.Unit.PX, fv.getWidth() + 23, Style.Unit.PX);
		p.setWidgetTopHeight(w, 0, Style.Unit.PX, fv.getHeightWithImage(), Style.Unit.PX);
	}

	public String extractStartString(HashMap<String, Object> h)
	{
		String result = "$f@";
		String answer = "";

		if (h.get("antwoordString") != null)
			answer = (String) h.get("antwoordString");

		for (int i = 0; i < answer.length(); i++)
		{
			if (answer.charAt(i) == '#')
			{
				result = answer.substring(0, i + 1);
				break;
			}
		}
		return result;
	}

	public FormuleEditor getEditor()
	{
		return editor;
	}
	
	public void lastStep(String useranswer, boolean show, boolean setState)
	{
		LayoutPanel current = stepPanels.get(stepPanels.size() - 1);
		current.remove(editor.getAsPanel());
		if (viewers.size() > stepPanels.size() - 1)
		{	
			current.remove(viewers.get(stepPanels.size() - 1).getAsPanel());
			viewers.remove(stepPanels.size() - 1);
		}
		editor = null;
		checkimg.setVisible(false);
		if (hasPrefix)
			current.remove(prefixViewer.getAsPanel());
		terugButton.getElement().getStyle().setVisibility(Visibility.VISIBLE);

		FormuleViewer fv = new FormuleViewer(prefix.substring(2, prefix.length() - 1) + useranswer.substring(2, useranswer.length() - 1));
		fv.setFont(font);
		
		if (!eigenOpdr || stapNr > 0)
		{
			fv.showResult(FormuleViewer.CORRECT);
		}
		else
		{
			fv.showResult(FormuleViewer.NONE);
		}
		
		if (latest_answer_viewer != null && !(isToets() && show && !stepsForLinKwad))
		{
			latest_answer_viewer.showResult(FormuleViewer.NONE);
		}
		latest_answer_viewer = fv;
		viewers.add(fv);
		contentPanel.remove(feedbackPanel);
		addFormuleViewer(fv, current);
		
		if (hasFeedback)
		{	
			contentPanel.add(feedbackPanel);
			contentPanel.setWidgetLeftRight(feedbackPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(feedbackPanel, stepPanelY + fv.getHeightWithImage(), Style.Unit.PX, feedbackPanelHeight, Style.Unit.PX); 
		}
		nagekeken = true;
		correct = Boolean.TRUE;
		score = scoreMax;
		if (mode == OpdrNavIF.OEFENEN_STRAFPUNTEN)
			score = Math.max(0, scoreMax - errorCount * foutStraf);
		if (!setState)
			comRoot.setChanged(false);
	}
	
	public void addStep(String useranswer, boolean show, boolean setState)
	{
//		if (linStrategieVersie || linOefenVersie) // in zelftoets mis ik alle tussenstappen hierdoor...
//			return;
		voegRegelToe(useranswer, show, setState);
	}
	
	public void zetPijlVakMaat(PijlVak pijlVak)
	{
		contentPanel.setWidgetTopHeight(pijlVak, pijlVak.getAbsoluteTop() - contentPanel.getAbsoluteTop(), Style.Unit.PX, pijlVak.getHeight(), Style.Unit.PX);
	}

	public void voegRegelToe(String useranswer, boolean show, boolean setState)
	{
		sluitRegelAf(useranswer, show, setState);
		FormuleViewer fv = viewers.get(viewers.size() - 1);
		
		pijlVak = new PijlVak("", this, false); 
		int y = stepPanelY + fv.getHeightWithImage()/2;
		//if (pijl)
		contentPanel.add(pijlVak);
		contentPanel.setWidgetRightWidth(pijlVak, 0, Style.Unit.PX, pijlX, Style.Unit.PX);
		contentPanel.setWidgetTopHeight(pijlVak, y, Style.Unit.PX, pijlVak.getHeight(), Style.Unit.PX);
		pijlVak.setPijlVisible(pijl);
		pijlVakken.add(pijlVak);
		pijlVak.paintComponent();
		
		LayoutPanel stepPanel = maakNieuwStapPanel();
		//if(!setState)
			stapNr++;

		if (hasFeedback)
		{	feedbackPanel.removeFromParent();
			contentPanel.add(feedbackPanel);
			contentPanel.setWidgetLeftRight(feedbackPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(feedbackPanel, stepPanelY +  fv.getHeightWithImage(), Style.Unit.PX, feedbackPanelHeight, Style.Unit.PX); 
		}

		editor = addNewEditor(stepPanel);
		stepPanelY += fv.getHeightWithImage() + stapH;
		contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, hoogteStepPanelMetEditor(), Style.Unit.PX);
		requestFocus();
		checkimg.setVisible(false);
		
		//nodig om te zorgen dat gebruikerssubstituties voor pijlen blijven staan:
		if(gebruikersSubstitutiesVak != null && gebruikersSubstitutiesVak.asWidget().isAttached())
			voegGebruikersSubstitutiesVakToe();
		
		scrollToBottom();
	}
	
	public void sluitRegelAf(String useranswer, boolean show, boolean setState)
	{
		stapOk = false;
		nagekeken = false;
		correct = Boolean.FALSE;//moet correct hier niet null zijn?
		contentPanel.remove(feedbackPanel);
		vervangEditorDoorViewer(useranswer, show, setState);
		terugButton.getElement().getStyle().setVisibility(Visibility.VISIBLE);
	}
	
	public void voegGebruikersSubstitutiesVakToe()
	{
		gebruikersSubstitutiesVak.asWidget().removeFromParent();
		contentPanel.add(gebruikersSubstitutiesVak);
		contentPanel.setWidgetRightWidth(gebruikersSubstitutiesVak, 21, Style.Unit.PX, gebruikersSubstitutiesVak.getWidth(), Style.Unit.PX);
		contentPanel.setWidgetTopHeight(gebruikersSubstitutiesVak, 27, Style.Unit.PX, gebruikersSubstitutiesVak.getHeight(), Style.Unit.PX);
	}
	
	public void addBordjesStap()
	{
		String select = viewers.get(viewers.size() - 1).getSelectionString();
		if (select == null || select.length() == 0)
			return;
		
		if (editor == null)
			addStep("$f" + latest_answer_viewer.toString() + "@", !isToets(), false);
		
		if(!editor.toString().equals(select + "="))
		{
			editor.clearAll();
			editor.insert(select);
			editor.insert("=");
			editor.paint();
			requestFocus();
		}
		
	}
	
	public void resize()
	{	LayoutPanel current = stepPanels.get(stepPanels.size() - 1);
		if(editor != null && editor.getAsPanel().getParent() == current)
		{	if(current.getParent() == contentPanel) // FIXME why? 
				contentPanel.setWidgetTopHeight(current, stepPanelY, Style.Unit.PX, hoogteStepPanelMetEditor(), Style.Unit.PX);
			current.setWidgetTopHeight(editor.getAsPanel(), hasPrefix?Math.max(prefixViewer.getAsHoogte() - editor.getAsHoogte(), 0):0, Style.Unit.PX, hoogteStepPanelMetEditor(), Style.Unit.PX);
			current.setWidgetLeftWidth(editor.getAsPanel(), hasPrefix?prefixViewer.getWidth() + 23:23, Style.Unit.PX, editor.getMainRegel().getWidth(), Style.Unit.PX);
			if(hasPrefix)
			{	current.setWidgetTopHeight(prefixViewer.getAsPanel(), Math.max(editor.getMainRegel().getAsHoogte() - prefixViewer.getMainRegel().getAsHoogte(), 0), Style.Unit.PX, prefixViewer.getHeight(), Style.Unit.PX);
			}
			if(feedbackPanel.isAttached())
			{
				contentPanel.setWidgetTopHeight(feedbackPanel, stepPanelY + hoogteStepPanelMetEditor(), Style.Unit.PX, feedbackPanelHeight, Style.Unit.PX);
			}
		}
		scrollToBottom();
	}

	public void copyStep()
	{
		if (correct != Boolean.TRUE || isToets()) // FIXME copystep in mode 2 of 3
		{
			String currentTekst = "";
			if (editor == null)
			{	
				if (latest_answer_viewer != null)
					voegRegelToe("$f" + latest_answer_viewer.toString() + "@", !isToets(), false);
				else
					voegRegelToe("$f@", !isToets(), false);
			}
			if (stapNr > 0)
			{
				editor.getMainRegel().deleteAll();
				currentTekst = viewers.get(stapNr - 1).toString();
				if (hasPrefix)
					currentTekst = removePrefix(currentTekst);
				currentTekst = removeIsTeken(currentTekst);
				editor.insert(currentTekst);
				requestFocus();
			}
			terugButton.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		}
	}
	
	public void closeEditor()
	{
		this.asWidget().removeFromParent();
		
	}
	
	public void downStep()
	{
		if (nagekeken)
			isVeranderdNaNakijken = true;

		if (correct != Boolean.TRUE || isToets())
		{
			if (stapOk || isToets() || substitutieVak)
			{	
				String userAnswer = "";
				if (editor == null)
				{
					userAnswer = latest_answer_viewer.toString();
				}
				else
				{
					userAnswer = editor.toString();
				}

				if (!"".equals(userAnswer))
				{
					voegRegelToe("$f" + userAnswer + "@", !isToets(), false);
				}
			}
		}
	}

	/**
	 * Retourneert true als zelftoets of eindtoets.
	 * 
	 * @return
	 */
	private boolean isToets() 
	{
		return mode == OpdrNavIF.ZELFTOETS || mode == OpdrNavIF.EINDTOETS;
	}
	
	public boolean isUitgeklapt()
	{
		return isUitgeklapt;
	}

	public boolean isNagekeken()
	{
		return nagekeken;
	}

	public boolean isBoss()
	{
		return isBoss;
	}

	public void backStep(boolean setState)
	{
		nagekeken = false;
		correct = Boolean.FALSE;
		LayoutPanel current = stepPanels.get(stapNr);
		//deze wordt null als je in een pijlvak zit. 
		
		if (stapNr > 0 || !hasStartString)
		{
			if (viewers.size() == stapNr + 1)
			{	
				current.remove(viewers.get(viewers.size() - 1).getAsPanel());
				viewers.remove(stapNr);
			}
			else
			{	
				current.remove(editor.getAsPanel());
				if(hasPrefix)
					current.remove(prefixViewer.getAsPanel());
				checkimg.setVisible(false);
			}
			if (stapNr > 0)
			{	
				stepPanels.remove(stapNr);
				current = stepPanels.get(stapNr - 1);
				stepPanelY -= stapH + viewers.get(viewers.size() - 1).getHeight();
				latest_answer_viewer = viewers.get(viewers.size() - 1);
			}
			else
				latest_answer_viewer = null; 
			
			haalPijlVakWeg();
			
			if (feedbackPanel.isAttached())
			{	
				contentPanel.remove(feedbackPanel);
			}
			
			if (stapNr > 1 || (stapNr > 0 && !hasStartString))
			{	
				String currentTekst = viewers.get(viewers.size() - 1).toString();
				if (hasPrefix)
				{	
					currentTekst = removePrefix(currentTekst);
				}
				
				if (!isVergelijkingVak)
				{
					currentTekst = removeIsTeken(currentTekst);
				}
				
				if (!linStrategieVersie && !bordjesMethode)
				{	
					current.remove(viewers.get(viewers.size() - 1).getAsPanel());
					editor = addNewEditor(current);
					editor.insert(currentTekst);
					viewers.remove(viewers.size() - 1);
					if (viewers.size() > 0)
						latest_answer_viewer = viewers.get(viewers.size() - 1);
				}
				else
				{
					editor = null; // fix selecteren bordjesmethode
				}
			}
			else if (!hasStartString) //stapNr is nu 0, je zit dus in eerste regel
			{
				if (!linStrategieVersie && !bordjesMethode)
				{
					editor = addNewEditor(current);
				}
				else
				{
					editor = null; // fix selecteren bordjesmethode
				}
			}
			else
			{	editor = null;
				checkimg.setVisible(false);
			}
			if(stapNr > 0)
				stapNr--;
			stapOk = false;
			
			if(stapNr == 0 && hasStartString)
				terugButton.getElement().getStyle().setVisibility(Visibility.HIDDEN);
				
			if (!isToets() && (stapNr > 0 || !hasStartString) && !linStrategieVersie && !bordjesMethode)
			{
				kijkNa(true, true, setState);
				requestFocus();
			}
			else
			{
				hasFeedback = false;
				stapOk = true;
				if(bordjesMethode)
					viewers.get(viewers.size() - 1).setSelectable(true);
			}
		}
		else 
		{
			haalPijlVakWeg();
			stapOk = true;
		}
	}

	private boolean focusEnabled = true;

	private boolean rmknop;

	private void requestFocus() {
		if(focusEnabled && editor != null)
			editor.requestFocus();
	}		
	
	// save and restore focusEnabled
	
	boolean setFocusEnabled(boolean enabled) {
		boolean old = focusEnabled;
		focusEnabled = enabled;
		return old;
	}
	
	public void haalPijlVakWeg()
	{
		if(pijlVak != null && pijlVak.getParent() != null)
		{	
			// vinkje weg
			if (imagesStappen != null && imagesStappen.size() > 0)
			{
				contentPanel.remove(imagesStappen.get(imagesStappen.size() - 1));
				imagesStappen.remove(imagesStappen.size() - 1);
			}
			
			contentPanel.remove(pijlVak);
			
			if (pijlVak.geefOperator().equals("sub"))
				substitutie = null;
			pijlVakken.remove(pijlVakken.size() - 1);
			if (pijlVakken.size() > 0)
				pijlVak = pijlVakken.get(pijlVakken.size() - 1);
			else
				pijlVak = null;
		}
	}
	

	public void setFeedback(String feedback)
	{
		this.feedback = feedback;
		hasFeedback = !"".equals(feedback.trim());
		TekstBuffer b = new TekstBuffer();
		//Ik denk dat randomvariabelen bij initialisatie feedback al zijn ingevuld. 
//		try{
//			feedback = FormuleParser.randomizeTekstVakString(feedback, randomVarNamen, randomVarWaarden);
//		}
//		catch(Exception e){}
		ArrayList<Object> feedbackList = b.convertTekst(feedback, null, false);
		feedbackPanel.clear();
		feedbackPanel.setSize(breedte - 10, feedbackPanelHeight);
		feedbackPanel.setObjects(feedbackList);
		feedbackPanel.resize();
		feedbackPanelHeight = feedbackPanel.getHeight();
	}
	
	public String getFeedback()
	{
		return feedback;
	}

	public void setAndAddFeedback(String feedback)
	{
		setFeedback(feedback);
				
		contentPanel.remove(feedbackPanel);
		if (hasFeedback)
		{	contentPanel.add(feedbackPanel);
			contentPanel.setWidgetLeftRight(feedbackPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			int height = 23;
			if(editor != null && hoogteStepPanelMetEditor() > 23)
				height = hoogteStepPanelMetEditor();
			else if(latest_answer_viewer != null && latest_answer_viewer.getHeight() > 23)
				height = latest_answer_viewer.getHeight();
			contentPanel.setWidgetTopHeight(feedbackPanel, stepPanelY + height, Style.Unit.PX, feedbackPanelHeight, Style.Unit.PX); 
		}
		if(editor != null)
		{
			zetGoedFoutEditor(editor.getGoedHalfFout());
		}
		scrollToBottom();
	}

	public Panel getAsPanel()
	{
		return mainPanel;
	}

	public Boolean getEigenOpdr()
	{
		return eigenOpdr;
	}
	
	public void zetEditorTerug()
	{
		stapNr++;
		terugButton.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		LayoutPanel stepPanel = maakNieuwStapPanel();
		FormuleViewer fv = viewers.get(viewers.size() - 1);

		if (hasFeedback)
		{	feedbackPanel.removeFromParent();
		}

		if(linStrategieVersie)
		{	FormuleViewer viewer = new FormuleViewer(fv.toString());
			viewer.setFont(font);
			viewer.getMainRegel().getCanvas().getElement().getStyle().setMarginLeft(23, Unit.PX);
			viewers.add(viewer);
			addFormuleViewer(viewer, stepPanel);
		}
		else
		{
			editor = addNewEditor(stepPanel);
			
		}
		stepPanelY += fv.getHeightWithImage() + stapH;
		if(linStrategieVersie)
			contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, viewers.get(viewers.size()-1).getHeight(), Style.Unit.PX);
		else
		{	contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, hoogteStepPanelMetEditor(), Style.Unit.PX);
			requestFocus();
		}
		scrollToBottom();
	}
	
	public String getLatestAnswer()
	{
		if(latest_answer_viewer != null)
			return latest_answer_viewer.toString();
		return null;
	}
	
	public String getOperator()
	{
		return pijlVakken.get(pijlVakken.size() - 1).geefOperator();
	}
	
	public Expressie getOperatorExpressie()
	{
		String expString = pijlVakken.get(pijlVakken.size() - 1).geefExpressieString();
		return FormuleParser.geefExpressie(expString);
	}

	public String removePrefix(String s)
	{
		if ((s != null) && (s.length() > 0))
		{
			int index = s.indexOf("=");
			if (index == -1)
				index = s.indexOf("\u2248");
			if (index > -1)
			{
				s = s.substring(index + 1);
			}
		}
		
		return s;
	}

	public String removeIsTeken(String s)
	{
		if ((s != null) && (s.length() > 0) && (s.charAt(s.length() - 1) == '=' || s.charAt(s.length() - 1) == '\u2248'))
		{
			int isIndex = s.length() - 1;
			s = s.substring(0, isIndex);
		}
		return s;
	}
	
	/**
	 * Removes the codes marking a formula, 
	 * i.e., "$f" at the start of the string 
	 * and "@" at the end of the string.
	 * If the given string contains no codes, the original string is returned.
	 * 
	 * @param s
	 * @return
	 */
	public String removeFormulaCodes(String s)
	{
		String formulaStartString = "$f"; 
		String formulaEndString = "@";
		
		if (s.startsWith(formulaStartString))
		{
			s = s.substring(2); // trim "$f"
		}
		
		if (s.endsWith(formulaEndString))
		{
			int lastIndex = s.length() - 1;
			s = s.substring(0, lastIndex); // trim "@"
		}
		
		return s;
	}
	
	/**
	 * Adds the codes marking a formula, 
	 * i.e., "$f" at the start of the string 
	 * and "@" at the end of the string.
	 * If the given string already contains the codes, 
	 * the original string is returned.
	 * 
	 * @param s
	 * @return
	 */
	public String addFormulaCodes(String s)
	{
		String formulaStartString = "$f"; 
		String formulaEndString = "@";
		
		if (!s.startsWith(formulaStartString))
		{
			s = formulaStartString + s;
		}
		
		if (!s.endsWith(formulaEndString))
		{
			s = s + formulaEndString;
		}
		
		return s;
	}
	
	public void setParentRegel(TekstRegel parentRegel) {
		setFont(parentRegel);
	}
	
	public void setFont(TekstRegel parentRegel)
	{
		//als geen fontOvererving, dan hoeft er niets te gebeuren.
		if(!fontOvererving)
			return;
		font = FormuleFont.createFromFontSize(parentRegel.getFont().getFontSize(), false);
		if(!FormuleFont.formTimes)
			font.setFont(parentRegel.getFont().getFont());
		if(prefixViewer != null)
			prefixViewer.setFont(font);
		for(int i = 0; i < viewers.size(); i++)
			viewers.get(i).setFont(font);
		if(editor != null)
		{
			editor.setFont(font);
			editor.setDefaultFont(font);
		}
		
		stepPanelY = 0;
		for(int i = 0; i < viewers.size(); i++)
		{
			LayoutPanel p = stepPanels.get(i);
			FormuleViewer v = viewers.get(i);
			p.clear();
			addFormuleViewer(v, p);
			contentPanel.setWidgetTopHeight(p, stepPanelY, Style.Unit.PX, v.getHeight(), Style.Unit.PX);
			stepPanelY += v.getHeight() + stapH;
		}
		if(editor != null)
		{	contentPanel.setWidgetTopHeight(stepPanels.get(stepPanels.size() - 1), stepPanelY, Style.Unit.PX, hoogteStepPanelMetEditor(), Style.Unit.PX);
			editor.setCurrentElementRepaint();
		}
	}
	
	public void verhoogErrorCount()
	{
		//Alleen maar aanroepen vanuit FormuleEditorWithAnswer.verhoogErrorCount(); Dan weet je zeker dat er iets veranderd is.
		errorCount++;
	}

	public FormuleEditorWithAnswer addNewEditor(LayoutPanel p)
	{
		//Als nodig: prefixViewer toevoegen
		if(hasPrefix)
		{
			
			p.clear();
			addPrefixViewer(p);
			
		}
		FormuleEditorWithAnswer editor = editorInstance();
		editor.zetMode(mode);
		editor.setFormuleToolBijFocus(true);
		editor.setFont(font);
		editor.setDefaultFont(font);
		editor.setCurrent(0, 0);
//		int width = editor.getMainRegel().getWidth();
//		if(hasPrefix)
//		{	editor.getPrefixPanel().add(prefixViewer.getAsPanel());
//			width += prefixViewer.getWidth();
//		}
		TouchPanel tp = (TouchPanel) editor.getAsPanel();
		p.add(tp);
		p.setWidgetLeftWidth(tp, hasPrefix?prefixViewer.getWidth() + 23:23, Style.Unit.PX, editor.getMainRegel().getWidth(), Style.Unit.PX);
		p.setWidgetTopHeight(tp, hasPrefix?Math.max(prefixViewer.getAsHoogte() - editor.getAsHoogte(), 0):0, Style.Unit.PX, Math.max(hasPrefix?prefixViewer.getHeight():0, editor.getMainRegel().getHeight()), Style.Unit.PX);
		addFormulePanelListeners(tp, editor);
		return editor;
	}
	
	public LayoutPanel maakNieuwStapPanel()
	{
		final LayoutPanel panel = new LayoutPanel();
		panel.setWidth((breedte - 5) + "px");
		panel.addDomHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				if(editor != null)
				{	
					if (panel.equals(editor.getAsPanel().getParent())) // parent kan null zijn in rare gevallen
					{	
						if (event.getClientX() > editor.getAsPanel().getAbsoluteLeft() + editor.getMainRegel().getWidth())	
						{	
							requestFocus();
							editor.startSelection(editor.getMainRegel().getWidth(), 0);
							editor.endSelection(editor.getMainRegel().getWidth(), 0);
						}
						else if (event.getClientX() < editor.getAsPanel().getAbsoluteLeft())
						{	
							requestFocus();
							editor.startSelection(0, 0);
							editor.endSelection(0, 0);
							editor.cursorToLeft();
						}
						else if (!editor.getKeyboard().getEditor().equals(editor))
						{	
							requestFocus();
							editor.setCurrentElementRepaint(editor.getCurrentRegel());
						}
					}
				}
			}
			
		}, ClickEvent.getType());
		
		stepPanels.add(panel);
		contentPanel.add(panel);
		contentPanel.setWidgetLeftRight(panel, 5, Style.Unit.PX, 5, Style.Unit.PX);
		return panel;
	}

	static class FormuleEditorWithCalculator extends FormuleEditorWithAnswer {

		public FormuleEditorWithCalculator(HashMap<String, Object> h,
				boolean isVergelijkingVak, FormuleEditorWithSteps fe,
				String[] randomVarNamen,
				HashMap<String, Number> randomVarWaarden,
				AntwoordVakChecker avChecker) {
			super(h, isVergelijkingVak, fe, randomVarNamen, randomVarWaarden, avChecker);
		}

		@Override
		public void enter() {
			fe.berekenStap();
		}
		
	}
	
	
	FormuleEditorWithAnswer editorInstance() {
		if(rmknop && !teltMee && !check)  // en een andere conditie?
			return new FormuleEditorWithCalculator(h, isVergelijkingVak, this, randomVarNamen, randomVarWaarden, avChecker);
		
		return new FormuleEditorWithAnswer(h, isVergelijkingVak, this, randomVarNamen, randomVarWaarden, avChecker);
		
	}

	private void addButtonHandler(final TouchButton tb)
	{
		tb.addTouchStartHandler(new TouchStartHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				if (nagekeken)
					zetIsVeranderdNaNakijken(true);
				backStep(false);
			}
		});
	}
	
	private void addAbcButtonHandler(final TouchButton tb)
	{	tb.addTouchStartHandler(new TouchStartHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("abc");
			}
		});
	}
	
	private void addSubButtonHandler(final TouchButton tb)
	{	tb.addTouchStartHandler(new TouchStartHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	if(subExtra)
				{
					voegGebruikersSubstitutiesVakToe();
					
				}
				else
				{
					if(substitutie == null)
						maakStap("sub");
					
				}
			}
		});
	}
	
	private void addPlusButtonHandler(final TouchButton tb)
	{	tb.addTouchStartHandler(new TouchStartHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("+");
			}
		});
	}
	
	private void addMinButtonHandler(final TouchButton tb)
	{	tb.addTouchStartHandler(new TouchStartHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("-");
			}
		});
	}
	
	private void addMaalButtonHandler(final TouchButton tb)
	{	tb.addTouchStartHandler(new TouchStartHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("*");
			}
		});
	}
	
	private void addDeelButtonHandler(final TouchButton tb)
	{	tb.addTouchStartHandler(new TouchStartHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap(":");
			}
		});
	}
	
	private void addHaakjesButtonHandler(final TouchButton tb)
	{	tb.addTouchStartHandler(new TouchStartHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("haakjes");
				maakBewerkingStap();
			}
		});
	}
	
	private void addHerleidButtonHandler(final TouchButton tb)
	{	tb.addTouchStartHandler(new TouchStartHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("herleid");
				maakBewerkingStap();
			}
		});
	}
	
	private void addOntbindButtonHandler(final TouchButton tb)
	{	tb.addTouchStartHandler(new TouchStartHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("ontbind");
				maakBewerkingStap();
			}
		});
	}
	
	private void addSplitsButtonHandler(final TouchButton tb)
	{	tb.addTouchStartHandler(new TouchStartHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("splits");
				maakBewerkingStap();
			}
		});
	}
	
	private void addWortelButtonHandler(final TouchButton tb)
	{	tb.addTouchStartHandler(new TouchStartHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("wortel");
				maakBewerkingStap();
			}
		});
	}
	
	private void addDownButtonHandler(final TouchButton tb)
	{
		tb.addTouchStartHandler(new TouchStartHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				downStep();
			}
		});
	}
	
	private void addCopyButtonHandler(final TouchButton tb)
	{
		tb.addTouchStartHandler(new TouchStartHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				copyStep();
			}
		});
	}
	
	private void addCloseButtonHandler(final TouchButton tb)
	{
		tb.addTouchStartHandler(new TouchStartHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				closeEditor();
			}
		});
	}

	private void addRmKnopHandler(final TouchButton rmKnop) {
		rmKnop.addTouchStartHandler(new TouchStartHandler() {

			@Override
			public void onTouchStart(TouchStartEvent event) {
				berekenStap();
			}
			
		});
	}
/*
			Expressie antwoord = FormuleParser.geefExpressie("$f" + x + "@");
			viewer.getAsPanel().removeFromParent();
			x="";
			if(antwoord != null) 
			{
				double waarde = antwoord.geefWaarde();
				double afgerond = new DecRound(new BasisExpressie(waarde), new BasisExpressie(3)).geefWaarde();
				boolean afgerondOp3 = 
						! Algebra.isGelijkDouble(waarde, afgerond, MARGE);
				if(Double.isNaN(waarde))
				{	x = "?";
					btn.setText(EXACT);
				} else
				{ 
					btn.setText(afgerondOp3 ? CIRCA : EXACT);
					if(op3)
					{
						x = Expressie.df3.format(waarde);
					}
					else 
					{	
						double abs = Math.abs(waarde);
						if( abs < E_MIN || abs >= E_MAX) 
						{
							x = Expressie.dfe.format(waarde);						
							x = x.replace("E", "*10$m") + "@";
						} else {
							x = Expressie.df.format(waarde);
						}
					}
				}
	
 */
	static final double E_MAX = 1.0E7;
	static final double E_MIN = 1.0E-3;
	static final double MARGE = 0.00000000000000001;
	
	void berekenStap() {
		
		// checks vooraf....
		if(editor != null) {
			String formule0 = editor.toString();
			if(formule0.isEmpty() && latest_answer_viewer != null)
			{
				formule0= latest_answer_viewer.toString();
			}
// Strip = at end
			if(formule0.endsWith("=")||formule0.endsWith("\u2248"))
				formule0 = formule0.substring(0, formule0.length()-1);
			String formule1;
			Expressie antwoord = FormuleParser.geefExpressie("$f" + formule0 + "@");
			if(antwoord != null && ! (antwoord instanceof BasisExpressie)) {
				double waarde = antwoord.geefWaarde();
				double afgerond = new DecRound(new BasisExpressie(waarde), new BasisExpressie(aantalDecRm)).geefWaarde();
				boolean isAfgerond = 
						! Algebra.isGelijkDouble(waarde, afgerond, MARGE);
				if(!Double.isNaN(waarde))
				{	double abs = Math.abs(afgerond);
					if( abs < E_MIN || abs >= E_MAX) 
					{
						formule1 = Expressie.dfe.format(afgerond);						
						formule1  = formule1.replace("E", "*10$m") + "@";
					} else {
						formule1 = Expressie.df.format(afgerond);
					}
					if(isAfgerond) {
						formule0 += '\u2248';
					}
					if(!editor.toString().isEmpty())
						voegRegelToe("$f" + formule0 +"@", check && !isToets(), false);
					editor.insert(formule1);
				}
				
			}
		}
		
	}


	class BordjesTouchHandler extends FormuleEditorTouchHandler {

		BordjesTouchHandler(FormuleHolder editor) {
			super(editor);
		}
		
		@Override
		public void onTouchStart(TouchStartEvent event)
		{
			super.onTouchStart(event);
			if(editor != null)
				editor.requestFocus();
		}

		@Override
		public void onTouchEnd(TouchEndEvent event) 
		{
			super.onTouchEnd(event);
			
			if (bordjesMethode)
			{
				addBordjesStap();
			}
		}
	}
	
	
	private void addFormulePanelListeners(final TouchPanel tp, final FormuleHolder editor)
	{
		tp.addTouchHandler(new BordjesTouchHandler(editor));
	}
	
	private void freezeViewer(FormuleViewer f)
	{
		f.setSelectable(false);
	}

	@Override
	public HashMap<String, Object> getState()
	{
		int stapNr = 0;
		String[] formuleVakInhouden = null;
		
		String[] pijlVakInhouden = null;
		String[] pijlVakOperatoren = null;
		
		boolean ingevuld = true;
		boolean nagekeken = false;
		boolean isVeranderdNaNakijken = false;
		String antwoordString = "";
		String substitutieString = "";
		String[] gebruikersSubStrings = null;
		int errorCount = 0;
		
		if (editor != null && !isToets())
			//Sietske: Hier wellicht ook beter editor.kijkNa(false, false, false); zie getState FormuleEditorWithAnswer.
			editor.kijkNa();
		
		// zet score/correct als editor nog open staat en gevuld is.
		if ((editor != null && isToets())// && !editor.toString().isEmpty()) // ook berekenen als editor leeg!
			|| editor == null) // als editor null dan wordt in getState() geen kijkNa() gedaan en wordt correct ten onrechte false
		{
			// bepaal score en correct
			bepaalScoreEnCorrect();
		}
		
		stapNr = this.stapNr;
		errorCount = this.errorCount;
		int formuleVakInhoudenSize = stapNr + 1;
		formuleVakInhouden = new String[formuleVakInhoudenSize];
		for (int i = 0; i < formuleVakInhoudenSize; i++)
		{
			if (viewers.size() > i && viewers.get(i) != null)
				formuleVakInhouden[i] = "$f" + getViewerString(i) + "@" ;
			else if (!linStrategieVersie)
			{
				// voeg een lege inhoud toe, maar niet voor de strategieversie 
				formuleVakInhouden[i] = "$f@";
			}
		}
		
		// voor linStrategieVersie kan er een null zijn ivm met te grote stapNr
		
		pijlVakInhouden = new String[stapNr];
		pijlVakOperatoren = new String[stapNr];
		for (int i = 0; i < stapNr && pijlVakken.size() > i; i++) 
		{
			pijlVakInhouden[i] = pijlVakken.get(i).geefExpressieString();
			pijlVakOperatoren[i] = pijlVakken.get(i).geefOperator();
		}
		
		if (editor != null)
			antwoordString = editor.toString();
		
		if (!antwoordString.equals(""))
		{
			this.ingevuld = true;
			formuleVakInhouden[stapNr] = "$f" + antwoordString + "@"; // antwoordstring is laatste inhoud.
		}
		
		if (!hasStartString && !"$f@".equals(formuleVakInhouden[0]))
			this.ingevuld = true;
		if (hasStartString && stapNr > 0 && !"$f@".equals(formuleVakInhouden[1]))
			this.ingevuld = true;
		ingevuld = this.ingevuld;
		
		nagekeken = this.nagekeken;
		isVeranderdNaNakijken = this.isVeranderdNaNakijken;

		if (substitutie != null)
			substitutieString = "$f" + substitutie.toString() + "@";
		if(gebruikersSubstitutiesVak != null)
			gebruikersSubStrings = gebruikersSubstitutiesVak.geefRegels();

		HashMap<String, Object> h = new HashMap<String, Object>();
		h.put("stapNr", new Integer(stapNr));
		h.put("formuleVakInhouden", formuleVakInhouden);
		h.put("antwoordString", "$f" + antwoordString + "@");
		h.put("pijlVakInhouden", pijlVakInhouden);
		h.put("pijlVakOperatoren", pijlVakOperatoren);
		h.put("ingevuld", new Boolean(ingevuld));
		h.put("nagekeken", new Boolean(nagekeken));
		h.put("isVeranderdNaNakijken", new Boolean(isVeranderdNaNakijken));
		h.put("errorCount", new Integer(errorCount));
		h.put("substitutieString", substitutieString);
		h.put("gebruikersSubStrings", gebruikersSubStrings);
		
		if (dwologger!= null) 
		{
			if(editor != null) // NPE if null
			dwologger.updateLog(editor.buildLoggingMap());
			dwologger.getStateHook(h);
		}
		return h;
	}

	private String getViewerString(int i) 
	{
		String string = (viewers.get(i)).toString();
		if (hasPrefix && !isVergelijkingVak)
		{
			string = removePrefix(string);
		}
		
		return string;
	}
	
	/**
	 * Copy the first part of the given size of the array.
	 * 
	 * @param array
	 * @param size
	 * @return
	 */
	private static String[] partArray(String[] array, int size)
	{
		String[] part = new String[size];
		
		System.arraycopy(array, 0, part, 0, size);
		
		return part;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		if(h == null) return; // setStateNull()
		logger.fine("setState " + h);

		boolean enabled = setFocusEnabled(false);
		try {
			setState0(h);
		} finally {
			setFocusEnabled(enabled);
		}
	}
	
	private void setState0(Map<String, Object>h) 
	{
		int stapNr = 0;
		String[] formuleVakInhouden = null;
		String[] pijlVakInhouden = null;
		String[] pijlVakOperatoren = null;
		boolean ingevuld = false;
		boolean nagekeken = false;
		boolean isVeranderdNaNakijken = false;
		int errorCount = 0;
		String substitutieString = "";
		String antwoordString = "";
		String[] gebruikersSubStrings = null;
		
		if (h.get("stapNr") != null)
			stapNr = ((Number) h.get("stapNr")).intValue();
		if (h.get("ingevuld") != null)
			ingevuld = (Boolean) h.get("ingevuld");
		if (h.get("nagekeken") != null)
			nagekeken = (Boolean) h.get("nagekeken");
		if (h.get("isVeranderdNaNakijken") != null)
			isVeranderdNaNakijken = ((Boolean) h.get("isVeranderdNaNakijken")).booleanValue();
		if (h.get("errorCount") != null)
			errorCount = ((Number) h.get("errorCount")).intValue();
		if (h.get("formuleVakInhouden") != null)
		{
			formuleVakInhouden = JSONUtilities.toStringArray(h.get("formuleVakInhouden"));
			
			// workaroud... FIXME Waarom is de laatste formuleVakInhouden null (strategiemodus)?
			if (formuleVakInhouden[formuleVakInhouden.length - 1] == null)
			{
				formuleVakInhouden = partArray(formuleVakInhouden, formuleVakInhouden.length - 1);
			}
			
			for (int i = 0; i < formuleVakInhouden.length; i++) 
			{
				if (formuleVakInhouden[i].startsWith("$f"))
					formuleVakInhouden[i] = formuleVakInhouden[i].substring(2, formuleVakInhouden[i].length()-1);
			}
		}
		if (h.containsKey("pijlVakInhouden"))
		{	
			pijlVakInhouden = JSONUtilities.toStringArray(h.get("pijlVakInhouden"));
			for (int i = 0; i < pijlVakInhouden.length; i++)
			{	
				if (pijlVakInhouden[i] != null && pijlVakInhouden[i].startsWith("$f"))
					pijlVakInhouden[i] = pijlVakInhouden[i].substring(2, pijlVakInhouden[i].length() - 1);
			}
		}
		if (h.containsKey("pijlVakOperatoren"))
			pijlVakOperatoren = JSONUtilities.toStringArray(h.get("pijlVakOperatoren"));
		
		if (h.get("antwoordString") != null)
			antwoordString = (String) h.get("antwoordString");
		if (h.get("substitutieString") != null)
			substitutieString = (String) h.get("substitutieString");
		if (h.containsKey("gebruikersSubStrings"))
			gebruikersSubStrings = JSONUtilities.toStringArray(h.get("gebruikersSubStrings"));
		

		this.stapNr = stapNr;
		this.ingevuld = ingevuld;
		this.nagekeken = nagekeken;
		this.isVeranderdNaNakijken = isVeranderdNaNakijken;
		this.errorCount = errorCount;
		if(gebruikersSubstitutiesVak != null)
			gebruikersSubstitutiesVak.zetRegels(gebruikersSubStrings);
		
		if (!substitutieString.equals(""))
			substitutie = FormuleParser.geefExpressie(substitutieString);

		int oudStepPanelY = stepPanelY;
		
		stepPanelY = 0;
		for (int i = 0; i < stapNr + 1; i++)
		{
			
			if (i == 0 && hasStartString)
			{	
				if (i < stapNr)
				{	
					if(linStrategieVersie || linOefenVersie || bordjesMethode || !(pijlVakOperatoren[i] == null || pijlVakOperatoren[i].equals("")))
					{	
						zetPijlVakNeer(pijlVakOperatoren, pijlVakInhouden, i, viewers.get(i).getHeight()/2);
					}
					i++; // sla de eerste over
				}
				else //in dit geval is stapNr 0. 
				{	
					stepPanelY = oudStepPanelY;
					return;
				}
			}

			FormuleViewer fv = new FormuleViewer(setViewerString(formuleVakInhouden, i));
			fv.setFont(font);
			
			while (i < viewers.size()) // haal de rest van de viewers vanaf i weg
			{	
				int last = viewers.size()-1;
				
				Panel asPanel = viewers.get(last).getAsPanel();
				if (asPanel.getParent() != null)
					stepPanels.get(last).remove(asPanel);
				viewers.remove(last);
				asPanel.removeFromParent();
			}
			viewers.add(fv);
			
			LayoutPanel stepPanel = null;
			if (i == 0 || i == 1 && hasStartString && !(linStrategieVersie || linOefenVersie || bordjesMethode))
			{
				stepPanel = stepPanels.get(i);
				if (editor != null)
				{	
					stepPanel.remove(editor.getAsPanel());
					editor.getAsPanel().removeFromParent();
					editor = null;
					checkimg.setVisible(false);
				}
				else if (viewers.size() > i)
					stepPanel.remove(viewers.get(i).getAsPanel());
				if (hasPrefix && (i < stapNr || nagekeken))
					stepPanel.remove(prefixViewer.getAsPanel());
			}
			else
			{
				stepPanel = maakNieuwStapPanel();
				if (hasPrefix && !nagekeken)
					addPrefixViewer(stepPanel);
			}

			stepPanel.removeFromParent();
			contentPanel.add(stepPanel);
			contentPanel.setWidgetLeftRight(stepPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			
			if (i < formuleVakInhouden.length && ("".equals(formuleVakInhouden[i]) || "$f@".equals(formuleVakInhouden[i])))
			{
				// formuleVakInhoud[i] is leeg
				
				viewers.remove(fv);
				editor = addNewEditor(stepPanel);
				if (antwoordString.startsWith("$f") && antwoordString.endsWith("@"))
					antwoordString = antwoordString.substring(2, antwoordString.length() - 1);
				
				// lege formulevakinhoud alleen vervangen door het antwoord uit FEWA als die er niet al in staat
				if ((i > 0) && (!bevatString(formuleVakInhouden[i - 1], antwoordString))) // bevat de vorige formuleVakInhoud antwoordString?
				{
					// als FEWS de het antwoord uit FEWA nog niet bevat, voeg het dan toe
					editor.insert(antwoordString);
				}
				else
				{
					// FEWS bevat al het antwoord uit FEWA, dus voeg een lege editor toe
					editor.insert("");
				}
				
				//hier setChanged(false)?
				if (viewers.size() > 0)
					stepPanelY += viewers.get(viewers.size() - 1).getHeight() + stapH;
				contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, hoogteStepPanelMetEditor(), Style.Unit.PX);
				
				if (i < stapNr)
					zetPijlVakNeer(pijlVakOperatoren, pijlVakInhouden, i, stepPanelY + hoogteStepPanelMetEditor()/2);
			}
			else
			{
				addFormuleViewer(fv, stepPanel);
				if (viewers.size() > 1)
					stepPanelY += viewers.get(viewers.size() - 2).getHeight() + stapH;
				contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, fv.getHeightWithImage(), Style.Unit.PX);
				
				if (i < stapNr)
					zetPijlVakNeer(pijlVakOperatoren, pijlVakInhouden, i, stepPanelY + fv.getHeightWithImage()/2);
					
				if (bordjesMethode)
				{	
					if (viewers.size() > 1)
						freezeViewer(viewers.get(viewers.size() - 2));
					if (!nagekeken)
						addFormulePanelListeners((TouchPanel) fv.getAsPanel(), fv); 
				}
			}
			
			if (viewers.size() > 0)
				latest_answer_viewer = viewers.get(viewers.size() - 1);
			
//			if(isToets())
//				fv.getAsPanel().getElement().getStyle().setMarginLeft(23, Unit.PX);
			if (!isToets())
			{
				if (i == stapNr && nagekeken)
				{	
					VergelijkingMeerv verg = FormuleParser.parseVergelijking("$f" + fv.toString() + "@");
					if (linStrategieVersie 
						|| (bordjesMethode && (verg != null) && verg.isEindOplossing(verg.geefVergelijkingVar()) && (correct != null? correct: false))) 
						// de eindoplossing moet natuurlijk wel goed zijn
						// als correct null is, wordt verderop een nieuwe editor gemaakt met de laatste stap en nagekeken
					{	
						fv.showResult(FormuleViewer.CORRECT);
						setAndAddFeedback(Text.constants.feedbackTekst04());
						//"De vergelijking is correct opgelost."
						stapOk = false;
					}
					else if (editor != null)//doel: laatste antwoord nogmaals nakijken, om juiste feedback te genereren.
					{
						editor.kijkNa(true);
						//maakNakijkenAf(false);
					}
					else
					{	
						viewers.remove(fv);
						stepPanel.remove(fv.getAsPanel());
						editor = addNewEditor(stepPanel);
						String currentTekst = latest_answer_viewer.toString();
						if (hasPrefix)
							currentTekst = removePrefix(currentTekst);
						currentTekst = removeIsTeken(currentTekst);
						editor.insert(currentTekst);
						//hier setChanged(false)?
						if (viewers.size() > 0)
							latest_answer_viewer = viewers.get(viewers.size() - 1);
						editor.kijkNa(true);
						//maakNakijkenAf(false);
					}
					
				}
				else if (i == stapNr && editor != null)
				{
					if (editor.toString().equals("") && (i > 1 || (!hasStartString && i > 0)))
					{
						//stap terug doen en die nakijken, zodat de feedback goed kan worden bepaald. Alleen nodig bij oefenmodi.
						stepPanel.remove(editor);
						checkimg.setVisible(false);
						stapNr--;
						this.stapNr--;
						stepPanels.remove(stepPanels.size() - 1);
						stepPanel = stepPanels.get(stepPanels.size() - 1);
						stepPanel.remove(viewers.get(viewers.size() - 1).getAsPanel());
						stepPanelY -= stapH + viewers.get(viewers.size() - 1).getHeight();
						viewers.remove(viewers.size() - 1);
						if (pijlVakken.size() > 0)
						{	
							if (pijlVakken.get(pijlVakken.size() - 1).getParent() != null)
								contentPanel.remove(pijlVakken.get(pijlVakken.size() - 1));
							pijlVakken.remove(pijlVakken.size() - 1);
						}
						
						editor = addNewEditor(stepPanel);
						String currentTekst = latest_answer_viewer.toString();
						if (hasPrefix)
							currentTekst = removePrefix(currentTekst);
						currentTekst = removeIsTeken(currentTekst);
						editor.insert(currentTekst);
						
						if (viewers.size() > 0)
							latest_answer_viewer = viewers.get(viewers.size() - 1);
						editor.kijkNa(true);
					}
					else 
					{	
						editor.kijkNa(true);
					}
				}
				else if (i == stapNr && (linOefenVersie))
				{
					fv.showResult(FormuleViewer.ALMOSTCORRECT);
				}
				else if (i == stapNr && !linStrategieVersie)
				{
					//nu: editor = null. 
					viewers.remove(fv);
					stepPanel.remove(fv.getAsPanel());
					editor = addNewEditor(stepPanel);
					editor.insert(latest_answer_viewer.toString());
					editor.kijkNa(true);
					//maakNakijkenAf(false);
				}
				else if (i == stapNr - 1 && !nagekeken && !linStrategieVersie && !substitutieVak)
					fv.showResult(FormuleViewer.ALMOSTCORRECT);
				else
					fv.showResult(FormuleViewer.NONE);
				if (correct == Boolean.TRUE)
				{	
					score = scoreMax;
					if (mode == OpdrNavIF.OEFENEN_STRAFPUNTEN) // met aftrek
						score = Math.max(scoreMax - errorCount * foutStraf, 0);
				}
				else
					score = 0;
				
			} // if !toets
			else if ((i == stapNr) && (editor == null))
			{
				// de laatste stap moet een editor zijn
				viewers.remove(fv);
				stepPanel.remove(fv.getAsPanel());
				editor = addNewEditor(stepPanel);
				editor.insert(latest_answer_viewer.toString());
			}
			
		} // for loop over alle stappen 
		
		if (isToets())
		{	
			if (nagekeken && !isVeranderdNaNakijken)
			{	
				kijkToetsNa(true, true);
			}
			else
			{	
				bepaalScoreEnCorrect();
				
				for (int i = 0; i < viewers.size(); i++)
				{
					viewers.get(i).showResult(FormuleViewer.NONE);
				}
			}
		}

		if (stapNr > 0 || stapNr == 0 && !hasStartString)
			terugButton.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		
		if (editor != null)
		{	
			editor.setCurrentElementRepaint();
		}
		
		scrollToBottom();
	}
	
	/**
	 * Checkt of formuleVakInhoud de gegeven antwoordstring bevat.
	 * 
	 * @param formuleVakInhoud
	 * @param antwoordString
	 * @return
	 */
	private boolean bevatString(String formuleVakInhoud, String antwoordString)
	{
		boolean bevat = false;
		
		String s;
		
		if (isVergelijkingVak)
			s = formuleVakInhoud;
		else
			// voor formulevak het =-teken weghalen
			s = formuleVakInhoud.substring(0, formuleVakInhoud.length() - 1);
		
		bevat = s.equals(antwoordString);
		
		return bevat;
	}

	/**
	 * Bepaal score en correct zonder iets aan het beeld te doen.
	 */
	private void bepaalScoreEnCorrect() {
		
		final String formule;
		final String formuleMin1;
		
		if (editor != null && !editor.toString().isEmpty())
		{
			formule = editor.toString();
			formuleMin1 = getLatestAnswer();
		} 
		else 
		{
			if (stapNr > 0) 
			{
				formule = getLatestAnswer();
				int size = viewers.size();
				
				if (size >= 2)
					formuleMin1 = viewers.get(size - 2).toString(); // voor strategieen viewers.get(size - 1).toString()?
				else
					formuleMin1 = null;
			}
			else
			{
				formule = getLatestAnswer();
				formuleMin1 = getLatestAnswer();
			}
		}
		
		try
		{
			bepaalScoreEnCorrect(formule, formuleMin1);
		}
		catch (RestartException r)
		{
			r.restart(new Runnable()
			{
				public void run()
				{
					try
					{
						bepaalScoreEnCorrect(formule, formuleMin1);
					}
					catch (RestartException e)
					{
						e.restart(this);
					}
				}
			});
		}
	}
	
	/**
	 * Bepaal de score en correct gegeven de twee stappen.
	 * 
	 * @param formule
	 * @param formuleMin1
	 * 		De formule uit de vorige stap.
	 */
	private void bepaalScoreEnCorrect(String formule, String formuleMin1) throws RestartException
	{
		HashMap<String, Object> checkResults = new HashMap<String, Object>();
		
		if (formule == null)
			formule = "";
		if (formuleMin1 == null)
			formuleMin1 = "";

		// verwijder een eventuele prefix
		if (hasPrefix)
		{
			formule = removePrefix(formule);
			formuleMin1 = removePrefix(formuleMin1);
		}
		formule = removeIsTeken(formule);
		formuleMin1 = removeIsTeken(formuleMin1);

		// checkAnswer() verwacht gecodeerde formules
		String formuleCoded = "$f" + formule + "@";
		String formuleMin1Coded = "$f" + formuleMin1 + "@";
		checkResults = avChecker.checkAnswer(formuleCoded, formuleMin1Coded, getSubstitutie(), getGebruikersSubstituties());

		this.correct = (Boolean) checkResults.get("correct");
		this.score = (Integer) checkResults.get("score");
		
		// strafpunten
		if(mode == OpdrNav.OEFENEN_STRAFPUNTEN) 
		{
			score = Math.max(0, score - foutStraf * errorCount);
		}
	}
	
	private String setViewerString(String[] formuleVakInhouden, int i) {
		String string = formuleVakInhouden.length > i?formuleVakInhouden[i]:"";
		if(hasPrefix && !isVergelijkingVak) {
			string = prefix.substring(2, prefix.length()-1) + string;
		}
		return string;
	}
	
	/**
	 * Zet zo nodig pijlvakken neer.
	 * 
	 * @param pijlVakOperatoren
	 * @param pijlVakInhouden
	 * @param i
	 * @param h
	 */
	public void zetPijlVakNeer(String[] pijlVakOperatoren, String[] pijlVakInhouden, int i, int h)
	{
		if(pijlVakken.size() > i && pijlVakken.get(i) != null)
		{	if(pijlVakken.get(i).getParent() != null)
				contentPanel.remove(pijlVakken.get(i));
			pijlVakken.remove(i);
		
		}
		
		if (pijlVakOperatoren != null && pijlVakOperatoren.length > i && pijlVakOperatoren[i] != null)
		{	pijlVak = new PijlVak(pijlVakOperatoren[i], this, true);
			pijlVak.paintComponent();
		}
		else
			//pijlVak = new PijlVak("implicatie", this);
			return;
		int breedte = pijlX;
		
		if (pijlVakOperatoren[i].equals("sub") || pijlVakOperatoren[i].equals("abc"))
			breedte = pijlX + 30;
		
		//if(pijl)
			contentPanel.add(pijlVak);
			if (pijlVakInhouden != null && pijlVakInhouden.length > i && pijlVakInhouden[i] != null)
				pijlVak.zetExpressie(pijlVakInhouden[i]);
			contentPanel.setWidgetRightWidth(pijlVak, 0, Style.Unit.PX, breedte, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(pijlVak, h, Style.Unit.PX, pijlVak.getHeight(), Style.Unit.PX);
		pijlVak.setPijlVisible(pijl);
		
			
		//if ("GR".equals(WiskOpdr.deployVariant) && pijlVakOperatoren != null && pijlVakOperatoren[i] != null && (pijlVakOperatoren[i].equals("sub") || pijlVakOperatoren[i].equals("abc")))
		//	pijlVak.setLocation(getSize().width - pijlX - 60, y);
		
		
		pijlVakken.add(i, pijlVak);
	}
	
	public void checkStap(int pijlVakNr, VergelijkingMeerv v1, VergelijkingMeerv v2)
	{
		boolean gelijkw = Algebra.zijnGelijkwaardigeVergelijkingen(v1, v2);
		zetGoedFoutStap(gelijkw ? AntwoordVakChecker.GOED : AntwoordVakChecker.FOUT, pijlVakNr);

	}
	
	private void zetGoedFoutStap(int uitslag, int pijlVakNr)
	{
		if (!check)
			return;
		
		Image stapimg = new Image(FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
		if (uitslag == AntwoordVakChecker.FOUT)
			stapimg.setUrl(FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri());
		//else if (uitslag == GEEN)
		//	imageComponentenStap[pijlVakNr] = new ImageComponent(null);
		
		imagesStappen.add(stapimg);
		
		contentPanel.add(stapimg);
		contentPanel.setWidgetRightWidth(stapimg, pijlX - 40, Style.Unit.PX, 20, Style.Unit.PX);
		contentPanel.setWidgetTopHeight(stapimg, stepPanelY  - stapH, Style.Unit.PX, 20, Style.Unit.PX);
		
	}
	
	private void zetGoedFoutEditor(int uitslag)
	{
		if (!check)
			return;
		//contentPanel.remove(checkimg);
		if ((latest_answer_viewer != null) && !isToets())
			latest_answer_viewer.showResult(FormuleViewer.NONE);

		if (uitslag == AntwoordVakChecker.FOUT)
			checkimg.setUrl(FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri());
		else if (uitslag == AntwoordVakChecker.HALF || uitslag == AntwoordVakChecker.DOOR)
			checkimg.setUrl(FORMULE_BUNDLE.mw_vinkje_geel().getSafeUri());
		else if (uitslag == AntwoordVakChecker.GOED)
			checkimg.setUrl(FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
		
		checkimg.removeFromParent();
		LayoutPanel parent = (LayoutPanel) editor.getAsPanel().getParent();
		parent.add(checkimg);
		checkimg.setVisible(true);
		parent.setWidgetLeftWidth(checkimg, 0, Style.Unit.PX, 20, Style.Unit.PX);
		parent.setWidgetTopHeight(checkimg, 0, Style.Unit.PX, 20, Style.Unit.PX);
	}

	/**
	 * Reset het goed/fout-plaatje en verberg de feedback.
	 */
	void resetimg() 
	{
		checkimg.setVisible(false);
	}
	
	public void kijkNa()
	{
		// reset isVeranderdNaNakijken
		zetIsVeranderdNaNakijken(false);

		kijkNa(false);
	}
	
	public void kijkNa(boolean setState)
	{
		kijkNa(false, true, setState);
	}
	
	/**
	 * Vuurt een cbook event af. Is ook aan te roepen vanuit FormuleEditorWithAnswer. Deze klasse 
	 * handelt de kijkNa() af van een stap in FormuleEditorWithSteps.
	 * 
	 * @param event
	 */
	void fireEvent(CBookEvent event) 
	{
		DWOplayer.clientfactory.getEventBus().fireEventFromSource(event, this);
		if(comRoot != null) //comRoot is null bij substitutieAntwoordVak
			comRoot.fireEvent(event);
	}

	public void kijkNa(boolean backStep, boolean show, boolean setState)
	{
		if(!isToets() && editor == null)
		{
			// er is al nagekeken en/of er valt niets na te kijken
			//(bijv voor in/uitklappen tekstvak is deze return nodig, om te voorkomen dat correct weer op false wordt gezet).
			return;
		}
		nagekeken = false;
		correct = Boolean.FALSE;
		score = 0;
		
		if(!isToets() && editor != null)
		{	
			editor.kijkNa(backStep, show, setState);
			
		}
		else if(isToets())
		{
			kijkToetsNa(setState, true);
		}
	}
	
	/**
	 * Kijk een toets na.
	 * @param setState
	 * @param show altijd <b>true</b>
	 */
	public void kijkToetsNa(boolean setState, boolean show)
	{
		int start = 0;
		if (hasStartString)
			start = 1;
		
		if (editor != null && editor.toString().equals(""))
		{	
			backStep(setState); 
		}
		
		if (editor != null && !editor.toString().equals(""))
		{	
			vervangEditorDoorViewer("$f" + editor.toString() + "@", true, true);
		}
				
		aantalStappen = viewers.size();
		String[] viewersInhouden = new String[viewers.size()];
		if (viewers.size() > 0)
			viewersInhouden[0] = viewers.get(0).toString();
		
		for (int i = viewers.size() - 1; i > start - 1; i--)
		{
			viewersInhouden[i] = viewers.get(i).toString();
			stepPanels.get(i).remove(viewers.get(i).getAsPanel());
			if (i > start)
			{	
				stepPanelY -= stapH + viewers.get(i).getHeight();
				stepPanels.remove(i);
				haalPijlVakWeg();
			}
			viewers.remove(i);
		}
		
		for (int i = 0; i < imagesStappen.size(); i++)
		{
			contentPanel.remove(imagesStappen.get(i));
		}
		
		imagesStappen.clear();

		stapNr = start;
		for (int i = start; i < aantalStappen; i++)
		{
			if (editor == null)
				editor = addNewEditor(stepPanels.get(i));
			
			editor.clearAll();
			String currentTekst = viewersInhouden[i];
			
			if (hasPrefix)
				currentTekst = removePrefix(currentTekst);
			
			currentTekst = removeIsTeken(currentTekst);
			editor.insert(currentTekst);
			
			if (stepsForLinKwad && start > 0)
			{
				checkStap(i - 1, FormuleParser.parseVergelijking("$f" + viewersInhouden[i-1] + "@"), FormuleParser.parseVergelijking("$f" + viewersInhouden[i] + "@"));
			}
			
			editor.kijkNa(setState);
		}
		if (editor != null)
			zetGoedFoutEditor(editor.getGoedHalfFout());

	}
	
	public void maakNakijkenAf(boolean backStep, boolean show, boolean setState)
	{
		int goedHalfFout = editor.getGoedHalfFout();
		if (goedHalfFout == AntwoordVakChecker.GEEN)
			ingevuld = false;
		else
			ingevuld = true;
		if (isToets())
		{
			correct = editor.isCorrect(); //XXX wordt elders gezet, maar waar en waarom?
			score = editor.getScore(); // altijd score ophalen, ook bij noshow
			if (show)
			{
				if (stepPanels.size() < aantalStappen)
				{
					setFeedback(editor.getFeedback());
					addStep("$f" + editor.toString() + "@", show, setState);
				}
				else
				{
					nagekeken = true;
					//score = editor.getScore();
					if(goedHalfFout == AntwoordVakChecker.GOED)
					{
						setFeedback(editor.getFeedback());
						if (!Boolean.TRUE.equals(correct))
							lastStep("$f" + editor.toString() + "@", show, setState);
					}
					else
					{
						setAndAddFeedback(editor.getFeedback());
					}
				}
				return;
			}
			else // hier ook score ophalen!!!! 
			{	
				if(goedHalfFout == AntwoordVakChecker.FOUT && editor.isSyntaxFout())
				{
					nagekeken = true;
					setAndAddFeedback(editor.getFeedback());
				}
				else
				{	
					setFeedback("");
					feedbackPanel.removeFromParent();
				
					addStep("$f" + editor.toString() + "@", show, setState);
				}
				return;
			}
		} // isToets
		//stapOk juiste waarde geven.
		if (goedHalfFout == AntwoordVakChecker.GOED)
		{	
			stapOk = false;
			if (!pijl)
				stapOk = true;
			//correct = true; -- dit gebeurt in lastStep nog (en anders gebeurt lastStep niet!)
			//score = editor.getScore();
		}
		else if(goedHalfFout == AntwoordVakChecker.DOOR)
		{	
			stapOk = true;
			//score = editor.getScore();
			
		}
		else 
		{	
			stapOk = false;
			nagekeken = true;
			//score = editor.getScore();
		}
		score = editor.getScore();
		if (mode == OpdrNavIF.OEFENEN_STRAFPUNTEN)
		{
			//in score niet aantal fouten uit deze specifieke editor (regel), maar aantal fouten uit gehele editorWithSteps meenemen
			score = editor.getScoreZonderAftrek();
			score = Math.max(0, score - foutStraf * errorCount);
		}
		
		if (bordjesMethode)
		{	if (stapOk || goedHalfFout == AntwoordVakChecker.GOED)
			{	
				vervangEditorDoorViewer("$f" + editor.toString() + "@", show, setState);
			}
			else
				zetGoedFoutEditor(editor.getGoedHalfFout());
			
		}
		else
		{
			String feedback = editor.getFeedback();
			if (goedHalfFout == AntwoordVakChecker.DOOR)
			{
				if(backStep || linOefenVersie)
					setAndAddFeedback(feedback);
				else
				{	setFeedback(feedback);
					addStep("$f" + editor.toString() + "@", show, setState); // deze regel wordt in 'setState' aangeroepen als je de state terugzet, komt er zomaar een extra regel, why?
				}
			}
			else if (goedHalfFout == AntwoordVakChecker.HALF || goedHalfFout == AntwoordVakChecker.FOUT)
				setAndAddFeedback(feedback);
			else if (goedHalfFout == AntwoordVakChecker.GOED)
			{ 	setFeedback(feedback);
				lastStep("$f" + editor.toString() + "@", show, setState);
			}
		}
	}
	
	public boolean controleerStap()
	{
		if(!linOefenVersie)
			return true;
		if (stapNr == 0)
			return true;
		String op = pijlVakken.get(stapNr - 1).geefOperator();
		Expressie en = FormuleParser.geefExpressie("$f" + pijlVakken.get(stapNr - 1).geefExpressieString() + "@");
		if (op.equals("implicatie") || op.equals("abc") || en == null)
			return true;
		
		VergelijkingMeerv verg = FormuleParser.parseVergelijking("$f" + latest_answer_viewer.toString() + "@");
		VergelijkingMeerv vergNieuw = null;

		int aantalDelen = verg.geefAantal();
		
		for (int i = 0; i < aantalDelen && aantalDelen > 0; i++)
		{
			if ((editor != null && editor.partEquationSelected(i)) || latest_answer_viewer.partEquationSelected(i))
			{
				vergNieuw = verg.bewerkVergelijking(op, en, i);
				break;
			}
		}
		if (vergNieuw == null)
			vergNieuw = verg.bewerkVergelijking(op, en);

		VergelijkingMeerv vergAntwoord = FormuleParser.parseVergelijking("$f" + editor.toString() + "@");
		if (op.equals("sub"))
		{
			if (isVergelijkingVak)
				vergAntwoord = vergAntwoord.substitueer(substitutie, "p");
			else
				vergAntwoord = vergAntwoord.substitueer(substitutie, "u");
		}
		return vergNieuw.isGelijkMet(vergAntwoord);
	}
	
		
	@Override
	public int getScore()
	{
		if(!teltMee)
			return 0;
		return score;
	}
	
	@Override
	public int[][] getScoreObjectives()
	{
		if (logObjectives == null)
			return null;
		int[][] scoreObjectives = new int[logObjectives.length][];
		for (int i = 0; i < logObjectives.length; i++)
			scoreObjectives[i] = new int[logObjectives[i].length];
		for (int i = 0; i < logObjectives.length; i++)
			for (int j = 0; j < logObjectives[i].length; j++)
			{
				if (logObjectives[i][j])
					scoreObjectives[i][j] = score;
			}
		return scoreObjectives;
	}

	@Override
	public Boolean isCorrect()
	{
		if(!teltMee)
			return true;
		return correct;
	}
	
	public void zetNagekeken(boolean b) {
		if (ingevuld)
		{	nagekeken = b;
		}
	}
	
	void zetIsVeranderdNaNakijken(boolean b)
	{
		this.isVeranderdNaNakijken = b;
	}
	
	public void zetIngevuld(boolean b) {
		ingevuld = b;
	}
	
	public void zetSubstitutie(Expressie e)
	{
		substitutie = e;
	}
	
	public Expressie getSubstitutie()
	{
		return substitutie;
	}
	
	public Vergelijking[] getGebruikersSubstituties()
	{
		if(gebruikersSubstitutiesVak == null)
			return null;
		String[] gebruikersSubstitutieStrings = gebruikersSubstitutiesVak.geefRegels();
		if (gebruikersSubstitutieStrings != null)
		{
			boolean subCorrect = true;
			gebruikersSubstituties = new Vergelijking[gebruikersSubstitutieStrings.length];
			for (int i = 0; i < gebruikersSubstitutieStrings.length; i++)
			{
				try
				{
					gebruikersSubstituties[i] = (FormuleParser.parseVergelijking(gebruikersSubstitutieStrings[i], avChecker.getFunctieMVDefSet())).geefVergelijking(0);
					//gebruikersSubstituties[i] = (FormuleParser.parseVergelijking(gebruikersSubstitutieStrings[i])).geefVergelijking(0);
					if (!gebruikersSubstituties[i].geefExpLinks().isVar())
						subCorrect = false;
				}
				catch (Exception e)
				{
					subCorrect = false;
				}
			}
			if (!subCorrect)
				gebruikersSubstituties = null;
		}
		return gebruikersSubstituties;
	}
	
	//gebruikt voor opvragen gebruikerssubstituties
	public String[] geefRegels()
	{
		String[] regels = new String[stapNr + 1];
		for (int i = 0; i < viewers.size(); i++)
			regels[i] = "$f" + this.getViewerString(i) + "@";
		if(viewers.size() < stapNr + 1 && editor != null)
			regels[stapNr] = "$f" + editor.toString() + "@";
		return regels;
		
	}
	
	//gebruikt voor terugzetten gebruikerssubstituties
	public void zetRegels(String[] regelStrings)
	{	if(regelStrings==null) return;
		int aantalFormuleRegels = regelStrings.length;
		for(int i=0 ; i<aantalFormuleRegels ; i++)
		{	if(i>0)
				downStep();
		if (regelStrings[i].startsWith("$f") && regelStrings[i].endsWith("@"))
			regelStrings[i] = regelStrings[i].substring(2, regelStrings[i].length() - 1);	
		editor.insert(regelStrings[i]);
		}
	}
	
	
	private void maakStap(String operator)
	{
		nagekeken = false;
		if (stapNr == 0 && editor != null && editor.toString().equals("")) 
			return;
		if (!stapOk && editor != null && !editor.toString().equals(""))
			return;
		else if(!stapOk && latest_answer_viewer.getResult() == FormuleViewer.CORRECT)
		{
			return;
			//Deze return zorgt dat je niet nog een stap kunt doen nadat je in de lineaire strategie-versie de juiste oplossing hebt gevonden.
		}
		
		if (operator.equals("implicatie"))
		{
			addStep("$f" + editor.toString() + "@", !isToets(), false);
			
		}
		else
		{
			//Er staat al een pijl naar de volgende regel. Deze pijl moet worden vervangen door de nieuwe operatie.
			if(stepPanels.size() == pijlVakken.size())
			{
				if(pijlVak != null && pijlVak.getParent() != null)
				{	contentPanel.remove(pijlVak);
					pijlVakken.remove(pijlVakken.size() - 1);
				}
				stapOk = false;
			}
			//Er staat al een pijl naar de volgende regel en daar staat ook al een vak klaar. De pijl en het bijbehorende vak moeten worden veranderd.
			//Dit gebeurt bijvoorbeeld als er een gewone pijl staat, die wordt veranderd in een bewerkingspijl (plus, bijvoorbeeld).
			else if (stepPanels.size() > pijlVakken.size() 
				&& (stapNr < stepPanels.size() - 1 || editor != null && editor.toString().equals("")) 
				&& pijlVakken.size() > 0 && !stapOk)//ik denk dat pijlVakken.size() hier weer weg kan.
			{
				LayoutPanel current = stepPanels.get(stepPanels.size() - 1);
				stepPanelY -= stapH + latest_answer_viewer.getHeight();
				if (editor == null)
				{	
					current.remove(viewers.get(viewers.size() - 1).getAsPanel());
					viewers.remove(viewers.size() - 1);
				}
				else
				{	
					current.remove(editor.getAsPanel());
					editor = null;
					checkimg.setVisible(false);
				}
				if (hasPrefix)
					current.remove(prefixViewer.getAsPanel());
				stepPanels.remove(stepPanels.size() - 1);
				contentPanel.remove(pijlVak);
				pijlVakken.remove(pijlVakken.size() - 1);
				stapNr--;
			}			
			//Als er een nieuwe pijl wordt bijgemaakt en de editor nog in de laatste regel stond, dan moet die editor worden veranderd in een viewer.
			else if (stepPanels.size() > pijlVakken.size() && stapNr >= stepPanels.size() - 1 && editor != null)
			{
				vervangEditorDoorViewer("$f"+ editor.toString() + "@", !isToets(), false);
				
			}
			
			pijlVak = new PijlVak(operator, this, false);
			int y = stepPanelY + latest_answer_viewer.getHeight()/2;
			contentPanel.add(pijlVak);
			contentPanel.setWidgetRightWidth(pijlVak, 0, Style.Unit.PX, pijlX, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(pijlVak, y, Style.Unit.PX, pijlVak.getHeight(), Style.Unit.PX);
			
			if (operator.equals("abc") || operator.equals("sub"))
				contentPanel.setWidgetRightWidth(pijlVak, 0, Style.Unit.PX, pijlX + 30, Style.Unit.PX);
			
			pijlVak.setPijlVisible(pijl);
			pijlVak.paintComponent();
			pijlVakken.add(pijlVak);
			
			pijlVak.getEditor().requestFocus();
			scrollToBottom();
		}
	}
	
	/**
	 * Als er een geldige editor is, vervang deze door een viewer met het gegeven antwoord.
	 * 
	 * @param antwoord
	 * @param show Toon feedback
	 * @param setState
	 */
	public void vervangEditorDoorViewer(String antwoord, boolean show, boolean setState)
	{
		if (editor == null)
		{
			return;
		}
		
		LayoutPanel current = stepPanels.get(stepPanels.size() - 1);
		current.remove(editor.getAsPanel());
		int selectionStartX = -1;
		int selectionStartY = 0;
		int selectionEndX = -1;
		int selectionEndY = 0;
		
		if (editor.hasSelection())
		{	
			int[] selectionBounds = editor.getSelectionBounds();
			selectionStartX = selectionBounds[0];
			selectionEndX = selectionBounds[1];
			selectionStartY = selectionBounds[2];
			selectionEndY = selectionBounds[3];
		}
		
		int goedHalfFout = editor.getGoedHalfFout();
		editor = null;
		checkimg.setVisible(false);
		
		if (hasPrefix)
		{
			current.remove(prefixViewer.getAsPanel());
			
			// antwoord ook ontdoen van prefix
			antwoord = removeFormulaCodes(antwoord);
			antwoord = removePrefix(antwoord);
			antwoord = addFormulaCodes(antwoord);
		}
		
		// antwoord eindigt op = of ≈ 
		if (!isVergelijkingVak && !hasPrefix && 
				((antwoord.charAt(antwoord.length() - 2)) != '=') && (antwoord.charAt(antwoord.length() - 2) != '≈'))
		{
			antwoord = antwoord.substring(0, antwoord.length() - 1) + "=@";
		}
		
		if (bordjesMethode) 
		{
			// convert to stringStrikt.
			try
			{
				VergelijkingMeerv e = FormuleParser.parseVergelijking(antwoord);
				antwoord = "$f"+ e.toStringStrikt() + "@";
			}
			catch(Exception e){}
		}
		
		FormuleViewer fv = new FormuleViewer(prefix.substring(2, prefix.length() - 1) + antwoord.substring(2, antwoord.length() - 1));
		fv.setFont(font);
		fv.setSelection(selectionStartX, selectionStartY, selectionEndX, selectionEndY);
		
		if (show && !substitutieVak)
			if (!eigenOpdr || stapNr > 0) // eigenopdracht niet tonen voor stapNr 0
			{
				fv.showResult(FormuleViewer.ALMOSTCORRECT); // waarom? Voor bordjesmethode kom ik hier met een goed antwoord...
			}
		else if (!isToets())
			fv.showResult(FormuleViewer.NONE);
		
		VergelijkingMeerv verg = FormuleParser.parseVergelijking("$f" + fv.toString() + "@");

		// deze hier al, dan gaat het goed na comRoot.setChanged() die FEWS.getState() triggert die de goede latestanswer en viewers nodig heeft
		if (latest_answer_viewer != null 
			&& !(hasStartString && stapNr == 1) 
			&& !(isToets() && show && !stepsForLinKwad))
		{
			latest_answer_viewer.showResult(FormuleViewer.NONE);
		}
		
		latest_answer_viewer = fv;
		viewers.add(fv);

		if (bordjesMethode && verg.isEindOplossing(verg.geefVergelijkingVar()))
		{	
			fv.showResult(FormuleViewer.CORRECT);
			if (isToets() && nagekeken && !isVeranderdNaNakijken)
				setAndAddFeedback(Text.constants.feedbackTekst04());
			//"De vergelijking is correct opgelost."
			stapOk = false;
			//nagekeken = true; // niet voor een toets. Waarom uberhaupt?
			correct = Boolean.TRUE;
			score = scoreMax;
			if (mode == OpdrNavIF.OEFENEN_STRAFPUNTEN)
				score = Math.max(0, scoreMax - errorCount * foutStraf);
			if (!setState) // voor een zelftoets die al is nagekeken, wordt hierdoor het antwoord goed getoond met groene bol -> gefixt in OpdrNav.setChanged()
				comRoot.setChanged(false);
		}
		
		if (isToets())
		{
			if (show && !stepsForLinKwad && !substitutieVak)
			{	
				if (goedHalfFout == AntwoordVakChecker.GOED)
					fv.showResult(FormuleViewer.CORRECT);
				else if (goedHalfFout == AntwoordVakChecker.DOOR || goedHalfFout == AntwoordVakChecker.HALF)
					fv.showResult(FormuleViewer.ALMOSTCORRECT);
				else if (goedHalfFout == AntwoordVakChecker.FOUT)
					fv.showResult(FormuleViewer.WRONG);
			}
			else
			{
				fv.showResult(FormuleViewer.NONE);
			}
				
		}
			
		addFormuleViewer(fv, current);
		
		if (bordjesMethode)
		{	
			if (viewers.size() > 1)
				freezeViewer(viewers.get(viewers.size() - 2));
			if (!verg.isEindOplossing(verg.geefVergelijkingVar()))
				addFormulePanelListeners((TouchPanel) fv.getAsPanel(), fv); 
		}
	}

	/**
	 * Bij stap terug in StelselEditor moet viewer weer vervangen kunnen worden door editor.
	 * 
	 * @param setState
	 */
	public void vervangViewerDoorEditor(boolean setState)
	{
		String antwoord = latest_answer_viewer.toString();
		LayoutPanel current = stepPanels.get(stepPanels.size() - 1);
		current.remove(latest_answer_viewer.getAsPanel());
		viewers.remove(latest_answer_viewer);
		latest_answer_viewer = viewers.get(viewers.size() - 1);
		if (hasPrefix)
		{	//TODO: als ook gebruikt voor plekken waar een prefix aanwezig: prefix van antwoord af halen.
		
		}
		
		editor = addNewEditor(current);
		editor.insert(antwoord);
		
	}
	
	
	public void maakBewerkingStap()
	{
		stapNr++;
		String operator = pijlVak.geefOperator();
		
		Expressie en = FormuleParser.geefExpressie("$f" + pijlVak.geefExpressieString() + "@");
		VergelijkingMeerv verg = FormuleParser.parseVergelijking("$f" + latest_answer_viewer.toString() + "@");
		VergelijkingMeerv vergNieuw = null;

		if (linOefenVersie || linStrategieVersie)
		{
			int aantalDelen = verg.geefAantal();
			for (int i = 0; i < aantalDelen && aantalDelen > 0; i++)
			{
				//Deel van een vergelijking selecteren.
				if ((editor != null && editor.partEquationSelected(i)) || latest_answer_viewer.partEquationSelected(i))
				{
					vergNieuw = verg.bewerkVergelijking(operator, en, i);
					break;
				}

			}
			if (vergNieuw == null)
				vergNieuw = verg.bewerkVergelijking(operator, en);
		}
		else
			vergNieuw = verg.bewerkVergelijking(operator, en);

		if (!isToets() || (hasStartString && stapNr - 1 == 0))
		{	
			//formuleVakken[stapNr - 1].setEditable(false);
		}
		
		LayoutPanel stepPanel = maakNieuwStapPanel();
		if (linOefenVersie)
		{
			editor = addNewEditor(stepPanel);
			stepPanelY += stapH + latest_answer_viewer.getHeight();
			contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, hoogteStepPanelMetEditor(), Style.Unit.PX);
			//if (!verg.toString().equals(vergNieuw.toString()) || linStrategieVersie)
			requestFocus();
			stapOk = false;	
			
		}
		else //if(linStrategieVersie || bordjesMethode)
		{
			FormuleViewer fv = null;
			try
			{
				fv = new FormuleViewer(prefix.substring(2, prefix.length() - 1) + vergNieuw.toString());
			}
			catch (Exception e)
			{	fv = new FormuleViewer("");
				vergNieuw = null;
			}
			fv.setFont(font);
			//fv.showResult(fv.ALMOSTCORRECT);
			
			if (latest_answer_viewer != null && !(hasStartString && stapNr == 1))
				latest_answer_viewer.showResult(FormuleViewer.NONE);
			stepPanelY += stapH + latest_answer_viewer.getHeight();
			if (vergNieuw != null)
				latest_answer_viewer = fv;
			
			// deze moet voor de comRoot.setChanged(), want die triggert een FEWS.getState() waarin up to date viewers nodig zijn!
			viewers.add(fv);

			if (vergNieuw != null && vergNieuw.isEindOplossing(vergNieuw.geefVergelijkingVar()))
			{	
				if (isToets())
				{
					if (nagekeken && !isVeranderdNaNakijken)
					{
						fv.showResult(FormuleViewer.CORRECT);
						setAndAddFeedback(Text.constants.feedbackTekst04());
					}
					else
					{
						fv.showResult(FormuleViewer.NONE);
					}
				}
				else
				{
					fv.showResult(FormuleViewer.CORRECT);
					setAndAddFeedback(Text.constants.feedbackTekst04());
				}
				//"De vergelijking is correct opgelost."
				stapOk = false;
				
				if (!isToets() && linStrategieVersie)
				{
					nagekeken = true; // niet voor toets! uberhaupt niet? voor linStrategieVersie gaat het nu mis bij oefenen
				}
				
				correct = Boolean.TRUE;
				score = scoreMax;
				if (mode == OpdrNavIF.OEFENEN_STRAFPUNTEN)
					score = Math.max(0, scoreMax - errorCount * foutStraf);
				comRoot.setChanged(false);
			}
			else if (vergNieuw != null && linStrategieVersie)
			{	
				fv.showResult(FormuleViewer.NONE);
				stapOk = true;
			}
			else if (vergNieuw != null && !substitutieVak)
			{
				fv.showResult(FormuleViewer.ALMOSTCORRECT);
				stapOk = true;
			}
			else
			{
				fv.showResult(FormuleViewer.NONE);
				stapOk = false;
			}

			Panel p = fv.getAsPanel();
			p.getElement().getStyle().setProperty("display", "inline");
			stepPanel.add(p);
			if (bordjesMethode)
				addFormulePanelListeners((TouchPanel) p, fv); 
			
			contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, fv.getHeightWithImage(), Style.Unit.PX);
			if (linOefenVersie)
			{	
				stepPanel.remove(p);
				viewers.remove(fv);
				addStep("$f" + fv.toString() + "@", !isToets(), false); 
				stapOk = false;
			}
			else if (!bordjesMethode && !linStrategieVersie)
			{
				addStep("$f" + fv.toString() + "@", !isToets(), false);
				stapOk = false;
			}
			scrollToBottom();
		}
		
		if (stapNr > 0 || stapNr == 0 && !hasStartString)
			terugButton.getElement().getStyle().setVisibility(Visibility.VISIBLE);
	}
	
	public void wis() {
		while(stapNr > (hasStartString?1:0))
			backStep(false);
		//Als het kan nog één keer vaker (als bijvoorbeeld geen startstring, maar nog wel ingevulde formule op eerste regel).
		if(terugButton.isVisible())
			backStep(false);
		
	}
	
	public int hoogteStepPanelMetEditor()
	{
		int hoogte = editor.getMainRegel().getHeight();
		if(prefixViewer != null)
		{	hoogte = Math.max(hoogte, prefixViewer.getHeight());
			//en om te zorgen dat subscripts van de prefix niet kunnen verdwijnen:
			hoogte += Math.max(prefixViewer.getHeight() - prefixViewer.getAsHoogte() - editor.getMainRegel().getHeight() + editor.getMainRegel().getAsHoogte(), 0);
		}
		
		return hoogte;
	}
	
	

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		mode = comRoot.getMode();
		if(dwologger != null)
			dwologger.setCommunicationRoot(comRoot);
		if(editor != null) {
			editor.setCommunicationRoot(comRoot);
			//editor.zetMode(mode); // FIXME why null? after init?
			if(isVergelijkingVak)
				comRoot.addCBookEventListener("balansvergelijking", new CBookEventListener() {
					
					@Override
					public void acceptCBookEvent(CBookEvent event) {
						editor.acceptCBookEvent(event); // steeds een andere editor!
					}
				});
		}
		
	}
	
	public OpdrNavIF getCommunicationRoot()
	{
		return comRoot;
	}

	@Override
	public Widget asWidget()
	{
		return facade.wrap(getAsPanel());
	}
	
	@Override
	public int getAsHoogte() {
		return font.getAscent();
		
	}

	@Override
	public int getHeight() {
		return facade.wrapHeight(hoogte);
	}
	protected void setHeight(int h) {
		hoogte = h;
		mainPanel.setPixelSize(-1, h-2);
		sp.setPixelSize(-1, h-50);
	}
	
	@Override
	public int getWidth() {
		return facade.wrapWidth(breedte);
	}
	
	//voor aanpassen breedte in geval van volledigeBreedte
	public void zetVolledigeBreedte(int breedte)
	{
		if(volledigeBreedte)
		{	this.breedte = breedte;
			mainPanel.getElement().getStyle().setWidth(breedte - 2, Unit.PX);
			headerPanel.getElement().getStyle().setWidth(breedte - 2, Unit.PX);
			sp.getElement().getStyle().setWidth(breedte - 5, Unit.PX);
			feedbackPanel.getElement().getStyle().setWidth(breedte - 25, Unit.PX);	
			for(int i = 0; i < stepPanels.size(); i++)
				stepPanels.get(i).setWidth((breedte - 5) + "px");
		}
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		viewers.get(0).setAsHoogte(ashoogte);
	}
	
	public void scrollToBottom()
	{
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
	        @Override
	        public void execute() {
	            sp.scrollToBottom();
	        }
		});
	}

	void fire(String command, String message) {
		if( comRoot != null)
			comRoot.fireEvent(new CBookEvent(this, command, message));	
	}
	
	public static boolean isNoordhoff()
	{
		String dependentName = DWOplayer.PARAMETERS.keyboardStyle();
		return "noordhoff".equals(dependentName);
	}

	private String toMathML(String source) {
		if(isVergelijkingVak)
		{
			VergelijkingMeerv verg = FormuleParser.parseVergelijking(source);
			if(source == null) return "";
			return verg.visit(ContentMathML.INSTANCE).toString();
		} else {
			Expressie antwoord = FormuleParser.geefExpressie(source);
			if(antwoord == null) return "";
			return antwoord.visit(ContentMathML.INSTANCE).toString();
		}
	}
	
	
	@Override
	public void getResponses(List<String> responses) {
		String response = "";
		if(editor != null) {
			String useranswer = "$f" + editor.toString() + "@";
			response = toMathML(useranswer);
		} else if (latest_answer_viewer != null) {
			String useranswer = "$f" + latest_answer_viewer.toString() + "@";
			response = toMathML(useranswer);			
		} else {
			response = toMathML(antwoordString);
		}
		responses.add(response);		
	}

	/**
	 * Zet het veld dat bij houdt of de FormuleEditorWithSteps is uitgeklapt.
	 */
	public void setUitgeklapt(boolean b)
	{
		this.isUitgeklapt = b;
	}

	public void setIsBoss(boolean b)
	{
		this.isBoss = b;		
	}

// Word gezet in setParentRegel()
	@Override
	public void setFontSize(int font_size) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFontName(String font_name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFontStyle(int font_style) {
		// TODO Auto-generated method stub
		
	}

	public Object getStep() {
		if(stapNr == 0)
			return "start";
		return Integer.valueOf(stapNr);
	}
	
	public int bepaalHoogte()
	{
		int hoogte = 0;
		for(int i = 0; i < viewers.size(); i++)
		{
			hoogte += viewers.get(i).getHeight() + stapH;
		}
		if(editor != null)
			hoogte += editor.getHeight() + stapH;
		if (hasFeedback) 
			hoogte += feedbackPanelHeight;
		return hoogte;
	}
	
	public void zetPijl(boolean p)
	{
		pijl = p;
	}
	
	public void zetScrollOptie(boolean b)
	{
		//poging: gewoon scrollPanel er tussenuit halen.
		if(b)
		{
			mainPanel.remove(contentPanel);
			sp.setWidget(contentPanel);
			mainPanel.add(sp);
				
		}
		else
		{
			mainPanel.remove(sp);
			mainPanel.add(contentPanel);
				
		}
		
	}
	
	public void zetMetRand(boolean b)
	{
		boxMetRand = b;
		mainPanel.getElement().getStyle().setBorderWidth(boxMetRand ? 1 : 0, Unit.PX);
	}
	
	public void zetLinkerRand()
	{
		mainPanel.getElement().getStyle().setProperty("borderLeft", "1px solid gray" );
	}
	
	public void zetCheck(boolean c)
	{
		check = c;
	}
	
	public boolean getCheck()
	{
		return check;
	}

	public int getStapNr()
	{
		return stapNr;
	}
	
	public void setHeader(boolean b)
	{
		if(b)
			mainPanel.add(headerPanel);
		else
			mainPanel.remove(headerPanel);
	}
	
	public FlowPanel getHeaderPanel()
	{
		return headerPanel;
	}
	
	@Override
	public int[][] getMeasuredMisconceptions() {
		if(avChecker != null)
			return avChecker.getMeasuredMisconceptions();
		return null;
					
	}

	@Override
	public int[][] getPossibleMisconceptions() {
		if(avChecker != null)
			return avChecker.getPossibleMisconceptions();
		return null;
	}
}