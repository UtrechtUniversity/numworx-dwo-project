package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews;

//import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nl.uu.fi.dwo.formule.client.formuleholder.FormuleHolder;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.views.ImageView;
import nl.uu.fi.dwo.mobile.utils.StringUtils;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Algebra;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.expressies.VergelijkingMeerv;
import fi.wiskopdr.text.Text_nl;


public class CheckValueUnit implements InteractionStub{

	public static Text_nl rb = new Text_nl();
	static final String holderId = "dockholder";
	OpdrNavIF comRoot;
	
	private HashMap<String, Object> launchState; 
	
	private LayoutPanel basisPanel;
	int breedte = 110;
	int hoogte = 24; 
	int ashoogte = hoogte / 2;
	
	private int mode;
    
    private boolean ingevuld;
    private boolean nagekeken;
    
    private boolean correct;
    private boolean fout;
    
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
	
	private PushButton checkButton;
	private String knopImageString = "";
	private TekstVakPanel[] ipValueList;
	
	private int aantalValueObjects;
	
	private FlowPanel nakijkAchtergrond;
	private Image goedKrulImage, foutKruisImage;//goedKrulHalfImage
	
	private boolean logOption;
	private String logID;
	
	private boolean[][] logObjectives;
	
	private boolean check = true;
	private boolean teltMee = true;
	
	private String answer = "";
	private boolean view = false;
	
	public CheckValueUnit(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)//, TekstVakPanel[] ipValueList)
	{
		
		if (h != null && h.get("breedte") != null)
			breedte = ((Number) h.get("breedte")).intValue();
		if (h != null && h.get("hoogte") != null)
			hoogte = ((Number) h.get("hoogte")).intValue();
		if (h != null && h.get("interactiePanelLaunchState") != null)
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		
		//this.parent = parent;
		
		init(breedte, hoogte, launchState, randomVarWaarden);
		this.ipValueList = ipValueList;
		
		initialize(h, randomVarNamen, randomVarWaarden);
	}

	@Override
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		breedte = width - 30;
		hoogte = height;
		//this.randomVarWaarden = randomValues;

		if (launchData != null)
		{
			if(launchData.get("scoreMax") != null) 
				scoreMax = ((Number)launchData.get("scoreMax")).intValue();
		    if(launchData.get("logOption") != null) 
		    	logOption = ((Boolean)launchData.get("logOption")).booleanValue();
			if(launchData.get("logID") != null) 
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
		}
	}
	
	private void initialize(HashMap<String, Object> h, String[] randomVarNamen, HashMap randomVarWaarden)
	{
		attempts = new Vector();
		
		basisPanel = new LayoutPanel();
		//basisPanel.setSize("" + breedte + "px", "" + hoogte + "px");
		
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
       		if(imWidth <= 0) imWidth = breedte;
			if(imHeight <= 0) imHeight = 20;
			//checkButton.setSize(imWidth,imHeight);
			//zetMaat();
	    }
		if(knopImage != null)
		{	checkButton = new PushButton(knopImage);
			checkButton.getElement().getStyle().setPadding(0, Style.Unit.PX);
			checkButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		}
		else
		{	checkButton = new PushButton(rb.getString("klaarKnopLabel"));
			checkButton.getElement().getStyle().setFontSize(12, Style.Unit.PX);
			checkButton.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		}
		breedte = imWidth;
		hoogte = imHeight + 5;
		ashoogte = hoogte / 2 + 7;
		basisPanel.setSize("" + breedte + "px", "" + hoogte + "px");
		basisPanel.add(checkButton);
		basisPanel.setWidgetLeftWidth(checkButton, 0, Style.Unit.PX, imWidth, Style.Unit.PX);
		basisPanel.setWidgetTopHeight(checkButton, 5, Style.Unit.PX, imHeight, Style.Unit.PX);
		checkButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{	e.stopPropagation();
				kijkNa();
				//if(fout) errorCount++;
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
	
	public void zetWaardeObjecten(TekstVakPanel[] waardeObjecten)
	{
		ipValueList = waardeObjecten;
		 for(int i=0 ; i<ipValueList.length ; i++)
        {  if(ipValueList[i] != null)
            {	ipValueList[i].getAsPanel().addDomHandler(new MouseDownHandler(){
	    			public void onMouseDown(MouseDownEvent e){
	    				for(int i = 0; i < ipValueList.length; i++)
	    				{	if(e.getSource() == ipValueList[i].getAsPanel())
	    				
	    					{	nakijkAchtergrond.setVisible(false);
	    						goedKrulImage.setVisible(false);
	    						//goedKrulHalfImage.setVisible(false);
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
	    boolean ingevuld = false;
	    boolean nagekeken = false;
	    Vector attempts = new Vector();
	    int attemptsCount = 0;
		int errorCount = 0;
       
	    if(h.get("ingevuld") != null) 
	    	ingevuld = ((Boolean)h.get("ingevuld")).booleanValue();
	    if(h.get("nagekeken") != null) 
	    	nagekeken = ((Boolean)h.get("nagekeken")).booleanValue();
	    if(h.get("attempts") != null)
	    	attempts = new Vector(JSONUtilities.toArrayList(h.get("attempts")));
	    if(h.get("attemptsCount") != null) 
	    	attemptsCount = ((Number)h.get("attemptsCount")).intValue();
	    if(h.get("errorCount") != null) 
	    	errorCount = ((Number)h.get("errorCount")).intValue();
	    
        this.ingevuld = ingevuld;
        this.nagekeken = nagekeken;
        this.attempts = attempts;
        this.attemptsCount = attemptsCount;
	    this.errorCount = errorCount;
        
        if(ingevuld && (mode==0 || mode==1 || nagekeken)) kijkNa();
	}
	
	public HashMap<String, Object> getState()
	{   
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

	    //if(!("MW".equals(WiskOpdr.deployVariant) || "GR".equals(WiskOpdr.deployVariant))) 
	    kijkNa(false);
		if(logOption)
		{	
	    	HashMap<String, Object> logMap = new HashMap<String, Object>();
			
	    	String logString = answer;
			logMap.put("logAnswer", logString);
			logMap.put("logScore", new Integer(score));
			logMap.put("logMaxScore", new Integer(scoreMax));
			logMap.put("logErrorCount", new Integer(errorCount));
			logMap.put("logAttemptsCount", new Integer(attemptsCount));
			logMap.put("logAttempts", attempts);
			
			//WiskOpdr.setLog(logID, logMap);
		}
         
	    HashMap<String, Object> h = new HashMap<String, Object>();
        h.put("ingevuld", new Boolean(ingevuld));
        h.put("nagekeken", new Boolean(nagekeken));
        h.put("attempts", attempts);
        h.put("attemptsCount", new Integer(attemptsCount));
        h.put("errorCount", new Integer(errorCount));
        
        return h;
	}
	
	public void setAttempt()
	{
		String goedFout = "";
		if(goedKrulImage.isVisible())
			goedFout = "goed";
		if(foutKruisImage.isVisible())
			goedFout = "fout";
		
		String logString = "";
		//String[] options = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","X","Y","Z"};
		//for(int i=0 ; i<ipList.length ; i++)
		// {   ipList[i] = ((TekstInteractiePanelVak)getParent()).zoekInteractiePanel(i+1);
        //    if(((TekstVakPanel)ipList[i]).isIpSelected() && i<options.length) logString = logString + options[i];
        //}
		
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
	
	public void zetMode(int mode)
    {   this.mode = mode;
    	checkButton.setVisible(mode==0 || mode==1);
    }
	
	public void zetNagekeken(boolean b)
	{	if(ingevuld) nagekeken = b;
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
		zetMode(comRoot.getMode());
	}
	
	@Override
	public Widget asWidget() {
		return basisPanel;
	}
    
    public void kijkNa()
    {
    	kijkNa(true);
    }
    
    public void kijkNa(boolean show)
    {	nakijkAchtergrond.setVisible(false);
		goedKrulImage.setVisible(false);
        //goedKrulHalfImage.setVisible(false);
        foutKruisImage.setVisible(false);
        
        boolean juist = true;
        ingevuld = false;
        answer = "";
        
        correct = false;
        fout = true;
        score = 0;
        
        //ipValueList = new InteractiePanel[aantalValueObjects];
        //for(int i=0 ; i<ipValueList.length ; i++)
       // {   //ipValueList[i] = ((TekstInteractiePanelVak)getParent()).zoekInteractiePanel(i+1);
            //ipValueList[i].addActionListener(this);
        //}
        
        if(checkSamen)
        {
        	if(formuleStrings!=null)
        	{
        		
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
        				
        			}
        				
        			boolean stapJuist = true;
        			v[h] = FormuleParser.parseVergelijking(formuleString);
        			for(int i=0 ; i<aantalValueObjects ; i++)
    		        {   
    	        		Expressie e = ipValueList[i].geefObjectWaarde();
    	        		if(e!=null) 
    	        		{	ingevuld = true;
    	        			v[h] = v[h].substitueer(e, "V?("+(i+1)+")");
    	        		}
    	        		else if (ipValueList[i].objectNullWaarde())
    	        		{	
    	        		}
    	        		else 
    	        		{	stapJuist = false;
    	        			break;
    	        		}
    		        }
        			
        			String[][] tekenParen = {{"<","<"},{"<","\u2264"},{"\u2264","<"},{"\u2264","\u2264"},{">",">"},{"\u2265",">"},{">","\u2265"},{"\u2265","\u2265"}};
        			
        			boolean[] stappenJuist = new boolean[v[h].geefAantal()];
        			for(int k=0 ; k<stappenJuist.length ; k++)
        			{
        				stappenJuist[k] = false;
        				if(v[h].geefVergelijking(k).geefVergTeken().equals(">") 
        						|| v[h].geefVergelijking(k).geefVergTeken().equals("<")
        						|| v[h].geefVergelijking(k).geefVergTeken().equals("\u2265") //groter dan of gelijk aan
        						|| v[h].geefVergelijking(k).geefVergTeken().equals("\u2264")
        						|| v[h].geefVergelijking(k).geefVergTeken().equals("~")) //kleiner dan of gelijk aan
            			{	Expressie expL = v[h].geefVergelijking(k).geefExpLinks();
            				Expressie expR = v[h].geefVergelijking(k).geefExpRechts();
            				if(expL.isWaarde() && expR.isWaarde() && v[h].geefVergelijking(k).geefVergTeken().equals("<"))
            					stappenJuist[k] = expL.geefWaarde() < expR.geefWaarde()-0.000000001;
            				else if(expL.isWaarde() && expR.isWaarde() && v[h].geefVergelijking(k).geefVergTeken().equals(">"))
            					stappenJuist[k] = expL.geefWaarde() > expR.geefWaarde()+0.000000001;
            				else if(expL.isWaarde() && expR.isWaarde() && v[h].geefVergelijking(k).geefVergTeken().equals("\u2264"))
            					stappenJuist[k] = expL.geefWaarde() < expR.geefWaarde()+0.000000001;
            				else if(expL.isWaarde() && expR.isWaarde() && v[h].geefVergelijking(k).geefVergTeken().equals("\u2265"))
            					stappenJuist[k] = expL.geefWaarde() > expR.geefWaarde()-0.000000001;
            				else if(v[h].geefVergelijking(k).geefVergTeken().equals("~"))
            				{	Expressie e1 = expR.kind2.kind1;
            					Expressie e2 = expL;
            					Expressie e3 = expR.kind2.kind2;
            					if(e1.isWaarde() && e2.isWaarde() && e3.isWaarde())
            					{
            						if(Algebra.isGelijkDouble(expR.kind1.geefWaarde(), 0)) //{"<","<"}
            							stappenJuist[k] = e1.geefWaarde() < e2.geefWaarde()-0.000000001 && e2.geefWaarde() < e3.geefWaarde()-0.000000001;
            						else if(Algebra.isGelijkDouble(expR.kind1.geefWaarde(), 1)) //{"<","\u2264"}
            							stappenJuist[k] = e1.geefWaarde() < e2.geefWaarde()-0.000000001 && e2.geefWaarde() < e3.geefWaarde()+0.000000001;
            						else if(Algebra.isGelijkDouble(expR.kind1.geefWaarde(), 2)) //{"\u2264","<"}
            							stappenJuist[k] = e1.geefWaarde() < e2.geefWaarde()+0.000000001 && e2.geefWaarde() < e3.geefWaarde()-0.000000001;
            						else if(Algebra.isGelijkDouble(expR.kind1.geefWaarde(), 3)) //{"\u2264","\u2264"}
            							stappenJuist[k] = e1.geefWaarde() < e2.geefWaarde()+0.000000001 && e2.geefWaarde() < e3.geefWaarde()+0.000000001;
            						else if(Algebra.isGelijkDouble(expR.kind1.geefWaarde(), 4)) //{">",">"}
            							stappenJuist[k] = e1.geefWaarde() > e2.geefWaarde()+0.000000001 && e2.geefWaarde() > e3.geefWaarde()+0.000000001;
            						else if(Algebra.isGelijkDouble(expR.kind1.geefWaarde(), 5)) //{"\u2265",">"}
            							stappenJuist[k] = e1.geefWaarde() > e2.geefWaarde()-0.000000001 && e2.geefWaarde() > e3.geefWaarde()+0.000000001;
            						else if(Algebra.isGelijkDouble(expR.kind1.geefWaarde(), 6)) //{">","\u2265"}
            							stappenJuist[k] = e1.geefWaarde() > e2.geefWaarde()+0.000000001 && e2.geefWaarde() > e3.geefWaarde()-0.000000001;
            						else if(Algebra.isGelijkDouble(expR.kind1.geefWaarde(), 7)) //{"\u2265","\u2265"}
            							stappenJuist[k] = e1.geefWaarde() > e2.geefWaarde()-0.000000001 && e2.geefWaarde() > e3.geefWaarde()-0.000000001;
            					}
            				}
                				
            			}
            			else stappenJuist[k] = v[h].geefVergelijking(k).isOplossing(new BasisExpressie(1.212131415),"q");
        				
        				if(k==0)
        					stapJuist = stappenJuist[k];
        				else
        					stapJuist = stapJuist || stappenJuist[k];
        			}
        			
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
        			if(!juist && locationStrings==null) break;
        			
        			if(locationStrings!=null){
        				for(int i=0 ; i<locationStrings.length ; i++){
            				int location = Integer.parseInt(locationStrings[i].trim());
            				(ipValueList[location-1]).zetGoedFout(stapJuist);
            			}
        			}
        			
		        }
        	}
        	else
        	{	VergelijkingMeerv v = FormuleParser.parseVergelijking(formuleString);
	        	for(int i=0 ; i<aantalValueObjects ; i++)
		        {   
	        		Expressie e = (ipValueList[i]).geefObjectWaarde();
	        		if(e!=null) 
	        		{	ingevuld = true;
	        			v = v.substitueer(e, "V?("+(i+1)+")");
	        		}
	        		else 
	        		{	juist = false;
	        			break;
	        		}
		        }
	        	juist = v.isOplossing(new BasisExpressie(1.212131415),"q");
	        }
        }
        else
        {
        	for(int i=0 ; i<aantalValueObjects ; i++)
	        {   //changed opvragen en straks weer terugzetten; wordt altijd op false gezet door kijkNa in ipobjectIsCorrect.
        		boolean changed = ipValueList[i].ipObjectIsChanged();
        		boolean stapJuist = ipValueList[i].ipObjectIsCorrect();
	        	ingevuld = ingevuld || ipValueList[i].ipObjectIsIngevuld();
	        	juist = juist && stapJuist;
	        	if(view)ipValueList[i].zetGoedFout(stapJuist);
	        	ipValueList[i].setChanged(changed);
		    }
	    }
        if(juist)
        {   
            correct = true;
            fout = false;
            score = scoreMax;
            if(mode == 1)
            	score = Math.max(0, scoreMax - errorCount * foutStraf);
        }
        else 
        {   
            correct = false;
            fout = true;
            verhoogErrorCount();
            score = 0;
        }
        if(show && check)
        {	if (ingevuld)
				comRoot.setChanged(teltMee && !juist);
        	nakijkAchtergrond.setVisible(true);
			if(correct)
        		goedKrulImage.setVisible(true);
        	else
        		foutKruisImage.setVisible(true);
        }
			
        
        //if(show || mode==0 || mode==1)produceAction("changed");
    }
    
    public void verhoogErrorCount()
    {
    	boolean changed = false;
    	for(int i=0 ; i<aantalValueObjects ; i++)
        {   
    		if(ipValueList[i].ipObjectIsChanged())
    			changed = true;
        }
    	if(changed)
		{
			errorCount++;
		}
    	for(int i = 0; i < aantalValueObjects; i++)
    		ipValueList[i].setChanged(false);
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
}
