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
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.DWOLogger;
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
import com.googlecode.mgwt.dom.client.event.touch.TouchCancelEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartHandler;
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.wiskopdr.AntwoordFormuleVakChecker;
import fi.wiskopdr.AntwoordVakChecker;
import fi.wiskopdr.AntwoordVergelijkingVakChecker;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Algebra;
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
public class FormuleEditorWithSteps implements InteractionView, FacetAware, TekstElementWithFont
{
	private final static Logger logger = Logger.getLogger("FormuleEditorWithSteps");

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
	private int breedte = 600;
	private int hoogte = 250;
	private boolean volledigeBreedte = false;
	private HashMap<String, Object> launchState;
	private ObjectMap instellingen;
	private ArrayList<FormuleViewer> viewers = new ArrayList<FormuleViewer>();
	private FormuleEditorWithAnswer editor = null;
	private FormuleViewer prefixViewer;
	private FormuleViewer latest_answer_viewer;
	private ScrollPanel sp = null;
	private AntwoordVakChecker avChecker = null;
	
	private LayoutPanel contentPanel = null;
	private TekstVak feedbackPanel = null;
	int feedbackPanelHeight = 34;
	private FlowPanel mainPanel = null;
	private OpdrNavIF comRoot;
	private int mode;
	
	private PijlVak pijlVak;
	private boolean pijl = false;
	//private int pijlX = "GR".equals(WiskOpdr.deployVariant) ? 105 : 130;
	private int pijlX = 130;
	private int stepPanelY = 0; //locatie van bovenrand van het laatste (onderste) stepPanel
	private int stapH = 21;
	
	private Expressie substitutie;
	private Vergelijking[] gebruikersSubstituties;
	
	private TouchButton terugButton;
	private TouchButton downButton;
	private TouchButton copyButton;
	private FormuleButton plusKnop, minKnop, maalKnop, deelKnop, haakjesKnop, herleidKnop, abcKnop, subKnop;
	private TouchButton rmKnop;
	
	private FormuleButton ontbindKnop, splitsKnop, wortelBewerkKnop;
	private boolean abcVisible, subVisible;
	private boolean bewerkingKnoppen, bewerkingKnoppenExtra;
	private int stapNr = 0;
	protected HashMap<String, Object> h = null;
	protected static String[] randomVarNamen = null;
	protected static HashMap randomVarWaarden = null;
	private ArrayList<LayoutPanel> stepPanels = new ArrayList<LayoutPanel>();
	private ArrayList<PijlVak> pijlVakken = new ArrayList<PijlVak>();
	
	private ArrayList<Image> imagesStappen = new ArrayList<Image>();
	private Image checkimg;
	
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
		
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
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
		boolean rmknop = false;
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
			if (launchState.get("boxMetRand") != null)
				boxMetRand = (Boolean) launchState.get("boxMetRand");
			if (launchState.get("antwoordString") != null)
				antwoordString = (String) launchState.get("antwoordString");
			if (launchState.get("scoreMax") != null)
				scoreMax = ((Number) launchState.get("scoreMax")).intValue();
			if (launchState.containsKey("abcKnop"))
				abcVisible = ((Boolean) launchState.get("abcKnop")).booleanValue();
			if (launchState.containsKey("subKnop"))
				subVisible = ((Boolean) launchState.get("subKnop")).booleanValue();
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
			}
			
			rmknop = !isVergelijkingVak && launchStateMap.getBoolean("rmKnop");
			if (launchState.containsKey("pijl"))
				pijl = ((Boolean) launchState.get("pijl")).booleanValue();
			
			//op verzoek van Noordhoff:
			if(isNoordhoff())
				pijl = false;
			bordjesMethode = Boolean.TRUE.equals( launchState.get("bordjesMethode"));
			linStrategieVersie = Boolean.TRUE.equals(launchState.get("linStrategieVersie"));
			linOefenVersie = Boolean.TRUE.equals(launchState.get("linOefenVersie"));
			
			
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
		}
		
		mainPanel = new FlowPanel();
		mainPanel.addStyleName("formuleEditorWithSteps");
		mainPanel.setPixelSize(breedte-2, hoogte-2);
		mainPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		mainPanel.getElement().getStyle().setBorderColor("gray");
		mainPanel.getElement().getStyle().setBackgroundColor("white");
		mainPanel.getElement().getStyle().setBorderWidth(boxMetRand ? 1 : 0, Unit.PX);
		
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
		copyButton.setVisible(!linStrategieVersie && !bordjesMethode);

		rmKnop = new TouchButton();
		Image rmImage = new Image(DWOplayer.DWO_BUNDLE.rmknop().getSafeUri());
		rmKnop.add(rmImage);
		rmKnop.getElement().getStyle().setFloat(Style.Float.RIGHT);
		addRmKnopHandler(rmKnop);
		rmKnop.setVisible(rmknop);
		
		//FIXME: hoe onderscheid maken tussen Noordhoff en gewone DWO?
		abcKnop = new FormuleButton("abc", 1);
		//Image abcKnopImg = new Image(DWOplayer.DWO_BUNDLE.abcknop().getSafeUri());
		//abcKnop = new TouchButton();
		//abcKnop.add(abcKnopImg);
		//abcKnop.setText("abc");
		abcKnop.getElement().getStyle().setFloat(Style.Float.RIGHT);
		//abcKnop.getElement().getStyle().setBackgroundColor("red");
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
		
		if(!(linStrategieVersie || bordjesMethode))
		{	mainPanel.add(copyButton);
			mainPanel.add(downButton);
		}
		mainPanel.add(terugButton);
		mainPanel.add(rmKnop);
		mainPanel.add(subKnop);
		mainPanel.add(abcKnop);
		mainPanel.add(plusKnop);
		mainPanel.add(minKnop);
		mainPanel.add(maalKnop);
		mainPanel.add(deelKnop);
		mainPanel.add(haakjesKnop);
		mainPanel.add(herleidKnop);
		mainPanel.add(ontbindKnop);
		mainPanel.add(splitsKnop);
		mainPanel.add(wortelBewerkKnop);
		
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
		
		checkimg = new Image(FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
		contentPanel.add(checkimg);
		checkimg.setVisible(false);
		
		sp.setWidget(contentPanel);
		mainPanel.add(sp);

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

	private FormuleEditorWithSteps() {
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
		if(viewers.size() > stepPanels.size() - 1)
		{	current.remove(viewers.get(stepPanels.size() - 1).getAsPanel());
			viewers.remove(stepPanels.size() - 1);
		}
		editor = null;
		checkimg.setVisible(false);
		if (hasPrefix)
			current.remove(prefixViewer.getAsPanel());
		terugButton.getElement().getStyle().setVisibility(Visibility.VISIBLE);

		FormuleViewer fv = new FormuleViewer(prefix.substring(2, prefix.length() - 1) + useranswer.substring(2, useranswer.length() - 1));
		fv.setFont(font);
		fv.showResult(FormuleViewer.CORRECT);
		if (latest_answer_viewer != null && !(isToets() && show && !stepsForLinKwad))
		{
			latest_answer_viewer.showResult(FormuleViewer.NONE);

		}
		latest_answer_viewer = fv;
		viewers.add(fv);
		contentPanel.remove(feedbackPanel);
		addFormuleViewer(fv, current);
		
		if (hasFeedback)
		{	contentPanel.add(feedbackPanel);
			contentPanel.setWidgetLeftRight(feedbackPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(feedbackPanel, stepPanelY + fv.getHeightWithImage(), Style.Unit.PX, feedbackPanelHeight, Style.Unit.PX); 
		}
		nagekeken = true;
		correct = Boolean.TRUE;
		score = scoreMax;
		if(mode == 1)
			score = Math.max(0, scoreMax - errorCount * foutStraf);
		if(!setState)
			comRoot.setChanged(false);
	}
	
	public void addStep(String useranswer, boolean show, boolean setState)
	{
		if(linStrategieVersie || linOefenVersie)
			return;
		voegRegelToe(useranswer, show, setState);
	}
	
	public void zetPijlVakMaat()
	{
		contentPanel.setWidgetTopHeight(pijlVak, pijlVak.getAbsoluteTop() - contentPanel.getAbsoluteTop(), Style.Unit.PX, pijlVak.getHeight(), Style.Unit.PX);
	}

	public void voegRegelToe(String useranswer, boolean show, boolean setState)
	{
		//int goedHalfFout = editor.getGoedHalfFout();
		stapOk = false;
		nagekeken = false;
		correct = Boolean.FALSE;//moet correct hier niet null zijn?
		contentPanel.remove(feedbackPanel);
		
		vervangEditorDoorViewer(useranswer, show, setState);
		
		terugButton.getElement().getStyle().setVisibility(Visibility.VISIBLE);
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
		scrollToBottom();
	}
	
	public void addBordjesStap()
	{
		String select = viewers.get(viewers.size() - 1).getSelectionString();
		if(select == null || select.length() == 0)
			return;
		
		if(editor == null)
			addStep("$f" + latest_answer_viewer.toString() + "@", !isToets(), false);
		
		editor.clearAll();
		editor.insert(select);
		editor.insert("=");
		editor.paint();
		requestFocus();
	}
	
	public void resize()
	{
		LayoutPanel current = stepPanels.get(stepPanels.size() - 1);
		if(editor != null && editor.getAsPanel().getParent() == current)
		{	if(current.getParent() == contentPanel) // FIXME why? 
				contentPanel.setWidgetTopHeight(current, stepPanelY, Style.Unit.PX, hoogteStepPanelMetEditor(), Style.Unit.PX);
			current.setWidgetTopHeight(editor.getAsPanel(), 0, Style.Unit.PX, hoogteStepPanelMetEditor(), Style.Unit.PX);
			current.setWidgetLeftWidth(editor.getAsPanel(), hasPrefix?prefixViewer.getWidth() + 23:23, Style.Unit.PX, editor.getMainRegel().getWidth(), Style.Unit.PX);
			if(hasPrefix)
			{	current.setWidgetTopHeight(prefixViewer.getAsPanel(), editor.getMainRegel().getAsHoogte() - prefixViewer.getMainRegel().getAsHoogte(), Style.Unit.PX, prefixViewer.getHeight(), Style.Unit.PX);
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
		if ( correct != Boolean.TRUE || isToets()) // FIXME copystep in mode 2 of 3
		{
			String currentTekst = "";
			if(editor == null)
			{	if(latest_answer_viewer != null)
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
	
	public void downStep()
	{
		if (correct != Boolean.TRUE || isToets())
		{
			if (stapOk || isToets())
			{	
				if (editor == null)
					voegRegelToe("$f" + latest_answer_viewer.toString() + "@", !isToets(), false);
				else
					voegRegelToe("$f" + editor.toString() + "@", !isToets(), false);
			}
		}
	}

	private boolean isToets() 
	{
		return mode == OpdrNavIF.ZELFTOETS || mode == OpdrNavIF.EINDTOETS;
	}
	
	public boolean isUitgeklapt()
	{
		return isUitgeklapt;
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
			}
			else if (!hasStartString) //stapNr is nu 0, je zit dus in eerste regel
			{
				if (!linStrategieVersie && !bordjesMethode)
				{
					editor = addNewEditor(current);
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
		{	contentPanel.remove(pijlVak);
			if(pijlVak.geefOperator().equals("sub"))
				substitutie = null;
			pijlVakken.remove(pijlVakken.size() - 1);
			if(pijlVakken.size() > 0)
				pijlVak = pijlVakken.get(pijlVakken.size() - 1);
			else
				pijlVak = null;
		}
	}
	

	public void setFeedback(String feedback)
	{
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

	public Boolean getExact()
	{
		return exact;
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
		int index = s.indexOf("=");
		if (index == -1)
			index = s.indexOf("\u2248");
		if (index > -1)
		{
			s = s.substring(index + 1);
		}
		return s;
	}

	public String removeIsTeken(String s)
	{
		if ((s.length() > 0) && (s.charAt(s.length() - 1) == '=' || s.charAt(s.length() - 1) == '\u2248'))
		{
			int isIndex = s.length() - 1;
			s = s.substring(0, isIndex);
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
		p.setWidgetTopHeight(tp, 0, Style.Unit.PX, Math.max(hasPrefix?prefixViewer.getHeight():0, editor.getMainRegel().getHeight()), Style.Unit.PX);
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
				{	if(editor.getAsPanel().getParent().equals(panel))
					{	if(event.getClientX() > editor.getAsPanel().getAbsoluteLeft() + editor.getMainRegel().getWidth())	
						{	requestFocus();
							editor.startSelection(editor.getMainRegel().getWidth(), 0);
							editor.endSelection(editor.getMainRegel().getWidth(), 0);
						}
						else if(event.getClientX() < editor.getAsPanel().getAbsoluteLeft())
						{	requestFocus();
							editor.startSelection(0, 0);
							editor.endSelection(0, 0);
							editor.cursorToLeft();
						}
						else if(!editor.getKeyboard().getEditor().equals(editor))
						{	requestFocus();
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

	
	FormuleEditorWithAnswer editorInstance() {
		return new FormuleEditorWithAnswer(h, isVergelijkingVak, this, randomVarNamen, randomVarWaarden, avChecker);
		
	}

	private void addButtonHandler(final TouchButton tb)
	{
		tb.addTouchStartHandler(new TouchStartHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
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
			{	if(substitutie == null)
				maakStap("sub");
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

	private void addRmKnopHandler(final TouchButton rmKnop) {
		rmKnop.addTouchStartHandler(new TouchStartHandler() {

			@Override
			public void onTouchStart(TouchStartEvent event) {
				berekenStap();
			}
			
		});
	}
	
	
	protected void berekenStap() {
		// TODO Auto-generated method stub
		logger.severe("Implement me!");
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
		public void onTouchEnd(TouchEndEvent event) {
			super.onTouchEnd(event);
			if(bordjesMethode)
			{ 	addBordjesStap();
				
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
		String antwoordString = "";
		String substitutieString = "";
		String[] gebruikersSubStrings = null;
		int errorCount = 0;
		
		if(editor != null && !isToets())
			//Sietske: Hier wellicht ook beter editor.kijkNa(false, false, false); zie getState FormuleEditorWithAnswer.
			editor.kijkNa();
// zet score/correct als editor nog open staat en gevuld is.
		if(editor != null && isToets() && !editor.toString().isEmpty())
			bepaalScore();
		
		stapNr = this.stapNr;
		errorCount = this.errorCount;
		formuleVakInhouden = new String[stapNr + 1];
		for (int i = 0; i < stapNr + 1; i++)
		{
			if (viewers.size() > i && viewers.get(i) != null)
				formuleVakInhouden[i] = "$f" + getViewerString(i) + "@" ;
			else
				formuleVakInhouden[i] = "$f@";
		}
		
		pijlVakInhouden = new String[stapNr];
		pijlVakOperatoren = new String[stapNr];
		for (int i = 0; i < stapNr && pijlVakken.size() > i; i++) {
			pijlVakInhouden[i] = pijlVakken.get(i).geefExpressieString();
			pijlVakOperatoren[i] = pijlVakken.get(i).geefOperator();
		}
		if(editor != null)
			antwoordString = editor.toString();
		if(!antwoordString.equals(""))
		{
			this.ingevuld = true;
			formuleVakInhouden[stapNr] = "$f" + antwoordString + "@"; // antwoordstring is laatste inhoud.
		}
		if(!hasStartString && !formuleVakInhouden[0].equals("$f@"))
			this.ingevuld = true;
		if(hasStartString && stapNr > 0 && !formuleVakInhouden[1].equals("$f@"))
			this.ingevuld = true;
		ingevuld = this.ingevuld;
		
		nagekeken = this.nagekeken;
		if (substitutie != null)
			substitutieString = "$f" + substitutie.toString() + "@";
		//terugzetten als gebruikersSubstitutiesVak gemaakt:
		//gebruikersSubStrings = gebruikersSubstitutiesVak.geefRegels();

		HashMap<String, Object> h = new HashMap<String, Object>();
		h.put("stapNr", new Integer(stapNr));
		h.put("formuleVakInhouden", formuleVakInhouden);
		h.put("antwoordString", "$f" + antwoordString + "@");
		h.put("pijlVakInhouden", pijlVakInhouden);
		h.put("pijlVakOperatoren", pijlVakOperatoren);
		h.put("ingevuld", new Boolean(ingevuld));
		h.put("nagekeken", new Boolean(nagekeken));
		h.put("errorCount", new Integer(errorCount));
		h.put("substitutieString", substitutieString);
		h.put("gebruikersSubStrings", gebruikersSubStrings);
		
		if(dwologger!= null) dwologger.getStateHook(h);
		return h;
	}

	private String getViewerString(int i) {
		String string = (viewers.get(i)).toString();
		if(hasPrefix && !isVergelijkingVak)
		{	 
			
			string = removePrefix(string);
		}
		
		return string;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
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
		if (h.get("errorCount") != null)
			errorCount = ((Number) h.get("errorCount")).intValue();
		if (h.get("formuleVakInhouden") != null)
		{
			formuleVakInhouden = JSONUtilities.toStringArray(h.get("formuleVakInhouden"));
			for (int i = 0; i < formuleVakInhouden.length; i++) {
				if(formuleVakInhouden[i].startsWith("$f"))
					formuleVakInhouden[i] = formuleVakInhouden[i].substring(2, formuleVakInhouden[i].length()-1);
			}
		}
		if (h.containsKey("pijlVakInhouden"))
		{	pijlVakInhouden = JSONUtilities.toStringArray(h.get("pijlVakInhouden"));
			for(int i = 0; i < pijlVakInhouden.length; i++)
			{	if(pijlVakInhouden[i] != null && pijlVakInhouden[i].startsWith("$f"))
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
		this.errorCount = errorCount;
		//terugzetten als gebruikersSubstitutiesVak gemaakt:
		//gebruikersSubstitutiesVak.zetRegels(gebruikersSubStrings);
		
		if (!substitutieString.equals(""))
			substitutie = FormuleParser.geefExpressie(substitutieString);

		int oudStepPanelY = stepPanelY;
		
		stepPanelY = 0;
		for (int i = 0; i < stapNr + 1; i++)
		{
			
			if (i == 0 && hasStartString)
			{	
				if(i < stapNr)
				{	if(linStrategieVersie || linOefenVersie || bordjesMethode || !(pijlVakOperatoren[i] == null || pijlVakOperatoren[i].equals("")))
					{	zetPijlVakNeer(pijlVakOperatoren, pijlVakInhouden, i, viewers.get(i).getHeight()/2);
					}
					i++;
				}
				else //in dit geval is stapNr 0. 
				{	stepPanelY = oudStepPanelY;
					return;
				}
			}

			FormuleViewer fv = new FormuleViewer(setViewerString(formuleVakInhouden, i));
			fv.setFont(font);
			
			while(i < viewers.size())
			{	int last = viewers.size()-1;
				
				Panel asPanel = viewers.get(last).getAsPanel();
				if(asPanel.getParent() != null)
					stepPanels.get(last).remove(asPanel);
				viewers.remove(last);
				asPanel.removeFromParent();
			}
			viewers.add(fv);
			
			LayoutPanel stepPanel = null;
			if (i == 0 || i == 1 && hasStartString && !(linStrategieVersie || linOefenVersie || bordjesMethode))
			{
				stepPanel = stepPanels.get(i);
				if(editor != null)
				{	stepPanel.remove(editor.getAsPanel());
					editor.getAsPanel().removeFromParent();
					editor = null;
					checkimg.setVisible(false);
				}
				else if(viewers.size() > i)
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
				if(antwoordString.startsWith("$f") && antwoordString.endsWith("@"))
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
				if(viewers.size() > 0)
					stepPanelY += viewers.get(viewers.size() - 1).getHeight() + stapH;
				contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, hoogteStepPanelMetEditor(), Style.Unit.PX);
				
				if(i < stapNr)
					zetPijlVakNeer(pijlVakOperatoren, pijlVakInhouden, i, stepPanelY + hoogteStepPanelMetEditor()/2);
			}
			else
			{
				addFormuleViewer(fv, stepPanel);
				if(viewers.size() > 1)
					stepPanelY += viewers.get(viewers.size() - 2).getHeight() + stapH;
				contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, fv.getHeightWithImage(), Style.Unit.PX);
				
				if(i < stapNr)
					zetPijlVakNeer(pijlVakOperatoren, pijlVakInhouden, i, stepPanelY + fv.getHeightWithImage()/2);
					
				if(bordjesMethode)
				{	if(viewers.size() > 1)
						freezeViewer(viewers.get(viewers.size() - 2));
					if(!nagekeken)
						addFormulePanelListeners((TouchPanel) fv.getAsPanel(), fv); 
				}
			}
			
			if (viewers.size() > 0)
				latest_answer_viewer = viewers.get(viewers.size() - 1);
			
//			if(isToets())
//				fv.getAsPanel().getElement().getStyle().setMarginLeft(23, Unit.PX);
			if(!isToets())
			{	if (i == stapNr && nagekeken)
				{	VergelijkingMeerv verg = FormuleParser.parseVergelijking("$f" + fv.toString() + "@");
					if(linStrategieVersie || (bordjesMethode && verg.isEindOplossing(verg.geefVergelijkingVar())))
					{	fv.showResult(FormuleViewer.CORRECT);
						setAndAddFeedback(Text.constants.feedbackTekst04());
						//"De vergelijking is correct opgelost."
						stapOk = false;
					}
					else if(editor != null)//doel: laatste antwoord nogmaals nakijken, om juiste feedback te genereren.
					{
						editor.kijkNa(true);
						//maakNakijkenAf(false);
					}
					else
					{	viewers.remove(fv);
						stepPanel.remove(fv.getAsPanel());
						editor = addNewEditor(stepPanel);
						String currentTekst = latest_answer_viewer.toString();
						if (hasPrefix)
							currentTekst = removePrefix(currentTekst);
						currentTekst = removeIsTeken(currentTekst);
						editor.insert(currentTekst);
						//hier setChanged(false)?
						if(viewers.size() > 0)
							latest_answer_viewer = viewers.get(viewers.size() - 1);
						editor.kijkNa(true);
						//maakNakijkenAf(false);
					}
					
				}
				else if(i == stapNr && editor != null)
				{
					if(editor.toString().equals("") && (i > 1 || (!hasStartString && i > 0)))
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
						if(pijlVakken.size() > 0)
						{	
							if(pijlVakken.get(pijlVakken.size() - 1).getParent() != null)
								contentPanel.remove(pijlVakken.get(pijlVakken.size() - 1));
							pijlVakken.remove(pijlVakken.size() - 1);
						}
						
						editor = addNewEditor(stepPanel);
						String currentTekst = latest_answer_viewer.toString();
						if (hasPrefix)
							currentTekst = removePrefix(currentTekst);
						currentTekst = removeIsTeken(currentTekst);
						editor.insert(currentTekst);
						
						if(viewers.size() > 0)
							latest_answer_viewer = viewers.get(viewers.size() - 1);
						editor.kijkNa(true);
					}
					else 
					{	editor.kijkNa(true);
						
					}
				}
				else if(i == stapNr && (bordjesMethode || linOefenVersie))
				{
					fv.showResult(FormuleViewer.ALMOSTCORRECT);
				}
				else if(i == stapNr && !linStrategieVersie)
				{
					//nu: editor = null. 
					viewers.remove(fv);
					stepPanel.remove(fv.getAsPanel());
					editor = addNewEditor(stepPanel);
					editor.insert(latest_answer_viewer.toString());
					editor.kijkNa(true);
					//maakNakijkenAf(false);
				}
				else if (i == stapNr - 1 && !nagekeken && !linStrategieVersie)
					fv.showResult(FormuleViewer.ALMOSTCORRECT);
				else
					fv.showResult(FormuleViewer.NONE);
				if(correct == Boolean.TRUE)
				{	score = scoreMax;
					if(mode == 1) // met aftrek
						score = Math.max(scoreMax - errorCount * foutStraf, 0);
				}
				else
					score = 0;
			}	
		}
		if(isToets())
		{	if(nagekeken)
			{	
				
				kijkToetsNa(true, true);

			}
			else
			{	
				// FIXME bepaal score voor 'getScore()' en haal daarna alle vinkjes weer weg....	kijkna(false,false,false)?		
				
				bepaalScore();
				
				for(int i = 0; i < viewers.size(); i++)
				{
					viewers.get(i).showResult(FormuleViewer.NONE);
				}
			}
			
		}

		if (stapNr > 0 || stapNr == 0 && !hasStartString)//1 || stapNr == 1 && !hasStartString)
			terugButton.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		
		if(editor != null)
		{	editor.setCurrentElementRepaint();
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
	 * Bepaal de score zonder iets aan het beeld te doen.
	 * <ol>
	 * <li> Als we een niet lege editor hebben, bepaal dan zijn score.
	 * <li> Als de editor leeg is, maak een editor van de laatste stap en dan 1
	 * </ol>
	 * @see #kijkToetsNa(boolean,boolean)
	 */
	private void bepaalScore() {
		
		if(editor != null && ! editor.toString().isEmpty())
		{
			// er is een editor, XXX pas op voor *linversie*.
			//editor.kijkNa(false, false, false); // roept maakNakijkenAf aan en zet zo score		DEZE WERKT NIET	
			// hopelijk deze wel
			bepaalScore(editor.toString(), getLatestAnswer());
		} else {
			if( stapNr > 0) {
				String formule = latest_answer_viewer.toString();
				int size = viewers.size();
				String formuleMin1;
				if(size >= 2)
					formuleMin1 = viewers.get(size-2).toString();
				else
					formuleMin1 = null;
				bepaalScore(formule, formuleMin1);
			}
			
		}
		
	}
/**
 * Hulpje van bepaalScore().
 * Bepaal score van twee opeenvolgende regels.
 * @param formule laatste formule
 * @param formuleMin1 voorlaatste formule/null
 */
	private void bepaalScore(String formule, final String formuleMin1) {
		FormuleEditorWithAnswer editor = this.editor;
		final FormuleEditorWithSteps deze = this;
		FormuleEditorWithSteps not_this = new FormuleEditorWithSteps() {

			@Override
			public void maakNakijkenAf(boolean backStep, boolean show,
					boolean setState) {
				deze.score = ((FormuleEditorWithAnswer) this.getEditor()).getScore();
				deze.correct = ((FormuleEditorWithAnswer) this.getEditor()).isCorrect();
			}
// alle upcalls in ed.kijkna hier overnemen
// onder geen beding!
			public void backStep(boolean setState) {}
			public void resize() {}
			public boolean controleerStap() {
				return true; // MOET DIE OOK?
			}
			// getlatestanswer, getsubstitutie, getgebruikersubstitutie, verhoog error count
			/* (non-Javadoc)
			 * @see nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps#getLatestAnswer()
			 */
			@Override
			public String getLatestAnswer() {
				return formuleMin1;
				//return super.getLatestAnswer();
			}
			/* (non-Javadoc)
			 * @see nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps#getSubstitutie()
			 */
			@Override
			public Expressie getSubstitutie() {
				return deze.getSubstitutie();
			}
			/* (non-Javadoc)
			 * @see nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.FormuleEditorWithSteps#getGebruikersSubstituties()
			 */
			@Override
			public Vergelijking[] getGebruikersSubstituties() {
				return deze.getGebruikersSubstituties();
			}
			
		};
		FormuleEditorWithAnswer ed =new FormuleEditorWithAnswer(h, isVergelijkingVak, not_this, randomVarNamen, randomVarWaarden, avChecker);; // ed heeft this als fe, maar this niet ed als editor!
		ed.zetMode(mode);
		ed.insert(formule);
		not_this.editor = ed; // voor de upcall maakNakijkenAf
		ed.kijkNa(false,false,false);
		this.editor = editor;
	}

	private String setViewerString(String[] formuleVakInhouden, int i) {
		String string = formuleVakInhouden.length > i?formuleVakInhouden[i]:"";
		if(hasPrefix && !isVergelijkingVak) {
			string = prefix.substring(2, prefix.length()-1) + string;
		}
		return string;
	}
	
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
		if(!check)
			return;
		//contentPanel.remove(checkimg);
		if(latest_answer_viewer != null)
			latest_answer_viewer.showResult(FormuleViewer.NONE);

		if(uitslag == AntwoordVakChecker.FOUT)
			checkimg.setUrl(FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri());
		else if(uitslag == AntwoordVakChecker.HALF || uitslag == AntwoordVakChecker.DOOR)
			checkimg.setUrl(FORMULE_BUNDLE.mw_vinkje_geel().getSafeUri());
		else if(uitslag == AntwoordVakChecker.GOED)
			checkimg.setUrl(FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
		
		checkimg.setVisible(true);
		contentPanel.setWidgetLeftWidth(checkimg, 3, Style.Unit.PX, 20, Style.Unit.PX);
		contentPanel.setWidgetTopHeight(checkimg, stepPanelY, Style.Unit.PX, 20, Style.Unit.PX);
		
		
	}

	
	public void kijkNa()
	{
		kijkNa(false);
	}
	
	public void kijkNa(boolean setState)
	{
		kijkNa(false, true, setState);
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
		if(editor != null && editor.toString().equals(""))
		{	backStep(setState); 
		}
		if (editor != null && !editor.toString().equals(""))
		{	vervangEditorDoorViewer("$f" + editor.toString() + "@", true, true);
		}
				
		aantalStappen = viewers.size();
		String[] viewersInhouden = new String[viewers.size()];
		if(viewers.size() > 0)
			viewersInhouden[0] = viewers.get(0).toString();
		for(int i = viewers.size() - 1; i > start - 1; i--)
		{
			viewersInhouden[i] = viewers.get(i).toString();
			stepPanels.get(i).remove(viewers.get(i).getAsPanel());
			if(i > start)
			{	stepPanelY -= stapH + viewers.get(i).getHeight();
				stepPanels.remove(i);
				haalPijlVakWeg();
			}
			viewers.remove(i);
		}
		for(int i = 0; i < imagesStappen.size(); i++)
		{
			contentPanel.remove(imagesStappen.get(i));
		}
		imagesStappen.clear();

		stapNr = start;
		for (int i = start; i < aantalStappen; i++)
		{
			if(editor == null)
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
		if(editor != null)
			zetGoedFoutEditor(editor.getGoedHalfFout());

	}
	
	public void maakNakijkenAf(boolean backStep, boolean show, boolean setState)
	{
		int goedHalfFout = editor.getGoedHalfFout();
		if(goedHalfFout == AntwoordVakChecker.GEEN)
			ingevuld = false;
		else
			ingevuld = true;
		if(isToets())
		{
			correct = editor.isCorrect(); //XXX wordt elders gezet, maar waar en waarom?
			score = editor.getScore(); // altijd score ophalen, ook bij noshow
			if(show)
			{
				if(stepPanels.size() < aantalStappen)
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
			{	if(goedHalfFout == AntwoordVakChecker.FOUT && editor.isSyntaxFout())
				{
					nagekeken = true;
					setAndAddFeedback(editor.getFeedback());
				}
				else
				{	setFeedback("");
					feedbackPanel.removeFromParent();
				
					addStep("$f" + editor.toString() + "@", show, setState);
				}
				return;
			}
		}
		//stapOk juiste waarde geven.
		if(goedHalfFout == AntwoordVakChecker.GOED)
		{	stapOk = false;
			if (!pijl)
				stapOk = true;
			//correct = true; -- dit gebeurt in lastStep nog (en anders gebeurt lastStep niet!)
			//score = editor.getScore();
		}
		else if(goedHalfFout == AntwoordVakChecker.DOOR)
		{	stapOk = true;
			//score = editor.getScore();
			
		}
		else 
		{	stapOk = false;
			nagekeken = true;
			//score = editor.getScore();
		}
		score = editor.getScore();
		if(mode == 1)
		{
			//in score niet aantal fouten uit deze specifieke editor (regel), maar aantal fouten uit gehele editorWithSteps meenemen
			score = editor.getScoreZonderAftrek();
			score = Math.max(0, score - foutStraf * errorCount);
		}
		
		if(bordjesMethode)
		{	if(stapOk || goedHalfFout == AntwoordVakChecker.GOED)
			{	vervangEditorDoorViewer("$f" + editor.toString() + "@", show, setState);
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
		if (op.equals("implicatie") || en == null)
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
			vergAntwoord = vergAntwoord.substitueer(substitutie, "p");
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
		return gebruikersSubstituties;
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
			else if(stepPanels.size() > pijlVakken.size() && (stapNr < stepPanels.size() - 1 || editor != null && editor.toString().equals("")) && pijlVakken.size() > 0 && !stapOk)//ik denk dat pijlVakken.size() hier weer weg kan.
			{
				LayoutPanel current = stepPanels.get(stepPanels.size() - 1);
				stepPanelY -= stapH + latest_answer_viewer.getHeight();
				if(editor == null)
				{	current.remove(viewers.get(viewers.size() - 1).getAsPanel());
					viewers.remove(viewers.size() - 1);
				}
				else
				{	current.remove(editor.getAsPanel());
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
			else if(stepPanels.size() > pijlVakken.size() && stapNr >= stepPanels.size() - 1 && editor != null)
			{
				vervangEditorDoorViewer("$f"+ editor.toString() + "@", !isToets(), false);
				
			}
			
			pijlVak = new PijlVak(operator, this, false);
			int y = stepPanelY + latest_answer_viewer.getHeight()/2;
			contentPanel.add(pijlVak);
			contentPanel.setWidgetRightWidth(pijlVak, 0, Style.Unit.PX, pijlX, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(pijlVak, y, Style.Unit.PX, pijlVak.getHeight(), Style.Unit.PX);
			if(operator.equals("abc") || operator.equals("sub"))
				contentPanel.setWidgetRightWidth(pijlVak, 0, Style.Unit.PX, pijlX + 30, Style.Unit.PX);
			pijlVak.setPijlVisible(pijl);
			pijlVak.paintComponent();
			pijlVakken.add(pijlVak);
			
			pijlVak.getEditor().requestFocus();
			scrollToBottom();
		}
	}
	
	public void vervangEditorDoorViewer(String antwoord, boolean show, boolean setState)
	{
		if(editor == null)
		{
			// TODO voeg in de laatste viewer zonodig een =-teken toe
//			if (!isVergelijkingVak && !hasPrefix && (antwoord.charAt(antwoord.length() - 2)) != '=')
//				antwoord = antwoord.substring(0, antwoord.length() - 1) + "=@";
//
//			int lastIndex = viewers.size() - 1;
//			viewers.set(lastIndex, new FormuleViewer(antwoord));
			// dit geeft rare effecten van een viewer die blijft staan
			// 1. je ziet in eerste instantie zonder =, als je de nieuwe lege regel eronder weghaalt, zie je wel de =
			// 2. als je dan de regel met goede = (die onterecht blijft staan) weghaalt, blijft een viewer (zonder =) in beeld
			
			return;
		}
		
		LayoutPanel current = stepPanels.get(stepPanels.size() - 1);
		current.remove(editor.getAsPanel());
		int selectionStartX = -1;
		int selectionStartY = 0;
		int selectionEndX = -1;
		int selectionEndY = 0;
		if(editor.hasSelection())
		{	int[] selectionBounds = editor.getSelectionBounds();
			selectionStartX = selectionBounds[0];
			selectionEndX = selectionBounds[1];
			selectionStartY = selectionBounds[2];
			selectionEndY = selectionBounds[3];
		}
		int goedHalfFout = editor.getGoedHalfFout();
		editor = null;
		checkimg.setVisible(false);
		if(hasPrefix)
			current.remove(prefixViewer.getAsPanel());
		if (!isVergelijkingVak && !hasPrefix && (antwoord.charAt(antwoord.length() - 2)) != '=')
			antwoord = antwoord.substring(0, antwoord.length() - 1) + "=@";
		if(bordjesMethode) {
			// convert to stringStrikt.
			try{
				VergelijkingMeerv e = FormuleParser.parseVergelijking(antwoord);
				antwoord = "$f"+ e.toStringStrikt() + "@";
			}
			catch(Exception e){}
		}
		FormuleViewer fv = new FormuleViewer(prefix.substring(2, prefix.length() - 1) + antwoord.substring(2, antwoord.length() - 1));
		fv.setFont(font);
		fv.setSelection(selectionStartX, selectionStartY, selectionEndX, selectionEndY);
		fv.showResult(FormuleViewer.ALMOSTCORRECT);
		VergelijkingMeerv verg = FormuleParser.parseVergelijking("$f" + fv.toString() + "@");
		if(bordjesMethode && verg.isEindOplossing(verg.geefVergelijkingVar()))
		{	fv.showResult(FormuleViewer.CORRECT);
			setAndAddFeedback(Text.constants.feedbackTekst04());
			//"De vergelijking is correct opgelost."
			stapOk = false;
			nagekeken = true;
			correct = Boolean.TRUE;
			score = scoreMax;
			if(mode == 1)
				score = Math.max(0, scoreMax - errorCount * foutStraf);
			if(!setState)
				comRoot.setChanged(false);
		}
		if(isToets())
		{
			if(show && !stepsForLinKwad)
			{	if(goedHalfFout == AntwoordVakChecker.GOED)
					fv.showResult(FormuleViewer.CORRECT);
				else if(goedHalfFout == AntwoordVakChecker.DOOR || goedHalfFout == AntwoordVakChecker.HALF)
					fv.showResult(FormuleViewer.ALMOSTCORRECT);
				else if(goedHalfFout == AntwoordVakChecker.FOUT)
					fv.showResult(FormuleViewer.WRONG);
			}
			else
			{
				fv.showResult(FormuleViewer.NONE);
			}
				
		}
			
		
		if (latest_answer_viewer != null && !(hasStartString && stapNr == 1) && !((mode == 2 || mode == 3) && show && !stepsForLinKwad))
			latest_answer_viewer.showResult(FormuleViewer.NONE);
		latest_answer_viewer = fv;
		viewers.add(fv);
		addFormuleViewer(fv, current);
		if(bordjesMethode)
		{	if(viewers.size() > 1)
				freezeViewer(viewers.get(viewers.size() - 2));
			if(!verg.isEindOplossing(verg.geefVergelijkingVar()))
				addFormulePanelListeners((TouchPanel) fv.getAsPanel(), fv); 
		}
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

		if ((mode != 2 && mode != 3) || (hasStartString && stapNr - 1 == 0))
		{	//formuleVakken[stapNr - 1].setEditable(false);
		}
		
		LayoutPanel stepPanel = maakNieuwStapPanel();
		if(linOefenVersie)
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
			try{
				fv = new FormuleViewer(prefix.substring(2, prefix.length() - 1) + vergNieuw.toString());
			}
			catch(Exception e)
			{	fv = new FormuleViewer("");
				vergNieuw = null;
			}
			fv.setFont(font);
			//fv.showResult(fv.ALMOSTCORRECT);
			
			if (latest_answer_viewer != null && !(hasStartString && stapNr == 1))
				latest_answer_viewer.showResult(FormuleViewer.NONE);
			stepPanelY += stapH + latest_answer_viewer.getHeight();
			if(vergNieuw != null)
				latest_answer_viewer = fv;
			
			if(vergNieuw != null && vergNieuw.isEindOplossing(vergNieuw.geefVergelijkingVar()))
			{	fv.showResult(FormuleViewer.CORRECT);
				setAndAddFeedback(Text.constants.feedbackTekst04());
				//"De vergelijking is correct opgelost."
				stapOk = false;
				nagekeken = true;
				correct = Boolean.TRUE;
				score = scoreMax;
				if(mode == 1)
					score = Math.max(0, scoreMax - errorCount * foutStraf);
				comRoot.setChanged(false);
			}
			else if(vergNieuw != null && linStrategieVersie)
			{	fv.showResult(FormuleViewer.NONE);
				stapOk = true;
			}
			else if(vergNieuw != null)
			{
				fv.showResult(FormuleViewer.ALMOSTCORRECT);
				stapOk = true;
			}
			else
			{
				fv.showResult(FormuleViewer.NONE);
				stapOk = false;
			}
			viewers.add(fv);
			Panel p = fv.getAsPanel();
			p.getElement().getStyle().setProperty("display", "inline");
			stepPanel.add(p);
			if(bordjesMethode)
				addFormulePanelListeners((TouchPanel) p, fv); 
			
			contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, fv.getHeightWithImage(), Style.Unit.PX);
			if(linOefenVersie)
			{	
				stepPanel.remove(p);
				viewers.remove(fv);
				addStep("$f" + fv.toString() + "@", !isToets(), false); 
				stapOk = false;
			}
			else if(!bordjesMethode && !linStrategieVersie)
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
			hoogte = Math.max(hoogte, prefixViewer.getHeight());
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
}