package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.util.promise.Promise;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import fi.wiskopdr.AntwoordVakChecker;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.RestartException;
import fi.wiskopdr.WiskOpdr;
import fi.wiskopdr.expressies.Algebra;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import fi.wiskopdr.text.Text;
import fi.wiskopdr.text.TextConstants;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.ideas.client.RuleIF;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.LessonMode;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.TekstElement;
import nl.uu.fi.dwo.interaction.client.event.CBookEvent;
import nl.uu.fi.dwo.interaction.client.event.CBookEventListener;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.CorrectieFacade;
import nl.uu.fi.dwo.mobile.client.sco.CorrectieReview;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.ActivityInterface;
import nl.uu.fi.dwo.mobile.client.ui.OpdrNav;
import nl.uu.fi.dwo.mobile.client.ui.SVGButton;
import nl.uu.fi.dwo.mobile.client.ui.SVGButton.ButtonListener;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.berekeningvak.BerekeningVakButton;
import nl.uu.fi.dwo.mobile.utils.LogBuilder;
import nl.uu.fi.dwo.mobile.utils.Logging;
import nl.uu.fi.dwo.mobile.utils.Review;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;

public class CheckSelectieUnit implements InteractionStub, InteractionViewWithMisconceptions, CBookEventListener
{
	private final static Logger logger = Logger.getLogger("CheckSelectieUnit");

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
	FeedbackPanel feedbackPanel;
	
	private LayoutPanel basisPanel;
	private Widget widget;
	
	int breedte = 126;
	int hoogte = 26; 
	int ashoogte = hoogte /2;
	
	private int mode;
	    
    private boolean ingevuld;
    private boolean nagekeken;
	private boolean isVeranderdNaNakijken = false;
	private boolean editable = true;
    
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
	
	private SVGButton checkButton;
	private String knopImageString = "";
	private TekstVakPanel[] ipList; 
	private boolean[] juisteSelecties;
	
	private boolean hasFeedback = false;
	private List<Map<String,Object>> answerModels;
	
	FlowPanel nakijkAchtergrond;
	Image goedKrulImage, foutKruisImage, goedKrulHalfImage;
	
	
	private boolean logOption;
	private String logID;
	
	private boolean[][] logObjectives;
	private boolean[][][] logMisconceptions;
	private int[][] possibleMisconceptions;
	private int[][] measuredMisconceptions;
	
	private boolean check = true;
	private boolean teltMee = true;
	private Logging dwologger;
	
	private int[] randomSequence;
	

	public void randomizePositions()
	{
		boolean zwevend = true;
		Vector v = new Vector();
		randomizedPositions = new Point[juisteSelecties.length];
		for(int i=0 ; i<ipList.length ; i++)
		{	if(!(ipList[i] instanceof TekstVakPanel))
				return;
			if(!ipList[i].isZwevend()) {
				zwevend = false;
				break;
			}
			v.addElement(ipList[i].geefLocatie());
		}
		for(int i=0 ; i<ipList.length && zwevend; i++)
		{	int r = (int)((ipList.length-i)*Math.random());
			Point p = (Point)(v.elementAt(r));
			if(!positionsRandomized) randomizedPositions[i] = p;
			ipList[i].zetLocatie(p.getX(), p.getY());
			v.removeElementAt(r);
		}
		
		if(!zwevend && !positionsRandomized) {
			for(int i=0 ; i<ipList.length ; i++)
			{	((TekstVakPanel)ipList[i]).setRandomPositioned(true);
			}
			Vector parents = new Vector();
			Vector parentsTemp = new Vector();
			randomSequence = new int[ipList.length];
			
			boolean randomizable = true; //randomiseren gaat niet goed als objecten dezelfde parent hebben (en niet zwevend zijn)
			if(ipList.length>1) {
				TekstVak tv0 = (TekstVak)ipList[0].asWidget().getParent().getParent();
				TekstVak tv1 = (TekstVak)ipList[1].asWidget().getParent().getParent();
				if(tv0==tv1) {
					randomizable = false;
				}
			}
			for(int i=0 ; i<ipList.length && randomizable ; i++)
			{	
				TekstVak tv = (TekstVak)ipList[i].asWidget().getParent().getParent();
				parents.addElement(tv);
				parentsTemp.addElement(tv);
				ArrayList<Object> objects = tv.getOpdrachtObjects();
				objects.remove(ipList[i]);
				Panel p = (Panel)ipList[i].asWidget().getParent();
				tv.clear();
			}
			for(int i=0 ; i<ipList.length && randomizable; i++)
			{	int r = (int)((ipList.length-i)*Math.random());
				randomSequence[i] = r;
				TekstVak tv = (TekstVak)(parentsTemp.elementAt(r));
				ArrayList<Object> objects = tv.getOpdrachtObjects();
				objects.add(0,ipList[i]);
				parentsTemp.removeElementAt(r);
			}
			for(int i=0 ; i<parents.size()  && randomizable; i++)
			{	TekstVak tv = (TekstVak)parents.elementAt(i);
				tv.reLayout();
			}
		}
		positionsRandomized = true;
	}	
	
/**
 * Externe kijkna. Doe alles wat de UI zou doen.
 */
	public void kijkNa()
	{
		kijkNa_intern();
		attemptsCount++;
		setAttempt();
		adviseMe();
	}

	// voorheen kijkna, wordt alleen intern aangeroepen.
	private void kijkNa_intern()
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
        
        int puntenFeedback = 0;
        boolean half = false;
        String feedback = null;
        int goedHalfFout = AntwoordVakChecker.GEEN;
        
        if(checkFormule)
        {
        	if(formuleStrings!=null)
        	{
        		if(hasFeedback && answerModels!=null) {
            		
        			for(int m=0 ; m<answerModels.size() ; m++)
        	        {	Map<String,Object> answerModel = answerModels.get(m);
            			ObjectMap map = JSONUtilities.wrapMap(answerModel);
            			String[] formuleStrings = map.getStringArray("formuleStrings");
            			puntenFeedback = map.getInt("puntenFeedback");
            			feedback = map.getString("feedback");
            			goedHalfFout = map.getInt("goedHalfFout");
            			
            			boolean modelFits = true;
            			
            			if(formuleStrings==null) {
            				juist = goedHalfFout == 0;
    	        			half = goedHalfFout == 1;
    	        			break;
            			}
            				
            			
            			VergelijkingMeerv[] v = new VergelijkingMeerv[formuleStrings.length];
    	        		for(int h=0 ; h<formuleStrings.length ; h++)
    			        {
    	        			boolean stapJuist = true;
    	        			v[h] = FormuleParser.parseVergelijking(formuleStrings[h]);
    	        			logger.info(formuleStrings[h].toString());
    	        			logger.info(v[h].toString());
    	        			if(v[h]==null)
    	        			{	modelFits = false;
    	        				break;
    	        			}
    	        			
    	        			for(int i=0 ; i<ipList.length ; i++)
    	        	        {   Expressie e = ipList[i].isIpSelected() ? ipList[i].geefObjectWaarde() : new BasisExpressie(0);
    	    	        		if(e!=null) 
    	    	        		{	v[h] = v[h].substitueer(e, "V?("+(i+1)+")");
    	    	        		logger.info(v[h].toString());
    	    	        		}
    	    	        		else 
    	    	        		{	stapJuist = false;
    	    	        			break;
    	    	        		}
    	        	        	ingevuld = ingevuld || ipList[i].isIpSelected();
    	        	        }
    	        			
    						stapJuist = v[h].isWareBeweringNummeriek();
    	        			modelFits = modelFits && stapJuist;
    	        			if(!modelFits) break;
    			        }
    	        		if(modelFits) {
    	        			juist = goedHalfFout == 0;
    	        			half = goedHalfFout == 1;
    	        			break;
    	        		}
    	        		else {
            				puntenFeedback = 0;
            				feedback = "";
            				juist = false;
            				half = false;
            			}
    	        	 }
        		}
        		else {
	        		VergelijkingMeerv[] v = new VergelijkingMeerv[formuleStrings.length];
	        		for(int h=0 ; h<formuleStrings.length ; h++)
			        {
	        			boolean stapJuist = true;
	        			v[h] = FormuleParser.parseVergelijking(formuleStrings[h]);
	        			logger.info(formuleStrings[h].toString());
	        			logger.info(v[h].toString());
	        			if(v[h]==null)
	        			{	juist = false;
	        				break;
	        			}
	        			
	        			for(int i=0 ; i<ipList.length ; i++)
	        	        {   Expressie e = ipList[i].isIpSelected() ? ipList[i].geefObjectWaarde() : new BasisExpressie(0);
	    	        		if(e!=null) 
	    	        		{	v[h] = v[h].substitueer(e, "V?("+(i+1)+")");
	    	        			logger.info(v[h].toString());
	    	        		}
	    	        		else 
	    	        		{	stapJuist = false;
	    	        			break;
	    	        		}
	        	        	ingevuld = ingevuld || ipList[i].isIpSelected();
	        	        }
	        			
	        			stapJuist = v[h].isWareBeweringNummeriek();
	        			juist = juist && stapJuist;
	        			if(!juist) break;
			        }
        		}
	        }
	        else juist = false;
        	
        }
        else
        {   for(int i=0 ; i<ipList.length ; i++)
	        {   if(ipList[i] != null)
	            {	juist = juist && ipList[i].isIpSelected() == juisteSelecties[i];
		            if(measuredMisconceptions!=null && logMisconceptions!=null && ((TekstVakPanel)ipList[i]).isIpSelected())
		            {	for( int j=0 ; logMisconceptions[i]!=null && j<logMisconceptions[i].length && j<measuredMisconceptions.length ; j++)
		        		{	for( int k=0 ; logMisconceptions[i][j]!=null && k<logMisconceptions[i][j].length && k<measuredMisconceptions[j].length; k++)
		            		{	if(logMisconceptions[i][j][k])
		        					measuredMisconceptions[j][k] = 1;
		            		}
		        		}
		            }	
		            ingevuld = ingevuld || ipList[i].isIpSelected();
	            }
	        }
        
        	//logger.info("answerModels = "+answerModels);
        	if(hasFeedback && answerModels!=null) {
        		for(int m=0 ; m<answerModels.size() ; m++)
    	        {	Map<String,Object> answerModel = answerModels.get(m);
        			ObjectMap map = JSONUtilities.wrapMap(answerModel);
        			boolean[] juisteSelecties = map.getBooleanArray("juisteSelecties");
        			String[] formuleStrings = map.getStringArray("formuleStrings");
        			puntenFeedback = map.getInt("puntenFeedback");
        			feedback = map.getString("feedback");
        			goedHalfFout = map.getInt("goedHalfFout");
        			//logger.info("puntenFeedback = "+puntenFeedback);
        			
        			boolean modelFits = true;
        			for(int i=0 ; i<ipList.length ; i++)
        	        {   if(ipList[i] != null)
        	            {	modelFits = modelFits && ipList[i].isIpSelected() == juisteSelecties[i];
        		            if(measuredMisconceptions!=null && logMisconceptions!=null && ((TekstVakPanel)ipList[i]).isIpSelected())
        		            {	for( int j=0 ; logMisconceptions[i]!=null && j<logMisconceptions[i].length && j<measuredMisconceptions.length ; j++)
        		        		{	for( int k=0 ; logMisconceptions[i][j]!=null && k<logMisconceptions[i][j].length && k<measuredMisconceptions[j].length; k++)
        		            		{	if(logMisconceptions[i][j][k])
        		        					measuredMisconceptions[j][k] = 1;
        		            		}
        		        		}
        		            }	
        	            }
        	        }
        			if(modelFits) {
        				half = true;
        				break;
        			}
        			else {
        				puntenFeedback = 0;
        				feedback = "";
        				goedHalfFout = AntwoordVakChecker.FOUT;
        			}
        		}
        	}
        }
        boolean changedTemp = changed;
        
        if(juist && half)
        {   correct = true;
            fout = false;
            score = puntenFeedback;
            if (mode == OpdrNav.OEFENEN_STRAFPUNTEN)
            	score = Math.max(0, scoreMax - errorCount * foutStraf);
        }
        else if(juist)
        {   //goedKrulImage.setVisible(true);
            correct = true;
            fout = false;
            score = scoreMax;
            if (mode == OpdrNav.OEFENEN_STRAFPUNTEN)
            	score = Math.max(0, scoreMax - errorCount * foutStraf);
        }
        else if(half)
        {
        	correct = AntwoordVakChecker.GOED == goedHalfFout;
            fout = AntwoordVakChecker.FOUT == goedHalfFout;
            score = puntenFeedback;
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
        {	if (ingevuld && changedTemp)
    			comRoot.setChanged(teltMee && !juist);
        	nakijkAchtergrond.setVisible(true);
        	if(correct)
        		goedKrulImage.setVisible(true);
        	else if(half)
        		goedKrulHalfImage.setVisible(true);
        	else
        		foutKruisImage.setVisible(true);
        	
        	if(feedback!=null && !"".equals(feedback.trim())) {
	        	feedbackPanel = new FeedbackPanel(basisPanel, feedback);
	        	feedbackPanel.show(basisPanel.getAbsoluteLeft()+basisPanel.getOffsetWidth()-30, basisPanel.getAbsoluteTop()+40);
	        	
        	}
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
    
	private void fireEvent(CBookEvent event) 
	{
		activity.getEventBus().fireEventFromSource(event, this);
		comRoot.fireEvent(event);
	}

    public void kijkNa(int stapNr)
    { 	kijkNa_intern();
    }
    
    public void verhoogErrorCount()
    {
    	if(changed)
    		errorCount++;
    	changed = false;
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

	private CorrectieFacade correctie;
	private final ActivityInterface activity;

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
	    if (this.ingevuld) comRoot.setVisited(); // zie Scorewidget: "Gedaan" subtieler maken
		if(dwologger != null) {
			Map<String, Object> map = buildLogParameters();
			if (mode != OpdrNavIF.ZELFTOETS && this.ingevuld && (!nagekeken || isVeranderdNaNakijken) ) {
				this.nagekeken = nagekeken = true;
				zetIsVeranderdNaNakijken(isVeranderdNaNakijken = false);
				dwologger.log(map);
			} else if (mode == OpdrNavIF.ZELFTOETS && ingevuld) { 
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
        if(randomSequence!=null)
        	h.put("randomSequence", randomSequence);
        h.put("ingevuld", Boolean.valueOf(ingevuld));
        h.put("nagekeken", Boolean.valueOf(nagekeken));
        h.put("editable", Boolean.valueOf(editable));
		h.put("isVeranderdNaNakijken", Boolean.valueOf(isVeranderdNaNakijken));
        h.put("attempts", attempts);
        h.put("attemptsCount", Integer.valueOf(attemptsCount));
        h.put("errorCount", Integer.valueOf(errorCount));
        
        if(correctie != null) correctie.correctie(h);
        return h;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		
		if(h == null) {
			if(randomizePositions && !positionsRandomized) randomizePositions();
			return; // setStateNull();
		}
		Point[] randomizedPositions = null;
	    boolean ingevuld = false;
	    boolean nagekeken = false;
		boolean isVeranderdNaNakijken = false;
		boolean editable = true;
	    Vector attempts = new Vector();
	    int attemptsCount = 0;
		int errorCount = 0;
        ObjectMap map = JSONUtilities.wrapMap(h);
		CorrectieFacade.showReview(h, asOne(), this, getScoreMax(), activity);
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
		if(map.containsKey("randomSequence"))
			randomSequence = map.getIntArray("randomSequence");
	    if(map.containsKey("ingevuld")) 
	    		ingevuld = map.getBoolean("ingevuld");
	    if(map.containsKey("nagekeken")) 
	    	nagekeken = map.getBoolean("nagekeken");
	    editable = map.getBoolean("editable", true);
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
	    this.editable = editable;
	    basisPanel.setStyleDependentName("readonly", !editable);
        
        if(randomizePositions) 
        {   for(int i=0 ; i<ipList.length ; i++)
	        {   
	        	Point p = randomizedPositions[i];
	            ipList[i].zetLocatie(p.getX(), p.getY()); //niet meer nodig.
	        }
	        //(((TekstInteractiePanelVak)((Component)ipList[0]).getParent()).getTekstVak()).layoutTekst();
        	
        		if(randomSequence!=null && !positionsRandomized) {
//        			Vector parents = new Vector();
//        			Widget [] randomizedWidgets = new Widget[ipList.length];
//        			for(int i=0 ; i<ipList.length ; i++)
//        			{	TekstVak tv = (TekstVak)ipList[i].asWidget().getParent().getParent();
//        				ArrayList<Object> objects = tv.getOpdrachtObjects();
//        				parents.addElement(objects);
//        				objects.remove(ipList[i].asWidget());
//        			}
//        			for(int i=0 ; i<ipList.length ; i++)
//        			{	int r = randomSequence[i];
//        				Panel p = (Panel)(parents.elementAt(r));
//        				randomizedWidgets[i] = p;
//        				p.add(ipList[i].asWidget());
//        				parents.removeElementAt(r);
//        				//ipList[i].resize(); zou moeten, maar werkt niet ivm boekhouding
//        			}
        			
        				Vector parents = new Vector();
        				Vector parentsTemp = new Vector();
        				for(int i=0 ; i<ipList.length ; i++)
        				{	((TekstVakPanel)ipList[i]).setRandomPositioned(true);
        				}
        				boolean randomizable = true; //randomiseren gaat niet goed als objecten dezelfde parent hebben (en niet zwevend zijn)
        				if(ipList.length>1) {
        					TekstVak tv0 = (TekstVak)ipList[0].asWidget().getParent().getParent();
        					TekstVak tv1 = (TekstVak)ipList[1].asWidget().getParent().getParent();
        					if(tv0==tv1) {
        						randomizable = false;
        					}
        				}
        				for(int i=0 ; i<ipList.length && randomizable; i++)
        				{	TekstVak tv = (TekstVak)ipList[i].asWidget().getParent().getParent();
        					parents.addElement(tv);
        					parentsTemp.addElement(tv);
        					ArrayList<Object> objects = tv.getOpdrachtObjects();
        					objects.remove(ipList[i]);
        					Panel p = (Panel)ipList[i].asWidget().getParent();
        					tv.clear();
        				}
        				for(int i=0 ; i<ipList.length  && randomizable; i++)
        				{	int r = randomSequence[i];
        					TekstVak tv = (TekstVak)(parentsTemp.elementAt(r));
        					ArrayList<Object> objects = tv.getOpdrachtObjects();
        					objects.add(0,ipList[i]);
        					parentsTemp.removeElementAt(r);
        				}
        				for(int i=0 ; i<parents.size()  && randomizable; i++)
        				{	TekstVak tv = (TekstVak)parents.elementAt(i);
        					tv.reLayout();
        				}
        			
        			
        		}
        			
        }
        
        if(ingevuld && (mode == OpdrNavIF.OEFENEN 
        		|| mode == OpdrNavIF.OEFENEN_STRAFPUNTEN 
        		|| (nagekeken && !isVeranderdNaNakijken && (mode != OpdrNavIF.EINDTOETS || LessonMode.browse == comRoot.getLessonMode()))
        		||Review.isReview(comRoot)))
        {
        	kijkNa_intern();
        	if(feedbackPanel!=null)
        		feedbackPanel.hide();
        }
        correctie = CorrectieFacade.get(h, this, getScoreMax(), comRoot, dwologger, activity);
	}
	
	public void setAttempt()
	{
		if(dwologger != null && ingevuld) {
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
		if(correct)
			map.put("success", Boolean.TRUE);
		if(fout && !logString.isEmpty()) // bij fout en niet ingevuld: geen success
			map.put("success", Boolean.FALSE);
		return map;
	}
	
	public void wis()
	{
		//ipList = null;
		//juisteSelecties = null;
		nakijkAchtergrond.setVisible(false);
	    goedKrulImage.setVisible(false);
	    goedKrulHalfImage.setVisible(false);
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

	public void zetMode(int mode, LessonMode lessonMode)
	{
		this.mode = mode;
		//checkButton.setVisible(mode==0 || mode==1|| lessonMode == LessonMode.review);
		boolean visible = mode==0 || mode==1 || lessonMode == LessonMode.review;
		basisPanel.setWidgetVisible(checkButton, visible && check);
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
	
//	public void stop()
//	{
//		kijkNa_intern();
//		
//	}

//	public void start()
//	{
//	}
//
//	public void destroy()
//	{
//	}

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
		zetMode(comRoot.getMode(), comRoot.getLessonMode());
		if(dwologger != null) dwologger.setCommunicationRoot(comRoot);
		comRoot.addCBookEventListener("action.setNotEditable", this);
	}
	
	public CheckSelectieUnit(ActivityInterface a, HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)//, TekstVakPanel[] ipList)
	{
		this.activity = a;
//		if (h != null && h.containsKey("breedte"))
//			breedte = ((Number) h.get("breedte")).intValue();
//		if (h != null && h.containsKey("hoogte"))
//			hoogte = ((Number) h.get("hoogte")).intValue();
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
		breedte = width;// - 30;
		hoogte = height;
		//this.randomVarWaarden = randomValues;
		ObjectMap map = JSONUtilities.wrapMap(launchData);
		if (map != null)
		{
			if(map.containsKey("juisteSelecties"))
				juisteSelecties = map.getBooleanArray("juisteSelecties");
			if(map.containsKey("hasFeedback"))
				hasFeedback = map.getBoolean("hasFeedback");
			if(map.containsKey("answerModels"))
				answerModels = map.getMapList("answerModels");
			
			if(map.containsKey("scoreMax")) 
				scoreMax = map.getInt("scoreMax");
		    if(map.containsKey("randomizePositions")) 
		    	randomizePositions = map.getBoolean("randomizePositions");
		    if(map.containsKey("multiSelections")) 
		    	multiSelections = map.getBoolean("multiSelections");
		    if(map.containsKey("logOption")) 
		    	logOption = map.getBoolean("logOption");
			if(map.containsKey("logID") && logOption) 
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
			if(map.containsKey("logMisconceptions"))
			{
				ObjectList logMisconceptionsList = ( map.getObjectList("logMisconceptions"));
				logMisconceptions = new boolean[logMisconceptionsList.size()][][];
				for(int i = 0; i < logMisconceptionsList.size(); i++)
				{
					ObjectList logMisconceptionsList2 = logMisconceptionsList.getObjectList(i);
					try{
					logMisconceptions[i] = new boolean[logMisconceptionsList2.size()][];
					for(int j = 0; j < logMisconceptionsList2.size(); j++)
					{	logMisconceptions[i][j] = logMisconceptionsList2.getBooleanArray(j);
					}
					}
					catch(Exception e)
					{
					}
				}
			}
			if(map.containsKey("knopImageString")) 
				knopImageString = map.getString("knopImageString");
		}

		{
			LogBuilder builder = activity.logBuilder()
					.setLaunchData(map)
					.setClassName("fi.wiskopdr.CheckUnitPanel/" + getAantalSelectieObjecten());
			
			dwologger = builder.build();
		}

		
	}

	private void adviseMe() {
		Optional<DwoGlobalVars> instance = activity.vars();
		if (instance.isPresent() &&
			instance.get().withUser() && logOption && comRoot.getLessonMode() == LessonMode.normal) {
			String id = logID;
			if(! id.startsWith("adviseMe:")) 
				return;
			String[] split = id.split(":");
			String userid = instance.get().getUserID().toString();
			String classid;
			try {
				classid = instance.get().getCurrentSchoolClass().getId().getIdString();
			} catch (Exception e) {
				classid = "";
			}
			String exerciseid = split[1];
			String id2 = split[2];
			Map<String,String> context = new HashMap<>();
			context.put("userid", userid);
			context.put("groupid", classid);
			context.put("language", StubView.getLocale());
			RuleIF[] math = toMathML(id2, context);
			PromiseCallback<RuleIF> defer = new PromiseCallback<>();
			WiskOpdr.ideas.adviseMe(math, exerciseid, defer );
			activity.agent().addBarrier(defer.getPromise());
			Logger LOG = Logger.getLogger("TextEditor");
			defer.getPromise().onResolve(() -> { 
				Promise<RuleIF> p = defer.getPromise();
				Throwable t = p.getFailure();
				if ( t != null) {
					LOG.log(Level.SEVERE, "adviseMe", t);
				} else {
					RuleIF r = p.getValue();
					if ( r.isException()) {
						LOG.severe(r.getExpr());
					} else {
						LOG.info(r.getExpr());
					}
				}
			} );
		}
	}
	
	private RuleIF[] toMathML(String base, Map<String, String> context) {
		RuleIF[] result = new RuleIF[ipList.length];
		for(int i = 0; i < ipList.length; i++) {
			TekstVakPanel t = ipList[i];
			RuleIF r = t.getSelectRule(base, context);
			result[i] = r;
		}
		return result;
	}

	private void initialize(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		attempts = new Vector();
		
		basisPanel = new LayoutPanel();
		basisPanel.setStylePrimaryName("checkselectieunit");
		//basisPanel.setSize("" + breedte + "px", "" + hoogte + "px");
		
		//int imWidth = breedte - 30;
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
       		if(imWidth <= 0) 
				imWidth = breedte;
			if(imHeight <= 0) 
				imHeight = 20;
		}
		
		if(knopImage != null)
		{	checkButton = new SVGButton(knopImage);
			checkButton.getElement().getStyle().setPadding(0, Style.Unit.PX);
			checkButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		}
		else
		{	
			String backgroundColorString = (String)DWOplayer.templateConstants.checkButton("background-color");
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
		
		//breedte = imWidth + 30;
		breedte = imWidth;
		hoogte = imHeight + 5;
		ashoogte = hoogte / 2 + 7;
		basisPanel.setPixelSize(breedte ,  hoogte );
		basisPanel.add(checkButton);
		basisPanel.setWidgetLeftWidth(checkButton, 0, Style.Unit.PX, imWidth, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(checkButton, 5, Style.Unit.PX, imHeight, Style.Unit.PX);
		basisPanel.setWidgetVisible(checkButton, check);
//		checkButton.addClickHandler(new ClickHandler(){
//			public void onClick(ClickEvent e)
//			{
//				e.stopPropagation();
//				if(!editable) return;
//				kijkNa();
//	        	attemptsCount++;
//				setAttempt();
//				adviseMe();
//			}
//		});
		checkButton.addButtonListener(new ButtonListener() {
			@Override
			public void onClick(Object sender)
			{
				//e.stopPropagation();
				if(!editable) return;
				kijkNa();
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
		
		
		//TODO: Noordhoff-onderscheid maken (ook in plaatsing, alleen in Noordhoff in knop?)
		
		//goedKrulImage = new Image(FormuleHolder.FORMULE_BUNDLE.goedkrul_en().getSafeUri());
		//foutKruisImage = new Image(DWOplayer.DWO_BUNDLE.foutkruis().getSafeUri());
		goedKrulImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_groen().getSafeUri());
		foutKruisImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_kruisje_rood().getSafeUri());
		goedKrulHalfImage = new Image(FormuleHolder.FORMULE_BUNDLE.mw_vinkje_geel().getSafeUri());
		
		basisPanel.add(goedKrulImage);
		basisPanel.add(foutKruisImage);
		basisPanel.add(goedKrulHalfImage);
		//basisPanel.setWidgetLeftWidth(goedKrulImage, imWidth, Style.Unit.PX, 30, Style.Unit.PX);
		//basisPanel.setWidgetTopHeight(goedKrulImage, 0, Style.Unit.PX, imHeight + 5, Style.Unit.PX);
		//basisPanel.setWidgetLeftWidth(foutKruisImage, imWidth, Style.Unit.PX, 30, Style.Unit.PX);
		//basisPanel.setWidgetTopHeight(foutKruisImage, 0, Style.Unit.PX, imHeight + 5, Style.Unit.PX);
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
		
		if(XMLView.misconceptions != null && XMLView.misconceptions.length>0)
	     {	 possibleMisconceptions = new int[XMLView.misconceptions.length][];
	     	 measuredMisconceptions = new int[XMLView.misconceptions.length][];
		     for(int i=0 ; i<XMLView.misconceptions.length ; i++)
		     {	 possibleMisconceptions[i] = new int[XMLView.misconceptions[i].length];
		     	 measuredMisconceptions[i] = new int[XMLView.misconceptions[i].length];
		    	 for(int j=0 ; j<XMLView.misconceptions[i].length ; j++)
			     {  possibleMisconceptions[i][j] = 0;
			     	measuredMisconceptions[i][j] = 0;
			     }
		     }
	     }
		
		if(possibleMisconceptions != null && logMisconceptions!=null)
        {
        	for(int i=0 ; i<logMisconceptions.length ; i++)
        	{	for( int j=0 ; logMisconceptions[i]!=null && j<logMisconceptions[i].length && j<possibleMisconceptions.length ; j++)
        		{	for( int k=0 ; k<logMisconceptions[i][j].length && k<possibleMisconceptions[j].length; k++)
            		{	if(logMisconceptions[i][j][k])
	        				possibleMisconceptions[j][k] = 1;
            		}
        		}
        	}
        }
        
		
	}
	
	public void zetSelectieObjecten(TekstVakPanel[] selectieObjecten)
	{
		ipList = selectieObjecten;
		for(int i=0 ; i<ipList.length ; i++)
        {   if(ipList[i] != null)
            {	ipList[i].getAsPanel().addDomHandler(new MouseDownHandler(){
	    			public void onMouseDown(MouseDownEvent e){
	    				if (editable)
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
	    				if (editable)
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
        
        
	}
	
	public int getAantalSelectieObjecten()
	{
		return juisteSelecties.length;
	}

	void selectClickAction(int i)
	{
		if (nagekeken)
			zetIsVeranderdNaNakijken(true);

		nakijkAchtergrond.setVisible(false);
		goedKrulImage.setVisible(false);
		goedKrulHalfImage.setVisible(false);
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

	@Override
	public int[][] getMeasuredMisconceptions() {
		return measuredMisconceptions;
	}

	@Override
	public int[][] getPossibleMisconceptions() {
		return possibleMisconceptions;
	}

	@Override
	public void acceptCBookEvent(CBookEvent event) {
		if("action.setNotEditable".equals(event.getCommand())) {
			editable = false;
			basisPanel.setStyleDependentName("readonly", !editable);
		}
		
	}

	public class FeedbackPanel extends PopupPanel{
		
		private TekstVak feedbackTekst = new TekstVak();
		private String feedback = "";
		
		public FeedbackPanel(Panel vakPanel, String text) {
//			feedbackTekst.getElement().setInnerText(text);
//			feedbackTekst.getElement().getStyle().setColor(""+CssColor.make(49,71,112));
			feedback = text;
			
			feedbackTekst = new TekstVak();
			feedbackTekst.setSize(200, 50);
			feedbackTekst.setFontSize(XMLView.getDefaultFontSize());
			feedbackTekst.setFontName(XMLView.getDefaultFontName());
			feedbackTekst.setColor(CssColor.make("black"));
			feedbackTekst.setCentering(false, true);
			feedbackTekst.setPasHoogteBreedteAan(true, false);
			feedbackTekst.setTekstVakBreedte(190);
			add(feedbackTekst);
			
			zetFeedback();
			
			BerekeningVakButton closeButton = new BerekeningVakButton("sluit");
			closeButton.setSize(15, 15);
			closeButton.addButtonListener(new CloseButtonListener());
			closeButton.asWidget().getElement().getStyle().setTextAlign(TextAlign.RIGHT);
			
			VerticalPanel vp = new VerticalPanel();
			HorizontalPanel hp = new HorizontalPanel();
			hp.setWidth("100%");
			
//			LayoutPanel warning = new LayoutPanel();
//			warning.getElement().setInnerText("\u2757");
//			hp.add(warning);
			
			hp.add(closeButton);
			hp.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
			
			vp.add(hp);
			vp.add(feedbackTekst);
			
			getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			getElement().getStyle().setBorderColor(""+CssColor.make(38,115,182));
			getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
			getElement().getStyle().setPadding(5, Style.Unit.PX);
			getElement().getStyle().setBackgroundColor(""+CssColor.make(255,255,204));
			getElement().getStyle().setProperty("boxShadow", "3px 3px 3px #96A1BD");
			add(vp);
			setAutoHideEnabled(true);
			setWidth("180px");
			
			vakPanel.addAttachHandler(new AttachEvent.Handler() {
				@Override
				public void onAttachOrDetach(AttachEvent event) {
					hide();
				}
			});
		}
		
		public void zetFeedback()
		{
			TekstBuffer b = new TekstBuffer(activity);
			//Volgens mij zijn randomvariabelen feedback bij aanmaken antwoordmodel al ingevuld, dus hier weggelaten.
//			try{
//				feedback = FormuleParser.randomizeTekstVakString(feedback, randomVarNamen, randomVarWaarden);
//			}
//			catch(Exception e){}
			ArrayList<Object> feedbackList = b.convertTekst(feedback, null, false);
			feedbackTekst.clear();
			int tekstVakBreedte = 190;
			for(int i = 0; i < feedbackList.size(); i++)
			{
				Object object = feedbackList.get(i);
				if(object instanceof TekstElement && ((TekstElement) object).getWidth() > tekstVakBreedte)
					tekstVakBreedte = ((TekstElement) object).getWidth();
			}
			feedbackTekst.setSize(tekstVakBreedte + 10, 50);
			feedbackTekst.setTekstVakBreedte(tekstVakBreedte);
			feedbackTekst.setObjects(feedbackList);
			//voegFeedbackSluitKnopToe();
			feedbackTekst.resize();
			//zetFeedbackZichtbaar(true);
		}
		
		public void show(int x, int y) {
			setPopupPosition(x,y);
			super.show();
			setVisible(true);
		}
		
		public void hide() {
			super.hide();
		}
		
		private class CloseButtonListener implements ButtonListener {
			
			public void onClick(Object sender) {
				hide();
			}
		}

	}
}
