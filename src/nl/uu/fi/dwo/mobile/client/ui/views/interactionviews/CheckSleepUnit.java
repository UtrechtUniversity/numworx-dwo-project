package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.Collections;
//import java.util.ArrayList;
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
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.SVGButton;
import nl.uu.fi.dwo.mobile.client.ui.SVGButton.ButtonListener;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.utils.Review;
import nl.uu.fi.dwo.mobile.utils.StringUtils;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

import com.vaadin.pointerevents.client.PointerDownEvent;
import com.vaadin.pointerevents.client.PointerDownHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.AntwoordVakChecker;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.RestartException;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import fi.wiskopdr.text.Text;

public class CheckSleepUnit implements InteractionStub, CBookEventListener {
	
	private final static Logger logger = Logger.getLogger("CheckSleepUnit");

	public static final String ACTION_CORRECT = "action.correct";
	public static final String ACTION_FALSE = "action.false";
	public static final String ACTION_FALSE2 = "action.false_2";

	private static final CBookEvent EVENT_CORRECT = new CBookEvent(ACTION_CORRECT); 
	private static final CBookEvent EVENT_FALSE = new CBookEvent(ACTION_FALSE); 
	private static final CBookEvent EVENT_FALSE2 = new CBookEvent(ACTION_FALSE2); 

	static final String holderId = "dockholder";
	
	private HashMap<String, Object> launchState; 
	String[] randomVarNamen = null;
	HashMap randomVarWaarden = null;
	
	OpdrNavIF comRoot;
	
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
    
    private int attemptsCount;
	private Vector attempts;
    
    private Point[] randomizedPositions;
    private boolean positionsRandomized;
    private boolean randomizePositions; //Let op verschil!! Ook bij checkSelectieUnit. Bij opnieuw kun je opnieuw randomiseren.
    private boolean snapToTarget;
    
    private int acceptedMarge;
    private Point[] positions;
    
    private boolean checkFormule = false;
	private String formuleString = "$f@";
	private String[] formuleStrings = null;
    
    private int score;
    private int errorCount;
    private int scoreMax=10;
    private int foutStraf = 2;
    private boolean changed = false;
    private boolean editable = true;
    
	static int GOED = 1;
	static int FOUT = 0;
	static int HALF = 2;
	static int GEEN = 3;
	
	private SVGButton checkButton;
	private String knopImageString = "";
	private TekstVakPanel[] ipListSleep; 
	private TekstVakPanel[] ipListDoel;
	
	private int aantalSleepObjects;
	private int aantalDoelObjects;
	
	private boolean hasFeedback = false;
	private List<Map<String,Object>> answerModels;
	
	FlowPanel nakijkAchtergrond;
	Image goedKrulImage, foutKruisImage, goedKrulHalfImage; 
	
	private boolean logOption;
	private String logID;
	
	private boolean[][] logObjectives;
	
	private boolean check = true;
	private boolean teltMee = true;
	
	private String answer;
	
	private boolean relocate;
	private boolean view = false;
	private boolean verzamelDoel;
	private DWOLogger dwologger;
	private CorrectieFacade correctie;
	private Widget widget;
	private ActivityComponent activity;
	
	public void randomizePositions()
	{
		Vector v = new Vector();
		randomizedPositions = new Point[aantalSleepObjects];
		for(int i=0 ; i<aantalSleepObjects ; i++)
		{	if(!(ipListSleep[i] instanceof TekstVakPanel) || !ipListSleep[i].isZwevend())return;
			v.addElement(ipListSleep[i].geefLocatie());
		}
		for(int i=0 ; i<aantalSleepObjects ; i++)
		{	int r = (int)((aantalSleepObjects-i)*Math.random());
			Point p = (Point)(v.elementAt(r));
			if(!positionsRandomized) randomizedPositions[i] = p;
			ipListSleep[i].zetLocatie(p.getX(), p.getY());
			v.removeElementAt(r);
		}
		positionsRandomized = true;
		for(int i=0 ; i<aantalSleepObjects; i++)
	    { 	ipListSleep[i].setStartSleep((int) randomizedPositions[i].getX(), (int) randomizedPositions[i].getY());
	    }
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
	
	private void setEditable(boolean editable) {
		this.editable = editable;
		basisPanel.setStyleDependentName("readonly", !editable);
		if (ipListSleep != null)
			for(TekstVakPanel panel: ipListSleep) {
				panel.setEditable(editable);
		}
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
				randomizedPositionsX[i] = (int) randomizedPositions[i].getX();
				randomizedPositionsY[i] = (int) randomizedPositions[i].getY();
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

	    //if(!("MW".equals(WiskOpdr.deployVariant) || "GR".equals(WiskOpdr.deployVariant))) 
	   	kijkNa(false);
		if(dwologger != null) {
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
        if(randomizedPositionsX != null) 
        	h.put("randomizedPositionsX", randomizedPositionsX);
        
        if(randomizedPositionsY != null) 
        	h.put("randomizedPositionsY", randomizedPositionsY);
        
        h.put("ingevuld", new Boolean(ingevuld));
        h.put("nagekeken", new Boolean(nagekeken));
		h.put("isVeranderdNaNakijken", new Boolean(isVeranderdNaNakijken));
		h.put("editable", Boolean.valueOf(editable));
        h.put("attempts", attempts);
        h.put("attemptsCount", new Integer(attemptsCount));
        h.put("errorCount", new Integer(errorCount));
        if(correctie != null) correctie.correctie(h);
        return h;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		if(h == null) return; // setStateNull();
		Point[] randomizedPositions = null;
	    boolean ingevuld = false;
	    boolean nagekeken = false;
		boolean isVeranderdNaNakijken = false;
		boolean editable = true;
	    Vector attempts = new Vector();
	    int attemptsCount = 0;
		int errorCount = 0;
		CorrectieFacade.showReview(h, asOne(), this, getScoreMax(), activity);

		if(h.get("randomizedPositions") != null) 
	    {	List<Object> randomizedPositionsList = JSONUtilities.toArrayList(h.get("randomizedPositions"));
			randomizedPositions = new Point[randomizedPositionsList.size()];
			for(int i = 0; i < randomizedPositionsList.size(); i++)
				randomizedPositions[i] = (Point) randomizedPositionsList.get(i);
		}
	    else if(h.get("randomizedPositionsX") != null)
	    {	List<Object> randomizedPositionsXList = JSONUtilities.toArrayList(h.get("randomizedPositionsX"));
    		List<Object> randomizedPositionsYList = JSONUtilities.toArrayList(h.get("randomizedPositionsY"));
    		randomizedPositions = new Point[randomizedPositionsXList.size()];
    		for(int i = 0; i < randomizedPositionsXList.size(); i++)
    			randomizedPositions[i] = new Point(((Number)randomizedPositionsXList.get(i)).intValue(), 
    				((Number)randomizedPositionsYList.get(i)).intValue());
	    }
	    if(h.get("ingevuld") != null) 
	    	ingevuld = ((Boolean)h.get("ingevuld")).booleanValue();
	    if(h.get("nagekeken") != null) 
	    	nagekeken = ((Boolean)h.get("nagekeken")).booleanValue();
	    if(h.containsKey("editable"))
	    	editable = ((Boolean)h.get("editable")).booleanValue();
		if (h.get("isVeranderdNaNakijken") != null)
			isVeranderdNaNakijken = ((Boolean) h.get("isVeranderdNaNakijken")).booleanValue();
	    if(h.get("attempts") != null)
	    	attempts = new Vector(JSONUtilities.toArrayList(h.get("attempts")));
	    if(h.get("attemptsCount") != null) 
	    	attemptsCount = ((Number)h.get("attemptsCount")).intValue();
	    if(h.get("errorCount") != null) 
	    	errorCount = ((Number)h.get("errorCount")).intValue();
        
        this.randomizedPositions = randomizedPositions;
        this.ingevuld = ingevuld;
        this.nagekeken = nagekeken;
		this.isVeranderdNaNakijken = isVeranderdNaNakijken;
        this.attempts = attempts;
        this.attemptsCount = attemptsCount;
	    this.errorCount = errorCount;
	    setEditable( editable );

        
        if(randomizePositions) 
        {   for(int i=0 ; i<aantalSleepObjects ; i++)
	        {   
	        	Point p = randomizedPositions[i];
	            ipListSleep[i].setStartSleep((int)p.getX(), (int)p.getY()); //niet meer nodig.
	        }
	    }
 // When to show feedback       
        if(ingevuld && (mode == OpdrNavIF.OEFENEN 
        		|| mode == OpdrNavIF.OEFENEN_STRAFPUNTEN 
        		|| (nagekeken && !isVeranderdNaNakijken && (mode != OpdrNavIF.EINDTOETS || LessonMode.browse == comRoot.getLessonMode()))
        		||Review.isReview(comRoot)))
        {
        	kijkNa();
        }
        correctie = CorrectieFacade.get(h, this, getScoreMax(),comRoot,dwologger, activity);
	}
	
	public void setAttempt()
	{
		if(dwologger != null) {
			Map<String, Object> map = buildLogParameters();
			dwologger.log(map);
		}
// Wim: wordt niet gebruikt, en in een andere volgorde als het VergelijkingVak.		
		String goedFout = "";
		if(correct)
			goedFout = "goed";
		if(fout)
			goedFout = "fout";
		
		String logString = "";
		
		String s = logString;
		s = s + "   ;   ";
		s = s + goedFout;
		s = s + "   ;   ";
		s = s + "score = " + score;
		s = s + "   ;   ";
		s = s + new Date().toString();
		

		attempts.addElement(s);
	}

	private Map<String, Object> buildLogParameters() {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("response", answer);
		map.put("score", Collections.singletonMap("raw", score));
		if(correct)
			map.put("success", Boolean.TRUE);
		if(fout)
			map.put("success", Boolean.FALSE);
		return map;
	}
	
	public void wis()
	{
		ipListSleep = null;
		aantalSleepObjects = 0;
		
		nakijkAchtergrond.setVisible(false);
    	goedKrulImage.setVisible(false);
    	goedKrulHalfImage.setVisible(false);
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
    }
    
    public void start(){}
    
    public void destroy(){}
    
    public void opnieuw()
    { 	positionsRandomized = false;
    	if(randomizePositions) randomizePositions();
    	score = 0;
		correct = false;
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
        answer = "";
                
        correct = false;
        fout = true;
        score = 0;
        
        int puntenFeedback = 0;
        boolean half = false;
        String feedback = null;
        int goedHalfFout = AntwoordVakChecker.GEEN;
        
        
        Point[] doelPosities = new Point[aantalDoelObjects];
        for(int i=0 ; i < aantalDoelObjects; i++)
        {   doelPosities[i] = ipListDoel[i].geefLocatie();
        }
        
        Point[] posities = new Point[aantalSleepObjects];
        TekstVakPanel[] sleepObjecten = new TekstVakPanel[aantalSleepObjects];
        
        for(int i=0 ; i<aantalSleepObjects ; i++)
        {   posities[i] = ipListSleep[i].geefLocatie();
            sleepObjecten[i] = ipListSleep[i];
        }
        
        if(checkFormule)
        {	if(hasFeedback && answerModels!=null) {
        		juist = false;
	        	for(int m=0 ; m<answerModels.size() ; m++)
		        {
	        		Map<String,Object> answerModel = answerModels.get(m);
        			ObjectMap map = JSONUtilities.wrapMap(answerModel);
        			String[] formuleStrings = map.getStringArray("formuleStrings");
        			puntenFeedback = map.getInt("puntenFeedback");
        			feedback = map.getString("feedback");
        			goedHalfFout = map.getInt("goedHalfFout");
        			
        			boolean modelFits = true;
        			
        			if(formuleStrings!=null)
    	        	{	for(int i=0 ; i<sleepObjecten.length ; i++)
    	    	        {   sleepObjecten[i].wisGoedFoutSleep();
    	    	        }
    	        		
    	        		boolean hasLocationStrings = false;
    	        		VergelijkingMeerv[] v = new VergelijkingMeerv[formuleStrings.length];
    	        		for(int h=0 ; h<formuleStrings.length ; h++)
    			        {
    	        			String formuleString = formuleStrings[h];
    	        			String locationStringTotal = null;
    	        			String[] locationStrings = null;
    	        			
    	        			
    	        			int indexSC = formuleStrings[h].indexOf(";");
    	        			if(indexSC>-1){
    	        				formuleString = formuleStrings[h].substring(0,indexSC) + "@";
    	        				locationStringTotal = formuleStrings[h].substring(indexSC+1, formuleStrings[h].length()-1);
    	        				locationStrings = StringUtils.split(locationStringTotal, ",");
    	        				hasLocationStrings = true;
    	        			}
    	        			
    	        			boolean stapJuist = true;
    	        			v[h] = FormuleParser.parseVergelijking(formuleString);
    	        			if(v[h]==null)
    	        			{	modelFits = false;
    	        				break;
    	        			}
    	        			for(int i=0 ; i<aantalDoelObjects ; i++)
    	    		        { 	ipListDoel[i].zetSleepObjecten(sleepObjecten);
    	    	        		Expressie e = ipListDoel[i].geefSleepObjectWaarde();
    	    	        		if(verzamelDoel) e = ipListDoel[i].geefSleepObjectVerzamelWaarde();
    	    	        		if(e!=null) 
    	    	        		{	v[h] = v[h].substitueer(e, "V?("+(i+1)+")");
    	            			logger.info("checkVergel.: "+v[h].toString());
    	    	        		}
    	    	        		else if(locationStrings==null)
    	    	        		{	stapJuist = false;
    	    	        			break;
    	    	        		}
    	    	        	}
    	        			
    	        			try {
    							stapJuist = v[h].isOplossing(new BasisExpressie(1.212131415),"q");
    							logger.info("stapJuist.: "+stapJuist);
    						} catch (RestartException e) {
    							stapJuist = false;
    						}
    	        			modelFits = modelFits && stapJuist;
    	        			if(!modelFits && !hasLocationStrings) 
    	        			{	break;
    	        			}
    	        			if(locationStrings!=null){
    	        				for(int i=0 ; i<locationStrings.length ; i++){
    	            				int location = Integer.parseInt(locationStrings[i].trim());
    	            				ipListDoel[location-1].zetGoedFoutSleep(stapJuist);
    	            			}
    	        			}
    			        }
    	        	}
        			logger.info("modelFits: "+modelFits);
        			if(modelFits) {
        				
        				half = true;
        				juist = goedHalfFout==AntwoordVakChecker.GOED;
        				break;
        			}
	        	}
        	}
        	else {
	        	if(formuleStrings!=null)
	        	{	for(int i=0 ; i<sleepObjecten.length ; i++)
	    	        {   sleepObjecten[i].wisGoedFoutSleep();
	    	        }
	        		
	        		boolean hasLocationStrings = false;
	        		VergelijkingMeerv[] v = new VergelijkingMeerv[formuleStrings.length];
	        		for(int h=0 ; h<formuleStrings.length ; h++)
			        {
	        			String formuleString = formuleStrings[h];
	        			String locationStringTotal = null;
	        			String[] locationStrings = null;
	        			
	        			
	        			int indexSC = formuleStrings[h].indexOf(";");
	        			if(indexSC>-1){
	        				formuleString = formuleStrings[h].substring(0,indexSC) + "@";
	        				locationStringTotal = formuleStrings[h].substring(indexSC+1, formuleStrings[h].length()-1);
	        				locationStrings = StringUtils.split(locationStringTotal, ",");
	        				hasLocationStrings = true;
	        			}
	        			
	        			boolean stapJuist = true;
	        			v[h] = FormuleParser.parseVergelijking(formuleString);
	        			if(v[h]==null)
	        			{	juist = false;
	        				break;
	        			}
	        			for(int i=0 ; i<aantalDoelObjects ; i++)
	    		        { 	ipListDoel[i].zetSleepObjecten(sleepObjecten);
	    	        		Expressie e = ipListDoel[i].geefSleepObjectWaarde();
	    	        		if(verzamelDoel) e = ipListDoel[i].geefSleepObjectVerzamelWaarde();
	    	        		if(e!=null) 
	    	        		{	v[h] = v[h].substitueer(e, "V?("+(i+1)+")");
	    	        		}
	    	        		else if(locationStrings==null)
	    	        		{	stapJuist = false;
	    	        			break;
	    	        		}
	    	        	}
	        			
	        			try {
							stapJuist = v[h].isOplossing(new BasisExpressie(1.212131415),"q");
						} catch (RestartException e) {
							stapJuist = false;
						}
	        			juist = juist && stapJuist;
	        			if(!juist && !hasLocationStrings) 
	        			{	break;
	        			}
	        			if(locationStrings!=null){
	        				for(int i=0 ; i<locationStrings.length ; i++){
	            				int location = Integer.parseInt(locationStrings[i].trim());
	            				ipListDoel[location-1].zetGoedFoutSleep(stapJuist);
	            			}
	        			}
			        }
	        	}
	        	else
	        	{	VergelijkingMeerv v = FormuleParser.parseVergelijking(formuleString);
		        	for(int i=0 ; i<aantalDoelObjects ; i++)
			        {   
		        		ipListDoel[i].zetSleepObjecten(sleepObjecten);
		        		Expressie e = ipListDoel[i].geefSleepObjectWaarde();
		        		if(e!=null) 
		        		{	v = v.substitueer(e, "V?("+(i+1)+")");
		        		}
		        		else 
		        		{	juist = false;
		        			break;
		        		}
			        }
		        	try {
						juist = v.isOplossing(new BasisExpressie(1.212131415),"q");
					} catch (RestartException e) {
						juist = false;
					}
		        	
	        	}
	        	// construeer antwoord (brxxx)
				for(int i=0 ; i<aantalDoelObjects ; i++)
		        {   
	        		ipListDoel[i].zetSleepObjecten(sleepObjecten);
	        		Expressie e = ipListDoel[i].geefSleepObjectWaarde();
	        		if(e!=null) 
	        		{	answer = answer + e.toString();
	        		}
		        }
        	}
		} // checkformule
        else
        {
        	for (int i = 0; i < aantalSleepObjects; i++)
	        {
        		ipListSleep[i].wisGoedFoutSleep();
	        }
        	boolean stapJuist = true;
	        for (int i = 0; i < aantalDoelObjects && i < aantalSleepObjects; i++)
	        {  
	        	int dx = (int) Math.abs(posities[i].getX() - doelPosities[i].getX());
	        	int dy = (int) Math.abs(posities[i].getY() - doelPosities[i].getY());
	        	
	        	if (!isBinnenMarge(ipListSleep[i], ipListDoel[i]))
	        	{
	        		stapJuist = false;
	        		juist = juist && stapJuist;
	        		if(!view) break;
	        	}
	        	else 
	        		answer = answer + i + "-" + i + ",";
	        	
	        	
	        	if(view && show){
    				if(stapJuist) ipListSleep[i].zetGoedFoutSleep(stapJuist);
    				else 
    					for(int j=0 ; j < aantalDoelObjects; j++)
	    	            {
	    	        		dx = (int) Math.abs(posities[i].getX() - doelPosities[j].getX());
	    		        	dy = (int) Math.abs(posities[i].getY() - doelPosities[j].getY());
	    		        	if(dx < acceptedMarge && dy < acceptedMarge) 
	    		        	{	ipListSleep[i].zetGoedFoutSleep(false);
	    		        		break;
	    		        	}
	    	            }
    					stapJuist = true;
        		}
	        }
	        //TODO Fout antwoord 'construeren'  (wordt nu niet gedaan)
	        for(int i=aantalDoelObjects ; i<aantalSleepObjects ; i++)
	        {   stapJuist = true;
	        	for(int j=0 ; j<aantalDoelObjects ; j++)
	            {
	        		int dx = (int) Math.abs(posities[i].getX() - doelPosities[j].getX());
		        	int dy = (int) Math.abs(posities[i].getY() - doelPosities[j].getY());
		        	if(dx < acceptedMarge && dy < acceptedMarge) 
		        	{	stapJuist = false;
		        		answer = answer + j + "-" + i + ",";
		        		break;
		        	}
	            }
	        	juist = juist && stapJuist;
	        	if(view && show && !stapJuist)
    				ipListSleep[i].zetGoedFoutSleep(stapJuist);
	        }
	        if(answer.length()>0 && answer.charAt(answer.length()-1)==',')answer = answer.substring(0,answer.length()-1);
        }
        
        if(juist && half)
        {   correct = true;
            fout = false;
            score = puntenFeedback;
            if (mode == OpdrNav.OEFENEN_STRAFPUNTEN)
            	score = Math.max(0, scoreMax - errorCount * foutStraf);
        }
        else if(juist)
        {   correct = true;
            fout = false;
            score = scoreMax;
            if (mode == OpdrNav.OEFENEN_STRAFPUNTEN)
            	score = Math.max(0, scoreMax - errorCount * foutStraf);
        }
        else if(half) 
        {
        	correct = false;
            fout = false;
            score = puntenFeedback;
            if (mode == OpdrNav.OEFENEN_STRAFPUNTEN)
            	score = Math.max(0, scoreMax - errorCount * foutStraf);
        }
        else 
        {   correct = false;
            fout = true;
            verhoogErrorCount();
        	score = 0;
        }
        
		if (show && check)
		{
			if (ingevuld && changed)
				comRoot.setChanged(teltMee && !juist);
			nakijkAchtergrond.setVisible(true);
			if (correct)
				goedKrulImage.setVisible(true);
			else if(half)
        		goedKrulHalfImage.setVisible(true);
			else
				foutKruisImage.setVisible(true);

		}
        
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
    
    /**
     * True als het gegeven sleepObject binnen de marge van het gegeven sleepDoel valt,
     * anders false.
     * 
     * @param sleepObject
     * @param sleepDoel
     * @return
     */
    private boolean isBinnenMarge(TekstVakPanel sleepObject, TekstVakPanel sleepDoel)
	{
		boolean isBinnenMarge = false;
		int sleepObjectX = (int) sleepObject.geefLocatie().getX();
		int sleepObjectY = (int) sleepObject.geefLocatie().getY();
		int sleepObjectBreedte = sleepObject.getWidth();
		int sleepObjectHoogte = sleepObject.getHeight();

		int sleepDoelX = (int) sleepDoel.geefLocatie().getX();
		int sleepDoelY = (int) sleepDoel.geefLocatie().getY();
		int sleepDoelBreedte = sleepDoel.getWidth();
		int sleepDoelHoogte = sleepDoel.getHeight();
		
		if (sleepObjectX > sleepDoelX - acceptedMarge // check x-coordinaat
			&& sleepObjectX < (sleepDoelX + sleepDoelBreedte - sleepObjectBreedte + acceptedMarge)
			&& sleepObjectY > sleepDoelY - acceptedMarge // check y-coordinaat
			&& sleepObjectY < sleepDoelY + sleepDoelHoogte - sleepObjectHoogte + acceptedMarge)
		{
			isBinnenMarge = true;
		}
		
		return isBinnenMarge;
	}

	private void fireEvent(CBookEvent event) 
	{
		activity.getEventBus().fireEventFromSource(event, this);
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
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		zetMode(comRoot.getMode(), comRoot.getLessonMode());
		if(dwologger!=null)
			dwologger.setCommunicationRoot(comRoot);
		comRoot.addCBookEventListener("action.setNotEditable", this);
	}
	
	
    public CheckSleepUnit(ActivityComponent activity, HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)//, TekstVakPanel[] ipListSleep, TekstVakPanel[] ipListDoel)
	{
		this.activity = activity;
//		if (h != null && h.get("breedte") != null)
//			breedte = ((Number) h.get("breedte")).intValue();
//		if (h != null && h.get("hoogte") != null)
//			hoogte = ((Number) h.get("hoogte")).intValue();
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		
		//this.ipListSleep = ipListSleep;
		//this.ipListDoel = ipListDoel;
		init(breedte, hoogte, launchState, randomVarWaarden);
		
		initialize(h, randomVarNamen, randomVarWaarden);
	}
	
    @Override
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		breedte = width;// - 30;
		hoogte = height;
		ObjectMap map = JSONUtilities.wrapMap(launchData);
		
		if (launchData != null)
		{
			if(map.containsKey("hasFeedback"))
				hasFeedback = map.getBoolean("hasFeedback");
			if(map.containsKey("answerModels"))
				answerModels = map.getMapList("answerModels");
			
			if(launchData.get("scoreMax") != null) 
				scoreMax = ((Number)launchData.get("scoreMax")).intValue();
		    if(launchData.get("randomizePositions") != null) 
		    	randomizePositions = ((Boolean)launchData.get("randomizePositions")).booleanValue();
		    if(launchData.get("logOption") != null) 
		    	logOption = ((Boolean)launchData.get("logOption")).booleanValue();
			if(logOption && launchData.get("logID") != null) 
				logID = (String)launchData.get("logID");
			if(launchData.get("check") != null) 
				check = ((Boolean)launchData.get("check")).booleanValue();
			if(launchData.get("teltMee") != null) 
				teltMee = ((Boolean)launchData.get("teltMee")).booleanValue();
			if(launchData.get("checkFormule") != null) 
				checkFormule = ((Boolean)launchData.get("checkFormule")).booleanValue();
			if(launchData.get("aantalSleepObjects") != null)
				aantalSleepObjects = ((Number)launchData.get("aantalSleepObjects")).intValue();
			if(launchData.get("aantalDoelObjects") != null)
				aantalDoelObjects = ((Number)launchData.get("aantalDoelObjects")).intValue();
			if(launchData.get("snapToTarget") != null)
				snapToTarget = ((Boolean) launchData.get("snapToTarget")).booleanValue();
			if(launchData.get("acceptedMarge") != null)
				acceptedMarge = ((Number) launchData.get("acceptedMarge")).intValue();
			if(launchData.get("formuleString") != null)
				formuleString = (String) launchData.get("formuleString");
			if(launchData.get("relocate") != null)
				relocate = ((Boolean) launchData.get("relocate")).booleanValue();
			if(launchData.get("view") != null)
				view = ((Boolean) launchData.get("view")).booleanValue();
			if(launchData.get("verzamelDoel") != null)
				verzamelDoel = ((Boolean) launchData.get("verzamelDoel")).booleanValue();
				
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
			String[] smObjectives = JSONUtilities.toStringArray(launchData.get("smObjectives"));
			if (smObjectives != null && smObjectives.length > 0)
				logOption = true;
	
			if(launchData.get("knopImageString") != null) 
				knopImageString = (String)launchData.get("knopImageString");
			
			if(logOption) {
				dwologger = new DWOLogger(activity);
				dwologger.setLogID(logID);
				dwologger.setClassName("fi.wiskopdr.CheckSleepUnitPanel/" + getAantalSleepObjects() + "," + getAantalDoelObjects());
				dwologger.setMaxScore(scoreMax);
				dwologger.setLogObjectives(logObjectives);
				dwologger.setSMObjectives(smObjectives);
				dwologger.setTeltMee(teltMee);
			}
		}
	}
	
	private void initialize(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		attempts = new Vector();
		
		basisPanel = new LayoutPanel();
		basisPanel.setStylePrimaryName("checksleepunit");
		//basisPanel.setSize("" + breedte + "px", "" + hoogte + "px");
		
		int imWidth = breedte;
		int imHeight = hoogte;
		Image knopImage = null;
		if(knopImageString!=null && !"".equals(knopImageString))
       	{  	ImageView imageView = new ImageView(knopImageString, activity);
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
				if (!editable) return;
				kijkNa();
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
		goedKrulHalfImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_geel().getSafeUri());
		
		basisPanel.add(goedKrulImage);
		basisPanel.add(foutKruisImage);
		basisPanel.add(goedKrulHalfImage);
		
//		basisPanel.setWidgetLeftWidth(goedKrulImage, imWidth, Style.Unit.PX, 30, Style.Unit.PX);
//		basisPanel.setWidgetTopHeight(goedKrulImage, 0, Style.Unit.PX, imHeight + 5, Style.Unit.PX);
//		basisPanel.setWidgetLeftWidth(foutKruisImage, imWidth, Style.Unit.PX, 30, Style.Unit.PX);
//		basisPanel.setWidgetTopHeight(foutKruisImage, 0, Style.Unit.PX, imHeight + 5, Style.Unit.PX);
		basisPanel.setWidgetRightWidth(goedKrulImage, 2, Style.Unit.PX, 15, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(goedKrulImage, 7, Style.Unit.PX, 20, Style.Unit.PX);
		basisPanel.setWidgetRightWidth(goedKrulHalfImage, 2, Style.Unit.PX, 15, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(goedKrulHalfImage, 7, Style.Unit.PX, 20, Style.Unit.PX);
		basisPanel.setWidgetRightWidth(foutKruisImage, 2, Style.Unit.PX, 15, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(foutKruisImage, 6, Style.Unit.PX, 20, Style.Unit.PX);
		
		goedKrulImage.setVisible(false);
		foutKruisImage.setVisible(false);
		goedKrulHalfImage.setVisible(false);
		
				
		for(int i=0 ; formuleStrings!=null && i<formuleStrings.length ; i++)
        {	try{
				formuleStrings[i] = FormuleParser.randomizeString(formuleStrings[i], randomVarNamen, randomVarWaarden);
	    	}
	    	catch(Exception e){	}
        }
		
		
		
	}
	
	public void zetSleepDoelObjecten(TekstVakPanel[] sleepObjecten, TekstVakPanel[] doelObjecten)
	{
		ipListDoel = doelObjecten;
		ipListSleep = sleepObjecten;
		Point[] doelPosities = new Point[aantalDoelObjects];
		//System.out.println("aantalDoelObjects: " + aantalDoelObjects);
        for(int i=0 ; i<aantalDoelObjects ; i++)
        {   doelPosities[i] = ipListDoel[i].geefLocatie();
        }
        
        for(int i=0 ; i<aantalSleepObjects ; i++)
        {   if(ipListSleep[i] != null)
        	{	ipListSleep[i].zetSleepDoelPosities(doelPosities);
        		ipListSleep[i].zetSleepDoelen(ipListDoel); // gebruiken voor snap to target als sleepdoel groter dan sleepobject
	            ipListSleep[i].zetSleepdoelMarge(acceptedMarge);
	            ipListSleep[i].zetSleepSnap(snapToTarget);
	            ipListSleep[i].setRelocate(relocate);
	            if(relocate)
	            	ipListSleep[i].setStartSleep();
	            ipListSleep[i].getAsPanel().addDomHandler(new MouseDownHandler(){
		    		public void onMouseDown(MouseDownEvent e){
		    			clickAction();
		    		}
		    	}, MouseDownEvent.getType());
	            ipListSleep[i].getAsPanel().addDomHandler(new TouchStartHandler(){
		    		public void onTouchStart(TouchStartEvent e){
		    			clickAction();
		    		}
		    	}, TouchStartEvent.getType());
	            ipListSleep[i].getAsPanel().addDomHandler(new PointerDownHandler(){
		    		public void onPointerDown(PointerDownEvent e){
		    			clickAction();
		    		}
		    	}, PointerDownEvent.getType());
            }
        }
        
        if(randomizePositions && !positionsRandomized) randomizePositions();
	}
	
	public int getAantalSleepObjects()
	{
		return aantalSleepObjects;
	}
	
	public int getAantalDoelObjects()
	{
		return aantalDoelObjects;
	}
	
	private void clickAction()
	{
		if (!editable) return;

		if (nagekeken)
			zetIsVeranderdNaNakijken(true);

		nakijkAchtergrond.setVisible(false);
		goedKrulImage.setVisible(false);
		goedKrulHalfImage.setVisible(false);
		foutKruisImage.setVisible(false);
		correct = false;
		score = 0;
		ingevuld = true;
		for(int j = 0; j < ipListSleep.length; j++)
			ipListSleep[j].wisGoedFoutSleep();
		changed = true;
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
		if("action.setNotEditable".equals(event.getCommand()))
		{
			setEditable (false);
		}
		
	}
}
