package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.samengesteldestappen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import fi.wiskopdr.text.Text;
import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.HasResize;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstRegel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel.Tupel;

public class SamengesteldeStappenPanel implements InteractionStub, FacetAware, TekstElementWithFont, HasResize {
	
	private final static Logger logger = Logger.getLogger("samengesteldeStappen");
	
	static final String holderId = "dockholder";
	private HashMap<String, Object> launchState; 
	private OpdrNavIF comRoot = null;
	
	int breedte = 400;
	int hoogte = 300; 
	private boolean volledigeBreedte = false;
	int choiceHeight = 30;
	
	int offset = 5;
    int labelWidth = 50;
    int labelHeight = 25;
    int buttonWidth = 20;
    int buttonHeight = 24;
	
	private FlowPanel mainPanel;
	private TekstVakPanel stappenVak;
	private StappenKeuzeVak keuzeVak;
	private TekstRegel parent;
	
	private FormuleKeyboardIF kb = null;
	
	private boolean ideasStatistiek = false;
	private int scoreMax = 10;
	private boolean[] stepRequired = null;
	private ArrayList<Integer> selectedSteps = new ArrayList<Integer>();
	
	Canvas hintButton = null;
	
	String[] randomVarNamen = null;
	HashMap<String, Number> randomVarWaarden = null;

	private ActivityComponent activity;
	
	
	public SamengesteldeStappenPanel(ActivityComponent a, HashMap<String, Object> h, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden, int volleBreedte)
	{
		this.activity = a;
		if (h != null && h.containsKey("breedte"))
			breedte = ((Number) h.get("breedte")).intValue();
		if (h != null && h.containsKey("hoogte"))
			hoogte = ((Number) h.get("hoogte")).intValue();
		if (h != null && h.containsKey("volledigeBreedte"))
			volledigeBreedte = ((Boolean) h.get("volledigeBreedte")).booleanValue();
		if (h != null && h.containsKey("interactiePanelLaunchState"))
			launchState = (HashMap<String, Object>) h.get("interactiePanelLaunchState");
		this.randomVarNamen = randomVarNamen;
		this.randomVarWaarden = randomVarWaarden;
		if(volledigeBreedte)
			breedte = volleBreedte;
		
		init(breedte, hoogte, launchState, randomVarWaarden);
		initialize(h);
	}
	
	@Override
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		breedte = width;
		hoogte = height;
		ObjectMap map = JSONUtilities.wrapMap(launchData);
		if (map != null)
		{
			if(map.containsKey("ideasStatistiek"))
				this.ideasStatistiek = map.getBoolean("ideasStatistiek");
			if(map.containsKey("scoreMax"))
				this.scoreMax = map.getInt("scoreMax");
			if(map.containsKey("stepRequired"))
				this.stepRequired = map.getBooleanArray("stepRequired");
		}
	}
	
	public void initialize(HashMap<String, Object> h)
	{
		// alle vakken op een panel zetten. 
		
		mainPanel = new FlowPanel();
		
		//Stappenvak initialiseren
		int nrOfSteps = 20;//later nog wat flexibeler?
	    stappenVak = makeStepsTVP(nrOfSteps);
	    
		mainPanel.add(stappenVak);
		Canvas[] stepNumbers = new Canvas[nrOfSteps];
		Context2d gIm;
		for(int i = 0; i < nrOfSteps; i++)
		{
			stepNumbers[i] = Canvas.createIfSupported();
			gIm = stepNumbers[i].getContext2d();
			
			stepNumbers[i].setWidth(labelWidth + "px");
			stepNumbers[i].setHeight(labelHeight + "px");
			stepNumbers[i].setCoordinateSpaceWidth(labelWidth);
			stepNumbers[i].setCoordinateSpaceHeight(labelHeight);
			
			gIm.setFillStyle(CssColor.make(220, 220, 220).toString());
			gIm.setLineWidth(1.0d);
			gIm.rect(0, 0, labelWidth, labelHeight);
			gIm.fill();
			gIm.setFillStyle("black");
			gIm.setFont("bold 14px Arial");
			gIm.fillText("" + (i + 1), i<10?23:18, 18);
			
			stappenVak.geefTekstVak(i, 0).add(stepNumbers[i]);
		}
		
		
		FlowPanel choiceLine = new FlowPanel();
		//choiceLine.getElement().getStyle().setBackgroundColor("seaGreen");
		
		Canvas actionBox = Canvas.createIfSupported();
		gIm = actionBox.getContext2d();
		
		actionBox.setWidth(labelWidth + "px");
		actionBox.setHeight(labelHeight + "px");
		actionBox.setCoordinateSpaceWidth(labelWidth);
		actionBox.setCoordinateSpaceHeight(labelHeight);
		
		gIm.setFillStyle(CssColor.make(220, 220, 220).toString());
		gIm.setLineWidth(1.0d);
		gIm.rect(0, 0, labelWidth, labelHeight);
		gIm.fill();
		gIm.setFillStyle("black");
		gIm.setFont("bold 14px Arial");
		gIm.fillText(Text.constants.actionLabel(), 5, 18);
		actionBox.getElement().getStyle().setProperty("display", "inline-block");
		choiceLine.add(actionBox);
		
		//KeuzeVak initialiseren
		HashMap<String, Object> keuzeVakMap = new HashMap<String, Object>();
		keuzeVakMap.put("breedte", breedte - buttonWidth - labelWidth - 2 * offset - (ideasStatistiek?(offset + buttonWidth):0)); 
		keuzeVakMap.put("height", buttonHeight);
		keuzeVakMap.put("launchState", launchState);
		keuzeVak = new StappenKeuzeVak(activity, keuzeVakMap, randomVarNamen, randomVarWaarden);
		keuzeVak.setParent(this);
		Widget kvWidget = keuzeVak.asWidget();
		kvWidget.getElement().getStyle().setProperty("display", "inline-block");
		kvWidget.getElement().getStyle().setMarginRight(offset - 1, Style.Unit.PX);
		kvWidget.getElement().getStyle().setMarginLeft(offset - 1, Style.Unit.PX);
		kvWidget.getElement().getStyle().setMarginTop(offset - 1, Style.Unit.PX);
		kvWidget.addDomHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) 
			{
				kvWidget.getElement().getStyle().setCursor(Cursor.POINTER);  
			}
		}, MouseOverEvent.getType());
		choiceLine.add(kvWidget);
		
		if(ideasStatistiek)
		{
			hintButton = Canvas.createIfSupported();
			gIm = hintButton.getContext2d();
			
			hintButton.setWidth(buttonWidth + "px");
			hintButton.setHeight(buttonHeight + "px");
			hintButton.setCoordinateSpaceWidth(buttonWidth);
			hintButton.setCoordinateSpaceHeight(buttonHeight);
			
			CanvasGradient gradient = gIm.createLinearGradient(0, 0, buttonWidth, buttonHeight);
			gradient.addColorStop(0, "white");
			gradient.addColorStop(1, CssColor.make(200, 200, 200).toString());
			gIm.setFillStyle(gradient);
			gIm.setStrokeStyle("black");
			gIm.setLineWidth(1.0d);
			gIm.rect(0, 0, buttonWidth, buttonHeight);
			gIm.fill();
			gIm.stroke();
			
			gIm.setFillStyle("black");
			gIm.setFont("bold 14px Arial");
			gIm.fillText("?", 5, 18);
			hintButton.getElement().getStyle().setProperty("display", "inline-block");
			hintButton.getElement().getStyle().setMarginRight(4, Style.Unit.PX);
			choiceLine.add(hintButton);
			hintButton.addDomHandler(new ClickHandler(){
				public void onClick(ClickEvent e)
				{
					stappenVak.getHintIdeasStatistiek();
				    resize();
				}
			}, ClickEvent.getType());

			hintButton.addDomHandler(new MouseOverHandler() {
				public void onMouseOver(MouseOverEvent event) 
				{
				hintButton.getElement().getStyle().setCursor(Cursor.POINTER);  
				}
			}, MouseOverEvent.getType());
		}
		
		Canvas backButton = Canvas.createIfSupported();
		gIm = backButton.getContext2d();
		
		backButton.setWidth(buttonWidth + "px");
		backButton.setHeight(buttonHeight + "px");
		backButton.setCoordinateSpaceWidth(buttonWidth);
		backButton.setCoordinateSpaceHeight(buttonHeight);
		
		CanvasGradient gradient = gIm.createLinearGradient(0, 0, buttonWidth, buttonHeight);
		gradient.addColorStop(0, "white");
		gradient.addColorStop(1, CssColor.make(200, 200, 200).toString());
		gIm.setFillStyle(gradient);
		gIm.setStrokeStyle("black");
		gIm.setLineWidth(1.0d);
		gIm.rect(0, 0, buttonWidth, buttonHeight);
		gIm.fill();
		gIm.stroke();
		
		gIm.setLineWidth(2.0d);
		gIm.beginPath();
		gIm.moveTo(6, 9);
		gIm.lineTo(13, 9);
		gIm.lineTo(13, 17);
		gIm.moveTo(8, 7);
		gIm.lineTo(6, 9);
		gIm.lineTo(8, 11);
		gIm.closePath();
		gIm.stroke();
		
		choiceLine.add(backButton);
		
		backButton.addDomHandler(new ClickHandler(){
			public void onClick(ClickEvent e)
			{
				stappenVak.removeFeedback();
				stappenVak.backAction();
				if(selectedSteps.size() > 0)
			        selectedSteps.remove(selectedSteps.size() - 1);
				resize();
				comRoot.setChanged(isCorrect() == null || !isCorrect().equals(Boolean.TRUE));
			}
		}, ClickEvent.getType());
		
		backButton.addDomHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) 
			{
			backButton.getElement().getStyle().setCursor(Cursor.POINTER);  
			}
		}, MouseOverEvent.getType());
		
		mainPanel.add(choiceLine);
		resize();
	}
	
	private TekstVakPanel makeStepsTVP(int number) 
    {
      HashMap<String,Object> ipLaunchState = new HashMap<String,Object>();
      
      ArrayList<ArrayList<String>> teksten = new ArrayList<ArrayList<String>>();
     
      ArrayList<Double> breedtes = new ArrayList<Double>();
      breedtes.add(52.0);
      breedtes.add((double) (breedte - 52));

      ArrayList<Double> hoogtes = new ArrayList<Double>();
      for(int i = 0; i < number; i++)
    	  hoogtes.add(27.0);
      
      ipLaunchState.put("teksten", teksten);
      ipLaunchState.put("pasAanH", new Boolean(true));
      ipLaunchState.put("cellSpaceRow", new Integer(6));
      ipLaunchState.put("breedtes", breedtes);
      ipLaunchState.put("hoogtes", hoogtes);
      ipLaunchState.put("ideasStatistiek", ideasStatistiek);
      
      HashMap<String, Object> launchData = new HashMap<String, Object>();
      launchData.put("soortInteractiePanel", new Integer(9));
      launchData.put("setNr", new Integer(3));
      launchData.put("interactiePanelLaunchState", ipLaunchState);
      launchData.put("breedte", new Integer(breedte));
      launchData.put("volledigeBreedte", volledigeBreedte);
      launchData.put("hoogte", new Integer(4));
      
      TekstVakPanel stappenVak = new TekstVakPanel(activity, launchData, randomVarNamen, randomVarWaarden, breedte);
      stappenVak.setKeyboard(kb);
      //stappenVak.setCommunicationRoot(comRoot); // gebeurt nu in setComRoot. 
      stappenVak.initialiseerStappen();
      //stappenVak.addActionListener(this);
      stappenVak.setParentStappen(this);
      return stappenVak;
    }
	
	private ArrayList<Tupel> getInitialStatistiekState(ObjectMap instellingen)
	{
		ArrayList<Tupel> initialState = new ArrayList<Tupel>();
		ArrayList<Object>[] stepContents = keuzeVak.getStepContents();
		for(int i = 0; i < stepContents.length; i++)
		{
			ArrayList<Object> contentMap = stepContents[i];
			TekstVakPanel tempVak = makeStepsTVP(20);
			tempVak.setCommunicationRoot(comRoot);
			tempVak.zetInstellingen(instellingen);
			
			initialState.addAll(tempVak.getAnswerModelsNew(contentMap));
			//initialState.addAll(stappenVak.getAnswerModels(contentMap));
		}
		return initialState;
	}
	
	public void resize()
	{
		hoogte = stappenVak.getHeight() + choiceHeight;
		mainPanel.setHeight(hoogte + "px");
		if(parent != null)
			parent.resize();
		
	}
	
	public void setParentRegel(TekstRegel parent)
	{
		this.parent = parent;
		stappenVak.setParent(parent.getTekstVak());
	}
	
	public void makeStep(ArrayList<Object> stepContents)
	{
		stappenVak.maakStapNieuw(stepContents, randomVarNamen, randomVarWaarden);
		//Alle elementen van de contentMapList één voor één aan het stappenvak toevoegen, in dezelfde stap. Per objectmap uitzoeken wat het is; tekst, een antwoordvak of een tekstvak? 
		//zit dat in de launchdata makkelijk bereikbaar?
		//Eerst even wachten hoe dit eruit gaat zien. 
	}
	
	public void makeStep(ObjectMap contentMap)
	{
		stappenVak.maakStap(contentMap, randomVarNamen, randomVarWaarden);
		resize(); 
	}
	
	public void addSelectedStep(int stepNr)
	{
	  selectedSteps.add(stepNr);
	}
	
	public void zetInstellingen(ObjectMap instellingen)
	{
		stappenVak.zetInstellingen(instellingen);
		if(ideasStatistiek)
	    {   ArrayList<Tupel> initialState = getInitialStatistiekState(instellingen);
	        stappenVak.initialiseIdeasStatistiek(initialState);
	    }
	}
	
	public void setKeyboard(FormuleKeyboardIF kb)
	{
		this.kb = kb;
	}

	@Override
	public HashMap<String, Object> getState() {
		
		HashMap<String, Object> state = stappenVak.getState();
		state.put("selectedSteps", selectedSteps);
		return state;
	}

	@Override
	public void setState(HashMap<String, Object> h) {
		ObjectMap map = JSONUtilities.wrapMap(h);
		if(map == null)
			return;
		int[] selectedStepsList = null;
		if(map.containsKey("selectedSteps"))
			selectedStepsList = map.getIntArray("selectedSteps");
		selectedSteps = new ArrayList<Integer>();
		for(int i = 0; i < selectedStepsList.length; i++)
			selectedSteps.add(selectedStepsList[i]);
		stappenVak.setState(h);
		resize();
	}

	@Override
	public int getScore() {
		if(isCorrect() != null && isCorrect().equals(Boolean.TRUE))
		      return scoreMax;
		return 0;
		//return stappenVak.getScore();
	}

	@Override
	public int[][] getScoreObjectives() {
		return null;
		//return stappenVak.getScoreObjectives();
	}

	@Override
	public Boolean isCorrect() {
		if(ideasStatistiek)
		{
			return(stappenVak.isCorrect()); 
		}
		
		if(stappenVak.isCorrect() == null)
			return null;
		//Requirement 1: stappenVak is correct
	    if(stappenVak.isCorrect() && stepRequired != null)
	    {
	      boolean correct = true;
	      //Requirement 2: all required steps are present
	      for(int i = 0; i < stepRequired.length; i++)
	      {
	        if(stepRequired[i])
	        {
	          if(!selectedSteps.contains(i))
	          {  correct = false;
	             break;
	          }
	        }
	      }
	      return correct;
	    }
	    return false;
	        //stappenVak.isCorrect();
	}

	@Override
	public void kijkNa() {
		stappenVak.kijkNa();
		if(ideasStatistiek && (comRoot.getMode() == OpdrNavIF.EINDTOETS || comRoot.getMode() == OpdrNavIF.ZELFTOETS))
			stappenVak.kijkNaIdeasStatistiek();
	}

	@Override
	public void zetNagekeken(boolean b) {
		stappenVak.zetNagekeken(b);
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		stappenVak.setCommunicationRoot(comRoot);
		if(ideasStatistiek && (comRoot.getMode() == OpdrNavIF.EINDTOETS || comRoot.getMode() == OpdrNavIF.ZELFTOETS))
			hintButton.removeFromParent();
		
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		//wordt al geregeld bij initialiseren
	}

	@Override
	public Widget asWidget() {
		return mainPanel;
	}

	@Override
	public int getAsHoogte() {
		//return 12;
		
		return stappenVak.getAsHoogte();
	}

	@Override
	public int getHeight() {
		return hoogte;
	}

	@Override
	public int getWidth() {
		return breedte;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		stappenVak.setAsHoogte(ashoogte);
	}

	@Override
	public void getResponses(List<String> responses) {
		// TODO Auto-generated method stub
		
	}

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
	
}