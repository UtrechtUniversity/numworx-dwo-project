package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.vectomatic.dom.svg.OMSVGPathElement;
import org.vectomatic.dom.svg.OMSVGPathSegList;
import org.vectomatic.dom.svg.OMSVGRectElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.text.Text;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.CorrectieFacade;
import nl.uu.fi.dwo.mobile.client.sco.DWOLogger;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.SVGButton;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.Review;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;

public class AntwoordKeuzeVak implements InteractionStub, FacetAware, CBookEventListener {
	
	private final ActivityComponent activity;

	//public static Text_nl rb = new Text_nl();
	
	public static final String ACTION_CORRECT = "action.correct";
	public static final String ACTION_FALSE = "action.false";
	public static final String ACTION_FALSE2 = "action.false_2";
	
	//tbv antwoordvak hypothesetoetsen
	public static final String TEXT_FEEDBACK = "text.feedback";
	public static final String TEXT_CHOICE = "text.choice";
	public static final String INT_INDEX = "int.index";


	private static final CBookEvent EVENT_CORRECT = new CBookEvent(ACTION_CORRECT); 
	private static final CBookEvent EVENT_FALSE = new CBookEvent(ACTION_FALSE); 
	private static final CBookEvent EVENT_FALSE2 = new CBookEvent(ACTION_FALSE2); 

	static final String holderId = "dockholder";
	private HashMap<String, Object> launchState; 
	String[] randomVarNamen = null;
	HashMap<String,Number> randomVarWaarden = null;
	OpdrNavIF comRoot;
	
	private int borderWidth = (Integer)DWOplayer.templateConstants.answerboxCombo("border-width");
	private String borderColorString = (String)DWOplayer.templateConstants.answerboxCombo("border-color");
	private int borderRadius = (Integer)DWOplayer.templateConstants.answerboxCombo("border-radius");
	
	private LayoutPanel basisPanel;
	int breedte = 110;
	int hoogte = 24; 
	int ashoogte = hoogte /2;
	
	//private Canvas uitklapPijlCanvas;
	private SVGButton uitklapPijlKnop;
	private Context2d gIm;
	
	TekstVak huidigeKeuzeVak;
	TekstVak[] keuzeOptieVakken;
	PopupPanel popupBox;
	boolean isShowing = false;
	
	private int mode;
	
	private int selectedIndex = 0;
	    
    private boolean ingevuld;
    private boolean nagekeken;
	private boolean isVeranderdNaNakijken = false;
	private boolean editable = true;
    
    private boolean correct;
    private boolean fout;
    
    private int attemptsCount;
	private Vector attempts;
	
	ArrayList<Object> kiesObjects;
	private String antwoordString;
	private String[] keuzeMogelijkheden;
	private ObjectMap[] answerModels;
	private boolean hasFeedback, checkExternal;

	private int goedHalfFout;
	private int puntenFeedback;
	private String feedback;
	Label feedbackLabel;
	PopupPanel feedbackPanel;
	TekstVak feedbackTekst;
	Canvas feedbackSluitKnop;
	Context2d gImFeedback;
	LayoutPanel checkPanel;
	LayoutPanel popupPanel;
	
	private boolean gelijkwaardig;
	private boolean volledigeBreedte = false;
	
	
	private int score;
    private int errorCount;
    private int scoreMax=10;
    private int foutStraf = 2;
    private boolean changed = false;
    
	static int GOED = 1;
	static int FOUT = 0;
	static int HALF = 2;
	static int GEEN = 3;
	
	Image goedKrulImage, foutKruisImage, goedKrulHalfImage;
	
	
	private boolean logOption;
	private String logID;
	
	private boolean[][] logObjectives;
	
	private boolean check;
	private boolean teltMee;
	private int hoogtePopup;
	private Logging logging;
	
	private boolean verbergFeedback = false;
	private CorrectieFacade correctie;
	
	
	public AntwoordKeuzeVak(ActivityComponent activity, HashMap<String, Object> h, String[] randomVarNamen, HashMap<String,Number> randomVarWaarden, int volleBreedte)
	{
		this.activity = activity;
		if (h != null && h.containsKey("breedte"))
			breedte = ((Number) h.get("breedte")).intValue();
		if (h != null && h.containsKey("hoogte"))
			hoogte = ((Number) h.get("hoogte")).intValue();
		if(h != null && h.containsKey("volledigeBreedte"))
			volledigeBreedte =((Boolean) h.get("volledigeBreedte"));
		if (h != null && h.containsKey("interactiePanelLaunchState"))
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		if(volledigeBreedte)
			breedte = volleBreedte;
		
		init(breedte, hoogte, launchState, randomVarWaarden);
		
		initialize(h, randomVarNamen, randomVarWaarden);
	}

	@Override
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		breedte = width;
		hoogte = height;
		ObjectMap map = JSONUtilities.wrapMap(launchData);
		if (map != null)
		{
			if(map.containsKey("keuzeMogelijkheden") )
				keuzeMogelijkheden = map.getStringArray("keuzeMogelijkheden");
			if(map.containsKey("antwoordString"))
				antwoordString = map.getString("antwoordString");
			if(map.containsKey("scoreMax")) 
				scoreMax = map.getInt("scoreMax");
			if(map.containsKey("answerModels") )
			{	ObjectList answerModelsList =map.getObjectList("answerModels");
				answerModels = new ObjectMap[answerModelsList.size()];
				for(int i = 0; i < answerModelsList.size(); i++)
					answerModels[i] = answerModelsList.getObjectMap(i);
			}
			if(map.containsKey("hasFeedback") )
				hasFeedback = map.getBoolean("hasFeedback");
			if(map.containsKey("logObjectives"))
			{	ObjectList logObjectivesList = ( map.getObjectList("logObjectives") );
				logObjectives = new boolean[logObjectivesList.size()][];
				for(int i = 0; i < logObjectivesList.size(); i++)
				{	logObjectives[i] = logObjectivesList.getBooleanArray(i);
				}
			}
			String logIDLabel = "";
			if(map.containsKey("logIDLabel"))
				logIDLabel = map.getString("logIDLabel");
		    if(map.containsKey("check")) 
				check = map.getBoolean("check");
			if(map.containsKey("teltMee")) 
				teltMee = map.getBoolean("teltMee");
			if(map.containsKey("checkExternal"))
				checkExternal = map.getBoolean("checkExternal");
			if(map.containsKey("logOption")) 
		    	logOption = map.getBoolean("logOption");
			if(map.containsKey("logID") && logOption) 
				logID = map.getString("logID");
			String[] smObjectives = JSONUtilities.toStringArray(launchData.get("smObjectives"));
			if (smObjectives != null && smObjectives.length > 0)
				logOption = true;
			if (logOption  )
		    {	
		    	DWOLogger dwologger = new DWOLogger();
		    	dwologger.setMaxScore(scoreMax);
		    	dwologger.setLogID(logID);
		    	dwologger.setClassName("fi.wiskopdr.AntwoordKeuzeVak/"+keuzeMogelijkheden.length);
		    	if(map.containsKey("logIDLabel"))
					dwologger.setLogIDLabel(map.getString("logIDLabel"));
		    	dwologger.setLogObjectives(logObjectives);
		    	dwologger.setSMObjectives(smObjectives);
		    	dwologger.setLogIDLabel(logIDLabel);
		    	dwologger.setTeltMee(teltMee);
		    	logging = dwologger;
		    }
			
		}
		
		
	}
	
	private void initialize(HashMap<String, Object> h, String[] randomVarNamen, HashMap<String,Number> randomVarWaarden)
	{
		attempts = new Vector();
		
		basisPanel = new LayoutPanel();
		//basisPanel.setStylePrimaryName("antwoordkeuzevak");
		
		ashoogte = hoogte / 2 + 5+borderWidth;
		basisPanel.setPixelSize(breedte,  hoogte+borderWidth);
		popupBox = new PopupPanel(true);
		popupPanel = new LayoutPanel();
		popupBox.add(popupPanel);
		popupBox.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		popupBox.getElement().getStyle().setBorderColor(CssColor.make(150, 150, 150).toString());
		popupBox.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
		popupBox.getElement().getStyle().setPadding(0, Style.Unit.PX);
		popupBox.getElement().getStyle().setProperty("boxShadow", "1px 1px 5px 3px #A6A6A6");
		
		huidigeKeuzeVak = maakKeuzeVak();
		huidigeKeuzeVak.setStyleName(DWOplayer.templateCss().answerboxCombo());
		huidigeKeuzeVak.getElement().getStyle().setBackgroundColor(CssColor.make(255, 255, 255).toString());
//		huidigeKeuzeVak.getElement().getStyle().setBorderColor(CssColor.make(150, 150, 150).toString());
//		huidigeKeuzeVak.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
//		huidigeKeuzeVak.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		huidigeKeuzeVak.setMarges(0,5);
		//huidigeKeuzeVak.setCentering(false, true);
		
		huidigeKeuzeVak.setPixelSize(breedte - 42, hoogte-borderWidth);
		basisPanel.add(huidigeKeuzeVak);
		basisPanel.setWidgetLeftRight(huidigeKeuzeVak, 0, Style.Unit.PX, 20, Style.Unit.PX);
		basisPanel.setWidgetTopBottom(huidigeKeuzeVak, 0, Style.Unit.PX, 0, Style.Unit.PX);
		kiesObjects = new ArrayList<Object> ();
		kiesObjects.add(Text.constants.keuzeVakKiesLabel());
		huidigeKeuzeVak.setObjects(kiesObjects);
		
		
		huidigeKeuzeVak.addDomHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				if(!editable) return;
				
				int top = basisPanel.getAbsoluteTop() + basisPanel.getOffsetHeight();
			    int topMax = activity.parameters().getWindowHeight() - hoogtePopup;
			    top = Math.min(top,topMax);
			    popupBox.setPopupPosition(basisPanel.getAbsoluteLeft(), top);
				if(isShowing)
				{	popupBox.hide();
					isShowing = false;
				}
				else
				{	popupBox.show();
					isShowing = true;
				}
			}
		}, ClickEvent.getType());
		
		huidigeKeuzeVak.addDomHandler(new MouseOverHandler(){
			public void onMouseOver(MouseOverEvent e)
			{	if(!isShowing && popupBox.isShowing())
				{	isShowing = true;
					
				}
			}
		}, MouseOverEvent.getType());
		
		huidigeKeuzeVak.addDomHandler(new MouseOutHandler(){
			public void onMouseOut(MouseOutEvent e)
			{	isShowing = false;
				
			}
		}, MouseOutEvent.getType());
	
		
		//TODO: Noordhoff-onderscheid maken (ook in plaatsing, alleen in Noordhoff in knop?)
		
		goedKrulImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
		goedKrulHalfImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_geel().getSafeUri());
		foutKruisImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri());
		
		feedbackPanel = new PopupPanel(true);
		feedbackPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		feedbackPanel.getElement().getStyle().setBorderColor("black");
		feedbackPanel.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
		feedbackPanel.getElement().getStyle().setPadding(2, Style.Unit.PX);
		feedbackPanel.getElement().getStyle().setBackgroundColor("#FFFFDD");
		
		feedbackTekst = new TekstVak();
		feedbackTekst.setSize(200, 50);
		feedbackTekst.setFontSize(XMLView.getDefaultFontSize());
		feedbackTekst.setFontName(XMLView.getDefaultFontName());
		feedbackTekst.setColor(CssColor.make("black"));
		feedbackTekst.setCentering(false, true);
		feedbackTekst.setPasHoogteBreedteAan(true, false);
		feedbackTekst.setTekstVakBreedte(190);
		feedbackPanel.add(feedbackTekst);
		
		feedbackSluitKnop = Canvas.createIfSupported();
		gImFeedback = feedbackSluitKnop.getContext2d();
		
		feedbackSluitKnop.setWidth(10 + "px");
		feedbackSluitKnop.setHeight(10 + "px");
		feedbackSluitKnop.setCoordinateSpaceWidth(10);
		feedbackSluitKnop.setCoordinateSpaceHeight(10);
		
		CanvasGradient feedbackGradient = gImFeedback.createLinearGradient(0, 0, 10, 10);
		feedbackGradient.addColorStop(0, CssColor.make(242, 242, 242).toString());
		feedbackGradient.addColorStop(1, CssColor.make(221, 221, 221).toString());
		gImFeedback.setFillStyle(feedbackGradient);
		//gIm.setFillStyle(CssColor.make(245, 245, 245).toString());
		gImFeedback.fillRect(0, 0, 10, 10);
		gImFeedback.setStrokeStyle("black");
		gImFeedback.beginPath();
		gImFeedback.moveTo(1, 1);
		gImFeedback.lineTo(9, 9);
		gImFeedback.moveTo(1, 9);
		gImFeedback.lineTo(9, 1);
		gImFeedback.stroke();
		
		feedbackSluitKnop.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		feedbackSluitKnop.getElement().getStyle().setProperty("verticalAlign", "top");
		voegFeedbackSluitKnopToe();
		
		feedbackSluitKnop.addDomHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				feedbackPanel.hide();
			}
		}, ClickEvent.getType());
					
		feedbackLabel = new Label("?");
		feedbackLabel.getElement().getStyle().setFontSize(11, Style.Unit.PX);
		feedbackLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		feedbackLabel.getElement().getStyle().setPadding(0, Style.Unit.PX);
		feedbackLabel.getElement().getStyle().setMarginTop(0, Style.Unit.PX);
		feedbackLabel.getElement().getStyle().setMarginLeft(3, Style.Unit.PX);
		feedbackLabel.getElement().getStyle().setPaddingLeft(4, Style.Unit.PX);
		feedbackLabel.getElement().getStyle().setBackgroundColor(CssColor.make(230, 230, 230).toString());
		feedbackLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		feedbackLabel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
		feedbackLabel.setWidth(10 + "px");
		feedbackLabel.setVisible(false);
		
		feedbackLabel.addDomHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				feedbackPanel.setPopupPosition(asWidget().getAbsoluteLeft() + 10, asWidget().getAbsoluteTop() + asWidget().getOffsetHeight() + 10);
				feedbackPanel.show();
			}
		}, ClickEvent.getType());
		
		checkPanel = new LayoutPanel();
		checkPanel.add(goedKrulImage);
		checkPanel.add(foutKruisImage);
		checkPanel.add(goedKrulHalfImage);
		checkPanel.add(feedbackLabel);
		checkPanel.setWidgetRightWidth(goedKrulImage, 1, Style.Unit.PX, 15, Style.Unit.PX);
		checkPanel.setWidgetTopHeight(goedKrulImage, -3, Style.Unit.PX, 20, Style.Unit.PX);
		checkPanel.setWidgetRightWidth(goedKrulHalfImage, 1, Style.Unit.PX, 15, Style.Unit.PX);
		checkPanel.setWidgetTopHeight(goedKrulHalfImage, -3, Style.Unit.PX, 20, Style.Unit.PX);
		checkPanel.setWidgetRightWidth(foutKruisImage, 1, Style.Unit.PX, 15, Style.Unit.PX);
		checkPanel.setWidgetTopHeight(foutKruisImage, -3, Style.Unit.PX, 20, Style.Unit.PX);
		checkPanel.setWidgetRightWidth(feedbackLabel, 5, Style.Unit.PX, 15, Style.Unit.PX);
		checkPanel.setWidgetBottomHeight(feedbackLabel, 0, Style.Unit.PX, 10, Style.Unit.PX);
		
		
		//basisPanel.add(goedKrulImage);
		//basisPanel.add(foutKruisImage);
		//basisPanel.setWidgetLeftWidth(goedKrulImage, imWidth, Style.Unit.PX, 30, Style.Unit.PX);
		//basisPanel.setWidgetTopHeight(goedKrulImage, 0, Style.Unit.PX, imHeight + 5, Style.Unit.PX);
		//basisPanel.setWidgetLeftWidth(foutKruisImage, imWidth, Style.Unit.PX, 30, Style.Unit.PX);
		//basisPanel.setWidgetTopHeight(foutKruisImage, 0, Style.Unit.PX, imHeight + 5, Style.Unit.PX);
		//basisPanel.setWidgetRightWidth(goedKrulImage, 1, Style.Unit.PX, 15, Style.Unit.PX);
		//basisPanel.setWidgetTopHeight(goedKrulImage, 6, Style.Unit.PX, 15, Style.Unit.PX);
		//basisPanel.setWidgetRightWidth(foutKruisImage, 2, Style.Unit.PX, 15, Style.Unit.PX);
		//basisPanel.setWidgetTopHeight(foutKruisImage, 5, Style.Unit.PX, 15, Style.Unit.PX);
		goedKrulImage.setVisible(false);
		goedKrulHalfImage.setVisible(false);
		foutKruisImage.setVisible(false);
		basisPanel.add(checkPanel);
		basisPanel.setWidgetRightWidth(checkPanel, 0, Style.Unit.PX, 16, Style.Unit.PX);
		basisPanel.setWidgetTopBottom(checkPanel, 0, Style.Unit.PX, 0, Style.Unit.PX);
		
				
		int aantalKeuzes = 0;
		int hoogtePanels = 0;
		TekstBuffer tb = new TekstBuffer(activity, randomVarNamen, randomVarWaarden, null);
		if (keuzeMogelijkheden != null)
			aantalKeuzes = keuzeMogelijkheden.length;
		keuzeOptieVakken = new TekstVak[aantalKeuzes + 1];
		keuzeOptieVakken[0] = maakKeuzeVak();
		keuzeOptieVakken[0].setPasHoogteBreedteAan(true, false);
		keuzeOptieVakken[0].setObjects(kiesObjects);
		popupPanel.add(keuzeOptieVakken[0]);
		keuzeOptieVakken[0].resize();
		//popupPanel.setWidgetLeftWidth(keuzeOptieVakken[0], 0, Style.Unit.PX, breedte - 23, Style.Unit.PX);
		popupPanel.setWidgetLeftRight(keuzeOptieVakken[0], 0, Style.Unit.PX, 0, Style.Unit.PX);
		popupPanel.setWidgetTopHeight(keuzeOptieVakken[0], hoogtePanels, Style.Unit.PX, keuzeOptieVakken[0].getHeight(), Style.Unit.PX);
		//keuzeOptieVakken[0].getElement().getStyle().setBackgroundColor(CssColor.make(163, 184, 204).toString());
		keuzeOptieVakken[0].getElement().getStyle().setBackgroundColor(CssColor.make(255, 255, 255).toString());
		keuzeOptieVakken[0].addDomHandler(new MouseOverHandler(){
			public void onMouseOver(MouseOverEvent event) {
				zetVakAangewezen(0);
			}
		}, MouseOverEvent.getType());
//		keuzeOptieVakken[0].addDomHandler(new ClickHandler(){
//			public void onClick(ClickEvent event) {
//				zetSelectie(0);
//			}
//		}, ClickEvent.getType());
		
		FlowPanel[] keuzeOptiePanels = new FlowPanel[aantalKeuzes + 1];
		keuzeOptiePanels[0] = new FlowPanel();
		keuzeOptiePanels[0].addDomHandler(new MouseOverHandler(){
			public void onMouseOver(MouseOverEvent event) {
				zetVakAangewezen(0);
			}
		}, MouseOverEvent.getType());
		keuzeOptiePanels[0].addDomHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				zetSelectie(0);
			}
		}, ClickEvent.getType());
		popupPanel.add(keuzeOptiePanels[0]);
		//popupPanel.setWidgetLeftWidth(keuzeOptiePanels[0], 0, Style.Unit.PX, breedte - 23, Style.Unit.PX);
		popupPanel.setWidgetLeftRight(keuzeOptiePanels[0], 0, Style.Unit.PX, 0, Style.Unit.PX);
		popupPanel.setWidgetTopHeight(keuzeOptiePanels[0], hoogtePanels, Style.Unit.PX, keuzeOptieVakken[0].getHeight(), Style.Unit.PX);
		hoogtePanels += keuzeOptieVakken[0].getHeight();
		
		
		for (int i = 0; i < aantalKeuzes; i++)
		{
			keuzeOptieVakken[i + 1] = maakKeuzeVak();
			keuzeOptieVakken[i + 1].setPasHoogteBreedteAan(true, false);
			
			keuzeOptiePanels[i + 1] = new FlowPanel();
			
			try
			{
				keuzeMogelijkheden[i] = FormuleParser.randomizeTekstVakString(keuzeMogelijkheden[i], randomVarNamen, randomVarWaarden);
			}
			catch (Exception e)
			{	
			}
			ArrayList<Object> keuzeOptie = tb.convertTekst(keuzeMogelijkheden[i].trim(), null, false);
					
			keuzeOptieVakken[i + 1].setObjects(keuzeOptie);
			keuzeOptieVakken[i + 1].resize();
			popupPanel.add(keuzeOptieVakken[i + 1]);
			//popupPanel.setWidgetLeftWidth(keuzeOptieVakken[i + 1], 0, Style.Unit.PX, breedte - 23, Style.Unit.PX);
			popupPanel.setWidgetLeftRight(keuzeOptieVakken[i + 1], 0, Style.Unit.PX, 0, Style.Unit.PX);
			popupPanel.setWidgetTopHeight(keuzeOptieVakken[i + 1], hoogtePanels, Style.Unit.PX, keuzeOptieVakken[i + 1].getHeight(), Style.Unit.PX);
			popupPanel.add(keuzeOptiePanels[i + 1]);
			//popupPanel.setWidgetLeftWidth(keuzeOptiePanels[i + 1], 0, Style.Unit.PX, breedte - 23, Style.Unit.PX);
			popupPanel.setWidgetLeftRight(keuzeOptiePanels[i + 1], 0, Style.Unit.PX, 0, Style.Unit.PX);
			popupPanel.setWidgetTopHeight(keuzeOptiePanels[i + 1], hoogtePanels, Style.Unit.PX, keuzeOptieVakken[i + 1].getHeight(), Style.Unit.PX);
			
			hoogtePanels += keuzeOptieVakken[i + 1].getHeight();
			
			final int index = i + 1;
			keuzeOptiePanels[i + 1].addDomHandler(new MouseOverHandler(){
				public void onMouseOver(MouseOverEvent event) {
					zetVakAangewezen(index);
				}
			}, MouseOverEvent.getType());
			keuzeOptiePanels[i + 1].addDomHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
					event.stopPropagation();
					event.preventDefault();
					zetSelectie(index);
				}
			}, ClickEvent.getType());
			
		}
		popupPanel.setPixelSize(breedte - 23, hoogtePanels);
		this.hoogtePopup = hoogtePanels;
		
		try
		{
			antwoordString = FormuleParser.randomizeTekstVakString(antwoordString, randomVarNamen, randomVarWaarden);
		}
		catch (Exception e)
		{
		}
		
		uitklapPijlKnop = new SVGButton("") {
			public void draw() {
				float w = width;
				float h = height;
				float e = w / 24;
				OMSVGRectElement rect = doc.createSVGRectElement(e/2, e/2, width - e, height - e, borderRadius*2*e, borderRadius*2*e);
				rect.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, borderColorString);
				rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, borderColorString);
				rect.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_WIDTH_PROPERTY, "" + 2*borderWidth*e);
				svg.appendChild(rect);
				
				OMSVGPathElement pijlDown = doc.createSVGPathElement();
				OMSVGPathSegList segsPijlDown = pijlDown.getPathSegList();
				segsPijlDown.appendItem(pijlDown.createSVGPathSegMovetoAbs(8*e, h/2-1*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(16*e, h/2-1*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(12*e, h/2+3*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(8*e, h/2-1*e));
				segsPijlDown.appendItem(pijlDown.createSVGPathSegLinetoAbs(16*e, h/2-1*e));
				pijlDown.getStyle().setSVGProperty(SVGConstants.CSS_STROKE_PROPERTY, "white");
				pijlDown.getStyle().setSVGProperty(SVGConstants.CSS_FILL_PROPERTY, "white");
				svg.appendChild(pijlDown);
			}
			protected void setBorderActive(boolean b) {}
		};
		uitklapPijlKnop.setSize(20, hoogte+borderWidth);
		
		basisPanel.add(uitklapPijlKnop);
		basisPanel.setWidgetRightWidth(uitklapPijlKnop, 20, Style.Unit.PX, 20, Style.Unit.PX);
		basisPanel.setWidgetTopBottom(uitklapPijlKnop, 0, Style.Unit.PX, 0, Style.Unit.PX);

		
		uitklapPijlKnop.addButtonListener(uitklapPijlKnop.new DefaultButtonListener() {
			public void onClick(Object sender) {
				if (!editable) return;
				
				int top = basisPanel.getAbsoluteTop() + basisPanel.getOffsetHeight();
				int topMax = activity.parameters().getWindowHeight() - hoogtePopup;
				top = Math.min(top, topMax);
				popupBox.setPopupPosition(basisPanel.getAbsoluteLeft(), top);
				if(isShowing)
				{	popupBox.hide();
					isShowing = false;
				}
				else
				{	popupBox.show();
					isShowing = true;
				}
			}
		});
		
		
		
		
		
	}
	
	public void voegFeedbackSluitKnopToe()
	{
		feedbackTekst.add(feedbackSluitKnop);
		feedbackTekst.setWidgetRightWidth(feedbackSluitKnop, 0, Style.Unit.PX, 10, Style.Unit.PX);
		feedbackTekst.setWidgetTopHeight(feedbackSluitKnop, 0, Style.Unit.PX, 10, Style.Unit.PX);
	}
	
	public TekstVak maakKeuzeVak()
	{
		TekstVak vak = new TekstVak();
		vak.setSize(breedte - 23, hoogte);
		vak.setMarges(2, 5);
		vak.getElement().getStyle().setBackgroundColor(CssColor.make(239, 241, 243).toString());
		vak.setFontName(XMLView.getDefaultFontName());
		vak.setFontSize(XMLView.getDefaultFontSize());
		vak.setColor(CssColor.make("black"));
		vak.setCentering(false, true);
		return vak;
	}
	
	public void zetVakAangewezen(int index)
	{
		for(int i = 0; i < keuzeOptieVakken.length; i++)
		{
			keuzeOptieVakken[i].getElement().getStyle().setBackgroundColor(CssColor.make(239, 241, 243).toString());
		}
		//keuzeOptieVakken[index].getElement().getStyle().setBackgroundColor(CssColor.make(163, 184, 204).toString());
		keuzeOptieVakken[index].getElement().getStyle().setBackgroundColor(CssColor.make(255, 255, 255).toString());
	}
	
	public void zetSelectie(int index)
	{
		
		if (selectedIndex != index)
		{
			// er is een verandering
			resetimg();
			zetFeedbackZichtbaar(false);
			
			if (nagekeken)
				zetIsVeranderdNaNakijken(true);
		}

		selectedIndex = index;
		huidigeKeuzeVak.clear();
		
		ArrayList<Object> huidigeKeuze = new ArrayList<Object> ();
		huidigeKeuze.add(Text.constants.keuzeVakKiesLabel());
		
		TekstBuffer tb = new TekstBuffer(activity);
		if(index > 0)
			huidigeKeuze = tb.convertTekst(keuzeMogelijkheden[index - 1], null, false);
		
		huidigeKeuzeVak.setObjects(huidigeKeuze);
		huidigeKeuzeVak.getElement().getStyle().setBackgroundColor(CssColor.make(255, 255, 255).toString());
		
		
		popupBox.hide();
		isShowing = false;
		changed = true;
		
		if (!checkExternal &&(mode == OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN || Review.isReview(comRoot)))
		{
			kijkNa();
			attemptsCount++;
		} 
		else if(checkExternal) {
			zetGoedFout(GEEN);
		}
		
		
		//tbv antwoordvak hypothesetoetsen
		if (hasFeedback && index > 0)
		{
			ObjectMap answerModel = answerModels[index-1];
			Map<String,Object> map = new HashMap<String,Object>();
			try{
				String feedbackText = answerModel.getString("feedback");
				if(feedbackText != null)
					map.put("content", feedbackText);
				else
				{
					ObjectMap feedbackMap = answerModel.getObjectMap("feedback");
					map.put("content", feedbackMap);
				}
			}
			catch(Exception e)
			{
				System.out.println("Exception in CBookEvent feedback AntwoordKeuzeVak");
			}
			fireEvent(new CBookEvent(this, TEXT_FEEDBACK, map));
			if(verbergFeedback && index > 0)
				zetSelectie(0);
		}

	}
	
	
	public HashMap<String, Object> getState()
	{
		boolean ingevuld = false;
		boolean nagekeken = false;
		boolean isVeranderdNaNakijken = false;
		String antwoord = "";
		Vector attempts = new Vector();
		int attemptsCount = 0;
		int errorCount = 0;

		if(!checkExternal)
			kijkNa(false, false); // XXX mischien true hier?
		
		
		ingevuld = this.ingevuld;
		nagekeken = this.nagekeken;
		isVeranderdNaNakijken = this.isVeranderdNaNakijken;
		if(selectedIndex > 0)
			antwoord = keuzeMogelijkheden[selectedIndex - 1];
		else
			antwoord = Text.constants.keuzeVakKiesLabel();
		attempts = this.attempts;
		attemptsCount = this.attemptsCount;
		errorCount = this.errorCount;

		//if (!("MW".equals(WiskOpdr.deployVariant) || "GR".equals(WiskOpdr.deployVariant)))
//		if (logOption)
//		{
//			HashMap<String, Object> logMap = new HashMap<String, Object>();
//
//			String logString = antwoord;
//			if (selectedIndex == 0)
//				logString = "";
//
//			logMap.put("logAnswer", logString);
//			logMap.put("logScore", new Integer(score));
//			logMap.put("logMaxScore", new Integer(scoreMax));
//			logMap.put("logErrorCount", new Integer(errorCount));
//			logMap.put("logAttemptsCount", new Integer(attemptsCount));
//			logMap.put("logAttempts", attempts);
//
//			//WiskOpdr.setLog(logID, logMap);
//
//		}

		if(logging instanceof DWOLogger) {
			DWOLogger dwologger = (DWOLogger) logging;
			Map<String, Object> map = buildLogParameters();
			if (mode == OpdrNavIF.EINDTOETS && ingevuld && (!nagekeken || isVeranderdNaNakijken) ) {
				this.nagekeken = nagekeken = true;
				zetIsVeranderdNaNakijken(isVeranderdNaNakijken = false);
				dwologger.log(map);
			} else {
				dwologger.updateLog(map);
			}
		}
		
		
		HashMap<String, Object> h = new HashMap<String, Object>();
		h.put("ingevuld", new Boolean(ingevuld));
		h.put("nagekeken", new Boolean(nagekeken));
		h.put("editable", Boolean.valueOf(editable));
		h.put("isVeranderdNaNakijken", new Boolean(isVeranderdNaNakijken));
		h.put("antwoord", antwoord);
		h.put("attempts", attempts);
		h.put("attemptsCount", new Integer(attemptsCount));
		h.put("errorCount", new Integer(errorCount));

		if(correctie != null) correctie.correctie(h);
		return h;
	}
	
	public void setAttempt()
	{
		if(logOption) {
			Map<String, Object> log = buildLogParameters();
// TODO feedback
			logging.log(log);
		}
//		String goedFout = "";
//		
//		if (goedKrulImage.isVisible())
//			goedFout = "goed";
//		//if (goedKrulHalfImage.isVisible())
//		//	goedFout = "half";
//		if (foutKruisImage.isVisible())
//			goedFout = "fout";
//		String formule = "";
//		//String string = huidigeKeuzeVak.getOpdrachtObjects().toString();
//		String string = "";
//		if(selectedIndex > 0)
//			string = keuzeMogelijkheden[selectedIndex - 1];
//		
//		
//		//String string = (String) antwoordKV.getItemText(antwoordKV.getSelectedIndex());
//
//		/*
//		String fbTekst = "";
//		if (feedbackTekst.isVisible() && feedbackTekst.getParent() != null)
//			fbTekst = feedbackTekst.getText();
//			*/
//
//		String s = string;
//		s = s + "   ;   ";
//		s = s + "Regelnummer = ";
//		s = s + "   ;   ";
//		s = s + goedFout;
//		s = s + "   ;   ";
//		s = s + "score = " + score;
//		s = s + "   ;   ";
//		s = s + new Date().toString();
//		s = s + "   ;   ";
//		//s = s + fbTekst;
//
//		attempts.addElement(s);
//		//System.out.println(s);
	}

	private Map<String, Object> buildLogParameters() {
		Map<String, Object> log  = new HashMap<String, Object>();
		if(correct)
			log.put("success", Boolean.TRUE);
		if(fout)
			log.put("success", Boolean.FALSE);
		String formule = "";
		if(selectedIndex > 0) {
			formule = keuzeMogelijkheden[selectedIndex - 1];
		}
		log.put("response", formule.trim());
		log.put("score", Collections.singletonMap("raw", score));
		log.put("step", "");
		return log;
	}
	
	
	public void setState(HashMap<String, Object> h)
	{
		if( h == null) return; // setStateNull();
		boolean ingevuld = false;
		boolean nagekeken = false;
		boolean editable = true;
		boolean isVeranderdNaNakijken = false;
		String antwoord = "";
		Vector attempts = new Vector();
		int attemptsCount = 0;
		int errorCount = 0;
		AcceptsOneWidget cmd = p -> {
			basisPanel.add(p);
			basisPanel.setWidgetBottomHeight(p, 0, Unit.PX, 16, Unit.PX);
			basisPanel.setWidgetRightWidth(p, 0, Unit.PX, 16, Unit.PX);
		};
		CorrectieFacade.showReview(h, cmd, this, scoreMax);

		if (h.containsKey("ingevuld"))
			ingevuld = ((Boolean) h.get("ingevuld")).booleanValue();
		if (h.containsKey("nagekeken"))
			nagekeken = ((Boolean) h.get("nagekeken")).booleanValue();
		if (h.containsKey("editable"))
			editable = ((Boolean)h.get("editable")).booleanValue();
		if (h.containsKey("isVeranderdNaNakijken"))
			isVeranderdNaNakijken = ((Boolean) h.get("isVeranderdNaNakijken")).booleanValue();
		if (h.containsKey("antwoord"))
			antwoord = (String) h.get("antwoord");
		if (h.containsKey("attempts"))
		{
			attempts = toVector(h.get("attempts"));
			if(attempts == null) attempts = new Vector();
		}
		if (h.containsKey("attemptsCount"))
			attemptsCount = ((Number) h.get("attemptsCount")).intValue();
		if (h.containsKey("errorCount"))
			errorCount = ((Number) h.get("errorCount")).intValue();

		this.ingevuld = ingevuld;
		this.nagekeken = nagekeken;
		this.isVeranderdNaNakijken = isVeranderdNaNakijken;
		this.attempts = attempts;
		this.attemptsCount = attemptsCount;
		this.errorCount = errorCount;
		this.editable = editable;

		selectedIndex = 0;
		for(int i = 0; i < keuzeMogelijkheden.length; i++)
			if(keuzeMogelijkheden[i].equals(antwoord))
			{	selectedIndex = i + 1;
				break;
			}
		huidigeKeuzeVak.clear();
		
		ArrayList<Object> huidigeKeuze = new ArrayList<Object> ();
		huidigeKeuze.add(Text.constants.keuzeVakKiesLabel());
		
		TekstBuffer tb = new TekstBuffer(activity, randomVarNamen, randomVarWaarden, null);
		if(selectedIndex > 0)
			huidigeKeuze = tb.convertTekst(keuzeMogelijkheden[selectedIndex - 1], null, false);
		
		huidigeKeuzeVak.setObjects(huidigeKeuze);
		//antwoordKV.setSelectedIndex(index);
		//antwoordKV.setSelectedItem(antwoord);

		if (ingevuld && (mode == OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN 
				|| (nagekeken && !isVeranderdNaNakijken && (mode != OpdrNavIF.EINDTOETS || LessonMode.browse == comRoot.getLessonMode()))
				|| Review.isReview(comRoot)))
			kijkNa(true, true);
		
		basisPanel.setStyleDependentName("readonly", !editable);
		correctie = CorrectieFacade.get(h, this, getScoreMax(), comRoot, logging);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Vector toVector(Object object)
	{
		if (object == null || object instanceof Vector)
			return (Vector) object;
		if (object instanceof Collection)
		{
			return new Vector((Collection) object);
		}
		return null;
	}

	@Override
	public int getScore() {
		if (!teltMee)
			return 0;
		return score;
	}
	
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

	public int getScoreMax()
	{
		if (!teltMee)
			return 0;
		return scoreMax;
	}

	public Boolean isCorrect()
	{
		if (!teltMee)
			return Boolean.TRUE;
		if (!correct && !fout) return null;
		return correct;
	}

	public boolean isFout()
	{
		if (!teltMee)
			return false;
		return fout;
	}

	public void zetMode(int mode)
	{
		this.mode = mode;
	}
	
	private void fireEvent(CBookEvent event) 
	{
		activity.getEventBus().fireEventFromSource(event, this);
		comRoot.fireEvent(event);
	}

	@Override
	public void kijkNa() 
	{
		// reset isVeranderdNaNakijken
		zetIsVeranderdNaNakijken(false);
		
		kijkNa(true, false);
		setAttempt();
	}
	
	private void kijkNa(boolean show, boolean setState)
	{
//		System.out.println("AntwoordKeuzeVak.kijkNa(show=" + Boolean.toString(show) + ")");
		
		checkAntwoord(show);

		//ingevuld = antwoordKV.getSelectedIndex() > 0;
		ingevuld = selectedIndex > 0;

		correct = false;
		fout = true;
		score = 0;

		if (!ingevuld)
		{	if (show)
				zetGoedFout(GEEN);
			fout = false;
			return;
		}

		if (hasFeedback)
		{
			if (goedHalfFout == 0)
			{
				if (show)
					zetGoedFout(GOED);
				score = puntenFeedback;
				if (mode == OpdrNavIF.OEFENEN_STRAFPUNTEN)
					score = Math.max(0, puntenFeedback - errorCount * foutStraf);
				correct = true;
				fout = false;
			}
			else if (goedHalfFout == 1)
			{
				if (show)
					zetGoedFout(HALF);
				score = puntenFeedback;
				if (mode == OpdrNavIF.OEFENEN_STRAFPUNTEN)
					score = Math.max(0, puntenFeedback - errorCount * foutStraf);
				correct = false;
				fout = false;
			}
			else if (goedHalfFout == 2)
			{
				if (show)
					zetGoedFout(FOUT);
				score = puntenFeedback;
				if (mode == OpdrNavIF.OEFENEN_STRAFPUNTEN)
					score = Math.max(0, puntenFeedback - errorCount * foutStraf);
				correct = false;
				fout = true;
				verhoogErrorCount();
			}
		}
		else
		{	if (gelijkwaardig)
			{
				if (show)
				{	
					zetGoedFout(GOED);
				}
				correct = true;
				fout = false;
				score = scoreMax;
				if (mode == OpdrNavIF.OEFENEN_STRAFPUNTEN)
					score = Math.max(0, scoreMax - errorCount * foutStraf);
			}
			else
			{
				if (show)
					zetGoedFout(FOUT);
				correct = false;
				fout = true;
				verhoogErrorCount();
				score = 0;
			}
		}

		
		if (show && ingevuld && !setState)
			comRoot.setChanged(teltMee && fout);
		
		if (correct) 
			fireEvent(EVENT_CORRECT);
		if (fout && errorCount > 1) 
			fireEvent(EVENT_FALSE2);
		if (fout)
			fireEvent(EVENT_FALSE);
	}
	
	private void verhoogErrorCount()
	{
		if(changed)
			errorCount++;
		changed = false;
	}
	
	private void zetGoedFout(int uitslag)
	{
		if (!check)
		{	return;
		}
		
		goedKrulImage.setVisible(false);
		goedKrulHalfImage.setVisible(false);
		foutKruisImage.setVisible(false);
		feedbackLabel.setVisible(false);
		
		if (uitslag == GEEN)
			return;
		else if (uitslag == GOED)
			goedKrulImage.setVisible(true);
		else if (uitslag == FOUT)
			foutKruisImage.setVisible(true);
		else if (uitslag == HALF)
			goedKrulHalfImage.setVisible(true);
		if(hasFeedback && !feedback.trim().equals(""))
			feedbackLabel.setVisible(true);
	}
	
	public void checkAntwoord(boolean show)
	{
		if (hasFeedback)
		{
			int aantalAnswerModels = answerModels.length;
			for (int h = 0; h < aantalAnswerModels; h++)
			{
				setAnswerModel(h);
				gelijkwaardig = false;
				if(selectedIndex > 0)
					gelijkwaardig = antwoordString.trim().equals(keuzeMogelijkheden[selectedIndex - 1].trim());

				if (gelijkwaardig || h == aantalAnswerModels - 1)
				{
					if (!feedback.trim().equals("") && !verbergFeedback)
					{
						zetFeedback();
						//feedbackLabel.setVisible(show);
					}
					else
					{
						//feedbackButton.setVisible(false);
						//if (feedbackPanel.getParent() != null)
						//{
							//Container c = feedbackPanel.getParent();
							//c.remove(feedbackPanel);
							//c.repaint();
						//}

					}
					break;
				}

			}
		}
		else
		{
			gelijkwaardig = false;
			if(selectedIndex > 0)
				gelijkwaardig = antwoordString.trim().equals(keuzeMogelijkheden[selectedIndex - 1].trim());
			//System.out.println("+" + antwoordString);
			//System.out.println("+" + ((String) huidigeKeuzeVak.getOpdrachtObjects().get(0)));
		}
		//repaint();
	}
	
	public void setAnswerModel(int nr)
	{
		ObjectMap h = answerModels[nr];
		if (h == null)
			return;

		String antwoordString = "$f@";
		int puntenFeedback = 0;
		String feedback = "";
		int goedHalfFout = 0;

		if (h != null)
		{
			if (h.containsKey("antwoordString"))
				antwoordString = (String) h.getString("antwoordString");
			if (h.containsKey("puntenFeedback"))
				puntenFeedback = h.getInt("puntenFeedback");
			if (h.containsKey("feedback"))
				feedback = h.getString("feedback");
			if (h.containsKey("goedHalfFout"))
				goedHalfFout = h.getInt("goedHalfFout");

		}

		try
		{
			antwoordString = FormuleParser.randomizeTekstVakString(antwoordString, randomVarNamen, randomVarWaarden);
		}
		catch (Exception e)
		{
		}
		try
		{
			feedback = FormuleParser.randomizeTekstVakString(feedback, randomVarNamen, randomVarWaarden);
		}
		catch (Exception e)
		{
			feedback = "$f???@";
		}

		this.goedHalfFout = goedHalfFout;
		this.puntenFeedback = puntenFeedback;
		this.antwoordString = antwoordString;
		this.feedback = feedback;

		//zetJuisteAntwoord(antwoordString);

	}
	
	public void zetFeedback()
	{
		TekstBuffer b = new TekstBuffer(activity);
		try{
			feedback = FormuleParser.randomizeTekstVakString(feedback, randomVarNamen, randomVarWaarden);
		}
		catch(Exception e){}
		ArrayList<Object> feedbackList = b.convertTekst(feedback, null, false);
		feedbackTekst.clear();
		feedbackTekst.setObjects(feedbackList);
		voegFeedbackSluitKnopToe();
		feedbackTekst.resize();
		//feedbackLabel.setVisible(true);
	}

	@Override
	public void zetNagekeken(boolean b) {
		if (ingevuld)
			nagekeken = b;
	}

	private void zetIsVeranderdNaNakijken(boolean b)
	{
		this.isVeranderdNaNakijken = b;
	}
	
	/**
	 * Reset het goed/fout-plaatje en verberg de feedback.
	 */
	void resetimg() 
	{
		goedKrulImage.setVisible(false);
		goedKrulHalfImage.setVisible(false);
		foutKruisImage.setVisible(false);
		//zetFeedbackZichtbaar(false); waar dan weer op true?
		feedbackPanel.hide();
	}
	
	public void zetFeedbackZichtbaar(boolean b)
	{
		feedbackLabel.setVisible(b);
		if(b)
			checkPanel.getElement().getStyle().setCursor(Cursor.POINTER);
		else
			checkPanel.getElement().getStyle().setCursor(Cursor.DEFAULT);
	}

//	public TekstRegel findParentRegel()
//	{
//		Widget parent = asWidget();
//		while (parent != null && !(parent instanceof TekstRegel))
//		{
//			parent = parent.getParent();
//		}
//		return (TekstRegel) parent;
//		
//	}
	
	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		zetMode(comRoot.getMode());
		if(logging != null) logging.setCommunicationRoot(comRoot);
		if(comRoot.hasListeners("text.feedback")) // Alleen als er 'text' messages kunnen worden ontvangen)
			verbergFeedback = true;
		comRoot.addCBookEventListener("action.setNotEditable", this);
	}

	

	@Override
	public Widget asWidget() {
		return basisPanel;
	}

		public int getAsHoogte() {
		return ashoogte;
	}

	@Override
	public int getHeight() {
		return hoogte+borderWidth;
	}

	@Override
	public int getWidth() {
		return breedte;
	}
	
	public void zetVolledigeBreedte(int breedte)
	{	//niet meer nodig, want volledige breedte wordt al gezet in constructor.
		if(volledigeBreedte)
		{
			this.breedte = breedte;
			basisPanel.setPixelSize(breedte, hoogte);
			popupPanel.setPixelSize(breedte - 23, hoogtePopup);
			huidigeKeuzeVak.setPixelSize(breedte - 22, hoogte - 2);	
		}
	}

	
	@Override
	public void setAsHoogte(int ashoogte) {
		//this.ashoogte = ashoogte;
		
	}

	@Override
	public void getResponses(List<String> responses) {
		responses.add(Integer.toString(selectedIndex));
	}

	public int getSelectedIndex()
	{
		return selectedIndex;
	}
	
	public String getSelectedItem()
	{
		if(selectedIndex > 0)
			return keuzeMogelijkheden[selectedIndex - 1];
		else
			return "";
	}
	
	public String getLogIDLabel()
	{
		if(logging != null && logging instanceof DWOLogger)
			return ((DWOLogger) logging).getLogIDLabel();
		return "";
	}
	
	public String getAntwoordString()
	{
		return antwoordString;
	}
	
	public ObjectMap[] getAnswerModels()
	{
		return answerModels;
	}
	@Override
	public void acceptCBookEvent(CBookEvent event) {
		if("action.setNotEditable".equals(event.getCommand())) {
			editable = false;
			basisPanel.setStyleDependentName("readonly", !editable);
		}
	}

}
