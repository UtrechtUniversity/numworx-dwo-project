package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

//import java.util.ArrayList;
import java.awt.Component;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.bouwkamp.gwt.user.client.ui.RoundedPanel;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;








import com.googlecode.mgwt.ui.client.widget.RoundPanel;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.RestartException;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import fi.wiskopdr.text.Text;
import fi.wiskopdr.text.TextConstants;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.DWOLogger;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;

public class CheckSelectieUnit implements InteractionStub
{
	public static final String ACTION_CORRECT = "action.correct";
	public static final String ACTION_FALSE = "action.false";
	public static final String ACTION_FALSE2 = "action.false_2";

	private static final CBookEvent EVENT_CORRECT = new CBookEvent(ACTION_CORRECT); 
	private static final CBookEvent EVENT_FALSE = new CBookEvent(ACTION_FALSE); 
	private static final CBookEvent EVENT_FALSE2 = new CBookEvent(ACTION_FALSE2); 

	public static TextConstants rb = Text.constants;
	static final String holderId = "dockholder";
	
	private HashMap<String, Object> launchState; 
	//String[] randomVarNamen = null;
	//HashMap randomVarWaarden = null;
	
	OpdrNavIF comRoot;
	
	private LayoutPanel basisPanel;
	int breedte = 110;
	int hoogte = 24; 
	int ashoogte = hoogte /2;
	
	private int mode;
	    
    private boolean ingevuld;
    private boolean nagekeken;
	private boolean isVeranderdNaNakijken = false;
    
    private boolean correct;
    private boolean fout;
    
    private int attemptsCount;
	private Vector attempts;
    
    private Point[] randomizedPositions;
    private boolean positionsRandomized;
    
    private boolean multiSelections;
    private boolean randomizePositions;
    private boolean checkFormule;
    private String[] formuleStrings = null;
    
    private int score;
    private int errorCount;
    private int scoreMax=10;
    private int foutStraf = 2;
    private boolean changed = false;
    
	static int GOED = 1;
	static int FOUT = 0;
	static int HALF = 2;
	static int GEEN = 3;
	
	private PushButton checkButton;
	private String knopImageString = "";
	private TekstVakPanel[] ipList; 
	private boolean[] juisteSelecties;
	
	FlowPanel nakijkAchtergrond;
	Image goedKrulImage, foutKruisImage; //goedKrulHalfImage
	
	
	private boolean logOption;
	private String logID;
	
	private boolean[][] logObjectives;
	
	private boolean check = true;
	private boolean teltMee = true;
	private DWOLogger dwologger;
	
	
	

	public void randomizePositions()
	{
		Vector v = new Vector();
		randomizedPositions = new Point[juisteSelecties.length];
		for(int i=0 ; i<ipList.length ; i++)
		{	if(!(ipList[i] instanceof TekstVakPanel) || !ipList[i].isZwevend())return;
			v.addElement(ipList[i].geefLocatie());
		}
		for(int i=0 ; i<ipList.length ; i++)
		{	int r = (int)((ipList.length-i)*Math.random());
			Point p = (Point)(v.elementAt(r));
			if(!positionsRandomized) randomizedPositions[i] = p;
			ipList[i].zetLocatie(p.getX(), p.getY());
			v.removeElementAt(r);
		}
		positionsRandomized = true;
	}	
	
	public void kijkNa()
    {
		// reset isVeranderdNaNakijken
		zetIsVeranderdNaNakijken(false);

		kijkNa(true);
		
    }
    
    public void kijkNa(boolean show)
    {
        boolean juist = true;
        ingevuld = false;
        
        correct = false;
        fout = true;
        score = 0;
        
        if(checkFormule)
        {
        	if(formuleStrings!=null)
        	{
        		
        		VergelijkingMeerv[] v = new VergelijkingMeerv[formuleStrings.length];
        		for(int h=0 ; h<formuleStrings.length ; h++)
		        {
        			boolean stapJuist = true;
        			v[h] = FormuleParser.parseVergelijking(formuleStrings[h]);
        			if(v[h]==null)
        			{	juist = false;
        				break;
        			}
        			
        			for(int i=0 ; i<ipList.length ; i++)
        	        {   Expressie e = ipList[i].isIpSelected() ? ipList[i].geefObjectWaarde() : new BasisExpressie(0);
    	        		if(e!=null) 
    	        		{	v[h] = v[h].substitueer(e, "V?("+(i+1)+")");
    	        		}
    	        		else 
    	        		{	stapJuist = false;
    	        			break;
    	        		}
        	        	ingevuld = ingevuld || ipList[i].isIpSelected();
        	        }
        			
        			try {
						stapJuist = v[h].isOplossing(new BasisExpressie(1.212131415),"q");
					} catch (RestartException e) {
						stapJuist = false; // eigenlijk "weet niet"
					}
        			juist = juist && stapJuist;
        			if(!juist) break;
		        }
        	}
        	else juist = false;
        	
        }
        else
        {   for(int i=0 ; i<ipList.length ; i++)
	        {   if(ipList[i] != null)
	            {	juist = juist && ipList[i].isIpSelected() == juisteSelecties[i];
	            	ingevuld = ingevuld || ipList[i].isIpSelected();
	            }
	        }
        }
        
        if(juist)
        {   //goedKrulImage.setVisible(true);
            correct = true;
            fout = false;
            score = scoreMax;
            if (mode == OpdrNav.OEFENEN_STRAFPUNTEN)
            	score = Math.max(0, scoreMax - errorCount * foutStraf);
        }
        else 
        {   //foutKruisImage.setVisible(true);
            correct = false;
            fout = true;
            verhoogErrorCount();
        	score = 0;
        }
        
        if(show && check)
        {	if (ingevuld && changed)
    			comRoot.setChanged(teltMee && !juist);
        	nakijkAchtergrond.setVisible(true);
        	if(correct)
        		goedKrulImage.setVisible(true);
        	else
        		foutKruisImage.setVisible(true);
        }
        
		if (correct) 
			fireEvent(EVENT_CORRECT);
		if (fout && errorCount > 1) 
			fireEvent(EVENT_FALSE2);
		if (fout)
			fireEvent(EVENT_FALSE);        
    }
    
	private void fireEvent(CBookEvent event) 
	{
		DWOplayer.clientfactory.getEventBus().fireEventFromSource(event, this);
		comRoot.fireEvent(event);
	}

    public void kijkNa(int stapNr)
    { 	kijkNa();
    }
    
    public void verhoogErrorCount()
    {
    	if(changed)
    		errorCount++;
    	changed = false;
    }
		
		
	@Override
	public Widget asWidget() {
		return basisPanel;
	}

	@Override
	public HashMap<String, Object> getState() {
		
		Object[] randomizedPositionsX = null;
		Object[] randomizedPositionsY = null;
		if(randomizedPositions != null)
		{	randomizedPositionsX = new Object[randomizedPositions.length];
			randomizedPositionsY = new Object[randomizedPositions.length];
			for(int i = 0; i < randomizedPositions.length; i++)
			{
				Point point = randomizedPositions[i];
				if(point != null) {
					randomizedPositionsX[i] = (int) point.getX();
					randomizedPositionsY[i] = (int) point.getY();
				} else
					randomizedPositionsX[i] = randomizedPositionsY[i] = 0;
			}
		}
		
		
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

	    kijkNa(false);
		if(dwologger != null) {
			Map<String, Object> map = buildLogParameters();
			dwologger.updateLog(map);
		}
         
	    HashMap<String, Object> h = new HashMap<String, Object>();
        if(randomizedPositionsX != null) 
        	h.put("randomizedPositionsX", randomizedPositionsX);
        if(randomizedPositionsY != null) 
        	h.put("randomizedPositionsY", randomizedPositionsY);
        
        h.put("ingevuld", new Boolean(ingevuld));
        h.put("nagekeken", new Boolean(nagekeken));
		h.put("isVeranderdNaNakijken", new Boolean(isVeranderdNaNakijken));
        h.put("attempts", attempts);
        h.put("attemptsCount", new Integer(attemptsCount));
        h.put("errorCount", new Integer(errorCount));
        return h;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		if(h == null) return; // setStateNull();
		Point[] randomizedPositions = null;
	    boolean ingevuld = false;
	    boolean nagekeken = false;
		boolean isVeranderdNaNakijken = false;
	    Vector attempts = new Vector();
	    int attemptsCount = 0;
		int errorCount = 0;
        ObjectMap map = JSONUtilities.wrapMap(h);
		
		if(map.containsKey("randomizedPositions")) 
	    {	ObjectList list = map.getObjectList("randomizedPositions");
	    	randomizedPositions = new Point[list.size()];
	    	for(int i = 0; i < list.size(); i++)
	    		randomizedPositions[i] = (Point) list.get(i);
	    }
	    else if(map.containsKey("randomizedPositionsX"))
	    {	int[] listX = map.getIntArray("randomizedPositionsX");
	    	int[] listY = map.getIntArray("randomizedPositionsY");
	    	randomizedPositions = new Point[listX.length];
	    	for(int i = 0; i < listX.length; i++)
	    		randomizedPositions[i] = new Point(listX[i], 
	    				listY[i]);
	    }
	    if(map.containsKey("ingevuld")) 
	    	ingevuld = map.getBoolean("ingevuld");
	    if(map.containsKey("nagekeken")) 
	    	nagekeken = map.getBoolean("nagekeken");
		if (h.get("isVeranderdNaNakijken") != null)
			isVeranderdNaNakijken = ((Boolean) h.get("isVeranderdNaNakijken")).booleanValue();
	    if(map.containsKey("attempts"))
	    	attempts = new Vector(map.getList("attempts"));
	    if(map.containsKey("attemptsCount")) 
	    	attemptsCount = map.getInt("attemptsCount");
	    if(map.containsKey("errorCount")) 
	    	errorCount = map.getInt("errorCount");
        
        this.randomizedPositions = randomizedPositions;
        this.ingevuld = ingevuld;
        this.nagekeken = nagekeken;
		this.isVeranderdNaNakijken = isVeranderdNaNakijken;
        this.attempts = attempts;
        this.attemptsCount = attemptsCount;
	    this.errorCount = errorCount;
        
        if(randomizePositions) 
        {   for(int i=0 ; i<ipList.length ; i++)
	        {   
	        	Point p = randomizedPositions[i];
	            ipList[i].zetLocatie(p.getX(), p.getY()); //niet meer nodig.
	        }
	        //(((TekstInteractiePanelVak)((Component)ipList[0]).getParent()).getTekstVak()).layoutTekst();
        }
        
        if(ingevuld && (mode == OpdrNavIF.OEFENEN || mode == OpdrNavIF.OEFENEN_STRAFPUNTEN || (nagekeken && !isVeranderdNaNakijken)))
        {
        	kijkNa();
        }
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
//		//else if(goedKrulHalfImage.isVisible())
//		//	goedFout = "half";
//		else if(foutKruisImage.isVisible())
//			goedFout = "fout";
//		
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
		String logString = "";
		String[] options = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","X","Y","Z"};
		for(int i=0 ; i<ipList.length ; i++)
		{   //ipList[i] = parent.zoekTekstVakPanel(i+1);
			if(ipList[i] != null && ipList[i].isIpSelected() && i<options.length) 
				logString = logString + options[i];
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("response", logString);
		map.put("score", Collections.singletonMap("raw", score));
		if(goedKrulImage.isVisible())
			map.put("success", Boolean.TRUE);
		if(foutKruisImage.isVisible())
			map.put("success", Boolean.FALSE);
		return map;
	}
	
	public void wis()
	{
		//ipList = null;
		//juisteSelecties = null;
		nakijkAchtergrond.setVisible(false);
	    goedKrulImage.setVisible(false);
	    //goedKrulHalfImage.setVisible(false);
	    foutKruisImage.setVisible(false);
		
	    correct = false;
	    score = 0;
	    errorCount = 0;
	    attemptsCount = 0;
	    nagekeken = false;
	    ingevuld = false;
	    
	    attempts = new Vector();
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
		checkButton.setVisible(mode==0 || mode==1);
	}

	public void zetNagekeken(boolean b)
	{
		if (ingevuld)
			nagekeken = b;
	}

	private void zetIsVeranderdNaNakijken(boolean b)
	{
		this.isVeranderdNaNakijken = b;
	}
	
	public void stop()
	{
		kijkNa();
		
	}

	public void start()
	{
	}

	public void destroy()
	{
	}

	public void opnieuw()
	{
		positionsRandomized = false;
    	if(randomizePositions) randomizePositions();
    	score = 0;
		correct = false;
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		zetMode(comRoot.getMode());
		if(dwologger != null) dwologger.setCommunicationRoot(comRoot);
	}
	
	public CheckSelectieUnit(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)//, TekstVakPanel[] ipList)
	{
		
		if (h != null && h.containsKey("breedte"))
			breedte = ((Number) h.get("breedte")).intValue();
		if (h != null && h.containsKey("hoogte"))
			hoogte = ((Number) h.get("hoogte")).intValue();
		if (h != null && h.containsKey("interactiePanelLaunchState"))
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		
		//this.parent = parent;
		
		init(breedte, hoogte, launchState, randomVarWaarden);
		//this.ipList = ipList;
		
		initialize(h, randomVarNamen, randomVarWaarden);
	}

	@Override
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		breedte = width - 30;
		hoogte = height;
		//this.randomVarWaarden = randomValues;
		ObjectMap map = JSONUtilities.wrapMap(launchData);
		if (map != null)
		{
			/*
			if(launchData.get("juisteSelecties") instanceof ArrayList)
			{
				ArrayList<Boolean> juisteSelectiesList = (ArrayList<Boolean>) launchData.get("juisteSelecties");
				juisteSelecties = new boolean[juisteSelectiesList.size()];
				for(int i = 0; i < juisteSelectiesList.size(); i++)
					juisteSelecties[i] = juisteSelectiesList.get(i);
			}
			*/
			if(map.containsKey("juisteSelecties"))
				juisteSelecties = map.getBooleanArray("juisteSelecties");
			if(map.containsKey("scoreMax")) 
				scoreMax = map.getInt("scoreMax");
		    if(map.containsKey("randomizePositions")) 
		    	randomizePositions = map.getBoolean("randomizePositions");
		    if(map.containsKey("multiSelections")) 
		    	multiSelections = map.getBoolean("multiSelections");
		    if(map.containsKey("logOption")) 
		    	logOption = map.getBoolean("logOption");
			if(map.containsKey("logID")) 
				logID = map.getString("logID");
			if(map.containsKey("check")) 
				check = map.getBoolean("check");
			if(map.containsKey("teltMee")) 
				teltMee = map.getBoolean("teltMee");
			if(map.containsKey("checkFormule")) 
				checkFormule = map.getBoolean("checkFormule");
			
			if (map.containsKey("formuleStrings")) {
				formuleStrings = map.getStringArray("formuleStrings");
				if("en".equals(LocaleInfo.getCurrentLocale().getLocaleName()))
				{	for(int i = 0; i < formuleStrings.length; i++)
						formuleStrings[i] = formuleStrings[i].replaceAll("of", "or");
				}
				
			}
			if(map.containsKey("logObjectives"))
			{	ObjectList logObjectivesList = ( map.getObjectList("logObjectives") );
				logObjectives = new boolean[logObjectivesList.size()][];
				for(int i = 0; i < logObjectivesList.size(); i++)
				{	logObjectives[i] = logObjectivesList.getBooleanArray(i);
				}
			}
			if(map.containsKey("knopImageString")) 
				knopImageString = map.getString("knopImageString");
		}
		if(logOption) {
			dwologger = new DWOLogger();
			dwologger.setClassName("fi.wiskopdr.CheckUnitPanel");
			dwologger.setLogID(logID);
			dwologger.setMaxScore(scoreMax);
			dwologger.setLogObjectives(logObjectives);
		}

		
	}
	
	private void initialize(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		attempts = new Vector();
		
		basisPanel = new LayoutPanel();
		//basisPanel.setSize("" + breedte + "px", "" + hoogte + "px");
		
		//int imWidth = breedte - 30;
		int imWidth = breedte;
		int imHeight = 20;
		Image knopImage = null;
		if(knopImageString!=null && !"".equals(knopImageString))
       	{  	ImageView imageView = new ImageView(knopImageString);
       		knopImage = imageView.getImage();
			if(knopImage != null)
			{
				imWidth = imageView.getWidth();
				imHeight = imageView.getHeight();
			}
       		if(imWidth <= 0) 
				imWidth = breedte;
			if(imHeight <= 0) 
				imHeight = 20;
		}
		if(knopImage != null)
		{	checkButton = new PushButton(knopImage);
			checkButton.getElement().getStyle().setPadding(0, Style.Unit.PX);
			checkButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		}
		else
		{	checkButton = new PushButton(Text.constants.klaarKnopLabel());
			checkButton.getElement().getStyle().setFontSize(12, Style.Unit.PX);
			checkButton.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		}
		
		//breedte = imWidth + 30;
		breedte = imWidth;
		hoogte = imHeight + 5;
		ashoogte = hoogte / 2 + 7;
		basisPanel.setPixelSize(breedte ,  hoogte );
		basisPanel.add(checkButton);
		basisPanel.setWidgetLeftWidth(checkButton, 0, Style.Unit.PX, imWidth, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(checkButton, 5, Style.Unit.PX, imHeight, Style.Unit.PX);
		checkButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{	e.stopPropagation();
				kijkNa();
	        	attemptsCount++;
				setAttempt();
			}
		});
		
		nakijkAchtergrond = new FlowPanel();
		if(knopImage != null)
			nakijkAchtergrond.getElement().getStyle().setBackgroundColor("white");
		nakijkAchtergrond.getElement().getStyle().setProperty("borderRadius", (10) + "px");
		nakijkAchtergrond.setVisible(false);
		basisPanel.add(nakijkAchtergrond);
		basisPanel.setWidgetRightWidth(nakijkAchtergrond, 2, Style.Unit.PX, 16, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(nakijkAchtergrond, 7, Style.Unit.PX, 16, Style.Unit.PX);
		
		
		//TODO: Noordhoff-onderscheid maken (ook in plaatsing, alleen in Noordhoff in knop?)
		
		//goedKrulImage = new Image(FormuleHolder.FORMULE_BUNDLE.goedkrul_en().getSafeUri());
		//foutKruisImage = new Image(DWOplayer.DWO_BUNDLE.foutkruis().getSafeUri());
		goedKrulImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
		foutKruisImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri());
		
		basisPanel.add(goedKrulImage);
		basisPanel.add(foutKruisImage);
		//basisPanel.setWidgetLeftWidth(goedKrulImage, imWidth, Style.Unit.PX, 30, Style.Unit.PX);
		//basisPanel.setWidgetTopHeight(goedKrulImage, 0, Style.Unit.PX, imHeight + 5, Style.Unit.PX);
		//basisPanel.setWidgetLeftWidth(foutKruisImage, imWidth, Style.Unit.PX, 30, Style.Unit.PX);
		//basisPanel.setWidgetTopHeight(foutKruisImage, 0, Style.Unit.PX, imHeight + 5, Style.Unit.PX);
		basisPanel.setWidgetRightWidth(goedKrulImage, 1, Style.Unit.PX, 15, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(goedKrulImage, 6, Style.Unit.PX, 20, Style.Unit.PX);
		basisPanel.setWidgetRightWidth(foutKruisImage, 1, Style.Unit.PX, 15, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(foutKruisImage, 5, Style.Unit.PX, 20, Style.Unit.PX);
		goedKrulImage.setVisible(false);
		foutKruisImage.setVisible(false);
		
				
		for(int i=0 ; formuleStrings!=null && i<formuleStrings.length ; i++)
        {	try{
				formuleStrings[i] = FormuleParser.randomizeString(formuleStrings[i], randomVarNamen, randomVarWaarden);
	    	}
	    	catch(Exception e){	}
        }
		
	}
	
	public void zetSelectieObjecten(TekstVakPanel[] selectieObjecten)
	{
		ipList = selectieObjecten;
		for(int i=0 ; i<ipList.length ; i++)
        {   if(ipList[i] != null)
            {	ipList[i].getAsPanel().addDomHandler(new MouseDownHandler(){
	    			public void onMouseDown(MouseDownEvent e){
	    				for(int i = 0; i < ipList.length; i++)
	    				{	if(e.getSource() == ipList[i].getAsPanel())
	    				
	    					{	selectClickAction(i);
	    						break;
	    					}
	    				}
	    			}
	    		}, MouseDownEvent.getType());
	            ipList[i].getAsPanel().addDomHandler(new TouchStartHandler(){
	    			public void onTouchStart(TouchStartEvent e){
	    				for(int i = 0; i < ipList.length; i++)
	    				{	if(e.getSource() == ipList[i].getAsPanel())
	    					{	selectClickAction(i);
	    						break;
	    					}
	    				}
	    			}
	    		}, TouchStartEvent.getType());
            }
        }
        
        if(randomizePositions && !positionsRandomized) randomizePositions();
	}
	
	public int getAantalSelectieObjecten()
	{
		return juisteSelecties.length;
	}

	public void selectClickAction(int i)
	{
		if (nagekeken)
			zetIsVeranderdNaNakijken(true);

		nakijkAchtergrond.setVisible(false);
		goedKrulImage.setVisible(false);
		//goedKrulHalfImage.setVisible(false);
		foutKruisImage.setVisible(false);
		correct = false;
		score = 0;
		
		if(!multiSelections)
		{
			for(int j = 0; j < ipList.length; j++)
				if(i != j)
					ipList[j].setSelected(false);
		}
		changed = true;
	}
	/*
	@Override //nodig?
	public void onModuleLoad() {
		HashMap<String, Object> h = new HashMap<String, Object>();
		h.put("breedte", (Integer)breedte);
		h.put("hoogte", (Integer)hoogte);
		Widget kbp = null;

		initialize(h, null, null);
		
		RootPanel.get(holderId).add(basisPanel);
		RootPanel.get(holderId).addStyleName("root");
		
		Stub.publish(this);
	}
	*/

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

}
