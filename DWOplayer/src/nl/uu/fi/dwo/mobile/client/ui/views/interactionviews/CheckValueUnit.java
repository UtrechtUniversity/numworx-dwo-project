package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

//import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.CorrectieFacade;
import nl.uu.fi.dwo.mobile.client.sco.CorrectieReview;
import nl.uu.fi.dwo.mobile.client.sco.DWOLogger;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.SVGButton;
import nl.uu.fi.dwo.mobile.client.ui.SVGButton.ButtonListener;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.Review;
import nl.uu.fi.dwo.mobile.utils.StringUtils;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.RestartException;
import fi.wiskopdr.expressies.Algebra;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import fi.wiskopdr.text.Text;


public class CheckValueUnit implements InteractionStub, CBookEventListener {

	
	public static final String ACTION_CORRECT = "action.correct";
	public static final String ACTION_FALSE = "action.false";
	public static final String ACTION_FALSE2 = "action.false_2";

	private static final CBookEvent EVENT_CORRECT = new CBookEvent(ACTION_CORRECT); 
	private static final CBookEvent EVENT_FALSE = new CBookEvent(ACTION_FALSE); 
	private static final CBookEvent EVENT_FALSE2 = new CBookEvent(ACTION_FALSE2); 

	static final String holderId = "dockholder";
	OpdrNavIF comRoot;
	
	private HashMap<String, Object> launchState; 
	
	private LayoutPanel basisPanel;
	int breedte = 126;
	int hoogte = 26; 
	int ashoogte = hoogte / 2;
	
	private int mode;
    
    private boolean ingevuld;
    private boolean nagekeken;
	private boolean isVeranderdNaNakijken = false;
    
    private boolean correct;
    private boolean fout;
    private boolean editable = true;
    
    private int attemptsCount;
	private Vector attempts;
    
    private boolean checkSamen = false;
	private String formuleString = "$f@";
	private String[] formuleStrings = null;
    
	private int errorCount;
	private int foutStraf = 2;
    private int score;
    private int scoreMax=10;
    
	static int GOED = 1;
	static int FOUT = 0;
	static int HALF = 2;
	static int GEEN = 3;
	
	private SVGButton checkButton;
	private String knopImageString = "";
	private TekstVakPanel[] ipValueList;
	
	private int aantalValueObjects;
	
	private FlowPanel nakijkAchtergrond;
	private Image goedKrulImage, foutKruisImage;//goedKrulHalfImage
	
	private boolean logOption;
	private String logID;
	private Logging dwologger;
	
	private boolean[][] logObjectives;
	
	private boolean check = true;
	private boolean teltMee = true;
	
	private String answer = "";
	private boolean view = false;
	private CorrectieFacade correctie;
	private Widget widget;
	private final ActivityInterface activity;
	
	public CheckValueUnit(ActivityInterface activity, HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)//, TekstVakPanel[] ipValueList)
	{
		this.activity = activity;
//		if (h != null && h.get("breedte") != null)
//			breedte = ((Number) h.get("breedte")).intValue();
//		if (h != null && h.get("hoogte") != null)
//			hoogte = ((Number) h.get("hoogte")).intValue();
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		
		//this.parent = parent;
		
		init(breedte, hoogte, launchState, randomVarWaarden);
		this.ipValueList = ipValueList; // waar moet ipValueList vandaan komen??
		
		initialize(h, randomVarNamen, randomVarWaarden);
	}

	@Override
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		breedte = width;// - 30;
		hoogte = height;
		//this.randomVarWaarden = randomValues;

		if (launchData != null)
		{
			if(launchData.get("scoreMax") != null) 
				scoreMax = ((Number)launchData.get("scoreMax")).intValue();
		    if(launchData.get("logOption") != null) 
		    	logOption = ((Boolean)launchData.get("logOption")).booleanValue();
			if(logOption && launchData.get("logID") != null) 
				logID = (String)launchData.get("logID");
			if(launchData.get("check") != null) 
				check = ((Boolean)launchData.get("check")).booleanValue();
			if(launchData.get("teltMee") != null) 
				teltMee = ((Boolean)launchData.get("teltMee")).booleanValue();
			if(launchData.get("checkSamen") != null)
				checkSamen = ((Boolean) launchData.get("checkSamen")).booleanValue();
			if(launchData.get("aantalValueObjects") != null)
				aantalValueObjects = ((Number) launchData.get("aantalValueObjects")).intValue();
			if(launchData.get("formuleString") != null)
				formuleString = (String) launchData.get("formuleString");
			if(launchData.get("view") != null)
				view = ((Boolean) launchData.get("view")).booleanValue();
			
			if(launchData.get("formuleStrings") != null)
			{	formuleStrings = JSONUtilities.toStringArray(launchData.get("formuleStrings"));
				if("en".equals(LocaleInfo.getCurrentLocale().getLocaleName()))
				{	for(int i = 0; i < formuleStrings.length; i++)
						formuleStrings[i] = formuleStrings[i].replaceAll("of", "or");
				}
			}
			if(launchData.get("logObjectives") != null)
			{	List<Object> logObjectivesList = JSONUtilities.toArrayList( launchData.get("logObjectives") );
				logObjectives = new boolean[logObjectivesList.size()][];
				for(int i = 0; i < logObjectivesList.size(); i++)
				{	List<Object> list = JSONUtilities.toArrayList(logObjectivesList.get(i));
					logObjectives[i] = new boolean[list.size()];
					for(int j = 0; j < list.size(); j++)
						logObjectives[i][j] = ((Boolean)(list.get(j))).booleanValue() ;
				}
			}
			if(launchData.get("knopImageString") != null) 
				knopImageString = (String)launchData.get("knopImageString");
			String[] smObjectives = JSONUtilities.toStringArray(launchData.get("smObjectives"));
			if (smObjectives != null && smObjectives.length > 0)
				logOption = true;
			if(logOption) {
				dwologger = activity.logBuilder().setLogOption(logOption)
				.setLogID(logID)
				.setClassName("fi.wiskopdr.CheckValueUnitPanel")
				.setMaxScore(scoreMax)
				.setLogObjectives(logObjectives)
				.setSmObjectives(smObjectives)
				.setTeltMee(teltMee).build();
			}

		}
	}
	
	private void initialize(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		attempts = new Vector();
		
		basisPanel = new LayoutPanel();
		//basisPanel.setSize("" + breedte + "px", "" + hoogte + "px");
		
		int imWidth = breedte;
		int imHeight = hoogte;
		Image knopImage = null;
		if(knopImageString!=null && !"".equals(knopImageString))
       	{  	ImageView imageView = new ImageView(knopImageString,activity);
       		knopImage = imageView.getImage();
			if(knopImage != null)
			{
				imWidth = imageView.getWidth();
				imHeight = imageView.getHeight();
			}
       		if(imWidth <= 0) imWidth = breedte;
			if(imHeight <= 0) imHeight = 20;
			//checkButton.setSize(imWidth,imHeight);
			//zetMaat();
	    }
		if(knopImage != null)
		{	checkButton = new SVGButton(knopImage);
			checkButton.getElement().getStyle().setPadding(0, Style.Unit.PX);
			checkButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		}
		else
		{	String backgroundColorString = (String)DWOplayer.templateConstants.checkButton("background-color");
			String borderColorString = (String)DWOplayer.templateConstants.checkButton("border-color");
			String textColorString = (String)DWOplayer.templateConstants.checkButton("text-color");
			
			checkButton = new SVGButton(Text.constants.klaarKnopLabel()); 
			checkButton.setFontSize(12);			
			checkButton.setBackgroundColor(CssColor.make(backgroundColorString));
			checkButton.setBorderColor(CssColor.make(backgroundColorString));
			checkButton.setBorderColorActive(CssColor.make(borderColorString));
			checkButton.setTextColor(CssColor.make(textColorString));
			checkButton.setCenter(false);
			checkButton.setSize(breedte, hoogte);
		}
		breedte = imWidth;
		hoogte = imHeight + 5;
		ashoogte = hoogte / 2 + 7;
		basisPanel.setSize("" + breedte + "px", "" + hoogte + "px");
		basisPanel.add(checkButton);
		basisPanel.setWidgetLeftWidth(checkButton, 0, Style.Unit.PX, imWidth, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(checkButton, 5, Style.Unit.PX, imHeight, Style.Unit.PX);
		checkButton.addButtonListener(new ButtonListener(){
			@Override
			public void onClick(Object sender)
			{	//e.stopPropagation();
				kijkNa();
				//if(fout) errorCount++;
	        	attemptsCount++;
				setAttempt();
			}
		});
		
		nakijkAchtergrond = new FlowPanel();
		//if(knopImage != null)
			nakijkAchtergrond.getElement().getStyle().setBackgroundColor("white");
		nakijkAchtergrond.getElement().getStyle().setProperty("borderRadius", (10) + "px");
		nakijkAchtergrond.setVisible(false);
		basisPanel.add(nakijkAchtergrond);
		basisPanel.setWidgetRightWidth(nakijkAchtergrond, 3, Style.Unit.PX, 16, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(nakijkAchtergrond, 8, Style.Unit.PX, 16, Style.Unit.PX);
		
		
		//TODO: Noordhoff-onderscheid maken
				//goedKrulImage = new Image(FormuleHolder.FORMULE_BUNDLE.goedkrul_en().getSafeUri());
				//foutKruisImage = new Image(DWOplayer.DWO_BUNDLE.foutkruis().getSafeUri());
		goedKrulImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
		foutKruisImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri());
		
		basisPanel.add(goedKrulImage);
		basisPanel.add(foutKruisImage);
//		basisPanel.setWidgetLeftWidth(goedKrulImage, imWidth, Style.Unit.PX, 30, Style.Unit.PX);
//		basisPanel.setWidgetTopHeight(goedKrulImage, 0, Style.Unit.PX, imHeight + 5, Style.Unit.PX);
//		basisPanel.setWidgetLeftWidth(foutKruisImage, imWidth, Style.Unit.PX, 30, Style.Unit.PX);
//		basisPanel.setWidgetTopHeight(foutKruisImage, 0, Style.Unit.PX, imHeight + 5, Style.Unit.PX);
		basisPanel.setWidgetRightWidth(goedKrulImage, 2, Style.Unit.PX, 15, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(goedKrulImage, 7, Style.Unit.PX, 20, Style.Unit.PX);
		basisPanel.setWidgetRightWidth(foutKruisImage, 2, Style.Unit.PX, 15, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(foutKruisImage, 6, Style.Unit.PX, 20, Style.Unit.PX);
		goedKrulImage.setVisible(false);
		foutKruisImage.setVisible(false);
		
				
		for(int i=0 ; formuleStrings!=null && i<formuleStrings.length ; i++)
        {	try{
				formuleStrings[i] = FormuleParser.randomizeString(formuleStrings[i], randomVarNamen, randomVarWaarden);
	    	}
	    	catch(Exception e){	}
        }
		
	}
	
	public void zetWaardeObjecten(TekstVakPanel[] waardeObjecten)
	{
		ipValueList = waardeObjecten;
		for (int i = 0; i < ipValueList.length; i++)
		{
			if (ipValueList[i] != null)
			{
				ipValueList[i].getAsPanel().addDomHandler(
					new MouseDownHandler()
					{
						public void onMouseDown(MouseDownEvent e)
						{
							for (int i = 0; i < ipValueList.length; i++)
							{
								if (e.getSource() == ipValueList[i].getAsPanel())

								{
									if (nagekeken)
										zetIsVeranderdNaNakijken(true);

									nakijkAchtergrond.setVisible(false);
									goedKrulImage.setVisible(false);
									// goedKrulHalfImage.setVisible(false);
									foutKruisImage.setVisible(false);
									correct = false;
									score = 0;

									break;
								}
							}
						}
					}, MouseDownEvent.getType());
			}
		}
	}
	
	public int getAantalValueObjects()
	{
		return aantalValueObjects;
	}
	
	
	public void setState(HashMap<String, Object> h)
	{
		if(h == null) return; // setStateNull();
	    boolean ingevuld = false;
	    boolean nagekeken = false;
		boolean isVeranderdNaNakijken = false;
		boolean editable = true;
	    Vector attempts = new Vector();
	    int attemptsCount = 0;
		int errorCount = 0;
		CorrectieFacade.showReview(h, asOne(), this, getScoreMax(), activity);
      
	    if(h.get("ingevuld") != null) 
	    	ingevuld = ((Boolean)h.get("ingevuld")).booleanValue();
	    if(h.get("nagekeken") != null) 
	    	nagekeken = ((Boolean)h.get("nagekeken")).booleanValue();
		if (h.get("isVeranderdNaNakijken") != null)
			isVeranderdNaNakijken = ((Boolean) h.get("isVeranderdNaNakijken")).booleanValue();
		if (h.containsKey("editable"))
			editable = ((Boolean)h.get("editable")).booleanValue();
	    if(h.get("attempts") != null)
	    	attempts = new Vector(JSONUtilities.toArrayList(h.get("attempts")));
	    if(h.get("attemptsCount") != null) 
	    	attemptsCount = ((Number)h.get("attemptsCount")).intValue();
	    if(h.get("errorCount") != null) 
	    	errorCount = ((Number)h.get("errorCount")).intValue();
	    
        this.ingevuld = ingevuld;
        this.nagekeken = nagekeken;
		this.isVeranderdNaNakijken = isVeranderdNaNakijken;
        this.attempts = attempts;
        this.attemptsCount = attemptsCount;
	    this.errorCount = errorCount;
	    this.editable = editable;
	    basisPanel.setStyleDependentName("readonly", !editable);
        
        if(ingevuld && (mode == OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN || (nagekeken && !isVeranderdNaNakijken)||Review.isReview(comRoot))) 
        	kijkNa();
        correctie = CorrectieFacade.get(h, this, getScoreMax(),comRoot,dwologger, activity);
	}
	
	public HashMap<String, Object> getState()
	{   
	    boolean ingevuld = false;
	    boolean nagekeken = false;
		boolean isVeranderdNaNakijken = false;
	    Vector attempts = new Vector();
	    int attemptsCount = 0;
		int errorCount = 0;
		
	    ingevuld = this.ingevuld;
	    nagekeken = this.nagekeken;
		isVeranderdNaNakijken = this.isVeranderdNaNakijken;
	    
	    attempts = this.attempts;
	    attemptsCount = this.attemptsCount;
	    errorCount = this.errorCount;

	    //if(!("MW".equals(WiskOpdr.deployVariant) || "GR".equals(WiskOpdr.deployVariant))) 
	    kijkNa(false, false);
		if(dwologger != null) {
			Map<String, Object> map = buildLogParameters();
			dwologger.updateLog(map);
		}
        
	    HashMap<String, Object> h = new HashMap<String, Object>();
        h.put("ingevuld", new Boolean(ingevuld));
        h.put("nagekeken", new Boolean(nagekeken));
        h.put("editable", Boolean.valueOf(editable));
		h.put("isVeranderdNaNakijken", new Boolean(isVeranderdNaNakijken));
        h.put("attempts", attempts);
        h.put("attemptsCount", new Integer(attemptsCount));
        h.put("errorCount", new Integer(errorCount));
        
        if(correctie != null) correctie.correctie(h);
        return h;
	}
	
	public void setAttempt()
	{
		if(dwologger != null) {
			Map<String, Object> map = buildLogParameters();
			dwologger.log(map);
		}
		
//		String goedFout = "";
//		if(goedKrulImage.isVisible())
//			goedFout = "goed";
//		if(foutKruisImage.isVisible())
//			goedFout = "fout";
//		
//		String logString = "";
//		//String[] options = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","X","Y","Z"};
//		//for(int i=0 ; i<ipList.length ; i++)
//		// {   ipList[i] = ((TekstInteractiePanelVak)getParent()).zoekInteractiePanel(i+1);
//        //    if(((TekstVakPanel)ipList[i]).isIpSelected() && i<options.length) logString = logString + options[i];
//        //}
//		
//		String s = logString;
//		s = s + "   ;   ";
//		s = s + goedFout;
//		s = s + "   ;   ";
//		s = s + "score = " + score;
//		s = s + "   ;   ";
//		s = s + new Date().toString();
//		
//
//		attempts.addElement(s);
//		System.out.println(s);
	}

	private Map<String, Object> buildLogParameters() {
//		String logString = "";
//		String[] options = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","X","Y","Z"};
//		for(int i=0 ; i<ipList.length ; i++)
//	    {   ipList[i] = ((TekstInteractiePanelVak)getParent()).zoekInteractiePanel(i+1);
//            if(((TekstVakPanel)ipList[i]).isIpSelected() && i<options.length) logString = logString + options[i];
//        }
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("response", answer);
		map.put("score", Collections.singletonMap("raw", score));
		if(goedKrulImage.isVisible())
			map.put("success", Boolean.TRUE);
		if(foutKruisImage.isVisible())
			map.put("success", Boolean.FALSE);
		return map;
	}
	
	public void wis()
	{
		//wanneer wordt wis aangeroepen? ipValueList null maken en aantalvalueObjects op 0 zetten lijkt me niet slim..
		//ipValueList = null;
		//aantalValueObjects = 0;
		nakijkAchtergrond.setVisible(false);
		goedKrulImage.setVisible(false);
	    foutKruisImage.setVisible(false);
	   
	    correct = false;
	    score = 0;
	    nagekeken = false;
	    ingevuld = false;
	    errorCount = 0;
	    attemptsCount = 0;
	    attempts = new Vector();
	}
	
	public int getScore()
	{	if(!teltMee) return 0;
	    return score;
	}
	
	public int[][] getScoreObjectives()
	{	if(logObjectives==null)return null;
		int[][] scoreObjectives = new int[logObjectives.length][];
		for(int i =0; i<logObjectives.length; i++)
			scoreObjectives[i] = new int[logObjectives[i].length];
		for(int i=0 ; i<logObjectives.length ; i++)
			for(int j = 0; j<logObjectives[i].length; j++)
		{	if(logObjectives[i][j]) scoreObjectives[i][j] = score;
		}
		return scoreObjectives;
	}
	
	public int getScoreMax()
	{	if(!teltMee) return 0;
	    return scoreMax;
	}

	public Boolean isCorrect()
	{	if(!teltMee)return true;
		if(!correct && !fout) return null;
	    return correct;
	}
	
	public boolean isFout()
	{	if(!teltMee)return false;
	    return fout;
	}
	
	public void zetMode(int mode, LessonMode lessonMode)
    {   this.mode = mode;
    	checkButton.setVisible(mode==0 || mode==1 || lessonMode == LessonMode.review);
    }
	
	public void zetNagekeken(boolean b)
	{	if(ingevuld) nagekeken = b;
	}
	
	private void zetIsVeranderdNaNakijken(boolean b)
	{
		this.isVeranderdNaNakijken = b;
	}
	
    public void stop()
    {
        kijkNa();
        //if(ingevuld) produceAction("changed");
    }
    
    public void start(){}
    
    public void destroy(){}
    
    public void opnieuw()
    { 	score = 0;
		correct = false;
    }
    
	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		zetMode(comRoot.getMode(), comRoot.getLessonMode());
		if(dwologger != null)
			dwologger.setCommunicationRoot(comRoot);
		comRoot.addCBookEventListener("action.setNotEditable", this);
	}
	
	@Override
	public Widget asWidget() {
		if (widget == null) {
			widget = CorrectieReview.wrap(basisPanel, activity);
		}
		return widget;
	}
	
	private AcceptsOneWidget asOne() {
		if (widget instanceof AcceptsOneWidget) return (AcceptsOneWidget) widget ; else return null;
	}
    
    public void kijkNa()
    {
		// reset isVeranderdNaNakijken
		zetIsVeranderdNaNakijken(false);

    	kijkNa(true, true);
    }
    
    private void kijkNa(boolean show, boolean setState)
    {	
    	//Er wordt zonder show nagekeken vanuit getState. Als er dan al een checkimg staat, moet dit niet worden weggehaald. 
    	if (show)
    	{
    		nakijkAchtergrond.setVisible(false);
    		goedKrulImage.setVisible(false);
    		//goedKrulHalfImage.setVisible(false);
    		foutKruisImage.setVisible(false);
        }
    	
        boolean juist = true;
        ingevuld = false;
        answer = "";
        boolean changed = false;
    	for (int i = 0 ; i < aantalValueObjects ; i++)
        {   
    		if (ipValueList[i] != null && ipValueList[i].ipObjectIsChanged())
    			changed = true;
        }
    	correct = false;
        fout = true;
        score = 0;
        
        //ipValueList = new InteractiePanel[aantalValueObjects];
        //for(int i=0 ; i<ipValueList.length ; i++)
       // {   //ipValueList[i] = ((TekstInteractiePanelVak)getParent()).zoekInteractiePanel(i+1);
            //ipValueList[i].addActionListener(this);
        //}
        
        if (checkSamen)
        {
        	if (formuleStrings!=null)
        	{
        		VergelijkingMeerv[] v = new VergelijkingMeerv[formuleStrings.length];
        		for (int h=0 ; h<formuleStrings.length ; h++)
		        {
        			String formuleString = formuleStrings[h];
        			String locationStringTotal = null;
        			String[] locationStrings = null;
        			
        			
        			int indexSC = formuleStrings[h].indexOf(";");
        			if (indexSC>-1)
        			{
        				formuleString = formuleStrings[h].substring(0,indexSC) + "@";
        				locationStringTotal = formuleStrings[h].substring(indexSC+1, formuleStrings[h].length()-1);
        				locationStrings = StringUtils.split(locationStringTotal, ",");
        				
        			}
        				
        			boolean stapJuist = true;
        			v[h] = FormuleParser.parseVergelijking(formuleString);
        			for (int i=0 ; i<aantalValueObjects ; i++)
    		        {   
        				if (ipValueList[i] != null)
        				{
	    	        		Expressie e = ipValueList[i].geefObjectWaarde();
	    	        		if (e!=null) 
	    	        		{	
	    	        			ingevuld = true;
	    	        			v[h] = v[h].substitueer(e, "V?("+(i+1)+")");
	    	        		}
	    	        		else if (ipValueList[i].objectNullWaarde())
	    	        		{	
	    	        		}
	    	        		else 
	    	        		{	
	    	        			stapJuist = false;
	    	        			break;
	    	        		}
        				}
        				else
        				{
        					stapJuist = false;
    	        			break;
        				}
    		        }
        			
        			stapJuist = v[h].isWareBeweringNummeriek();
        			
        			/*
        			if(v[h].geefAantal()==1 && (v[h].geefVergelijking(0).geefVergTeken().equals(">") || v[h].geefVergelijking(0).geefVergTeken().equals("<")))
        			{	// een nog zwakke manier om ongelijkheden te checken als de expressies nummeriek zijn en bij een enkelvoudige vergelijking
        				Expressie expL = v[h].geefVergelijking(0).geefExpLinks();
        				Expressie expR = v[h].geefVergelijking(0).geefExpRechts();
        				if(expL.isWaarde() && expR.isWaarde() && v[h].geefVergelijking(0).geefVergTeken().equals("<"))
        					stapJuist = expL.geefWaarde() < expR.geefWaarde()-0.000000001;
        				else if(expL.isWaarde() && expR.isWaarde() && v[h].geefVergelijking(0).geefVergTeken().equals(">"))
        					stapJuist = expL.geefWaarde() > expR.geefWaarde()+0.000000001;
        			}
        			else stapJuist = v[h].isOplossing(new BasisExpressie(1.212131415),"q");
        			*/
        			juist = juist && stapJuist;
        			if (!juist && locationStrings==null) break;
        			
        			if (locationStrings!=null){
        				for (int i=0 ; i<locationStrings.length ; i++)
        				{
            				int location = Integer.parseInt(locationStrings[i].trim());
            				if (show)
            				{
            					if (ipValueList[location-1] != null)
            						(ipValueList[location-1]).zetGoedFout(stapJuist);
            				}
            			}
        			}
        			
		        }
        	} // formuleStrings != null
        	else
        	{
        		VergelijkingMeerv v = FormuleParser.parseVergelijking(formuleString);
	        	for (int i=0 ; i<aantalValueObjects ; i++)
		        {
	        		if (ipValueList[i] != null)
	        		{
		        		Expressie e = (ipValueList[i]).geefObjectWaarde();
		        		if (e!=null) 
		        		{	
		        			ingevuld = true;
		        			v = v.substitueer(e, "V?("+(i+1)+")");
		        		}
		        		else 
		        		{	
		        			juist = false;
		        			break;
		        		}
	        		}
	        		else
	        		{
	        			juist = false;
	        			break;
	        		}
		        }
	        	try 
	        	{
					juist = v.isOplossing(new BasisExpressie(1.212131415),"q");
				} 
	        	catch (RestartException e) 
	        	{
					juist = false; // Weet Niet
				}
	        }
        } // checkSamen
        else
        { // check afzonderlijk
        	for (int i=0 ; i<aantalValueObjects ; i++)
	        {   
        		if (ipValueList[i] != null)
        		{
	        		//changed opvragen en straks weer terugzetten; wordt altijd op false gezet door kijkNa in ipobjectIsCorrect.
	        		boolean ipValueChanged = ipValueList[i].ipObjectIsChanged();
	        		boolean stapJuist = ipValueList[i].ipObjectIsCorrect(); // dit roept via FEWA.kijkNa() CheckValueUnit.getState() en daarmee kijkNa() aan...
		        	ingevuld = ingevuld || ipValueList[i].ipObjectIsIngevuld();
		        	juist = juist && stapJuist;
		        	if (view)
		        	{
		        		if (show)
		        			ipValueList[i].zetGoedFout(stapJuist);
		        	}
		        	else
		        	{
		        		ipValueList[i].ipObjectResetFeedbackImage(); // anders staan er door FEWA.getState() gewoon vinken/kruizen
		        	}
		        		
		        	ipValueList[i].setChanged(ipValueChanged);
        		}
		    }
	    }
        if (juist)
        {   
            correct = true;
            fout = false;
            score = scoreMax;
            if (mode == OpdrNav.OEFENEN_STRAFPUNTEN)
            	score = Math.max(0, scoreMax - errorCount * foutStraf);
        }
        else 
        {   
            correct = false;
            fout = true;
            verhoogErrorCount(changed & setState);
            score = 0;
        }
        if (show && check)
        {	
        	if (ingevuld && changed)
				comRoot.setChanged(teltMee && !juist);
        	nakijkAchtergrond.setVisible(true);
			if(correct)
        		goedKrulImage.setVisible(true);
        	else
        		foutKruisImage.setVisible(true);
        }
			
        
        //if(show || mode==0 || mode==1)produceAction("changed");
        
		if (show) // alleen als feedback moet worden getoond
		{
			if (correct) 
				fireEvent(EVENT_CORRECT);
			if (fout && errorCount > 1) 
				fireEvent(EVENT_FALSE2);
			if (fout)
				fireEvent(EVENT_FALSE);
		}
    }
    
	private void fireEvent(CBookEvent event) 
	{
		activity.getEventBus().fireEventFromSource(event, this);
		comRoot.fireEvent(event);
	}

    public void verhoogErrorCount(boolean changed)
    {
    	if (changed)
		{
			errorCount++;
		}
    	for (int i = 0; i < aantalValueObjects; i++)
    	{
    		if (ipValueList[i] != null)
    		{
    			ipValueList[i].setChanged(false);
    		}
    	}
	}
    
    public void kijkNa(int stapNr)
    { 	kijkNa();
    }
    
    @Override
	public int getAsHoogte() {
		return ashoogte;
	}

	@Override
	public int getHeight() {
		return hoogte;
	}

	@Override
	public int getWidth() {
		return breedte;
	}
	
	public void zetVolledigeBreedte(int breedte){
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		this.ashoogte = ashoogte;
	}

	@Override
	public void acceptCBookEvent(CBookEvent event) {
		if ("action.setNotEditable".equals(event.getCommand())) {
			editable = false;
			basisPanel.setStyleDependentName("readonly", !editable);
		}
		
	}
}
