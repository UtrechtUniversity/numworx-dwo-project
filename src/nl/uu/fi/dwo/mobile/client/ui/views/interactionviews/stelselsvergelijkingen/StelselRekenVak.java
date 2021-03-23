package nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.stelselsvergelijkingen;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.OpdrNavIF;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;
import nl.uu.fi.dwo.mobile.DWOplayer;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.views.interactionviews.TekstRegel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import fi.wiskopdr.AntwoordStelselVakChecker;
import fi.wiskopdr.expressies.BasisExpressie;
import fi.wiskopdr.expressies.Expressie;


public class StelselRekenVak extends LayoutPanel  {
	
	private final static Logger logger = Logger.getLogger("StelseRekenVak");
	
	StelselEditor hoofdEditor;
	String[] varNamen = {"n", "p"};
	Expressie[][] oplossingen = {{new BasisExpressie(0), new BasisExpressie(2)}};
	
	private boolean check;
	private boolean teltMee;
	
	private String[] randomVars;
	private HashMap<String, Number> randomVarWaarden;
	
	int headerHoogte = 23;
	int marge = 3;
	
	int scrollHoogte = 0;
	int scrollWidth = 0;
	int contentHoogte = 0;
	
	boolean boxMetRand;
	private int borderWidth = (Integer)DWOplayer.templateConstants.answerboxFEWA("border-width");
	
	StelselAntwoordVak antwoordVak;
	
	private ScrollPanel scrollPane;
	LayoutPanel contentPanel;
	FlowPanel headerPanel;
	
	//Image ic;
	private OpdrNavIF comRoot;

	private ActivityComponent activity;
	
	public StelselRekenVak(ActivityComponent a, StelselAntwoordVak antwoordVak, HashMap<String, Object> h, String[] randomVarNamen, HashMap<String, Number> randomVarWaarden)
	{
		this.activity = a;
		int hoogte = antwoordVak.hoogte - (antwoordVak.oplossingenRegelZichtbaar?27:0);
		scrollHoogte = hoogte - headerHoogte - marge - 2*borderWidth;
		scrollWidth = antwoordVak.breedte - 2*borderWidth;
		contentHoogte = scrollHoogte;
		this.setPixelSize(antwoordVak.breedte, hoogte);
		contentPanel = new LayoutPanel();
		//contentPanel.getElement().getStyle().setBackgroundColor("white");
		
		contentPanel.setPixelSize(scrollWidth - 3, contentHoogte); // wordt aangepast zodra hoogte hoofdEditor wordt aangepast.
		scrollPane = new ScrollPanel();
		scrollPane.setPixelSize(scrollWidth, scrollHoogte);
		scrollPane.getElement().getStyle().setOverflowX(Overflow.HIDDEN); // zorgt dat geen horizontale scrollbar verschijnt zodra vertical scrollbar verschijnt.
		scrollPane.getElement().getStyle().setOverflowY(Overflow.AUTO);
		scrollPane.getElement().getStyle().setFloat(Style.Float.LEFT);
		scrollPane.setWidget(contentPanel);
		add(scrollPane);
		
		this.antwoordVak = antwoordVak;
		
		ObjectMap map = JSONUtilities.wrapMap(h);
		Map<String, Object> launchState = null;
		if(map.containsKey("interactiePanelLaunchState"))
		{	launchState = map.getMap("interactiePanelLaunchState");
		}
		AntwoordStelselVakChecker avChecker = new AntwoordStelselVakChecker((HashMap<String, Object>) launchState, randomVarNamen, randomVarWaarden);
		hoofdEditor = new StelselEditor(activity, this, h, randomVarNamen, randomVarWaarden, avChecker);
		contentPanel.add(hoofdEditor);
		contentPanel.setWidgetLeftRight(hoofdEditor, -1, Style.Unit.PX, 0, Style.Unit.PX);
		contentPanel.setWidgetTopHeight(hoofdEditor, 0, Style.Unit.PX, 100, Style.Unit.PX);
		headerPanel = hoofdEditor.getHeaderPanel();
		add(headerPanel);
		setWidgetLeftRight(headerPanel, 0, Style.Unit.PX, 2, Style.Unit.PX);
		setWidgetTopHeight(headerPanel, 0, Style.Unit.PX, headerHoogte, Style.Unit.PX);
		setWidgetLeftRight(scrollPane, 0, Style.Unit.PX, 2, Style.Unit.PX);
		setWidgetTopHeight(scrollPane, headerHoogte + marge, Style.Unit.PX, scrollHoogte, Style.Unit.PX);
		
		if(launchState != null)
			init(launchState, randomVarNamen, randomVarWaarden);
		
	}
	
	public void init(Map<String, Object> h, String[] randomVars, HashMap<String, Number> randomValues)
	{
		int scoreMax = 10;
		boolean check = true;
		boolean teltMee = true;
		ObjectMap map = JSONUtilities.wrapMap(h);
		
		if (map.containsKey("scoreMax"))
			scoreMax = map.getInt("scoreMax");
		if (map.containsKey("check"))
			check = map.getBoolean("check");
		if (map.containsKey("teltMee"))
			teltMee = map.getBoolean("teltMee");
		
		this.check = check;
		this.teltMee = teltMee;
		this.randomVars = randomVars;
		this.randomVarWaarden = randomValues;
		
		hoofdEditor.zetScoreMax(scoreMax);
		hoofdEditor.zetCheck(check);
		
	}
	
	public void zetVarNamen(String[] varNamen)
	{
		this.varNamen = varNamen;
		hoofdEditor.zetVarNamen(varNamen);
	}
	
	public void setHeight(int h)
	{
		int currentScrollHeight = scrollPane.getVerticalScrollPosition();
		super.setHeight(h + "px");
		scrollHoogte = h - headerHoogte - marge - 2*borderWidth;
		contentHoogte = Math.max(scrollHoogte, hoofdEditor.geefHoogteEditorEnKinderen());
		scrollPane.setPixelSize(scrollWidth, scrollHoogte);
		if(contentHoogte > scrollHoogte)
			contentPanel.setPixelSize(scrollWidth - 20, contentHoogte);
		else
			contentPanel.setPixelSize(scrollWidth - 3, contentHoogte);
		scrollPane.setVerticalScrollPosition(currentScrollHeight);
	}
	
	public void zetVolledigeBreedte(int b)
	{
		logger.info("zetVolledigeBreedte: " + b);
		super.setWidth(b + "px");
		scrollWidth = b - 2*borderWidth;
		scrollPane.setPixelSize(scrollWidth, scrollHoogte);
		if(contentHoogte > scrollHoogte)
			contentPanel.setPixelSize(scrollWidth - 20, contentHoogte);
		else
			contentPanel.setPixelSize(scrollWidth - 3, contentHoogte);
		hoofdEditor.zetVolledigeBreedte(scrollWidth - 20);
	}
	
	public HashMap<String, Object> getState()
	{
		return hoofdEditor.getState();
		
	}
	
	public void setState(HashMap<String, Object> h)
	{
		hoofdEditor.setState(h);
	}
	
	public void zetJuisteOplossingen(Expressie[][] oplossingen)
	{
		this.oplossingen = oplossingen;
		hoofdEditor.zetOplossingen(oplossingen);
	}
	
	public void plaatsEditors()
	{
		int aantalKolommen = hoofdEditor.geefEindAantalKinderen();
		int kolomBreedte = hoofdEditor.getScrollPanel().getOffsetWidth()/aantalKolommen;
		hoofdEditor.setSizes(kolomBreedte);
		int h = hoofdEditor.geefHoogteEditorEnKinderen();
		int verticalFocusPosition = h;
		if(hoofdEditor.heeftKinderen())
			verticalFocusPosition = hoofdEditor.setLocations(-1, 0, 0);
		if(h > scrollHoogte)
		{	contentHoogte = h;
			contentPanel.setPixelSize(scrollWidth - 20, contentHoogte);
		}
		else
		{	contentHoogte = scrollHoogte;
			contentPanel.setPixelSize(scrollWidth - 3, contentHoogte);
		}
		contentPanel.setWidgetTopHeight(hoofdEditor, 0, Style.Unit.PX, hoofdEditor.geefHoogte(), Style.Unit.PX);
		
		scrollNaarFocus(verticalFocusPosition - scrollHoogte);
	}
	
	public StelselAntwoordVak geefAntwoordVak()
	{
		return antwoordVak;
	}
	
	public StelselEditor geefHoofdEditor()
	{
		return hoofdEditor;
	}
	
	public int getScore()
	{
		if (!teltMee)
			return 0;
		return hoofdEditor.getScoreEditorOfKinderen();
	}
	
	public boolean isCorrect()
	{
		if(!teltMee)
			return true;
		return hoofdEditor.zijnEditorOfKinderenCorrect();
	}
	
	public boolean isFout()
	{
		if(!teltMee)
			return false;
		//TODO: invullen.
		return false;
	}
	
	public void kijkNa()
	{
		//TODO: invullen (editors nakijken).
	}
	
	public void kijkNa(int stapNr)
	{
		//TODO: invullen (editors nakijken).
	}
	
	public void start()
	{
		//TODO: invullen.
	}
	
	public void setCommunicationRoot(OpdrNavIF comRoot)
	{
		this.comRoot = comRoot;
		if (hoofdEditor != null)
		{
			hoofdEditor.setCommunicationRoot(comRoot);
		}
	}

	public void setParentRegel(TekstRegel regel)
	{
		hoofdEditor.setParentRegel(regel);
	}
	
	public void scrollNaarFocus(int position)
	{
		scrollPane.setVerticalScrollPosition(position); 
	}
	
}
