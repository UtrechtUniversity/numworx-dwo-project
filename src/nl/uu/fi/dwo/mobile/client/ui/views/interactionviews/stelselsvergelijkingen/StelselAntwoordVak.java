package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.stelselsvergelijkingen;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import fi.wiskopdr.FormuleParser;
import fi.wiskopdr.expressies.Expressie;
import fi.wiskopdr.text.Text;
import nl.uu.fi.dwo.interaction.client.FacetAware;
import nl.uu.fi.dwo.interaction.client.FormuleFont;
import nl.uu.fi.dwo.interaction.client.InteractionStub;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.sco.CorrectieFacade;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.TekstElementWithFont;
import nl.uu.fi.dwo.mobile.client.ui.views.XMLView;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstRegel;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstVak;
import nl.uu.fi.dwo.mobile.utils.TekstBuffer;


public class StelselAntwoordVak implements InteractionStub, FacetAware, TekstElementWithFont
{
	static final String holderId = "dockholder";
	private HashMap<String, Object> launchState; 
	
	int breedte = 100;
	int hoogte = 100; 
	private boolean volledigeBreedte = false;
	int ashoogte = 12;
	
	FlowPanel mainPanel;
	private StelselRekenVak rekenVak;
	private StelselOplossingenVak oplossingenVak;
	TekstVak oplossingenLabelVak;
	/**
	 * Even een field tbv fontovererving.
	 */
	ArrayList<Object> opdrachtObjects;
	/**
	 * Het font dat door fontovererving gezet moet worden voor oplossingenLabelVak.
	 */
	FormuleFont font;
	LayoutPanel oplossingenRegel;
	boolean fontOvererving = false;
	
	boolean rekenVakZichtbaar = true;
	boolean oplossingenRegelZichtbaar = true;
	
	private String[] randomVarNamen;
	private HashMap<String,Number> randomVarWaarden;
	
	private boolean check;
	private boolean teltMee;

	private boolean logOption;
	private String logID;
	
	private boolean boxMetRand;
	private int borderWidth = (Integer)DWOplayer.templateConstants.answerboxFEWA("border-width");
	
	private boolean[][] logObjectives;

	private double eqTestValueMin = 0;
	private double eqTestValueMax = 5;
	
	private int scoreMax = 10;
	
	private String[] varNamen;
	String variabelenString;
	private Expressie[][] oplossingen;
	private OpdrNavIF comRoot;
	private CorrectieFacade correctie;
	private ActivityComponent activity;
	
	public StelselAntwoordVak(ActivityComponent a, HashMap<String, Object> h, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden)
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
		
		init(breedte, hoogte, launchState, randomVarWaarden);
		initialize();
	}
	
	@Override
	public void init(int width, int height, Map<String, Object> launchData,
			Map<String, Number> values) {
		breedte = width;
		hoogte = height;
		ObjectMap map = JSONUtilities.wrapMap(launchData);
		String antwoordString = "$f@";
//		String variabelenString = "$f@";
		variabelenString = "$f@";
		
		if (map != null)
		{
			if (map.containsKey("antwoordString"))
				antwoordString = map.getString("antwoordString");
			if (map.containsKey("variabelenString"))
				variabelenString = map.getString("variabelenString");
			if (map.containsKey("check"))
				check = map.getBoolean("check");
			if (map.containsKey("teltMee"))
				teltMee = map.getBoolean("teltMee");
			if (map.containsKey("logOption"))
				logOption = map.getBoolean("logOption");
			if (map.containsKey("logID"))
				logID = map.getString("logID");
			if (map.containsKey("boxMetRand"))
				boxMetRand = map.getBoolean("boxMetRand");

			if (map.containsKey("eqTestValueMin"))
				eqTestValueMin = map.getDouble("eqTestValueMin");
			if (map.containsKey("eqTestValueMax"))
				eqTestValueMax = map.getDouble("eqTestValueMax");
			if (map.containsKey("scoreMax"))
				scoreMax = map.getInt("scoreMax");
			if (map.containsKey("rekenVakZichtbaar"))
				rekenVakZichtbaar = map.getBoolean("rekenVakZichtbaar");
			if (map.containsKey("oplossingenRegelZichtbaar"))
				oplossingenRegelZichtbaar = map.getBoolean("oplossingenRegelZichtbaar");
			if (map.containsKey("logObjectives"))
			{
				ObjectList logObjectivesList = (map.getObjectList("logObjectives"));
				logObjectives = new boolean[logObjectivesList.size()][];
				for (int i = 0; i < logObjectivesList.size(); i++)
				{
					logObjectives[i] = logObjectivesList.getBooleanArray(i);
				}
			}
		}
		
		try
		{
			antwoordString = FormuleParser.randomizeTekstVakString(antwoordString, randomVarNamen, randomVarWaarden);
		}
		catch (Exception e)
		{
		}
		antwoordString = antwoordString.replace(" ", "");
		
		try
		{
			variabelenString = FormuleParser.randomizeTekstVakString(variabelenString, randomVarNamen, randomVarWaarden);
		}
		catch (Exception e)
		{
		}
		variabelenString = variabelenString.replace(" ", "");
		
		try
		{
			//als nodig: haakjes weghalen. Anders alleen $f en @ weghalen.
			if (variabelenString.startsWith("$f("))
				variabelenString = variabelenString.substring(3, variabelenString.length() - 2);
			else
				variabelenString = variabelenString.substring(2, variabelenString.length() -1);
			varNamen = variabelenString.split(",");
			
			//variabelen omzetten naar nette varnamen (met name belangrijk voor variabelen met subscripts)
			for (int i = 0; i < varNamen.length; i++)
			{
				if (varNamen[i].length() > 0)
					FormuleParser.geefExpressie("$f"+ varNamen[i] + "@").geefVarNaam();
				
			}
			//splitsen in verschillende oplossingen. Eerst $f en @ weghalen.
			antwoordString = antwoordString.substring(2, antwoordString.length() - 1);
			antwoordString = antwoordString.replace("),(", "):(");
			String[] oplossingenStrings = antwoordString.split(":");
			oplossingen = new Expressie[oplossingenStrings.length][varNamen.length];
			for (int i = 0; i < oplossingenStrings.length; i++)
			{
				//haakjes verwijderen:
				if (oplossingenStrings[i].length() > 0)
				{
					String opl = oplossingenStrings[i].substring(1, oplossingenStrings[i].length() - 1);
					String[] varWaardes;
					if (opl.contains(";"))
						varWaardes = opl.split(";");
					else
						varWaardes = opl.split(",");
					for (int j = 0; j < varNamen.length; j++)
					{
						oplossingen[i][j] = FormuleParser.geefExpressie("$f" + varWaardes[j] + "@");
					}
				}
			}
			
			oplossingenLabelVak = new TekstVak();
			oplossingenLabelVak.setPasHoogteBreedteAan(true, true);
			
			// hier wil ik het font zetten in geval van fontovererving
			if (fontOvererving)
			{
				// font zetten van oplossingenLabelVak
				// hier heb ik het font nog niet, pas in setParentRegel()
			}
			
			TekstBuffer tb = new TekstBuffer(activity, randomVarNamen, randomVarWaarden, null);
//			ArrayList<Object> opdrachtObjects = new ArrayList<Object>();
			opdrachtObjects = new ArrayList<Object>();
			opdrachtObjects = tb.convertTekst(Text.constants.oplossingenLabel() + "$f(" + variabelenString + ")@:", null, false);
			
			oplossingenLabelVak.setFontName(XMLView.getDefaultFontName());
			oplossingenLabelVak.setFontSize(XMLView.getDefaultFontSize());
			oplossingenLabelVak.setColor(CssColor.make(0, 0, 0));
			oplossingenLabelVak.setObjects(opdrachtObjects);
		}
		catch(Exception e)
		{}
		
	}
	
	public void initialize()
	{
		// alle vakken op een panel zetten. 
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName(DWOplayer.templateCss().answerboxFEWS());
		if(!boxMetRand) {
			mainPanel.getElement().getStyle().setBorderStyle(Style.BorderStyle.NONE);
			mainPanel.getElement().getStyle().setBackgroundColor("transparent");
		}
//		mainPanel.getElement().getStyle().setBackgroundColor("white");
//		mainPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
//		mainPanel.getElement().getStyle().setBorderColor("gray");
//		mainPanel.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
		
		
		//rekenVak initialiseren
		if (rekenVakZichtbaar)
		{
			HashMap<String, Object> rekenVakMap = new HashMap<String, Object>();
			rekenVakMap.put("breedte", breedte);
			int h = 0;
			if (rekenVakZichtbaar && oplossingenRegelZichtbaar)
				h = hoogte - 30;
			else if (rekenVakZichtbaar)
				h = hoogte;
			rekenVakMap.put("hoogte", h);
			rekenVakMap.put("volledigeBreedte", volledigeBreedte);
			rekenVakMap.put("interactiePanelLaunchState", launchState);
			rekenVak = new StelselRekenVak(activity, this, rekenVakMap, randomVarNamen, randomVarWaarden);
			if(!boxMetRand) {
				rekenVak.getElement().getStyle().setBackgroundColor("transparent");
			}
			rekenVak.zetVarNamen(varNamen);
			rekenVak.zetJuisteOplossingen(oplossingen);
			mainPanel.add(rekenVak);
		}
		
		//oplossingenVak en oplossingenRegel initialiseren
		if (oplossingenRegelZichtbaar)
		{
			HashMap<String, Object> oplossingenVakMap = new HashMap<String, Object>();
			oplossingenVakMap.put("breedte", breedte - oplossingenLabelVak.getInhoudBreedte() - 15); //15 is door trial-en-error gevonden
			oplossingenVakMap.put("hoogte", 30);//deze hoogte doet er volgens mij niet zo veel meer toe.
			oplossingenVakMap.put("volledigeBreedte", volledigeBreedte);
			oplossingenVakMap.put("interactiePanelLaunchState", launchState);
			oplossingenVak = new StelselOplossingenVak(activity, this, oplossingenVakMap, randomVarNamen, randomVarWaarden); // font meegeven? 
			oplossingenVak.zetVarNamen(varNamen);
			oplossingenVak.zetJuisteOplossingen(oplossingen);
			
			oplossingenRegel = new LayoutPanel();
			oplossingenLabelVak.resize();
			
			fontOvererving = oplossingenVak.isFontOvererving();

			resize();
			mainPanel.add(oplossingenRegel);
		}
	}
	
	//resize wordt aangeroepen als de oplossingenregel van hoogte verandert.
	public void resize()
	{
		int hoogteRegel = Math.max(oplossingenLabelVak.getHeight(), oplossingenVak.getHeight());
		if (hoogteRegel == oplossingenRegel.getOffsetHeight())
			return;
		oplossingenRegel.setPixelSize(breedte - 2*borderWidth, hoogteRegel);
		oplossingenRegel.clear();
		for (int i = 0; i < hoogteRegel/2 + 1; i++)
		{
			FlowPanel panel = new FlowPanel();
			panel.getElement().getStyle().setBackgroundColor(CssColor.make(219,221,223).toString());//(CssColor.make(200 + 100*i/hoogteRegel, 200 + 100*i/hoogteRegel, 200 + 100*i/hoogteRegel).toString());
			oplossingenRegel.add(panel);
			oplossingenRegel.setWidgetLeftRight(panel, 0, Style.Unit.PX, 0, Style.Unit.PX);
			oplossingenRegel.setWidgetTopHeight(panel, hoogteRegel - 2*i, Style.Unit.PX, 2, Style.Unit.PX);
		}	
		oplossingenRegel.add(oplossingenLabelVak);
		oplossingenRegel.setWidgetLeftWidth(oplossingenLabelVak, 2, Style.Unit.PX, oplossingenLabelVak.getInhoudBreedte(), Style.Unit.PX);
		oplossingenRegel.setWidgetTopHeight(oplossingenLabelVak, 2 + Math.max(oplossingenVak.geefAsHoogte() - oplossingenLabelVak.getAsHoogte(), 0), Style.Unit.PX, oplossingenLabelVak.getHeight(), Style.Unit.PX);
		oplossingenRegel.add(oplossingenVak.asWidget());
		oplossingenRegel.setWidgetLeftRight(oplossingenVak.asWidget(), 2 + oplossingenLabelVak.getInhoudBreedte() + 5, Style.Unit.PX, 3, Style.Unit.PX);
		oplossingenRegel.setWidgetTopHeight(oplossingenVak.asWidget(), 2 + Math.max(oplossingenLabelVak.getAsHoogte() - oplossingenVak.geefAsHoogte(), 0), Style.Unit.PX, Math.max(oplossingenLabelVak.getHeight(), oplossingenVak.getHeight()), Style.Unit.PX);
		if (rekenVakZichtbaar)
			rekenVak.setHeight(hoogte - hoogteRegel - 4);
	}
	
	public void focusNaarOplossingenVak()
	{
		if (oplossingenRegelZichtbaar)
			oplossingenVak.requestFocus();
	}
	
	public void requestFocus()
	{
		if(rekenVakZichtbaar)
		{	if(!rekenVak.geefHoofdEditor().heeftKinderen())
			{	
				rekenVak.geefHoofdEditor().requestFocus(false);
			}
		}
		else
			focusNaarOplossingenVak();
	}
		
	@Override
	public HashMap<String, Object> getState()
	{
		HashMap<String, Object> h = new HashMap<String, Object>();
		if (rekenVakZichtbaar)
		{
			h = rekenVak.getState();
		}
		if (oplossingenRegelZichtbaar)
		{
			HashMap<String, Object> h1 = oplossingenVak.getState();
			h.put("oplRegelState", h1);
		}
		if(correctie != null) correctie.correctie(h);
		return h;
	}

	@Override
	public void setState(HashMap<String, Object> h)
	{
		if (h == null)
			return;
		CorrectieFacade.showReview(h, p -> {
			mainPanel.add(p);
			if (null != mainPanel.getElement().getStyle().getPosition())
				mainPanel.getElement().getStyle().setPosition(Position.RELATIVE);
		}, this, scoreMax);
		if (rekenVakZichtbaar)
			rekenVak.setState(h);
		if (oplossingenRegelZichtbaar)
		{
			if (h.containsKey("oplRegelState"))
			{
				HashMap<String, Object> h1 = (HashMap<String, Object>) h.get("oplRegelState");
				oplossingenVak.setState(h1);
			}
		}
		correctie = CorrectieFacade.get(h, this, scoreMax,comRoot, null, activity);
	}

	@Override
	public int getScore() {
		if(oplossingenRegelZichtbaar)
			return oplossingenVak.getScore();
		else
			return rekenVak.getScore();
	}

	@Override
	public int[][] getScoreObjectives() {
		return null;
	}

	@Override
	public Boolean isCorrect() {
		if(oplossingenRegelZichtbaar)
			return oplossingenVak.isCorrect();
		else
		{	return rekenVak.isCorrect();
		}
	}

	@Override
	public void kijkNa() {		
	}

	@Override
	public void zetNagekeken(boolean b) {
	}

	@Override
	public void setCommunicationRoot(OpdrNavIF comRoot) {
		
		this.comRoot = comRoot;
		//mode = comRoot.getMode();
		//if(dwologger != null)
		//	dwologger.setCommunicationRoot(comRoot);
		if(oplossingenVak != null)
			oplossingenVak.setCommunicationRoot(comRoot);
		if(rekenVak != null)
			rekenVak.setCommunicationRoot(comRoot);
	}

	@Override
	public void zetVolledigeBreedte(int breedte) {
		if(volledigeBreedte)
		{	this.breedte = breedte;
			if(rekenVakZichtbaar)
				rekenVak.zetVolledigeBreedte(breedte);
			if(oplossingenRegelZichtbaar)
			{	oplossingenVak.zetVolledigeBreedte(breedte - oplossingenLabelVak.getInhoudBreedte() - 15);
				resize();
			}
		}
	}

	public boolean isRekenVakZichtbaar()
	{
		return rekenVakZichtbaar;
	}
	
	public StelselRekenVak getRekenVak()
	{
		return rekenVak;
	}
	
	@Override
	public Widget asWidget() {
		return mainPanel;
	}

	@Override
	public int getAsHoogte() {
		return ashoogte;
	}

	@Override
	public int getHeight() {
		return hoogte + 2*borderWidth;
	}

	@Override
	public int getWidth() {
		return breedte;
	}

	@Override
	public void setAsHoogte(int ashoogte) {
		this.ashoogte = ashoogte;
		
	}

	@Override
	public void getResponses(List<String> responses) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFontSize(int font_size)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFontName(String font_name)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFontStyle(int font_style)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setParentRegel(TekstRegel regel)
	{
		// hier font zetten tbv oplossingenLabelVak
//		font = FormuleFont.createFromFontSize(regel.getFont().getFontSize(), false);
//		if (!FormuleFont.formTimes)
//		{
//			font.setFont(regel.getFont().getFont());
//		}

		if (rekenVak != null)
		{
			rekenVak.setParentRegel(regel);
		}
		
		if (oplossingenVak != null)
		{
			oplossingenVak.setFont(regel);
		}
		
		if (oplossingenLabelVak != null)
		{
			if (fontOvererving)
			{
				font = FormuleFont.createFromFontSize(regel.getFont().getFontSize(), false);
				if (!FormuleFont.formTimes)
				{
					font.setFont(regel.getFont().getFont());
				}

				oplossingenLabelVak.setFontSize(font.getFontSize());
				oplossingenLabelVak.setFontName(font.getFont());
			}
		}
	}
}
