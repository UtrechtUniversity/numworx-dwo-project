package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditor;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleEditorTouchHandler;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleViewer;
import nl.uu.fi.dwo.formule.client.formuleobjects.FormuleButton;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionView;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.FormuleKeyboard;
import nl.uu.fi.dwo.mobile.client.ui.TouchButton;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.utils.PopupFacade;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
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
import com.googlecode.mgwt.ui.client.widget.touch.TouchPanel;

import fi.wiskopdr.AntwoordVakChecker;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.Vergelijking;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import fi.wiskopdr.text.Text;








/**
 * Used for showing formula's that can be solved in steps.
 * 
 * @author Evertson Croes
 * 
 */
public class FormuleEditorWithSteps implements InteractionView
{

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
	private Map<String, Object> instellingen;
	private ArrayList<FormuleViewer> viewers = new ArrayList<FormuleViewer>();
	private FormuleEditorWithAnswer editor = null;
	private Widget prefixViewer;
	private FormuleViewer latest_answer_viewer;
	private ScrollPanel sp = null;
	
	private LayoutPanel contentPanel = null;
	//contentPanel is layoutPanel geworden om pijlvakken (met operatoren, abc, substitutie, etc) neer te kunnen zetten.
	//private FlowPanel contentPanel = null;
	private FlowPanel feedbackPanel = null;
	int feedbackPanelHeight = 34;
	private FlowPanel mainPanel = null;
	private OpdrNavIF comRoot;
	private int mode;
	
	//private PijlVak[] pijlVakken;
	private PijlVak pijlVak;
	private boolean pijl = false;
	//private int pijlX = "GR".equals(WiskOpdr.deployVariant) ? 105 : 130;
	private int pijlX = 130;
	//private int stepPanelX = 0;
	private int stepPanelY = 0; //locatie van bovenrand van het laatste (onderste) stepPanel
	private int stapH = 21;
	
	private Expressie substitutie;
	private Vergelijking[] gebruikersSubstituties;
	
	private TouchButton tb = null;
	private TouchButton downButton = null;
	private TouchButton copyButton = null;
	private FormuleButton plusKnop, minKnop, maalKnop, deelKnop, haakjesKnop, herleidKnop, abcKnop, subKnop;
	private FormuleButton ontbindKnop, splitsKnop, wortelBewerkKnop;
	private boolean abcVisible, subVisible;
	private boolean bewerkingKnoppen, bewerkingKnoppenExtra;
	private int stapNr = 0;
	protected HashMap<String, Object> h = null;
	protected String[] randomVarNamen = null;
	protected HashMap randomVarWaarden = null;
	private ArrayList<FlowPanel> stepPanels = new ArrayList<FlowPanel>();
	private ArrayList<PijlVak> pijlVakken = new ArrayList<PijlVak>();
	//private FormuleFont font = FormuleFont.createFromFontSize(16);

	private int score;
	private int scoreMax;
	private Boolean correct;
	
	private boolean stapOk = true;

	private FormuleFont defaultfont;
	//private boolean answeredCorrectly = false;
	private CssColor hlColor = CssColor.make(255, 255, 255);
	//private CssColor bgColor = CssColor.make(240, 240, 240);
	private CssColor bgColor = CssColor.make(255, 255, 255);
	private boolean ingevuld;
	private boolean nagekeken;
	private boolean hasFeedback;

	protected boolean isVergelijkingVak = false;
	private PopupFacade facade;
	
	private boolean bordjesMethode;
	private boolean linStrategieVersie; // TODO implement this
	private boolean linOefenVersie;

	public FormuleEditorWithSteps(HashMap<String, Object> h, boolean isVergelijkingVak, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		defaultfont = FormuleFont.createFromFontSize(XMLView.getDefaultFontSize());
		
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		this.isVergelijkingVak = isVergelijkingVak;

		if (h != null)
			this.h = h;
		ObjectMap map = JSONUtilities.wrapMap(h);
		if(map.containsKey("breedte"))
			breedte = map.getInt("breedte");
		if(map.containsKey("hoogte"))
			hoogte = map.getInt("hoogte");
		if(map.containsKey("volledigeBreedte"))
			volledigeBreedte = map.getBoolean("volledigeBreedte");
		
		facade = new PopupFacade(h);
		
		if (h.get("interactiePanelLaunchState") != null)
		{
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
			if (launchState.get("startString") != null)
				startString = (String) launchState.get("startString");
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
			
			if (launchState.containsKey("pijl"))
				pijl = ((Boolean) launchState.get("pijl")).booleanValue();
			
			
			
			
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

			FormuleViewer f = new FormuleViewer(prefix);
			f.setFont(defaultfont);
			prefixViewer = f.getAsPanel();
			prefixViewer.getElement().getStyle().setProperty("display", "inline-block");
			prefixViewer.getElement().getStyle().setProperty("clear", "both");
			prefixViewer.getElement().getStyle().setMarginLeft(23, Unit.PX);
		}
		
		mainPanel = new FlowPanel();
		mainPanel.getElement().getStyle().setWidth(breedte - 2, Unit.PX);//-2 om ook rand zichtbaar te krijgen
		mainPanel.getElement().getStyle().setHeight(hoogte - 2, Unit.PX);//-2 om ook rand zichtbaar te krijgen
		mainPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		mainPanel.getElement().getStyle().setBorderColor("gray");
		//mainPanel.getElement().getStyle().setBackgroundColor(CssColor.make(240, 240, 240).toString());
		mainPanel.getElement().getStyle().setBackgroundColor("white");
		mainPanel.getElement().getStyle().setBorderWidth(boxMetRand ? 1 : 0, Unit.PX);
		//mainPanel.getElement().getStyle().setProperty("lineHeight", "2.0");

		Image buttonImg = new Image(DWOplayer.DWO_BUNDLE.pijlterug().getSafeUri());
		buttonImg.getElement().getStyle().setMargin(2, Unit.PX);
		tb = new TouchButton();
		tb.add(buttonImg);
		tb.getElement().getStyle().setFloat(Style.Float.RIGHT);
		tb.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		addButtonHandler(tb);
		
		Image downButtonImg = new Image(DWOplayer.DWO_BUNDLE.pijldown().getSafeUri());
		downButtonImg.getElement().getStyle().setMargin(2, Unit.PX);
		downButton = new TouchButton();
		downButton.add(downButtonImg);
		downButton.getElement().getStyle().setFloat(Style.Float.RIGHT);
		addDownButtonHandler(downButton);
		//downButton.setVisible...

		Image copyButtonImg = new Image(DWOplayer.DWO_BUNDLE.pijlcopy().getSafeUri());
		copyButtonImg.getElement().getStyle().setMargin(2, Unit.PX);
		copyButton = new TouchButton();
		copyButton.add(copyButtonImg);
		copyButton.getElement().getStyle().setFloat(Style.Float.RIGHT);
		addCopyButtonHandler(copyButton);
		copyButton.setVisible(!linStrategieVersie && !bordjesMethode);
		
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
		mainPanel.add(tb);
		
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

		sp.getElement().getStyle().setWidth(breedte - 5, Unit.PX);
		sp.getElement().getStyle().setHeight(hoogte - 50, Unit.PX);
		sp.getElement().getStyle().setOverflow(Overflow.AUTO);
		sp.getElement().getStyle().setFloat(Style.Float.LEFT);

		contentPanel = new LayoutPanel();
		
		feedbackPanel = new FlowPanel();
		feedbackPanel.getElement().getStyle().setFontSize(14, Unit.PX);
		feedbackPanel.getElement().getStyle().setProperty("lineHeight", "1.2");
		feedbackPanel.getElement().getStyle().setWidth(breedte - 25, Unit.PX);
		feedbackPanel.getElement().getStyle().setProperty("display", "inline-block");
		feedbackPanel.getElement().getStyle().setBackgroundColor("#FFFFDD");
		feedbackPanel.getElement().getStyle().setFontSize(XMLView.getDefaultFontSize(), Style.Unit.PX);

		sp.setWidget(contentPanel);
		mainPanel.add(sp);

		FlowPanel stepPanel = new FlowPanel();
		layoutStepPanel(stepPanel);
//		highLight(stepPanel, true);
		if (hasPrefix)
			stepPanel.add(prefixViewer);

		if (!startString.equals("$f@") && stapNr == 0)
		{
			if(bordjesMethode && isVergelijkingVak) {
				// convert to stringStrikt.
				VergelijkingMeerv e = FormuleParser.parseVergelijking(startString);
				startString = "$f"+ e.toStringStrikt() + "@";
			}
			
			
			
			
			if (!isVergelijkingVak && !hasPrefix && (startString.charAt(startString.length() - 2)) != '=')
				startString = startString.substring(0, startString.length() - 1) + "=@";

			hasStartString = true;
			FormuleViewer f = new FormuleViewer(prefix.substring(2, prefix.length() - 1) + startString.substring(2, startString.length() - 1));
			f.setFont(defaultfont);
			f.getMainRegel().getCanvas().getElement().getStyle().setMarginLeft(23, Unit.PX);

			viewers.add(f);
			latest_answer_viewer = f;
			Panel pnl = f.getAsPanel();
			pnl.getElement().getStyle().setProperty("display", "inline-block");
			pnl.getElement().getStyle().setProperty("clear", "both");
			stepPanel.add(pnl);
			if(bordjesMethode){
				Logger.getLogger("FormuleEditorWithStep").info("bordjesmethode");
				addFormulePanelListeners((TouchPanel) pnl, f);
			}
			//highLight(stepPanel, false);
			contentPanel.add(stepPanel);
			contentPanel.setWidgetLeftRight(stepPanel, 5, Style.Unit.PX, 5, Style.Unit.PX); 
			contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, f.getHeight(), Style.Unit.PX);
			stepPanels.add(stepPanel);

			if(!(linStrategieVersie || linOefenVersie || bordjesMethode))
			{	pijlVak = new PijlVak("", this, false);
				int y = stepPanelY + f.getHeight()/2;
				if(pijl)
				{	contentPanel.add(pijlVak);
					contentPanel.setWidgetRightWidth(pijlVak, 0, Style.Unit.PX, pijlX, Style.Unit.PX);
					contentPanel.setWidgetTopHeight(pijlVak, y, Style.Unit.PX, pijlVak.getHeight(), Style.Unit.PX);
				}
				
				pijlVakken.add(pijlVak);
				pijlVak.paintComponent();
				
				stapNr++;
				stepPanelY += f.getHeight() + stapH;
	
				FlowPanel stepPanelNew = new FlowPanel();
				layoutStepPanel(stepPanelNew);
	//			highLight(stepPanelNew, true);
	
				if (hasPrefix)
					stepPanelNew.add(prefixViewer);
				editor = addNewEditor(stepPanelNew);
	
				contentPanel.add(stepPanelNew);
				contentPanel.setWidgetLeftRight(stepPanelNew, 5, Style.Unit.PX, 5, Style.Unit.PX); 
				contentPanel.setWidgetTopHeight(stepPanelNew, stepPanelY, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
				
				stepPanels.add(stepPanelNew);
				stapOk = false;
			}
		}
		else
		{
			if (hasPrefix)
				stepPanel.add(prefixViewer);
			editor = addNewEditor(stepPanel);
			contentPanel.add(stepPanel);
			contentPanel.setWidgetLeftRight(stepPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
			stepPanels.add(stepPanel);
		}

		contentPanel.getElement().addClassName("insert_formule_steps");

		//zorgen dat cursor niet direct, maar pas bij focus verschijnt
		if(editor != null)
			editor.setCurrentElementRepaint();
		
	}

	public void zetInstellingen(Map<String, Object> instellingen)
	{
		this.instellingen = instellingen;
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
	
	public void lastStep(String useranswer)
	{
		if (correct == Boolean.TRUE)
			return;
		FlowPanel current = stepPanels.get(stepPanels.size() - 1);
//		highLight(current, false);
		current.remove(editor.getAsPanel());
		if (hasPrefix)
			current.remove(prefixViewer);
		tb.getElement().getStyle().setVisibility(Visibility.VISIBLE);

		FormuleViewer fv = new FormuleViewer(prefix.substring(2, prefix.length() - 1) + useranswer.substring(2, useranswer.length() - 1));
		fv.setFont(defaultfont);
		
		fv.showResult(fv.CORRECT);
		if (latest_answer_viewer != null)
		{
			latest_answer_viewer.showResult(fv.NONE);

		}
		latest_answer_viewer = fv;
		viewers.add(fv);
		Panel p = fv.getAsPanel();
		p.getElement().getStyle().setProperty("display", "inline");
		contentPanel.remove(feedbackPanel);
		current.add(p);
		if (hasFeedback)
		{	contentPanel.add(feedbackPanel);
			contentPanel.setWidgetLeftRight(feedbackPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(feedbackPanel, stepPanelY + fv.getHeight(), Style.Unit.PX, feedbackPanelHeight, Style.Unit.PX); 
		}
		nagekeken = true;
		correct = true;
		score = scoreMax;
		comRoot.setChanged(false);
	}
	
	public void addStep(String useranswer)
	{
		if(linStrategieVersie || linOefenVersie)
			return;
		voegRegelToe(useranswer);
	}

	public void voegRegelToe(String useranswer)
	{
		stapOk = false;
		nagekeken = false;
		correct = false;
		contentPanel.remove(feedbackPanel);
		vervangEditorDoorViewer(useranswer);
		
		
//		highLight(current, false);
		tb.getElement().getStyle().setVisibility(Visibility.VISIBLE);

//		if (!isVergelijkingVak && !hasPrefix && (useranswer.charAt(useranswer.length() - 2)) != '=')
//			useranswer = useranswer.substring(0, useranswer.length() - 1) + "=@";
//// bordjes methode parse string to strikt.
//		if(bordjesMethode) {
//			// convert to stringStrikt.
//			VergelijkingMeerv e = FormuleParser.parseVergelijking(useranswer);
//			useranswer = "$f"+ e.toStringStrikt() + "@";
//		}
		FormuleViewer fv = viewers.get(viewers.size() - 1);
		

		pijlVak = new PijlVak("", this, false); //new PijlVak("implicatie");
		int y = stepPanelY + fv.getHeight()/2;
				//formuleVakken[stapNr].getLocation().y + formuleVakken[stapNr].getSize().height / 2;
		if (pijl)
		{	contentPanel.add(pijlVak);
			contentPanel.setWidgetRightWidth(pijlVak, 0, Style.Unit.PX, pijlX, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(pijlVak, y, Style.Unit.PX, pijlVak.getHeight(), Style.Unit.PX);
		}
		pijlVakken.add(pijlVak);
		pijlVak.paintComponent();
		
		//pijlVak = pijlVakken[stapNr];
		
		FlowPanel stepPanel = new FlowPanel();
		layoutStepPanel(stepPanel);
//		highLight(stepPanel, true);
		stapNr++;

		if (hasFeedback)
		{	feedbackPanel.removeFromParent();
			contentPanel.add(feedbackPanel);
			contentPanel.setWidgetLeftRight(feedbackPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(feedbackPanel, stepPanelY + fv.getHeight(), Style.Unit.PX, feedbackPanelHeight, Style.Unit.PX); 
		}

		if (hasPrefix)
			stepPanel.add(prefixViewer);
		editor = addNewEditor(stepPanel);
		stepPanelY += fv.getHeight() + stapH;
		contentPanel.add(stepPanel);
		contentPanel.setWidgetLeftRight(stepPanel, 5, Style.Unit.PX, 5, Style.Unit.PX); 
		contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
		stepPanels.add(stepPanel);
		//stepPanelY += editor.getHeight() + stapH;
		
		
		//sp.getElement().setScrollTop(sp.getElement().getScrollHeight());
		editor.requestFocus();
		scrollToBottom();
		
	}
	
	public void addBordjesStap()
	{
		System.out.println("addBordjesStap");
		
		String select = viewers.get(viewers.size() - 1).getSelectionString();
		if(select == null || select.length() == 0)
			return;
		
		if(editor == null)
			addStep("$f" + latest_answer_viewer.toString() + "@");
		
		editor.clearAll();
		editor.insert(select);
		editor.insert("=");
		editor.paint();
		editor.requestFocus();
		
	}
	
	
	public void resize()
	{
		if(editor != null)
		{	FlowPanel current = stepPanels.get(stepPanels.size() - 1);
			if(current.getParent() == contentPanel) // FIXME why? 
				contentPanel.setWidgetTopHeight(current, stepPanelY, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
			if(feedbackPanel.isAttached())
			{
				contentPanel.setWidgetTopHeight(feedbackPanel, stepPanelY + editor.getHeight(), Style.Unit.PX, feedbackPanelHeight, Style.Unit.PX);
			}
			//stepPanelY = current.getAbsoluteTop() - contentPanel.getAbsoluteTop() + editor.getHeight() + stapH;
			//System.out.println("resize: stepPanelY = " + stepPanelY);
		}
		scrollToBottom();
	}

	public void copyStep()
	{
		if ( correct != Boolean.TRUE)
		{
			String currentTekst = "";
			if(editor == null)
			{	if(latest_answer_viewer != null)
					voegRegelToe("$f" + latest_answer_viewer.toString() + "@");
				else
					voegRegelToe("$f@");
			}
			if (stapNr > 0)
			{
				editor.getMainRegel().deleteAll();
				currentTekst = viewers.get(stapNr - 1).toString();
				if (hasPrefix)
					currentTekst = removePrefix(currentTekst);
				currentTekst = removeIsTeken(currentTekst);
				editor.insert(currentTekst);
				editor.requestFocus();
			}
			tb.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		}

	}
	
	public void downStep()
	{
		if ( correct != Boolean.TRUE)
		{
			System.out.println("downStep 1");
			if(stapOk)
			{	System.out.println("downStep2");
				if(editor == null)
					voegRegelToe("$f" + latest_answer_viewer.toString() + "@");
				else
					voegRegelToe("$f" + editor.toString() + "@");
			}
			
		}

	}

	public void backStep()
	{
		nagekeken = false;
		correct = false;
		FlowPanel current = stepPanels.get(stapNr);
		if (stapNr > 0)
		{
			if(viewers.size() == stapNr + 1)
			{	current.remove(viewers.get(viewers.size() - 1).getAsPanel());
				viewers.remove(stapNr);
				
			}
			else
			{	current.remove(editor.getAsPanel());
			}
			stepPanels.remove(stapNr);
			current = stepPanels.get(stapNr - 1);
			stepPanelY -= stapH + viewers.get(viewers.size() - 1).getHeight();
			latest_answer_viewer = viewers.get(viewers.size() - 1);
			if(pijlVak != null && pijlVak.isAttached())
			{	contentPanel.remove(pijlVak);
				if(pijlVak.geefOperator().equals("sub"))
					substitutie = null;
				pijlVakken.remove(pijlVakken.size() - 1);
				if(pijlVakken.size() > 0)
					pijlVak = pijlVakken.get(pijlVakken.size() - 1);
				else
					pijlVak = null;
			}
			if(feedbackPanel.isAttached())
				contentPanel.remove(feedbackPanel);
			if(stapNr > 1 || !hasStartString)
			{	String currentTekst = viewers.get(viewers.size() - 1).toString();
				if (hasPrefix)
					currentTekst = removePrefix(currentTekst);
				if(!linStrategieVersie && !bordjesMethode)
				{	current.remove(viewers.get(viewers.size() - 1).getAsPanel());
					editor = addNewEditor(current);
					editor.insert(currentTekst);
					viewers.remove(viewers.size() - 1);
					if(viewers.size() > 0)
						latest_answer_viewer = viewers.get(viewers.size() - 1);
				}
			}
			else
				editor = null;
			stapNr--;
			stapOk = false;
			
			if (stapNr == 0 || stapNr == 1 && hasStartString)
				tb.getElement().getStyle().setVisibility(Visibility.HIDDEN);
			if ((mode == 0 || mode == 1) && (stapNr > 0 || !hasStartString) && !linStrategieVersie && !bordjesMethode)
			{
				//if (tips && diagnose)
				//	kijkNaIdeas();
				//else
				kijkNa(true, true);
				editor.requestFocus();
			}
			else
			{
				stapOk = true;
				if(bordjesMethode)
					viewers.get(viewers.size() - 1).setSelectable(true);
				
//				if (mode == 0 || mode == 1)
//					zetGoedFout(GEEN, -1);
//				else
//				{
//					zetGoedFout(GEEN, stapNr + 1);
//					zetGoedFoutStap(GEEN, stapNr);
//				}

			}
//			if (linStrategieVersie)
//			{
//				zetGoedFout(GEEN, -1);
//				formuleVak.setEditable(false);
//				fout = false;
//			}
			
//			if ("MW".equals(WiskOpdr.deployVariant) || "GR".equals(WiskOpdr.deployVariant))
//				remove(mwFeedbackPanel);
//			else
//				remove(feedbackTekst);
//			if (stapNr > 1 || !hasStartString)
//				formuleVakken[stapNr - 1].setEditable(true);
//			if (bordjesMethode && stapNr > 0)
//				formuleVakken[stapNr - 1].setSelectable(true);

		}
		
		
		
		
		
		
//		nagekeken = false;
//		System.out.println("backStep");
//		//FlowPanel current = stepPanels.get(stapNr);
//
//		if (correct != Boolean.TRUE && editor != null && editor.toString().length() > 0)
//		{
//			current.remove(editor.getAsPanel());
//			editor = addNewEditor(current);
//		}
//		else if (correct == Boolean.TRUE)
//		{
//			contentPanel.remove(feedbackPanel);
//			current.remove(viewers.get(viewers.size() - 1).getAsPanel());
////			current.getElement().getStyle().setBackgroundColor(hlColor.toString());
//			viewers.remove(viewers.get(viewers.size() - 1));
//			String currentTekst = latest_answer_viewer.toString();
//			if (hasPrefix)
//				currentTekst = removePrefix(currentTekst);
//			editor = addNewEditor(current);
//			editor.insert(currentTekst);
//			if (stapNr > 1 || stapNr > 0 && !hasStartString)
//				latest_answer_viewer = viewers.get(viewers.size() - 1);
//			correct = null;
//			comRoot.setChanged();
//		}
//		else if (stapNr > 0 || stapNr == 0 && !hasStartString)
//		{
//			contentPanel.remove(feedbackPanel);
//			//alleen maar nodig om in editor te stoppen. Dat hoeft niet altijd.
//			String currentTekst = latest_answer_viewer.toString();
//			currentTekst = removeIsTeken(currentTekst);
//			if (hasPrefix)
//				currentTekst = removePrefix(currentTekst);
//			contentPanel.remove(current);
//			if(editor != null)
//				stepPanelY = stepPanelY - stapH - latest_answer_viewer.getHeight();
//			else
//				stepPanelY = stepPanelY - stapH - viewers.get(viewers.size() - 2).getHeight();
//			stepPanels.remove(stepPanels.size() - 1);
//			
//			if(pijlVak != null && pijlVak.isAttached())
//			{	contentPanel.remove(pijlVak);
//				pijlVakken.remove(pijlVakken.size() - 1);
//			}
//			
//			current = stepPanels.get(stepPanels.size() - 1);
//			if(pijlVakken.size() > 0)
//				pijlVak = pijlVakken.get(pijlVakken.size() - 1);
////			current.getElement().getStyle().setBackgroundColor(hlColor.toString());
//
//			current.remove(viewers.get(viewers.size() - 1).getAsPanel());
//			viewers.remove(viewers.get(viewers.size() - 1));
//			if (stapNr > 1 || stapNr > 0 && !hasStartString || linStrategieVersie)
//				latest_answer_viewer = viewers.get(viewers.size() - 1);
//
//			if(!linStrategieVersie && stapNr > 0)
//			{
//				editor = addNewEditor(current);
//				editor.insert(currentTekst);
//			}
//			
//			stapNr--;
//			if (stapNr == 0)
//			{
//				tb.getElement().getStyle().setVisibility(Visibility.HIDDEN);
//			}
//
//		}
	}
	
//	public void zetStapOk(int goedHalfFout)
//	{
//		if(goedHalfFout == 0)
//		{
//			stapOk = false;
//			if (!pijl)
//				stapOk = true;
//		}
//		else if(goedHalfFout == 1)
//			stapOk = true;
//		else 
//			stapOk = false;
//	}

	public void setFeedback(String feedback)
	{
		hasFeedback = !"".equals(feedback.trim());
		feedbackPanel.clear();
		feedbackPanel.getElement().setInnerHTML(feedback);
		feedbackPanel.getElement().getStyle().setPadding(10, Unit.PX);
		feedbackPanelHeight = 34;
		//TODO: hoogte feedbackPanel bepalen.
	}

	public void setAndAddFeedback(String feedback)
	{
		hasFeedback = !"".equals(feedback.trim());
		feedbackPanel.clear();
		contentPanel.remove(feedbackPanel);
		feedbackPanel.getElement().setInnerHTML(feedback);
		feedbackPanel.getElement().getStyle().setPadding(10, Unit.PX);
		feedbackPanelHeight= 34;
		if (hasFeedback)
		{	contentPanel.add(feedbackPanel);
			contentPanel.setWidgetLeftRight(feedbackPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			int height = 23;
			if(editor != null && editor.getHeight() > 23)
				height = editor.getHeight();
			else if(latest_answer_viewer != null && latest_answer_viewer.getHeight() > 23)
				height = latest_answer_viewer.getHeight();
			contentPanel.setWidgetTopHeight(feedbackPanel, stepPanelY + height, Style.Unit.PX, feedbackPanelHeight, Style.Unit.PX); 
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
		tb.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		FlowPanel stepPanel = new FlowPanel();
		layoutStepPanel(stepPanel);
//		highLight(stepPanel, true);
		//stapNr++;
		FormuleViewer fv = viewers.get(viewers.size() - 1);

		if (hasFeedback)
		{	feedbackPanel.removeFromParent();
		//feedbackpanel staat nu een beetje in de weg; maar evne weglaten dus.
			//contentPanel.add(feedbackPanel);
			//contentPanel.setWidgetLeftRight(feedbackPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			//contentPanel.setWidgetTopHeight(feedbackPanel, stepPanelY + fv.getHeight(), Style.Unit.PX, feedbackPanelHeight, Style.Unit.PX); 
		}

		
		
		if(linStrategieVersie)
		{	FormuleViewer viewer = new FormuleViewer(fv.toString());
			viewer.setFont(defaultfont);
			viewer.getMainRegel().getCanvas().getElement().getStyle().setMarginLeft(23, Unit.PX);
			viewers.add(viewer);
			stepPanel.add(viewer.getAsPanel());
		}
		else
		{
			if (hasPrefix)
				stepPanel.add(prefixViewer);
			editor = addNewEditor(stepPanel);
			
		}
		stepPanelY += fv.getHeight() + stapH;
		contentPanel.add(stepPanel);
		contentPanel.setWidgetLeftRight(stepPanel, 5, Style.Unit.PX, 5, Style.Unit.PX); 
		if(linStrategieVersie)
			contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, viewers.get(viewers.size()-1).getHeight(), Style.Unit.PX);
		else
		{	contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
			editor.requestFocus();
		}
		stepPanels.add(stepPanel);
		//stepPanelY += editor.getHeight() + stapH;
		
		
		//sp.getElement().setScrollTop(sp.getElement().getScrollHeight());
		scrollToBottom();
		
		
		
//		FlowPanel current = stepPanels.get(stepPanels.size() - 1);
//		if(editor != null)
//		{	current.add(editor.getAsPanel());
//			editor.requestFocus();
//		}
		
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
		if (s.charAt(s.length() - 1) == '=' || s.charAt(s.length() - 1) == '\u2248')
		{
			int isIndex = s.length() - 1;
			s = s.substring(0, isIndex);
		}
		return s;
	}

	public FormuleEditorWithAnswer addNewEditor(Panel p)
	{
		FormuleEditorWithAnswer editor = editorInstance();
		editor.zetMode(mode);
		editor.setFormuleToolBijFocus(true);
		//editor.zetSubstitutie(substitutie);
		if (!hasPrefix)
			editor.getAsPanel().getElement().getStyle().setMarginLeft(13, Unit.PX);
		editor.getAsPanel().getElement().getStyle().setMarginTop(5, Unit.PX);
		editor.setFont(defaultfont);
		//editor.setFont(editor.getDefaultFont());
		TouchPanel tp = (TouchPanel) editor.getAsPanel();
		tp.getElement().getStyle().setProperty("display", "inline-block");
		editor.setCurrent(0, 0);
		//editor.requestFocus();
		if (hasPrefix)
			p.add(prefixViewer);
		p.add(tp);
		addFormulePanelListeners(tp, editor);
		return editor;
	}

	
	FormuleEditorWithAnswer editorInstance() {
		return new FormuleEditorWithAnswer(h, isVergelijkingVak, this, randomVarNamen, randomVarWaarden);
	}

	public void layoutStepPanel(Widget w)
	{
		w.getElement().getStyle().setWidth(breedte - 5, Unit.PX);
		w.getElement().getStyle().setFloat(Float.LEFT);
		w.getElement().getStyle().setProperty("clear", "both");
		w.getElement().getStyle().setProperty("display", "block");
		//heeft dit zin?
//		w.getElement().getStyle().setBackgroundColor(hlColor.toString());
		//w.getElement().getStyle().setBackgroundColor("yellow");
	}

	
//	public void highLight(Widget w, boolean b)
//	{
//		w.getElement().getStyle().setBackgroundColor(b ? hlColor.toString() : bgColor.toString());
//		//w.getElement().getStyle().setBackgroundColor(b ? "yellow" : "red");
//	}

	private void addButtonHandler(final TouchButton tb)
	{
		tb.addTouchHandler(new TouchHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				backStep();
			}

			@Override
			public void onTouchMove(TouchMoveEvent event)
			{
			}

			@Override
			public void onTouchEnd(TouchEndEvent event)
			{
			}

			@Override
			public void onTouchCanceled(TouchCancelEvent event)
			{
			}
		});
	}
	
	private void addAbcButtonHandler(final TouchButton tb)
	{	tb.addTouchHandler(new TouchHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("abc");
			}
			@Override
			public void onTouchMove(TouchMoveEvent event){}
			
			@Override
			public void onTouchEnd(TouchEndEvent event){}
			
			@Override
			public void onTouchCanceled(TouchCancelEvent event){}
		});
	}
	
	private void addSubButtonHandler(final TouchButton tb)
	{	tb.addTouchHandler(new TouchHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	if(substitutie == null)
				maakStap("sub");
			}
			@Override
			public void onTouchMove(TouchMoveEvent event){}
			
			@Override
			public void onTouchEnd(TouchEndEvent event){}
			
			@Override
			public void onTouchCanceled(TouchCancelEvent event){}
		});
	}
	
	private void addPlusButtonHandler(final TouchButton tb)
	{	tb.addTouchHandler(new TouchHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("+");
			}
			@Override
			public void onTouchMove(TouchMoveEvent event){}
			
			@Override
			public void onTouchEnd(TouchEndEvent event){}
			
			@Override
			public void onTouchCanceled(TouchCancelEvent event){}
		});
	}
	
	private void addMinButtonHandler(final TouchButton tb)
	{	tb.addTouchHandler(new TouchHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("-");
			}
			@Override
			public void onTouchMove(TouchMoveEvent event){}
			
			@Override
			public void onTouchEnd(TouchEndEvent event){}
			
			@Override
			public void onTouchCanceled(TouchCancelEvent event){}
		});
	}
	
	private void addMaalButtonHandler(final TouchButton tb)
	{	tb.addTouchHandler(new TouchHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("*");
			}
			@Override
			public void onTouchMove(TouchMoveEvent event){}
			
			@Override
			public void onTouchEnd(TouchEndEvent event){}
			
			@Override
			public void onTouchCanceled(TouchCancelEvent event){}
		});
	}
	
	private void addDeelButtonHandler(final TouchButton tb)
	{	tb.addTouchHandler(new TouchHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap(":");
			}
			@Override
			public void onTouchMove(TouchMoveEvent event){}
			
			@Override
			public void onTouchEnd(TouchEndEvent event){}
			
			@Override
			public void onTouchCanceled(TouchCancelEvent event){}
		});
	}
	
	private void addHaakjesButtonHandler(final TouchButton tb)
	{	tb.addTouchHandler(new TouchHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("haakjes");
				maakBewerkingStap();
			}
			@Override
			public void onTouchMove(TouchMoveEvent event){}
			
			@Override
			public void onTouchEnd(TouchEndEvent event){}
			
			@Override
			public void onTouchCanceled(TouchCancelEvent event){}
		});
	}
	
	private void addHerleidButtonHandler(final TouchButton tb)
	{	tb.addTouchHandler(new TouchHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("herleid");
				maakBewerkingStap();
			}
			@Override
			public void onTouchMove(TouchMoveEvent event){}
			
			@Override
			public void onTouchEnd(TouchEndEvent event){}
			
			@Override
			public void onTouchCanceled(TouchCancelEvent event){}
		});
	}
	
	private void addOntbindButtonHandler(final TouchButton tb)
	{	tb.addTouchHandler(new TouchHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("ontbind");
				maakBewerkingStap();
			}
			@Override
			public void onTouchMove(TouchMoveEvent event){}
			
			@Override
			public void onTouchEnd(TouchEndEvent event){}
			
			@Override
			public void onTouchCanceled(TouchCancelEvent event){}
		});
	}
	
	private void addSplitsButtonHandler(final TouchButton tb)
	{	tb.addTouchHandler(new TouchHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("splits");
				maakBewerkingStap();
			}
			@Override
			public void onTouchMove(TouchMoveEvent event){}
			
			@Override
			public void onTouchEnd(TouchEndEvent event){}
			
			@Override
			public void onTouchCanceled(TouchCancelEvent event){}
		});
	}
	
	private void addWortelButtonHandler(final TouchButton tb)
	{	tb.addTouchHandler(new TouchHandler()
		{	@Override
			public void onTouchStart(TouchStartEvent event)
			{	maakStap("wortel");
				maakBewerkingStap();
			}
			@Override
			public void onTouchMove(TouchMoveEvent event){}
			
			@Override
			public void onTouchEnd(TouchEndEvent event){}
			
			@Override
			public void onTouchCanceled(TouchCancelEvent event){}
		});
	}
	
//	private void addFormuleButtonHandler(final TouchButton tb)
//	{
//		tb.addTouchHandler(new TouchHandler()
//		{
//			@Override
//			public void onTouchStart(TouchStartEvent event)
//			{	copyStep();
//				if(event.getSource() == abcKnop)
//				{	maakStap("abc");
//				}
//				else if(event.getSource() == subKnop)
//				{
//					if(substitutie == null)
//						maakStap("sub");
//				}
//				else if(event.getSource() == plusKnop)
//				{	maakStap("+");
//				}
//				else if(event.getSource() == minKnop)
//				{	maakStap("-");
//				}
//				else if(event.getSource() == maalKnop)
//				{	maakStap("*");
//				}
//				else if(event.getSource() == deelKnop)
//				{	maakStap(":");
//				}
//				else if(event.getSource() == haakjesKnop)
//				{	maakStap("haakjes");
//					maakBewerkingStap();
//				}
//				else if(event.getSource() == herleidKnop)
//				{	maakStap("herleid");
//					maakBewerkingStap();
//				}
//				else if(event.getSource() == ontbindKnop)
//				{	maakStap("ontbind");
//					maakBewerkingStap();
//				}
//				else if(event.getSource() == splitsKnop)
//				{	maakStap("splits");
//					maakBewerkingStap();
//				}
//				else if(event.getSource() == wortelBewerkKnop)
//				{	maakStap("wortel");
//					maakBewerkingStap();
//				}
//			}
//
//			@Override
//			public void onTouchMove(TouchMoveEvent event)
//			{
//			}
//
//			@Override
//			public void onTouchEnd(TouchEndEvent event)
//			{	
//			
//			}
//
//			@Override
//			public void onTouchCanceled(TouchCancelEvent event)
//			{
//			}
//		});
//	}

	private void addDownButtonHandler(final TouchButton tb)
	{
		tb.addTouchHandler(new TouchHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				downStep();
			}

			@Override
			public void onTouchMove(TouchMoveEvent event)
			{
			}

			@Override
			public void onTouchEnd(TouchEndEvent event)
			{
			}

			@Override
			public void onTouchCanceled(TouchCancelEvent event)
			{
			}
		});
	}
	
	private void addCopyButtonHandler(final TouchButton tb)
	{
		tb.addTouchHandler(new TouchHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				copyStep();
			}

			@Override
			public void onTouchMove(TouchMoveEvent event)
			{
			}

			@Override
			public void onTouchEnd(TouchEndEvent event)
			{
			}

			@Override
			public void onTouchCanceled(TouchCancelEvent event)
			{
			}
		});
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
				
//				if(editor.hasSelection()) Nee! Je selecteert eigenlijk altijd in een viewer!
//				{	String select = editor.getSelectionString();
//					if(select != null && select.length() > 0)
//					{
//						hier.
//					}
//				}
//				FormuleEditor ed = FormuleEditorWithSteps.this.editor;
//				if(ed != null && editor != ed && editor.hasSelection()) {
//					String select = editor.getSelectionString();
//					if(select != null && select.length()>0)
//					{	ed.clearAll();
//						ed.insert(select);
//						ed.insert("=");
//						//editor.clearSelection();
//						editor.paint();
//					}
//				}
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
		
		if(editor != null && mode != 2 && mode != 3)
			editor.kijkNa();

		stapNr = this.stapNr;
		formuleVakInhouden = new String[stapNr + 1];
		for (int i = 0; i < stapNr + 1; i++)
		{
			if (viewers.size() > i && viewers.get(i) != null)
				formuleVakInhouden[i] = "$f" + (viewers.get(i)).toString() + "@" ;
			else
				formuleVakInhouden[i] = "$f@";
		}
		
		pijlVakInhouden = new String[stapNr];
		pijlVakOperatoren = new String[stapNr];
		for (int i = 0; i < stapNr; i++) {
			pijlVakInhouden[i] = pijlVakken.get(i).geefExpressieString();
			pijlVakOperatoren[i] = pijlVakken.get(i).geefOperator();
		}
		if(editor != null)
			antwoordString = editor.toString();
		ingevuld = this.ingevuld;
		nagekeken = this.nagekeken;
		if (substitutie != null)
			substitutieString = "$f" + substitutie.toString() + "@";
		//terugzetten als gebruikersSubstitutiesVak gemaakt:
		//gebruikersSubStrings = gebruikersSubstitutiesVak.geefRegels();

		HashMap<String, Object> h = new HashMap<String, Object>();
		h.put("stapNr", new Integer(stapNr));
		h.put("formuleVakInhouden", formuleVakInhouden);
		h.put("antwoordString", antwoordString);
		h.put("pijlVakInhouden", pijlVakInhouden);
		h.put("pijlVakOperatoren", pijlVakOperatoren);
		h.put("ingevuld", new Boolean(ingevuld));
		h.put("nagekeken", new Boolean(nagekeken));
		h.put("substitutieString", substitutieString);
		h.put("gebruikersSubStrings", gebruikersSubStrings);
		
		return h;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		int stapNr = 0;
		String[] formuleVakInhouden = null;
		String[] pijlVakInhouden = null;
		String[] pijlVakOperatoren = null;
		boolean ingevuld = true;
		Boolean nagekeken = null;
		String substitutieString = "";
		String antwoordString = "";
		String[] gebruikersSubStrings = null;
		

		if (h.get("stapNr") != null)
			stapNr = ((Number) h.get("stapNr")).intValue();
		if (h.get("ingevuld") != null)
			ingevuld = (Boolean) h.get("ingevuld");
		if (h.get("nagekeken") != null)
			nagekeken = (Boolean) h.get("nagekeken");
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
			{	if(pijlVakInhouden[i].startsWith("$f"))
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
		//terugzetten als gebruikersSubstitutiesVak gemaakt:
		//gebruikersSubstitutiesVak.zetRegels(gebruikersSubStrings);
		
		if (!substitutieString.equals(""))
			substitutie = FormuleParser.geefExpressie(substitutieString);

		stepPanelY = 0;
		correct = nagekeken; //wordt hieronder nog aangepast als nodig.
		for (int i = 0; i < stapNr + 1; i++)
		{
			
			if (i == 0 && hasStartString)
			{	
				//if(!(linStrategieVersie || linOefenVersie || bordjesMethode) || stapNr > 0)
				//	zetPijlVakNeer(pijlVakOperatoren, pijlVakInhouden, i, viewers.get(i).getHeight()/2);
				if(i < stapNr)
				{	if(linStrategieVersie || linOefenVersie || bordjesMethode || !pijlVakOperatoren[i].equals(""))
					{	zetPijlVakNeer(pijlVakOperatoren, pijlVakInhouden, i, viewers.get(i).getHeight()/2);
					}
					i++;
				}
				else
					return;
			}

			FormuleViewer fv = new FormuleViewer(formuleVakInhouden.length > i?formuleVakInhouden[i]:"");
			fv.setFont(defaultfont);
			
			if(i < viewers.size())
			{	if(viewers.get(i).getAsPanel().isAttached())
					stepPanels.get(i).remove(viewers.get(i).getAsPanel());
				viewers.remove(i);
			}
			viewers.add(fv);
			Panel p = fv.getAsPanel();
			p.getElement().getStyle().setProperty("display", "inline");

			FlowPanel stepPanel = null;
			if (i == 0 || i == 1 && hasStartString && !(linStrategieVersie || linOefenVersie || bordjesMethode))
			{
				stepPanel = stepPanels.get(i);
				if(editor != null)
				{	stepPanel.remove(editor.getAsPanel());
					editor = null;
				}
				else if(viewers.size() > i)
					stepPanel.remove(viewers.get(i).getAsPanel());
				if (hasPrefix && (i < stapNr || nagekeken))
					stepPanel.remove(prefixViewer);
			}
			else
			{
				stepPanel = new FlowPanel();
				layoutStepPanel(stepPanel);
				stepPanels.add(stepPanel);
				if (hasPrefix && !nagekeken)
					stepPanel.add(prefixViewer);

			}

			stepPanel.removeFromParent();
			contentPanel.add(stepPanel);
			contentPanel.setWidgetLeftRight(stepPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			
			
			if (i < formuleVakInhouden.length && ("".equals(formuleVakInhouden[i]) || "$f@".equals(formuleVakInhouden[i])))
			{
				viewers.remove(fv);
				editor = addNewEditor(stepPanel);
				if(antwoordString.startsWith("$f") && antwoordString.endsWith("@"))
					antwoordString = antwoordString.substring(2, antwoordString.length() - 1);
				System.out.println("antwoordString = " + antwoordString);
				editor.insert(antwoordString);
//				highLight(stepPanel, true);
				if(viewers.size() > 0)
					stepPanelY += viewers.get(viewers.size() - 1).getHeight() + stapH;
				contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
				
				if(i < stapNr)
					zetPijlVakNeer(pijlVakOperatoren, pijlVakInhouden, i, stepPanelY + editor.getHeight()/2);
				
				
			}
			else
			{
				stepPanel.add(p);
//				highLight(stepPanel, false);
				if(viewers.size() > 1)
					stepPanelY += viewers.get(viewers.size() - 2).getHeight() + stapH;
				contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, fv.getHeight(), Style.Unit.PX);
				
				if(i < stapNr)
					zetPijlVakNeer(pijlVakOperatoren, pijlVakInhouden, i, stepPanelY + fv.getHeight()/2);
					
				if(bordjesMethode)
				{	if(viewers.size() > 1)
						freezeViewer(viewers.get(viewers.size() - 2));
					if(!nagekeken)
						addFormulePanelListeners((TouchPanel) p, fv); 
				}
				//stepPanelY += fv.getHeight() + stapH;
			}
			
			
//			stepPanel.removeFromParent();
//			contentPanel.add(stepPanel);
//			contentPanel.setWidgetLeftRight(stepPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
//			contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
//			stepPanelY += editor.getHeight() + stapH;

			if (viewers.size() > 0)
				latest_answer_viewer = viewers.get(viewers.size() - 1);
			
//			if(mode == 2 || mode == 3)
//				fv.getAsPanel().getElement().getStyle().setMarginLeft(23, Unit.PX);
			if(mode != 2 && mode != 3)
			{	if (i == stapNr && nagekeken)
				{	if((linStrategieVersie || bordjesMethode))
					{	fv.showResult(FormuleViewer.CORRECT);
						setAndAddFeedback(Text.rb.getString("feedbackTekst04"));
						//"De vergelijking is correct opgelost."
						stapOk = false;
					}
					else //doel: laatste antwoord nogmaals nakijken, om juiste feedback te genereren.
					{
						viewers.remove(fv);
						stepPanel.remove(p);
						editor = addNewEditor(stepPanel);
						String currentTekst = latest_answer_viewer.toString();
						if (hasPrefix)
							currentTekst = removePrefix(currentTekst);
						currentTekst = removeIsTeken(currentTekst);
						editor.insert(currentTekst);
						if(viewers.size() > 0)
							latest_answer_viewer = viewers.get(viewers.size() - 1);
						editor.kijkNa();
						maakNakijkenAf(false);
					}
					
				}
				else if(i == stapNr && editor != null)
				{
					if(editor.toString().equals("") && (i > 1 || (!hasStartString && i > 0)))
					{
						//stap terug doen en die nakijken, zodat de feedback goed kan worden bepaald. Alleen nodig bij oefenmodi.
						stepPanel.remove(editor);
						stapNr--;
						this.stapNr--;
						stepPanels.remove(stepPanels.size() - 1);
						stepPanel = stepPanels.get(stepPanels.size() - 1);
						stepPanel.remove(viewers.get(viewers.size() - 1).getAsPanel());
						stepPanelY -= stapH + viewers.get(viewers.size() - 1).getHeight();
						viewers.remove(viewers.size() - 1);
						if(pijlVakken.size() > 0)
						{	
							if(pijlVakken.get(pijlVakken.size() - 1).isAttached())
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
						editor.kijkNa();
					}
					else 
					{	editor.kijkNa();
						
					}
				}
				else if(i == stapNr && (bordjesMethode || linOefenVersie))
				{
					fv.showResult(FormuleViewer.ALMOSTCORRECT);
				}
				else if(i == stapNr)
				{
					//nu: editor = null. 
					viewers.remove(fv);
					stepPanel.remove(p);
					editor = addNewEditor(stepPanel);
					editor.insert(latest_answer_viewer.toString());
					editor.kijkNa();
					maakNakijkenAf(false);
				}
				else if (i == stapNr - 1 && !nagekeken && !(linStrategieVersie))
					fv.showResult(FormuleViewer.ALMOSTCORRECT);
				else
					fv.showResult(FormuleViewer.NONE);
				score = correct == Boolean.TRUE ? scoreMax : 0;
			}	
		}
		if(mode == 2 || mode == 3)
		{	if(nagekeken)
			{	
				int start = 0;
				if (hasStartString)
					start = 1;
				if(editor != null && editor.toString().equals(""))
				{	stepPanels.remove(stepPanels.size() - 1);
					stapNr--;
				}
				else if (editor != null)
				{	vervangEditorDoorViewer("$f" + editor.toString() + "@");
					editor = addNewEditor(stepPanels.get(stepPanels.size() - 1));
				}
				else
					editor = addNewEditor(stepPanels.get(stepPanels.size() - 1));
				for (int i = start; i < stapNr + 1; i++)
				{
					editor.clearAll();
					editor.insert(viewers.get(i).toString());
					editor.kijkNa();
					int goedHalfFout = editor.getGoedHalfFout();
					if(goedHalfFout == AntwoordVakChecker.GOED || goedHalfFout == AntwoordVakChecker.HALF || goedHalfFout == AntwoordVakChecker.DOOR)
						viewers.get(i).showResult(FormuleViewer.CORRECT);
					else 
						viewers.get(i).showResult(FormuleViewer.WRONG);
					
					//formuleVak = formuleVakken[i];
					//checkAntwoord();
					
					//Komt uit wiskOpdr; kijken of hier ook nog nodig.
					/*if (stepsForLinKwad && pijl && start > 0)
					{
						checkStap(i - 1, formuleVakken[i - 1], formuleVakken[i]);
						if (i == stapNr)
							kijkNa(i);
					}
					else    */
						//kijkNa(i);
				}
				if(editor != null)
				{
					int goedHalfFout = editor.getGoedHalfFout();
					if(goedHalfFout == AntwoordVakChecker.GOED)
					{
						editor.getAsPanel().removeFromParent();
						String antwoord = viewers.get(viewers.size() - 1).toString();
								
						if(antwoord.endsWith("="))
						{	antwoord = antwoord.substring(0, antwoord.length() - 1);
							viewers.get(viewers.size() - 1).getMainRegel().deleteAll();
							viewers.get(viewers.size() - 1).getMainRegel().insert(antwoord);
						}
					}
					else if(goedHalfFout != AntwoordVakChecker.GEEN)
					{	
						viewers.get(viewers.size() - 1).getAsPanel().removeFromParent();
						viewers.remove(viewers.size() - 1);
						if(editor.toString().endsWith("="))
						{	String antwoord = editor.toString().substring(0, editor.toString().length() - 1);
							
							editor.clearAll();
							editor.insert(antwoord);
							editor.setimg(antwoord);
						}
						correct = false;
						score = editor.getScore();
					}
				}
			}
			else
			{	for(int i = 0; i < viewers.size(); i++)
				{
					viewers.get(i).getAsPanel().getElement().getStyle().setMarginLeft(23, Unit.PX);
				}
			}
			
		}
//		if(editor != null)
//			stepPanelY -= editor.getHeight() + stapH;
//		else
//			stepPanelY -= latest_answer_viewer.getHeight() + stapH;
		

		if (stapNr > 1 || stapNr == 1 && !hasStartString)
			tb.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		
		if(editor != null)
			editor.setCurrentElementRepaint();
		scrollToBottom();
		
		
	}
	
	public void zetPijlVakNeer(String[] pijlVakOperatoren, String[] pijlVakInhouden, int i, int h)
	{
		if(pijlVakken.size() > i && pijlVakken.get(i) != null)
		{	if(pijlVakken.get(i).isAttached())
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
		if (pijlVakInhouden != null && pijlVakInhouden.length > i && pijlVakInhouden[i] != null)
			pijlVak.zetExpressie(pijlVakInhouden[i]);
			
		int breedte = pijlX;
			
		if (pijlVakOperatoren[i].equals("sub") || pijlVakOperatoren[i].equals("abc"))
			breedte = pijlX + 30;
		//if ("GR".equals(WiskOpdr.deployVariant) && pijlVakOperatoren != null && pijlVakOperatoren[i] != null && (pijlVakOperatoren[i].equals("sub") || pijlVakOperatoren[i].equals("abc")))
		//	pijlVak.setLocation(getSize().width - pijlX - 60, y);
		if(pijl)
		{	contentPanel.add(pijlVak);
			contentPanel.setWidgetRightWidth(pijlVak, 0, Style.Unit.PX, breedte, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(pijlVak, h, Style.Unit.PX, pijlVak.getHeight(), Style.Unit.PX);
		}
		
		pijlVakken.add(i, pijlVak);
		//pijlVakken.add(pijlVak);
		
	}
	
	public void kijkNa()
	{
		kijkNa(false, true);
	}
	
	
	
	public void kijkNa(boolean backStep, boolean show)
	{
		nagekeken = false;
		correct = false;
		score = 0;
		
		if((mode == 0 || mode == 1) && editor != null)
			editor.kijkNa(backStep, show);
		else if(mode == 2 || mode == 3)
		{
			if(editor != null && !editor.toString().equals(""))
				editor.kijkNa(backStep, show);
			else if(editor != null && (stapNr > 1 || stapNr == 1 && !hasStartString))//nog niets ingevuld op de regel --> vorige regel controleren
			{
				backStep();
				kijkNa(backStep, show);
				//hier
			}
			else if(stapNr > 1 || stapNr == 1 && !hasStartString)
			{
				FlowPanel stepPanel = stepPanels.get(stepPanels.size() - 1);
				FormuleViewer viewer = viewers.get(viewers.size() - 1);
				viewer.getAsPanel().removeFromParent();
				viewers.remove(viewer);
				
				editor = addNewEditor(stepPanel);
				String currentTekst = latest_answer_viewer.toString();
				if (hasPrefix)
					currentTekst = removePrefix(currentTekst);
				currentTekst = removeIsTeken(currentTekst);
				editor.insert(currentTekst);
				if(viewers.size() > 0)
					latest_answer_viewer = viewers.get(viewers.size() - 1);
				editor.kijkNa(backStep, show);

			}
			else return;
			
			if(editor.getGoedHalfFout() == AntwoordVakChecker.GOED)
			{
				nagekeken = true;
				correct = true;
				score = scoreMax;
			}
			
		}
	}
	
	public void maakNakijkenAf(boolean backStep)
	{
		int goedHalfFout = editor.getGoedHalfFout();
		if(goedHalfFout == AntwoordVakChecker.GEEN)
			ingevuld = false;
		else
			ingevuld = true;
		if(mode == 2 || mode ==3)
		{	if(goedHalfFout == AntwoordVakChecker.FOUT && editor.isSyntaxFout())
			{
				nagekeken = true;
				String feedback = editor.getFeedback();
				setAndAddFeedback(feedback);
			}
			else
			{	setFeedback("");
				feedbackPanel.removeFromParent();
			
				addStep("$f" + editor.toString() + "@");
			}
			return;
		}
		
		
		//stapOk juiste waarde geven.
		if(goedHalfFout == AntwoordVakChecker.GOED)
		{	stapOk = false;
			if (!pijl)
				stapOk = true;
		}
		else if(goedHalfFout == AntwoordVakChecker.DOOR)
		{	stapOk = true;
			score = editor.getScore();
			
		}
		else 
		{	stapOk = false;
			nagekeken = true;
			score = editor.getScore();
		}
		
		if(bordjesMethode)
		{	if(stapOk || goedHalfFout == AntwoordVakChecker.GOED)
			{	vervangEditorDoorViewer("$f" + editor.toString() + "@");
			}
			
		}
		else
		{
			String feedback = editor.getFeedback();
			if (goedHalfFout == AntwoordVakChecker.DOOR)
			{
				if(backStep)
					setAndAddFeedback(feedback);
				else
				{	setFeedback(feedback);
					addStep("$f" + editor.toString() + "@");
				}
			}
			else if (goedHalfFout == AntwoordVakChecker.HALF)
				setFeedback(feedback);
			else if (goedHalfFout == AntwoordVakChecker.GOED)
			{ 	setFeedback(feedback);
				lastStep("$f" + editor.toString() + "@");
			}
			else if (goedHalfFout == AntwoordVakChecker.FOUT)
				setAndAddFeedback(feedback);
			}
	}
	
	public boolean controleerStap()
	{
		if(!linOefenVersie)
			return true;
		if (stapNr == 0)
			return true;
		String op = pijlVakken.get(stapNr - 1).geefOperator();
		//pijlVakken[stapNr - 1].formuleVak.setEditable(false);
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
		return score;
	}

	@Override
	public Boolean isCorrect()
	{
		return correct;
	}
	
	public void zetNagekeken(boolean b) {
		if (ingevuld)
			nagekeken = b;
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
	
	/*
	private void maakStap()
	{
		//if (!stappen)
		//	return;

		if (!stapOk && mode != 2 && mode != 3)
		{
			nagekeken = false;
			maakStap("implicatie");
		}
		else if (stapOk || mode == 2 || mode == 3)
		{
			nagekeken = false;
			stapOk = false;
			pijlVakken[stapNr] = new PijlVak("implicatie");
			int y = stepPanelY + editor.getHeight()/2;
					//formuleVakken[stapNr].getLocation().y + formuleVakken[stapNr].getSize().height / 2;
			if (pijl)
			{	contentPanel.add(pijlVakken[stapNr]);
				contentPanel.setWidgetRightWidth(pijlVakken[stapNr], 0, Style.Unit.PX, pijlX, Style.Unit.PX);
				contentPanel.setWidgetTopHeight(pijlVakken[stapNr], y, Style.Unit.PX, pijlVakken[stapNr].getHeight(), Style.Unit.PX);
			}
			pijlVak = pijlVakken[stapNr];

			// formuleVakken[stapNr].setEditable(false);
			if ((mode != 2 && mode != 3) || (hasStartString && stapNr == 0))
				formuleVakken[stapNr].setEditable(false);

			formuleVakken[stapNr + 1] = new FormuleVak();
			formuleVakken[stapNr + 1].setFont(formuleVakFont);
			y = formuleVakken[stapNr].getLocation().y + formuleVakken[stapNr].getSize().height + stapH;
			int x = formuleVakX;
			formuleVakken[stapNr + 1].setLocation(x, y);
			formuleVakken[stapNr + 1].addActionListener(this);
			add(formuleVakken[stapNr + 1]);

			formuleVak = formuleVakken[stapNr + 1];
			formuleVakSimpel = formuleVakken[stapNr + 1];

			stapNr++;
			formuleVak.requestFocus();
		}
	}
	*/

	
	private void maakStap(String operator)
	{
		nagekeken = false;
		//tipGebruikt = false;
		//hulpGebruikt = false;
		if (stapNr == 0 && editor != null && editor.toString().equals("")) 
				//formuleVakken[stapNr] != null && formuleVakken[stapNr].toString().equals("$f@"))
			return;
		if (!stapOk && editor != null && !editor.toString().equals(""))
				//formuleVakken[stapNr] != null && !formuleVakken[stapNr].toString().equals("$f@"))
			return;
		else if(!stapOk && latest_answer_viewer.getResult() == FormuleViewer.CORRECT)
		{
			return;
			//Deze return zorgt dat je niet nog een stap kunt doen nadat je in de lineaire strategie-versie de juiste oplossing hebt gevonden.
		}
		
		if (operator.equals("implicatie"))
		{
			System.out.println("editor.toString in maakStap implicatie: " + editor.toString());
			addStep("$f" + editor.toString() + "@");
			
		}
		else
		{
			if (stapOk)
				stapNr++;
			stapOk = false;
			
			//Er staat al een pijl naar de volgende regel. Deze pijl moet worden vervangen door de nieuwe operatie.
			if(stepPanels.size() == pijlVakken.size())
			{
				if(pijlVak != null && pijlVak.isAttached())
				{	contentPanel.remove(pijlVak);
					pijlVakken.remove(pijlVakken.size() - 1);
				}
			}
			//Er staat al een pijl naar de volgende regel en daar staat ook al een vak klaar. De pijl en het bijbehorende vak moeten worden veranderd.
			//Dit gebeurt bijvoorbeeld als er een gewone pijl staat, die wordt veranderd in een bewerkingspijl (plus, bijvoorbeeld).
			else if(stepPanels.size() > pijlVakken.size() && stapNr < stepPanels.size())
			{
				FlowPanel current = stepPanels.get(stepPanels.size() - 1);
				stepPanelY -= stapH + latest_answer_viewer.getHeight();
				if(editor == null)
				{	current.remove(viewers.get(viewers.size() - 1).getAsPanel());
					viewers.remove(viewers.size() - 1);
				}
				else
					current.remove(editor.getAsPanel());
				if (hasPrefix)
					current.remove(prefixViewer);
				stepPanels.remove(stepPanels.size() - 1);
				contentPanel.remove(pijlVak);
				pijlVakken.remove(pijlVakken.size() - 1);
			}
			//Als er een nieuwe pijl wordt bijgemaakt en de editor nog in de laatste regel stond, dan moet die editor worden veranderd in een viewer.
			else if(stepPanels.size() > pijlVakken.size() && stapNr >= stepPanels.size() && editor != null)
			{
				System.out.println("editor to String in maakStap: " + editor.toString());
				vervangEditorDoorViewer("$f"+ editor.toString() + "@");
				
			}
			
//			if(pijlVak != null && pijlVak.isAttached())
//			{	contentPanel.remove(pijlVak);
//				pijlVakken.remove(pijlVakken.size() - 1);
//			}
			
				
			pijlVak = new PijlVak(operator, this, false);
			
			int y = stepPanelY + latest_answer_viewer.getHeight()/2;
			if(pijl)
			{	contentPanel.add(pijlVak);
				contentPanel.setWidgetRightWidth(pijlVak, 0, Style.Unit.PX, pijlX, Style.Unit.PX);
				contentPanel.setWidgetTopHeight(pijlVak, y, Style.Unit.PX, pijlVak.getHeight(), Style.Unit.PX);
				if(operator.equals("abc") || operator.equals("sub"))
					contentPanel.setWidgetRightWidth(pijlVak, 0, Style.Unit.PX, pijlX + 30, Style.Unit.PX);
			}
			
			//pijlVakken[stapNr - 1].setLocation(getSize().width - pijlX, y);
//			if (operator.equals("abc") || operator.equals("sub"))
//				pijlVakken[stapNr - 1].setLocation(getSize().width - pijlX - 30, y);
//			if ("GR".equals(WiskOpdr.deployVariant) && (operator.equals("abc") || operator.equals("sub")))
//				pijlVakken[stapNr - 1].setLocation(getSize().width - pijlX - 60, y);
			pijlVak.paintComponent();
			pijlVakken.add(pijlVak);
			
			
			pijlVak.getEditor().requestFocus();
			scrollToBottom();
		}
	}
	
	public void vervangEditorDoorViewer(String antwoord)
	{
		if(editor == null)
			return;
		FlowPanel current = stepPanels.get(stepPanels.size() - 1);
		current.remove(editor.getAsPanel());
		editor = null;
		if(hasPrefix)
			current.remove(prefixViewer);
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
		//System.out.println(" useranswer3: "+ useranswer);
		fv.setFont(defaultfont);
		fv.showResult(fv.ALMOSTCORRECT);
		VergelijkingMeerv verg = FormuleParser.parseVergelijking("$f" + fv.toString() + "@");
		if(bordjesMethode && verg.isEindOplossing(verg.geefVergelijkingVar()))
		{	fv.showResult(fv.CORRECT);
			setAndAddFeedback(Text.rb.getString("feedbackTekst04"));
			//"De vergelijking is correct opgelost."
			stapOk = false;
			nagekeken = true;
			correct = true;
			score = scoreMax;
			comRoot.setChanged(false);
		}
		if(mode == 2 || mode == 3)
			fv.showResult(fv.NONE);
		if (latest_answer_viewer != null && !(hasStartString && stapNr == 1))
			latest_answer_viewer.showResult(fv.NONE);
		latest_answer_viewer = fv;
		viewers.add(fv);
		Panel p = fv.getAsPanel();
		p.getElement().getStyle().setProperty("display", "inline");
		current.add(p);
		if(bordjesMethode)
		{	if(viewers.size() > 1)
				freezeViewer(viewers.get(viewers.size() - 2));
			if(!verg.isEindOplossing(verg.geefVergelijkingVar()))
				addFormulePanelListeners((TouchPanel) p, fv); 
		}
	}

	
	public void maakBewerkingStap()
	{
		//if (fout)
		//	return;
		String operator = pijlVak.geefOperator();
		
		Expressie en = FormuleParser.geefExpressie("$f" + pijlVak.geefExpressieString() + "@");
		//VergelijkingMeerv verg = formuleVakken[stapNr - 1].geefVergelijking();
		
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
		
		//if(stepPanels.get(stapNr) != null)
		//	contentPanel.remove(stapNr);
		//if (formuleVakken[stapNr] != null)
		//	remove(formuleVakken[stapNr]);
		FlowPanel stepPanel = new FlowPanel();
		layoutStepPanel(stepPanel);
		stepPanels.add(stepPanel);
		
		
		if(linOefenVersie)
		{
			if (hasPrefix && !nagekeken)
				stepPanel.add(prefixViewer);
			editor = addNewEditor(stepPanel);
			stepPanelY += stapH + latest_answer_viewer.getHeight();
			contentPanel.add(stepPanel);
			contentPanel.setWidgetLeftRight(stepPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, editor.getHeight(), Style.Unit.PX);
			//if (!verg.toString().equals(vergNieuw.toString()) || linStrategieVersie)
				editor.requestFocus();
			
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
			//System.out.println(" useranswer3: "+ useranswer);
			fv.setFont(defaultfont);
			//fv.showResult(fv.ALMOSTCORRECT);
			
			if (latest_answer_viewer != null && !(hasStartString && stapNr == 1))
				latest_answer_viewer.showResult(fv.NONE);
			stepPanelY += stapH + latest_answer_viewer.getHeight();
			if(vergNieuw != null)
				latest_answer_viewer = fv;
			
			if(vergNieuw != null && vergNieuw.isEindOplossing(vergNieuw.geefVergelijkingVar()))
			{	fv.showResult(fv.CORRECT);
				setAndAddFeedback(Text.rb.getString("feedbackTekst04"));
				//"De vergelijking is correct opgelost."
				stapOk = false;
				nagekeken = true;
				correct = true;
				score = scoreMax;
				comRoot.setChanged(false);
			}
			else if(vergNieuw != null)
			{	fv.showResult(fv.NONE);
				stapOk = true;
			}
			else
			{
				fv.showResult(fv.NONE);
				stapOk = false;
			}
			viewers.add(fv);
			Panel p = fv.getAsPanel();
			p.getElement().getStyle().setProperty("display", "inline");
			stepPanel.add(p);
			if(bordjesMethode)
				addFormulePanelListeners((TouchPanel) p, fv); 
			
			contentPanel.add(stepPanel);
			contentPanel.setWidgetLeftRight(stepPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);
			contentPanel.setWidgetTopHeight(stepPanel, stepPanelY, Style.Unit.PX, fv.getHeight(), Style.Unit.PX);
			//stapNr++;
			
			if(!bordjesMethode && !linStrategieVersie)
			{	
				stepPanel.remove(p);
				viewers.remove(fv);
				addStep("$f" + fv.toString() + "@");
				stapOk = false;
			}
			scrollToBottom();
		}
		
		if (stapNr > 0 || stapNr == 0 && !hasStartString)
			tb.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		
		
//		formuleVakken[stapNr] = new FormuleVak();
//		formuleVakken[stapNr].setFont(formuleVakFont);
//
//		y = formuleVakken[stapNr - 1].getLocation().y + formuleVakken[stapNr - 1].getSize().height + stapH;
//		x = formuleVakX;
//		formuleVakken[stapNr].setLocation(x, y);
//		formuleVakken[stapNr].addActionListener(this);
//		add(formuleVakken[stapNr]);
//
//		formuleVak = formuleVakken[stapNr];
		
//		{
//			if (!linOefenVersie)
//			{
//				//formuleVak.vulVak("$f" + vergNieuw.toString() + "@");
//				//formuleVak.finish();
//				editor.insert(vergNieuw.toString());
//				editor.enter();
//			}
//			else
//				editor.requestFocus();
//			
//		}
		//editor.requestFocus();
		

		//formuleVak.requestFocus();

	}
	
	
	

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		mode = comRoot.getMode();
		if(editor != null) editor.zetMode(mode); // FIXME why null? after init?
	}

	@Override
	public Widget asWidget()
	{
		return facade.wrap(getAsPanel());
	}
	
	@Override
	public int getAsHoogte() {
		return defaultfont.getAscent();
		
	}

	@Override
	public int getHeight() {
		return facade.wrapHeight(hoogte);
	}
	protected void setHeight(int h) {
		hoogte = h;
	}
	
	@Override
	public int getWidth() {
		return facade.wrapWidth(breedte);
	}
	
	//voor aanpassen breedte in geval van volledigeBreedte
	public void zetVolledigeBreedte(int breedte)
	{
		if(volledigeBreedte)
			this.breedte = breedte;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		viewers.get(0).setAsHoogte(ashoogte);
	}
	
	//public static void setDefaultFont()
	public void scrollToBottom()
	{
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
	        @Override
	        public void execute() {
	            sp.scrollToBottom();
	        }
		});
	}
	
}
