package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

//import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import fi.wiskopdr.text.Text_nl;
import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.Stub;
import nl.uu.fi.dwo.mobile.DWOplayer;

public class CheckSelectieUnit implements InteractionStub
{
	public static Text_nl rb = new Text_nl();
	static final String holderId = "dockholder";
	
	private HashMap<String, Object> launchState; 
	String[] randomVarNamen = null;
	HashMap randomVarWaarden = null;
	
	private LayoutPanel basisPanel;
	int breedte = 110;
	int hoogte = 24; 	
	
	private int mode;
	    
    private boolean ingevuld;
    private boolean nagekeken;
    
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
    
	static int GOED = 1;
	static int FOUT = 0;
	static int HALF = 2;
	static int GEEN = 3;
	
	private PushButton checkButton;
	private TekstVakPanel[] ipList; 
	private boolean[] juisteSelecties;
	
	Image goedKrulImage, foutKruisImage; //goedKrulHalfImage
	
	
	private boolean logOption;
	private String logID;
	
	private boolean[][] logObjectives;
	
	private boolean check;
	private boolean teltMee;
	

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
        			
        			stapJuist = v[h].isOplossing(new BasisExpressie(1.212131415),"q");
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
        {   goedKrulImage.setVisible(true);
            correct = true;
            fout = false;
            score = scoreMax;
        }
        else 
        {   foutKruisImage.setVisible(true);
            correct = false;
            fout = true;
            score = 0;
        }
        }
    
    public void kijkNa(int stapNr)
    { 	kijkNa();
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
				randomizedPositionsX[i] = (int) randomizedPositions[i].getX();
				randomizedPositionsY[i] = (int) randomizedPositions[i].getY();
			}
		}
		
		
		boolean ingevuld = false;
	    boolean nagekeken = false;
	    Vector attempts = new Vector();
	    int attemptsCount = 0;
		int errorCount = 0;
		
		ingevuld = this.ingevuld;
	    nagekeken = this.nagekeken;
	    attempts = this.attempts;
	    attemptsCount = this.attemptsCount;
	    errorCount = this.errorCount;

	    kijkNa(false);
		if(logOption)
		{	
	    	HashMap logMap = new HashMap<String, Object>();
			
	    	String logString = "";
			String[] options = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","X","Y","Z"};
			for(int i=0 ; i<ipList.length ; i++)
	        {   if(ipList[i].isIpSelected() && i<options.length) logString = logString + options[i];
	        }
			logMap.put("logAnswer", logString);
			logMap.put("logScore", new Integer(score));
			logMap.put("logMaxScore", new Integer(scoreMax));
			logMap.put("logErrorCount", new Integer(errorCount));
			logMap.put("logAttemptsCount", new Integer(attemptsCount));
			logMap.put("logAttempts", attempts);
			
			//WiskOpdr.setLog(logID, logMap);
		}
         
	    HashMap<String, Object> h = new HashMap<String, Object>();
        if(randomizedPositionsX != null) 
        	h.put("randomizedPositionsX", randomizedPositionsX);
        if(randomizedPositionsY != null) 
        	h.put("randomizedPositionsY", randomizedPositionsY);
        
        h.put("ingevuld", new Boolean(ingevuld));
        h.put("nagekeken", new Boolean(nagekeken));
        h.put("attempts", attempts);
        h.put("attemptsCount", new Integer(attemptsCount));
        h.put("errorCount", new Integer(errorCount));
        return h;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		Point[] randomizedPositions = null;
	    boolean ingevuld = false;
	    boolean nagekeken = false;
	    Vector attempts = new Vector();
	    int attemptsCount = 0;
		int errorCount = 0;
        
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
	    		randomizedPositions[i] = new Point(((Integer)randomizedPositionsXList.get(i)).intValue(), 
	    				((Integer)randomizedPositionsYList.get(i)).intValue());
	    }
	    if(h.get("ingevuld") != null) 
	    	ingevuld = ((Boolean)h.get("ingevuld")).booleanValue();
	    if(h.get("nagekeken") != null) 
	    	nagekeken = ((Boolean)h.get("nagekeken")).booleanValue();
	    if(h.get("attempts") != null)
	    	attempts = (Vector)h.get("attempts");
	    if(h.get("attemptsCount") != null) 
	    	attemptsCount = ((Number)h.get("attemptsCount")).intValue();
	    if(h.get("errorCount") != null) 
	    	errorCount = ((Number)h.get("errorCount")).intValue();
        
        this.randomizedPositions = randomizedPositions;
        this.ingevuld = ingevuld;
        this.nagekeken = nagekeken;
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
        
        if(ingevuld && (mode==0 || nagekeken)){
        	kijkNa();
        }
	}
	
	public void setAttempt()
	{
		String goedFout = "";
		if(goedKrulImage.isVisible())
			goedFout = "goed";
		//else if(goedKrulHalfImage.isVisible())
		//	goedFout = "half";
		else if(foutKruisImage.isVisible())
			goedFout = "fout";
		
		String logString = "";
		String[] options = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","X","Y","Z"};
		for(int i=0 ; i<ipList.length ; i++)
        {   //ipList[i] = parent.zoekTekstVakPanel(i+1);
            if(ipList[i] != null && ipList[i].isIpSelected() && i<options.length) 
            	logString = logString + options[i];
        }
		
		String s = logString;
		s = s + "   ;   ";
		s = s + goedFout;
		s = s + "   ;   ";
		s = s + "score = " + score;
		s = s + "   ;   ";
		s = s + new Date().toString();
		

		attempts.addElement(s);
		System.out.println(s);
	}
	
	public void wis()
	{
		//ipList = null;
		juisteSelecties = null;
		
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

	public boolean isCorrect()
	{
		if (!teltMee)
			return true;
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
		// TODO Auto-generated method stub
		
	}
	
	public CheckSelectieUnit(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden, TekstVakPanel[] ipList)
	{
		
		if (h != null && h.get("breedte") != null)
			breedte = ((Number) h.get("breedte")).intValue();
		if (h != null && h.get("hoogte") != null)
			hoogte = ((Number) h.get("hoogte")).intValue();
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		
		//this.parent = parent;
		
		init(breedte, hoogte, launchState, randomVarWaarden);
		this.ipList = ipList;
		
		initialize(h, randomVarNamen, randomVarWaarden);
	}

	@Override
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		breedte = width;
		hoogte = height;
		//this.randomVarWaarden = randomValues;

		if (launchData != null)
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
			if(launchData.get("juisteSelecties") != null)
			{	List<Object> juisteSelectiesList = JSONUtilities.toArrayList(launchData.get("juisteSelecties"));
				juisteSelecties = new boolean[juisteSelectiesList.size()];
				for(int i = 0; i < juisteSelectiesList.size(); i++)
					juisteSelecties[i] = ((Boolean) juisteSelectiesList.get(i)).booleanValue();
			}
			if(launchData.get("scoreMax") != null) 
				scoreMax = ((Number)launchData.get("scoreMax")).intValue();
		    if(launchData.get("randomizePositions") != null) 
		    	randomizePositions = ((Boolean)launchData.get("randomizePositions")).booleanValue();
		    if(launchData.get("multiSelections") != null) 
		    	multiSelections = ((Boolean)launchData.get("multiSelections")).booleanValue();
		    if(launchData.get("logOption") != null) 
		    	logOption = ((Boolean)launchData.get("logOption")).booleanValue();
			if(launchData.get("logID") != null) 
				logID = (String)launchData.get("logID");
			if(launchData.get("check") != null) 
				check = ((Boolean)launchData.get("check")).booleanValue();
			if(launchData.get("teltMee") != null) 
				teltMee = ((Boolean)launchData.get("teltMee")).booleanValue();
			if(launchData.get("checkFormule") != null) 
				checkFormule = ((Boolean)launchData.get("checkFormule")).booleanValue();
			
			if (launchData.get("formuleStrings") != null) {
				formuleStrings = JSONUtilities.toStringArray(launchData.get("formuleStrings"));
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
		}
	}
	
	private void initialize(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		attempts = new Vector();
		
		basisPanel = new LayoutPanel();
		basisPanel.setSize("" + breedte + "px", "" + hoogte + "px");
		
		checkButton = new PushButton(rb.getString("klaarKnopLabel"));
		basisPanel.add(checkButton);
		basisPanel.setWidgetLeftWidth(checkButton, 0, Style.Unit.PX, breedte - 20, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(checkButton, 0, Style.Unit.PX, 20, Style.Unit.PX);
		checkButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{	e.stopPropagation();
				kijkNa();
	        	if(fout) errorCount++;
	        	attemptsCount++;
				setAttempt();
			}
		});
		
		goedKrulImage = new Image(FormuleHolder.FORMULE_BUNDLE.goedkrul_en());
		foutKruisImage = new Image(DWOplayer.DWO_BUNDLE.foutkruis());
		
		basisPanel.add(goedKrulImage);
		basisPanel.add(foutKruisImage);
		basisPanel.setWidgetLeftWidth(goedKrulImage, breedte - 20, Style.Unit.PX, 20, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(goedKrulImage, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		basisPanel.setWidgetLeftWidth(foutKruisImage, breedte - 20, Style.Unit.PX, 20, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(foutKruisImage, 0, Style.Unit.PX, hoogte, Style.Unit.PX);
		goedKrulImage.setVisible(false);
		foutKruisImage.setVisible(false);
		
				
		for(int i=0 ; formuleStrings!=null && i<formuleStrings.length ; i++)
        {	try{
				formuleStrings[i] = FormuleParser.randomizeString(formuleStrings[i], randomVarNamen, randomVarWaarden);
	    	}
	    	catch(Exception e){	}
        }
		
		//ipList = new TekstVakPanel[juisteSelecties.length];
        for(int i=0 ; i<ipList.length ; i++)
        {   //Widget panel = basisPanel.getParent();
        	//ipList[i] = parent.zoekTekstVakPanel(i+1);
            if(ipList[i] != null)
            {	ipList[i].getAsPanel().addDomHandler(new MouseDownHandler(){
	    			public void onMouseDown(MouseDownEvent e){
	    				for(int i = 0; i < ipList.length; i++)
	    				{	if(e.getSource() == ipList[i].getAsPanel())
	    				
	    					{	selectClickAction(i);
	    						/*
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
	    						*/
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
	    						/*	
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
	    						*/
	    						break;
	    					}
	    				}
	    			}
	    		}, TouchStartEvent.getType());
            }
        }
        
        if(randomizePositions && !positionsRandomized) randomizePositions();
		
	}

	public void selectClickAction(int i)
	{
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

}
