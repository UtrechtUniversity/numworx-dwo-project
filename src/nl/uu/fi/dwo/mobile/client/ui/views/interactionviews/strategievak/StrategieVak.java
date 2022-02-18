package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.strategievak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.FormuleKeyboardIF;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.HasResize;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstRegel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVak;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVakPanel;

public class StrategieVak implements InteractionStub, FacetAware, TekstElementWithFont, HasResize {
	
	private final static Logger logger = Logger.getLogger("StrategieVak");
	
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
	TekstVakPanel stappenVak;
	private StappenKeuzeVak keuzeVak;
	private TekstRegel parent;
	
	private FormuleKeyboardIF kb = null;
	
	private int scoreMax = 10;
	private boolean[] stepRequired = null;
	private ArrayList<Integer> selectedSteps = new ArrayList<Integer>();
		
	String[] randomVarNamen = null;
	HashMap<String, Number> randomVarWaarden = null;

	private ActivityComponent activity;
	
	
	public StrategieVak(ActivityComponent a, HashMap<String, Object> h, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden, int volleBreedte)
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
			if(map.containsKey("scoreMax"))
				this.scoreMax = map.getInt("scoreMax");
			if(map.containsKey("stepRequired"))
				this.stepRequired = map.getBooleanArray("stepRequired");
		}
	}
	
	class SwapStap implements ClickHandler {
	  private Widget w;

    SwapStap(Widget w) { this.w = w; }

    @Override
    public void onClick(ClickEvent event) {
      int nr;
      Widget vak = w.getParent();
      nr = stappenVak.getRowOf(vak);
      moveKeuzeVak(nr);     
    }
	  
	}
	
	
	class RemoveStap implements ClickHandler {
	  RemoveStap(Widget w) { this.w = w;}
	  Widget w;
 
	  @Override
      public void onClick(ClickEvent event) {
	    int nr;
	    Widget vak = w.getParent();
        nr = stappenVak.getRowOf(vak);
        logger.info("remove " + nr);
        stappenVak.removeFeedback();
        stappenVak.backStep(nr);
        stappenVak.backStep(nr);
        if(selectedSteps.size() > 0)
            selectedSteps.remove(nr/2);
        resize();
        comRoot.setChanged(isCorrect() == null || !isCorrect().equals(Boolean.TRUE));
        
        nr = stappenVak.getRowOf(keuzeVak.asWidget().getParent());
        if (nr < 0 || nr > selectedSteps.size() * 2) {
          moveKeuzeVak(selectedSteps.size()*2);
        }
        
     }
	  
	}
	public void initialize(HashMap<String, Object> h)
	{
		// alle vakken op een panel zetten. 
		
		mainPanel = new FlowPanel();
		
		//Stappenvak initialiseren
		int nrOfSteps = 21;//later nog wat flexibeler?
	    stappenVak = makeStepsTVP(nrOfSteps);
	    
		mainPanel.add(stappenVak);
        ImageResource v = DWOplayer.DWO_BUNDLE.verwijder();
        String css = DWOplayer.DWO_BUNDLE.dwoplayercss().removeBtn();
		for(int i = 1; i < nrOfSteps; i += 2)
		{
		  Image img = new Image(v);
		  img.setStylePrimaryName(css);
          img.addClickHandler(new RemoveStap(img));
          TekstVak vak = stappenVak.geefTekstVak(i, 2);
          vak.add(img); 
          vak.setSize(labelWidth, labelHeight);
		}
		for(int i = 0; i < nrOfSteps; i += 2)
        {
            
            Label stepNumbersi = new Label("+");
            stepNumbersi.addClickHandler(new SwapStap(stepNumbersi));
            //stepNumbersi.setPixelSize(labelWidth-12 ,labelHeight-8); // pushbutton marge = 12x8
            stappenVak.geefTekstVak(i, 0).add(stepNumbersi);
            stappenVak.geefTekstVak(i, 0).setPasHoogteBreedteAan(false, false);
            stappenVak.geefTekstVak(i, 1).setPasHoogteBreedteAan(false, false);
            stappenVak.geefTekstVak(i, 1).getElement().getStyle().setOverflow(Style.Overflow.VISIBLE);                      
            stappenVak.geefTekstVak(i, 2).setPasHoogteBreedteAan(false, false);
        }
		
		//KeuzeVak initialiseren
		HashMap<String, Object> keuzeVakMap = new HashMap<String, Object>();
		keuzeVakMap.put("breedte", breedte - 10); 
		keuzeVakMap.put("height", buttonHeight);
		keuzeVakMap.put("launchState", launchState);
		keuzeVak = new StappenKeuzeVak(activity, keuzeVakMap, randomVarNamen, randomVarWaarden);
		keuzeVak.setParent(this);
		
        makeStep(new ArrayList<>());
		stappenVak.geefTekstVak(0, 1).add(keuzeVak);
		resize();
	}
	
	private TekstVakPanel makeStepsTVP(int number) 
    {
      HashMap<String,Object> ipLaunchState = new HashMap<String,Object>();
      
      ArrayList<ArrayList<String>> teksten = new ArrayList<ArrayList<String>>();
     
      ArrayList<Double> breedtes = new ArrayList<Double>();
      breedtes.add(10.0);
      breedtes.add(breedte - 52.0 - 10.0);
      breedtes.add(52.0);
      
      ArrayList<Double> hoogtes = new ArrayList<Double>();
      for(int i = 0; i < number; i++)
    	  hoogtes.add(Double.valueOf(plusHoogte));
      
      ipLaunchState.put("teksten", teksten);
      ipLaunchState.put("pasAanH", Boolean.valueOf(true));
      ipLaunchState.put("cellSpaceRow", Integer.valueOf(0));
      ipLaunchState.put("breedtes", breedtes);
      ipLaunchState.put("hoogtes", hoogtes);
      
      HashMap<String, Object> launchData = new HashMap<String, Object>();
      launchData.put("soortInteractiePanel", Integer.valueOf(9));
      launchData.put("setNr", Integer.valueOf(3));
      launchData.put("interactiePanelLaunchState", ipLaunchState);
      launchData.put("breedte", Integer.valueOf(breedte));
      launchData.put("volledigeBreedte", volledigeBreedte);
      launchData.put("hoogte", Integer.valueOf(4));
      
      TekstVakPanel stappenVak = new TekstVakPanel(activity, launchData, randomVarNamen, randomVarWaarden);
      stappenVak.setKeyboard(kb);
      stappenVak.initialiseerStappen();
      stappenVak.setKolom(1);
      stappenVak.setParentStappen(this);
      return stappenVak;
    }
	
	public void resize()
	{
		hoogte = stappenVak.getHeight();
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
	
	public void makeStep(int index, ArrayList<Object> stepContents) {
	  stappenVak.forwardStep(index, stepContents, randomVarNamen, randomVarWaarden);
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
	
	public void addSelectedStep(int index, int stepNr) {
	  selectedSteps.add(index, stepNr);
	}
	
	public void zetInstellingen(ObjectMap instellingen)
	{
		stappenVak.zetInstellingen(instellingen);
	}
	
	public void setKeyboard(FormuleKeyboardIF kb)
	{
		this.kb = kb;
		stappenVak.setKeyboard(kb);
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
		List<Integer> selectedStepsList = Collections.emptyList();
		if(map.containsKey("selectedSteps"))
			selectedStepsList = map.getIntegerList("selectedSteps");
		selectedSteps = new ArrayList<Integer>(selectedStepsList);
		stappenVak.setState(h);
		resize();
		moveKeuzeVak();
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
	}

	@Override
	public void zetNagekeken(boolean b) {
		stappenVak.zetNagekeken(b);
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		this.comRoot = comRoot;
		stappenVak.setCommunicationRoot(comRoot);
// instellingen en keyboard zelf ophalen
		zetInstellingen(comRoot.getConfiguration());
		setKeyboard(comRoot.getKeyboard());
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

	int plusHoogte = 12;// hoogte van de + als er geen keuzevak is.
  protected void moveKeuzeVak(int nr) {
    int old = stappenVak.getRowOf(keuzeVak.asWidget().getParent());
	if (nr == old) 
      return;
	if (old > -1) {
	  stappenVak.geefTekstVak(old, 0).setSize(10, plusHoogte); 
	  stappenVak.geefTekstVak(old, 1).setSize(breedte - 52 - 10, plusHoogte);
	  stappenVak.geefTekstVak(old, 2).setSize(52, plusHoogte);
	}
    keuzeVak.asWidget().removeFromParent();
    TekstVak vak = stappenVak.geefTekstVak(nr, 1);
	vak.add(keuzeVak);
	vak.getWidgetContainerElement(keuzeVak.asWidget()).getStyle().clearOverflow();
	vak.setSize(breedte - 52 - 10, buttonHeight+10+4); // padding 5 margin 2
	stappenVak.resize();
  }

  public void moveKeuzeVak() {
    moveKeuzeVak(selectedSteps.size()*2);
  }
	
}